package com.example.fragment;

import java.util.ArrayList;

import com.example.Util.Entity;
import com.example.Util.GroupEntity;
import com.example.weibo.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class MenuAdapter extends BaseAdapter  {
	private Activity context;
	private ArrayList<GroupEntity> list;

	MenuAdapter(Activity context,ArrayList<GroupEntity> list) {
		this.context = context;
		this.list = list;
	}
	public void onDateChange(ArrayList<GroupEntity> list){
		this.list=list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return 5;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//GroupEntity entity = list.get(position);
		convertView = context.getLayoutInflater().inflate(
				R.layout.item_list, parent, false);
		TextView text = (TextView)convertView.findViewById(R.id.tv_group);
		text.setText("hahah");

		return convertView;
	}

}
