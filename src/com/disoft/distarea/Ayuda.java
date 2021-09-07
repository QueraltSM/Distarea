package com.disoft.distarea;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.disoft.distarea.extras.Automailer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.view.MenuCompat;

//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.SherlockActivity;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuInflater;
//import com.actionbarsherlock.view.MenuItem;

public class Ayuda extends AppCompatActivity{
	SharedPreferences sharedPrefs; WebView wv;
	View v, popupView; PopupWindow popupWindow;
	
	public boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    if(sharedPrefs.getInt("internetmode",0)==0)
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	    else return false;}
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); setContentView(R.layout.ayuda);
		ActionBar ab = getSupportActionBar();
    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|
        		ActionBar.DISPLAY_HOME_AS_UP);
    ab.setTitle(getString(R.string.ayuda)); ab.setIcon(R.drawable.action_help);
		wv = (WebView)findViewById(R.id.webView); v = findViewById(R.id.base);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		if(isNetworkAvailable()) recargaVentas();		
		else{TextView nointernet = new TextView(getBaseContext());
			nointernet.setText(R.string.nointernet); nointernet.setTextColor(Color.BLACK);
			nointernet.setGravity(17); LinearLayout base = (LinearLayout) findViewById(R.id.ll);
			base.removeView(wv); base.addView(nointernet); base.setOrientation(LinearLayout.VERTICAL);
			ImageButton ajustes = new ImageButton(getBaseContext());
			ImageButton recargar = new ImageButton(getBaseContext());
			LinearLayout linea = new LinearLayout(getBaseContext());
			ajustes.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
			recargar.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
			ajustes.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
				if(sharedPrefs.getBoolean("ch",true)==true) v.performHapticFeedback(1);
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));}});
			recargar.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
				if(sharedPrefs.getBoolean("ch",true)==true) v.performHapticFeedback(1);
				startActivity(new Intent(Ayuda.this,Ayuda.class)); finish();}});
			linea.addView(ajustes); linea.addView(recargar); base.addView(linea);
			linea.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));
			linea.setGravity(17);
			ajustes.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
			recargar.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
			((LinearLayout.LayoutParams)linea.getLayoutParams()).weight = 0;
		}
	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ayuda, menu); return true; }
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(Ayuda.this, ListaCompra.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); return true;
        }else if (item.getItemId() == R.id.support) {
        	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
    			{Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
    			if(display.getRotation()==Surface.ROTATION_90)
    				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    			else if(display.getRotation()==Surface.ROTATION_270){
    				if(android.os.Build.VERSION.SDK_INT == 8)
    					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    				else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
    		}else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			final LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
			popupView = layoutInflater.inflate(R.layout.popupsino, null);  
			popupWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
			popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
			popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, -100);
			((TextView) popupView.findViewById(R.id.texto)).setText(R.string.textoayuda);
			final EditText email = (EditText) getLayoutInflater().inflate(R.layout.edittext, null); email.setEms(15);
			email.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE 
					| InputType.TYPE_TEXT_FLAG_MULTI_LINE
					| InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
			((LinearLayout)popupView.findViewById(R.id.cajatexto)).addView(email);
			ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
			ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
			if(!isNetworkAvailable()) si.setEnabled(false);
			si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
			si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
				if(sharedPrefs.getBoolean("ch",true)==true) v.performHapticFeedback(1);
				if(sharedPrefs.getBoolean("ms",false)==true){ popupWindow.dismiss();
				popupView = layoutInflater.inflate(R.layout.popupms, null);  
				popupWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
				popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
				popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
				((TextView) popupView.findViewById(R.id.texto)).setText
				((((TextView) popupView.findViewById(R.id.texto)).getText())+" "+getString(R.string.motivo1));
				ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
				ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
				si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
				no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
				si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)==true) v.performHapticFeedback(1);
					if(((EditText)popupView.findViewById(R.id.mspass)).getText().toString()
							.equals(sharedPrefs.getString("pass", ""))){
						Automailer am = new Automailer(getString(R.string.userauto),getString(R.string.passauto));
						String[] para={getString(R.string.disoftayuda)}; am.setTo(para);
						am.setFrom(getString(R.string.userauto));
						am.setSubject("ID:"+sharedPrefs.getInt("id",0)+" "+getString(R.string.asuntoayuda));
						am.setBody(email.getText().toString());
						try {if(am.send()) { 
							Toast.makeText(Ayuda.this, getString(R.string.menviado), Toast.LENGTH_LONG).show(); 
				    	} else { 
				      Toast.makeText(Ayuda.this, getString(R.string.mfallo), Toast.LENGTH_LONG).show(); 
				    }} catch(Exception e) {e.printStackTrace();
				      Toast.makeText(Ayuda.this, getString(R.string.merror), Toast.LENGTH_LONG).show();} 
					  popupWindow.dismiss();}
					else ((EditText)popupView.findViewById(R.id.mspass)).requestFocus();
					}});
					no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)==true) v.performHapticFeedback(1);
					 popupWindow.dismiss();
					 Toast.makeText(getApplicationContext(), getString(R.string.cancel1), Toast.LENGTH_LONG).show();
					}});
				}else{
					Automailer am = new Automailer(getString(R.string.userauto),getString(R.string.passauto));
					String[] para={getString(R.string.disoftayuda)}; am.setTo(para);
					am.setFrom(getString(R.string.userauto));
					am.setSubject("ID:"+sharedPrefs.getInt("id",0)+" "+getString(R.string.asuntoayuda));
					am.setBody(email.getText().toString());
					try {if(am.send()) { 
						Toast.makeText(Ayuda.this, getString(R.string.menviado), Toast.LENGTH_LONG).show(); 
			      } else { 
			      Toast.makeText(Ayuda.this, getString(R.string.mfallo), Toast.LENGTH_LONG).show(); 
			    }} catch(Exception e) {e.printStackTrace();
			    	Toast.makeText(Ayuda.this, getString(R.string.merror), Toast.LENGTH_LONG).show();} 
				  popupWindow.dismiss();}
			}});
			no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
			no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
				if(sharedPrefs.getBoolean("ch",true)==true) v.performHapticFeedback(1);
				popupWindow.dismiss();
				Toast.makeText(getApplicationContext(), getString(R.string.cancel1), Toast.LENGTH_LONG).show();
			}});return true;
        }return true;
    }
	
	@Override public void onBackPressed() {
		super.onBackPressed();   
	  Intent intent = new Intent(Ayuda.this, ListaCompra.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);}

	public void recargaVentas(){
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setBuiltInZoomControls(true);
    wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    //wv.loadUrl("https://docs.google.com/viewer?url=http://www.dmhc.ca.gov/testpage.pdf");
    //wv.loadUrl("http://docs.google.com/gview?embedded=true&url=http://www.dmhc.ca.gov/testpage.pdf");
    wv.loadUrl("http://www.distarea.es/videos.html#");
		wv.setWebViewClient(new WebViewClient());}
}