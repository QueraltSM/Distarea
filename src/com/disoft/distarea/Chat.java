package com.disoft.distarea;

//TODO separar la parte de API <19 de la otra,	
//TODO en esa nueva aplicar lo de ThumbnailTest (?)
//TODO Eliminar el boolean editmode y transferir su funcionamiento al entero "mode" (+ info en ventanaXML/contabilizar)

//TODO Intentar arreglar compartir una carpeta, con permisos para edición a aquellos que tengan el enlace, DESDE LA CREACIÓN.

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.disoft.distarea.extras.Automailer;
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.extras.FileUtils;
import com.disoft.distarea.extras.GoogleDriveCallback;
import com.disoft.distarea.extras.GoogleDriveController;
import com.disoft.distarea.extras.SharedPreferencesController;
import com.disoft.distarea.extras.SharedPreferencesTableLoader;
import com.disoft.distarea.extras.StringTranslator;
import com.disoft.distarea.extras.Table;
import com.disoft.distarea.models.CliF;
import com.disoft.distarea.models.Est;
import com.disoft.distarea.models.Msj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//import android.support.v7.M;
//import com.actionbarsherlock.view.MenuInflater;
//import com.actionbarsherlock.view.MenuItem;
/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;*/

public class Chat extends AppCompatActivity implements GoogleDriveCallback{
	
	private static final String FILE_CACHE_NAME = "pendingfiles.distarea";
	private String nombreCarpeta;
	private ArrayList<String> ficheros,uploadedFiles;
	ArrayList<CliF> clientesf = new ArrayList<CliF>();
	ArrayList<Integer> ocultos;
	private GoogleDriveController googleDriveController;
	ActionBar ab;
	ProgressDialog conectaGD;
	String referencia, idop, anoactual, idfoto, millis, extension, solofichero, prevdate="",docUri,ids="",envids="";
	String[] partes;
	Locale spanish = new Locale("es", "ES");
	View v, popupView; PopupWindow popupWindow;
	Menu menu; MenuItem fav; Est e; File f, fotoa;
	int emailenviado=0, flagfocus=0, offset=0, flagmodoadjunto=0, toppos=0, flagMensajesOcultos=0;
	SharedPreferences sharedPrefs; 
	DatabaseHandler db; Msj mensaje;
	LinearLayout ll, adjuntos, ladjuntos; 
	EditText cuerpo, files;
	LinearLayout.LayoutParams parenv, parrec, parspacer; 
	SimpleDateFormat nombrefichero = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", spanish),
					 sdf = new SimpleDateFormat("yyyy-MM-dd",spanish),
					 sdfdia = new SimpleDateFormat("dd-MM-yyyy",spanish),
					 postgrestyle = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",spanish);
	
	//Dialog declaraciones
	Dialog dialog; 
	TextView nombrepreliminar; 
	Spinner s; 
	String bypass="",dicempresa="", oriempresa="";
	SharedPreferencesTableLoader sptl;
	EditText 	empresa,fecha,factura,nif,total,recargo,retencion,implicito,iva0,iva4,iva10,iva99,
				iva21,igic0,igic3,igic7,igic9,igic13,igic20,igic99,cuentaContrapartida,contrapartida,
				importeContrapartida,cuentaCobro,cuentaPago,importeCobro,importePago,cobrado,
				pagado,efectiva,aviso,modo,descripcion; 
	LinearLayout lineaEmpresa,lineaFecha,lineaFactura,lineaNIF,lineaTotal,lineaIVA0,lineaIVA4,lineaIVA10,
				lineaIVA99,lineaIVA21,lineaRecargo,lineaRetencion,lineaImplicito,lineaCuentaContrapartida,
				lineaImporteContrapartida,lineaCuentaCobro,lineaCuentaPago,lineaAviso,lineaModo,
				lineaImporteCobro,lineaImportePago,lineaCobrado,lineaPagado,lineaEfectiva,lineaIGIC0,
				lineaIGIC3,lineaIGIC7,lineaIGIC9,lineaIGIC13,lineaIGIC20,lineaIGIC99,lineaDescripcion;
	Button cancelar,continuar,finalizar; 
	ImageView preview; 
	LinearLayout micytip;
	ImageButton mic,tip,mas,btnfecha, btnrevert, btnmas;
	boolean rev=false;
	
	@SuppressLint("NewApi")
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		ab = getSupportActionBar();
		ficheros = new ArrayList<String>();
		uploadedFiles = new ArrayList<String>();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
		ab.setTitle("Chat"); db = new DatabaseHandler(this);
		e = db.getEstablecimiento(getIntent().getIntExtra("eid",0));
		ab.setSubtitle(e.getNombre());
		ab.setIcon(R.drawable.social_person); setContentView(R.layout.chat);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		v = findViewById(R.id.basechat); ll = (LinearLayout) findViewById(R.id.burbujas);
		cuerpo = (EditText) findViewById(R.id.cuerpo);
		files = (EditText) findViewById(R.id.files);
		adjuntos = (LinearLayout) findViewById(R.id.adjuntos);
		ladjuntos = (LinearLayout) findViewById(R.id.listadjuntos);
		parenv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		parenv.gravity=Gravity.RIGHT; parenv.setMargins(100, 10, 0, 10);
		parrec = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		parrec.gravity=Gravity.LEFT; parrec.setMargins(0, 10, 100, 10);
		parspacer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		parspacer.gravity=Gravity.CENTER;
		parspacer.setMargins(0, 15, 0, 15);
		sptl = new SharedPreferencesTableLoader(this);
	 
		//Botón enviar
		ImageButton enviar = (ImageButton) findViewById(R.id.enviar);
		enviar.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v) {
				if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
				if(sharedPrefs.getInt("internetmode", 0)==2)
					Toast.makeText(getBaseContext(), "No puede acceder a internet con el modo Offline activo.", Toast.LENGTH_LONG).show();
				else{
				if(!cuerpo.getText().toString().equals("")){ 
					if(isNetworkAvailable()){
						if(sharedPrefs.getBoolean("ms",false)==true){
							LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
							final View popupView = layoutInflater.inflate(R.layout.popupms, null);  
							popupWindow = new PopupWindow(popupView,android.view.ViewGroup.LayoutParams.MATCH_PARENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
							popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
							popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
							((TextView) popupView.findViewById(R.id.texto)).setText((((TextView) popupView.findViewById(R.id.texto)).getText())+" "+getString(R.string.motivo1));
							ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
							ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
							si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
							no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
							si.setOnClickListener(new OnClickListener(){
								@Override public void onClick(View v) {
									if(sharedPrefs.getBoolean("ch",true)) 
										v.performHapticFeedback(1);
									if(((EditText)popupView.findViewById(R.id.mspass)).getText().toString().equals(sharedPrefs.getString("pass", ""))) new EnviarMensaje().execute();
									else ((EditText)popupView.findViewById(R.id.mspass)).requestFocus(); 
								}
							});
							no.setOnClickListener(new OnClickListener(){
								@Override public void onClick(View v) {
									if(sharedPrefs.getBoolean("ch",true)) 
										v.performHapticFeedback(1);
									popupWindow.dismiss();
									Toast.makeText(getApplicationContext(), getString(R.string.cancel1),Toast.LENGTH_LONG).show();
								}
							});
						}else {
							if(!ficheros.isEmpty()){
								if(flagmodoadjunto==2){
									/*for(String s : ficheros){ Log.e("FICHERO",s);
									if(s.contains(".jpg"))
										googleDriveController.uploadFile(new File(s), s.split("/")[s.split("/").length-1], "image/jpg");
									else if(s.contains(".xml"))
										googleDriveController.uploadFile(new File(s), s.split("/")[s.split("/").length-1], "text/xml");
									else 
										googleDriveController.uploadFile(new File(s), s.split("/")[s.split("/").length-1], "application/vnd.google-apps.file");
									}
							/*/		
									Log.e("SIGUIENTE",ficheros.get(0));
									if(googleDriveController == null){ Log.e("GDC","null"); 
										nombreCarpeta = "Distarea_"+sharedPrefs.getInt("id",0);
										googleDriveController = new GoogleDriveController(Chat.this,Chat.this,nombreCarpeta);
										googleDriveController.connect();
									}else Log.e("GDC",googleDriveController.isConnected()+"");
							if(ficheros.get(0).contains(".jpg"))
								googleDriveController.uploadFile(new File(ficheros.get(0)), ficheros.get(0).split("/")[ficheros.get(0).split("/").length-1], "image/jpg");
							else if(ficheros.get(0).contains(".xml"))
								googleDriveController.uploadFile(new File(ficheros.get(0)), ficheros.get(0).split("/")[ficheros.get(0).split("/").length-1], "text/xml");
							else 
								googleDriveController.uploadFile(new File(ficheros.get(0)), ficheros.get(0).split("/")[ficheros.get(0).split("/").length-1], "application/vnd.google-apps.file");
							}}
							new EnviarMensaje().execute();	
						}
					}else{
						Msj mensaje = new Msj(db.getLastMidEnv()+1,sharedPrefs.getInt("id",0),
								cuerpo.getText().toString(),files.getText().toString(),new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),
								new SimpleDateFormat("HH:mm:ss",spanish).format(new Date()),"",
								e.getEid(),"A","","","","","P",
								sharedPrefs.getString("seudonimo",sharedPrefs.getString("nombre","")),0,"");
						Toast.makeText(getBaseContext(),getString(R.string.malmacen),Toast.LENGTH_LONG).show();
						db.almacenarMensajeEnviado(mensaje);
					}
				}
			}
			}
		});
	
		//Conversación
		if(db.getConversacionCount(e.getEid())>(offset+1)*20){
			final Button veranteriores = new Button(getBaseContext());
			veranteriores.setText("Ver mensajes anteriores");
			veranteriores.setBackgroundDrawable(getResources().getDrawable(R.drawable.botondisoft));
			veranteriores.setTextColor(getResources().getColor(android.R.color.black));
			veranteriores.setLayoutParams(parspacer);
			veranteriores.setOnClickListener(new OnClickListener(){ public void onClick(View v){
			offset++; 
			toppos=ll.getChildCount();
			ll.removeAllViews();
			if(db.getConversacionCount(e.getEid())>(offset+1)*20){
				ll.addView(veranteriores);}
			mostrarConversacion();
			Log.e("OFFSET",""+offset);
			Log.e("TOPPOS",""+toppos);
			Log.e("CHILDCOUNT",""+ll.getChildCount());
			//ll.getChildAt((offset+1)*20-offset*20).setFocusableInTouchMode(true); 
			//ll.getChildAt((offset+1)*20-offset*20).requestFocus(); }});
			ll.getChildAt(ll.getChildCount()-toppos+offset).setFocusableInTouchMode(true); 
			ll.getChildAt(ll.getChildCount()-toppos+offset).requestFocus(); }});
			ll.addView(veranteriores);
		}
		mostrarConversacion();
    	
		if(e.getLogo()!=null){
			File f = new File( File.separator + "data" + File.separator + "data" + 
			File.separator + Chat.this.getPackageName() + 
   			File.separator + "logos" + File.separator + e.getLogo());
			ab.setIcon(Drawable.createFromPath(f.getAbsolutePath()));
		}else ab.setIcon(getResources().getDrawable(R.drawable.tienda));
		
		if (!SharedPreferencesController.getResourceID(getBaseContext()).equals("FAIL")){
			//nombreCarpeta = sharedPrefs.getString("seudonimo","");
			nombreCarpeta = "Distarea_"+sharedPrefs.getInt("id",0);
			googleDriveController = new GoogleDriveController(this,this,nombreCarpeta);
			googleDriveController.connect();
		}
	}
		 
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		//if(e.getCnae() == null || !e.getCnae().contains("58100"))
		if(e.getConfigura() != null && e.getConfigura().contains(",C,"))
			inflater.inflate(R.menu.chat, menu);
		else inflater.inflate(R.menu.chatof, menu);
		this.menu = menu; 
		if(sharedPrefs.getInt("internetmode", 0)==2) return false;
		else return true; 
	}

	@Override public void onBackPressed(){
		if(flagMensajesOcultos==0){
			Intent intent = new Intent(Chat.this, Conversaciones.class);
	    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
	    	startActivity(intent); finish(); 
    	}else{
    		ab.setSubtitle(e.getNombre());
			for(int i=0;i<ll.getChildCount();i++)
				ll.getChildAt(i).setVisibility(View.VISIBLE);
    	}}
    
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent(Chat.this, Conversaciones.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); finish(); 
		}else if (item.getItemId() == R.id.overflow){
			if(sharedPrefs.getInt("internetmode", 0)==2){
				Toast.makeText(getBaseContext(), "No puede acceder a internet con el modo Offline activo.", Toast.LENGTH_LONG).show();
				menu.close();
				return false;}
		}else if (item.getItemId() == R.id.overflowplus){
			/*if(SharedPreferencesController.getValue(getBaseContext(), "_root")!=null){
				nombreCarpeta = "Distarea_"+sharedPrefs.getInt("id",0);
				googleDriveController = new GoogleDriveController(Chat.this,Chat.this,nombreCarpeta);
				googleDriveController.connect(); googleDriveController.checkBusinessResource();
			}*/
			if(sharedPrefs.getInt("internetmode", 0)==2){
				Toast.makeText(getBaseContext(), "No puede acceder a internet con el modo Offline activo.", Toast.LENGTH_LONG).show();
				menu.close();
				return false;}
			else{
			if(sharedPrefs.getInt("solicitacliest", 0)==e.getEid()){
				if(SharedPreferencesController.getResourceFolder(getBaseContext(),nombreCarpeta)==null){
					/*if(android.os.Build.VERSION.SDK_INT >= 14)
			  			menu.performIdentifierAction(R.id.overflowplus,0); //Crearlo en los xml
			  		else{ SubMenu subMenu = menu.getItem(menu.size()-1).getSubMenu();
			  			  menu.performIdentifierAction(subMenu.getItem().getItemId(), 0); }*/
					item.getSubMenu().clear();
					menu.clear();
					MenuInflater inflater = getMenuInflater();
					inflater.inflate(R.menu.chat, menu);
					Toast.makeText(getBaseContext(), "Conectando cuenta Google Drive... Espere, por favor...", Toast.LENGTH_LONG).show();
					nombreCarpeta = "Distarea_"+sharedPrefs.getInt("id",0);
					googleDriveController = new GoogleDriveController(Chat.this,Chat.this,nombreCarpeta);
					googleDriveController.connect();
				}
			}else if(e.getReferencia().equals("0") || 
					SharedPreferencesController.getResourceFolder(getBaseContext(),nombreCarpeta)==null){
				item.getSubMenu().clear();
				checkReferencia(false);
				return true; 
			}
			}
		}else if (item.getItemId() == R.id.adjarc){
			if (Build.VERSION.SDK_INT <19) //Comprobación si es KitKat o anterior:
				startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("*/*"),1);
			else startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT)
					.addCategory(Intent.CATEGORY_OPENABLE).setType("*/*"),1);
		}else if (item.getItemId() == R.id.adjfot){
			bypass=getExternalCacheDir()+File.separator+"IMG_"+
							new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime())
							+"_"+sharedPrefs.getString("idfoto","000")+".jpg";
			startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
				.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(bypass))),2);
		}else if (item.getItemId() == R.id.adjvid){
			startActivityForResult(new Intent(MediaStore.ACTION_VIDEO_CAPTURE),3);
		}else if (item.getItemId() == R.id.adjaud){
			startActivityForResult(new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION),4);
		}else if (item.getItemId() == R.id.adjarcplus){
			if(sharedPrefs.getInt("solicitacliest", 0)==e.getEid()){
				new descargaClientes(7).execute(); 	
			}else{
				//Composición de campos para nombre fichero
				//Referencia
				if(sharedPrefs.getInt("solicitacliest", 0)==e.getEid())
					referencia = sharedPrefs.getString("ultimareferencia","-1");
				else
					referencia = appendReferenceNumberToUri(e.getReferencia());
				
				//Año actual
				anoactual = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());
				
				//ID OP
				if(sharedPrefs.getBoolean("solicitacli", false))
					idop = ""+sharedPrefs.getInt("solicitaclin", 0);
				else idop = "00";
				
				//ID Foto
				idfoto = sharedPrefs.getString("idfoto", "000");
				
				//Millis
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, 1970);
				millis=""+c.getTimeInMillis();
				/*if(millis == null || millis.equals(""))
					millis=""+sharedPrefs.getLong("lastmillis", 0);*/
				
				bypass=getExternalCacheDir()+File.separator+
						referencia+"_"+anoactual+"_"+idop+"_"+idfoto+"_"+millis;//+".jpg"; (No hay extensión, no se ha elegido fichero aún)
				solofichero=referencia+"_"+anoactual+"_"+idop+"_"+idfoto+"_"+millis;
				sharedPrefs.edit().putLong("lastmillis", Long.parseLong(millis))
					.putString("bypass", bypass).commit();
			if (Build.VERSION.SDK_INT <19) //Comprobación si es KitKat o anterior:
				startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("*/*"),7);
			else startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT)
					.addCategory(Intent.CATEGORY_OPENABLE).setType("*/*"),7);
			}
			}else if (item.getItemId() == R.id.adjfotplus){
			if(sharedPrefs.getInt("solicitacliest", 0)==e.getEid()){
				new descargaClientes(8).execute(); 	
			}else{
				//Composición de campos para nombre fichero
				//Referencia
				if(sharedPrefs.getInt("solicitacliest", 0)==e.getEid())
					referencia = sharedPrefs.getString("ultimareferencia","-1");
				else
					referencia = appendReferenceNumberToUri(e.getReferencia());
				
				//Año actual
				anoactual = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());
				
				//ID OP
				if(sharedPrefs.getBoolean("solicitacli", false))
					idop = ""+sharedPrefs.getInt("solicitaclin", 0);
				else idop = "00";
				
				//ID Foto
				idfoto = sharedPrefs.getString("idfoto", "000");
				
				//Millis
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, 1970);
				millis=""+c.getTimeInMillis();
				/*if(millis == null || millis.equals(""))
					millis=""+sharedPrefs.getLong("lastmillis", 0);*/
				
				bypass=getExternalCacheDir()+File.separator+
						referencia+"_"+anoactual+"_"+idop+"_"+idfoto+"_"+millis+".jpg";
				solofichero=referencia+"_"+anoactual+"_"+idop+"_"+idfoto+"_"+millis;
				sharedPrefs.edit().putLong("lastmillis", Long.parseLong(millis))
					.putString("bypass", bypass).commit();
			/*bypass=getExternalCacheDir()+File.separator+
  					appendReferenceNumberToUri(e.getReferencia())+"_"+
					new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime())
					+"_"+sharedPrefs.getString("idfoto","000")+".jpg"; */
			startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
				.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(bypass))),8); }
		}else if (item.getItemId() == R.id.recpend){
			showPendingFileChooserDialog();
		}return true;
	}
	 
	public boolean isNetworkAvailable() {
		 ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		 if(sharedPrefs.getInt("internetmode",0)==0)
		 return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		 else return false;
	}
	 
	@SuppressLint("NewApi")
	@Override public void onActivityResult(int requestCode, int resultCode, final Intent intent) {
		if (resultCode == Activity.RESULT_OK){
			final LinearLayout ll = new LinearLayout(getBaseContext());
			final ImageButton ib = new ImageButton(getBaseContext());
			final TextView tv = new TextView(getBaseContext());
			ll.setOrientation(LinearLayout.HORIZONTAL);
			ib.setImageDrawable(getResources().getDrawable(R.drawable.navigation_cancel));
			ib.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
			tv.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Large);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
			tv.setLayoutParams(param);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			if (requestCode > 0 && requestCode<5) { // Adjuntar archivo
				final String path;
				if(requestCode==2){
					path = bypass;
					//Actualizo idfoto
					String idfoto = sharedPrefs.getString("idfoto","000");
					idfoto = String.valueOf(Integer.parseInt(idfoto)+1);
					if(idfoto.length()==1) idfoto="00"+idfoto;
					else if(idfoto.length()==2) idfoto="0"+idfoto;
					SharedPreferences.Editor spe = sharedPrefs.edit();
					spe.putString("idfoto", idfoto).commit(); 
				}else path = intent.getData().toString();
				FileUtils.getPath(getBaseContext(), intent.getData());
	  			tv.setText(path.substring(path.lastIndexOf("/")+1));
	  			if(FileUtils.getMimeType(new File(path)).contains("image")){
	  				tv.setClickable(true);					
					tv.setOnClickListener(new OnClickListener(){
						@Override public void onClick(View arg0) {				
							ventanaXML(ll,ib,tv,true,path,0);
						}});
	  			}
	  			ib.setOnClickListener(new OnClickListener(){ 
	  				public void onClick(View v){
	  					ladjuntos.removeView(ladjuntos.findViewWithTag(path));
	  					if(ladjuntos.getChildCount()==0){ adjuntos.setVisibility(View.GONE);
	  					if(menu!=null){ menu.clear();
						MenuInflater inflater = getMenuInflater();
						if(e.getCnae() == null || !e.getCnae().contains("58100"))
							inflater.inflate(R.menu.chatof, menu);
						else inflater.inflate(R.menu.chat, menu);
						flagmodoadjunto=0; }}
	  				}
	  			});
	  			ll.addView(ib); ll.addView(tv); ll.setTag(path);
	  			ladjuntos.addView(ll); 
	  			if(cuerpo.getText().toString().equals(""))
	 				cuerpo.setText(path.substring(path.lastIndexOf("/")+1));
	  			ficheros.add(path);
	  			adjuntos.setVisibility(View.VISIBLE);
	  			
	  			if(flagmodoadjunto==0){
	  				if(menu!=null){menu.clear();
	  				MenuInflater inflater = getMenuInflater();
	  				inflater.inflate(R.menu.chatof, menu);
	  				flagmodoadjunto=1; }}
	  			
	  		}else if (requestCode == 6) { //Llamada a Voz a Texto
	  			ArrayList<String> text = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	  			writeFieldsViaVoice(text);
	  			
	  		}else if (requestCode == 7){
	  			if(Build.VERSION.SDK_INT>=19) docUri = intent.getData().toString();
	  			final String path = FileUtils.getPath(getBaseContext(), intent.getData());
	  			//final String path = intent.getData().toString();
	  			
	  			//Intento arreglo selección fichero 3/11
	  			//Copio origen a destino, para usarlo como criterio.
	  			extension = FileUtils.getExtension(docUri);

	  			Log.e("DOCURI",""+docUri);
	  			//Log.e("DOCURI_PARSED",""+Uri.parse(docUri)); = DOCURI
	  			//Log.e("SELECCION",""+Uri.parse(docUri).toString()); = DOCURI
	  			Log.e("RUTA",""+FileUtils.getPath(getBaseContext(), Uri.parse(docUri)));
	  			Log.e("TESTEXIST",""+new File(FileUtils.getPath(getBaseContext(), Uri.parse(docUri))).exists());
	  			//Log.e("RUTA2",""+FileUtils.getFile(getBaseContext(), Uri.parse(docUri))); = RUTA
	  			
	  			Log.e("SEL-EXTENSION",""+extension);
	  			Log.e("SEL-BYPASS",""+bypass);
	  			boolean ok = FileUtils.copyFile(FileUtils.getPath(getBaseContext(), Uri.parse(docUri)), bypass+extension);
	  			
	  			//boolean ok = FileUtils.copyFile(docUri, bypass);
	  			
	  			Log.e("COPIA",""+ok);
	  			ventanaXML(ll, ib, tv,false,bypass+extension,1);
	  			//ventanaXML(ll, ib, tv,false,path,1);
	  		}else if(requestCode == 8){ 
	  			bypass=sharedPrefs.getString("bypass", "");
	  			ventanaXML(ll, ib, tv,false,bypass,2);
	  		}else if(requestCode == GoogleDriveController.RESOLVE_CONNECTION_REQUEST_CODE){
  				try{googleDriveController.connect(); }
  				catch(Exception ex){ ex.printStackTrace(); }
		    }else if(requestCode == GoogleDriveController.SUCESS_CODE){
  				Toast.makeText(getBaseContext(),"Conectado", Toast.LENGTH_LONG).show(); }
	  	}	    	
	}

    public void mostrarConversacion(){
    	List<Msj> conver = db.getConversacion(e.getEid(), offset);
    	Collections.reverse(conver); int tick=0, rmte=3;
   	 for(Msj m : conver){
   		 final TextView burbuja = new TextView(this);
   		 burbuja.setTag(m);
   		 burbuja.setTextColor(getResources().getColor(android.R.color.black));
   		 if(m.getTipomsj().equals("A") && m.getClienteglobal()==sharedPrefs.getInt("id",0)){ 
   			 burbuja.setLayoutParams(parenv);
   			 burbuja.setBackgroundDrawable(getResources().getDrawable(R.drawable.burbujaverde3));}
   		 if(m.getTipomsj().equals("G") || 
   				 (m.getTipomsj().equals("A") && m.getClienteglobal()!=sharedPrefs.getInt("id",0))){ 
   			 burbuja.setLayoutParams(parrec);
   			 if(m.getTipomsj().equals("G"))
   				 burbuja.setBackgroundDrawable(getResources().getDrawable(R.drawable.burbujablanca3));
   			 else
  				 burbuja.setBackgroundDrawable(getResources().getDrawable(R.drawable.burbujamagenta3));
   		 }
   		 	burbuja.setText(m.getMensaje().replaceAll("<br/>", "\n")+"\n"+m.getHorarealiz().substring(0,m.getHorarealiz().length()-3));
   		 if(m.getTipomsj().equals("A") && m.getClienteglobal()==sharedPrefs.getInt("id",0))
   			burbuja.setText(burbuja.getText().toString()+" "+Html.fromHtml("&#10003"));
   		 SpannableString ss = new SpannableString(burbuja.getText().toString());
   		 if(m.getTipomsj().equals("A") && m.getClienteglobal()==sharedPrefs.getInt("id",0)) tick=2; else tick=0; 
   		 rmte=0; if(m.getRmte().equals(e.getNombre())) m.getRmte().length(); //Parche cancelación tamaño remitente = establecimiento
   		 if(m.getTipomsj().equals("A") && m.getClienteglobal()!=sharedPrefs.getInt("id",0)){ rmte=3;
   			 //Sobreescribo la burbuja, para poder colar el remitente antes
   			burbuja.setText(m.getMensaje().replaceAll("<br/>", "\n")+"\n--"+m.getRmte()+" "+m.getHorarealiz().substring(0,m.getHorarealiz().length()-3));
   			ss = new SpannableString(burbuja.getText().toString());
   			/*Log.e("BURBUJA:",""+burbuja.getText().toString());
   			Log.e("BURBUJA.LENGTH",""+burbuja.getText().length());
   			Log.e("HORAREALIZ.LENGTH","- "+m.getHorarealiz().length());
   			Log.e("RMTE","- "+rmte);
   			Log.e("RMTE.LENGTH","- "+m.getRmte().length());
   			Log.e("+3","+ 3");
   			Log.e("TICK","+ "+tick);
   			Log.e("DESDE",""+(burbuja.getText().length()-m.getHorarealiz().length()-rmte-m.getRmte().length()+3-tick));
   			Log.e("HASTA",""+(burbuja.getText().length()-m.getHorarealiz().length()-rmte+3-tick));*/
   			
   			ss.setSpan( new StyleSpan(Typeface.ITALIC), (burbuja.getText().length()-m.getHorarealiz().length()-rmte-m.getRmte().length()+3-tick), 
  				 (burbuja.getText().length()-m.getHorarealiz().length()-rmte+3-tick), Spannable.SPAN_INCLUSIVE_INCLUSIVE );
   			
   			ss.setSpan( new RelativeSizeSpan(0.8f), burbuja.getText().length()-rmte-m.getRmte().length()-m.getHorarealiz().length()+3-tick, 
     				 burbuja.getText().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE );
   			ss.setSpan( new AlignmentSpan.Standard(Alignment.ALIGN_OPPOSITE), 
     				 burbuja.getText().length()-rmte-m.getRmte().length()-m.getHorarealiz().length()+3-tick, 
     				 burbuja.getText().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE );
   			ss.setSpan( new ForegroundColorSpan(Color.DKGRAY), 
      				 burbuja.getText().length()-m.getHorarealiz().length()+3-tick, 
      				 burbuja.getText().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE );
   		 }
   		 
   		/*Log.e("BURBUJA:",""+burbuja.getText().toString());
		Log.e("BURBUJA.LENGTH",""+burbuja.getText().length());
		Log.e("RMTE","- "+rmte);
		Log.e("RMTE.LENGTH","- "+m.getRmte().length());
		Log.e("HORAREALIZ.LENGTH","- "+m.getHorarealiz().length());
		Log.e("+3","+ 3");
		Log.e("TICK","- "+tick);
		Log.e("DESDE",""+(burbuja.getText().length()-rmte-m.getRmte().length()-m.getHorarealiz().length()+3-tick));
		Log.e("HASTA",""+(burbuja.getText().length()));*/
		else{
   		 ss.setSpan( new RelativeSizeSpan(0.8f), burbuja.getText().length()-m.getHorarealiz().length()+3-tick, 
  				 burbuja.getText().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE );
   		ss.setSpan( new AlignmentSpan.Standard(Alignment.ALIGN_OPPOSITE), 
 				 burbuja.getText().length()-m.getHorarealiz().length()+3-tick, 
 				 burbuja.getText().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE );
   		ss.setSpan( new ForegroundColorSpan(Color.LTGRAY), 
  				 burbuja.getText().length()-m.getHorarealiz().length()+3-tick, 
  				 burbuja.getText().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE );
		}
   		 
   		 
   		if(m.getTipomsj().equals("A") && m.getEstado().equals("F") && m.getClienteglobal()==sharedPrefs.getInt("id",0))
   			ss.setSpan( new ForegroundColorSpan(Color.BLUE), 
   					burbuja.getText().length()-tick, 
     				burbuja.getText().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE );
   		else
   			envids+=m.getMidbd()+",";
   		ss.setSpan( new StyleSpan(android.graphics.Typeface.BOLD), 
					burbuja.getText().length()-rmte-tick, 
 				burbuja.getText().length()-rmte, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
   		 burbuja.setText(ss);

   		 if(prevdate.equals("")){ prevdate = m.getFecharealiz();
   			 TextView spacer = new TextView(getBaseContext());
   			 spacer.setBackgroundDrawable(getResources().getDrawable(R.drawable.burbujanaranja2));
   			 try{String fecha[]=m.getFecharealiz().split("-");
   	    	 spacer.setText(fecha[2]+" "+getResources().getStringArray(R.array.mes)[Integer.parseInt(fecha[1])-1]);
   	    	}catch(Exception e){} 
   			 spacer.setLayoutParams(parspacer);
   			 spacer.setTextColor(getResources().getColor(android.R.color.black));
   			 spacer.setPadding(5, 0, 5, 0);
   			 ll.addView(spacer);
   		 }
   		 else if(!prevdate.equals(m.getFecharealiz())){ prevdate = m.getFecharealiz();
   			 TextView spacer = new TextView(getBaseContext());
   			 spacer.setBackgroundDrawable(getResources().getDrawable(R.drawable.burbujanaranja2));
   			 try{String fecha[]=m.getFecharealiz().split("-");
   	    	 spacer.setText(fecha[2]+" "+getResources().getStringArray(R.array.mes)[Integer.parseInt(fecha[1])-1]);
   	    	}catch(Exception e){}
   			 spacer.setLayoutParams(parspacer);
   			 spacer.setTextColor(getResources().getColor(android.R.color.black));
   			 spacer.setPadding(5, 0, 5, 0);
   			 ll.addView(spacer);
   		 }
   		 burbuja.setFocusable(true);
   		 burbuja.setFocusableInTouchMode(true);
   		 Linkify.addLinks(burbuja, Linkify.ALL);
   		 burbuja.setOnLongClickListener(new OnLongClickListener(){
			@Override public boolean onLongClick(View arg0) {
				final Msj m = ((Msj)burbuja.getTag());
				final Dialog d = new Dialog(Chat.this);
				d.requestWindowFeature(Window.FEATURE_NO_TITLE);
				d.setCancelable(true); d.setContentView(R.layout.dburbuja);
				if(m.getClienteglobal()!=sharedPrefs.getInt("id",0)){
					((LinearLayout)d.findViewById(R.id.btnversoloeste)).setVisibility(View.VISIBLE);
					((LinearLayout)d.findViewById(R.id.btnresponder)).setVisibility(View.VISIBLE);
				}else{
					((LinearLayout)d.findViewById(R.id.btnversoloeste)).setVisibility(View.GONE);
					((LinearLayout)d.findViewById(R.id.btnresponder)).setVisibility(View.GONE);
				}
					
				((LinearLayout)d.findViewById(R.id.btncompartir)).setOnClickListener(new OnClickListener(){
	     			@Override public void onClick(View v){ 
		     				Intent sendIntent = new Intent();
							sendIntent.setAction(Intent.ACTION_SEND);
							//sendIntent.putExtra(Intent.EXTRA_TEXT, burbuja.getText().toString());
							sendIntent.putExtra(Intent.EXTRA_TEXT, m.getMensaje());
							sendIntent.setType("text/plain");
							startActivity(Intent.createChooser(sendIntent, "Reenviar mensaje a..."));
	     				d.dismiss(); }});
				((LinearLayout)d.findViewById(R.id.btnborrarmensaje)).setOnClickListener(new OnClickListener(){
	     			@Override public void onClick(View v){ 
	     				AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
	     				adb.setTitle("Borrar mensaje").setIcon(getResources().getDrawable(R.drawable.content_discard));
	     				adb.setCancelable(false);
	     				adb.setMessage("Está a punto de borrar un mensaje del dispositivo. Es posible que " +
	     						"este mensaje reaparezca si borra los datos y los vuelve a descargar. ¿Está " +
	     						"seguro de querer borrarlo?");
	     				adb.setPositiveButton("Bórralo", new AlertDialog.OnClickListener(){
	     					@Override public void onClick(DialogInterface di, int w){
	     						if(m.getTipomsj().equals("A") && 
	     							m.getClienteglobal() == sharedPrefs.getInt("id",0))
	     							db.deleteMensajeEnviado(m);
	     						else db.deleteMensajeRecibido(m);
	     						burbuja.setVisibility(View.GONE);
	     						di.dismiss(); d.dismiss();
	     					}});
	     				adb.setNegativeButton("Cancelar", new AlertDialog.OnClickListener(){
	     					@Override public void onClick(DialogInterface di, int w){
	     						di.dismiss(); }});
	     				adb.create().show();
	     			}});
				((LinearLayout)d.findViewById(R.id.btnversoloeste)).setOnClickListener(new OnClickListener(){
	     			@Override public void onClick(View v){ 
	     				for(int i=0;i<ll.getChildCount();i++){
	     					if(ll.getChildAt(i).getClass().toString().equals(TextView.class.toString())){
	     						if(ll.getChildAt(i).getTag()==null || 
	     							((Msj)ll.getChildAt(i).getTag()).getClienteglobal()!=m.getClienteglobal()){
		     							ll.getChildAt(i).setVisibility(View.GONE);
		     							flagMensajesOcultos=1;
		     							ab.setSubtitle("Mensajes de "+m.getRmte());
	     						}
	     					}
	     				}
	     			d.dismiss();	
	     			}});
				((LinearLayout)d.findViewById(R.id.btnresponder)).setOnClickListener(new OnClickListener(){
	     			@Override public void onClick(View v){ d.dismiss();
	     			}});
				((ImageButton)d.findViewById(R.id.cancel)).setOnClickListener(new OnClickListener(){
	     			@Override public void onClick(View v){ d.dismiss(); }});
				d.show();
				return false;
		 }});
   		 ll.addView(burbuja);
   		 if(m.getTipomsj().equals("G") && m.getEstado().equals("R")
   			 && flagfocus==0){ flagfocus=1;
   			 burbuja.requestFocus(); }
   		 if(m.hashCode()==conver.get(conver.size()-1).hashCode() && flagfocus==0)
   			burbuja.requestFocus();
   		 
//   		 Guardar en la BBDD como leído
   		if(sharedPrefs.getInt("internetmode", 0)==0){
	   		 if(m.getEstado()==null ||
	   				// m.getTipomsj().equals("A") && m.getEstado().equals("F") || ???
	     			m.getTipomsj().equals("G") && m.getEstado().equals("R")){ //Replicar como leído
	     		m.setEstado("L"); db.actualizarMensajeRecibido(m);
	     		ids+=m.getMidbd()+",";
	     		/*mensaje = m;
	     		new marcarLeido().execute(); }*/
	   		 }else if(m.getMid() != sharedPrefs.getInt("id", 0)){ //Mensaje replicado para usuarioapp
	   			m.setEstado("L"); db.actualizarMensajeRecibido(m);
	   		 }
	   			 
		}
    }
   	if(sharedPrefs.getInt("internetmode", 0)==0)
   	 if(!ids.equals("") || !envids.equals("")) new marcarLeidos().execute();
	
    }
    
   /* public class marcarLeido extends AsyncTask<String, Void, Boolean> {
		 protected void onPostExecute(Boolean success){
			 if(success) Toast.makeText(getBaseContext(),
					 "Mensaje marcado como leído.",Toast.LENGTH_LONG).show();}
		 
    @Override protected Boolean doInBackground(String... arg0) {
   	 try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
 		 try{DriverManager.setLoginTimeout(20);
 			Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
 			Statement st = conn.createStatement();
			st.executeUpdate("UPDATE mensajeapp SET estado='F' WHERE id="+mensaje.getMidbd());
 			st.close();conn.close();}catch(SQLException e){e.printStackTrace(); return false;}
    	return true;}}*/
    
   	public class marcarLeidos extends AsyncTask<String, Void, Boolean> {
		 protected void onPostExecute(Boolean success){
			 if(success){
				 if(ids.split(",").length==2)
					 Toast.makeText(getBaseContext(),
							 "Mensaje nuevo, marcado como leído.",Toast.LENGTH_LONG).show();
				 else Toast.makeText(getBaseContext(),
							 "Mensajes nuevos, marcados como leídos.",Toast.LENGTH_LONG).show();
				 //Recargo para refrescar mensajes marcados/desmarcados
				 if(!ids.equals("") || !envids.equals("")){
					 Log.e("INTENT","GO");
					 /*Intent i = new Intent(Chat.this, Chat.class);
			      	 i.putExtra("eid",getIntent().getIntExtra("eid",0)); 
			      	 startActivity(i); finish();*/
				 }
			 }
	}
		 
   @Override protected Boolean doInBackground(String... arg0) {
  	 try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
		 try{DriverManager.setLoginTimeout(20);
			Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st = conn.createStatement();
			//Mensajes recibidos leídos por mí
			if(!ids.equals("")){ Log.e("CHECK","RECIBIDOS");
				st.executeUpdate("UPDATE mensajeapp SET estado='F' WHERE id IN ("+ids.substring(0,ids.length()-1)+")");
				st.close(); }
			
			//Mensajes enviados leídos por el Establecimiento
			if(!envids.equals("")){ Log.e("CHECK","ENVIADOS");
			ResultSet rs = st.executeQuery("SELECT idmensajeappmovil FROM mensajeapp WHERE " +
					"tipomensaje='A' AND estado='F' AND id IN("+envids.substring(0,envids.length()-1)+")");
			while(rs.next()){
				Msj m = db.getMensajeEnv(rs.getInt(1));
				m.setEstado("L"); db.actualizarMensajeEnviado(m); }
			rs.close(); st.close(); }
			
			conn.close();}catch(SQLException e){e.printStackTrace(); return false;}
   	return true;}}
   	 
    public boolean isNumeric(String s){
    	try{ Double.parseDouble(s); }catch(Exception e){ return false; }
    	return true; }    
    
    private class EnviarMensaje extends AsyncTask<String, Void, Boolean> {    	
      	ProgressDialog loading; 
      	int midbd=0; 
      	String tocho;
      	
    	protected void onPreExecute() {
			tocho = cuerpo.getText().toString();
         	loading = new ProgressDialog(Chat.this);
         	loading.setMessage("Enviando mensaje...");
         	loading.setCancelable(false); loading.show(); 
        }

        protected void onPostExecute(final Boolean success) {
        	Msj mensaje = new Msj(db.getLastMidEnv()+1,sharedPrefs.getInt("id",0),
    					tocho,files.getText().toString(),new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),
    					new SimpleDateFormat("HH:mm:ss",spanish).format(new Date()),"",
    					e.getEid(),"A","","","","","E",
    					sharedPrefs.getString("seudonimo",sharedPrefs.getString("nombre","")),midbd,"");
        	if(sharedPrefs.getBoolean("ms",false)==true) popupWindow.dismiss();
        	db.almacenarMensajeEnviado(mensaje);
        	if (loading.isShowing()) {
        		loading.dismiss();
        		if(emailenviado==1){
        			if(flagmodoadjunto==1)
        				Toast.makeText(getBaseContext(), "E-mail con adjuntos enviado", Toast.LENGTH_LONG).show();
        			if(db.mensajePendiente() != null) db.deleteMensajeEnviado(db.mensajePendiente());
        		}
        		/*Intent intent = new Intent(Chat.this, Conversaciones.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent); finish();*/
        	}
        	/*Log.e("INTENTO","SABER");
			new checkEnvio().execute();
			Log.e("DONDE","ESTOY");*/
        	Intent intent = new Intent(Chat.this, Conversaciones.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); finish();
        }
         	
      	@Override protected Boolean doInBackground(String... arg0) {
      		try{ Class.forName("org.postgresql.Driver");
      		}catch(ClassNotFoundException e){ e.printStackTrace(); }
    		try{ DriverManager.setLoginTimeout(20);
    			//XXX Condición si envía por e-mail
    			//XXX Envío por correo
	  			/*Automailer am = new Automailer(getString(R.string.userauto),getString(R.string.passauto));
	  			String[] mail = {"",""};
	  			if(files != null) mail[0] = e.getMail();
	  			if(e.getMsjmail()!=null ) mail[1] = e.getMsjmail();
	  			am.setTo(mail);
	  			if(sharedPrefs.getString("mail","").equals(""))
	  				am.setFrom(getString(R.string.userauto));
	  			else am.setFrom(sharedPrefs.getString("mail",""));
	  			am.setSubject("Mensaje de "+
	  				sharedPrefs.getString("seudonimo", sharedPrefs.getString("nombre", ""))+
	  				" para "+e.getNombre());
	  			am.setBody(tocho);*/
	  			//Adjuntos van en la condición posterior
    			
    				if(!ficheros.isEmpty()){
    					if(flagmodoadjunto==2){
    					tocho+="\n\n Se han subido "+ficheros.size()+
								" archivos a la nube: "+nombreCarpeta;
    					/*tocho+="\n\n Puede ver los archivos en el siguiente enlace:\nhttps://drive.google.com/folder/d/"+
								SharedPreferencesController.getResourceID(getBaseContext())+"/edit\n";*/
    					//https://drive.google.com/folderview?id=0B8D4h5KtxirNcGNKVXRLWUlVLVk&usp=sharing
    					tocho+="\n\n Puede ver los archivos en el siguiente enlace:\nhttps://drive.google.com/folderview?id="+
								SharedPreferencesController.getResourceFolder(getBaseContext(),nombreCarpeta)+"&usp=sharing\n";
    					for(String s : ficheros){
    						float size = new File(s).length(); int div=0; String medida="";
    						while(size>1024){ size=size/1024; div++;}
    						switch (div){
    							case 1: medida="KB"; break; case 2: medida="MB"; break;
    							case 3: medida="GB"; break; default: medida="B";
    						}
    						tocho+="\n\t ·"+s.substring(s.lastIndexOf("/")+1)+"("+String.format("%.2f",size)+medida+")";
    					}
    					}else if(flagmodoadjunto==1){
    						tocho+="\n\n Se han adjuntado "+ficheros.size()+
    								" archivos al correo enviado.\n";
    						for(String s : ficheros){
        						float size = new File(s).length(); int div=0; String medida="";
        						while(size>1024){ size=size/1024; div++;}
        						switch (div){
        							case 1: medida="KB"; break; case 2: medida="MB"; break;
        							case 3: medida="GB"; break; default: medida="B";
        						}
        						tocho+="\n\t ·"+s.substring(s.lastIndexOf("/")+1)+"("+String.format("%.2f",size)+medida+")";
    					}
    						//XXX Envío por correo (Sin condición, sólo si hay archivos)
    			  			Automailer am = new Automailer(getString(R.string.userauto),getString(R.string.passauto));
    			  			String[] mail = {"",""};
    			  			if(files != null) mail[0] = e.getMail();
    			  			if(e.getMsjmail()!=null ) mail[1] = e.getMsjmail();
    			  			am.setTo(mail);
    			  			if(sharedPrefs.getString("mail","").equals(""))
    			  				am.setFrom(getString(R.string.userauto));
    			  			else am.setFrom(sharedPrefs.getString("mail",""));
    			  			am.setSubject("Mensaje de "+
    			  				sharedPrefs.getString("seudonimo", sharedPrefs.getString("nombre", ""))+
    			  				" para "+e.getNombre());
    			  			am.setBody(tocho);
    			  			//Adjuntos
    			  			try{
    			  				for(String s : ficheros) am.addAttachment(s);
    			  			  //else am.addAttachment(files.getText().toString());
    			  			if(am.send()) emailenviado=1;
    			  		}catch (Exception e1) {e1.printStackTrace();}
    					}
    				}
    				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
    				Statement st = conn.createStatement();
    				ResultSet rs = st.executeQuery("INSERT INTO mensajeapp (clienteglobal,mensaje," +
    					"fecharealizacion,horarealizacion,idestablecimiento,tipomensaje,estado," +
    					"idmensajeappmovil,remitente) VALUES("+sharedPrefs.getInt("id",0)+",'"+tocho+"','"+
    					new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date())+"','"+
    					new SimpleDateFormat("HH:mm:ss",spanish).format(new Date())+"',"+
    					e.getEid()+",'A','E',"+(db.getLastMidEnv()+1)+",'"+
    					sharedPrefs.getString("seudonimo",sharedPrefs.getString("nombre",""))+"')" +
    					" RETURNING id");
    				if(rs.next()) midbd=rs.getInt(1);
    				rs.close(); st.close(); conn.close();
    		}catch (SQLException e){ e.printStackTrace(); }
    		return true; 
      	}
    }

    protected void onStart() { super.onStart(); }
    
    /*
     *
     * 	METODOS GOOGLE DRIVE CALLBACK
     * 
     */
    
	@Override public void onConnected(String callbackInfo) {
		//new consulta().execute();
	}

	@Override public void onDisconnect(String callbackInfo) {	}

	@Override public void onFileUploaded(String callbackInfo) {
		Toast.makeText(getBaseContext(), "Fichero subido con éxito: "+callbackInfo, Toast.LENGTH_SHORT).show();
		Log.e("SUBIDO",callbackInfo);
		uploadedFiles.add(callbackInfo);
		uploadNextFile();
	}

	@Override public void onErrorFileUpload(String callbackInfo) {
		Toast.makeText(getBaseContext(), callbackInfo, Toast.LENGTH_LONG).show();
		Log.e("FALLADO",callbackInfo);
		uploadNextFile();
	}

	@Override public void onLocalFileError(String callbackInfo) {
		Toast.makeText(getBaseContext(), callbackInfo, Toast.LENGTH_LONG).show();
		uploadNextFile();
	}

	@Override public void onFinishUploads(String callbackInfo) { }

	@Override public void onStatusChange(String callbackInfo) { }

	@Override public void onBusinessFolderCreated(String callbackInfo) {
		//try{conectaGD.dismiss();}catch(Exception e){e.printStackTrace();}
		Toast.makeText(getBaseContext(), "Ya puede enviar archivos a la asesoría "+e.getNombre()+
				" a través de su Google Drive.", Toast.LENGTH_LONG).show();
		SharedPreferences.Editor spe = sharedPrefs.edit();
    	spe.putString("cuentagoogle",googleDriveController.getAccountName()).commit();
    	//Log.e("CUENTA",googleDriveController.getAccountName());
	}

	@Override public void onFolderConnected(String callbackInfo) { 
		Toast.makeText(getBaseContext(), "Ya puede enviar archivos a la asesoría "+e.getNombre()+
				" a través de su Google Drive.", Toast.LENGTH_LONG).show();
		SharedPreferences.Editor spe = sharedPrefs.edit();
    	spe.putString("cuentagoogle",googleDriveController.getAccountName()).commit();
	}
	
	/*
	 * 		PRIVATE METHODS
	 */
	
	/**
	 * Genera el XML del programa de gestion
	 * @return XML Formateado
	 */
	
	private String generateXML() {
		String fin ="\n\r";
		String xml="<xml>"+fin+"<version>1</version>"+fin;
		if(Build.VERSION.SDK_INT>=19)
			xml+="<uri>"+docUri+"</uri>"+fin;
		if(empresa.getText().toString()!=null && !empresa.getText().toString().equals(""))
			xml+="<empresa>"+empresa.getText().toString()+"</empresa>"+fin;
		if(fecha.getText().toString()!=null && !fecha.getText().toString().equals(""))
			xml+="<fecha>"+fecha.getText().toString()+"</fecha>"+fin;
		if(factura.getText().toString()!=null && !factura.getText().toString().equals(""))
			xml+="<factura>"+factura.getText().toString()+"</factura>"+fin;
		if(nif.getText().toString()!=null && !nif.getText().toString().equals(""))
			xml+="<nif>"+nif.getText().toString()+"</nif>"+fin;
		if(total.getText().toString()!=null && !total.getText().toString().equals(""))
			xml+="<total>"+total.getText().toString()+"</total>"+fin;
		if(iva0.getText().toString()!=null && !iva0.getText().toString().equals(""))
			xml+="<iva0>"+iva0.getText().toString()+"</iva0>"+fin;
		if(iva4.getText().toString()!=null && !iva4.getText().toString().equals(""))
			xml+="<iva4>"+iva0.getText().toString()+"</iva4>"+fin;
		if(iva10.getText().toString()!=null && !iva10.getText().toString().equals(""))
			xml+="<iva10>"+iva0.getText().toString()+"</iva10>"+fin;
		if(iva21.getText().toString()!=null && !iva21.getText().toString().equals(""))
			xml+="<iva21>"+iva0.getText().toString()+"</iva21>"+fin;
		if(iva99.getText().toString()!=null && !iva99.getText().toString().equals(""))
			xml+="<iva99>"+iva0.getText().toString()+"</iva99>"+fin;
		if(igic0.getText().toString()!=null && !igic0.getText().toString().equals(""))
			xml+="<igic0>"+igic0.getText().toString()+"</igic0>"+fin;
		if(igic3.getText().toString()!=null && !igic3.getText().toString().equals(""))
			xml+="<igic3>"+igic3.getText().toString()+"</igic3>"+fin;
		if(igic7.getText().toString()!=null && !igic7.getText().toString().equals(""))
			xml+="<igic7>"+igic7.getText().toString()+"</igic7>"+fin;
		if(igic9.getText().toString()!=null && !igic9.getText().toString().equals(""))
			xml+="<igic9>"+igic9.getText().toString()+"</igic9>"+fin;
		if(igic13.getText().toString()!=null && !igic13.getText().toString().equals(""))
			xml+="<igic13>"+igic13.getText().toString()+"</igic13>"+fin;
		if(igic20.getText().toString()!=null && !igic20.getText().toString().equals(""))
			xml+="<igic20>"+igic20.getText().toString()+"</igic20>"+fin;
		if(igic99.getText().toString()!=null && !igic99.getText().toString().equals(""))
			xml+="<igic99>"+igic99.getText().toString()+"</igic99>"+fin;
		if(recargo.getText().toString()!=null && !recargo.getText().toString().equals(""))
			xml+="<recargo>"+recargo.getText().toString()+"</recargo>"+fin;
		if(retencion.getText().toString()!=null && !retencion.getText().toString().equals(""))
			xml+="<retencion>"+retencion.getText().toString()+"</retencion>"+fin;
		if(implicito.getText().toString()!=null && !implicito.getText().toString().equals(""))
			xml+="<implicito>"+implicito.getText().toString()+"</implicito>"+fin;
		if(cuentaContrapartida.getText().toString()!=null && !cuentaContrapartida.getText().toString().equals(""))
			xml+="<cuentacontrapartida>"+cuentaContrapartida.getText().toString()+"</cuentacontrapartida>"+fin;
		if(importeContrapartida.getText().toString()!=null && !importeContrapartida.getText().toString().equals(""))
			xml+="<importecontrapartida>"+cuentaContrapartida.getText().toString()+"</importecontrapartida>"+fin;
		if(cuentaCobro.getText().toString()!=null && !cuentaCobro.getText().toString().equals(""))
			xml+="<cuentacobro>"+cuentaCobro.getText().toString()+"</cuentacobro>"+fin;
		if(cuentaPago.getText().toString()!=null && !cuentaPago.getText().toString().equals(""))
			xml+="<cuentapago>"+cuentaPago.getText().toString()+"</cuentapago>"+fin;	
		if(importeCobro.getText().toString()!=null && !importeCobro.getText().toString().equals(""))
			xml+="<importecobro>"+importeCobro.getText().toString()+"</importecobro>"+fin;	
		if(importePago.getText().toString()!=null && !importePago.getText().toString().equals(""))
			xml+="<importepago>"+importePago.getText().toString()+"</importepago>"+fin;
		if(importePago.getText().toString()!=null && !importePago.getText().toString().equals(""))
			xml+="<importepago>"+importePago.getText().toString()+"</importepago>"+fin;
		if(cobrado.getText().toString()!=null && !cobrado.getText().toString().equals(""))
			xml+="<cobrado>"+cobrado.getText().toString()+"</cobrado>"+fin;
		if(pagado.getText().toString()!=null && !pagado.getText().toString().equals(""))
			xml+="<pagado>"+pagado.getText().toString()+"</pagado>"+fin;
		if(efectiva.getText().toString()!=null && !efectiva.getText().toString().equals(""))
			xml+="<efectiva>"+efectiva.getText().toString()+"</efectiva>"+fin;
		if(modo.getText().toString()!=null && !modo.getText().toString().equals(""))
			xml+="<modo>"+modo.getText().toString()+"</modo>"+fin;
		if(descripcion.getText().toString()!=null && !descripcion.getText().toString().equals(""))
			xml+="<descripcion>"+descripcion.getText().toString()+"</descripcion>"+fin;
		if(aviso.getText().toString()!=null && !aviso.getText().toString().equals(""))
			xml+="<aviso>"+aviso.getText().toString()+"</aviso>"+fin;
		xml+="</xml>"+fin;
		//Log.e("XML",xml);
		return xml;
	}
	
	/**
	 * Sube el siguiente fichero que quede en el array de memoria
	 */

	private void addPendingFile(String filename){
		String pendingFileURI = getExternalCacheDir()+File.separator+"pendingfiles.distarea";
		File f = new File(pendingFileURI);
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(pendingFileURI, true));
			output.append(filename+"\n");
			output.close();
		} catch (FileNotFoundException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();}	
	}
	
	private void addElementToAdjuntos(String fileName){
		try{
			final LinearLayout ll = new LinearLayout(getBaseContext());
			final ImageButton ib = new ImageButton(getBaseContext());
			final TextView tv = new TextView(getBaseContext());
			ll.setOrientation(LinearLayout.HORIZONTAL);
			ib.setImageDrawable(getResources().getDrawable(R.drawable.navigation_cancel));
			ib.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
			tv.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Large);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
			tv.setLayoutParams(param);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			tv.setText(fileName);
			tv.setClickable(true);					
			tv.setOnClickListener(new OnClickListener(){ @Override public void onClick(View arg0) {				
					ventanaXML(ll,ib,tv,true,getExternalCacheDir()+File.separator+""+tv.getText().toString(),0);
			}});
			ib.setOnClickListener(new OnClickListener(){ public void onClick(View v){
					ladjuntos.removeView(ladjuntos.findViewWithTag(tv.getText().toString()));
					removePendingFileByName(tv.getText().toString());
					removePendingFileByName(tv.getText().toString().replaceAll(".jpg", ".xml"));
					if(ladjuntos.getChildCount()==0){ adjuntos.setVisibility(View.GONE);
					menu.clear();
					MenuInflater inflater = getMenuInflater();
					inflater.inflate(R.menu.chat, menu);
					flagmodoadjunto=0; }
				}
			});
			ll.addView(ib); 
			ll.addView(tv); 
			ll.setTag(tv.getText().toString());			
			ladjuntos.addView(ll);
		}
		catch(Exception ex){ }
	}
	
	private void showPendingFileChooserDialog(){
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 LinearLayout ll = new LinearLayout(getBaseContext());
		 ll.setOrientation(LinearLayout.VERTICAL);
		 ll.setBackgroundColor(Color.WHITE);
		 ScrollView scroll = new ScrollView(getBaseContext());
		 scroll.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		 scroll.addView(ll);
		 ArrayList<String> pendingFiles = pendingFilesToArrayList();
		 if(pendingFiles.size()==0){
			 TextView noFilesToAdd = new TextView(getBaseContext());
			 noFilesToAdd.setText(getResources().getString(R.string.theresNoFilesToAdd));
			 noFilesToAdd.setTextColor(getResources().getColor(R.color.azulDisoft1));
			 noFilesToAdd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			 ll.addView(noFilesToAdd);
		 }
		 final ArrayList<String> pendingFilesAdded = new ArrayList<String>();
		 for(int j = pendingFiles.size() - 1; j >= 0; j--){
			 try{
				 String fileURI = pendingFiles.get(j);
				 if(!existFileInArray(fileURI)){
					 LinearLayout tempLinearLayout = new LinearLayout(getBaseContext());
					 final TextView tempTextView = new TextView(getBaseContext());
					 CheckBox tempCheckBox = new CheckBox(getBaseContext());
					 tempCheckBox.setOnClickListener(new OnClickListener(){				 
				        @Override
				        public void onClick(View v) {
				            if(((CheckBox)v).isChecked()){
				            	pendingFilesAdded.add(tempTextView.getText().toString());
				            }else{
				            	pendingFilesAdded.remove(tempTextView.getText().toString());
				            }
				        }
					 });
					 
					 tempLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
					 tempTextView.setVisibility(View.GONE);			 
					 tempTextView.setText(fileURI);	
					 tempCheckBox.setTextColor(getResources().getColor(R.color.azulDisoft1));
					 tempCheckBox.setText(fileURI.split(File.separator)[fileURI.split(File.separator).length-1]);			 
					 tempLinearLayout.addView(tempCheckBox);
					 tempLinearLayout.addView(tempTextView);
					 ll.addView(tempLinearLayout);
				 }
			 }
			 catch(Exception ex){ ex.printStackTrace(); }			 
		 }
		 builder.setPositiveButton("Recuperar",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                for(String s : pendingFilesAdded){
                	if(!existFileInArray(s)){
                		ficheros.add(s);
                    	String fileName = s.split(File.separator)[s.split(File.separator).length-1];
                    	if(!fileName.contains(".xml")){
                    		addElementToAdjuntos(fileName);
                    		adjuntos.setVisibility(View.VISIBLE);
                    	}
                	}
                }
                
                if(flagmodoadjunto==0){
					menu.clear();
					MenuInflater inflater = getMenuInflater();
					inflater.inflate(R.menu.chatofp, menu);
					flagmodoadjunto=2; }
            }
	     });
		 builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
	     });
		 builder.setTitle("Recuperar pendientes");
		 builder.setView(scroll);		 
		 builder.create().show();
	}
	
	private void removeCachedFile(ArrayList<String> filesToRemove){		
		try {
			File tempFile = new File(getExternalCacheDir()+File.separator+"temp"+(System.currentTimeMillis()));
			BufferedReader bufferedReader = new BufferedReader(new FileReader(getExternalCacheDir()+File.separator+FILE_CACHE_NAME));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			String content = "";
			while((content = bufferedReader.readLine())!=null){
				if(!filesToRemove.contains(content)){
					writer.append(content+"\n");
				}
			}
			bufferedReader.close();
			writer.close();
			tempFile.renameTo(new File(getExternalCacheDir()+File.separator+FILE_CACHE_NAME));
		} catch (FileNotFoundException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	private boolean existFileInArray(String fileURI){
		return ficheros.contains(fileURI);
	}
	
	private void uploadNextFile() {		
		if(!ficheros.isEmpty()){
			uploadedFiles.add(ficheros.get(0));
			Log.e("RETIRANDO",ficheros.get(0));
			ficheros.remove(0);
		}
		if(!ficheros.isEmpty()){
			Log.e("SIGUIENTE",ficheros.get(0));
			if(googleDriveController == null){ Log.e("GDC","null");
			googleDriveController = new GoogleDriveController(Chat.this,Chat.this,nombreCarpeta);
			googleDriveController.connect();}
			else Log.e("GDC",googleDriveController.isConnected()+"");
			if(ficheros.get(0).contains(".jpg")){
				googleDriveController.uploadFile(new File(ficheros.get(0)),ficheros.get(0).split("/")[ficheros.get(0).split("/").length-1], "image/jpg");				
			}else if(ficheros.get(0).contains(".xml")){
				googleDriveController.uploadFile(new File(ficheros.get(0)),ficheros.get(0).split("/")[ficheros.get(0).split("/").length-1], "text/html");				
			}else						
				googleDriveController.uploadFile(new File(ficheros.get(0)),ficheros.get(0).split("/")[ficheros.get(0).split("/").length-1], "application/vnd.google-apps.file");
		}
		else{
			Log.e("FINALIZADO",""+uploadedFiles.size());
			removeCachedFile(uploadedFiles); //Tiene el doble de lo subido, no sé el por qué
			Log.e("FINALIZADO",""+uploadedFiles.size());
			uploadedFiles.clear();
		}
	}
	
	private static String appendReferenceNumberToUri(String referencia){
		String uri="";
		int ref=0;
		try{ ref = Integer.parseInt(referencia); }
		catch(Exception e){}
			if(ref<10)
				uri = "00"+ref;
			else if(ref<100)
				uri = "0"+ref;
			else uri = ""+ref;
		if(ref==0) return referencia;
		else 	   return uri;
	}
	
	private void removePendingFileByName(String name){
		int count = 0;
		boolean encontrado = false;
		for(String file: ficheros){		
			if(file.contains(name)){	
				encontrado = true;
				break;
			}
			count++;
		}
		if(encontrado) ficheros.remove(count);
	}
	
	private ArrayList<String> pendingFilesToArrayList(){
		String pendingFileURI = getExternalCacheDir()+File.separator+"pendingfiles.distarea";
		ArrayList<String> pendingFiles = new ArrayList<String>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(pendingFileURI));
			String s = "";
			while((s = bufferedReader.readLine()) != null){
				pendingFiles.add(s);
			} bufferedReader.close();			
		} catch (FileNotFoundException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace();
		} return pendingFiles;
	}
	
	private void leerXML(String xmlURI){
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(xmlURI));
			String thisLine = "";    
			while ((thisLine = bufferedReader.readLine()) != null) {
			   if(thisLine.contains("<empresa>")){
				   thisLine = thisLine.replaceAll("<empresa>","");
				   thisLine = thisLine.replaceAll("</empresa>","");
				   empresa.setText(thisLine);
				   lineaEmpresa.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<factura>")){
				   thisLine = thisLine.replaceAll("<factura>","");
				   thisLine = thisLine.replaceAll("</factura>","");
				   factura.setText(thisLine);
				   lineaFactura.setVisibility(View.VISIBLE);
			   }
			   else if(thisLine.contains("<total>")){
				   thisLine = thisLine.replaceAll("<total>","");
				   thisLine = thisLine.replaceAll("</total>","");
				   total.setText(thisLine);
				   lineaTotal.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<fecha>")){
				   thisLine = thisLine.replaceAll("<fecha>","");
				   thisLine = thisLine.replaceAll("</fecha>","");
				   fecha.setText(thisLine);
				   lineaFecha.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<igic0>")){
				   thisLine = thisLine.replaceAll("<igic0>","");
				   thisLine = thisLine.replaceAll("</igic0>","");
				   igic0.setText(thisLine);
				   lineaIGIC0.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<igic3>")){
				   thisLine = thisLine.replaceAll("<igic3>","");
				   thisLine = thisLine.replaceAll("</igic3>","");
				   igic3.setText(thisLine);
				   lineaIGIC3.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<igic7>")){
				   thisLine = thisLine.replaceAll("<igic7>","");
				   thisLine = thisLine.replaceAll("</igic7>","");
				   igic7.setText(thisLine);
				   lineaIGIC7.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<igic9>")){
				   thisLine = thisLine.replaceAll("<igic9>","");
				   thisLine = thisLine.replaceAll("</igic9>","");
				   igic9.setText(thisLine);
				   lineaIGIC9.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<igic13>")){
				   thisLine = thisLine.replaceAll("<igic13>","");
				   thisLine = thisLine.replaceAll("</igic13>","");
				   igic13.setText(thisLine);
				   lineaIGIC13.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<igic20>")){
				   thisLine = thisLine.replaceAll("<igic20>","");
				   thisLine = thisLine.replaceAll("</igic20>","");
				   igic20.setText(thisLine);
				   lineaIGIC20.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<igic99>")){
				   thisLine = thisLine.replaceAll("<igic99>","");
				   thisLine = thisLine.replaceAll("</igic99>","");
				   igic99.setText(thisLine);
				   lineaIGIC99.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<iva0>")){
				   thisLine = thisLine.replaceAll("<iva0>","");
				   thisLine = thisLine.replaceAll("</iva0>","");
				   iva0.setText(thisLine);
				   lineaIVA0.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<iva4>")){
				   thisLine = thisLine.replaceAll("<iva4>","");
				   thisLine = thisLine.replaceAll("</iva4>","");
				   iva4.setText(thisLine);
				   lineaIVA4.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<iva10>")){
				   thisLine = thisLine.replaceAll("<iva10>","");
				   thisLine = thisLine.replaceAll("</iva10>","");
				   iva10.setText(thisLine);
				   lineaIVA10.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<iva21>")){
				   thisLine = thisLine.replaceAll("<iva21>","");
				   thisLine = thisLine.replaceAll("</iva21>","");
				   iva21.setText(thisLine);
				   lineaIVA21.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<iva99>")){
				   thisLine = thisLine.replaceAll("<iva99>","");
				   thisLine = thisLine.replaceAll("</iva99>","");
				   iva99.setText(thisLine);
				   lineaIVA99.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<nif>")){
				   thisLine = thisLine.replaceAll("<nif>","");
				   thisLine = thisLine.replaceAll("</nif>","");
				   nif.setText(thisLine);
				   lineaNIF.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<cuentacontrapartida>")){
				   thisLine = thisLine.replaceAll("<cuentacontrapartida>","");
				   thisLine = thisLine.replaceAll("</cuentacontrapartida>","");
				   cuentaContrapartida.setText(thisLine);
				   lineaCuentaContrapartida.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<cuentacobro>")){
				   thisLine = thisLine.replaceAll("<cuentacobro>","");
				   thisLine = thisLine.replaceAll("</cuentacobro>","");
				   cuentaCobro.setText(thisLine);
				   lineaCuentaCobro.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<importecontrapartida>")){
				   thisLine = thisLine.replaceAll("<importecontrapartida>","");
				   thisLine = thisLine.replaceAll("</importecontrapartida>","");
				   importeContrapartida.setText(thisLine);
				   lineaImporteContrapartida.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<importecobro>")){
				   thisLine = thisLine.replaceAll("<importecobro>","");
				   thisLine = thisLine.replaceAll("</importecobro>","");
				   importeCobro.setText(thisLine);
				   lineaImporteCobro.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<importepago>")){
				   thisLine = thisLine.replaceAll("<importepago>","");
				   thisLine = thisLine.replaceAll("</importepago>","");
				   importePago.setText(thisLine);
				   lineaImportePago.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<cobrado>")){
				   thisLine = thisLine.replaceAll("<cobrado>","");
				   thisLine = thisLine.replaceAll("</cobrado>","");
				   cobrado.setText(thisLine);
				   lineaCobrado.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<pagado>")){
				   thisLine = thisLine.replaceAll("<pagado>","");
				   thisLine = thisLine.replaceAll("</pagado>","");
				   pagado.setText(thisLine);
				   lineaPagado.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<recargo>")){
				   thisLine = thisLine.replaceAll("<recargo>","");
				   thisLine = thisLine.replaceAll("</recargo>","");
				   recargo.setText(thisLine);
				   lineaRecargo.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<retencion>")){
				   thisLine = thisLine.replaceAll("<retencion>","");
				   thisLine = thisLine.replaceAll("</retencion>","");
				   retencion.setText(thisLine);
				   lineaRetencion.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<implicito>")){
				   thisLine = thisLine.replaceAll("<implicito>","");
				   thisLine = thisLine.replaceAll("</implicito>","");
				   implicito.setText(thisLine);
				   lineaImplicito.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<aviso>")){
				   thisLine = thisLine.replaceAll("<aviso>","");
				   thisLine = thisLine.replaceAll("</aviso>","");
				   aviso.setText(thisLine);
				   lineaAviso.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<modo>")){
				   thisLine = thisLine.replaceAll("<modo>","");
				   thisLine = thisLine.replaceAll("</modo>","");
				   modo.setText(thisLine);
				   lineaModo.setVisibility(View.VISIBLE);
			   }else if(thisLine.contains("<descripcion>")){
				   thisLine = thisLine.replaceAll("<descripcion>","");
				   thisLine = thisLine.replaceAll("</descripcion>","");
				   descripcion.setText(thisLine);
				   lineaDescripcion.setVisibility(View.VISIBLE);
			   }
			}
		} catch (IOException e) { e.printStackTrace(); } 
	}

	private void writeFieldsViaVoice(ArrayList<String> text) {
		Table table = sptl.load();
		SharedPreferences sp = getSharedPreferences(sptl.KEY,0);
		String [] mensaje;
		String contenido = "";
		String contenidoAviso = "";
		if(text.get(0).toUpperCase().contains("AVISO") || text.get(0).toUpperCase().contains("AVISÓ")){
			mensaje = StringTranslator.getAviso(text.get(0));
			if(mensaje.length==2){
				contenido = mensaje[0];
				contenidoAviso = mensaje[1];
			}
			else{
				contenido = text.get(0);
				contenidoAviso = "";
			}
		}else{
			contenido = text.get(0);
			contenidoAviso = "";
		}
		if(!contenidoAviso.equals("")){
			aviso.setText(aviso.getText().toString()+" "+contenidoAviso);
			lineaAviso.setVisibility(View.VISIBLE);
		}
		Map<String,String> map = StringTranslator.getStringMapped(contenido, table);
		//Log.e("VOZ",text.get(0));
		try{
			if(map.containsKey("EMPRESA")) {
				oriempresa = map.get("EMPRESA");
				sp = getSharedPreferences(sptl.KEY+".empresa",0);
				if(!sp.getString(map.get("EMPRESA"), "").equals("") &&
						sp.getString(map.get("EMPRESA"), "")!=null){
					dicempresa=sp.getString(map.get("EMPRESA"),"");
					//empresa.setText(""); empresa.append(dicempresa);
					empresa.setText(dicempresa);
					btnrevert.setVisibility(View.VISIBLE);
					btnrevert.setOnClickListener(new OnClickListener(){
						@Override public void onClick(View v){
							if(rev){ rev=false;
							empresa.setText(""); empresa.append(dicempresa); 
							}else{ rev=true;
							empresa.setText(""); empresa.append(oriempresa); }
						}});
				} else //empresa.setText(""); empresa.append(oriempresa);
					empresa.setText(oriempresa);
				lineaEmpresa.setVisibility(View.VISIBLE);
				
				if(!oriempresa.equals(""))
					 //Toast.makeText(getBaseContext(), "Crea onTextChanged", Toast.LENGTH_LONG).show();
					 //Mari se asustó al salirle este mensaje al Continuar fotos
						empresa.addTextChangedListener(new TextWatcher() {@Override public void 
				          onTextChanged(CharSequence s, int start, int before, int count) {
							if(s.length()==0){ btnmas.setVisibility(View.GONE);
										  btnrevert.setVisibility(View.GONE);
							}else if(s.toString().toUpperCase().equals(oriempresa.toUpperCase()) || 
									s.toString().toUpperCase().equals(dicempresa.toUpperCase())){
								btnmas.setVisibility(View.GONE);
								if(!s.equals("")) btnrevert.setVisibility(View.VISIBLE);
							}else{ btnmas.setVisibility(View.VISIBLE);
								   btnrevert.setVisibility(View.GONE);}
					}
				        @Override public void afterTextChanged(Editable arg0) {}
				        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				      });
			}					
			if(map.containsKey("FECHA")) {
				//Log.e("VOZ FECHA",map.get("FECHA"));
				fecha.setText(StringTranslator.translateDate(map.get("FECHA")));
				lineaFecha.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("FACTURA")) {
				//Log.e("VOZ FACTURA",map.get("FACTURA"));
				factura.setText(StringTranslator.translateLargeBill(map.get("FACTURA")));
				lineaFactura.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("NIF")) {
				//Log.e("VOZ NIF",map.get("NIF"));
				nif.setText(StringTranslator.translateNIF(map.get("NIF")));
				lineaNIF.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("TOTAL")) {
				//Log.e("VOZ TOTAL",map.get("TOTAL"));
				total.setText(StringTranslator.translateTotal(map.get("TOTAL")));
				lineaTotal.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IVA 0")) {
				//Log.e("VOZ IVA",map.get("IVA 0"));
				iva0.setText(StringTranslator.translateTotal(map.get("IVA 0")));
				lineaIVA0.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IVA 4")) {
				//Log.e("VOZ IVA",map.get("IVA 4"));
				iva4.setText(StringTranslator.translateTotal(map.get("IVA 4")));
				lineaIVA4.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IVA 10")) {
				//Log.e("VOZ IVA",map.get("IVA 10"));
				iva10.setText(StringTranslator.translateTotal(map.get("IVA 10")));
				lineaIVA10.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IVA 21")) {
				//Log.e("VOZ IVA",map.get("IVA 21"));
				iva21.setText(StringTranslator.translateTotal(map.get("IVA 21")));
				lineaIVA21.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IVA 99")) {
				//Log.e("VOZ IVA",map.get("IVA 99"));
				iva99.setText(StringTranslator.translateTotal(map.get("IVA 9")));
				lineaIVA99.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IGIC 0")) {
				//Log.e("VOZ IVA",map.get("IGIC 0"));
				igic0.setText(StringTranslator.translateTotal(map.get("IGIC 0")));
				lineaIGIC0.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IGIC 3")) {
				//Log.e("VOZ IVA",map.get("IGIC 3"));
				igic3.setText(StringTranslator.translateTotal(map.get("IGIC 3")));
				lineaIGIC3.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IGIC 7")) {
				//Log.e("VOZ IVA",map.get("IGIC 7"));
				igic7.setText(StringTranslator.translateTotal(map.get("IGIC 7")));
				lineaIGIC7.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IGIC 9")) {
				//Log.e("VOZ IVA",map.get("IGIC 9"));
				igic9.setText(StringTranslator.translateTotal(map.get("IGIC 9")));
				lineaIGIC9.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IGIC 13 CON 5")) {
				//Log.e("VOZ IVA",map.get("IGIC 13 CON 5"));
				igic13.setText(StringTranslator.translateTotal(map.get("IGIC 13 CON 5")));
				lineaIGIC13.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IGIC 0")) {
				//Log.e("VOZ IVA",map.get("IGIC 0"));
				igic0.setText(StringTranslator.translateTotal(map.get("IGIC 0")));
				lineaIGIC0.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IGIC 20")) {
				//Log.e("VOZ IVA",map.get("IGIC 20"));
				igic20.setText(StringTranslator.translateTotal(map.get("IGIC 20")));
				lineaIGIC20.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IGIC 99")) {
				//Log.e("VOZ IVA",map.get("IGIC 99"));
				igic99.setText(StringTranslator.translateTotal(map.get("IGIC 99")));
				lineaIGIC99.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("RECARGO")) {
				//Log.e("VOZ RECARGO",map.get("RECARGO"));
				recargo.setText(StringTranslator.translateTotal(map.get("RECARGO")+"%"));
				lineaRecargo.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("RETENCION")) {
				//Log.e("VOZ RETENCION",map.get("RETENCION"));
				retencion.setText(StringTranslator.translateTotal(map.get("RETENCION")+"%"));
				lineaRetencion.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IMPLICITO")) {
				//Log.e("VOZ IMPLICITO",map.get("IMPLICITO"));
				//implicito.setText(StringTranslator.translateTotal(map.get("IMPLICITO")+"%"));
				implicito.setText(map.get("IMPLICITO"));
				lineaImplicito.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("CUENTA CONTRAPARTIDA")) {
				//Log.e("VOZ IMPLICITO",map.get("CUENTA CONTRAPARTIDA"));
				cuentaContrapartida.setText(map.get("CUENTA CONTRAPARTIDA"));
				lineaCuentaContrapartida.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("CUENTA COBRO")) {
				//Log.e("VOZ IMPLICITO",map.get("CUENTA COBRO"));
				cuentaCobro.setText(map.get("CUENTA COBRO"));
				lineaCuentaCobro.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("CUENTA PAGO")) {
				//Log.e("VOZ IMPLICITO",map.get("CUENTA PAGO"));
				cuentaPago.setText(map.get("CUENTA PAGO"));
				lineaCuentaPago.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IMPORTE CONTRAPARTIDA")) {
				//Log.e("VOZ IMPLICITO",map.get("IMPORTE CONTRAPARTIDA"));
				importeContrapartida.setText(StringTranslator.translateTotal(map.get("IMPORTE CONTRAPARTIDA")));
				lineaImporteContrapartida.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IMPORTE COBRO")) {
				//Log.e("VOZ IMPLICITO",map.get("IMPORTE COBRO"));
				importeCobro.setText(StringTranslator.translateTotal(map.get("IMPORTE COBRO")));
				lineaImporteCobro.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("IMPORTE PAGO")) {
				//Log.e("VOZ IMPLICITO",map.get("IMPORTE PAGO"));
				importePago.setText(StringTranslator.translateTotal(map.get("IMPORTE PAGO")));
				lineaImportePago.setVisibility(View.VISIBLE);
			}	
			if(map.containsKey("COBRADO")) {
				//Log.e("VOZ IMPLICITO",map.get("COBRADO"));
				cobrado.setText(StringTranslator.translateDate(map.get("COBRADO")));
				lineaCobrado.setVisibility(View.VISIBLE);
			}	
			if(map.containsKey("PAGADO")) {
				//Log.e("VOZ IMPLICITO",map.get("PAGADO"));
				pagado.setText(StringTranslator.translateDate(map.get("PAGADO")));
				lineaPagado.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("EFECTIVA")) {
				//Log.e("VOZ IMPLICITO",map.get("EFECTIVA"));
				efectiva.setText(StringTranslator.translateDate(map.get("EFECTIVA")));
				lineaEfectiva.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("MODO")) {
				modo.setText(map.get("MODO"));
				lineaModo.setVisibility(View.VISIBLE);
			}
			if(map.containsKey("DESCRIPCION")) {
				descripcion.setText(map.get("DESCRIPCION"));
				lineaDescripcion.setVisibility(View.VISIBLE);
			}
		}catch(Exception ex){ ex.printStackTrace(); }
	}

	@SuppressLint("NewApi")
	private void ventanaXML(final LinearLayout ll, final ImageButton ib,final TextView tv,
			final boolean editmode,final String XMLUri,final int mode) {
		//Creo ventana de nombre, con desplegable y etc.
		dialog = new Dialog(Chat.this);
		dialog.setCancelable(false);
		dialog.setTitle("Adjunto con datos");
		dialog.setContentView(R.layout.dtipofoto);	  			     
		s = (Spinner)dialog.findViewById(R.id.spinner);
		preview = (ImageView)dialog.findViewById(R.id.preview);
		nombrepreliminar = (TextView)dialog.findViewById(R.id.nombrefichero);
		micytip = (LinearLayout)dialog.findViewById(R.id.micytip);
		mic = (ImageButton)dialog.findViewById(R.id.mic);
		tip = (ImageButton)dialog.findViewById(R.id.tip);
		mas = (ImageButton)dialog.findViewById(R.id.mas);
		btnfecha = (ImageButton)dialog.findViewById(R.id.btnfecha);
		btnrevert = (ImageButton)dialog.findViewById(R.id.btnrevert);
		btnmas = (ImageButton)dialog.findViewById(R.id.btnmas);
		initDialogEditTextAndLayout();
		cancelar = (Button)dialog.findViewById(R.id.cancelar);
		continuar = (Button)dialog.findViewById(R.id.continuar);		
		finalizar = (Button)dialog.findViewById(R.id.aceptar);
		//Amago
		//final String XMLURI = FileUtils.getPath(getBaseContext(),Uri.parse(XMLUri));
		if(bypass.contains("."))
			bypass = bypass.substring(0,bypass.lastIndexOf("."));
		if(mode==1) bypass = XMLUri; //path
		ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(Chat.this,R.array.tipofoto,android.R.layout.simple_spinner_item);
		adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adaptador);
		s.setSelection(1);
		//Condiciones normal
		if(mode>0){
			/*if(sharedPrefs.getInt("solicitacliest", 0)==e.getEid())
				referencia = sharedPrefs.getString("ultimareferencia","-1");
			else
				referencia = appendReferenceNumberToUri(e.getReferencia());*/
			/*if(sharedPrefs.getBoolean("solicitacli", false))
				idop = ""+sharedPrefs.getInt("solicitaclin", 0);
			else idop = "00";*/
			/*anoactual = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()); 
			idfoto = sharedPrefs.getString("idfoto", "000");*/
			/*if(millis == null || millis.equals(""))
				millis=""+sharedPrefs.getLong("lastmillis", 0);
			bypass=getExternalCacheDir()+File.separator+
					referencia+"_"+anoactual+"_"+idop+"_"+idfoto+"_"+millis;
			solofichero=referencia+"_"+anoactual+"_"+idop+"_"+idfoto+"_"+millis;*/
			Log.e("BYPASS",bypass);
			if(editmode==true)
				bypass=sharedPrefs.getString("bypass","");
			Log.e("BYPASS2",bypass);
			
			if(mode==2){
				solofichero=bypass.substring(bypass.lastIndexOf("/")+1);
				if(solofichero.contains("."))
					partes=solofichero.substring(0,solofichero.lastIndexOf(".")).split("_");
				else partes=solofichero.split("_");
			for(int i=0;i<partes.length;i++)
				Log.e("PARTES","["+i+"] = "+partes[i]);
			
			 /*bypass+=".jpg"; solofichero+=".jpg";*/ extension=".jpg"; }
			else if(mode==1){
				//Log.e("TYPE",getContentResolver().getType(Uri.parse(XMLUri)));
				//bypass+=FileUtils.getExtension(XMLUri);
				//solofichero+=FileUtils.getExtension(XMLUri); }
				//solofichero = bypass;
				//TEST (WTF repetido?)
				
				
				/*if(sharedPrefs.getInt("solicitacliest", 0)==e.getEid())
					referencia = sharedPrefs.getString("ultimareferencia","-1");
				else
					referencia = appendReferenceNumberToUri(e.getReferencia());
				if(sharedPrefs.getBoolean("solicitacli", false))
					idop = ""+sharedPrefs.getInt("solicitaclin", 0);
				else idop = "00";
				anoactual = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()); 
				idfoto = sharedPrefs.getString("idfoto", "000");
				if(millis == null || millis.equals(""))
					millis=""+sharedPrefs.getLong("lastmillis", 0);
				bypass=getExternalCacheDir()+File.separator+
						referencia+"_"+anoactual+"_"+idop+"_"+idfoto+"_"+millis;
				solofichero=referencia+"_"+anoactual+"_"+idop+"_"+idfoto+"_"+millis;*/
				solofichero=bypass.substring(bypass.lastIndexOf("/")+1);
				if(solofichero.contains(".")){
					extension=FileUtils.getExtension(solofichero);
				
					solofichero=solofichero.substring(0,solofichero.lastIndexOf("."));
				}	
				
				Log.e("SOLOFICHERO",""+solofichero);
			}
		}
		if(extension==null) extension="";
		
		 nombrepreliminar.setText(solofichero+extension);
		 	
		 fecha.addTextChangedListener(new TextWatcher() {@Override public void 
	          onTextChanged(CharSequence s, int start, int before, int count) {
			String ano=""+Calendar.getInstance().get(Calendar.YEAR);
			if(count>=4 && fecha!=null){
				 ano = fecha.getText().toString().substring(
						fecha.getText().toString().length()-4,
	    				fecha.getText().toString().length());
				if(isNumeric(ano) && partes!=null)
					nombrepreliminar.setText(partes[0]+"_"+ano+"_"+partes[2]+"_"+partes[3]+"_"+partes[4]+extension);
			}
		}
	        @Override public void afterTextChanged(Editable arg0) {}
	        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	      });
		 fecha.setOnFocusChangeListener(new OnFocusChangeListener(){ //Format control
				@Override public void onFocusChange(View v, boolean focus) {
					if(fecha.getText().toString().split("-").length!=3){
						fecha.setText(sdfdia.format(new Date()));
						Toast.makeText(getBaseContext(), "Fecha inválida, se ha tomado la del dispositivo.", Toast.LENGTH_LONG).show();
						nombrepreliminar.setText(partes[0]+"_"+anoactual+"_"+partes[2]+"_"+partes[3]+"_"+partes[4]+extension);
					}
			}});
		 Log.e("ORIEMPRESA",oriempresa);
		 if(!oriempresa.equals(""))
			 //Toast.makeText(getBaseContext(), "Crea onTextChanged", Toast.LENGTH_LONG).show();
			 //Mari se asustó al salirle este mensaje al Continuar fotos
				empresa.addTextChangedListener(new TextWatcher() {@Override public void 
		          onTextChanged(CharSequence s, int start, int before, int count) {
					if(s.length()==0){ btnmas.setVisibility(View.GONE);
								  btnrevert.setVisibility(View.GONE);
					}else if(s.toString().toUpperCase().equals(oriempresa.toUpperCase()) || 
							s.toString().toUpperCase().equals(dicempresa.toUpperCase())){
						btnmas.setVisibility(View.GONE);
						if(!s.equals("")) btnrevert.setVisibility(View.VISIBLE);
					}else{ btnmas.setVisibility(View.VISIBLE);
						   btnrevert.setVisibility(View.GONE);}
			}
		        @Override public void afterTextChanged(Editable arg0) {}
		        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		      });
		 preview.setImageDrawable(getResources().getDrawable(R.drawable.nopreviewavailable));
		 	if(editmode){
		 		if(new File(XMLUri.replaceAll(FileUtils.getExtension(XMLUri), ".xml")).exists()){
		 			//final String idfoto = XMLUri.split("_")[3];
		 			micytip.setVisibility(View.VISIBLE);
					mas.setVisibility(View.VISIBLE);
					nombrepreliminar.setText(XMLUri.substring(XMLUri.lastIndexOf("/")+1));
					fecha.addTextChangedListener(new TextWatcher() {@Override public void 
				          onTextChanged(CharSequence s, int start, int before, int count) {
						String ano="";
						if(count>=4){
							 ano = fecha.getText().toString().substring(
									fecha.getText().toString().length()-4,
				    				fecha.getText().toString().length());
							if(isNumeric(ano) && partes!=null)
								nombrepreliminar.setText(partes[0]+"_"+ano+"_"+partes[2]+"_"+partes[3]+"_"+partes[4]+extension);
						}
					}
				        @Override public void afterTextChanged(Editable arg0) {}
				        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				      });
					fecha.setOnFocusChangeListener(new OnFocusChangeListener(){ //Format control
						@Override public void onFocusChange(View v, boolean focus) {
							if(fecha.getText().toString().split("-").length!=3){
								fecha.setText(sdfdia.format(new Date()));
								Toast.makeText(getBaseContext(), "Fecha inválida, se ha tomado la del dispositivo.", Toast.LENGTH_LONG).show();
								nombrepreliminar.setText(partes[0]+"_"+anoactual+"_"+partes[2]+"_"+partes[3]+"_"+partes[4]+extension);
							}
					}});
		 		} 
		 		else nombrepreliminar.setText(solofichero+extension);
		 	//Exista o no el XML, cuando se edita:
		 	s.setVisibility(View.INVISIBLE);
		 	if(Build.VERSION.SDK_INT>=19 && mode==1){
		 		docUri="";
		 		try{FileInputStream in = new FileInputStream(XMLUri.replace(FileUtils.getExtension(XMLUri), ".xml"));
		 	    	int len = 0; byte[] data1 = new byte[1024];
		 	        while ( -1 != (len = in.read(data1)) ){
		 	        	if(new String(data1, 0, len).contains("<uri>")){
		 	        		docUri = new String(data1, 0, len);
		 	                docUri = docUri.split("uri>")[1];
		 	                docUri = docUri.substring(0,docUri.length()-2);
		 	                break; }
		 	        } in.close();
		 	    } catch (FileNotFoundException e) { e.printStackTrace(); 
		 	    } catch (IOException e) { e.printStackTrace(); }
		 		if(!docUri.equals("")){ //XXX foto?
		 			try{ExifInterface exif = new ExifInterface(XMLUri);// XMLUri
					int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			 		preview.setImageBitmap(DocumentsContract.getDocumentThumbnail(getBaseContext().getContentResolver(),
							Uri.parse(docUri),new Point(512,512),null));
			 		if(orientation == 8)//ExifInterface.ORIENTATION_ROTATE_270
						preview.setRotation(270);
				    else if(orientation == 3)//ExifInterface.ORIENTATION_ROTATE_180
				    	preview.setRotation(180);
				    else if(orientation == 6)//ExifInterface.ORIENTATION_ROTATE_90
				        preview.setRotation(90);
		 			}catch(Exception ex){ preview.setImageBitmap(DocumentsContract.getDocumentThumbnail(getBaseContext().getContentResolver(),
							Uri.parse(docUri),new Point(512,512),null)); 
		 		}}else preview.setImageDrawable(getResources().getDrawable(R.drawable.nopreviewavailable));
		 	}else{
				try{//XXX foto
					ExifInterface exif = new ExifInterface(XMLUri);
					int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
					Bitmap foto = FileUtils.getThumbnail(getBaseContext(), new File(XMLUri));
					if(foto==null){
						try{BitmapFactory.Options options = new BitmapFactory.Options();
							options.inPreferredConfig=Config.RGB_565;
							options.outMimeType="image/jpeg";
							options.inSampleSize=8;
							foto = BitmapFactory.decodeFile(XMLUri, options);
							preview.setImageBitmap(foto);
						}catch(Exception ex2){
						preview.setImageDrawable(getResources().getDrawable(R.drawable.nopreviewavailable));
					}}
					preview.setImageBitmap(foto);
					if(orientation == 8)//ExifInterface.ORIENTATION_ROTATE_270
						preview.setRotation(270);
				    else if(orientation == 3)//ExifInterface.ORIENTATION_ROTATE_180
				    	preview.setRotation(180);
				    else if(orientation == 6)//ExifInterface.ORIENTATION_ROTATE_90
				        preview.setRotation(90);
				}catch(Exception ex2){
					preview.setImageDrawable(getResources().getDrawable(R.drawable.nopreviewavailable));
				}}
		 	
		 	}else{
		 		fecha.addTextChangedListener(new TextWatcher() {@Override public void 
			          onTextChanged(CharSequence s, int start, int before, int count) {
					String ano="";
					if(count>=4){
						 ano = fecha.getText().toString().substring(
								fecha.getText().toString().length()-4,
			    				fecha.getText().toString().length());
						 if(isNumeric(ano))
							 nombrepreliminar.setText(partes[0]+"_"+ano+"_"+partes[2]+"_"+partes[3]+"_"+partes[4]+extension);
					}
				}
			        @Override public void afterTextChanged(Editable arg0) {}
			        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			      });
		 		fecha.setOnFocusChangeListener(new OnFocusChangeListener(){ //Format control
					@Override public void onFocusChange(View v, boolean focus) {
						if(fecha.getText().toString().split("-").length!=3){
							fecha.setText(sdfdia.format(new Date()));
							Toast.makeText(getBaseContext(), "Fecha inválida, se ha tomado la del dispositivo.", Toast.LENGTH_LONG).show();
							nombrepreliminar.setText(partes[0]+"_"+anoactual+"_"+partes[2]+"_"+partes[3]+"_"+partes[4]+extension);
						}
				}});
		 		//if(mode==1){
		 		/*if(Build.VERSION.SDK_INT>=19 && mode==1){ //XXX foto?
		 		
		 			Log.e("XMLURI",""+XMLUri);
		 			Log.e("ORIGINAL",""+FileUtils.getPath(getBaseContext(),Uri.parse(docUri)));
		 			//try{ExifInterface exif = new ExifInterface(FileUtils.getPath(getBaseContext(),Uri.parse(docUri))); //XMLUri
		 			try{ExifInterface exif = new ExifInterface(XMLUri);
					int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			 		preview.setImageBitmap(DocumentsContract.getDocumentThumbnail(getBaseContext().getContentResolver(),
							Uri.parse(XMLUri),new Point(512,512),null));
			 		
					/*preview.setImageBitmap(DocumentsContract.getDocumentThumbnail(getBaseContext().getContentResolver(),
							Uri.parse(bypass),new Point(512,512),null));/
			 		if(orientation == 8)//ExifInterface.ORIENTATION_ROTATE_270
						preview.setRotation(270);
				    else if(orientation == 3)//ExifInterface.ORIENTATION_ROTATE_180
				    	preview.setRotation(180);
				    else if(orientation == 6)//ExifInterface.ORIENTATION_ROTATE_90
				        preview.setRotation(90);
			 		Log.e("FINAL","TODO \"OK\"");
		 			}catch(Exception ex){ 
		 				Log.e("EXIF","NULL");
		 				ex.printStackTrace();
		 				preview.setImageBitmap(DocumentsContract.getDocumentThumbnail(getBaseContext().getContentResolver(),
							Uri.parse(XMLUri),new Point(512,512),null)); } //docUri
		 		}else{*/
			 		try{ExifInterface exif = new ExifInterface(XMLUri); //XMLUri
					int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
					Bitmap foto = FileUtils.getThumbnail(getBaseContext(), new File(XMLUri)); //XMLUri
						if(foto==null){
							try{BitmapFactory.Options options = new BitmapFactory.Options();
								options.inPreferredConfig=Config.RGB_565;
								options.outMimeType="image/jpeg";
								options.inSampleSize=8;
								foto = BitmapFactory.decodeFile(XMLUri, options); //XMLUri
								preview.setImageBitmap(foto);
							}catch(Exception ex2){
							preview.setImageDrawable(getResources().getDrawable(R.drawable.nopreviewavailable));
						}}
						preview.setImageBitmap(foto);
						if(orientation == 8)//ExifInterface.ORIENTATION_ROTATE_270
							preview.setRotation(270);
					    else if(orientation == 3)//ExifInterface.ORIENTATION_ROTATE_180
					    	preview.setRotation(180);
					    else if(orientation == 6)//ExifInterface.ORIENTATION_ROTATE_90
					        preview.setRotation(90);
						//if(foto==null) preview.setImageDrawable(getResources().getDrawable(R.drawable.nopreviewavailable));
						//}
			 		}catch(Exception ex){ preview.setImageDrawable(getResources().getDrawable(R.drawable.nopreviewavailable)); }
		 		//}

		s.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override public void onItemSelected(AdapterView<?> arg0,
					View v, int i, long l) {
				if(s.getSelectedItemId()==1){ micytip.setVisibility(View.VISIBLE);
					mas.setVisibility(View.VISIBLE);
					if(fecha.getText().toString()!=null && !fecha.getText().toString().equals("")){
						String ano="";
						if(fecha.getText().toString().length()>=4 && fecha.getText().toString().split("-").length==3){
			    			 ano = fecha.getText().toString().substring(
			    					fecha.getText().toString().length()-4,
				    				fecha.getText().toString().length());
			    			 if(isNumeric(ano))
			    				 nombrepreliminar.setText(partes[0]+"_"+ano+"_"+partes[2]+"_"+partes[3]+"_"+partes[4]+extension);
			    		}else{
							fecha.setText(sdfdia.format(new Date()));
							Toast.makeText(getBaseContext(), "Fecha inválida, se ha tomado la del dispositivo.", Toast.LENGTH_LONG).show();
							nombrepreliminar.setText(partes[0]+"_"+anoactual+"_"+partes[2]+"_"+partes[3]+"_"+partes[4]+extension);
						}
					}
					
					else nombrepreliminar.setText(solofichero+extension);
				}else{ micytip.setVisibility(View.GONE);
					mas.setVisibility(View.GONE);
					lineaEmpresa.setVisibility(View.GONE);
					lineaFecha.setVisibility(View.GONE);
					lineaFactura.setVisibility(View.GONE);
					lineaNIF.setVisibility(View.GONE);
					lineaTotal.setVisibility(View.GONE);
					lineaIVA0.setVisibility(View.GONE);
					lineaRecargo.setVisibility(View.GONE);
					lineaRetencion.setVisibility(View.GONE);
					lineaImplicito.setVisibility(View.GONE);
					lineaModo.setVisibility(View.GONE);
					lineaDescripcion.setVisibility(View.GONE);
		    		nombrepreliminar.setText(bypass.substring(bypass.lastIndexOf("/")+1));
				}}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
		 	}
		dialog.show();

		//Intento de tamaño ventana para API 8
		 WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		 lp.copyFrom(dialog.getWindow().getAttributes());
		 lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		 dialog.getWindow().setAttributes(lp);
		
		//Condiciones nombre para documento
		   mic.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, Long.valueOf(5000));
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
			try {startActivityForResult(intent, 6);
			} catch (ActivityNotFoundException a) {
		  Toast.makeText(getApplicationContext(),
		  		"Dispositivo no soportado",Toast.LENGTH_SHORT).show();
		  a.printStackTrace();} }});
		   tip.setOnClickListener(new OnClickListener(){@SuppressLint("NewApi")
		@Override public void onClick(View v) {
			   //Texto ayuda micrófono
			   AlertDialog.Builder adb;
			   if (Build.VERSION.SDK_INT <14)
				    adb = new AlertDialog.Builder(Chat.this);
			   else adb = new AlertDialog.Builder(Chat.this,1);
				adb.setTitle("Ayuda dictado documentos");
				adb.setMessage(getString(R.string.ayudadictado));
				adb.setNegativeButton("Volver",new DialogInterface.OnClickListener(){
					 public void onClick(DialogInterface dialog, int id){ dialog.dismiss(); }});
				AlertDialog ad = adb.create(); ad.show();
				TextView message = (TextView) ad.findViewById(android.R.id.message);
			    message.setTextSize(12);
		   }});
		 btnfecha.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
		final Calendar c = Calendar.getInstance();
		int ano,mes,dia;
		ano = c.get(Calendar.YEAR);
		mes = c.get(Calendar.MONTH);
		dia = c.get(Calendar.DAY_OF_MONTH);
		 
		DatePickerDialog dpd = new DatePickerDialog(Chat.this,new DatePickerDialog.OnDateSetListener() {
			@Override public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
				fecha.setText(dayOfMonth+"-"+ (monthOfYear+1)+"-"+year); }},ano,mes,dia);
		   dpd.show(); }});
		 
		 btnmas.setOnClickListener(new OnClickListener(){
				@Override public void onClick(View v){
					AlertDialog.Builder adb = new AlertDialog.Builder(Chat.this);
					adb.setCancelable(false)
						.setTitle("Registrar diccionario")
						.setMessage("¿Desea añadir al diccionario la traducción \n\""+oriempresa.toUpperCase().trim()+
								"\" -> \""+empresa.getText().toString().toUpperCase().trim()+"\"\npara el tipo \"Nombres de empresa\"?")
						.setIcon(android.R.drawable.ic_menu_sort_alphabetically)
						.setPositiveButton("Añadir registro", new DialogInterface.OnClickListener(){
							@Override public void onClick(DialogInterface dialog, int id){
								//Añadir a la tabla de Empresa el nuevo cambiazo.
								sptl.setValue(empresa.getText().toString().toUpperCase().trim(),oriempresa.toUpperCase().trim());
								sptl.load();
								dialog.dismiss(); }})
						.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
							@Override public void onClick(DialogInterface dialog, int id){
								dialog.dismiss(); }});
					adb.create().show();
				}});
		 
		   mas.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
		 final Dialog subdialog = new Dialog(Chat.this);
		 subdialog.setCancelable(true);
		 subdialog.setTitle("Mostrar/Ocultar campos");
		 subdialog.setContentView(R.layout.dcamposdocase);
		 CheckBox cb1 = (CheckBox)subdialog.findViewById(R.id.cb1);
		 CheckBox cb2 = (CheckBox)subdialog.findViewById(R.id.cb2);
		 CheckBox cb3 = (CheckBox)subdialog.findViewById(R.id.cb3);
		 CheckBox cb4 = (CheckBox)subdialog.findViewById(R.id.cb4);
		 CheckBox cb5 = (CheckBox)subdialog.findViewById(R.id.cb5);
		 CheckBox cb6 = (CheckBox)subdialog.findViewById(R.id.cb6);
		 CheckBox cb7 = (CheckBox)subdialog.findViewById(R.id.cb7);
		 CheckBox cb8 = (CheckBox)subdialog.findViewById(R.id.cb8);
		 CheckBox cb9 = (CheckBox)subdialog.findViewById(R.id.cb9);
		 CheckBox cbIVA4 = (CheckBox)subdialog.findViewById(R.id.cbIVA4);
		 CheckBox cbIVA10 = (CheckBox)subdialog.findViewById(R.id.cbIVA10);
		 CheckBox cbIVA21 = (CheckBox)subdialog.findViewById(R.id.cbIVA21);
		 CheckBox cbIVA99 = (CheckBox)subdialog.findViewById(R.id.cbIVA99);
		 CheckBox cbIGIC0 = (CheckBox)subdialog.findViewById(R.id.cbIGIC0);
		 CheckBox cbIGIC3 = (CheckBox)subdialog.findViewById(R.id.cbIGIC3);
		 CheckBox cbIGIC7 = (CheckBox)subdialog.findViewById(R.id.cbIGIC7);
		 CheckBox cbIGIC9 = (CheckBox)subdialog.findViewById(R.id.cbIGIC9);
		 CheckBox cbIGIC13 = (CheckBox)subdialog.findViewById(R.id.cbIGIC13);
		 CheckBox cbIGIC20 = (CheckBox)subdialog.findViewById(R.id.cbIGIC20);
		 CheckBox cbIGIC99 = (CheckBox)subdialog.findViewById(R.id.cbIGIC99);
		 CheckBox cbImporteContrapartida = (CheckBox)subdialog.findViewById(R.id.cbImporteContrapartida);
		 CheckBox cbImporteCobro = (CheckBox)subdialog.findViewById(R.id.cbImporteCobro);
		 CheckBox cbImportePago = (CheckBox)subdialog.findViewById(R.id.cbImportePago);
		 CheckBox cbCuentaContrapartida = (CheckBox)subdialog.findViewById(R.id.cbCuentaContrapartida);
		 CheckBox cbCuentaCobro = (CheckBox)subdialog.findViewById(R.id.cbCuentaCobro);
		 CheckBox cbCuentaPago = (CheckBox)subdialog.findViewById(R.id.cbCuentaPago);
		 CheckBox cbCobrado = (CheckBox)subdialog.findViewById(R.id.cbCobrado);
		 CheckBox cbPagado = (CheckBox)subdialog.findViewById(R.id.cbPagado);
		 CheckBox  cbEfectiva = (CheckBox)subdialog.findViewById(R.id.cbEfectiva);
		 CheckBox  cbAviso = (CheckBox)subdialog.findViewById(R.id.cbAVISO);
		 CheckBox  cbModo = (CheckBox)subdialog.findViewById(R.id.cbModo);
		 CheckBox  cbDescripcion = (CheckBox)subdialog.findViewById(R.id.cbDescripcion);
		 Button volver = (Button)subdialog.findViewById(R.id.volver);
		 if(lineaEmpresa.getVisibility()==View.VISIBLE) cb1.setChecked(true);
		 if(lineaFecha.getVisibility()==View.VISIBLE) cb2.setChecked(true);
		 if(lineaFactura.getVisibility()==View.VISIBLE) cb3.setChecked(true);
		 if(lineaNIF.getVisibility()==View.VISIBLE) cb4.setChecked(true);
		 if(lineaTotal.getVisibility()==View.VISIBLE) cb5.setChecked(true);
		 if(lineaIVA0.getVisibility()==View.VISIBLE) cb6.setChecked(true);
		 if(lineaRecargo.getVisibility()==View.VISIBLE) cb7.setChecked(true);
		 if(lineaRetencion.getVisibility()==View.VISIBLE) cb8.setChecked(true);
		 if(lineaImplicito.getVisibility()==View.VISIBLE) cb9.setChecked(true);
		 if(lineaCuentaContrapartida.getVisibility()==View.VISIBLE) cbCuentaContrapartida.setChecked(true);
		 if(lineaCuentaCobro.getVisibility()==View.VISIBLE) cbCuentaCobro.setChecked(true);
		 if(lineaCuentaPago.getVisibility()==View.VISIBLE) cbCuentaPago.setChecked(true);
		 if(lineaCobrado.getVisibility()==View.VISIBLE) cbCobrado.setChecked(true);
		 if(lineaPagado.getVisibility()==View.VISIBLE) cbPagado.setChecked(true);
		 if(lineaEfectiva.getVisibility()==View.VISIBLE) cbEfectiva.setChecked(true);
		 if(lineaIVA4.getVisibility()==View.VISIBLE) cbIVA4.setChecked(true);
		 if(lineaIVA10.getVisibility()==View.VISIBLE) cbIVA10.setChecked(true);
		 if(lineaIVA21.getVisibility()==View.VISIBLE) cbIVA21.setChecked(true);
		 if(lineaIVA99.getVisibility()==View.VISIBLE) cbIVA99.setChecked(true);
		 if(lineaImporteContrapartida.getVisibility()==View.VISIBLE) cbImporteContrapartida.setChecked(true);
		 if(lineaImporteCobro.getVisibility()==View.VISIBLE) cbImporteCobro.setChecked(true);
		 if(lineaImportePago.getVisibility()==View.VISIBLE) cbImportePago.setChecked(true);
		 if(lineaIGIC0.getVisibility()==View.VISIBLE) cbIGIC0.setChecked(true);
		 if(lineaIGIC3.getVisibility()==View.VISIBLE) cbIGIC3.setChecked(true);
		 if(lineaIGIC7.getVisibility()==View.VISIBLE) cbIGIC7.setChecked(true);
		 if(lineaIGIC9.getVisibility()==View.VISIBLE) cbIGIC9.setChecked(true);
		 if(lineaIGIC13.getVisibility()==View.VISIBLE) cbIGIC13.setChecked(true);
		 if(lineaIGIC20.getVisibility()==View.VISIBLE) cbIGIC20.setChecked(true);
		 if(lineaIGIC99.getVisibility()==View.VISIBLE) cbIGIC99.setChecked(true);
		 if(lineaAviso.getVisibility()==View.VISIBLE) cbAviso.setChecked(true);
		 if(lineaModo.getVisibility()==View.VISIBLE) cbModo.setChecked(true);
		 if(lineaDescripcion.getVisibility()==View.VISIBLE) cbDescripcion.setChecked(true);
		 cb1.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaEmpresa.setVisibility(View.VISIBLE); 
				else{	   lineaEmpresa.setVisibility(View.GONE); 
							empresa.setText(""); }}});
		 cb2.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaFecha.setVisibility(View.VISIBLE); 
				else{	   lineaFecha.setVisibility(View.GONE);
							fecha.setText(""); } }});
		 cb3.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaFactura.setVisibility(View.VISIBLE); 
				else{	   lineaFactura.setVisibility(View.GONE);
						   factura.setText(""); }}});
		 cb4.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaNIF.setVisibility(View.VISIBLE); 
				else{	   lineaNIF.setVisibility(View.GONE); 
							nif.setText(""); }}});
		 cb5.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaTotal.setVisibility(View.VISIBLE); 
				else{	   lineaTotal.setVisibility(View.GONE);
							total.setText(""); }
				}});	
		 cb6.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaIVA0.setVisibility(View.VISIBLE); 
				else{	   lineaIVA0.setVisibility(View.GONE);
							iva0.setText(""); }}});
		 cb7.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaRecargo.setVisibility(View.VISIBLE); 
				else{	   lineaRecargo.setVisibility(View.GONE);
				recargo.setText(""); }}});
		 cb8.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaRetencion.setVisibility(View.VISIBLE); 
				else{	   lineaRetencion.setVisibility(View.GONE); 
				retencion.setText(""); }}});
		 cb9.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaImplicito.setVisibility(View.VISIBLE); 
				else	{   lineaImplicito.setVisibility(View.GONE);
							implicito.setText(""); }}});
		cbCuentaContrapartida.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaCuentaContrapartida.setVisibility(View.VISIBLE); 
				else	{   lineaCuentaContrapartida.setVisibility(View.GONE);
							cuentaContrapartida.setText(""); }}});
		cbCuentaCobro.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaCuentaCobro.setVisibility(View.VISIBLE); 
				else	{   lineaCuentaCobro.setVisibility(View.GONE);
							cuentaCobro.setText(""); }}});
		cbCuentaPago.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaCuentaPago.setVisibility(View.VISIBLE); 
				else	{   lineaCuentaPago.setVisibility(View.GONE);
							cuentaPago.setText(""); }}});
		cbCobrado.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaCobrado.setVisibility(View.VISIBLE); 
				else	{   lineaCobrado.setVisibility(View.GONE);
							cobrado.setText(""); }}});
		cbPagado.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaPagado.setVisibility(View.VISIBLE); 
				else	{   lineaPagado.setVisibility(View.GONE);
							pagado.setText(""); }}});
		cbEfectiva.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaEfectiva.setVisibility(View.VISIBLE); 
				else	{   lineaEfectiva.setVisibility(View.GONE);
							efectiva.setText(""); }}});
		cbIVA4.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaIVA4.setVisibility(View.VISIBLE); 
				else	{   lineaIVA4.setVisibility(View.GONE);
							iva4.setText(""); }}});
		cbIVA10.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaIVA10.setVisibility(View.VISIBLE); 
				else	{   lineaIVA10.setVisibility(View.GONE);
							iva10.setText(""); }}});
		cbIVA21.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaIVA21.setVisibility(View.VISIBLE); 
				else	{   lineaIVA21.setVisibility(View.GONE);
							iva21.setText(""); }}});
		cbIVA99.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaIVA99.setVisibility(View.VISIBLE); 
				else	{   lineaIVA99.setVisibility(View.GONE);
							iva99.setText(""); }}});
		cbImporteContrapartida.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaImporteContrapartida.setVisibility(View.VISIBLE); 
				else	{   lineaImporteContrapartida.setVisibility(View.GONE);
							importeContrapartida.setText(""); }}});
		cbImporteCobro.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaImporteCobro.setVisibility(View.VISIBLE); 
				else	{   lineaImporteCobro.setVisibility(View.GONE);
							importeCobro.setText(""); }}});
		cbImportePago.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaImportePago.setVisibility(View.VISIBLE); 
				else	{   lineaImportePago.setVisibility(View.GONE);
							importePago.setText(""); }}});
		cbIGIC0.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaIGIC0.setVisibility(View.VISIBLE); 
				else	{   lineaIGIC0.setVisibility(View.GONE);
							igic0.setText(""); }}});
		cbIGIC3.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaIGIC3.setVisibility(View.VISIBLE); 
				else	{   lineaIGIC3.setVisibility(View.GONE);
							igic3.setText(""); }}});
		cbIGIC7.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaIGIC7.setVisibility(View.VISIBLE); 
				else	{   lineaIGIC7.setVisibility(View.GONE);
							igic7.setText(""); }}});
		cbIGIC9.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaIGIC9.setVisibility(View.VISIBLE); 
				else	{   lineaIGIC9.setVisibility(View.GONE);
							igic9.setText(""); }}});
		cbIGIC13.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaIGIC13.setVisibility(View.VISIBLE); 
				else	{   lineaIGIC13.setVisibility(View.GONE);
							igic13.setText(""); }}});
		cbIGIC20.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaIGIC20.setVisibility(View.VISIBLE); 
				else	{   lineaIGIC20.setVisibility(View.GONE);
							igic20.setText(""); }}});
		cbIGIC99.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaIGIC99.setVisibility(View.VISIBLE); 
				else	{   lineaIGIC99.setVisibility(View.GONE);
							igic99.setText(""); }}});
		cbModo.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaModo.setVisibility(View.VISIBLE); 
				else	{   lineaModo.setVisibility(View.GONE);
							modo.setText(""); }}});
		cbDescripcion.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaDescripcion.setVisibility(View.VISIBLE); 
				else	{   lineaDescripcion.setVisibility(View.GONE);
							descripcion.setText(""); }}});
		cbAviso.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton cb, boolean estado) {
				if(estado) lineaAviso.setVisibility(View.VISIBLE); 
				else	{   lineaAviso.setVisibility(View.GONE);
							aviso.setText(""); }}});				
		 volver.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v) { subdialog.dismiss(); }});
		 subdialog.show();
		 
		//Intento de tamaño ventana para API 8
		 WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		 lp.copyFrom(subdialog.getWindow().getAttributes());
		 lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		 subdialog.getWindow().setAttributes(lp);
		   }});
		cancelar.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v){ dialog.dismiss();
			oriempresa=""; }});
		if(!editmode){
			
		
		continuar.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v){ 
				contabilizar(ll,tv,ib,XMLUri,mode);
			dialog.dismiss(); oriempresa=""; extension="";
			
			Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, 1970);
				millis=""+c.getTimeInMillis();
				SharedPreferences.Editor spe = sharedPrefs.edit();
				spe.putLong("lastmillis", Long.parseLong(millis)).commit();
				//XXX!! Cambiar denominación fichero foto antes de pedir otra!
			if(mode==1){ //Adjuntar archivos
				
				//Supongo que hará igual que mode==2
				//Referencia
				if(sharedPrefs.getInt("solicitacliest", 0)==e.getEid())
					referencia = sharedPrefs.getString("ultimareferencia","-1");
				else
					referencia = appendReferenceNumberToUri(e.getReferencia());
				
				//Año actual
				anoactual = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());
				
				//ID OP
				if(sharedPrefs.getBoolean("solicitacli", false))
					idop = ""+sharedPrefs.getInt("solicitaclin", 0);
				else idop = "00";
				
				//ID Foto
				idfoto = sharedPrefs.getString("idfoto", "000");
				
				//Millis
				millis=""+c.getTimeInMillis();
				
				bypass=getExternalCacheDir()+File.separator+
						referencia+"_"+anoactual+"_"+idop+"_"+idfoto+"_"+millis;//+".jpg";
				solofichero=referencia+"_"+anoactual+"_"+idop+"_"+idfoto+"_"+millis;
				sharedPrefs.edit().putLong("lastmillis", Long.parseLong(millis))
					.putString("bypass",bypass).commit();
				
				if (Build.VERSION.SDK_INT <19) //Comprobación si es KitKat o anterior:
					startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("*/*"),7);
				else startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT)
						.addCategory(Intent.CATEGORY_OPENABLE).setType("*/*"),7);
			}else if (mode==2){ //Sacar foto
				//Composición de campos para nombre fichero
				//Referencia
				if(sharedPrefs.getInt("solicitacliest", 0)==e.getEid())
					referencia = sharedPrefs.getString("ultimareferencia","-1");
				else
					referencia = appendReferenceNumberToUri(e.getReferencia());
				
				//Año actual
				anoactual = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());
				
				//ID OP
				if(sharedPrefs.getBoolean("solicitacli", false))
					idop = ""+sharedPrefs.getInt("solicitaclin", 0);
				else idop = "00";
				
				//ID Foto
				idfoto = sharedPrefs.getString("idfoto", "000");
				
				//Millis
				millis=""+c.getTimeInMillis();
				
				bypass=getExternalCacheDir()+File.separator+
						referencia+"_"+anoactual+"_"+idop+"_"+idfoto+"_"+millis+".jpg";
				solofichero=referencia+"_"+anoactual+"_"+idop+"_"+idfoto+"_"+millis;
				sharedPrefs.edit().putLong("lastmillis", Long.parseLong(millis))
					.putString("bypass",bypass).commit();
				startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(bypass))),8); } 
		}});
		
		finalizar.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v){ 
				contabilizar(ll,tv,ib,XMLUri,mode);
				dialog.dismiss(); oriempresa="";
			}});
		
		}else{ //editmode=true
			final String xmlConExtension = XMLUri.replaceAll(FileUtils.getExtension(XMLUri), ".xml");
			continuar.setVisibility(View.GONE);
			if(new File(xmlConExtension).exists()){
				leerXML(xmlConExtension);
				finalizar.setOnClickListener(new OnClickListener(){
				@Override public void onClick(View v) {
					try{File f = new File(xmlConExtension);
						FileChannel outChan = new FileOutputStream(f, true).getChannel();
					    outChan.truncate(0);
					    outChan.close();					    
						String xml = generateXML();
					    OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
					    fout.write(xml); fout.close();
					}
					catch (Exception ex) { ex.printStackTrace(); }
					dialog.dismiss(); }});
			}else finalizar.setVisibility(View.GONE);
		}
	}
	
	private void contabilizar(final LinearLayout ll, final TextView tv, final ImageButton ib, String xmlURI, int mode){
		//Significado mode:
			//mode = 0 -> ¿Editar? Para eso ya tenía un boolean
			//mode = 1 -> Adjuntar archivo
			//mode = 2 -> Sacar foto

			fotoa = new File(bypass);
		File foto = new File(bypass.substring(0,bypass.lastIndexOf("/")+1)+nombrepreliminar.getText().toString());
		bypass = foto.getPath(); //}
		tv.setText(nombrepreliminar.getText().toString());
		tv.setClickable(true);					
			tv.setOnClickListener(new OnClickListener(){
				@Override public void onClick(View arg0) {
					ventanaXML(ll,ib,tv,true,getExternalCacheDir()+File.separator+tv.getText().toString(),0);
			}});
		ib.setOnClickListener(new OnClickListener(){ 
			public void onClick(View v){
				ladjuntos.removeView(ladjuntos.findViewWithTag(tv.getText().toString()));
				removePendingFileByName(tv.getText().toString());
				removePendingFileByName(tv.getText().toString().replaceAll(FileUtils.getExtension(tv.getText().toString()),".xml"));
				if(ladjuntos.getChildCount()==0){ adjuntos.setVisibility(View.GONE); 
				menu.clear();
				MenuInflater inflater = getMenuInflater();
				if(e.getCnae() == null || !e.getCnae().contains("58100"))
					inflater.inflate(R.menu.chatof, menu);
				else inflater.inflate(R.menu.chat, menu);
				flagmodoadjunto=0; }
			}
		});
		ll.addView(ib); ll.addView(tv); 
		ll.setTag(tv.getText().toString());
		ladjuntos.addView(ll); 
		if(cuerpo.getText().toString().equals(""))
			cuerpo.setText(bypass.substring(bypass.lastIndexOf("/")+1));
		if(mode==2) fotoa.renameTo(foto);
		/*else FileUtils.copyFile(FileUtils.getPath(getBaseContext(), FileUtils.getUri(new File (xmlURI))), 
				getExternalCacheDir()+File.separator+nombrepreliminar.getText().toString()+extension);*/
		ficheros.add(bypass);
		addPendingFile(getExternalCacheDir()+File.separator+nombrepreliminar.getText().toString());		
		adjuntos.setVisibility(View.VISIBLE);
		
	//CREAR FICHERO XML
	try{String XMLUri = getExternalCacheDir()+File.separator+
				nombrepreliminar.getText().toString().replace(FileUtils.getExtension(nombrepreliminar.getText().toString()), ".xml");
		if(mode==2) docUri = FileUtils.getUri(foto).toString();
		File f = new File(XMLUri);
		String xml = generateXML();
	    OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
	    fout.write(xml); fout.close();
	    ficheros.add(XMLUri); 
	    addPendingFile(XMLUri);
	}catch (Exception ex) { ex.printStackTrace(); }
	//Actualizo idfoto
		String idfoto = sharedPrefs.getString("idfoto","000");
		idfoto = String.valueOf(Integer.parseInt(idfoto)+1);
		if(idfoto.length()==1) idfoto="00"+idfoto;
		else if(idfoto.length()==2) idfoto="0"+idfoto;
		SharedPreferences.Editor spe = sharedPrefs.edit();
		spe.putString("idfoto", idfoto).commit();
		
		if(flagmodoadjunto==0){
			menu.clear();
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.chatofp, menu);
			flagmodoadjunto=2; }
	}

	private void checkReferencia(final boolean xml) {
		AlertDialog.Builder adb = new AlertDialog.Builder(Chat.this);
		final EditText adet = new EditText(Chat.this);
		adet.setInputType(2);
		adet.setEms(5);
		adb.setCancelable(false);
		adb.setTitle("Introduzca referencia");
		adb.setMessage("Debe identificar el establecimiento "+e.getNombre()+
			" con una referencia numérica para poder enviar ficheros a esta asesoría.");
		if(SharedPreferencesController.getResourceID(getBaseContext()).equals("FAIL"))
			adb.setMessage("Debe identificar el establecimiento "+e.getNombre()+
					" con una referencia numérica para poder enviar ficheros a esta asesoría.\n\n" +
					"A continuación, se le solicitará escoger su cuenta de Google para vincular los archivos que " +
					"vaya a cargar, a su cuenta de Google Drive, para poder compartirlos con la asesoría seleccionada. " +
					"Si no quiere hacer esto ahora, puede cancelar en la siguiente ventana.");
		if(!e.getReferencia().equals("0")) adet.setText(""+e.getReferencia());
		adb.setView(adet);
		adet.requestFocus();
		adb.setPositiveButton("Continuar",new DialogInterface.OnClickListener(){
			 public void onClick(DialogInterface dialog, int id){
				 if(adet.getText().toString()!=null && !adet.getText().toString().equals("")){
						 e.setReferencia(adet.getText().toString());
						 db.updateEstablecimiento(e);
						 if(xml){ //No entra nunca (¿?)
							 String ano="";
							 if(fecha.getText().toString().length()>=4){ //No incluyo control de guiones
								 ano = fecha.getText().toString().substring(
										 fecha.getText().toString().length()-4,
										 fecha.getText().toString().length());
								 if(isNumeric(ano))
									 nombrepreliminar.setText(partes[0]+"_"+ano+"_"+partes[2]+"_"+partes[3]+"_"+partes[4]+".jpg"); }
							 	else nombrepreliminar.setText(partes[0]+"_"+partes[1]+"_"+partes[2]+"_"+partes[3]+"_"+partes[4]+".jpg");
						 } 
						 dialog.dismiss();
						 nombreCarpeta = "Distarea_"+sharedPrefs.getInt("id",0);//sharedPrefs.getString("seudonimo","");
						 googleDriveController = new GoogleDriveController(Chat.this,Chat.this,nombreCarpeta);
						 googleDriveController.connect();
						 
						 //Recreo el menú, para que ahora aparezca el resto de opciones.
						 menu.clear();
						 MenuInflater inflater = getMenuInflater();
						 inflater.inflate(R.menu.chat, menu);
					 }
				 else adet.requestFocus();
			 }});
		adb.setNegativeButton("Cancelar",new DialogInterface.OnClickListener(){
			 public void onClick(DialogInterface dialog, int id){ dialog.dismiss(); }});
		AlertDialog ad = adb.create(); ad.show();
	}
	
	 private class descargaClientes extends AsyncTask<String, Void, Boolean> {    	
	      	ProgressDialog loading; int noint=0, reqcode=0;
	      	ArrayList<String> clientes = new ArrayList<String>();
	      	public descargaClientes(int reqcode){ this.reqcode = reqcode; }
	      	
	      	protected void onPreExecute() {
	      		if(!isNetworkAvailable()) noint=1;
	         	loading = new ProgressDialog(Chat.this);
	         	loading.setMessage("Descargando sus clientes...");
	         	loading.setCancelable(false); loading.show(); 
	        }

	        protected void onPostExecute(final Boolean success) {
	        	if (loading.isShowing()) loading.dismiss();
	        	for(CliF c : db.getAllClientesF(e.getEid())) //XXX Añadir check referencia válido?
	        		clientes.add(c.getRef()+" "+c.getNombre());
	        	if(clientes.size()>0){
	        	final TextView s = new TextView(getBaseContext()),
	        		    	   i = new TextView(getBaseContext());
	        	final AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
	        	
			    builder.setTitle("Seleccione cliente");
			    if(noint==1) builder.setIcon(R.drawable.dc);
			    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    final View layout = inflater.inflate(R.layout.dclientes,null);
			    builder.setView(layout);
			    EditText buscar = (EditText)layout.findViewById(R.id.busqueda);
			    final ListView lv = (ListView) layout.findViewById(R.id.list);
			    lv.setAdapter(new ArrayAdapter<String>(Chat.this,
			            android.R.layout.simple_list_item_single_choice,clientes));
			    lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			    lv.setOnItemClickListener(new OnItemClickListener(){
					@Override public void onItemClick(AdapterView<?> av, View v, int id, long l) {
						s.setText(clientes.get(id));
		        		i.setText(""+id);
				}});
				buscar.addTextChangedListener(new TextWatcher() {
	    			@Override public void afterTextChanged(Editable ed) {
	    				if (ed.toString().length()>=3){
	    					final ArrayList<String> busqueda = new ArrayList<String>();
	    					for(String c : clientes){
	    						if(c.toLowerCase().contains(ed.toString().toLowerCase()))
	    							busqueda.add(c);
	    					}
	    					if(busqueda.size()>=1){
	    						lv.setAdapter(new ArrayAdapter<String>(Chat.this,
	    					            android.R.layout.simple_list_item_single_choice,busqueda));
	    						lv.setOnItemClickListener(new OnItemClickListener(){
	    							@Override public void onItemClick(AdapterView<?> av, View v, int id, long l) {
	    					        		   s.setText(busqueda.get(id));
	    					        		   i.setText(""+id);
	    						}});
	    					}
	    				}else{
	    					lv.setAdapter(new ArrayAdapter<String>(Chat.this,
 					            android.R.layout.simple_list_item_single_choice,clientes));
 						lv.setOnItemClickListener(new OnItemClickListener(){
 							@Override public void onItemClick(AdapterView<?> av, View v, int id, long l) {
	 			        		   s.setText(clientes.get(id));
	 			        		   i.setText(""+id);
	 						}});
	    				}
	    			}
	    				@Override public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
	    				@Override public void onTextChanged(CharSequence s, int start,int before, int count) {}});
			      builder.setPositiveButton("Seleccionar", new DialogInterface.OnClickListener() {
			               @Override public void onClick(DialogInterface dialog, int id) {
			            	   if(s.getText().toString().equals(""))
			            		   Toast.makeText(getBaseContext(),"Elija un cliente para continuar.",Toast.LENGTH_LONG).show();
			            	   else{
			            		   String str = s.getText().toString();
			            		   Calendar c = Calendar.getInstance();
			           				c.set(Calendar.YEAR, 1970);
			           				millis=""+c.getTimeInMillis();
			            		   
			            	   bypass=getExternalCacheDir()+File.separator+
			         					//sharedPrefs.getString("ultimareferencia", "0")+"_"+
			            			   appendReferenceNumberToUri(str.substring(0,str.indexOf(" ")))+"_"+
			       					new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime())
			       					+"_"+sharedPrefs.getInt("solicitaclin", 0)+"_"
			       					+sharedPrefs.getString("idfoto","000")+"_"+millis;
			            	   if(reqcode==8) bypass+=".jpg";
			            	   sharedPrefs.edit().putInt("ultimocliente", Integer.valueOf(i.getText().toString()))
		            		   		.putString("ultimareferencia", appendReferenceNumberToUri(str.substring(0,str.indexOf(" "))))
		            		   		.putLong("lastmillis", Long.parseLong(millis)).putString("bypass",bypass).commit();
			            	   if(reqcode==7)
			            		   if (Build.VERSION.SDK_INT <19) //Comprobación si es KitKat o anterior:
			           					startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("*/*"),7);
			           			   else startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT)
			           					.addCategory(Intent.CATEGORY_OPENABLE).setType("*/*"),7);
			            	   else if (reqcode==8){
			            		   startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
			       						.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(bypass))),reqcode);
			            	   }
			            	   dialog.dismiss(); }
			            }})
			           .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			               @Override public void onClick(DialogInterface dialog, int id) {
			            	   dialog.dismiss();
			            }});
			    try{
			    s.setText(clientes.get(sharedPrefs.getInt("ultimocliente", 0)));
  		   	i.setText(""+sharedPrefs.getInt("ultimocliente", 0));
			    }catch(Exception e){e.printStackTrace();}
			    AlertDialog ad = builder.create();
			    ad.show();}
	        	else
	        		Toast.makeText(getBaseContext(), "El admin de su asesoría debe crear antes clientes en la web www.distarea.es",
	        				Toast.LENGTH_LONG).show();
	        }
	         	
	      	@Override protected Boolean doInBackground(String... arg0) {
	      		if(noint==0){
	      		//ArrayList<Integer> activos = new ArrayList<Integer>();
	      		try{ Class.forName("org.postgresql.Driver");
	      		}catch(ClassNotFoundException e){ e.printStackTrace(); }
	    		try{ DriverManager.setLoginTimeout(20);
		    		 Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
	    			 Statement st = conn.createStatement();
	    			 ResultSet rs = st.executeQuery("SELECT idclientesf, nombre, email, idestablecimientofk, " +
	    			 		"idpaisfk, idprovinciafk, idmunicipiofk, direccion, telefono, telefono2, " +
	    			 		"fecha_nacimiento, nif, referencia, iddistarea, idistarea_vendedor, sexo, tipocliente, activo " +
	    					"FROM clientesf WHERE idestablecimientofk="+sharedPrefs.getInt("solicitacliest",0)+
	    			 		/*" AND '"+sharedPrefs.getString("lastdateclientesf", "1970-01-01 00:00:00")+
	    			 		"'::date < fecha_ultima_revision*/
	    					" ORDER BY referencia ASC");
	    			 while(rs.next()){ 
	    				 if(rs.getString(18).equals("S")){
	    				 CliF c = new CliF(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
	    						 rs.getString(5), rs.getString(8), rs.getString(9), rs.getString(13),
	    						 rs.getInt(15),rs.getString(16));
	    				 //activos.add(rs.getInt(1));
	    				 
	    				// COPIADO DE LISTACOMPRA -  COMPATIBILIDAD
	    				 
	    				//Posibles nulls
	    				 if(rs.getString(6)!=null) c.setProvincia(rs.getString(6));
	    				 if(rs.getString(7)!=null) c.setMunicipio(rs.getString(7));
	    				 if(rs.getInt(10)!=0) c.setMovil(String.valueOf(rs.getInt(10)));
	    				 if(rs.getString(11)!=null) c.setFechanac(rs.getString(11));
	    				 if(rs.getString(12)!=null) c.setNif(rs.getString(12));
	    				 if(rs.getString(16)!=null) c.setSexo(rs.getString(16));
	    				 if(rs.getString(17)!=null) c.setTipocliente(rs.getString(17));
	    				 
	    				 if(db.buscaClienteF(c.getIdcf())!=null){
	    					 Log.e("UPDATE",""+c.getIdcf());
	    					 db.updateClienteF(c);
	    				 }else{
	    					 Log.e("ADD",""+c.getIdcf());
	    					 db.addClienteF(c);
	    				 }
	    				 }else{ 
	    					 if(db.buscaClienteF(rs.getInt(1))!=null)
	    						 db.deleteClienteF(rs.getInt(1)); }
	    			 }
	    			 rs.close(); st.close(); conn.close(); 
	    			 sharedPrefs.edit().putString("lastdateclientesf", 
	    					 postgrestyle.format(new Date())).commit();
	    			 
	    			 
	    				 /* Tramo previo a la copia de ahí arriba ^^
	    				 c.setProvincia(rs.getString(6));
	    				 c.setMunicipio(rs.getString(7));
	    				 c.setMovil(rs.getString(10));
	    				 c.setFechanac(rs.getString(11));
	    				 c.setNif(rs.getString(12));
	    				 c.setIddis(rs.getInt(14));
	    				 c.setTipocliente(rs.getString(16));
	    				 if(db.buscaClienteF(c.getIdcf())!=null) 
	    					 db.updateClienteF(c);
	    				 else db.addClienteF(c);
	    			 }else{
	    				 if(db.buscaClienteF(rs.getInt(1))!=null)
	    					 db.deleteClienteF(rs.getInt(1));
	    			 }}
	    			 rs.close(); st.close(); conn.close(); 
	    			 sharedPrefs.edit().putString("lastdateclientesf", 
	    					 postgrestyle.format(new Date())).commit();
	    			 /*
	    			//Borrado de clientes desaparecidos
	    			 List<CliF> borrarinactivos = db.getAllClientesFAV(e.getEid());
	    			 for(CliF c : borrarinactivos){
	    				 int flag=0;
	    				 for(int a : activos){
	    					 if(a==c.getIdcf()){
	    						 flag=1; break;}
	    				 }
	    				 if(flag==0){
	    					 Log.e("BORRANDO",""+c.getNombre());
	    					 db.deleteClienteF(c.getIdcf()); }
	    			 }*/
	    			 
	    		}catch (SQLException e){ e.printStackTrace(); }
	      		}return true; 
	      	}
	    }
	
	
	 /*private class descargaClientes extends AsyncTask<String, Void, Boolean> {    	
	      	ProgressDialog loading;
	      	ArrayList<String> clientes = new ArrayList<String>();
	      	
	      	protected void onPreExecute() {
	         	loading = new ProgressDialog(Chat.this);
	         	loading.setMessage("Descargando sus clientes...");
	         	loading.setCancelable(false); loading.show(); 
	        }

	        protected void onPostExecute(final Boolean success) {
	        	if (loading.isShowing()) loading.dismiss();
	        	if(clientes.size()>0){
	        	final TextView s = new TextView(getBaseContext()),
	        		    	   i = new TextView(getBaseContext());
	        	final AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
	        	
			    builder.setTitle("Seleccione cliente");
			    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    final View layout = inflater.inflate(R.layout.dclientes,null);
			    builder.setView(layout);
			    EditText buscar = (EditText)layout.findViewById(R.id.busqueda);
			    final ListView lv = (ListView) layout.findViewById(R.id.list);
			    lv.setAdapter(new ArrayAdapter<String>(Chat.this,
			            android.R.layout.simple_list_item_single_choice,clientes));
			    lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			    lv.setOnItemClickListener(new OnItemClickListener(){
					@Override public void onItemClick(AdapterView<?> av, View v, int id, long l) {
						s.setText(clientes.get(id));
		        		i.setText(""+id);
				}});
				buscar.addTextChangedListener(new TextWatcher() {
	    			@Override public void afterTextChanged(Editable ed) {
	    				if (ed.toString().length()>=3){
	    					final ArrayList<String> busqueda = new ArrayList<String>();
	    					for(String c : clientes){
	    						if(c.toLowerCase().contains(ed.toString().toLowerCase()))
	    							busqueda.add(c);
	    					}
	    					if(busqueda.size()>=1){
	    						lv.setAdapter(new ArrayAdapter<String>(Chat.this,
	    					            android.R.layout.simple_list_item_single_choice,busqueda));
	    						lv.setOnItemClickListener(new OnItemClickListener(){
	    							@Override public void onItemClick(AdapterView<?> av, View v, int id, long l) {
	    					        		   s.setText(busqueda.get(id));
	    					        		   i.setText(""+id);
	    						}});
	    					}
	    				}else{
	    					lv.setAdapter(new ArrayAdapter<String>(Chat.this,
    					            android.R.layout.simple_list_item_single_choice,clientes));
    						lv.setOnItemClickListener(new OnItemClickListener(){
    							@Override public void onItemClick(AdapterView<?> av, View v, int id, long l) {
	 			        		   s.setText(clientes.get(id));
	 			        		   i.setText(""+id);
	 						}});
	    				}
	    			}
	    				@Override public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
	    				@Override public void onTextChanged(CharSequence s, int start,int before, int count) {}});
			      builder.setPositiveButton("Seleccionar", new DialogInterface.OnClickListener() {
			               @Override public void onClick(DialogInterface dialog, int id) {
			            	   if(s.getText().toString().equals(""))
			            		   Toast.makeText(getBaseContext(),"Elija un cliente para continuar.",Toast.LENGTH_LONG).show();
			            	   else{
			            		   String str = s.getText().toString();
			            		   Calendar c = Calendar.getInstance();
			           				c.set(Calendar.YEAR, 1970);
			           				millis=""+c.getTimeInMillis();
			            		   SharedPreferences.Editor spe = sharedPrefs.edit();
			            		   spe.putInt("ultimocliente", Integer.valueOf(i.getText().toString()));
			            		   spe.putString("ultimareferencia", appendReferenceNumberToUri(Integer.valueOf(str.substring(0,str.indexOf(" ")))));
			            		   spe.putLong("lastmillis", Long.parseLong(millis)).commit();
			            	   bypass=getExternalCacheDir()+File.separator+
			         					sharedPrefs.getString("ultimareferencia", "0")+"_"+
			       					new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime())
			       					+"_"+sharedPrefs.getInt("solicitaclin", 0)+"_"
			       					+sharedPrefs.getString("idfoto","000")+"_"+millis+".jpg";
			       			startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
			       				.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(bypass))),8);
			            	   dialog.dismiss(); }
			            }})
			           .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			               @Override public void onClick(DialogInterface dialog, int id) {
			            	   dialog.dismiss();
			            }});
			    try{
			    s.setText(clientes.get(sharedPrefs.getInt("ultimocliente", 0)));
     		   	i.setText(""+sharedPrefs.getInt("ultimocliente", 0));
			    }catch(Exception e){e.printStackTrace();}
			    AlertDialog ad = builder.create();
			    ad.show();}
	        	else
	        		Toast.makeText(getBaseContext(), "El admin de su asesoría debe crear antes clientes en la web www.distarea.es",
	        				Toast.LENGTH_LONG).show();
	        }
	         	
	      	@Override protected Boolean doInBackground(String... arg0) {
	      		try{ Class.forName("org.postgresql.Driver");
	      		}catch(ClassNotFoundException e){ e.printStackTrace(); }
	    		try{ DriverManager.setLoginTimeout(20);
		    		 Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
	    			 Statement st = conn.createStatement();
	    			 ResultSet rs = st.executeQuery("SELECT referencia, nombre FROM clientesf " +
	    			 		"WHERE idestablecimientofk="+sharedPrefs.getInt("solicitacliest",0)+
	    			 		" AND activo='S' ORDER BY referencia ASC");
	    			 while(rs.next()) clientes.add(rs.getInt(1)+" "+rs.getString(2));
	    			 rs.close(); st.close(); conn.close();
	    		}catch (SQLException e){ e.printStackTrace(); }
	    		return true; 
	      	}
	    }*/
		    		
	private void initDialogEditTextAndLayout() {
		empresa = (EditText)dialog.findViewById(R.id.empresa);
		fecha = (EditText)dialog.findViewById(R.id.fecha);
		factura = (EditText)dialog.findViewById(R.id.factura);
		nif = (EditText)dialog.findViewById(R.id.nif);
		total = (EditText)dialog.findViewById(R.id.total);
		iva0 = (EditText)dialog.findViewById(R.id.iva0);
		iva4 = (EditText)dialog.findViewById(R.id.iva4);
		iva10 = (EditText)dialog.findViewById(R.id.iva10);
		iva21 = (EditText)dialog.findViewById(R.id.iva21);
		iva99 = (EditText)dialog.findViewById(R.id.iva99);
		igic0 = (EditText)dialog.findViewById(R.id.igic0);
		igic3 = (EditText)dialog.findViewById(R.id.igic3);
		igic7 = (EditText)dialog.findViewById(R.id.igic7);
		igic9 = (EditText)dialog.findViewById(R.id.igic9);
		igic13 = (EditText)dialog.findViewById(R.id.igic13);
		igic20 = (EditText)dialog.findViewById(R.id.igic20);
		igic99 = (EditText)dialog.findViewById(R.id.igic99);
		recargo = (EditText)dialog.findViewById(R.id.recargo);
		retencion = (EditText)dialog.findViewById(R.id.retencion);
		implicito = (EditText)dialog.findViewById(R.id.implicito);
		cuentaContrapartida = (EditText)dialog.findViewById(R.id.cuentaContrapartida);
		cuentaCobro = (EditText)dialog.findViewById(R.id.cuentaCobro);
		cuentaPago = (EditText)dialog.findViewById(R.id.cuentaPago);
		importeContrapartida = (EditText)dialog.findViewById(R.id.importeContrapartida);
		importeCobro = (EditText)dialog.findViewById(R.id.importeCobro);
		importePago = (EditText)dialog.findViewById(R.id.importePago);
		cobrado = (EditText)dialog.findViewById(R.id.cobrado);
		pagado = (EditText)dialog.findViewById(R.id.pagado);
		efectiva = (EditText)dialog.findViewById(R.id.efectiva);
		aviso = (EditText)dialog.findViewById(R.id.aviso); 
		modo = (EditText)dialog.findViewById(R.id.modo); 
		descripcion = (EditText)dialog.findViewById(R.id.descripcion); 
		lineaEmpresa = (LinearLayout)dialog.findViewById(R.id.lineaEmpresa);
		lineaFecha = (LinearLayout)dialog.findViewById(R.id.lineaFecha);
		lineaFactura = (LinearLayout)dialog.findViewById(R.id.lineaFactura);
		lineaNIF = (LinearLayout)dialog.findViewById(R.id.lineaNIF);
		lineaTotal = (LinearLayout)dialog.findViewById(R.id.lineaTotal);
		lineaIVA0 = (LinearLayout)dialog.findViewById(R.id.lineaIVA0);
		lineaIVA4 = (LinearLayout)dialog.findViewById(R.id.lineaIVA4);
		lineaIVA21 = (LinearLayout)dialog.findViewById(R.id.lineaIVA21);
		lineaIVA10 = (LinearLayout)dialog.findViewById(R.id.lineaIVA10);
		lineaIVA99 = (LinearLayout)dialog.findViewById(R.id.lineaIVA99);
		lineaRecargo = (LinearLayout)dialog.findViewById(R.id.lineaRecargo);
		lineaRetencion = (LinearLayout)dialog.findViewById(R.id.lineaRetencion);
		lineaImplicito = (LinearLayout)dialog.findViewById(R.id.lineaImplicito);
		lineaCuentaContrapartida = (LinearLayout)dialog.findViewById(R.id.lineaCuentaContrapartida);
		lineaCuentaCobro = (LinearLayout)dialog.findViewById(R.id.lineaCuentaCobro);
		lineaCuentaPago = (LinearLayout)dialog.findViewById(R.id.lineaCuentaPago);
		lineaImporteContrapartida = (LinearLayout)dialog.findViewById(R.id.lineaImporteContrapartida);
		lineaImporteCobro = (LinearLayout)dialog.findViewById(R.id.lineaImporteCobro);
		lineaImportePago = (LinearLayout)dialog.findViewById(R.id.lineaImportePago);
		lineaCobrado = (LinearLayout)dialog.findViewById(R.id.lineaCobrado);
		lineaPagado = (LinearLayout)dialog.findViewById(R.id.lineaPagado);
		lineaEfectiva = (LinearLayout)dialog.findViewById(R.id.lineaEfectiva);
		lineaIGIC0 = (LinearLayout)dialog.findViewById(R.id.lineaIGIC0);
		lineaIGIC3 = (LinearLayout)dialog.findViewById(R.id.lineaIGIC3);
		lineaIGIC7 = (LinearLayout)dialog.findViewById(R.id.lineaIGIC7);
		lineaIGIC9 = (LinearLayout)dialog.findViewById(R.id.lineaIGIC9);
		lineaIGIC13 = (LinearLayout)dialog.findViewById(R.id.lineaIGIC13);
		lineaIGIC20 = (LinearLayout)dialog.findViewById(R.id.lineaIGIC20);
		lineaIGIC99 = (LinearLayout)dialog.findViewById(R.id.lineaIGIC99);
		lineaAviso = (LinearLayout)dialog.findViewById(R.id.lineaAVISO);
		lineaModo = (LinearLayout)dialog.findViewById(R.id.lineaModo);
		lineaDescripcion = (LinearLayout)dialog.findViewById(R.id.lineaDescripcion);
	}
	/*
	private class consulta extends AsyncTask<Void,Void, Boolean> {
		protected void onPostExecute(Boolean success){
			Log.e("POST_QUERYFOLDER",nombreCarpeta+"_resource = "+success);
			if(success) Toast.makeText(getBaseContext(),"Ya está listo para enviar contabilizaciones. " +
					"Elija \"Sacar foto\" para empezar.",Toast.LENGTH_LONG).show();
		}
		
		@Override protected Boolean doInBackground(Void... arg0) {
			googleDriveController.queryFolder(nombreCarpeta);
			if(SharedPreferencesController.getValue(getBaseContext(), nombreCarpeta+"_resource")!= null)
				return true;
			else return false;
		}
	}
/*
	public class checkEnvio extends AsyncTask<String, Void, Boolean> {
		 int files, totalf;
		
		 protected void onPreExecute(){
			 files = ficheros.size();
			 totalf = ficheros.size();
			 Log.e("FICHEROS.SIZE",""+ficheros.size());
		 }
		
		 protected void onPostExecute(Boolean success){
			 if(success)
				 Log.e("FIN","OK");
				 //Toast.makeText(getBaseContext(),"Todos enviados. (O eso creo)",Toast.LENGTH_LONG).show();
			 else Log.e("FIN","NO OK");
				 //Toast.makeText(getBaseContext(),"Ha habido un problema enviando, revise su Drive.",Toast.LENGTH_LONG).show();
			 Intent intent = new Intent(Chat.this, Conversaciones.class);
			 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			 startActivity(intent); finish();
		 }
		 
   @Override protected Boolean doInBackground(String... arg0) {
  		 while(!ficheros.isEmpty()){
  			 if(ficheros.size()<files){
  				/*Toast.makeText(getBaseContext(),
  						 "Enviados "+(totalf-ficheros.size())+" de "+totalf+".",Toast.LENGTH_LONG).show();/
  				Log.e("STATUS","Enviados "+(totalf-ficheros.size())+" de "+totalf+".");
  				files = ficheros.size();
  			 }
  		 }
   	return true;}}*/
	
	/*void openWhatsappContact(String number) {
	    Uri uri = Uri.parse("smsto:" + number);
	    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
	    i.setPackage("com.whatsapp");  
	    startActivity(Intent.createChooser(i, ""));
	}*/
	
	public void onClickWhatsApp(View view) {

        if (whatsappInstalledOrNot("com.whatsapp")) {
        	//PackageManager pm = getBaseContext().getPackageManager();
	    	String number = "34661948912";
	    	String text = "Mensaje de prueba"; //burbuja.getText().toString();
	    	Uri uri = Uri.parse("smsto:" + number);
	    	//Intent waIntent = new Intent(Intent.ACTION_SENDTO, uri);
	    	final ComponentName name = new ComponentName("com.whatsapp", "com.whatsapp.ContactPicker");
	    	Intent waIntent = new Intent();
	    	waIntent.setComponent(name);
	    	waIntent.putExtra(Intent.EXTRA_PHONE_NUMBER,uri);
	    	waIntent.putExtra(Intent.EXTRA_TEXT, text);
	        waIntent.setType("text/plain");
	        //waIntent.setPackage("com.whatsapp");
	        //pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
	        /*Intent intent = new Intent();
	                intent.setComponent(new ComponentName(packageName,
	                        ri.activityInfo.name));*/
	        
	        //waIntent.setPackage(pm.get)
	        startActivity(waIntent);
	        //startActivity(Intent.createChooser(waIntent, "Enviar mensaje a"));

	   } else{
	        Toast.makeText(this, "WhatsApp no está instalado en el dispositivo.", Toast.LENGTH_LONG).show();
	        Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);
	   }  

	}
	
	private boolean whatsappInstalledOrNot(String uri) {
	    PackageManager pm = getPackageManager();
	    boolean app_installed = false;
	    try {
	        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
	        app_installed = true;
	    } catch (PackageManager.NameNotFoundException e) {
	        app_installed = false;
	    }
	    return app_installed;
	}
}