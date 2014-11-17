package com.vitalgroup;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class Forms extends Activity implements LocationListener {
	
	private ProgressDialog pDialog;
	private Spinner dropdown;
	private static final int PICK_IMAGE = 1;
	private static final int PICK_CAMERA_IMAGE = 2;
	private static final int SAVE_ANNOTATE = 3;
	Bitmap photo;
	Uri imageUri;
	@SuppressWarnings("unused")
	private static final String TAG = Forms.class.getSimpleName();
	String user_id;
	boolean photo_selected = false;

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
	  
	// Connection Detector Object
	ConnectionDetector cd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set screen orientation only portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.forms);
		
		user_id = getIntent().getStringExtra("user_id");

		cd = new ConnectionDetector(this);
		if(cd.isConnectingToInternet()){
			String url = Setting.WEB_URL + "?func=get_form_brands&token=" + Setting.TOKEN;
			new GetBrandsName().execute(url);
		}else{
			Toast.makeText(getApplicationContext(), "Internet not connected", Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.forms_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.menu_annotate_image:
			if(photo_selected){
				openImageEditor();
			}
			break;
		
		case R.id.menu_saved_forms:
			Intent intent = new Intent(this, SavedForms.class);
			startActivity(intent);
			break;
		}
		
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		
        if(resultCode == RESULT_OK){
        	switch (requestCode) {  
            case SAVE_ANNOTATE:
            	byte[] photoByte = data.getByteArrayExtra("image");
            	Bitmap photoBmp = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
            	photo = photoBmp.copy(Bitmap.Config.ARGB_8888, true);
            	ImageView capture_image = (ImageView)findViewById(R.id.capture_image);
        		capture_image.setImageBitmap(photo);;
            	break;
            }
        }
            
        Uri selectedImageUri = null;
        String filePath = null;
        switch (requestCode) {
        case PICK_IMAGE:
            if (resultCode == Activity.RESULT_OK) {
                selectedImageUri = data.getData();
            }
            break;
        case PICK_CAMERA_IMAGE:
             if (resultCode == RESULT_OK) {
                //use imageUri here to access the image
                selectedImageUri = imageUri;
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
            }
             break;
        }
        
        if(selectedImageUri != null){
            try {
                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();
 
                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);
                
                if (selectedImagePath != null) {
                    filePath = selectedImagePath;
                } else if (filemanagerstring != null) {
                    filePath = filemanagerstring;
                } else {
                    Toast.makeText(getApplicationContext(), "Unknown path",
                    Toast.LENGTH_LONG).show();
                    Log.e("Bitmap", "Unknown path");
                }
 
                if (filePath != null) {
                    decodeFile(filePath);
                    photo_selected = true;
                } else {
                    photo = null;
                    photo_selected = false;
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Internal error",
                Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }
	}
	
	public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
	
	public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
 
        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;
 
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
 
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        photo = BitmapFactory.decodeFile(filePath, o2);
        ImageView capture_image = (ImageView)findViewById(R.id.capture_image);
		capture_image.setImageBitmap(photo);
		
		
		
		capture_image.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				
				openImageEditor();
				return true;
			}
		});
    }
	
	public void galleryImage(View v){
		try {
            Intent gintent = new Intent();
            gintent.setType("image/*");
            gintent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
            Intent.createChooser(gintent, "Select Picture"),PICK_IMAGE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
            e.getMessage(),
            Toast.LENGTH_LONG).show();
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
	}
	
	public void captureImage(View v){
		//define the file-name to save photo taken by Camera activity
        String fileName = "new-photo-name.jpg";
        //create parameters for Intent with filename
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
        //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
        imageUri = generateTimeStampPhotoFileUri();
        //create new Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, PICK_CAMERA_IMAGE);
	}
	
	private Uri generateTimeStampPhotoFileUri() {
		 
        Uri photoFileUri = null;
        File outputDir = getPhotoDirectory();
        if (outputDir != null) {
                Time t = new Time();
                t.setToNow();
                File photoFile = new File(outputDir, System.currentTimeMillis()
                                + ".jpg");
                photoFileUri = Uri.fromFile(photoFile);
        }
        return photoFileUri;
	}
	
	private File getPhotoDirectory() {
        File outputDir = null;
        String externalStorageStagte = Environment.getExternalStorageState();
        if (externalStorageStagte.equals(Environment.MEDIA_MOUNTED)) {
                File photoDir = Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                outputDir = new File(photoDir, getString(R.string.app_name));
                if (!outputDir.exists())
                        if (!outputDir.mkdirs()) {
                                Toast.makeText(
                                                this,
                                                "Failed to create directory "
                                                                + outputDir.getAbsolutePath(),
                                                Toast.LENGTH_SHORT).show();
                                outputDir = null;
                        }
        }
        return outputDir;
	}
	
	private void openImageEditor(){
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		Intent intent = new Intent(Forms.this, Annotate.class);
		intent.putExtra("image", byteArray);
		startActivityForResult(intent, SAVE_ANNOTATE);
	}
	
	public void saveForm(View v){
		EditText store_name = (EditText)findViewById(R.id.store_name);
		String brand_name = String.valueOf(dropdown.getSelectedItem());
		EditText city = (EditText)findViewById(R.id.city);
		EditText notes = (EditText)findViewById(R.id.notes);
		if(store_name.getText().toString().matches("")){
			DialogManager dialog = new DialogManager(this);
			dialog.setTitle("Error").setMessage("Store Name Required.").alert();
		}else{
			getLocation();
			String lat = String.valueOf(latitude);
			String lng = String.valueOf(longitude);
			String url = Setting.WEB_URL;
			//new SubmitFormTask(store_name.getText().toString(), brand_name, notes.getText().toString(), lat, lng).execute(url);
			
			String image_data;
			if(photo_selected){
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    	photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
		    	byte[] byte_arr = stream.toByteArray();
		    	image_data = Base64.encodeBytes(byte_arr);
			}else{
				image_data = "0";
			}
			
			
			DatabaseHandler db = new DatabaseHandler(this);
			
			FormRecords form = new FormRecords();
			form.setStoreName(store_name.getText().toString());
			form.setBrandName(brand_name);
			form.setCity(city.getText().toString());
			form.setNotes(notes.getText().toString());
			form.setImageData(image_data);
			form.setLatitude(lat);
			form.setLongitude(lng);
			form.setUserId(user_id);
			db.addNewForm(form);
			
			Toast.makeText(getApplicationContext(), "Form Saved.", Toast.LENGTH_LONG).show();
			
			// reload activity
			reload();
		}
		
	}
	
	// restart activity
	public void reload() {

	    Intent intent = getIntent();
	    overridePendingTransition(0, 0);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	    finish();

	    overridePendingTransition(0, 0);
	    startActivity(intent);
	}
	
	public void addBrands(JSONArray brands) throws JSONException{
		dropdown = (Spinner)findViewById(R.id.brand_name_dd);
		List<String> brand_name = new ArrayList<String>();
		for(int i = 0; i < brands.length(); i++){
			JSONObject brand = brands.getJSONObject(i);
			brand_name.add(brand.getString("fb_name"));
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, brand_name);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dropdown.setAdapter(dataAdapter);
	}

	// Declaring a Location Manager
	protected LocationManager locationManager;
	  
	  public Location getLocation() {
	      try {
	          locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

	          // getting GPS status
	          isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

	          // getting network status
	          isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

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
	  
	private class GetBrandsName extends AsyncTask<String, Void, Void> {
		int r_code = 0;
		@SuppressWarnings("unused")
		JSONObject caps;
		JSONArray brands_list;

		public GetBrandsName() {

			// Showing progress dialog
			pDialog = new ProgressDialog(Forms.this);
			pDialog.setIcon(R.drawable.ic_launcher);
			pDialog.setTitle("Loading Brands");
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
                    	r_code = jsonObj.getInt("return_code");
                    	brands_list = new JSONArray(jsonObj.getString("data"));
        		        
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
				try {
					addBrands(brands_list);
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
	
	// submit form
	private class SubmitFormTask extends AsyncTask<String, Void, Void> {
		int r_code = 0;
		private String store_name;
		private String brand_name;
		private String notes;
		private String lat;
		private String lng;
		String image_str;

		public SubmitFormTask(String _store_name, String _brand_name, String _notes, String _lat, String _lng) {
			store_name = _store_name;
			brand_name = _brand_name;
			notes = _notes;
			lat = _lat;
			lng = _lng;
			
			// Showing progress dialog
			pDialog = new ProgressDialog(Forms.this);
			pDialog.setIcon(R.drawable.ic_launcher);
			pDialog.setTitle("Data Uploading.");
			pDialog.setMessage("Please wait...");
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setProgress(0);
			pDialog.setMax(100);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected Void doInBackground(String... urls) {
			String url = urls[0];
		    
	    	Log.d("urls", url);
	    	
	    	// Creating service handler class instance
	        ServiceHandler sh = new ServiceHandler();
	        
	        if(photo_selected){
	        	ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    	photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
		    	byte[] byte_arr = stream.toByteArray();
		    	image_str = Base64.encodeBytes(byte_arr);
	        }else{
	        	image_str = "0";
	        }
	        
	    	ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
	    	 
            nameValuePairs.add(new BasicNameValuePair("func", "submit_form"));
            nameValuePairs.add(new BasicNameValuePair("image",image_str));
            nameValuePairs.add(new BasicNameValuePair("store_name", store_name));
            nameValuePairs.add(new BasicNameValuePair("brand_name", brand_name));
            nameValuePairs.add(new BasicNameValuePair("notes", notes));
            nameValuePairs.add(new BasicNameValuePair("lat", lat));
            nameValuePairs.add(new BasicNameValuePair("lng", lng));
            nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
            nameValuePairs.add(new BasicNameValuePair("token", Setting.TOKEN));
	        
	        // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST, nameValuePairs);
            Log.d("Response: ", "> " + jsonStr);
	            
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String rc = jsonObj.getString("return_code");
                    Log.d("Return Code", rc);
                    
                    try {
                    	r_code = jsonObj.getInt("return_code");
                    	
        		        
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
				Toast.makeText(getApplicationContext(), "Form Submited.", Toast.LENGTH_LONG).show();
				finish();
			}else{
				Toast.makeText(getApplicationContext(), "This Device Cannot Registered.", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}
}



