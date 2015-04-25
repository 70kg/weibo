package com.example.adapter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Text;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Util.Entity;
import com.example.bigpic.BigPicActivity;
import com.example.bigpic.BigPicActivity1;
import com.example.loadimage.ImageLoader;
import com.example.Util.Defs;
import com.example.weibo.CommentActivity;
import com.example.weibo.R;
import com.example.weibo.R.id;
import com.example.weibo.R.layout;
import com.example.weibo.RoundImageView;

public class Myadapter extends BaseAdapter implements OnClickListener{
	ArrayList<Entity> list;
	LayoutInflater inflater;
	private ImageLoader mImageLoader;

	private Context mContext;


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
		final Entity entity = list.get(position);
		ViewHolder holder;
		if(convertView ==null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.listitem, null);
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.content = (TextView)convertView.findViewById(R.id.content);
			holder.image = (RoundImageView)convertView.findViewById(R.id.imageview);
			holder.repost_counts = (TextView)convertView.findViewById(R.id.reposts_count);
			holder.comment_counts = (TextView)convertView.findViewById(R.id.comment_count);
			holder.attitudes_counts = (TextView)convertView.findViewById(R.id.attitudes_counts);
			holder.repost_img = (ImageView)convertView.findViewById(R.id.repost_img);
			holder.comments_img = (ImageView)convertView.findViewById(R.id.comment_img);
			holder.attitudes_img = (ImageView)convertView.findViewById(R.id.attitudes_img);
			holder.from_type = (TextView)convertView.findViewById(R.id.from_type);
			holder.time = (TextView)convertView.findViewById(R.id.time);

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

			convertView.setTag(holder);
			setTags(holder,entity);
		}else{
			holder = (ViewHolder)convertView.getTag();
			holder.image1.setVisibility(View.GONE);
			holder.image2.setVisibility(View.GONE);
			holder.image3.setVisibility(View.GONE);
			holder.image4.setVisibility(View.GONE);
			holder.image5.setVisibility(View.GONE);
			holder.image6.setVisibility(View.GONE);
			holder.image7.setVisibility(View.GONE);
			holder.image8.setVisibility(View.GONE);
			holder.image9.setVisibility(View.GONE);
			holder.repost_img.setVisibility(View.GONE);
			holder.comments_img.setVisibility(View.GONE);
			holder.attitudes_img.setVisibility(View.GONE);
			holder.repost_counts.setVisibility(View.GONE);
			holder.attitudes_counts.setVisibility(View.GONE);
			holder.comment_counts.setVisibility(View.GONE);
			setTags( holder,entity);
		}
		ToControltheAssignment(entity,holder,convertView);
		ImageviewClickEvent(holder,entity);
		return convertView;
	}
	/**
	 * 控件赋值
	 * @param entity
	 * @param holder
	 */
	private void ToControltheAssignment(final Entity entity, ViewHolder holder,View convertView) {

		View view = convertView.findViewById(R.id.weibo2_view);
		if(!(entity.getEntity2() ==null)){
			if((String)holder.weibo2_content.getTag() ==entity.getEntity2().getContent()){
				holder.weibo2_content.setText("@"+entity.getEntity2().getName()+" :"+entity.getEntity2().getContent());
				extractMention2Link(holder.weibo2_content);
				holder.weibo2_content.setAutoLinkMask(0x01);
				holder.weibo2_content.setVisibility(View.VISIBLE);
			}else{
				holder.weibo2_content.setVisibility(View.GONE);
			}
			//转发微博只有一张配图
			if(entity.getEntity2().getWeibo_pic()!=null&&entity.getEntity2().getWeibo_pic().size()==1){
				if(holder.image1.getTag() ==entity.getEntity2().getWeibo_pic().get(0)){
					holder.image1.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(entity.getEntity2().getWeibo_pic().get(0).replace("thumbnail", "bmiddle"), holder.image1, false);	
				}else{
					holder.image1.setVisibility(View.GONE);
				}
			}else{
				view.setBackgroundResource(R.drawable.popup);
				display(holder, entity.getEntity2().getWeibo_pic(),convertView);
			}

		}else{
			view.setBackground(null);
			//如果只有一张图片  显示大的
			if(entity.getWeibo_pic()!=null&&entity.getWeibo_pic().size()==1){
				if(holder.image1.getTag() ==entity.getWeibo_pic().get(0)){
					holder.image1.setVisibility(View.VISIBLE);
					mImageLoader.DisplayImage(entity.getWeibo_pic().get(0).replace("thumbnail", "bmiddle"), holder.image1, false);	
				}else{
					holder.image1.setVisibility(View.GONE);
				}
			}else{
				display(holder, entity.getWeibo_pic(),convertView);
			}

		}
		/**
		 * 微博item设置点击事件
		 */
	/*	weibo_item = convertView.findViewById(R.id.weibo_item);
		weibo_item.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,CommentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("weibo", entity);
				intent.putExtras(bundle);
				mContext.startActivity(intent);

			}
		});*/

		String url="";
		url = entity.getUser_pic();
		mImageLoader.DisplayImage(url, holder.image, false);

		holder.content.setText(entity.getContent());
		extractMention2Link(holder.content);
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

		if(entity.getReposts_count() !=0&&(int)holder.repost_img.getTag()==entity.getReposts_count()){
			holder.repost_counts.setVisibility(View.VISIBLE);
			holder.repost_counts.setText(entity.getReposts_count()+"");
			holder.repost_img.setVisibility(View.VISIBLE);
		}else{
			holder.repost_img.setVisibility(View.GONE);
		}
		if(entity.getComments_counts() !=0&&(int)holder.comments_img.getTag()==entity.getComments_counts()){
			holder.comment_counts.setVisibility(View.VISIBLE);
			holder.comment_counts.setText(entity.getComments_counts()+"");
			holder.comments_img.setVisibility(View.VISIBLE);
		}else{
			holder.comments_img.setVisibility(View.GONE);
		}
		if(entity.getAttitudes_count()!=0&&(int)holder.attitudes_img.getTag()==entity.getAttitudes_count()){
			holder.attitudes_counts.setVisibility(View.VISIBLE);
			holder.attitudes_counts.setText(entity.getAttitudes_count()+"");
			holder.attitudes_img.setVisibility(View.VISIBLE);
		}else{
			holder.attitudes_img.setVisibility(View.GONE);
		}
	}
	/**
	 * 设置Tags
	 * @param holder
	 * @param entity
	 */
	private void setTags(ViewHolder holder, Entity entity) {
		if(entity.getAttitudes_count() !=0){
			holder.attitudes_img.setTag(Integer.valueOf(entity.getAttitudes_count()));
		}else{
			holder.attitudes_img.setTag(null);
		}

		if(entity.getComments_counts() !=0){
			holder.comments_img.setTag(Integer.valueOf(entity.getComments_counts()));
		}else{
			holder.comments_img.setTag(null);
		}

		if(entity.getReposts_count() !=0){
			holder.repost_img.setTag(Integer.valueOf(entity.getReposts_count()));
		}else{
			holder.repost_img.setTag(null);
		}

		if(!(entity.getEntity2()==null)){
			holder.weibo2_content.setTag(String.valueOf(entity.getEntity2().getContent()));
		}else{
			holder.weibo2_content.setVisibility(View.GONE);
		}
		//-------------------
		if(entity.getWeibo_pic()!=null){
			holder.image1.setTag(String.valueOf(entity.getWeibo_pic().get(0)));
		}else if(entity.getEntity2() !=null){
			if(entity.getEntity2().getWeibo_pic()!=null){
				holder.image1.setTag(String.valueOf(entity.getEntity2().getWeibo_pic().get(0)));
			}
		}else{
			holder.image1.setTag(null);
		}
		//--------------------------------
		if(entity.getWeibo_pic()!=null&&entity.getWeibo_pic().size()>=2){
			holder.image2.setTag(String.valueOf(entity.getWeibo_pic().get(1)));
		}else if(entity.getEntity2() !=null){
			if(entity.getEntity2().getWeibo_pic()!=null&&entity.getEntity2().getWeibo_pic().size()>=2){
				holder.image2.setTag(String.valueOf(entity.getEntity2().getWeibo_pic().get(1)));
			}

		}else{
			holder.image2.setVisibility(View.GONE);
		}
		//--------------------------------
		if(entity.getWeibo_pic()!=null&&entity.getWeibo_pic().size()>=3){
			holder.image3.setTag(String.valueOf(entity.getWeibo_pic().get(2)));
			holder.image3.setVisibility(View.VISIBLE);
		}else if(entity.getEntity2() !=null){
			if(entity.getEntity2().getWeibo_pic()!=null&&entity.getEntity2().getWeibo_pic().size()>=3){
				holder.image3.setTag(String.valueOf(entity.getEntity2().getWeibo_pic().get(2)));
				holder.image3.setVisibility(View.VISIBLE);
			}

		}else{
			holder.image3.setVisibility(View.GONE);
		}
		//--------------------------------
		if(entity.getWeibo_pic()!=null&&entity.getWeibo_pic().size()>=4){
			holder.image4.setTag(String.valueOf(entity.getWeibo_pic().get(3)));
			holder.image4.setVisibility(View.VISIBLE);
		}else if(entity.getEntity2() !=null){
			if(entity.getEntity2().getWeibo_pic()!=null&&entity.getEntity2().getWeibo_pic().size()>=4){
				holder.image4.setTag(String.valueOf(entity.getEntity2().getWeibo_pic().get(3)));
				holder.image4.setVisibility(View.VISIBLE);
			}

		}else{
			holder.image4.setVisibility(View.GONE);
		}
		//--------------------------------
		if(entity.getWeibo_pic()!=null&&entity.getWeibo_pic().size()>=5){
			holder.image5.setTag(String.valueOf(entity.getWeibo_pic().get(4)));
			holder.image5.setVisibility(View.VISIBLE);
		}else if(entity.getEntity2() !=null){
			if(entity.getEntity2().getWeibo_pic()!=null&&entity.getEntity2().getWeibo_pic().size()>=5){
				holder.image5.setTag(String.valueOf(entity.getEntity2().getWeibo_pic().get(4)));
				holder.image5.setVisibility(View.VISIBLE);
			}

		}else{
			holder.image5.setVisibility(View.GONE);
		}
		//--------------------------------
		if(entity.getWeibo_pic()!=null&&entity.getWeibo_pic().size()>=6){
			holder.image6.setTag(String.valueOf(entity.getWeibo_pic().get(5)));
			holder.image6.setVisibility(View.VISIBLE);
		}else if(entity.getEntity2() !=null){
			if(entity.getEntity2().getWeibo_pic()!=null&&entity.getEntity2().getWeibo_pic().size()>=6){
				holder.image6.setTag(String.valueOf(entity.getEntity2().getWeibo_pic().get(5)));
				holder.image6.setVisibility(View.VISIBLE);
			}

		}else{
			holder.image6.setVisibility(View.GONE);
		}
		//--------------------------------
		if(entity.getWeibo_pic()!=null&&entity.getWeibo_pic().size()>=7){
			holder.image7.setTag(String.valueOf(entity.getWeibo_pic().get(6)));
			holder.image7.setVisibility(View.VISIBLE);
		}else if(entity.getEntity2() !=null){
			if(entity.getEntity2().getWeibo_pic()!=null&&entity.getEntity2().getWeibo_pic().size()>=7){
				holder.image7.setTag(String.valueOf(entity.getEntity2().getWeibo_pic().get(6)));
				holder.image7.setVisibility(View.VISIBLE);
			}

		}else{
			holder.image7.setVisibility(View.GONE);
		}
		//--------------------------------
		if(entity.getWeibo_pic()!=null&&entity.getWeibo_pic().size()>=8){
			holder.image8.setTag(String.valueOf(entity.getWeibo_pic().get(7)));
			holder.image8.setVisibility(View.VISIBLE);
		}else if(entity.getEntity2() !=null){
			if(entity.getEntity2().getWeibo_pic()!=null&&entity.getEntity2().getWeibo_pic().size()>=8){
				holder.image8.setTag(String.valueOf(entity.getEntity2().getWeibo_pic().get(7)));
				holder.image8.setVisibility(View.VISIBLE);
			}

		}else{
			holder.image8.setVisibility(View.GONE);
		}
		//--------------------------------
		if(entity.getWeibo_pic()!=null&&entity.getWeibo_pic().size()>=9){
			holder.image9.setTag(String.valueOf(entity.getWeibo_pic().get(8)));
			holder.image9.setVisibility(View.VISIBLE);
		}else if(entity.getEntity2() !=null){
			if(entity.getEntity2().getWeibo_pic()!=null&&entity.getEntity2().getWeibo_pic().size()>=9){
				holder.image9.setTag(String.valueOf(entity.getEntity2().getWeibo_pic().get(8)));
				holder.image9.setVisibility(View.VISIBLE);
			}

		}else{
			holder.image9.setVisibility(View.GONE);
		}
	}
	/**
	 * 图片的点击事件
	 * @param holder
	 * @param entity
	 */
	private void ImageviewClickEvent(ViewHolder holder,final Entity entity) {
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

	}
	private void clickevent(Entity entity,int i){
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
	private void display(ViewHolder holder,ArrayList<String> pic2,View convertView){
		String new_pic_url;
		if(pic2 !=null){
			for (int i = 0; i < pic2.size(); i++) {
				new_pic_url=pic2.get(i).replace("thumbnail", "bmiddle");
				switch (i) {
				case 0:	
					ImageView image = (ImageView) convertView.findViewWithTag(pic2.get(i));
					if(image.getTag() ==pic2.get(i)){
						holder.image1.setVisibility(View.VISIBLE);
						holder.image1.setBackgroundResource(R.drawable.re_beijing);
						mImageLoader.DisplayImage(new_pic_url, image, false);	
					}else{
						image.setVisibility(View.GONE);
					}

					break;
				case 1:
					if(holder.image2.getTag() ==pic2.get(i)){
						holder.image2.setVisibility(View.VISIBLE);
						holder.image2.setBackgroundResource(R.drawable.re_beijing);
						mImageLoader.DisplayImage(new_pic_url, holder.image2, false);	
					}else{
						holder.image2.setVisibility(View.GONE);
					}
					break;
				case 2:
					if(holder.image3.getTag() ==pic2.get(i)){
						holder.image3.setVisibility(View.VISIBLE);
						holder.image3.setBackgroundResource(R.drawable.re_beijing);
						mImageLoader.DisplayImage(new_pic_url, holder.image3, false);	
					}else{
						holder.image3.setVisibility(View.GONE);
					}
					break;
				case 3:
					if(holder.image4.getTag() ==pic2.get(i)){
						holder.image4.setVisibility(View.VISIBLE);
						holder.image4.setBackgroundResource(R.drawable.re_beijing);
						mImageLoader.DisplayImage(new_pic_url, holder.image4, false);	
					}else{
						holder.image4.setVisibility(View.GONE);
					}
					break;
				case 4:
					if(holder.image5.getTag() ==pic2.get(i)){
						holder.image5.setVisibility(View.VISIBLE);
						holder.image5.setBackgroundResource(R.drawable.re_beijing);
						mImageLoader.DisplayImage(new_pic_url, holder.image5, false);	
					}else{
						holder.image5.setVisibility(View.GONE);
					}
					break;
				case 5:
					if(holder.image6.getTag() ==pic2.get(i)){
						holder.image6.setVisibility(View.VISIBLE);
						holder.image6.setBackgroundResource(R.drawable.re_beijing);
						mImageLoader.DisplayImage(new_pic_url, holder.image6, false);	
					}else{
						holder.image6.setVisibility(View.GONE);
					}
					break;
				case 6:
					if(holder.image7.getTag() ==pic2.get(i)){
						holder.image7.setVisibility(View.VISIBLE);
						holder.image7.setBackgroundResource(R.drawable.re_beijing);
						mImageLoader.DisplayImage(new_pic_url, holder.image7, false);	
					}else{
						holder.image7.setVisibility(View.GONE);
					}
					break;
				case 7:
					if(holder.image8.getTag() ==pic2.get(i)){
						holder.image8.setVisibility(View.VISIBLE);
						holder.image8.setBackgroundResource(R.drawable.re_beijing);
						mImageLoader.DisplayImage(new_pic_url, holder.image8, false);	
					}else{
						holder.image8.setVisibility(View.GONE);
					}
					break;
				case 8:
					if(holder.image9.getTag() ==pic2.get(i)){
						holder.image9.setVisibility(View.VISIBLE);
						holder.image9.setBackgroundResource(R.drawable.re_beijing);
						mImageLoader.DisplayImage(new_pic_url, holder.image9, false);	
					}else{
						holder.image9.setVisibility(View.GONE);
					}
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
	/**
	 * 微博字符串处理
	 * @param v
	 */
	public static void extractMention2Link(TextView v) {
		v.setAutoLinkMask(0);
		Pattern mentionsPattern = Pattern.compile("@(\\w+?\\-*?)(?=\\W|$)(.)");
		String mentionsScheme = String.format("%s/?%s=", Defs.MENTIONS_SCHEMA, Defs.PARAM_UID);
		Linkify.addLinks(v, mentionsPattern, mentionsScheme, new MatchFilter() {

			@Override
			public boolean acceptMatch(CharSequence s, int start, int end) {
				return s.charAt(end-1) !='.';
			}

		}, new TransformFilter() {
			@Override
			public String transformUrl(Matcher match, String url) {
				return match.group(1); 
			}
		});

		Pattern trendsPattern = Pattern.compile("#(\\w+?)#");
		String trendsScheme = String.format("%s/?%s=", Defs.TRENDS_SCHEMA, Defs.PARAM_UID);
		Linkify.addLinks(v, trendsPattern, trendsScheme, null, new TransformFilter() {
			@Override
			public String transformUrl(Matcher match, String url) {
				//Log.d(TAG, match.group(1));
				return match.group(1); 
			}
		});

	}

}
