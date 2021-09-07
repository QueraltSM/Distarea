package com.disoft.distarea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;*/

public class Opciones3 extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	SharedPreferences sharedPrefs; LayoutInflater layoutInflater;
	View v, popupView; PopupWindow popupWindow;
	ListPreference recpass, ps; EditTextPreference mail, rs; CheckBoxPreference ms; 
	private OnSharedPreferenceChangeListener ospclrecpass, ospclmail, ospclps, ospclrs, ospclms;
	String asteriscos="********************", rp; int flagms = 0, cambios = 0;
	private AppCompatDelegate delegado;

	@Override protected void onCreate(Bundle savedInstanceState) {
		getDelegado().installViewFactory();
		getDelegado().onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
	  ActionBar ab = getDelegado().getSupportActionBar();
	  ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME
	  		|ActionBar.DISPLAY_HOME_AS_UP);
	  ab.setTitle(getString(R.string.opciones));
	  ab.setSubtitle(getString(R.string.seg));
	  ab.setIcon(R.drawable.action_settings);
	  addPreferencesFromResource(R.xml.opciones3);
	  getDelegado().setContentView(R.layout.opciones);
	  findViewById(R.id.info).setVisibility(View.GONE);
	  v = findViewById(R.id.base);
	  sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	  if(!sharedPrefs.getBoolean("ms", false)) flagms=1;
	      
	  // Recordar contraseña
	  recpass = (ListPreference) findPreference("recpass");
	  recpass.setSummary(recpass.getEntry());
	  ospclrecpass = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override public void onSharedPreferenceChanged(SharedPreferences prefs,String key) {
			recpass = (ListPreference) findPreference("recpass");
			recpass.setSummary(recpass.getEntry()); cambios=1; }};
		sharedPrefs.registerOnSharedPreferenceChangeListener(ospclrecpass);
		  
		// E-mail
		mail = (EditTextPreference) findPreference("mail");
		if(!sharedPrefs.getString("mail", "").equals(""))
		mail.setSummary(sharedPrefs.getString("mail", ""));
		else mail.setSummary(getString(R.string.option3Undefined));
		ospclmail = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override public void onSharedPreferenceChanged(SharedPreferences prefs,String key) {
		 	mail = (EditTextPreference) findPreference("mail");
		  if(isEmailValid(sharedPrefs.getString("mail", ""))) {  cambios=1;
		  	mail.setSummary(sharedPrefs.getString("mail", "")); }
		  else if(sharedPrefs.getString("mail", "").equals("")
		  		&& !sharedPrefs.getString("recpass", "").equals("email")){
		  	mail.setSummary("");
		  }else{ mail.setSummary(getString(R.string.options3InvalidEmail));
		  Toast.makeText(getBaseContext(),getString(R.string.options3CheckEmail),Toast.LENGTH_LONG)
		  	.show();}}};
		sharedPrefs.registerOnSharedPreferenceChangeListener(ospclmail);
		
		// Pregunta Secreta
		ps = (ListPreference) findPreference("ps");
		ps.setSummary(ps.getEntry());
		ospclps = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override public void onSharedPreferenceChanged(SharedPreferences prefs,String key) {
			ps = (ListPreference) findPreference("ps");
			ps.setSummary(ps.getEntry()); cambios=1; }};
		sharedPrefs.registerOnSharedPreferenceChangeListener(ospclps);
	      
	  // Respuesta Secreta
		rs = (EditTextPreference) findPreference("rs");
		if(sharedPrefs.getString("rs", "").equals("")) rs.setSummary(getString(R.string.option3Undefined));
		else{ if(sharedPrefs.getBoolean("ms", false)){
			if(sharedPrefs.getString("rs", "").length()>=20) rs.setSummary(asteriscos);
			else rs.setSummary(asteriscos.substring(20-sharedPrefs.getString("rs", "").length()));
		}else rs.setSummary(sharedPrefs.getString("rs", "")); }
		ospclrs = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			rs = (EditTextPreference) findPreference("rs");
			if(sharedPrefs.getString("rs", "").equals("")) rs.setSummary(getString(R.string.option3Undefined));
			else{ if(sharedPrefs.getBoolean("ms", false)){ cambios=1;
				if(sharedPrefs.getString("rs", "").length()>=20) rs.setSummary(asteriscos);
				else rs.setSummary(asteriscos.substring(20-sharedPrefs.getString("rs", "").length()));
			}else rs.setSummary(sharedPrefs.getString("rs", "")); } }};
		sharedPrefs.registerOnSharedPreferenceChangeListener(ospclrs);
	      
	  //Modo Seguro
	  ospclms = new SharedPreferences.OnSharedPreferenceChangeListener(){
	  @Override	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
	    final CheckBoxPreference ms = (CheckBoxPreference)findPreference("ms");
	    if(sharedPrefs.getBoolean("ms",false)==false){ if(flagms==0){
	    	SharedPreferences.Editor spe = sharedPrefs.edit();
	  		spe.putBoolean("ms", true); spe.commit(); ms.setChecked(true);
	    	layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
	    	popupView = layoutInflater.inflate(R.layout.popupms, null);  
	    	final PopupWindow popupWindow = new PopupWindow(popupView,android.view.ViewGroup.LayoutParams.MATCH_PARENT,
	    	 		android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
	    	popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
	    	popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
	    	((TextView) popupView.findViewById(R.id.texto)).setText((((TextView)popupView
	    			.findViewById(R.id.texto)).getText())+" "+getString(R.string.options3DisableSafeMode));
	    	ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
	    	ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
	    	si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	    	no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	    	si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
	    	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    	if(((EditText)popupView.findViewById(R.id.mspass)).getText().toString()
	    			.equals(sharedPrefs.getString("pass", ""))){ flagms=1;
	    	  SharedPreferences.Editor spe = sharedPrefs.edit();
	    	  spe.putBoolean("ms", false); spe.commit(); ms.setChecked(false);
	    	  popupWindow.dismiss();
	    	}else ((EditText)popupView.findViewById(R.id.mspass)).requestFocus(); }});
	    	no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
	    	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    	popupWindow.dismiss(); }});
	    }}else{
	    	SharedPreferences.Editor spe = sharedPrefs.edit();
	  		spe.putBoolean("ms", true); spe.commit(); ms.setChecked(true);
	  		flagms=0; }
	   	}};
	    sharedPrefs.registerOnSharedPreferenceChangeListener(ospclms);
	
	    Preference myPref = findPreference("opcionesav");
      myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
    	  @Override public boolean onPreferenceClick(Preference preference) {
    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    		  if(sharedPrefs.getBoolean("ms",false)==true){
    			layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
    		  	popupView = layoutInflater.inflate(R.layout.popupms, null);  
    		  	popupWindow = new PopupWindow(popupView,android.view.ViewGroup.LayoutParams.MATCH_PARENT,
    		   		android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
    		  	popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
    		  	popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
    		  	((TextView) popupView.findViewById(R.id.texto)).setText
    		  	((((TextView) popupView.findViewById(R.id.texto)).getText())+" "+getString(R.string.motivo4));
    		  	ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
    		  	ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
    		  	si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
    		  	no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
    		  	si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
    		  		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    		  		if(((EditText)popupView.findViewById(R.id.mspass)).getText().toString()
    		  				.equals(sharedPrefs.getString("pass", ""))){
    		  			  popupWindow.dismiss();
    		  			  Intent intent = new Intent(Opciones3.this, OpcionesAv.class);
    	                  startActivity(intent);
    		  		}else ((EditText)popupView.findViewById(R.id.mspass)).requestFocus();
    		  	}});
    		  	no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
    		  	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    		  		popupWindow.dismiss();
    		  	}});
    		  return true;}else{
    		  	LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
      			popupView = layoutInflater.inflate(R.layout.popupsino, null);  
      			popupWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
      			popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
      			popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
      			((TextView) popupView.findViewById(R.id.texto)).setText(getString(R.string.options3Warning));
      			ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
    		  	ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
    		  	si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
    		  	no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
    		  	si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
    		  		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
                Intent intent = new Intent(Opciones3.this, OpcionesAv.class);
                popupWindow.dismiss(); startActivity(intent); }});
    		  	no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
    		  		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    		  		popupWindow.dismiss(); }});
    		  }return true;
    	  }});
	    
	}
	
	@Override public void onDestroy() {
	    super.onDestroy(); if(popupWindow != null) popupWindow.dismiss(); }
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if(sharedPrefs.getBoolean("ch",true)==true) v.performHapticFeedback(1);
        if (item.getItemId() == android.R.id.home) {
        	if(cambios==1) new ReplicaSeguridad().execute();
          Intent intent = new Intent(this, Opciones.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent); return true;}
        return true;}
	
	@Override public void onBackPressed() {
	    super.onBackPressed();
	    if(cambios==1) new ReplicaSeguridad().execute();
	    Intent intent = new Intent(this, Opciones.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent); }

	@Override public void onSharedPreferenceChanged(SharedPreferences sp, String s) {}
	
	//Comprobador de e-mail correcto
    public static boolean isEmailValid(String email) {
        boolean isValid = false; CharSequence inputStr = email;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) { isValid = true; } return isValid; }

    private class ReplicaSeguridad extends AsyncTask<String, Void, Boolean> {
    	@Override protected Boolean doInBackground(String... arg0) {
    		try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
				try{DriverManager.setLoginTimeout(20);
				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st = conn.createStatement();
				st.executeUpdate("UPDATE clienteglobal SET " +
						"mail='"+sharedPrefs.getString("mail", "")+"'," +
						"recpass='"+recpass.getValue()+"'," +
						"pregunta='"+sharedPrefs.getString("ps", "")+"'," +
						"respuesta='"+sharedPrefs.getString("rs", "")+"' " +
						"WHERE id="+sharedPrefs.getInt("id",0));
				st.close(); conn.close(); }catch(SQLException e){e.printStackTrace();}
    	return true; }}

	private AppCompatDelegate getDelegado() {
		if (delegado == null) {
			delegado = AppCompatDelegate.create(this, null);
		}
		return delegado;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getDelegado().onPostCreate(savedInstanceState);
	}

	/*@Override
	public void setContentView(View view) {
		getDelegado().setContentView(view);
	}*/

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		getDelegado().setContentView(view, params);
	}

	@Override
	public void addContentView(View view, ViewGroup.LayoutParams params) {
		getDelegado().addContentView(view, params);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		getDelegado().onPostResume();
	}

	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		super.onTitleChanged(title, color);
		getDelegado().setTitle(title);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		getDelegado().onConfigurationChanged(newConfig);
	}

	@Override
	protected void onStop() {
		super.onStop();
		getDelegado().onStop();
	}

	public void invalidateOptionsMenu() {
		getDelegado().invalidateOptionsMenu();
	}
  
}