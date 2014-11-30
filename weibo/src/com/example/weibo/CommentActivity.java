package com.example.weibo;

import java.util.ArrayList;

import com.example.weibo.weibolist.ILoadListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.CommentList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CommentActivity extends Activity implements ILoadListener{
	private Oauth2AccessToken mAccessToken;

	/** 微博评论接口 */
	private CommentsAPI mCommentsAPI;
	int page=1;
	String name;
	String content;
	String id;
	TextView contenttextview;
	TextView nametextview;
	ArrayList<Entity> list = new ArrayList<Entity>();
	Myadapter adapter;
	Entity entity;
	weibolist commentlistview;
	ArrayList<String> url = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commentlayout);
		Intent intent =getIntent();
		name=intent.getStringExtra("name");
		content=intent.getStringExtra("content");
		id=intent.getStringExtra("id");
		contenttextview=(TextView)findViewById(R.id.content1);
		nametextview=(TextView)findViewById(R.id.name1);
		nametextview.setText(name);
		contenttextview.setText(content);
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(this);

		//对commentsAPI实例化
		mCommentsAPI = new CommentsAPI(mAccessToken);
		//Toast.makeText(CommentActivity.this, id, 1500).show();
		commentlistview = (weibolist)findViewById(R.id.commentlistview);
		getDate();
		showListView(list);
	}
	
	
	//显示List
	private void showListView(ArrayList<Entity> list){
		if(adapter ==null){
			commentlistview.setInterface(this);
			//adapter = new Myadapter(CommentActivity.this,url, list);
			//Toast.makeText(CommentActivity.this, "123", 1500).show();
			commentlistview.setAdapter(adapter);
		}else{
			adapter.onDateChange(list);
		}
	}
	private void getDate(){
		//获取当前评论
		mCommentsAPI.show(Long.parseLong(id), 0L, 0L, 20, 1, 0, mListener);
		//Toast.makeText(CommentActivity.this, "123", 1500).show();
		
	
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
						url.add(comments.commentList.get(i).user.profile_image_url);
						list.add(entity);
					}
					adapter.onDateChange(list);
                /* Toast.makeText(CommentActivity.this,
                         "获取评论成功, 条数: " + comments.commentList.get(1).text, 
                         Toast.LENGTH_LONG).show();*/
             }
         }
	}

	@Override
	public void onWeiboException(WeiboException arg0) {
		// TODO Auto-generated method stub
		
	}
	
};

}
