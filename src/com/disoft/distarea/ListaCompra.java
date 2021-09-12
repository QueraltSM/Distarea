 package com.disoft.distarea;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
//import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
//import android.view.MenuItem;
import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import android.support.v4.view.MenuCompat;
import android.support.v4.internal.view.SupportSubMenu;
import android.support.v4.internal.view.SupportMenuItem;
//import android.support.v4.app.ActivityCompat;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import com.disoft.distarea.extras.Automailer;
import com.disoft.distarea.extras.Common;
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.extras.NotificacionMensajes;
import com.disoft.distarea.models.Art;
import com.disoft.distarea.models.ArtEst;
import com.disoft.distarea.models.CliF;
import com.disoft.distarea.models.Est;
import com.disoft.distarea.models.Msj;
import com.disoft.distarea.models.Ped;
import com.google.android.gms.common.ConnectionResult;
import com.google.zxing.integration.android.IntentIntegrator;
//import com.google.zxing.integration.android.IntentIntegrator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//TODO REVISAR SI LOS PEDIDOS AHORA SON DEL USUARIO, O SI LOS SIGUE EXCLUYENDO LA COMPARACIÓN
//ADEMÁS, REVISAR MENSAJES PENDIENTES DE SER LEÍDOS (¿4?)
public class ListaCompra extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
	final static int xmlversion=1;
	Locale spanish = new Locale("es", "ES"); Calendar c; Est e;
	SharedPreferences sharedPrefs; DatabaseHandler db; ActionBar ab; Menu m;
	SimpleDateFormat sdfdia = new SimpleDateFormat("yyyy-MM-dd",spanish),
			sdfdiashow = new SimpleDateFormat("dd-MM-yyyy",spanish),
			sdfhora = new SimpleDateFormat("HH:mm:ss",spanish),
			sdfhorass = new SimpleDateFormat("HH:mm",spanish),
			sdffichero = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",spanish),
			postgrestyle = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",spanish);
	LinearLayout ll; TableLayout tl; String envio="Se recoger? personalmente.", nfirma, version;
	View popupView; PopupWindow popupWindow, popupWindow2, popupWindow3;
	TextView contador; int[] eids; private String[] mNameList; double subtotal=0.00;
	AppCompatSpinner mIcsSpinner; IcsAdapter mAdapter; Spinner tipo; boolean auto=false;
	List<Integer> est = new ArrayList<Integer>(), est2 = new ArrayList<Integer>();
	PorterDuffColorFilter greyFilter; int idclif=0; RelativeLayout rl;
	ProgressDialog loadingBE; int eidBE; ImageButton bg;
	// Campos en popupadd
	EditText artarticulo, artunidades, artcbarras, artobservaciones, artprecio;
	// Campos en popuparticulopedido
	EditText artpedcbarras, artpedcantidad, artpedprecio, artpedobs;
	int orden = 0, flaganterior = 0, flaglptotal=0, ver = 0, flagvc=0, eidactual,
		flagartpend=0, flagdoonce=0, /*flagpedidook=0,*/ flagdir=0, nchecks=0, nart=0,
		existe=0, flagrefresh=0, flageapart=0, flagfalsototal=0, flagcopia=1,
		flagfirma=0, flagcbarras=0, flagop=0, flagsendall=0, sobre=0, midbd=0;
	String direccionpedido="", referenciapedido="", fechapedido="", horapedido="",
			observacionespedido="";
	//Campos de reservas
	 int year,month,day,hourini,minuteini;
	 String cminuteini;
	//Apaño Restaurantes
	 OnClickListener etNum, etTxt;
	 //Intento salvación cargarápida Spinner
	 //Map<Integer,String> logos = new HashMap<Integer,String>();
	 ArrayList<String> logos = new ArrayList<String>();

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//LanguageController.changeLanguageToContext(getBaseContext(), "en");
		if(com.google.android.gms.common.GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getBaseContext())
				!=ConnectionResult.SUCCESS)
			Toast.makeText(getBaseContext(), "Es posible que haya que actualizar la app. Contacte con Disoft.", Toast.LENGTH_LONG).show();
		//else Toast.makeText(getBaseContext(), "GooglePlayServices Ver. 7.3 ", Toast.LENGTH_LONG).show();

		//Overlay TEST MODE
		/*if(getString(R.string.dirbbdd).contains("demomovil")){
			TextView testmode = new TextView(getBaseContext());
			testmode.setText("TEST MODE");
			testmode.setTextColor(Color.RED);

			//super.setTheme(R.style.TestModeABS);
			Toast.makeText(getBaseContext(), "TEST MODE", Toast.LENGTH_LONG).show();
		}*/

		setContentView(R.layout.listacompra);
		ll = (LinearLayout) findViewById(R.id.base);
		tl = (TableLayout) findViewById(R.id.tl);
		bg = (ImageButton) findViewById(R.id.bg);
		rl = new RelativeLayout(getBaseContext());
		RelativeLayout pie = (RelativeLayout) findViewById(R.id.pie);
		RelativeLayout pie2 = (RelativeLayout) findViewById(R.id.pie2);
		pie.setClickable(true); pie2.setClickable(true);
		OnClickListener invocaPie = new OnClickListener(){
			@Override public void onClick(View v){
				AlertDialog.Builder adb = new AlertDialog.Builder(ListaCompra.this);
				View layley = getLayoutInflater().inflate(R.layout.dleyenda, null);
				adb.setView(layley);
				if(e==null){
					if(db.checkCD())
						((LinearLayout)layley.findViewById(R.id.ley_reservavarios)).setVisibility(View.VISIBLE);
					else ((LinearLayout)layley.findViewById(R.id.ley_reservavarios)).setVisibility(View.GONE);
					((LinearLayout)layley.findViewById(R.id.ley_mas2)).setVisibility(View.VISIBLE);
					((TextView)layley.findViewById(R.id.ley_datosestablecimiento)).setVisibility(View.GONE);
				}else{((LinearLayout)layley.findViewById(R.id.ley_reservavarios)).setVisibility(View.GONE);
					((LinearLayout)layley.findViewById(R.id.ley_mas2)).setVisibility(View.GONE);
					((TextView)layley.findViewById(R.id.ley_datosestablecimiento)).setVisibility(View.VISIBLE); }
				if(e!=null && e.getCnae() != null && e.getCnae().contains("58100")){
					((LinearLayout)layley.findViewById(R.id.ley_mensajeprivado)).setVisibility(View.GONE);
					((LinearLayout)layley.findViewById(R.id.ley_mensajeasesoria)).setVisibility(View.VISIBLE);
				}else{((LinearLayout)layley.findViewById(R.id.ley_mensajeprivado)).setVisibility(View.VISIBLE);
					((LinearLayout)layley.findViewById(R.id.ley_mensajeasesoria)).setVisibility(View.GONE); }
				if(e!=null && e.getEid()<0 || e!=null && (e.getConfigura() != null && e.getConfigura().contains(",+,")))
					((LinearLayout)layley.findViewById(R.id.ley_mas)).setVisibility(View.VISIBLE);
				else ((LinearLayout)layley.findViewById(R.id.ley_mas)).setVisibility(View.GONE);
				if(e!=null && e.getTv()!=null && !e.getTv().equals("") && ((ImageButton)findViewById(R.id.carrocompra)).getVisibility()==View.VISIBLE)
					((LinearLayout)layley.findViewById(R.id.ley_carrito)).setVisibility(View.VISIBLE);
				else ((LinearLayout)layley.findViewById(R.id.ley_carrito)).setVisibility(View.GONE);
				if(e!= null && e.getConfigura() != null && e.getConfigura().contains(",R,"))
					((LinearLayout)layley.findViewById(R.id.ley_reservasuno)).setVisibility(View.VISIBLE);
				else ((LinearLayout)layley.findViewById(R.id.ley_reservasuno)).setVisibility(View.GONE);
				if(e!=null && e.getEid()<0){
					((TextView)layley.findViewById(R.id.ley_datosestablecimiento)).setVisibility(View.GONE);
					((LinearLayout)layley.findViewById(R.id.ley_mensajeprivado)).setVisibility(View.GONE);
					((LinearLayout)layley.findViewById(R.id.ley_carrito)).setVisibility(View.GONE);
					((LinearLayout)layley.findViewById(R.id.ley_lapiz)).setVisibility(View.VISIBLE);
				}else ((LinearLayout)layley.findViewById(R.id.ley_lapiz)).setVisibility(View.GONE);
				/*adb.setOnCancelListener(new OnCancelListener(){
					@Override public void onCancel(DialogInterface dialog) {
						dialog.dismiss(); }});*/
				final AlertDialog d = adb.create();
				layley.setOnClickListener(new OnClickListener(){
					@Override public void onClick(View v){ d.dismiss(); }});
				d.show();
			}};
		pie.setOnClickListener(invocaPie); pie2.setOnClickListener(invocaPie);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		if (sharedPrefs.getString("nombre", "").equals("")) {
			if(isNetworkAvailable()){
				sharedPrefs.edit().clear().commit();
				Intent i = new Intent(this, Registro.class); startActivity(i); finish();}
			else ll.post(new Runnable(){ public void run(){
				errorConexion(getApplicationContext());}});
		}else {
			if (sharedPrefs.getInt("internetmode", 0) != 2) {
				// Schedule the alarm!
				Log.e("CREA", "ALARMA");
				PendingIntent receiver = PendingIntent.getService(this, 0,
						new Intent(ListaCompra.this, NotificacionMensajes.class), 0);
				AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime(), 30000, receiver);
			}
			db = new DatabaseHandler(this);
			if (db.getReadableDatabase().getVersion() == 5)
				db.onUpgrade(db.getReadableDatabase(), 5, 6);
			ab = getSupportActionBar();
			//ab.setIcon(R.drawable.ic_launcher2);
//			ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.azbackground));
			try {
				version = getBaseContext().getPackageManager().getPackageInfo(getBaseContext()
						.getPackageName(), 0).versionName;
			} catch (Exception e) {
			}
			// Spinner
			mIcsSpinner = (AppCompatSpinner) findViewById(R.id.ics_spinner);
			mNameList = new String[db.getEstablecimientosHabilitadosCount() + 1 + db.getListasCount()];
			int i = 1;
			eids = new int[mNameList.length];
			mNameList[0] = getString(R.string.seleccionar);
			logos.add(null);
			for (Est e : db.getAllEstablecimientos()) {
				if (e.getEid() < 0) {
					logos.add(null); //Relleno nulos
					mNameList[i] = e.getNombre();
					eids[i] = e.getEid();
					i++;
				}
			}
			for (Est e : db.getAllEstablecimientos()) {
				if (e.getFav() && e.getPrior() >= 0) {
					if (e.getLogo() != null) logos.add(e.getLogo());
					else logos.add(null); //Añado logos
					mNameList[i] = e.getNombre();
					eids[i] = e.getEid();
					i++;
				}
			}
			mAdapter = new IcsAdapter(ListaCompra.this, R.layout.spinner, mNameList);
			mIcsSpinner.setAdapter(mAdapter);
//			long l=0;
//			mIcsSpinner.setOnItemClickListener(this.onItemSelected(null,null,mIcsSpinner.getSelectedItemPosition(),l));
			mIcsSpinner.setOnItemSelectedListener(this);
		int k=0;
		if(!sharedPrefs.getString("main","0").equals("0")) for(;k<eids.length;k++)
			if(sharedPrefs.getString("main","0").equals(String.valueOf(eids[k])))
				if(mNameList[k]!=null) mIcsSpinner.setSelection(getIntent().getIntExtra("establecimiento",k));
				else mIcsSpinner.setSelection(0);
		if(getIntent().getIntExtra("establecimiento",0)==0 && getIntent().getIntExtra("eid",0)!=0)
			for(int j=0;j<eids.length;j++) if(eids[j]==getIntent().getIntExtra("eid",0))
				mIcsSpinner.setSelection(j);
		//mIcsSpinner.setOnItemSelectedListener(this);
		}
		idclif=getIntent().getIntExtra("idclif",0);
		greyFilter = new PorterDuffColorFilter(Color.LTGRAY, PorterDuff.Mode.DARKEN);
		if(getIntent().getBooleanExtra("auto",false)) auto=true;
	}

	@Override public void onItemSelected(AdapterView<?> av, View v, final int position, long id) {
		ver = 0;
		flagvc = 1; // XXX VistaControl
		//Comprobación de Ad-Día
		if (sharedPrefs.getString("lastaddate", "").compareTo(sdfdia.format(new Date())) < 0) {
			for (Est e : db.getAllEstablecimientos()) {
				if (e.getPrior() == 2) {
					e.setPrior(1);
					db.updateEstablecimiento(e);
				}
			}
			SharedPreferences.Editor spe = sharedPrefs.edit();
			spe.putString("lastaddate", sdfdia.format(new Date()));
			spe.commit();
		}
		ab.setTitle(getString(R.string.menu));
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
		if (mNameList[position].equals(getString(R.string.seleccionar))) {
			ab.setIcon(R.drawable.ic_launcher2);
			e = null; //Vacío e para que no entre en otros establecimientos al volver a Seleccionar
			mIcsSpinner.setOnLongClickListener(new OnLongClickListener() {
				@SuppressLint("NewApi")
				@Override
				public boolean onLongClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						/*if(android.os.Build.VERSION.SDK_INT>=11)
							new CMparalelo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
						else new CMparalelo().execute();*/
					if (sharedPrefs.getInt("internetmode", 0) == 0) {
						/*try{PendingIntent.getService(ListaCompra.this, 0, new Intent(
								ListaCompra.this, NotificacionMensajes.class), 0).send();
						} catch (CanceledException e) {e.printStackTrace();}*/
						startService(new Intent(ListaCompra.this, NotificacionMensajes.class));
					}
					return true;
				}
			});
			if (db.checkCD()) //Existe alguna cancha deportiva? Para mostrar botón reserva múltiples canchas.
				((ImageButton) findViewById(R.id.reserva)).setVisibility(View.VISIBLE);
			((ImageButton) findViewById(R.id.agregar)).setVisibility(View.VISIBLE);
			((ImageButton) findViewById(R.id.borrar)).setVisibility(View.GONE);
			((ImageButton) findViewById(R.id.mensaje)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.pedido)).setVisibility(View.GONE);
			((ImageButton) findViewById(R.id.anteriores)).setVisibility(View.GONE);
			((ImageButton) findViewById(R.id.carrocompra)).setVisibility(View.GONE);
			((RelativeLayout) findViewById(R.id.vistacontrol)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.base)).getBackground().setColorFilter(null);
			//Según la info que busque, volcarla aquí
			if (getIntent().getIntExtra("vienedereg", 0) != 1) contenidoVistaControl();
			else flagdoonce = 1;
			((ImageButton) findViewById(R.id.agregar)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) { //Crear una Lista
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
					popupView = layoutInflater.inflate(R.layout.popupsino, null);
					popupWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
					popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
						Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
						if (display.getRotation() == Surface.ROTATION_90)
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						else if (display.getRotation() == Surface.ROTATION_270) {
							if (android.os.Build.VERSION.SDK_INT == 8)
								setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
							else
								setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
						}
					} else {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
						popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, -100);
					}
					if (sharedPrefs.getString("tipo", "P").equals("P"))
						((TextView) popupView.findViewById(R.id.texto))
								.setText(getString(R.string.popuplistaparticular));
					else ((TextView) popupView.findViewById(R.id.texto))
							.setText(getString(R.string.popuplistaempresa));
					LinearLayout linealista = new LinearLayout(getBaseContext());
					final EditText nombrelista = (EditText) getLayoutInflater().inflate(R.layout.edittext, null);
					TextView txtlista = new TextView(getBaseContext());
					nombrelista.setEms(15);
					nombrelista.setTextColor(Color.BLACK);
					nombrelista.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
					txtlista.setText(getString(R.string.nombrelista));
					txtlista.setTextColor(Color.BLACK);
					linealista.addView(txtlista);
					linealista.addView(nombrelista);
					((LinearLayout) popupView.findViewById(R.id.cajatexto)).addView(linealista);
					final ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
					ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
					si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
					si.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (sharedPrefs.getBoolean("ch", true))
								v.performHapticFeedback(1);
							if (nombrelista.getText().toString().equals("") ||
									nombrelista.getText().toString().equals(getString(R.string.seleccionar))) {
								nombrelista.requestFocus();
								if (nombrelista.getText().toString().equals(getString(R.string.seleccionar)))
									Toast.makeText(getBaseContext(), getString(R.string.errornombreseleccionar),
											Toast.LENGTH_LONG).show();
							} else {
								int lle = db.getLastListEid();
								if (lle > 0) lle = 0;
								db.addEstablecimiento(new Est(lle - 1, 0, nombrelista.getText().toString(),
										"", "", "", "", false, false, 0, 0.0f, "", "", "", "", "",
										getString(R.string.tvgenerica), "", "", 0, "", "", "", "", "", "", "", "0", "A", "0"));
								popupWindow.dismiss();
								Intent intent = new Intent(ListaCompra.this, ListaCompra.class);
								//intent.putExtra("establecimiento",1);
								intent.putExtra("eid", lle - 1);
								startActivity(intent);
								finish();
							}
						}
					});
					no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
					no.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (sharedPrefs.getBoolean("ch", true) == true)
								v.performHapticFeedback(1);
							popupWindow.dismiss();
							Toast.makeText(getApplicationContext(), R.string.cancelalista, Toast.LENGTH_LONG).show();
						}
					});
					nombrelista.setOnKeyListener(new View.OnKeyListener() {
						public boolean onKey(View v, int keyCode, KeyEvent event) {
							if (event.getAction() == KeyEvent.ACTION_DOWN) {
								switch (keyCode) {
									case KeyEvent.KEYCODE_DPAD_CENTER:
									case KeyEvent.KEYCODE_ENTER:
										si.performClick();
										return true;
									default:
										break;
								}
							}
							return false;
						}
					});
				}
			});
			((ImageButton) findViewById(R.id.reserva)).setOnClickListener(reservaCD);
			((ImageButton) findViewById(R.id.mensaje)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					Intent i = new Intent(ListaCompra.this, Conversaciones.class);
					startActivity(i);
				}
			});
		} else if (eids[position] < 0) {// XXX Lista
			ab.setIcon(null);
			mIcsSpinner.setOnLongClickListener(null);
			eidactual = eids[position];
			flagvc = 0;
			flaglptotal = 0;
			flagop = 1;
			e = db.getEstablecimiento(eidactual); //Amago Listas sin CNAE
			// XXX Revisar por qué tarda tanto, si no hay cosas de tiendas =/
			ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
			((LinearLayout) findViewById(R.id.pedido)).setVisibility(View.VISIBLE);
			((ImageButton) findViewById(R.id.agregar)).setVisibility(View.VISIBLE);
			((ImageButton) findViewById(R.id.reserva)).setVisibility(View.GONE);
			((ImageButton) findViewById(R.id.anteriores)).setVisibility(View.GONE);
			((RelativeLayout) findViewById(R.id.vistacontrol)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.estado)).setVisibility(View.GONE);
			//((TextView)findViewById(R.id.nohaypendientes)).setText(getString(R.string.nhplista));
			((ImageButton) findViewById(R.id.agregar)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					contenidoBotonAdd();
				}
			});
			((ImageButton) findViewById(R.id.borrar)).setVisibility(View.VISIBLE);
			((ImageButton) findViewById(R.id.mensaje)).setVisibility(View.GONE);
			tl.setVisibility(View.GONE);
			((ImageButton) findViewById(R.id.borrar)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
							.getSystemService(LAYOUT_INFLATER_SERVICE);
					popupView = layoutInflater.inflate(R.layout.popupsino, null);
					popupWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
					popupWindow.setBackgroundDrawable(getResources().getDrawable(
							android.R.drawable.alert_light_frame));
					popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
						if (display.getRotation() == Surface.ROTATION_90)
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						else if (display.getRotation() == Surface.ROTATION_270) {
							if (android.os.Build.VERSION.SDK_INT == 8)
								setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
							else
								setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
						}
					} else
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					((TextView) popupView.findViewById(R.id.texto)).setText(R.string.txteditalista);
					LinearLayout linealista = new LinearLayout(getBaseContext());
					final EditText nombrelista = (EditText) getLayoutInflater().inflate(R.layout.edittext, null);
					TextView txtlista = new TextView(getBaseContext());
					nombrelista.setEms(15);
					nombrelista.setTextColor(Color.BLACK);
					nombrelista.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
							| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
					nombrelista.setText(db.getEstablecimiento(eidactual).getNombre());
					txtlista.setText(getString(R.string.nombrelista));
					txtlista.setTextColor(Color.BLACK);
					linealista.addView(txtlista);
					linealista.addView(nombrelista);
					((LinearLayout) popupView.findViewById(R.id.cajatexto)).addView(linealista);
					final ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
					//si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
					si.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (sharedPrefs.getBoolean("ch", true))
								v.performHapticFeedback(1);
							if (nombrelista.getText().toString().equals(""))
								nombrelista.requestFocus();
							else {
								if (nombrelista.getText().toString().equals(getString(R.string.seleccionar)))
									Toast.makeText(getBaseContext(), getString(R.string.errornombreseleccionar),
											Toast.LENGTH_LONG).show();
								else {
									Est e = db.getEstablecimiento(eidactual);
									e.setNombre(nombrelista.getText().toString());
									db.updateEstablecimiento(e);
									popupWindow.dismiss();
									Intent intent = new Intent(ListaCompra.this, ListaCompra.class);
									//intent.putExtra("establecimiento",mIcsSpinner.getSelectedItemPosition());
									intent.putExtra("eid", eidactual);
									startActivity(intent);
									finish();
								}
							}
						}
					});
					ImageButton borrar = new ImageButton(getBaseContext());
					borrar.setLayoutParams(new LayoutParams(
							si.getLayoutParams().width, si.getLayoutParams().height));
					borrar.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
					borrar.setImageDrawable(getResources().getDrawable(R.drawable.content_discard));
					//borrar.setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
					LinearLayout.LayoutParams posboton = new LinearLayout.LayoutParams(
							si.getLayoutParams());
					posboton.weight = 1;
					borrar.setLayoutParams(posboton);
					borrar.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (sharedPrefs.getBoolean("ch", true))
								v.performHapticFeedback(1);
							final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
									.getSystemService(LAYOUT_INFLATER_SERVICE);
							popupView = layoutInflater.inflate(R.layout.popupsino, null);
							popupWindow2 = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
									android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
							popupWindow2.setBackgroundDrawable(getResources().getDrawable(
									android.R.drawable.alert_light_frame));
							popupWindow2.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
							if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
								Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
								if (display.getRotation() == Surface.ROTATION_90)
									setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
								else if (display.getRotation() == Surface.ROTATION_270) {
									if (android.os.Build.VERSION.SDK_INT == 8)
										setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
									else
										setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
								}
							} else
								setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
							((TextView) popupView.findViewById(R.id.texto)).setText(getString(R.string.borrarlista));
							//Molaría incluir cuántos artículos no están marcados en la lista
							ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
							si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
							si.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									popupWindow2.dismiss();
									if (sharedPrefs.getBoolean("ms", false) == true) {
										popupView = layoutInflater.inflate(R.layout.popupms, null);
										popupWindow2 = new PopupWindow(popupView,
												android.view.ViewGroup.LayoutParams.MATCH_PARENT,
												android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
										popupWindow2.setBackgroundDrawable(getResources().getDrawable(
												android.R.drawable.alert_light_frame));
										popupWindow2.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
										((TextView) popupView.findViewById(R.id.texto)).setText((((TextView) popupView
												.findViewById(R.id.texto)).getText()) + " " + getString(R.string.motivo2));
										ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
										ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
										si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
										no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
										si.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												if (sharedPrefs.getBoolean("ch", true))
													v.performHapticFeedback(1);
												if (((EditText) popupView.findViewById(R.id.mspass)).getText()
														.toString().equals(sharedPrefs.getString("pass", ""))) {
													if (sharedPrefs.getBoolean("ch", true))
														v.performHapticFeedback(1);
													List<Ped> articuloslista = db.getAllPedidos
															(db.getEstablecimiento(mNameList[position]).getEid());
													if (!articuloslista.isEmpty())
														for (Ped p : articuloslista) {
															db.deleteArticuloPedido(p);
															db.deleteArticuloEstablecimiento(p.getAid(), e.getEid());
															db.deleteArticulo(db.getArticulo(p.getAid()));
														}
													List<Ped> articuloslistaAnt = db.getAllPedidosAnt
															(db.getEstablecimiento(mNameList[position]).getEid());
													if (!articuloslistaAnt.isEmpty())
														for (Ped p : articuloslistaAnt) {
															db.deleteArticuloPedidoAnt(p);
															db.deleteArticuloEstablecimiento(p.getAid(), e.getEid());
															db.deleteArticulo(db.getArticulo(p.getAid()));
														}
													db.deleteEstablecimiento(db.getEstablecimiento(mNameList[position]));
													Intent intent = new Intent(ListaCompra.this, ListaCompra.class);
													startActivity(intent);
													finish();
												} else
													((EditText) popupView.findViewById(R.id.mspass)).requestFocus();
											}
										});
										no.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												if (sharedPrefs.getBoolean("ch", true))
													v.performHapticFeedback(1);
												popupWindow2.dismiss();
												Toast.makeText(getApplicationContext(), R.string.cancelaborrarlista, Toast.LENGTH_LONG).show();
												//Toast.makeText(getApplicationContext(),getString(R.string.cancel2),Toast.LENGTH_LONG).show();
											}
										});
									} else {
										List<Ped> articuloslista = db.getAllPedidos
												(db.getEstablecimiento(mNameList[position]).getEid());
										if (!articuloslista.isEmpty())
											for (Ped p : articuloslista) {
												db.deleteArticuloPedido(p);
												db.deleteArticuloEstablecimiento(p.getAid(), e.getEid());
												db.deleteArticulo(db.getArticulo(p.getAid()));
											}
										List<Ped> articuloslistaAnt = db.getAllPedidosAnt
												(db.getEstablecimiento(mNameList[position]).getEid());
										if (!articuloslistaAnt.isEmpty())
											for (Ped p : articuloslistaAnt) {
												db.deleteArticuloPedidoAnt(p);
												db.deleteArticuloEstablecimiento(p.getAid(), e.getEid());
												try {
													db.deleteArticulo(db.getArticulo(p.getAid()));
												} catch (Exception e) {
													Log.e("BD", "" + p.getAid() + " ya no existe");
												}
											}
										db.deleteEstablecimiento(db.getEstablecimiento(mNameList[position]));
										Intent intent = new Intent(ListaCompra.this, ListaCompra.class);
										startActivity(intent);
										finish();
									}
								}
							});
							ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
							no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
							no.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (sharedPrefs.getBoolean("ch", true))
										v.performHapticFeedback(1);
									popupWindow2.dismiss();
									Toast.makeText(getApplicationContext(), R.string.cancelaborrarlista, Toast.LENGTH_LONG).show();
									//Toast.makeText(getApplicationContext(),getString(R.string.cancel2),Toast.LENGTH_LONG).show();
								}
							});
						}
					});
					LinearLayout botones = (LinearLayout) popupView.findViewById(R.id.botones);
					botones.addView(borrar, botones.indexOfChild(si) + 1);
					//((LinearLayout)popupView.findViewById(R.id.cajatexto)).addView(borrar);
					ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
					//no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
					no.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (sharedPrefs.getBoolean("ch", true))
								v.performHapticFeedback(1);
							popupWindow.dismiss();
						}
					});
					nombrelista.setOnKeyListener(new View.OnKeyListener() {
						public boolean onKey(View v, int keyCode, KeyEvent event) {
							if (event.getAction() == KeyEvent.ACTION_DOWN) {
								switch (keyCode) {
									case KeyEvent.KEYCODE_DPAD_CENTER:
									case KeyEvent.KEYCODE_ENTER:
										si.performClick();
										return true;
									default:
										break;
								}
							}
							return false;
						}
					});
				}
			});
			List<Ped> lista = db.getPedidosPendientes(eidactual);
			if (!lista.isEmpty()) {
				tl.removeViews(1, tl.getChildCount() - 1);
				mostrarPedido(lista);
				ver = 1;
				tl.setVisibility(View.VISIBLE);
				bg.setVisibility(View.GONE);
				((LinearLayout) findViewById(R.id.base)).getBackground().setColorFilter(null);
			} else {
				if (sharedPrefs.getBoolean("bg", false)) {
					bg.setVisibility(View.VISIBLE);
					bg.setImageDrawable(getResources().getDrawable(R.drawable.content_new));
					bg.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							contenidoBotonAdd();
						}
					});
					((LinearLayout) findViewById(R.id.base)).getBackground().setColorFilter(greyFilter);
				}
			}
		} else { // XXX Pedidos
			ab.setIcon(null);
			mIcsSpinner.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					Intent i = new Intent(ListaCompra.this, Establecimiento.class);
					i.putExtra("eid", eidactual);
					i.putExtra("tipo", 3);
					startActivity(i);
					return false;
				}
			});
			flagvc = 0;
			ab.setTitle(getString(R.string.mipedido));
			ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
			((LinearLayout) findViewById(R.id.pedido)).setVisibility(View.VISIBLE);
			((ImageButton) findViewById(R.id.mensaje)).setVisibility(View.VISIBLE);
			((ImageButton) findViewById(R.id.mensaje)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					final Msj pend = db.mensajePendiente();
					if (pend != null) db.deleteMensajeEnviado(pend);
						/* Mensajes -> Chat
						Intent i = new Intent(ListaCompra.this, Mensajes.class);
							i.putExtra("tipo", 2).putExtra("eid", eidactual); startActivity(i); }});*/
					Intent i = new Intent(ListaCompra.this, Chat.class);
					i.putExtra("eid", eidactual);
					startActivity(i);
				}
			});
			((RelativeLayout) findViewById(R.id.vistacontrol)).setVisibility(View.GONE);
			((ImageButton) findViewById(R.id.borrar)).setVisibility(View.GONE);
			//Estado y otra información

			eidactual = eids[position];
			e = db.getEstablecimiento(eidactual);

			if (e.getConfigura() != null && e.getConfigura().contains(",C,"))
				sobre = R.drawable.content_email_clip;
			else sobre = R.drawable.content_email;
			((ImageButton) findViewById(R.id.mensaje)).setImageDrawable(getResources().getDrawable(sobre));

			if (eidactual == sharedPrefs.getInt("solicitacliest", 0)) {
				flagop = 1;
				ab.setSubtitle(sharedPrefs.getString("solicitaclinom", ""));
			} else {
				flagop = 0;
				ab.setSubtitle(null);
			}
			TextView estado = (TextView) findViewById(R.id.estado);
			final TableLayout tl = (TableLayout) findViewById(R.id.tl);
			//TextView nhp = (TextView) findViewById(R.id.nohaypendientes);

			//Botonaco
			if (sharedPrefs.getBoolean("bg", false)) {
				bg.setVisibility(View.VISIBLE);
				((LinearLayout) findViewById(R.id.base)).getBackground().setColorFilter(greyFilter);
				//if(e.getCnae() != null && e.getCnae().contains("52120")){//Tipo Cancha Deportiva
				if (e.getConfigura() != null && e.getConfigura().contains(",R,")) {
					bg.setImageDrawable(getResources().getDrawable(R.drawable.tennis2));
					bg.setOnClickListener(reservaCD);
				} else if (e.getCnae() != null && e.getCnae().contains("58100")) { //Tipo Asesoría
					bg.setImageDrawable(getResources().getDrawable(sobre));
					bg.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (sharedPrefs.getBoolean("ch", true))
								v.performHapticFeedback(1);
							final Msj pend = db.mensajePendiente();
							if (pend != null) db.deleteMensajeEnviado(pend);
							/* Mensajes -> Chat
							Intent i = new Intent(ListaCompra.this, Mensajes.class);
								i.putExtra("tipo", 2).putExtra("eid", eidactual); startActivity(i); }});*/
							Intent i = new Intent(ListaCompra.this, Chat.class);
							i.putExtra("eid", eidactual);
							startActivity(i);
						}
					});
					//}else{ //Genérico: +
				} else if (e.getConfigura() != null && e.getConfigura().contains(",+,")) {
					bg.setImageDrawable(getResources().getDrawable(R.drawable.content_new));
					bg.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							contenidoBotonAdd();
						}
					});
				}
			} else {
				bg.setVisibility(View.GONE);
				((LinearLayout) findViewById(R.id.base)).getBackground().setColorFilter(null);
			}

			List<Ped> pendientes = db.getPedidosPendientes(eidactual);


			//Ad prioritario (Cinterno)
			try {
				Date ddhasta = new Date(); //El día siguiente
				ddhasta.setTime(sdfdia.parse(e.getCih()).getTime() + 86400000);
				if (e.getPrior() == 1 && sdfdia.parse(e.getCid()).before(new Date())
						&& ddhasta.after(new Date())) {
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
						if (display.getRotation() == Surface.ROTATION_90)
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						else if (display.getRotation() == Surface.ROTATION_270) {
							if (android.os.Build.VERSION.SDK_INT == 8)
								setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
							else
								setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
						}
					} else
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
					popupView = layoutInflater.inflate(R.layout.popupad, null);
					popupWindow = new PopupWindow(popupView,
							android.view.ViewGroup.LayoutParams.MATCH_PARENT,
							android.view.ViewGroup.LayoutParams.MATCH_PARENT, true);
					popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
					popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
					WebView wv = (WebView) popupView.findViewById(R.id.webView);
					if (e.getCinterno() != null && !e.getCinterno().equals(""))
						wv.loadDataWithBaseURL("", e.getCinterno(), "text/html", "UTF-8", "");
					e.setPrior(2);
					db.updateEstablecimiento(e);
					((ImageButton) popupView.findViewById(R.id.cerrar)).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (sharedPrefs.getBoolean("ch", true))
								v.performHapticFeedback(1);
							popupWindow.dismiss();
						}
					});
				}
			} catch (NotFoundException e1) {
			} catch (ParseException e1) {
			} catch (NullPointerException e1) {
			}

			//Caso Asesoría
			//if(e.getCnae() != null && e.getCnae().contains("58100")){
			if (db.getEstablecimiento(eidactual).getTv() != null &&
					!db.getEstablecimiento(eidactual).getTv().equals("") && eidactual > 0) {
				((ImageButton) findViewById(R.id.carrocompra)).setVisibility(View.VISIBLE);
				((ImageButton) findViewById(R.id.carrocompra)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true))
							v.performHapticFeedback(1);
						if (sharedPrefs.getInt("internetmode", 0) == 2)
							Toast.makeText(getBaseContext(), "No puede acceder a internet con el modo Offline activo.", Toast.LENGTH_LONG).show();
						else {
							if (e.getEid() == sharedPrefs.getInt("solicitacliest", 0) &&
									e.getConfigura().contains(",V,")) {
								new descargaClientes(TiendaVirtual.class).execute();
							} else {
								Intent intent = new Intent(ListaCompra.this, TiendaVirtual.class);
								intent.putExtra("establecimiento", mIcsSpinner.getSelectedItemPosition());
								intent.putExtra("eid", eidactual);
								startActivity(intent);
								finish();
							}
						}
					}
				});
			} else
				((ImageButton) findViewById(R.id.carrocompra)).setVisibility(View.GONE);
			if (e.getConfigura() != null && !e.getConfigura().contains(",+,")) {
				((ImageButton) findViewById(R.id.agregar)).setVisibility(View.GONE);
				estado.setVisibility(View.GONE);
			} else {
				((ImageButton) findViewById(R.id.agregar)).setVisibility(View.VISIBLE);
				((ImageButton) findViewById(R.id.agregar)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true))
							v.performHapticFeedback(1);
						contenidoBotonAdd();
					}
				});
				estado.setVisibility(View.VISIBLE);
			}

			//Caso Cancha Deportiva
			//if(e.getCnae() != null && e.getCnae().contains("52120")){
			if (e.getConfigura() != null && e.getConfigura().contains(",R,")) {
				((ImageButton) findViewById(R.id.reserva)).setVisibility(View.VISIBLE);
				((ImageButton) findViewById(R.id.reserva)).setOnClickListener(reservaCD);
			} else ((ImageButton) findViewById(R.id.reserva)).setVisibility(View.GONE);

			if (!pendientes.isEmpty()) {
				flaglptotal = 1;
				if (pendientes.get(0).getEstado() == 0 || pendientes.get(0).getEstado() == -1) {
					if (idclif != 0)
						try {
							estado.setText(getString(R.string.pp) + " (" +
									db.buscaClienteF(idclif).getNombre() + ")");
						} catch (Exception e) {
							estado.setText(getString(R.string.pp));
						}
					else if (e.getEid() == sharedPrefs.getInt("solicitacliest", 0) &&
							e.getConfigura().contains(",V,") && db.countIdclif(e.getEid()).length == 1)
						//Sólo hay un cliente, mostrarlo directamente
						try {
							idclif = db.countIdclif(e.getEid())[0];
							//Log.e("TRYIDCLIF",""+idclif);
							estado.setText(getString(R.string.pp) + " (" +
									db.buscaClienteF(db.countIdclif(e.getEid())[0]).getNombre() + ")");
						} catch (Exception e) {
							estado.setText(getString(R.string.pp));
						}
					else estado.setText(getString(R.string.pp));
					estado.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));
					estado.setTextColor(getResources().getColor(R.color.azulDisoft1));
				} else if (pendientes.get(0).getEstado() == 1 || pendientes.get(0).getEstado() == -2) {
					estado.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));
					estado.setText(getString(R.string.pc));
					estado.setTextColor(Color.RED);
				}
				tl.removeViews(1, tl.getChildCount() - 1);
				//Condición autoventa con OP
				if (e.getEid() == sharedPrefs.getInt("solicitacliest", 0) &&
						e.getConfigura().contains(",V,")) {
					if (idclif == 0) {
						int[] nidclif = db.countIdclif(e.getEid());
						if (nidclif.length == 1) idclif = nidclif[0];
						else if (nidclif.length > 1) {
							Toast.makeText(getBaseContext(), "Elija un cliente anterior desde \"Seleccionar\"",
									Toast.LENGTH_LONG).show();
							Intent intent = new Intent(ListaCompra.this, ListaCompra.class);
							intent.putExtra("establecimiento", 0);
							startActivity(intent);
							finish();
						}
						List<Ped> pedclientef = new ArrayList<Ped>();
						for (Ped p : pendientes)
							if (p.getIdclif() == idclif)
								pedclientef.add(p);
						mostrarPedido(pedclientef);
					} else {
						List<Ped> pedclientef = new ArrayList<Ped>();
						for (Ped p : pendientes)
							if (p.getIdclif() == idclif)
								pedclientef.add(p);
						mostrarPedido(pedclientef);
						try {
							estado.setText(getString(R.string.pp) + " (" +
									db.buscaClienteF(idclif).getNombre() + ")");
						} catch (Exception e) {
							estado.setText(getString(R.string.pp));
						}
					}
				} else mostrarPedido(pendientes);
				tl.setVisibility(View.VISIBLE);
				estado.setVisibility(View.VISIBLE);
				bg.setVisibility(View.GONE);
				((LinearLayout) findViewById(R.id.base)).getBackground().setColorFilter(null);
				//nhp.setText(getString(R.string.nhppedidopendiente));
				ver = 1;
			}
			final List<Ped> anterior = db.getPedidoAnt(eidactual);
			int p0 = 0;
			if (anterior != null) for (Ped p : anterior) if (p.getPid() == 0) p0++;
			if (anterior != null && pendientes.isEmpty() && anterior.size() != p0 &&
					eidactual != sharedPrefs.getInt("solicitacliest", 0)) {
				//ll.removeAllViews(); ver=0;
				((ImageButton) findViewById(R.id.anteriores)).setVisibility(View.VISIBLE);
				((ImageButton) findViewById(R.id.anteriores)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true))
							v.performHapticFeedback(1);

						if (e.getEid() == sharedPrefs.getInt("solicitacliest", 0) &&
								e.getConfigura().contains(",V,"))
							new descargaClientes(ListaAnterior.class).execute();
						else {
							Intent intent = new Intent(ListaCompra.this, ListaAnterior.class);
							intent.putExtra("establecimiento", mIcsSpinner.getSelectedItemPosition());
							intent.putExtra("eid", eidactual);
							startActivity(intent);
							finish();
						}
					}
				});
			} else {
				((ImageButton) findViewById(R.id.anteriores)).setVisibility(View.GONE);
				((ImageButton) findViewById(R.id.agregar)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true))
							v.performHapticFeedback(1);
						contenidoBotonAdd();
					}
				});
			}
			if (ver == 0) {
				//nhp.setVisibility(View.VISIBLE);
				Bitmap bm1 = BitmapFactory.decodeResource(getResources(), sobre),
						bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.content_new);
				SpannableStringBuilder ssb;
				if (e.getCnae() == null || !e.getCnae().contains("58100")) {
					ssb = new SpannableStringBuilder(getString(R.string.nohaypedidos) + "  ");
					ssb.setSpan(new ImageSpan(bm1), 33, 34, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					ssb.setSpan(new ImageSpan(bm2), 67, 68, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					ssb.setSpan(new String("  "), 51, 52, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					ssb.setSpan(new String("  "), 59, 60, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				} else {
					ssb = new SpannableStringBuilder(getString(R.string.nohaypedidosasesoria) + "  ");
					ssb.setSpan(new ImageSpan(bm1), 33, 34, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					//ssb.setSpan(new String("  "), 51, 52, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				}
				//nhp.setText(ssb, BufferType.SPANNABLE);
				tl.setVisibility(View.GONE);
				estado.setVisibility(View.GONE);
			}
		}
		ordenadores();
	}

	@Override public void onNothingSelected(AdapterView<?> parent) { }

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if(android.os.Build.VERSION.SDK_INT < 14) {
			inflater.inflate(R.menu.listacompra, menu);
			SubMenu subMenu1 = menu.addSubMenu("");

		/*subMenu1.add(getString(R.string.mensajes)).setIcon(R.drawable.content_email)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override public boolean onMenuItemClick(MenuItem item) {
						if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
						//startActivity(new Intent(ListaCompra.this,Mensajes.class)); return false; }});
						startActivity(new Intent(ListaCompra.this,Conversaciones.class)); return false; }});*/

			subMenu1.add(getString(R.string.establecimientos)).setIcon(R.drawable.social_group);
			((SupportMenuItem) subMenu1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
					startActivity(new Intent(ListaCompra.this, ListaEstablecimientos.class));
					return false;
				}
			});

			subMenu1.add(getString(R.string.opciones)).setIcon(R.drawable.action_settings);
			((SupportMenuItem) subMenu1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
					startActivity(new Intent(ListaCompra.this, Opciones.class));
					return false;
				}
			});

			subMenu1.add(getString(R.string.ayuda)).setIcon(R.drawable.action_help);
			((SupportMenuItem) subMenu1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
					if (sharedPrefs.getInt("internetmode", 0) == 2)
						Toast.makeText(getBaseContext(), "No puede acceder a internet con el modo Offline activo.", Toast.LENGTH_LONG).show();
					else {
						//startActivity(new Intent(ListaCompra.this, Ayuda.class));
						startActivity(new Intent(Intent.ACTION_VIEW).setData(
								Uri.parse("http://www.distarea.es/video.html")));
					}
					return false;
				}
			});

			MenuItem subMenu1Item = subMenu1.getItem();
			subMenu1Item.setIcon(R.drawable.menu_overflow_light);
			subMenu1Item.setTitle(R.string.menutitle);
//		subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}else inflater.inflate(R.menu.listacomprav14, menu);
		m = menu;
		if(sharedPrefs.getInt("internetmode", 0)==0)
			m.getItem(1).setVisible(false);
		else m.getItem(0).setVisible(false);

		return true;
	}

	@SuppressLint("NewApi")
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
		if (item.getItemId() == android.R.id.home){
			Intent i = new Intent(ListaCompra.this, ListaCompra.class);
			i.putExtra("establecimiento", 0);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i); finish(); }
		else {if(android.os.Build.VERSION.SDK_INT >= 14){
			if (item.getItemId() == R.id.scanner) {
				/*try{Intent intent = new Intent("com.google.zxing.client.android.SCAN");
					intent.setPackage("com.disoft.distarea");
					intent.putExtra("SCAN_MODE","QR_CODE_MODE");
					startActivityForResult(intent, 10);
				} catch (Exception e) {e.printStackTrace();}*/
				try{
					IntentIntegrator integrator = new IntentIntegrator(ListaCompra.this);
					integrator.initiateScan();
				} catch (Exception e) {e.printStackTrace();}
				// return true;
			}else if(item.getItemId() == R.id.offline){
				if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
				Toast.makeText(getBaseContext(), "Modo Offline", Toast.LENGTH_LONG).show();
				sharedPrefs.edit().putInt("internetmode", 2).commit();
				stopService(new Intent(ListaCompra.this, NotificacionMensajes.class));
				//m.getItem(0).setIcon(R.drawable.dc_l);
				m.getItem(1).setVisible(true);
				m.getItem(0).setVisible(false);
				 //return true;
			}else if(item.getItemId() == R.id.online){
				if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
				Toast.makeText(getBaseContext(), "Conexión reestablecida", Toast.LENGTH_LONG).show();
				sharedPrefs.edit().putInt("internetmode", 0).commit();
				startService(new Intent(ListaCompra.this, NotificacionMensajes.class));
				//m.getItem(0).setIcon(R.drawable.conn2_l);
				m.getItem(1).setVisible(false);
				m.getItem(0).setVisible(true);
				// return true;
			}
			/*else if(item.getItemId() == R.id.mensajes){
				if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
				//startActivity(new Intent(ListaCompra.this,Mensajes.class)); return true; }
				startActivity(new Intent(ListaCompra.this,Conversaciones.class)); return true; }*/
			else if(item.getItemId() == R.id.establecimientos){
				if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
				startActivity(new Intent(ListaCompra.this,ListaEstablecimientos.class));
				return true; }
			else if(item.getItemId() == R.id.configuracion){
				if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
				startActivity(new Intent(ListaCompra.this,Opciones.class)); return true;}
			else if(item.getItemId() == R.id.ayuda){
				if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
				//startActivity(new Intent(ListaCompra.this, Ayuda.class));
				startActivity(new Intent(Intent.ACTION_VIEW).setData(
						Uri.parse("http://www.facdis.es/videos.html#")));
				return true;} }
			else return false;
		}
		return true;
	}

	@Override public boolean onKeyUp(int keyCode, KeyEvent event)  {
  	if(keyCode == KeyEvent.KEYCODE_MENU){
  		if(android.os.Build.VERSION.SDK_INT >= 14)
			((Menu)m).performIdentifierAction(R.id.overflow, 0); //Crearlo en los xml
  		else{ SubMenu subMenu = m.getItem(m.size()-1).getSubMenu();
        m.performIdentifierAction(subMenu.getItem().getItemId(), 0); }
  		return true;} return super.onKeyUp(keyCode, event);}

	@Override public void onBackPressed() {
		super.onBackPressed();
    if(flagvc==0){
    	Intent i = new Intent(ListaCompra.this, ListaCompra.class);
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
      i.putExtra("establecimiento", 0);
		startActivity(i); finish(); }}

	@Override public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode,resultCode,intent);
		if (requestCode == 0) { // Llamada desde añadir a un pedido

			if (resultCode == RESULT_OK) {
				EditText cbarras = (EditText) popupView.findViewById(R.id.cbarras);
				if (db.getArticulosCount() > 0) {
					Art artcodb = db.getArticulo(intent.getStringExtra("SCAN_RESULT"));
					if (artcodb != null) {
						EditText articulo = (EditText) popupView.findViewById(R.id.articulo);
						Spinner tipo = (Spinner) popupView.findViewById(R.id.tipo);
						articulo.setText(artcodb.getArticulo());
						tipo.setSelection(((ArrayAdapter<String>) tipo
							.getAdapter()).getPosition(artcodb.getTipo()));
						popupView.findViewById(R.id.unidades).requestFocus();
					} else
						popupView.findViewById(R.id.articulo).requestFocus();
				}
				//if(popupView.findViewById(R.id.avanzado).getDrawableState())
				cbarras.setText(intent.getStringExtra("SCAN_RESULT"));
				//cbarras.setVisibility(View.VISIBLE);
			}
		} else if (requestCode == 1) { // Llamada desde editar un artículo pedido

			if (resultCode == RESULT_OK) {
				EditText cbarras = (EditText) popupView.findViewById(R.id.popartpedcbarras);
				cbarras.setText(intent.getStringExtra("SCAN_RESULT")); }
		}

		else if (requestCode == 3) { // Llamada desde buscar un artículo
																 // dentro de un pedido anterior

			if (resultCode == RESULT_OK) {
				((EditText) popupView.findViewById(R.id.cadena)).setText(intent
						.getStringExtra("SCAN_RESULT"));}
		}

		else if (requestCode == 5) { //Llamada a VentanaFirma
			if (resultCode == RESULT_OK){ flagfirma=2;
				nfirma=intent.getStringExtra("result"); }}

		else if (requestCode == 6) { //Llamada a Voz a Texto
			if (resultCode == RESULT_OK && null != intent) {
      	ArrayList<String> text = intent
      			.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
      	artarticulo.setText(text.get(0).substring(0,1).toUpperCase(spanish)
      			+ text.get(0).substring (1)); }}

		else if (requestCode == 7) { //Llamada a Voz a Texto (Add -> Observación)
	  		if (resultCode == RESULT_OK && null != intent) {
	  			ArrayList<String> text = intent
	  				.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	  			artobservaciones.setText(text.get(0).substring(0,1).toUpperCase(spanish)
	    			+ text.get(0).substring (1)); }}
		else if (requestCode == 8) { //Llamada a Voz a Texto (PopupArticuloPedido -> Nota)
	  		if (resultCode == RESULT_OK && null != intent) {
	  			ArrayList<String> text = intent
	  				.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	  			artpedobs.setText(text.get(0).substring(0,1).toUpperCase(spanish)
	    			+ text.get(0).substring (1)); }}
		//else if (requestCode == 10) { //Escanear QR en ListaCompra (multiusos)
		else if (requestCode == IntentIntegrator.REQUEST_CODE){
			if (resultCode == RESULT_OK && null != intent) {
				new trataQR(intent.getStringExtra("SCAN_RESULT")).execute();}}

		else if (resultCode == RESULT_CANCELED) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
	}

	private static class ViewHolder{ TextView spinnerText; ImageView logo; ProgressBar loading; }

	public class IcsAdapter extends ArrayAdapter<String> {
		String[] nameList = new String[mNameList.length];

		public IcsAdapter(Context context, int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
			this.nameList = objects;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {

			View view = getCustomView(position, convertView, parent);
			DisplayMetrics m = getResources().getDisplayMetrics();
			//parent.setLayoutParams(new LayoutParams(
			//(int)(m.widthPixels-TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, m)),
			//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			//parent.setBackgroundColor(getResources().getColor(R.color.azulDisoft3)); //(Todos de azul)
			if (mIcsSpinner.getSelectedItemPosition() == position)
				view.setBackgroundColor(getResources().getColor(R.color.verdeDisoft3));
			else view.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));
			return view;
		}

		@Override
		public void setNotifyOnChange(boolean notifyOnChange) {
			super.setNotifyOnChange(notifyOnChange);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@SuppressLint("NewApi")
		public View getCustomView(int position, View row, ViewGroup parent) {
			ViewHolder vh;
			if (row == null) {
				row = getLayoutInflater().inflate(R.layout.spinner, parent, false);
				vh = new ViewHolder();
				vh.spinnerText = (TextView) row.findViewById(R.id.name_view);
				vh.logo = (ImageView) row.findViewById(R.id.iconospinner);
				vh.loading = (ProgressBar) row.findViewById(R.id.loading);
				row.setTag(vh);
			} else vh = (ViewHolder) row.getTag();

			if (position < mNameList.length) {
				String cateName = mNameList[position];
				vh.spinnerText.setText(cateName);
				if (android.os.Build.VERSION.SDK_INT >= 11)
					new loadSpinner(row, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
				else new loadSpinner(row, position).execute();
			}
			row.setPadding(0, 10, 0, 10);
			//row.setLayoutParams(new AbsListView.LayoutParams(120,LayoutParams.WRAP_CONTENT));
			return row;
		}

	}

	OnClickListener reservaCD = new OnClickListener() { @Override public void onClick(View v) { //Provisional -> siempre aquí.
		if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
		/*final Calendar c = Calendar.getInstance(), r = Calendar.getInstance();
		r.add(Calendar.DAY_OF_MONTH, 1);
		hourini = 6; minuteini = 0;
		if(minuteini<10) cminuteini="0"+minuteini;
		else cminuteini=String.valueOf(minuteini);
		final AlertDialog alertDialog;
		AlertDialog.Builder builder;
		LayoutInflater inflater = LayoutInflater.from(ListaCompra.this);
		final View layout = inflater.inflate(R.layout.dreserva, null);
		//final TimePicker hc = (TimePicker)layout.findViewById(R.id.horacomienzo);
		//final TimePicker hf = (TimePicker)layout.findViewById(R.id.horafinalizacion);
		((TextView)layout.findViewById(R.id.cabecera)).setText("Reserva cancha deportiva");
		((Button)layout.findViewById(R.id.fecha)).setText(r.get(Calendar.DAY_OF_MONTH)+
				"/"+(r.get(Calendar.MONTH)+1)+"/"+r.get(Calendar.YEAR));
		((Button)layout.findViewById(R.id.horainicio)).setText(hourini+":"+cminuteini);
		((ImageButton)layout.findViewById(R.id.flechaizfecha)).setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v){
				if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
				r.add(Calendar.DAY_OF_MONTH,-1);
				((Button)layout.findViewById(R.id.fecha)).setText(r.get(Calendar.DAY_OF_MONTH)+
						"/"+(r.get(Calendar.MONTH)+1)+"/"+r.get(Calendar.YEAR));
				if(r.getTime().equals(c.getTime())){ //XXX REVISAR FECHA PARA SELECTOR
					((ImageButton)v.findViewById(R.id.flechaizfecha)).setEnabled(false);
					if(r.get(Calendar.HOUR_OF_DAY)<c.get(Calendar.HOUR_OF_DAY)){
						if(r.get(Calendar.MINUTE)<c.get(Calendar.MINUTE)){
							if(c.getMaximum(Calendar.MINUTE)>=r.get(Calendar.MINUTE)+30){ r.set(Calendar.MINUTE,0);
								if(c.getActualMaximum(Calendar.HOUR_OF_DAY)>=r.get(Calendar.HOUR_OF_DAY)+1)
									r.set(Calendar.HOUR_OF_DAY,0);
								else r.add(Calendar.HOUR_OF_DAY,1);
							}else{
								r.set((Calendar.HOUR_OF_DAY), c.get(Calendar.HOUR_OF_DAY));
								r.set((Calendar.MINUTE), 30); }
						}
					}((Button)layout.findViewById(R.id.horainicio)).setText(r.get(Calendar.HOUR_OF_DAY)+
							":"+r.get(Calendar.MINUTE));
				}
			}});
			((ImageButton)layout.findViewById(R.id.flechadefecha)).setOnClickListener(new OnClickListener(){
				@Override public void onClick(View v){
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					((ImageButton)layout.findViewById(R.id.flechaizfecha)).setEnabled(true);
					r.add(Calendar.DAY_OF_MONTH,1);
					((Button)layout.findViewById(R.id.fecha)).setText(r.get(Calendar.DAY_OF_MONTH)+
							"/"+(r.get(Calendar.MONTH)+1)+"/"+r.get(Calendar.YEAR));
			}});
			((Button)layout.findViewById(R.id.fecha)).setOnClickListener(new OnClickListener(){
				@Override public void onClick(View v){
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					//Llamar ventana calendario.
			}});
			((Button)layout.findViewById(R.id.horainicio)).setOnClickListener(new OnClickListener(){
				@SuppressLint("NewApi")
				@Override public void onClick(View v){
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					//Llamar ventana de selección de hora.
					final Dialog dialog = new Dialog(ListaCompra.this);
					dialog.setCancelable(false);
					dialog.setTitle("Hora inicial");
					dialog.setContentView(R.layout.dhora);
					final TimePicker hc = (TimePicker)dialog.findViewById(R.id.horacomienzo);
					hc.setIs24HourView(true);
					hc.setCurrentHour(Integer.parseInt(((Button)layout.findViewById(R.id.horainicio))
							.getText().toString().split(":")[0]));
					hc.setCurrentMinute(Integer.parseInt(((Button)layout.findViewById(R.id.horainicio))
							.getText().toString().split(":")[1]));
					((Button)dialog.findViewById(R.id.aceptar)).setOnClickListener(
							new OnClickListener(){ public void onClick(View v) {
								if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
								hourini=hc.getCurrentHour();
								if(hc.getCurrentMinute()>=30)minuteini=30;
								else minuteini=0;
								if(minuteini==0) cminuteini="0"+minuteini;
								else cminuteini=""+minuteini;
								((Button)layout.findViewById(R.id.horainicio)).setText(hourini+":"+cminuteini);
								dialog.dismiss();
					}});
					((Button)dialog.findViewById(R.id.cancelar)).setOnClickListener(new OnClickListener(){ public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
						dialog.dismiss(); }});
					dialog.show();
			}});
		builder = new AlertDialog.Builder(ListaCompra.this);
		builder.setCancelable(false); builder.setView(layout);
		alertDialog = builder.create(); //XXX SEGUIR CON RESERVAS (DISEÑO)
		((ImageButton)layout.findViewById(R.id.cerrar)).setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v){
				if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
				alertDialog.dismiss(); }});
		((Button)layout.findViewById(R.id.aceptar)).getBackground()
			.setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
		alertDialog.show();*/
		if(sharedPrefs.getInt("internetmode", 0)==2)
			Toast.makeText(getBaseContext(), "No puede acceder a internet con el modo Offline activo.", Toast.LENGTH_LONG).show();
		else{
			Intent i = new Intent (ListaCompra.this, TiendaVirtual.class);
			if(e!=null){ i.putExtra("eid",e.getEid());
			Log.e("EID",""+e.getEid()); }
			i.putExtra("funcion", "reservaonline");
			startActivity(i);
		}
	}};

	OnClickListener reservaMulti = new OnClickListener() { @Override public void onClick(View v) {
		if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
			//Buscar en BD si hay X tipos de CNAE
				//Si no hay, mandar a buscar por tipo CNAE (añadir en ListaEstablecimientos también)
				//Si hay, dar selector de tipo (X tipos de CNAE descargados) ((+ para buscar nuevos?))
	}};

	public void mostrarPedido(final List<Ped> pedidos) {
		int par = 0, gr=1; double t = 0.0; TableRow fila;
		TextView art, ud, tipo, precio, importe, obs;
		TextView nota;
		nchecks=0; nart=0;

		for (final Ped p : pedidos) { nart++;
			fila = new TableRow(getBaseContext()); art = new TextView(getBaseContext());
			ud = new TextView(getBaseContext()); tipo = new TextView(getBaseContext());
			precio = new TextView(getBaseContext()); // Sólo usado en apaisado
			importe = new TextView(getBaseContext()); // Sólo usado en apaisado
			obs = new TextView(getBaseContext()); // Sólo usado en apaisado
			nota = new TextView(getBaseContext()); //Sólo usado en vertical
			fila.setTag(p);
			//Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/roboto.tff");
			art.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Medium);
			art.setTextColor(getResources().getColor(android.R.color.black));
			ud.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Medium);
			ud.setTextColor(getResources().getColor(android.R.color.black));
			tipo.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Medium);
			tipo.setTextColor(getResources().getColor(android.R.color.black));
			precio.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Medium);
			precio.setTextColor(getResources().getColor(android.R.color.black));
			importe.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Medium);
			importe.setTextColor(getResources().getColor(android.R.color.black));
			obs.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Small);
			obs.setTextColor(getResources().getColor(android.R.color.black));
			ud.setGravity(Gravity.CENTER); precio.setGravity(Gravity.RIGHT);
			importe.setGravity(Gravity.RIGHT); ud.setSingleLine(true);
			precio.setSingleLine(true); importe.setSingleLine(true);

			// Recuperar Artículo
			Art a = db.getArticulo(p.getAid()); art.setText(a.getArticulo());
			if (a.getTipo().equals("UN") || a.getTipo().equals("GR")) {
				Short s = ((Double) p.getCantidad()).shortValue(); ud.setText(s.toString());
			} else ud.setText(Double.valueOf(p.getCantidad()).toString());
			String[] tipound = getResources().getStringArray(R.array.tipoundv);
			if (a.getTipo().equals("KG")) { tipo.setText(tipound[1]); gr=1; }
			else if (a.getTipo().equals("GR")) { tipo.setText(tipound[2]); gr=1000; }
			else if (a.getTipo().equals("MT")) { tipo.setText(tipound[3]); gr=1; }
			else { tipo.setText(tipound[0]); gr=1; }
			tipo.setText(" "+tipo.getText()); //Espacio entre cantidad y tipo de unidad.
			obs.setText(p.getObservacion()); /*obs.setMaxLines(10);
			obs.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Small);*/


			// Cálculo de importe y adición de símbolo de moneda
			switch (Integer.valueOf(sharedPrefs.getString("moneda", "1"))) {
			case 2: // 1€ = $1.3121
				double dol = p.getPrecio() * 1.3121;
				importe.setText("$" + String.format("%.2f", dol * p.getCantidad()/gr));
				precio.setText("$" + String.format("%.2f", dol));
				t += p.getPrecio() * 1.3121 * p.getCantidad()/gr;
				//subtotal = t;
				break;
			case 3: // 1€ = 0.866358534 £
				double pou = p.getPrecio() * 0.866358534;
				importe.setText(String.format("%.2f", pou * p.getCantidad()/gr) + "£");
				precio.setText(String.format("%.2f", pou) + "£");
				t += p.getPrecio() * 0.866358534 * p.getCantidad()/gr;
				//subtotal = t;
				break;
			case 4: // 1€ = 129.423949 ¥
				double yen = p.getPrecio() * 129.423949;
				importe.setText(String.format("%.0f", yen * p.getCantidad()/gr) + "¥");
				precio.setText(String.format("%.0f", yen) + "¥");
				t += p.getPrecio() * 129.423949 * p.getCantidad()/gr;
				//subtotal = t;
				break;
			default:
				importe.setText(String.format("%.2f",p.getPrecio() * p.getCantidad()/gr)+ "€");
				precio.setText(String.format("%.2f", p.getPrecio()) + "€");
				t += p.getPrecio() * p.getCantidad()/gr;
				//subtotal = t;
				break;}
			TableRow.LayoutParams lp = new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.CENTER_VERTICAL;
			TableRow.LayoutParams lp2 = new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
			lp2.gravity = Gravity.CENTER_VERTICAL; lp2.span=2;
			TableRow.LayoutParams lpa = new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
			lpa.gravity = Gravity.CENTER_VERTICAL;
			TableRow.LayoutParams lpo = new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
			lpo.gravity = Gravity.CENTER_VERTICAL;
			TableRow.LayoutParams lpc = new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
			lpc.gravity = Gravity.CENTER; //lpc.span=2;
			if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
				lpa.span=3; fila.addView(art,lpa);
				fila.addView(ud,lp); fila.addView(tipo,lp);
				//Botón Nota
				final ImageButton btnnota = new ImageButton(getBaseContext());
				btnnota.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
				btnnota.setImageDrawable(getResources().getDrawable(R.drawable.content_new_event));
				btnnota.setPadding(5,5,5,5);
				TableRow.LayoutParams lpn = new TableRow.LayoutParams(
						TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
				lpn.gravity = Gravity.CENTER;
				fila.addView(btnnota,lpn);
				final TableRow filanota = new TableRow(getBaseContext());
				if(!p.getObservacion().equals("")){
					filanota.setVisibility(View.GONE);
					filanota.setTag(p);
					if(par==0) filanota.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));
					else filanota.setBackgroundColor(getResources().getColor(R.color.azulDisoft2));

					TableRow.LayoutParams lpfn = new TableRow.LayoutParams(
							TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
					lpfn.span = 7;
					 filanota.addView(nota,lpfn);
					btnnota.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
						if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
						if(((Ped)filanota.getTag()).getAid()==p.getAid()){
						if(filanota.getVisibility()==View.GONE) filanota.setVisibility(View.VISIBLE);
						else filanota.setVisibility(View.GONE);}
					}});
					nota.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Small);
					nota.setTextColor(getResources().getColor(android.R.color.black));
					nota.setText(p.getObservacion());}
				else btnnota.setVisibility(View.INVISIBLE);

				final CheckBox cb = new CheckBox(getBaseContext());
				if(p.getEstado()<0) {	cb.setChecked(true); nchecks++; }
				cb.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					db.switchCBArticuloPedido(p);
					if(cb.isChecked()) nchecks++; else nchecks--;
					if(nchecks>0)
						contador.setText("("+String.valueOf(nchecks)+"/"+String.valueOf(nart)+"):");
					else
						contador.setText("("+String.valueOf(nart)+"):"); }});
				cb.setButtonDrawable(R.drawable.btn_check_holo_light); fila.addView(cb,lpc);
				if (par == 0) { par = 1; //art.setTypeface(tf);
					if(p.getPrecio()==0) flagfalsototal=1;
					fila.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));
				} else { par = 0;
					if(p.getPrecio()==0) flagfalsototal=1;
					fila.setBackgroundColor(getResources().getColor(R.color.azulDisoft2)); }
				fila.setTag(p); fila.setLongClickable(true);
				fila.setOnLongClickListener(OnLongClickTableRow);
				tl.addView(fila);
				if(!p.getObservacion().equals("")) tl.addView(filanota);
			} else { lpa.span=2;
				fila.addView(art,lpa); fila.addView(ud,lp);
				fila.addView(tipo,lp); fila.addView(precio,lp);
				TextView espacio = new TextView(getBaseContext());
				fila.addView(espacio); fila.addView(importe,lp);
				espacio = new TextView(getBaseContext());
				fila.addView(espacio); lpo.span=2; fila.addView(obs,lpo);
				final CheckBox cb = new CheckBox(getBaseContext());
				cb.setButtonDrawable(R.drawable.btn_check_holo_light);
				TableRow.LayoutParams lpcb = new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
				lpcb.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT; //lpcb.leftMargin = 8;
				//lpcb.span=2;
				if(p.getEstado()<0) {	cb.setChecked(true); nchecks++; }
				cb.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					db.switchCBArticuloPedido(p);
					if(cb.isChecked()) nchecks++; else nchecks--;
					if(nchecks>0)
						contador.setText("("+String.valueOf(nchecks)+"/"+String.valueOf(nart)+"):");
					else
						contador.setText("("+String.valueOf(nart)+"):");
					}});
				fila.addView(cb,lpcb);
				if (par == 0) { par = 1;
					if(p.getPrecio()==0) flagfalsototal=1;
					fila.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));
				} else { par = 0;
					if(p.getPrecio()==0) flagfalsototal=1;
					fila.setBackgroundColor(getResources().getColor(R.color.azulDisoft2)); }
				fila.setTag(p); fila.setLongClickable(true);
				fila.setOnLongClickListener(OnLongClickTableRow);
				tl.addView(fila);
		}}

		//Tramo ordenación inicial
		//sadasd NO entra aquí
		Log.e("ENTRA","ORDENAR");
		ImageView flechaarticulo = (ImageView) findViewById(R.id.sortarticulo);
		Matrix matrix = new Matrix();
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				((ImageView) findViewById(R.id.sortprecio)).setVisibility(View.INVISIBLE);
				((ImageView) findViewById(R.id.sortimporte)).setVisibility(View.INVISIBLE);}
			((ImageView) findViewById(R.id.sortcheck)).setVisibility(View.INVISIBLE);
			flechaarticulo.setVisibility(View.VISIBLE);
			matrix.postRotate(0f, flechaarticulo.getDrawable().getBounds().width() / 2,
				flechaarticulo.getDrawable().getBounds().height() / 2);
			flechaarticulo.setImageMatrix(matrix);
			/*pedidos.clear();
			pedidos.addAll(db.getPedidosPendientes(eidactual));*/
			Collections.sort(pedidos,new Comparator<Ped>() {
					public int compare(Ped p1, Ped p2) {
						return db.getArticulo(p1.getAid()).getArticulo()
							.compareToIgnoreCase(db.getArticulo(p2.getAid()).getArticulo()); }});
			orden = 1;

		// Total XXX
		TextView totaltxt = new TextView(getBaseContext());
		TableRow total = new TableRow(getBaseContext());
		contador = new TextView(getBaseContext());
		ImageView botontotal = new ImageView(getBaseContext());
		botontotal.setImageDrawable(getResources().getDrawable(R.drawable.social_forward));
		botontotal.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
		botontotal.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
		TableRow.LayoutParams lpt = new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		lpt.gravity = Gravity.CENTER_VERTICAL;
		totaltxt.setText(R.string.total);
		if(nchecks>0)
			contador.setText("("+String.valueOf(nchecks)+"/"+String.valueOf(nart)+"):");
		else contador.setText("("+String.valueOf(nart)+"):");
		totaltxt.setTextAppearance(getBaseContext(),android.R.style.TextAppearance_Large);
		totaltxt.setTextColor(getResources().getColor(android.R.color.black));
		contador.setTextColor(getResources().getColor(android.R.color.black));
		total.setBackgroundColor(getResources().getColor(R.color.verdeDisoft3));
		total.addView(totaltxt,lpt); total.addView(contador,lpt);
		totaltxt = new TextView(getBaseContext());
		totaltxt.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Medium);
		totaltxt.setTextColor(Color.BLACK);
		switch (Integer.valueOf(sharedPrefs.getString("moneda", "1"))) {
		case 2: totaltxt.setText("$" + String.format("%.2f", t)); break;
		case 3: totaltxt.setText(String.format("%.2f", t) + "£"); break;
		case 4: totaltxt.setText(String.format("%.0f", t) + "¥"); break;
		default: totaltxt.setText(String.format("%.2f", t) + "€"); break;}
		//if(flagfalsototal==1) totaltxt.setText("¿"+totaltxt.getText()+"?");
		total.addView(totaltxt);
		TableRow.LayoutParams params = new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT)
			params.span = 3;  else params.span = 5;
		totaltxt.setLayoutParams(params);
		TableRow.LayoutParams lp = new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT; lp.span=2;
		if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE) lp.span = 4;
		botontotal.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		popupView = layoutInflater.inflate(R.layout.popuptotal,null);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		popupWindow = new PopupWindow(popupView,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
		else popupWindow = new PopupWindow(popupView,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
				android.R.drawable.alert_light_frame));
		popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER,0, 0);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			if(display.getRotation()==Surface.ROTATION_90)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else if(display.getRotation()==Surface.ROTATION_270){
				if(android.os.Build.VERSION.SDK_INT == 8)
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
		}else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		LinearLayout enviar = (LinearLayout)popupView.findViewById(R.id.btnenviarpedido),
					 adjunta = (LinearLayout)popupView.findViewById(R.id.btnadjuntafirma),
					 compartir = (LinearLayout)popupView.findViewById(R.id.btncompartirlista),
					 quitar = (LinearLayout)popupView.findViewById(R.id.btnquitarmarcados),
					 borrar = (LinearLayout)popupView.findViewById(R.id.btnborrartodo),
					 marcar = (LinearLayout)popupView.findViewById(R.id.btnmarcartodo),
					 desmarcar = (LinearLayout)popupView.findViewById(R.id.btndesmarcartodo),
					 datoscamarero = (LinearLayout)popupView.findViewById(R.id.btndatoscamarero);
		ImageButton cancelar = (ImageButton)popupView.findViewById(R.id.cancel);
		if(eidactual<0){
			enviar.setVisibility(View.GONE);
			((TextView)popupView.findViewById(R.id.txtborrartodo))
						.setText(getString(R.string.borrartodoalt));}
		if(nchecks==0){quitar.setVisibility(View.GONE); desmarcar.setVisibility(View.GONE);}
		else if(nchecks==nart) marcar.setVisibility(View.GONE);
		if(db.getEstablecimiento(eidactual).getRestrped().equals("S")
				||!isNetworkAvailable()) { enviar.setEnabled(false);
				enviar.getBackground().setColorFilter(0xFFFF0000,PorterDuff.Mode.SRC_IN);
				if(!db.getEstablecimiento(eidactual).getTv().equals("") && eidactual>0)
					adjunta.setVisibility(View.VISIBLE);}
		else{ enviar.setEnabled(true); adjunta.setVisibility(View.GONE); }

		if(e.getCnae() != null && e.getCnae().contains("39110") && flagop==1 ||
		e.getCnae() != null && e.getCnae().contains("39100") && flagop==1 )
		//CAMBIO CONDICIÓN PARA PROBAR DETALLES POR CLIENTE
		//if(flagop==1)
			if(eidactual==sharedPrefs.getInt("solicitacliest", 0))
				datoscamarero.setVisibility(View.VISIBLE);
			/*if(idclif!=0)
				compartir.setVisibility(View.GONE);
			else
				compartir.setVisibility(View.VISIBLE);
			 */

		enviar.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			//Pruebo a evitar ANR por el servicio 2ndo plano
			stopService(new Intent(ListaCompra.this, NotificacionMensajes.class));
			/*if(sharedPrefs.getString("nombre", "").equals(
					sharedPrefs.getString("seudonimo",sharedPrefs.getString("nombre", ""))) ||
					sharedPrefs.getString("dir","").equals("")){
				final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View popupViewDatos = layoutInflater.inflate(R.layout.popupsino, null);
				final PopupWindow popupWindowDatos = new PopupWindow(popupViewDatos,android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
				popupWindowDatos.setBackgroundDrawable(getResources().getDrawable(
						android.R.drawable.alert_light_frame));
				popupWindowDatos.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				if(display.getRotation()==Surface.ROTATION_90)
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				else if(display.getRotation()==Surface.ROTATION_270){
					if(android.os.Build.VERSION.SDK_INT == 8)
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
				}else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				TextView txt =(TextView) popupViewDatos.findViewById(R.id.texto);
				txt.setText("Se han detectado las siguientes carencias de información:\n");
				if(sharedPrefs.getString("nombre", "").equals(
						sharedPrefs.getString("seudonimo",sharedPrefs.getString("nombre", ""))))
					txt.setText(txt.getText().toString()+
							"\n\t· El nombre de usuario y el seudónimo son el mismo ["+
							sharedPrefs.getString("nombre", "")+"].\n");
				if(sharedPrefs.getString("dir","").equals(""))
					txt.setText(txt.getText().toString()+
							"\n\t· La dirección no puede estar vacía.\n");
				txt.setText(txt.getText().toString()+"\nPara completar la información " +
						"necesaria para realizar el pedido, por favor, acceda a Configuración -> " +
						"Datos de Usuario, o pulse el botón de la izquierda.");
				ImageButton si = (ImageButton) popupViewDatos.findViewById(R.id.si);
				si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				si.setImageDrawable(getResources().getDrawable(R.drawable.action_settings));
				si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
					Intent i = new Intent(ListaCompra.this, Opciones2.class); startActivity(i); }});
				ImageButton no = (ImageButton) popupViewDatos.findViewById(R.id.no);
				no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
					popupWindowDatos.dismiss(); }});
			}else{*/

				popupWindow.dismiss();
			if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
					popupView = layoutInflater.inflate(R.layout.popupsino, null);
					popupWindow = new PopupWindow(popupView,android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
					popupWindow.setBackgroundDrawable(getResources().getDrawable(
						android.R.drawable.alert_light_frame));
					popupWindow.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
					{Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
					if(display.getRotation()==Surface.ROTATION_90)
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					else if(display.getRotation()==Surface.ROTATION_270){
						if(android.os.Build.VERSION.SDK_INT == 8)
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
					}else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			//} Amplío función "No tienes dirección" a todo
					//Funcionamiento Restaurante
					if(e.getCnae() != null && e.getCnae().contains("39110") && flagop==1 ||
							e.getCnae() != null && e.getCnae().contains("39100") && flagop==1 ){
						//referenciapedido = sharedPrefs.getString("ref"+eidactual,"");
						referenciapedido = e.getReferenciapedido();

						if(eidactual==sharedPrefs.getInt("solicitacliest", 0))
							new envioPedido().execute();
					}
			//Fin Restaurantes
			else{ //Funcionamiento normal

				//Intento colar aquí en medio el AlertDialog de la descarga de precios.
				if(db.hasManualPrices(pedidos.get(0).getPid())){
				AlertDialog.Builder adb = new AlertDialog.Builder(ListaCompra.this);
				adb.setCancelable(false).setIcon(R.drawable.collections_labels).setTitle("Descarga de Precios")
						.setMessage("¿Desea conectarse al servidor para descargar los precios reales de los productos seleccionados?")
						.setPositiveButton("Descargar", new DialogInterface.OnClickListener(){
							@Override public void onClick(DialogInterface dialog, int which){
								Intent i = new Intent (ListaCompra.this, TiendaVirtual.class);
								i.putExtra("eid",e.getEid());
								i.putExtra("funcion", "calcularprecio");
								if(idclif!=0) i.putExtra("idclif",idclif);
								else i.putExtra("idclif",0);
								i.putExtra("pid",pedidos.get(0).getPid());
								dialog.dismiss(); popupWindow.dismiss(); startActivity(i);
						}})
						.setNegativeButton("Omitir", new DialogInterface.OnClickListener(){
							@Override public void onClick(DialogInterface dialog, int which){
								dialog.dismiss();
						}});
					adb.create().show();
				}
				((TextView) popupView.findViewById(R.id.texto)).setText(getString(R.string.textofinish));
				RadioGroup rg = new RadioGroup(getBaseContext());
				final RadioButton ad = new RadioButton(getBaseContext());
				final RadioButton rp = new RadioButton(getBaseContext());
				final TextView txtdir = new TextView(getBaseContext());
				final EditText dir = (EditText) getLayoutInflater().inflate(R.layout.edittext, null);
				ad.setText(getString(R.string.ad)); rp.setText(getString(R.string.rp));
				ad.setTextColor(Color.BLACK); rp.setTextColor(Color.BLACK);
				ad.setButtonDrawable(android.R.drawable.btn_radio);
				rp.setButtonDrawable(android.R.drawable.btn_radio);
				rg.addView(ad); rg.addView(rp); txtdir.setText(getString(R.string.direnv));
				txtdir.setTextColor(Color.BLACK); dir.setText(sharedPrefs.getString("dir",""));
				txtdir.setVisibility(View.GONE); dir.setVisibility(View.GONE);
				dir.setTextColor(Color.BLACK);
				((LinearLayout)popupView.findViewById(R.id.cajatexto)).addView(rg);
				((LinearLayout)popupView.findViewById(R.id.cajatexto)).addView(txtdir);
				((LinearLayout)popupView.findViewById(R.id.cajatexto)).addView(dir);
				rg.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if(((RadioButton)group.findViewById(checkedId)).equals(ad)){
							((ImageButton)popupView.findViewById(R.id.si)).setEnabled(true);
							txtdir.setVisibility(View.VISIBLE);
							dir.setVisibility(View.VISIBLE); }
						else if(((RadioButton)group.findViewById(checkedId)).equals(rp)){
							((ImageButton)popupView.findViewById(R.id.si)).setEnabled(true);
							txtdir.setVisibility(View.GONE);
							dir.setVisibility(View.GONE); }
				}});
				if(!sharedPrefs.getString("mail","").equals("")){
					final CheckBox copia = new CheckBox(getBaseContext());
					copia.setText("Enviar copia del pedido a mi e-mail");
					copia.setTextColor(Color.BLACK); copia.setChecked(true);
					copia.setButtonDrawable(R.drawable.btn_check_holo_light);
					copia.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
						@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
							if(isChecked)flagcopia=1;else flagcopia=0;}});
					((LinearLayout)popupView.findViewById(R.id.cajatexto)).addView(copia);}
				final CheckBox firma = new CheckBox(getBaseContext());
				firma.setText("Firmar el pedido");
				firma.setTextColor(Color.BLACK);
				if(e.getReqfirm()!=null && e.getReqfirm().equals("S")) firma.setChecked(true);
				else firma.setChecked(false);
				firma.setButtonDrawable(R.drawable.btn_check_holo_light);
				if(flagfirma==2) firma.setEnabled(false); else firma.setEnabled (true);
				firma.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
					@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
						if(isChecked)flagfirma=1;else flagfirma=0;}});
				if(db.getEstablecimiento(eidactual).equals("")) flagfirma=0;
				else
					((LinearLayout)popupView.findViewById(R.id.cajatexto)).addView(firma);

				ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
				si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				si.setEnabled(false);

				//Por-defecto, Autoventa
				if(sharedPrefs.getInt("solicitacliest", 0)==e.getEid() && e.getConfigura() != null
						&& e.getConfigura().contains(",V,")){
					firma.setChecked(false); rp.setChecked(true); si.setEnabled(true); }

				si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
					if(dir.getText().toString().equals("") && ad.isChecked())
						Toast.makeText(getBaseContext(), "Rellene la dirección, si va a hacer un pedido a domicilio.",
								Toast.LENGTH_LONG).show();
					else{
					File f = new File(getApplicationInfo().dataDir+"/firmas/"+
						db.getPedidoPendiente(eidactual).getPid()+".jpg");
					if(f.exists()) flagfirma=2;
					if(flagfirma==1){
						Intent i = new Intent(ListaCompra.this, VentanaFirma.class);
						i.putExtra("pid", String.valueOf(db.getPedidoPendiente(eidactual).getPid()));
						startActivityForResult(i,5);}
					if(flagfirma==0 || flagfirma==2){
					if(dir.getVisibility()==View.VISIBLE && dir.getText().equals(""))
						dir.requestFocus();
					else if(dir.getVisibility()==View.VISIBLE){
						popupWindow.dismiss(); flagdir = 1;
						//envio = "Enviar a: "+direccionpedido;
						direccionpedido = dir.getText().toString();
						envio = "A domicilio";
					}else if(dir.getVisibility()==View.GONE){
						envio = "Se recogerá personalmente."; popupWindow.dismiss(); }
					if(flagdir==1){
						if(sharedPrefs.getString("dir","").equals("")){
							final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
									.getSystemService(LAYOUT_INFLATER_SERVICE);
								View popupView2 = layoutInflater.inflate(R.layout.popupsino, null);
								popupWindow2 = new PopupWindow(popupView2,
									android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
									android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
								popupWindow2.setBackgroundDrawable(getResources()
									.getDrawable(android.R.drawable.alert_light_frame));
								popupWindow2.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
								((TextView) popupView2.findViewById(R.id.texto)).setText(getString(R.string.savedir));
								final ImageButton si = (ImageButton) popupView2.findViewById(R.id.si);
								si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
								//Compacto función botones sí/no de Dirección
								View.OnClickListener controlPedido = new View.OnClickListener() {
									@Override public void onClick(View v) {
									if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
									SharedPreferences.Editor spe = sharedPrefs.edit();
									spe.putString("dir",dir.getText().toString());
									spe.commit(); popupWindow2.dismiss();
									extras();
								}};
								si.setOnClickListener(controlPedido);
								ImageButton no = (ImageButton) popupView2.findViewById(R.id.no);
								no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
								no.setOnClickListener(controlPedido);
								dir.setOnKeyListener(new View.OnKeyListener(){
					   			      public boolean onKey(View v, int keyCode, KeyEvent event){
					   			          if (event.getAction() == KeyEvent.ACTION_DOWN){
					   			              switch (keyCode){
					   			                  case KeyEvent.KEYCODE_DPAD_CENTER:
					   			                  case KeyEvent.KEYCODE_ENTER:
					   			                      si.performClick(); return true;
					   			                  default: break;
					   			              }
					   			          }return false;
					   			      }});
						}else extras();
					}else extras();
					}
				}
				}


				public void extras() { //Ventana datos extra final pedido
					final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
							.getSystemService(LAYOUT_INFLATER_SERVICE);
					View popupView3 = layoutInflater.inflate(R.layout.popupsino, null); //XXX EXTRAER CADENAS
					popupWindow3 = new PopupWindow(popupView3,
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
					popupWindow3.setBackgroundDrawable(getResources()
						.getDrawable(android.R.drawable.alert_light_frame));
					popupWindow3.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, -100);
					TextView reftxt = new TextView(getBaseContext()),
									 fechatxt = new TextView(getBaseContext()),
									 horatxt = new TextView(getBaseContext()),
									 obspedtxt = new TextView(getBaseContext());
					final EditText referencia = (EditText) getLayoutInflater().inflate(R.layout.edittext, null),
												 fechaped = (EditText) getLayoutInflater().inflate(R.layout.edittext, null),
												 horaped = (EditText) getLayoutInflater().inflate(R.layout.edittext, null),
												 observpedido = (EditText) getLayoutInflater().inflate(R.layout.edittext, null);
					LinearLayout linea = new LinearLayout(getBaseContext());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
																		LinearLayout.LayoutParams.MATCH_PARENT,
																		LinearLayout.LayoutParams.WRAP_CONTENT);
					linea.setLayoutParams(params);
					reftxt.setText("Referencia: "); fechatxt.setText("Fecha de entrega: ");
					horatxt.setText("Hora de entrega: "); obspedtxt.setText("Observaciones del pedido: ");
					reftxt.setTextColor(Color.BLACK); fechatxt.setTextColor(Color.BLACK);
					horatxt.setTextColor(Color.BLACK); obspedtxt.setTextColor(Color.BLACK);
					referencia.setTextColor(Color.BLACK); fechaped.setTextColor(Color.BLACK);
					horaped.setTextColor(Color.BLACK); observpedido.setTextColor(Color.BLACK);
					referencia.setEms(15); fechaped.setEms(15);
					horaped.setEms(15); observpedido.setMaxLines(5);
					((TextView) popupView3.findViewById(R.id.texto)).setText("Informacion adicional");
					((TextView) popupView3.findViewById(R.id.texto)).setTextAppearance(getBaseContext(),
							android.R.style.TextAppearance_Large);
					((TextView) popupView3.findViewById(R.id.texto)).setTextColor(Color.BLACK);
					((TextView) popupView3.findViewById(R.id.texto)).setGravity(Gravity.CENTER_HORIZONTAL);
					linea.addView(reftxt); linea.addView(referencia);
					((LinearLayout)popupView3.findViewById(R.id.cajatexto)).addView(linea);
					linea = new LinearLayout(getBaseContext()); linea.setLayoutParams(params);
					linea.addView(fechatxt); linea.addView(fechaped);
					((LinearLayout)popupView3.findViewById(R.id.cajatexto)).addView(linea);
					linea = new LinearLayout(getBaseContext()); linea.setLayoutParams(params);
					linea.addView(horatxt); linea.addView(horaped);
					((LinearLayout)popupView3.findViewById(R.id.cajatexto)).addView(linea);
					((LinearLayout)popupView3.findViewById(R.id.cajatexto)).addView(obspedtxt);
					((LinearLayout)popupView3.findViewById(R.id.cajatexto)).addView(observpedido);
					//referencia.setText(sharedPrefs.getString("ref"+eidactual,""));
					referencia.setText(e.getReferenciapedido());
					fechaped.setText(e.getFechacita());
					horaped.setText(e.getHoracita());
					observpedido.setText(e.getObservaciones());
					popupView3.findViewById(R.id.no).setVisibility(View.GONE);
					ImageButton si = (ImageButton) popupView3.findViewById(R.id.si);
					si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
					si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						popupWindow3.dismiss();
						referenciapedido = referencia.getText().toString();
						fechapedido = fechaped.getText().toString();
						horapedido = horaped.getText().toString();
						observacionespedido = observpedido.getText().toString();
						ventanaSeguro(layoutInflater);
					}});
				}

				});
				//Cancelar envío
				ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
				no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				no.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						popupWindow.dismiss();
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				}});
			}
					//}//Ampliación "No hay dirección"
		}});
		if(auto){ enviar.performClick(); auto=false; }

		adjunta.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			Intent i = new Intent(ListaCompra.this, VentanaFirma.class);
			i.putExtra("pid", String.valueOf(db.getPedidoPendiente(eidactual).getPid()));
					startActivityForResult(i,5);
		}});
		compartir.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			popupWindow.dismiss();
			List<Ped> listaPedidos = db.getPedidosPendientes(e.getEid());
			String lista = "Lista de la compra para "+e.getNombre()+"\n";
			for(int i=0;i<listaPedidos.size();i++){
				lista+=(i+1)+". "+db.getArticulo(listaPedidos.get(i).getAid()).getArticulo()+", ";
				if(db.getArticulo(listaPedidos.get(i).getAid()).getTipo().equals("UN") ||
						db.getArticulo(listaPedidos.get(i).getAid()).getTipo().equals(""))
				lista+=((Double)listaPedidos.get(i).getCantidad()).intValue()+"\n";
				else lista+=listaPedidos.get(i).getCantidad()+"\n";
			}

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, lista);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, "Compartir lista de la compra")); }});
		quitar.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View popupView2 = layoutInflater.inflate(R.layout.popupsino, null);
			popupWindow2 = new PopupWindow(popupView2,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
			popupWindow2.setBackgroundDrawable(getResources()
					.getDrawable(android.R.drawable.alert_light_frame));
			popupWindow2.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
			if(db.getEstablecimiento(eidactual).getEid()<0){
				Bitmap mas = BitmapFactory.decodeResource(getResources(),R.drawable.content_new),
						lupa = BitmapFactory.decodeResource(getResources(),R.drawable.action_search);
				SpannableStringBuilder ssb = new SpannableStringBuilder(getString(R.string.confirmaquitaralt)
						+" (Puede recuperar los artículos creados pulsando   ···  )");
				ssb.setSpan(new ImageSpan(getBaseContext(),Bitmap.createScaledBitmap(mas,32,32,true)),
						ssb.length()-8,ssb.length()-7,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				ssb.setSpan(new ImageSpan(getBaseContext(),Bitmap.createScaledBitmap(lupa,32,32,true)),
						ssb.length()-3,ssb.length()-2,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				((TextView) popupView2.findViewById(R.id.texto)).setText(ssb,BufferType.SPANNABLE);
			}else{ ((TextView) popupView2.findViewById(R.id.texto))
									.setText(getString(R.string.confirmaquitar));}
			ImageButton si = (ImageButton) popupView2.findViewById(R.id.si);
			si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
				if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
				for(Ped p : db.getPedidosPendientesDe(eidactual,idclif)){
					if(p.getEstado()<0){
						if(sharedPrefs.getInt("solicitacliest", 0)==eidactual){
							if(p.getIdclif()!=0 && p.getIdclif()==idclif){
								db.deleteArticuloPedido(p);
								/*if(!db.checkArticuloPedido(p.getAid())){
									db.deleteArticulo(db.getArticulo(p.getAid()));
									db.deleteArticuloEstablecimiento(p.getAid(), eidactual);
								}*/
						}}else{
							db.deleteArticuloPedido(p);
							/*if(!db.checkArticuloPedido(p.getAid())){
								db.deleteArticulo(db.getArticulo(p.getAid()));
								db.deleteArticuloEstablecimiento(p.getAid(),0);
							}*/
						}
					}
				}
				/*for(Ped p : db.getAllPedidos(eidactual))
					if(p.getEstado()<0) db.deleteArticuloPedido(p);*/
				popupWindow2.dismiss(); popupWindow.dismiss();
				Intent intent = new Intent(ListaCompra.this,ListaCompra.class);
				intent.putExtra("eid",eidactual);
				startActivity(intent); finish(); }});
			ImageButton no = (ImageButton) popupView2.findViewById(R.id.no);
			no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			no.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					popupWindow2.dismiss(); }});
		}});
		borrar.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View popupView2 = layoutInflater.inflate(R.layout.popupsino, null);
			popupWindow2 = new PopupWindow(popupView2,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
			popupWindow2.setBackgroundDrawable(getResources()
					.getDrawable(android.R.drawable.alert_light_frame));
			popupWindow2.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
			if(db.getEstablecimiento(eidactual).getEid()<0)
				((TextView) popupView2.findViewById(R.id.texto))
				.setText(getString(R.string.confirmaborraralt));
			else ((TextView) popupView2.findViewById(R.id.texto))
									.setText(getString(R.string.confirmaborrar));
			ImageButton si = (ImageButton) popupView2.findViewById(R.id.si);
			si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			si.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
					for(Ped p : db.getPedidosPendientes(eidactual)){
						if(sharedPrefs.getInt("solicitacliest", 0)==eidactual){
						if(p.getIdclif()!=0 && p.getIdclif()==idclif){
							if(!db.checkArticuloPedido(p.getAid())){
								db.deleteArticulo(db.getArticulo(p.getAid()));
								db.deleteArticuloEstablecimiento(p.getAid(), eidactual);
							}}
						}else{
							if(!db.checkArticuloPedido(p.getAid())){
								db.deleteArticulo(db.getArticulo(p.getAid()));
								db.deleteArticuloEstablecimiento(p.getAid(),0);
							}
						}
					}
					db.deletePedido(db.getPedidoPendiente(eidactual,idclif));
					popupWindow2.dismiss(); popupWindow.dismiss();
					Intent intent = new Intent(ListaCompra.this,ListaCompra.class);
					intent.putExtra("eid",eidactual);
					startActivity(intent); finish(); }});
			ImageButton no = (ImageButton) popupView2.findViewById(R.id.no);
			no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			no.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					popupWindow2.dismiss(); }});
		}});
		marcar.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			popupWindow.dismiss();
			for(int i=0; i < db.getArticulosPedidosCount(db.getPedidoPendiente(eidactual).getPid()); i++){ //Antes contaba los artículos con tl.getChildCount()-2
				switch(pedidos.get(i).getEstado()){
				case -4: pedidos.get(i).setEstado(3); break;
				case -3: pedidos.get(i).setEstado(2); break;
				case -2: pedidos.get(i).setEstado(1); break;
				case -1: pedidos.get(i).setEstado(0);}
				db.switchCBArticuloPedido(pedidos.get(i));}
			Intent i = new Intent(ListaCompra.this, ListaCompra.class);
			i.putExtra("eid",eidactual);
			startActivity(i); finish(); }});
		desmarcar.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			popupWindow.dismiss();
			for(int i=0; i < db.getArticulosPedidosCount(db.getPedidoPendiente(eidactual).getPid()); i++){ //Ver Marcar
				switch(pedidos.get(i).getEstado()){
				case 3: pedidos.get(i).setEstado(-4); break;
				case 2: pedidos.get(i).setEstado(-3); break;
				case 1: pedidos.get(i).setEstado(-2); break;
				case 0: pedidos.get(i).setEstado(-1);}
				db.switchCBArticuloPedido(pedidos.get(i));}
			Intent i = new Intent(ListaCompra.this, ListaCompra.class);
			//i.putExtra("establecimiento", mIcsSpinner.getSelectedItemPosition());
			i.putExtra("eid",eidactual);
			startActivity(i); finish(); }});

		datoscamarero.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);

			   //Ventana datos extra final pedido
					final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
							.getSystemService(LAYOUT_INFLATER_SERVICE);
					View popupView3 = layoutInflater.inflate(R.layout.popupsino, null); //XXX EXTRAER CADENAS
					popupWindow3 = new PopupWindow(popupView3,
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
					popupWindow3.setBackgroundDrawable(getResources()
						.getDrawable(android.R.drawable.alert_light_frame));
					popupWindow3.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, -100);
					TextView reftxt = new TextView(getBaseContext()),
									 fechatxt = new TextView(getBaseContext()),
									 horatxt = new TextView(getBaseContext()),
									 obspedtxt = new TextView(getBaseContext());
					final EditText referencia = (EditText) getLayoutInflater().inflate(R.layout.edittext, null),
												 fechaped = (EditText) getLayoutInflater().inflate(R.layout.edittext, null),
												 horaped = (EditText) getLayoutInflater().inflate(R.layout.edittext, null),
												 observpedido = (EditText) getLayoutInflater().inflate(R.layout.edittext, null);
					LinearLayout linea = new LinearLayout(getBaseContext());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
																		LinearLayout.LayoutParams.MATCH_PARENT,
																		LinearLayout.LayoutParams.WRAP_CONTENT);
					linea.setLayoutParams(params);
					reftxt.setText("Referencia: "); fechatxt.setText("Fecha de entrega: ");
					horatxt.setText("Hora de entrega: "); obspedtxt.setText("Observaciones del pedido: ");
					reftxt.setTextColor(Color.BLACK); fechatxt.setTextColor(Color.BLACK);
					horatxt.setTextColor(Color.BLACK); obspedtxt.setTextColor(Color.BLACK);
					referencia.setTextColor(Color.BLACK); fechaped.setTextColor(Color.BLACK);
					horaped.setTextColor(Color.BLACK); observpedido.setTextColor(Color.BLACK);
					referencia.setEms(15); fechaped.setEms(15);
					horaped.setEms(15); observpedido.setMaxLines(5);
					((TextView) popupView3.findViewById(R.id.texto)).setText("Informacion adicional");
					((TextView) popupView3.findViewById(R.id.texto)).setTextAppearance(getBaseContext(),
							android.R.style.TextAppearance_Large);
					((TextView) popupView3.findViewById(R.id.texto)).setTextColor(Color.BLACK);
					((TextView) popupView3.findViewById(R.id.texto)).setGravity(Gravity.CENTER_HORIZONTAL);
					linea.addView(reftxt); linea.addView(referencia);
					((LinearLayout)popupView3.findViewById(R.id.cajatexto)).addView(linea);
					linea = new LinearLayout(getBaseContext()); linea.setLayoutParams(params);
					linea.addView(fechatxt); linea.addView(fechaped);
					((LinearLayout)popupView3.findViewById(R.id.cajatexto)).addView(linea);
					linea = new LinearLayout(getBaseContext()); linea.setLayoutParams(params);
					linea.addView(horatxt); linea.addView(horaped);
					((LinearLayout)popupView3.findViewById(R.id.cajatexto)).addView(linea);
					((LinearLayout)popupView3.findViewById(R.id.cajatexto)).addView(obspedtxt);
					((LinearLayout)popupView3.findViewById(R.id.cajatexto)).addView(observpedido);
					//referencia.setText(sharedPrefs.getString("ref"+eidactual,""));
					referencia.setText(e.getReferenciapedido());
					fechaped.setText(e.getFechacita());
					horaped.setText(e.getHoracita());
					observpedido.setText(e.getObservaciones());
					popupView3.findViewById(R.id.no).setVisibility(View.GONE);
					ImageButton si = (ImageButton) popupView3.findViewById(R.id.si);
					si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
					si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						popupWindow3.dismiss();
							e.setReferenciapedido(referencia.getText().toString());
							e.setFechacita(fechaped.getText().toString());
							e.setHoracita(horaped.getText().toString());
							e.setObservaciones(observpedido.getText().toString());
							db.updateDatosEstablecimiento(e);
					}});
			}});

		//ImageButton cancelar = (ImageButton)popupView.findViewById(R.id.cancel);
		cancelar.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				popupWindow.dismiss(); }});
		}});
		botontotal.setPadding(5,5,5,5); total.addView(botontotal,lp);
		tl.addView(total);
		if(auto) botontotal.performClick();
	}

	public void ventanaSeguro(LayoutInflater layoutInflater){ //Sólo para Pedido
	//Si hay modo Seguro:
		if (sharedPrefs.getBoolean("ms", false) == true) {
			popupView = layoutInflater.inflate(R.layout.popupms, null);
			popupWindow = new PopupWindow(popupView,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
			popupWindow.setBackgroundDrawable(getResources().getDrawable(
				android.R.drawable.alert_light_frame));
			popupWindow.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
			((TextView) popupView.findViewById(R.id.texto)).setText((((TextView) popupView
				.findViewById(R.id.texto)).getText())+ " " + getString(R.string.motivo2));
			ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
			ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
			si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
				if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
				if (((EditText) popupView.findViewById(R.id.mspass)).getText()
					.toString().equals(sharedPrefs.getString("pass",""))) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
							InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(((EditText) popupView.findViewById(R.id.mspass)).getWindowToken(), 0);
							popupWindow.dismiss();
							new envioPedido().execute();
					} else ((EditText) popupView.findViewById(R.id.mspass)).requestFocus();
				}});
			no.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {
				if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(((EditText) popupView.findViewById(R.id.mspass)).getWindowToken(), 0);
				popupWindow.dismiss();
				Toast.makeText(getApplicationContext(),getString(R.string.cancel2),Toast.LENGTH_LONG).show();
			}});
			//Si no es Modo Seguro
		} else {if (sharedPrefs.getBoolean("ch", true)) popupView.performHapticFeedback(1);
				new envioPedido().execute(); }
	}

	private OnLongClickListener OnLongClickTableRow = new OnLongClickListener() {
		@Override public boolean onLongClick(View v) {
			if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
			LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
			popupView = layoutInflater.inflate(R.layout.popuparticulopedido,null);
			popupWindow = new PopupWindow(popupView,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
			popupWindow.setBackgroundDrawable(getResources().getDrawable(
				android.R.drawable.alert_light_frame));
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			{popupWindow.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
			Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			if(display.getRotation()==Surface.ROTATION_90)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else if(display.getRotation()==Surface.ROTATION_270){
				if(android.os.Build.VERSION.SDK_INT == 8)
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
			}else{ popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER,0, -100);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);}
			final ImageButton editararticulo = (ImageButton)popupView.findViewById(R.id.editararticulo);
			final Ped p = (Ped) v.getTag();
			TextView artpedarticulo = (TextView) popupView.findViewById(R.id.popartpedarticulo);
			final EditText editartpedarticulo = (EditText) getLayoutInflater().inflate(R.layout.edittext, null);
			ArtEst ae = db.getArticuloEstablecimientoInterno(p.getAid());
			if(ae==null) editararticulo.setVisibility(View.VISIBLE); flageapart=0;
			editararticulo.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			editararticulo.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) { flageapart=1;
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					editartpedarticulo.setText(db.getArticulo(p.getAid()).getArticulo());
					editartpedarticulo.setGravity(Gravity.CENTER);
					editartpedarticulo.setOnFocusChangeListener(new OnFocusChangeListener() {
						@Override public void onFocusChange(View v,boolean hasFocus){if(hasFocus == true){
							InputMethodManager inputMgr = (InputMethodManager)
									getSystemService(Context.INPUT_METHOD_SERVICE);
							inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
							inputMgr.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT); }}});
					RelativeLayout rl = (RelativeLayout)popupView.findViewById(R.id.encabezado);
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(rl.getLayoutParams());
					lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
					editararticulo.setVisibility(View.GONE);
					rl.removeViewAt(1); rl.addView(editartpedarticulo,lp);
					editartpedarticulo.requestFocus(); }});
			artpedarticulo.setText(db.getArticulo(p.getAid()).getArticulo());
			tipo = (Spinner) popupView.findViewById(R.id.tipo);
			tipo.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			int nt= 0;
			for(String t : getResources().getStringArray(R.array.tipoundv))
				if(t.equals(db.getArticulo(p.getAid()).getTipo())){
					tipo.setSelection(nt); break;} else nt++;
			artpedcantidad = (EditText) popupView.findViewById(R.id.popartpedcantidad);
			artpedcantidad.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override public void onFocusChange(View v,boolean hasFocus){if(hasFocus == true){
					InputMethodManager inputMgr = (InputMethodManager)
							getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
					inputMgr.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);
				}else if(artpedcantidad.getText().toString().equals("")
						|| artpedcantidad.getText().toString().equals("0")) artpedcantidad.setText("1");
					}});
			artpedcantidad.requestFocus();
			if (tipo.getSelectedItemPosition() == 0 || tipo.getSelectedItemPosition() == 2) {
				Short s = ((Double) p.getCantidad()).shortValue();
				artpedcantidad.setText(s.toString());
			} else {
				artpedcantidad.setText(String.valueOf(p.getCantidad()).replaceAll(",", "."));
				artpedcantidad.setInputType(InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			}
			tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override public void onItemSelected(
				AdapterView<?> adapterView,View view, int i, long l) {
					if (tipo.getSelectedItemPosition() == 0 || tipo.getSelectedItemPosition() == 2 ||
							tipo.getSelectedItemPosition() == 3 ) {
						Short s = Double.valueOf(artpedcantidad.getText().toString()).shortValue();
						artpedcantidad.setText(s.toString());
						artpedcantidad.setInputType(InputType.TYPE_CLASS_NUMBER);
					} else
						artpedcantidad.setInputType(InputType.TYPE_CLASS_NUMBER
								| InputType.TYPE_NUMBER_FLAG_DECIMAL);
				}
				@Override public void onNothingSelected(AdapterView<?> adapterView) { }
			});
			final String coin; final double conv;
			if (sharedPrefs.getString("moneda", "").equals("4")) {
				coin = "¥"; conv = 129.423949;
			} else if (sharedPrefs.getString("moneda", "").equals("2")) {
				coin = "$"; conv = 1.3121;
			} else if (sharedPrefs.getString("moneda", "").equals("3")) {
				coin = "£"; conv = 0.866358534;
			} else { coin = "€"; conv = 1;}
			artpedprecio = (EditText) popupView.findViewById(R.id.popartpedprecio);

				//Parte de flagop==0
				TextView preciofijo = (TextView) popupView.findViewById(R.id.popartpedpreciofijo);

				if (sharedPrefs.getString("moneda", "").equals("4")) {
					preciofijo.setText(String.format("%.0f", (p.getPrecio() * conv)).replaceAll(",", ".")+coin);
					//artpedprecio.setInputType(InputType.TYPE_CLASS_NUMBER);
				} else
					preciofijo.setText(String.format("%.2f", (p.getPrecio() * conv)).replaceAll(",", ".")+coin);

				artpedprecio.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override public void onFocusChange(View v, boolean hasFocus) { if (hasFocus == true) {
							InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
							inputMgr.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);}
						else if(artpedprecio.getText().toString().equals("")) artpedprecio.setText("0");
						/*if(artpedprecio.getText().toString().equals("0"))
							((TextView)popupView.findViewById(R.id.popartpedtxtprecio)).setTextColor(Color.RED);*/
						}});
				if (sharedPrefs.getString("moneda", "").equals("4")) {
					artpedprecio.setText(String.format("%.0f",
							(p.getPrecio() * conv)).replaceAll(",", "."));
					artpedprecio.setInputType(InputType.TYPE_CLASS_NUMBER);
				} else
					artpedprecio.setText(String.format("%.2f",
							(p.getPrecio() * conv)).replaceAll(",", "."));
				if(flagop==0){ preciofijo.setVisibility(View.VISIBLE);
							   artpedprecio.setVisibility(View.GONE); }
			final TextView importe = (TextView) popupView
					.findViewById(R.id.popartpedimporte);
			double imp = Double.valueOf(artpedcantidad.getText().toString())
					* Double.valueOf(artpedprecio.getText().toString());
			if (sharedPrefs.getString("moneda", "").equals("2")) {
				((TextView) popupView.findViewById(R.id.popartpedtxtprecio))
						.setText(((TextView) popupView.findViewById(R.id.popartpedtxtprecio))
						.getText().toString()+ " $");
				importe.setText(getString(R.string.importe) + ": " + coin
						+ String.format("%.2f", imp));
			} else if (sharedPrefs.getString("moneda", "").equals("4"))
				importe.setText(coin + " " + getString(R.string.importe) + ": "
						+ String.format("%.0f", imp) + coin);
			else
				importe.setText(coin + " " + getString(R.string.importe) + ": "
						+ String.format("%.2f", imp) + coin);
			TextWatcher inputTextWatcher = new TextWatcher() {
				@Override public void afterTextChanged(Editable s) {
					double impor = 0; int gr=1;
					if(tipo.getSelectedItemPosition() == 2) gr=1000;
					try{if(artpedcantidad.getText().length() != 0 && artpedprecio.getText().length() != 0)
						impor = Double.valueOf(artpedcantidad.getText().toString())/gr
								* Double.valueOf(artpedprecio.getText().toString());}
					catch(NumberFormatException e){e.printStackTrace(); impor=0;}

					if (sharedPrefs.getString("moneda", "").equals("2"))
						importe.setText(getString(R.string.importe) + ": "
								+ coin + String.format("%.2f", impor));
					else if (sharedPrefs.getString("moneda", "").equals("4"))
						importe.setText(getString(R.string.importe) + ": "
								+ String.format("%.0f", impor) + coin);
					else
						importe.setText(getString(R.string.importe) + ": "
								+ String.format("%.2f", impor) + coin);
					/*if(impor==0){
						importe.setText("¿"+importe.getText().toString()+"?");
						if(!artpedcantidad.getText().toString().equals("") &&
								!artpedcantidad.getText().toString().equals("0"))
						((TextView)popupView.findViewById(R.id.popartpedtxtprecio)).setTextColor(Color.RED);
					}
					else ((TextView)popupView.findViewById(R.id.popartpedtxtprecio)).setTextColor(Color.BLACK);*/
				}
				@Override public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
				@Override public void onTextChanged(CharSequence s, int start,int before, int count) {}
			};
			artpedcantidad.addTextChangedListener(inputTextWatcher);
			artpedprecio.addTextChangedListener(inputTextWatcher);
			final ImageButton avanzado = (ImageButton) popupView.findViewById(R.id.avanzado);
			avanzado.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			avanzado.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
					if(avanzado.getDrawable().getConstantState().equals(getResources()
							.getDrawable(R.drawable.navigation_expand).getConstantState())) {
						avanzado.setImageDrawable(getResources().getDrawable(R.drawable.navigation_collapse));
						popupView.findViewById(R.id.cajacbarras).setVisibility(View.VISIBLE);
						popupView.findViewById(R.id.cajaobs).setVisibility(View.VISIBLE);
						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
							popupView.findViewById(R.id.opciones).setVisibility(View.VISIBLE);
					} else {
						avanzado.setImageDrawable(getResources().getDrawable(R.drawable.navigation_expand));
						popupView.findViewById(R.id.cajacbarras).setVisibility(View.GONE);
						popupView.findViewById(R.id.cajaobs).setVisibility(View.GONE);
						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
							popupView.findViewById(R.id.opciones).setVisibility(View.GONE);
					} }});
			artpedobs = (EditText) popupView.findViewById(R.id.popartpedobs);
			artpedobs.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus == true) {
						InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
						inputMgr.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);}
			}});
			artpedobs.setText(p.getObservacion());
			ImageButton artpedmicobs = (ImageButton) popupView.findViewById(R.id.popartpedmicobs);
			artpedmicobs.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
				if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
				try {startActivityForResult(intent, 8);
				artpedobs.setText("");} catch (ActivityNotFoundException a) {
					Toast.makeText(getApplicationContext(),
							getString(R.string.previousListDeviceNotSupported),Toast.LENGTH_SHORT).show();} }});

			artpedcbarras = (EditText) popupView.findViewById(R.id.popartpedcbarras);
			artpedcbarras.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus == true) {
						InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
						inputMgr.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);}
			}});
			artpedcbarras.setText(db.getArticulo(p.getAid()).getCbarras());
			ImageButton escanear = (ImageButton) popupView.findViewById(R.id.popartpedscan);
			escanear.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			escanear.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					/*try{Intent intent = new Intent("com.google.zxing.client.android.SCAN");
						intent.setPackage("com.disoft.distarea");
						intent.putExtra("SCAN_MODE","QR_CODE_MODE,PRODUCT_MODE");
						startActivityForResult(intent, 1);
					} catch (Exception e) {e.printStackTrace();}*/
					try{
						IntentIntegrator integrator = new IntentIntegrator(ListaCompra.this);
						integrator.initiateScan();
					} catch (Exception e) {e.printStackTrace();}
			}});
			ImageButton editar = (ImageButton) popupView.findViewById(R.id.popartpededi);
			editar.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			editar.setTag(p);
			editar.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					// Actualizar el Pedido en la BBDD
					int fail=0;
					if(flageapart==1){ int flagnomart=0;
						if(!editartpedarticulo.getText().toString().equals(
								db.getArticulo(p.getAid()).getArticulo()))
						for(Art a : db.getAllArticulos())
							if(a.getArticulo().equals(editartpedarticulo.getText().toString())) flagnomart=1;
						if (editartpedarticulo.getText().toString().equals("") || flagnomart==1){ fail=1;
							if(flagnomart==1)
								Toast.makeText(getBaseContext(),R.string.yaexiste,Toast.LENGTH_LONG).show();}}
					if(fail==1) editartpedarticulo.requestFocus();
					else if (artpedcantidad.getText().toString().equals("")) artpedcantidad.requestFocus();
					else if (artpedprecio.getText().toString().equals("") && flagop==1) artpedprecio.requestFocus();
					else if (!artpedcbarras.getText().toString().equals("")) {
						Ped actPed = (Ped) v.getTag(); int aid = 0; flagcbarras=0;
						Art articulo = db.getArticulo(artpedcbarras.getText().toString());
						if (articulo != null) aid = articulo.getAid();
						if (aid > 0){ if (aid != actPed.getAid()) { flagcbarras=1;
							if(avanzado.getDrawable().getConstantState().equals(getResources()
									.getDrawable(R.drawable.navigation_expand).getConstantState()))
								avanzado.performClick();
							artpedcbarras.requestFocus();
							((TextView) popupView.findViewById(R.id.popartpedtxtcbarras)).setTextColor(Color.RED);
							artpedcbarras.addTextChangedListener(new TextWatcher() {
			String codigobarras = artpedcbarras.getText().toString();
			@Override public void afterTextChanged(Editable s) {
				if (!s.equals(codigobarras))
					((TextView) popupView.findViewById(R.id.popartpedtxtcbarras)).setTextColor(Color.BLACK);}
				@Override public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
				@Override public void onTextChanged(CharSequence s, int start,int before, int count) {}});
						}}
						if(flagcbarras==0){ if(!artpedcantidad.getText().toString().equals("")
								&& !artpedcantidad.getText().toString().equals("0"))
							actPed.setCantidad(Double.parseDouble(artpedcantidad.getText().toString()));
						else actPed.setCantidad(1);
						if(!artpedprecio.getText().toString().equals("")
								&& !artpedprecio.getText().toString().equals("."))
							actPed.setPrecio(Double.parseDouble(artpedprecio.getText().toString()) / conv);
						else actPed.setPrecio(0.0 / conv);
						actPed.setObservacion(artpedobs.getText().toString());
						actPed.setPreciomanual("S");
						db.updatePedido(actPed);
						Art actArt = db.getArticulo(actPed.getAid());
						if(flageapart==1) actArt.setArticulo(editartpedarticulo.getText().toString());
						actArt.setTipo(getResources().getStringArray(R.array.tipoundv)[tipo.getSelectedItemPosition()]);
						actArt.setCbarras(artpedcbarras.getText().toString());
						db.updateArticulo(actArt); popupWindow.dismiss();
						Intent intent = new Intent(ListaCompra.this,ListaCompra.class);
						//intent.putExtra("establecimiento",mIcsSpinner.getSelectedItemPosition());
						intent.putExtra("eid",eidactual);
						startActivity(intent); finish(); }
					} else{ Ped actPed = (Ped) v.getTag();
						if(!artpedcantidad.getText().toString().equals("")
								&& !artpedcantidad.getText().toString().equals("0"))
							actPed.setCantidad(Double.parseDouble(artpedcantidad.getText().toString()));
						else actPed.setCantidad(1);
						if(!artpedprecio.getText().toString().equals("")
								&& !artpedprecio.getText().toString().equals("."))
							actPed.setPrecio(Double.parseDouble(artpedprecio.getText().toString()) / conv);
						else actPed.setPrecio(0.00 / conv);
						actPed.setObservacion(artpedobs.getText().toString());
						db.updatePedido(actPed);
						Art actArt = db.getArticulo(actPed.getAid());
						if(flageapart==1) actArt.setArticulo(editartpedarticulo.getText().toString());
						actArt.setTipo(getResources().getStringArray(R.array.tipoundv)[tipo.getSelectedItemPosition()]);
						actArt.setCbarras(artpedcbarras.getText().toString());
						db.updateArticulo(actArt); popupWindow.dismiss();
						Intent intent = new Intent(ListaCompra.this,ListaCompra.class);
						//intent.putExtra("establecimiento",mIcsSpinner.getSelectedItemPosition());
						intent.putExtra("eid",eidactual);
						startActivity(intent); finish(); }
			}});
			ImageButton cancelar = (ImageButton) popupView.findViewById(R.id.popartpedcan);
			cancelar.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			cancelar.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
					popupWindow.dismiss();
			}});
			ImageButton borrar = (ImageButton) popupView.findViewById(R.id.popartpedbor);
			borrar.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			borrar.setTag(p);
			borrar.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
					LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
					View popupView2 = layoutInflater.inflate(R.layout.popupsino, null);
					popupWindow2 = new PopupWindow(popupView2,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
					popupWindow2.setBackgroundDrawable(getResources()
						.getDrawable(android.R.drawable.alert_light_frame));
					popupWindow2.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
					((TextView) popupView2.findViewById(R.id.texto))
						.setText(getString(R.string.textoconfborart));
					ImageButton si = (ImageButton) popupView2.findViewById(R.id.si);
					si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
					si.setTag(v.getTag());
					si.setOnClickListener(new OnClickListener() {
						@Override public void onClick(View v) {
							if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
							Ped p = (Ped) v.getTag(); db.deleteArticuloPedido(p);
							if(p.getAid()<0 || eidactual<0){
								db.deleteArticulo(db.getArticulo(p.getAid()));
								db.deleteArticuloEstablecimiento(p.getAid(), eidactual);
							}
							//No sé por qué hacía esto al borrar: Me cargo el artículo si no está usado y soy Operador
							/*if(sharedPrefs.getInt("solicitacliest", 0)==eidactual){
								if(p.getIdclif()!=0 && p.getIdclif()==idclif){
									db.deleteArticuloPedido(p);
									if(!db.checkArticuloPedido(p.getAid())){
										db.deleteArticulo(db.getArticulo(p.getAid()));
										db.deleteArticuloEstablecimiento(p.getAid(), eidactual);
									}
								}
							}else{
								db.deleteArticuloPedido(p);
								if(!db.checkArticuloPedido(p.getAid())){
									db.deleteArticulo(db.getArticulo(p.getAid()));
									db.deleteArticuloEstablecimiento(p.getAid(),0);
								}
							}*/
							popupWindow2.dismiss(); popupWindow.dismiss();
							Intent intent = new Intent(ListaCompra.this,ListaCompra.class);
							//intent.putExtra("establecimiento",mIcsSpinner.getSelectedItemPosition());
							intent.putExtra("eid",eidactual);
							startActivity(intent); finish();
					}});
					ImageButton no = (ImageButton) popupView2.findViewById(R.id.no);
					no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
					no.setOnClickListener(new OnClickListener() {
						@Override public void onClick(View v) {
							if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
							popupWindow2.dismiss();
					}});
			}});return false;
		}
	};

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if(sharedPrefs.getInt("internetmode",0)==0)
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		else return false;}

	public void contenidoBotonAdd() {
		if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
		if(e.getConfigura() != null && e.getConfigura().contains(",PD,")){
			if(e.getTv() == null || e.getTv().equals("")){
				Toast.makeText(getBaseContext(), "No ha sido posible conectar con la tienda virtual de este " +
						"establecimiento. Por favor, p\u00F3ngase en contacto con el due\u00F1o del establecimiento, " +
						"o espere a que se reestablezca la conexi\u00F3n." , Toast.LENGTH_LONG).show();
			}else{
			if(db.getAllArticulosEstablecimientoFrom(e.getEid()).size()==0 && e.getConfigura().contains(",V,")){
			//Popup descarga productos -> Establecimiento
			AlertDialog.Builder adb = new AlertDialog.Builder(ListaCompra.this);
			adb.setCancelable(false).setIcon(R.drawable.av_download).setTitle("Descarga artículos")
				.setMessage("Para poder trabajar con el establecimiento "+e.getNombre()+" debe descargar sus artículos primero. Recomendamos conectar a través de una red Wi-Fi, para evitar un consumo excesivo de datos móviles. ¿Está seguro de querer descargarlos ahora?");
			final CheckBox cbimagenes = new CheckBox(getBaseContext());
			cbimagenes.setText("Descargar tambi\u00E9n im\u00E1genes de los art\u00F3culos (tardar\u00EDa a\u00FAn m\u00E1s).");
			cbimagenes.setTextColor(getResources().getColor(android.R.color.black));
			if(sharedPrefs.getBoolean("vi",true)) cbimagenes.setChecked(true);
			else cbimagenes.setChecked(false);
			adb.setView(cbimagenes);
			adb.setPositiveButton("Descargar", new DialogInterface.OnClickListener(){
					@Override public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						//Descargar los artículos
						if(sharedPrefs.getBoolean("ch",true)) ll.performHapticFeedback(1);
				 		Intent intent = new Intent(ListaCompra.this, TiendaVirtual.class);
				    	intent.putExtra("eid", e.getEid());
				    	intent.putExtra("intent", "listacompra");
				    	intent.putExtra("funcion", "descarga");
				    	intent.putExtra("imagenes", cbimagenes.isChecked());
				    	Toast.makeText(getBaseContext(), R.string.dltoast ,Toast.LENGTH_LONG).show();
				    	startActivity(intent);
						//Toast.makeText(getBaseContext(), "Ahora se descargarían los artículos.", Toast.LENGTH_LONG).show();
			}}).setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
					@Override public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
			}});
			adb.create().show();
			}else if(db.getPedidosPendientes(e.getEid()).size()==
					db.getAllArticulosEstablecimientoFrom(e.getEid()).size() && !e.getConfigura().contains(",PD,"))
				Toast.makeText(getBaseContext(),"No quedan artículos que añadir. Si echa en falta algún artículo, pruebe a actualizar la lista de Artículos de este establecimiento.",Toast.LENGTH_LONG).show();
			else{
		if(e.getCnae() != null && e.getCnae().contains("39110") ||
				e.getCnae() != null && e.getCnae().contains("39100") ){ //CNAE Restaurante
			//Previo (reqTel)
			if(sharedPrefs.getString("tel","").equals("")){
				final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View popupViewDatos = layoutInflater.inflate(R.layout.popupsino, null);
				final PopupWindow popupWindowDatos = new PopupWindow(popupViewDatos,android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
				popupWindowDatos.setBackgroundDrawable(getResources().getDrawable(
						android.R.drawable.alert_light_frame));
				popupWindowDatos.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				if(display.getRotation()==Surface.ROTATION_90)
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				else if(display.getRotation()==Surface.ROTATION_270){
					if(android.os.Build.VERSION.SDK_INT == 8)
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
				}else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				TextView txt =(TextView) popupViewDatos.findViewById(R.id.texto);
				txt.setText("Se ha detectado la siguiente carencia de información:\n" +
						"\n\t· No se ha definido número de teléfono por defecto.\n" +
						"\nPara completar la información " +
						"necesaria para realizar el pedido, por favor, acceda a Configuración -> " +
						"Datos de Usuario, o pulse el botón de la izquierda.");
				ImageButton si = (ImageButton) popupViewDatos.findViewById(R.id.si);
				si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				si.setImageDrawable(getResources().getDrawable(R.drawable.action_settings));
				si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
					Intent i = new Intent(ListaCompra.this, Opciones2.class); startActivity(i); }});
				ImageButton no = (ImageButton) popupViewDatos.findViewById(R.id.no);
				no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
					popupWindowDatos.dismiss(); }});
			}else{
			//Solicitud Mesa
			//Texto + cuadro texto mesa/teléfono
			//Enviar/cancelar
				//if(sharedPrefs.getString("ref"+eidactual,"").equals("") || ver==0) {
				if(e.getReferenciapedido().equals("") || ver==0) {
			int flagit=0; //Define si referencia -> numérico o texto
			final EditText referencia = (EditText) getLayoutInflater().inflate(R.layout.edittext, null);
			final ImageButton inputtype = new ImageButton(getBaseContext());

			final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
			popupView = layoutInflater.inflate(R.layout.popupsino, null);
			if (sharedPrefs.getBoolean("ch", true)) popupView.performHapticFeedback(1);
			popupWindow = new PopupWindow(popupView,android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
			popupWindow.setBackgroundDrawable(getResources().getDrawable(
				android.R.drawable.alert_light_frame));
			popupWindow.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			{Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			if(display.getRotation()==Surface.ROTATION_90)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else if(display.getRotation()==Surface.ROTATION_270){
				if(android.os.Build.VERSION.SDK_INT == 8)
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
			}else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			((TextView)popupView.findViewById(R.id.texto)).setGravity(Gravity.BOTTOM);
			referencia.setTextColor(Color.BLACK); referencia.setSingleLine();
			referencia.setEms(9);
			if (sharedPrefs.getInt("solicitacliest",0)==e.getEid() && e.getEid()>0) flagit=1;
			etTxt = new OnClickListener() { @Override public void onClick(View v) {
				referencia.setInputType(InputType.TYPE_CLASS_TEXT);
				inputtype.setImageDrawable(getResources().getDrawable(R.drawable.a123));
				referencia.setText(""); referencia.requestFocus();
				inputtype.setOnClickListener(etNum); }};
			etNum = new OnClickListener() { @Override public void onClick(View v) {
					referencia.setInputType(InputType.TYPE_CLASS_PHONE);
					inputtype.setImageDrawable(getResources().getDrawable(R.drawable.abc));
					//referencia.setText(sharedPrefs.getString("tel", ""));
					referencia.requestFocus();
					inputtype.setOnClickListener(etTxt); }};
			if(flagit==0){ referencia.setInputType(InputType.TYPE_CLASS_PHONE);
				inputtype.setImageDrawable(getResources().getDrawable(R.drawable.abc));
				inputtype.setOnClickListener(etTxt);
				referencia.setText(sharedPrefs.getString("tel", ""));
				((TextView)popupView.findViewById(R.id.texto)).setText("Nº Tlf: ");
			}else{ referencia.setInputType(InputType.TYPE_CLASS_TEXT);
			inputtype.setImageDrawable(getResources().getDrawable(R.drawable.a123));
			inputtype.setOnClickListener(etNum);
			((TextView)popupView.findViewById(R.id.texto)).setText("Mesa/\nNº Tlf: ");}
			LinearLayout linea = new LinearLayout(getBaseContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			linea.setLayoutParams(params);
			LinearLayout.LayoutParams paramswrap = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			inputtype.setLayoutParams(paramswrap);
			LinearLayout.LayoutParams paramset = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT); paramset.weight=1;
					paramset.gravity=Gravity.BOTTOM;
			referencia.setLayoutParams(paramset);
			linea.addView(referencia); linea.addView(inputtype);
			((LinearLayout)popupView.findViewById(R.id.cajatexto)).setOrientation(LinearLayout.HORIZONTAL);
			((LinearLayout)popupView.findViewById(R.id.cajatexto)).addView(linea);
			referencia.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			final ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
			si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
				if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(popupView.getWindowToken(), 0);
				popupWindow.dismiss();
				//sharedPrefs.edit().putString("ref"+eidactual,referencia.getText().toString()).commit();
				e.setReferenciapedido(referencia.getText().toString());
				db.updateDatosEstablecimiento(e);
				//referenciapedido = referencia.getText().toString();
										// new envioPedido().execute();

				//Empieza Articulos Restaurante

				/*int contador = db.getArticulosCount();
				for(Ped p : db.getAllPedidos(eidactual)){
					for(Art a : db.getAllArticulos()){
						if(a.getAid()==p.getAid()) contador--;}}
				for(Art a : db.getAllArticulos()){
					for(String[] a2 : db.getAllArticulosEstablecimientoBut(eidactual)){
						if(a.getAid()==Integer.parseInt(a2[0])) contador--; }}*/
				/*
				int contador = db.getAllArticulosEstablecimientoFrom(e.getEid()).size()+1;
				//XXX Bypass descarga Restaurante
				if(contador>0){
					Intent i = new Intent(ListaCompra.this, ListaArticulos.class);
					i.putExtra("establecimiento", mIcsSpinner.getSelectedItemPosition());
					i.putExtra("eid", eidactual); startActivity(i); }
				else{
					AlertDialog.Builder adb = new AlertDialog.Builder(ListaCompra.this);
					adb.setCancelable(false).setIcon(R.drawable.av_download).setTitle("Descarga artículos")
						.setMessage("Para poder trabajar con el establecimiento "+e.getNombre()+" debe descargar sus artículos primero. Recomendamos conectar a través de una red Wi-Fi, para evitar un consumo excesivo de datos móviles. ¿Está seguro de querer descargarlos ahora?")
						.setPositiveButton("Descargar", new DialogInterface.OnClickListener(){
							@Override public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								//Descargar los artículos
								Toast.makeText(getBaseContext(), "Ahora se descargarían los artículos de Restaurante", Toast.LENGTH_LONG).show();
					}}).setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
							@Override public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
					}});
					adb.create().show();
				}*/

				//Continuación: Soy Restaurante y apunto una mesa -> ListaArticulos
				int contador = db.getAllArticulosEstablecimientoFrom(e.getEid()).size()+1;
				//XXX Bypass descarga Restaurante
				if(contador>0){
					Intent i = new Intent(ListaCompra.this, ListaArticulos.class);
					i.putExtra("establecimiento", mIcsSpinner.getSelectedItemPosition());
					i.putExtra("eid", eidactual); startActivity(i); }
				else{
					AlertDialog.Builder adb = new AlertDialog.Builder(ListaCompra.this);
					adb.setCancelable(false).setIcon(R.drawable.av_download).setTitle("Descarga artículos")
						.setMessage("Para poder trabajar con el establecimiento "+e.getNombre()+" debe descargar sus artículos primero. Recomendamos conectar a través de una red Wi-Fi, para evitar un consumo excesivo de datos móviles. ¿Está seguro de querer descargarlos ahora?")
						.setPositiveButton("Descargar", new DialogInterface.OnClickListener(){
							@Override public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								//Descargar los artículos
								//Toast.makeText(getBaseContext(), "Ahora se descargarían los artículos de Restaurante", Toast.LENGTH_LONG).show();
								if(sharedPrefs.getBoolean("ch",true)) popupView.performHapticFeedback(1);
						 		Intent intent = new Intent(ListaCompra.this, TiendaVirtual.class);
						 		intent.putExtra("eid", e.getEid());
						 		intent.putExtra("intent", "listacompra");
						 		Toast.makeText(getBaseContext(), R.string.dltoast ,Toast.LENGTH_LONG).show();
						 		startActivity(intent);
					}}).setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
							@Override public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
					}});
					adb.create().show();
				}

			}});

			ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
			no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			no.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {
				if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(popupView.getWindowToken(), 0);
				popupWindow.dismiss();
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}});

			referencia.setOnKeyListener(new View.OnKeyListener(){
 			      public boolean onKey(View v, int keyCode, KeyEvent event){
 			          if (event.getAction() == KeyEvent.ACTION_DOWN){
 			              switch (keyCode){
 			                  case KeyEvent.KEYCODE_DPAD_CENTER:
 			                  case KeyEvent.KEYCODE_ENTER:
 			                      si.performClick(); return true;
 			                  default: break;
 			              }
 			          }return false;
 			      }});
				}else{ //Continúa añadiendo al pedido actual
					//Empieza Articulos Restaurante

					/*int contador = db.getArticulosCount();
					for(Ped p : db.getAllPedidos(eidactual)){
						for(Art a : db.getAllArticulos()){
							if(a.getAid()==p.getAid()) contador--;}}
					for(Art a : db.getAllArticulos()){
						for(String[] a2 : db.getAllArticulosEstablecimientoBut(eidactual)){
							if(a.getAid()==Integer.parseInt(a2[0])) contador--; }}*/
					int contador = db.getAllArticulosEstablecimientoFrom(e.getEid()).size()+1;
					//XXX Bypass descarga Restaurante
					if(contador>0){
						Intent i = new Intent(ListaCompra.this, ListaArticulos.class);
						i.putExtra("establecimiento", mIcsSpinner.getSelectedItemPosition());
						i.putExtra("eid", eidactual); startActivity(i); }
					else{
						AlertDialog.Builder adb = new AlertDialog.Builder(ListaCompra.this);
						adb.setCancelable(false).setIcon(R.drawable.av_download).setTitle("Descarga artículos")
							.setMessage("Para poder trabajar con el establecimiento "+e.getNombre()+" debe descargar sus artículos primero. Recomendamos conectar a través de una red Wi-Fi, para evitar un consumo excesivo de datos móviles. ¿Está seguro de querer descargarlos ahora?")
							.setPositiveButton("Descargar", new DialogInterface.OnClickListener(){
								@Override public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									//Descargar los artículos
									//Toast.makeText(getBaseContext(), "Ahora se descargarían los artículos de Restaurante", Toast.LENGTH_LONG).show();
									if(sharedPrefs.getBoolean("ch",true)) popupView.performHapticFeedback(1);
							 		Intent intent = new Intent(ListaCompra.this, TiendaVirtual.class);
							 		intent.putExtra("eid", e.getEid());
							 		intent.putExtra("intent", "listacompra");
							 		Toast.makeText(getBaseContext(), R.string.dltoast ,Toast.LENGTH_LONG).show();
							 		startActivity(intent);
						}}).setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
								@Override public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
						}});
						adb.create().show();
					}
				}
		//Fin Restaurante
		}}else if(sharedPrefs.getInt("solicitacliest", 0)==e.getEid() && e.getConfigura() != null
				&& e.getConfigura().contains(",V,")){
					//new descargaClientes(ListaArticulos.class).execute();
				if(idclif!=0){
					Intent i = new Intent(ListaCompra.this, ListaArticulos.class);
 	   				i.putExtra("establecimiento", mIcsSpinner.getSelectedItemPosition());
 	   				i.putExtra("idclif", idclif);
 	   				i.putExtra("eid", eidactual); startActivity(i); }
				else new descargaClientes(ListaArticulos.class).execute();
			}else{
				Intent i = new Intent(ListaCompra.this, ListaArticulos.class);
				i.putExtra("establecimiento", mIcsSpinner.getSelectedItemPosition());
				i.putExtra("eid", eidactual); startActivity(i); }
		}
		}}else{ //Si no son artículos descargados de una TiendaVirtual
			if(sharedPrefs.getInt("solicitacliest", 0)==e.getEid() && e.getConfigura() != null
					&& e.getConfigura().contains(",V,"))
				new descargaClientes(ListaArticulos.class).execute();
		final LayoutInflater layoutInflater=(LayoutInflater)getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		popupView = layoutInflater.inflate(R.layout.popupadd, null);
		popupWindow = new PopupWindow(popupView,android.view.ViewGroup.LayoutParams.MATCH_PARENT,
			android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
			android.R.drawable.alert_light_frame));
		popupWindow.setContentView(popupView);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			{popupWindow.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
			Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			if(display.getRotation()==Surface.ROTATION_90)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else if(display.getRotation()==Surface.ROTATION_270){
				if(android.os.Build.VERSION.SDK_INT == 8)
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
		}else{ popupWindow.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, -100);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);}
		artarticulo = (EditText) popupView.findViewById(R.id.articulo);
		artarticulo.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == true) {
					InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
					inputMgr.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);} }});
		artunidades = (EditText) popupView.findViewById(R.id.unidades);
		artunidades.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override public void onFocusChange(View v, boolean hasFocus) {if (hasFocus == true) {
					InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
					inputMgr.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);
					artunidades.setSelection(artunidades.getText().length());
		}else if(artunidades.getText().toString().equals("")
				 || artunidades.getText().toString().equals("0")) artunidades.setText("1");
			}});
		tipo = (Spinner) popupView.findViewById(R.id.tipo);
		tipo.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
		tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> adapterView,View view,int i,long l) {
				if (tipo.getSelectedItemPosition() == 0) {
					Short s = Double.valueOf(artunidades.getText().toString()).shortValue();
					artunidades.setText(s.toString());
					artunidades.setInputType(InputType.TYPE_CLASS_NUMBER);
				} else artunidades.setInputType(InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_FLAG_DECIMAL); }
				@Override public void onNothingSelected(AdapterView<?> adapterView) {} });
		final ImageButton avanzado = (ImageButton) popupView.findViewById(R.id.avanzado);
		avanzado.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
		avanzado.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) {
			if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
			if (avanzado.getDrawable().getConstantState().equals(getResources()
				.getDrawable(R.drawable.navigation_expand).getConstantState())) {
					avanzado.setImageDrawable(getResources().getDrawable(R.drawable.navigation_collapse));
					popupView.findViewById(R.id.opciones).setVisibility(View.VISIBLE);
			} else {
				avanzado.setImageDrawable(getResources().getDrawable(R.drawable.navigation_expand));
				popupView.findViewById(R.id.opciones).setVisibility(View.GONE); } }});
		artprecio = (EditText) popupView.findViewById(R.id.precio);
		//((TextView)popupView.findViewById(R.id.popprecio)).setTextColor(Color.RED);
		artprecio.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == true) {
					InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
					inputMgr.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);
				/*if(artprecio.getText().toString().equals("0"))
					((TextView)popupView.findViewById(R.id.popprecio)).setTextColor(Color.RED);
				else ((TextView)popupView.findViewById(R.id.popprecio)).setTextColor(Color.BLACK);*/
			}else if(artprecio.getText().toString().equals("")) artprecio.setText("0");
			/*if(artprecio.getText().toString().equals("0"))
				((TextView)popupView.findViewById(R.id.popprecio)).setTextColor(Color.RED);
			else ((TextView)popupView.findViewById(R.id.popprecio)).setTextColor(Color.BLACK);*/
				}});
				if (sharedPrefs.getString("moneda", "1").equals("1"))
					((TextView) popupView.findViewById(R.id.moneda)).setText("€");
				else if (sharedPrefs.getString("moneda", "1").equals("2"))
					((TextView) popupView.findViewById(R.id.dolar)).setText("$");
				else if (sharedPrefs.getString("moneda", "1").equals("3"))
					((TextView) popupView.findViewById(R.id.moneda)).setText("£");
				else if (sharedPrefs.getString("moneda", "1").equals("4")) {
					artprecio.setInputType(InputType.TYPE_CLASS_NUMBER);
					((TextView) popupView.findViewById(R.id.moneda)).setText("¥"); }
				artobservaciones = (EditText) popupView.findViewById(R.id.obs);
				artobservaciones.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus == true) {
							InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
							inputMgr.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);} }});
				artcbarras = (EditText) popupView.findViewById(R.id.cbarras);
				artcbarras.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus == true) {
							InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
							inputMgr.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);} }});
				artcbarras.addTextChangedListener(new TextWatcher() {@Override public void
					onTextChanged(CharSequence s, int start, int before, int count) {
						((TextView)popupView.findViewById(R.id.popcbarras)).setTextColor(Color.BLACK);}
				@Override public void afterTextChanged(Editable arg0) {}
				@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				});
				ImageButton con = (ImageButton) popupView.findViewById(R.id.confirm);
				ImageButton can = (ImageButton) popupView.findViewById(R.id.cancel);
				ImageButton scan = (ImageButton) popupView.findViewById(R.id.scan);
				ImageButton list = (ImageButton) popupView.findViewById(R.id.list);
				//ProgressBar loading = (ProgressBar) popupView.findViewById(R.id.loading);
				ImageButton web = (ImageButton) popupView.findViewById(R.id.web);
				ImageButton mic = (ImageButton) popupView.findViewById(R.id.mic);
				ImageButton micobs = (ImageButton) popupView.findViewById(R.id.micobs);
				/*con.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				can.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				scan.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				list.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				web.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				mic.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);*/
				con.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					for(final Art a : db.getAllArticulos())
						if(a.getArticulo().equals(artarticulo.getText().toString())){ existe=1; //Si el artículo existe
							Ped pen = db.getPedidoPendiente(eidactual);
							if (pen != null){ //Si hay pedido

								/*
								COMENTO TRAMO SUMAR/REEMPLAZAR, YA QUE DEJA DE TENER SENTIDO AL PERMITIR REPETIDOS
								for(final Ped p : db.getPedidosPendientes(eidactual)){
									if(p.getAid()==a.getAid()){ flagartpend=1; //Y además está ya en el pedido
										//Ventana sumar/reemplazar/cancelar
										View popupView2 = layoutInflater.inflate(R.layout.popupsino, null);
										popupWindow2 = new PopupWindow(popupView2,android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
											android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
										popupWindow2.setBackgroundDrawable(getResources().getDrawable(
											android.R.drawable.alert_light_frame));
										popupWindow2.setContentView(popupView2);
										if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
											{popupWindow2.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
											Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
											if(display.getRotation()==Surface.ROTATION_90)
												setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
											else if(display.getRotation()==Surface.ROTATION_270){
												if(android.os.Build.VERSION.SDK_INT == 8)
													setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
												else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
										}else{ popupWindow2.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
											setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);}
										((TextView)popupView2.findViewById(R.id.texto)).setText(getString(R.string.artrep1)+
											" "+artunidades.getText().toString()+" "+a.getTipo()+" "+getString(R.string.de)+
											" "+a.getArticulo()+getString(R.string.artrep2)+" "+p.getCantidad()+" "+
											a.getTipo()+getString(R.string.artrepops));
											ImageButton suma = (ImageButton)popupView2.findViewById(R.id.si);
											ImageButton reemplazo = (ImageButton)popupView2.findViewById(R.id.no);
											ImageButton cancelar = new ImageButton(getBaseContext());
											suma.setImageDrawable(getResources().getDrawable(R.drawable.content_new));
											reemplazo.setImageDrawable(getResources().getDrawable(R.drawable.content_edit));
											cancelar.setImageDrawable(getResources().getDrawable(R.drawable.navigation_cancel));
											cancelar.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
											LinearLayout.LayoutParams posboton = new LinearLayout.LayoutParams(
												reemplazo.getLayoutParams()); posboton.weight=1; cancelar.setLayoutParams(posboton);
											((LinearLayout)popupView2.findViewById(R.id.botones)).addView(cancelar);
										//Opción Suma XXX
											suma.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
												if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
												Ped temp = new Ped(0,p.getPid(), a.getAid(), eidactual,
													sdfdia.format(new Date()),
													p.getCantidad()+Double.valueOf(artunidades.getText().toString()),
													p.getPrecio(),p.getObservacion(), p.getEstado(), p.getAfid(),
													p.getOferta(),p.getIdclif(),p.getPreciomanual());
												Toast.makeText(getBaseContext(), R.string.sumacant,
													Toast.LENGTH_LONG).show(); popupWindow2.dismiss();
												artarticulo.setText(""); artunidades.setText("1"); artprecio.setText("0");
												artobservaciones.setText(""); artcbarras.setText(""); artarticulo.requestFocus();
												db.updatePedido(temp); }});
										//Fin Suma
										//Opción Reemplazo
											reemplazo.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
												if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
												Ped temp = new Ped(0,p.getPid(), a.getAid(), eidactual,
													sdfdia.format(new Date()),
													Double.valueOf(artunidades.getText().toString()),
													p.getPrecio(),p.getObservacion(), p.getEstado(), p.getAfid(),
													p.getOferta(),p.getIdclif(), p.getPreciomanual());
												Toast.makeText(getBaseContext(), "Reemplazando cantidad de artículo repetido.",
													Toast.LENGTH_LONG).show(); popupWindow2.dismiss();
												artarticulo.setText(""); artunidades.setText("1"); artprecio.setText("0");
												artobservaciones.setText(""); artcbarras.setText(""); artarticulo.requestFocus();
												db.updatePedido(temp);  }});
										//Fin Reemplazo
										//Opción Cancelar
											cancelar.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
												if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
												Toast.makeText(getBaseContext(), "No se han realizado cambios.",
														Toast.LENGTH_LONG).show(); popupWindow2.dismiss(); }});
										//Fin Cancelar
										 break;}
										//Fin ventana
									}
								*/
								if(flagartpend==0){ //Y no existe en el pedido
									pen = new Ped(db.getLastAutoid()+1,pen.getPid(), a.getAid(), eidactual,
										sdfdia.format(new Date()), Double.valueOf(artunidades.getText().toString()),
										Double.valueOf(artprecio.getText().toString()),
										artobservaciones.getText().toString(), pen.getEstado(), pen.getAfid(),
										pen.getOferta(), idclif,"S");
									Toast.makeText(getBaseContext(), "Añadiendo artículo creado anteriormente.",
											Toast.LENGTH_LONG).show();
									artarticulo.setText(""); artunidades.setText("1"); artprecio.setText("0");
									artobservaciones.setText(""); artcbarras.setText(""); artarticulo.requestFocus();}
							}else{ if(eidactual>0){ //Si no hay pedido, y es un Establecimiento
									pen = new Ped(db.getLastAutoid()+1,sharedPrefs.getInt("nped",0)+1, a.getAid(), eidactual,
											sdfdia.format(new Date()), Double.valueOf(artunidades.getText().toString()),
											Double.valueOf(artprecio.getText().toString()),
											artobservaciones.getText().toString(),0,"","",
											idclif,"S");
									SharedPreferences.Editor spe = sharedPrefs.edit();
									spe.putInt("nped",sharedPrefs.getInt("nped",0)+1).commit(); }
								else pen = new Ped(db.getLastAutoid()+1,eidactual, a.getAid(), eidactual, //Si no hay pedido, y es una Lista
											sdfdia.format(new Date()), Double.valueOf(artunidades.getText().toString()),
											Double.valueOf(artprecio.getText().toString()),
											artobservaciones.getText().toString(),0,"","",
											idclif,"S");
							Toast.makeText(getBaseContext(), "Añadiendo artículo creado anteriormente.",
									Toast.LENGTH_LONG).show();
							artarticulo.setText(""); artunidades.setText("1"); artprecio.setText("0");
							artobservaciones.setText(""); artcbarras.setText(""); artarticulo.requestFocus();
							 } db.addPedido(pen); flagrefresh=1; } //Fin si el artículo existe
					//SIEMPRE ESTÁ ENTRANDO EN db.addPedido, CUANDO ABRO LA VENTANA DE REPES
					if(existe==0){
						Art ecb = db.getArticulo(artcbarras.getText().toString());
						if (artarticulo.getText().toString().equals("")) artarticulo.requestFocus();
						else if (artunidades.getText().toString().equals("")
								|| artunidades.getText().toString().equals("0"))
							artunidades.requestFocus();
						else if (artprecio.getText().toString().equals("")
								|| artprecio.getText().toString().equals("."))
							artprecio.requestFocus();
						else if (!artcbarras.getText().toString().equals("") && ecb!=null &&
								!ecb.getArticulo().equals(artarticulo.getText().toString())){
							if(((LinearLayout)popupView.findViewById(R.id.opciones)).getVisibility()==View.GONE)
								((ImageButton)popupView.findViewById(R.id.avanzado)).performClick();
							((TextView)popupView.findViewById(R.id.popcbarras)).setTextColor(Color.RED);
							Toast.makeText(getBaseContext(),"El código de barras insertado ya existe en otro artículo.",
									Toast.LENGTH_LONG).show();
							artcbarras.requestFocus();
						}else /*if(flagrefresh==0)*/{
							Est e = db.getEstablecimiento(mIcsSpinner.getSelectedItem().toString());
							Art checkcb = new Art(db.getLastAid() + 1,
								artarticulo.getText().toString(),artcbarras.getText().toString(),
								getResources().getStringArray(R.array.tipoundv)[tipo.getSelectedItemPosition()]);
							db.addArticulo(checkcb);
							//Creo ArtEst
							db.addArticuloEstablecimiento(new ArtEst(checkcb.getAid(),"-"+checkcb.getAid(),e.getEid(),"","",0,"S"));

							// Comprueba si hay pendiente, si no, comprueba si
							// hay cerrado sin enviar, y si no, crea uno nuevo

							Ped pen = db.getPedidoPendiente(e.getEid());
							if (pen != null){
								pen = new Ped(db.getLastAutoid()+1,pen.getPid(), checkcb.getAid(), e.getEid(),
										sdfdia.format(new Date()), Double.valueOf(artunidades.getText().toString()),
										Double.valueOf(artprecio.getText().toString()),
										artobservaciones.getText().toString(), pen.getEstado(), pen.getAfid(),
										pen.getOferta(),pen.getIdclif(),"S");
								if(e.getEid()<0){ //Lista
									 Ped ant = pen;
									 ant.setAutoid(db.getLastAutoidAnt()+1);
									 db.addPedidoAnt(ant);
								}else{

								}
							}else{
								if(e.getEid()>0){ //Establecimiento
									pen = new Ped(db.getLastAutoid()+1,sharedPrefs.getInt("nped",0)+1, checkcb.getAid(), e.getEid(),
											sdfdia.format(new Date()), Double.valueOf(artunidades.getText().toString()),
											Double.valueOf(artprecio.getText().toString()),
											artobservaciones.getText().toString(),0,"","",
											idclif,"S");
									SharedPreferences.Editor spe = sharedPrefs.edit();
									spe.putInt("nped",sharedPrefs.getInt("nped",0)+1).commit();
								}else //Lista
									pen = new Ped(db.getLastAutoid()+1,e.getEid(), checkcb.getAid(), e.getEid(),
											sdfdia.format(new Date()), Double.valueOf(artunidades.getText().toString()),
											Double.valueOf(artprecio.getText().toString()),
											artobservaciones.getText().toString(),0,"","",0,"S");
								    Ped ant = pen;
								    ant.setAutoid(db.getLastAutoidAnt()+1);
									db.addPedidoAnt(ant);
							} db.addPedido(pen); flagrefresh=1;
							if (flaganterior == 1) flagrefresh=1;
							Toast.makeText(getBaseContext(),
									"Artículo \""+artarticulo.getText().toString()+"\" creado.",
									Toast.LENGTH_LONG).show();
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
							artarticulo.setText(""); artunidades.setText("1"); artprecio.setText("0");
							artobservaciones.setText(""); artcbarras.setText(""); artarticulo.requestFocus(); } } }});
				mic.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
					try {startActivityForResult(intent, 6);
					artarticulo.setText("");} catch (ActivityNotFoundException a) {
						Toast.makeText(getApplicationContext(),
								"Dispositivo no soportado",Toast.LENGTH_SHORT).show();} }});
				micobs.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
					try {startActivityForResult(intent, 7);
					artobservaciones.setText("");} catch (ActivityNotFoundException a) {
						Toast.makeText(getApplicationContext(),
								getString(R.string.previousListDeviceNotSupported),Toast.LENGTH_SHORT).show();} }});
				scan.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						/*try {Intent intent = new Intent("com.google.zxing.client.android.SCAN");
							intent.setPackage("com.disoft.distarea");
							intent.putExtra("SCAN_MODE","QR_CODE_MODE,PRODUCT_MODE");
							startActivityForResult(intent, 0);
						} catch (Exception e) {e.printStackTrace();}*/
						try{
							IntentIntegrator integrator = new IntentIntegrator(ListaCompra.this);
							integrator.initiateScan();
						} catch (Exception e) {e.printStackTrace();}
					}});

						// Se reciben los datos (onActivityResult)
				//new parallelCount(list, web, loading).execute();
				//int contador = //db.getArticulosCount();
				//XXX CAMBIO PROVISIONAL: REVISO SI AHORA SÓLO PERMITE IR A LA LUPA DE
				//LA VENTANA EMERGENTE DEL BOTÓN + SI HAY PEDIDOS ANTERIORES.
				/*List<Ped> lista = new ArrayList<Ped>();
				for(Art a : db.getAllArticulos())
					try{
					lista.add(db.searchPedidoAnt(e.getEid(), "aid", String.valueOf(a.getAid())).get(0));
					}catch(Exception e){}
				int contador = lista.size();*/
				int contador = db.getArticulosPedidosAntCount(eidactual);
				//Toast.makeText(getBaseContext(), contador+" = "+db.getAllArticulos().size(), Toast.LENGTH_LONG).show();
				//contador -= db.getArticulosPedidosCount(db.getPedidoPendiente(e.getEid()).getPid());
				//asd.klm //XXX Revisar este potaje <---
				if(contador>0){
		    		list.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
		    				if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
		    				popupWindow.dismiss();
		    				Intent i = new Intent(ListaCompra.this, ListaArticulos.class);
		    				i.putExtra("establecimiento", mIcsSpinner.getSelectedItemPosition());
		    				i.putExtra("eid", eidactual); //i.putExtra("volver", 0);
		    				/*i.putExtra("borradoanterior", borradoanterior);*/ startActivity(i); }});//}
				}else{
					list.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
		    			if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
		    			Toast.makeText(getBaseContext(), getString(R.string.nohayant), Toast.LENGTH_LONG).show();
		    		}});

				}
		    	if(db.getEstablecimiento(eidactual).getTv()==null
						|| db.getEstablecimiento(eidactual).getTv().equals("")) web.setEnabled(false);
				else if(!isNetworkAvailable()) { web.setEnabled(false);
					web.getBackground().setColorFilter(0xFFFF0000,PorterDuff.Mode.SRC_IN);}
				else{ web.setEnabled(true);
				web.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
					popupWindow.dismiss();
					Intent intent = new Intent(ListaCompra.this,TiendaVirtual.class);
					intent.putExtra("establecimiento",mIcsSpinner.getSelectedItemPosition());
					intent.putExtra("eid", eidactual);
					startActivity(intent); finish(); }}); }

				can.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
						popupWindow.dismiss();
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
						if(/*borradoanterior==1 || */flagrefresh==1){ flagrefresh=0;
							Intent i = new Intent(ListaCompra.this, ListaCompra.class);
							//i.putExtra("establecimiento", mIcsSpinner.getSelectedItemPosition());
							i.putExtra("eid",eidactual);
							startActivity(i); finish();} }});
				//Popuptip
				if (sharedPrefs.getBoolean("tiplupa", false)==false){
				final View popupViewTip = layoutInflater.inflate(R.layout.popuptip, null);
				final PopupWindow popupWindowTip = new PopupWindow(popupViewTip,android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
				popupWindowTip.setBackgroundDrawable(getResources().getDrawable(
					android.R.drawable.alert_light_frame));
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
					{popupWindowTip.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 200);
					Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
					if(display.getRotation()==Surface.ROTATION_90)
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					else if(display.getRotation()==Surface.ROTATION_270){
						if(android.os.Build.VERSION.SDK_INT == 8)
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
				}else{ popupWindowTip.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 200);
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);}
				((TextView)popupViewTip.findViewById(R.id.tip)).setText(getString(R.string.tiplupa));
				((ImageView)popupViewTip.findViewById(R.id.cancel))
					.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						popupWindowTip.dismiss();
						if(((CheckBox)popupViewTip.findViewById(R.id.nvamem)).isChecked()){
							SharedPreferences.Editor spe = sharedPrefs.edit();
							spe.putBoolean("tiplupa",true); spe.commit(); } }});
				}}
	}

	public void contenidoVistaControl() {
		if(flagdoonce==0){ flagdoonce=1;
		final List<Integer> pedpend = new ArrayList<Integer>();
		final List<Integer> mensajes = new ArrayList<Integer>();
		for(Est e : db.getAllEstablecimientos())
			/*if(e.getEid()>0)*/ { est.add(e.getEid());
				if(db.getAllPedidos(e.getEid()).isEmpty()) continue;
				else{ int estado = db.getAllPedidos(e.getEid()).get(0).getEstado();
					if(estado>-3||estado<2) if(e.getFav()==true && e.getPrior()>=0 || e.getEid()<0)
						pedpend.add(e.getEid()); }}
		if (sharedPrefs.getString("vcdate", "").compareTo(sdfdia.format(new Date()))<0){
			//if(sharedPrefs.getString("tipo","P").equals("P")) new REparalelo().execute();
			new CMparalelo().execute();
			// We want the alarm to go off 15 seconds from now.
			/*long firstTime = SystemClock.elapsedRealtime();
			firstTime += 15*1000;
			long a= c.getTimeInMillis();*/

			SharedPreferences.Editor spe = sharedPrefs.edit();
			spe.putString("vcdate", sdfdia.format(new Date())); spe.commit(); }
		for(Msj m : db.getAllMensajesRec())
			if(!m.getTipomsj().equals("M") && !m.getEstado().equals("L")){
				mensajes.add(m.getMid());}

		//Cadenas
		int pendientes = pedpend.size();
		for(int e : pedpend){
			if(e==sharedPrefs.getInt("solicitacliest",0) &&
					db.getEstablecimiento(e).getConfigura()!=null &&
					db.getEstablecimiento(e).getConfigura().contains(",V,")){
				pendientes+=db.getPedidosClientes(e).size()-1;
				if(db.getPedidosClientes(e).size()>1) flagsendall=1;
			}
		}

		if(pendientes==1){
			((LinearLayout)findViewById(R.id.lineaped)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.infoped)).setText(getString(R.string.unpp));
		}else if (pendientes>1){
			((LinearLayout)findViewById(R.id.lineaped)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.infoped)).setText(getString(R.string.tiene)+" "+pendientes+
						" "+getString(R.string.xpp2));
		}else
			((LinearLayout)findViewById(R.id.lineaped)).setVisibility(View.GONE);

		/*
			if(pedpend.get(0)==sharedPrefs.getInt("solicitacliest",0) &&
					db.getEstablecimiento(pedpend.get(0)).getConfigura().contains(",V,")){
						final LinearLayout autoventa = new LinearLayout(getBaseContext());
						LinearLayout lineafondo = new LinearLayout(getBaseContext());
						TextView cabecera = new TextView(getBaseContext());
						ImageButton plus = new ImageButton(getBaseContext());
						lineafondo.setGravity(LinearLayout.HORIZONTAL);
						LinearLayout.LayoutParams lplf = new LinearLayout.LayoutParams(
								LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						cabecera.setText(db.getEstablecimiento(pedpend.get(0)).getNombre()+"\n");
						cabecera.setTextColor(Color.BLACK);
						plus.setImageDrawable(getResources().getDrawable(R.drawable.content_new));
						plus.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
						//plus.setAdjustViewBounds(true);
						plus.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
							if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
							e = (Est)autoventa.getTag();
							eidactual = e.getEid();
							new descargaClientes(ListaArticulos.class).execute();
						}});
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
								lp.weight = 1;
								//lp.gravity=Gravity.RIGHT;
						//plus.setLayoutParams(lp);
						lineafondo.addView(plus,lp);
						autoventa.addView(cabecera);
						autoventa.setOrientation(LinearLayout.VERTICAL);
						autoventa.setBackgroundDrawable(getResources().getDrawable(R.drawable.test3az));
						autoventa.setTag(db.getEstablecimiento(pedpend.get(0)));
						/*autoventa.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
							if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
							e = (Est)autoventa.getTag();
							eidactual = e.getEid();
							new descargaClientes(ListaArticulos.class).execute();
						}});*//*
						for(final int in : db.getPedidosClientes(pedpend.get(0))){
							TextView cliente = new TextView(getBaseContext());
							cliente.setPadding(20, 0, 0, 0);
							cliente.setBackgroundDrawable(getResources().getDrawable(R.drawable.test3v2));
							cliente.setTextColor(Color.BLACK);
							CliF cf = db.buscaClienteF(in);
							if(cf!=null) cliente.setText(cf.getNombre());
							else cliente.setText("???");
							final int sendeid = pedpend.get(0);
							cliente.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
								if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
								Intent intent = new Intent(ListaCompra.this, ListaCompra.class);
								intent.putExtra("eid",sendeid).putExtra("idclif",in);
								startActivity(intent); finish();
							}}); autoventa.addView(cliente);
						}
						autoventa.addView(lineafondo,lplf);
						((LinearLayout)findViewById(R.id.lineaped)).addView(autoventa);
			}else{

			TextView pedido = new TextView(getBaseContext());
			pedido.setBackgroundDrawable(getResources().getDrawable(R.drawable.test3az));
			pedido.setTextColor(Color.BLACK);
			pedido.setText(db.getEstablecimiento(pedpend.get(0)).getNombre());
			pedido.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
				if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
				for(int i=0; i<eids.length; i++)
					if(eids[i]==pedpend.get(0))
						mIcsSpinner.setSelection(i);
			}});
			((LinearLayout)findViewById(R.id.lineaped)).addView(pedido);
			}}
		else if (pendientes>1){
			((LinearLayout)findViewById(R.id.lineaped)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.infoped)).setText(getString(R.string.tiene)+" "+pendientes+
					" "+getString(R.string.xpp2));*/
			for(int i=0;i<pedpend.size();i++){
				//Si soy OP de Autoventa, detectar clientes
				if(pedpend.get(i)==sharedPrefs.getInt("solicitacliest",0) &&
						db.getEstablecimiento(pedpend.get(i)).getConfigura()!=null &&
						db.getEstablecimiento(pedpend.get(i)).getConfigura().contains(",V,")){
							final LinearLayout autoventa = new LinearLayout(getBaseContext());
							LinearLayout lineafondo = new LinearLayout(getBaseContext());
							TextView cabecera = new TextView(getBaseContext());
							ImageButton plus = new ImageButton(getBaseContext());
							ImageButton sendall = new ImageButton(getBaseContext());
							lineafondo.setGravity(LinearLayout.HORIZONTAL);
							LinearLayout.LayoutParams lplf = new LinearLayout.LayoutParams(
									LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
							cabecera.setText(db.getEstablecimiento(pedpend.get(i)).getNombre()+"\n");
							cabecera.setTextColor(Color.BLACK);
							plus.setImageDrawable(getResources().getDrawable(R.drawable.content_new));
							plus.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
							//plus.setAdjustViewBounds(true);
							plus.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
								if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
								e = (Est)autoventa.getTag();
								eidactual = e.getEid();
								new descargaClientes(ListaArticulos.class).execute();
							}});
							sendall.setImageDrawable(getResources().getDrawable(R.drawable.send_all));
							sendall.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
							sendall.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
								if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
								AlertDialog.Builder b = new AlertDialog.Builder(ListaCompra.this);
								b.setTitle("Enviar todos");
								b.setMessage("Con esta opción puede enviar todos los pedidos pendientes a sus Clientes." +
										" Automáticamente, se asumirán los datos adicionales por defecto de un pedido: " +
										"Recogida en local, copia al e-mail propio y sin firma. ¿Desea enviarlos todos?" +
										" No habrá confirmación adicional, ni posibilidad de editarlos luego.");
								b.setIcon(R.drawable.send_all);
								b.setPositiveButton("Enviar todos", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which){
										dialog.dismiss(); new enviarTodos().execute();
										}});
								b.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which){dialog.dismiss();}});
								AlertDialog ad = b.create(); ad.show();
								//Toast.makeText(getBaseContext(), "Enviar Todo", Toast.LENGTH_LONG).show();
							}});
							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
									LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
									lp.weight = 1;
							/*LinearLayout.LayoutParams lpd = new LinearLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							lpd.gravity=Gravity.RIGHT;*/
							//plus.setLayoutParams(lp);
							lineafondo.addView(plus,lp);
							if(flagsendall==1)
								lineafondo.addView(sendall,lp);
							autoventa.addView(cabecera);
							autoventa.setOrientation(LinearLayout.VERTICAL);
							autoventa.setBackgroundDrawable(getResources().getDrawable(R.drawable.test3az));
							autoventa.setTag(db.getEstablecimiento(pedpend.get(i)));
							ArrayList<Integer> pcs = (ArrayList<Integer>)db.getPedidosClientes(pedpend.get(i));
							Collections.sort(pcs,new Comparator<Integer>() {
								public int compare(Integer c1, Integer c2) {
									if(db.buscaClienteF(c1)!=null && db.buscaClienteF(c2)!=null)
										return db.buscaClienteF(c1).getNombre()
												.compareToIgnoreCase(db.buscaClienteF(c2).getNombre());
									else{
										if(db.buscaClienteF(c1)==null && db.buscaClienteF(c2)!=null)
											return 1;
										else if(db.buscaClienteF(c1)!=null && db.buscaClienteF(c2)==null)
											return -1;
										else return 0;
									}
							}});
							for(final int in : pcs){
								TextView cliente = new TextView(getBaseContext());
								cliente.setPadding(20, 0, 0, 0);
								cliente.setBackgroundDrawable(getResources().getDrawable(R.drawable.test3v2));
								cliente.setTextColor(Color.BLACK);
								CliF cf = db.buscaClienteF(in);
								if(cf!=null) cliente.setText(cf.getNombre());
								else cliente.setText("???");
								final int sendeid = pedpend.get(i);
								cliente.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
									if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
									Intent intent = new Intent(ListaCompra.this, ListaCompra.class);
									intent.putExtra("eid",sendeid).putExtra("idclif",in);
									startActivity(intent); finish();
								}}); autoventa.addView(cliente);
							}
							autoventa.addView(lineafondo,lplf);
							((LinearLayout)findViewById(R.id.lineaped)).addView(autoventa);
				}else{
					TextView pedido = new TextView(getBaseContext());
					pedido.setBackgroundDrawable(getResources().getDrawable(R.drawable.test3az));
					pedido.setTextColor(Color.BLACK);
					pedido.setText(db.getEstablecimiento(pedpend.get(i)).getNombre());
					final int repitei = i;
					pedido.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						for(int j=0; j<eids.length; j++)
							if(eids[j]==pedpend.get(repitei))
								mIcsSpinner.setSelection(j); }});
					((LinearLayout)findViewById(R.id.lineaped)).addView(pedido); }}//}

		if(est2.size()==1 || sharedPrefs.getInt("verest",0)<0){
			((LinearLayout)findViewById(R.id.lineaest)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.infoest)).setText(getString(R.string.unest));
			if(est2.size()==1){
			SharedPreferences.Editor spe = sharedPrefs.edit();
			spe.putInt("verest",(-1)*(est2.get(0)));
			spe.commit();}

			final Est e = db.getEstablecimiento((-1)*(sharedPrefs.getInt("verest",1)));
			((LinearLayout)findViewById(R.id.lineaest)).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						Intent i = new Intent(ListaCompra.this, Establecimiento.class);
						i.putExtra("eid",e.getEid());
						startActivity(i); finish(); }});

		} else if (est2.size()>1 || sharedPrefs.getInt("verest",0)>1){
			((LinearLayout)findViewById(R.id.lineaest)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.infoest)).setText(getString(R.string.tiene)+" "+(est2.size()-est.size())+
					" "+getString(R.string.xest2));
			if(est2.size()>1){
				SharedPreferences.Editor spe = sharedPrefs.edit();
				spe.putInt("verest",est2.size()); spe.commit(); }
			((LinearLayout)findViewById(R.id.lineaest)).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						Intent i = new Intent(ListaCompra.this, ListaEstablecimientos.class);
						startActivity(i); finish(); }});
		} else
			((LinearLayout)findViewById(R.id.lineaest)).setVisibility(View.GONE);

		if(mensajes.size()==1){
			((LinearLayout)findViewById(R.id.lineamsj)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.infomsj)).setText(getString(R.string.unmsj));
			((LinearLayout)findViewById(R.id.lineamsj)).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					Msj m = db.getMensajeRec(mensajes.get(0));
						/* Mensaje -> Chat
						Intent i = new Intent(ListaCompra.this, Mensaje.class);
						i.putExtra("tipo", 0).putExtra("mid", m.getMid())
							.putExtra("mensaje", m.getMensaje()).putExtra("http", m.getHttp())
							.putExtra("estado", m.getEstado()).putExtra("fecharealiz", m.getFecharealiz())
							.putExtra("horarealiz", m.getHorarealiz()).putExtra("fecharec", m.getFecharec())
							.putExtra("horarec", m.getHorarec()).putExtra("eid", m.getEid());*/
						Intent i = new Intent(ListaCompra.this, Chat.class);
						i.putExtra("eid", m.getEid());
						startActivity(i); finish(); }});
		}
		else if (mensajes.size()>1){
			((LinearLayout)findViewById(R.id.lineamsj)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.infomsj)).setText(getString(R.string.tiene)+" "+mensajes.size()+
					" "+getString(R.string.xmsj2));
			((LinearLayout)findViewById(R.id.lineamsj)).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						//Intent i = new Intent(ListaCompra.this, Mensajes.class);
						Intent i = new Intent(ListaCompra.this, Conversaciones.class);
						startActivity(i); finish(); }});
		}
		else
			((LinearLayout)findViewById(R.id.lineamsj)).setVisibility(View.GONE);
		if(((LinearLayout)findViewById(R.id.lineamsj)).getVisibility()==View.GONE &&
				((LinearLayout)findViewById(R.id.lineaest)).getVisibility()==View.GONE &&
				((LinearLayout)findViewById(R.id.lineaped)).getVisibility()==View.GONE){
			((TextView)findViewById(R.id.nhn)).setVisibility(View.VISIBLE);
		}else
			((TextView)findViewById(R.id.nhn)).setVisibility(View.GONE);
	}}

	public void ordenadores() {
		if (!mNameList[mIcsSpinner.getSelectedItemPosition()]
				.equals(getString(R.string.seleccionar)) && ver!=0) {
			final List<Ped> pedidos = db.getPedidosPendientes(eidactual);
			((ImageView) findViewById(R.id.sortarticulo)).setVisibility(View.INVISIBLE);
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				((ImageView) findViewById(R.id.sortprecio)).setVisibility(View.INVISIBLE);
				((ImageView) findViewById(R.id.sortimporte)).setVisibility(View.INVISIBLE); }
			((ImageView) findViewById(R.id.sortcheck)).setVisibility(View.INVISIBLE);
			orden = 0;
			((TextView) findViewById(R.id.txtarticulo)).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					ImageView flechaarticulo = (ImageView) findViewById(R.id.sortarticulo);
					Matrix matrix = new Matrix();
					switch (orden) {
					default:
						if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
							((ImageView) findViewById(R.id.sortprecio)).setVisibility(View.INVISIBLE);
							((ImageView) findViewById(R.id.sortimporte)).setVisibility(View.INVISIBLE);}
						((ImageView) findViewById(R.id.sortcheck)).setVisibility(View.INVISIBLE);
						flechaarticulo.setVisibility(View.VISIBLE);
						matrix.postRotate(0f, flechaarticulo.getDrawable().getBounds().width() / 2,
							flechaarticulo.getDrawable().getBounds().height() / 2);
						flechaarticulo.setImageMatrix(matrix);
						pedidos.clear();
						pedidos.addAll(db.getPedidosPendientes(eidactual));
						Collections.sort(pedidos,new Comparator<Ped>() {
								public int compare(Ped p1, Ped p2) {
									return db.getArticulo(p1.getAid()).getArticulo()
										.compareToIgnoreCase(db.getArticulo(p2.getAid()).getArticulo()); }});
						orden = 1; break;
					case 1:
						matrix.postRotate(180f, flechaarticulo
							.getDrawable().getBounds().width() / 2,
						flechaarticulo.getDrawable().getBounds().height() / 2);
						flechaarticulo.setImageMatrix(matrix);
						pedidos.clear();
						pedidos.addAll(db.getPedidosPendientes(eidactual));
						Collections.sort(pedidos, Collections.reverseOrder(new Comparator<Ped>() {
							public int compare(Ped p1, Ped p2) {return db.getArticulo(
								p1.getAid()).getArticulo().compareToIgnoreCase(
								db.getArticulo(p2.getAid()).getArticulo());}
						}));
						orden = 0; break;
					/*case 2:
						flechaarticulo.setVisibility(View.INVISIBLE);
						orden = 0; pedidos.clear();
						pedidos.addAll(db.getPedidosPendientes(eidactual));*/
					}
					while (tl.getChildAt(1) != null)
						tl.removeViewAt(1);mostrarPedido(pedidos); }});
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			((TextView) findViewById(R.id.txtprecio)).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					ImageView flechaprecio = (ImageView) findViewById(R.id.sortprecio);
					Matrix matrix = new Matrix();
					switch (orden) {
					default:
						((ImageView)findViewById(R.id.sortarticulo)).setVisibility(View.INVISIBLE);
						((ImageView)findViewById(R.id.sortimporte)).setVisibility(View.INVISIBLE);
						((ImageView) findViewById(R.id.sortcheck)).setVisibility(View.INVISIBLE);
						flechaprecio.setVisibility(View.VISIBLE);
						matrix.postRotate(0f, flechaprecio.getDrawable().getBounds().width() / 2,
						flechaprecio.getDrawable().getBounds().height() / 2);
						flechaprecio.setImageMatrix(matrix);
						pedidos.clear();
						pedidos.addAll(db.getPedidosPendientes(eidactual));
						Collections.sort(pedidos,new Comparator<Ped>() {
							public int compare(Ped p1, Ped p2) {
								return Double.compare(p1.getPrecio(),p2.getPrecio()); }});
						orden = 3; break;
					case 3:
						matrix.postRotate(180f, flechaprecio.getDrawable().getBounds().width() / 2,
						flechaprecio.getDrawable().getBounds().height() / 2);
						flechaprecio.setImageMatrix(matrix);
						pedidos.clear();
						pedidos.addAll(db.getPedidosPendientes(eidactual));
						Collections.sort(pedidos, Collections.reverseOrder(new Comparator<Ped>() {
							public int compare(Ped p1, Ped p2) {
								return Double.compare(p1.getPrecio(),p2.getPrecio()); }}));
						orden = 4; break;
					case 4:
						flechaprecio.setVisibility(View.INVISIBLE);
						orden = 0; pedidos.clear();
						pedidos.addAll(db.getPedidosPendientes(eidactual));
					}
					while (tl.getChildAt(1) != null)
						tl.removeViewAt(1); mostrarPedido(pedidos); }});
			((TextView) findViewById(R.id.txtimporte)).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
						ImageView flechaimporte = (ImageView) findViewById(R.id.sortimporte);
						Matrix matrix = new Matrix();
						switch (orden) {
						default:
							((ImageView) findViewById(R.id.sortarticulo)).setVisibility(View.INVISIBLE);
							((ImageView) findViewById(R.id.sortprecio)).setVisibility(View.INVISIBLE);
							((ImageView) findViewById(R.id.sortcheck)).setVisibility(View.INVISIBLE);
							flechaimporte.setVisibility(View.VISIBLE);
							matrix.postRotate(0f, flechaimporte.getDrawable().getBounds().width() / 2,
								flechaimporte.getDrawable().getBounds().height() / 2);
							flechaimporte.setImageMatrix(matrix);
							pedidos.clear();
							pedidos.addAll(db.getPedidosPendientes(eidactual));
							Collections.sort(pedidos,new Comparator<Ped>() {
								public int compare(Ped p1, Ped p2) {
									return Double.compare(p1.getPrecio()
										*p1.getCantidad(),p2.getPrecio()*p2.getCantidad()); }});
							orden = 5; break;
						case 5:
							matrix.postRotate(180f, flechaimporte.getDrawable().getBounds().width() / 2,
							flechaimporte.getDrawable().getBounds().height() / 2);
							flechaimporte.setImageMatrix(matrix);
							pedidos.clear();
							pedidos.addAll(db.getPedidosPendientes(eidactual));
							Collections.sort(pedidos, Collections.reverseOrder(new Comparator<Ped>() {
								public int compare(Ped p1, Ped p2) {return Double.compare(
									p1.getPrecio()*p1.getCantidad(),p2.getPrecio()*p2.getCantidad()); }}));
							orden = 6; break;
						case 6:
							flechaimporte.setVisibility(View.INVISIBLE);
							orden = 0; pedidos.clear();
							pedidos.addAll(db.getPedidosPendientes(eidactual));
						}
						while (tl.getChildAt(1) != null)
							tl.removeViewAt(1); mostrarPedido(pedidos); }});
			}
			((TextView) findViewById(R.id.txtcheck)).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
						ImageView flechacheck= (ImageView) findViewById(R.id.sortcheck);
						Matrix matrix = new Matrix();
						List<Ped> checks = new ArrayList<Ped>(), unchecks = new ArrayList<Ped>();
						switch (orden) {
						default:
							((ImageView) findViewById(R.id.sortarticulo)).setVisibility(View.INVISIBLE);
							if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
								((ImageView) findViewById(R.id.sortprecio)).setVisibility(View.INVISIBLE);
								((ImageView) findViewById(R.id.sortimporte)).setVisibility(View.INVISIBLE);}
							flechacheck.setVisibility(View.VISIBLE);
							matrix.postRotate(0f, flechacheck.getDrawable().getBounds().width() / 2,
									flechacheck.getDrawable().getBounds().height() / 2);
							flechacheck.setImageMatrix(matrix);
							checks.clear(); unchecks.clear();
							for(Ped p : db.getPedidosPendientes(eidactual)){
								if(p.getEstado()<0) checks.add(p); else unchecks.add(p);}
							pedidos.clear(); pedidos.addAll(checks); pedidos.addAll(unchecks);
							orden = 7; break;
						case 7:
							matrix.postRotate(180f, flechacheck.getDrawable().getBounds().width() / 2,
								flechacheck.getDrawable().getBounds().height() / 2);
							flechacheck.setImageMatrix(matrix);
							checks.clear(); unchecks.clear();
							for(Ped p : db.getPedidosPendientes(eidactual)){
								if(p.getEstado()<0) checks.add(p); else unchecks.add(p);}
							pedidos.clear(); pedidos.addAll(unchecks); pedidos.addAll(checks);
							orden = 8; break;
						case 8:
							flechacheck.setVisibility(View.INVISIBLE);
							orden = 0; pedidos.clear();
							pedidos.addAll(db.getPedidosPendientes(eidactual));
						}
						while (tl.getChildAt(1) != null)
							tl.removeViewAt(1); mostrarPedido(pedidos); }});
		}
	}

	public void errorConexion(Context context) {
		AlertDialog.Builder b = new AlertDialog.Builder(ListaCompra.this);
		b.setTitle("Error de conexión");
		b.setMessage("Para darse de alta, debe tener conexión a Internet.");
		b.setIcon(R.drawable.device_access_network_wifi_light);
		b.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which){finish();}});
		AlertDialog ad = b.create(); ad.show(); }

	private class envioPedido extends AsyncTask<String, Void, Boolean> {
		ProgressDialog loading; int noint = sharedPrefs.getInt("internetmode", 0);
		protected void onPreExecute() {
			loading = new ProgressDialog(ListaCompra.this);
			loading.setMessage("Enviando su pedido...");
			loading.setCancelable(false); loading.show(); }

		protected void onPostExecute(final Boolean success) {
			if (loading.isShowing()) {loading.dismiss();}
			List<Ped> listaPendientes = db.getPedidosPendientesDe(eidactual,idclif);
			for (Ped p : listaPendientes) {
				if (noint == 0 && /*flagpedidook==1 && success*/midbd!=0) p.setEstado(2);
				else p.setEstado(1);
				db.updatePedido(p); }
			if (listaPendientes.get(0).getEstado() > 1){
				for(Ped p : listaPendientes){ int flagupdate=0;
					if(db.getPedidoAnt(eidactual)!=null)
						for(Ped pe : db.getPedidoAnt(eidactual))
							if(p.getAid()==pe.getAid() && pe.getPid()!=0){ //Actualizar
								db.updatePedidoAnt(p); flagupdate=1; break;}
					if(flagupdate==0) db.addPedidoAnt(p);}
			}
			if (noint == 0 && /*flagpedidook==1 && success*/ midbd!=0){ //Si no hay problema de conexión y recibo ID mensaje en Postgre
				//db.deletePedido(db.getPedidoPendiente(eidactual,idclif));
				db.deletePedido(listaPendientes.get(0));
				e.setReferenciapedido(""); e.setFechacita(""); e.setHoracita(""); e.setObservaciones("");
				db.updateDatosEstablecimiento(e); }
				popupWindow.dismiss();
				LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
				popupView = layoutInflater.inflate(R.layout.popupsino, null);
				popupWindow = new PopupWindow(popupView,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
				popupWindow.setBackgroundDrawable(getResources()
					.getDrawable(android.R.drawable.alert_light_frame));
				popupWindow.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
				if (noint == 0) { if(midbd!=0){//if(flagpedidook==1)
						((TextView) popupView.findViewById(R.id.texto)).setText(R.string.popok);
				}else
						((TextView) popupView.findViewById(R.id.texto)).setText(R.string.popnook);
				((ImageButton) popupView.findViewById(R.id.si)).setVisibility(View.GONE);
			} else {
				((TextView) popupView.findViewById(R.id.texto)).setText(R.string.popinternetfail);
				ImageButton ajustes = (ImageButton) popupView.findViewById(R.id.si);
				ajustes.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				ajustes.setImageDrawable(getResources().getDrawable(R.drawable.action_settings));
				ajustes.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				}});
			}
				ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
				no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				no.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						popupWindow.dismiss();
						Intent intent = new Intent(ListaCompra.this,ListaCompra.class);
						startActivity(intent); finish(); }});
		}

		@Override protected Boolean doInBackground(String... params) {
				if (!isNetworkAvailable()){ noint = 1; return false;}
				else return Enviar();
			}
	}

	public Boolean Enviar(){
				String nombre = sharedPrefs.getString("seudonimo", "");
				if(sharedPrefs.getString("seudonimo","").equals(""))
					nombre = sharedPrefs.getString("nombre", "");
				Log.e("noooo", "antteeees");
				Automailer am = new Automailer(getString(R.string.userauto),getString(R.string.passauto));
				Est e = db.getEstablecimiento(eidactual);
				Log.e("siii", "despuuuuues");

				//XXX CUERPO
				//Test cambio fuente
				CliF cli =null;
				cli = db.buscaClienteF(idclif);
				String style = "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" " +
						"content=\"text/html; charset=utf-8\"></head><body>" +
						"<p style=\"font-family:Courier;\"> ",
					   linea = "---------------------------------------",
					   cuerpo = linea+"<br/>Pedido realizado por DISTAREA<br/>"+linea+"<br/>";
				if(e.getEid()==sharedPrefs.getInt("solicitacliest",0) && e.getConfigura().contains(",V,"))
					 cuerpo += "Establecimiento: "+e.getNombre()+"<br/>Referencia Interna DISTAREA: "+eidactual+
						"<br/>Cliente: "+db.buscaClienteF(idclif).getNombre()+"<br/>ID Cliente: "+idclif+
						" ["+db.buscaClienteF(idclif).getRef()+"]";
				else cuerpo += "Destinatario: "+e.getNombre()+"<br/>Referencia Interna DISTAREA: "+eidactual;
					cuerpo += "<br/>Numero de pedido:"+db.getPedidoPendiente(eidactual).getPid()+"<br/>Fecha y Hora: "+
					sdfdiashow.format(new Date()) + " a las "+sdfhora.format(new Date())+
					"<br/>"+linea+"<br/>DATOS DEL EMISOR<br/>"+linea+"<br/><br/>Referencia Distarea: "+
					sharedPrefs.getInt("id",0)+"<br/>Pseudonimo: "+nombre+"<br/>Direccion: "+
					sharedPrefs.getString("dir","*No definida*")+"<br/>E-mail: "+
					sharedPrefs.getString("mail","*No definido*")+"<br/>Telefono: "+
					sharedPrefs.getString("tlf","*No definido*");

					String xml ="<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n" +
					   		"<PEDIDO>\n" +
							"\t<VERSION>"+xmlversion+"</VERSION>\n"+
							"\t<APLICACION>DISTAREA</APLICACION>\n"+
							"\t<SESSION_ID></SESSION_ID>\n"+
					  		"\t<NUMERO>"+db.getPedidoPendiente(eidactual,idclif).getPid()+"</NUMERO>\n" +
							"\t<DOCUMENTO></DOCUMENTO>\n"+
							"\t<TIPODOCUMENTO>P</TIPODOCUMENTO>\n"+
							"\t<DOCUMENTOANTERIOR></DOCUMENTOANTERIOR>\n"+
							"\t<TIPODOCUMENTOANTERIOR></TIPODOCUMENTOANTERIOR>\n"+
							"\t<PEDIDO></PEDIDO>\n"+
							"\t<FECHAPEDIDO>"+sdfdiashow.format(new Date())+"</FECHAPEDIDO>\n"+
							"\t<RECTIFICATIVA></RECTIFICATIVA>\n"+
							"\t<DOMICILIAR></DOMICILIAR>\n"+
							"\t<VENDEDOR>"+sharedPrefs.getString("solicitacliref", "")+"</VENDEDOR>\n"+ //XXX REFERENCIA OPERADOR
							"\t<NOMBREVENDEDOR>"+sharedPrefs.getString("solicitaclinom", "")+"</NOMBREVENDEDOR>\n"+
							"\t<OPERADOR></OPERADOR>\n"+
							"\t<NOMBREOPERADOR></NOMBREOPERADOR>\n"+
					  		"\t<DESTINATARIO>"+e.getNombre()+"</DESTINATARIO>\n"+
					  		"\t<EID>"+eidactual+"</EID>\n";
					   if(sharedPrefs.getInt("solicitacliest", 0)==eidactual)
						   xml+="\t<OID>"+sharedPrefs.getInt("solicitaclin", 0)+"</OID>\n";
					   else xml+="\t<OID></OID>\n";
					   xml+="\t<FECHA>"+sdfdiashow.format(new Date())+"</FECHA>\n"+
					  		"\t<HORA>"+sdfhorass.format(new Date())+"</HORA>\n"+
					  		"\t<VENCIMIENTO></VENCIMIENTO>\n"+
					  		"\t<ALMACEN></ALMACEN>\n"+
					  		"\t<TRAMITE></TRAMITE>\n"+
					  		"\t<EMISOR>\n";
					  if(e.getEid()==sharedPrefs.getInt("solicitacliest",0) && e.getConfigura().contains(",V,")){
						xml+="\t\t<IDDISTAREA></IDDISTAREA>\n"+
							"\t\t<USUDISTAREA></USUDISTAREA>\n";
					  }else{ //Si es autoventa, el cliente puede tener su propio Distarea: no relleno el del OP
					  	xml+="\t\t<IDDISTAREA>"+sharedPrefs.getInt("id", 0)+"</IDDISTAREA>\n"+
					  		"\t\t<USUDISTAREA>"+sharedPrefs.getString("nombre", "")+"</USUDISTAREA>\n";
					  }
					   if(cli!=null){
					   xml+="\t\t<IDCLIENTE>"+cli.getRef()+"</IDCLIENTE>\n"+
					   		"\t\t<IDCLIENTESF>"+cli.getIdcf()+"</IDCLIENTESF>\n"+
					  		"\t\t<NOMBRECLIENTE>"+cli.getNombre()+"</NOMBRECLIENTE>\n"+
					  		"\t\t<NOMBRE></NOMBRE>\n"+
					  		"\t\t<APELLIDOS></APELLIDOS>\n"+
			   				"\t\t<DIRECCION>"+cli.getDireccion()+"</DIRECCION>\n"+
			   				"\t\t<CODIGOPOSTAL></CODIGOPOSTAL>\n"+
			   				"\t\t<CODPAIS>"+cli.getPais()+"</CODPAIS>\n"+
					  		"\t\t<CODPROVINCIA>"+cli.getProvincia()+"</CODPROVINCIA>\n"+
					  		"\t\t<CODISLA></CODISLA>\n"+
					  		"\t\t<CODMUNICIPIO>"+cli.getMunicipio()+"</CODMUNICIPIO>\n"+
					  		"\t\t<PAIS>"/*+db.buscarNombrePais(cli.getPais())*/+"</PAIS>\n"+
					  		"\t\t<PROVINCIA></PROVINCIA>\n"+
					  		"\t\t<ISLA></ISLA>\n"+
					  		"\t\t<MUNICIPIO></MUNICIPIO>\n"+
					  		"\t\t<EMAIL>"+cli.getMail()+"</EMAIL>\n";
					   		if(cli.getTel().length()>=9)
					   			xml+="\t\t<TELEFONO>"+cli.getTel().substring(0,9)+"</TELEFONO>\n";
					   		else
					   			xml+="\t\t<TELEFONO>"+cli.getTel()+"</TELEFONO>\n";
					   		if(cli.getMovil().length()>=9)
					   			xml+="\t\t<MOVIL>"+cli.getMovil().substring(0,9)+"</MOVIL>\n";
					   		else
					   			xml+="\t\t<TIPO>"+cli.getTipocliente()+"</TIPO>\n";
					   }else
					   xml+="\t\t<IDCLIENTE>"+e.getReferencia()+"</IDCLIENTE>\n"+
					   		"\t\t<IDCLIENTESF></IDCLIENTESF>\n"+
			   				"\t\t<NOMBRECLIENTE>"+sharedPrefs.getString("seudonimo", "")+"</NOMBRECLIENTE>\n"+
				  			"\t\t<NOMBRE></NOMBRE>\n"+
				  			"\t\t<APELLIDOS></APELLIDOS>\n"+
			   				"\t\t<DIRECCION>"+sharedPrefs.getString("dir", "")+"</DIRECCION>\n"+
			   				"\t\t<CODIGOPOSTAL>"+sharedPrefs.getString("cp", "")+"</CODIGOPOSTAL>\n"+
				   			"\t\t<CODPAIS>"+db.buscarCodigoPais(sharedPrefs.getString("pais", ""))+"</CODPAIS>\n"+
					  		"\t\t<CODPROVINCIA></CODPROVINCIA>\n"+
					  		"\t\t<CODISLA></CODISLA>\n"+
					  		"\t\t<CODMUNICIPIO></CODMUNICIPIO>\n"+
					  		"\t\t<PAIS>"+sharedPrefs.getString("pais", "")+"</PAIS>\n"+
					  		"\t\t<PROVINCIA></PROVINCIA>\n"+
					  		"\t\t<ISLA></ISLA>\n"+
					  		"\t\t<MUNICIPIO></MUNICIPIO>\n"+
					  		"\t\t<EMAIL>"+sharedPrefs.getString("mail", "")+"</EMAIL>\n";
					  		if(sharedPrefs.getString("tel", "").length()>=9)
					   xml+="\t\t<TELEFONO>"+sharedPrefs.getString("tel", "").substring(sharedPrefs.getString("tel", "").length()-9)+"</TELEFONO>\n";
					  		else
					   xml+="\t\t<TELEFONO>"+sharedPrefs.getString("tel", "")+"</TELEFONO>\n";
					   xml+="\t\t<MOVIL></MOVIL>\n"+
					  		"\t\t<TIPO></TIPO>\n";

					   xml+="\t\t<TARIFA></TARIFA>\n"+
							"\t\t<EXENTO></EXENTO>\n"+
							"\t\t<DTO></DTO>\n"+
							"\t\t<RAZONSOCIAL></RAZONSOCIAL>\n"+
					  		"\t\t<ALIASWEB></ALIASWEB>\n"+
					  		"\t\t<NOMBREEMPRESA>"+sharedPrefs.getString("nemp", "")+"</NOMBREEMPRESA>\n"+
					  		"\t\t<CODIGOSUCURSAL>"+sharedPrefs.getString("codsuc", "0")+"</CODIGOSUCURSAL>\n";

					   if(cli!=null && cli.getFechanac() != null && !cli.getFechanac().equals(""))
					   xml+="\t\t<DIANACIMIENTO>"+cli.getFechanac().split("-")[2]+"</DIANACIMIENTO>\n"+
					  		"\t\t<MESNACIMIENTO>"+cli.getFechanac().split("-")[1]+"</MESNACIMIENTO>\n"+
					  		"\t\t<ANIONACIMIENTO>"+cli.getFechanac().split("-")[0]+"</ANIONACIMIENTO>\n";
					   else
					   xml+="\t\t<DIANACIMIENTO></DIANACIMIENTO>\n"+
						  	"\t\t<MESNACIMIENTO></MESNACIMIENTO>\n"+
						  	"\t\t<ANIONACIMIENTO></ANIONACIMIENTO>\n";
					   xml+="\t</EMISOR>\n"+
					  		"\t<CESTO>\n";

				if(sharedPrefs.getString("tipo", "P").equals("E"))
					cuerpo+="<br/>Nombre de Empresa: "+sharedPrefs.getString("nemp","");
				if(!sharedPrefs.getString("codsuc", "0").equals("0"))
					cuerpo+="<br/>Referencia de la sucursal: "+sharedPrefs.getString("codsuc", "0")
					+"<br/><br/>"+linea+"<br/>SOLICITUD<br/>"+linea+"<br/>";
				else cuerpo+="<br/><br/>"+linea+"<br/>SOLICITUD<br/>"+linea+"<br/>";
				int pos=1; String dol="", coin="";
				for(Ped p : db.getPedidosPendientesDe(eidactual,idclif)){
					String codigo =""; int div=1;
					xml+="\t\t<NUEVO>\n"+
						 "\t\t\t<LINEA>"+pos+"</LINEA>\n"+
						 "\t\t\t<CODIGOFACDIS>";
					try{xml+=db.getArticuloEstablecimientoInterno(p.getAid()).getAfid();}
					catch(Exception ex){ex.printStackTrace();}
					String fid = "", sfid = "";
					if(db.getArticuloEstablecimiento(p.getAid(),eidactual)!=null)
						fid = db.getArticuloEstablecimiento(p.getAid(),eidactual).getFid();
					if(db.getArticuloEstablecimiento(p.getAid(),eidactual)!=null)
						sfid = db.getArticuloEstablecimiento(p.getAid(),eidactual).getSfid();
					xml+="</CODIGOFACDIS>\n" +
						 "\t\t\t<CODIGOBARRA>"+db.getArticulo(p.getAid()).getCbarras()+"</CODIGOBARRA>\n"+
						 "\t\t\t<FAMILIA>"+fid+"</FAMILIA>\n"+
						 "\t\t\t<SUBFAMILIA>"+sfid+"</SUBFAMILIA>\n"+
						 "\t\t\t<MODELO>";
						 //Depende de las Notas
						if(!p.getObservacion().trim().equals("") && p.getObservacion().contains("MODELO"))
						 xml+=p.getObservacion().trim().substring(p.getObservacion().trim().lastIndexOf(" "),p.getObservacion().indexOf("("));
						//¿Es posible que fuera lastIndexOf("MODELO:"), en p.getObservacion()?
					xml+="</MODELO>\n"+
						 "\t\t\t<LOTE></LOTE>\n"+
						 "\t\t\t<NOMBRE>"+db.getArticulo(p.getAid()).getArticulo()+"</NOMBRE>\n"+
						 "\t\t\t<UNIDADES>"+String.format("%.2f",p.getCantidad()).replace(".", ",")+"</UNIDADES>\n"+
						 "\t\t\t<PROMOCION></PROMOCION>\n"+
						 "\t\t\t<TIPOUNIDAD>"+db.getArticulo(p.getAid()).getTipo()+"</TIPOUNIDAD>\n"+
						 "\t\t\t<IMPORTE>"+String.format("%.2f",p.getPrecio()).replace(".", ",")+"</IMPORTE>\n"+
						 "\t\t\t<TOTAL>"+String.format("%.2f", p.getPrecio()*p.getCantidad())+"</TOTAL>\n"+
						 "\t\t\t<NOTAS>"+p.getObservacion()+"</NOTAS>\n"+
						 "\t\t\t<TIPOOFERTA></TIPOOFERTA>\n"+
						 "\t\t\t<CODIGOOFERTA></CODIGOOFERTA>\n"+
						 "\t\t\t<DOCUMENTOANTERIOR></DOCUMENTOANTERIOR>\n"+
						 "\t\t\t<TIPODOCUMENTOANTERIOR></TIPODOCUMENTOANTERIOR>\n"+
						 "\t\t\t<FECHA></FECHA>\n"+
						 "\t\t\t<PRECIOMANUAL></PRECIOMANUAL>\n"+ //XXX VER SI TE PONGO S O N
						 "\t\t</NUEVO>\n";
					//XXX AÑADIR TOTAL A SUBTOTAL AQUÍ.
					subtotal+=p.getPrecio()*p.getCantidad();
					if(db.getArticuloEstablecimientoInterno(p.getAid())==null){
						if(db.getArticulo(p.getAid()).getCbarras()!=null)
							if(!db.getArticulo(p.getAid()).getCbarras().equals(""))
								codigo ="Código de barras["+db.getArticulo(p.getAid()).getCbarras()+"]";}
					else codigo ="Código en FacDis["+db.getArticuloEstablecimientoInterno(p.getAid()).getAfid()+"]";
					if(db.getArticulo(p.getAid()).getTipo().equals("UN")){

						if(p.getCantidad()==1)
							cuerpo+="Nº"+pos+". "+codigo+", "+
								String.format("%.0f",p.getCantidad()).replace(".", ",")+" Unidad de "+
								//((Double)p.getCantidad()).shortValue()+" Unidad de "+
								db.getArticulo(p.getAid()).getArticulo();
						else
							cuerpo+="Nº"+pos+". "+codigo+", "+
									String.format("%.0f",p.getCantidad()).replace(".", ",")+" Unidades de "+
									db.getArticulo(p.getAid()).getArticulo();
					}else{
						String tipo = "";
						if(db.getArticulo(p.getAid()).getTipo().equals("MT")) tipo="Metro(s)";
						else if (db.getArticulo(p.getAid()).getTipo().equals("GR")){ tipo="Gramos"; div=1000;}
						else if (db.getArticulo(p.getAid()).getTipo().equals("KG")) tipo="Kilos/Litros";
						//else tipo="Unidad(es)";
						cuerpo+="Nº"+pos+". "+codigo+", "+String.format("%.2f",p.getCantidad()).replace(".", ",")
								+" "+tipo+" de "+db.getArticulo(p.getAid()).getArticulo();}

					if(p.getPrecio()==0) cuerpo+=".<br/>";
					else{
						if(sharedPrefs.getString("moneda","1").equals("2")) dol="$";
						else if(sharedPrefs.getString("moneda","1").equals("3")) coin="£";
						else if(sharedPrefs.getString("moneda","1").equals("4")) coin="¥";
						else coin="€";
						cuerpo+=" a "+dol+String.format("%.2f",p.getPrecio()).replace(".", ",")+coin+"c/u. Total "+dol+
								String.format("%.2f",Common.round(Common.round(p.getCantidad()/div,2)*p.getPrecio(),2))+coin+"<br/>";

					}
					if(p.getObservacion()==null || p.getObservacion().equals(""))//{
						cuerpo+="<br/>";
					else cuerpo+=p.getObservacion()+"<br/><br/>";
				pos++;
				}
				cuerpo+="SUBTOTAL= "+dol+String.format("%.2f",subtotal).replace(".", ",")+coin+"<br/>"+linea;
				xml+="\t</CESTO>\n" +
					 "\t<LISTACOMPRA></LISTACOMPRA>\n" +
					 "\t<TOTALCESTO>\n" +
					 "\t\t<SUBTOTAL>"+String.format("%.2f",subtotal).replace(".", ",")+"</SUBTOTAL>\n" +
					 "\t\t<DTOFINFACTURA></DTOFINFACTURA>\n";
				//IMPUESTOS (for)
				xml+="\t\t<IMPUESTOS>\n" +
					 "\t\t\t<IDIMPUESTO></IDIMPUESTO>\n" +
					 "\t\t\t<TIPO></TIPO>\n" +
					 "\t\t\t<BASE></BASE>\n" +
					 "\t\t\t<IMPORTE></IMPORTE>\n" +
					 "\t\t</IMPUESTOS>\n";
				xml+="\t\t<TOTALIMPUESTOS></TOTALIMPUESTOS>\n" +
					 "\t\t<TOTALGASTOS></TOTALGASTOS>\n" +
					 "\t\t<TOTAL>"+String.format("%.2f",subtotal).replace(".", ",")+"</TOTAL>\n" +
					 "\t\t<PUNTOSBONIFICACION></PUNTOSBONIFICACION>\n" +
					 "\t</TOTALCESTO>\n" +
					 "\t<FORMAPAGO>\n" +
					 "\t\t<ENTREGADO></ENTREGADO>\n" +
					 "\t\t<DEVOLUCION></DEVOLUCION>\n";
				//PAGO (for)
				xml+="\t\t<PAGO>\n" +
					 "\t\t\t<CUENTA></CUENTA>\n" +
					 "\t\t\t<IMPORTE></IMPORTE>\n" +
					 "\t\t</PAGO>\n";
				xml+="\t</FORMAPAGO>\n" +
					 "\t<ENTREGA>\n";
				if(envio.equals("Se recogerá personalmente.")) // RECOGER | DOMICILIO
					 xml+="\t\t<MODOENTREGA>RECOGER</MODOENTREGA>\n";
				else xml+="\t\t<MODOENTREGA>DOMICILIO</MODOENTREGA>\n";
				xml+="\t\t<DIRECCIONENTREGA>"+direccionpedido+"</DIRECCIONENTREGA>\n" +
					 "\t\t<CODIGOENVIO></CODIGOENVIO>\n" +
					 "\t</ENTREGA>\n" +
					 "\t<CONTACTOENTREGA>\n" +
					 "\t\t<CODIGO></CODIGO>\n" +
					 "\t\t<IDENTIFICADOR></IDENTIFICADOR>\n" +
					 "\t\t<NOMBRECONTACTO></NOMBRECONTACTO>\n" +
					 "\t\t<DIRECCION></DIRECCION>\n" +
					 "\t\t<CODIGOPOSTAL></CODIGOPOSTAL>\n" +
					 "\t\t<CODPAIS></CODPAIS>\n" +
					 "\t\t<CODPROVINCIA></CODPROVINCIA>\n" +
					 "\t\t<CODISLA></CODISLA>\n" +
					 "\t\t<CODMUNICIPIO></CODMUNICIPIO>\n"+
					 "\t\t<PAIS></PAIS>\n" +
					 "\t\t<PROVINCIA></PROVINCIA>\n" +
					 "\t\t<ISLA></ISLA>\n" +
					 "\t\t<MUNICIPIO></MUNICIPIO>\n" +
					 "\t\t<EMAIL></EMAIL>\n" +
					 "\t\t<TELEFONO></TELEFONO>\n" +
					 "\t\t<MOVIL></MOVIL>\n" +
					 "\t\t<NIF></NIF>\n" +
					 "\t</CONTACTOENTREGA>\n" +
					 "\t<OTROS>\n" +
					 "\t\t<ESTADO>P</ESTADO>\n" +
					 "\t\t<REFERENCIA>"+referenciapedido+"</REFERENCIA>\n" +
					 "\t\t<FECHACITA>"+fechapedido+"</FECHACITA>\n" +
					 "\t\t<HORACITA>"+horapedido+"</HORACITA>\n" +
					 "\t\t<OBSERVA>"+observacionespedido+"</OBSERVA>\n" +
					 "\t\t<FIRMA>";
				if(flagfirma==2){
					try{File firma = new File(getApplicationInfo().dataDir+"/firmas/"+
							sharedPrefs.getInt("nped",1)+".jpg");
						if(firma.exists()){
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inPreferredConfig=Config.ALPHA_8;//.RGB_565;
							options.outMimeType="image/jpeg";
							options.inSampleSize=4;
							Bitmap bm = BitmapFactory.decodeFile(firma.getPath(),options);
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
							xml+=Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
							}
						}catch(Exception ex){ex.printStackTrace();}
				}
				xml+="</FIRMA>\n" +
				 	 "\t</OTROS>\n" +
				 	 "</PEDIDO>";
				//Eliminar caracteres raros de la String xml
				xml = xml.replaceAll("ñ", "n").replaceAll("Ñ", "N").replaceAll("á", "a").replaceAll("Á", "A")
				.replaceAll("é", "e").replaceAll("É", "E").replaceAll("í", "i").replaceAll("Í", "I")
				.replaceAll("ó", "o").replaceAll("Ó", "O").replaceAll("ú", "u").replaceAll("Ú", "U");

				if(eidactual==sharedPrefs.getInt("solicitacliest", 0))
					cuerpo+="<br/>Operador: "+sharedPrefs.getString("solicitaclinom","")+
							"<br/>Código operador: "+sharedPrefs.getInt("solicitaclin",0);
				cuerpo+="<br/>Modo de entrega: "+envio+"<br/>";
				if(!envio.equals("Se recogerá personalmente."))
					cuerpo+="Dirección a enviar: "+direccionpedido+"<br/>";
				cuerpo+="Referencia: "+referenciapedido+"<br/>"+
						"Fecha de entrega/cita propuesta: "+fechapedido+"<br/>"+
						"Hora de entrega/cita propuesta: "+horapedido+"<br/>"+
						"Otras observaciones: "+observacionespedido+"<br/>";
				if(flagfirma==2) cuerpo+="Este pedido ha sido firmado.";
				else cuerpo+="Este pedido no ha sido firmado.";
				//csv+="[FIN]"+(contador-1);

				Log.e("siii", "llego aqui");
				am.setFrom(getString(R.string.userauto));
				am.setSubject("Pedido de "+nombre+" para "+e.getNombre());
				am.setBody(style+cuerpo+"</p></body></html>");
				am.setHTML();
				//ENVIAR MENSAJE INTERNO
		Log.e("siii", "yess");
				try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException ex){ex.printStackTrace();}
				try{DriverManager.setLoginTimeout(20);
					Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
					Statement st = conn.createStatement();
					Log.e("siii", "sooo");
					ResultSet rs = st.executeQuery("INSERT INTO mensajeapp (clienteglobal,mensaje,fecharealizacion,horarealizacion," +
						"idestablecimiento,tipomensaje,estado,idmensajeappmovil,remitente,xml,xmlestado) VALUES " +
						"("+sharedPrefs.getInt("id",0)+",'"+cuerpo.replace("<br/>", "\n")+"','"+
						new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date())+"','"+
						new SimpleDateFormat("HH:mm:ss",spanish).format(new Date())+"',"+
						eidactual+",'A','E',"+(db.getLastMidEnv()+1)+",'"+
						sharedPrefs.getString("nombre","")+"','"+xml+"','P') RETURNING id"); //Incluyo remitente
					if(rs.next()) midbd = rs.getInt(1);
					rs.close(); st.close(); conn.close();
				}catch (Exception ex){ex.printStackTrace();}
				//MI COPIA
				Msj mensaje = new Msj(db.getLastMidEnv()+1,sharedPrefs.getInt("id",0),
						cuerpo,"",new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),
						new SimpleDateFormat("HH:mm:ss",spanish).format(new Date()),"",
						eidactual,"A","","","","","E",
						sharedPrefs.getString("seudonimo",sharedPrefs.getString("nombre","")),midbd,xml);
				db.almacenarMensajeEnviado(mensaje);

		Log.e("siii", "ajjaaaa");
				//Mensaje al establecimiento
				if(e.getConfigura() != null && e.getConfigura().contains(",M,"))
				try{ if(e.getTv()!=null &&!e.getTv().equals("")){ //XXX Xiaomi error carpeta envío CSV
					//No envío fichero. Sólo adjunto XML como campo en la BBDD.
					/*String nombrexml = sharedPrefs.getInt("id",0)+","+sharedPrefs.getInt("nped",1)+","+
							eidactual+","+sdffichero.format(new Date())+".xml";
					OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
						openFileOutput(nombrexml, Context.MODE_PRIVATE),"windows-1252");
				  outputStreamWriter.write(xml); outputStreamWriter.close();
				  am.addAttachment(getApplicationInfo().dataDir+"/files/"+nombrexml);*/
					if(flagfirma==2){
						am.addAttachment(getApplicationInfo().dataDir+"/firmas/"+
							sharedPrefs.getInt("nped",1)+".jpg");} }
				am.setTo(new String[] {e.getMsjmail()});
	 			//if(am.send()) flagpedidook=1;
				am.send();
				} catch(Exception er){er.printStackTrace(); return false;}
				//else flagpedidook=1;

				//Mensaje copia cliente
				if(flagcopia==1)
				try{ if(e.getTv()!=null &&!e.getTv().equals("")){ //XXX Xiaomi error carpeta envío CSV
					//No envío fichero. Sólo adjunto XML como campo en la BBDD.
					/*String nombrexml = sharedPrefs.getInt("id",0)+","+sharedPrefs.getInt("nped",1)+","+
							eidactual+","+sdffichero.format(new Date())+".xml";
					OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
						openFileOutput(nombrexml, Context.MODE_PRIVATE),"windows-1252");
				  outputStreamWriter.write(xml); outputStreamWriter.close();
				  am.addAttachment(getApplicationInfo().dataDir+"/files/"+nombrexml);*/
					if(flagfirma==2){
						am.addAttachment(getApplicationInfo().dataDir+"/firmas/"+
							sharedPrefs.getInt("nped",1)+".jpg");} }

					Log.e("siii", "casi final");

				am.setTo(new String[] {sharedPrefs.getString("mail","")});

					Log.e("siii", "en medio");
				am.send();

					Log.e("siii", "no peta");
				} catch(Exception er){er.printStackTrace(); return false;}

				return true;
			}


	public class enviarTodos extends AsyncTask<String, Integer, Boolean> {
		ProgressDialog loading; int noint=sharedPrefs.getInt("internetmode", 0);
		ArrayList<Integer> pcs, midbds;

		protected void onPreExecute() {
			if(!isNetworkAvailable()) noint=1;
			eidactual=sharedPrefs.getInt("solicitacliest",0);
			e = db.getEstablecimiento(eidactual);
			loading = new ProgressDialog(ListaCompra.this);
			loading.setMessage("Enviando TODOS los pedidos\nEspere, por favor...");
			loading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			loading.setIndeterminate(false);
			loading.setMax(db.getPedidosClientes(eidactual).size());
			loading.setCancelable(false); loading.show();
			midbds = new ArrayList<Integer>();
			pcs = (ArrayList<Integer>)db.getPedidosClientes(eidactual);
			Collections.sort(pcs,new Comparator<Integer>() {
				public int compare(Integer c1, Integer c2) {
					return db.buscaClienteF(c1).getNombre()
						.compareToIgnoreCase(db.buscaClienteF(c2).getNombre()); }});
		}
		//XXX REVISAR PARA CONTROLAR TANTOS MIDBD COMO PEDIDOS SE HAGAN
		protected void onPostExecute(final Boolean success) {
			if (loading.isShowing()) {loading.dismiss();}
			for(final int in : pcs){
				idclif = in;
			List<Ped> listaPendientes = db.getPedidosPendientesDe(eidactual,idclif);
			for (Ped p : listaPendientes) {
				if (/*noint == 0 && /*flagpedidook==1 && success*/midbds.get(pcs.indexOf(in))!=0) p.setEstado(2);
				else p.setEstado(1);
				db.updatePedido(p); }
			if (listaPendientes.get(0).getEstado() > 1){
				for(Ped p : listaPendientes){ int flagupdate=0;
					if(db.getPedidoAnt(eidactual)!=null)
						for(Ped pe : db.getPedidoAnt(eidactual))
							if(p.getAid()==pe.getAid() && pe.getPid()!=0){ //Actualizar
								db.updatePedidoAnt(p); flagupdate=1; break;}
					if(flagupdate==0) db.addPedidoAnt(p);

				}
			}
			if (/*noint == 0 && /*flagpedidook==1 && success*/ midbds.get(pcs.indexOf(in))!=0){ //Si no hay problema de conexión y recibo ID mensaje en Postgre
				//db.deletePedido(db.getPedidoPendiente(eidactual,idclif));
				db.deletePedido(listaPendientes.get(0));
			}
		}
			//Después de todos los borrados, limpieza general del Establecimiento, y mensaje de salida.
				e.setReferenciapedido(""); e.setFechacita(""); e.setHoracita(""); e.setObservaciones("");
				db.updateDatosEstablecimiento(e);
				//popupWindow.dismiss();
				LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
				popupView = layoutInflater.inflate(R.layout.popupsino, null);
				popupWindow = new PopupWindow(popupView,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
				popupWindow.setBackgroundDrawable(getResources()
					.getDrawable(android.R.drawable.alert_light_frame));
				popupWindow.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
				if (noint == 0) { if(midbds.size()==pcs.size() && !midbds.contains(0)){//if(flagpedidook==1)
					((TextView) popupView.findViewById(R.id.texto)).setText("Se han enviado todos sus pedidos correctamente");
				}else
					((TextView) popupView.findViewById(R.id.texto)).setText(R.string.popnook);
				((ImageButton) popupView.findViewById(R.id.si)).setVisibility(View.GONE);
			} else {
				((TextView) popupView.findViewById(R.id.texto)).setText(R.string.popinternetfail);
				ImageButton ajustes = (ImageButton) popupView.findViewById(R.id.si);
				ajustes.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				ajustes.setImageDrawable(getResources().getDrawable(R.drawable.action_settings));
				ajustes.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				}});
			}
				ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
				no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				no.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						popupWindow.dismiss();
						Intent intent = new Intent(ListaCompra.this,ListaCompra.class);
						startActivity(intent); finish(); }});
		}

		protected void onProgressUpdate(Integer... progreso) {
	            loading.setProgress(progreso[0]);}

		@Override protected Boolean doInBackground(String... arg0) {
			for(final int in : pcs){
				idclif = in;
				if(Enviar()){
					//publishProgress((int) ((pcs.indexOf(in) / (float) db.getPedidosClientes(eidactual).size()) * 100));
					publishProgress(pcs.indexOf(in));
					midbds.add(midbd);
				} subtotal=0; midbd=0;
			}
			return true;
		}

	}

	private class CMparalelo extends AsyncTask<String, Void, Boolean> {
		ProgressDialog loading; int nuevos=0;
		protected void onPreExecute() {
			loading = new ProgressDialog(ListaCompra.this);
			loading.setMessage("Comprobando sus mensajes\nEspere, por favor...");
			loading.setCancelable(false); loading.show(); }

		protected void onPostExecute(final Boolean success) {
			if (loading.isShowing()) {loading.dismiss();}
			if(nuevos>0)
				try{PendingIntent.getService(ListaCompra.this, 0, new Intent(
						ListaCompra.this, NotificacionMensajes.class), 0).send();
				} catch (CanceledException e) {e.printStackTrace();}
			}

		@Override protected Boolean doInBackground(String... params) {
			SharedPreferences.Editor spe = sharedPrefs.edit();
			try {Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
			Connection conn; int flagest=0;
			try{DriverManager.setLoginTimeout(20);
			conn = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st = conn.createStatement();
			String sql="SELECT COUNT(*) FROM mensajeapp WHERE id>" +
				sharedPrefs.getInt("lastmid", 0) + " AND tipomensaje<>'A' AND " +
				"zonainfluencia LIKE '%" + sharedPrefs.getString("cp","") + "%' OR id>" +
				sharedPrefs.getInt("lastmid", 0) + " AND tipomensaje<>'A' " +
				"AND clienteglobal=" + sharedPrefs.getInt("id", -1);
			if(sharedPrefs.getInt("usuarioapp", 0)!=0){ flagest=1;
				sql+=" OR id>"+sharedPrefs.getInt("lastmid", 0) + " AND tipomensaje='A' " +
					"AND idestablecimiento=" + sharedPrefs.getInt("usuarioapp", 0) +
					" AND clienteglobal<>"+sharedPrefs.getInt("id", -1); }
			ResultSet rs = st.executeQuery(sql);
			if(rs.next()) nuevos = rs.getInt(1);
			rs.close(); st.close(); conn.close(); }catch (SQLException e){e.printStackTrace();}
			Log.e("NUEVOS CMPARALELO",""+nuevos);
			if(nuevos>0){
				try{DriverManager.setLoginTimeout(20);
				conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st = conn.createStatement();
				ResultSet rs;
				if(flagest==0)
					rs = st.executeQuery("SELECT id,clienteglobal,mensaje,http,fecharealizacion," +
							"horarealizacion,idestablecimiento,tipomensaje,numeroenvios,desdefecha," +
							"hastafecha,remitente,xml FROM mensajeapp WHERE id>"+sharedPrefs.getInt("lastmid", 0) +
							" AND tipomensaje<>'A' AND zonainfluencia LIKE '%" + sharedPrefs.getString("cp","") +
							"%' OR id>"+sharedPrefs.getInt("lastmid", 0) + " AND tipomensaje<>'A'" +
							" AND clienteglobal=" + sharedPrefs.getInt("id", -1) + " ORDER BY id ASC");
				else
					rs = st.executeQuery("SELECT id,clienteglobal,mensaje,http,fecharealizacion," +
							"horarealizacion,idestablecimiento,tipomensaje,numeroenvios,desdefecha," +
							"hastafecha,remitente,xml FROM mensajeapp WHERE id>"+sharedPrefs.getInt("lastmid", 0) +
							" AND tipomensaje<>'A' AND zonainfluencia LIKE '%" + sharedPrefs.getString("cp","") +
							"%' OR id>"+sharedPrefs.getInt("lastmid", 0) + " AND tipomensaje<>'A'" +
							" AND clienteglobal=" + sharedPrefs.getInt("id", -1) + " OR id>" +
							sharedPrefs.getInt("lastmid", 0)+" AND tipomensaje='A' AND idestablecimiento=" +
							sharedPrefs.getInt("usuarioapp", 0) +" ORDER BY id ASC");
				while(rs.next()){
					//Posibles nulls
					String http="", tipo="", desde="", hasta="", rmte="", xml="";
					if(rs.getString(4)!=null) http=rs.getString(4);
					if(rs.getString(8)!=null) tipo=rs.getString(8);
					if(rs.getString(10)!=null) desde=rs.getDate(10).toString();
					if(rs.getString(11)!=null) hasta=rs.getTime(11).toString();
					if(rs.getString(12)!=null) rmte=rs.getString(12);
					if(rs.getString(13)!=null) xml=rs.getString(13);
					//Guardo siempre el mid, para que vaya sobreescribiendo con el último
					if(rs.getInt(1)>sharedPrefs.getInt("lastmid",0))
						spe.putInt("lastmid", rs.getInt(1));
					if(tipo.equals("M")){
						if(rs.getInt(9)>0&&new Date().after(rs.getDate(10))&&rs.getDate(11).after(new Date())){
							try{DriverManager.setLoginTimeout(20);
								Connection conn2 = DriverManager.getConnection(getString(R.string.dirbbdd));
								Statement st2 = conn2.createStatement();
								st2.executeUpdate("UPDATE mensajeapp SET numeroenvios=" +
										(rs.getInt(9)-1)+"WHERE id="+rs.getInt(1));
								st2.close(); conn2.close();}catch (SQLException e){e.printStackTrace();}
							//No sé qué hacer: Dejar fecha y hora, o sustituirlo por aviso de mensaje Masivo?
							//http,"-Masivo-","","",
							Msj temporal = new Msj(db.getLastMidRec()+1,rs.getInt(2),rs.getString(3),
									http,rs.getDate(5).toString(),rs.getTime(6).toString(),"",
									rs.getInt(7),rs.getString(8),rs.getDate(10).toString(),rs.getTime(11).toString(),
									sdfdia.format(new Date()),sdfhora.format(new Date()),"R",rmte,rs.getInt(1),xml);
							db.recibirMensaje(temporal);
						}}else{
							String frec=sdfdia.format(new Date()),
									hrec=sdfhora.format(new Date());
							Msj temporal = new Msj(db.getLastMidRec()+1,rs.getInt(2),rs.getString(3),
									http,rs.getDate(5).toString(),rs.getTime(6).toString(),"",
									rs.getInt(7),tipo,desde,hasta,frec,hrec,"R",rmte,rs.getInt(1),xml);
							db.recibirMensaje(temporal);
							//Avisar recepción mensaje
							try {Statement st2 = conn.createStatement();
							st2.executeUpdate("UPDATE mensajeapp SET fecharecepcion='"+frec+
									"' , horarecepcion='"+hrec+"' WHERE id="+rs.getInt(1));
							//Antes cambiaba el estado a leído aquí.
							st2.close();}catch (SQLException e){e.printStackTrace();} }}
				spe.commit();
				try {Statement st2 = conn.createStatement();
				st2.executeUpdate("UPDATE clienteglobal SET ultimomensaje="+
				sharedPrefs.getInt("lastmid", 0)+" WHERE id="+sharedPrefs.getInt("id",-1));
				st2.close();}catch (SQLException e){e.printStackTrace();}
				rs.close(); st.close(); conn.close();
				}catch (SQLException e){e.printStackTrace();}
				if(flagest==1){ try {
					conn = DriverManager.getConnection(getString(R.string.dirbbdd));
					Statement st3 = conn.createStatement();
					ResultSet rs3 = st3.executeQuery("SELECT id,clienteglobal,mensaje,http,fecharealizacion," +
							"horarealizacion,idestablecimiento,tipomensaje,numeroenvios,desdefecha," +
							"hastafecha,remitente,xml FROM mensajeapp WHERE id>"+sharedPrefs.getInt("lastmid", 0) +
							" AND tipomensaje<>'A' AND zonainfluencia LIKE '%" + sharedPrefs.getString("cp","") +
							"%' OR id>"+sharedPrefs.getInt("lastmid", 0) + " AND tipomensaje<>'A'" +
							" AND clienteglobal=" + sharedPrefs.getInt("id", -1) + " ORDER BY id ASC");
					while(rs3.next()){
						//Posibles nulls
						String http="", tipo="", desde="", hasta="", rmte="", xml="";
						if(rs3.getString(4)!=null) http=rs3.getString(4);
						if(rs3.getString(8)!=null) tipo=rs3.getString(8);
						if(rs3.getString(10)!=null) desde=rs3.getDate(10).toString();
						if(rs3.getString(11)!=null) hasta=rs3.getTime(11).toString();
						if(rs3.getString(12)!=null) rmte=rs3.getString(12);
						if(rs3.getString(13)!=null) xml=rs3.getString(13);
						//Guardo siempre el mid, para que vaya sobreescribiendo con el último
						if(rs3.getInt(1)>sharedPrefs.getInt("lastmid",0))
							spe.putInt("lastmid", rs3.getInt(1));
						String frec=new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),
								hrec=new SimpleDateFormat("HH:mm:ss",spanish).format(new Date());
						Msj temporal = new Msj(db.getLastMidRec()+1,rs3.getInt(2),rs3.getString(3),
								http,rs3.getDate(5).toString(),rs3.getTime(6).toString(),"",
								rs3.getInt(7),tipo,desde,hasta,frec,hrec,"R","*"+rmte,rs3.getInt(1),xml);
						db.recibirMensaje(temporal);
						//Avisar recepción mensaje
						try {Statement st4 = conn.createStatement();
						st4.executeQuery("UPDATE mensajeapp SET fecharecepcion='"+frec+
								"' , horarecepcion='"+hrec+"' , estado='F' WHERE id="+rs3.getInt(1));
						st4.close(); conn.close();}catch (SQLException e){e.printStackTrace();} }
				}catch (SQLException e){e.printStackTrace();}
				spe.commit();
				try { conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st2 = conn.createStatement();
				st2.executeUpdate("UPDATE clienteglobal SET ultimomensaje="+
						sharedPrefs.getInt("lastmid", 0)+" WHERE id="+sharedPrefs.getInt("id",-1));
				st2.close();}catch (SQLException e){e.printStackTrace();}

				}}//Fin si nuevos>0
				// Comprobar estado de mensajes enviados
				if(!db.getAllMensajesEnv().isEmpty()){
					try{DriverManager.setLoginTimeout(20);
					Connection conn3 = DriverManager.getConnection(getString(R.string.dirbbdd));
					Statement st3 = conn3.createStatement();
					ResultSet rs2 = st3.executeQuery("SELECT idmensajeappmovil,estado FROM mensajeapp " +
							"WHERE clienteglobal="+sharedPrefs.getInt("id",-1)+" AND idmensajeappmovil>0 " +
							"AND tipomensaje='A' AND estado<>'E' AND estado<>null");
					while(rs2.next()) for(Msj m : db.getAllMensajesEnv())
						if(m.getMid() == rs2.getInt(1)) { if(!m.getEstado().equals(rs2.getString(2))){
							m.setEstado(rs2.getString(2)); db.actualizarMensajeEnviado(m);
						}continue;}
					rs2.close(); st3.close(); conn3.close();}catch (SQLException e){e.printStackTrace();}}
				return true; }}

	/*private class REparalelo extends AsyncTask<String, Void, Boolean> {
			ProgressDialog loading;
			protected void onPreExecute() {
	     	loading = new ProgressDialog(ListaCompra.this);
	     	loading.setMessage("Buscando establecimientos nuevos\nEspere, por favor...");
	     	loading.setCancelable(false); loading.show(); }

			protected void onPostExecute(final Boolean success) {
				if (loading.isShowing()) {loading.dismiss();}}
				@Override protected Boolean doInBackground(String... params) {
					try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
					try{DriverManager.setLoginTimeout(20);
					Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery("SELECT "+getString(R.string.camposest)+
						", textcat_all(cnae || ',') FROM app_company, categoriaempresa WHERE zonainfluencia LIKE '%"+
						sharedPrefs.getString("cp","")+"%' AND app_company.idcompanyapp=categoriaempresa.idcompanyapp " +
						"AND restringepedidos <> 'S' AND activo = true AND configura LIKE '%,MCS,%' GROUP BY "+getString(R.string.camposest));
					while(rs.next()){
						if(db.getEstablecimientosCount()==0)
							db.addEstablecimiento(new Est(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),
								rs.getString(5),rs.getString(6),"",rs.getBoolean(7),false,
								0,0.0f,rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),
								rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),0,
								rs.getString(16),rs.getString(17),"",rs.getString(18),rs.getString(19),
								rs.getString(20),rs.getString(21),"0",rs.getString(22),rs.getString(23)));

						else{
							int flag = 0;
							for(Est e : db.getAllEstablecimientos())
								if(e.getEid()==rs.getInt(1))
									flag=1;
							if(flag==0)
								db.addEstablecimiento(new Est(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),
									rs.getString(5),rs.getString(6),"",rs.getBoolean(7),false,
									0,0.0f,rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),
									rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),0,
									rs.getString(16),rs.getString(17),"",rs.getString(18),rs.getString(19),
									rs.getString(20),rs.getString(21),"0",rs.getString(22),rs.getString(23))); }}
					rs.close(); st.close(); conn.close(); } catch (SQLException e) {e.printStackTrace();}
					for(Est e : db.getAllEstablecimientos()) if(e.getEid()>0) est2.add(e.getEid());
					est2.removeAll(est);
//}
			return true;}}*/

	class loadSpinner extends AsyncTask<String, Void, Boolean> {
		View row; int position; Bitmap b;//Drawable d;
		public loadSpinner(View row, int position) {
			this.row = row; this.position = position;
		}

		protected void onPreExecute() {}

		protected void onPostExecute(final Boolean success) {
			if(success){
				ViewHolder vh = (ViewHolder)row.getTag();
				//vh.logo.setImageDrawable(d);
				vh.logo.setImageBitmap(b);
				vh.loading.setVisibility(View.GONE);
				vh.logo.setVisibility(View.VISIBLE);
			}
		}

		@Override protected Boolean doInBackground(String... params) {
			if(mNameList[position].equals(getString(R.string.seleccionar)))
				decodeSampledBitmap(R.drawable.action_about,"");
			else if(eids[position]<0)
				decodeSampledBitmap(R.drawable.collections_view_as_list,"");
			else if(eids[position]>0 && logos.get(position)!=null){
				try{File logo = new File( File.separator + "data" + File.separator + "data" +
		    		File.separator + ListaCompra.this.getPackageName() +
		    		File.separator + "logos" + File.separator + logos.get(position));
				if(logo.exists()) decodeSampledBitmap(0,logo.getPath());
				}catch(Exception e){e.printStackTrace();}
			}else decodeSampledBitmap(R.drawable.tienda,"");
			return true;}

	public void decodeSampledBitmap(int resId, String path) {

	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    if(resId==0) 	BitmapFactory.decodeFile(path, options);
	    else 			BitmapFactory.decodeResource(getResources(), resId, options);

	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > 100 || width > 100) {

	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;

	        while ((halfHeight / inSampleSize) > 100
	                && (halfWidth / inSampleSize) > 100) {
	            inSampleSize *= 2;
	        }
	    }
	    options.inJustDecodeBounds = false;
	    options.inSampleSize = inSampleSize;

	    if(resId==0) 	b = BitmapFactory.decodeFile(path, options);
	    else 			b = BitmapFactory.decodeResource(getResources(), resId, options);
	}}

	private class descargaClientes extends AsyncTask<String, Void, Boolean> {
      	ProgressDialog loading; int noint=sharedPrefs.getInt("internetmode",0);
      	ArrayList<CliF> clientes = new ArrayList<CliF>();
      	Class<?> destino; public descargaClientes(Class<?> destino) { this.destino=destino; }

      	protected void onPreExecute() {
      		if(!isNetworkAvailable()) noint=1;

         	loading = new ProgressDialog(ListaCompra.this);
         	loading.setMessage("Descargando sus clientes...");
         	loading.setCancelable(false); loading.show();
        }

        protected void onPostExecute(final Boolean success) {
        	if (loading.isShowing()) loading.dismiss();
        	for(CliF c : db.getAllClientesFAV(e.getEid()))
        		//clientes.add(c.getRef()+" "+c.getNombre());
        		if(c.getRef()!=null && !c.getRef().equals("") && !c.getRef().equals(" "))
        			clientes.add(c);
        	if(clientes.size()>0){
        	final TextView s = new TextView(getBaseContext()),
        		    	   i = new TextView(getBaseContext()),
        				   n = new TextView(getBaseContext());
        	final AlertDialog.Builder builder = new AlertDialog.Builder(ListaCompra.this);

		    builder.setTitle("Seleccione cliente");
		    if(noint==1) builder.setIcon(R.drawable.dc);
		    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    final View layout = inflater.inflate(R.layout.dclientes,null);
		    builder.setView(layout);
		    EditText buscar = (EditText)layout.findViewById(R.id.busqueda);
		    ImageButton refrescar = (ImageButton)layout.findViewById(R.id.refrescaClientes);
		    final ListView lv = (ListView) layout.findViewById(R.id.list);


		    final ArrayList<String> nomclientes = new ArrayList<String>();
		    for(CliF cl : clientes) nomclientes.add(cl.getRef()+" "+cl.getNombre());

		    lv.setAdapter(new ArrayAdapter<String>(ListaCompra.this,
		            android.R.layout.simple_list_item_single_choice,nomclientes));

		    lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		    lv.setOnItemClickListener(new OnItemClickListener(){
				@Override public void onItemClick(AdapterView<?> av, View v, int id, long l) {
					s.setText(clientes.get(id).getRef()+" "+clientes.get(id).getNombre());
	        		i.setText(""+id); // Posición el lista
	        		n.setText(""+clientes.get(id).getIdcf());
	        		Log.e("IDCLIF",""+n.getText().toString());
			}});
			buscar.addTextChangedListener(new TextWatcher() {
    			@Override public void afterTextChanged(Editable ed) {
    				if (ed.toString().length()>=3){
    					final ArrayList<CliF> busqueda = new ArrayList<CliF>();
    					for(CliF c : clientes){
    						String nombre = c.getRef()+" "+c.getNombre();
    						if(nombre.toLowerCase().contains(ed.toString().toLowerCase()))
    							busqueda.add(c);
    					}
    					if(busqueda.size()>=1){
    						ArrayList<String> nombusqueda = new ArrayList<String>();
    						for(CliF cl : busqueda) nombusqueda.add(cl.getRef()+" "+cl.getNombre());
    						lv.setAdapter(new ArrayAdapter<String>(ListaCompra.this,
    					            android.R.layout.simple_list_item_single_choice,nombusqueda));
    						lv.setOnItemClickListener(new OnItemClickListener(){
    							@Override public void onItemClick(AdapterView<?> av, View v, int id, long l) {
    					        		   s.setText(busqueda.get(id).getRef()+" "+busqueda.get(id).getNombre());
    					        		   i.setText(""+id);
    					        		   n.setText(""+busqueda.get(id).getIdcf());
    						}});
    					}
    				}else{
    					lv.setAdapter(new ArrayAdapter<String>(ListaCompra.this,
					            android.R.layout.simple_list_item_single_choice,nomclientes));
						lv.setOnItemClickListener(new OnItemClickListener(){
							@Override public void onItemClick(AdapterView<?> av, View v, int id, long l) {
 			        		   s.setText(clientes.get(id).getRef()+" "+clientes.get(id).getNombre());
 			        		   i.setText(""+id);
 			        		   n.setText(""+clientes.get(id).getIdcf());
 						}});
    				}
    			}
    				@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    				@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}});
		      builder.setPositiveButton("Seleccionar", new DialogInterface.OnClickListener() {
		               @Override public void onClick(DialogInterface dialog, int id) {
		            	   if(s.getText().toString().equals(""))
		            		   Toast.makeText(getBaseContext(),"Elija un cliente para continuar.",Toast.LENGTH_LONG).show();
		            	   else{
		            		   SharedPreferences.Editor spe = sharedPrefs.edit();
		            		   spe.putInt("ultimocliente", Integer.valueOf(i.getText().toString()));
		            		   spe.putInt("ultimovendedor", Integer.valueOf(n.getText().toString())).commit();
		            		   //ultimovendedor ES el valor a conservar
		            		   //ultimocliente es la posición para conservar en lista.
		            	   dialog.dismiss();
		            	   Intent i = new Intent(ListaCompra.this, destino);
		            	   i.putExtra("establecimiento", mIcsSpinner.getSelectedItemPosition());
		            	   if(destino.equals(TiendaVirtual.class)){
		            		   i.putExtra("funcion", "autoventa");
		            		   i.putExtra("referencia", s.getText().toString().split(" ")[0]);
		            	   }//else
		            	   i.putExtra("idclif", sharedPrefs.getInt("ultimovendedor",0));
		            	   i.putExtra("eid", eidactual); startActivity(i);}
		            }})
		           .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
		               @Override public void onClick(DialogInterface dialog, int id) {
		            	   dialog.dismiss();
		            }});
		    try{
		    s.setText(clientes.get(sharedPrefs.getInt("ultimocliente", 0)).getRef()+" "+
		    		clientes.get(sharedPrefs.getInt("ultimocliente", 0)).getNombre());
 		   	i.setText(""+sharedPrefs.getInt("ultimocliente", 0));
 		   	n.setText(""+sharedPrefs.getInt("ultimovendedor", 0));
		    }catch(Exception e){e.printStackTrace();}
		    final AlertDialog ad = builder.create();
		    ad.show();
		    refrescar.setOnClickListener(new OnClickListener() {
	               @Override public void onClick(View v) {
	            	   //Refrescar y actualizar ventana
	            	   ad.dismiss(); new descargaClientes(destino).execute();
	            }});
        	}
        	else
        		Toast.makeText(getBaseContext(), "El admin de su asesoría debe crear antes clientes en la web www.distarea.es",
        				Toast.LENGTH_LONG).show();
        }

        @Override protected Boolean doInBackground(String... arg0) {
        	if(noint==0){
        	if(db.getAllClientesFAV(e.getEid()).isEmpty()){
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
	    			 		/*
	    			 		" AND '"+sharedPrefs.getString("lastdateclientesf", "1970-01-01 00:00:00")+
    			 			"'::date <= fecha_ultima_revision" +
    			 			" AND '"+sharedPrefs.getString("lastdateclientesf", "1970-01-01 00:00:00")+
    			 			"'::time <= hora_ultima_revision "
    			 			*/" ORDER BY referencia ASC");
    			 while(rs.next()){
    				 if(rs.getString(18).equals("S")){
    				 CliF c = new CliF(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
    						 rs.getString(5), "", "", rs.getString(8), String.valueOf(rs.getInt(9)),
    						 "","","",rs.getString(13), rs.getInt(14), rs.getInt(15), "M", "");
    				 //activos.add(rs.getInt(1));
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

    			 //Borrado de clientes desaparecidos
    			 /*List<CliF> borrarinactivos = db.getAllClientesFAV(e.getEid());
    			 for(CliF c : borrarinactivos){
    				 int flag=0;
    				 for(int a : activos){
    					 if(a==c.getIdcf()){
    						 flag=1; break;}
    				 }
    				 if(flag==0){
    					 Log.e("BORRANDO",""+c.getNombre());
    					 db.deleteClienteF(c.getIdcf());
    				 }
    			 }*/

    		}catch (SQLException e){ e.printStackTrace(); }
        	}}/*else clientes = (ArrayList<CliF>) db.getAllClientesF(e.getEid());*/ return true;
        	}
    }

	@SuppressLint("NewApi")
	private class trataQR extends AsyncTask<String, Void, Boolean> {
		String link; int tipo=-1, segtipo, eid;
		public trataQR(String link) { this.link=link; }

		protected void onPostExecute(final Boolean success) {
        	if(success){
        		if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
        		//BeepManager bm = new BeepManager(ListaCompra.this);
//				IntentIntegrator ii = new IntentIntegrator(ListaCompra.this);
//				ii.initiateScan();
//				IntentIntegrator.initiateScan(ListaCompra.this);
        		switch(tipo){
        		case 0:
        			final AlertDialog.Builder adb = new AlertDialog.Builder(ListaCompra.this);
        			adb.setMessage("Identificado correctamente. Pulse en la pantalla del punto " +
	            			"de información en el QR. Tiene "+segtipo+" segundos para hacerlo.")
	            	   .setCancelable(true)
	            	   .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
						@Override public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss(); }});
        			final AlertDialog ad = adb.create();
        			CountDownTimer cdt = new CountDownTimer(segtipo*1000,1000){
        				 @Override public void onTick(long l) {
        			            ad.setMessage("Identificado correctamente. Pulse en la pantalla del punto " +
        	            			"de información en el QR. Tiene "+((int)Math.round(l/1000.0)-1)+
        	            			" segundos para hacerlo.");
        			        }
        			     @Override public void onFinish(){ ad.dismiss(); }
        			};
        			ad.show(); cdt.start();
        			break;
        		case 1:
        			//bm.playBeepSoundAndVibrate();
        			//En el ordenador irá a un link.
        			break;
        		case 2:
        			Intent i = new Intent(Intent.ACTION_VIEW);
        			i.setData(Uri.parse(link));
        			startActivity(i);
        			break;
        		case 3:
        			//bm.playBeepSoundAndVibrate();
        			//En el ordenador irá a un link. Añadir mi ID.
        			break;
        		case 4:
        			Intent in = new Intent(Intent.ACTION_VIEW);
        			in.setData(Uri.parse(link+"&id="+sharedPrefs.getInt("id",0)));
        			startActivity(in);
        			break;
        		case 5:
        			if(db.getEstablecimiento(eid)==null){
        				eidBE = eid; new BEparalelo().execute();
        			}else{
        				Intent inte = new Intent(ListaCompra.this, ListaCompra.class);
        				inte.putExtra("eid",eid);
        				startActivity(inte); finish();
        			}
        			break;
        		case 9:
        			//bm.playBeepSoundAndVibrate();
        			break;
        		}
        	}else
        		Toast.makeText(getBaseContext(),
        				"Ha habido un error en la comunicación con la base de datos. Inténtelo de nuevo, " +
        				"o póngase en contacto con nosotros.", Toast.LENGTH_LONG).show();
        	}

        @Override protected Boolean doInBackground(String... arg0) {
        	SimpleDateFormat sdfQR = new SimpleDateFormat("yyyyMMdd//HHmmss",spanish);
        	int resultado=0;
        	Date ahora = new Date();
      		try{ Class.forName("org.postgresql.Driver");
      		}catch(ClassNotFoundException e){ e.printStackTrace(); }
    		try{ DriverManager.setLoginTimeout(20);
	    		 Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
    			 Statement st = conn.createStatement();
    			 ResultSet rs = st.executeQuery("SELECT idestablecimiento, tipo, segundostipos " +
	    			 		"FROM publicidad WHERE link='"+link+"' AND activo='S'");
    			 if(rs.next()){
    				 eid=rs.getInt(1);
    				 tipo=rs.getInt(2);
    				 segtipo=rs.getInt(3);
    			 }else{rs.close(); st.close(); conn.close(); return false;}
    			 rs.close(); st.close();
    			 Statement st2 = conn.createStatement();
    			 resultado = st2.executeUpdate("INSERT INTO qr_temp (idestablecimiento, quien, referencia, fecha, hora) VALUES " +
	    			 		"("+eid+","+sharedPrefs.getInt("id", 0)+"," +
	    			 		"'"+tipo+"//"+segtipo+"//"+sdfQR.format(ahora)+"//"+link+"','" +
	    			 		sdfdia.format(ahora)+"','"+sdfhorass.format(ahora)+"')");
    			 Log.e("AHORA",""+sdfhorass.format(ahora));
    			 st2.close(); conn.close();
    		}catch (SQLException e){ e.printStackTrace(); }
    		if(resultado>0) return true;
    		else return false;
      	}
    }

	//S?lo se usa una vez, para a?adir un establecimiento por QR
	class BEparalelo extends AsyncTask<String, Void, Boolean> {
		Est temp=null; int flag=0;

			protected void onPreExecute() {
	     	loadingBE = new ProgressDialog(ListaCompra.this);
	     	loadingBE.setMessage(getString(R.string.beespere));
	     	loadingBE.setCancelable(false); loadingBE.show(); }

		protected void onPostExecute(boolean success) {
			if(flag==0) {
				db.addEstablecimiento(temp);
				new mensajeNuevoUsuario().execute();
			}
		}

			@Override protected Boolean doInBackground(String... params) {
	    try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
	    try{DriverManager.setLoginTimeout(20);
			Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT ac.* FROM " +
					"app_company AS ac INNER JOIN categoriaempresa AS ce ON " +
					"ac.idcompanyapp=ce.idcompanyapp WHERE ac.idcompanyapp="+eidBE+" AND activo = true");
			while(rs.next()){
				for(Est e : db.getAllEstablecimientos())
					if(e.getEid()==rs.getInt(1)) flag=1;
				if(flag==0){
					temp = new Est(rs.getInt(1),rs.getInt(18),rs.getString(23),rs.getString(9),
						rs.getString(8),rs.getString(6),"",rs.getBoolean(19),true,1,0.0f,rs.getString(10),
						rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),
						rs.getString(4),rs.getString(2),0,rs.getString(27),rs.getString(7),"",
						rs.getString(29),rs.getString(30),rs.getString(31),rs.getString(33),"0",rs.getString(35),"");
					//Busco CNAE
					try{Statement st2 = conn.createStatement();
					ResultSet rs2 = st2.executeQuery("SELECT textcat_all(cnae || ',') " +
							"FROM categoriaempresa WHERE idcompanyapp="+rs.getInt(1));
					if(rs2.next()){ temp.setCnae(rs2.getString(1));
					} rs2.close(); st2.close(); }catch (SQLException e) {e.printStackTrace();}
				}
			} rs.close(); st.close(); conn.close(); } catch (SQLException e) {e.printStackTrace();}
		return true; }}

	 public class mensajeNuevoUsuario extends AsyncTask<String, Void, Boolean> {
		  	protected void onPostExecute(final Boolean success) {new aumentaEst().execute();}
				@Override protected Boolean doInBackground(String... params) {
		  //Mensaje
			String yo = sharedPrefs.getString("seudonimo", sharedPrefs.getString("nombre",""));
			if(yo.equals("")) yo = sharedPrefs.getString("nombre","");
				String cuerpo = getString(R.string.nuevocliente1)+" "+yo+" ";
				if(sharedPrefs.getString("tipo", "P").equals("E")
						&& !sharedPrefs.getString("nemp","").equals(""))
						cuerpo+=getString(R.string.deempresa)+" "+sharedPrefs.getString("nemp","")+" ";
				cuerpo+=getString(R.string.nuevocliente2)+" ";
				if(!sharedPrefs.getString("dir", "").equals("")){
					cuerpo+=getString(R.string.condir)+" "+sharedPrefs.getString("dir", "");
					if(!sharedPrefs.getString("tel","").equals(""))
						cuerpo+=getString(R.string.ycontel)+" "+sharedPrefs.getString("tel", "")+".";
					else cuerpo+=".";}
			else{ if(!sharedPrefs.getString("tel","").equals(""))
					cuerpo+=getString(R.string.contel)+" "+sharedPrefs.getString("tel", "")+".";
				else cuerpo+=".";}
			try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException ex){ex.printStackTrace();}
			try{DriverManager.setLoginTimeout(20);
				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st = conn.createStatement();
				st.executeUpdate("INSERT INTO mensajeapp (clienteglobal,mensaje,fecharealizacion,horarealizacion," +
					"idestablecimiento,tipomensaje,estado,remitente) VALUES " +
					"("+sharedPrefs.getInt("id",0)+",'"+cuerpo+"','"+
					new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date())+"','"+
					new SimpleDateFormat("HH:mm:ss",spanish).format(new Date())+"',"+
					eidBE+",'A','E','Sistema')");
				st.close(); conn.close(); } catch (SQLException e) {e.printStackTrace();}
				return true;}
		  }

	 public class aumentaEst extends AsyncTask<String, Void, Boolean> {
		  	protected void onPostExecute(final Boolean success) { new descargaLogos().execute(); }
				@Override protected Boolean doInBackground(String... params) {
					//Aumentar contador de clientes de cada Establecimiento
					try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
			    try{DriverManager.setLoginTimeout(20);
					Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
					Statement st = conn.createStatement();
						st.executeUpdate("UPDATE app_company SET nclientes=nclientes+1 " +
							"WHERE idcompanyapp="+ eidBE);
					st.close(); conn.close(); } catch (SQLException e) {e.printStackTrace();}
				return true;
				}}

	 public class descargaLogos extends AsyncTask<String, Void, Boolean> {//Sólo descarga de nuevos

	    protected void onPostExecute(final Boolean success) {
	    	try{if (loadingBE.isShowing()) {loadingBE.dismiss();}}catch(Exception e){}
	    	sharedPrefs.edit().putString("main", ""+eidBE).commit();
	     	Intent i = new Intent(ListaCompra.this, ListaCompra.class);
				startActivity(i); finish();}

			@Override protected Boolean doInBackground(String... params) {
	  	URL url; URLConnection conn;
	    BufferedInputStream inStream; BufferedOutputStream outStream;
	    File outFile; FileOutputStream fileStream;
	    outFile = new File( File.separator + "data" + File.separator + "data" +
	    		File.separator + ListaCompra.this.getPackageName() +
	    		File.separator + "logos" + File.separator);

	    if(!outFile.exists()){ outFile.mkdir();
	    	try{new File (outFile+ File.separator+".nomedia").createNewFile();
	    	}catch (IOException e){e.printStackTrace();} }
	    for(Est es : db.getAllEstablecimientos()){
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
	      }catch(Exception e){} }} return true; }
	  }

}