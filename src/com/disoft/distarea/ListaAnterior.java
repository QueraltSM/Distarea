package com.disoft.distarea;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.SherlockListActivity;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuInflater;
//import com.actionbarsherlock.view.MenuItem;
//import com.actionbarsherlock.widget.SearchView;
import android.support.v7.app.ActionBar;
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.Art;
import com.disoft.distarea.models.ArtEst;
import com.disoft.distarea.models.Est;
import com.disoft.distarea.models.Ped;

//TODO Funciona bien el selector, falta que se almacenen los cambios realizados en el anterior, y mandar
//las casillas marcadas al Intent con el refresh.
public class ListaAnterior extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
	Locale spanish = new Locale("es", "ES"); DatabaseHandler db;
	SimpleDateFormat sdfdia = new SimpleDateFormat("yyyy-MM-dd",spanish),
					 sdfes  = new SimpleDateFormat("dd-MM-yyyy",spanish);
	SharedPreferences sharedPrefs; View v, popupView; ListView lv; 
  EditText artarticulo, artobservaciones; PopupWindow popupWindow; Menu menu;
  List<Art> articulos = new ArrayList<Art>(); Art a; String prevdate="";
  int marcados=0, orden=0, pid, eid, color=0, flagcheckposedit=0; ImageView sortarticulo; 
  List<Ped> anterior = new ArrayList<Ped>(), ordenable = new ArrayList<Ped>(),
   	checkpos = new ArrayList<Ped>();
  //LongClick
  int flageapart=0, flagcbarras=0; Spinner tipo; PopupWindow popupWindow2; 
  EditText artpedcantidad, artpednombre, artpedprecio, artpedobs, artpedcbarras;
  Est e;
 
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); db = new DatabaseHandler(this);
    e = db.getEstablecimiento(getIntent().getIntExtra("eid",0));
    ActionBar ab = getSupportActionBar();
    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME
    		|ActionBar.DISPLAY_HOME_AS_UP);
    ab.setTitle(getString(R.string.pedidosanteriores)); 
    ab.setSubtitle(e.getNombre());
    ab.setIcon(getResources().getDrawable(R.drawable.device_access_alarms));
    setContentView(R.layout.listaanterior); v = findViewById(R.id.base);
    lv = (ListView)findViewById(android.R.id.list);
    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    eid = getIntent().getIntExtra("eid",0); anterior = db.getPedidoAnt(eid);
    articulos = db.getAllArticulos(); 
    for(Ped p : anterior) if(p.getPid()==0) ordenable.add(p); //Quitar Pid=0 (Descargados)
    for(Ped p : ordenable) anterior.remove(p); ordenable.clear();
    pid = sharedPrefs.getInt("nped",0)+1; 
    //Collections.reverse(anterior);
    Collections.sort(anterior, new Comparator<Ped>(){public int compare(Ped p1, Ped p2) {
    	try{Date d1 = sdfdia.parse(p1.getFecha()), d2 = sdfdia.parse(p2.getFecha());
    		if(d1.after(d2)) return -1; else if (d2.after(d1)) return 1; 
    		else 
    			return db.getArticulo(p1.getAid()).getArticulo()
    				.compareToIgnoreCase(db.getArticulo(p2.getAid()).getArticulo()); 
    	} catch (ParseException e) {return -1;} }});
    ordenable.addAll(anterior); mostrarLista(ordenable);
    
    //XXX Ordenadores 
    sortarticulo = (ImageView) findViewById(R.id.sortarticulo);
		((TextView) findViewById(R.id.txtarticulo)).setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
				Matrix matrix = new Matrix(); switch (orden) {
				default:
					sortarticulo.setVisibility(View.VISIBLE);
					matrix.postRotate(0f,sortarticulo.getDrawable().getBounds().width() / 2,
						sortarticulo.getDrawable().getBounds().height() / 2);
					sortarticulo.setImageMatrix(matrix);
					Collections.sort(ordenable,new Comparator<Ped>(){public int compare(Ped p1, Ped p2) {
						return db.getArticulo(p1.getAid()).getArticulo()
							.compareToIgnoreCase(db.getArticulo(p2.getAid()).getArticulo()); }});
					orden = 1; break;
				case 1:
					matrix.postRotate(180f,sortarticulo.getDrawable().getBounds().width() / 2,
						sortarticulo.getDrawable().getBounds().height() / 2);
					sortarticulo.setImageMatrix(matrix);
					Collections.sort(ordenable, Collections.reverseOrder(new Comparator<Ped>(){
						public int compare(Ped p1, Ped p2) {
							return db.getArticulo(p1.getAid()).getArticulo()
								.compareToIgnoreCase(db.getArticulo(p2.getAid()).getArticulo()); }}));
					orden = 2; break;
				case 2:
					sortarticulo.setVisibility(View.INVISIBLE);
					ordenable.clear(); ordenable.addAll(anterior);
					orden = 0; break; }
				mostrarLista(ordenable); }});
    }
    
  @Override public boolean onCreateOptionsMenu(Menu menu) {
  	MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.listaanterior, menu);
    this.menu = menu;
    SearchView search = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
    	public boolean onQueryTextChange(String s) {
       	if(s.length()<3){ lv.removeAllViewsInLayout(); ordenable.clear();
          for(Ped p : anterior) ordenable.add(p);
          mostrarLista(ordenable); 
        }else{ lv.removeAllViewsInLayout();
          for(Ped p : anterior)
          	try{if(!db.getArticulo(p.getAid()).getArticulo().matches("(?i).*"+s+".*"))
          	ordenable.remove(p); } catch (Exception e) {e.printStackTrace();}
          mostrarLista(ordenable);
        }return true;
    }
	@Override public boolean onQueryTextSubmit(String query) {return true;} };
  search.setOnQueryTextListener(queryTextListener);
    return true; }
 
  @Override public boolean onOptionsItemSelected(MenuItem item) {
  	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    if (item.getItemId() == android.R.id.home) {
    	Intent intent = new Intent(ListaAnterior.this, ListaCompra.class);
    	intent.putExtra("eid",getIntent().getIntExtra("eid",0));
      startActivity(intent); finish(); return true; }
    else if(item.getItemId() == R.id.seleccionar) {
			for(Ped pa : checkpos){
	      	Ped p = new Ped(db.getLastAutoid()+1,pid,pa.getAid(),pa.getEid(),
	        	new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),
	        	pa.getCantidad(),pa.getPrecio(),pa.getObservacion(),0,pa.getAfid(),"",
	        	getIntent().getIntExtra("idclif",0),"S");
	        db.addPedido(p); }
				SharedPreferences.Editor spe = sharedPrefs.edit();
				spe.putInt("nped",sharedPrefs.getInt("nped",0)+1).commit();
				if(e.getConfigura() != null && !e.getConfigura().contains(",PD,") ||
						e.getConfigura() != null && e.getConfigura().contains(",PD,") && 
						db.getPedidosPendientes(e.getEid()).size()!=
						db.getAllArticulosEstablecimientoFrom(e.getEid()).size()){
							//Ventana + ó x
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
				if(checkpos.size()!=0)
					((TextView) popupView.findViewById(R.id.texto)).setText(getString(R.string.okeinsertarahora));
				else
					((TextView) popupView.findViewById(R.id.texto)).setText(getString(R.string.insertarahora));
				ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
				ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
				si.setImageDrawable(getResources().getDrawable(R.drawable.content_new));
				si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
				si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					popupWindow.dismiss();
					contenidoBotonAdd();
				}});
				no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
				no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					popupWindow.dismiss(); Intent i = new Intent(ListaAnterior.this, ListaCompra.class);
					i.putExtra("eid",getIntent().getIntExtra("eid",0));
					startActivity(i); finish(); }});
				
				}else{ //Salta la ventana si no queda nada que pedir
					Intent i = new Intent(ListaAnterior.this, ListaCompra.class);
					i.putExtra("eid", getIntent().getIntExtra("eid",0));
					startActivity(i); }
      }return true;
    }
    
  @Override public void onBackPressed() {
    super.onBackPressed();   
    Intent intent = new Intent(ListaAnterior.this, ListaCompra.class);
    intent.putExtra("eid",getIntent().getIntExtra("eid",0));
    startActivity(intent); finish(); }

	@Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}
	@Override public void onNothingSelected(AdapterView<?> parent) {}

	public class Adaptador extends ArrayAdapter<String> {
    public Adaptador(Context context, int textViewResourceId, String[] objects){
    	super(context, textViewResourceId, objects); }
 
  @Override public View getView(final int position, View convertView, ViewGroup parent){
    LayoutInflater inflater = ListaAnterior.this.getLayoutInflater();
    final View row = inflater.inflate(R.layout.lvlistaanterior, parent, false);
    row.setTag(ordenable.get(position));
    if(getIntent().getStringExtra("checkpos")!=null && !getIntent().getStringExtra("checkpos").equals("") 
    		&& flagcheckposedit!=getIntent().getStringExtra("checkpos").split(";").length)
    for(String s : getIntent().getStringExtra("checkpos").split(";"))
    	if(ordenable.get(position).getAid()==Integer.valueOf(s)){
    		((CheckBox)row.findViewById(R.id.cb)).setChecked(true);
    		checkpos.add((Ped)row.getTag());
    		flagcheckposedit++; }
    row.setLongClickable(true);
	row.setOnLongClickListener(OnLongClickTableRow);
    if(orden==0){
    	if(position==0) prevdate=ordenable.get(position).getFecha();
	    else{ if(!prevdate.equals(ordenable.get(position).getFecha())){
	    	prevdate=ordenable.get(position).getFecha();
	    	//if(color==0) color=1; else color=0; 
	    	}}
    }
    ImageButton vernota = (ImageButton)row.findViewById(R.id.tienenota);
    if(ordenable.get(position).getObservacion().equals(""))
    	vernota.setVisibility(View.INVISIBLE);
    else{
    	((TextView)row.findViewById(R.id.nota))
    		.setText(ordenable.get(position).getObservacion());
    	vernota.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
				if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
				if(((LinearLayout)row.findViewById(R.id.lineanota)).getVisibility()==View.GONE){
					((LinearLayout)row.findViewById(R.id.lineanota)).setVisibility(View.VISIBLE);
					((TextView)row.findViewById(R.id.nota)).setSelected(true);
				}else
					((LinearLayout)row.findViewById(R.id.lineanota)).setVisibility(View.GONE);
			}});
    }
    TextView articulo = (TextView)row.findViewById(R.id.nombreart);
    articulo.setText(db.getArticulo(ordenable.get(position).getAid()).getArticulo());
    TextView und = (TextView)row.findViewById(R.id.undart);
    if(db.getArticulo(ordenable.get(position).getAid()).getTipo().equals("UN")){
	     	Short s = ((Double)ordenable.get(position).getCantidad()).shortValue();
	     	und.setText(String.valueOf(s)); }
    else und.setText(String.valueOf(ordenable.get(position).getCantidad()));
    TextView tipo = (TextView)row.findViewById(R.id.tipoart);
    tipo.setText(db.getArticulo(ordenable.get(position).getAid()).getTipo());
    TextView precio = (TextView)row.findViewById(R.id.precioart);
    switch (Integer.valueOf(sharedPrefs.getString("moneda", "1"))) {
    	case 2:	// 1€ = $1.3121
    		double dol = ordenable.get(position).getPrecio() * 1.3121;
    		precio.setText("$" + String.format("%.2f", dol)); break;
			case 3: // 1€ = 0.866358534 £
				double pou = ordenable.get(position).getPrecio() * 0.866358534;
				precio.setText(String.format("%.2f", pou) + "£"); break;
			case 4: // 1€ = 129.423949 ¥
				double yen = ordenable.get(position).getPrecio() * 129.423949;
				precio.setText(String.format("%.0f", yen) + "¥"); break;
			default:
				precio.setText(String.format("%.2f", 
					ordenable.get(position).getPrecio()) + "€");
				break; }
    TextView fecha = (TextView)row.findViewById(R.id.fechaart);
    if(findViewById(R.id.txtfecha).getVisibility() != View.GONE) 
    	((LinearLayout)row.findViewById(R.id.campofecha)).setVisibility(View.VISIBLE);
    try{fecha.setText(sdfes.format(sdfdia.parse(ordenable.get(position).getFecha())));
	} catch (ParseException e) {e.printStackTrace();}
    	((CheckBox)row.findViewById(R.id.cb)).setButtonDrawable(R.drawable.btn_check_holo_light);
      ((CheckBox)row.findViewById(R.id.cb)).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
						if(((CheckBox)row.findViewById(R.id.cb)).isChecked())
							checkpos.add((Ped)row.getTag());
						else checkpos.remove((Ped)row.getTag()); }});
      ((LinearLayout)row.findViewById(R.id.linea)).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					((CheckBox)row.findViewById(R.id.cb)).setChecked(
		     			!((CheckBox)row.findViewById(R.id.cb)).isChecked());
					if(((CheckBox)row.findViewById(R.id.cb)).isChecked())
						checkpos.add((Ped)row.getTag());
					else checkpos.remove((Ped)row.getTag()); }});
      for(Ped p : checkpos) if(((Ped)row.getTag()).getAid()==p.getAid())
      	((CheckBox)row.findViewById(R.id.cb)).setChecked(true); return row; }}
    
  public void mostrarLista(final List<Ped> pedidos){
  	String[] npedidos = new String[pedidos.size()]; int i=0; 
    for(Ped p : pedidos)
    	if(db.getArticulo(p.getAid())!=null){
    		npedidos[i]=db.getArticulo(p.getAid()).getArticulo(); i++;}
    ArrayAdapter<String> adapter = new Adaptador(this,R.layout.lvlistaanterior,npedidos);
	lv.setAdapter(adapter);}
    //setListAdapter(adapter);}
  
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
			final ImageButton editarnota = (ImageButton)popupView.findViewById(R.id.popartpedmicobs);
			final Ped p = (Ped) v.getTag();
			TextView artpedarticulo = (TextView) popupView.findViewById(R.id.popartpedarticulo);
			final EditText editartpedarticulo = (EditText) getLayoutInflater().inflate(R.layout.edittext, null);
			ArtEst ae = db.getArticuloEstablecimientoInterno(p.getAid());
			if(ae==null) editararticulo.setVisibility(View.VISIBLE); flageapart=0;
			//editararticulo.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			//? Transparente xD ^^
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
					try{Intent intent = new Intent("com.google.zxing.client.android.SCAN");
						intent.setPackage("com.disoft.distarea");
						intent.putExtra("SCAN_MODE","QR_CODE_MODE,PRODUCT_MODE");
						startActivityForResult(intent, 1);
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
					else if (artpedprecio.getText().toString().equals("")) artpedprecio.requestFocus();
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
						db.updatePedido(actPed);
						Art actArt = db.getArticulo(actPed.getAid());
						if(flageapart==1) actArt.setArticulo(editartpedarticulo.getText().toString());
						actArt.setTipo(getResources().getStringArray(R.array.tipoundv)[tipo.getSelectedItemPosition()]);
						actArt.setCbarras(artpedcbarras.getText().toString());
						db.updateArticulo(actArt); popupWindow.dismiss();
						Intent intent = new Intent(ListaAnterior.this,ListaAnterior.class);
						intent.putExtra("eid",eid);
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
						//XXX Actualizar pedido ANTERIOR
						db.updatePedidoAnt(actPed);
						Art actArt = db.getArticulo(actPed.getAid());
						if(flageapart==1) actArt.setArticulo(editartpedarticulo.getText().toString());
						actArt.setTipo(getResources().getStringArray(R.array.tipoundv)[tipo.getSelectedItemPosition()]);
						actArt.setCbarras(artpedcbarras.getText().toString());
						db.updateArticulo(actArt); popupWindow.dismiss();
						Intent intent = new Intent(ListaAnterior.this,ListaAnterior.class);
						//XXX Conservar lista casillas on
						String cadenaposiciones="";
						for(Ped p : checkpos) cadenaposiciones+=p.getAid()+";";
						intent.putExtra("checkpos",cadenaposiciones);
						intent.putExtra("eid",eid);
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
							popupWindow2.dismiss(); popupWindow.dismiss();
							Intent intent = new Intent(ListaAnterior.this,ListaAnterior.class);
							//intent.putExtra("establecimiento",mIcsSpinner.getSelectedItemPosition());
							intent.putExtra("eid",eid);
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
  
  public void contenidoBotonAdd() {
		if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
		//Condición para ListaArticulos (Estándar + nº artículos actual en Pedido = size())
		if(e.getConfigura() != null && e.getConfigura().contains(",PD,")){
			Intent i = new Intent(ListaAnterior.this, ListaArticulos.class);
			i.putExtra("eid", e.getEid()); startActivity(i);
		}else{
		LayoutInflater layoutInflater=(LayoutInflater)getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		popupView = layoutInflater.inflate(R.layout.popupadd, null);
		popupWindow = new PopupWindow(popupView,android.view.ViewGroup.LayoutParams.MATCH_PARENT,
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
		}else{ popupWindow.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, -100);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);}
		artarticulo = (EditText) popupView.findViewById(R.id.articulo);
		artarticulo.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == true) {
					InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
					inputMgr.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);} }});
		final EditText artunidades = (EditText) popupView.findViewById(R.id.unidades);
		artunidades.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override public void onFocusChange(View v, boolean hasFocus) {if (hasFocus == true) {
					InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
					inputMgr.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);
		}else if(artunidades.getText().toString().equals("")
				 || artunidades.getText().toString().equals("0")) artunidades.setText("1");
			}});
		final Spinner tipo = (Spinner) popupView.findViewById(R.id.tipo);
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
		final EditText artprecio = (EditText) popupView.findViewById(R.id.precio);
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
				ImageButton con = (ImageButton) popupView.findViewById(R.id.confirm);
				ImageButton can = (ImageButton) popupView.findViewById(R.id.cancel);
				ImageButton scan = (ImageButton) popupView.findViewById(R.id.scan);
				ImageButton list = (ImageButton) popupView.findViewById(R.id.list);
				ImageButton web = (ImageButton) popupView.findViewById(R.id.web);
				ImageButton mic = (ImageButton) popupView.findViewById(R.id.mic);
				ImageButton micobs = (ImageButton) popupView.findViewById(R.id.micobs);
				/*con.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				can.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				scan.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				list.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				web.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
				mic.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);*/
				//Transparentes? ^^
				con.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					for(Art a : db.getAllArticulos())
						if(a.getArticulo().equals(artarticulo.getText().toString())){ //Si el artículo existe
							Ped pen = db.getPedidoPendiente(eid);
							if (pen != null){ int flagartpend=0; //Si hay pedido
								for(Ped p : db.getPedidosPendientes(eid))
									if(p.getAid()==a.getAid()){ //Y además está ya en el pedido
										p = new Ped(db.getLastAutoid()+1,p.getPid(), a.getAid(), eid, 
											sdfdia.format(new Date()), 
											p.getCantidad()+Double.valueOf(artunidades.getText().toString()),
											p.getPrecio(),p.getObservacion(), p.getEstado(),p.getAfid(),
											p.getOferta(),getIntent().getIntExtra("idclif",0),"S");
										Toast.makeText(getBaseContext(), getString(R.string.previousListAddingRepeated), 
												Toast.LENGTH_LONG).show();
										artarticulo.setText(""); artunidades.setText("1"); artprecio.setText("0");
										artobservaciones.setText(""); artarticulo.requestFocus();
										db.updatePedido(p); flagartpend=1; break; }
								if(flagartpend==0){ //Y no existe en el pedido
									pen = new Ped(db.getLastAutoid()+1,pen.getPid(), a.getAid(), eid, 
										sdfdia.format(new Date()), Double.valueOf(artunidades.getText().toString()),
										Double.valueOf(artprecio.getText().toString()),
										artobservaciones.getText().toString(), pen.getEstado(), pen.getAfid(),
										pen.getOferta(),getIntent().getIntExtra("idclif",0),"S");}
									Toast.makeText(getBaseContext(), getString(R.string.previousListAddingItemAbove), 
										Toast.LENGTH_LONG).show();
									artarticulo.setText(""); artunidades.setText("1"); artprecio.setText("0");
									artobservaciones.setText(""); artarticulo.requestFocus();
						}else{ if(eid>0){ //Si no hay pedido, y es un Establecimiento
									pen = new Ped(db.getLastAutoid()+1,sharedPrefs.getInt("nped",0)+1, a.getAid(), eid,
											sdfdia.format(new Date()), Double.valueOf(artunidades.getText().toString()), 
											Double.valueOf(artprecio.getText().toString()),
											artobservaciones.getText().toString(),0,"","",
											getIntent().getIntExtra("idclif",0),"S");
									SharedPreferences.Editor spe = sharedPrefs.edit();
									spe.putInt("nped",sharedPrefs.getInt("nped",0)+1).commit(); }
								else pen = new Ped(db.getLastAutoid()+1,eid, a.getAid(), eid, //Si no hay pedido, y es una Lista
											sdfdia.format(new Date()), Double.valueOf(artunidades.getText().toString()), 
											Double.valueOf(artprecio.getText().toString()),
											artobservaciones.getText().toString(),0,"","",
											getIntent().getIntExtra("idclif",0),"S");
							Toast.makeText(getBaseContext(), getString(R.string.previousListAddingItemAbove), 
									Toast.LENGTH_LONG).show();
							artarticulo.setText(""); artunidades.setText("1"); artprecio.setText("0");
							artobservaciones.setText(""); artarticulo.requestFocus();
							} db.addPedido(pen); } //Fin si el artículo existe
					if (artarticulo.getText().toString().equals("")) artarticulo.requestFocus();
					else if (artunidades.getText().toString().equals(""))
						artunidades.requestFocus();
					else {
						
						Date hoy = new Date(); Art checkcb;
						checkcb = new Art(db.getLastAid() + 1,
								artarticulo.getText().toString(), null,
								getResources().getStringArray(R.array.tipoundv)
								[tipo.getSelectedItemPosition()]);
						db.addArticulo(checkcb);
						Ped pen = db.getPedidoPendiente(e.getEid());
						if (pen != null)
							pen = new Ped(db.getLastAutoid()+1,pen.getPid(), checkcb.getAid(), e.getEid(), 
								new SimpleDateFormat("yyyy-MM-dd",spanish).format(hoy), 
								Double.valueOf(artunidades.getText().toString()),
								Double.valueOf(artprecio.getText().toString()), 
								artobservaciones.getText().toString(), pen.getEstado(), pen.getAfid(),
								pen.getOferta(),getIntent().getIntExtra("idclif",0),"S");
						else{
							pen = new Ped(db.getLastAutoid()+1,pid, checkcb.getAid(), e.getEid(),
								new SimpleDateFormat("yyyy-MM-dd",spanish).format(hoy), 
								Double.valueOf(artunidades.getText().toString()), 
								Double.valueOf(artprecio.getText().toString()),
								artobservaciones.getText().toString(),0,"","",
								getIntent().getIntExtra("idclif",0),"S");
							SharedPreferences.Editor spe = sharedPrefs.edit();
							spe.putInt("nped",sharedPrefs.getInt("nped",0)+1).commit(); }
						db.addPedido(pen);
						Toast.makeText(getBaseContext(), getString(R.string.previousListItemCreated), Toast.LENGTH_LONG).show();
						artarticulo.setText(""); artunidades.setText("1"); artprecio.setText("0");
						artobservaciones.setText(""); artarticulo.requestFocus(); } }});
				mic.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
	    		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
	    		try {startActivityForResult(intent, 6);
	        	artarticulo.setText("");} catch (ActivityNotFoundException a) {
	          Toast.makeText(getApplicationContext(),
	        		  getString(R.string.previousListDeviceNotSupported),Toast.LENGTH_SHORT).show();} }});
				micobs.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
		    		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		    		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
		    		try {startActivityForResult(intent, 7);
		        	artobservaciones.setText("");} catch (ActivityNotFoundException a) {
		          Toast.makeText(getApplicationContext(),
		        		  getString(R.string.previousListDeviceNotSupported),Toast.LENGTH_SHORT).show();} }});
				scan.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					try {Intent intent = new Intent("com.google.zxing.client.android.SCAN");
						intent.setPackage("com.disoft.distarea");
						intent.putExtra("SCAN_MODE","QR_CODE_MODE,PRODUCT_MODE");
						startActivityForResult(intent, 0);
					} catch (Exception e) {e.printStackTrace();}
					//XXX HACER: Se reciben los datos (onActivityResult)
				}});
				int contador = db.getArticulosCount();
				for(Ped p : db.getAllPedidos(getIntent().getIntExtra("eid",0))){
					for(Art a : db.getAllArticulos()){
						if(a.getAid()==p.getAid())
							contador--; }}
				if(contador>0)
					list.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						popupWindow.dismiss();
						Intent i = new Intent(ListaAnterior.this, ListaArticulos.class);
						i.putExtra("establecimiento",getIntent().getIntExtra("establecimiento",0));
						i.putExtra("eid", getIntent().getIntExtra("eid",0));
						//i.putExtra("volver", 0);
						startActivity(i); }});
				else list.setEnabled(false);
				if(db.getEstablecimiento(getIntent().getIntExtra("eid",0)).getTv()==null)
					web.setEnabled(false);
				else{ web.setEnabled(true);
				web.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
						popupWindow.dismiss();
						Intent intent = new Intent(ListaAnterior.this,TiendaVirtual.class);
						intent.putExtra("establecimiento",getIntent().getIntExtra("establecimiento",0));
						intent.putExtra("eid", getIntent().getIntExtra("eid",0));
						startActivity(intent); finish(); }}); }
				can.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						popupWindow.dismiss();
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
						Intent i = new Intent(ListaAnterior.this, ListaCompra.class);
						//i.putExtra("establecimiento", getIntent().getIntExtra("establecimiento",0));
						i.putExtra("eid",getIntent().getIntExtra("eid",0));
						startActivity(i); finish(); }});
		}
	}

  @Override public void onActivityResult(int requestCode, int resultCode, Intent intent) {
  	if (requestCode == 6) { //Llamada a Voz a Texto (Add -> Artículo)
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
  	else if (requestCode == 0) { // Llamada desde añadir a un pedido

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
			
  	}else if (resultCode == RESULT_CANCELED) {
  		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
  	}
  }
}