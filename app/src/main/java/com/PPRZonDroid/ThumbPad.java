package com.PPRZonDroid;

import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Created by benwelton on 5/30/17.
 */

public class ThumbPad {
	private int RIGHT;
	private int UP;
	private int LEFT;
	private int DOWN;

	public ViewGroup.LayoutParams params;
	private ViewGroup mLayout;

	private float x, y, distance, angle;

	 public ThumbPad(ViewGroup layout, int right, int up, int left, int down){
		mLayout = layout;

		params = mLayout.getLayoutParams();

		RIGHT = right;
		UP = up;
		LEFT = left;
		DOWN = down;
	}

	public int getRegion(MotionEvent arg1){

		x = arg1.getX() - (params.width/2) ;
		y = (params.height/2) - arg1.getY();
		distance = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y,2));
		angle = (float) cal_angle(x, y);

		if(distance<(params.width/2) && distance>=(params.width/5)) {
			if (angle < 45 || angle > 315) return RIGHT;
			else if (angle > 45 && angle < 135) return UP;
			else if (angle > 135 && angle < 225) return LEFT;
			else if (angle > 225 && angle < 315) return DOWN;
		}

		return 0;
	}

	private double cal_angle(float x, float y) {
		if(x >= 0 && y >= 0)
			return Math.toDegrees(Math.atan(y / x));
		else if(x < 0)
			return Math.toDegrees(Math.atan(y / x)) + 180;
		else if(x >= 0 && y < 0)
			return Math.toDegrees(Math.atan(y / x)) + 360;
		return 0;
	}

	public int getRight(){
	 	return RIGHT;
	}

	public int getUp(){
	 	return UP;
	}

	public int getLeft(){
	 	return LEFT;
	}

	public int getDown(){
	 	return DOWN;
	}
}
