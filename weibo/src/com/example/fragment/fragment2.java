package com.example.fragment;

import java.util.ArrayList;

import com.example.Util.Entity;
import com.example.Util.Entity2;
import com.example.adapter.Myadapter;
import com.example.from_sina.AccessTokenKeeper;
import com.example.weibo.R;
import com.example.weibo.weibolist;
import com.example.weibo.weibolist.ILoadListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;



public class fragment2 extends Fragment implements ILoadListener{
	View rootview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootview= inflater.inflate(R.layout.fragment_2, container, false);
		return rootview;
	}

	ArrayList<Entity> list = new ArrayList<Entity>();

	private Oauth2AccessToken mAccessToken;

	/** 用于获取微博信息流等操作的API */
	private StatusesAPI mStatusesAPI;

	Entity entity;
	Entity2 entity2;

	int page = 1;
	Myadapter adapter;
	weibolist listview;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
		// 对statusAPI实例化
		mStatusesAPI = new StatusesAPI(mAccessToken);
		//初始化控件
		listview = (weibolist)rootview.findViewById(R.id.mention_me_listview);
		getDate();
		showListView(list);
	}

	private void showListView(ArrayList<Entity> list) {
		// TODO Auto-generated method stub
		if(adapter ==null){
			listview.setInterface(this);
			adapter = new Myadapter(getActivity(), list);
			listview.setAdapter(adapter);
		}else{
			adapter.onDateChange(list);
		}
	}

	private void getDate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		//获取@我的微博
		mStatusesAPI.mentions(0L, 0L, 20, 1, 0, 0, 0, false, mListener);

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
						Toast.makeText(getActivity(), "加载20条微博", 1500).show();
						for(int i=0;i<20;i++){
							entity = new Entity();
							entity.setName(statuses.statusList.get(i).user.screen_name);
							entity.setContent(statuses.statusList.get(i).text);
							entity.setUser_pic(statuses.statusList.get(i).user.profile_image_url);
							entity.setWeibo_pic(statuses.statusList.get(i).pic_urls);
							entity.setId(statuses.statusList.get(i).id);
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
}
