package com.pp.droidnav;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PresenterActivity extends Activity {

	NetworkService mService = null;
	private boolean boundToService = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_presenter);
	}

	@Override
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

	public void buttonAction(View v) {
		Button pressedButton = (Button) v;

		if(NetworkService.tcpConnected && NetworkService.running)
		{
			switch(pressedButton.getId()){

				case R.id.buttonStart : mService.sendViaTCP("k", "116");
					 break;

				case R.id.buttonEnd : mService.sendViaTCP("k", "27");
					 break;

				case R.id.buttonFirst : mService.sendViaTCP("k", "36");
					 break;

				case R.id.buttonLast : mService.sendViaTCP("k", "35");
					 break;

				case R.id.buttonBlack : mService.sendViaTCP("k", "66");
					 break;

				case R.id.buttonWhite : mService.sendViaTCP("k", "87");
					 break;

				case R.id.buttonPrev : mService.sendViaTCP("k", "37");
					 break;

				case R.id.buttonNext : mService.sendViaTCP("k", "39");
					 break;
			}
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
			switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
				if (action == KeyEvent.ACTION_UP) {
					mService.sendViaTCP("k", "39");
				}
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if (action == KeyEvent.ACTION_DOWN) {
					mService.sendViaTCP("k", "37");
				}
				return true;
			default:
				return super.dispatchKeyEvent(event);
			}
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
}
