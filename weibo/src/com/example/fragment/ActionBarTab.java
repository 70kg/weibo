package com.example.fragment;

import java.util.ArrayList;
import java.util.List;

import com.example.weibo.R;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.AdapterView.OnItemClickListener;



public class ActionBarTab extends FragmentActivity implements 	ActionBar.TabListener {
   private String TAG = "ViewPager";
	
	SectionPagerAdapter mSectionPagerAdapter;

	ViewPager mViewPager;

	ActionBarDrawerToggle mActionBarDrawerToggle;

	private DrawerLayout mDrawerLayout;

	private ActionBar actionBar;

	private ListView listview;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.action_main);

		ColorDrawable drawable = new ColorDrawable(getResources().getColor(android.R.color.holo_orange_light));

		getActionBar().setBackgroundDrawable(drawable);

		mViewPager = (ViewPager)findViewById(R.id.pager);

		listview = (ListView)findViewById(R.id.left_drawer);

		listview.setAdapter(new MenuAdapter(this));

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

		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

		mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close){
			@Override
			public void onDrawerClosed(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerClosed(drawerView);
				invalidateOptionsMenu();
			}
			@Override
			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerOpened(drawerView);
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
				// TODO Auto-generated method stub
				actionBar.setSelectedNavigationItem(arg0);
				//Log.e(TAG, arg0+"");
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				Log.e(TAG, "arg0--"+arg0+"  agr1--"+arg1+"  arg2--"+arg2);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				Log.e(TAG, "onPageScrollStateChanged--"+arg0+"");

			}
		});
		for(int i=0;i<mSectionPagerAdapter.getCount();i++){
			actionBar.addTab(actionBar.newTab().
					setText(mSectionPagerAdapter.getPageTitle(i))
					.setTabListener(this));
			
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

	
		MenuItem item = menu.findItem(R.id.action_share);
		ShareActionProvider provider = (ShareActionProvider) item
				.getActionProvider();
		
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "share");
		intent.putExtra(Intent.EXTRA_TEXT, "share by Action Provider");
		provider.setShareIntent(intent);

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

}

