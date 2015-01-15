package com.example.test_new_function;


import com.example.weibo.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class easyview extends View {
	private static final int MIN_MOVE_DIS=5;
	
	private Bitmap fgBitmap,bgBitmap;
	
	private Canvas mcCanvas;
	
	private Paint mPaint;
	
	private Path mPath;
	
	private int screenW,screenH;
	
	private float preX,preY;
	
	public easyview(Context context, AttributeSet attrs) {
		super(context, attrs);
    //计算参数
		cal(context);
	//初始化对象
		init(context);
	
	}
	private void init(Context context) {
     //实例化路径对象
		mPath = new Path();
		//实例化画笔并开启抗锯齿和抗抖动
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
		//设置画笔透明度为0
		mPaint.setARGB(128, 255, 0, 0);
		//设置混合模式为DST_IN
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		
		//设置画笔风格为描边
		mPaint.setStyle(Paint.Style.STROKE);
		//设置路径结合处样式
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		//设置笔触类型
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		//设置描边宽度
		mPaint.setStrokeWidth(50);
		//生成前景图
		fgBitmap = Bitmap.createBitmap(screenW,screenH,Config.ARGB_8888);
		//将其注入画布
		mcCanvas = new Canvas(fgBitmap);
		//绘制画布背景为中性灰
		mcCanvas.drawColor(0xFF808080);
		//获取背景底图bitmap
		bgBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.a4);
		//缩放背景底图至屏幕大小
		bgBitmap = Bitmap.createScaledBitmap(bgBitmap,screenW,screenH,true);
		
		
		
	}
	
	private void cal(Context context) {
		// 获取屏幕尺寸数组
				int[] screenSize = MeasureUtil.getScreenSize((Activity) context);

				// 获取屏幕宽高
				screenW = screenSize[0];
				screenH = screenSize[1];
		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		//绘制背景
		canvas.drawBitmap(bgBitmap, 0, 0,null);
		//绘制前景
		canvas.drawBitmap(fgBitmap, 0, 0,null);
		
		mcCanvas.drawPath(mPath, mPaint);
	
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		/*
		 * 获取当前事件位置坐标
		 *
		 */
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mPath.reset();
			mPath.moveTo(x, y);
			preX =x;
			preY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			float dx = Math.abs(x - preX);
			float dy = Math.abs(y - preY);
			if (dx >= MIN_MOVE_DIS || dy >= MIN_MOVE_DIS) {
				mPath.quadTo(preX, preY, (x + preX) / 2, (y + preY) / 2);
				preX = x;
				preY = y;
			}
			break;
	
		}//重绘视图
		invalidate();
		return true;
	}
	/**
	 * 测绘工具类
	 * 
	 * @author Aige
	 * @since 2014/11/19
	 */
	public final static class MeasureUtil {
		/**
		 * 获取屏幕尺寸
		 * 
		 * @param activity
		 *            Activity
		 * @return 屏幕尺寸像素值，下标为0的值为宽，下标为1的值为高
		 */
		public static int[] getScreenSize(Activity activity) {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			return new int[] { metrics.widthPixels, metrics.heightPixels };
		}
	}
}

