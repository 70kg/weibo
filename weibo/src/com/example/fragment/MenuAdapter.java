package com.example.fragment;

import com.example.weibo.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class MenuAdapter extends BaseAdapter  {
	private Activity context;

	MenuAdapter(Activity context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return 6;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = context.getLayoutInflater().inflate(
					R.layout.item_list, parent, false);
		}

		return convertView;
	}

}
