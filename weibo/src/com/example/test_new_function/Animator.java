package com.example.test_new_function;

import java.util.ArrayList;
import java.util.List;

import com.example.weibo.R;

import android.R.animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

public class Animator extends Activity implements View.OnClickListener{
	private int[] res = {R.id.iv_a,R.id.iv_b,R.id.iv_c,R.id.iv_d,R.id.iv_e,R.id.iv_f,R.id.iv_g,R.id.iv_h};
	private List<ImageView> list = new ArrayList<ImageView>();
	private boolean flag =true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_new_function);

		for(int i=0;i<res.length;i++){
			ImageView imageview = (ImageView)findViewById(res[i]);
			imageview.setOnClickListener(this);
			list.add(imageview);
		}
	}

	public void move(View view){

		/*ObjectAnimator.ofFloat(imageview, "translationX", 0F,200F).setDuration(1000).start();
		ObjectAnimator.ofFloat(imageview, "translationY", 0F,200F).setDuration(1000).start();

		ObjectAnimator.ofFloat(imageview, "rotation", 0F,360F).setDuration(1000).start();*/
		/*	PropertyValuesHolder p1 = PropertyValuesHolder.ofFloat("translationX", 0F,200F);
		PropertyValuesHolder p2 = PropertyValuesHolder.ofFloat("translationY", 0F,200F);
		PropertyValuesHolder p3 = PropertyValuesHolder.ofFloat("rotation", 0F,360F);
		ObjectAnimator.ofPropertyValuesHolder(imageview, p1,p2,p3).setDuration(1000).start();
		 */
		/*    AnimatorSet set = new AnimatorSet();
      ObjectAnimator a1 = ObjectAnimator.ofFloat(imageview, "translationX", 0F,200F);
      ObjectAnimator a2 = ObjectAnimator.ofFloat(imageview, "translationY", 0F,200F);
      ObjectAnimator a3 = ObjectAnimator.ofFloat(imageview, "rotation", 0F,360F);
     // set.playSequentially(a1,a2,a3);
      set.play(a1).with(a2);
      set.play(a1).after(a3);
      set.setDuration(1000);
      set.addListener(new AnimatorListenerAdapter() {
    	  @Override
    	public void onAnimationEnd(android.animation.Animator animation) {
    		// TODO Auto-generated method stub
    		super.onAnimationEnd(animation);
    		Toast.makeText(Animator.this, "123", 1500).show();
    	}
	});
      set.start();


	}
		 */
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_a:
			if(flag){
				startanim();
			}else{
				closeanim();
			}
			
			break;

		default:
			break;
		}
	}

	private void closeanim() {
		for(int i=1;i<res.length;i++){
AnimatorSet set = new AnimatorSet();
			
ObjectAnimator animator = ObjectAnimator.ofFloat(list.get(i), "translationX", 500F*(float)Math.cos(Math.toRadians(15*(i-1))),0F);
ObjectAnimator animator1 = ObjectAnimator.ofFloat(list.get(i), "translationY", 500F*(float)Math.sin(Math.toRadians(15*(i-1))),0F);
			set.setDuration(500);
			set.play(animator1).with(animator);
			
			set.setStartDelay(200);
			set.start();
			flag = true;
		}
		
	}

	private void startanim() {
		// TODO Auto-generated method stub
		for(int i=1;i<res.length;i++){
			AnimatorSet set = new AnimatorSet();
			System.out.println(500F*(float)Math.sin(Math.toRadians(15*i)));
			
			ObjectAnimator animator = ObjectAnimator.ofFloat(list.get(i), "translationX", 0F,500F*(float)Math.cos(Math.toRadians(15*(i-1))));
			ObjectAnimator animator1 = ObjectAnimator.ofFloat(list.get(i), "translationY", 0F,500F*(float)Math.sin(Math.toRadians(15*(i-1))));
			set.setDuration(500);
			set.play(animator1).with(animator);
			set.setInterpolator(new BounceInterpolator());
			set.setStartDelay(200);
			set.start();
			flag = false;
		}
	}
}
