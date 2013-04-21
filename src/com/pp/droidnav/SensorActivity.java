package com.pp.droidnav;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.CheckBox;
import android.widget.Toast;

public class SensorActivity extends Activity implements SensorEventListener{
	NetworkService mService = null;
	private boolean boundToService = false;

	private SensorManager mSensorManager;
	private Sensor mOrientation, mProximity;
	private CheckBox checkBoxProximity,checkBoxOrientation;

   @SuppressWarnings("deprecation")
@Override
	protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   setContentView(R.layout.activity_sensor);
       mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
       mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
       mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
       checkBoxProximity = (CheckBox) findViewById(R.id.checkBoxProximity);
       checkBoxOrientation = (CheckBox) findViewById(R.id.checkBoxOrientation);
       checkBoxProximity.setChecked(true);
       checkBoxOrientation.setChecked(false);
   }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
		String proximitySensorReading = null;
		String orientationSensorReading = null;

		if(sensor.getType() == Sensor.TYPE_PROXIMITY && checkBoxProximity.isChecked() ) {
			proximitySensorReading = String.valueOf(event.values[0]);
			if(NetworkService.tcpConnected && NetworkService.running)
				mService.sendViaTCP("p", proximitySensorReading);
		}
		else if(sensor.getType() == Sensor.TYPE_ORIENTATION && checkBoxOrientation.isChecked() ) {
			orientationSensorReading = String.valueOf(event.values[0]) + " " + String.valueOf(event.values[1]) + " " + String.valueOf(event.values[2]);
			if(NetworkService.tcpConnected && NetworkService.running)
				mService.sendViaUDP("o", orientationSensorReading);
		}
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
		mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
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
