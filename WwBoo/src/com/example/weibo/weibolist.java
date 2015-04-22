package com.example.weibo;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class weibolist extends ListView implements OnScrollListener {
	View footer;
	int totalItemCount;
	int lastVisibleItem;
	boolean isloading;
	ILoadListener listener;


	public weibolist(Context context,AttributeSet attrs ) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
		initView(context);
	}
	public weibolist(Context context,AttributeSet attrs,int defStyle ) {
		// TODO Auto-generated constructor stub
		super(context, attrs,defStyle);		initView(context);

	}

	public weibolist(Context context) {
		super(context);		initView(context);

		// TODO Auto-generated constructor stub
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if(totalItemCount == lastVisibleItem&& scrollState ==SCROLL_STATE_IDLE){
			if(!isloading){
				isloading=true;
				footer.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
				listener.onLoad();
			}
		}

	}
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		this.lastVisibleItem = firstVisibleItem+visibleItemCount;
		this.totalItemCount = totalItemCount;

	}
	private void initView(Context context){
		LayoutInflater inflater = LayoutInflater.from(context);
		footer = inflater.inflate(R.layout.footer, null);
		footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
		//this.setBackgroundColor(Color.WHITE);
		this.addFooterView(footer);

		this.setOnScrollListener(this);
	}
	
	
	public void loadcomplete(){
		isloading=false;
		footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
	}
	public void setInterface(ILoadListener listener){
		this.listener=listener;
	}
	public interface ILoadListener{
		public void onLoad();
	}

}
