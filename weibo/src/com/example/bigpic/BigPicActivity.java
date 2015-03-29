package com.example.bigpic;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.example.weibo.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class BigPicActivity  extends Activity{

	ZoomImageView imageView;
	Bitmap bitmap = null;
	private String uri;
	private String old_uri;
	private ArrayList<String> pic_urls;
	private int id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bigpic_layout);

		imageView = (ZoomImageView)findViewById(R.id.zoom_imageview);
		Intent intent = getIntent();
		pic_urls= intent.getStringArrayListExtra("pic_urls");
		id  = intent.getIntExtra("id", 0);
		old_uri = pic_urls.get(id);
		uri = old_uri.replace("thumbnail", "large");
		Log.e("大图activity里面的uri", uri);
		pic_thread();

	}
	
	//下载图片
	private void pic_thread(){
		final Handler handler = new Handler(){  
			public void handleMessage(Message msg){  
				imageView.setImageBitmap((Bitmap) msg.obj);
			}  
		}; 

		new Thread(new Runnable(){  

			@Override  
			public void run() {  
				try {  
					URL imageUrl = new URL(uri);  
					HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();  
					InputStream inputStream = conn.getInputStream();  
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);  
					Message msg = new Message();  
					msg.obj = bitmap;  
					handler.sendMessage(msg);  
				} catch (IOException e) {  
					// TODO Auto-generated catch block  
					e.printStackTrace();  
				}  
			}  
		}).start();  
	}
}
