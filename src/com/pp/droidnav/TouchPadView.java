package com.pp.droidnav;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

public class TouchPadView extends View  {

	int x=0, y=0, oldX=0, oldY=0;

	public TouchPadView(Context context) {
		 super(context);
	}

	public TouchPadView(Context context, AttributeSet attrs) {
		super(context, attrs);
}

	public TouchPadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		String payload = null;
		int action = event.getAction();
		switch(action)
		{
			case MotionEvent.ACTION_DOWN :  oldX = (int) event.getX();
											oldY = (int) event.getY();
											break;
			case MotionEvent.ACTION_MOVE :  x = ((int)event.getX() - oldX);
											y = ((int)event.getY() - oldY);
											oldX = (int) event.getX();
											oldY = (int) event.getY();
											payload = x + " " + y;
											TouchpadActivity.mService.sendViaUDP("m", payload);
											break;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	private final GestureDetector gestureDetector = new GestureDetector(new TouchPadGestureListener());

	private class TouchPadGestureListener extends SimpleOnGestureListener {

		@Override
			public boolean onDown(MotionEvent event) {
			return true;
			}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.v(Common.TAG,"Double tap");
			TouchpadActivity.mService.sendViaTCP("c", "l");
			TouchpadActivity.mService.sendViaTCP("c", "l");
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.v(Common.TAG,"Single tap");
			TouchpadActivity.mService.sendViaTCP("c", "l");
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			Log.v(Common.TAG,"Long press");
			TouchpadActivity.mService.sendViaTCP("c", "r");
			return;
		}
	}
}
