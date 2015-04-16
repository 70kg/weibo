package com.example.fragment;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.example.Util.GroupEntity;
import com.example.from_sina.AccessTokenKeeper;
import com.example.from_sina.Constants;
import com.example.from_sina.WBAuthCodeActivity;
import com.example.loadimage.ImageLoader;
import com.example.weibo.MainActivity;
import com.example.weibo.R;
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
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;



public class ActionBarTab extends FragmentActivity implements 	ActionBar.TabListener {
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
	private ListView listview;
	private ArrayList<GroupEntity> list;
	//微博分组
	private GroupAPI mGroupAPI;
	private Oauth2AccessToken mAccessToken;
	MenuAdapter mMenuAdapter;
	private TextView left_name;
	private ImageView left_imageview;
	private UsersAPI mUsersAPI;
	private ImageLoader mImageLoader;
	private WeiboAuth mWeiboAuth;
	public ImageLoader getImageLoader(){
		return mImageLoader;
	}
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.action_main);
		mImageLoader = new ImageLoader(this);
		// 获取当前已保存过的 Token
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		Log.e("mAccessToken______", mAccessToken+"");
		mUsersAPI = new UsersAPI(mAccessToken);
		mGroupAPI = new GroupAPI(mAccessToken);

		ColorDrawable drawable = new ColorDrawable(Color.WHITE);

		getActionBar().setBackgroundDrawable(drawable);
		//初始化
		mViewPager = (ViewPager)findViewById(R.id.pager);

		listview = (ListView)findViewById(R.id.left_drawer);
		left_name = (TextView)findViewById(R.id.left_name);
		left_imageview = (ImageView)findViewById(R.id.left_image);
		// 初始化微博对象
		mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);

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
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mDrawerLayout.closeDrawers();
			}
		});

		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);

		//actionBar.setDisplayOptions(0);
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
		fragments.add(new fragment1());
		fragments.add(new fragment2());
		fragments.add(new fragment_3());

		mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(),fragments);
		mViewPager.setAdapter(mSectionPagerAdapter);
		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		mViewPager.setOffscreenPageLimit(3);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				actionBar.setSelectedNavigationItem(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		for(int i=0;i<mSectionPagerAdapter.getCount();i++){
			actionBar.addTab(actionBar.newTab().
					setText(mSectionPagerAdapter.getPageTitle(i))
					.setTabListener(this));

		}
		enableEmbeddedTabs(actionBar);
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
	private void showlistview(ArrayList<GroupEntity> list) {
		if(mMenuAdapter ==null){
			mMenuAdapter = new MenuAdapter(this, list);
			listview.setAdapter(mMenuAdapter);
		}else{
			mMenuAdapter.onDateChange(list);
		}

	}
	/**
	 * 反射修改tab到antionbar
	 * @param actionBar
	 */
	private void enableEmbeddedTabs(Object actionBar){
		try {
			Method setHasEmbeddedTabsMethod = actionBar.getClass().getDeclaredMethod("setHasEmbeddedTabs", boolean.class);
			setHasEmbeddedTabsMethod.setAccessible(true);
			setHasEmbeddedTabsMethod.invoke(actionBar, true);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {  
			return true;  
		}  
		// TODO Auto-generated method stub
		/*	boolean open = false;
		switch (item.getItemId()) {
		case android.R.id.home:
			if(!open){
				mDrawerLayout.openDrawer(Gravity.LEFT);
				open = true;
			}else{
				mDrawerLayout.closeDrawers();
				open = false;
			}

			break;

		}*/

		/*if(mActionBarDrawerToggle.onOptionsItemSelected(item)){
			return true;
		}*/
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
                Toast.makeText(ActionBarTab.this, 
                        R.string.weibosdk_demo_toast_obtain_code_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            
            String code = values.getString("code");
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(ActionBarTab.this, 
                        R.string.weibosdk_demo_toast_obtain_code_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            mCode = code;
            Toast.makeText(ActionBarTab.this, 
                    R.string.weibosdk_demo_toast_obtain_code_success, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(ActionBarTab.this, 
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            UIUtils.showToast(ActionBarTab.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG);
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
                    LogUtil.d(TAG, "Success! " + token.toString());
                    
                    mAccessToken = token;
                    String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                            new java.util.Date(mAccessToken.getExpiresTime()));
                    String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
                    AccessTokenKeeper.writeAccessToken(ActionBarTab.this, mAccessToken);
                    
                } else {
                    LogUtil.d(TAG, "Failed to receive access token");
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                LogUtil.e(TAG, "onWeiboException： " + e.getMessage());
                Toast.makeText(ActionBarTab.this, 
                        R.string.weibosdk_demo_toast_obtain_token_failed, Toast.LENGTH_SHORT).show();
			}
        });
    }

}

