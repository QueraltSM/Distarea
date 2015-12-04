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
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.SherlockListActivity;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuInflater;
//import com.actionbarsherlock.view.MenuItem;
//import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
//import com.actionbarsherlock.view.SubMenu;
//import com.actionbarsherlock.widget.SearchView;
import android.support.v7.app.ActionBar;
import com.disoft.distarea.extras.Automailer;
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.Est;

public class ListaEstablecimientos extends AppCompatActivity implements AdapterViewCompat.OnItemSelectedListener {
  SharedPreferences sharedPrefs; DatabaseHandler db; String codigoinv;
  View v, popupView; PopupWindow popupWindow; EditText email;
  ListView lv; CheckBox invita; String[] nestablecimiento; Menu m;
  List<Est> establecimientos = new ArrayList<Est>(); ActionBar ab;
  Integer[] favorito 	= {R.drawable.rating_not_important, R.drawable.rating_important};
  Integer[] tipo = {R.drawable.tienda, R.drawable.desc6}; int[] eids;
  Locale spanish = new Locale("es", "ES"); ProgressDialog loading;
  int margen = 10;//android.R.dimen.activity_horizontal_margin;
  SimpleDateFormat shf = new SimpleDateFormat("HH:mm:ss",spanish),
			 	   sdf = new SimpleDateFormat("yyyyMMdd",spanish);

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); ab = getSupportActionBar();
    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
    ab.setTitle(getString(R.string.establecimientos)); ab.setIcon(R.drawable.tienda);
    setContentView(R.layout.listaestablecimientos); v = findViewById(R.id.base);
    lv = (ListView)findViewById(android.R.id.list);
    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    db = new DatabaseHandler(this); establecimientos = db.getEstablecimientosVisibles();
    mostrarLista(establecimientos);
    if(getIntent().getIntExtra("vienedereg",0)==1 && 
    		sharedPrefs.getString("tipo", "P").equals("E"))
    	ventanaAniadir();
    if(sharedPrefs.getInt("verest", 0)!=0){
    	SharedPreferences.Editor spe = sharedPrefs.edit(); 
    	spe.putInt("verest", 0); spe.commit(); }
    ((TextView)findViewById(R.id.pie)).setSelected(true);}
    
  @Override public boolean onCreateOptionsMenu(Menu menu) {
  	MenuInflater inflater = getMenuInflater();
  	if(establecimientos.size()>0){
  	if(android.os.Build.VERSION.SDK_INT >= 14)
  		inflater.inflate(R.menu.listaestablecimientosv14, menu);
  	else inflater.inflate(R.menu.listaestablecimientos, menu);
    SearchView searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
      public boolean onQueryTextChange(String s) {
      	if(s.length()<3){ lv.removeAllViewsInLayout(); mostrarLista(establecimientos);
        }else{ lv.removeAllViewsInLayout();
         	mostrarLista(db.searchEstablecimiento(s.toString()));
        }return true;}
      @Override public boolean onQueryTextSubmit(String query) {return true;} };
    searchView.setOnQueryTextListener(queryTextListener); }
  	else/*{*/ if(android.os.Build.VERSION.SDK_INT >= 14)
  			inflater.inflate(R.menu.listaestablecimientossinv14, menu);
  		//else inflater.inflate(R.menu.listaestablecimientossin, menu); }
  	if(android.os.Build.VERSION.SDK_INT < 14) {
		SubMenu subMenu1 = menu.addSubMenu(Menu.NONE, Menu.NONE, 200, "");
		// XXX Contenido del submen�
		subMenu1.add(getString(R.string.invest)).setIcon(R.drawable.content_email)
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						if (!isNetworkAvailable()) {
							Toast.makeText(getApplicationContext(), getString(R.string.nointernet),
									Toast.LENGTH_LONG).show();
							return false;
						} else {
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
							popupView = layoutInflater.inflate(R.layout.popupsino, null);
							popupWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
							popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
							popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
							((TextView) popupView.findViewById(R.id.texto)).setText(R.string.textoinvitarest);
							LinearLayout lineamail = new LinearLayout(getBaseContext());
							email = (EditText) getLayoutInflater().inflate(R.layout.edittext, null);
							TextView txtemail = new TextView(getBaseContext());
							email.setEms(15);
							email.setInputType(InputType.TYPE_CLASS_TEXT |
									InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
							txtemail.setText(getString(R.string.mail));
							txtemail.setTextColor(Color.BLACK);
							lineamail.addView(txtemail);
							lineamail.addView(email);
							((LinearLayout) popupView.findViewById(R.id.cajatexto)).addView(lineamail);
							ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
							ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
							si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
							si.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (sharedPrefs.getBoolean("ch", true))
										v.performHapticFeedback(1);
									if (email.getText().toString().equals("") || !isEmailValid(email.getText().toString()))
										email.requestFocus();
									else {
										popupWindow.dismiss();
										if (sharedPrefs.getBoolean("ms", false) == true) {
											LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
											popupView = layoutInflater.inflate(R.layout.popupms, null);
											popupWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.MATCH_PARENT,
													android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
											popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
											popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
											((TextView) popupView.findViewById(R.id.texto)).setText
													((((TextView) popupView.findViewById(R.id.texto)).getText()) + " " + getString(R.string.motivo3));
											ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
											ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
											si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
											no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
											si.setOnClickListener(new OnClickListener() {
												@Override
												public void onClick(View v) {
													if (sharedPrefs.getBoolean("ch", true) == true)
														v.performHapticFeedback(1);
													if (((EditText) popupView.findViewById(R.id.mspass)).getText().toString()
															.equals(sharedPrefs.getString("pass", ""))) {
														new envioInvitacion().execute();
													} else
														((EditText) popupView.findViewById(R.id.mspass)).requestFocus();
												}
											});
											no.setOnClickListener(new OnClickListener() {
												@Override
												public void onClick(View v) {
													if (sharedPrefs.getBoolean("ch", true) == true)
														v.performHapticFeedback(1);
													popupWindow.dismiss();
													Toast.makeText(getApplicationContext(), getString(R.string.cancelainvitar) + " " + email.getText(), Toast.LENGTH_LONG).show();
												}
											});
										} else {
											new envioInvitacion().execute();
										}
										popupWindow.dismiss();
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
									Toast.makeText(getApplicationContext(), getString(R.string.cancelainvitar), Toast.LENGTH_LONG).show();
								}
							});
						}

						return false;
					}
				});

		subMenu1.add(getString(R.string.addest)).setIcon(R.drawable.social_add_group)
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						if (!isNetworkAvailable()) {
							Toast.makeText(getApplicationContext(),
									getString(R.string.nointernet), Toast.LENGTH_LONG).show();
							return false;
						} else {
							ventanaAniadir();
							return false;
						}
					}
				});

		subMenu1.add(getString(R.string.hidest)).setIcon(R.drawable.social_person_outline)
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						Intent i = new Intent(ListaEstablecimientos.this, ListaEst.class);
						i.putExtra("vienede", "ocultar");
						startActivity(i);
						finish();
						return false;
					}
				});

		subMenu1.add(getString(R.string.resest)).setIcon(R.drawable.social_person)
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						Intent i = new Intent(ListaEstablecimientos.this, ListaEst.class);
						i.putExtra("vienede", "restaurar");
						startActivity(i);
						finish();
						return false;
					}
				});
		MenuItem subMenu1Item = subMenu1.getItem();
		subMenu1Item.setIcon(R.drawable.menu_overflow_light);
		subMenu1Item.setTitle(R.string.titlemenuest);
//  		subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);}
	}
  	m = menu; return true;
    }
 
  @Override public boolean onOptionsItemSelected(MenuItem item) {
  	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
      if (item.getItemId() == android.R.id.home) {
        Intent intent = new Intent(this, ListaCompra.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent); finish(); return true;
      }else{ 
      	if(android.os.Build.VERSION.SDK_INT >= 14){
      		if(item.getItemId() == R.id.invest){
      			if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
      			if(!isNetworkAvailable()){
      				Toast.makeText(getApplicationContext(), getString(R.string.nointernet), 
      						Toast.LENGTH_LONG).show();
      				return false; } else {
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
      			popupView = layoutInflater.inflate(R.layout.popupsino, null);  
      			popupWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
      			popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
      			popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
      			((TextView) popupView.findViewById(R.id.texto)).setText(R.string.textoinvitarest);
      			LinearLayout lineamail = new LinearLayout(getBaseContext());
      			email = (EditText) getLayoutInflater().inflate(R.layout.edittext, null);
      			TextView txtemail = new TextView(getBaseContext()); email.setEms(15);
      			email.setInputType(InputType.TYPE_CLASS_TEXT |
      					InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
      			txtemail.setText(getString(R.string.mail)); txtemail.setTextColor(Color.BLACK);
      			lineamail.addView(txtemail); lineamail.addView(email);
      			((LinearLayout)popupView.findViewById(R.id.cajatexto)).addView(lineamail);
      			ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
      			ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
      			si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
      			si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
      			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
      			if(email.getText().toString().equals("") || !isEmailValid(email.getText().toString()))
      				email.requestFocus();
      			else{ popupWindow.dismiss();
      				if(sharedPrefs.getBoolean("ms",false)==true){
      					LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
      					popupView = layoutInflater.inflate(R.layout.popupms, null);  
      					popupWindow = new PopupWindow(popupView,android.view.ViewGroup.LayoutParams.MATCH_PARENT,
      					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
      					popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
      					popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
      					((TextView) popupView.findViewById(R.id.texto)).setText
      						((((TextView) popupView.findViewById(R.id.texto)).getText())+" "+getString(R.string.motivo3));
      					ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
      					ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
      					si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
      					no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
      					si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
      						if(sharedPrefs.getBoolean("ch",true)==true) v.performHapticFeedback(1);
      						if(((EditText)popupView.findViewById(R.id.mspass)).getText().toString()
      							.equals(sharedPrefs.getString("pass", ""))){
      								new envioInvitacion().execute();
      						}else ((EditText)popupView.findViewById(R.id.mspass)).requestFocus();
    						}});
    					no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
    						if(sharedPrefs.getBoolean("ch",true)==true) v.performHapticFeedback(1);
    						popupWindow.dismiss();
    						Toast.makeText(getApplicationContext(), getString(R.string.cancelainvitar)+" "+email.getText(),Toast.LENGTH_LONG).show();
    					}});
    				}else{
    					new envioInvitacion().execute();
    				}popupWindow.dismiss();
    			} 
    		}});
    		no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
    		no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
    			if(sharedPrefs.getBoolean("ch",true)==true) v.performHapticFeedback(1);
    			popupWindow.dismiss();
    			Toast.makeText(getApplicationContext(), getString(R.string.cancelainvitar), Toast.LENGTH_LONG).show();
    		}});}
      		}
      		/*else if(item.getItemId() == R.id.addest){
      			if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
    				if(!isNetworkAvailable()){ Toast.makeText(getApplicationContext(), 
      						getString(R.string.nointernet),Toast.LENGTH_LONG).show();
      			return true; } else { ventanaAniadir(); return true; }}*/
      		else if(item.getItemId() == R.id.hidest){
      		if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					Intent i = new Intent(ListaEstablecimientos.this, ListaEst.class);
					i.putExtra("vienede", "ocultar");
					startActivity(i); finish(); return false;}
      		else if(item.getItemId() == R.id.resest){
      			if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						Intent i = new Intent(ListaEstablecimientos.this, ListaEst.class);
						i.putExtra("vienede", "restaurar");
						startActivity(i); finish(); return false;}
      		}
      	
      	if(item.getItemId() == R.id.refrescarmenu) {
      	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
      	if(!isNetworkAvailable()){
  				Toast.makeText(getApplicationContext(), getString(R.string.nointernet), 
  						Toast.LENGTH_LONG).show();
  				return false; } else {
      	if(sharedPrefs.getString("tipo","P").equals("P"))
      		new RefrescaEstablecimientos().execute();
      	Intent i = new Intent(ListaEstablecimientos.this, ListaEstablecimientos.class);
      	startActivity(i); finish();
  			}}}return true;
  }
  
  @Override public boolean onKeyUp(int keyCode, KeyEvent event)  {
  	if(keyCode == KeyEvent.KEYCODE_MENU){
  		if(android.os.Build.VERSION.SDK_INT >= 14)
  			m.performIdentifierAction(R.id.overflow,0);
  		else{ SubMenu subMenu = m.getItem(m.size()-1).getSubMenu();
        m.performIdentifierAction(subMenu.getItem().getItemId(), 0);
  		}return true;} return super.onKeyUp(keyCode, event);}
  
  @Override public void onBackPressed() {
    super.onBackPressed(); Intent intent = new Intent(this, ListaCompra.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent); finish(); }
  
  //Comprobador de e-mail correcto
  public static boolean isEmailValid(String email) {
      boolean isValid = false;
      String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) { isValid = true; }
        return isValid;
    }

	@Override public void onItemSelected(AdapterViewCompat<?> parent, View view, int position, long id) {}
	@Override public void onNothingSelected(AdapterViewCompat<?> parent) {}

	static class ViewHolder{ TextView item; ImageView icon, icon2, icon3;
  		LinearLayout tramo1, tramo2; int pos;}

  public class Adaptador extends ArrayAdapter<String>{
    public Adaptador(Context context, int textViewResourceId, String[] objects){
    	super(context, textViewResourceId, objects);}
 
    @Override public View getView(int position, View row, ViewGroup parent){
        LayoutInflater inflater = ListaEstablecimientos.this.getLayoutInflater();
        final ViewHolder vh;
        if(row==null){ //Si no estoy errado, la relaci�n reside en la posici�n en la vista, y la posici�n en la lista
            vh = new ViewHolder();
            row = inflater.inflate(R.layout.lvlistaestablecimientos, parent, false);
            vh.item = (TextView)row.findViewById(R.id.nombreest);
            vh.icon = (ImageView)row.findViewById(R.id.favorito);
            vh.icon2 = (ImageView)row.findViewById(R.id.tipo);
            vh.icon3 = (ImageView)row.findViewById(R.id.irdirecto);
            vh.tramo1 = (LinearLayout)row.findViewById(R.id.tramo1);
            vh.tramo2 = (LinearLayout)row.findViewById(R.id.tramo2);
            vh.pos = position;
            row.setTag(vh); } else  vh = (ViewHolder) row.getTag();
        vh.item.setText(nestablecimiento[position]);
        
        if(nestablecimiento[position].equals("A�adir establecimiento")){
    	   //vh.icon.setVisibility(View.GONE);
        	vh.icon.setImageDrawable(getResources().getDrawable(R.drawable.content_new));
    	   vh.tramo2.setVisibility(View.INVISIBLE);
    	   ((FrameLayout)row.findViewById(R.id.icono)).setVisibility(View.GONE);
    	   vh.item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    	   vh.item.setGravity(Gravity.CENTER);
       }else{
    	   vh.icon.setVisibility(View.VISIBLE);
    	   vh.tramo2.setVisibility(View.VISIBLE);
    	   ((FrameLayout)row.findViewById(R.id.icono)).setVisibility(View.VISIBLE);
    	   vh.item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    	   vh.item.setGravity(Gravity.LEFT|Gravity.CENTER_HORIZONTAL);
        if(db.getEstablecimiento(eids[position]).getFav())
        	vh.icon.setImageResource(favorito[1]);
        else vh.icon.setImageResource(favorito[0]);
        //Logo
		    if(db.getEstablecimiento(eids[position]).getLogo()==null){
		    	if(eids[position]>0) vh.icon2.setImageResource(tipo[0]);
		    	else vh.icon2.setImageResource(tipo[1]); } //Nunca hay listas, pero por si acaso?
		    else { File f = new File( File.separator + "data" + File.separator + "data" + 
		    		File.separator + ListaEstablecimientos.this.getPackageName() + 
		    		File.separator + "logos" + File.separator + 
		        		db.getEstablecimiento(eids[position]).getLogo());
		        	Bitmap bitmap = decodeSampledBitmap(0,f.getAbsolutePath()); //BitmapFactory.decodeFile(f.getAbsolutePath());
		        	vh.icon2.setImageBitmap(bitmap); }
		
	    vh.tramo1.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v) {
				if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	      		Intent i = new Intent(ListaEstablecimientos.this, Establecimiento.class);
	      		i.putExtra("eid", eids[vh.pos]); startActivity(i);
		}});
	    
		vh.tramo2.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v) {
				if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			  	Intent intent = new Intent(ListaEstablecimientos.this, ListaCompra.class);
			  	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			  	intent.putExtra("eid", eids[vh.pos]); startActivity(intent);
		}});    
        
       }
		        return row;}
}
  
  public Bitmap decodeSampledBitmap(int resId, String path) {

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
	    Bitmap bitmap;
	    if(resId==0) 	bitmap = BitmapFactory.decodeFile(path, options);
	    else 			bitmap = BitmapFactory.decodeResource(getResources(), resId, options);
	    
	    return bitmap;
	    /*int mayor=0;
	    if(bitmap.getWidth()>bitmap.getHeight()) mayor=bitmap.getWidth()/2;
	    else mayor=bitmap.getHeight()/2;
	    
	    return redondear(bitmap, mayor);*/
	    /*int esquina = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, 
	    		getResources().getDisplayMetrics())/2;*/
	    //int esquina = (int) (48 * Resources.getSystem().getDisplayMetrics().density);
	    //return redondear(bitmap,300);
	}

  public void mostrarLista(final List<Est> establecimientos){
  	if(establecimientos.size()>0){
  		if(((ViewGroup)v).getChildCount()==2)
  			((ViewGroup)v).removeView(((ViewGroup)v).findViewWithTag("limpio"));
  	//nestablecimiento = new String[establecimientos.size()];
  	nestablecimiento = new String[establecimientos.size()+1];
  	eids = new int[establecimientos.size()+1];
      //Ordenaci�n por nombre
  	if(sharedPrefs.getInt("verest",0)>0){
  		SharedPreferences.Editor spe = sharedPrefs.edit();
			spe.putInt("verest",0); spe.commit();}
  	List<Est> fav = new ArrayList<Est>(), nofav = new ArrayList<Est>();
  	for(Est e : establecimientos){
  		if(e.getFav()) fav.add(e); else nofav.add(e);}
      Collections.sort(fav, new Comparator<Est>() { public int compare(Est e1, Est e2) {
        return e1.getNombre().compareToIgnoreCase(e2.getNombre()); }});
      Collections.sort(nofav, new Comparator<Est>() { public int compare(Est e1, Est e2) {
      	return e1.getNombre().compareToIgnoreCase(e2.getNombre()); }});
      establecimientos.clear(); establecimientos.addAll(fav);
      establecimientos.addAll(nofav); int i = 0;
      for(Est es : establecimientos){ 
      	nestablecimiento[i]=es.getNombre(); eids[i]=es.getEid(); i++; }
      nestablecimiento[i]="A�adir establecimiento"; eids[i]=0;
      ArrayAdapter<String> adapter = new Adaptador(this,R.layout.lvlistaestablecimientos, nestablecimiento);
//      setListAdapter(adapter); ListView list = getListView();
		lv.setAdapter(adapter); ListView list = lv;
      list.setOnItemClickListener(new OnItemClickListener() {
      	@Override public void onItemClick(AdapterView<?> av, View v, int position, long l){
      		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
      		if(position==establecimientos.size()) ventanaAniadir();
      		}});
      
  	}else{ TextView limpio; 
  		if(((ViewGroup)v).getChildCount()==1){
  			limpio = new TextView(getBaseContext()); limpio.setTag("limpio");
  			limpio.setTextAppearance(getBaseContext(),android.R.style.TextAppearance_Small);
  			limpio.setTextColor(getResources().getColor(android.R.color.black));
  			limpio.setPadding(margen, 0, margen, 0);((ViewGroup) v).addView(limpio);}
  		else  limpio = (TextView)((ViewGroup)v).findViewWithTag("limpio");
  		if(db.getEstablecimientosVisibles().size()>0)
  			limpio.setText(R.string.busnores);
  		else{ if(sharedPrefs.getString("tipo","P").equals("P"))
  				limpio.setText(getString(R.string.limpio));
  			else limpio.setText(getString(R.string.limpioempresa));
  		//Negritas (165-171), (230-240), (393-400)
			//Imagen ... (175-176),  (404-405)
  		Bitmap bm = BitmapFactory.decodeResource( getResources(), R.drawable.menu_overflow);
  		SpannableStringBuilder ssb = new SpannableStringBuilder(limpio.getText()+"  ");
			ssb.setSpan(new ImageSpan(bm),169,170,Spannable.SPAN_INCLUSIVE_INCLUSIVE); 
			ssb.setSpan(new ImageSpan(bm),394,395,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			StyleSpan bs1 = new StyleSpan(Typeface.BOLD),
								bs2 = new StyleSpan(Typeface.BOLD),
								bs3 = new StyleSpan(Typeface.BOLD);
			ssb.setSpan(bs1, 160, 166, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			ssb.setSpan(bs2, 222, 232, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			ssb.setSpan(bs3, 383, 390, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			limpio.setText(ssb, BufferType.SPANNABLE);
  		}
  		nestablecimiento=new String[]{"A�adir establecimiento"}; 
  		eids=new int[]{0};
  		ArrayAdapter<String> adapter = new Adaptador(this,R.layout.lvlistaestablecimientos, nestablecimiento);
		lv.setAdapter(adapter);
//        setListAdapter(adapter);
//		ListView list = getListView();
		ListView list = lv;
        list.setOnItemClickListener(new OnItemClickListener() {
          	@Override public void onItemClick(AdapterView<?> av, View v, int position, long l){
          		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
          		if(position==establecimientos.size()) ventanaAniadir(); }});
  		}
  	}

  private class envioInvitacion extends AsyncTask<String, Void, Boolean> {
		ProgressDialog loading;
	    String emails;
		protected void onPreExecute() {
		emails = email.getText().toString();
     	loading = new ProgressDialog(ListaEstablecimientos.this);
     	loading.setMessage(getString(R.string.envinv));
     	loading.setCancelable(false); loading.show(); }

    protected void onPostExecute(final Boolean success) {
     	if (loading.isShowing()) {loading.dismiss();}
     	if(success) Toast.makeText(ListaEstablecimientos.this, 
     		getString(R.string.invitado)+" "+email.getText(), Toast.LENGTH_LONG).show();
     	else Toast.makeText(ListaEstablecimientos.this, 
     		getString(R.string.noinvitado)+" "+email.getText(), Toast.LENGTH_LONG).show();
     	}
    
		@Override protected Boolean doInBackground(String... params) {
			Automailer am = new Automailer(getString(R.string.userauto),getString(R.string.passauto));
			String[] para = {emails}; am.setTo(para);
			am.setFrom(getString(R.string.userauto));
			am.setSubject(sharedPrefs.getString("seudonimo",sharedPrefs.getString("nombre",""))+
				" "+getString(R.string.asuntoauto));
			am.setBody(getString(R.string.cuerpoauto)+
				sharedPrefs.getString("seudonimo",sharedPrefs.getString("nombre",""))+" "+
				"\nDirecci�n: "+sharedPrefs.getString("dir","*Direcci�n no especificada*")+
				"\nC�digo Postal: "+sharedPrefs.getString("cp","")+"\nE-mail: "+
				sharedPrefs.getString("mail","*E-mail no especificado*"));
			try { 
				//Si hiciera falta adjuntar alg�n archivo,
				//ya sea una imagen, o el .apk, se har�a aqu�:
				//m.addAttachment("/sdcard/filelocation"); 
				if(am.send()) return true; else return false; 
				} catch(Exception e){e.printStackTrace();} 
			return false;
		}}
  
  public boolean isNetworkAvailable() {
	 ConnectivityManager connectivityManager = (ConnectivityManager) 
			 getSystemService(Context.CONNECTIVITY_SERVICE);
	 NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	 if(sharedPrefs.getInt("internetmode",0)==0)
	 return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	 else return false;}
  
  public void ventanaAniadir(){
  	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		{Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		if(display.getRotation()==Surface.ROTATION_90)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		else if(display.getRotation()==Surface.ROTATION_270){ 
			if(android.os.Build.VERSION.SDK_INT == 8)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
		}else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
			AlertDialog.Builder adb = new AlertDialog.Builder(ListaEstablecimientos.this);
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View layout = inflater.inflate(R.layout.daddest,null);
			adb	.setTitle("A�adir Establecimiento").setIcon(R.drawable.social_add_group).setView(layout)
				.setCancelable(false).setMessage(getString(R.string.textobuscar)+" "+getString(R.string.tbemp));
			final EditText buscar = (EditText) layout.findViewById(R.id.buscar),
					 	   codinv = (EditText) layout.findViewById(R.id.codinv);
			
			adb.setPositiveButton("Buscar", new DialogInterface.OnClickListener(){
				@Override public void onClick(DialogInterface dialog, int which) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
						if(buscar.getText().toString().equals("") && 
								codinv.getText().toString().equals(""))
							codinv.requestFocus();
						else if(!buscar.getText().toString().equals("") && 
								!codinv.getText().toString().equals(""))
							Toast.makeText(getBaseContext(), getString(R.string.err2lineas),
									Toast.LENGTH_LONG).show();
						else { dialog.dismiss();
							if(!buscar.getText().toString().equals("")) {
								Intent i = new Intent(ListaEstablecimientos.this, ListaEst.class);
								i.putExtra("vienede", "a�adir"); i.putExtra("query", buscar.getText().toString().trim());
								startActivity(i); finish();
							}else { codigoinv = codinv.getText().toString().trim();
								new BuscaCodigoInvitacion().execute();}}
				}});
			
			/*
			 //INTENTO QUE ENTER SEA IGUAL QUE PULSAR SEGUIR -> IDENTIFICAR BOTONES DIALOG (�FUERA DE ADB?)
			 buscar.setOnEditorActionListener(new OnEditorActionListener() {
		        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
		                
		            }    
		            return false;
		        }
		    });*/
			
			adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
				@Override public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}});
			AlertDialog alert = adb.create(); alert.show();
			final Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
			buscar.setOnKeyListener(new View.OnKeyListener(){
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
			codinv.setOnKeyListener(new View.OnKeyListener(){
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
			
  }
  
  public static int charToNum(String s){ 
		if(s.charAt(0)=='J') return 0; 
		return ((int)s.charAt(0)-64);
}
  
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
    
  public class RefrescaEstablecimientos extends AsyncTask<String, Void, Boolean> {
  	private ProgressDialog loading;
        
    protected void onPreExecute() {
    	loading = new ProgressDialog(ListaEstablecimientos.this);
    	loading.setMessage(getString(R.string.busestnue));
     	loading.setCancelable(false); loading.show();}

    protected void onPostExecute(final Boolean success) {
     	if(loading.isShowing()){try{loading.dismiss();}catch(Exception e){}}
     	new descargaLogos().execute();}

		@Override protected Boolean doInBackground(String... arg0) {
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
								rs.getString(5),rs.getString(6),"",rs.getBoolean(7),true,
								0,0.0f,rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),
								rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),0,
								rs.getString(16),rs.getString(17),"",rs.getString(18),rs.getString(19),
								rs.getString(20),rs.getString(21),"0",rs.getString(22),rs.getString(23)));
					else{ int flag = 0;
						for(Est e : db.getAllEstablecimientos())
							if(e.getEid()==rs.getInt(1)) flag=1;
						if(flag==0){
							db.addEstablecimiento(new Est(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),
									rs.getString(5),rs.getString(6),"",rs.getBoolean(7),true,
									0,0.0f,rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),
									rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),0,
									rs.getString(16),rs.getString(17),"",rs.getString(18),rs.getString(19),
									rs.getString(20),rs.getString(21),"0",rs.getString(22),rs.getString(23))); }}}
				rs.close(); st.close(); conn.close(); } catch (SQLException e) {e.printStackTrace();}
			loading.setMessage(getString(R.string.dldata));
			return true; }}
  
  public class BuscaCodigoInvitacion extends AsyncTask<String, Void, Boolean> {
		int flagmensaje=0, eid;
		protected void onPreExecute() {
     	loading = new ProgressDialog(ListaEstablecimientos.this);
     	loading.setMessage(getString(R.string.be1));
     	loading.setCancelable(false); loading.show(); }

    protected void onPostExecute(final Boolean success) {
    	if (loading.isShowing()) {loading.dismiss();}
     	if(success) { popupWindow.dismiss(); new aumentaEst(eid).execute(); 
     	new descargaLogos().execute();
     	}else{
     		if(flagmensaje==1)
     			Toast.makeText(getBaseContext(),getString(R.string.errcodinv1),Toast.LENGTH_LONG).show();
     		else if(flagmensaje==2)
     			Toast.makeText(getBaseContext(),getString(R.string.errcodinv2),Toast.LENGTH_LONG).show();
     		else if(flagmensaje==3)
     			Toast.makeText(getBaseContext(),getString(R.string.errcodinv3),Toast.LENGTH_LONG).show();
     	}}

		@Override protected Boolean doInBackground(String... params) { 
			if(!codigoinv.matches("^1[01]{1}[A-Ja-j]+\\d+[A-Ja-j]{2}$")){
			int flagrepe=0;
			try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
			try{DriverManager.setLoginTimeout(20);
				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM invitacliente WHERE" +
						" codigoinvitacion='"+codigoinv+"' AND ((cliente="+sharedPrefs.getInt("id", 0)+
						" AND estado='P') OR cliente=0) ORDER BY cliente DESC");
				if(rs.next()){
					eid=rs.getInt(2);
					for(Est e : db.getAllEstablecimientos())
						if(eid==e.getEid()) flagrepe=1;
					/*if(flagrepe==0){
						try{v.post(new Runnable(){ public void run(){
							loading.setMessage(getString(R.string.estenc));}});
						}catch(Exception e){e.printStackTrace();}*/
						
						if(rs.getInt(4)!=0){
							try {Statement st2 = conn.createStatement();
							st2.executeUpdate("UPDATE invitacliente SET estado='A' WHERE id="+rs.getInt(1)+";" +
									"UPDATE invitacliente SET fechaok='"+sdf.format(new Date())+
									"', horaok='"+shf.format(new Date())+
									"' WHERE (fechaok isnull OR fechaok='') AND id="+rs.getInt(1));
							st2.close(); }catch (SQLException e){e.printStackTrace();}}
						else if(flagrepe==0){ 
							try {Statement st2 = conn.createStatement();
							ResultSet rs2 = st2.executeQuery("SELECT MAX(id) FROM invitacliente");
							int nextid=0;
							if(rs2.next()) nextid = rs2.getInt(1)+1;
							if(nextid!=0){
								Statement st3 = conn.createStatement();
								st3.executeUpdate("INSERT INTO invitacliente VALUES (" +
										nextid+","+eid+",'"+rs.getString(3)+"',"+
										sharedPrefs.getInt("id",0)+",'A','"+rs.getString(6)+"','"+rs.getString(7)+
										"','"+sdf.format(new Date())+"','"+shf.format(new Date())+"','"+
										rs.getString(10)+"','"+rs.getString(11)+"')");
								st2.close(); st3.close();}}catch (SQLException e){e.printStackTrace();}}
						DriverManager.setLoginTimeout(20);
						Statement st4 = conn.createStatement(); int ncliente=0; //XXX nclientes?
						ResultSet rs4 = st4.executeQuery("SELECT "+getString(R.string.camposest)+
								", textcat_all(cnae || ','), nclientes FROM app_company, categoriaempresa " +
								"WHERE app_company.idcompanyapp="+eid+
								"AND app_company.idcompanyapp=categoriaempresa.idcompanyapp GROUP BY "+
								getString(R.string.camposest)+", nclientes");
						while(rs4.next()) {
							if(db.getEstablecimientosCount()==0)
								db.addEstablecimiento(new Est(rs4.getInt(1),rs4.getInt(2),rs4.getString(3),rs4.getString(4),
										rs4.getString(5),rs4.getString(6),"",rs4.getBoolean(7),true,
										1,0.0f,rs4.getString(8),rs4.getString(9),rs4.getString(10),rs4.getString(11),
										rs4.getString(12),rs4.getString(13),rs4.getString(14),rs4.getString(15),0,
										rs4.getString(16),rs4.getString(17),"",rs4.getString(18),rs4.getString(19),
										rs4.getString(20),rs4.getString(21),"0",rs4.getString(22),rs4.getString(23)));
							else {
								int flag = 0;
								for(Est e : db.getAllEstablecimientos())
									if(e.getEid()==rs4.getInt(1)) flag=1;
									if(flag==0)
										db.addEstablecimiento(new Est(rs4.getInt(1),rs4.getInt(2),rs4.getString(3),rs4.getString(4),
												rs4.getString(5),rs4.getString(6),"",rs4.getBoolean(7),true,
												1,0.0f,rs4.getString(8),rs4.getString(9),rs4.getString(10),rs4.getString(11),
												rs4.getString(12),rs4.getString(13),rs4.getString(14),rs4.getString(15),0,
												rs4.getString(16),rs4.getString(17),"",rs4.getString(18),rs4.getString(19),
												rs4.getString(20),rs4.getString(21),"0",rs4.getString(22),rs4.getString(23)));
						}ncliente = rs4.getInt(24); //XXX ncliente?
					} rs4.close(); st4.close();
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
					else if(!sharedPrefs.getString("tel","").equals(""))
							cuerpo+=getString(R.string.contel)+" "+sharedPrefs.getString("tel", "")+".";
						Statement st5 = conn.createStatement();
						st5.executeUpdate("INSERT INTO mensajeapp (clienteglobal,mensaje,fecharealizacion,horarealizacion," +
							"idestablecimiento,tipomensaje,estado,remitente) VALUES " +
							"("+sharedPrefs.getInt("id",0)+",'"+cuerpo+"','"+
							new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date())+"','"+
							new SimpleDateFormat("HH:mm:ss",spanish).format(new Date())+"',"+
							eid+",'A','E','Sistema')");
		  			st5.close();}
					//else{ flagmensaje=2; rs.close(); st.close(); conn.close(); return false; }}
				else{ flagmensaje=1; rs.close(); st.close(); conn.close(); return false; }
				rs.close(); st.close(); conn.close(); } catch (SQLException e) {e.printStackTrace();}}
			else{ String code1="", code2="", estab="", ref="";// int sumatorio=0;
		     	for(int i=0;i<codigoinv.length()-2;i++){
		     		if(Character.isLetter(codigoinv.charAt(i))){
		     			code1+=charToNum(""+codigoinv.toUpperCase().charAt(i)); 
		     						estab+=charToNum(""+codigoinv.toUpperCase().charAt(i)); }
		     		else if(Character.isDigit(codigoinv.charAt(i)))
		     			code1+=Integer.parseInt(""+codigoinv.charAt(i));
		     	}
		     	//for(int i=0;i<code1.length();i++) sumatorio+=Integer.parseInt(""+code1.charAt(i));
		     	code2=""+charToNum(codigoinv.toUpperCase().substring(codigoinv.length()-2,codigoinv.length()-1))+
		     				charToNum(codigoinv.toUpperCase().substring(codigoinv.length()-1,codigoinv.length()));
		     	ref=codigoinv.substring(estab.length()+2,codigoinv.length()-2);
		     	if(Integer.parseInt(code1)%13==Integer.parseInt(code2)){
		     		if(codigoinv.substring(1,2).equals("1") && sharedPrefs.getString("tipo", "E").equals("E") 
		     				|| codigoinv.substring(1,2).equals("0")&&sharedPrefs.getString("tipo", "E").equals("P")){
		     		try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
		  			try{DriverManager.setLoginTimeout(20);
		  				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
		     		DriverManager.setLoginTimeout(20);
						Statement st4 = conn.createStatement(); int ncliente=0;
						ResultSet rs4 = st4.executeQuery("SELECT "+getString(R.string.camposest)+
						", textcat_all(cnae || ','), nclientes FROM app_company, categoriaempresa " +
						"WHERE app_company.idcompanyapp="+estab+
						"AND app_company.idcompanyapp=categoriaempresa.idcompanyapp GROUP BY "+
						getString(R.string.camposest)+", nclientes");
						while(rs4.next()) {
							if(db.getEstablecimientosCount()==0){
								db.addEstablecimiento(new Est(rs4.getInt(1),rs4.getInt(2),rs4.getString(3),rs4.getString(4),
										rs4.getString(5),rs4.getString(6),"",rs4.getBoolean(7),true,
										1,0.0f,rs4.getString(8),rs4.getString(9),rs4.getString(10),rs4.getString(11),
										rs4.getString(12),rs4.getString(13),rs4.getString(14),rs4.getString(15),0,
										rs4.getString(16),rs4.getString(17),"",rs4.getString(18),rs4.getString(19),
										rs4.getString(20),rs4.getString(21),ref,rs4.getString(22),
										rs4.getString(23)));
								SharedPreferences.Editor spe = sharedPrefs.edit();
								spe.putString("main",String.valueOf(rs4.getInt(1))).commit();
							}else {
								int flag = 0;
								for(Est e : db.getAllEstablecimientos())
									if(e.getEid()==rs4.getInt(1)) flag=1;
									if(flag==0)
										db.addEstablecimiento(new Est(rs4.getInt(1),rs4.getInt(2),rs4.getString(3),rs4.getString(4),
												rs4.getString(5),rs4.getString(6),"",rs4.getBoolean(7),true,
												1,0.0f,rs4.getString(8),rs4.getString(9),rs4.getString(10),rs4.getString(11),
												rs4.getString(12),rs4.getString(13),rs4.getString(14),rs4.getString(15),0,
												rs4.getString(16),rs4.getString(17),"",rs4.getString(18),rs4.getString(19),
												rs4.getString(20),rs4.getString(21),ref,rs4.getString(22),
												rs4.getString(23)));
						}ncliente = rs4.getInt(24);
						eid = rs4.getInt(1);
					} rs4.close(); st4.close();
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
						Statement st5 = conn.createStatement();
						st5.executeUpdate("INSERT INTO mensajeapp (clienteglobal,mensaje,http,fecharealizacion,horarealizacion," +
							"idestablecimiento,tipomensaje,estado,remitente) VALUES " +
							"("+sharedPrefs.getInt("id",0)+",'"+cuerpo+"','["+ref+"]','"+
							new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date())+"','"+
							new SimpleDateFormat("HH:mm:ss",spanish).format(new Date())+"',"+
							eid+",'A','E','Sistema')");
		  			st5.close();} catch(Exception e){e.printStackTrace();}
		     		}else{ flagmensaje=3; return false;}
		     	}else{ flagmensaje=3; return false;}
			}
			return true; }
	}
   
  public class descargaLogos extends AsyncTask<String, Void, Boolean> {//S�lo descarga de nuevos
		/*ProgressDialog loading;
		protected void onPreExecute() {
     	loading = new ProgressDialog(ListaEstablecimientos.this);
     	loading.setMessage("Descargando logos...");
     	loading.setCancelable(false); loading.show(); }*/

    protected void onPostExecute(final Boolean success) {
     	try{if (loading.isShowing()) {loading.dismiss();}}catch(Exception e){}
     	Intent i = new Intent(ListaEstablecimientos.this, ListaEstablecimientos.class);
			startActivity(i); finish();}

		@Override protected Boolean doInBackground(String... params) {
  	URL url; URLConnection conn;
    BufferedInputStream inStream; BufferedOutputStream outStream;
    File outFile; FileOutputStream fileStream;
    outFile = new File( File.separator + "data" + File.separator + "data" + 
    		File.separator + ListaEstablecimientos.this.getPackageName() +
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
  }
  
  public static Bitmap redondear(Bitmap bitmap, int pixels) {
      Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
              .getHeight(), Config.ARGB_8888);
      Canvas canvas = new Canvas(output);

      final int color = 0xff424242;
      final Paint paint = new Paint();
      final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
      final RectF rectF = new RectF(rect);
      final float roundPx = pixels;

      paint.setAntiAlias(true);
      canvas.drawARGB(0, 0, 0, 0);
      paint.setColor(color);
      canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

      paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
      canvas.drawBitmap(bitmap, rect, rect, paint);

      return output;
  }
}