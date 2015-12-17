package com.disoft.distarea;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
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

/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;*/
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.Est;

public class Opciones extends PreferenceActivity{
//public class Opciones extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	SharedPreferences sharedPrefs; LayoutInflater layoutInflater;
	private OnSharedPreferenceChangeListener ospcltono, ospclmain;
	View v, popupView; PopupWindow popupWindow;  
	DatabaseHandler db; ListPreference main;
	private AppCompatDelegate delegado;

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		getDelegado().installViewFactory();
		getDelegado().onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.opciones);
		getDelegado().setContentView(R.layout.opciones);

		ActionBar ab = getDelegado().getSupportActionBar();
		db = new DatabaseHandler(this);
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		ab.setTitle(getString(R.string.opciones));
//		ab.setIcon(R.drawable.action_settings);

		findViewById(R.id.info).setVisibility(View.GONE);
		v = findViewById(R.id.base);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		//Tono Notificaciones
		Uri ringtoneUri = Uri.parse(sharedPrefs.getString("tono", ""));
		Ringtone ringtone = RingtoneManager.getRingtone(getBaseContext(), ringtoneUri);
		RingtonePreference tonos = (RingtonePreference) findPreference("tono");
		
		if(ringtone != null){
			if(ringtone.getTitle(getBaseContext()) != null){
				String mitono = ringtone.getTitle(getBaseContext());
				tonos.setSummary(mitono);
			}else tonos.setSummary("");
		}
  	  	  
	    ospcltono = new SharedPreferences.OnSharedPreferenceChangeListener(){
	    	@Override	
	    	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
	    		Uri ringtoneUri = Uri.parse(prefs.getString("tono", ""));
	    		Ringtone ringtone = RingtoneManager.getRingtone(getBaseContext(), ringtoneUri);
	    		try{String mitono = ringtone.getTitle(getBaseContext());
	    			RingtonePreference tonos = (RingtonePreference) findPreference("tono"); 
	    			tonos.setSummary(mitono);
	    		}catch(NullPointerException e){e.printStackTrace();};
	    	}
	    };
	    sharedPrefs.registerOnSharedPreferenceChangeListener(ospcltono);
	    
	    //Establecimiento inicial
	    main = (ListPreference) findPreference("main");

	    //Preparación del desplegable
	    List<String> entradas = new ArrayList<String>(); 
	    List<String> numeros = new ArrayList<String>();
	    entradas.add("Ninguno (Resumen)"); numeros.add("0");
	    for(Est e: db.getAllEstablecimientos()){
	    	if(e.getFav()){ 
	    		entradas.add(e.getNombre()); numeros.add(String.valueOf(e.getEid())); 
	    	}
	    }
		main.setEntries(entradas.toArray(new String[entradas.size()])); 
		main.setEntryValues(numeros.toArray(new String[numeros.size()]));
		main.setSummary(main.getEntry());
		ospclmain= new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				main = (ListPreference) findPreference("main");
				main.setSummary(main.getEntry()); }};
		sharedPrefs.registerOnSharedPreferenceChangeListener(ospclmain);
	      
	      Preference myPref = findPreference("opciones2");
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
	    		  			  Intent intent = new Intent(Opciones.this, Opciones2.class);
	    	                  startActivity(intent);
	    		  		}else ((EditText)popupView.findViewById(R.id.mspass)).requestFocus();
	    		  	}});
	    		  	no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
	    		  	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    		  		popupWindow.dismiss();
	    		  	}});
	    		  return true;}else{
                  Intent intent = new Intent(Opciones.this, Opciones2.class);
                  startActivity(intent);
                  return true;}
	    	  }});
	      
	      final Preference myPref2 = findPreference("opciones3");
	      myPref2.setOnPreferenceClickListener(new OnPreferenceClickListener() {
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
		    		  			  Intent intent = new Intent(Opciones.this, Opciones3.class);
		    		  			  startActivity(intent);
		    		  		}else ((EditText)popupView.findViewById(R.id.mspass)).requestFocus();
		    		  	}});
		    		  	no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
		    		  	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		    		  		popupWindow.dismiss();
		    		  	}});
		    		  return true;}else{
		    			  Intent intent = new Intent(Opciones.this, Opciones3.class);
		    			  startActivity(intent);
		    			  return true;}
		    		  }});
	      
	      //OpcionesVoz
	      Preference myPref3 = findPreference("opcionesTec");
	      myPref3.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	    	  @Override public boolean onPreferenceClick(Preference preference) {
	    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		    		  			  Intent intent = new Intent(Opciones.this, OpcionesTec.class);
		    		  			  startActivity(intent);
		    		  return true;
		    		  }});
	      
	      //About
				Preference about = findPreference("opAbout");
	      about.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	    	  @Override public boolean onPreferenceClick(Preference preference) {
	    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	  			  Intent intent = new Intent(Opciones.this, OpAbout.class);
	  			  startActivity(intent); return true;
	    	  }});
	}
	
	@Override public void onDestroy() { super.onDestroy();
		getDelegado().onDestroy();
	    if(popupWindow != null) popupWindow.dismiss(); }
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, ListaCompra.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); return true; }
        return true; }
	
	@Override public void onBackPressed() { super.onBackPressed();   
	    Intent intent = new Intent(this, ListaCompra.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent); }

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