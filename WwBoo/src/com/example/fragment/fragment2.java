package com.example.fragment;

import java.util.ArrayList;

import com.example.Util.Entity;
import com.example.Util.Entity2;
import com.example.Util.LogUtil;
import com.example.adapter.Myadapter;
import com.example.autoloadlistview.AutoLoadListView;
import com.example.autoloadlistview.AutoLoadListView.OnLoadNextListener;
import com.example.from_sina.AccessTokenKeeper;
import com.example.weibo.R;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class fragment2 extends Fragment{
	private	View rootview;
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
	private Entity entity;
	private	Entity2 entity2;
	private	int page = 1;
	private Myadapter adapter;
	private	AutoLoadListView listview;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
		// 对statusAPI实例化
		mStatusesAPI = new StatusesAPI(mAccessToken);
		
		//初始化控件
		listview = (AutoLoadListView)rootview.findViewById(R.id.mention_me_listview);
		/**
		 * 加载更多
		 */
		listview.setOnLoadNextListener(new OnLoadNextListener() {
			@Override
			public void onLoadNext() {
				mStatusesAPI.mentions(0L, 0L, 15, ++page, 0, 0, 0, false, mListener);
			}
		});
		getDate();
		showListView(list);
	}
	private void showListView(ArrayList<Entity> list) {
		if(adapter ==null){
			adapter = new Myadapter(getActivity(), list);
			listview.setAdapter(adapter);
		}else{
			adapter.onDateChange(list);
		}
	}

	private void getDate() {
		mStatusesAPI.mentions(0L, 0L, 15, 1, 0, 0, 0, false, mListener);
	}

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
		entity.setFavorited(statuses.statusList.get(i).favorited);
		if(!(statuses.statusList.get(i).retweeted_status ==null)){
			entity2 = new Entity2();
			entity2.setName(statuses.statusList.get(i).retweeted_status.user.screen_name);
			entity2.setContent(statuses.statusList.get(i).retweeted_status.text);
			entity2.setWeibo_pic(statuses.statusList.get(i).retweeted_status.pic_urls);
			entity.setEntity2(entity2);
		}
	}
	public RequestListener mListener = new RequestListener() {
		@Override
		public void onWeiboException(WeiboException arg0) {
		}
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				StatusList statuses = StatusList.parse(response);
				if (statuses != null && statuses.total_number > 0&& statuses.statusList!=null) {
					for(int i=0;i<statuses.statusList.size();i++){
						addToList(i, statuses);
						list.add(entity);
					}
					LogUtil.Log("list", list);
					adapter.onDateChange(list);
				}

			}
		}
	};
}
