package com.example.weibo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundImageView extends ImageView {
	//绘图的paint
	private Paint mBitmapPaint;
	//矩阵 用于放大缩小
	private Matrix mMatrix;
	//渲染图像，使用图像为绘制图像着色
	private BitmapShader mBitmapShader;
	//圆角的半径
	private int mRadius;
	//view的宽度
	private int mWidth;
	public RoundImageView(Context context)
	{
		this(context, null);
	}
	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mMatrix = new Matrix();
		mBitmapPaint = new Paint();
		mBitmapPaint.setAntiAlias(true);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
		mRadius = mWidth / 2;
		setMeasuredDimension(mWidth, mWidth);
	}
	/**
	 * 初始化BitmapShader
	 */
	private void setUpShader(){
		Drawable drawable = getDrawable();
		if (drawable == null)
		{
			return;
		}

		Bitmap bmp = drawableToBitamp(drawable);
		// 将bmp作为着色器，就是在指定区域内绘制bmp
		mBitmapShader = new BitmapShader(bmp, TileMode.CLAMP, TileMode.CLAMP);
		float scale = 1.0f;

		// 拿到bitmap宽或高的小值
		int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
		scale = mWidth * 1.0f / bSize;

		if (!(bmp.getWidth() == getWidth() && bmp.getHeight() == getHeight()))
		{
			// 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
			scale = Math.max(getWidth() * 1.0f / bmp.getWidth(),
					getHeight() * 1.0f / bmp.getHeight());
		}
		// shader的变换矩阵，我们这里主要用于放大或者缩小
		mMatrix.setScale(scale, scale);
		// 设置变换矩阵
		mBitmapShader.setLocalMatrix(mMatrix);
		// 设置shader
		mBitmapPaint.setShader(mBitmapShader);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		if (getDrawable() == null)
		{
			return;
		}
		setUpShader();
		canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
	}
	
	
	private Bitmap drawableToBitamp(Drawable drawable) {
		if (drawable instanceof BitmapDrawable)
		{
			BitmapDrawable bd = (BitmapDrawable) drawable;
			return bd.getBitmap();
		}
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}
	

}
