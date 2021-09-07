package com.disoft.distarea;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.disoft.distarea.extras.NotificacionMensajes;

public class Autorun extends BroadcastReceiver {
	SharedPreferences sp;

    @Override public void onReceive(Context context, Intent intent) {
    	if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
    	sp = PreferenceManager.getDefaultSharedPreferences(context);
    	if (!sp.getString("nombre", "").equals("")){
    		Intent myStarterIntent = new Intent(context, NotificacionMensajes.class);
        myStarterIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(myStarterIntent); }
    }}
}
