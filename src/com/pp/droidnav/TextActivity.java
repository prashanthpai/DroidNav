package com.pp.droidnav;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.ClipboardManager;

@SuppressWarnings("deprecation")
public class TextActivity extends Activity {

	NetworkService mService = null;
	private boolean boundToService = false;

	private ClipboardManager cm;
	private EditText editText;
	private String text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text);
		cm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		editText = (EditText) findViewById(R.id.editText);

		Button buttonPaste = (Button) findViewById(R.id.buttonPaste);
		Button buttonSpeak = (Button) findViewById(R.id.buttonSpeak);
		
		buttonPaste.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				editText = (EditText) findViewById(R.id.editText);
				text = editText.getText().toString().replace("\n", " ");
				if(NetworkService.tcpConnected && NetworkService.running)
					mService.sendViaTCP("t", text);
			}
		});
		
		buttonSpeak.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				editText = (EditText) findViewById(R.id.editText);
				text = editText.getText().toString().replace("\n", " ");
				if(NetworkService.tcpConnected && NetworkService.running)
					mService.sendViaTCP("a", text);
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
		editText.setText(cm.getText());
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
