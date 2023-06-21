package com.smashedpotato.gotosleep;

import java.lang.reflect.Method;
import java.util.Calendar;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class AlarmService extends IntentService {
	int SLEEP_DELAY=15000;
	int TOAST_OFFEST=1000;
	int TOAST_DELAY=2500;
	

	int CLOSE_DELAY=15000;
	public AlarmService() {
		super("AlarmService");
		// TODO Auto-generated constructor stub
		Log.d("AlarmService","alarm trigger constructer");
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		final SharedPreferences settings = getSharedPreferences("PREFS", 0);
		final SharedPreferences.Editor editor = settings.edit();
	    final Context context = getApplicationContext();
	    final Calendar c = Calendar.getInstance();
		boolean day[] = new boolean[8];
		
		day[Calendar.SUNDAY] = settings.getBoolean("sun", false);
		day[Calendar.MONDAY] = settings.getBoolean("mon", false);
		day[Calendar.TUESDAY] = settings.getBoolean("tue", false);
		day[Calendar.WEDNESDAY] = settings.getBoolean("wed", false);
		day[Calendar.THURSDAY] = settings.getBoolean("thu", false);
		day[Calendar.FRIDAY] = settings.getBoolean("fri", false);
		day[Calendar.SATURDAY] = settings.getBoolean("sat", false);
		
		Log.d("AlarmService","alarmDayCheck");
		
		
		
        if(day[c.get(Calendar.DAY_OF_WEEK)]||!settings.getBoolean("repeat", true)){
        	
        	Log.d("AlarmService","actioning");
        	
			Handler mHandlerToast1 = new Handler(getMainLooper());
			mHandlerToast1.post(new Runnable() {
                @Override
                public void run() {
                	Toast.makeText(context, "Time to sleep!", Toast.LENGTH_LONG).show();
                }
            });
		      
            
            Handler mHandlerToast2 = new Handler(getMainLooper());
            Runnable mUpdateTimeToast2 = new Runnable() {
          	   public void run() {
          		 Toast.makeText(context, "Sleep in: 3", Toast.LENGTH_LONG).show();
          	   }};
          	 mHandlerToast2.postDelayed(mUpdateTimeToast2, TOAST_OFFEST+TOAST_DELAY*1);
          	 
             Handler mHandlerToast3 = new Handler(getMainLooper());
             Runnable mUpdateTimeToast3 = new Runnable() {
           	   public void run() {
           		 Toast.makeText(context, "Sleep in: 2", Toast.LENGTH_LONG).show();
           	   }};
           	 mHandlerToast3.postDelayed(mUpdateTimeToast3,TOAST_OFFEST+TOAST_DELAY*2);
           	 
             Handler mHandlerToast4 = new Handler(getMainLooper());
             Runnable mUpdateTimeToast4 = new Runnable() {
           	   public void run() {
           		 Toast.makeText(context, "Sleep in: 1", Toast.LENGTH_LONG).show();
           	   }};
           	 mHandlerToast4.postDelayed(mUpdateTimeToast4, TOAST_OFFEST+TOAST_DELAY*3);
            
            
            
            
            Handler mHandler2 = new Handler(getMainLooper());
            Runnable mUpdateTimeTask2 = new Runnable() {
          	   public void run() {
		   	        //Intent sleepI = new Intent(getBaseContext(), MainActivity.class);
		   	        //sleepI.putExtra("sleepMode", true);
		   	        //sleepI.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		   	        //context.startActivity(sleepI);
          		   
          			Log.d("SleepActivity","actioning");
         	       
        					
        			
        			   
        	        //disable wifi
        	        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        	        if(wifiManager.isWifiEnabled()){
        	        	wifiManager.setWifiEnabled(false);
        	        	editor.putBoolean("wifiStateChanged", true);
        	        	editor.commit();
        	        }
        	        
        	        //disable 3G
        	        try {
        	            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        	            Method setMobileDataEnabledMethod = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        	            setMobileDataEnabledMethod.setAccessible(true);
        	            setMobileDataEnabledMethod.invoke(connectivityManager, false);
        	        } catch (Exception e) {
        	            e.printStackTrace();
        	        }
        			        
        	               			         
        	         
        	         Handler mHandler2 = new Handler(getMainLooper());
        	         Runnable mUpdateTimeTask2 = new Runnable() {
        	       	   public void run() {
        	       		Log.d("SleepActivity","ScreenOn and Finish");
        	       		
        	            if(settings.getBoolean("wifiStateChanged",false)) wifiManager.setWifiEnabled(true);
        	            	
        	       	   }};
        	          mHandler2.postDelayed(mUpdateTimeTask2, CLOSE_DELAY*4);
          		   
          		   
          		   
          	   }};
             mHandler2.postDelayed(mUpdateTimeTask2, SLEEP_DELAY);


        }

      		 Log.d("AlarmService","finishing wakefulintent");
      		 AlarmReceiver.completeWakefulIntent(intent);

        
	}

}
