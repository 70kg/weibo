package com.example.weibo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Util.Entity;
import com.example.adapter.Myadapter;
import com.example.adapter.commentadapter;
import com.example.from_sina.AccessTokenKeeper;
import com.example.loadimage.ImageLoader;
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
	ImageView current_weibo_user_pic;
	ArrayList<Entity> list = new ArrayList<Entity>();
	commentadapter adapter;
	Entity entity;
	weibolist commentlistview;
	ArrayList<String> url = new ArrayList<String>();
	Entity currentweiboentity;
	private ImageLoader mImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commentlayout);
		Intent intent =getIntent();
		currentweiboentity = (Entity)intent.getSerializableExtra("weibo");
		id =currentweiboentity.getId();
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(this);

		//对commentsAPI实例化
		mCommentsAPI = new CommentsAPI(mAccessToken);
		//Toast.makeText(CommentActivity.this, id, 1500).show();
		//初始化控件
		commentlistview = (weibolist)findViewById(R.id.commentlistview);

		mImageLoader = new ImageLoader(this);
		getDate();
		showListView(list);
		addHeadView(this);
	}

	public ImageLoader getImageLoader(){
		return mImageLoader;
	}
	private void addHeadView(Context context) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.comment_head, null);
		nametextview = (TextView)v.findViewById(R.id.current_weibo_user_name);
		contenttextview = (TextView)v.findViewById(R.id.current_weibo_content);
		current_weibo_user_pic = (ImageView)v. findViewById(R.id.current_weibo_user_pic);
		//控件赋值
		nametextview.setText(currentweiboentity.getName());
		contenttextview.setText(currentweiboentity.getContent());
		if(currentweiboentity.getUser_picl() !=null){
			//Log.e("log", "head v =" +currentweiboentity.getUser_picl());
			mImageLoader.DisplayImage(currentweiboentity.getUser_picl(), current_weibo_user_pic, false);
		}


		ViewHolder holder = new ViewHolder();
        holder.content = (TextView)v.findViewById(R.id.current_weibo2_content);
		holder.image1 = (ImageView)v.findViewById(R.id.current_weibo2_pic_1_1);
		holder.image2 = (ImageView)v.findViewById(R.id.current_weibo2_pic_1_2);
		holder.image3 = (ImageView)v.findViewById(R.id.current_weibo2_pic_1_3);
		holder.image4 = (ImageView)v.findViewById(R.id.current_weibo2_pic_2_1);
		holder.image5 = (ImageView)v.findViewById(R.id.current_weibo2_pic_2_2);
		holder.image6= (ImageView)v.findViewById(R.id.current_weibo2_pic_2_3);
		holder.image7 = (ImageView)v.findViewById(R.id.current_weibo2_pic_3_1);
		holder.image8 = (ImageView)v.findViewById(R.id.current_weibo2_pic_3_2);
		holder.image9 = (ImageView)v.findViewById(R.id.current_weibo2_pic_3_3);
		//微博配图
		//有转发微博
		if(!(currentweiboentity.getEntity2() ==null)){
			display(holder, currentweiboentity.getEntity2().getWeibo_pic());
			holder.content.setText(currentweiboentity.getEntity2().getContent());
			holder.content.setVisibility(View.VISIBLE);
			Log.e("currentweiboentity.getEntity2().getWeibo_pic()", currentweiboentity.getEntity2().getWeibo_pic()+"");
		}else{
			//没有转发微博
			View images =v.findViewById(R.id.current_weibo2_view);
			images.setBackground(null);
			display(holder, currentweiboentity.getWeibo_pic());
			Log.e("currentweiboentity.getWeibo_pic()", ""+currentweiboentity.getWeibo_pic());
		}
		Log.e("log", "head v =" +current_weibo_user_pic);
		commentlistview.addHeaderView(v);
	}



	private void display(ViewHolder holder,ArrayList<String> pic2){
		if(pic2 !=null){
			for (int i = 0; i < pic2.size(); i++) {
				switch (i) {
				case 0:				
					holder.image1.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(pic2.get(i), holder.image1, false);
					break;
				case 1:
					holder.image2.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(pic2.get(i), holder.image2, false);
					break;
				case 2:
					holder.image3.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(pic2.get(i), holder.image3, false);
					break;
				case 3:
					holder.image4.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(pic2.get(i), holder.image4, false);
					break;
				case 4:
					holder.image5.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(pic2.get(i), holder.image5, false);
					break;
				case 5:
					holder.image6.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(pic2.get(i), holder.image6, false);
					break;
				case 6:
					holder.image7.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(pic2.get(i), holder.image7, false);
					break;
				case 7:
					holder.image8.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(pic2.get(i), holder.image8, false);
					break;
				case 8:
					holder.image9.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(pic2.get(i), holder.image9, false);
					break;

				default:
					break;
				}
			}
		}
	}



	class ViewHolder{
		TextView content;
		ImageView image1;
		ImageView image2;
		ImageView image3;
		ImageView image4;
		ImageView image5;
		ImageView image6;
		ImageView image7;
		ImageView image8;
		ImageView image9;
	}










	//显示List
	private void showListView(ArrayList<Entity> list){
		if(adapter ==null){
			commentlistview.setInterface(this);
			adapter = new commentadapter(getApplicationContext(), list);
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
