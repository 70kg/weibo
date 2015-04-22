package com.example.autoloadlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
public class AutoLoadListView extends ListView implements OnScrollListener{
	int totalItemCount;
	int lastVisibleItem;
	
	public AutoLoadListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init() ;
	}

	public AutoLoadListView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}

	public AutoLoadListView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}

	private LoadingFooter mLoadingFooter;

	private OnLoadNextListener mLoadNextListener;
	private void init() {
		mLoadingFooter = new LoadingFooter(getContext());
		addFooterView(mLoadingFooter.getView());
		setOnScrollListener(this);
	}

	public void setOnLoadNextListener(OnLoadNextListener listener) {
		mLoadNextListener = listener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (lastVisibleItem == totalItemCount
				&& totalItemCount != 0
				&& totalItemCount != (getHeaderViewsCount() + getFooterViewsCount())
				&& mLoadNextListener != null) {
			mLoadingFooter.setState(LoadingFooter.State.Loading);
			mLoadNextListener.onLoadNext();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		this.lastVisibleItem = firstVisibleItem+visibleItemCount;
		this.totalItemCount = totalItemCount;
		if (mLoadingFooter.getState() == LoadingFooter.State.Loading
				|| mLoadingFooter.getState() == LoadingFooter.State.TheEnd) {
			return;
		}
		
	}

	public void setState(LoadingFooter.State status) {
		mLoadingFooter.setState(status);
	}

	public void setState(LoadingFooter.State status, long delay) {
		mLoadingFooter.setState(status, delay);
	}

	public interface OnLoadNextListener {
		public void onLoadNext();
	}
	
	
	

}