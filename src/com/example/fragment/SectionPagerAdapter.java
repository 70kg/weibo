package com.example.fragment;

import java.util.List;

import com.example.tab_view.TabBarView.IconTabProvider;
import com.example.weibo.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionPagerAdapter extends FragmentPagerAdapter implements IconTabProvider{
	private String[] titles = new String[] { "Time Line", "@me", "comment" };
	List<Fragment> fragments;
	private int[] tab_icons={R.drawable.ic_tab1,
			R.drawable.ic_tab2,
			R.drawable.ic_tab3,
	};
	public SectionPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}
	public SectionPagerAdapter(FragmentManager fm,List<Fragment> fragments) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.fragments = fragments;
		
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}
	@Override
	public int getPageIconResId(int position) {
		// TODO Auto-generated method stub
		return tab_icons[position];
	}

}

