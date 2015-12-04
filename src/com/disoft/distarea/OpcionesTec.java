package com.disoft.distarea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;*/
import com.disoft.distarea.extras.FileUtils;

public class OpcionesTec extends PreferenceActivity {
	PreferenceScreen cuentadrive;
	SharedPreferences sharedPrefs;
	View v;

	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		AppCompatActivity apa = new AppCompatActivity();
		ActionBar ab = apa.getSupportActionBar();
		//db = new DatabaseHandler(this);
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME |ActionBar.DISPLAY_HOME_AS_UP);
		ab.setTitle("Opciones Técnicas");
		ab.setIcon(R.drawable.action_settings);
		setContentView(R.layout.opciones);
		findViewById(R.id.info).setVisibility(View.GONE);
		addPreferencesFromResource(R.xml.opcionestec);
		v = findViewById(R.id.base);
		
		final PreferenceScreen cuentadrive = (PreferenceScreen) findPreference("cuentadrive");
		cuentadrive.setSummary(//SharedPreferencesController.getValue(getBaseContext(), "_resource"));
				sharedPrefs.getString("cuentagoogle", "*No asignada*"));
		/*cuentadrive.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	    	  @Override public boolean onPreferenceClick(Preference preference) {
	    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    		  if(!sharedPrefs.getString("cuentagoogle", "*No asignada*").equals("*No asignada*")){
	    			  AlertDialog.Builder builder = new AlertDialog.Builder(OpcionesTec.this);
	    			  builder.setTitle("Desconectar cuenta Google Drive")
	    			  		 .setMessage("Está seguro de querer salir de la cuenta de "
	    			  				 +sharedPrefs.getString("cuentagoogle", "*No asignada*"))
	    			  		 .setCancelable(false)
	    			  		 .setIcon(R.drawable.dc)
	    			  		 .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
	 	    		            public void onClick(DialogInterface dialog, int whichButton) {
		    		            	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		    		            	//Diversos intentos de quitar la token por defecto, para poder
		    		            	//CAMBIAR de usuario. Actualmente sólo reconecta al mismo.
		    		            	*//*
		    		            	//new quitarCuenta().execute();
		    		            	
		    		            		GoogleApiClient gac = new GoogleApiClient.Builder(getBaseContext())
		    		                    	.addApi(Drive.API).addApi(Plus.API)
		    		                    	.addScope(Drive.SCOPE_FILE).build();
		    		            		//gac.reconnect();
		    		            		try{
		    		            			
		    		            		GoogleAuthUtil.clearToken(getBaseContext(), 
												GoogleAuthUtil.getToken(getBaseContext(),null,null));
		    		            		}catch(Exception e){e.printStackTrace();}
		    		            		gac.disconnect();
									*//*
		    		            	SharedPreferences.Editor spe = sharedPrefs.edit();
		    		            	spe.putString("cuentagoogle", "*No asignada*").commit();
		    		            	SharedPreferencesController.nukeAll(getBaseContext());
		    		            	cuentadrive.setSummary(sharedPrefs.getString("cuentagoogle", "*No asignada*"));
		    		                dialog.dismiss();
		    		            }})
	    			  		 .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	    		            public void onClick(DialogInterface dialog, int whichButton) {
	    		            	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    		                dialog.dismiss();
	    		            }}).show();
	    		  }
	    		  return true;
	    	  }});*/
	
	
	final PreferenceScreen exportarpreferencias = (PreferenceScreen) findPreference("exportarpreferencias");
	exportarpreferencias.setOnPreferenceClickListener(new OnPreferenceClickListener() {
    	  @Override public boolean onPreferenceClick(Preference preference) {
    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    		  try{
    		  File f1 = new File(File.separator + "data" + File.separator + "data" + 
  		    		File.separator + getBaseContext().getPackageName() + File.separator + 
  		    		"shared_prefs" + File.separator + "com.disoft.distarea_preferences.xml");
    		  File f2 = new File(getExternalCacheDir()+File.separator+"preferencias.txt");
    		  copy(f1,f2); }
    		  catch(Exception e){e.printStackTrace();}
    		  Toast.makeText(getBaseContext(), "Fichero de preferencias exportado", Toast.LENGTH_LONG).show();
    		  return true;
    	  }});
		
	final PreferenceScreen exportarbd = (PreferenceScreen) findPreference("exportarbd");
	exportarbd.setOnPreferenceClickListener(new OnPreferenceClickListener() {
    	  @Override public boolean onPreferenceClick(Preference preference) {
    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    		  String bd = File.separator + "data" + File.separator + "data" + 
	  		    		File.separator + getBaseContext().getPackageName() + File.separator +
	  		    		"databases" + File.separator + "DistareaDB",
	  		    	 dest = getExternalCacheDir()+File.separator+"("+sharedPrefs.getInt("id",0)+")DistareaDB.sq3";
    		  try{FileUtils.copyFile(bd, dest);
    		  Toast.makeText(getBaseContext(), "Base de datos exportada", Toast.LENGTH_LONG).show();
    		  Intent i = new Intent(Intent.ACTION_SEND);
    		  i.setType("vnd.android.cursor.dir/email");
    		  String to[] = {"comercial@disoftweb.com"};
    		  i.putExtra(Intent.EXTRA_EMAIL, to);
    		  i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+dest));
    		  i.putExtra(Intent.EXTRA_SUBJECT, "Soporte Distarea");
    		  startActivity(Intent.createChooser(i, "Enviar Base de Datos mediante..."));
    		  }catch(Exception e){e.printStackTrace();}
    		  return true;
    	  }});
	}
	
	public void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, Opciones.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); return true;}
        return true;}
	
	/*private class quitarCuenta extends AsyncTask<String, Void, Boolean> {
		
		@Override protected Boolean doInBackground(String... params) {
		try{/*AccountManagerFuture<Bundle> future = AccountManager.get(getBaseContext())
    			.getAuthToken(AccountManager.get(getBaseContext())
    			.getAccountsByType("com.google")[0], "ah", null,
    			OpcionesTec.this, null, null);
			Bundle 	bundle = future.getResult();
			String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);*//*
			String authToken = AccountManager.get(getBaseContext()).peekAuthToken(getBaseContext(),
					Constants.AUTHTOKEN_TYPE);
			AccountManager.get(getBaseContext()).invalidateAuthToken("com.google", authToken);
		} catch (Exception e) {e.printStackTrace();}
	return true;}}*/
}
