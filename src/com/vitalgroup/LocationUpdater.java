package com.vitalgroup;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class LocationUpdater extends Service {
	private static final String TAG = GPSService.class.getSimpleName();
	@SuppressWarnings("unused")
	private ProgressDialog pDialog;
	
	Context _context;
	
	// Connection Detector Object
	ConnectionDetector cd;
		
	// GPSTracker class
    GPSTracker gps;
	
	public LocationUpdater() {
		this._context = this;

		// create class object
        //gps = new GPSTracker();
	}
	
	@Override
	  public void onCreate() {
	    super.onCreate();
	    Log.i(TAG, "Service creating");
	    gps = new GPSTracker();
	    //timer = new Timer("TweetCollectorTimer");
	    //timer.schedule(updateTask, 1000L, 60 * 1000L);
	  }
	 
	  @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		  updateLocation();
		  Log.i(TAG, "start command");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	  public void onDestroy() {
	    super.onDestroy();
	    Log.i(TAG, "Service destroying");
	     
	    //timer.cancel();
	    //timer = null;
	  }

	public void updateLocation(){
		final Handler h = new Handler();
	    h.postDelayed(new Runnable(){
	    	private int time = 0;
	        @Override
	        public void run()
	        {
	        	time += 1;
	        	Log.d("time", "" + time);
	        	cd = new ConnectionDetector(getApplicationContext());
	        	if(cd.isConnectingToInternet()){
	        		new UpdateCoords().execute();
	        	}else{
	        		//Toast.makeText(_context, "Internet not connected", Toast.LENGTH_SHORT).show();
	        	}
	        	
	            h.postDelayed(this, Setting.LOCATION_UPDATE_TIME);
	        }
	    }, Setting.LOCATION_UPDATE_TIME); // 1 second delay (takes millis)
	    
	    /*
	    // check if GPS enabled    
        if(gps.canGetLocation()){
            new GetResult().execute();
        }else{
            gps.showSettingsAlert();
        }
        */
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
     * Async task class to get json by making HTTP call
     * */
    private class UpdateCoords extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            //pDialog = new ProgressDialog(_act);
            //pDialog.setMessage("Please wait...");
            //pDialog.setCancelable(false);
            //pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
 
            
            String lat = gps.getLatitude();
            String lng = gps.getLongitude();
            Log.d("lat-lng", "lat=" + lat + "lng=" + lng);
            //String url = Setting.WEB_URL + "?func=update_location&imei=" + Setting.get_imei_number(getApplicationContext()) + "&lat=" + lat + "&lng=" + lng + "&token=" + Setting.TOKEN;
            String url = Setting.WEB_URL + "?func=get_user_info";
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
 
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String rc = jsonObj.getString("return_code");
                    Log.d("Return Code", rc);
                    //Toast.makeText(getApplicationContext(), rc, Toast.LENGTH_LONG).show();
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
 
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            //if (pDialog.isShowing())
                //pDialog.dismiss();

        }
 
    }
}
