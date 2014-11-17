package com.vitalgroup;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoScreen extends Activity 
{	
	private ProgressDialog pDialog;
	
	private boolean verify_imei = false;
	private boolean verify_sim_serial = false;
	private boolean verify_internet_status = false;
	private boolean verify_gps_status = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set screen orientation only portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// set content layout
		setContentView(R.layout.info);
		
		// hide all icons and reset text
		hideAllIcons();
		
		// set sim imei number
		TextView imeiView = (TextView)findViewById(R.id.sim_imei_value);
		imeiView.setText(Setting.get_imei_number(getApplicationContext()));
		
		// set sim serial number
		TextView simSerialView = (TextView)findViewById(R.id.sim_serial_value);
		simSerialView.setText(Setting.get_sim_serial_number(getApplicationContext()));
		
		verify();
	}
	
	// verify again
	public void verifyAgain(View v){
		// hide all icons and reset text
		hideAllIcons();

		// set sim imei number
		TextView imeiView = (TextView)findViewById(R.id.sim_imei_value);
		imeiView.setText(Setting.get_imei_number(getApplicationContext()));
		
		// set sim serial number
		TextView simSerialView = (TextView)findViewById(R.id.sim_serial_value);
		simSerialView.setText(Setting.get_sim_serial_number(getApplicationContext()));
		
		verify();
	}
	
	// connect now
	public void connectNow(View v){
		String msg = "";
		boolean error = false;
		
		if(!verify_imei){
			msg += "IMEI Number not verified.\n";
			error = true;
		}
		
		if(!verify_sim_serial){
			msg += "Sim Serial Number not verified.\n";
			error = true;
		}
		
		if(!verify_internet_status){
			msg += "Internet not connected.\n";
			error = true;
		}
		
		if(!verify_gps_status){
			msg += "GPS is turned off.\n";
			error = true;
		}
		
		if(error){
			DialogManager dm = new DialogManager(this);
			dm.setTitle("Error").setMessage(msg).alert();
		}else{
			finish();
			
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}
	
	public void verify(){
		
		ConnectionDetector cd = new ConnectionDetector(this);
		if(cd.isConnectingToInternet()){
			String url = Setting.WEB_URL + "?func=verify_device_info&imei=" + Setting.get_imei_number(getApplicationContext()) + "&ssn=" + Setting.get_sim_serial_number(getApplicationContext()) + "&token=" + Setting.TOKEN;
			new VerifyData().execute(url);
		}
		else{
			updateDeviceStatus(false, false, false, isOnGPS());
		}
		
	}
	
	public void updateInfo(boolean imei, boolean sim_serial){
		updateDeviceStatus(imei, sim_serial, true, isOnGPS());
	}
	
	public void updateDeviceStatus(boolean imei, boolean sim_serial, boolean net, boolean gps){
		
		// verify imei
		if(imei){
			ImageView imeiTickIcon = (ImageView)findViewById(R.id.imei_tick_icon);
			imeiTickIcon.setVisibility(View.VISIBLE);
			verify_imei = true;
		}else{
			ImageView imeiCrossIcon = (ImageView)findViewById(R.id.imei_cross_icon);
			imeiCrossIcon.setVisibility(View.VISIBLE);
		}
		
		// verify sim serial
		if(sim_serial){
			ImageView simTickIcon = (ImageView)findViewById(R.id.sim_tick_icon);
			simTickIcon.setVisibility(View.VISIBLE);
			verify_sim_serial = true;
		}else{
			ImageView simCrossIcon = (ImageView)findViewById(R.id.sim_cross_icon);
			simCrossIcon.setVisibility(View.VISIBLE);
		}
		
		// verify internet status
		if(net){
			TextView netView = (TextView)findViewById(R.id.internet_value);
			netView.setText("Connected");
			
			ImageView netTickIcon = (ImageView)findViewById(R.id.internet_tick_icon);
			netTickIcon.setVisibility(View.VISIBLE);
			
			verify_internet_status = true;
		}else{
			TextView netView = (TextView)findViewById(R.id.internet_value);
			netView.setText("Not Connected");
			
			ImageView netCrossIcon = (ImageView)findViewById(R.id.internet_cross_icon);
			netCrossIcon.setVisibility(View.VISIBLE);
		}
		
		// verify gps status
		if(gps){
			TextView gpsView = (TextView)findViewById(R.id.gps_value);
			gpsView.setText("ON");
			
			ImageView gpsTickIcon = (ImageView)findViewById(R.id.gps_tick_icon);
			gpsTickIcon.setVisibility(View.VISIBLE);
			
			verify_gps_status = true;
		}else{
			TextView gpsView = (TextView)findViewById(R.id.gps_value);
			gpsView.setText("OFF");
			
			ImageView gpsCrossIcon = (ImageView)findViewById(R.id.gps_cross_icon);
			gpsCrossIcon.setVisibility(View.VISIBLE);
		}
	}
	
	public void hideAllIcons(){
		
		// hide imei tick icon
		ImageView imeiTickIcon = (ImageView)findViewById(R.id.imei_tick_icon);
		imeiTickIcon.setVisibility(View.GONE);
		
		// hide imei cros icon
		ImageView imeiCrossIcon = (ImageView)findViewById(R.id.imei_cross_icon);
		imeiCrossIcon.setVisibility(View.GONE);
	
		// hide sim tick icon
		ImageView simTickIcon = (ImageView)findViewById(R.id.sim_tick_icon);
		simTickIcon.setVisibility(View.GONE);
	
		// hide sim cros icon
		ImageView simCrossIcon = (ImageView)findViewById(R.id.sim_cross_icon);
		simCrossIcon.setVisibility(View.GONE);
	
		// hide internet tick icon
		ImageView netTickIcon = (ImageView)findViewById(R.id.internet_tick_icon);
		netTickIcon.setVisibility(View.GONE);
	
		// hide internet cros icon
		ImageView netCrossIcon = (ImageView)findViewById(R.id.internet_cross_icon);
		netCrossIcon.setVisibility(View.GONE);
		
		// hide gps tick icon
		ImageView gpsTickIcon = (ImageView)findViewById(R.id.gps_tick_icon);
		gpsTickIcon.setVisibility(View.GONE);
		
		// hide gps cross icon
		ImageView gpsCrossIcon = (ImageView)findViewById(R.id.gps_cross_icon);
		gpsCrossIcon.setVisibility(View.GONE);
		
		// reset internet status text
		TextView netView = (TextView)findViewById(R.id.internet_value);
		netView.setText("");
		
		// reset gps status text
		TextView gpsView = (TextView)findViewById(R.id.gps_value);
		gpsView.setText("");
		
		verify_imei = false;
		verify_sim_serial = false;
		verify_internet_status = false;
		verify_gps_status = false;
	}

	public boolean isOnGPS(){
		boolean result = false;
		try {
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	
	        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	            result = true;
	        } 
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return result;
	}
	
	private class VerifyData extends AsyncTask<String, Void, Void> {
		boolean d_imei;
		boolean d_sim_serial;

		public VerifyData() {
			// Showing progress dialog
			pDialog = new ProgressDialog(InfoScreen.this);
			pDialog.setIcon(R.drawable.ic_launcher);
			pDialog.setTitle("Verify Data");
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
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET)
            		;
            Log.d("Response: ", "> " + jsonStr);
	            
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    
                    d_imei = jsonObj.getBoolean("imei_status");
                	d_sim_serial = jsonObj.getBoolean("sim_serial_status");
                    
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
			
			updateInfo(d_imei, d_sim_serial);
		}

	}
}