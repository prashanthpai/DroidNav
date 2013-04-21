package com.pp.droidnav;

import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

public class TouchpadActivity extends Activity {

	static NetworkService mService = null;
	private boolean boundToService = false;

	@SuppressWarnings("unused")
	private View touchpad = null;
	@SuppressWarnings("unused")
	private View scrollpad = null;
	private Button buttonLeftClick = null;
	private Button buttonRightClick = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_touchpad);
		touchpad = (View) findViewById(R.id.viewTouchPad);
		scrollpad = (View) findViewById(R.id.viewScrollPad);

		buttonLeftClick = (Button) findViewById(R.id.buttonLeftClick);
		buttonRightClick = (Button) findViewById(R.id.buttonRightClick);

		buttonLeftClick.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(NetworkService.tcpConnected && NetworkService.running)
					mService.sendViaTCP("c", "l");
			}
		});
		buttonRightClick.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(NetworkService.tcpConnected && NetworkService.running)
					mService.sendViaTCP("c", "r");
			}
		});
	}

	protected void onStart() {
		super.onStart();
		if(NetworkService.running) {
			Intent intent = new Intent(this, NetworkService.class);
			if(getApplicationContext().bindService(intent, mConnection, 0))
				Toast.makeText(this,"Connected to DroidNav service",Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(this,"DroidNav service not running!",Toast.LENGTH_SHORT).show();
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@SuppressWarnings("unchecked")
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((LocalBinder<NetworkService>) service).getService();
			boundToService = true;
		}
		@Override
		public void onServiceDisconnected(ComponentName className) {
			mService = null;
			boundToService = false;
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (boundToService && mConnection!=null) {
			getApplicationContext().unbindService(mConnection);
			boundToService = false;
			mService = null;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
				if (action == KeyEvent.ACTION_UP) {
					mService.sendViaTCP("v", "i");
				}
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if (action == KeyEvent.ACTION_DOWN) {
					mService.sendViaTCP("v", "d");
				}
				return true;
			default:
				return super.dispatchKeyEvent(event);
		}
	}
}
