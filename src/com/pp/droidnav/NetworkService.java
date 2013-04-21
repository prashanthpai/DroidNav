package com.pp.droidnav;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class NetworkService extends Service {
	public static boolean running = false;
	public static boolean tcpConnected = false;
	private String ServerIP = null;
	private int ServerPort = 0;
	private int NOTIFICATION = 1;
	private NotificationManager mNM;

	private Socket tcpSocket = null;
	private OutputStream sout = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new LocalBinder<NetworkService>(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LoadPreferences();
		try {
			tcpSocket = new Socket(ServerIP, ServerPort);
			if(tcpSocket!=null)
				sout = tcpSocket.getOutputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			cleanUp();
			stopSelf();
		} catch (IOException e) {
			e.printStackTrace();
			cleanUp();
			stopSelf();
		}
		tcpConnected = true;
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		showNotification();
		running = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cleanUp();
	}

	private void cleanUp()
	{
		sendViaUDP("a","bye");
		try {
			if(tcpSocket!=null)
				tcpSocket.close();
		} catch (IOException e) {
			Log.v(Common.TAG,e.getMessage());
		}
		finally {
		tcpConnected = false;
		running = false;
		mNM.cancel(NOTIFICATION);
		Toast.makeText(this,"DroidNav service destroyed",Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressWarnings("deprecation")
	private void showNotification() {
		CharSequence notificationTitle = "DroidNav service running";
		CharSequence notificationText = "Connected to : " + ServerIP + " : " + ServerPort;

		// Set the icon, scrolling text and time stamp
		Notification notification = new Notification(R.drawable.ic_launcher, notificationText,System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ConnectionActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, notificationTitle,	notificationText, contentIntent);

		// Send the notification.
		mNM.notify(NOTIFICATION, notification);
	}

	private void LoadPreferences(){
		ServerIP = PreferenceManager.getDefaultSharedPreferences(this).getString(Common.SERVER_IP_KEY,Common.DEFAULT_SERVER_IP_VALUE);
		ServerPort = PreferenceManager.getDefaultSharedPreferences(this).getInt(Common.SERVER_PORT_KEY, Common.DEFAULT_SERVER_PORT_VALUE);	
	}

	public void sendViaUDP(String key,String value)
	{
		String payLoad = key + ":" + value;
		if(!key.isEmpty() && !value.isEmpty())
			new SendUDP().execute(payLoad);
	}

	public void sendViaTCP(String key,String value)
	{
		String payLoad = key + ":" + value;
		if(!key.isEmpty() && !value.isEmpty())
			new SendTCP().execute(payLoad);
	}

	class SendTCP extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
				params[0] += "\n";
				try {
					sout.write(params[0].getBytes("UTF-8"));
				} catch (IOException e) {
					e.printStackTrace();
					cleanUp();
				}
				return null;
		}
	}

	class SendUDP extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			try {
				MulticastSocket s = new MulticastSocket();
				try {
					String msg = params[0];
					//Log.v(Common.TAG,msg);
					InetAddress IP = InetAddress.getByName(ServerIP);
					DatagramPacket p = new DatagramPacket(msg.getBytes("UTF-8"), msg.length(), IP, ServerPort);
					s.send(p);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					s.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
