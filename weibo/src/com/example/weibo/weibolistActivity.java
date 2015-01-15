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
import com.example.Util.Entity;
import com.example.Util.Entity2;
import com.example.adapter.Myadapter;
import com.example.from_sina.AccessTokenKeeper;
import com.example.loadimage.ImageLoader;
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
	
	Entity  entity;
	Entity2 entity2;
	int page =1;
	Myadapter adapter;
	weibolist listview;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibolist);
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		// 对statusAPI实例化
		mStatusesAPI = new StatusesAPI(mAccessToken);
		
		//初始化控件
		listview = (weibolist)findViewById(R.id.listview);
		getDate();
		showListView(list);
	
	}

	//显示List
	private void showListView(ArrayList<Entity> list){
		if(adapter ==null){
			listview.setInterface(this);
			adapter = new Myadapter(weibolistActivity.this, list);
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
					if (statuses != null && statuses.total_number > 0) {
						Toast.makeText(weibolistActivity.this, "加载20条微博", 1500).show();
						for(int i=0;i<20;i++){
							entity = new Entity();
							entity.setName(statuses.statusList.get(i).user.screen_name);
							entity.setContent(statuses.statusList.get(i).text);
							entity.setUser_picl(statuses.statusList.get(i).user.profile_image_url);
							entity.setWeibo_pic(statuses.statusList.get(i).pic_urls);
							entity.setId(statuses.statusList.get(i).id);
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
