package com.example.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Util.CommentTimeLineEntity;
import com.example.adapter.CommentTimeLineAdapter;
import com.example.autoloadlistview.AutoLoadListView;
import com.example.autoloadlistview.AutoLoadListView.OnLoadNextListener;
import com.example.from_sina.AccessTokenKeeper;
import com.example.weibo.R;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.CommentList;



public class fragment_3 extends Fragment {
	private	View rootview;

	/** 微博评论接口 */
	private CommentsAPI mCommentsAPI;
	private Oauth2AccessToken mAccessToken;
	private	int page=1;
	private CommentTimeLineEntity entity;
	private	AutoLoadListView commentlistview;
	private	ArrayList<CommentTimeLineEntity> list = new ArrayList<CommentTimeLineEntity>();
	private	CommentTimeLineAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.fragment_3, container, false);
		return rootview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
		mCommentsAPI = new CommentsAPI(mAccessToken);
		//控件初始化
		commentlistview = (AutoLoadListView)rootview.findViewById(R.id.commentlistview);
		commentlistview.setOnLoadNextListener(new OnLoadNextListener() {
			@Override
			public void onLoadNext() {
				mCommentsAPI.timeline(0L, 0L, 20, ++page, false, listener);
			}
		});
		getDate();
		showListView(list);

	}
	//显示List
	private void showListView(ArrayList<CommentTimeLineEntity> list){
		if(adapter ==null){
			adapter = new CommentTimeLineAdapter(getActivity(), list);
			commentlistview.setAdapter(adapter);
		}else{
			adapter.onDateChange(list);
		}
	}
	private void getDate(){
		mCommentsAPI.timeline(0L, 0L, 20, 1, false, listener);	
	}

	public RequestListener listener  = new RequestListener() {
		@Override
		public void onWeiboException(WeiboException arg0) {
		}
		@Override
		public void onComplete(String response) {
			// TODO Auto-generated method stub
			if (!TextUtils.isEmpty(response)) {
				CommentList comments = CommentList.parse(response);
				if(comments != null && comments.total_number > 0 ){
					for(int i=0;i<comments.commentList.size();i++){
						entity = new CommentTimeLineEntity();
						entity.setCreated_at(comments.commentList.get(i).created_at);
						entity.setReply_comment(comments.commentList.get(i).reply_comment);
						entity.setSource(comments.commentList.get(i).source);
						entity.setStatus(comments.commentList.get(i).status);
						entity.setText(comments.commentList.get(i).text);
						entity.setUser(comments.commentList.get(i).user);
						list.add(entity);
					}
					adapter.onDateChange(list);
				}
			}
		}
	};

}
