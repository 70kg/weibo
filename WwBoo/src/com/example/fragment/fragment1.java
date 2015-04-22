package com.example.fragment;

import java.util.ArrayList;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.Util.Entity;
import com.example.Util.Entity2;
import com.example.Util.GetDataHelper;
import com.example.Util.NetUtil;
import com.example.adapter.Myadapter;
import com.example.autoloadlistview.AutoLoadListView;
import com.example.autoloadlistview.AutoLoadListView.OnLoadNextListener;
import com.example.colorfulload.PullToRefreshView;
import com.example.colorfulload.PullToRefreshView.OnRefreshListener;
import com.example.from_sina.AccessTokenKeeper;
import com.example.loadimage.ImageLoader;
import com.example.weibo.CommentActivity;
import com.example.weibo.DeclareCurrentWeiBo;
import com.example.weibo.R;
import com.example.weibo.UploadWeiBo;
import com.example.weibo.weibolist.ILoadListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.StatusList;
public class fragment1 extends Fragment implements ILoadListener 
{
	// 加载更多
	public static final int MSG_LOAD_MORE = 0;
	// 刷新
	public static final int MSG_REFRESH = 1;
	private PullToRefreshView pullToRefreshView;
	private View rootView;
	ArrayList<Entity> list = new ArrayList<Entity>();
	/** 当前 Token 信息 */
	private Oauth2AccessToken mAccessToken;
	/** 用于获取微博信息流等操作的API */
	private StatusesAPI mStatusesAPI;
	
	private Entity  entity;
	private Entity2 entity2;
	private int page =1;
	private Myadapter adapter;
	private AutoLoadListView listview;
	private Long Long_since_id;
	private String since_id;
	private View fuction_layout;
	private ImageView refresh_button;
	private ImageView add_button;
	private ArrayList<Entity> mList = new ArrayList<Entity>();
	GetDataHelper mDataHelper;
	private ShowActionBar showActionBar;
	private boolean actionbar_is_show =true;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(msg.what==MSG_REFRESH){
				pullToRefreshView.setRefreshing(false);
				try {
					Long_since_id = Long.parseLong(since_id);
				} catch (Exception e) {
				}
				if(NetUtil.checkNet(getActivity())){
					mStatusesAPI.friendsTimeline(Long_since_id, 0L, 20, 1, false, 0, false, new_mListener);
					adapter.onDateChange(list);
				}else{
					Toast.makeText(getActivity(), "无网络链接", Toast.LENGTH_SHORT).show();
				}

			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView =inflater.inflate(R.layout.fragment_1, container, false);
		return rootView;
	}
	float downy;
	float y;
	boolean isActionDown = false;
	boolean isShow = false;
	/*
	 * 上滑出现按钮
	 */
	private void startAnimator(View view){
		if(isShow){
			ObjectAnimator.ofFloat(view, "translationY", -300.0F, 200.0F)  
			.setDuration(800)  
			.start();	
			isShow = false;
		}
	}
	/*
	 * 下滑隐藏按钮
	 */
	private void backAnimator(View view){
		if(!isShow){
			ObjectAnimator.ofFloat(view, "translationY",200.0F,-300.0F )  
			.setDuration(800)  
			.start(); 
			isShow = true;	
		}
	}
	/**
	 * 显示add button 隐藏刷新按钮
	 */
	private void show_add_button(View view) {
		ObjectAnimator.ofFloat(view, "translationX", 0.0F, 200.0F)  
		.setDuration(800)  
		.start();	

	}
	/**
	 * 刷新按钮回来
	 * @param v
	 */
	private void show_refresh_button(View v) {
		ObjectAnimator.ofFloat(v, "translationX", 200.0F, 0.0F)  
		.setDuration(800)  
		.start();

	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
		// 对statusAPI实例化
		mStatusesAPI = new StatusesAPI(mAccessToken);
		
		mDataHelper = new GetDataHelper(getActivity());

		list = mDataHelper.restoreNlist();
		//初始化控件
		refresh_button = (ImageView)rootView.findViewById(R.id.refresh_button);
		listview = (AutoLoadListView)rootView.findViewById(R.id.listview);
		fuction_layout = rootView.findViewById(R.id.fuciton_layout);
		add_button = (ImageView)rootView.findViewById(R.id.add_button);
		if(!NetUtil.checkNet(getActivity())){
			Toast.makeText(getActivity(), "无网络连接", Toast.LENGTH_SHORT).show();
		}else{
			getDate();
		}
		showListView(list);
		/**
		 * 发布微博
		 */
		add_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				show_refresh_button(refresh_button);
				startActivity(new Intent(getActivity(),UploadWeiBo.class));
			}
		});
		/*
		 * 刷新按钮回来
		 */
		add_button.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				show_refresh_button(refresh_button);
				return true;
			}
		});
		/**
		 * 长按显示add button
		 */
		refresh_button.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				show_add_button(v);
				return true;
			}
		});

		/**
		 * 点击回到最上面并刷新
		 */
		refresh_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listview.setSelection(0);
				pullToRefreshView.setRefreshing(true);
				handler.sendEmptyMessage(MSG_REFRESH);
			}
		});
		//将图标移到屏幕下面
		ObjectAnimator.ofFloat(fuction_layout, "translationY",0.0F,200.0F ).setDuration(0) .start(); 
		ObjectAnimator.ofFloat(fuction_layout, "translationX",0.0F,-50.0F ).setDuration(0) .start(); 



		pullToRefreshView = (PullToRefreshView)rootView.findViewById(R.id.pull_refresh);
		pullToRefreshView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				handler.sendEmptyMessageDelayed(MSG_REFRESH, 2000);

			}
		});
		/**
		 * listview item 点击事件  跳转
		 */
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),CommentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("weibo", list.get(position));
				intent.putExtras(bundle);
				getActivity().startActivity(intent);
			}
		});
		/**
		 * item长按显示dialog
		 */
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				new AlertDialog.Builder(getActivity()).setItems(new String[]{"评论","转发","收藏","复制"},
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(getActivity(),DeclareCurrentWeiBo.class);
						switch (which) {
						case 0:
							//评论
							Bundle bundle1 = new Bundle();
							bundle1.putSerializable("weibo", list.get(position));
							intent.putExtras(bundle1);
							intent.putExtra("type", 0);
							getActivity().startActivity(intent);
							break;
						case 1:
							//转发
							Bundle bundle = new Bundle();
							intent.putExtra("type", 1);
							bundle.putSerializable("weibo", list.get(position));
							intent.putExtras(bundle);
							getActivity().startActivity(intent);
							break;
						case 2:
							//收藏
							
							Toast.makeText(getActivity(), "收藏成功！", Toast.LENGTH_SHORT).show();
							break;
						case 3:
							//复制
							
							Toast.makeText(getActivity(), "复制成功！", Toast.LENGTH_SHORT).show();
							break;
						
						}


					}
				}).create().show();;
				return true;

			}
		});
		listview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {

				case MotionEvent.ACTION_MOVE:
					if(!isActionDown){
						//当做点击点
						downy = event.getY();
						isActionDown = true;
					}else{
						y = event.getY();
					}
					if(y-downy>40){
						//Log.e("下滑", "下滑");
						backAnimator(fuction_layout);
						if(showActionBar!=null&&actionbar_is_show ==false){
							showActionBar.Show();
							actionbar_is_show = true;
						}

					}else if(downy-y>40){
						//Log.e("上滑", "上滑");
						startAnimator(fuction_layout);
						if(showActionBar!=null&&actionbar_is_show ==true){
							showActionBar.Hold();
							actionbar_is_show = false;
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					isActionDown = false;
					break;
				}
				return false;
			}
		});

		listview.setOnLoadNextListener(new OnLoadNextListener() {

			@Override
			public void onLoadNext() {
				onLoad();
			}
		});

	}
	//显示List
	private void showListView(ArrayList<Entity> list){
		if(adapter ==null){
			adapter = new Myadapter(getActivity(), list);
			listview.setAdapter(adapter);
		}else{
			adapter.onDateChange(list);
		}
	}
	/*
	 * 加载初始化数据
	 */
	private void getDate(){
		//获取当前微博
		list.clear();
		mStatusesAPI.friendsTimeline(0L, 0L, 20, 1, false, 0, false, mListener);
	}

	/*
	 * 加载更多
	 * @see com.example.weibo.weibolist.ILoadListener#onLoad()
	 */
	@Override
	public void onLoad() {
		mStatusesAPI.friendsTimeline(0L, 0L, 20, ++page, false, 0, false, mListener);
		showListView(list);

	}

	/*
	 * 解析新浪微博
	 */
	public RequestListener mListener = new RequestListener() {

		@Override
		public void onWeiboException(WeiboException arg0) {
		}
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				if (response.startsWith("{\"statuses\"")) {
					// 调用 StatusList#parse 解析字符串成微博列表对象
					StatusList statuses = StatusList.parse(response);
					if (statuses != null && statuses.total_number > 0) {
						Toast.makeText(getActivity(), "加载20条微博", 1500).show();
						since_id=statuses.statusList.get(0).id;
						for(int i=0;i<20;i++){
							addToList(i, statuses);
							list.add(entity);
						}
						adapter.onDateChange(list);
						mDataHelper.saveNlist(list);
					}

				}
			}
		}
	};
	/**
	 * list赋值
	 * @param i
	 * @param statuses
	 */
	private void addToList(int i,StatusList statuses){
		entity = new Entity();
		entity.setName(statuses.statusList.get(i).user.screen_name);
		entity.setContent(statuses.statusList.get(i).text);
		entity.setUser_pic(statuses.statusList.get(i).user.avatar_hd);
		entity.setWeibo_pic(statuses.statusList.get(i).pic_urls);
		entity.setId(statuses.statusList.get(i).id);
		entity.setYuanshi_pic(statuses.statusList.get(i).original_pic);
		entity.setReposts_count(statuses.statusList.get(i).reposts_count);
		entity.setComments_counts(statuses.statusList.get(i).comments_count);
		entity.setAttitudes_count(statuses.statusList.get(i).attitudes_count);
		entity.setTime(statuses.statusList.get(i).created_at);
		entity.setFrom_type(statuses.statusList.get(i).source);
		if(!(statuses.statusList.get(i).retweeted_status ==null)){
			entity2 = new Entity2();
			entity2.setName(statuses.statusList.get(i).retweeted_status.user.screen_name);
			entity2.setContent(statuses.statusList.get(i).retweeted_status.text);
			entity2.setWeibo_pic(statuses.statusList.get(i).retweeted_status.pic_urls);
			entity.setEntity2(entity2);
		}
	}
	/**
	 * 刷新微博的监听器
	 */
	public RequestListener new_mListener = new RequestListener() {

		@Override
		public void onWeiboException(WeiboException arg0) {
		}
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				if (response.startsWith("{\"statuses\"")) {
					// 调用 StatusList#parse 解析字符串成微博列表对象
					StatusList statuses = StatusList.parse(response);
					if (statuses != null && statuses.statusList!=null) {
						since_id=statuses.statusList.get(0).id;
						for(int i=0;i< statuses.statusList.size();i++){
							addToList(i, statuses);
							if(!list.contains(entity)){
								list.add(i, entity);
							}
						}
						mDataHelper.saveNlist(list);
						Toast.makeText(getActivity(), "刷新"+statuses.statusList.size()+"条微博", Toast.LENGTH_SHORT).show();
						adapter.onDateChange(list);
						pullToRefreshView.setRefreshing(false);
					}
					
					else{
						Toast.makeText(getActivity(), "没有新微薄", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	};
	@Override
	public void onDestroy() {
		ImageLoader imageLoader = adapter.getImageLoader();
		if (imageLoader != null){
			imageLoader.clearCache();
		}
		super.onDestroy();
		mDataHelper.saveNlist(list);
	}
	public interface ShowActionBar{
		public void Show();
		public void Hold();
	}
	public void setShowActionBar(ShowActionBar actionBar){
		this.showActionBar = actionBar;
	}

}
