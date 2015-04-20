package com.example.bigpic;


import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.bigpic.BigPicActivity1.AsyncImageLoader.ImageCallback;
import com.example.bigpic.ZoomImageView.click;
import com.example.weibo.R;

public class BigPicActivity1  extends Activity {

	Bitmap bitmap = null;
	private ArrayList<String> pic_urls;
	private ArrayList<String> new_pic_urls  = new ArrayList<String>();
	final Handler handler = new Handler(){  
		public void handleMessage(Message msg){  
			for(int i=0;i<new_pic_urls.size();i++){
				if(msg.what ==i){
					mImageViews.get(i).setImageBitmap((Bitmap) msg.obj);
				}

			}
		}	

	}; 

	private ViewPager mViewPager;
	private ArrayList<ImageView> mImageViews = new ArrayList<ImageView>();
	private int id;
	private List<ZoomImageView> mList = new ArrayList<ZoomImageView>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bigpic_layout);

		mViewPager = (ViewPager)findViewById(R.id.id_viewpager);

		Intent intent = getIntent();
		pic_urls= intent.getStringArrayListExtra("pic_urls");
		id = intent.getIntExtra("id", 0);
		/**
		 * 创建ZoomImageView集合，替换url,添加图片单击事件
		 */
		for(int i=0;i<pic_urls.size();i++){
			ZoomImageView item = new ZoomImageView(getApplicationContext());
			item.setclick(new click() {
				@Override
				public void click() {
					// TODO Auto-generated method stub
					finish();
				}
			});
			mList.add(item);
			new_pic_urls.add(pic_urls.get(i).replace("thumbnail", "large"));
		}

		mViewPager.setAdapter(new PagerAdapter() {

			private AsyncImageLoader asyncImageLoader= new AsyncImageLoader();  

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0==arg1;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return new_pic_urls.size();
			}
			@Override
			public Object instantiateItem(final ViewGroup container, final int position) {
				Drawable cachedImage = asyncImageLoader.loadDrawable(
						new_pic_urls.get(position), new ImageCallback() {

							public void imageLoaded(Drawable imageDrawable,
									String imageUrl) {
								ZoomImageView image =mList.get(position);
								image.setImageDrawable(imageDrawable);
								container.removeView(mList.get(position));
								container.addView(image);
							}
						});
				ZoomImageView image = mList.get(position);
				image.setImageDrawable(cachedImage);
				container.removeView(mList.get(position));
				container.addView(image);
				return mList.get(position);
			}
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				// TODO Auto-generated method stub
				container.removeView(mList.get(position));
			}
		});
		mViewPager.setCurrentItem(id);

	}

	/**
	 * 异步加载图片
	 */
	static class AsyncImageLoader {

		// 软引用，使用内存做临时缓存 （程序退出，或内存不够则清除软引用）
		private HashMap<String, SoftReference<Drawable>> imageCache;

		public AsyncImageLoader() {
			imageCache = new HashMap<String, SoftReference<Drawable>>();
		}

		/**
		 * 定义回调接口
		 */
		public interface ImageCallback {
			public void imageLoaded(Drawable imageDrawable, String imageUrl);
		}


		/**
		 * 创建子线程加载图片
		 * 子线程加载完图片交给handler处理（子线程不能更新ui，而handler处在主线程，可以更新ui）
		 * handler又交给imageCallback，imageCallback须要自己来实现，在这里可以对回调参数进行处理
		 *
		 * @param imageUrl ：须要加载的图片url
		 * @param imageCallback：
		 * @return
		 */
		public Drawable loadDrawable(final String imageUrl,
				final ImageCallback imageCallback) {

			//如果缓存中存在图片  ，则首先使用缓存
			if (imageCache.containsKey(imageUrl)) {
				SoftReference<Drawable> softReference = imageCache.get(imageUrl);
				Drawable drawable = softReference.get();
				if (drawable != null) {
					imageCallback.imageLoaded(drawable, imageUrl);//执行回调
					return drawable;
				}
			}

			/**
			 * 在主线程里执行回调，更新视图
			 */
			final Handler handler = new Handler() {
				public void handleMessage(Message message) {
					imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
				}
			};
			/**
			 * 创建子线程访问网络并加载图片 ，把结果交给handler处理
			 */
			new Thread() {
				@Override
				public void run() {
					Drawable drawable = loadImageFromUrl(imageUrl);
					// 下载完的图片放到缓存里
					imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);
				}
			}.start();

			return null;
		}


		/**
		 * 下载图片  （注意HttpClient 和httpUrlConnection的区别）
		 */
		public Drawable loadImageFromUrl(String url) {

			try {
				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000*15);
				HttpGet get = new HttpGet(url);
				HttpResponse response;

				response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();

					Drawable d = Drawable.createFromStream(entity.getContent(),
							"src");

					return d;
				} else {
					return null;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		//清除缓存
		public void clearCache() {

			if (this.imageCache.size() > 0) {

				this.imageCache.clear();
			}

		}

	}

	
}
