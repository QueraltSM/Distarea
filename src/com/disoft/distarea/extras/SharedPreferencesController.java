package com.disoft.distarea.extras;

import android.content.Context;

public class SharedPreferencesController {

	private static final String sharedPrefFileKey = "com.disoft.driveuploader.PREFERENCE_FILE_KEY";
	
	public static void putValue(String value,Context context,String keyName){
		context.getSharedPreferences(sharedPrefFileKey, Context.MODE_PRIVATE).edit().putString(keyName, value).commit();
	}
	
	public static String getValue(Context context,String keyName){
		return context.getSharedPreferences(sharedPrefFileKey, Context.MODE_PRIVATE).getString(keyName, null);
	}	
	
	public static void resetKey(Context context, String keyName){
		context.getSharedPreferences(sharedPrefFileKey, Context.MODE_PRIVATE).edit().remove(keyName).commit();
	}
	
	public static void nukeAll(Context context){
		context.getSharedPreferences(sharedPrefFileKey, Context.MODE_PRIVATE).edit().clear().commit();
	}
	
	public static String getResourceFolder(Context context, String businessName){
		return context.getSharedPreferences(sharedPrefFileKey, Context.MODE_PRIVATE).getString(businessName+"_resource", null);
	}
	public static String getResourceID(Context context){
		return context.getSharedPreferences(sharedPrefFileKey, Context.MODE_PRIVATE).getString("_resource", "FAIL");
	}
	public static String getRootFolder(Context context){
		return context.getSharedPreferences(sharedPrefFileKey, Context.MODE_PRIVATE).getString("_root", "");
	}
}
