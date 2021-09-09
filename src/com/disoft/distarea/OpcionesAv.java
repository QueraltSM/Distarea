package com.disoft.distarea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;*/
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.CliF;
import com.disoft.distarea.models.Est;
import com.disoft.distarea.models.Ped;

public class OpcionesAv extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	SharedPreferences sharedPrefs; LayoutInflater layoutInflater; String est, codemppub, claveemp;
	Preference redescargaclif;
	View v, popupView; PopupWindow popupWindow; DatabaseHandler db; Locale spanish = new Locale("es", "ES");
	SimpleDateFormat postgrestyle = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",spanish);
	private AppCompatDelegate delegado;

	@Override protected void onCreate(Bundle savedInstanceState) {
		getDelegado().installViewFactory();
		getDelegado().onCreate(savedInstanceState);
	      super.onCreate(savedInstanceState);
	      ActionBar ab = getDelegado().getSupportActionBar(); db = new DatabaseHandler(this);
	      ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME
	        |ActionBar.DISPLAY_HOME_AS_UP);
	      ab.setTitle("Configuración"); ab.setSubtitle(getString(R.string.optionsAvAdvancedOptions));
	      ab.setIcon(R.drawable.action_settings);
	      addPreferencesFromResource(R.xml.opcionesav);
	      getDelegado().setContentView(R.layout.opciones);
	      findViewById(R.id.info).setVisibility(View.GONE);
	      v = findViewById(R.id.base);
	      sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	      
	      redescargaclif = findPreference("redescargaclif");
	      if(redescargaclif!=null){
	      redescargaclif.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	    	  @Override public boolean onPreferenceClick(Preference preference) {
	    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    		  if(isNetworkAvailable()){
	    		  List<CliF> clientesAV =  db.getAllClientesF(sharedPrefs.getInt("solicitacliest", 0));
	    		  for(CliF c : clientesAV) db.deleteClienteF(c.getIdcf());
	    		  new descargaClientes().execute();
	    		  return true;
	    		  }else return false;
	    	  }});
	      }
	      
	      Preference borrest = findPreference("borrest");
	      borrest.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	    	  @Override public boolean onPreferenceClick(Preference preference) {
	    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    		  LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
      			popupView = layoutInflater.inflate(R.layout.popupsino, null);  
      			popupWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
      			popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
      			popupWindow.showAtLocation(findViewById(R.id.base), Gravity.CENTER, 0, 0);
      			((TextView) popupView.findViewById(R.id.texto)).setText(getString(R.string.optionsAvWarning));
      			ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
    		  	ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
    		  	si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
    		  	no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
    		  	si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
    		  		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    		  		popupWindow.dismiss();
	    		  for(Est e : db.getAllEstablecimientos()){
	    		  	if(e.getEid()<0) continue;
	    		  	else{
	    		  		for(Ped p : db.getAllPedidos(e.getEid()))
	    		  			db.deletePedido(p);
	    		  		db.deletePedidoAnt(e.getEid());
	    		  		db.deleteEstablecimiento(e);
	    		  }} Toast.makeText(getBaseContext(), getString(R.string.optionsAvAllDeleted), Toast.LENGTH_LONG).show(); 
    		  	}});
    		  	no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
    		  		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    		  		popupWindow.dismiss(); }});
	    		  return true;
	    	  }});
	      
	      Preference myPref = findPreference("resetvcdate");
	      myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	    	  @Override public boolean onPreferenceClick(Preference preference) {
	    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    		  SharedPreferences.Editor spe = sharedPrefs.edit();
	    		  spe.putString("vcdate", ""); spe.commit();
	    		  for(Est e : db.getAllEstablecimientos())
	    		  	if(e.getEid()>0 && e.getPrior()==2){
	    		  		e.setPrior(1); db.updateEstablecimiento(e); 
	    		  	}
	    		  Toast.makeText(getBaseContext(), getString(R.string.optionsAvMessageReseted), Toast.LENGTH_LONG).show();
	    		  return true;
	    	  }});
	      
	      Preference descargacnae = findPreference("descargacnae");
	      descargacnae.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	    	  @Override public boolean onPreferenceClick(Preference preference) {
	    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    		  db.onUpgrade(db.getReadableDatabase(), 5, 6);
	    		  //Toast.makeText(getBaseContext(), getString(R.string.optionsAvMessageReseted), Toast.LENGTH_LONG).show();
	    		  return true;
	    	  }});
	      
	      final CheckBoxPreference solicitacli = (CheckBoxPreference) findPreference("solicitacli");
	      if(sharedPrefs.getInt("solicitacliest",0)==0 ||
	    		  sharedPrefs.getString("solicitaclinom", "") == null ||
    			  sharedPrefs.getString("solicitaclinom", "").equals(""))
    		  solicitacli.setChecked(false);
	      if(sharedPrefs.getBoolean("solicitacli", false))
	    	  solicitacli.setSummaryOn("Conectado a "+db.getEstablecimiento(
	    			  sharedPrefs.getInt("solicitacliest",0)).getNombre()+" como "+
	    			  sharedPrefs.getString("solicitaclinom", "")+".");
	      solicitacli.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	    	  @Override public boolean onPreferenceClick(Preference preference) {
	    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    		  //if(sharedPrefs.getInt("solicitaclin",0)>0){
	    		  if(!sharedPrefs.getBoolean("solicitacli",false)){
	    			  List<CliF> clientesAV =  db.getAllClientesF(sharedPrefs.getInt("solicitacliest", 0));
	    			  SharedPreferences.Editor spe = sharedPrefs.edit();
	    			  if(!sharedPrefs.getString("main", "0").equals(""+sharedPrefs.getInt("solicitacliest",0)))
							spe.putString("main", "0");
						spe.putInt("solicitaclin", 0) //ClaveOP, pasar a String
						.putString("solicitaclinom", "") //NombreOP
						.putString("solicitacliref", "") //CodFacdisOP
						.putString("lastdateclientesf", "1970-01-01 00:00:00") //FechaUpdateClientesF, porque ahora se limpian
						.putInt("solicitacliest", 0).commit(); //EstOP
						for(CliF c : clientesAV) db.deleteClienteF(c.getIdcf());
						//getPreferenceScreen().removePreference(findPreference("redescargaclif"));
						redescargaclif.setEnabled(false);
	    			  solicitacli.setChecked(false); return true;
	    		  }else{
	    			  AlertDialog.Builder builder = new AlertDialog.Builder(OpcionesAv.this);
	    			  /*final EditText codigoempleado = (EditText) getLayoutInflater().inflate(R.layout.edittext, null);
	    					  		 clave = (EditText) getLayoutInflater().inflate(R.layout.edittext, null);*/
	    			  final EditText codigoempleado = (EditText)getLayoutInflater().inflate(R.layout.edittext, null),
	    					  		 clave = (EditText)getLayoutInflater().inflate(R.layout.edittext, null);
	    			  clave.setSingleLine();
	    			  if(sharedPrefs.getInt("solicitaclin",0)!=0)
	    				  codigoempleado.setText(String.valueOf(sharedPrefs.getInt("solicitaclin",0)));
	    			  codigoempleado.setInputType(InputType.TYPE_CLASS_NUMBER);
	    			  //codigoempleado.setBackgroundDrawable(getResources().getDrawable(R.drawable.apptheme_edit_text_holo_dark));
	    			  //codigoempleado.setTextColor(getResources().getColor(android.R.color.black));
	    			  /*codigoempleado.setBackgroundDrawable(getResources().getDrawable(R.drawable.apptheme_edit_text_holo_dark));
	    			  clave.setBackgroundDrawable(getResources().getDrawable(R.drawable.apptheme_edit_text_holo_dark));*/
	    			  //clave.setTextColor(getResources().getColor(android.R.color.black));
	    			  builder.setPositiveButton("Comprobar",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
	    		            }});
	    			  builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
	    		            public void onClick(DialogInterface dialog, int whichButton) {
	    		            	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    		                solicitacli.setChecked(false); dialog.dismiss();
	    		            }});
	    				 builder.setTitle("Conectar con su empresa");
	    				 LinearLayout campos = new LinearLayout(getBaseContext());
	    				 LinearLayout linea1 = new LinearLayout(getBaseContext());
	    				 TextView textolinea1 = new TextView(getBaseContext());
	    				 campos.setOrientation(LinearLayout.VERTICAL);
	    				 textolinea1.setTextColor(getResources().getColor(android.R.color.black));
	    				 textolinea1.setText("C\u00F3digo de empleado:");
	    				 textolinea1.setGravity(Gravity.RIGHT);
	    				 LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(
	    						LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	    					lp.weight=1f;
	    				 linea1.addView(textolinea1,lp);
	    				 linea1.addView(codigoempleado,lp);
	    				 campos.addView(linea1);
	    				 LinearLayout linea2 = new LinearLayout(getBaseContext());
	    				 TextView textolinea2 = new TextView(getBaseContext());
	    				 textolinea2.setTextColor(getResources().getColor(android.R.color.black));
	    				 textolinea2.setText("Clave:");
	    				 textolinea2.setGravity(Gravity.RIGHT);
	    				 linea2.addView(textolinea2,lp);
	    				 linea2.addView(clave,lp);
	    				 campos.addView(linea2);
	    				 builder.setView(campos);
	    				 builder.setCancelable(false);
	    				 final AlertDialog dialog = builder.create();
	    				 dialog.show();
	    				 dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
	    					 @Override public void onClick(View v) {
	    						 if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    						if(codigoempleado.getText().toString().equals(""))
		    		            	Toast.makeText(getBaseContext(), getString(R.string.introduzcacodigo), Toast.LENGTH_LONG).show();
		    		            else{
		    		           		//Consulta SQL asesoría válida
		    		            	codemppub = codigoempleado.getText().toString().trim();
		    		            	claveemp = clave.getText().toString().trim();
		    		            	codigoempleado.setId(R.id.codigoempleadoId);
		    		            	clave.setId(R.id.claveId);
		    		            	new compruebaCliente(solicitacli,dialog).execute();
		    		            	}
	    				     }});
	    				 
	    					final Button b = dialog.getButton(Dialog.BUTTON_POSITIVE);
	    					clave.setOnKeyListener(new View.OnKeyListener(){
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
	    				 return true;
	    		  }
	    	  }});
	      
	      if(solicitacli.isChecked())
	    	  //getPreferenceScreen().addPreference(findPreference("redescargaclif"));
	    	  redescargaclif.setEnabled(true);
	      else //getPreferenceScreen().removePreference(findPreference("redescargaclif"));
	    	  redescargaclif.setEnabled(false);
	      
	      /*Preference syncEst = findPreference("syncest");
	      syncEst.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	    	  @Override public boolean onPreferenceClick(Preference preference) {
	    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    		  Toast.makeText(getBaseContext(), 
	  						getString(R.string.optionsAvSearchingYourUser), 
	  	    			Toast.LENGTH_LONG).show();
	    		  new syncEst().execute();//Buscar en Est mi nombre
	    		  if(sharedPrefs.getInt("syncest", 0)!=0)
	    		  Toast.makeText(getBaseContext(), 
	  						getString(R.string.optionsAvSyncMessages)+est+".", 
	  	    			Toast.LENGTH_LONG).show();
	    		  return true;
	    	  }});*/
	      
	      /*Preference myPref = findPreference("opciones2");
	      myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	    	  @Override public boolean onPreferenceClick(Preference preference) {
	    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    		  //
	    		  return true;
	    	  }});*/
	      
	}
	
	/*private class syncEst extends AsyncTask<String, Void, Boolean> {
		protected void onPostExecute(Boolean success){
			if(sharedPrefs.getInt("syncest", 0)!=0)
  		  Toast.makeText(getBaseContext(), 
						getString(R.string.optionsAvSyncMessages)+est+".", 
	    			Toast.LENGTH_LONG).show();}
		
		@Override protected Boolean doInBackground(String... params) {
			try {Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
			try{DriverManager.setLoginTimeout(20);
				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT idcompanyapp,nombre FROM app_company WHERE " +
				"usuarioapp LIKE '%" + sharedPrefs.getString("nombre", "") + "%'");
				if(rs.next()){ est = rs.getString(2);
					SharedPreferences.Editor spe = sharedPrefs.edit();
					spe.putInt("syncest", rs.getInt(1)); spe.commit(); }
			rs.close(); st.close(); conn.close(); }catch (SQLException e){e.printStackTrace();}
			//Añade Establecimiento si existe
			if(sharedPrefs.getInt("syncest",0)!=0)
				if(db.getEstablecimiento(sharedPrefs.getInt("syncest",0))==null){
				try{DriverManager.setLoginTimeout(20);
				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM app_company " +
					"WHERE idcompanyapp="+sharedPrefs.getInt("syncest",0));
				while(rs.next()) {
					if(db.getEstablecimientosCount()==0)
						db.addEstablecimiento(new Est(rs.getInt(1),rs.getInt(18),rs.getString(23),
							rs.getString(9),rs.getString(8),rs.getString(6),"",rs.getBoolean(19),
							true,1,0.0f,rs.getString(10),rs.getString(11),rs.getString(12),
							rs.getString(13),rs.getString(14),rs.getString(15),rs.getString(4),
							rs.getString(2),0,rs.getString(27),rs.getString(7),"",rs.getString(29),
							rs.getString(30),rs.getString(31),rs.getString(33),0));
					else {
						int flag = 0;
						for(Est e : db.getAllEstablecimientos())
							if(e.getEid()==rs.getInt(1)) flag=1;
							if(flag==0)
								db.addEstablecimiento(new Est(rs.getInt(1),rs.getInt(18),rs.getString(23),
									rs.getString(9),rs.getString(8),rs.getString(6),"",rs.getBoolean(19),
									true,1,0.0f,rs.getString(10),rs.getString(11),rs.getString(12),
									rs.getString(13),rs.getString(14),rs.getString(15),rs.getString(4),
									rs.getString(2),0,rs.getString(27),rs.getString(7),"",rs.getString(29),
									rs.getString(30),rs.getString(31),rs.getString(33),0));
					}
				} rs.close(); st.close();}catch(Exception e){}}
			return true;
		}}*/
	
	@Override public void onDestroy(){super.onDestroy(); if(popupWindow != null) popupWindow.dismiss();}
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, ListaCompra.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); return true;}
        return true;}
	
	@Override public void onBackPressed() {
	    super.onBackPressed();   
	    Intent intent = new Intent(this, ListaCompra.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
	}

	@Override public void onSharedPreferenceChanged(SharedPreferences sp, String s) {}
	
	private class compruebaCliente extends AsyncTask<String, Void, Boolean> {
		ProgressDialog loading;
		CheckBoxPreference solicitacli;
		AlertDialog dialog;
		String fin;
		
		protected void onPreExecute() {
     	loading = new ProgressDialog(OpcionesAv.this);
     	loading.setMessage("Comprobando código...");
     	loading.setCancelable(false); loading.show(); }
		
		private compruebaCliente(CheckBoxPreference solicitacli, AlertDialog dialog){
			this.solicitacli = solicitacli;
			this.dialog = dialog;
		}

    protected void onPostExecute(final Boolean success) {
     		if (loading.isShowing()) {loading.dismiss();}
     		if(success){ solicitacli.setChecked(true); 
     		redescargaclif.setEnabled(false);
     		solicitacli.setSummaryOn(fin);
     		dialog.dismiss(); }
     		else{ ((EditText) dialog.findViewById(R.id.codigoempleadoId)).setText("");
     			  ((EditText) dialog.findViewById(R.id.claveId)).setText(""); }
     		Toast.makeText(getBaseContext(), fin, Toast.LENGTH_LONG).show();
     		//getPreferenceScreen().addPreference(findPreference("redescargaclif"));
     		redescargaclif.setEnabled(true);
     	}
    
		@Override protected Boolean doInBackground(String... params) {
			try {Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
			Connection conn;
			try{DriverManager.setLoginTimeout(20);
				conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM operadores WHERE idoperador=" + codemppub.trim() +
						" AND clave='" + claveemp.trim() + "' AND activo='S'");
				if(rs.next()){
					//Comprueba Establecimiento
					if(db.getEstablecimiento(rs.getInt(4)) == null || 
							db.getEstablecimiento(rs.getInt(4)).getNombre().equals("")){
						//Descargar
						Statement st2 = conn.createStatement();
						ResultSet rs2 = st2.executeQuery("SELECT "+getString(R.string.camposest)+
								", textcat_all(cnae || ',') FROM app_company, categoriaempresa WHERE app_company.idcompanyapp="+
								rs.getInt(4)+" AND app_company.idcompanyapp=categoriaempresa.idcompanyapp " +
								"AND restringepedidos <> 'S' AND activo = true GROUP BY "+getString(R.string.camposest));
						if(rs2.next())
							db.addEstablecimiento(new Est(rs2.getInt(1),rs2.getInt(2),rs2.getString(3),rs2.getString(4),
									rs2.getString(5),rs2.getString(6),"",rs2.getBoolean(7),true,
									0,0.0f,rs2.getString(8),rs2.getString(9),rs2.getString(10),rs2.getString(11),
									rs2.getString(12),rs2.getString(13),rs2.getString(14),rs2.getString(15),0,
									rs2.getString(16),rs2.getString(17),"",rs2.getString(18),rs2.getString(19),
									rs2.getString(20),rs2.getString(21),"0",rs2.getString(22),rs2.getString(23)));
						rs2.close();
					}
					fin = "Conectado a "+db.getEstablecimiento(rs.getInt(4)).getNombre()+" como "+rs.getString(2);
					SharedPreferences.Editor spe = sharedPrefs.edit();
					//Comprueba favorito
					if(sharedPrefs.getString("main", "0").equals("0"))
						spe.putString("main", ""+rs.getInt(4));
					spe.putInt("solicitaclin", Integer.parseInt(codemppub)) //ClaveOP, pasar a String
					.putString("solicitaclinom", rs.getString(2)) //NombreOP
					.putString("solicitacliref", rs.getString(7)) //CodFacdisOP
					.putInt("solicitacliest", rs.getInt(4)).commit(); //EstOP
				}else{ rs.close(); st.close(); conn.close();
					   fin = "Código incorrecto. Póngase en contacto con el admin de su Asesoría.";
					   return false; }
			rs.close(); st.close(); 
			conn.close(); }catch (SQLException e){e.printStackTrace();}
			return true;
		}
	}
	
	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if(sharedPrefs.getInt("internetmode",0)==0)
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		else return false;}
	
	private class descargaClientes extends AsyncTask<String, Void, Boolean> {    	
      	ProgressDialog loading; int noint=sharedPrefs.getInt("internetmode",0);
      	ArrayList<CliF> clientes = new ArrayList<CliF>();
      	
      	protected void onPreExecute() {
      			
         	loading = new ProgressDialog(OpcionesAv.this);
         	loading.setMessage("Descargando sus clientes...");
         	loading.setCancelable(false); loading.show(); 
        }

        protected void onPostExecute(final Boolean success) {
        	if (loading.isShowing()) loading.dismiss();
        	else
        		Toast.makeText(getBaseContext(), "El admin de su asesoría debe crear antes clientes en la web www.distarea.es",
        				Toast.LENGTH_LONG).show();
        }
         	
        @Override protected Boolean doInBackground(String... arg0) {
        	if(db.getAllClientesFAV(sharedPrefs.getInt("solicitacliest", 0)).isEmpty()){
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
    			 
    		}catch (SQLException e){ e.printStackTrace(); }  
        	}return true;
		}}

	private AppCompatDelegate getDelegado() {
		if (delegado == null) {
			delegado = AppCompatDelegate.create(this, null);
		}
		return delegado;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getDelegado().onPostCreate(savedInstanceState);
	}

	/*@Override
	public void setContentView(View view) {
		getDelegado().setContentView(view);
	}*/

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		getDelegado().setContentView(view, params);
	}

	@Override
	public void addContentView(View view, ViewGroup.LayoutParams params) {
		getDelegado().addContentView(view, params);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		getDelegado().onPostResume();
	}

	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		super.onTitleChanged(title, color);
		getDelegado().setTitle(title);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		getDelegado().onConfigurationChanged(newConfig);
	}

	@Override
	protected void onStop() {
		super.onStop();
		getDelegado().onStop();
	}

	public void invalidateOptionsMenu() {
		getDelegado().invalidateOptionsMenu();
	}
}