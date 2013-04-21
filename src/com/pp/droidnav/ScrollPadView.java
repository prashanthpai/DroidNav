package com.pp.droidnav;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

public class ScrollPadView extends View {

	public ScrollPadView(Context context) {
		 super(context);
	}

	public ScrollPadView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollPadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	@SuppressWarnings("deprecation")
	private final GestureDetector gestureDetector = new GestureDetector(new ScrollPadGestureListener());

	private class ScrollPadGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent event) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (Math.abs(e1.getY() - e2.getY()) > Common.SCROLL_SWIPE_MAX_OFF_PATH)
				return false;
			if(e1.getY() - e2.getY() > Common.SCROLL_SWIPE_MIN_DISTANCE && Math.abs(velocityY) > Common.SCROLL_SWIPE_THRESHOLD_VELOCITY) {
				Log.v(Common.TAG,"UP");
				if(NetworkService.tcpConnected && NetworkService.running)
					TouchpadActivity.mService.sendViaTCP("s", "u");
				return true;
			} else if (e2.getY() - e1.getY() > Common.SCROLL_SWIPE_MIN_DISTANCE && Math.abs(velocityY) > Common.SCROLL_SWIPE_THRESHOLD_VELOCITY) {
				Log.v(Common.TAG,"DOWN");
				if(NetworkService.tcpConnected && NetworkService.running)
					TouchpadActivity.mService.sendViaTCP("s", "d");
				return true;
			}
			return false;
		}
	}
}
