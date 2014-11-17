package com.vitalgroup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
        //Intent serviceIntent = new Intent(GPSService.class.getName());
        //context.startService(serviceIntent);
        context.startService(new Intent(context, GPSService.class));
	}
}