package com.example.weibo;

import com.example.from_sina.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class UploadWeiBo extends Activity {
	private static int RESULT_LOAD_IMAGE = 1;

	private EditText weibocontent;
	private ImageView weiboimage;
	private Button selectibutton;
	private Button uploadweibo;
	private String weibo_content_text;
	/** 当前 Token 信息 */
	private Oauth2AccessToken mAccessToken;
	/** 用于获取微博信息流等操作的API */
	private StatusesAPI mStatusesAPI;
	Bitmap myBitmap;
	View v;
	PopupWindow popup;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uploadweibo);
		weibocontent = (EditText)findViewById(R.id.upload_weibo_content);
		weiboimage = (ImageView)findViewById(R.id.upload_weibo_image);
		selectibutton = (Button)findViewById(R.id.select_image);
		uploadweibo = (Button)findViewById(R.id.upload_weibo);
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		// 对statusAPI实例化
		mStatusesAPI = new StatusesAPI(mAccessToken);
		v = this.getLayoutInflater().inflate(R.layout.popwindow, null);
		 popup = new PopupWindow(v, 300, 300, false);
		/**
		 * 选择图片
		 */
		selectibutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);

			}
		});


		/**
		 * 发布微博
		 */
		uploadweibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popup.showAsDropDown(v);
			
				weibo_content_text = weibocontent.getText().toString();
				if(myBitmap==null){
					mStatusesAPI.update(weibo_content_text, "", "", mListener);
				}else{
					mStatusesAPI.upload(weibo_content_text, myBitmap, "", "", mListener);
				}

			}
		});

	}

	/**
	 * 微博 OpenAPI 回调接口。
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				if (response.startsWith("{\"created_at\"")) {
					// 调用 Status#parse 解析字符串成微博对象
					Status status = Status.parse(response);
					popup.dismiss();
					Toast.makeText(UploadWeiBo.this, 
							"发送一送微博成功, id = " + status.id, 
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(UploadWeiBo.this, response, Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {

		}
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();

			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			Log.e("picturePath", picturePath);
			cursor.close();
			myBitmap = BitmapFactory.decodeFile(picturePath);
			weiboimage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

		}

	}

}
