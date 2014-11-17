package com.vitalgroup;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LocationTracker extends Activity {
	
	private ProgressDialog pDialog;
    
	// Connection Detector Object
	ConnectionDetector cd;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set screen orientation only portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.location_tracker);
		
		// get user detail url
		cd = new ConnectionDetector(this);
		if(cd.isConnectingToInternet()){
			//String gud_url = Setting.WEB_URL + "?func=get_user_info&imei=" + Setting.get_imei_number(getApplicationContext()) + "&token=" + Setting.TOKEN;
	        //new DownloadImageTask((ImageView) findViewById(R.id.user_img), (TextView) findViewById(R.id.user_name)).execute(gud_url);
		}else{
			//Toast.makeText(getApplicationContext(), "Internet not connected", Toast.LENGTH_LONG).show();
			//finish();
		}
		
		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Please Wait...");
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.show();
		
		Bundle bundle = getIntent().getExtras();
		String u = Setting.MAP_URL + "?func=show_map&uid=" + bundle.getString("user_id") + "&token=" + Setting.TOKEN;
		//String u = Setting.MAP_URL + "?func=show_map&uid=2&token=" + Setting.TOKEN;
		
		WebView mapView = (WebView)findViewById(R.id.map_view);
		mapView.getSettings().setJavaScriptEnabled(true);
		 // Brower niceties -- pinch / zoom, follow links in place
		mapView.getSettings().setGeolocationEnabled(true);
		mapView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		//mapView.getSettings().setJavaScriptEnabled(true);
		mapView.getSettings().setBuiltInZoomControls(true);

        // HTML5 API flags
		mapView.getSettings().setAppCacheEnabled(true);
		mapView.getSettings().setDatabaseEnabled(true);
		mapView.getSettings().setDomStorageEnabled(true);
		mapView.getSettings().setUseWideViewPort(true);
		mapView.getSettings().setLoadWithOverviewMode(true);
        
		mapView.setWebViewClient(new WebViewClient(){
			
			@Override
			public void onPageFinished(WebView view, String url){
				if(pDialog.isShowing())
					pDialog.dismiss();
			}
		});
		mapView.loadUrl(u);
	}
	
	
}


