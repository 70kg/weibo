package com.example.adapter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.Util.Entity;
import com.example.Util.LogUtil;
import com.example.Util.StringUtil;
import com.example.loadimage.ImageLoader;
import com.example.weibo.R;
import com.example.weibo.RoundImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class commentadapter extends BaseAdapter{
	ArrayList<Entity> list;
	LayoutInflater inflater;
	//关于图片的异步加载
	private ImageLoader mImageLoader;
	private Context mContext;

	public commentadapter(Context context,ArrayList<Entity> list) {
		this.mContext=context;
		this.list = list;
		this.inflater=LayoutInflater.from(context);
		mImageLoader = new ImageLoader(context);
	}
	public ImageLoader getImageLoader(){
		return mImageLoader;
	}
	public void onDateChange(ArrayList<Entity> list){
		this.list=list;
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return list.size();
	}
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity entity = list.get(position);
		ViewHolder holder = new ViewHolder();
		convertView = inflater.inflate(R.layout.comment_item, null);
		holder.content = (TextView)convertView.findViewById(R.id.comment_content);
		holder.name= (TextView)convertView.findViewById(R.id.comment_name);
		holder.image = (RoundImageView)convertView.findViewById(R.id.comment_pic);
		holder.from_type = (TextView)convertView.findViewById(R.id.from_type);
		holder.time = (TextView)convertView.findViewById(R.id.time);

		holder.name.setText(entity.getName());
		holder.name.getPaint().setFakeBoldText(true);
		holder.content.setText(entity.getContent());
		StringUtil.extractMention2Link(holder.content);
		mImageLoader.DisplayImage(entity.getUser_pic(), holder.image, false);
		//设置时间和来源
		if(entity.getTime()!=null){
			holder.time.setText(entity.getTime().substring(10,16));
		}
		if(entity.getFrom_type()!=null){
			String type = entity.getFrom_type();
			Pattern p = Pattern.compile("\\>(.*?)\\<");
			Matcher m = p.matcher(type);
			while(m.find()){
				holder.from_type.setText(m.group(1));
			}
		}
		return convertView;
	}
	class ViewHolder{
		RoundImageView image;
		TextView name;
		TextView from_type;
		TextView time;
		TextView content;
	}
}
