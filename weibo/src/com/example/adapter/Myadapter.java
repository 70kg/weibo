package com.example.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Util.Entity;
import com.example.loadimage.ImageLoader;
import com.example.weibo.CommentActivity;
import com.example.weibo.R;
import com.example.weibo.R.id;
import com.example.weibo.R.layout;

public class Myadapter extends BaseAdapter{
	ArrayList<Entity> list;
	LayoutInflater inflater;
	//关于图片的异步加载

	private ImageLoader mImageLoader;

	private Context mContext;

	private View weibo_item;

	public Myadapter(Context context,ArrayList<Entity> list){
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
		final Entity entity = list.get(position);
		ViewHolder holder;
		//if(convertView ==null){
		holder = new ViewHolder();
		convertView = inflater.inflate(R.layout.listitem, null);
		holder.name = (TextView)convertView.findViewById(R.id.name);
		holder.content = (TextView)convertView.findViewById(R.id.content);
		holder.image = (ImageView)convertView.findViewById(R.id.imageview);
		holder.image1 = (ImageView)convertView.findViewById(R.id.weibo2_pic_1_1);
		holder.image2 = (ImageView)convertView.findViewById(R.id.weibo2_pic_1_2);
		holder.image3 = (ImageView)convertView.findViewById(R.id.weibo2_pic_1_3);
		holder.image4 = (ImageView)convertView.findViewById(R.id.weibo2_pic_2_1);
		holder.image5 = (ImageView)convertView.findViewById(R.id.weibo2_pic_2_2);
		holder.image6= (ImageView)convertView.findViewById(R.id.weibo2_pic_2_3);
		holder.image7 = (ImageView)convertView.findViewById(R.id.weibo2_pic_3_1);
		holder.image8 = (ImageView)convertView.findViewById(R.id.weibo2_pic_3_2);
		holder.image9 = (ImageView)convertView.findViewById(R.id.weibo2_pic_3_3);
		holder.weibo2_content = (TextView)convertView.findViewById(R.id.weibo2_content);
		/*	
           convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}*/

		//控件赋值
		String url="";
		url = entity.getUser_picl();
		mImageLoader.DisplayImage(url, holder.image, false);
		holder.content.setText(entity.getContent());		
		holder.name.setText(entity.getName());
		//微博配图
		if(!(entity.getEntity2() ==null)){
			display(holder, entity.getEntity2().getWeibo_pic());
			holder.weibo2_content.setText( entity.getEntity2().getContent());
			holder.weibo2_content.setVisibility(View.VISIBLE);
			
		}else{
			View view = convertView.findViewById(R.id.weibo2_view);
			view.setBackground(null);
			display(holder, entity.getWeibo_pic());
		}
		weibo_item = (convertView).findViewById(R.id.weibo_item);
		weibo_item.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext,CommentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("weibo", entity);
				intent.putExtras(bundle);
				mContext.startActivity(intent);

			}
		});
		return convertView;
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
		TextView name;
		TextView content;
		ImageView image;
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
	}

}
