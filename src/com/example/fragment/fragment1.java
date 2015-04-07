package com.example.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.Util.Entity;
import com.example.Util.Entity2;
import com.example.adapter.Myadapter;
import com.example.autoloadlistview.AutoLoadListView;
import com.example.autoloadlistview.AutoLoadListView.OnLoadNextListener;
import com.example.colorfulload.PullToRefreshView;
import com.example.colorfulload.PullToRefreshView.OnRefreshListener;
import com.example.from_sina.AccessTokenKeeper;
import com.example.loadimage.ImageLoader;
import com.example.weibo.R;
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
	private AutoLoadListView listView;
	private View rootView;
	ArrayList<Entity> list = new ArrayList<Entity>();
	/** 当前 Token 信息 */
	private Oauth2AccessToken mAccessToken;
	/** 用于获取微博信息流等操作的API */
	private StatusesAPI mStatusesAPI;
	Entity  entity;
	Entity2 entity2;
	int page =1;
	Myadapter adapter;
	AutoLoadListView listview;
	Long l;
	String since_id;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(msg.what==MSG_REFRESH){
				pullToRefreshView.setRefreshing(false);
				//listView.smoothScrollToPosition(0);
				try {
					 l = Long.parseLong(since_id);
					 Log.e("l------", l+"");
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				mStatusesAPI.friendsTimeline(l, 0L, 20, 0, false, 0, false, mListener);
				showListView(list);
				//listView.setState(State.Idle);
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView =inflater.inflate(R.layout.fragment_1, container, false);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
		// 对statusAPI实例化
		mStatusesAPI = new StatusesAPI(mAccessToken);
		//初始化控件
		listview = (AutoLoadListView)rootView.findViewById(R.id.listview);
		pullToRefreshView = (PullToRefreshView)rootView.findViewById(R.id.pull_refresh);
		pullToRefreshView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				handler.sendEmptyMessageDelayed(MSG_REFRESH, 2000);
				
			}
		});
		listview.setOnLoadNextListener(new OnLoadNextListener() {

			@Override
			public void onLoadNext() {
				//handler.sendEmptyMessageDelayed(MSG_LOAD_MORE, 3000);
				onLoad();
			}
		});
		getDate();
		showListView(list);

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
			// TODO Auto-generated method stub
			if (!TextUtils.isEmpty(response)) {
				if (response.startsWith("{\"statuses\"")) {
					// 调用 StatusList#parse 解析字符串成微博列表对象
					StatusList statuses = StatusList.parse(response);
					if (statuses != null && statuses.total_number > 0) {
						Toast.makeText(getActivity(), "加载20条微博", 1500).show();
						since_id=statuses.statusList.get(0).id;
						Log.e("real since_id------", since_id+"");
						for(int i=0;i<20;i++){
							entity = new Entity();
							entity.setName(statuses.statusList.get(i).user.screen_name);
							entity.setContent(statuses.statusList.get(i).text);
							entity.setUser_pic(statuses.statusList.get(i).user.profile_image_url);
							entity.setWeibo_pic(statuses.statusList.get(i).pic_urls);
							entity.setId(statuses.statusList.get(i).id);
							entity.setYuanshi_pic(statuses.statusList.get(i).original_pic);
							entity.setReposts_count(statuses.statusList.get(i).reposts_count);
							entity.setComments_counts(statuses.statusList.get(i).comments_count);
							entity.setAttitudes_count(statuses.statusList.get(i).attitudes_count);
							if(!(statuses.statusList.get(i).retweeted_status ==null)){
								entity2 = new Entity2();
								entity2.setName(statuses.statusList.get(i).retweeted_status.user.screen_name);
								entity2.setContent(statuses.statusList.get(i).retweeted_status.text);
								entity2.setWeibo_pic(statuses.statusList.get(i).retweeted_status.pic_urls);
								entity.setEntity2(entity2);
							}
							list.add(entity);
						}
						adapter.onDateChange(list);
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
	}
}
