package com.example.adapter;

import java.util.ArrayList;

import org.w3c.dom.Text;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Util.Entity;
import com.example.bigpic.BigPicActivity;
import com.example.loadimage.ImageLoader;
import com.example.weibo.CommentActivity;
import com.example.weibo.R;
import com.example.weibo.R.id;
import com.example.weibo.R.layout;

public class Myadapter extends BaseAdapter implements OnClickListener{
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
		holder.repost_counts = (TextView)convertView.findViewById(R.id.reposts_count);
		holder.comment_counts = (TextView)convertView.findViewById(R.id.comment_count);
		holder.attitudes_counts = (TextView)convertView.findViewById(R.id.attitudes_counts);
		holder.repost_img = (ImageView)convertView.findViewById(R.id.repost_img);
		holder.comments_img = (ImageView)convertView.findViewById(R.id.comment_img);
		holder.attitudes_img = (ImageView)convertView.findViewById(R.id.attitudes_img);

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
		url = entity.getUser_pic();
		mImageLoader.DisplayImage(url, holder.image, false);
		holder.content.setText(entity.getContent());		
		holder.name.setText(entity.getName());
		if(entity.getReposts_count() !=0){
			holder.repost_counts.setText(entity.getReposts_count()+"");
		}else{
			holder.repost_img.setVisibility(View.GONE);
		}
		if(entity.getComments_counts() !=0){
			holder.comment_counts.setText(entity.getComments_counts()+"");

		}else{
			holder.comments_img.setVisibility(View.GONE);
		}
		if(entity.getAttitudes_count()!=0){
			holder.attitudes_counts.setText(entity.getAttitudes_count()+"");

		}else{
			holder.attitudes_img.setVisibility(View.GONE);
		}
		//Log.e("entity.getWeibo_pic()", entity.getWeibo_pic()+"");
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
				//Toast.makeText(mContext, entity.getComments_counts(), 1500).show();
				Intent intent = new Intent(mContext,CommentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("weibo", entity);
				intent.putExtras(bundle);
				mContext.startActivity(intent);

			}
		});
		holder.image.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext,entity.getName() , 1500).show();

			}
		});
		holder.image1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickevent(entity, 0);

			}
		});
		holder.image2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickevent(entity, 1);
				
			}
		});
		holder.image3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickevent(entity, 2);
				
			}
		});
		holder.image4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickevent(entity, 3);
				
			}
		});
		holder.image5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickevent(entity, 4);
				
			}
		});
		holder.image6.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickevent(entity, 5);
				
			}
		});
		holder.image7.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickevent(entity, 6);
				
			}
		});
		holder.image8.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickevent(entity, 7);
				
			}
		});
		holder.image9.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickevent(entity, 8);
				
			}
		});
		return convertView;
	}
	//图片的点击事件
	private void clickevent(Entity entity,int i){
		ArrayList<String> pic_urls;
		if(entity.getEntity2()== null){
			pic_urls = entity.getWeibo_pic();
		}else{
			pic_urls = entity.getEntity2().getWeibo_pic();
		}
		Intent intent = new Intent(mContext,BigPicActivity.class);
		intent.putExtra("id", i);
		intent.putStringArrayListExtra("pic_urls", pic_urls);
		mContext.startActivity(intent);
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
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.weibo2_pic_1_1:
			Toast.makeText(mContext, "peitu", 1500).show();
			break;

		default:
			break;
		}
	}
	/**
	 * 将多图缩略图地址改为原始图片地址
	 * @param thumbnail_url
	 * @return
	 */
	private ArrayList<String> ReplaceUrl(ArrayList<String> thumbnail_url){
		ArrayList<String> lage_url = null ;
		if(thumbnail_url !=null){
			for(int i=0;i<thumbnail_url.size();i++){
				String url = thumbnail_url.get(i);
				String url1 = url.replaceAll("thumbnail", "large");
				lage_url.add(url1);
			}
		}

		return lage_url;

	}

}
