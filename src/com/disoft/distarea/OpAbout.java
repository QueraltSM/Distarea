package com.disoft.distarea;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;*/
 
public class OpAbout extends AppCompatActivity {
  View v; SharedPreferences sharedPrefs; String versionName; int versionCode;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); ActionBar ab = getSupportActionBar();
    versionName=""; versionCode=0;
    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME
    		|ActionBar.DISPLAY_HOME_AS_UP); ab.setIcon(R.drawable.action_about);
    ab.setTitle(getString(R.string.about));
    setContentView(R.layout.opabout); v = findViewById(R.id.base);
    ((TextView)findViewById(R.id.enlace)).setOnClickListener(new OnClickListener(){
    	@Override public void onClick(View v) {
    		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    		Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
    				Uri.parse("http://www.disoft.es/"));
    		startActivity(browserIntent);}});
    TextView version = (TextView)findViewById(R.id.version);
		try{versionName = getBaseContext().getPackageManager().getPackageInfo(getBaseContext()
					.getPackageName(), 0).versionName;
		versionCode = getBaseContext().getPackageManager().getPackageInfo(getBaseContext()
				.getPackageName(), 0).versionCode;
		}catch (NameNotFoundException e) {}
    version.setText(version.getText()+" "+versionName+"\nBuild: "+versionCode); }
    
    @Override public boolean onCreateOptionsMenu(Menu menu){return true;}
    @Override public boolean onOptionsItemSelected(MenuItem item) {
    	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    	super.onBackPressed(); return true;}
}