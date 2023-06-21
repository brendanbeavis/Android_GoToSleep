package com.smashedpotato.gotosleep;

import java.lang.reflect.Method;
import java.util.Calendar;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

	boolean sleepMode=false;

	boolean enabled=false;
	boolean sunState;
	boolean monState;
	boolean tueState;
	boolean wedState;
	boolean thuState;
	boolean friState;
	boolean satState;
	boolean repeatState;
	int hour;
	int minute;
	Calendar alarmTime;
	AlarmReceiver alarm = new AlarmReceiver();
	String bedtimeText="Bedtime starts in: ";
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	long difference;
	TimePicker timePicker1;
	ToggleButton su;
	ToggleButton mo;
	ToggleButton tu;
	ToggleButton we;
	ToggleButton th;
	ToggleButton fr;
	ToggleButton sa;
	Switch setButton;
	CheckBox repeat;
	int CLOSE_DELAY=15000;
	private AdView mAdView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		settings = getSharedPreferences("PREFS", 0);
	    editor = settings.edit();



	    Intent intent = getIntent();
	    if(intent.hasExtra("sleepMode")){
	    	sleepMode=intent.getBooleanExtra("sleepMode",false);
	    }
	    if(sleepMode){
	    	requestWindowFeature(Window.FEATURE_NO_TITLE);
	    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    	setContentView(R.layout.activity_main);
	    	sleepNow();
	    }
	    else{
	    	setContentView(R.layout.activity_main);
			timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
			su = (ToggleButton) findViewById(R.id.togSun);
			mo = (ToggleButton) findViewById(R.id.togMon);
			tu = (ToggleButton) findViewById(R.id.togTue);
			we = (ToggleButton) findViewById(R.id.togWed);
			th = (ToggleButton) findViewById(R.id.togThu);
			fr = (ToggleButton) findViewById(R.id.togFri);
			sa = (ToggleButton) findViewById(R.id.togSat);
			setButton = (Switch) findViewById(R.id.switchEnable);
			repeat = (CheckBox) findViewById(R.id.tRepeat);
			
			loadConfigFromPref();
			loadConfigToScreen();
			
			repeat.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
			    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			    { toggleRepeat();   }
			});
			mAdView = (AdView) findViewById(R.id.adView);
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdView.loadAd(adRequest);
	    }
	    Button b = findViewById(R.id.btnPP);
	    b.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bbeavis.com/android_privacy_policy.html"));
				startActivity(browserIntent);
			}
		});
	}
	
	private void toggleRepeat(){
		if(repeat.isChecked()){
    		su.setEnabled(true);
    		mo.setEnabled(true);
    		tu.setEnabled(true);
    		we.setEnabled(true);
    		th.setEnabled(true);
    		fr.setEnabled(true);
    		sa.setEnabled(true);
        }
        else{
        	su.setEnabled(false);
    		mo.setEnabled(false);
    		tu.setEnabled(false);
    		we.setEnabled(false);
    		th.setEnabled(false);
    		fr.setEnabled(false);
    		sa.setEnabled(false);
        }
	}
	
	private void loadConfigFromPref() {
		sunState = settings.getBoolean("sun", false);
		monState = settings.getBoolean("mon", false);
		tueState = settings.getBoolean("tue", false);
		wedState = settings.getBoolean("wed", false);
		thuState = settings.getBoolean("thu", false);
		friState = settings.getBoolean("fri", false);
		satState = settings.getBoolean("sat", false);
		repeatState = settings.getBoolean("repeat", false);
		enabled = settings.getBoolean("enabled", false);
		hour = settings.getInt("hour",-1);
		minute = settings.getInt("minute",-1);
	}
	
	private void loadConfigToScreen() {
		
		// set current time into timepicker
		final Calendar c = Calendar.getInstance();
		if(hour<0 || minute<0){
			timePicker1.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
			timePicker1.setCurrentMinute(c.get(Calendar.MINUTE));
		}
		else{
			timePicker1.setCurrentHour(hour);
			timePicker1.setCurrentMinute(minute);
		}
		su.setChecked(sunState);
		mo.setChecked(monState);
		tu.setChecked(tueState);
		we.setChecked(wedState);
		th.setChecked(thuState);
		fr.setChecked(friState);
		sa.setChecked(satState);
		
		setButton.setChecked(enabled);
		
		if(enabled){
			setButton.setText("GoToSleep is Enabled ");
			
		}
		else{
			setButton.setText("GoToSleep is Disabled");
		}
		if(repeatState) repeat.setChecked(true);
		else repeat.setChecked(false);
		
		toggleRepeat();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		sleepMode=false;
	}
	@Override
	public void onPause() {
	    super.onPause();
	    if(!sleepMode){
			//read values
			hour = timePicker1.getCurrentHour();
			minute = timePicker1.getCurrentMinute();
			Log.d("theTimeSet","time set: "+hour+":"+minute);
			sunState = su.isChecked();
			monState = mo.isChecked();
			tueState = tu.isChecked();
			wedState = we.isChecked();
			thuState = th.isChecked();
			friState = fr.isChecked();
			satState = sa.isChecked();
			repeatState = repeat.isChecked();
			
			//save values
			editor.putBoolean("sun", sunState);
			editor.putBoolean("mon", monState);
			editor.putBoolean("tue", tueState);
			editor.putBoolean("wed", wedState);
			editor.putBoolean("thu", thuState);
			editor.putBoolean("fri", friState);
			editor.putBoolean("sat", satState);
			editor.putBoolean("enabled", enabled);
			editor.putInt("hour", hour);
			editor.putInt("minute", minute);
			editor.putBoolean("repeat", repeatState);
		    editor.commit();
			
		    if(enabled){
				// Set the alarm to start at users time
				Calendar now = Calendar.getInstance();
				now.setTimeInMillis(System.currentTimeMillis());
				alarmTime = Calendar.getInstance();
				alarmTime.setTimeInMillis(System.currentTimeMillis());
				alarmTime.set(Calendar.HOUR_OF_DAY, hour);
				alarmTime.set(Calendar.MINUTE, minute);
				alarmTime.set(Calendar.SECOND, 0);
				difference = alarmTime.getTimeInMillis() - now.getTimeInMillis();
				if(enabled) alarm.setAlarm(this,alarmTime);
				String bedTime=bedtimeText;
				if(difference<0) difference+=(1000*60*60*24);
				if(difference<60000){
					difference=60000;
					alarmTime.set(Calendar.MINUTE, minute+1);
				}
				int days = (int) (difference / (1000*60*60*24));
				int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
				int mins = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
				if(days>0) bedTime = bedTime + days+" days ";
				if(hours>0) bedTime = bedTime + hours+" hours ";
				if(mins>0) bedTime = bedTime + mins+" mins";
				Toast.makeText(this, bedTime, Toast.LENGTH_LONG).show();
		    }
		}
	}
	
	public void buttonClick(View v){
		enabled=setButton.isChecked();
		if(enabled){
			setButton.setText("GoToSleep is Enabled ");
		}
		else{
			alarm.cancelAlarm(this);
			setButton.setText("GoToSleep is Disabled");
		}
	}
	
	
	
	   private void sleepNow() {
			Log.d("SleepActivity","actioning");
	       
			LinearLayout blackScreen = (LinearLayout) findViewById(R.id.blackScreen);
			LinearLayout sleepScreen = (LinearLayout) findViewById(R.id.sleepScreen);			
			sleepScreen.setVisibility(View.VISIBLE);
			blackScreen.setAlpha(0f);
			blackScreen.setVisibility(View.VISIBLE);
	        blackScreen.animate().alpha(1f).setDuration(CLOSE_DELAY).setListener(null);
	        
			   
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
			        
	        Handler mHandler = new Handler(getMainLooper());
	        Runnable mUpdateTimeTask = new Runnable() {
	      	   public void run() {
	      		 Log.d("SleepActivity","ScreenOff");
	      		WindowManager.LayoutParams params = getWindow().getAttributes();
	      		params.screenBrightness = 0;
	      		getWindow().setAttributes(params);
	      		
	      	   }};
	         mHandler.postDelayed(mUpdateTimeTask, CLOSE_DELAY);
			         
	         
	         Handler mHandler2 = new Handler(getMainLooper());
	         Runnable mUpdateTimeTask2 = new Runnable() {
	       	   public void run() {
	       		Log.d("SleepActivity","ScreenOn and Finish");
	       		WindowManager.LayoutParams params = getWindow().getAttributes();
	       		params.screenBrightness = -1;
	       		getWindow().setAttributes(params);
	            if(settings.getBoolean("wifiStateChanged",false)) wifiManager.setWifiEnabled(true);
	            	
	       		   finish();
	       	   }};
	          mHandler2.postDelayed(mUpdateTimeTask2, CLOSE_DELAY*4);
		}
		
	
	
	@Override
    public void onBackPressed() {
		if(!sleepMode){
			onPause();
			finish();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
	    switch (menuItem.getItemId()) {
	        case android.R.id.home:
	            break;
	    }
	    return (super.onOptionsItemSelected(menuItem));
	}
	

}
