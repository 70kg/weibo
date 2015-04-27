package com.example.adapter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.Util.CommentTimeLineEntity;
import com.example.Util.Entity;
import com.example.Util.StringUtil;
import com.example.loadimage.ImageLoader;
import com.example.weibo.R;
import com.example.weibo.RoundImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommentTimeLineAdapter extends BaseAdapter{
	private ArrayList<CommentTimeLineEntity> mCommentlist;
	LayoutInflater inflater;
	//关于图片的异步加载
	private ImageLoader mImageLoader;
	private Context mContext;
	public CommentTimeLineAdapter(Context mContext,ArrayList<CommentTimeLineEntity> commentlist){
		this.mCommentlist = commentlist;
		this.mContext = mContext;
		this.inflater=LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(mContext);
	}
	public ImageLoader getImageLoader(){
		return mImageLoader;
	}
	public void onDateChange(ArrayList<CommentTimeLineEntity> commentlist){
		this.mCommentlist=commentlist;
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return mCommentlist.size();
	}
	@Override
	public Object getItem(int position) {
		return mCommentlist.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CommentTimeLineEntity commententity = mCommentlist.get(position);
		ViewHolder holder = new ViewHolder();
		convertView= inflater.inflate(R.layout.comment_timeline_item, null);
		holder.image = (RoundImageView)convertView.findViewById(R.id.comment_time_user_image);
		holder.name = (TextView)convertView.findViewById(R.id.comment_name);
		holder.from_type = (TextView)convertView.findViewById(R.id.comment_from_type);
		holder.time = (TextView)convertView.findViewById(R.id.comment_time);
		holder.content = (TextView)convertView.findViewById(R.id.comment_content);
		holder.original_comment = (TextView)convertView.findViewById(R.id.original_comment);
		
		holder.name.setText(commententity.getUser().screen_name);
		holder.name.getPaint().setFakeBoldText(true);
		if(commententity.getSource()!=null){
			String type = commententity.getSource();
			Pattern p = Pattern.compile("\\>(.*?)\\<");
			Matcher m = p.matcher(type);
			while(m.find()){
				holder.from_type.setText(m.group(1));
			}
		}
		if(commententity.getCreated_at()!=null){
			holder.time.setText(commententity.getCreated_at().substring(10,16));
		}
		mImageLoader.DisplayImage(commententity.getUser().avatar_large, holder.image, false);
		
		if(commententity.getReply_comment()!=null){//评论来源评论，当本评论属于对另一评论的回复时
			holder.content.setText("回复@"+commententity.getReply_comment().user.screen_name+" :"+commententity.getText());
			holder.original_comment.setText("回复@"+commententity.getReply_comment().user.screen_name+" 的评论:"+commententity.getReply_comment().text);
		}else{//评论某人的微博
			holder.content.setText(commententity.getText());
			holder.original_comment.setText("评论@"+commententity.getStatus().user.screen_name+" 的微博:"+commententity.getStatus().text);
		}
		StringUtil.extractMention2Link(holder.content);
		StringUtil.extractMention2Link(holder.original_comment);
		return convertView;
	}
	class ViewHolder{
		RoundImageView image;
		TextView name;
		TextView from_type;
		TextView time;
		TextView content;
		TextView original_comment;
	}

}
