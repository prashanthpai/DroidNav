package com.pp.droidnav;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Button;

public class ConnectionActivity extends Activity {

	private EditText editTextIP;
	private EditText editTextPort;
	private ToggleButton toggleButtonConnect;

	private String ServerIP = null;
	private int ServerPort = 0;
	private Pattern IPPattern = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection);

		IPPattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		editTextIP = (EditText) findViewById(R.id.editTextIP);
		editTextPort = (EditText) findViewById(R.id.editTextPort);
		toggleButtonConnect = (ToggleButton) findViewById(R.id.toggleButtonConnect);

		LoadPreferences();
		editTextIP.setText(ServerIP);
		editTextPort.setText(String.valueOf(ServerPort));

		Button buttonPresenter = (Button) findViewById(R.id.buttonPresenter);
		buttonPresenter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), PresenterActivity.class));
			}
		});

		Button buttonSensor = (Button) findViewById(R.id.buttonSensor);
		buttonSensor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), SensorActivity.class));
			}
		});

		Button buttonTouchpad = (Button) findViewById(R.id.buttonTouchpad);
		buttonTouchpad.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), TouchpadActivity.class));
			}
		});

		Button buttonText = (Button) findViewById(R.id.buttonText);
		buttonText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), TextActivity.class));
			}
		});

	}

	public void onToggleClicked(View view) {

		ServerIP = editTextIP.getText().toString();
		ServerPort = Integer.parseInt(editTextPort.getText().toString());
		boolean on = toggleButtonConnect.isChecked();
		toggleButtonConnect.setChecked(false);

		if (on && isValidIP(ServerIP)) {
			SavePreferences(ServerIP, ServerPort);
			if(!NetworkService.running) {
				Intent intent = new Intent(ConnectionActivity.this,NetworkService.class);
				if(startService(intent)!=null)
				{
					findViewById(R.id.linearLayoutFunctions).setVisibility(View.VISIBLE);
					toggleButtonConnect.setChecked(true);
				}
				else
					toggleButtonConnect.setChecked(false);
			}
		} else if(!on) {
			if(NetworkService.running)
			{
				stopService(new Intent(ConnectionActivity.this,NetworkService.class));
				findViewById(R.id.linearLayoutFunctions).setVisibility(View.INVISIBLE);
			}
		}
	}

	private void SavePreferences(String ServerIP, int ServerPort){
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString(Common.SERVER_IP_KEY,ServerIP);
		editor.putInt(Common.SERVER_PORT_KEY, ServerPort);
		editor.commit();
	}

	private void LoadPreferences(){
		ServerIP = PreferenceManager.getDefaultSharedPreferences(this).getString(Common.SERVER_IP_KEY, Common.DEFAULT_SERVER_IP_VALUE);
		ServerPort = PreferenceManager.getDefaultSharedPreferences(this).getInt(Common.SERVER_PORT_KEY, Common.DEFAULT_SERVER_PORT_VALUE);
	}

	private boolean isValidIP(String IP)
	{
		Matcher matcher = IPPattern.matcher(IP);
		if(matcher.matches())
		{
			try {
				if(Inet4Address.getByName(IP).isReachable(1000))
					return true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				return false;
			}
		}
		Toast.makeText(this,"Invalid/Unreachable IP",Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	protected void onResume() {
		if(!NetworkService.running)
		{
			toggleButtonConnect.setChecked(false);
			findViewById(R.id.linearLayoutFunctions).setVisibility(View.INVISIBLE);
		}
		else
		{
			toggleButtonConnect.setChecked(true);
			findViewById(R.id.linearLayoutFunctions).setVisibility(View.VISIBLE);
		}
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_connection, menu);
		return true;
	}

}