package com.example.fragment;

import java.util.ArrayList;

import com.example.Util.Entity;
import com.example.adapter.commentadapter;
import com.example.from_sina.AccessTokenKeeper;
import com.example.loadimage.ImageLoader;
import com.example.weibo.R;
import com.example.weibo.weibolist;
import com.example.weibo.weibolist.ILoadListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.CommentList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



public class fragment_3 extends Fragment implements ILoadListener{
	View rootview;
	TextView contenttextview;
	TextView nametextview; 
	ImageView current_weibo_user_pic;
	/** 微博评论接口 */
	private CommentsAPI mCommentsAPI;
	private Oauth2AccessToken mAccessToken;
	int page=1;
	Entity entity;
	weibolist commentlistview;
	ArrayList<Entity> list = new ArrayList<Entity>();
	commentadapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.fragment_3, container, false);
		return rootview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());

		//对commentsAPI实例化
		mCommentsAPI = new CommentsAPI(mAccessToken);
		//控件初始化
		commentlistview = (weibolist)rootview.findViewById(R.id.commentlistview);
		getDate();
		showListView(list);

	}
	//显示List
	private void showListView(ArrayList<Entity> list){
		if(adapter ==null){
			commentlistview.setInterface(this);
			adapter = new commentadapter(getActivity(), list);
			commentlistview.setAdapter(adapter);
		}else{
			adapter.onDateChange(list);
		}
	}
	private void getDate(){
		mCommentsAPI.timeline(0L, 0L, 20, page, false, listener);	
	}

	public RequestListener listener  = new RequestListener() {

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete(String response) {
			// TODO Auto-generated method stub
			if (!TextUtils.isEmpty(response)) {
				CommentList comments = CommentList.parse(response);
				if(comments != null && comments.total_number > 0 ){
					for(int i=0;i<comments.commentList.size();i++){
						entity = new Entity();
						entity.setComments_counts(comments.total_number);
						entity.setName(comments.commentList.get(i).user.screen_name);
						entity.setContent(comments.commentList.get(i).text);
						entity.setUser_pic(comments.commentList.get(i).user.profile_image_url);
						list.add(entity);
					}
					adapter.onDateChange(list);
				}
			}
		}
	};
	@Override
	public void onLoad() {
		//获取当前评论
				mCommentsAPI.timeline(0L, 0L, 20, page, false, listener);
	}
}
