package com.disoft.distarea;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class VentanaFirma extends Activity {
	
SharedPreferences sharedPrefs;
Boolean limpieza = false;
FrameLayout fl;
	
	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		setContentView(R.layout.ventanafirma);
		ImageButton cancelar = (ImageButton) findViewById(R.id.cancelar);
		ImageButton borrar = (ImageButton) findViewById(R.id.borrar);
		ImageButton aceptar = (ImageButton) findViewById(R.id.aceptar);
		fl = (FrameLayout) findViewById(R.id.firma);
		fl.addView(new Firma(this));
		cancelar.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			Intent returnIntent = new Intent(); setResult(RESULT_CANCELED, returnIntent); 
			finish();}});
		borrar.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    	((Firma) fl.getChildAt(0)).borrarFirma(); }});
    aceptar.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
    	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
     	((Firma) fl.getChildAt(0)).guardarFirma(getIntent().getStringExtra("pid"));
     	Intent returnIntent = new Intent();
      //returnIntent.putExtra("result",((Firma) fl.getChildAt(0)).nfirma);
     	returnIntent.putExtra("result",getIntent().getStringExtra("pid"));
      setResult(RESULT_OK,returnIntent); finish();
    }});
	}	
}
