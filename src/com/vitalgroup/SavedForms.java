package com.vitalgroup;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



public class SavedForms extends Activity {
	
	private ProgressDialog pDialog;
    
	// Connection Detector Object
	ConnectionDetector cd;
	
	private static final boolean SINGLE_FORM = false;
	private static final boolean ALL_FORMS = true;
	private static final int DELETE = 0;
	private static final int SUBMIT = 1;
	int form_id;
	int selected_option;
	
	private int total_forms = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set screen orientation only portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// set content view
		setContentView(R.layout.saved_forms);
		
		fetchSavedForms();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		//getMenuInflater().inflate(R.menu.menu_save_forms, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.sumit_all_saved_forms:
			DialogManager dm = new DialogManager(SavedForms.this, true);

			dm.setTitle("Are you sure.")
			.setTwoButton("Yes",new OnPositiveListener() {
				
				@Override
				public void OnPositive() {
					// TODO Auto-generated method stub
					submitForm(ALL_FORMS);
				}
			}, 
			"No", new OnNegativeListener() {
				
				@Override
				public void OnNegative() {
					// TODO Auto-generated method stub
					
				}
			});
			break;
		}
		
		return true;
	}
	
	public void fetchSavedForms(){
		DatabaseHandler db = new DatabaseHandler(this);

		total_forms = db.getFormsCount();
		
		if(total_forms > 0){
			
			new LoadAllForms().execute();
		}else{
			
			RelativeLayout forms_container = (RelativeLayout)findViewById(R.id.menu_box);
			
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			
			TextView no_form = new TextView(this);
			no_form.setLayoutParams(layoutParams);
			no_form.setText("No Forms");
			no_form.setTextColor(Color.WHITE);
			no_form.setTextSize(20);
			forms_container.addView(no_form);
		}
	}
	
	private void submitForm(boolean act){
		submitForm(act, 0);
	}
	
	private void submitForm(boolean act, int form_id){
		DatabaseHandler db = new DatabaseHandler(SavedForms.this);
		
		if(act == SINGLE_FORM){
        	// submit single form
        	FormRecords form = db.getForm(String.valueOf(form_id));
        	String url = Setting.WEB_URL;
			new SubmitFormTask(form.getStoreName(), form.getBrandName(), form.getCity(), form.getNotes(), form.getImageData(), form.getLatitude(), form.getLongitude(), form.getUserId(), form.getFormId(), act).execute(url);
        }
        else{
        	// submit all forms
        	
        	List<FormRecords> forms = db.getAllForms();
			for(FormRecords form : forms){
				String url = Setting.WEB_URL;
				new SubmitFormTask(form.getStoreName(), form.getBrandName(), form.getCity(), form.getNotes(), form.getImageData(), form.getLatitude(), form.getLongitude(), form.getUserId(), form.getFormId(), act).execute(url);
			}
        }
	}
	
	// delete form
	private void deleteForm(String _fid){
		DatabaseHandler db = new DatabaseHandler(this);
		db.deleteForm(_fid);
		//LinearLayout fc = (LinearLayout)findViewById(R.id.saved_forms_wrapper);
		//LinearLayout form_layout = (LinearLayout)findViewById(Integer.parseInt(_fid));
		//fc.removeView(form_layout);
		// reload activity
		reload();
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
	
	// load form AsyncTask
	private class LoadAllForms extends AsyncTask<Void, Void, Void>{
		
		private boolean isError = false;
		private String errorString = "";
		List<FormRecords> forms;
		
		public LoadAllForms(){
			// Showing progress dialog
			pDialog = new ProgressDialog(SavedForms.this);
			pDialog.setIcon(R.drawable.ic_launcher);
			pDialog.setTitle("Loading Forms");
			pDialog.setMessage("Please wait...");
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setProgress(0);
			pDialog.setMax(100);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		protected Void doInBackground(Void... params){
			
			try {
				
				DatabaseHandler db = new DatabaseHandler(SavedForms.this);
				forms = db.getAllForms();
				
			} catch (Exception e) {
				// TODO: handle exception
				isError = true;
				errorString = String.valueOf(e);
			}
			
			
			return null;
		}
		
		protected void onPostExecute(Void result){
			if(pDialog.isShowing())
				pDialog.dismiss();
			
			if(isError){
				Toast.makeText(SavedForms.this, errorString, Toast.LENGTH_LONG).show();
			}
			else{
				LinearLayout forms_container = (LinearLayout)findViewById(R.id.saved_forms_wrapper);
				for(FormRecords form : forms){
					@SuppressWarnings("deprecation")
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 100);
					layoutParams.setMargins(10, 0, 10, 10);
					
					LinearLayout form_layout = new LinearLayout(SavedForms.this);
					form_layout.setLayoutParams(layoutParams);
					form_layout.setOrientation(LinearLayout.HORIZONTAL);
					form_layout.setBackgroundColor(Color.WHITE);
					form_layout.setId(Integer.parseInt(form.getFormId()));
					form_layout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							form_id = v.getId();
							String[] options = new String[]{"Delete", "Submit"};
							AlertDialog.Builder builder = new AlertDialog.Builder(SavedForms.this);
							builder.setTitle("Select Option.");
							builder.setItems(options, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									selected_option = which;
									
									DialogManager dm = new DialogManager(SavedForms.this, true);
									
									dm.setTitle("Are you sure.")
									.setTwoButton("Yes",new OnPositiveListener() {
										
										@Override
										public void OnPositive() {
											// TODO Auto-generated method stub
											
											if(selected_option == DELETE){
												deleteForm(String.valueOf(form_id));
											}
											else if(selected_option == SUBMIT){
												submitForm(SINGLE_FORM, form_id);
											}
										}
									}, 
									"No", new OnNegativeListener() {
										
										@Override
										public void OnNegative() {
											// TODO Auto-generated method stub
											
										}
									});
								}
							});
							builder.show();
						}
					});
					form_layout.setOnTouchListener(new OnTouchListener() {
						
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							// TODO Auto-generated method stub
							
							switch(event.getAction()){
							case MotionEvent.ACTION_DOWN:
								
								v.setBackgroundColor(Color.LTGRAY);
								break;
								
							case MotionEvent.ACTION_UP:
								v.setBackgroundColor(Color.WHITE);
								break;
							}
							
							return false;
						}
					});
					
					// create image view
					ImageView vital_logo = new ImageView(SavedForms.this);
					vital_logo.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
					vital_logo.setImageDrawable(getResources().getDrawable(R.drawable.vital_small_logo));
					
					// create text view
					TextView store_name = new TextView(SavedForms.this);
					LinearLayout.LayoutParams store_name_layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					store_name_layout.setMargins(15, 15, 15, 15);
					store_name.setLayoutParams(store_name_layout);
					store_name.setText(form.getStoreName());
					
					form_layout.addView(vital_logo);	// add vital logo image
					form_layout.addView(store_name);	// add store name
					
					// add form to forms container
					forms_container.addView(form_layout);
					
				}
			}
		}
	}
	
	// submit form
	private class SubmitFormTask extends AsyncTask<String, Void, Void> {
		int r_code = 0;
		private String store_name;
		private String brand_name;
		private String city;
		private String notes;
		private String image_data;
		private String lat;
		private String lng;
		private String user_id;
		private boolean act;
		private String fid;

		public SubmitFormTask(String _store_name, String _brand_name, String _city, String _notes, String _image_data, String _lat, String _lng, String _user_id, String _fid, boolean _act) {

			this.store_name = _store_name;
			this.brand_name = _brand_name;
			this.city = _city;
			this.notes = _notes;
			this.image_data = _image_data;
			this.lat = _lat;
			this.lng = _lng;
			this.user_id = _user_id;
			this.fid = _fid;
			this.act = _act;
			
			// Showing progress dialog
			pDialog = new ProgressDialog(SavedForms.this);
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
	        
	    	ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
	    	 
            nameValuePairs.add(new BasicNameValuePair("func", "submit_form"));
            nameValuePairs.add(new BasicNameValuePair("image",image_data));
            nameValuePairs.add(new BasicNameValuePair("store_name", store_name));
            nameValuePairs.add(new BasicNameValuePair("brand_name", brand_name));
            nameValuePairs.add(new BasicNameValuePair("city", city));
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
				if(act == SINGLE_FORM){
					deleteForm(fid);
				}
			}else{
				Toast.makeText(getApplicationContext(), "This Device Cannot Registered.", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}
}


