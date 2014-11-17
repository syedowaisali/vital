package com.vitalgroup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GPSService extends Service implements LocationListener {
   
  private static final String TAG = GPSService.class.getSimpleName();
  
  private String web_url = "http://www.vitalgroup.biz/vgmobile/vgc_ws.php";
  private String token = "31f7070affb3de320b85bdb71dd1316661917e923da586b5d3b70ef168664a408f86299e2846f4fdf43d1a1f01555b50";
  
  // update interval
  private int interval = 59000;		// 1 minute;

  // flag for GPS status
  boolean isGPSEnabled = false;

  // flag for network status
  boolean isNetworkEnabled = false;

  // flag for GPS status
  boolean canGetLocation = false;

  Location location; // location
  double latitude; // latitude
  double longitude; // longitude

  // The minimum distance to change Updates in meters
  private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

  // The minimum time between updates in milliseconds
  private static final long MIN_TIME_BW_UPDATES = (long) (1000 * 10 * 1); // 1 minute

  // Declaring a Location Manager
  protected LocationManager locationManager;

	private void doingTask(){
		Log.i("Task Status"," Task Stared");
		final Handler handler = new Handler();
	    Timer timer = new Timer();
	    TimerTask doAsynchronousTask = new TimerTask() {       
	        @Override
	        public void run() {
	            handler.post(new Runnable() {
	                public void run() {       
	                    try {
	                    	Log.i(TAG, "Timer task doing work");
	                    	if(isConnectingToInternet()){
	                    		new UpdateCoords().execute();
	                    	}else{
	                    		Log.i("Network Status", "No Network Found");
	                    	}
	                    	
	                    } catch (Exception e) {
	                        // TODO Auto-generated catch block
	                    }
	                }
	            });
	        }
	    };
	    timer.schedule(doAsynchronousTask, 0, interval); //execute in every 1 minute
	}

  @Override
  public IBinder onBind(Intent intent) {
    // TODO Auto-generated method stub
    return null;
  }
 
  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, "Service creating");
    getLocation();
    //timer = new Timer("GPSService");
    //timer.schedule(updateTask, 1000L, 60 * 1000L);
    
    doingTask();
  }
 
  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.i(TAG, "Service destroying");
  }
  
  public boolean isConnectingToInternet(){
      ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
  }
  
  public Location getLocation() {
      try {
          locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

          // getting GPS status
          isGPSEnabled = locationManager
                  .isProviderEnabled(LocationManager.GPS_PROVIDER);

          // getting network status
          isNetworkEnabled = locationManager
                  .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

          if (!isGPSEnabled && !isNetworkEnabled) {
              // no network provider is enabled
          } else {
              this.canGetLocation = true;
              // First get location from Network Provider
              if (isNetworkEnabled) {
                  locationManager.requestLocationUpdates(
                          LocationManager.NETWORK_PROVIDER,
                          MIN_TIME_BW_UPDATES,
                          MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                  Log.d("Network", "Network");
                  if (locationManager != null) {
                      location = locationManager
                              .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                      if (location != null) {
                          latitude = location.getLatitude();
                          longitude = location.getLongitude();
                      }
                  }else{
                	  Log.i("location-manager", "Unknown");
                  }
              }
              // if GPS Enabled get lat/long using GPS Services
              if (isGPSEnabled) {
                  if (location == null) {
                      locationManager.requestLocationUpdates(
                              LocationManager.GPS_PROVIDER,
                              MIN_TIME_BW_UPDATES,
                              MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                      Log.d("GPS Enabled", "GPS Enabled");
                      if (locationManager != null) {
                          location = locationManager
                                  .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                          if (location != null) {
                              latitude = location.getLatitude();
                              longitude = location.getLongitude();
                          }
                      }
                  }
              }
          }

      } catch (Exception e) {
          e.printStackTrace();
      }

      return location;
  }
  
  @Override
  public void onLocationChanged(Location location) {
  	//txtLat = (TextView) findViewById(R.id.textview1);
  	//txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
	  latitude = location.getLatitude();
	  longitude = location.getLongitude();
  }
   
  @Override
  public void onProviderDisabled(String provider) {
  	Log.d("Latitude","disable");
  }
   
  @Override
  public void onProviderEnabled(String provider) {
  	Log.d("Latitude","enable");
  }
   
  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
  	Log.d("Latitude","status");
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
          

          
          String lat = String.valueOf(latitude);
          String lng = String.valueOf(longitude);
          Log.d("lat-lng", "lat=" + lat + "lng=" + lng);
          String url = web_url + "?func=update_location&imei=" + getIMEINum() + "&ssn=" + getSimSerialNum() + "&lat=" + lat + "&lng=" + lng + "&token=" + token;
          //String url = "http://www.bluewaydesign.com/vgc/vgc_ws.php?func=get_user_info";
          // Making a request to url and getting response
          String jsonStr = getWebData(url);

    	  Log.d("Response: ", "> " + jsonStr);

          if (jsonStr != null) {
              try {
                  //JSONObject jsonObj = new JSONObject(jsonStr);
                  //String rc = jsonObj.getString("return_code");
                  //Log.d("Return Code", rc);
                  //Toast.makeText(getApplicationContext(), rc, Toast.LENGTH_LONG).show();
            	  //Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
                  
              } catch (Exception e) {
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
  
  private String getWebData(String url) {
		// TODO Auto-generated method stub
  	  String response = "";
  	  try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            //HttpEntity httpEntity = null;
            //HttpResponse httpResponse = null;
            HttpGet httpGet = new HttpGet(url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpClient.execute(httpGet, responseHandler);
  
            //httpEntity = httpResponse.getEntity();
            //response = EntityUtils.toString(httpEntity);
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         
        return response;
 
    }
  
  private String getIMEINum(){
	  TelephonyManager telMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		return telMgr.getDeviceId();
  }
  
  private String getSimSerialNum(){
	  TelephonyManager telMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		return telMgr.getSimSerialNumber();
  }
  
}


