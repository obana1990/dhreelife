package com.my.dhreelife;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class KeepAliveService extends Service{

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return Service.START_STICKY;
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {

	}
}
