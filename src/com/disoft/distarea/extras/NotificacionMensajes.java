package com.disoft.distarea.extras;

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
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.support.v7.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.disoft.distarea.Chat;
import com.disoft.distarea.Conversaciones;
import com.disoft.distarea.R;
import com.disoft.distarea.models.Est;
import com.disoft.distarea.models.Msj;

public class NotificacionMensajes extends Service {

	private NotificationManager nm; private static final int ID_NOTIFICACION_CREAR = 1101;
	SharedPreferences sharedPrefs; int counterm=0; Intent intencion; String est, nuevosest="";
	DatabaseHandler db; Locale spanish = new Locale("es", "ES");
	SimpleDateFormat sdfdia = new SimpleDateFormat("yyyy-MM-dd",spanish),
					 sdfhora = new SimpleDateFormat("HH:mm:ss",spanish),
					 sdfhorass = new SimpleDateFormat("HH:mm",spanish);

	@Override public void onCreate() {
		db = new DatabaseHandler(this);
		nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);}
	
	//@SuppressLint("NewApi")
	Handler myHandler = new Handler() {//@SuppressLint("NewApi")
	@Override public void handleMessage(Message msg){
    switch (msg.what){case 0:
    	try{Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), 
  			Uri.parse(sharedPrefs.getString("tono", "")));
  			r.play();} catch (Exception e) {e.printStackTrace();}
    		NotificationManager nm = (NotificationManager) getBaseContext()
    				.getSystemService(Context.NOTIFICATION_SERVICE);
    		//if(android.os.Build.VERSION.SDK_INT >= 11){
    			NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
    			builder.setSmallIcon(R.drawable.content_email_light)
    		         .setLargeIcon(BitmapFactory.decodeResource(
    		        		getResources(), R.drawable.ic_launcher2))
    		         .setWhen(System.currentTimeMillis()).setAutoCancel(true)
    		         .setTicker(getString(R.string.messageNotificationNewMessageOnDistarea));
    			if(sharedPrefs.getBoolean("vibra",true))
    				builder.setVibrate(new long[] { 100, 500, 100, 500, 100, 500, 100 });
    			if(counterm>1){ 
    				intencion = new Intent (NotificacionMensajes.this, Conversaciones.class);
    				PendingIntent intencionPendiente = PendingIntent.getActivity(getBaseContext(),0,intencion, 0);
    				builder.setContentIntent(intencionPendiente)
						 .setContentTitle("Distarea")
						 .setContentText(getString(R.string.messageNotificationHave)+" "+
								 counterm+" "+getString(R.string.messageNotificationNewMessages));
    				Notification n = builder.build();
    				nm.notify(ID_NOTIFICACION_CREAR, n);
    			}else{
    				intencion = new Intent (NotificacionMensajes.this, Chat.class);
  					intencion.putExtra("eid", db.getMensajeRec(db.getLastMidRec()).getEid());
  					PendingIntent intencionPendiente = PendingIntent.getActivity(getBaseContext(),0,intencion, 0);
  					builder.setContentIntent(intencionPendiente)
	 			 	 				 .setContentTitle("Distarea")
	 			 	 				 .setContentText(getString(R.string.messageNotificationNewMessageFrom)+" "+est+".");
  					Notification n = builder.build();
  					nm.notify(ID_NOTIFICACION_CREAR, n);
    			//}}else {
    			/*
    				Notification n = new Notification(R.drawable.content_email_light,
    					"Distarea", System.currentTimeMillis());
    				if(sharedPrefs.getBoolean("vibra",true)==true)
    					n.vibrate = new long[] { 100, 500, 100, 500, 100, 500, 100 };
    				if(counterm>1){
    					intencion = new Intent (NotificacionMensajes.this, Conversaciones.class);
    					PendingIntent intencionPendiente = PendingIntent.getActivity(getBaseContext(),0,intencion, 0);
  						n.setLatestEventInfo(getBaseContext(), "Distarea",
  								getString(R.string.messageNotificationHave)+" "+counterm+" "+
  								getString(R.string.messageNotificationNewMessages), intencionPendiente);
  						nm.notify(ID_NOTIFICACION_CREAR, n);
    				}else{
    					intencion = new Intent (NotificacionMensajes.this, Chat.class);
    					intencion.putExtra("eid", db.getMensajeRec(db.getLastMidRec()).getEid());
    					PendingIntent intencionPendiente = PendingIntent.getActivity(getBaseContext(),0,intencion, 0);
    					n.setLatestEventInfo(getBaseContext(), "Distarea",
    							getString(R.string.messageNotificationNewMessageFrom)+" "+est+".", intencionPendiente);}
    					nm.notify(ID_NOTIFICACION_CREAR, n);}
				}*/
			}
    		//Check Actividad actual
    		/*if(isForeground(getPackageName()+".Conversaciones"))
    			Conversaciones.reload(getBaseContext(),getActivity());*/
    		
          break;
        default: break; }
    }
};

	/*public boolean isForeground(String clase) {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1); 
	    ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
	    return componentInfo.getClassName().equals(clase);
	}*/
	
	private class CMparalelo extends AsyncTask<String, Void, Boolean> {
		protected void onPostExecute(Boolean success){
			Log.e("COUNTERM",""+counterm);
			Log.e("PREFERENCIA",""+sharedPrefs.getInt("counterm", 0));
			if(counterm>sharedPrefs.getInt("counterm",0)){ 
				Log.e("CREA","NOTIFICACION");
				myHandler.sendEmptyMessage(0);
				SharedPreferences.Editor Ed = sharedPrefs.edit();
				Ed.putInt("counterm",counterm).commit(); }
			//else sharedPrefs.edit().putInt("counterm",0).commit();
			
			if(!nuevosest.equals("")){
				new descargaLogos().execute();
			}}
		
		@Override protected Boolean doInBackground(String... params) {
  SharedPreferences.Editor spe = sharedPrefs.edit();
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
					"AND idestablecimiento=" + sharedPrefs.getInt("usuarioapp", 0) +
					" AND clienteglobal<>"+sharedPrefs.getInt("id", -1); }
		ResultSet rs = st.executeQuery(sql);
		if(rs.next()){ nuevos= rs.getInt(1); counterm += rs.getInt(1); }
	rs.close(); st.close(); conn.close(); }catch (SQLException e){e.printStackTrace();}
	if(nuevos>0){
		Log.e("NUEVOS",""+nuevos);
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
				"hastafecha,remitente FROM mensajeapp WHERE id>"+sharedPrefs.getInt("lastmid", 0) +
				" AND tipomensaje<>'A' AND zonainfluencia LIKE '%" + sharedPrefs.getString("cp","") + 
				"%' OR id>"+sharedPrefs.getInt("lastmid", 0) + " AND tipomensaje<>'A'" + 
				" AND clienteglobal=" + sharedPrefs.getInt("id", -1) + " OR id>" +
				sharedPrefs.getInt("lastmid", 0)+" AND tipomensaje='A' AND idestablecimiento=" +
				sharedPrefs.getInt("usuarioapp", 0) +" ORDER BY id ASC");
		while(rs.next()){
			//Posibles nulls
			String http="", tipo="", desde="", hasta="", rmte="";
			if(rs.getString(4)!=null) http=rs.getString(4);
			if(rs.getString(8)!=null) tipo=rs.getString(8);
			if(rs.getString(10)!=null) desde=rs.getDate(10).toString();
			if(rs.getString(11)!=null) hasta=rs.getTime(11).toString();
			if(rs.getString(12)!=null) rmte=rs.getString(12);
			//Guardo siempre el mid, para que vaya sobreescribiendo con el último
			if(rs.getInt(1)>sharedPrefs.getInt("lastmid",0))
				spe.putInt("lastmid", rs.getInt(1));
			/*if(db.getEstablecimiento(rs.getInt(7))!=null)
				est = db.getEstablecimiento(rs.getInt(7)).getNombre();
			else est = rs.getString(12);*/
			est = rmte;
			if(tipo.equals("M")){ //Si es masivo, decrementar el contador "numeroenvios"
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
						sdfdia.format(new Date()),sdfhora.format(new Date()),"R",rmte,rs.getInt(1),"");
					db.recibirMensaje(temporal);
			}}else{
				String frec=sdfdia.format(new Date()), 
						hrec=sdfhora.format(new Date());
				Msj temporal = new Msj(db.getLastMidRec()+1,rs.getInt(2),rs.getString(3),
						http,rs.getDate(5).toString(),rs.getTime(6).toString(),"",
					rs.getInt(7),tipo,desde,hasta,frec,hrec,"R",rmte,rs.getInt(1),"");
				db.recibirMensaje(temporal);
				//Avisar recepción mensaje
				try {Statement st2 = conn.createStatement();
					st2.executeUpdate("UPDATE mensajeapp SET fecharecepcion='"+frec+
							"' , horarecepcion='"+hrec+"' WHERE id="+rs.getInt(1));
					//Antes cambiaba el estado a leído aquí.
					st2.close();}catch (SQLException e){e.printStackTrace();} }
			if(db.getEstablecimiento(rs.getInt(7))==null){
				//Si no existe el Establecimiento, apuntarlo.
				nuevosest+=","+rs.getInt(7);
			}}
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
				"hastafecha,remitente FROM mensajeapp WHERE id>"+sharedPrefs.getInt("lastmid", 0) +
				" AND tipomensaje<>'A' AND zonainfluencia LIKE '%" + sharedPrefs.getString("cp","") + 
				"%' OR id>"+sharedPrefs.getInt("lastmid", 0) + " AND tipomensaje<>'A'" + 
				" AND clienteglobal=" + sharedPrefs.getInt("id", -1) + " ORDER BY id ASC");
		while(rs3.next()){
			//Posibles nulls
			String http="", tipo="", desde="", hasta="", rmte="";
			if(rs3.getString(4)!=null) http=rs3.getString(4);
			if(rs3.getString(8)!=null) tipo=rs3.getString(8);
			if(rs3.getString(10)!=null) desde=rs3.getDate(10).toString();
			if(rs3.getString(11)!=null) hasta=rs3.getTime(11).toString();
			if(rs3.getString(12)!=null) rmte=rs3.getString(12);
			//Guardo siempre el mid, para que vaya sobreescribiendo con el último
			if(rs3.getInt(1)>sharedPrefs.getInt("lastmid",0))
				spe.putInt("lastmid", rs3.getInt(1));
			String frec=new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),
						 hrec=new SimpleDateFormat("HH:mm:ss",spanish).format(new Date());
			Msj temporal = new Msj(db.getLastMidRec()+1,rs3.getInt(2),rs3.getString(3),
					http,rs3.getDate(5).toString(),rs3.getTime(6).toString(),"",
				rs3.getInt(7),tipo,desde,hasta,frec,hrec,"R","*"+rmte,rs3.getInt(1),"");
			db.recibirMensaje(temporal);
			//Avisar recepción mensaje
			try {Statement st4 = conn.createStatement();
				st4.executeQuery("UPDATE mensajeapp SET fecharecepcion='"+frec+
						"' , horarecepcion='"+hrec+/*"' , estado='F*/"' WHERE id="+rs3.getInt(1));
				//Me salto el estado, porque aún no lo ha abierto
				st4.close(); conn.close();}catch (SQLException e){e.printStackTrace();}
			if(db.getEstablecimiento(rs3.getInt(7))==null)
				//Si no existe el Establecimiento, apuntarlo.
				nuevosest+=","+rs3.getInt(7);
		}
		}catch (SQLException e){e.printStackTrace();}
		}
	//Descargar Establecimientos nuevos:
			if(!nuevosest.equals("")){
			try {conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st5 = conn.createStatement();
				ResultSet rs5 = st5.executeQuery("SELECT "+getString(R.string.camposest)+
					", textcat_all(cnae || ',') FROM app_company, categoriaempresa WHERE " +
					"app_company.idcompanyapp=categoriaempresa.idcompanyapp AND " +
					"app_company.idcompanyapp IN (" + nuevosest.substring(1) + ") GROUP BY "+getString(R.string.camposest));
			while(rs5.next()) {
				db.addEstablecimiento(new Est(rs5.getInt(1),rs5.getInt(2),rs5.getString(3),rs5.getString(4),
						rs5.getString(5),rs5.getString(6),"",rs5.getBoolean(7),true,
						1,0.0f,rs5.getString(8),rs5.getString(9),rs5.getString(10),rs5.getString(11),
						rs5.getString(12),rs5.getString(13),rs5.getString(14),rs5.getString(15),0,
						rs5.getString(16),rs5.getString(17),"",rs5.getString(18),rs5.getString(19),
						rs5.getString(20),rs5.getString(21),"0",rs5.getString(22),rs5.getString(23)));
				if(db.getEstablecimientosCount()==1)
					sharedPrefs.edit().putString("main", ""+rs5.getInt(1)).commit();
			} rs5.close(); st5.close();
			}catch(Exception e){e.printStackTrace();}
			//Marcar invitaciones como Aceptadas
			try {conn = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st6 = conn.createStatement();
			for(int i=0;i<nuevosest.length();i++){
				Log.e("NUEVOSEST",""+nuevosest.split(",")[i+1]);
				st6.executeQuery("UPDATE invitacliente SET (estado, fechaok, horaok) = "+
				"('A','"+sdfdia.format(new Date())+"','"+sdfhorass.format(new Date())+"') WHERE "+
				"company="+nuevosest.split(",")[i+1]+" AND cliente="+sharedPrefs.getInt("id", 0));
			} st6.close(); }catch(Exception e){e.printStackTrace();}
			
	}//Fin si nuevos>0
	// Comprobar estado de mensajes enviados
			if(!db.getAllMensajesEnv().isEmpty()){
			Connection conn3;
			try{DriverManager.setLoginTimeout(20);
			conn3 = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st3 = conn3.createStatement();
			ResultSet rs2 = st3.executeQuery("SELECT idmensajeappmovil,estado FROM mensajeapp " +
					"WHERE clienteglobal="+sharedPrefs.getInt("id",-1)+" AND idmensajeappmovil>0 " +
					"AND tipomensaje='A' AND estado='F'");
			while(rs2.next())  for(Msj m : db.getAllMensajesEnv())
				if(m.getMid() == rs2.getInt(1)) { if(!m.getEstado().equals(rs2.getString(2))){ 
					m.setEstado(rs2.getString(2)); db.actualizarMensajeEnviado(m);
				}continue;}
			rs2.close(); st3.close(); conn3.close();}catch (SQLException e){e.printStackTrace();}}
	}return true; }}
	
	@Override public int onStartCommand(Intent intenc, int flags, int idArranque) {
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		if(sharedPrefs.getInt("internetmode",0)==0){
		counterm=sharedPrefs.getInt("counterm",0);
		//Toast.makeText(getBaseContext(), getString(R.string.messageNotificationSearching), Toast.LENGTH_LONG).show();
		//counterm = sharedPrefs.getInt("counterm", 0);
		new CMparalelo().execute();
		if(myHandler.obtainMessage(0) != null)//{
			return START_STICKY;//}
		else return START_NOT_STICKY;
		}else{ stopSelf(); return START_NOT_STICKY; }
	}

	/*@Override public void onDestroy() {
		Log.e("LLAMANDO","A REPETIR");
		sharedPrefs.edit().putInt("counterm",0).commit();
		nm.cancel(ID_NOTIFICACION_CREAR);
		// Schedule the alarm!
		PendingIntent receiver = PendingIntent.getService(this, 0, 
				new Intent(this, NotificacionMensajes.class), 0);
		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
				SystemClock.elapsedRealtime(), 30000, receiver);}*/

	@Override public IBinder onBind(Intent i){return null;}
	
	public class descargaLogos extends AsyncTask<String, Void, Boolean> {//Sólo descarga de nuevos

    protected void onPostExecute(final Boolean success) {}

		@Override protected Boolean doInBackground(String... params) {
  	URL url; URLConnection conn;
    BufferedInputStream inStream; BufferedOutputStream outStream;
    File outFile; FileOutputStream fileStream;
    outFile = new File( File.separator + "data" + File.separator + "data" + 
    		File.separator + NotificacionMensajes.this.getPackageName() +
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
}