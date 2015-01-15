package com.example.weibo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.example.Util.Entity;
import com.example.adapter.Myadapter;
import com.example.adapter.commentadapter;
import com.example.from_sina.AccessTokenKeeper;
import com.example.weibo.weibolist.ILoadListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.CommentList;

public class CommentActivity extends Activity implements ILoadListener{
	private Oauth2AccessToken mAccessToken;

	/** 微博评论接口 */
	private CommentsAPI mCommentsAPI;
	int page=1;

	String id;
	TextView contenttextview;
	TextView nametextview;
	ArrayList<Entity> list = new ArrayList<Entity>();
	commentadapter adapter;
	Entity entity;
	weibolist commentlistview;
	ArrayList<String> url = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commentlayout);
		Intent intent =getIntent();
		Entity currentweiboentity = (Entity)intent.getSerializableExtra("weibo");
		id =currentweiboentity.getId();
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(this);

		//对commentsAPI实例化
		mCommentsAPI = new CommentsAPI(mAccessToken);
		//Toast.makeText(CommentActivity.this, id, 1500).show();
		commentlistview = (weibolist)findViewById(R.id.commentlistview);
		nametextview = (TextView)findViewById(R.id.name1);
		contenttextview = (TextView)findViewById(R.id.content1);
		nametextview.setText(currentweiboentity.getName());
		contenttextview.setText(currentweiboentity.getContent());
		getDate();
		showListView(list);
	}


	//显示List
	private void showListView(ArrayList<Entity> list){
		if(adapter ==null){
			commentlistview.setInterface(this);

			commentlistview.setAdapter(adapter);
		}else{
			adapter.onDateChange(list);
		}
	}
	private void getDate(){
		//获取当前评论
		mCommentsAPI.show(Long.parseLong(id), 0L, 0L, 20, 1, 0, mListener);
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub

		mCommentsAPI.show(Long.parseLong(id), 0L, 0L, 5, ++page, 0, mListener);
		showListView(list);
		commentlistview.loadcomplete();
	}
	public RequestListener mListener = new RequestListener(){

		@Override
		public void onComplete(String response) {
			// TODO Auto-generated method stub
			if (!TextUtils.isEmpty(response)) {
				CommentList comments = CommentList.parse(response);
				if(comments != null && comments.total_number > 0){
					for(int i=0;i<comments.commentList.size();i++){
						entity = new Entity();
						entity.setName(comments.commentList.get(i).user.screen_name);
						entity.setContent(comments.commentList.get(i).text);
						entity.setUser_picl(comments.commentList.get(i).user.profile_image_url);
						list.add(entity);
					}
					adapter.onDateChange(list);

				}
			}
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub

		}

	};

}
