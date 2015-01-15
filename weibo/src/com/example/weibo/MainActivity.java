package com.example.weibo;



import android.R.animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.from_sina.AccessTokenKeeper;
import com.example.from_sina.WBAuthCodeActivity;
import com.example.test_new_function.Animator;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;

public class MainActivity extends Activity {
	Button button;
	Button lookup;
	Oauth2AccessToken token;
	TextView textview;
	EditText editView;

	Button  button4;
	private static final String PREFERENCES_NAME = "com_weibo_sdk_android";

	private static final String KEY_UID           = "uid";
	private static final String KEY_ACCESS_TOKEN  = "access_token";
	private static final String KEY_EXPIRES_IN    = "expires_in";
	/** 用户信息接口 */
	private UsersAPI mUsersAPI;
	Button button2 ;
	Button button3;
	/** 用于获取微博信息流等操作的API */
	private StatusesAPI mStatusesAPI;
	
	private Button animator_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// 获取当前已保存过的 Token
		token = AccessTokenKeeper.readAccessToken(this);
		// 获取用户信息接口
		mUsersAPI = new UsersAPI(token);

		mStatusesAPI = new StatusesAPI(token);

		start();
		lookupToken();
		username();
		updata();
		button4 = (Button)findViewById(R.id.button4);
		animator_button = (Button)findViewById(R.id.animator_button);
		
		animator_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,Animator.class));
			}
		});
		button4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this,weibolistActivity.class));
			}
		});

	}
	public void lookupToken(){
		lookup = (Button)findViewById(R.id.button1);
		lookup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				token=AccessTokenKeeper.readAccessToken(MainActivity.this);
				String ui = token.getUid();
				/* SharedPreferences pref = getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
				 String ui = pref.getString("uid", null);*/
				Toast.makeText(MainActivity.this, ui, 1500).show();


			}
		});
	}
	public void start(){
		button = (Button)findViewById(R.id.shouquan);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(MainActivity.this, "123", 1500).show();
				startActivity(new Intent(MainActivity.this,WBAuthCodeActivity.class));
			}
		});
	}

	public void username(){
		button2 = (Button)findViewById(R.id.button2);
		button2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (token != null && token.isSessionValid()){
					//token=AccessTokenKeeper.readAccessToken(MainActivity.this);
					long uid = Long.parseLong(token.getUid());
					mUsersAPI.show(uid, mListener);
				}
				else{
					Toast.makeText(MainActivity.this, "345", 1500).show();
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

				// 调用 User#parse 将JSON串解析成User对象
				User user = User.parse(response);
				if (user != null) {
					Toast.makeText(MainActivity.this, 
							"获取User信息成功，用户昵称：" + user.screen_name, 
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {

			ErrorInfo info = ErrorInfo.parse(e.getMessage());
			Toast.makeText(MainActivity.this, info.toString(), Toast.LENGTH_LONG).show();
		}
	};

	public void updata(){
		button3 = (Button)findViewById(R.id.button3);
		button3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editView = (EditText)findViewById(R.id.editView);
				String txt = editView.getText().toString();
				mStatusesAPI.update(txt, null, null, mListener1);
			}
		});
	}
	/**
	 * 微博 OpenAPI 回调接口。
	 */
	private RequestListener mListener1 = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {

				if (response.startsWith("{\"statuses\"")) {
					// 调用 StatusList#parse 解析字符串成微博列表对象
					StatusList statuses = StatusList.parse(response);
					if (statuses != null && statuses.total_number > 0) {
						Toast.makeText(MainActivity.this, 
								"获取微博信息流成功, 条数: " + statuses.statusList.size(), 
								Toast.LENGTH_LONG).show();
					}
				} else if (response.startsWith("{\"created_at\"")) {
					// 调用 Status#parse 解析字符串成微博对象
					Status status = Status.parse(response);
					Toast.makeText(MainActivity.this, 
							"发送一送微博成功, id = " + status.id, 
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {

			ErrorInfo info = ErrorInfo.parse(e.getMessage());
			Toast.makeText(MainActivity.this, info.toString(), Toast.LENGTH_LONG).show();
		}
	};


}
