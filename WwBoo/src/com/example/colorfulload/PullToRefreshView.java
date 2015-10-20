package com.example.colorfulload;

import java.security.InvalidParameterException;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.example.weibo.R;

public class PullToRefreshView extends ViewGroup {

	private static final int DRAG_MAX_DISTANCE = 120;
	private static final float DRAG_RATE = .5f;
	private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

	public static final int STYLE_SUN = 0;
	public static final int MAX_OFFSET_ANIMATION_DURATION = 700;

	private static final int INVALID_POINTER = -1;

	private View mTarget;
	private ImageView mRefreshView;
	private Interpolator mDecelerateInterpolator;//开始快  后面慢
	private int mTouchSlop;
	private int mTotalDragDistance;
	private BaseRefreshView mBaseRefreshView;
	private float mCurrentDragPercent;
	private int mCurrentOffsetTop;
	private boolean mRefreshing;
	private int mActivePointerId;
	private boolean mIsBeingDragged;
	private float mInitialMotionY;
	private int mFrom;
	private float mFromDragPercent;
	private boolean mNotify;
	private OnRefreshListener mListener;

	public PullToRefreshView(Context context) {
		this(context, null);
	}

	public PullToRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RefreshView);
		final int type = a.getInteger(R.styleable.RefreshView_type, STYLE_SUN);
		a.recycle();

		mDecelerateInterpolator = new BounceInterpolator();
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mTotalDragDistance = Utils.convertDpToPixel(context, DRAG_MAX_DISTANCE);//最大滑动120

		mRefreshView = new ImageView(context);

		setRefreshStyle(type);

		addView(mRefreshView);

		setWillNotDraw(false);
		// ViewCompat.setChildrenDrawingOrderEnabled(this, true);
	}

	public void setRefreshStyle(int type) {
		setRefreshing(false);
		switch (type) {
		case STYLE_SUN:
			mBaseRefreshView = new SunRefreshView(getContext(), this);
			break;
		default:
			throw new InvalidParameterException("Type does not exist");
		}
		mRefreshView.setImageDrawable(mBaseRefreshView);
	}

	public int getTotalDragDistance() {
		return mTotalDragDistance;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		ensureTarget();
		if (mTarget == null)
			return;

		widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingRight() - getPaddingLeft(), MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
		mTarget.measure(widthMeasureSpec, heightMeasureSpec);
		mRefreshView.measure(widthMeasureSpec, heightMeasureSpec);
	}
	/**
	 * 获取listview
	 */
	private void ensureTarget() {
		if (mTarget != null)
			return;
		if (getChildCount() > 0) {
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				if (child != mRefreshView)
					mTarget = child;
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (!isEnabled() || canChildScrollUp() || mRefreshing) {
			return false;
		}

		final int action = MotionEventCompat.getActionMasked(ev);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			setTargetOffsetTop(0, true);
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			mIsBeingDragged = false;
			final float initialMotionY = getMotionEventY(ev, mActivePointerId);
			if (initialMotionY == -1) {
				return false;
			}
			mInitialMotionY = initialMotionY;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mActivePointerId == INVALID_POINTER) {
				return false;
			}
			final float y = getMotionEventY(ev, mActivePointerId);
			if (y == -1) {
				return false;
			}
			final float yDiff = y - mInitialMotionY;
			if (yDiff > mTouchSlop && !mIsBeingDragged) {
				mIsBeingDragged = true;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mIsBeingDragged = false;
			mActivePointerId = INVALID_POINTER;
			break;
		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		}

		return mIsBeingDragged;
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent ev) {

		if (!mIsBeingDragged) {
			return super.onTouchEvent(ev);
		}

		final int action = MotionEventCompat.getActionMasked(ev);

		switch (action) {
		case MotionEvent.ACTION_MOVE: {
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
			if (pointerIndex < 0) {
				return false;
			}

			final float y = MotionEventCompat.getY(ev, pointerIndex);
			final float yDiff = y - mInitialMotionY;//手指移动距离
			final float scrollTop = yDiff * DRAG_RATE;//*0.5
			mCurrentDragPercent = scrollTop / mTotalDragDistance;
			//Log.e("mTotalDragDistance----", mTotalDragDistance+"");//330
			if (mCurrentDragPercent < 0) {
				return false;
			}
			float boundedDragPercent = Math.min(1f, Math.abs(mCurrentDragPercent));
			float extraOS = Math.abs(scrollTop) - mTotalDragDistance;
			float slingshotDist = mTotalDragDistance;//330
			float tensionSlingshotPercent = Math.max(0,
					Math.min(extraOS, slingshotDist * 2) / slingshotDist);
			
			//Log.e("tensionSlingshotPercent----", tensionSlingshotPercent+"");//0-0.23....

			float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
					(tensionSlingshotPercent / 4), 2)) * 2f;
			//Log.e("tensionPercent----", tensionPercent+"");//0-0.24...

			float extraMove = (slingshotDist) * tensionPercent / 2;
			int targetY = (int) ((slingshotDist * boundedDragPercent) + extraMove);
			//Log.e("mCurrentDragPercent----", mCurrentDragPercent+"");//0-0.24...

			mBaseRefreshView.setPercent(mCurrentDragPercent, true);//在这里将百分比穿进去的啊
			
			//Log.e("targetY - mCurrentOffsetTop----", targetY - mCurrentOffsetTop+"");//很小整数

			setTargetOffsetTop(targetY - mCurrentOffsetTop, true);//如果注释，向下滑动list没反应，targetY - mCurrentOffsetTop一直变大
			break;
		}
		case MotionEventCompat.ACTION_POINTER_DOWN:
			final int index = MotionEventCompat.getActionIndex(ev);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			break;
		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: {
			if (mActivePointerId == INVALID_POINTER) {
				return false;
			}
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
			final float y = MotionEventCompat.getY(ev, pointerIndex);
			final float overScrollTop = (y - mInitialMotionY) * DRAG_RATE;
			mIsBeingDragged = false;
			if (overScrollTop > mTotalDragDistance) {
				setRefreshing(true, true);
			} else {
				mRefreshing = false;
				animateOffsetToStartPosition();
			}
			mActivePointerId = INVALID_POINTER;
			return false;
		}
		}

		return true;
	}

	/**
	 * 滚到起始位置
	 */
	private void animateOffsetToStartPosition() {
		mFrom = mCurrentOffsetTop;
		//Log.e("mFrom-====", mFrom+"");330
		mFromDragPercent = mCurrentDragPercent;
		long animationDuration = Math.abs((long) (MAX_OFFSET_ANIMATION_DURATION * mFromDragPercent));

		mAnimateToStartPosition.reset();
		mAnimateToStartPosition.setDuration(animationDuration);
		mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
		mAnimateToStartPosition.setAnimationListener(mToStartListener);
		
		mRefreshView.clearAnimation();
		mRefreshView.startAnimation(mAnimateToStartPosition);
	}

	/**
	 * 滚到正确位置,这个方法只在正在刷新时调用
	 */
	private void animateOffsetToCorrectPosition() {
		mFrom = mCurrentOffsetTop;
		mFromDragPercent = mCurrentDragPercent;
		//Log.e("mFrom-====", mFrom+"");330+

		mAnimateToCorrectPosition.reset();
		//mAnimateToCorrectPosition.setDuration(MAX_OFFSET_ANIMATION_DURATION);
		mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
		
		mRefreshView.clearAnimation();
		mRefreshView.startAnimation(mAnimateToCorrectPosition);

		if (mRefreshing) {
			mBaseRefreshView.start();
			if (mNotify) {
				if (mListener != null) {
					mListener.onRefresh();
				}
			}
		} else {
			mBaseRefreshView.stop();
			animateOffsetToStartPosition();
		}
		mCurrentOffsetTop = mTarget.getTop();
	}
	
	/**
	 * 滚动到起始位置的动画
	 */
	private final Animation mAnimateToStartPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			//Log.e("mAnimateToStartPosition----interpolatedTime", interpolatedTime+"");

			moveToStart(interpolatedTime);
		}
	};

	/**
	 * 滚动正确位置动画
	 */
	private final Animation mAnimateToCorrectPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			//Log.e("----interpolatedTime", interpolatedTime+"");
			int targetTop;
			int endTarget = mTotalDragDistance;
			targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
			int offset = targetTop - mTarget.getTop();

			mCurrentDragPercent = mFromDragPercent - (mFromDragPercent - 1.0f) * interpolatedTime;
			//Log.e("----mCurrentDragPercent", mCurrentDragPercent+"");

			//mBaseRefreshView.setPercent(mCurrentDragPercent, false);

			setTargetOffsetTop(offset, false /* requires update */);
		}
	};

	private void moveToStart(float interpolatedTime) {
		int targetTop = mFrom - (int) (mFrom * interpolatedTime);
		float targetPercent = mFromDragPercent * (1.0f - interpolatedTime);
		int offset = targetTop - mTarget.getTop();

		mCurrentDragPercent = targetPercent;
		mBaseRefreshView.setPercent(mCurrentDragPercent, true);
		setTargetOffsetTop(offset, false);
	}

	public void setRefreshing(boolean refreshing) {
		if (mRefreshing != refreshing) {
			setRefreshing(refreshing, false /* notify */);
		}
	}

	private void setRefreshing(boolean refreshing, final boolean notify) {
		if (mRefreshing != refreshing) {
			mNotify = notify;
			ensureTarget();
			mRefreshing = refreshing;
			if (mRefreshing) {
				mBaseRefreshView.setPercent(1f, true);
				animateOffsetToCorrectPosition();
			} else {
				animateOffsetToStartPosition();
			}
		}
	}

	private Animation.AnimationListener mToStartListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mBaseRefreshView.stop();
			mCurrentOffsetTop = mTarget.getTop();
		}
	};

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId) {
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
		}
	}

	private float getMotionEventY(MotionEvent ev, int activePointerId) {
		final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
		if (index < 0) {
			return -1;
		}
		return MotionEventCompat.getY(ev, index);
	}

	private void setTargetOffsetTop(int offset, boolean requiresUpdate) {
		mTarget.offsetTopAndBottom(offset);
		mBaseRefreshView.offsetTopAndBottom(offset);
		mCurrentOffsetTop = mTarget.getTop();
		if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
			invalidate();
		}
	}

	private boolean canChildScrollUp() {
		if (android.os.Build.VERSION.SDK_INT < 14) {
			if (mTarget instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) mTarget;
				return absListView.getChildCount() > 0
						&& (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
								.getTop() < absListView.getPaddingTop());
			} else {
				return mTarget.getScrollY() > 0;
			}
		} else {
			return ViewCompat.canScrollVertically(mTarget, -1);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		ensureTarget();
		if (mTarget == null)
			return;

		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		int left = getPaddingLeft();
		int top = getPaddingTop();
		int right = getPaddingRight();
		int bottom = getPaddingBottom();

		mTarget.layout(left, top + mCurrentOffsetTop, left + width - right, top + height - bottom + mCurrentOffsetTop);
		mRefreshView.layout(left, top, left + width - right, top + height - bottom);
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		mListener = listener;
	}

	public static interface OnRefreshListener {
		public void onRefresh();
	}

}
