package com.example.adapter;

import java.util.ArrayList;

import com.example.Util.Entity;
import com.example.loadimage.ImageLoader;
import com.example.weibo.R;

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
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Entity entity = list.get(position);
		ViewHolder holder = new ViewHolder();
		convertView = inflater.inflate(R.layout.comment_item, null);
		holder.content = (TextView)convertView.findViewById(R.id.comment_content);
		holder.name= (TextView)convertView.findViewById(R.id.comment_name);
		holder.pic = (ImageView)convertView.findViewById(R.id.comment_pic);

		holder.name.setText(entity.getName());
		holder.content.setText(entity.getContent());
		mImageLoader.DisplayImage(entity.getUser_pic(), holder.pic, false);
		return convertView;
	}
	class ViewHolder{
		ImageView pic;
		TextView name;
		TextView content;
	}
}
