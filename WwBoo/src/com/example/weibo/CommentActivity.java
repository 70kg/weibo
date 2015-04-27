package com.example.weibo;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Util.Defs;
import com.example.Util.Entity;
import com.example.Util.LogUtil;
import com.example.Util.StringUtil;

import android.app.ActionBar;

import com.example.adapter.commentadapter;
import com.example.autoloadlistview.AutoLoadListView;
import com.example.autoloadlistview.LoadingFooter;
import com.example.autoloadlistview.AutoLoadListView.OnLoadNextListener;
import com.example.bigpic.BigPicActivity1;
import com.example.from_sina.AccessTokenKeeper;
import com.example.loadimage.ImageLoader;
import com.example.weibo.weibolist.ILoadListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.CommentList;

public class CommentActivity extends Activity {
	private Oauth2AccessToken mAccessToken;
	/** 微博评论接口 */
	private CommentsAPI mCommentsAPI;
	private	int page=1;
	private	String id;
	private	ArrayList<Entity> list = new ArrayList<Entity>();
	private	commentadapter adapter;
	private	Entity entity;
	private	AutoLoadListView mCommentlistview;
	private ArrayList<String> url = new ArrayList<String>();
	private Entity currentweiboentity;
	private ImageLoader mImageLoader;
	private ActionBar actionBar;
	private boolean mCanload;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commentlayout);
		/**
		 * 设置actionbar颜色
		 */
		ColorDrawable drawable = new ColorDrawable(this.getResources().getColor(R.drawable.action_bar_color));
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(drawable);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		Intent intent =getIntent();
		currentweiboentity = (Entity)intent.getSerializableExtra("weibo");
		id =currentweiboentity.getId();
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		//对commentsAPI实例化
		mCommentsAPI = new CommentsAPI(mAccessToken);
		//初始化控件
		mCommentlistview = (AutoLoadListView)findViewById(R.id.commentlistview);
		mCommentlistview.setDivider(null);
		mCommentlistview.setOnLoadNextListener(new OnLoadNextListener() {

			@Override
			public void onLoadNext() {
				if(mCanload==true){
					mCommentsAPI.show(Long.parseLong(id), 0L, 0L, 40, ++page, 0, mListener);
				}else{
					Toast.makeText(CommentActivity.this, "没有更多评论", Toast.LENGTH_SHORT).show();
					mCommentlistview.setState(LoadingFooter.State.TheEnd);
				}

			}
		});
		mImageLoader = new ImageLoader(this);
		getDate();
		showListView(list);
		addHeadView(this);
	}

	public ImageLoader getImageLoader(){
		return mImageLoader;
	}
	private void addHeadView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.listitem, null);
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView)v.findViewById(R.id.name);
		holder.content = (TextView)v.findViewById(R.id.content);
		holder.image = (RoundImageView)v.findViewById(R.id.imageview);
		holder.repost_counts = (TextView)v.findViewById(R.id.reposts_count);
		holder.comment_counts = (TextView)v.findViewById(R.id.comment_count);
		holder.attitudes_counts = (TextView)v.findViewById(R.id.attitudes_counts);
		holder.repost_img = (ImageView)v.findViewById(R.id.repost_img);
		holder.comments_img = (ImageView)v.findViewById(R.id.comment_img);
		holder.attitudes_img = (ImageView)v.findViewById(R.id.attitudes_img);
		holder.from_type = (TextView)v.findViewById(R.id.from_type);
		holder.time = (TextView)v.findViewById(R.id.time);

		holder.image1 = (ImageView)v.findViewById(R.id.weibo2_pic_1_1);
		holder.image2 = (ImageView)v.findViewById(R.id.weibo2_pic_1_2);
		holder.image3 = (ImageView)v.findViewById(R.id.weibo2_pic_1_3);
		holder.image4 = (ImageView)v.findViewById(R.id.weibo2_pic_2_1);
		holder.image5 = (ImageView)v.findViewById(R.id.weibo2_pic_2_2);
		holder.image6= (ImageView)v.findViewById(R.id.weibo2_pic_2_3);
		holder.image7 = (ImageView)v.findViewById(R.id.weibo2_pic_3_1);
		holder.image8 = (ImageView)v.findViewById(R.id.weibo2_pic_3_2);
		holder.image9 = (ImageView)v.findViewById(R.id.weibo2_pic_3_3);
		holder.weibo2_content = (TextView)v.findViewById(R.id.weibo2_content);
		//控件赋值
		ToControltheAssignment(currentweiboentity, holder,v);
		onclicklistener listener = new onclicklistener(currentweiboentity, context);
		holder.image1.setOnClickListener(listener);
		holder.image2.setOnClickListener(listener);
		holder.image3.setOnClickListener(listener);
		holder.image4.setOnClickListener(listener);
		holder.image5.setOnClickListener(listener);
		holder.image6.setOnClickListener(listener);
		holder.image7.setOnClickListener(listener);
		holder.image8.setOnClickListener(listener);
		holder.image9.setOnClickListener(listener);
		mCommentlistview.addHeaderView(v);
	}
	/**
	 * 控件赋值
	 * @param entity
	 * @param holder
	 */
	private void ToControltheAssignment(final Entity entity, ViewHolder holder,View v) {
		View view =v.findViewById(R.id.weibo2_view);
		//有转发微博
		if(entity.getEntity2()!=null){
			holder.weibo2_content.setText("@"+entity.getEntity2().getName()+" :"+entity.getEntity2().getContent());
			StringUtil.extractMention2Link(holder.weibo2_content);
			holder.weibo2_content.setAutoLinkMask(0x01);
			holder.weibo2_content.setVisibility(View.VISIBLE);
			view.setBackgroundResource(R.drawable.popup);
			display(holder, entity.getEntity2().getWeibo_pic());
		}else{
			//没有转发微博
			view.setBackground(null);
			display(holder, entity.getWeibo_pic());
		}
		String url="";
		url = entity.getUser_pic();
		mImageLoader.DisplayImage(url, holder.image, false);

		holder.content.setText(entity.getContent());
		StringUtil.extractMention2Link(holder.content);
		holder.content.setAutoLinkMask(0x01);
		holder.name.setText(entity.getName());
		holder.name.getPaint().setFakeBoldText(true);//加粗
		//设置时间和来源
		holder.time.setText(entity.getTime().substring(10,16));
		String type = entity.getFrom_type();
		Pattern p = Pattern.compile("\\>(.*?)\\<");
		Matcher m = p.matcher(type);
		while(m.find()){
			holder.from_type.setText(m.group(1));
		}
		//评论 转发 点赞--------------

		if(entity.getReposts_count() !=0){
			holder.repost_counts.setVisibility(View.VISIBLE);
			holder.repost_counts.setText(entity.getReposts_count()+"");
			holder.repost_img.setVisibility(View.VISIBLE);
		}else{
			holder.repost_img.setVisibility(View.GONE);
		}
		if(entity.getComments_counts() !=0){
			holder.comment_counts.setVisibility(View.VISIBLE);
			holder.comment_counts.setText(entity.getComments_counts()+"");
			holder.comments_img.setVisibility(View.VISIBLE);
		}else{
			holder.comments_img.setVisibility(View.GONE);
		}
		if(entity.getAttitudes_count()!=0){
			holder.attitudes_counts.setVisibility(View.VISIBLE);
			holder.attitudes_counts.setText(entity.getAttitudes_count()+"");
			holder.attitudes_img.setVisibility(View.VISIBLE);
		}else{
			holder.attitudes_img.setVisibility(View.GONE);
		}
	}
	/**
	 * 显示微博配图
	 * @param holder
	 * @param pic2
	 */
	private void display(ViewHolder holder,ArrayList<String> pic2){
		String new_pic_url;
		if(pic2 !=null){
			for (int i = 0; i < pic2.size(); i++) {
				new_pic_url=pic2.get(i).replace("thumbnail", "bmiddle");
				switch (i) {
				case 0:				
					holder.image1.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(new_pic_url, holder.image1, false);
					break;
				case 1:
					holder.image2.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(new_pic_url, holder.image2, false);
					break;
				case 2:
					holder.image3.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(new_pic_url, holder.image3, false);
					break;
				case 3:
					holder.image4.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(new_pic_url, holder.image4, false);
					break;
				case 4:
					holder.image5.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(new_pic_url, holder.image5, false);
					break;
				case 5:
					holder.image6.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(new_pic_url, holder.image6, false);
					break;
				case 6:
					holder.image7.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(new_pic_url, holder.image7, false);
					break;
				case 7:
					holder.image8.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(new_pic_url, holder.image8, false);
					break;
				case 8:
					holder.image9.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(new_pic_url, holder.image9, false);
					break;

				default:
					break;
				}
			}
		}
	}
	class ViewHolder{
		TextView name;
		TextView content;
		RoundImageView image;
		TextView repost_counts;
		TextView comment_counts;
		TextView attitudes_counts;
		ImageView repost_img;
		ImageView comments_img;
		ImageView attitudes_img;
		ImageView image1;
		ImageView image2;
		ImageView image3;
		ImageView image4;
		ImageView image5;
		ImageView image6;
		ImageView image7;
		ImageView image8;
		ImageView image9;
		TextView weibo2_content;
		TextView from_type;
		TextView time;
	}
	//显示List
	private void showListView(ArrayList<Entity> list){
		if(adapter ==null){
			adapter = new commentadapter(CommentActivity.this, list);
			mCommentlistview.setAdapter(adapter);
		}else{
			adapter.onDateChange(list);
		}
	}
	/**
	 * 获取当前评论
	 */
	private void getDate(){
		mCommentsAPI.show(Long.parseLong(id), 0L, 0L, 40, 1, 0, mListener);
	}
	/**
	 * 加载更多
	 */


	/**
	 * 评论监听器
	 */
	public RequestListener mListener = new RequestListener(){
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				CommentList comments = CommentList.parse(response);
				if(comments != null && comments.total_number > 0 ){
					for(int i=0;i<comments.commentList.size();i++){
						entity = new Entity();
						entity.setComments_counts(comments.total_number);
						entity.setName(comments.commentList.get(i).user.screen_name);
						entity.setContent(comments.commentList.get(i).text);
						entity.setUser_pic(comments.commentList.get(i).user.avatar_large);
						entity.setFrom_type(comments.commentList.get(i).source);
						entity.setTime(comments.commentList.get(i).created_at);
						list.add(entity);
					}
					//LogUtil.Log("list.size", list.size());
					//LogUtil.Log("comments.total_number",comments.total_number);
					if(list.size()==comments.total_number){
						mCanload = false;
					}else{
						mCanload = true;
					}
					adapter.onDateChange(list);
				}
			}
		}
		@Override
		public void onWeiboException(WeiboException arg0) {
		}
	};
	
/*	@Override
	protected void onDestroy() {
		ImageLoader imageLoader = adapter.getImageLoader();
		if (imageLoader != null){
			imageLoader.clearCache();
		}
		super.onDestroy();
	}*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())  
		{         
		case android.R.id.home:             
			finish();
			return true;         
		default:             
			return super.onOptionsItemSelected(item);     
		}
	}
}
class onclicklistener implements OnClickListener{
	Entity entity;
	Context mContext;
	public onclicklistener(Entity entity,Context context) {
		this.entity=entity;
		this.mContext = context;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.weibo2_pic_1_1:
			clickevent(entity,0);
			break;
		case R.id.weibo2_pic_1_2:
			clickevent(entity,1);
			break;
		case R.id.weibo2_pic_1_3:
			clickevent(entity,2);
			break;
		case R.id.weibo2_pic_2_1:
			clickevent(entity,3);
			break;
		case R.id.weibo2_pic_2_2:
			clickevent(entity,4);
			break;
		case R.id.weibo2_pic_2_3:
			clickevent(entity,5);
			break;
		case R.id.weibo2_pic_3_1:
			clickevent(entity,6);
			break;
		case R.id.weibo2_pic_3_2:
			clickevent(entity,7);
			break;
		case R.id.weibo2_pic_3_3:
			clickevent(entity,8);
			break;

		}
	}
	public void clickevent(Entity entity,int i){
		ArrayList<String> pic_urls;
		if(entity.getEntity2()== null){
			pic_urls = entity.getWeibo_pic();
		}else{
			pic_urls = entity.getEntity2().getWeibo_pic();
		}
		Intent intent = new Intent(mContext,BigPicActivity1.class);
		intent.putExtra("id", i);
		intent.putStringArrayListExtra("pic_urls", pic_urls);
		mContext.startActivity(intent);
	}
}

