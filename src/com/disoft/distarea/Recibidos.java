package com.disoft.distarea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/
import android.support.v7.app.ActionBar;
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.Msj;

public class Recibidos extends Fragment {
	Locale spanish = new Locale("es", "ES");
	SharedPreferences sharedPrefs; DatabaseHandler db;
	View view; TableLayout tl; int par, nuevos=0;
	TableRow fila; TextView recibidode, fechahora, estado;
	FragmentActivity sfa; SharedPreferences.Editor spe;
	
	@Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
		sfa = getActivity();
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(sfa.getBaseContext());
		SharedPreferences.Editor Ed = sharedPrefs.edit();
		Ed.putInt("counterm",0).commit();
		spe = sharedPrefs.edit(); db = new DatabaseHandler(sfa);
		tl = (TableLayout) view.findViewById(R.id.tl); par=0;
		//Recoger Mensajes de la BBDD
		if(sfa.getIntent().getBooleanExtra("refresh",false)) {
			new CargaMensajes().execute(); sfa.getIntent().removeExtra("refresh"); }
		//Mostrar Mensajes Recibidos
		if(db.getAllMensajesRec().size()==0){
			sfa.runOnUiThread(new Runnable(){public void run(){
				((ScrollView) view.findViewById(R.id.sv)).setVisibility(View.GONE);
				((TextView) view.findViewById(R.id.leyenda)).setVisibility(View.GONE);
				TextView norecibidos = new TextView(sfa.getBaseContext());
				norecibidos.setText(getString(R.string.norecibidos));
				norecibidos.setTextColor(Color.BLACK); norecibidos.setGravity(17);
//				norecibidos.setTextAppearance(sfa.getBaseContext(), android.R.attr.textAppearanceLarge);
				((LinearLayout) view.findViewById(R.id.base)).addView(norecibidos); }});
		}
		if(db.getEstablecimientosCount()!=0){
			int nl=0;
			for(final Msj m : db.getAllMensajesRec()){
				fila = new TableRow(sfa.getBaseContext());
				recibidode = new TextView(sfa.getBaseContext());
				fechahora = new TextView(sfa.getBaseContext());
				estado = new TextView(sfa.getBaseContext());
				ImageButton ver = new ImageButton(sfa.getBaseContext());
				ver.setImageDrawable(getResources().getDrawable(R.drawable.content_unread));
				if(/*m.getRmte()==null || */m.getRmte().equals(""))
					recibidode.setText(db.getEstablecimiento(m.getEid()).getNombre());
				else recibidode.setText(m.getRmte()); 
				/*if(db.getEstablecimiento(m.getEid())==null)
					recibidode.setText("Establecimiento "+m.getEid());
				else
					recibidode.setText(db.getEstablecimiento(m.getEid()).getNombre());*/
				recibidode.setTextColor(Color.BLACK);
				try {fechahora.setText(new SimpleDateFormat("dd-MM-yyyy",spanish)
					.format(new SimpleDateFormat("yyyy-MM-dd",spanish)
					.parse(m.getFecharealiz()))+" "+m.getHorarealiz());
				} catch (ParseException e) {e.printStackTrace();}
				fechahora.setTextColor(Color.BLACK);
				if(m.getEstado().equals("P"))
					estado.setText("[ "+Html.fromHtml("&#8230")+"]");
				else if(m.getEstado().equals("R")){
					estado.setText("[  ! ]");
					recibidode.setTypeface(Typeface.DEFAULT_BOLD);
					fechahora.setTypeface(Typeface.DEFAULT_BOLD);
					estado.setTypeface(Typeface.DEFAULT_BOLD);
					nl++;
				}else if(m.getEstado().equals("L")){
					estado.setText("["+Html.fromHtml("&#10003")+"]");
					ver.setImageDrawable(getResources().getDrawable(R.drawable.content_read));}
				else estado.setText("[  ?  ]");
				estado.setTextColor(Color.BLACK);
				estado.setGravity(17);
				//ver.setTag(m);
				ver.setBackgroundDrawable(getResources().getDrawable(R.drawable.botondisoft));
				ver.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
				ver.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					Intent i = new Intent(sfa, Mensaje.class);
					i.putExtra("tipo", 0).putExtra("mid", m.getMid());
					startActivity(i); sfa.finish();
				}});
				TableRow.LayoutParams lpr= new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
				lpr.weight=0.5f;
				TableRow.LayoutParams lpf= new TableRow.LayoutParams(
						TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
				lpf.weight=0.3f;
				TableRow.LayoutParams lp= new TableRow.LayoutParams(
						TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
				lp.weight=0.1f;
				fila.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT); 
				fila.addView(recibidode,lpr); fila.addView(fechahora,lpf);
				fila.addView(estado,lp); fila.addView(ver,lp);
				if(par==0){ par=1;
					fila.setBackgroundColor(getResources().getColor(R.color.azulDisoft3)); }
				else{ par=0;
					fila.setBackgroundColor(getResources().getColor(R.color.azulDisoft2)); }
				sfa.runOnUiThread(new Runnable(){public void run(){tl.addView(fila);}});
			}
			//Contador de mensajes sin leer
			/*if(nl>0){ final int outnl = nl;
				sfa.runOnUiThread(new Runnable(){public void run(){
				 sfa.getSupportActionBar().getTabAt(0).setText(getString(R.string.recibidos)+"("+outnl+")");
				 sfa.getSupportActionBar().setSubtitle(getString(R.string.recibidos)+"("+outnl+")"); }}); }
			else{
				sfa.runOnUiThread(new Runnable(){public void run(){
				 sfa.getSupportActionBar().getTabAt(0).setText(getString(R.string.recibidos));
				 sfa.getSupportActionBar().setSubtitle(getString(R.string.recibidos)); }});
			}*/
		}
	}
 
    //@Override public SherlockFragmentActivity getSherlockActivity(){return super.getSherlockActivity();}
    //@Override public void onViewCreated(View view, Bundle savedInstanceState){super.onViewCreated(view, savedInstanceState);}
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.recibidos, container, false);
        return view;}
    @Override public void onSaveInstanceState(Bundle outState){ super.onSaveInstanceState(outState);
    	setUserVisibleHint(true); }
    
    //Clase asíncrona XXX
    public class CargaMensajes extends AsyncTask<String, Void, Boolean> {
      public ProgressDialog loading;
        
      protected void onPreExecute() {
      	loading = new ProgressDialog(getActivity());
      	loading.setMessage(getString(R.string.cmen));
      	loading.setCancelable(false); loading.show(); }

      protected void onPostExecute(final Boolean success) {
        if (loading.isShowing()) {loading.dismiss();}
        //sfa.getParent().findViewById(R.id.pager);
        Enviados env = (Enviados) sfa.getSupportFragmentManager()
            .findFragmentByTag("android:switcher:"+R.id.pager+":1");
        env.actualizaEstado(); Intent i = new Intent(sfa, Mensajes.class);
        i.putExtra("tipo", sfa.getIntent().getExtras().getInt("tipo",0));
        startActivity(i); sfa.finish();}

      @Override protected Boolean doInBackground(String... arg0) {
      	try {Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
      	Connection conn; int nuevos=0, flagest=0;
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
								"AND idestablecimiento=" + sharedPrefs.getInt("usuarioapp", 0); }
					ResultSet rs = st.executeQuery(sql);
					if(rs.next()) nuevos=rs.getInt(1);
					rs.close(); st.close(); conn.close();
			}catch (SQLException e){e.printStackTrace();}
      	if(nuevos>0){
			try{DriverManager.setLoginTimeout(20);
				conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st = conn.createStatement();
				ResultSet rs;
				if(flagest==0)
					rs = st.executeQuery("SELECT id,clienteglobal,mensaje,http,fecharealizacion," +
						"horarealizacion,idestablecimiento,tipomensaje,numeroenvios,desdefecha," +
						"hastafecha,remitente FROM mensajeapp WHERE id>"+sharedPrefs.getInt("lastmid", 0) +
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
							Msj temporal = new Msj(db.getLastMidRec()+1,rs.getInt(2),rs.getString(3),
									http,rs.getDate(5).toString(),rs.getTime(6).toString(),"",
								rs.getInt(7),rs.getString(8),rs.getDate(10).toString(),rs.getTime(11).toString(),
								new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),
								new SimpleDateFormat("HH:mm:ss",spanish).format(new Date()),"R",rmte,rs.getInt(1),xml);
							db.recibirMensaje(temporal);
				}}else{
						String frec=new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),
									 hrec=new SimpleDateFormat("HH:mm:ss",spanish).format(new Date());
						Msj temporal = new Msj(db.getLastMidRec()+1,rs.getInt(2),rs.getString(3),
								http,rs.getDate(5).toString(),rs.getTime(6).toString(),"",
							rs.getInt(7),tipo,desde,hasta,frec,hrec,"R",rmte,rs.getInt(1),xml);
						db.recibirMensaje(temporal);
						//Avisar recepción mensaje
						try {Statement st2 = conn.createStatement();
							st2.executeQuery("UPDATE mensajeapp SET fecharecepcion='"+frec+
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
				String http="", tipo="", desde="", hasta="", rmte="",xml="";
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
					"AND tipomensaje='A' AND estado<>'E'");
			while(rs2.next())  for(Msj m : db.getAllMensajesEnv())
				if(m.getMid() == rs2.getInt(1)) { if(!m.getEstado().equals(rs2.getString(2))){ 
					m.setEstado(rs2.getString(2)); db.actualizarMensajeEnviado(m);
				}continue;}
			rs2.close(); st3.close(); conn3.close();}catch (SQLException e){e.printStackTrace();}}
			return true;}}
}