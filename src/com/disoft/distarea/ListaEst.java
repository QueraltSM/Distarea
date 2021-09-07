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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.SherlockListActivity;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuInflater;
//import com.actionbarsherlock.view.MenuItem;
import android.support.v7.app.ActionBar;
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.Est;

public class ListaEst extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
 
	SharedPreferences sharedPrefs;	DatabaseHandler db;
	View v, popupView;	PopupWindow popupWindow;
	List<Est> establecimientos = new ArrayList<Est>(), checkpos = new ArrayList<Est>();
	final List<Est> ordenado = new ArrayList<Est>(); 
	List<String> snclientes = new ArrayList<String>(), 
  				 checkclientes = new ArrayList<String>();
  	ListView lv;	Menu menu;	ActionBar ab; ProgressDialog loading;
  	int marcados=0, orden=0, j=0, pid, eid=0, ncliente=0;	
  	Locale spanish = new Locale("es", "ES"); String query, cnae;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); ab = getSupportActionBar();
    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
    ab.setSubtitle(""); setContentView(R.layout.listaest);
    v = findViewById(R.id.base); lv = (ListView)findViewById(android.R.id.list);
    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    db = new DatabaseHandler(this);
    //Creo cuerpo para mensaje de bienvenida para Establecimientos
    /*String yo = sharedPrefs.getString("seudonimo", sharedPrefs.getString("nombre",""));
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
  		else cuerpo+=".";}*/
    
    if(getIntent().getStringExtra("vienede").equals("a�adir")){
    	ab.setTitle(getString(R.string.addest)); ab.setIcon(R.drawable.social_add_group);
    	if(sharedPrefs.getString("tipo","P").equals("P")) cnae="80100"; else cnae="90100";
     	query= "SELECT ac.* FROM app_company AS ac INNER JOIN categoriaempresa AS ce ON ac.idcompanyapp=ce.idcompanyapp " +
     		"WHERE LOWER(nombre) LIKE LOWER('%"+getIntent().getStringExtra("query")+"%') AND restringepedidos <> 'S' " +
     		"AND activo = true AND cnae='"+cnae+"' AND configura LIKE '%,MCS,%' OR zonainfluencia LIKE '%"+getIntent().getStringExtra("query")+"%' " +
     		"AND restringepedidos <> 'S' AND activo = true AND cnae='"+cnae+"' AND configura LIKE '%,MCS,%'";
     	new BEparalelo().execute();} //Modifica List<Est> establecimientos
    else if(getIntent().getStringExtra("vienede").equals("ocultar")){
    	ab.setTitle(getString(R.string.hidest)); ab.setIcon(R.drawable.social_person_outline);
     	establecimientos = db.getEstablecimientosVisibles();} //prior>=0
    else if(getIntent().getStringExtra("vienede").equals("restaurar")){
     	ab.setTitle(getString(R.string.resest)); ab.setIcon(R.drawable.social_person);
     	establecimientos = db.getEstablecimientosOcultos();} //prior==-1
    ordenado.addAll(establecimientos); orden = 0;
    mostrarLista(establecimientos); /*int i=0;
    for(Est e:establecimientos){ i++;
    	Toast.makeText(getBaseContext(),
    			"Vez "+i+":["+nclientes.get(i)+"] ->"+e.getEid(),Toast.LENGTH_LONG).show();}*/
    // XXX Ordenadores 
   	
   	((ImageView) findViewById(R.id.sortnombre)).setVisibility(View.INVISIBLE);
   	((ImageView) findViewById(R.id.sortcp)).setVisibility(View.INVISIBLE);
   	((TextView)findViewById(R.id.txtnombre)).setOnClickListener(new OnClickListener(){
   		@Override public void onClick(View v) {
   			if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    		ImageView flechaest = (ImageView) findViewById(R.id.sortnombre);
    		Matrix matrix = new Matrix();
    		switch (orden) {
    			default:
    				((ImageView) findViewById(R.id.sortcp)).setVisibility(View.INVISIBLE);
    				flechaest.setVisibility(View.VISIBLE);
    				matrix.postRotate(0f, flechaest.getDrawable().getBounds().width() / 2,
    					flechaest.getDrawable().getBounds().height() / 2);
    				flechaest.setImageMatrix(matrix);
    				Collections.sort(ordenado,new Comparator<Est>() {
    					public int compare(Est e1, Est e2) {
    						return e1.getNombre().compareToIgnoreCase(e2.getNombre()); }});
    				orden = 1; break;
    			case 1:
    				matrix.postRotate(180f, flechaest.getDrawable().getBounds().width() / 2,
    					flechaest.getDrawable().getBounds().height() / 2);
    				flechaest.setImageMatrix(matrix);
    				Collections.sort(ordenado, Collections.reverseOrder(new Comparator<Est>() {
    					public int compare(Est e1, Est e2) {
    						return e1.getNombre().compareToIgnoreCase(e2.getNombre()); }}));
    				orden = 2; break;
    			case 2:
    				flechaest.setVisibility(View.INVISIBLE);
    				orden = 0; ordenado.clear();
    				ordenado.addAll(establecimientos);
    		}  
    		mostrarLista(ordenado); }});
    ((TextView) findViewById(R.id.txtcp)).setOnClickListener(new OnClickListener() {
    	@Override public void onClick(View v) {
    		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    			ImageView flechacp = (ImageView) findViewById(R.id.sortcp);
    			Matrix matrix = new Matrix();
    			switch (orden){
    			default:
    				((ImageView)findViewById(R.id.sortnombre)).setVisibility(View.INVISIBLE);
    				flechacp.setVisibility(View.VISIBLE);
    				matrix.postRotate(0f, flechacp.getDrawable().getBounds().width() / 2,
    					flechacp.getDrawable().getBounds().height() / 2);
    				flechacp.setImageMatrix(matrix);
    				Collections.sort(ordenado,new Comparator<Est>() {
    					public int compare(Est e1, Est e2) {
    						return e1.getZi().compareToIgnoreCase(e2.getZi()); }});
    				orden = 3; break;
    			case 3:
    				matrix.postRotate(180f, flechacp.getDrawable().getBounds().width() / 2,
    					flechacp.getDrawable().getBounds().height() / 2);
    				flechacp.setImageMatrix(matrix);
    				Collections.sort(ordenado, Collections.reverseOrder(new Comparator<Est>() {
    				public int compare(Est e1, Est e2) {
    					return e1.getZi().compareToIgnoreCase(e2.getZi()); }}));
    				orden = 4; break;
    			case 4:
    				flechacp.setVisibility(View.INVISIBLE);
    				orden = 0; ordenado.clear();
    				ordenado.addAll(establecimientos);
    			} mostrarLista(ordenado); }});
    }
    
  @Override public boolean onCreateOptionsMenu(Menu menu){this.menu=menu; return true;}
  @Override public boolean onOptionsItemSelected(MenuItem item) {
  	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    if (item.getItemId() == android.R.id.home) {
        Intent intent = new Intent(ListaEst.this, ListaEstablecimientos.class);
        startActivity(intent); finish(); return true; }
    else if(item.getItemId() == R.id.seleccionar) { 
    	loading = new ProgressDialog(ListaEst.this);
    	if(checkpos.size()>1)
     	loading.setMessage(getString(R.string.dlx));
    	else loading.setMessage(getString(R.string.dl1));
     	loading.setCancelable(false); loading.show();
    	for(Est e : checkpos){
      	if(getIntent().getStringExtra("vienede").equals("a�adir")){
      		db.addEstablecimiento(e);
      		if(db.getEstablecimientosCount()==1)
				sharedPrefs.edit().putString("main", ""+e.getEid()).commit();
    	}else{ if(getIntent().getStringExtra("vienede").equals("ocultar")){
      					e.setPrior(-1); db.updateEstablecimiento(e); }
      				else if(getIntent().getStringExtra("vienede").equals("restaurar")){
      					e.setPrior(0); db.updateEstablecimiento(e); }} }
    	if(getIntent().getStringExtra("vienede").equals("a�adir"))
    		new mensajeNuevoUsuario().execute();
    	else{ Intent i = new Intent(ListaEst.this, ListaEstablecimientos.class);
				startActivity(i); finish();}
    } return true;
  }
    
  @Override public void onBackPressed() {
      super.onBackPressed();   
      Intent intent = new Intent(ListaEst.this, ListaEstablecimientos.class);
      startActivity(intent); finish(); }

	@Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

	@Override public void onNothingSelected(AdapterView<?> parent) {}

	public class Adaptador extends ArrayAdapter<String> {
    	public Adaptador(Context context, int textViewResourceId, String[] objects){
    		super(context, textViewResourceId, objects); }
 
    @Override public View getView(final int position,View convertView,ViewGroup parent){
     	LayoutInflater inflater = ListaEst.this.getLayoutInflater();
      final View row = inflater.inflate(R.layout.lvlistaest, parent, false);
      row.setTag(ordenado.get(position));
      TextView nombre = (TextView)row.findViewById(R.id.nombre);
      nombre.setText(ordenado.get(position).getNombre());
      TextView cp = (TextView)row.findViewById(R.id.cp);
      cp.setText(ordenado.get(position).getZi());
      //XXX Casi el mismo OCL para el CB.
      ((LinearLayout)row.findViewById(R.id.linea)).setOnClickListener(new OnClickListener() {
     	@Override public void onClick(View v) {
     		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
     		((CheckBox)row.findViewById(R.id.cb)).setChecked(
     			!((CheckBox)row.findViewById(R.id.cb)).isChecked());
				if(((CheckBox)row.findViewById(R.id.cb)).isChecked()){
				 	checkpos.add((Est)row.getTag());
				 	for(String s : snclientes){
				 		if(s.split(";")[0].equals(String.valueOf(((Est)row.getTag()).getEid())))
				 			checkclientes.add(s);
				 	}
				 	if(checkpos.size()==1)
				 		ab.setSubtitle(checkpos.size()+" "+getString(R.string.estmar1));
				 	else
				 		ab.setSubtitle(checkpos.size()+" "+getString(R.string.estmarx));
				  menu.clear(); MenuInflater inflater = getMenuInflater();
				  inflater.inflate(R.menu.listaest, menu);
				}else{
					checkpos.remove((Est)row.getTag()); String quita=""; 
					for(String s : checkclientes){
				 		if(s.split(";")[0].equals(String.valueOf(((Est)row.getTag()).getEid())))
				 			quita = s;}
					checkclientes.remove(quita);
					if(checkpos.size()==1)
				 		ab.setSubtitle(checkpos.size()+" "+getString(R.string.estmar1));
				 	else
				 		ab.setSubtitle(checkpos.size()+" "+getString(R.string.estmarx));
				  if(checkpos.isEmpty()){ ab.setSubtitle("");	menu.clear(); }}
		  }});
      ((CheckBox)row.findViewById(R.id.cb)).setOnClickListener(new OnClickListener() {
       	@Override public void onClick(View v) {
       		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
  				if(((CheckBox)row.findViewById(R.id.cb)).isChecked()){
  				 	checkpos.add((Est)row.getTag());
  				 	for(String s : snclientes)
  				 		if(s.split(";")[0].equals(String.valueOf(((Est)row.getTag()).getEid())))
  				 			checkclientes.add(s);
  				 	if(checkpos.size()==1)
  				 		ab.setSubtitle(checkpos.size()+" "+getString(R.string.estmar1));
  				 	else
  				 		ab.setSubtitle(checkpos.size()+" "+getString(R.string.estmarx));
  				  menu.clear(); MenuInflater inflater = getMenuInflater();
  				  inflater.inflate(R.menu.listaest, menu);
  				}else{
  					checkpos.remove((Est)row.getTag()); String quita=""; 
  					for(String s : checkclientes){
  				 		if(s.split(";")[0].equals(String.valueOf(((Est)row.getTag()).getEid())))
  				 			quita = s;}
  					checkclientes.remove(quita);
  					if(checkpos.size()==1)
  				 		ab.setSubtitle(checkpos.size()+" "+getString(R.string.estmar1));
  				 	else
  				 		ab.setSubtitle(checkpos.size()+" "+getString(R.string.estmarx));
  				  if(checkpos.isEmpty()){ ab.setSubtitle("");	menu.clear(); }}
  		  }});
		  for(Est e : checkpos)            	
		  	if(((Est)row.getTag()).getEid()==e.getEid())
		  		((CheckBox)row.findViewById(R.id.cb)).setChecked(true);
		  return row; }
}

  public void mostrarLista(final List<Est> establecimientos){
  	String []nestablecimientos = new String[establecimientos.size()]; int i=0;
  	for(Est e : establecimientos){ nestablecimientos[i]=e.getNombre(); i++; }
    ArrayAdapter<String> adapter=new Adaptador(this,R.layout.lvlistaest,nestablecimientos);
	  lv.setAdapter(adapter);}
//    	setListAdapter(adapter); }
  
  public class aumentaEst extends AsyncTask<String, Void, Boolean> {
  	protected void onPostExecute(final Boolean success) { new descargaLogos().execute(); }
		@Override protected Boolean doInBackground(String... params) {
			//Aumentar contador de clientes de cada Establecimiento
			try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
	    try{DriverManager.setLoginTimeout(20);
			Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st = conn.createStatement(); 
			for(String s : checkclientes)
				st.executeUpdate("UPDATE app_company SET nclientes=nclientes+1 " +
					"WHERE idcompanyapp="+ s.split(";")[0]);
			st.close(); conn.close(); } catch (SQLException e) {e.printStackTrace();}
		return true;
		}}
  
  public class descargaLogos extends AsyncTask<String, Void, Boolean> {//S�lo descarga de nuevos
	  ProgressDialog progress;
  	
  	protected void onPreExecute(){
  		progress = new ProgressDialog(ListaEst.this);
  		progress.setMessage("Descargando logos: ");
  	    progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
  	    progress.setIndeterminate(false);
  	    progress.setMax(checkpos.size());
  	    progress.setCancelable(false);
  	    progress.show();
  	}
  	
    protected void onPostExecute(final Boolean success) {
    	try{if (loading.isShowing()) {loading.dismiss();}}catch(Exception e){}
     	Intent i = new Intent(ListaEst.this, ListaEstablecimientos.class);
			startActivity(i); finish();}

		@Override protected Boolean doInBackground(String... params) {
  	URL url; URLConnection conn;
    BufferedInputStream inStream; BufferedOutputStream outStream;
    File outFile; FileOutputStream fileStream;
    outFile = new File( File.separator + "data" + File.separator + "data" + 
    		File.separator + ListaEst.this.getPackageName() +
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
      runOnUiThread(new Runnable(){public void run(){
			progress.setProgress(progress.getProgress()+1);}});
      }catch(Exception e){} }} return true; }
  }
  
  public class mensajeNuevoUsuario extends AsyncTask<String, Void, Boolean> {
  	protected void onPostExecute(final Boolean success) {new aumentaEst().execute();}
		@Override protected Boolean doInBackground(String... params) {
  //Mensaje
	String yo = sharedPrefs.getString("seudonimo", sharedPrefs.getString("nombre",""));
	if(yo.equals("")) yo = sharedPrefs.getString("nombre","");
	for(String s : checkclientes){
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
			s.split(";")[0]+",'A','E','Sistema')");
		st.close(); conn.close(); } catch (SQLException e) {e.printStackTrace();}
	}
		return true;}
  }
  
  private class BEparalelo extends AsyncTask<String, Void, Boolean> {
  	ProgressDialog loading;
		protected void onPreExecute() {
     	loading = new ProgressDialog(ListaEst.this);
     	loading.setMessage(getString(R.string.beespere));
     	loading.setCancelable(false); loading.show(); }

    protected void onPostExecute(final Boolean success) {
     	if (loading.isShowing()) {loading.dismiss();}
     	ordenado.addAll(establecimientos); orden = 0;
      mostrarLista(establecimientos);}
    
		@Override protected Boolean doInBackground(String... params) {
    try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
    try{DriverManager.setLoginTimeout(20);
		Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
		Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(query);
		while(rs.next()){ int flag = 0;
			for(Est e : db.getAllEstablecimientos())
				if(e.getEid()==rs.getInt(1)) flag=1;
			if(flag==0){
				Est temp = new Est(rs.getInt(1),rs.getInt(18),rs.getString(23),rs.getString(9),
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
				snclientes.add(rs.getInt(1)+";"+rs.getInt(32)); //eid;nclientes
				establecimientos.add(temp);}
		} rs.close(); st.close(); conn.close(); } catch (SQLException e) {e.printStackTrace();}
	return true; }}

}