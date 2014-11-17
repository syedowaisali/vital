package com.vitalgroup;


import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private ProgressDialog pDialog;
    
	// Connection Detector Object
	ConnectionDetector cd;
	
	String user_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set screen orientation only portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.activity_main);
		
		// get user detail url
		cd = new ConnectionDetector(this);
		if(cd.isConnectingToInternet()){
			String gud_url = Setting.WEB_URL + "?func=get_user_info&imei=" + Setting.get_imei_number(getApplicationContext()) + "&token=" + Setting.TOKEN;
	        new DownloadImageTask((ImageView) findViewById(R.id.user_img), (TextView) findViewById(R.id.user_name)).execute(gud_url);
		}else{
			Toast.makeText(getApplicationContext(), "Internet not connected", Toast.LENGTH_LONG).show();
			finish();
		}

		if(!isMyServiceRunning()){
			startService(new Intent(this, GPSService.class));
		}else{
			//Toast.makeText(getApplicationContext(), "Service Already Started", Toast.LENGTH_LONG).show();
		}
		
		
	}
	
	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (GPSService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void showForm(View v){
		Intent intent = new Intent(this, Forms.class);
		intent.putExtra("user_id", user_id);
		startActivity(intent);
	}
	
	public void showCal(View v){
		Toast.makeText(getApplicationContext(), "imei = " + Setting.get_imei_number(getApplicationContext()) + ", sim = " + Setting.get_sim_serial_number(getApplicationContext()), Toast.LENGTH_LONG).show();
		//Toast.makeText(getApplicationContext(), "lat = " + lat + ", lng = " + lng, Toast.LENGTH_LONG).show();
	}
	
	public void showLocationTracker(View v){
		// Run next activity
		Intent intent = new Intent(MainActivity.this, LocationTracker.class);
		Bundle bundle = new Bundle();
		bundle.putString("user_id", user_id);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	  
	  
	private class DownloadImageTask extends AsyncTask<String, Void, Void> {
		ImageView bmImage;
		TextView userName;
		Bitmap mIcon11 = null;
		String aun;
		int r_code = 0;
		JSONObject caps;

		public DownloadImageTask(ImageView bmImage, TextView userName) {
			this.bmImage = bmImage;
			this.userName = userName;
			// Showing progress dialog
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setIcon(R.drawable.ic_launcher);
			pDialog.setTitle("Loading Dashboard");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected Void doInBackground(String... urls) {
			String url = urls[0];
		      	
	    	Log.d("urls", url);
	    	// Creating service handler class instance
	        ServiceHandler sh = new ServiceHandler();
	        
	        // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
	            
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String rc = jsonObj.getString("return_code");
                    Log.d("Return Code", rc);
                    
                    try {
                    	String urldisplay = jsonObj.getString("avatar");
                    	aun = jsonObj.getString("fullname");
                    	
        		        InputStream in = new java.net.URL(urldisplay).openStream();
        		        mIcon11 = BitmapFactory.decodeStream(in);
        		        
        		        r_code = jsonObj.getInt("return_code");
        		        caps = new JSONObject(jsonObj.getString("caps"));
        		        
        		        user_id = jsonObj.getString("user_id");
        		        
        		      } catch (Exception e) {
        		          Log.e("Error", e.getMessage());
        		          e.printStackTrace();
        		      }
                    //Toast.makeText(getApplicationContext(), rc, Toast.LENGTH_LONG).show();
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
	         
            return null;
		}

		protected void onPostExecute(Void result) {
	      
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
		  
			if(r_code == 1){
				bmImage.setImageBitmap(getRoundedCornerBitmap(this.mIcon11));
				userName.setText(this.aun);
			      
				try {
					
					// check menu block
					if(caps.getBoolean("view_m_forms_module")){
						LinearLayout formLayout = (LinearLayout)findViewById(R.id.menu_form_block);
						formLayout.setVisibility(View.VISIBLE);
					}
					
					// check contacts block
					if(caps.getBoolean("view_m_contacts_module")){
						LinearLayout contactLayout = (LinearLayout)findViewById(R.id.menu_contacts_block);
						contactLayout.setVisibility(View.VISIBLE);
					}
					
					// check calendar block
					if(caps.getBoolean("view_m_calendar_module")){
						LinearLayout calLayout = (LinearLayout)findViewById(R.id.menu_calendar_block);
						calLayout.setVisibility(View.VISIBLE);
					}
					
					// check reminder block
					if(caps.getBoolean("view_m_reminders_module")){
						LinearLayout remLayout = (LinearLayout)findViewById(R.id.menu_reminders_block);
						remLayout.setVisibility(View.VISIBLE);
					}
					
					// check location tracker block
					if(caps.getBoolean("view_m_location_tracker_module")){
						LinearLayout locationLayout = (LinearLayout)findViewById(R.id.menu_location_tracker_block);
						locationLayout.setVisibility(View.VISIBLE);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				Toast.makeText(getApplicationContext(), "This Device Cannot Registered.", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	    		bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	 
	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = 300;
	 
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	 
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	 
	    return output;
	  }
	
}


