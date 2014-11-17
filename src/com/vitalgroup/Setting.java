package com.vitalgroup;

import java.util.regex.Pattern;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Setting {
	
	// set 1 second to splash screen time out
	public static final int SPLASH_TIMEOUT = 1000;
	
	// web service url
	public static final String WEB_URL = "http://www.vitalgroup.biz/vgmobile/vgc_ws.php";
	//http://70.40.201.192/vgc/vgc_ws.php?func=get_user_info&imei=863425013483776&token=31f7070affb3de320b85bdb71dd1316661917e923da586b5d3b70ef168664a408f86299e2846f4fdf43d1a1f01555b50
	// map service url
	public static final String MAP_URL = "http://www.vitalgroup.biz/vgmobile/m.map.php";
	
	// bearer access token 
	public static final String TOKEN = "31f7070affb3de320b85bdb71dd1316661917e923da586b5d3b70ef168664a408f86299e2846f4fdf43d1a1f01555b50";
	
	// location update time
	public static final int LOCATION_UPDATE_TIME = 59000;	// 1 minute
	
	// get imei number
	public static String get_imei_number(Context c){
		TelephonyManager telMgr = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
		return telMgr.getDeviceId();
	}
	
	// get sim serial number
	public static String get_sim_serial_number(Context c){
		TelephonyManager telMgr = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
		return telMgr.getSimSerialNumber();
	}
	
	// valid email pattern
	public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
		"[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
		"\\@" +
	    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
	    "(" +
	    "\\." +
	    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
	    ")+"
	);
}
