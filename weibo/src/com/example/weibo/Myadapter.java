package com.example.weibo;

import java.util.ArrayList;






import com.example.weibo.loadimage.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Myadapter extends BaseAdapter{
	ArrayList<Entity> list;
	LayoutInflater inflater;
	//关于图片的异步加载
	private boolean mBusy =false;
	private ImageLoader mImageLoader;
	
	private Context mContext;
	private ArrayList<String> uriArrays;
	private ArrayList<String> pic_urls;
	
	
	public void setFlagBusy(boolean busy){
		this.mBusy=busy;
	}
	public Myadapter(Context context,ArrayList<String> url,ArrayList<String> pic_urls,ArrayList<Entity> list){
		this.mContext=context;
		this.uriArrays=url;
		this.list = list;
		this.pic_urls = pic_urls;
		this.inflater=LayoutInflater.from(context);
		mImageLoader = new ImageLoader(context);
	}
	public ImageLoader getImageLoader(){
		return mImageLoader;
	}
	
	
	
	public Myadapter(Context context,ArrayList<Entity> list){
		this.list = list;
		this.inflater=LayoutInflater.from(context);
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
		ViewHolder holder;
		if(convertView ==null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.listitem, null);
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.content = (TextView)convertView.findViewById(R.id.content);
			holder.image = (ImageView)convertView.findViewById(R.id.imageview);
			
           convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		String url="";
		url = uriArrays.get(position% uriArrays.size());
		
		mImageLoader.DisplayImage(url, holder.image, false);
		
		holder.content.setText(entity.getContent());
		
		holder.name.setText(entity.getName());
		return convertView;
	}
	class ViewHolder{
		TextView name;
		TextView content;
		ImageView image;
		ImageView image1;
		
	}

}
