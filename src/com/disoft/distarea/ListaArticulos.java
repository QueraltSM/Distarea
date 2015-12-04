package com.disoft.distarea;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
//import android.widget.SearchView;
import android.support.v7.widget.SearchView;
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
import com.disoft.distarea.models.Fam;
import com.disoft.distarea.models.Ped;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

 
public class ListaArticulos extends AppCompatActivity implements AdapterViewCompat.OnItemSelectedListener {
	Locale spanish = new Locale("es", "ES");
	SharedPreferences sharedPrefs; DatabaseHandler db;
	View v; ListView lv; Menu menu; ImageView sortarticulo; 
	ArrayList<Fila> filas = new ArrayList<Fila>(), filasini = new ArrayList<Fila>();
	ArrayList<Art> articulos = new ArrayList<Art>(), original = new ArrayList<Art>();
	ArrayList<Fila> prev = new ArrayList<Fila>();
	ArrayList<String> pics = new ArrayList<String>();
	List<Fam> familias = new ArrayList<Fam>(); CheckBox buscar;
	RadioButton todos, seleccion, familia; RadioGroup botones;  
	int marcados=0, orden=0, flageapart=0, flagcbarras=0, idclif=0, flagcargado=0, marca=0, flagfamotros=0, ventanaAdd=0, vista=0;
	Est e; Art a; Fam f; String coin, lastsearch=""; ProgressBar loading;
	InputMethodManager imm; SearchView search; Boolean lupa=false, seleccionar=false; //MenuItem item;
	File fileparcel, dirparcel; Parcel parcel; Fila vermas;
	
	//Ventana BotonAdd
	//PopupWindow popupWindow;
	View popupView;
	Spinner tipo; EditText artarticulo, artunidades, artprecio, artcbarras;
	SimpleDateFormat sdfdia = new SimpleDateFormat("yyyy-MM-dd",spanish);
	private Animator mCurrentAnimator; private int mShortAnimationDuration;

	@Override public void onItemSelected(AdapterViewCompat<?> parent, View view, int position, long id) {}
	@Override public void onNothingSelected(AdapterViewCompat<?> parent) {}

	public static class Fila implements Parcelable {
	  Art articulo; Ped panterior; ArtEst artest; Double cantidad; //double precio;
  public Fila(Art a, Ped p, ArtEst ae){ articulo=a; panterior=p; artest=ae; cantidad=0.0; }
  protected Fila(Parcel in) {
      articulo = new Art(in.readInt(), in.readString(), in.readString(), in.readString());
      panterior = new Ped(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readString(),
    		  in.readDouble(), in.readDouble(), in.readString(), in.readInt(),
    		  in.readString(), in.readString(), in.readInt(), in.readString());
      artest = new ArtEst(in.readInt(), in.readString(), in.readInt(), in.readString(), in.readString());
      cantidad = in.readByte() == 0x00 ? null : in.readDouble();
  }
  @Override public int describeContents() {	return 0; }
  @Override public void writeToParcel(Parcel dest, int flags) {
	  //Art
	  dest.writeInt(articulo.getAid());
      dest.writeString(articulo.getArticulo());
      dest.writeString(articulo.getCbarras());
      dest.writeString(articulo.getTipo());
      
      //Ped
      dest.writeInt(panterior.getAutoid());
      dest.writeInt(panterior.getPid());
      dest.writeInt(panterior.getAid());
      dest.writeInt(panterior.getEid());
      dest.writeString(panterior.getFecha());
      dest.writeDouble(panterior.getCantidad());
      dest.writeDouble(panterior.getPrecio());
      dest.writeString(panterior.getObservacion());
      dest.writeInt(panterior.getEstado());
      dest.writeString(panterior.getAfid());
      dest.writeString(panterior.getOferta());
      dest.writeInt(panterior.getIdclif());
      dest.writeString(panterior.getPreciomanual());
	  
      //ArtEst
      if(artest!=null){
	      dest.writeInt(artest.getAid());
	      dest.writeString(artest.getAfid());
	      dest.writeInt(artest.getEid());
	      dest.writeString(artest.getFid());
	      dest.writeString(artest.getActivo());
      }
      
      if (cantidad == null) { dest.writeByte((byte) (0x00)); } 
      else { dest.writeByte((byte) (0x01)); dest.writeDouble(cantidad); }
  }

		public Art getArticulo(){ return articulo; }
		public Ped getPedido(){ return panterior; }
		public ArtEst getArticuloEstablecimiento(){ return artest; }

  public static final Parcelable.Creator<Fila> CREATOR = new Parcelable.Creator<Fila>() {
      @Override public Fila createFromParcel(Parcel in) { return new Fila(in); }
      @Override public Fila[] newArray(int size) { return new Fila[size]; } };
  } 
 
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if(savedInstanceState!=null && savedInstanceState.containsKey("marca"))
    	marca = savedInstanceState.getInt("marca");
    if(savedInstanceState!=null && savedInstanceState.containsKey("vista"))
    	vista = savedInstanceState.getInt("vista");
    if(savedInstanceState!=null && savedInstanceState.containsKey("seleccionar"))
    	seleccionar=savedInstanceState.getBoolean("seleccionar");
    if(savedInstanceState!=null && savedInstanceState.containsKey("lupa"))
    	lupa=savedInstanceState.getBoolean("lupa");
    if(savedInstanceState!=null && savedInstanceState.containsKey("lastsearch"))
    	lastsearch = savedInstanceState.getString("lastsearch");
    if(savedInstanceState!=null && savedInstanceState.containsKey("filas")){
    	filasini = savedInstanceState.getParcelableArrayList("filas");
    	if(filasini!=null) flagcargado=1;
    }
    if(savedInstanceState!=null && savedInstanceState.containsKey("ventanaAdd")){
    	ventanaAdd=savedInstanceState.getInt("ventanaAdd");
    	if(ventanaAdd==2){
    	if(savedInstanceState.containsKey("artarticulo") && artarticulo!=null)
    		artarticulo.setText(savedInstanceState.getString("artarticulo"));
    	if(savedInstanceState.containsKey("artunidades") && artunidades!=null)
    		artunidades.setText(savedInstanceState.getString("artunidades"));
    	if(savedInstanceState.containsKey("tipo") && tipo!=null)
    		tipo.setSelection(savedInstanceState.getInt("tipo"));
    	if(savedInstanceState.containsKey("artprecio") && artprecio!=null)
    		artprecio.setText(savedInstanceState.getString("artprecio"));
    	if(savedInstanceState.containsKey("artcbarras") && artcbarras!=null)
    		artcbarras.setText(savedInstanceState.getString("artcbarras"));
    	}
    }
    
    ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(getBaseContext());
    config.threadPriority(Thread.NORM_PRIORITY - 2);
	config.denyCacheImageMultipleSizesInMemory();
	//config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
	//config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
	//config.tasksProcessingOrder(QueueProcessingType.LIFO);
	config.writeDebugLogs();
    ImageLoader.getInstance().init(config.build());

	  ActionBar ab = getSupportActionBar();
    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
    ab.setTitle(getString(R.string.articulos)); 
    ab.setIcon(R.drawable.collections_view_as_list);
    setContentView(R.layout.listaarticulos); v = findViewById(R.id.base);
    lv = (ListView)findViewById(android.R.id.list);
    todos = (RadioButton) findViewById(R.id.todos);
    seleccion = (RadioButton) findViewById(R.id.seleccion);
    familia = (RadioButton) findViewById(R.id.familia);
    buscar = (CheckBox) findViewById(R.id.buscar);
    botones = (RadioGroup) findViewById(R.id.botones);
    loading = (ProgressBar) findViewById(R.id.loading);
    ((TextView)findViewById(R.id.pie2)).setSelected(true);
        
    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    if(sharedPrefs.getString("moneda", "1").equals("2")) coin="$";
    else if(sharedPrefs.getString("moneda", "1").equals("3")) coin="�";
    else if(sharedPrefs.getString("moneda", "1").equals("4")) coin="�";
    else coin="�";
    db = new DatabaseHandler(this); original = db.getAllArticulosAlfa();
    e = db.getEstablecimiento(getIntent().getIntExtra("eid",0));
    if(getIntent().getIntExtra("idclif",0)!=0)
    	idclif = getIntent().getIntExtra("idclif",0);
    if(sharedPrefs.getInt("solicitacliest",0) == e.getEid() && 
			   (e.getCnae() != null && e.getCnae().contains("39110") ||
				e.getCnae() != null && e.getCnae().contains("39100")))
			 ab.setSubtitle("Mesa " + e.getReferencia());
	else{
		if(idclif==0) ab.setSubtitle(e.getNombre());
		else ab.setSubtitle(e.getNombre() + " (" + db.buscaClienteF(idclif).getNombre() + ")");
	}
    if(filasini.isEmpty() && e.getEid()>0){
    dirparcel = new File(getExternalCacheDir()+File.separator+"parcel"+File.separator);
    //fileparcel = new File(dirparcel.getAbsolutePath()+e.getEid());
    fileparcel = new File(getExternalCacheDir()+File.separator+"parcel"+File.separator+e.getEid());
    parcel = Parcel.obtain();
    
    //Leer parcel
    /*
    try{ if(fileparcel.exists() && 1==0){
    		FileInputStream fis = new FileInputStream(getExternalCacheDir()+File.separator+"parcel"+File.separator+e.getEid());
    		byte[] array = new byte[(int) fis.getChannel().size()]; 
    		fis.read(array, 0, array.length); fis.close(); 
    		parcel.unmarshall(array, 0, array.length); 
    		parcel.setDataPosition(0); 
    		while(parcel.dataAvail()>0)
    			filasini.add(Fila.CREATOR.createFromParcel(parcel));
    		parcel.recycle();
    		if(filasini!=null){ flagcargado=1; Log.e("CARGAPARCEL","FILELOADED"); }
    	}else{ Log.e("CARGAPARCEL","NOFILE "+fileparcel.getAbsolutePath());
    		//Cargar filasini
    		/*Log.e("EID",""+e.getEid());
    		Log.e("ARTICULOS",""+db.getAllArticulosAlfa().size());
    		Log.e("NFILAS",""+db.preparaFilaLA2(e.getEid(),db.getAllArticulosAlfa()).size());
    		filasini = db.preparaFilaLA2(e.getEid(),db.getAllArticulosAlfa());
    		for(Fila f : filasini)
    			Log.e("FILA",""+f.articulo.getAid()+"-"+f.articulo.getArticulo());*/
    		
    /*	}
    }catch(Exception e){e.printStackTrace();
    	Log.e("CARGAPARCEL","ERROR");}*/
    }
    
    //Quitar los que ya est�n en el pedido [bypassed]
    /*for(Ped p : db.getAllPedidos(getIntent().getIntExtra("eid",0))){
    	for(Art a : original){ if(a.getAid()==p.getAid()) { original.remove(a); break; }}}
    List<ArtEst> aes = db.getAllArticulosEstablecimientoBut(getIntent().getIntExtra("eid",0));
    if(!aes.isEmpty()) { for(ArtEst ae : aes){ for(Art a : original){
    	 if(a.getAid()==ae.getAid()) { original.remove(a); break; } }}}*/
    //Collections.reverse(original);
    
	/*if(filas.size()>500){
		todos.setVisibility(View.GONE);
		familia.performClick();
	}*/
    
    /*for(Fila fi : filasini){
    	File pic = new File(getExternalCacheDir()+File.separator+"articulos"+File.separator+
				fi.articulo.getAid()+".png");
    	if(pic.exists()) pics.add(pic.getAbsolutePath());
    	else pics.add(null);
    }*/
	
	//Botones
	todos.setOnClickListener(new OnClickListener(){
		@Override public void onClick(View v) {
			if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
			findViewById(R.id.pestanias).setVisibility(View.GONE);
			if(buscar.isChecked()) search.setQueryHint("Buscar en todos");
			for(Fila fi : filasini){
				for(int i=0;i<filas.size();i++)
					if(filas.get(i).articulo.equals(fi.articulo))
						filasini.set(filasini.indexOf(fi), filas.get(i)); break; 
			}
			filas = filasini;
			prev = filas;
			mostrarLista(filas);
			/*if(buscar.isChecked() && !search.getQuery().toString().equals("") &&
					search.hasFocus()==false)
				search.setQuery(search.getQuery().toString(),false);*/
	}});
	
	todos.setOnLongClickListener(new OnLongClickListener(){
		@Override public boolean onLongClick(View arg0) {
			if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
			File archivo = new File(getExternalCacheDir()+File.separator+"parcel"+File.separator+e.getEid());
			archivo.delete(); filasini=null;
			Intent intent = new Intent(ListaArticulos.this, ListaArticulos.class);
			intent.putExtra("establecimiento", getIntent().getIntExtra("establecimiento",0));
			intent.putExtra("eid", getIntent().getIntExtra("eid",0));
			intent.putExtra("idclif",getIntent().getIntExtra("idclif",0));
			startActivity(intent); finish();				
			return false;
	}});
	
	seleccion.setOnClickListener(new OnClickListener(){
		@Override public void onClick(View v) {
			if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
			findViewById(R.id.pestanias).setVisibility(View.GONE);
			if(buscar.isChecked()) search.setQueryHint("Buscar en selecci�n");
			for(Fila fi : filasini){
				for(int i=0;i<filas.size();i++)
					if(filas.get(i).articulo.equals(fi.articulo))
						filasini.set(filasini.indexOf(fi), filas.get(i)); break;
			}
			ArrayList<Fila> marcadas = new ArrayList<Fila>();
			for(Fila fi : filasini)
				if(fi.cantidad>0)
					marcadas.add(fi);
			filas = marcadas;
			prev = filas;
			mostrarLista(filas);
			/*if(buscar.isChecked() && !search.getQuery().toString().equals("") &&
					search.hasFocus()==false)
				search.setQuery(search.getQuery().toString(),false);*/
	}});
	
	//Familias
    
    familias = db.getAllFamilias(e.getEid());
    if(!familias.isEmpty()){
    	if(familias.size()>1){ 
    		//if(flagcargado!=1) 	
    		if(savedInstanceState == null || !savedInstanceState.containsKey("vista")) vista=2;
    		//Parche Familia OTROS
    		if(e.getConfigura().contains(",+,")){
    			((TextView)findViewById(R.id.pie1)).setVisibility(View.VISIBLE);
    			for(Fam f : familias){
    				if(f.getFid().equals("000")){
    					flagfamotros=1;
    					int index = familias.indexOf(f);
    					f.setNombre("   +   ");
    					familias.set(index, f);
    				}
    					
    			}
    			if(flagfamotros==0){
    				//Creo Familia, la guardo en la BD, y la pongo en la lista actual
    				Fam otros = new Fam("000", "   +   ", "", e.getEid());
    				db.addFamilia("000", "OTROS", "", e.getEid());
    				familias.add(otros);
    			}/*else{
    				Fam fam = db.buscaFamilia("000", e.getEid());
    				int index = familias.indexOf(fam);
    				if(index>0){
    				fam.setNombre(" + ");
    				familias.set(index, fam); }
    				/*Log.e("INDEX",""+familias.indexOf(fam));
    				for(Fam fami : familias){
    					if (fami.getFid()=="000")
    						fami.setNombre(" + ");
    						familias.
    				}
      				familias.get(familias.indexOf(db.buscaFamilia("000", e.getEid()))).setNombre(" + ");*/
    		}
    		try{
    		Collections.sort(familias,new Comparator<Fam>(){
    			public int compare(Fam f1, Fam f2) {return f1.getNombre()
    			.compareToIgnoreCase(f2.getNombre()); }});
    		}catch(Exception e){}
    	}
    	OnClickListener pestania = new OnClickListener(){
    		@Override public void onClick(View v) {
    			if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
    			f = (Fam)v.getTag();
    			for(Fam fa : familias)
    				if(f.equals(fa))
    					marca=familias.indexOf(fa);

    			if(buscar.isChecked()) 
    				if(((Fam)botones.getChildAt(0).getTag()).getFid().equals("000") && marca==0)
						search.setQueryHint("Buscar en OTROS");
    				else
    					search.setQueryHint("Buscar en "+f.getNombre());
    			//Buscar art�culos por familia
    				ArrayList<Fila> defamilia = new ArrayList<Fila>();
    				for(Fila fi : filasini)
    					if(fi.artest != null && fi.artest.getFid().equals(f.getFid()))
    						defamilia.add(fi);
    				filas = defamilia;
    				prev = filas;
    				
    				//MELAJUEGO
    				vermas = new Fila(new Art(0,"Ver m�s productos",null,null),
    						new Ped(0,0,0,0,null,0.0,0.0,null,0,null,null,0,null),
    						new ArtEst(0,null,e.getEid(),f.getFid(),null,0f,null));
    				defamilia.add(vermas);
    				
    				mostrarLista(defamilia);
    	}};
    	
    	familia.setVisibility(View.VISIBLE);
    	familia.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v) {
				if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
				findViewById(R.id.pestanias).setVisibility(View.VISIBLE);
				if(marca==0) ventanaAdd=0;
				botones.getChildAt(marca).performClick();
				
				/*REBOOT
				if(botones.getCheckedRadioButtonId()!=-1)//{
					/*if(!((Fam)botones.getChildAt(0).getTag()).getFid().equals("000") && marca!=0)
						botones.getChildAt(0).performClick();
				}else if(!((Fam)botones.getChildAt(0).getTag()).getFid().equals("000") && marca!=0)
					botones.getChildAt(marca).performClick();*/
					//if(!((Fam)botones.getChildAt(0).getTag()).getFid().equals("000") && marca!=0)
					/*if(!((Fam)botones.getChildAt(0).getTag()).getFid().equals("000") &&
							botones.getCheckedRadioButtonId()!=0){
						//findViewById(botones.getCheckedRadioButtonId()).performClick();
						botones.getChildAt(botones.getCheckedRadioButtonId()).performClick();
						Log.e("MOMENTO","Familia onClickListener1");
						Log.e("PULSACI�N",""+botones.getCheckedRadioButtonId());
						Log.e("FID",((Fam)botones.getChildAt(botones.getCheckedRadioButtonId()).getTag()).getFid());
					}else if(!((Fam)botones.getChildAt(0).getTag()).getFid().equals("000")){
						botones.getChildAt(0).performClick();
						Log.e("MOMENTO","Familia onClickListener2");
						Log.e("PULSACI�N","0 (Est�tico)");
						Log.e("FID",((Fam)botones.getChildAt(botones.getCheckedRadioButtonId()).getTag()).getFid());
					}else{
						Log.e("MOMENTO","Familia onClickListener3");
						//Buscar art�culos por familia
	    				/*ArrayList<Fila> defamilia = new ArrayList<Fila>();
	    				for(Fila fi : filasini)
	    					if(fi.artest != null && fi.artest.fid.equals("000"))
	    						defamilia.add(fi);
	    				filas = defamilia;
	    				mostrarLista(defamilia);*
						if(!((Fam)botones.getChildAt(0).getTag()).getFid().equals("000") && marca==0)
							botones.getChildAt(marca).performClick();
					}*/
		}});
    	
    	
    	
    	//Creaci�n botones familias
    	for(Fam fa : familias){
    		RadioButton familia = new RadioButton(getBaseContext());
    		familia.setText(fa.getNombre());
    		familia.setTag(fa);
    		familia.setButtonDrawable(android.R.color.transparent);
		    familia.setBackgroundDrawable(getResources().getDrawable(R.drawable.tabdisoft));
		    familia.setPadding(20, 20, 20, 20);
		    familia.setTextColor(Color.BLACK);
    		if(fa.getFid().equals("000") && e.getConfigura().contains(",+,")){ //Override bot�n +
    			familia.setPadding(160, 20, 160, 20);
    			familia.setBackgroundDrawable(getResources().getDrawable(R.drawable.botondisoft));
    			familia.setOnClickListener(new OnClickListener(){
    				@Override public void onClick(View v) {
    					int miniflag=0;
    					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
    	    			f = (Fam)v.getTag();
    	    			for(Fam fam : familias)
    	    				if(f.equals(fam)){
    	    					if(marca==familias.indexOf(fam))
    	    						miniflag=1;
    	    					marca=familias.indexOf(fam);
    	    				}
    	    					
    	    			if(buscar.isChecked()) 
    	    				search.setQueryHint("Buscar en OTROS");
    	    			//Buscar art�culos por familia
    	    			
    	    			//INTENTO BYPASS DUPLICACI�N GIRO PANTALLA
	    				ArrayList<Fila> defamilia = new ArrayList<Fila>();
	    				//Comento para que no se muestre nada en +
	    				/*
	    				for(Fila fi : filasini){
	    					if(fi.artest == null || fi.artest.fid == null || fi.artest.fid.equals("000"))
	    						defamilia.add(fi);
	    				}*/
	    				filas = defamilia;
	    				mostrarLista(defamilia);
	    				
    	    			if(miniflag==1){ //Supuestamente, retocar el '+'
    	    				if(ventanaAdd==1){ 
    	    					//contenidoBotonAdd();
    	    					Intent intent = new Intent(ListaArticulos.this, TiendaVirtual.class);
    	    			    	intent.putExtra("eid", e.getEid());
    	    			    	intent.putExtra("catalogo","&hacer=catalogo");
    	    			    	startActivity(intent);	
    	    				}else ventanaAdd=1;
    	    			}else
    	    				Toast.makeText(getBaseContext(), 
    								"Pulse el bot�n + de nuevo para crear un art�culo nuevo", 
    								Toast.LENGTH_LONG).show();
    	    			
    	    				
    			}});
    			familia.setOnLongClickListener(new OnLongClickListener(){
    				@Override public boolean onLongClick(View v) {
    					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
    					if(ventanaAdd==1) contenidoBotonAdd();
    					return true;
    				}});
    		}else familia.setOnClickListener(pestania);
    		botones.addView(familia);
    	}
    	if(vista==2)
    	familia.performClick();
    	
    }//Fin familia >1
    else{
    	familia.setVisibility(View.GONE);
    	familia.setEnabled(false);
    }
    
    buscar.setOnClickListener(new OnClickListener(){
		@Override public void onClick(View v) {
			if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
			if(buscar.isChecked()){ //Abre buscar 
				if(menu!=null && menu.findItem(R.id.menuSearch)!=null){
				menu.findItem(R.id.menuSearch).setVisible(true);
				  try{MenuItemCompat.setActionView(menu.findItem(R.id.menuSearch), v).expandActionView();}catch(Exception e){}
//					try{menu.findItem(R.id.menuSearch).expandActionView();}catch(Exception e){}
				  search.setQueryHint("Buscar en todos");
				  //menu.findItem(R.id.help).setVisible(false);
				  if(seleccion.isChecked()) search.setQueryHint("Buscar en selecci�n");
				  else if(familia.isChecked() && botones.getCheckedRadioButtonId()!=-1)
					  if(((Fam)botones.getChildAt(0).getTag()).getFid().equals("000") && marca==0)
							search.setQueryHint("Buscar en OTROS");
					  else
						  search.setQueryHint("Buscar en "+((Fam)botones.getChildAt(marca).getTag()).getNombre());
				}
				if(prev.isEmpty()) prev = filas;
			}else{ //Esconde buscar
				if(menu!=null && menu.findItem(R.id.menuSearch)!=null){
				menu.findItem(R.id.menuSearch).setVisible(false); //menu.findItem(R.id.help).setVisible(true);
				  try{MenuItemCompat.setActionView(menu.findItem(R.id.menuSearch), v).collapseActionView();}catch(Exception e){}
//					try{menu.findItem(R.id.menuSearch).collapseActionView();}catch(Exception e){}
				  imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
				  imm.hideSoftInputFromInputMethod(null,0);
				  if(todos.isChecked()) todos.performClick();
				  else if(seleccion.isChecked()) seleccion.performClick();
				  else if(familia.isChecked())
					  //botones.getChildAt(botones.getCheckedRadioButtonId()-1).performClick();
					  //if(!((Fam)botones.getChildAt(0).getTag()).getFid().equals("000")){
					  	  //Entiendo que siempre ser� ventanaAdd=0 aqu�, para que el repulsado de Buscar
					  	  //no me abra la ventana emergente
					  	  ventanaAdd=0;
						  botones.getChildAt(marca).performClick();
						  Log.e("MOMENTO","Buscar no pulsado");
						  Log.e("PULSACI�N",""+marca);
						  Log.e("FID",""+((Fam)botones.getChildAt(marca).getTag()).getFid());
					  //}
				  //asdasd //Parchear funcionamiento bot�n lupa (paso a paso)
			}}

			SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
				public boolean onQueryTextChange(String s) {
					if(todos.isChecked()){ todos.performClick();
						search.setQueryHint("Buscar en todos"); }
					else if(seleccion.isChecked()){ seleccion.performClick();
					search.setQueryHint("Buscar en selecci�n"); }
					else if(familia.isChecked()){ 
						if(botones.getChildCount()>0){
							if(botones.getCheckedRadioButtonId()==-1){
								//botones.getChildAt(0).performClick();
								if(((Fam)botones.getChildAt(0).getTag()).getFid().equals("000"))
									search.setQueryHint("Buscar en OTROS");
								else
									search.setQueryHint("Buscar en "+((Fam)botones.getChildAt(0).getTag()).getNombre());
							}else{
								//botones.getChildAt(marca).performClick();
								if(((Fam)botones.getChildAt(0).getTag()).getFid().equals("000") && marca==0)
									search.setQueryHint("Buscar en OTROS");
								else
									search.setQueryHint("Buscar en "+((Fam)botones.getChildAt(marca).getTag()).getNombre());
							}
						}else search.setQueryHint("Buscar en todos");
					}
					
					if(s.length()>=3){
						
						ArrayList<Fila> busqueda = new ArrayList<Fila>();
						for(Fila fi : filas)
							if(fi.articulo.getArticulo().toLowerCase().contains(s.toLowerCase()))
								busqueda.add(fi);
						filas = busqueda;
						mostrarLista(filas);
					}else{
						filas = prev;
						mostrarLista(filas);
					}return true;
				}
				@Override public boolean onQueryTextSubmit(String query) {
					search.clearFocus();
					buscar.performClick();
					return true;} };
				if(menu!=null && menu.findItem(R.id.menuSearch)!=null){
				search.setOnQueryTextListener(queryTextListener);
				search.requestFocus(); 
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0); }
		}});
    //if(lastsearch.equals("") && buscar.isChecked()) buscar.setChecked(false);
    
    /*
    for(Art a : original){
    	try{Log.e("EID",""+e.getEid());
    		Log.e("AID",""+a.getAid());
    		Ped p = db.searchPedidoAnt(e.getEid(), "aid", String.valueOf(a.getAid())).get(0);
    		//Revisar c�mo puedo recoger la Lista ya planchada
    		ArtEst ae = db.getArticuloEstablecimiento(a.getAid(), e.getEid());
    		filas.add(new Fila(a,p,ae)); }catch(Exception e){ e.printStackTrace(); }
    }*/ //Test comparaci�n procesado
    final class loadSpinner extends AsyncTask<String, Void, Boolean> {
    	//Calendar c = Calendar.getInstance(); long t1;
    	
		protected void onPostExecute(final Boolean success) {
			if(success){ flagcargado=1;
				loading.setVisibility(View.GONE);
				articulos.addAll(original);
			    filasini.addAll(filas);
			    if(familia.isChecked()){
			    	//if(botones.getCheckedRadioButtonId()!=-1){
			    	if(ventanaAdd!=2) ventanaAdd=0;
						//findViewById(botones.getCheckedRadioButtonId()).performClick();
			    		botones.getChildAt(marca).performClick();
						/*Log.e("MOMENTO","loadSpinner onPostExecute1");
						Log.e("PULSACI�N",""+marca);
						Log.e("FID",""+((Fam)botones.getChildAt(marca).getTag()).getFid());*/
			    	//}else if(!((Fam)botones.getChildAt(0).getTag()).getFid().equals("000")){
			    		/*botones.getChildAt(0).performClick();
			    		Log.e("MOMENTO","loadSpinner onPostExecute2");
			    		Log.e("PULSACI�N","0 (Est�tico)");
						Log.e("FID",((Fam)botones.getChildAt(0).getTag()).getFid());
			    	}*/
			    }else mostrarLista(filas);
			    if(filasini!=null){
			    	//new backupParcel(0).execute();
			    }
			} //Log.e("MUESTRA",c.getTimeInMillis()-t1+"ms");
			}
		
		@Override protected Boolean doInBackground(String... params) {
			//t1 = c.getTimeInMillis();
			//Log.e("EMPIEZA","0ms");
			if(e.getEid()>0){
				//try{filas = db.preparaFilaLA2(e.getEid(), original);}
				try{filas = db.preparaFilaLA3(e.getEid());}
				catch(Exception e){ e.printStackTrace();}
				/*t1 = 
				Log.e("TERMINA",c.getTimeInMillis()-t1+"ms");*/
				if(filas!=null)
					return !filas.isEmpty();
				else return false; 
			}else{
				filas = db.preparaFilaLC(e.getEid());
				return !filas.isEmpty();
			}
		}
    }
    
    if(flagcargado!=1){ new loadSpinner().execute();
    	if(vista==2){
	    	if(marca>0)
	    		botones.getChildAt(marca).performClick();
	    	if(marca==0)
	    		if(!((Fam)botones.getChildAt(0).getTag()).getFid().equals("000"))
	    			botones.getChildAt(0).performClick();
    	}
//    	search.setQueryHint("Buscar en "+((Fam)botones.getChildAt(0).getTag()).getNombre());
    } else{
    	loading.setVisibility(View.GONE);
		articulos.addAll(original);
	    
	    buscar.setChecked(false);
	    switch(vista){
	    default: //Todos
	    	todos.performClick(); break;
	    case 1: //Seleccionados
	    	seleccion.performClick(); break;
	    case 2: //Familia
	    	familia.performClick();
	    	if(ventanaAdd!=2) ventanaAdd=0;
	    		botones.getChildAt(marca).performClick();
	    	break; 
	    }
	    mostrarLista(filas);
    }
    
    //XXX Ordenadores [Marcado para limpiar]
    sortarticulo = (ImageView) findViewById(R.id.sortarticulo);
    Matrix matrix = new Matrix();
    sortarticulo.setVisibility(View.VISIBLE);
	matrix.postRotate(0f, 
		sortarticulo.getDrawable().getBounds().width() / 2,
		sortarticulo.getDrawable().getBounds().height() / 2);
	sortarticulo.setImageMatrix(matrix);
	try{
	Collections.sort(filas,new Comparator<Fila>(){
		public int compare(Fila f1, Fila f2) {return f1.articulo.getArticulo()
		.compareToIgnoreCase(f2.articulo.getArticulo()); }});
	}catch(Exception e){}
	orden = 1;
	((TextView) findViewById(R.id.txtarticulo)).setOnClickListener(new OnClickListener() {
		@Override public void onClick(View vi) {
			if(sharedPrefs.getBoolean("ch",true)) vi.performHapticFeedback(1);
			Matrix matrix = new Matrix();
			switch (orden) {
			default:
				sortarticulo.setVisibility(View.VISIBLE);
				matrix.postRotate(0f, 
					sortarticulo.getDrawable().getBounds().width() / 2,
					sortarticulo.getDrawable().getBounds().height() / 2);
				sortarticulo.setImageMatrix(matrix);
				try{
				Collections.sort(filas,new Comparator<Fila>(){
					public int compare(Fila f1, Fila f2) {return f1.articulo.getArticulo()
					.compareToIgnoreCase(f2.articulo.getArticulo()); }});
				}catch(Exception e){}
				orden = 1; break;
			case 1:
				matrix.postRotate(180f, 
					sortarticulo.getDrawable().getBounds().width() / 2,
					sortarticulo.getDrawable().getBounds().height() / 2);
				sortarticulo.setImageMatrix(matrix);
				try{
				Collections.sort(filas, Collections.reverseOrder(new Comparator<Fila>(){
					public int compare(Fila f1, Fila f2) {return f1.articulo.getArticulo()
					.compareToIgnoreCase(f2.articulo.getArticulo()); }}));
				}catch(Exception e){}
				orden = 0; break;
			/*case 2:
				sortarticulo.setVisibility(View.INVISIBLE);
				//articulos.clear(); articulos.addAll(original);
				if(seleccion.isChecked()){ //Marcados
					ArrayList<Fila> marcadas = new ArrayList<Fila>();
					for(Fila fi : filasini)
						if(fi.cantidad>0)
							marcadas.add(fi);
					filas = marcadas;
				}else if(familia.isChecked() && botones.getCheckedRadioButtonId()!=-1){ //Familia
					ArrayList<Fila> defamilia = new ArrayList<Fila>();
					for(Fila fi : filasini)
						if(fi.artest.fid.equals(f.getFid()))
							defamilia.add(fi);
					filas = defamilia;
				}else //Todos
					filas = filasini;
				mostrarLista(filas);
				orden = 0; break;*/
			}
			mostrarLista(filas);
		}});
	//((TextView) findViewById(R.id.txtarticulo)).performClick();
	
	if(ventanaAdd==2) contenidoBotonAdd();
	
    }
  
  @Override public boolean onCreateOptionsMenu(Menu menu) {
	  	MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.listaarticulos2, menu);
	    this.menu = menu; 
	    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    search = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
	    
	    if(seleccionar && menu!=null && menu.findItem(R.id.seleccionar)!=null)
	    		menu.findItem(R.id.seleccionar).setVisible(true);
	    if(lupa){ buscar.setChecked(false); buscar.performClick();
	    	if(lastsearch!=null && !lastsearch.equals(""))
	    		search.setQuery(lastsearch, false);
	    }else buscar.setChecked(false);
	    return true; }
  
  @Override public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
      
      if(todos.isChecked()) vista=0;
      else if(seleccion.isChecked()) vista=1;
      else if(familia.isChecked()) vista=2;
      savedInstanceState.putInt("vista",vista);
	  for(int i=0;i<botones.getChildCount();i++)
		  if (botones.getChildAt(i).getId() == botones.getCheckedRadioButtonId())
			  savedInstanceState.putInt("marca",i);
      if(menu!=null && menu.findItem(R.id.seleccionar)!=null && 
    		  menu.findItem(R.id.seleccionar).isVisible())
    	  savedInstanceState.putBoolean("seleccionar",true);
      if(buscar.isChecked()) savedInstanceState.putBoolean("lupa",true);
      else savedInstanceState.putBoolean("lupa",false);
      if(search.getQuery().toString()!=null)
      	savedInstanceState.putString("lastsearch",search.getQuery().toString());
      Log.e("LASTSEARCH",""+search.getQuery().toString());
      if(flagcargado==1 && !savedInstanceState.containsKey("filas"))
    	  savedInstanceState.putParcelableArrayList("filas", filasini);
      if(ventanaAdd==2){ savedInstanceState.putInt("ventanaAdd",2);
      	savedInstanceState.putString("artarticulo",artarticulo.getText().toString());
      	savedInstanceState.putString("artunidades",artunidades.getText().toString());
      	savedInstanceState.putInt("tipo",tipo.getSelectedItemPosition());
      	savedInstanceState.putString("artprecio",artprecio.getText().toString());
      	savedInstanceState.putString("artcbarras",artcbarras.getText().toString());
      }
  }

  /*@Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      filas = savedInstanceState.getParcelableArrayList("filas");
      flagcargado=1;
  }*/
  
 //TODO REVISAR PARA QUITAR "BORRAR", SEG�N EL CASO; INCLUIR BUSCAR JUNTO A CONTINUAR
  @Override public boolean onOptionsItemSelected(MenuItem item) {
  	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	  if (item.getItemId() == android.R.id.home) {
	      Intent intent = new Intent(this, ListaCompra.class);
	      intent.putExtra("eid", e.getEid());
	      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
	      startActivity(intent); finish(); return true;
	  }else if(item.getItemId() == R.id.seleccionar) {
	  	int pid;
	  	if(getIntent().getIntExtra("eid",0)<0) //Lista => pid = eid = n<0
	  		pid = getIntent().getIntExtra("eid",0);
	  	else{ 
	  		pid = sharedPrefs.getInt("nped",0)+1;
		  	Ped pp = db.getPedidoPendiente(getIntent().getIntExtra("eid",0),idclif);
		  	if(pp!=null) pid=pp.getPid();
		  	else{SharedPreferences.Editor spe = sharedPrefs.edit();
				spe.putInt("nped",sharedPrefs.getInt("nped",0)+1).commit();}
	  	}
	  	// ADAPTAR AL SISTEMA ACTUAL
	  	for(Fila f : filasini){
	  		if(f.cantidad>0){ Art a = f.articulo;
	    	Ped p = new Ped(db.getLastAutoid()+1,pid,a.getAid(),getIntent().getIntExtra("eid",0),
	    			new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),f.cantidad,
	    			f.panterior.getPrecio(),"",0,f.panterior.getAfid(),"",idclif,"S");
	    	if(sharedPrefs.getInt("solicitacliest",0) != e.getEid()){
		    	f.panterior.setEstado(1); //Favorito
		    	if(f.artest!=null && !f.artest.getAfid().contains("-"))
		    	db.updatePedidoAnt(f.panterior); }
	    	db.addPedido(p); }
	}
	  	Intent intent = new Intent(ListaArticulos.this, ListaCompra.class);
	  	intent.putExtra("eid", e.getEid());
	  	intent.putExtra("idclif",idclif);
	  	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
	  	startActivity(intent); finish();
	  }return true;
  }

  @Override public void onBackPressed() {
      super.onBackPressed();
      Intent intent = new Intent(this, ListaCompra.class);
      intent.putExtra("eid", e.getEid());
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent); finish();
  }

  static class ViewHolder{ LinearLayout linea; ImageView pic; TextView item, cantidad, precio; 
  	CheckBox cb; int pos, aid; }
  
  public class Adaptador extends ArrayAdapter<String>{
    public Adaptador(Context context, int textViewResourceId, String[] object){
    	super(context, textViewResourceId, object); }
 
    @SuppressLint("NewApi")
	@Override public View getView(final int position, View row, ViewGroup parent){
      LayoutInflater inflater = ListaArticulos.this.getLayoutInflater();
      final DisplayMetrics metrics = new DisplayMetrics();
      getWindowManager().getDefaultDisplay().getMetrics(metrics);
      final ViewHolder vh; int flagzoom=0;
      if(row==null){ //Si no estoy errado, la relaci�n reside en la posici�n en la vista, y la posici�n en la lista
          vh = new ViewHolder();
          row = inflater.inflate(R.layout.lvlistaarticulos, parent, false);
          vh.linea = (LinearLayout)row.findViewById(R.id.linea);
          vh.pic = (ImageView)row.findViewById(R.id.pic);
          vh.item = (TextView)row.findViewById(R.id.nombreart);
          vh.cantidad = ((TextView)row.findViewById(R.id.cantidad));
          vh.precio = ((TextView)row.findViewById(R.id.precio));
          vh.cb = (CheckBox)row.findViewById(R.id.cb);
          vh.pos = position; row.setTag(vh); 
          //Intento ajuste
          if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
        		  && metrics.xdpi<=161) vh.item.setTextSize(14);
      } else { vh = (ViewHolder) row.getTag();
          vh.cb.setOnCheckedChangeListener(null); }
      	  vh.pos = position; //Redefino posici�n
      	  vh.cb.setFocusable(false);
      	  
      	try{
  		  if(new File(getExternalCacheDir()+File.separator+"articulos"+
  				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png").exists()){
  			  vh.pic.setImageDrawable(Drawable.createFromPath(getExternalCacheDir()+File.separator+"articulos"+
  				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png"));
  			  flagzoom=1;
  		  }else vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));
      	}catch(Exception e){vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));}
      	if(sharedPrefs.getInt("solicitacliest",0) != e.getEid()){
      	if(filas.get(vh.pos).panterior.getEstado()==1){
      		vh.linea.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));
	      	Bitmap bitmap = Bitmap.createBitmap(
	      			vh.pic.getDrawable().getIntrinsicWidth(),
	      			vh.pic.getDrawable().getIntrinsicHeight(),
	      			Config.ARGB_8888);
	        Canvas canvas = new Canvas(bitmap);
	        Bitmap foto = Bitmap.createScaledBitmap(((BitmapDrawable)vh.pic.getDrawable()).getBitmap(), 
	        		vh.pic.getDrawable().getIntrinsicWidth(),
	        		vh.pic.getDrawable().getIntrinsicHeight(), false);
	        Bitmap warn = ((BitmapDrawable)getResources().getDrawable(R.drawable.ministar)).getBitmap();
	        		
	        canvas.drawBitmap(foto, new Matrix(), new Paint());
	        canvas.drawBitmap(warn, new Matrix(), new Paint());
	        vh.pic.setImageBitmap(bitmap);
      	}else vh.linea.setBackgroundColor(getResources().getColor(android.R.color.white)); 
      	}
      	  
    	  vh.item.setText(filas.get(position).articulo.getArticulo()); 	  
    	  if(vh.item.getText().toString().equals("Ver m�s productos")){
    		  vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.content_new));
    		  vh.pic.setClickable(false); vh.pic.setLongClickable(false);
    		  row.setLongClickable(false);
    		  vh.cantidad.setVisibility(View.GONE);
    		  vh.precio.setVisibility(View.GONE);
    		  vh.cb.setVisibility(View.GONE);
    		  vh.linea.setOnClickListener(new OnClickListener(){
				@Override public void onClick(View v) {
					AlertDialog.Builder adb = new AlertDialog.Builder(ListaArticulos.this);
					String pretienda = "Est� a punto de entrar en la Tienda Virtual de "+e.getNombre()+
						" para ver los art�culos de la categor�a "+f.getNombre()+". ";
					String alatienda = "Ir a la Tienda Virtual";
					if(marcados==1){ alatienda = "A�adir al pedido e ir a la Tienda Virtual";
						pretienda+="\nEl art�culo marcado se a�adir� a su pedido, si contin�a.\n";
					}else if(marcados>1){ alatienda = "A�adir al pedido e ir a la Tienda Virtual";
						pretienda+="\nLos "+marcados+" art�culos marcados se a�adir�n a su pedido, si contin�a.\n";
					}pretienda+="�Est� seguro de querer continuar?";
					adb.setTitle("Ver m�s productos").setMessage(pretienda).setIcon(R.drawable.shop_cart)
						.setPositiveButton(alatienda, new DialogInterface.OnClickListener(){
						@Override public void onClick(DialogInterface dialog, int which) {
							//A�ADIR MARCADOS
							int pid;
							if(getIntent().getIntExtra("eid",0)<0) //Lista => pid = eid = n<0
						  		pid = getIntent().getIntExtra("eid",0);
						  	else{ 
						  		pid = sharedPrefs.getInt("nped",0)+1;
							  	Ped pp = db.getPedidoPendiente(getIntent().getIntExtra("eid",0),idclif);
							  	if(pp!=null) pid=pp.getPid();
							  	else{SharedPreferences.Editor spe = sharedPrefs.edit();
									spe.putInt("nped",sharedPrefs.getInt("nped",0)+1).commit();}
						  	}
						  	// ADAPTAR AL SISTEMA ACTUAL
						  	for(Fila f : filasini){
						  		if(f.cantidad>0){ Art a = f.articulo;
						    	Ped p = new Ped(db.getLastAutoid()+1,pid,a.getAid(),getIntent().getIntExtra("eid",0),
						    			new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),f.cantidad,
						    			f.panterior.getPrecio(),"",0,f.panterior.getAfid(),"",idclif,"S");
						    	if(sharedPrefs.getInt("solicitacliest",0) != e.getEid()){
							    	f.panterior.setEstado(1); //Favorito
							    	if(f.artest!=null && !f.artest.getAfid().contains("-"))
							    	db.updatePedidoAnt(f.panterior); }
						    	db.addPedido(p); }
						}
							
							Intent intent = new Intent(ListaArticulos.this, TiendaVirtual.class);
					    	intent.putExtra("eid", e.getEid());
					    	intent.putExtra("catalogo", "&hacer=catalogo&familia="+f.getFid());
					    	startActivity(intent); finish();
					}}).setNegativeButton("Cancelar y seguir aqu�",new DialogInterface.OnClickListener(){
						@Override public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss(); }});
					adb.create().show();
					
			}});
    	  }else{
    		  vh.cb.setVisibility(View.VISIBLE);
    		  vh.precio.setVisibility(View.VISIBLE);
    	  if(filas.get(position).articulo.getTipo().equals("UN") || filas.get(position).articulo.getTipo().equals(""))
    		  vh.cantidad.setText(""+((Double)filas.get(position).cantidad).intValue());
    	  else vh.cantidad.setText(""+filas.get(position).cantidad);
    	  if(filas.get(position).cantidad>0){
    		  vh.cantidad.setVisibility(View.VISIBLE);
    		  vh.cb.setChecked(true);
    	  }else{ vh.cb.setChecked(false);
    	  		vh.cantidad.setVisibility(View.GONE); }
    	  
    	  if(sharedPrefs.getString("moneda", "1").equals("2"))
        		vh.precio.setText(coin+String.format("%.2f", filas.get(position).panterior.getPrecio()));
          else vh.precio.setText(String.format("%.2f", filas.get(position).panterior.getPrecio())+coin);

      vh.cb.setButtonDrawable(R.drawable.btn_check_holo_light);
      vh.cb.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
				a = filas.get(vh.pos).articulo;
				if(vh.cb.isChecked()){
					//Ventana cantidad
					AlertDialog.Builder adb = new AlertDialog.Builder(ListaArticulos.this);
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					final View layout = inflater.inflate(R.layout.dcantidad,null);
					adb.setTitle("Cantidad "+a.getArticulo()).setCancelable(false).setView(layout);
					final EditText etcantidad = (EditText)layout.findViewById(R.id.etcantidad);
					TextView tvcantidad = (TextView)layout.findViewById(R.id.tvcantidad);
					if(a.getTipo().equals("KG") || a.getTipo().equals("MT"))
							etcantidad.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
					else etcantidad.setInputType(InputType.TYPE_CLASS_NUMBER);
					etcantidad.setSelection(0); etcantidad.requestFocus(); 
					tvcantidad.setText(a.getTipo());
					final CheckBox favorito = (CheckBox)layout.findViewById(R.id.favorito);
					if(sharedPrefs.getInt("solicitacliest",0) != e.getEid()){
					favorito.setVisibility(View.VISIBLE);
					if(filas.get(vh.pos).panterior.getEstado()==1)
						favorito.setChecked(true);
					else favorito.setChecked(false);
					favorito.setOnClickListener(new OnClickListener(){
						@Override public void onClick(View v) {
							if(favorito.isChecked()){
								filas.get(vh.pos).panterior.setEstado(1);
								db.updatePedidoAnt(filas.get(vh.pos).panterior);
								try{
						      		  if(new File(getExternalCacheDir()+File.separator+"articulos"+
						      				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png").exists()){
						      			  vh.pic.setImageDrawable(Drawable.createFromPath(getExternalCacheDir()+File.separator+"articulos"+
						      				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png"));
						      		  }else vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));
								}catch(Exception e){ 
						      		  vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));
						      		  //e.printStackTrace(); 
						      		  }
								//Estrellar
								vh.linea.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));
								Bitmap bitmap = Bitmap.createBitmap(
						      			vh.pic.getDrawable().getIntrinsicWidth(),
						      			vh.pic.getDrawable().getIntrinsicHeight(),
						      			Config.ARGB_8888);
								
								bitmap.getScaledWidth(1);
								
						        Canvas canvas = new Canvas(bitmap);
						        Bitmap base = Bitmap.createScaledBitmap(Bitmap.createBitmap(100, 100, Config.ALPHA_8)
						        		,100,100,false);

						        Bitmap foto = Bitmap.createScaledBitmap(((BitmapDrawable)vh.pic.getDrawable()).getBitmap(), 
						        		vh.pic.getDrawable().getIntrinsicWidth(),
						        		vh.pic.getDrawable().getIntrinsicHeight(), false);
						        Bitmap warn = ((BitmapDrawable)getResources().getDrawable(R.drawable.ministar)).getBitmap();
						        /*Bitmap warn = Bitmap.createScaledBitmap(
						        		((BitmapDrawable)getResources().getDrawable(R.drawable.ministar)).getBitmap(), 
						        		100, 100, false);*/
						        		
						        //canvas.drawBitmap(base, new Matrix(), new Paint());
						        canvas.drawBitmap(foto, new Matrix(), new Paint());
						        Rect r = new Rect();
						        r.inset(-15, -15);
						        
						        canvas.clipRect(r,Region.Op.REPLACE);
						        canvas.drawBitmap(warn, new Matrix(), new Paint());
						        
						        /*canvas.drawRect(r, 
						        		Region.CREATOR(Region.Op.REPLACE));*/
						        
						        
						        
						        vh.pic.setImageBitmap(bitmap);
						      	  
							}else{
								vh.linea.setBackgroundColor(getResources().getColor(android.R.color.white));
								filas.get(vh.pos).panterior.setEstado(0);
								db.updatePedidoAnt(filas.get(vh.pos).panterior);
								try{
						      		  if(new File(getExternalCacheDir()+File.separator+"articulos"+
						      				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png").exists()){
						      			  vh.pic.setImageDrawable(Drawable.createFromPath(getExternalCacheDir()+File.separator+"articulos"+
						      				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png"));
						      		  }else vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));
						      	  }catch(Exception e){ 
						      		  vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));
						      		  //e.printStackTrace(); 
						      		  }
							}
						}});
					}
					
					adb.setPositiveButton("A�adir", new DialogInterface.OnClickListener(){
						@Override public void onClick(DialogInterface dialog, int which) {
							if(etcantidad.getText().toString()!=null && !etcantidad.getText().toString().equals("")){
								if(Double.parseDouble(etcantidad.getText().toString())>0)
									filas.get(position).cantidad = Double.parseDouble(etcantidad.getText().toString());
								else filas.get(position).cantidad = 1.0;
								if(a.getTipo().equals("UN") || a.getTipo().equals(""))
									vh.cantidad.setText(""+(Integer.parseInt(etcantidad.getText().toString())));
								else{ vh.cantidad.setText(""+(Double.parseDouble(etcantidad.getText().toString()))); 
									etcantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
								}
								vh.cantidad.setVisibility(View.VISIBLE);
							}else vh.cb.setChecked(false);
							refrescaMarcados(); dialog.dismiss(); 
					}});
					final AlertDialog ad = adb.create(); ad.show();
					ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					etcantidad.setOnKeyListener(new View.OnKeyListener(){
					    public boolean onKey(View v, int keyCode, KeyEvent event){
					        if (event.getAction() == KeyEvent.ACTION_DOWN){
					            switch (keyCode){
					                case KeyEvent.KEYCODE_DPAD_CENTER:
					                case KeyEvent.KEYCODE_ENTER:
					                	ad.getButton(AlertDialog.BUTTON_POSITIVE).performClick(); 
					                	return true;
					                default: break;
					            }
					        }return false;
					}});
				}else{ //Desmarca casilla
					AlertDialog.Builder adb = new AlertDialog.Builder(ListaArticulos.this);
					adb.setTitle("Poner a cero")
					.setPositiveButton("Poner a cero", new DialogInterface.OnClickListener(){
						@Override public void onClick(DialogInterface dialog, int which) {
							filas.get(position).cantidad = 0.0; vh.cantidad.setVisibility(View.GONE);
							if(a.getTipo().equals("UN")||a.getTipo().equals(""))
								vh.cantidad.setText("0");
							else vh.cantidad.setText("0.0");
							refrescaMarcados(); dialog.dismiss();}})
					.setNegativeButton("Dejarlo como est�", new DialogInterface.OnClickListener(){
						@Override public void onClick(DialogInterface dialog, int which) {
							vh.cb.setChecked(true); dialog.dismiss();}});
					if(a.getTipo().equals("") || a.getTipo().equals("UN"))
						adb.setMessage("Est� a punto de quitar "+filas.get(position).cantidad.intValue()+
								" "+filas.get(position).articulo.getTipo()+" de "+filas.get(position).articulo.getArticulo()+
								" de este pedido. �Est� seguro de querer ponerlo a cero?");
					else adb.setMessage("Est� a punto de quitar los "+filas.get(position).cantidad+
							" "+filas.get(position).articulo.getTipo()+" de "+filas.get(position).articulo.getArticulo()+
							" de este pedido. �Est� seguro de querer ponerlo a cero?");
					
					if(sharedPrefs.getInt("solicitacliest",0) != e.getEid()){
					final CheckBox favorito = new CheckBox(getBaseContext());
					favorito.setText("Favorito"); favorito.setTextColor(getResources().getColor(android.R.color.black));
					if(filas.get(vh.pos).panterior.getEstado()==1) favorito.setChecked(true);
					else favorito.setChecked(false);
					favorito.setOnClickListener(new OnClickListener(){
						@Override public void onClick(View v) {
							if(favorito.isChecked()){
								vh.linea.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));
								filas.get(vh.pos).panterior.setEstado(1);
								db.updatePedidoAnt(filas.get(vh.pos).panterior);
								try{
									if(new File(getExternalCacheDir()+File.separator+"articulos"+
						      				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png").exists()){
						      			  vh.pic.setImageDrawable(Drawable.createFromPath(getExternalCacheDir()+File.separator+"articulos"+
						      				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png"));
						      		  }else vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));
								}catch(Exception e){ 
						      		  vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));
						      		  //e.printStackTrace(); 
						      		  }
						      		//Estrellar
									Bitmap bitmap = Bitmap.createBitmap(
							      			vh.pic.getDrawable().getIntrinsicWidth(),
							      			vh.pic.getDrawable().getIntrinsicHeight(),
							      			Config.ARGB_8888);
							        Canvas canvas = new Canvas(bitmap);
							        Bitmap foto = Bitmap.createScaledBitmap(((BitmapDrawable)vh.pic.getDrawable()).getBitmap(), 
							        		vh.pic.getDrawable().getIntrinsicWidth(),
							        		vh.pic.getDrawable().getIntrinsicHeight(), false);
							        Bitmap warn = ((BitmapDrawable)getResources().getDrawable(R.drawable.ministar)).getBitmap();
							        		
							        canvas.drawBitmap(foto, new Matrix(), new Paint());
							        canvas.drawBitmap(warn, new Matrix(), new Paint());
							        vh.pic.setImageBitmap(bitmap);
						      	  
							}else{
								vh.linea.setBackgroundColor(getResources().getColor(android.R.color.white));
								filas.get(vh.pos).panterior.setEstado(0);
								db.updatePedidoAnt(filas.get(vh.pos).panterior);
								try{
						      		  if(new File(getExternalCacheDir()+File.separator+"articulos"+
						      				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png").exists()){
						      			  vh.pic.setImageDrawable(Drawable.createFromPath(getExternalCacheDir()+File.separator+"articulos"+
						      				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png"));
						      		  }else vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));
						      	  }catch(Exception e){ 
						      		  vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));
						      		  //e.printStackTrace(); 
						      		  }
							}
						}});
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					params.gravity = Gravity.CENTER;
					favorito.setLayoutParams(params);
					adb.setView(favorito);
					}
					adb.create().show();
				} 
			}});
      
      vh.linea.setOnClickListener(new OnClickListener() {
  			@Override public void onClick(View v) {
  				if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
  				filas.get(position).cantidad++;
  				if(filas.get(position).articulo.getTipo().equals("UN")||filas.get(position).articulo.getTipo().equals(""))
  					 vh.cantidad.setText(""+filas.get(position).cantidad.intValue());
  				else vh.cantidad.setText(""+filas.get(position).cantidad);
  				vh.cantidad.setVisibility(View.VISIBLE);
  				if(!vh.cb.isChecked()) vh.cb.setChecked(true);
  				refrescaMarcados();
  				if(filas.get(position).artest!=null)
  					Log.e("AFID",""+filas.get(position).artest.getAfid());
  			}});
      row.setLongClickable(true);
      row.setOnLongClickListener(cantidadMenosUno);
      if(android.os.Build.VERSION.SDK_INT>=11) if(flagzoom==1)
	    	  vh.pic.setOnClickListener(new OnClickListener(){
	    		  @Override public void onClick(View arg0) {
	    			  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    			  zoomImageFromThumb(((ImageView)vh.pic), vh.pic.getDrawable(),vh.pos);
    		  }});
      vh.pic.setOnLongClickListener(new OnLongClickListener(){
		  @Override public boolean onLongClick(View v) {
			  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
			  if(filas.get(vh.pos).panterior.getEstado()==0){ //No era favorito
				//Ventana s�/no a�adir favorito
				final AlertDialog.Builder adb = new AlertDialog.Builder(ListaArticulos.this);
    			adb.setMessage("�Desea marcar el art�culo "+filas.get(position).articulo.getArticulo()+
    					" como uno de sus favoritos?")
    				.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
					@Override public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); }})
            	   .setPositiveButton("A�adir favorito", new DialogInterface.OnClickListener(){
					@Override public void onClick(DialogInterface dialog, int which) {
							
						
				filas.get(vh.pos).panterior.setEstado(1);
				db.updatePedidoAnt(filas.get(vh.pos).panterior);
				try{
		      		  if(new File(getExternalCacheDir()+File.separator+"articulos"+
		      				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png").exists()){
		      			  vh.pic.setImageDrawable(Drawable.createFromPath(getExternalCacheDir()+File.separator+"articulos"+
		      				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png"));
		      		  }else vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));
				}catch(Exception e){ 
		      		  vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));
		      		  //e.printStackTrace(); 
		      		  }
				//Estrellar
				vh.linea.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));
				
				Bitmap bitmap = Bitmap.createBitmap(
		      			vh.pic.getDrawable().getIntrinsicWidth(),
		      			vh.pic.getDrawable().getIntrinsicHeight(),
		      			Config.ARGB_8888);
				
		        Canvas canvas = new Canvas(bitmap);
		        /*Bitmap base = //Bitmap.createScaledBitmap(Bitmap.createBitmap(100, 100, Config.ALPHA_8),100,100,false);
		        		Bitmap.createScaledBitmap(((BitmapDrawable)getResources().getDrawable(R.drawable.cesta6)).getBitmap(),
		        		bitmap.getWidth(),bitmap.getHeight(), false);*/

		        Bitmap foto = Bitmap.createScaledBitmap(((BitmapDrawable)vh.pic.getDrawable()).getBitmap(), 
		        		vh.pic.getDrawable().getIntrinsicWidth(),
		        		vh.pic.getDrawable().getIntrinsicHeight(), false);
		        //Bitmap warn = ((BitmapDrawable)getResources().getDrawable(R.drawable.ministar)).getBitmap();
		        /*Bitmap warn = Bitmap.createScaledBitmap(
		        		((BitmapDrawable)getResources().getDrawable(R.drawable.ministar)).getBitmap(), 
		        		100, 100, false);*/
		        		
		        //canvas.drawBitmap(base, new Matrix(), new Paint());
		        canvas.drawBitmap(foto, new Matrix(), new Paint());
		        /*Rect r = new Rect();
		        r.inset(-15, -15);
		        
		        canvas.clipRect(r,Region.Op.REPLACE);*//*
		        canvas.drawBitmap(warn, new Matrix(), new Paint());
		        
		        
		        /*canvas.drawRect(r, 
		        		Region.CREATOR(Region.Op.REPLACE));*/
		        
				 vh.pic.setImageBitmap(bitmap);
				
				//Test Layers
				Drawable[] layers = new Drawable[2];
				//layers[0] = getResources().getDrawable(R.drawable.cesta6);
				layers[0] = ((BitmapDrawable)vh.pic.getDrawable());
				layers[1] = getResources().getDrawable(R.drawable.ministar);
				/*layers[1].setBounds(layers[0].getIntrinsicWidth()-vh.pic.getDrawable().getIntrinsicWidth()/2, 
									vh.pic.getDrawable().getIntrinsicHeight()/2, 
									layers[0].getIntrinsicWidth()-vh.pic.getDrawable().getIntrinsicWidth()/2, 
									vh.pic.getDrawable().getIntrinsicHeight()/2);*/
				//layers[0].setAlpha(0);
				LayerDrawable layerDrawable = new LayerDrawable(layers);
				//layerDrawable.setLayerInset(0, 0, 0, 125, 125);
				int test = layerDrawable.getIntrinsicWidth()-vh.pic.getDrawable().getIntrinsicWidth()/2;
				layerDrawable.setLayerInset(0, test, 0, test, 0);
				layerDrawable.setLayerInset(1, 0, 0, 125, 125);
				vh.pic.setImageDrawable(layerDrawable);
				
				dialog.dismiss(); }});
    			adb.create().show();
		      	  
			}else{
				//Ventana s�/no quitar favorito
				final AlertDialog.Builder adb = new AlertDialog.Builder(ListaArticulos.this);
    			adb.setMessage("�Desea desmarcar el art�culo "+filas.get(position).articulo.getArticulo()+
    					" de sus favoritos?")
    				.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
					@Override public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); }})
            	   .setPositiveButton("Quitar favorito", new DialogInterface.OnClickListener(){
					@Override public void onClick(DialogInterface dialog, int which) {
						
				vh.linea.setBackgroundColor(getResources().getColor(android.R.color.white));
				filas.get(vh.pos).panterior.setEstado(0);
				db.updatePedidoAnt(filas.get(vh.pos).panterior);
				try{
		      		  if(new File(getExternalCacheDir()+File.separator+"articulos"+
		      				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png").exists()){
		      			  vh.pic.setImageDrawable(Drawable.createFromPath(getExternalCacheDir()+File.separator+"articulos"+
		      				  File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png"));
		      		  }else vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));
		      	  }catch(Exception e){ 
		      		  vh.pic.setImageDrawable(getResources().getDrawable(R.drawable.cesta6));
		      		  //e.printStackTrace(); 
		      		  }
				dialog.dismiss(); }});
    			adb.create().show();
			}
	
			  return true;
	  }});
      
      /*if(e.getCnae() != null && e.getCnae().contains("39110") ||
				e.getCnae() != null && e.getCnae().contains("39100") )
    	row.setOnLongClickListener(cantidadMenosUno);
      else row.setOnLongClickListener(editaFila);*/
    	  }
      return row;}
  }
  
  public void mostrarLista(final List<Fila> articulos){
	  try{
	  	Collections.sort(articulos,new Comparator<Fila>(){
	  		public int compare(Fila f1, Fila f2) {return f1.articulo.getArticulo()
	  			.compareToIgnoreCase(f2.articulo.getArticulo()); }});
	  	//Amago meloquito melopongo "Ver m�s productos"
	  	if(articulos.contains(vermas)){ articulos.remove(vermas); articulos.add(vermas);
	  		if(articulos.size()-1>0)
	  			((TextView)findViewById(R.id.txtarticulo)).setText(getString(R.string.articulo)+"("+(articulos.size()-1)+")");
	  		else ((TextView)findViewById(R.id.txtarticulo)).setText(getString(R.string.articulo));
	  	}else{
	  		if(articulos.size()>0)
	  			((TextView)findViewById(R.id.txtarticulo)).setText(getString(R.string.articulo)+"("+articulos.size()+")");
	  		else ((TextView)findViewById(R.id.txtarticulo)).setText(getString(R.string.articulo));
	  	}
	  }catch(Exception e){}
	  	String []narticulos = new String[articulos.size()];
	    ArrayAdapter<String> adapter = new Adaptador(this,R.layout.lvlistaarticulos, narticulos);
	  lv.setAdapter(adapter);
//	    setListAdapter(adapter);
	  }



  private OnLongClickListener cantidadMenosUno = new OnLongClickListener() {
		@Override public boolean onLongClick(View v) {
			if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
			ViewHolder vh = ((ViewHolder)v.getTag());
			filas.get(vh.pos).cantidad--;
			if(filas.get(vh.pos).cantidad<=0){ filas.get(vh.pos).cantidad=0.0;
				vh.cantidad.setVisibility(View.GONE); vh.cb.setChecked(false); }
			
			if(filas.get(vh.pos).articulo.getTipo().equals("UN")||filas.get(vh.pos).articulo.getTipo().equals(""))
				 vh.cantidad.setText(""+filas.get(vh.pos).cantidad.intValue());
			else vh.cantidad.setText(""+filas.get(vh.pos).cantidad); 
			refrescaMarcados(); return true; //true = no hace click al levantar.
	}};  
		
	public void refrescaMarcados(){ marcados=0;
		for(Fila f : filasini) if(f.cantidad>0.0) marcados++;
		/*menu.clear(); MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listaarticulos2, menu);*/
		if (marcados==1){
			getSupportActionBar().setSubtitle("(" + marcados + getString(R.string.sel1));
			menu.findItem(R.id.seleccionar).setVisible(true);
			//inflater.inflate(R.menu.listaarticulos2, menu);}
		}else if(marcados>1){
			getSupportActionBar().setSubtitle("(" + marcados + getString(R.string.selx));
			menu.findItem(R.id.seleccionar).setVisible(true);
      //inflater.inflate(R.menu.listaarticulos2, menu);}
		}else{
			if(sharedPrefs.getInt("solicitacliest",0) == e.getEid() && 
				   (e.getCnae() != null && e.getCnae().contains("39110") ||
					e.getCnae() != null && e.getCnae().contains("39100")))
				getSupportActionBar().setSubtitle("Mesa " + e.getReferencia());
			else getSupportActionBar().setSubtitle(e.getNombre());
			menu.findItem(R.id.seleccionar).setVisible(false);
			//inflater.inflate(R.menu.listaarticulos, menu); 
		}
	}
		
	/*@Override public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 1) { if (resultCode == RESULT_OK) {
				EditText cbarras = (EditText) popupView.findViewById(R.id.popartpedcbarras);
				cbarras.setText(intent.getStringExtra("SCAN_RESULT")); } }
		else if (resultCode == RESULT_CANCELED) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);}
	}*/
	
	public void contenidoBotonAdd() {
		if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
		//final AlertDialog Dummy;
		AlertDialog.Builder abs0 = new AlertDialog.Builder(ListaArticulos.this);
		LayoutInflater inflater=(LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		//abs0.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
		popupView = inflater.inflate(R.layout.popupadd, null);
		abs0.setView(popupView); abs0.setCancelable(false);
		//final AlertDialog Dummy = new AlertDialog.Builder(this).create();
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
					((TextView) popupView.findViewById(R.id.moneda)).setText("�");
				else if (sharedPrefs.getString("moneda", "1").equals("2"))
					((TextView) popupView.findViewById(R.id.dolar)).setText("$");
				else if (sharedPrefs.getString("moneda", "1").equals("3"))
					((TextView) popupView.findViewById(R.id.moneda)).setText("�");
				else if (sharedPrefs.getString("moneda", "1").equals("4")) {
					artprecio.setInputType(InputType.TYPE_CLASS_NUMBER);
					((TextView) popupView.findViewById(R.id.moneda)).setText("�"); }
				((LinearLayout) popupView.findViewById(R.id.lineaobs)).setVisibility(View.GONE);
				((EditText) popupView.findViewById(R.id.obs)).setVisibility(View.GONE);
				/*artobservaciones.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus == true) {
							InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
							inputMgr.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);} }});*/
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
				((ImageButton) popupView.findViewById(R.id.list)).setVisibility(View.GONE);
				((ImageButton) popupView.findViewById(R.id.web)).setVisibility(View.GONE);
				ImageButton mic = (ImageButton) popupView.findViewById(R.id.mic);
				((ImageButton) popupView.findViewById(R.id.micobs)).setVisibility(View.GONE);
				
				con.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					if (artarticulo.getText().toString().equals("")) artarticulo.requestFocus();
					else if (artunidades.getText().toString().equals("") 
							|| artunidades.getText().toString().equals("0"))
						artunidades.requestFocus();
					else if (artprecio.getText().toString().equals("") 
							|| artprecio.getText().toString().equals("."))
						artprecio.requestFocus();
					
					//Compruebo parecidos
					ArrayList<Fila> contenidos = new ArrayList<Fila>();
					/*for(ArtEst ae : db.getAllArticulosEstablecimientoFrom(e.getEid())){
						Art a = db.getArticulo(ae.getAid());
						//for(final Art a : db.getAllArticulos())
						if(a.getArticulo().toLowerCase().contains(artarticulo.getText().toString().toLowerCase()))
							contenidos.add(a);
					}*/
					for(Fila f : filasini){
						if(f.articulo.getArticulo().toLowerCase().contains(artarticulo.getText().toString().toLowerCase()))
							contenidos.add(f);
						if(!artcbarras.getText().toString().equals("") && 
								f.articulo.getCbarras().equals(artcbarras.getText().toString()))
									contenidos.add(f);
					}
					
					
					if(contenidos.size()>0){
						//Mostrar lista art�culos parecidos + opci�n crear nuevo
						ContextThemeWrapper cw = new ContextThemeWrapper(ListaArticulos.this, R.style.AlertDialogTheme);
						AlertDialog.Builder abs = new AlertDialog.Builder(cw);
			            abs.setIcon(R.drawable.content_new);
			            abs.setTitle("Elija una opci�n");
			            final List<Fila> listafilas = new ArrayList<Fila>();
			            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
			                    ListaArticulos.this, android.R.layout.select_dialog_singlechoice);
			            for(Fila f : contenidos){ arrayAdapter.add(f.articulo.getArticulo()); listafilas.add(f); }
			            arrayAdapter.add("Crear nuevo: \""+artarticulo.getText().toString()+"\"");
			           
			            abs.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
			            	@Override public void onClick(DialogInterface dialog, int which) {
			            		dialog.dismiss(); }});
			            	
			            abs.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
			            	@Override public void onClick(DialogInterface dialog, int which) {
			            		if(arrayAdapter.getItem(which).equals("Crear nuevo: \""+artarticulo.getText().toString()+"\"")){
			            			//A�adir art�culo nuevo
									Art nart = new Art(db.getLastAid() + 1,
										artarticulo.getText().toString(),artcbarras.getText().toString(),
										getResources().getStringArray(R.array.tipoundv)[tipo.getSelectedItemPosition()]);
									db.addArticulo(nart);
									ArtEst nartest = new ArtEst(nart.getAid(),"-"+nart.getAid(),e.getEid(),"","",0,"S");
									db.addArticuloEstablecimiento(nartest);
									//Marcar con X cantidad en nueva familia
									Ped nped = new Ped(0,0, nart.getAid(), e.getEid(),
											sdfdia.format(new Date()), Double.valueOf(artunidades.getText().toString()), 
											Double.valueOf(artprecio.getText().toString()),"",1,"","",0,"S");
									//db.addPedidoAnt(nped);
									//Hacer visible
									Fila fi = new Fila(nart,nped,nartest);
									//filas.add(fi);
									filasini.add(fi);
									//mostrarLista(filas);
									filasini.get(filasini.indexOf(fi)).cantidad = Double.valueOf(artunidades.getText().toString());
									refrescaMarcados();
									seleccion.performClick();
									//new backupParcel(1).execute();
			            		}else{
			            			Fila sel = listafilas.get(which); 
			            			sel.cantidad = Double.valueOf(artunidades.getText().toString());
			            			//sel.panterior.setEstado(1); //(Favorito)
			            			if(sel.articulo.getTipo().equals("UN"))
			            				sel.cantidad = (double) sel.cantidad.intValue();
			            			filasini.set(filasini.indexOf(listafilas.get(which)), sel);
			            			refrescaMarcados();
			            			seleccion.performClick();
			            			//Toast.makeText(getBaseContext(), arrayAdapter.getItem(which), Toast.LENGTH_LONG).show();	
			            		}
			                    }});
			            abs.show();
					}else{
						//A�adir art�culo nuevo
						Art nart = new Art(db.getLastAid() + 1,
							artarticulo.getText().toString(),artcbarras.getText().toString(),
							getResources().getStringArray(R.array.tipoundv)[tipo.getSelectedItemPosition()]);
						db.addArticulo(nart);
						ArtEst nartest = new ArtEst(nart.getAid(),"-"+nart.getAid(),e.getEid(),"","",0,"S");
						db.addArticuloEstablecimiento(nartest);
						//Marcar con X cantidad en nueva familia
						Ped nped = new Ped(0,0, nart.getAid(), e.getEid(),
								sdfdia.format(new Date()), Double.valueOf(artunidades.getText().toString()), 
								Double.valueOf(artprecio.getText().toString()),"",1,"","",0,"S");
						//db.addPedidoAnt(nped);
						//Hacer visible
						Fila fi = new Fila(nart,nped,nartest);
						//filas.add(fi);
						filasini.add(fi);
						//mostrarLista(filas);
						filasini.get(filasini.indexOf(fi)).cantidad = Double.valueOf(artunidades.getText().toString());
						refrescaMarcados();
						seleccion.performClick();
						//Limpio casillas para nuevo art�culo, emito mensaje art�culo creado
						Toast.makeText(getBaseContext(), 
								"Art�culo \""+artarticulo.getText().toString()+"\" creado.", 
								Toast.LENGTH_LONG).show();
						artarticulo.setText(""); artunidades.setText("1"); artprecio.setText("0");
						artcbarras.setText(""); artarticulo.requestFocus();
						//Borro parcel para recrearlo con los nuevos
						/*File archivo = new File(getExternalCacheDir()+File.separator+"parcel"+File.separator+e.getEid());
						archivo.delete();*/
						//Dummy.dismiss();
						//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
					} }});
				/* XXX Anotaci�n fuera de tiempo:
				 * S� que la familia va en el campo ArtEst, pero le pondr� c�digoFacdis ""
				 * Tengo que crear un pedido anterior para conservar el precio, y el activo = S (supongo)
				 * Las Unidades van en la cantidad aparte...
				 * 
				 * Tengo que crearlo tanto en formato Fila, como manualmente para las BD
				*/
				mic.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
					try {startActivityForResult(intent, 6);
					artarticulo.setText("");} catch (ActivityNotFoundException a) {
						Toast.makeText(getApplicationContext(),
								"Dispositivo no soportado",Toast.LENGTH_SHORT).show();} }});

				scan.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
						try {Intent intent = new Intent("com.google.zxing.client.android.SCAN");
							intent.setPackage("com.disoft.distarea");
							intent.putExtra("SCAN_MODE","QR_CODE_MODE,PRODUCT_MODE");
							startActivityForResult(intent, 0);
						} catch (Exception e) {e.printStackTrace();} }});
						// Se reciben los datos (onActivityResult)
				//new parallelCount(list, web, loading).execute();
				//int contador = //db.getArticulosCount();
				//XXX CAMBIO PROVISIONAL: REVISO SI AHORA S�LO PERMITE IR A LA LUPA DE 
				//LA VENTANA EMERGENTE DEL BOT�N + SI HAY PEDIDOS ANTERIORES.
				//can = Dummy.getButton(AlertDialog.BUTTON_NEGATIVE);
				/*can.setNegativeButton(new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface d, int arg1) {
						if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
						d.dismiss();
						//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
						}});*/
				//XXX El amago post-cierre funciona. Probarlo con el con. ; y 
				//ver por qu� no conserva las cantidades...
				final AlertDialog Dummy = abs0.create();
				Dummy.show(); ventanaAdd=2;
				can.setOnClickListener(new View.OnClickListener() {
			        @Override public void onClick(View v) {
			        	if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
			            Dummy.dismiss(); ventanaAdd=1;
			        }});
	}
	
	@Override public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) { // Llamada desde a�adir a un pedido

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
		} else if (requestCode == 6) { //Llamada a Voz a Texto
			if (resultCode == RESULT_OK && null != intent) { 
      	ArrayList<String> text = intent
      			.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
      	artarticulo.setText(text.get(0).substring(0,1).toUpperCase(spanish) 
      			+ text.get(0).substring (1)); }}
	}

	//Escribir parcel
	final class backupParcel extends AsyncTask<String, Void, Boolean> {
		int modo; public backupParcel(int modo) { this.modo=modo; }
    	
		protected void onPostExecute(final Boolean success) {
			if(success) Log.e("PARCELSTATUS","FILEOK");
		}
		
		@Override protected Boolean doInBackground(String... params) {
			 try{
				if(modo==1){ //Borra el archivo para volverlo a crear.
					File archivo = new File(getExternalCacheDir()+File.separator+"parcel"+File.separator+e.getEid());
					archivo.delete();
				}
		    	if(!dirparcel.exists()) dirparcel.mkdirs();
		    	FileOutputStream fos = new FileOutputStream(getExternalCacheDir()+File.separator+"parcel"+File.separator+e.getEid());
		    	Log.e("N�FILAS",""+filasini.size());
		    	for(Fila f : filasini)
		    		f.writeToParcel(parcel, 0);
		    	fos.write(parcel.marshall());
		    	fos.flush(); fos.close(); return true;
		    }catch(Exception e){e.printStackTrace();
		    	Log.e("PARCELSTATUS","FAIL"); return false;}
		}
	
	}
	/*
	class cargaImagenes extends AsyncTask<String, Void, Boolean> {
		View row; int position; Drawable d;
		public cargaImagenes(View row, int position) {
			this.row = row; this.position = position;
		}

		protected void onPreExecute() {}

		protected void onPostExecute(final Boolean success) {
			if(success){
				ViewHolder vh = (ViewHolder)row.getTag();
				vh.pic.setImageDrawable(d);
			}
		}
		
		@Override protected Boolean doInBackground(String... params) {
			if(pics.get(position)!=null){
				try{File imagen = new File(pics.get(position));
				if(imagen.exists()) d = Drawable.createFromPath(imagen.getPath());
				else d = getResources().getDrawable(R.drawable.cesta6);
				}catch(Exception e){e.printStackTrace();}
			return true;}
			else{  d = getResources().getDrawable(R.drawable.cesta6); return false; }
		}
	}*/
	/*if(new File(getExternalCacheDir()+File.separator+"articulos"+File.separator+e.getEid()+File.separator+
  			filas.get(position).articulo.getAid()+".png").exists())
  				vh.pic.setImageDrawable(Drawable.createFromPath(getExternalCacheDir()+
  					File.separator+"articulos"+File.separator+e.getEid()+File.separator+filas.get(position).articulo.getAid()+".png"));*/
	
	@SuppressLint("NewApi")
	private void zoomImageFromThumb(final View thumbView, Drawable d, int pos) {
	    if (mCurrentAnimator != null) { mCurrentAnimator.cancel(); }

	    //final ImageView expandedImageView = new ImageView(ListaArticulos.this) ;
	    final ImageView expandedImageView = (ImageView) findViewById(R.id.zoom);
	    //expandedImageView.setImageDrawable(d);
	    expandedImageView.setImageDrawable(Drawable.createFromPath(getExternalCacheDir()+File.separator+"articulos"+
				  File.separator+e.getEid()+File.separator+filas.get(pos).articulo.getAid()+".png"));

	    final Rect startBounds = new Rect();
	    final Rect finalBounds = new Rect();
	    final Point globalOffset = new Point();

	    thumbView.getGlobalVisibleRect(startBounds);
	    findViewById(R.id.framebase).getGlobalVisibleRect(finalBounds, globalOffset);
	    startBounds.offset(-globalOffset.x, -globalOffset.y);
	    finalBounds.offset(-globalOffset.x, -globalOffset.y);

	    float startScale;
	    if ((float) finalBounds.width() / finalBounds.height()
	            > (float) startBounds.width() / startBounds.height()) {
	        startScale = (float) startBounds.height() / finalBounds.height();
	        float startWidth = startScale * finalBounds.width();
	        float deltaWidth = (startWidth - startBounds.width()) / 2;
	        startBounds.left -= deltaWidth;
	        startBounds.right += deltaWidth;
	    } else {
	        startScale = (float) startBounds.width() / finalBounds.width();
	        float startHeight = startScale * finalBounds.height();
	        float deltaHeight = (startHeight - startBounds.height()) / 2;
	        startBounds.top -= deltaHeight;
	        startBounds.bottom += deltaHeight;
	    }

	    //thumbView.setAlpha(0f);
	    expandedImageView.setVisibility(View.VISIBLE);

	    expandedImageView.setPivotX(0f);
	    expandedImageView.setPivotY(0f);

	    AnimatorSet set = new AnimatorSet();
	    set
	            .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
	                    startBounds.left, finalBounds.left))
	            .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
	                    startBounds.top, finalBounds.top))
	            .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
	            startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
	                    View.SCALE_Y, startScale, 1f));
	    set.setDuration(mShortAnimationDuration);
	    set.setInterpolator(new DecelerateInterpolator());
	    set.addListener(new AnimatorListenerAdapter() {
	        @Override public void onAnimationEnd(Animator animation) {
	            mCurrentAnimator = null; }

	        @Override public void onAnimationCancel(Animator animation) {
	            mCurrentAnimator = null; }
	    });
	    set.start();
	    mCurrentAnimator = set;

	    final float startScaleFinal = startScale;
	    expandedImageView.setOnClickListener(new View.OnClickListener() {
	        @Override public void onClick(View view) {
	            if (mCurrentAnimator != null) { mCurrentAnimator.cancel(); }

	            AnimatorSet set = new AnimatorSet();
	            set.play(ObjectAnimator
	                        				.ofFloat(expandedImageView, 
	                        								View.X, startBounds.left))
	                        .with(ObjectAnimator
	                                .ofFloat(expandedImageView, 
	                                        View.Y,startBounds.top))
	                        .with(ObjectAnimator
	                                .ofFloat(expandedImageView, 
	                                        View.SCALE_X, startScaleFinal))
	                        .with(ObjectAnimator
	                                .ofFloat(expandedImageView, 
	                                        View.SCALE_Y, startScaleFinal));
	            set.setDuration(mShortAnimationDuration);
	            set.setInterpolator(new DecelerateInterpolator());
	            set.addListener(new AnimatorListenerAdapter() {
	                @Override public void onAnimationEnd(Animator animation) {
	                    thumbView.setAlpha(1f);
	                    expandedImageView.setVisibility(View.GONE);
	                    mCurrentAnimator = null;
	                }

	                @Override public void onAnimationCancel(Animator animation) {
	                    thumbView.setAlpha(1f);
	                    expandedImageView.setVisibility(View.GONE);
	                    mCurrentAnimator = null;
	                }
	            });
	            set.start();
	            mCurrentAnimator = set;
	      }});
		}
	
}