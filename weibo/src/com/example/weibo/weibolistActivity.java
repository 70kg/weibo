package com.example.weibo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.example.weibo.weibolist.ILoadListener;
import com.example.weibo.loadimage.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.StatusList;

public class weibolistActivity extends Activity implements ILoadListener {
	ArrayList<Entity> list = new ArrayList<Entity>();
	/** 当前 Token 信息 */
	private Oauth2AccessToken mAccessToken;
	/** 用于获取微博信息流等操作的API */
	private StatusesAPI mStatusesAPI;
	/** 微博评论接口 */
	private CommentsAPI mCommentsAPI;
	//mid
	Long mid= 0L;
	Entity  entity;
	int page =1;
	Myadapter adapter;
	weibolist listview;
	//id
	ArrayList<String> name = new ArrayList<String>();
	ArrayList<String> content = new ArrayList<String>();
	ArrayList<String> id = new ArrayList<String>();
	//String id;

	ArrayList<String> url = new ArrayList<String>();
	ArrayList<String> pic_urls = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibolist);
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		// 对statusAPI实例化
		mStatusesAPI = new StatusesAPI(mAccessToken);
		//对commentsAPI实例化
		mCommentsAPI = new CommentsAPI(mAccessToken);
		//初始化控件
		listview = (weibolist)findViewById(R.id.listview);
		getDate();
		showListView(list);
		listview.setOnItemClickListener(listener);

	}

	//设置listview的点击事件

	private OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(weibolistActivity.this,CommentActivity.class);
			intent.putExtra("name", getname(position));
			intent.putExtra("content", getcontent(position));
			intent.putExtra("id", getid(position));
			
			startActivity(intent);
			onPause();
             
			//Toast.makeText(weibolistActivity.this, getname(position), 1500).show();
		}
	};
	//获取当前微博name的方法
	public String getname(int position){
		return name.get(position);
	}
	//获取当前微博content的方法
	public String getcontent(int position){
		return content.get(position);
	}
	//获取当前微博id的方法
	public String getid(int position){
		return id.get(position);
	}
	//显示List
	private void showListView(ArrayList<Entity> list){
		if(adapter ==null){

			listview.setInterface(this);
			adapter = new Myadapter(this, url,pic_urls,list);
			listview.setAdapter(adapter);
		}else{
			adapter.onDateChange(list);
		}
	}
	private void getDate(){
		//获取当前微博
		mStatusesAPI.friendsTimeline(0L, 0L, 20, 1, false, 0, false, mListener);
		//mCommentsAPI.show(id, since_id, max_id, count, page, authorType, listener);

	}

	public RequestListener mListener = new RequestListener() {

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete(String response) {
			// TODO Auto-generated method stub
			if (!TextUtils.isEmpty(response)) {
				if (response.startsWith("{\"statuses\"")) {
					// 调用 StatusList#parse 解析字符串成微博列表对象
					StatusList statuses = StatusList.parse(response);

					/*CommentList comments = CommentList.parse(response);

					comments.commentList.size();*/
					if (statuses != null && statuses.total_number > 0) {

						// mid =Long.parseLong(statuses.statusList.get(statuses.statusList.size()).mid)-1;statuses.statusList.get(i).text
						Toast.makeText(weibolistActivity.this, "加载20条微博", 1500).show();
						for(int i=0;i<20;i++){
							entity = new Entity();
							entity.setName(statuses.statusList.get(i).user.screen_name);
							entity.setContent(statuses.statusList.get(i).text);
							name.add(statuses.statusList.get(i).user.screen_name);
							content.add(statuses.statusList.get(i).text);
							id.add(statuses.statusList.get(i).id);
							url.add(statuses.statusList.get(i).user.profile_image_url);
							/*if(statuses.statusList.get(i).in_reply_to_screen_name !=null){
								imageurl.add(statuses.statusList.get(i).in_reply_to_screen_name);
							}*/
							pic_urls=statuses.statusList.get(i).pic_urls;
							list.add(entity);
						}
						adapter.onDateChange(list);
						//mid =Long.parseLong(statuses.statusList.get(statuses.statusList.size()).mid);
					}
				}
			}
		}
	};
	@Override
	public void onLoad() {
		//page++;
		mStatusesAPI.friendsTimeline(0L, 0L, 20, ++page, false, 0, false, mListener);
		showListView(list);
		listview.loadcomplete();
	}
	@Override
	protected void onDestroy() {
		
		
		ImageLoader imageLoader = adapter.getImageLoader();
		if (imageLoader != null){
			imageLoader.clearCache();
		}
		
		super.onDestroy();
	}

}
