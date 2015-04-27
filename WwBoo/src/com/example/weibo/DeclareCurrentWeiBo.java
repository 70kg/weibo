package com.example.weibo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Util.Entity;
import com.example.from_sina.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;

public class DeclareCurrentWeiBo extends Activity{
	private Oauth2AccessToken mAccessToken;
	/** 微博评论接口 */
	private CommentsAPI mCommentsAPI;
	private EditText input_content;
	private Button release;
	private Entity mCurrentWeiBoEntity;
	private int mType;//0 评论  1 转发
	private String mContnet;
	private String mCurrentWeiBoID;
	private boolean mIsToOroginalWeiBo = false;//是否评论给原微博
	private int mIsRepostAndComment =0;// 是否在转发的同时发表评论，0：否、1：评论给当前微博、2：评论给原微博、3：都评论，默认为0
	private com.sina.weibo.sdk.openapi.legacy.StatusesAPI msApi;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.declare_current_weibo);
		//初始化控件
		input_content = (EditText)findViewById(R.id.input_content);
		release = (Button)findViewById(R.id.release);
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		//对commentsAPI实例化
		mCommentsAPI = new CommentsAPI(mAccessToken);
		msApi = new com.sina.weibo.sdk.openapi.legacy.StatusesAPI(mAccessToken);


		
		Intent intent =getIntent();
		mCurrentWeiBoEntity = (Entity)intent.getSerializableExtra("weibo");
		mType = (int)intent.getIntExtra("type", 0);
		mCurrentWeiBoID = mCurrentWeiBoEntity.getId();
		if(mType ==0){
			release.setText("评论微博");
		}else{
			release.setText("转发微博");
		}
		release.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContnet = input_content.getText().toString();
				if(mType ==0){
					//评论
					mCommentsAPI.create(mContnet, Long.parseLong(mCurrentWeiBoID), 
							false, mListener);
				}else{
					//转发
					msApi.repost(Long.parseLong(mCurrentWeiBoID), mContnet, mIsRepostAndComment, repost_Listener);
				}
			}
		});
	}
	/**
	 * 评论微博
	 */
	private RequestListener mListener = new RequestListener(){
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				try {
						Comment status = Comment.parse(new JSONObject(response));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Toast.makeText(DeclareCurrentWeiBo.this, "评论成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(DeclareCurrentWeiBo.this, "评论失败", Toast.LENGTH_LONG).show();
				}
			}
		@Override
		public void onWeiboException(WeiboException arg0) {
		}
	};
	/**
	 * 转发微博
	 */
	private RequestListener repost_Listener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				if (response.startsWith("{\"created_at\"")) {
					// 调用 Status#parse 解析字符串成微博对象
					Status status = Status.parse(response);
					Toast.makeText(DeclareCurrentWeiBo.this, "转发微博成功" ,Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(DeclareCurrentWeiBo.this, "转发微博失败", Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {

		}
	};
}
