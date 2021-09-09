package com.disoft.distarea;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.actionbarsherlock.app.SherlockActivity;
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.Est;
import com.disoft.distarea.models.Msj;

public class Registro extends AppCompatActivity {
	EditText nombre, pass, pass2; TextView estado; Spinner pais; View v;
	SharedPreferences sharedPrefs; DatabaseHandler db; int flagexiste;
	String tipo, est="", email, ref="",gmail; SharedPreferences.Editor spe;
	Locale spanish = new Locale("es", "ES");
	SimpleDateFormat sdfdia = new SimpleDateFormat("yyyy-MM-dd",spanish),
			 		 sdfhora = new SimpleDateFormat("HH:mm:ss",spanish);
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); setContentView(R.layout.registro);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		db = new DatabaseHandler(getBaseContext()); v = findViewById(R.id.basereg);
		new Inicio().execute();		
		//if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			//v.setBackgroundDrawable(getResources().getDrawable(R.drawable.marcadeagua_land));
		final ImageButton continuar = (ImageButton) findViewById(R.id.continuar);
		final ImageButton clrnombre = (ImageButton) findViewById(R.id.clrnombre);
		final ImageButton clrpass = (ImageButton) findViewById(R.id.clrpass);
		final ImageButton clrpass2 = (ImageButton) findViewById(R.id.clrpass2);
		final ImageButton seepass = (ImageButton) findViewById(R.id.seepass);
		final ImageButton seepass2 = (ImageButton) findViewById(R.id.seepass2);
		nombre = (EditText) findViewById(R.id.nombre); pass = (EditText) findViewById(R.id.pass);
		pass2 = (EditText) findViewById(R.id.pass2); pais = (Spinner) findViewById(R.id.pais);
		estado = (TextView) findViewById(R.id.estado);
		
		nombre.addTextChangedListener(new TextWatcher() {@Override public void 
      onTextChanged(CharSequence s, int start, int before, int count) {
      	if (nombre.getText().toString().length()>0) clrnombre.setVisibility(View.VISIBLE);
        else clrnombre.setVisibility(View.INVISIBLE);}
      @Override public void afterTextChanged(Editable arg0) {}
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    });
		clrnombre.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			nombre.setText(""); nombre.requestFocus(); }});
		pass.addTextChangedListener(new TextWatcher() {@Override public void 
			onTextChanged(CharSequence s, int start, int before, int count) {
       	if (pass.getText().toString().length()>0) clrpass.setVisibility(View.VISIBLE);
        else clrpass.setVisibility(View.INVISIBLE);}
    @Override public void afterTextChanged(Editable arg0) {}
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    });
		seepass.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			if(pass.getInputType()==(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)){
				pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				seepass.setImageDrawable(getResources().getDrawable(R.drawable.eyesel4));
			}else{
				
				pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
				seepass.setImageDrawable(getResources().getDrawable(R.drawable.eye));
			}
		}});
		clrpass.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			pass.setText(""); pass.requestFocus(); }});
		pass2.addTextChangedListener(new TextWatcher() {@Override public void 
			onTextChanged(CharSequence s, int start, int before, int count) {
       	if (pass2.getText().toString().length()>0) clrpass2.setVisibility(View.VISIBLE);
        else clrpass2.setVisibility(View.INVISIBLE);}
    @Override public void afterTextChanged(Editable arg0) {}
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    });
		seepass2.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			if(pass2.getInputType()==(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)){
				pass2.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				seepass2.setImageDrawable(getResources().getDrawable(R.drawable.eyesel4));
			}else{
				pass2.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
				seepass2.setImageDrawable(getResources().getDrawable(R.drawable.eye));
			}
		}});
		clrpass2.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			pass2.setText(""); pass2.requestFocus(); }});
		continuar.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			if (nombre.getText().toString().equals("")){ estado.setText(R.string.nousernameerror);
				estado.setVisibility(View.VISIBLE); }
			else if(!nombre.getText().toString().matches("^[A-Za-z0-9_.��@-]+$")){
				estado.setText(R.string.caraperm); estado.setVisibility(View.VISIBLE); }
			else if (pass.getText().toString().equals("") || pass2.getText().toString().equals("")){
				estado.setText(R.string.nopassworderror); estado.setVisibility(View.VISIBLE); }
			else if(!pass.getText().toString().equals(pass2.getText().toString())){
				estado.setText(R.string.nopassmatch); estado.setVisibility(View.VISIBLE); }
			else if (nombre.getText().toString().length()<6){
				estado.setText(R.string.usernamelength); estado.setVisibility(View.VISIBLE); }
			else if (pass.getText().toString().length()<6){
				estado.setText(R.string.passlength); estado.setVisibility(View.VISIBLE); }
			else{ estado.setText(""); estado.setVisibility(View.INVISIBLE); new Login().execute(); }}});
		
		pass2.setOnKeyListener(new View.OnKeyListener(){
		    public boolean onKey(View v, int keyCode, KeyEvent event){
		        if (event.getAction() == KeyEvent.ACTION_DOWN){
		            switch (keyCode){
		                case KeyEvent.KEYCODE_DPAD_CENTER:
		                case KeyEvent.KEYCODE_ENTER:
		                    continuar.performClick(); return true;
		                default: break;
		            }
		        }return false;
		}});
		
		//Intento ajustado l�nea pa�s
		//Log.e("WIDTH",""+((TextView) findViewById(R.id.txtpass2)).getMeasuredWidth());
		//((LinearLayout)findViewById(R.id.basereg)).invalidate();
		//((TextView) findViewById(R.id.txtpass2)).post(new Runnable(){@Override public void run(){
		final TextView txtpass2 = (TextView) findViewById(R.id.txtpass2);
		txtpass2.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			 @SuppressLint("NewApi") @SuppressWarnings("deprecation")
			 @Override public void onGlobalLayout() {
			   LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
						txtpass2.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
				//Log.e("WIDTH1",""+txtpass2.getWidth());
				((TextView) findViewById(R.id.txtpais)).setLayoutParams(layoutParams1);

			if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
				txtpass2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			   else txtpass2.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}});
		
		
				/*((TextView) findViewById(R.id.txtpass2)).measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
				LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
						((TextView) findViewById(R.id.txtpass2)).getMeasuredWidth(),
						LinearLayout.LayoutParams.WRAP_CONTENT);
				Log.e("WIDTH1",""+((TextView) findViewById(R.id.txtpass2)).getMeasuredWidth());
				((TextView) findViewById(R.id.txtpais)).setLayoutParams(layoutParams1);*/
		//}});
		
		//((TextView) findViewById(R.id.txtpais)).setWidth(((TextView) findViewById(R.id.txtpass2)).getMeasuredWidth());
		/*((TextView) findViewById(R.id.pass2)).measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
		LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
				((EditText) findViewById(R.id.pass2)).getMeasuredWidth(),
				LinearLayout.LayoutParams.WRAP_CONTENT);
		Log.e("WIDTH2",""+((EditText) findViewById(R.id.pass2)).getMeasuredWidth());
		((LinearLayout) findViewById(R.id.restopais)).setLayoutParams(layoutParams2);*/
		
		final EditText pass2 = (EditText) findViewById(R.id.pass2);
		pass2.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			 @SuppressLint("NewApi") @SuppressWarnings("deprecation")
			 @Override public void onGlobalLayout() {
				 LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
							pass2.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
					//Log.e("WIDTH2",""+pass2.getWidth());
					//Log.e("WIDTHM2",""+pass2.getMeasuredWidth());
					((LinearLayout) findViewById(R.id.restopais)).setLayoutParams(layoutParams2);

				if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
					txtpass2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				   else txtpass2.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}});
	}
	
	//M�todo As�ncrono
	private class Login extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog loading; int nmensajes; EditText snmensajes;
	  	ArrayList<Msj> mensajes = new ArrayList<Msj>();

    protected void onPreExecute() {
     	loading = new ProgressDialog(Registro.this);
     	loading.setMessage(getString(R.string.conectando));
     	loading.setCancelable(false); loading.show(); }

    protected void onPostExecute(final Boolean success) {
     	if (loading.isShowing()) {loading.dismiss();}
     	if(flagexiste==1){
     		if (nombre.getText().toString().equals(pass.getText().toString())){
				//XXX ESTE TRAMO TIENE QUE IR DESPU�S DEL LOGIN
				AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
   			 	builder.setTitle("Cambie su contrase�a").setIcon(R.drawable.device_access_secure).setCancelable(false);
   			 	builder.setMessage("Por favor, introduzca una nueva contrase�a, distinta de su nombre de usuario.");
   			 	//Cuadros de texto
   			 	LinearLayout cuadros = new LinearLayout(getBaseContext()), linea1 = new LinearLayout(getBaseContext()), 
   			 			linea2 = new LinearLayout(getBaseContext());
   			 	final TextView estado2 = new TextView(getBaseContext());
				TextView txtpass1 = new TextView(getBaseContext()), txtpass2 = new TextView(getBaseContext());
   			 	txtpass1.setTextColor(getResources().getColor(android.R.color.black));
   			 	txtpass2.setTextColor(getResources().getColor(android.R.color.black));
   			 	final EditText pass1 = (EditText)getLayoutInflater().inflate(R.layout.edittext, null);
   			 	final EditText pass2 = (EditText)getLayoutInflater().inflate(R.layout.edittext, null);
   			 	final ImageButton seepass1 = new ImageButton(getBaseContext());
   			 	final ImageButton seepass2 = new ImageButton(getBaseContext());
   			 	seepass1.setImageDrawable(getResources().getDrawable(R.drawable.eye));
   			 	seepass2.setImageDrawable(getResources().getDrawable(R.drawable.eye));
   			 	seepass1.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
   			 	seepass2.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
   			 	seepass1.setPadding(0,0,0,0); seepass2.setPadding(0,0,0,0);
   			 	seepass1.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
   			 		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
   			 		if(pass1.getInputType()==(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)){
   			 			pass1.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
   			 			seepass1.setImageDrawable(getResources().getDrawable(R.drawable.eyesel4));
   			 		}else{
   			 			pass1.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
   			 			seepass1.setImageDrawable(getResources().getDrawable(R.drawable.eye));
   			 		}
   			 	}});
   			 	seepass2.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			 		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			 		if(pass2.getInputType()==(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)){
			 			pass2.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			 			seepass2.setImageDrawable(getResources().getDrawable(R.drawable.eyesel4));
			 		}else{
			 			pass2.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
			 			seepass2.setImageDrawable(getResources().getDrawable(R.drawable.eye));
			 		}
			 	}});
   			 	
   			 	pass1.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
   			 	pass2.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
   			 	txtpass1.setSingleLine(true); txtpass2.setSingleLine(true);
   			 	DisplayMetrics metrics = new DisplayMetrics();
   			 	getWindowManager().getDefaultDisplay().getMetrics(metrics);
   			 	
   			 	LinearLayout.LayoutParams lpc = new LinearLayout.LayoutParams(
   					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
     			 	lpc.gravity = Gravity.CENTER; lpc.leftMargin=20; lpc.rightMargin=20;
    			LinearLayout.LayoutParams lpd = new LinearLayout.LayoutParams(
  					//LinearLayout.LayoutParams.WRAP_CONTENT
    				metrics.widthPixels/3
  					, LinearLayout.LayoutParams.WRAP_CONTENT);
    			 	lpd.gravity = Gravity.LEFT; //lpd.weight = 1;
    			LinearLayout.LayoutParams lpi = new LinearLayout.LayoutParams(
    				metrics.widthPixels/3, LinearLayout.LayoutParams.WRAP_CONTENT);
     				lpi.gravity = Gravity.RIGHT; //lpi.weight = 1;
   			 	cuadros.setOrientation(LinearLayout.VERTICAL); cuadros.addView(estado2,lpc);
   			 	estado2.setVisibility(View.GONE); estado2.setTextColor(Color.RED);
   			 	txtpass1.setText("Contrase�a: "); txtpass2.setText("Repita contrase�a: ");
    			linea1.setOrientation(LinearLayout.HORIZONTAL); linea2.setOrientation(LinearLayout.HORIZONTAL);
   			 	linea1.addView(txtpass1,lpd); linea1.addView(pass1,lpi); linea1.addView(seepass1);
   			 	linea2.addView(txtpass2,lpd); linea2.addView(pass2,lpi); linea2.addView(seepass2);
   			 	linea1.setPadding(10, 0, 10, 0); linea2.setPadding(10, 0, 10, 0);
   			 	cuadros.addView(linea1); cuadros.addView(linea2); builder.setView(cuadros);
   			builder.setPositiveButton("Continuar", null);
   			
   			final AlertDialog alert = builder.create();
   			 
   			alert.setOnShowListener(new DialogInterface.OnShowListener() {
   			    @Override public void onShow(DialogInterface dialog) {
   			    	final Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
   			        b.setOnClickListener(new View.OnClickListener() {
   			            @Override public void onClick(View view) {
		    					if(!pass1.getText().toString().equals(pass2.getText().toString())){
		    						estado2.setText("Compruebe que ambos cuadros contienen la misma contrase�a.");
		    						estado2.setVisibility(View.VISIBLE);
		    					}else if(nombre.getText().toString().equals(pass1.getText().toString())){
		    						estado2.setText("Compruebe que la contrase�a ya no es igual a su nombre de usuario.");
		    						estado2.setVisibility(View.VISIBLE);
		    					}else if (pass1.getText().toString().length()<6){
		    						estado2.setText(R.string.passlength);
		    						estado2.setVisibility(View.VISIBLE);
		    					}else{ alert.dismiss();
		    						pass.setText(pass1.getText().toString());
		    						new NewPassword().execute();
		    						((SharedPreferences.Editor)sharedPrefs.edit()).putString("nombre", nombre.getText().toString())
		    						.putString("pass", pass.getText().toString()).commit();
		    						if(!mensajes.isEmpty()){
		    			     			nmensajes= mensajes.size();
		    			    			 AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
		    			    			 builder.setTitle("Recuperar mensajes").setIcon(R.drawable.content_email).setCancelable(false);
		    			    			 builder.setMessage("Se han encontrado "+nmensajes+" mensajes de sus sesiones anteriores. �Desea recuperar estos mensajes?");
		    			    			 if(nmensajes>50){ 
		    			    				LayoutInflater factory = LayoutInflater.from(Registro.this);
		    			    				View content = factory.inflate(R.layout.snmensajes, null);
		    			    				builder.setView(content);
		    			    				snmensajes = (EditText)content.findViewById(R.id.snmensajes);
		    			    				ImageButton menos = (ImageButton)content.findViewById(R.id.menos);
		    			    				ImageButton mas = (ImageButton)content.findViewById(R.id.mas);
		    			    				menos.setOnClickListener(new OnClickListener(){
		    			    					@Override public void onClick(View v){ int sn;
		    			    					if(snmensajes.getText().toString().equals("")) sn=1;
		    			    					else sn= Integer.parseInt(snmensajes.getText().toString());
		    			       					if(sn>1){ if(sn>nmensajes) sn=nmensajes; sn--;}
		    			       					snmensajes.setText(""+sn);
		    			       				}});
		    			    				mas.setOnClickListener(new OnClickListener(){
		    			       				@Override public void onClick(View v){ int sn;
		    			       					if(snmensajes.getText().toString().equals("")) sn=0;
		    			       					else sn = Integer.parseInt(snmensajes.getText().toString());
		    			       					if(sn<nmensajes) sn++;
		    			       					else sn = nmensajes;
		    			       					snmensajes.setText(""+sn);
		    			       				}});
		    			       			/*if(!snmensajes.hasFocus())
		    			       				if(snmensajes.getText().toString().equals("") || 
		    			       						Integer.parseInt(snmensajes.getText().toString())<1) 
		    			       					snmensajes.setText("1");
		    			       				else if(Integer.parseInt(snmensajes.getText().toString())>nmensajes)
		    			       					snmensajes.setText(nmensajes);*/
		    			    			/*snmensajes.addTextChangedListener(new TextWatcher() {@Override public void 
		    			    				onTextChanged(CharSequence s, int start, int before, int count) {
		    			    					if (nombre.getText().toString().length()>0) clrnombre.setVisibility(View.VISIBLE);
		    			    				    else clrnombre.setVisibility(View.INVISIBLE);}
		    			    				@Override public void afterTextChanged(Editable arg0) {}
		    			    				@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		    			    			});*/
		    			       		 }
		    			    			 builder.setNegativeButton("Pasar", new DialogInterface.OnClickListener(){
		    			    				@Override public void onClick(DialogInterface d, int arg1) {d.dismiss();
		    			    					new descargaLogos().execute(); }});
		    			    			builder.setPositiveButton("Recuperar", new DialogInterface.OnClickListener(){
		    			    				@Override public void onClick(DialogInterface d, int arg1) {d.dismiss();
		    			    					if(nmensajes>50){
		    			    						if(snmensajes.getText().toString().equals("")) nmensajes=0;
		    			    						else if(Integer.parseInt(snmensajes.getText().toString())<nmensajes)
		    			    						nmensajes=Integer.parseInt(snmensajes.getText().toString());
		    			    					}
		    			    					for(int i=0;i<nmensajes;i++){
		    			    						if(mensajes.get(i).getTipomsj().equals("A"))
		    			    							db.almacenarMensajeEnviado(mensajes.get(i));
		    			    						else
		    			    							db.recibirMensaje(mensajes.get(i)); }
		    			    					new descargaLogos().execute(); }});
		    			    			 
		    			    			 final AlertDialog alert = builder.create();
		    			    			 alert.show();
		    			    		}else{ new descargaLogos().execute(); }
		    					}
   			    			}});
   			     pass2.setOnKeyListener(new View.OnKeyListener(){
   			      public boolean onKey(View v, int keyCode, KeyEvent event){
   			          if (event.getAction() == KeyEvent.ACTION_DOWN){
   			              switch (keyCode){
   			                  case KeyEvent.KEYCODE_DPAD_CENTER:
   			                  case KeyEvent.KEYCODE_ENTER:
   			                      b.performClick(); return true;
   			                  default: break;
   			              }
   			          }return false;
   			      }});
   			   }});   			
   			   alert.show();
     		}else{
     			((SharedPreferences.Editor)sharedPrefs.edit()).putString("nombre", nombre.getText().toString())
				.putString("pass", pass.getText().toString()).commit();
     			if(!mensajes.isEmpty()){
	     			nmensajes= mensajes.size();
	    			 AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
	    			 builder.setTitle("Recuperar mensajes").setIcon(R.drawable.content_email).setCancelable(false);
	    			 builder.setMessage("Se han encontrado "+nmensajes+" mensajes de sus sesiones anteriores. �Desea recuperar estos mensajes?");
	    			 if(nmensajes>50){ 
	    				LayoutInflater factory = LayoutInflater.from(Registro.this);
	    				View content = factory.inflate(R.layout.snmensajes, null);
	    				builder.setView(content);
	    				snmensajes = (EditText)content.findViewById(R.id.snmensajes);
	    				ImageButton menos = (ImageButton)content.findViewById(R.id.menos);
	    				ImageButton mas = (ImageButton)content.findViewById(R.id.mas);
	    				menos.setOnClickListener(new OnClickListener(){
	    					@Override public void onClick(View v){ int sn;
	    					if(snmensajes.getText().toString().equals("")) sn=1;
	    					else sn= Integer.parseInt(snmensajes.getText().toString());
	       					if(sn>1){ if(sn>nmensajes) sn=nmensajes; sn--;}
	       					snmensajes.setText(""+sn);
	       				}});
	    				mas.setOnClickListener(new OnClickListener(){
	       				@Override public void onClick(View v){ int sn;
	       					if(snmensajes.getText().toString().equals("")) sn=0;
	       					else sn = Integer.parseInt(snmensajes.getText().toString());
	       					if(sn<nmensajes) sn++;
	       					else sn = nmensajes;
	       					snmensajes.setText(""+sn);
	       				}});
	       			/*if(!snmensajes.hasFocus())
	       				if(snmensajes.getText().toString().equals("") || 
	       						Integer.parseInt(snmensajes.getText().toString())<1) 
	       					snmensajes.setText("1");
	       				else if(Integer.parseInt(snmensajes.getText().toString())>nmensajes)
	       					snmensajes.setText(nmensajes);*/
	    			/*snmensajes.addTextChangedListener(new TextWatcher() {@Override public void 
	    				onTextChanged(CharSequence s, int start, int before, int count) {
	    					if (nombre.getText().toString().length()>0) clrnombre.setVisibility(View.VISIBLE);
	    				    else clrnombre.setVisibility(View.INVISIBLE);}
	    				@Override public void afterTextChanged(Editable arg0) {}
	    				@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	    			});*/
	       		 }
	    			 builder.setNegativeButton("Pasar", new DialogInterface.OnClickListener(){
	    				@Override public void onClick(DialogInterface d, int arg1) {d.dismiss();
	    					new descargaLogos().execute(); }});
	    			builder.setPositiveButton("Recuperar", new DialogInterface.OnClickListener(){
	    				@Override public void onClick(DialogInterface d, int arg1) {d.dismiss();
	    					if(nmensajes>50){
	    						if(snmensajes.getText().toString().equals("")) nmensajes=0;
	    						else if(Integer.parseInt(snmensajes.getText().toString())<nmensajes)
	    						nmensajes=Integer.parseInt(snmensajes.getText().toString());
	    					}
	    					for(int i=0;i<nmensajes;i++){
	    						if(mensajes.get(i).getTipomsj().equals("A"))
	    							db.almacenarMensajeEnviado(mensajes.get(i));
	    						else
	    							db.recibirMensaje(mensajes.get(i)); }
	    					new descargaLogos().execute(); }});
	    			 
	    			 final AlertDialog alert = builder.create();
	    			 alert.show();
	    		}else{ new descargaLogos().execute(); }
     		}
     		
     	}else if(flagexiste==2){
     		if (nombre.getText().toString().equals(pass.getText().toString())){
     			estado.setText("El nombre de usuario y la contrase�a no pueden ser el mismo.");
     			estado.setVisibility(View.VISIBLE);
     		}else{
     		final Dialog d = new Dialog(Registro.this);
     					 			 d.setCancelable(false);
     					 			 d.setTitle(getString(R.string.registerMode));
     					 			 d.setContentView(R.layout.dmodoregistro);
     		((RadioGroup)d.findViewById(R.id.modoregistro))
     			.setOnCheckedChangeListener(new OnCheckedChangeListener() {
     			@Override public void onCheckedChanged(RadioGroup group, int checkedId) {
     				if(checkedId==R.id.auto)
     					((LinearLayout)d.findViewById(R.id.linearegcode)).setVisibility(View.VISIBLE);
     				else ((LinearLayout)d.findViewById(R.id.linearegcode)).setVisibility(View.GONE);
     			}});
     		((Button)d.findViewById(R.id.cancelar)).setOnClickListener(new OnClickListener(){
     			@Override public void onClick(View v){ d.dismiss(); }});
     		((Button)d.findViewById(R.id.aceptar)).setOnClickListener(new OnClickListener(){
     			@Override public void onClick(View v){ 
     				EditText regcode = ((EditText)d.findViewById(R.id.regcode));
     				if(((RadioGroup)d.findViewById(R.id.modoregistro)).getCheckedRadioButtonId()==R.id.auto 
     					&& !regcode.getText().toString().equals(""))
     					if(regcode.getText().toString().matches("^1[01]{1}[A-Ja-j]+\\d+[A-Ja-j]{2}$")){
     					String code1="", code2=""; est="";
     					for(int i=0;i<regcode.getText().toString().length()-2;i++){
     						if(Character.isLetter(regcode.getText().toString().charAt(i))){
     								code1+=charToNum(""+regcode.getText().toString().toUpperCase(spanish).charAt(i)); 
     								est+=charToNum(""+regcode.getText().toString().toUpperCase(spanish).charAt(i)); }
     						else if(Character.isDigit(regcode.getText().toString().charAt(i)))
     							code1+=Integer.parseInt(""+regcode.getText().toString().charAt(i));
     					}
     					code2=""+charToNum(regcode.getText().toString().toUpperCase(spanish).substring(
     							regcode.getText().toString().length()-2,regcode.getText().toString().length()-1))+
     							charToNum(regcode.getText().toString().toUpperCase(spanish).substring(
     							regcode.getText().toString().length()-1,regcode.getText().toString().length()));
     					ref=regcode.getText().toString().substring(est.length()+2,regcode.getText().toString().length()-2);
     					if(Integer.parseInt(code1)%13==Integer.parseInt(code2)){ 
     						if(regcode.getText().toString().substring(1,2).equals("0"))tipo="P";
     						else if(regcode.getText().toString().substring(1,2).equals("1")) tipo="E";
     						new ReplicaDatos().execute(); new descargaLogos().execute();
     						d.dismiss(); 
     					}else Toast.makeText(getBaseContext(),getString(R.string.registerWrongCode), Toast.LENGTH_LONG).show();
     				}else Toast.makeText(getBaseContext(),getString(R.string.registerWrongCode), Toast.LENGTH_LONG).show();
     				else if(((RadioGroup)d.findViewById(R.id.modoregistro)).getCheckedRadioButtonId()==R.id.auto 
     						&& ((EditText)d.findViewById(R.id.regcode)).getText().toString().equals(""))
     					Toast.makeText(getBaseContext(), getString(R.string.registerPutRegCode), Toast.LENGTH_LONG).show();
     				else if(((RadioGroup)d.findViewById(R.id.modoregistro)).getCheckedRadioButtonId()==R.id.manual){ 
     					d.dismiss();
     					Intent i = new Intent(Registro.this, Registro2.class);
     					i.putExtra("nombre", nombre.getText().toString());
     					i.putExtra("pass", pass.getText().toString());
     					i.putExtra("pais", pais.getSelectedItem().toString());
     					startActivity(i); finish(); }
     			}}); d.show();
     		}} else if (flagexiste==3){ //No est� en uso ?
				Intent i = new Intent(Registro.this, ListaEstablecimientos.class);
				if(db.getEstablecimientosCount()==0) i.putExtra("vienedereg",1); 
				startActivity(i); finish(); }
			}

    protected Boolean doInBackground(final String... args) {
      try{AccountManager manager = (AccountManager) getSystemService(Context.ACCOUNT_SERVICE);
  		Account[] list = manager.getAccounts();
  		for(Account account: list){
  		    if(account.type.equalsIgnoreCase("com.google")){
  		        gmail = account.name; break; }}
    	  try{Class.forName("org.postgresql.Driver");}
      catch(ClassNotFoundException e){e.printStackTrace();}
      try{DriverManager.setLoginTimeout(20);
      	Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM clienteglobal WHERE nombre='"+
				nombre.getText().toString()+"' AND pass='"+pass.getText().toString()+"'");
				if(rs.next()){
					//Sin user/pass
					((SharedPreferences.Editor)sharedPrefs.edit()).putString("cp", String.valueOf(rs.getInt(3)))
					.putString("tipo",rs.getString(4)).putString("cnae",rs.getString(5))
					.putInt("lastmid",rs.getInt(6)).putString("mail",rs.getString(7))
					.putString("tel",rs.getString(8)).putString("seudonimo",rs.getString(12))
					.putString("dir",rs.getString(13)).putString("pais",rs.getString(14))
					.putString("recpass",rs.getString(15)).putString("ps",rs.getString(16))
					.putString("rs",rs.getString(17)).putInt("id",rs.getInt(1))
					.putString("nemp",rs.getString(18)).putBoolean("bg", true)
					.putString("nombreprev", rs.getString(2)).putString("passprev", rs.getString(10))
					.putString("cpprev", String.valueOf(rs.getInt(3))).putString("moneda", "1")
					.putString("tono", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString())
					.putString("idfoto", "000")
					.putString("codigoseguro", rs.getString(19))
					.putString("fechaalta",rs.getString(20))
					.putString("horaalta", rs.getString(21)).commit();
					if(rs.getString(7)==null || rs.getString(7).equals(""))
						((SharedPreferences.Editor)sharedPrefs.edit()).putString("mail", gmail).commit();
					db.reinicializarCNAE(rs.getString(5));
					if(sharedPrefs.getString("tipo", "P").equals("P")){
					runOnUiThread(new Runnable(){public void run(){
						loading.setMessage(getString(R.string.ri));}});
						DriverManager.setLoginTimeout(20);
						Statement st3 = conn.createStatement();
						
						/*NOTA: Quito la descarga de Establecimientos por CP, dejo s�lo los que se han mensajeado)
						 ResultSet rs3 = st3.executeQuery("SELECT "+getString(R.string.camposest)+
								", textcat_all(cnae || ',') FROM app_company, categoriaempresa WHERE zonainfluencia LIKE '%"+
								sharedPrefs.getString("cp","")+"%' AND app_company.idcompanyapp=categoriaempresa.idcompanyapp " +
								"AND restringepedidos <> 'S' AND activo = true AND configura LIKE '%,MCS,%' " +
								"OR app_company.idcompanyapp IN (SELECT idestablecimiento FROM mensajeapp WHERE clienteglobal=" + sharedPrefs.getInt("id", 0)
								+ " GROUP BY idestablecimiento) GROUP BY "+getString(R.string.camposest));
						 */
						
						ResultSet rs3 = st3.executeQuery("SELECT "+getString(R.string.camposest)+
								", textcat_all(cnae || ',') FROM app_company, categoriaempresa WHERE " +
								"app_company.idcompanyapp=categoriaempresa.idcompanyapp AND app_company.idcompanyapp " +
								"IN (SELECT idestablecimiento FROM mensajeapp WHERE clienteglobal=" + sharedPrefs.getInt("id", 0)
								+ " GROUP BY idestablecimiento) GROUP BY "+getString(R.string.camposest));
						while(rs3.next()) {
							runOnUiThread(new Runnable(){public void run(){
								loading.setMessage(getString(R.string.ccp2));}});
							if(db.getEstablecimientosCount()==0){
								db.addEstablecimiento(new Est(rs3.getInt(1),rs3.getInt(2),rs3.getString(3),rs3.getString(4),
										rs3.getString(5),rs3.getString(6),"",rs3.getBoolean(7),true,
										0,0.0f,rs3.getString(8),rs3.getString(9),rs3.getString(10),rs3.getString(11),
										rs3.getString(12),rs3.getString(13),rs3.getString(14),rs3.getString(15),0,
										rs3.getString(16),rs3.getString(17),"",rs3.getString(18),rs3.getString(19),
										rs3.getString(20),rs3.getString(21),"0",rs3.getString(22),rs3.getString(23)));
								//Conservo la primera descarga como Principal
								sharedPrefs.edit().putString("main", ""+rs3.getInt(1)).commit();
							}else {
								int flag = 0;
								for(Est e : db.getAllEstablecimientos())
									if(e.getEid()==rs3.getInt(1)) flag=1;
								if(flag==0)
									db.addEstablecimiento(new Est(rs3.getInt(1),rs3.getInt(2),rs3.getString(3),rs3.getString(4),
											rs3.getString(5),rs3.getString(6),"",rs3.getBoolean(7),true,
											0,0.0f,rs3.getString(8),rs3.getString(9),rs3.getString(10),rs3.getString(11),
											rs3.getString(12),rs3.getString(13),rs3.getString(14),rs3.getString(15),0,
											rs3.getString(16),rs3.getString(17),"",rs3.getString(18),rs3.getString(19),
											rs3.getString(20),rs3.getString(21),"0",rs3.getString(22),rs3.getString(23)));
							}} rs3.close(); st3.close(); flagexiste=1;
					}else if(sharedPrefs.getString("tipo", "P").equals("E")){ flagexiste=1;
					//Incluir consulta de si este nombre est� en app_company, como usuarioapp, y luego hacer un split de ","
					try{Statement st4 = conn.createStatement();
					ResultSet rs4 = st4.executeQuery("SELECT "+getString(R.string.camposest)+
							", textcat_all(cnae || ',') FROM app_company, categoriaempresa WHERE usuarioapp LIKE '%"+
							nombre.getText().toString()+"%' AND app_company.idcompanyapp=categoriaempresa.idcompanyapp " +
							"OR app_company.idcompanyapp IN (SELECT idestablecimiento FROM mensajeapp WHERE clienteglobal=" + sharedPrefs.getInt("id", 0)
							+ " GROUP BY idestablecimiento) GROUP BY "+getString(R.string.camposest));
					while(rs4.next()) {
						int flag = 0; String uas[] = rs4.getString(18).split(",");
						for(final String s : uas){
							if(s.equals(nombre.getText().toString())){
								SharedPreferences.Editor spe = sharedPrefs.edit();
								spe.putInt("usuarioapp", rs4.getInt(1));
								for(Est e : db.getAllEstablecimientos()) if(e.getEid()==rs4.getInt(1)) flag=1;
								//Si no estaba, se a�ade.
								if(flag==0) db.addEstablecimiento(new Est(rs4.getInt(1),rs4.getInt(2),rs4.getString(3),rs4.getString(4),
										rs4.getString(5),rs4.getString(6),"",rs4.getBoolean(7),true,
										1,0.0f,rs4.getString(8),rs4.getString(9),rs4.getString(10),rs4.getString(11),
										rs4.getString(12),rs4.getString(13),rs4.getString(14),rs4.getString(15),0,
										rs4.getString(16),rs4.getString(17),"",rs4.getString(18),rs4.getString(19),
										rs4.getString(20),rs4.getString(21),"0",rs4.getString(22),rs4.getString(23)));
									spe.putString("main", String.valueOf(rs4.getInt(1))).commit();
					}}} rs4.close(); st4.close();
					}catch(Exception e){e.printStackTrace();}}
					//Descarga Establecimientos con conversaciones
					try{
						Est estab = new Est();
						Statement st5 = conn.createStatement();
						ResultSet rs5 = st5.executeQuery("SELECT DISTINCT ac.* FROM app_company AS ac "+
								"INNER JOIN mensajeapp AS ma ON ma.idestablecimiento = ac.idcompanyapp "+ 
								"WHERE ma.clienteglobal="+sharedPrefs.getInt("id",0)+" AND tipomensaje = 'A'");
					while(rs5.next()){ 
						if(db.getEstablecimiento(rs5.getInt(1))==null){
							estab = new Est(rs5.getInt(1),rs5.getInt(18),rs5.getString(23),
								rs5.getString(9),rs5.getString(8),rs5.getString(6),"",rs5.getBoolean(19),
								true,1,0.0f,rs5.getString(10),rs5.getString(11),rs5.getString(12),
								rs5.getString(13),rs5.getString(14),rs5.getString(15),rs5.getString(4),
								rs5.getString(2),0,rs5.getString(27),rs5.getString(7),"",rs5.getString(29),
								rs5.getString(30),rs5.getString(31),rs5.getString(33),"0",rs5.getString(35),"");
							db.addEstablecimiento(estab);
						//Coger CNAEs de este Establecimiento
						try{Statement st6 = conn.createStatement();
						ResultSet rs6 = st6.executeQuery("SELECT textcat_all(cnae || ',') FROM categoriaempresa " +
								"WHERE idcompanyapp="+rs5.getInt(1));
						if(rs6.next()){ estab.setCnae(rs6.getString(1));
								db.updateEstablecimiento(estab);
						}rs6.close(); st6.close();
					}catch(Exception e){e.printStackTrace();}
						}//Cierra if(db.getEstablecimiento(rs5.getInt(1))==null){
					}rs5.close(); st5.close();
					
					//Recuperar mensajes (Di�logo en el onPostExecute()
					Statement st0 = conn.createStatement();
		  			ResultSet rs0 = st0.executeQuery("SELECT *, "+
		  					"(SELECT MAX(id) FROM mensajeapp) AS lastmid "+
		  					"FROM mensajeapp WHERE tipomensaje='G' AND "+
		  					/*"zonainfluencia LIKE '%"+sharedPrefs.getString("cp","0")+"%' OR" +*/
		  					" clienteglobal="+sharedPrefs.getInt("id",0)+
		  					" AND tipomensaje<>'M' AND remitente<>'Sistema' ORDER BY id DESC;");
		  			int midrec=1, midenv=1;
		  			while(rs0.next()){
		  				if(sharedPrefs.getInt("lastmid",0)==0) spe.putInt("lastmid",rs0.getInt(19));
		  				if(rs0.getString(9).equals("A")){
		  					mensajes.add(new Msj(midenv, rs0.getInt(2), rs0.getString(3),
		  						rs0.getString(4), rs0.getString(5), rs0.getString(6), rs0.getString(7), 
		  						rs0.getInt(8), rs0.getString(9), rs0.getString(11), rs0.getString(12), 
		  						rs0.getString(13), rs0.getString(14), rs0.getString(15), rs0.getString(17),
		  						rs0.getInt(1), rs0.getString(19)));
		  					midenv++;
		  				}else{
		  					mensajes.add(new Msj(midrec, rs0.getInt(2), rs0.getString(3),
		  						rs0.getString(4), rs0.getString(5), rs0.getString(6), rs0.getString(7), 
		  						rs0.getInt(8), rs0.getString(9), rs0.getString(11), rs0.getString(12), 
		  						rs0.getString(13), rs0.getString(14), "L", rs0.getString(17),
		  						rs0.getInt(1), rs0.getString(19)));
		  					midrec++;
		  			}}
		  			rs0.close(); st0.close();
				}catch(Exception e){e.printStackTrace();}
				} else {
					runOnUiThread(new Runnable(){public void run(){
						loading.setMessage(getString(R.string.cd));}});
					Statement st2 = conn.createStatement();
					ResultSet rs2 = st2.executeQuery("SELECT COUNT(*) FROM clienteglobal " +
							"WHERE nombre='"+nombre.getText().toString()+"'");
					if(rs2.next()){ if(rs2.getInt(1)>0){
							runOnUiThread(new Runnable(){public void run(){
								estado.setText(getString(R.string.usernametaken1));
								estado.setVisibility(View.VISIBLE); }});
							loading.dismiss();
						} else{ flagexiste=2; } rs2.close(); st2.close(); }
				} rs.close(); st.close(); conn.close();} catch (SQLException e) {}
      return true;}catch (Exception e){ e.printStackTrace(); runOnUiThread(new Runnable(){public void run(){
      	estado.setText(getString(R.string.nointernet)); }}); return false;}
      }
    }
	
   /* public class descargaLogos extends AsyncTask<String, Void, Boolean> {//S�lo descarga de nuevos

      protected void onPostExecute(final Boolean success) {
       	Intent i = new Intent(Registro.this, ListaEstablecimientos.class);
  			startActivity(i); finish();}

  		@Override protected Boolean doInBackground(String... params) {
    	URL url; URLConnection conn;
      BufferedInputStream inStream; BufferedOutputStream outStream;
      File outFile; FileOutputStream fileStream;
      
      outFile = new File( File.separator + "data" + File.separator + "data" + 
      		File.separator + Registro.this.getPackageName() +
      		File.separator + "logos" + File.separator);
      if(!outFile.exists()){ outFile.mkdir();
      	try{new File (outFile+ File.separator+".nomedia").createNewFile();
      	}catch (IOException e){e.printStackTrace();} }
      for(Est es : db.getAllEstablecimientos()){
      	if(es.getLogo()==null) continue; 
      	else{ try{ url = new URL(getString(R.string.dirlogos)+es.getLogo());
        conn = url.openConnection(); conn.setUseCaches(false);
        inStream = new BufferedInputStream(conn.getInputStream());
        outFile = new File (outFile + File.separator + es.getLogo());
        fileStream = new FileOutputStream(outFile);
        outStream = new BufferedOutputStream(fileStream, 4096);
        byte[] data = new byte[4096]; int bytesRead = 0;
        while((bytesRead = inStream.read(data, 0, data.length)) >= 0)
            outStream.write(data, 0, bytesRead);
        outStream.close(); fileStream.close(); inStream.close(); }
    catch(MalformedURLException e){} catch(FileNotFoundException e){} catch(Exception e){} }}
      return true;}
    }*/

    private class descargaLogos extends AsyncTask<URL, Integer, Long> {//S�lo descarga de nuevos
    	ProgressDialog progress;
    	
    	protected void onPreExecute(){
    		progress = new ProgressDialog(Registro.this);
    		progress.setMessage(getString(R.string.registerDownloadingLogos));
    	    progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	    progress.setIndeterminate(false);
    	    progress.setMax(db.getEstablecimientosCount());
    	    progress.setCancelable(false);
    	    progress.show();
    	}
    	
        protected void onPostExecute(Long result) {
        	if(progress.isShowing()) progress.dismiss();
         	Intent i = new Intent(Registro.this, ListaEstablecimientos.class);
    			startActivity(i); finish();}
        
        protected void onProgressUpdate(Integer... progreso) {
            progress.setProgress(progreso[0]);}

    		@Override protected Long doInBackground(URL... urls) {
      	URL url; URLConnection conn; long totalsize=0; int i=0;
        BufferedInputStream inStream; BufferedOutputStream outStream;
        File outFile; FileOutputStream fileStream;
        outFile = new File( File.separator + "data" + File.separator + "data" + 
        		File.separator + Registro.this.getPackageName() +
        		File.separator + "logos" + File.separator);
        if(!outFile.exists()){ outFile.mkdir();
        	try{new File (outFile+ File.separator+".nomedia").createNewFile();
        	}catch (IOException e){e.printStackTrace();} }
        
        
        	for(Est es : db.getAllEstablecimientos()){ i++;
        		 if(es.getLogo()==null) continue; 
             	else{ try{ url = new URL(getString(R.string.dirlogos)+es.getLogo());
               conn = url.openConnection(); conn.setUseCaches(false);
               inStream = new BufferedInputStream(conn.getInputStream());
               File outFile2 = new File (outFile + File.separator + es.getLogo());
               fileStream = new FileOutputStream(outFile2);
               outStream = new BufferedOutputStream(fileStream, 4096);
               byte[] data = new byte[4096]; int bytesRead = 0;
               while((bytesRead = inStream.read(data, 0, data.length)) >= 0)
                   outStream.write(data, 0, bytesRead);
               outStream.close(); fileStream.close(); inStream.close();
                 totalsize += outFile2.length();
                 publishProgress((int) ((i / (float) db.getEstablecimientosCount()) * 100));}
               catch(Exception e){} }}
        	return totalsize; }
      }
    
	//Clase inicio
	public class Inicio extends AsyncTask<String, Void, Boolean> {
		ProgressDialog loading;
		protected void onPreExecute() {
     	loading = new ProgressDialog(Registro.this);
     	loading.setMessage(getString(R.string.inicioload));
     	loading.setCancelable(false); loading.show(); }

    protected void onPostExecute(final Boolean success) {
     	if (loading.isShowing()) {loading.dismiss();}
     	ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
			(Registro.this, android.R.layout.simple_spinner_item, db.pedirNombresPaises());
		spinnerArrayAdapter.setDropDownViewResource
			(android.R.layout.simple_spinner_dropdown_item);
		pais.setAdapter(spinnerArrayAdapter);
		for (int position = 0; position < spinnerArrayAdapter.getCount(); position++){
			if(spinnerArrayAdapter.getItem(position).equals("Espa�a")){
				pais.setSelection(position); break; }} }

		@Override protected Boolean doInBackground(String... params) {
			db.inicializarPaises(); db.inicializarCNAE(); 
			db.inicializarActividades(); db.inicializarActividadesTipo(); 
			return true; }
	}
	
	//Registro Autom�tico
	private class ReplicaDatos extends AsyncTask<String, Void, Boolean> {
  	ProgressDialog loading; 
	protected void onPreExecute() {
     	loading = new ProgressDialog(Registro.this);
     	loading.setMessage(getString(R.string.registerSavingData));
     	loading.setCancelable(false); loading.show();
     	AccountManager manager = (AccountManager) getSystemService(Context.ACCOUNT_SERVICE);
  		Account[] list = manager.getAccounts();
  		for(Account account: list){
  		    if(account.type.equalsIgnoreCase("com.google")){
  		       email = account.name; break; }}}

    protected void onPostExecute(final Boolean success) {
     	if (loading.isShowing()) {loading.dismiss();}
     		Intent i = new Intent(Registro.this, ListaCompra.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i); finish(); 
			}
    
  	@Override protected Boolean doInBackground(String... arg0) {
  		//Descarga establecimiento
  		int ncliente=0; //XXX nclientes?
  		try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
  		try{DriverManager.setLoginTimeout(20);
  			Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
  			Statement st = conn.createStatement();
  			ResultSet rs = st.executeQuery("SELECT "+getString(R.string.camposest)+
  					", textcat_all(cnae || ','), nclientes FROM app_company, categoriaempresa WHERE app_company.idcompanyapp=" +
  					est+" AND app_company.idcompanyapp=categoriaempresa.idcompanyapp " +
  					"AND restringepedidos <> 'S' AND activo = true AND configura LIKE '%,MCS,%' GROUP BY "+getString(R.string.camposest)+", nclientes"); 
  			while(rs.next()){
  /*?*/				db.addEstablecimiento(new Est(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),
						rs.getString(5),rs.getString(6),"",rs.getBoolean(7),true,
						1,0.0f,rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),
						rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),0,
						rs.getString(16),rs.getString(17),"",rs.getString(18),rs.getString(19),
						rs.getString(20),rs.getString(21),ref,rs.getString(22),rs.getString(23)));
  					ncliente=rs.getInt(23);
  			} rs.close(); st.close(); conn.close(); } catch (SQLException e) {e.printStackTrace();}
  		//Replica datos
  		try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
			try{DriverManager.setLoginTimeout(20);
				//Genero c�digo seguro
				Random r = new Random();
				String codigoseguro="";
				for(int i=0;i<10;i++){
					if(i%2==0) codigoseguro+=(char)(r.nextInt(123-97)+97);
					else codigoseguro+=(r.nextInt(10-1)+1); }
			
				spe = sharedPrefs.edit();
				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
			//Aprovecho conexi�n para pedir n� lastmid
  			
  			Statement st0 = conn.createStatement();
  			ResultSet rs0 = st0.executeQuery("SELECT MAX(id) FROM mensajeapp");
  			if(rs0.next()) spe.putInt("lastmid",rs0.getInt(1));
  			rs0.close(); st0.close();
				
  			//Continuo con la replicaci�n
  			spe.putString("nombre", nombre.getText().toString())
	  			.putString("pass", pass.getText().toString()).putString("cp", "0")
	  			.putString("nemp", "").putString("recpass", "email")
	  			.putString("seudonimo", nombre.getText().toString())
	  			.putString("tel", "").putString("dir", "").putString("tipo", tipo)
	  			.putBoolean("ms", false).putString("moneda","1").putString("mail", email)
	  			.putString("pais", pais.getSelectedItem().toString())
	  			.putString("codsuc", "0").putString("cpprev", "0")
	  			.putString("nombreprev", nombre.getText().toString())
	  			.putString("passprev", pass.getText().toString())
	  			.putString("vcdate", new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()))
	  			.putString("tono", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString())
	  			.putString("main", est).putBoolean("bg", true)
	  			.putString("codigoseguro",codigoseguro)
	  			.putString("fechaalta",sdfdia.format(new Date()))
	  			.putString("horaalta",sdfhora.format(new Date())).commit();
				Statement st = conn.createStatement();
				st.executeUpdate("INSERT INTO clienteglobal (nombre,codigopostal,tipocliente,"+
						"mensajesadmitidos,ultimomensaje,mail,telefono,pass,seudonimo,direccion," +
						"pais,recpass,pregunta,respuesta,nombreempresa) VALUES ('"+
						sharedPrefs.getString("nombre","")+"',"+sharedPrefs.getString("cp","0")+",'"+
						sharedPrefs.getString("tipo","")+"','"+db.volcarCNAEtoString()+"',"+
						sharedPrefs.getInt("lastmid",0)+",'"+sharedPrefs.getString("mail","")+"','"+
						sharedPrefs.getString("tel","")+"','"+sharedPrefs.getString("pass","")+"','"+
						sharedPrefs.getString("seudonimo","")+"','"+sharedPrefs.getString("dir","")+"','"+
						sharedPrefs.getString("pais","Espa�a")+"','"+sharedPrefs.getString("recpass","")+"','"+
						sharedPrefs.getString("ps","") +"','"+sharedPrefs.getString("rs","")+"','"+
						sharedPrefs.getString("nemp","")+"')");
  				st.close(); conn.close(); }catch (SQLException e){e.printStackTrace();}
			try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
			try{DriverManager.setLoginTimeout(20);
			Connection conn2 = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st2 = conn2.createStatement();
			ResultSet rs2 = st2.executeQuery("SELECT id FROM clienteglobal " +
				"ORDER BY id DESC LIMIT 1");
			if(rs2.next()) { spe.putInt("id", rs2.getInt(1)); spe.commit(); }
			rs2.close(); st2.close();
		//Mensaje autom�tico
			if(!est.equals("")){
				String cuerpo = getString(R.string.nuevocliente1)+" "+sharedPrefs.getString("nombre","")
						+" "+getString(R.string.nuevocliente2);
			Connection conn3 = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st5 = conn3.createStatement();
				st5.executeUpdate("INSERT INTO mensajeapp (clienteglobal,mensaje,http,fecharealizacion,horarealizacion," +
					"idestablecimiento,tipomensaje,estado,remitente) VALUES " +
					"("+sharedPrefs.getInt("id",0)+",'"+cuerpo+"','["+ref+"]','"+
					new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date())+"','"+
					new SimpleDateFormat("HH:mm:ss",spanish).format(new Date())+"',"+
					est+",'A','E','Sistema')");
  			st5.close(); conn3.close();
  			new aumentaEst(Integer.parseInt(est)).execute();
			}}catch (SQLException e){e.printStackTrace();
			Toast.makeText(getBaseContext(),"Error de SQL, int�ntelo de nuevo...",Toast.LENGTH_LONG).show();
			Intent i = new Intent(Registro.this, Registro.class); startActivity(i); finish();}
  return true; }}
	
	//Parche cambio contrase�a
	private class NewPassword extends AsyncTask<String, Void, Boolean> {
		int result=0;

	    protected void onPostExecute(final Boolean success) {
	//    	if(success) //new Login().execute();
//	    		spe.putString("pass", pass.getText().toString()).commit();
	    	}
	    
	  	@Override protected Boolean doInBackground(String... arg0) {
	  		try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
		    try{DriverManager.setLoginTimeout(20);
				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st = conn.createStatement(); 
				result = st.executeUpdate("UPDATE clienteglobal SET pass='" + pass.getText().toString() +
						"' WHERE nombre='"+ nombre.getText().toString()+"'");
				st.close(); conn.close(); } catch (SQLException e) {e.printStackTrace();}
		    if(result>0) return true;
		    else return false; }}
	
	public static int charToNum(String s){ 
			if(s.charAt(0)=='J') return 0; 
			return ((int)s.charAt(0)-64);
	}
	
	/*public String numToChar(int n){
		String cadena="",numero=String.valueOf(n);
		for(int i=0;i<numero.length();i++){
			if(numero.charAt(i)=='0') cadena+="J"; else
			cadena+=String.valueOf((char)((int)numero.charAt(i)+16));
		} return cadena;
	}*/
	
	public class aumentaEst extends AsyncTask<String, Void, Boolean> {
  	int eid; public aumentaEst(int eid) { this.eid=eid; }
		@Override protected Boolean doInBackground(String... params) {
			//Aumentar contador de clientes de cada Establecimiento
			try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
	    try{DriverManager.setLoginTimeout(20);
			Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st = conn.createStatement(); 
			st.executeUpdate("UPDATE app_company SET nclientes=nclientes+1 " +
					"WHERE idcompanyapp="+ eid);
			st.close(); conn.close(); } catch (SQLException e) {e.printStackTrace();}
		return true;
		}}
}