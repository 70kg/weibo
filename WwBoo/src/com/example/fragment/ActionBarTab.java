package com.example.fragment;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.example.Util.GroupEntity;
import com.example.fragment.fragment1.ShowActionBar;
import com.example.from_sina.AccessTokenKeeper;
import com.example.from_sina.Constants;
import com.example.from_sina.WBAuthCodeActivity;
import com.example.loadimage.ImageLoader;
import com.example.tab_view.TabBarView;
import com.example.weibo.MainActivity;
import com.example.weibo.R;
import com.example.weibo.UploadWeiBo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.legacy.GroupAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.GroupList;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.UIUtils;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiConfiguration.Status;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;



public class ActionBarTab extends FragmentActivity implements ActionBar.TabListener,ShowActionBar {
	/**
	 * WeiboSDKDemo 程序的 APP_SECRET。
	 * 请注意：请务必妥善保管好自己的 APP_SECRET，不要直接暴露在程序中，此处仅作为一个DEMO来演示。
	 */
	private static final String WEIBO_DEMO_APP_SECRET = "29727f75db2e8f4e31a9090f6ba25bb2";
	/** 通过 code 获取 Token 的 URL */
	private static final String OAUTH2_ACCESS_TOKEN_URL = "https://open.weibo.cn/oauth2/access_token";
	/** 获取到的 Code */
	private String mCode;
	private String TAG = "ViewPager";
	SectionPagerAdapter mSectionPagerAdapter;
	ViewPager mViewPager;
	GroupEntity entity;
	ActionBarDrawerToggle mActionBarDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private ActionBar actionBar;
	private ListView left_listview;
	private ArrayList<GroupEntity> list;
	//微博分组
	private GroupAPI mGroupAPI;
	private Oauth2AccessToken mAccessToken;
	private MenuAdapter mMenuAdapter;
	private TextView left_name;
	private ImageView left_imageview;
	private Button left_login;
	private UsersAPI mUsersAPI;
	private ImageLoader mImageLoader;
	private WeiboAuth mWeiboAuth;

	private TabBarView tabBarView;

	private fragment1 fragment1;
	public ImageLoader getImageLoader(){
		return mImageLoader;
	}
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		//requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.action_main);
		fragment1 = new fragment1();
		//fragment1.setShowActionBar(this);
		LayoutInflater inflator =
				(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mImageLoader = new ImageLoader(this);
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		// 初始化微博对象
		mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
		mUsersAPI = new UsersAPI(mAccessToken);
		mGroupAPI = new GroupAPI(mAccessToken);

		ColorDrawable drawable = new ColorDrawable(this.getResources().getColor(R.drawable.action_bar_color));

		getActionBar().setBackgroundDrawable(drawable);

		//初始化
		mViewPager = (ViewPager)findViewById(R.id.pager);
		View v = inflator.inflate(R.layout.custom_ab, null);
		tabBarView = (TabBarView) v.findViewById(R.id.tab_bar);


		left_listview = (ListView)findViewById(R.id.left_drawer);
		left_name = (TextView)findViewById(R.id.left_name);
		left_imageview = (ImageView)findViewById(R.id.left_image);
		left_login = (Button)findViewById(R.id.login);
		left_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//startActivity(new Intent(ActionBarTab.this,WBAuthCodeActivity.class));
				mWeiboAuth.authorize(new AuthListener(), WeiboAuth.OBTAIN_AUTH_CODE);
				fetchTokenAsync(mCode, WEIBO_DEMO_APP_SECRET);
				Log.e("mAccessToken______", mAccessToken+"");
			}
		});

		//判断是否已经登录
		if(mAccessToken != null && mAccessToken.isSessionValid()){
			long uid = Long.parseLong(mAccessToken.getUid());
			mUsersAPI.show(uid, mListener);
		}else{
			//登录授权
			left_imageview.setImageResource(R.drawable.contact_icon);
			left_imageview.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mWeiboAuth.authorize(new AuthListener(), WeiboAuth.OBTAIN_AUTH_CODE);
					fetchTokenAsync(mCode, WEIBO_DEMO_APP_SECRET);
				}
			});
		}
		getDate();
		showlistview(list);
		/**
		 * 左侧listview点击事件
		 */
		left_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mDrawerLayout.closeDrawers();
			}
		});
		actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_SHOW_HOME);
		LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, 
				android.app.ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT|Gravity.BOTTOM); 
		actionBar.setCustomView(v, lp);
		actionBar.setDisplayHomeAsUpEnabled(true);


		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

		mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close){
			@Override
			public void onDrawerClosed(View drawerView) {
				invalidateOptionsMenu();
			}
			@Override
			public void onDrawerOpened(View drawerView) {

				invalidateOptionsMenu();
			}

		};
		mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(fragment1);
		fragments.add(new fragment2());
		fragments.add(new fragment_3());

		mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(),fragments);
		mViewPager.setAdapter(mSectionPagerAdapter);
		tabBarView.setViewPager(mViewPager);
		tabBarView.setStripHeight(3);
		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		mViewPager.setOffscreenPageLimit(3);


	}
	/**
	 * 获取用户信息
	 */
	private RequestListener mListener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				// 调用 User#parse 将JSON串解析成User对象
				User user = User.parse(response);
				if (user != null) {
					left_name.setText(user.screen_name);
					mImageLoader.DisplayImage(user.avatar_large, left_imageview, false);
				} 
			}
		}
		@Override
		public void onWeiboException(WeiboException e) {
		}
	};

	/**
	 * 获取分组信息
	 */
	private void getDate() {
		mGroupAPI.groups(listener);
	}
	public RequestListener listener  = new RequestListener() {

		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				GroupList  groups = GroupList.parse(response);
				if(groups !=null&&groups.total_number>0){
					for(int i=0;i<groups.groupList.size();i++){
						entity = new GroupEntity();
						entity.setName(groups.groupList.get(i).name);
						list.add(entity);
					}
					Log.e("list-----------", list+"");
					mMenuAdapter.onDateChange(list);
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException arg0) {

		}

	};
	/**
	 * 左侧listview绑定到adapter
	 * @param list
	 */
	private void showlistview(ArrayList<GroupEntity> list) {
		if(mMenuAdapter ==null){
			mMenuAdapter = new MenuAdapter(this, list);
			left_listview.setAdapter(mMenuAdapter);
		}else{
			mMenuAdapter.onDateChange(list);
		}

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {  
			return true;  
		}  
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);


		/*MenuItem item = menu.findItem(R.id.action_share);
		ShareActionProvider provider = (ShareActionProvider) item
				.getActionProvider();

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "share");
		intent.putExtra(Intent.EXTRA_TEXT, "share by Action Provider");
		provider.setShareIntent(intent);*/

		return true;
	}
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		mActionBarDrawerToggle.syncState();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		mActionBarDrawerToggle.onConfigurationChanged(newConfig);
	}
	/**
	 * 微博认证授权回调类。
	 */
	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			if (null == values) {
				return;
			}
			String code = values.getString("code");
			if (TextUtils.isEmpty(code)) {
				return;
			}
			mCode = code;
			Log.e("mcode", mCode+"");
		}
		@Override
		public void onCancel() {
		}

		@Override
		public void onWeiboException(WeiboException e) {
		}
	}

	/**
	 * 异步获取 Token。
	 * 
	 * @param authCode  授权 Code，该 Code 是一次性的，只能被获取一次 Token
	 * @param appSecret 应用程序的 APP_SECRET，请务必妥善保管好自己的 APP_SECRET，
	 *                  不要直接暴露在程序中，此处仅作为一个DEMO来演示。
	 */
	public void fetchTokenAsync(String authCode, String appSecret) {

		WeiboParameters requestParams = new WeiboParameters();
		requestParams.put(WBConstants.AUTH_PARAMS_CLIENT_ID,     Constants.APP_KEY);
		requestParams.put(WBConstants.AUTH_PARAMS_CLIENT_SECRET, appSecret);
		requestParams.put(WBConstants.AUTH_PARAMS_GRANT_TYPE,    "authorization_code");
		requestParams.put(WBConstants.AUTH_PARAMS_CODE,          authCode);
		requestParams.put(WBConstants.AUTH_PARAMS_REDIRECT_URL,  Constants.REDIRECT_URL);

		// 异步请求，获取 Token
		AsyncWeiboRunner.requestAsync(OAUTH2_ACCESS_TOKEN_URL, requestParams, "POST", new RequestListener() {
			@Override
			public void onComplete(String response) {
				LogUtil.d(TAG, "Response: " + response);
				// 获取 Token 成功
				Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(response);
				if (token != null && token.isSessionValid()) {
					Log.e("Success!------", token.toString());
					mAccessToken = token;
					AccessTokenKeeper.clear(ActionBarTab.this);
					AccessTokenKeeper.writeAccessToken(ActionBarTab.this, mAccessToken);
				} else {
					Log.e("Failed!------", token.toString());
				}
			}
			@Override
			public void onWeiboException(WeiboException e) {
			}
		});
	}
	/**
	 * 显示ActionBar
	 */
	@Override
	public void Show() {
		actionBar.show();
	}
	/**
	 * 隐藏ActionBar
	 */
	@Override
	public void Hold() {
		actionBar.hide();
	}


}

