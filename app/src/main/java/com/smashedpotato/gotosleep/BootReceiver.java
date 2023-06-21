package com.smashedpotato.gotosleep;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {
	AlarmReceiver alarm = new AlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
        	SharedPreferences settings = context.getSharedPreferences("PREFS", 0);
        	int hour = settings.getInt("hour",-0);
    		int minute = settings.getInt("minute",-0);
        	Calendar now = Calendar.getInstance();
			now.setTimeInMillis(System.currentTimeMillis());
			Calendar alarmTime = Calendar.getInstance();
			alarmTime.setTimeInMillis(System.currentTimeMillis());
			alarmTime.set(Calendar.HOUR_OF_DAY, hour);
			alarmTime.set(Calendar.MINUTE, minute);
			alarmTime.set(Calendar.SECOND, 0);
			
            alarm.setAlarm(context,alarmTime);
        }
    }
}
