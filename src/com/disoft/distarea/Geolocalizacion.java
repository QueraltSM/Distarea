package com.disoft.distarea;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;

/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;*/
import android.support.v7.app.ActionBar;


public class Geolocalizacion extends AppCompatActivity {
	WebView wv; View v, popupView; PopupWindow popupWindow; SharedPreferences sharedPrefs;	

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); final ActionBar ab = getSupportActionBar();
    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME
    		|ActionBar.DISPLAY_HOME_AS_UP);
    ab.setTitle(getString(R.string.geolocalizacion)); ab.setIcon(R.drawable.location_place);
    ab.setSubtitle(getIntent().getStringExtra("nombre"));
    setContentView(R.layout.geolocalizacion);
    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    v = (View) findViewById(R.id.base); wv = (WebView) findViewById(R.id.wv);
    wv.loadUrl("http://www.google.es/maps?q="+getIntent().getStringExtra("gps")+
    		"("+getIntent().getStringExtra("nombre")+")");
    wv.setWebViewClient(new WebViewClient()); wv.getSettings().setJavaScriptEnabled(true); }
	
	 @Override public boolean onOptionsItemSelected(MenuItem item) {
		 if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	   if (item.getItemId() == android.R.id.home) {
	  	 finish();return true; } return true; } 
}