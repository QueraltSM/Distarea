package com.disoft.distarea;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;*/
import android.support.v7.app.ActionBar;
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.Est;
import com.disoft.distarea.models.Ped;

public class Establecimiento extends AppCompatActivity {
	Locale spanish = new Locale("es", "ES");
	View v, popupView; PopupWindow popupWindow;
	Menu menu; MenuItem fav; WebView wv;
	SharedPreferences sharedPrefs; DatabaseHandler db;
	TextView nombre, tel, tv, mailmsj, cp, dir; ImageView icono;
	ImageButton call, send, contact, gps, mensaje, priormsj, pedido, descarga, intToText, textToInt;
	RatingBar valoracion; Est e; int flagprior; EditText referencia;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 ActionBar ab = getSupportActionBar();
	 ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
	 ab.setTitle(getString(R.string.establecimientos));
	 ab.setSubtitle(getIntent().getStringExtra("nombre"));
	 ab.setIcon(R.drawable.social_person); setContentView(R.layout.establecimiento);
	 v = findViewById(R.id.encabezado);
	 sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	 db = new DatabaseHandler(this);
	 e = db.getEstablecimiento(getIntent().getIntExtra("eid",0));
	 flagprior = e.getPrior();
	 valoracion = (RatingBar) findViewById(R.id.valoracion);
	 referencia = (EditText) findViewById(R.id.referencia);
	 intToText = (ImageButton) findViewById(R.id.intToText);
	 textToInt = (ImageButton) findViewById(R.id.textToInt);
	 nombre = (TextView) findViewById(R.id.nombre); icono = (ImageView) findViewById(R.id.icon);
	 tel = (TextView) findViewById(R.id.tel); tv = (TextView) findViewById(R.id.tv);
	 cp = (TextView) findViewById(R.id.cp); dir = (TextView) findViewById(R.id.dir); 
	 mailmsj = (TextView) findViewById(R.id.mailmsj); call = (ImageButton) findViewById(R.id.call);
	 call.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	 send = (ImageButton) findViewById(R.id.send);
	 send.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	 contact = (ImageButton) findViewById(R.id.contact);
	 contact.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	 gps = (ImageButton) findViewById(R.id.gps);
	 gps.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	 mensaje = (ImageButton)findViewById(R.id.mensaje);
	 mensaje.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	 priormsj = (ImageButton)findViewById(R.id.priormsj);
	 priormsj.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	 pedido = (ImageButton)findViewById(R.id.pedido);
	 pedido.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	 descarga = (ImageButton)findViewById(R.id.descarga);
	 descarga.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	 nombre.setText(e.getNombre()); nombre.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	 nombre.setOnClickListener(new OnClickListener(){ @Override public void onClick(View v) {
		  	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		  	Drawable resIcon = getResources().getDrawable(R.drawable.rating_not_important);
			  boolean estadofav;
		    if(fav.getIcon().getConstantState()==resIcon.getConstantState()) estadofav=false;
		    else estadofav=true;
		    if(e.getFav()!=estadofav || e.getVal()!=valoracion.getRating() || e.getPrior()!=flagprior){
		    	e.setFav(estadofav); e.setPrior(flagprior); e.setVal(valoracion.getRating());
		     	db.updateEstablecimiento(e); }
		  	Intent intent = new Intent(Establecimiento.this, ListaCompra.class);
		  	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		  	intent.putExtra("eid", e.getEid()); startActivity(intent); }});
	 wv = (WebView) findViewById(R.id.wv);
	 if(e.getLogo()!=null){
		 File f = new File( File.separator + "data" + File.separator + "data" + 
	   		File.separator + Establecimiento.this.getPackageName() + 
	   		File.separator + "logos" + File.separator + e.getLogo());
		 if(f.exists()) icono.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
	 }
	 icono.setOnLongClickListener(new OnLongClickListener(){
		@Override public boolean onLongClick(View v) {
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			if(!isNetworkAvailable()){
   				Toast.makeText(getApplicationContext(), getString(R.string.nointernet), 
   						Toast.LENGTH_LONG).show();
   				return false; } else {
	       new RefrescaEstablecimiento().execute();
	       /*try {Thread.sleep(1000);} catch (Exception e) {}
	       Intent i = new Intent(Establecimiento.this, Establecimiento.class);
	       i.putExtra("eid",e.getEid()); startActivity(i); finish();*/
   		}return false;
	 }});
	 /*else icono.setImageDrawable(getResources().getDrawable(R.drawable.tienda)); }
	 else icono.setImageDrawable(getResources().getDrawable(R.drawable.tienda));*/
	 if(e.getCvisual()!=null)
		 wv.loadDataWithBaseURL("", e.getCvisual(), "text/html", "UTF-8", "");
	 if(e.getFav()==false){ mensaje.setEnabled(false); pedido.setEnabled(false); }
	 if(e.getMail()==null || e.getMail().equals("")){ 
		 send.setEnabled(false); mailmsj.setVisibility(View.GONE); }
	 else{ //mailmsj.setText(mailmsj.getText()+" "+e.getMail());
		 SpannableString ssmail = new SpannableString(mailmsj.getText()+" "+e.getMail());
		 mailmsj.setClickable(true); mailmsj.setFocusable(true); mailmsj.setFocusableInTouchMode(true);
		 Linkify.addLinks(ssmail, Linkify.EMAIL_ADDRESSES); mailmsj.setText(ssmail);
		 mailmsj.requestFocus();
	 	send.setEnabled(true); mailmsj.setVisibility(View.VISIBLE); }
	 if(e.getGps()==null || e.getGps().equals("")){ gps.setEnabled(false);
	 		dir.setVisibility(View.GONE); }
	 else{
		 
		 
		 SpannableString ssdir = new SpannableString(dir.getText()+" "+e.getGps());
		 ssdir.setSpan(new UnderlineSpan(), getResources().getString(R.string.dir).length()+1, ssdir.length(), 0);
		 ssdir.setSpan(new ForegroundColorSpan(0x990000FF), getResources().getString(R.string.dir).length()+1,
				 ssdir.length(), 0);
		 
		 dir.setText(ssdir); dir.setClickable(true);
		 dir.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v) {
				Intent searchAddress = new Intent(Intent.ACTION_VIEW, 
						Uri.parse("geo:0,0?q="+e.getGps()+"("+e.getNombre()+")")); 
				startActivity(searchAddress); 
		 }});
	 }
	 if(e.getTel()==null || e.getTel().equals("")) { tel.setVisibility(View.GONE); 
	  	call.setEnabled(false); contact.setEnabled(false); }
	 else{ SpannableString sstel = new SpannableString(tel.getText()+" "+e.getTel());
	 	 tel.setClickable(true); tel.setFocusable(true); tel.setFocusableInTouchMode(true);
		 Linkify.addLinks(sstel, Linkify.PHONE_NUMBERS); tel.setText(sstel);
		 tel.requestFocus();
	 }
	 if(e.getTv()==null || e.getTv().equals("")){ tv.setVisibility(View.GONE);
	 	descarga.setEnabled(false); }
	 else{ tv.setText(tv.getText()+" "+e.getTv());
	 	descarga.setOnClickListener(new OnClickListener(){ @Override public void onClick(View v) {
	 		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	 		if(isNetworkAvailable()){
	 		AlertDialog.Builder b = new AlertDialog.Builder(Establecimiento.this);
			b.setTitle("Descargar Art\u00EDculos");
			b.setMessage("Est\u00E1 a punto de descargar todos los art\u00EDculos disponibles de esta Tienda Virtual." +
					" Una vez descargados, podr\u00E1 realizar pedidos desde el m\u00F3vil sin necesidad de conectar con" +
					" la tienda, salvo en el momento de enviar el pedido. El proceso de descarga puede llegar a tardar" +
					" unos minutos, dependiendo de la velocidad de su conexi�n, y la cantidad de art�culos disponibles." +
					" ¿Est\u00E1 seguro de querer continuar?");
			b.setIcon(R.drawable.av_download);
			LinearLayout checkboxes = new LinearLayout(getBaseContext());
			final CheckBox cbimagenes = new CheckBox(getBaseContext());
			checkboxes.setOrientation(LinearLayout.VERTICAL);
			cbimagenes.setText("Descargar tambi\u00E9n im\u00E1genes de los art\u00E9culos (tardar\u00E1 a\u00F3n m\u00E1s).");
			cbimagenes.setTextColor(getResources().getColor(android.R.color.black));
			if(sharedPrefs.getBoolean("vi",true)) cbimagenes.setChecked(true);
			else cbimagenes.setChecked(false);
			checkboxes.addView(cbimagenes);
			final CheckBox cblimpiararticulos = new CheckBox(getBaseContext());
			if(db.getArticuloEstablecimientoCount(e.getEid())>0){
				cblimpiararticulos.setText("Borrar todos los art\u00E9culos antes.");
				cblimpiararticulos.setOnClickListener(new OnClickListener(){ @Override public void onClick(View v){
					if(cblimpiararticulos.isChecked()){
						if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
						AlertDialog.Builder b = new AlertDialog.Builder(Establecimiento.this);
						b.setTitle("Borrar art�culos").setIcon(getResources().getDrawable(R.drawable.content_discard))
						.setMessage("Se borrar\u00E1n TODOS los art\u00E9culos previamente descargados de este establecimiento." +
								" Eso incluye el historial de pedidos anteriores, y sus favoritos. Por otro lado, si" +
								" hubieran art�culos descatalogados, estos dejar�n de aparecer.")
						.setPositiveButton("Entendido", new DialogInterface.OnClickListener(){ 
							@Override public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}});
						b.create().show();
					}
				}});
				cblimpiararticulos.setTextColor(getResources().getColor(android.R.color.black));
				checkboxes.addView(cblimpiararticulos);
			}
			b.setPositiveButton("Descargar art\u00E9culos", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					dialog.dismiss(); 
					if(cblimpiararticulos.isChecked()){
						new borrarArticulos(cbimagenes).execute();
					}else{
						Intent intent = new Intent(Establecimiento.this, TiendaVirtual.class);
				    	intent.putExtra("eid", e.getEid());
				    	intent.putExtra("intent", "establecimiento");
				    	intent.putExtra("funcion", "descarga");
				    	intent.putExtra("imagenes", cbimagenes.isChecked());
				    	intent.putExtra("borrados", false);
				    	Toast.makeText(getBaseContext(), R.string.dltoast ,Toast.LENGTH_LONG).show();
				    	startActivity(intent);
					}
					}});
			b.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){dialog.dismiss();}});
			b.setView(checkboxes);
			AlertDialog ad = b.create(); ad.show();
	 		}else Toast.makeText(getBaseContext(), "No se ha detectado conexi�n estable a Internet. Revise su conexi�n,  e int�ntelo de nuevo...",
	 				Toast.LENGTH_LONG).show();
	 		 }});
	 }
		 if(e.getPrior()>0){ //XXX Iniciales
			 priormsj.setOnClickListener(popupAd); 
			 priormsj.setBackgroundDrawable(getResources().getDrawable(R.drawable.botondisoft));
			 priormsj.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
		 }else{if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		 	 priormsj.setBackgroundDrawable(getResources()
		 			 .getDrawable(R.drawable.botondisoft_falsodesactivado));
		 	 priormsj.setOnClickListener(activaPrior);}
	 //}
	 if(!isNetworkAvailable()){ send.setEnabled(false); gps.setEnabled(false);
	 	send.getBackground().setColorFilter(0xFFFF0000,PorterDuff.Mode.SRC_IN);
	 	gps.getBackground().setColorFilter(0xFFFF0000,PorterDuff.Mode.SRC_IN);}
	 cp.setText(cp.getText()+" "+e.getZi()); 
	 call.setOnLongClickListener(new OnLongClickListener(){public boolean onLongClick(View v){
		 if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		 Toast.makeText(getBaseContext(), getString(R.string.establishmentCall), Toast.LENGTH_LONG).show();
		 return true; }});
	 send.setOnLongClickListener(new OnLongClickListener(){public boolean onLongClick(View v){
		 if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		 Toast.makeText(getBaseContext(), getString(R.string.establishmentSendEmail), Toast.LENGTH_LONG).show();
		 return true; }});
	 contact.setOnLongClickListener(new OnLongClickListener(){public boolean onLongClick(View v){
		 if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		 Toast.makeText(getBaseContext(), getString(R.string.establishmentAddToPhonebook), Toast.LENGTH_LONG).show();
		 return true; }});
	 mensaje.setOnLongClickListener(new OnLongClickListener(){public boolean onLongClick(View v){
		 if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		 Toast.makeText(getBaseContext(), getString(R.string.establishmentSendMessageViaDistarea), Toast.LENGTH_LONG).show();
		 return true; }});
	 gps.setOnLongClickListener(new OnLongClickListener(){public boolean onLongClick(View v){
		 if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		 Toast.makeText(getBaseContext(), getString(R.string.establishmentLocateStoreMap), Toast.LENGTH_LONG).show();
		 return true; }});
	 priormsj.setOnLongClickListener(new OnLongClickListener(){public boolean onLongClick(View v){
		 if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		 if(flagprior==0)
			 Toast.makeText(getBaseContext(), getString(R.string.establishmentBookmark), Toast.LENGTH_LONG).show();
		 else if(flagprior>0)
			 Toast.makeText(getBaseContext(), getString(R.string.establishmentSeePriorityMessage), Toast.LENGTH_LONG).show();
		 return true; }});
	 pedido.setOnLongClickListener(new OnLongClickListener(){public boolean onLongClick(View v){
		 if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		 Toast.makeText(getBaseContext(), getString(R.string.establishmentCreateView), Toast.LENGTH_LONG).show();
		 return true; }});
	 descarga.setOnLongClickListener(new OnLongClickListener(){public boolean onLongClick(View v){
		 if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		 Toast.makeText(getBaseContext(), getString(R.string.establishmentDownloadVirtualStore), Toast.LENGTH_LONG).show();
		 return true; }});
	 
	 call.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
	 	 if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	 	 Intent i = new Intent(Intent.ACTION_CALL); i.setData(Uri.parse("tel:"+e.getTel()));
	  	 startActivity(i); }});	 
	 contact.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
		 if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		 LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
		 popupView = layoutInflater.inflate(R.layout.popupsino, null);  
		 popupWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 
			android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
		 popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
		 popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
		 if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			{Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
			if(display.getRotation()==Surface.ROTATION_90)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else if(display.getRotation()==Surface.ROTATION_270){ 
				if(android.os.Build.VERSION.SDK_INT == 8)
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
		 }else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		 ((TextView) popupView.findViewById(R.id.texto)).setText(getString(R.string.textoaddcontacto));
		 ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
		 si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
		 ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
		 no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
		 si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
				addContact(); popupWindow.dismiss();
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				Toast.makeText(getBaseContext(), getString(R.string.contactoagregado), Toast.LENGTH_SHORT).show();
		 }});
		 no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
				popupWindow.dismiss();
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				Toast.makeText(getBaseContext(), getString(R.string.contactonoagregado), Toast.LENGTH_SHORT).show();
		 }});
		}});
   send.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
   	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
   	Intent intent = new Intent(Intent.ACTION_SENDTO,
		Uri.fromParts("mailto",e.getMail(), null));
    intent.putExtra(Intent.EXTRA_EMAIL, e.getMail()); startActivity(intent); }});
   gps.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
   	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    	Intent intent = new Intent(Establecimiento.this, Geolocalizacion.class);
    	intent.putExtra("nombre",e.getNombre());
    	intent.putExtra("gps",e.getGps()); startActivity(intent); }});
   mensaje.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
    if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    	Intent intent = new Intent(Establecimiento.this, Chat.class);
    	/* Mensajes -> Chat
    	Intent intent = new Intent(Establecimiento.this, Mensajes.class);
    	intent.putExtra("tipo",2);*/ 
    	intent.putExtra("eid", e.getEid()); 
    	startActivity(intent); }});
   pedido.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
  	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
  	Drawable resIcon = getResources().getDrawable(R.drawable.rating_not_important);
	  boolean estadofav;
    if(fav.getIcon().getConstantState()==resIcon.getConstantState()) estadofav=false;
    else estadofav=true;
    if(e.getFav()!=estadofav || e.getVal()!=valoracion.getRating() || e.getPrior()!=flagprior){
    	e.setFav(estadofav); e.setPrior(flagprior); e.setVal(valoracion.getRating());
     	db.updateEstablecimiento(e); }
  	Intent intent = new Intent(Establecimiento.this, ListaCompra.class);
  	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
  	intent.putExtra("eid", e.getEid()); startActivity(intent); }});
   	//
   	/*referencia.setOnClickListener(new OnClickListener(){
   		@Override public void onClick(View v) {
   			if(referencia.getInputType()==InputType.TYPE_CLASS_NUMBER){
				intToText.setVisibility(View.VISIBLE);
				textToInt.setVisibility(View.GONE);
			}else if(referencia.getInputType()==InputType.TYPE_CLASS_TEXT){
				textToInt.setVisibility(View.VISIBLE);
				intToText.setVisibility(View.GONE);
			}
	}});*/
   	textToInt.setOnClickListener(new OnClickListener(){
		@Override public void onClick(View v) {
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			referencia.setInputType(InputType.TYPE_CLASS_NUMBER);
			textToInt.setVisibility(View.GONE);
			intToText.setVisibility(View.VISIBLE);
	}});
   	intToText.setOnClickListener(new OnClickListener(){
		@Override public void onClick(View v) {
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			referencia.setInputType(InputType.TYPE_CLASS_TEXT);
			textToInt.setVisibility(View.VISIBLE);
			intToText.setVisibility(View.GONE);
	}});
   	referencia.setOnFocusChangeListener(new OnFocusChangeListener(){
   		@Override public void onFocusChange(View arg0, boolean focus) {
   			if(!focus){
   				textToInt.setVisibility(View.GONE);
   				intToText.setVisibility(View.GONE);
   				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   				imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0);
   			}else{
   				if(referencia.getInputType()==InputType.TYPE_CLASS_NUMBER){
   					intToText.setVisibility(View.VISIBLE);
   					textToInt.setVisibility(View.GONE);
   				}else if(referencia.getInputType()==InputType.TYPE_CLASS_TEXT){
   					textToInt.setVisibility(View.VISIBLE);
   					intToText.setVisibility(View.GONE);
   				}
   			}
	}});
   	if(e.getReferencia()== null || e.getReferencia()=="") e.setReferencia("0");
   	if(!e.getReferencia().equals("0")) referencia.setText(e.getReferencia());
	 valoracion.setStepSize(1); valoracion.setRating(e.getVal());
	 
	//PIE
	RelativeLayout pie = (RelativeLayout) findViewById(R.id.pie);
	pie.setClickable(true);
	pie.setOnClickListener(new OnClickListener(){
		@Override public void onClick(View v){
			AlertDialog.Builder adb = new AlertDialog.Builder(Establecimiento.this);
			View layley = getLayoutInflater().inflate(R.layout.dleyendaest, null); 
			adb.setView(layley);
			if(e.getTv()==null || e.getTv().equals(""))
				((LinearLayout)layley.findViewById(R.id.leyest_descargaproductos)).setVisibility(View.GONE);
			else
				((LinearLayout)layley.findViewById(R.id.leyest_descargaproductos)).setVisibility(View.VISIBLE);
			final AlertDialog d = adb.create();
			layley.setOnClickListener(new OnClickListener(){
				@Override public void onClick(View v){ d.dismiss(); }});
			d.show();
		}});
	
	 
	}
		 
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if(e.getFav()==true){
			inflater.inflate(R.menu.estrellita2, menu);
		 	if(e.getPrior()>0)
		 		menu.findItem(R.id.prioritario).setIcon(R.drawable.rating_favorite);
		 	else menu.findItem(R.id.prioritario).setIcon(R.drawable.rating_favorite_hollow);}
		else inflater.inflate(R.menu.estrellita, menu);
		fav = menu.findItem(R.id.favorito); this.menu = menu; return true; }

	@Override public boolean onOptionsItemSelected(MenuItem item) {
	 	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	  Drawable resIcon = getResources().getDrawable(R.drawable.rating_not_important);
	  Drawable resIcon2 = getResources().getDrawable(R.drawable.rating_important);
	  if (item.getItemId() == android.R.id.home) {
	  //Recoger si se ha valorado, y si es ahora favorito.
	    boolean estadofav;
    	if(fav.getIcon().getConstantState()==resIcon.getConstantState()) estadofav=false;
    	else estadofav=true;
	    if(e.getFav()!=estadofav || e.getVal()!=valoracion.getRating() ||
	    		e.getPrior()!=flagprior || (!referencia.getText().toString().equals("") 
	    		&& !e.getReferencia().equals(referencia.getText().toString()))){
	      e.setFav(estadofav); e.setPrior(flagprior); e.setVal(valoracion.getRating());
	      if(!referencia.getText().toString().equals(""))
	    	  e.setReferencia(referencia.getText().toString());
	      db.updateEstablecimiento(e); }
	    Intent i; 
	    if(getIntent().getIntExtra("tipo",2)==2)
	    	i = new Intent(this, ListaEstablecimientos.class);
	    else if(getIntent().getIntExtra("tipo",2)==3){
	    	i = new Intent(this, ListaCompra.class);
	    	i.putExtra("eid", getIntent().getIntExtra("eid",0));
	    }else{ i = new Intent(this, Mensaje.class);
	    	i.putExtra("tipo", getIntent().getIntExtra("tipo",2));
	    	i.putExtra("mid", getIntent().getIntExtra("mid",0));} 
	    	startActivity(i); finish(); return true; }
	    if (item.getItemId() == R.id.favorito) {
	      if(sharedPrefs.getBoolean("ch",true))v.performHapticFeedback(1);
	      MenuInflater inflater = getMenuInflater();
	      if(fav.getIcon().getConstantState()==resIcon2.getConstantState()){
	      	menu.clear(); inflater.inflate(R.menu.estrellita, menu);
	        fav = menu.findItem(R.id.favorito); mensaje.setEnabled(false);
	        pedido.setEnabled(false); e.setFav(false); e.setPrior(0);
	        e.setVal(valoracion.getRating()); priormsj.setEnabled(false);
	        db.updateEstablecimiento(e);}
	      else{
	        menu.clear(); inflater.inflate(R.menu.estrellita2, menu);
	        fav = menu.findItem(R.id.favorito); e.setFav(true); mensaje.setEnabled(true);
	        pedido.setEnabled(true); priormsj.setEnabled(true); e.setPrior(0);
	        menu.findItem(R.id.prioritario).setIcon(R.drawable.rating_favorite_hollow);
	        e.setVal(valoracion.getRating()); db.updateEstablecimiento(e);
	     	 		priormsj.setBackgroundDrawable(getResources()
	        		.getDrawable(R.drawable.botondisoft_falsodesactivado));
	     	 		priormsj.setOnClickListener(activaPrior);}
	      return true; }
	    if (item.getItemId() == R.id.prioritario) { //XXX Bot�n prioritario
	       if(e.getPrior()>0){
	      	 e.setPrior(0); e.setVal(valoracion.getRating());
	      	 menu.findItem(R.id.prioritario).setIcon(R.drawable.rating_favorite_hollow);
	      	 flagprior=0;
	      		 	priormsj.setBackgroundDrawable(getResources()
	 	        		.getDrawable(R.drawable.botondisoft_falsodesactivado));
	      		 	priormsj.setOnClickListener(activaPrior);
	       }else{
	      	 e.setPrior(1); e.setVal(valoracion.getRating());
	      	 menu.findItem(R.id.prioritario).setIcon(R.drawable.rating_favorite);
	      	 flagprior=1;  
	      	 priormsj.setBackgroundDrawable(getResources().getDrawable(R.drawable.botondisoft));
	      	 priormsj.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	      	 priormsj.setOnClickListener(popupAd); }
	       db.updateEstablecimiento(e);
	       return true; }
	    else if(item.getItemId() == R.id.refrescarmenu) {
	       if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	       if(!isNetworkAvailable()){
   				Toast.makeText(getApplicationContext(), getString(R.string.nointernet), 
   						Toast.LENGTH_LONG).show();
   				return false; } else {
	       new RefrescaEstablecimiento().execute();
	       /*try {Thread.sleep(1000);} catch (Exception e) {}
	       Intent i = new Intent(Establecimiento.this, Establecimiento.class);
	       i.putExtra("eid",e.getEid()); startActivity(i); finish();
   			*/}} return true;
	  } 
	
	View.OnClickListener popupAd = new View.OnClickListener(){ @Override public void onClick(View v) {
		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
  	if(e.getPrior()>0){
  		if(e.getCinterno()==null||e.getCinterno().equals(""))
				 Toast.makeText(getBaseContext(), getString(R.string.establishmentPromotionDate), Toast.LENGTH_LONG).show();
			 else{
				 if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
					{Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
					if(display.getRotation()==Surface.ROTATION_90)
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					else if(display.getRotation()==Surface.ROTATION_270){ 
						if(android.os.Build.VERSION.SDK_INT == 8)
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
				}else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  		LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
  		popupView = layoutInflater.inflate(R.layout.popupad, null);  
  		popupWindow = new PopupWindow(popupView,android.view.ViewGroup.LayoutParams.MATCH_PARENT,
  			android.view.ViewGroup.LayoutParams.MATCH_PARENT, true);
  		popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
  		popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
  		WebView wv = (WebView) popupView.findViewById(R.id.webView);
  			 if(e.getCinterno()!=null)
  				 wv.loadDataWithBaseURL("",e.getCinterno(),"text/html", "UTF-8", "");
  		flagprior=2;
  		((ImageButton)popupView.findViewById(R.id.cerrar)).setOnClickListener(new OnClickListener(){ 
  			@Override public void onClick(View v) {
  				if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
  				popupWindow.dismiss();}});
  	}} }};
	 
	View.OnClickListener activaPrior = new View.OnClickListener(){
		@Override public void onClick(View v) {
		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		  priormsj.setBackgroundDrawable(getResources().getDrawable(R.drawable.botondisoft));
		  priormsj.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
		  priormsj.setOnClickListener(popupAd); e.setPrior(1); flagprior=1;
		  menu.findItem(R.id.prioritario).setIcon(R.drawable.rating_favorite);
		}};

	@Override public void onBackPressed() {
		super.onBackPressed(); boolean estadofav;
	  Drawable resIcon = getResources().getDrawable(R.drawable.rating_not_important);
    if(fav.getIcon().getConstantState()==resIcon.getConstantState()) estadofav=false;
    else estadofav=true;
    if(e.getFav()!=estadofav || e.getVal()!=valoracion.getRating() ||
    		e.getPrior()!=flagprior || (!referencia.getText().toString().equals("") 
    		&& !e.getReferencia().equals(referencia.getText().toString()))){
    	e.setFav(estadofav); e.setPrior(flagprior); e.setVal(valoracion.getRating());
    	if(!referencia.getText().toString().equals(null) && !referencia.getText().toString().equals(""))
    		e.setReferencia(referencia.getText().toString());
     	db.updateEstablecimiento(e); }
    Intent i;
    if(getIntent().getIntExtra("tipo",2)==2) 
    	i = new Intent(this, ListaEstablecimientos.class);
    else if(getIntent().getIntExtra("tipo",2)==3){
    	i = new Intent(this, ListaCompra.class);
    	i.putExtra("eid", getIntent().getIntExtra("eid",0));
    }else { 
     	i = new Intent(this, Mensaje.class);
     	i.putExtra("tipo", getIntent().getIntExtra("tipo",2));
			i.putExtra("mid", getIntent().getIntExtra("mid",0));
    } startActivity(i); finish();
	}
	 
	 public void addContact() {
		 String DisplayName = e.getNombre(); String WorkNumber = e.getTel();
		 String emailID = e.getMail();
		 ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		 ops.add(ContentProviderOperation.newInsert(
		 ContactsContract.RawContacts.CONTENT_URI)
		     .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
		     .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
		 //------------------------------------------------------ Names
		 if (DisplayName != null) {
			 ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				 .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
		     .withValue(ContactsContract.Data.MIMETYPE,
		     ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
		     .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
		     DisplayName).build()); }
		 //------------------------------------------------------ Work Numbers
		 if (WorkNumber != null) {
		   ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
		     .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
		     .withValue(ContactsContract.Data.MIMETYPE,
		     ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
		     .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
		     .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
		     ContactsContract.CommonDataKinds.Phone.TYPE_WORK).build());}
		 //------------------------------------------------------ Email
		 if (emailID != null) {
		   ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
		  	 .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
		     .withValue(ContactsContract.Data.MIMETYPE,
		     ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
		     .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
		     .withValue(ContactsContract.CommonDataKinds.Email.TYPE, 
		     ContactsContract.CommonDataKinds.Email.TYPE_WORK).build());}
		 // Asking the Contact provider to create a new contact                 
		 try{getContentResolver().applyBatch(ContactsContract.AUTHORITY,ops);}
		 catch(Exception e){e.printStackTrace();
		   Toast.makeText(getBaseContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();} 
	 }
	 
	 public boolean isNetworkAvailable() {
		 ConnectivityManager connectivityManager = (ConnectivityManager) 
				 getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		 if(sharedPrefs.getInt("internetmode",0)==0)
		 return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		 else return false;}

	 public class RefrescaEstablecimiento extends AsyncTask<String, Void, Boolean> {
     private ProgressDialog loading; boolean estadofav; float val; String ref;
     
     protected void onPreExecute() {
		 String ref = "0";
		 try{ref = referencia.getText().toString();
		 }catch(NumberFormatException ex){ex.printStackTrace(); }
		val=valoracion.getRating();
     	loading = new ProgressDialog(Establecimiento.this);
			loading.setMessage(getString(R.string.establishmentUpdate)+Html.fromHtml("&#8230"));
			loading.setCancelable(false); loading.show();
			Drawable resIcon = getResources().getDrawable(R.drawable.rating_not_important);
    	if(fav.getIcon().getConstantState()==resIcon.getConstantState()) estadofav=false;
    	else estadofav=true;}
     
     protected void onPostExecute(final Boolean success) {
    	 try{if (loading.isShowing())loading.dismiss();}catch(Exception e){} 
    	 new descargaLogos().execute(); }

	@Override protected Boolean doInBackground(String... arg0) {
		try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException er){er.printStackTrace();}
		try{DriverManager.setLoginTimeout(20);
			Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT "+getString(R.string.camposest)+
					", textcat_all(cnae || ',') FROM app_company, categoriaempresa WHERE app_company.idcompanyapp='"+
					e.getEid()+"' AND app_company.idcompanyapp=categoriaempresa.idcompanyapp GROUP BY "+
					getString(R.string.camposest)+";");
			if(rs.next()){
					db.updateEstablecimiento(new Est(rs.getInt(1),rs.getInt(2),rs.getString(3),
						rs.getString(4),rs.getString(5),rs.getString(6),e.getCodcliente(),rs.getBoolean(7),
						estadofav,flagprior,val,rs.getString(8),rs.getString(9),
						rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),
						rs.getString(14),rs.getString(15),e.getTarifa(),rs.getString(16),rs.getString(17),"",
						rs.getString(18),rs.getString(19),rs.getString(20),rs.getString(21),
						ref,rs.getString(22),rs.getString(23)));
			} rs.close(); st.close(); conn.close();
		}catch(java.sql.SQLException e){e.printStackTrace();}
	return true;}}
	 
	 public class borrarArticulos extends AsyncTask<String, Integer, Boolean> {
		 ProgressDialog loading; CheckBox cbimagenes;
		 public borrarArticulos(CheckBox cbimagenes){this.cbimagenes=cbimagenes;};
		 
		 @Override protected void onPreExecute(){
			loading = new ProgressDialog(Establecimiento.this);
			loading.setMessage("Eliminando datos anteriores"+Html.fromHtml("&#8230"));
			loading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			loading.setIndeterminate(false);
			loading.setMax(db.getAllPedidosAnt(e.getEid()).size());
			loading.setCancelable(false); loading.show();
		 }
		 
		 @Override protected void onPostExecute(Boolean success){
			 if(loading.isShowing()) loading.dismiss();
			 Intent intent = new Intent(Establecimiento.this, TiendaVirtual.class);
			 intent.putExtra("eid", e.getEid());
			 intent.putExtra("intent", "establecimiento");
			 intent.putExtra("funcion", "descarga");
			 intent.putExtra("imagenes", cbimagenes.isChecked());
			 intent.putExtra("borrados", true);
			 Toast.makeText(getBaseContext(), R.string.dltoast ,Toast.LENGTH_LONG).show();
			 startActivity(intent);
		 }
		 
		 protected void onProgressUpdate(Integer... progreso) {
	            loading.setProgress(progreso[0]);
	            if(progreso[0]==-1){ loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            	loading.setMessage("Terminando de borrar"+Html.fromHtml("&#8230")); }
	            }

		 @Override protected Boolean doInBackground(String... params) {
			//Toast.makeText(getBaseContext(), "Limpiando art�culos..." ,Toast.LENGTH_LONG).show();
			 int contador=0; String articulos="";
			for(Ped p : db.getAllPedidosAnt(e.getEid())){
				articulos+=p.getAid()+",";
				/*try{db.deleteArticulo(db.getArticulo(p.getAid()));
					db.deleteArticuloEstablecimiento(p.getAid(), e.getEid());}
				catch(Exception ex){ex.printStackTrace();}*/
				publishProgress(contador); contador++;
			} articulos=articulos.substring(0,articulos.length()-1); //Borrar la �ltima coma.
			loading.setProgress(-1);
			db.massDeleteArticulos(articulos);
			Log.e("FIN","ARTICULOS");
			db.massDeleteArticulosEstablecimiento(e.getEid(), articulos);
			Log.e("FIN","ARTICULOSESTABLECIMIENTO");
			if(db.getPedidoPendiente(e.getEid())!=null)
				db.deletePedido(db.getPedidoPendiente(e.getEid()));
			Log.e("FIN","PEDIDO");
			db.deletePedidoAnt(e.getEid());
			Log.e("FIN","PEDIDOANT");
			return true;
		 }
		 
	 }

	 public class descargaLogos extends AsyncTask<String, Void, Boolean> {//S�lo descarga de nuevos

     protected void onPostExecute(final Boolean success) {
      	Intent i = new Intent(Establecimiento.this, Establecimiento.class);
      	i.putExtra("eid", getIntent().getIntExtra("eid",0));
 			startActivity(i); finish();}

 		@Override protected Boolean doInBackground(String... params) {
   	URL url; URLConnection conn;
     BufferedInputStream inStream; BufferedOutputStream outStream;
     File outFile; FileOutputStream fileStream;
	   outFile = new File( File.separator + "data" + File.separator + "data" + 
	  		 File.separator + Establecimiento.this.getPackageName() +
	  		 File.separator + "logos" + File.separator);
	   if(!outFile.exists()){ outFile.mkdir();
	   try{new File (outFile+ File.separator+".nomedia").createNewFile();
	   }catch (IOException e){e.printStackTrace();} }
	   if(e.getLogo()!=""){
	   try{url = new URL(getString(R.string.dirlogos)+e.getLogo());
	   	 conn = url.openConnection(); conn.setUseCaches(false);
	     inStream = new BufferedInputStream(conn.getInputStream());
	     outFile = new File (outFile + File.separator + e.getLogo());
	     fileStream = new FileOutputStream(outFile);
	     outStream = new BufferedOutputStream(fileStream, 4096);
	     byte[] data = new byte[4096]; int bytesRead = 0;
	     while((bytesRead = inStream.read(data, 0, data.length)) >= 0)
	       outStream.write(data, 0, bytesRead);
	     outStream.close(); fileStream.close(); inStream.close(); }
	   catch(MalformedURLException e){} catch(FileNotFoundException e){} catch(Exception e){} }
	   return true;}
   }
}