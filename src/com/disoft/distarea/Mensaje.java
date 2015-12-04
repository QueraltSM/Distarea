package com.disoft.distarea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;*/
import android.support.v7.app.ActionBar;
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.Est;
import com.disoft.distarea.models.Msj;

public class Mensaje extends AppCompatActivity {
	View v, popupView;PopupWindow popupWindow;
	SharedPreferences sharedPrefs; DatabaseHandler db;
	TextView establecimiento, http, fechahora, estado, cuerpo;
	ImageButton responder, borrar, info; Msj m;
	Locale spanish = new Locale("es", "ES"); int flagnoest=0;
	SimpleDateFormat sdfdiashow = new SimpleDateFormat("dd 'de' MM 'de' yyyy",spanish),
			sdfdia = new SimpleDateFormat("yyyy-MM-dd",spanish) ;

	 @Override protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	   ActionBar ab = getSupportActionBar();
	   ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME
	  		 |ActionBar.DISPLAY_HOME_AS_UP);
	   ab.setTitle(getString(R.string.mensajes));
	   if(getIntent().getIntExtra("tipo",0)==1){
	  	 ab.setSubtitle(getString(R.string.menviado)); ab.setIcon(R.drawable.av_upload);
	   }else{
	     ab.setSubtitle(getString(R.string.mrecibido)); ab.setIcon(R.drawable.av_download); }
	   setContentView(R.layout.mensaje);
	   v = findViewById(R.id.base);
	   sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	   SharedPreferences.Editor Ed = sharedPrefs.edit();
			Ed.putInt("counterm",0).commit();
	   db = new DatabaseHandler(this);
	   establecimiento = (TextView) findViewById(R.id.establecimiento);
	   http = (TextView) findViewById(R.id.http);
	   fechahora = (TextView) findViewById(R.id.fechahora);
	   estado = (TextView) findViewById(R.id.estado);
	   cuerpo = (TextView) findViewById(R.id.cuerpo);
	   responder = (ImageButton) findViewById(R.id.responder);
	   responder.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	   borrar = (ImageButton) findViewById(R.id.borrar);
	   borrar.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	   info = (ImageButton) findViewById(R.id.info);
	   info.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	   if(getIntent().getIntExtra("tipo",0)==1){
	  	 m = db.getMensajeEnv(getIntent().getIntExtra("mid", 0));
	  	 //if(db.getEstablecimiento(m.getEid())==null) flagnoest=1;
	  	 String frealiz = null, frec = null;
    	 try{frealiz= sdfdiashow.format(sdfdia.parse(m.getFecharealiz()));
    	 		 frec= sdfdiashow.format(sdfdia.parse(m.getFecharec()));}
    	 catch(Exception e){}
	  	 responder.setVisibility(View.GONE);
	  	 /*if(flagnoest==1)
    		 establecimiento.setText(getString(R.string.enviadoa)
      			 +getString(R.string.messageEstablishment)+m.getEid());
	  	 else*/
	  		 establecimiento.setText(getString(R.string.enviadoa)
	     		+" "+db.getEstablecimiento(m.getEid()).getNombre());
	     if(!m.getFecharealiz().equals("")&&!m.getHorarealiz().equals(""))
	     	 fechahora.setText(getString(R.string.enviado)
	     			+" "+getString(R.string.dia)+" "+frealiz
	        	+" "+getString(R.string.alas)+" "+m.getHorarealiz());
	       if(m.getEstado().equals("P")){
	       	estado.setText(getString(R.string.estado)+" "+getString(R.string.pendiente));
	       	ab.setSubtitle(getString(R.string.messagePending));
	       }else if(m.getEstado().equals("E"))
		       	estado.setText(getString(R.string.estado)+" "+getString(R.string.enviado));
	       else if(m.getEstado().equals("F") 
	       	&& !m.getFecharec().equals("") && !m.getHorarec().equals(""))
	      	 estado.setText(getString(R.string.estado)+" "+getString(R.string.leido)
        		+" "+getString(R.string.dia)+" "+frec
    	      +" "+getString(R.string.alas)+" "+m.getHorarec());
	       else if(m.getEstado().equals("F"))
	       	estado.setText(getString(R.string.estado)+" "+getString(R.string.leido));
	       else
	       	estado.setText(getString(R.string.estado)+" "+getString(R.string.desconocido));
	       }else{
	      	 m = db.getMensajeRec(getIntent().getIntExtra("mid", 0));
	      	 String frec = null, hrec = null;
	      	 try{frec= sdfdiashow.format(sdfdia.parse(m.getFecharec()));}catch(Exception e){};
	      	 if(flagnoest==1 || !db.getEstablecimiento(m.getEid()).getFav()
	      			 || m.getTipomsj().equals("A"))
	      		 responder.setEnabled(false);
	      	 if(m.getRmte().equals(""))
	      		 establecimiento.setText(getString(R.string.mrecibidode)
		      			 +" "+db.getEstablecimiento(m.getEid()).getNombre());
	      	 else
	      		 establecimiento.setText(getString(R.string.mrecibidode)
		      			 +" "+m.getRmte());
	      	 fechahora.setText(getString(R.string.recibido)
	      			 +" "+getString(R.string.dia)+" "+frec
	      			 +" "+getString(R.string.alas)+" "+m.getHorarec());
	        //Guardar en la BBDD como leído
	        	if(m.getTipomsj().equals("A") && m.getEstado().equals("R") ||
	        			m.getTipomsj().equals("G") && m.getEstado().equals("R")){ //Replicar como leído
	        		m.setEstado("L"); db.actualizarMensajeRecibido(m);
	        		new marcarLeido().execute();}
	        	if(m.getEstado().equals("L"))
		        	estado.setText(getString(R.string.leido));
	        }
	        if(!m.getHttp().equals("")) http.setText(" <"+m.getHttp()+">");
	        if(m.getEstado().equals("P")||m.getEstado().equals("E")){ 
	        	http.setVisibility(View.GONE);
		        if(m.getHttp().split("<!>").length>1){
		        	estado.setText(estado.getText()+" ("+m.getHttp().split("<!>").length+
		        			" archivos adjuntos)");
		        }else{if(!m.getHttp().equals(""))
		      			estado.setText(estado.getText()+" (1 archivo adjunto)"); 
		      }}
	        cuerpo.setText(m.getMensaje());
	        cuerpo.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));
			responder.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
				if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
				Intent i = new Intent(Mensaje.this, Mensajes.class);
				i.putExtra("tipo", 2); i.putExtra("eid", m.getEid());
				startActivity(i); finish(); }});
			borrar.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
				if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
				LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
				popupView = layoutInflater.inflate(R.layout.popupsino, null);  
				popupWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
				popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
				popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
				((TextView)popupView.findViewById(R.id.texto)).setText(getString(R.string.borrarmensaje));
				ImageButton si = (ImageButton)popupView.findViewById(R.id.si);
				si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					if(getIntent().getIntExtra("tipo", 0)==1)
						db.deleteMensajeEnviado(db.getMensajeEnv(m.getMid()));
					else
						db.deleteMensajeRecibido(db.getMensajeRec(m.getMid()));
					Intent i = new Intent(Mensaje.this, Mensajes.class);
					i.putExtra("tipo", getIntent().getIntExtra("tipo", 0));
					popupWindow.dismiss(); startActivity(i); finish(); }});
				ImageButton no = (ImageButton)popupView.findViewById(R.id.no);
				no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					popupWindow.dismiss(); }});
			}});
			if(flagnoest==1) info.setEnabled(false);
			else
				info.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					Est e = db.getEstablecimiento(m.getEid());
					Intent i = new Intent(Mensaje.this, Establecimiento.class);
					i.putExtra("tipo",getIntent().getIntExtra("tipo", 0)).putExtra("mid", m.getMid())
					 .putExtra("eid", e.getEid()); startActivity(i); finish(); }});
	 }
	 
	 public class marcarLeido extends AsyncTask<String, Void, Boolean> {
		 protected void onPostExecute(Boolean success){
			 if(success) Toast.makeText(getBaseContext(),
					 "Mensaje marcado como leído.",Toast.LENGTH_LONG).show();}
		 
     @Override protected Boolean doInBackground(String... arg0) {
    	 try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
  		 try{DriverManager.setLoginTimeout(20);
  			Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
  			Statement st = conn.createStatement();
  			st.executeUpdate("UPDATE mensajeapp SET estado='F' WHERE id="+m.getMidbd());
  			st.close();conn.close();}catch(SQLException e){e.printStackTrace(); return false;}
     	return true;}}

	 @Override public boolean onOptionsItemSelected(MenuItem item) {
		 if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	   if (item.getItemId() == android.R.id.home) {
	   	Intent intent = new Intent(Mensaje.this, Mensajes.class);
			intent.putExtra("tipo", getIntent().getIntExtra("tipo",0));
			startActivity(intent); finish(); return true; }
	   return true;} 
	 
	 @Override public void onBackPressed() {
		 super.onBackPressed(); 
	   Intent intent = new Intent(Mensaje.this, Mensajes.class);
	   intent.putExtra("tipo", getIntent().getIntExtra("tipo",0));
	   startActivity(intent); finish();
	 }
}