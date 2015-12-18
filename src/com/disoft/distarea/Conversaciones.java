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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;*/
import android.support.v7.app.ActionBar;
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.extras.NotificacionMensajes;
import com.disoft.distarea.models.Est;
import com.disoft.distarea.models.Msj;

public class Conversaciones extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
  SharedPreferences sharedPrefs; DatabaseHandler db; String codigoinv;
  View v, popupView; PopupWindow popupWindow; EditText email; int[] eids, mids;
  ListView lv; CheckBox invita; String[] nestablecimiento, dir, logos; Menu m;
  List<Msj> establecimientos = new ArrayList<Msj>(); ActionBar ab;
  Locale spanish = new Locale("es", "ES"); ProgressDialog loading;
  BroadcastReceiver receiver;
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",spanish),
  				   sdfmini = new SimpleDateFormat("dd-MM",spanish),
  				   stf = new SimpleDateFormat("HH:mm:ss",spanish),
  				   stfmini = new SimpleDateFormat("HH:mm",spanish);
  int margen = 10;//android.R.dimen.activity_horizontal_margin;

	@Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}
	@Override public void onNothingSelected(AdapterView<?> parent) {}

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); ab = getSupportActionBar();
    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
    ab.setTitle("Conversaciones"); ab.setIcon(R.drawable.social_cc_bcc);
    setContentView(R.layout.listaestablecimientos); v = findViewById(R.id.base);
    lv = (ListView)findViewById(android.R.id.list);
    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    db = new DatabaseHandler(this); establecimientos = db.getConversaciones();
    if(sharedPrefs.getInt("internetmode",0)!=2){
	    NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(1101); new revisaMensajesEnviados().execute();
		sharedPrefs.edit().putInt("counterm",0).commit();  
		try{PendingIntent.getService(Conversaciones.this, 0, new Intent(
				Conversaciones.this, NotificacionMensajes.class), 0).send();
		} catch (CanceledException e) {e.printStackTrace();}
    }else mostrarLista(establecimientos);
	}
  
  /*public static void reload(Context c, Activity a){
	  Intent intent = new Intent(c, Conversaciones.class);
	  intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	  c.startActivity(intent); 
  }*/
  
  static class ViewHolder{
	  TextView nombre, horatxt, lastmsj, nmensajes;
	  ImageView logo; int pos;
	  //ProgressBar progress;
  }
  
  public class Adaptador extends ArrayAdapter<String>{
    public Adaptador(Context context, int textViewResourceId, String[] objects){
    	super(context, textViewResourceId, objects);}
 
    @Override public View getView(final int position, View convertView, ViewGroup parent){
    	LayoutInflater inflater = Conversaciones.this.getLayoutInflater();
    	final ViewHolder vh;
    	View row = convertView;
        if(row==null){
        	vh = new ViewHolder();
            row = inflater.inflate(R.layout.lvconversaciones, parent, false);
            vh.nombre = (TextView)row.findViewById(R.id.nombreest);
            vh.horatxt = (TextView)row.findViewById(R.id.hora);
            vh.lastmsj = (TextView)row.findViewById(R.id.mensaje);
            vh.nmensajes = (TextView)row.findViewById(R.id.nmensajes);
            vh.logo=(ImageView)row.findViewById(R.id.icon);
          
            row.setTag(vh); }
        else vh = (ViewHolder) row.getTag();
        vh.nombre.setText(nestablecimiento[position]);
        
        //Logo
        //new loadSpinner(row,position).execute();
	    if(db.getEstablecimiento(eids[position]).getLogo()==null)
	    	vh.logo.setImageResource(R.drawable.cesta6);
	    else { File f = new File( File.separator + "data" + File.separator + "data" + 
	    		File.separator + Conversaciones.this.getPackageName() + 
	    		File.separator + "logos" + File.separator + 
	        		db.getEstablecimiento(eids[position]).getLogo());
	        	Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
	        	vh.logo.setImageBitmap(bitmap); }

        
	    //Resto
	    String prev="";
	    if(dir[position].equals("A") && db.getMensajeEnv(mids[position])!=null && 
	    		db.getMensajeEnv(mids[position]).getClienteglobal() == sharedPrefs.getInt("id", 0)){ //direccion.setImageResource(R.drawable.av_upload);
	    	Log.e("MIDS",""+mids[position]);
	    	Log.e("MENSAJE",db.getMensajeEnv(mids[position]).getMensaje());
	    	prev = db.getMensajeEnv(mids[position]).getMensaje();
		    if(prev.startsWith("---------------------------------------<br/>")) 
		    	prev = "Pedido realizado por DISTAREA";
	    if(db.getMensajeEnv(mids[position]).getFecharealiz().compareTo(sdf.format(new Date()))<0)
	    	try{String fecha[]=sdfmini.format(sdf.parse((db.getMensajeEnv(mids[position]).getFecharealiz()))).split("-");
	    		vh.horatxt.setText(fecha[0]+" "+getResources().getStringArray(R.array.mesmini)[Integer.parseInt(fecha[1])-1]);
	    	}catch(Exception e){}
	    else try{vh.horatxt.setText(stfmini.format(stf.parse(db.getMensajeEnv(mids[position]).getHorarealiz())));}
	    catch(Exception e){}
	    SpannableString ss = new SpannableString(Html.fromHtml("&#10003")+" "+prev);
	    if(db.getMensajeEnv(mids[position]).getEstado().equals("F")){ //TipoMsj='A'
	    	ss.setSpan( new ForegroundColorSpan(Color.BLUE), 
   					0, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE ); }
	    //Si no, Estado='E', color por defecto (GRIS)
	    vh.lastmsj.setText(ss);
	    }else if (dir[position].equals("G") || (dir[position].equals("A") && db.getMensajeEnv(mids[position])!=null &&
	    		db.getMensajeEnv(mids[position]).getClienteglobal() != sharedPrefs.getInt("id", 0))){
	    	prev = db.getMensajeRec(mids[position]).getMensaje();
	    	if(prev.startsWith("---------------------------------------<br/>")) 
		    	prev = "Pedido realizado por DISTAREA";
	    if(db.getMensajeRec(mids[position]).getFecharealiz().compareTo(sdf.format(new Date()))<0)
			try{String fecha[]=sdfmini.format(sdf.parse((db.getMensajeRec(mids[position]).getFecharealiz()))).split("-");
			vh.horatxt.setText(fecha[0]+" "+getResources().getStringArray(R.array.mesmini)[Integer.parseInt(fecha[1])-1]);
			}catch (ParseException e) {}
		else try{vh.horatxt.setText(stfmini.format(stf.parse(db.getMensajeRec(mids[position]).getHorarealiz())));}
	    catch(Exception e){}
	    if(db.getMensajeRec(mids[position]).getEstado().equals("R")){ //TipoMsj='G'
	    	SpannableString ss = new SpannableString(" "+Html.fromHtml("&#8226;")+"  "+prev);
	    	ss.setSpan( new ForegroundColorSpan(Color.BLUE), 
   					0, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
	    	vh.lastmsj.setText(ss);
	    	vh.lastmsj.setTypeface(Typeface.DEFAULT_BOLD);
	    	vh.nmensajes.setTypeface(Typeface.DEFAULT_BOLD);
	    	if(db.getMensajeRec(mids[position]).getClienteglobal()!=sharedPrefs.getInt("id",0))
	    		vh.lastmsj.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
	    }
	    else //Estado=='L'
	    	vh.lastmsj.setText("    "+prev);
	    }
	    else if (dir[position].equals("G") || (dir[position].equals("A") && db.getMensajeRec(mids[position])!=null &&
	    		db.getMensajeRec(mids[position]).getClienteglobal() != sharedPrefs.getInt("id", 0))){
	    	//&#8594; = -->
	    	prev = db.getMensajeRec(mids[position]).getMensaje();
	    	if(db.getMensajeRec(mids[position]).getFecharealiz().compareTo(sdf.format(new Date()))<0)
				try{String fecha[]=sdfmini.format(sdf.parse((db.getMensajeRec(mids[position]).getFecharealiz()))).split("-");
				vh.horatxt.setText(fecha[0]+" "+getResources().getStringArray(R.array.mesmini)[Integer.parseInt(fecha[1])-1]);
				}catch (ParseException e) {}
			else try{vh.horatxt.setText(stfmini.format(stf.parse(db.getMensajeRec(mids[position]).getHorarealiz())));}
		    catch(Exception e){}
	    	if(db.getMensajeRec(mids[position]).getEstado().equals("R")){ //TipoMsj='G'
		    	SpannableString ss = new SpannableString(Html.fromHtml("&#8594;")+" "+prev);
		    	ss.setSpan( new ForegroundColorSpan(Color.RED), 
	   					0, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
		    	vh.lastmsj.setText(ss);
		    	vh.nmensajes.setTypeface(Typeface.DEFAULT_BOLD);
		    	vh.lastmsj.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
	    	}else{
	    		SpannableString ss = new SpannableString(" "+Html.fromHtml("&#8594;")+"  "+prev);
	    		vh.lastmsj.setText(ss);
	    		vh.lastmsj.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
	    	}
	    	
	    	
	    	
	    }
	    /*SpannableString ss = new SpannableString(vh.lastmsj.getText().toString());
  		ss.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.darker_gray)),
  				0,1,Spannable.SPAN_INCLUSIVE_INCLUSIVE); vh.lastmsj.setText(ss);*/
	    vh.nmensajes.setText("("+db.getConversacionCount(eids[position])+")");
	    return row;}
}  

  public void mostrarLista(final List<Msj> establecimientos){
  	if(establecimientos.size()>0){
  		if(((ViewGroup)v).getChildCount()==2)
  			((ViewGroup)v).removeView(((ViewGroup)v).findViewWithTag("limpio"));
  	String[] nestablecimiento1 = new String[establecimientos.size()];
  	int[] eids1 = new int[establecimientos.size()], mids1 = new int[establecimientos.size()];
  	String[] dir1 = new String[establecimientos.size()];
  	String[] logos1 = new String[establecimientos.size()];
  	//logos = new String[establecimientos.size()];
      //Ordenaci?n por nombre
      /*Collections.sort(establecimientos, new Comparator<Msj>() { public int compare(Msj e1, Msj e2) {
        return db.getEstablecimiento(e1.getEid()).getNombre().compareToIgnoreCase(
        		db.getEstablecimiento(e2.getEid()).getNombre()); }});*/
  	  //TODO Reestudiar esta parte, para arreglar los mensajes descargados. Acaba i=-64
      int i = 0, repes=0;
      for(Msj es : establecimientos){
    	  if(db.getEstablecimiento(es.getEid())==null){
    		  new descargaEstablecimiento(es.getEid()).execute();
    		  break;
    	  }
    	  if(i==0){
    		  nestablecimiento1[i]=db.getEstablecimiento(es.getEid()).getNombre();
    	  	  eids1[i]=es.getEid(); mids1[i]=es.getMid(); dir1[i]=es.getTipomsj();
    	  	  logos1[i]=db.getEstablecimiento(es.getEid()).getLogo();
    	  	  //logos[i]=db.getEstablecimiento(es.getEid()).getLogo();
    	  	  i++;}
    	  else{ int flagrepe=0;
    		for(int j=0;j<i;j++){ if(es.getEid()==eids1[j]){ flagrepe=1; repes++; break;}}
    		if(flagrepe==0){
    			nestablecimiento1[i]=""+db.getEstablecimiento(es.getEid()).getNombre();
    			eids1[i]=es.getEid(); mids1[i]=es.getMid(); dir1[i]=es.getTipomsj();
    			logos1[i]=db.getEstablecimiento(es.getEid()).getLogo();
    			//logos[i]=db.getEstablecimiento(es.getEid()).getLogo();
    	  	} i++;}
    	  }
      //Como el tama?o se reduce, reasigno arrays.
      int k=0; i-=repes;
      nestablecimiento = new String[i]; 
      eids = new int[i]; 
      mids = new int[i];
      logos = new String[i];
      dir = new String[i];
      for(int j=0;j<i+repes;j++){ if(eids1[j]!=0){ //Parche vac?os (k)
    	  nestablecimiento[k]=nestablecimiento1[j]; eids[k]=eids1[j]; 
    	  mids[k]=mids1[j]; dir[k]=dir1[j]; logos[k]=logos1[j]; k++; }}
      ArrayAdapter<String> adapter = new Adaptador(this,R.layout.lvconversaciones, nestablecimiento);
      //setListAdapter(adapter); ListView list = getListView();
	  lv.setAdapter(adapter); ListView list = lv;
      list.setOnItemClickListener(new OnItemClickListener() {
      	@Override public void onItemClick(AdapterView<?> av, View v, int position, long l){
      		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
      		Intent i = new Intent(Conversaciones.this, Chat.class);
      		i.putExtra("eid",eids[position]); startActivity(i); }});
  	}else{ TextView limpio; 
  		if(((ViewGroup)v).getChildCount()==1){
  			limpio = new TextView(getBaseContext()); limpio.setTag("limpio");
  			limpio.setTextAppearance(getBaseContext(),android.R.style.TextAppearance_Small);
  			limpio.setTextColor(getResources().getColor(android.R.color.black));
  			limpio.setPadding(margen, 0, margen, 0);((ViewGroup) v).addView(limpio);}
  		else  limpio = (TextView)((ViewGroup)v).findViewWithTag("limpio");
  		if(db.getEstablecimientosVisibles().size()>0)
  			limpio.setText("No tiene conversaciones iniciadas. Puede enviar un mensaje a un establecimiento favorito usando el bot?n de la esquina.");
  		}
  	}
  
  	@Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if(android.os.Build.VERSION.SDK_INT < 14)
			inflater.inflate(R.menu.conversaciones, menu);
	  	else{ inflater.inflate(R.menu.conversacionesv14, menu);
		SearchView searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
	    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
	      public boolean onQueryTextChange(String s) {
	      	if(s.length()<3){ lv.removeAllViewsInLayout(); mostrarLista(establecimientos);
	        }else{ lv.removeAllViewsInLayout();
	         	mostrarLista(db.searchConversaciones(s.toString()));
	        }return true;}
	      @Override public boolean onQueryTextSubmit(String query) {return true;} };
	    searchView.setOnQueryTextListener(queryTextListener);} return true;}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
	  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	  if (item.getItemId() == android.R.id.home) { 
		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		  Intent intent = new Intent(Conversaciones.this, ListaCompra.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent); finish(); }
	  else if (item.getItemId() == R.id.refresh){
		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		  if(sharedPrefs.getInt("internetmode", 0)==2)
				Toast.makeText(getBaseContext(), "No puede acceder a internet con el modo Offline activo.", Toast.LENGTH_LONG).show();
		  else{
			  Intent intent = new Intent(Conversaciones.this, Conversaciones.class);
			  startActivity(intent); finish();
		  }
	}
	  /*else if (item.getItemId() == R.id.nueva) { 
		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
		  final List<Est> fav = new ArrayList<Est>();
		  List<Est> vis = db.getEstablecimientosVisibles();
		  for(Est es : vis){ int flag=0;
			  for(Msj m : establecimientos) if(es.getEid()==m.getEid()) flag=1;
			  if(es.getFav() && flag==0) fav.add(es); }
		  
		  Collections.sort(fav, new Comparator<Est>() { public int compare(Est e1, Est e2) {
		        return e1.getNombre().compareToIgnoreCase(e2.getNombre()); }});
		  String[] nuevos = new String[fav.size()];
		  for(int i=0;i<fav.size();i++) nuevos[i] = fav.get(i).getNombre();
		  AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setTitle("Nueva Conversaci?n");
		  LayoutInflater factory = LayoutInflater.from(this);
		  View content = factory.inflate(R.layout.dnuevaconversacion, null);

		    builder.setView(content).setNegativeButton("Cancelar", new OnClickListener(){
			@Override public void onClick(DialogInterface d, int arg1) {d.dismiss();}});
		    final AlertDialog alert = builder.create();
		    OnItemClickListener postconstr = 
		    	new OnItemClickListener() {
	                @Override public void onItemClick(AdapterView<?> av, View v, int i, long l) {
	                	Intent in = new Intent(Conversaciones.this, Chat.class);
						in.putExtra("eid",fav.get(i).getEid());
						alert.dismiss(); startActivity(in);
		    }};
		    lv.setOnItemClickListener(postconstr);
		    alert.show();
		  }*/ return true; }
	
	/* Revisar para men? con im?genes
	 @Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		if(android.os.Build.VERSION.SDK_INT < 14){
		inflater.inflate(R.menu.listacompra, menu); SubMenu subMenu1 = menu.addSubMenu("");
		
		subMenu1.add(getString(R.string.mensajes)).setIcon(R.drawable.content_email)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override public boolean onMenuItemClick(MenuItem item) {
						if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
						//startActivity(new Intent(ListaCompra.this,Mensajes.class)); return false; }});
						startActivity(new Intent(ListaCompra.this,Conversaciones.class)); return false; }});

		subMenu1.add(getString(R.string.establecimientos)).setIcon(R.drawable.social_group)
			.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override public boolean onMenuItemClick(MenuItem item) {
					if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
					startActivity(new Intent(ListaCompra.this,ListaEstablecimientos.class));
					return false; }});

		subMenu1.add(getString(R.string.opciones)).setIcon(R.drawable.action_settings)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override public boolean onMenuItemClick(MenuItem item) {
						if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
						startActivity(new Intent(ListaCompra.this,Opciones.class)); return false; }});

		subMenu1.add(getString(R.string.ayuda)).setIcon(R.drawable.action_help)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override public boolean onMenuItemClick(MenuItem item) {
						if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
						startActivity(new Intent(ListaCompra.this, Ayuda.class)); return false; }});

		MenuItem subMenu1Item = subMenu1.getItem();
		subMenu1Item.setIcon(R.drawable.menu_overflow_light);
		subMenu1Item.setTitle(R.string.menutitle);
		subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);}
		else inflater.inflate(R.menu.listacomprav14, menu);
		m = menu; return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
		if (item.getItemId() == android.R.id.home){
			Intent i = new Intent(ListaCompra.this, ListaCompra.class);
			i.putExtra("establecimiento", 0);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i); finish(); }
		else {if(android.os.Build.VERSION.SDK_INT >= 14){
			if(item.getItemId() == R.id.mensajes){
				if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
				//startActivity(new Intent(ListaCompra.this,Mensajes.class)); return true; }
				startActivity(new Intent(ListaCompra.this,Conversaciones.class)); return true; }
			else if(item.getItemId() == R.id.establecimientos){
				if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
				startActivity(new Intent(ListaCompra.this,ListaEstablecimientos.class));
				return true; }
			else if(item.getItemId() == R.id.configuracion){
				if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
				startActivity(new Intent(ListaCompra.this,Opciones.class)); return true;}
			else if(item.getItemId() == R.id.ayuda){
				if (sharedPrefs.getBoolean("ch", true)) ll.performHapticFeedback(1);
				startActivity(new Intent(ListaCompra.this, Ayuda.class)); return true;}
			else return false;
		}}
		return true;
	}
	
	@Override public boolean onKeyUp(int keyCode, KeyEvent event)  {
  	if(keyCode == KeyEvent.KEYCODE_MENU){
  		if(android.os.Build.VERSION.SDK_INT >= 14)
  			m.performIdentifierAction(R.id.overflow,0); //Crearlo en los xml
  		else{ SubMenu subMenu = m.getItem(m.size()-1).getSubMenu();
        m.performIdentifierAction(subMenu.getItem().getItemId(), 0); }
  		return true;} return super.onKeyUp(keyCode, event);} */
	
	@Override public void onBackPressed() { super.onBackPressed(); 
	Intent intent = new Intent(Conversaciones.this, ListaCompra.class);
	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
	startActivity(intent); finish(); }

  public boolean isNetworkAvailable() {
	 ConnectivityManager connectivityManager = (ConnectivityManager) 
			 getSystemService(Context.CONNECTIVITY_SERVICE);
	 NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	 if(sharedPrefs.getInt("internetmode",0)==0)
	 return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	 else return false;}
  
  private class descargaEstablecimiento extends AsyncTask<String, Void, Boolean> {
	  SimpleDateFormat sdfdia = new SimpleDateFormat("yyyy-MM-dd",spanish),
				 sdfhorass = new SimpleDateFormat("HH:mm",spanish);
	  int eid=0; Connection conn;
	  public descargaEstablecimiento(int eid) { this.eid=eid; }

		protected void onPreExecute() { }

		protected void onPostExecute(final Boolean success) {
			if(!success){
				Log.e("ERROR","Descarga establecimiento nulo fallida.");
				Intent intent = new Intent(Conversaciones.this, ListaCompra.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent); finish();
			}else{
				new descargaLogos().execute();
			}
		}
		
		@Override protected Boolean doInBackground(String... params) {
			boolean result = false;
		  try {conn = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st5 = conn.createStatement();
			ResultSet rs5 = st5.executeQuery("SELECT "+getString(R.string.camposest)+
				", textcat_all(cnae || ',') FROM app_company, categoriaempresa WHERE " +
				"app_company.idcompanyapp=categoriaempresa.idcompanyapp AND " +
				"app_company.idcompanyapp=" + eid + " GROUP BY "+getString(R.string.camposest));
			while(rs5.next()) {
				db.addEstablecimiento(new Est(rs5.getInt(1),rs5.getInt(2),rs5.getString(3),rs5.getString(4),
						rs5.getString(5),rs5.getString(6),"",rs5.getBoolean(7),true,
					1,0.0f,rs5.getString(8),rs5.getString(9),rs5.getString(10),rs5.getString(11),
					rs5.getString(12),rs5.getString(13),rs5.getString(14),rs5.getString(15),0,
					rs5.getString(16),rs5.getString(17),"",rs5.getString(18),rs5.getString(19),
					rs5.getString(20),rs5.getString(21),"0",rs5.getString(22),rs5.getString(23)));
				if(db.getEstablecimiento(rs5.getInt(1))!=null) result=true;
			} rs5.close(); st5.close();
			}catch(Exception e){e.printStackTrace();}
			//Marcar invitaciones como Aceptadas
			try {conn = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st6 = conn.createStatement();
				Log.e("NUEVOSEST",""+eid);
			st6.executeQuery("UPDATE invitacliente SET (estado, fechaok, horaok) = "+
			"('A','"+sdfdia.format(new Date())+"','"+sdfhorass.format(new Date())+"') WHERE "+
			"company="+eid+" AND cliente="+sharedPrefs.getInt("id", 0));
			st6.close(); }catch(Exception e){e.printStackTrace();}
			return result;
		}
  }
  
  public class descargaLogos extends AsyncTask<String, Void, Boolean> {//S?lo descarga de nuevos

	    protected void onPostExecute(final Boolean success) {
	    	Intent intent = new Intent(Conversaciones.this, Conversaciones.class);
			startActivity(intent); finish();
	    }

			@Override protected Boolean doInBackground(String... params) {
	  	URL url; URLConnection conn;
	    BufferedInputStream inStream; BufferedOutputStream outStream;
	    File outFile; FileOutputStream fileStream;
	    outFile = new File( File.separator + "data" + File.separator + "data" + 
	    		File.separator + Conversaciones.this.getPackageName() +
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
  
  private class revisaMensajesEnviados extends AsyncTask<String, Void, Boolean> {

		protected void onPreExecute() { }

		protected void onPostExecute(final Boolean success) {
			if(!success) Log.e("ERROR","PROBLEMAS DESCARGANDO ESTADO MENSAJES ENVIADOS");
			mostrarLista(establecimientos);
		}
		
		@Override protected Boolean doInBackground(String... params) {
			String envids="";
			for(Msj m : establecimientos){
				if(m.getTipomsj().equals("A") && m.getEstado().equals("E"))
					envids+=m.getMidbd()+",";
			}
			Log.e("ENVIDS",""+envids);
			//Mensajes enviados le?dos por el Establecimiento
			if(!envids.equals("")){
			try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
			try{DriverManager.setLoginTimeout(20);
			Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT idmensajeappmovil FROM mensajeapp WHERE " +
					"tipomensaje='A' AND estado='F' AND id IN("+envids.substring(0,envids.length()-1)+")");
			while(rs.next()){
				Msj m = db.getMensajeEnv(rs.getInt(1));
				Log.e("MID",""+m.getMid());
				m.setEstado("F"); db.actualizarMensajeEnviado(m);
				}
			rs.close(); st.close(); conn.close(); }catch(Exception e){e.printStackTrace(); return false;}
		}return true;}}
}