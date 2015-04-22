package com.example.loadimage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {

	private MemoryCache memoryCache = new MemoryCache();
	private AbstractFileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	// 绾跨▼姹�
	private ExecutorService executorService;

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}
	//-----------
	public static int calculateInSampleSize(BitmapFactory.Options options,  
			int reqWidth, int reqHeight) {  
		// 源图片的高度和宽度  
		final int height = options.outHeight;  
		final int width = options.outWidth;  
		int inSampleSize = 1;  
		if (height > reqHeight || width > reqWidth) {  
			// 计算出实际宽高和目标宽高的比率  
			final int heightRatio = Math.round((float) height / (float) reqHeight);  
			final int widthRatio = Math.round((float) width / (float) reqWidth);  
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高  
			// 一定都会大于等于目标的宽和高。  
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;  
		}  
		return inSampleSize;  
	} 
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,  
			int reqWidth, int reqHeight) {  
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小  
		final BitmapFactory.Options options = new BitmapFactory.Options();  
		options.inJustDecodeBounds = true;  
		// 调用上面定义的方法计算inSampleSize值  
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);  
		// 使用获取到的inSampleSize值再次解析图片  
		options.inJustDecodeBounds = false;  
		return BitmapFactory.decodeResource(res, resId, options);  
	}
	//---------------
	// 鏈�富瑕佺殑鏂规硶
	public void DisplayImage(String url, ImageView imageView, boolean isLoadOnlyFromCache) {
		imageViews.put(imageView, url);
		// 鍏堜粠鍐呭瓨缂撳瓨涓煡鎵�

		Bitmap bitmap = memoryCache.get(url);

		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else if (!isLoadOnlyFromCache){

			// 鑻ユ病鏈夌殑璇濆垯寮�惎鏂扮嚎绋嬪姞杞藉浘鐗�
			queuePhoto(url, imageView);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);

		// 鍏堜粠鏂囦欢缂撳瓨涓煡鎵炬槸鍚︽湁
		Bitmap b = null;
		if (f != null && f.exists()){
			b = decodeFile(f,200,200);
		}
		if (b != null){
			return b;
		}
		// 鏈�悗浠庢寚瀹氱殑url涓笅杞藉浘鐗�
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f,200,200);
			//-----------------------------------------------------------
			return bitmap;
		} catch (Exception ex) {
			Log.e("", "getBitmap catch Exception...\nmessage = " + ex.getMessage());
			return null;
		}
	}

	// decode杩欎釜鍥剧墖骞朵笖鎸夋瘮渚嬬缉鏀句互鍑忓皯鍐呭瓨娑堣�锛岃櫄鎷熸満瀵规瘡寮犲浘鐗囩殑缂撳瓨澶у皬涔熸槸鏈夐檺鍒剁殑
	private Bitmap decodeFile(File f,int reqWidth, int reqHeight) {
		try {
			// decode image size
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, options);
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);  
			options.inJustDecodeBounds = false;  
			/*// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 500;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}*/

			// decode with inSampleSize
			//BitmapFactory.Options o2 = new BitmapFactory.Options();
			//o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, options);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			// 鏇存柊鐨勬搷浣滄斁鍦║I绾跨▼涓�
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 * 闃叉鍥剧墖閿欎綅
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 鐢ㄤ簬鍦║I绾跨▼涓洿鏂扮晫闈�
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);

		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			Log.e("", "CopyStream catch Exception...");
		}
	}


}
