package com.vitalgroup;



import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MotionEvent;

public class SplashScreen extends Activity 
{	
	final SplashScreen sSplashScreen = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set screen orientation only portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// set content layout
		setContentView(R.layout.splash);			
		
		handler.postDelayed(runnable, Setting.SPLASH_TIMEOUT);	
	}
	
	
	
	final Handler handler = new Handler();
	final Runnable runnable = new Runnable()
	{
		public void run()
		{	
			startMainActivity();			
		}
	};	
	
	/**
     * Processes splash screen touch events
     */
	@Override
	public boolean onTouchEvent(MotionEvent evt)
	{
		if(evt.getAction() == MotionEvent.ACTION_DOWN)
		{
			handler.removeCallbacks(runnable);
			startMainActivity();			
		}
		return true;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.annotate_menu, menu);
		return true;
	}
	
	public void startMainActivity()
	{
		finish();
		
		// Run next activity
		Intent intent = new Intent();
		intent.setClass(sSplashScreen, InfoScreen.class);
		startActivity(intent);
	}
}