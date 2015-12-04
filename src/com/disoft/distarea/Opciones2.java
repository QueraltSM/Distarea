package com.disoft.distarea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
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
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;*/
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.Est;

public class Opciones2 extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	SharedPreferences sharedPrefs; DatabaseHandler db;
	EditTextPreference nombre, pass, cp, seudonimo, mail, tel, dir, nemp, codsuc;
	ListPreference moneda, tipo, pais; TextView info; View v; 
	int flag=0, flagcodseg=0; String asteriscos="********************", nuevotipo, est;
	private OnSharedPreferenceChangeListener /*ospclnombre, ospclpass,*/ ospclcp, 
	ospclmoneda, ospcltipo, ospclseudonimo, ospclmail, ospcltel, ospcldir, ospclpais,
	ospclnemp, ospclcodsuc;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppCompatActivity apa = new AppCompatActivity();
		ActionBar ab = apa.getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		ab.setTitle(getString(R.string.opciones)); ab.setSubtitle(R.string.subop2);
		ab.setIcon(R.drawable.action_settings); 
		addPreferencesFromResource(R.xml.opciones2);
		setContentView(R.layout.opciones); v = findViewById(R.id.base);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		nuevotipo = sharedPrefs.getString("tipo","");
		db = new DatabaseHandler(this);
		
		if(getIntent().getIntExtra("reg",0)==1){
			info = (TextView) findViewById(R.id.info);
			info.setText(R.string.info); info.setGravity(17); }
		else findViewById(R.id.info).setVisibility(View.GONE);
		
		//C�digo seguro
		final Preference codseg = findPreference("codigoseguro");
		codseg.setSummary(asteriscos.substring(20-sharedPrefs.getString("codigoseguro", "").length()));
		codseg.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	    	  @Override public boolean onPreferenceClick(Preference preference) {
	    		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
	    		if(flagcodseg==0){ flagcodseg=1;
	    			codseg.setSummary(sharedPrefs.getString("codigoseguro", ""));
	    		}else{ flagcodseg=0;
	    			codseg.setSummary(asteriscos.substring(20-sharedPrefs.getString("codigoseguro", "").length()));
	    		} return false;
	    	  }});
		
		
		// Nombre
		/*nombre = (EditTextPreference) findPreference("nombre");
		if(getIntent().getIntExtra("reg",0)==1){
			nombre.setSummary(getIntent().getStringExtra("nombre"));
			nombre.setSelectable(false);
		}else{
			nombre.setSummary(sharedPrefs.getString("nombre", ""));
			ospclnombre = new SharedPreferences.OnSharedPreferenceChangeListener() {
				@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
					nombre = (EditTextPreference) findPreference("nombre");
					nombre.setSummary(sharedPrefs.getString("nombre", "")); flag=1; }};
		sharedPrefs.registerOnSharedPreferenceChangeListener(ospclnombre);}*/
		nombre = (EditTextPreference) findPreference("nombre");
		nombre.setSummary(sharedPrefs.getString("nombre", ""));
		nombre.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean res = false;
                if (((String)newValue).length()>50 || ((String)newValue).length()<6) 
                	Toast.makeText(getBaseContext(), "La longitud del nombre debe ser entre 6 y 50 caracteres.", Toast.LENGTH_LONG).show();
                else if(!((String)newValue).matches("^[A-Za-z0-9_.��@-]+$"))
                	Toast.makeText(getBaseContext(), R.string.caraperm,Toast.LENGTH_LONG).show();
                else{ //Todo OK
                	res = true; flag = 1;  nombre.setSummary((String)newValue); }
                return res;
        }});

		// Contrase�a
		/*
		pass = (EditTextPreference) findPreference("pass");
		if(getIntent().getIntExtra("reg",0)==1){
			pass.setSummary(getIntent().getStringExtra("pass"));
			pass.setSelectable(false);
		}else{
			pass.setSummary(asteriscos.substring(20-sharedPrefs.getString("pass", "").length()));
			ospclpass = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				pass = (EditTextPreference) findPreference("pass");
				pass.setSummary(asteriscos.substring(20-sharedPrefs.getString("pass", "").length()));
				flag=1; }};
		sharedPrefs.registerOnSharedPreferenceChangeListener(ospclpass);}*/
		pass = (EditTextPreference) findPreference("pass");
		pass.setSummary(asteriscos.substring(20-sharedPrefs.getString("pass", "").length()));
		pass.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean res = false;
                if (((String)newValue).length()>20 || ((String)newValue).length()<6) 
                	Toast.makeText(getBaseContext(), "La longitud de la contrase�a debe ser entre 6 y 20 caracteres.", Toast.LENGTH_LONG).show();
                else{ //Todo OK
                	res = true; flag = 1;  pass.setSummary(asteriscos.substring(20-((String)newValue).length())); }
                return res;
        }});

		// C�digo Postal
		cp = (EditTextPreference) findPreference("cp");
		if (sharedPrefs.getString("cp", "") != null)
			cp.setSummary(sharedPrefs.getString("cp", ""));
		else cp.setSummary("");
		ospclcp = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				cp = (EditTextPreference) findPreference("cp");
				cp.setSummary(prefs.getString("cp", ""));
				if(sharedPrefs.getString("cp", "").matches("^[0-9]{5}$")&&
						!sharedPrefs.getString("cp", "").equals(sharedPrefs.getString("cpprev", ""))){
					LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
					View popupView = layoutInflater.inflate(R.layout.popupsino, null);
					final PopupWindow popupWindow = new PopupWindow(popupView,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,true);
					popupWindow.setBackgroundDrawable(getResources()
						.getDrawable(android.R.drawable.alert_light_frame));
					popupWindow.showAtLocation(v,Gravity.CENTER, 0, 0);
					((TextView) popupView.findViewById(R.id.texto)).setText(getString(R.string.option2AllTheEstablishments));
					ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
					si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
					si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
							if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
							popupWindow.dismiss(); /*new CambioCP().execute();*/}});
					ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
					no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
					no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
							if (sharedPrefs.getBoolean("ch", true))v.performHapticFeedback(1);
							popupWindow.dismiss();}});
				}flag=1; }};
		sharedPrefs.registerOnSharedPreferenceChangeListener(ospclcp);
		
		// Moneda
		moneda = (ListPreference) findPreference("moneda");
		moneda.setSummary(moneda.getEntry());
		ospclmoneda = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				moneda = (ListPreference) findPreference("moneda");
				moneda.setSummary(moneda.getEntry()); flag=1; }};
		sharedPrefs.registerOnSharedPreferenceChangeListener(ospclmoneda);
		
		// Tipo
		tipo = (ListPreference) findPreference("tipo");
		tipo.setSummary(tipo.getEntry());
		ospcltipo = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				tipo = (ListPreference) findPreference("tipo");
				tipo.setSummary(tipo.getEntry()); int flagtipo=0;
				if(!tipo.getValue().equals(nuevotipo)){
					nuevotipo = tipo.getValue(); flagtipo=1;}
				flag=1;
				if(nuevotipo.equals("E") && flagtipo==1){
					getPreferenceScreen().addPreference(nemp);
					getPreferenceScreen().addPreference(codsuc);
					findPreference("opCNAE").setTitle(R.string.actemp);
					Toast.makeText(getBaseContext(),getString(R.string.option2CheckTheNewFields),
							Toast.LENGTH_LONG).show();
				}else if(nuevotipo.equals("P") && flagtipo==1){
					getPreferenceScreen().removePreference(findPreference("nemp"));
					getPreferenceScreen().removePreference(findPreference("codsuc"));
					findPreference("opCNAE").setTitle(R.string.tda);
				} }};
				
		sharedPrefs.registerOnSharedPreferenceChangeListener(ospcltipo);
		
		// Seud�nimo
		seudonimo = (EditTextPreference) findPreference("seudonimo");
		if(!sharedPrefs.getString("seudonimo", "").equals(""))
			seudonimo.setSummary(sharedPrefs.getString("seudonimo", ""));
		else seudonimo.setSummary(sharedPrefs.getString("nombre", ""));
		ospclseudonimo = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			seudonimo = (EditTextPreference) findPreference("seudonimo");
			if(!sharedPrefs.getString("seudonimo", "").equals(""))
				seudonimo.setSummary(sharedPrefs.getString("seudonimo", ""));
			else seudonimo.setSummary(sharedPrefs.getString("nombre", "")); flag=1; }};
		sharedPrefs.registerOnSharedPreferenceChangeListener(ospclseudonimo);

		// E-mail
		mail = (EditTextPreference) findPreference("mail");
		if(!sharedPrefs.getString("mail", "").equals(""))
			mail.setSummary(sharedPrefs.getString("mail", ""));
		else mail.setSummary(getString(R.string.option2Undefined));
		ospclmail = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			mail = (EditTextPreference) findPreference("mail");
			if(isEmailValid(sharedPrefs.getString("mail", "")))
				mail.setSummary(sharedPrefs.getString("mail", ""));
			else if(sharedPrefs.getString("mail", "").equals("")
					&& !sharedPrefs.getString("recpass", "").equals("E-mail")){
				mail.setSummary("");
			}else{ mail.setSummary(getString(R.string.options2InvalidEmail));
			Toast.makeText(getBaseContext(),getString(R.string.options3CheckEmail),
				Toast.LENGTH_LONG).show();}
			flag=1; }};
		sharedPrefs.registerOnSharedPreferenceChangeListener(ospclmail);

		// Tel�fono
		tel = (EditTextPreference) findPreference("tel");
		if(!sharedPrefs.getString("tel", "").equals(""))
			tel.setSummary(sharedPrefs.getString("tel", ""));
		else tel.setSummary(getString(R.string.option2Undefined));
		ospcltel = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				tel = (EditTextPreference) findPreference("tel");
				if(!sharedPrefs.getString("tel", "").equals(""))
					tel.setSummary(sharedPrefs.getString("tel", ""));
				else tel.setSummary(getString(R.string.option2Undefined));
				flag=1; }};
		sharedPrefs.registerOnSharedPreferenceChangeListener(ospcltel);
		
		// Direcci�n
			dir = (EditTextPreference) findPreference("dir");
			if(!sharedPrefs.getString("dir", "").equals(""))
				dir.setSummary(sharedPrefs.getString("dir", ""));
			else dir.setSummary(getString(R.string.option2Undefined));
			ospcldir = new SharedPreferences.OnSharedPreferenceChangeListener() {
				@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
					dir = (EditTextPreference) findPreference("dir");
					if(!sharedPrefs.getString("dir", "").equals(""))
						dir.setSummary(sharedPrefs.getString("dir", ""));
					else dir.setSummary(getString(R.string.option2Undefined));
					flag=1; }};
			sharedPrefs.registerOnSharedPreferenceChangeListener(ospcldir);
			
		// Pais
			pais = (ListPreference) findPreference("pais"); 
			pais.setEntries(db.pedirNombresPaises()); 
			pais.setEntryValues(db.pedirNombresPaises());
			pais.setSummary(pais.getEntry());
			ospclpais= new SharedPreferences.OnSharedPreferenceChangeListener() {
				@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
					pais = (ListPreference) findPreference("pais");
					pais.setSummary(pais.getEntry());
					flag=1;
			}};
			sharedPrefs.registerOnSharedPreferenceChangeListener(ospclpais);
		
		//CNAE
			Preference myPref = findPreference("opCNAE");
			if(tipo.getEntry().equals("P")) myPref.setTitle(R.string.tda);
			else myPref.setTitle(R.string.actemp);
      myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
    	  @Override public boolean onPreferenceClick(Preference preference) {
    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
  			  Intent intent = new Intent(Opciones2.this, OpCNAE.class);
  			  startActivity(intent); return true;
    	  }});
      
   // Nombre de la Empresa
  		nemp = (EditTextPreference) findPreference("nemp");
  		if(sharedPrefs.getString("nemp", "").equals(""))
				nemp.setSummary(getString(R.string.option2Undefined));
			else  nemp.setSummary(sharedPrefs.getString("nemp", ""));
  		ospclnemp = new SharedPreferences.OnSharedPreferenceChangeListener() {
  			@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
  				if(tipo.getValue().equals("1")){
	  				nemp = (EditTextPreference) findPreference("nemp");
	  				if(sharedPrefs.getString("nemp", "").equals(""))
	  					nemp.setSummary(getString(R.string.option2Undefined));
	  				else nemp.setSummary(sharedPrefs.getString("nemp", ""));
	  				flag=1; }}};
  		sharedPrefs.registerOnSharedPreferenceChangeListener(ospclnemp);
      
   // C�digo Sucursal
   			codsuc = (EditTextPreference) findPreference("codsuc");
   			if(!sharedPrefs.getString("codsuc","0").equals("0"))
   				codsuc.setSummary(sharedPrefs.getString("codsuc", "0"));
   			else codsuc.setSummary(getString(R.string.option2NotABranch));
   			ospclcodsuc = new SharedPreferences.OnSharedPreferenceChangeListener() {
   				@Override public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
   					if(tipo.getValue().equals("1")){
	   					codsuc = (EditTextPreference) findPreference("codsuc");
	   					if(!sharedPrefs.getString("codsuc", "0").equals("0"))
	   						codsuc.setSummary(sharedPrefs.getString("codsuc", "0"));
	   					else codsuc.setSummary(getString(R.string.option2NotABranch));}}};
   			sharedPrefs.registerOnSharedPreferenceChangeListener(ospclcodsuc);
   			
   			if(tipo.getValue().equals("P")){
					getPreferenceScreen().removePreference(findPreference("nemp"));
					getPreferenceScreen().removePreference(findPreference("codsuc"));
   			}
   			
   		//Bot�n buscar usuarioapp
   			Preference syncEst = findPreference("syncest");
  	      syncEst.setOnPreferenceClickListener(new OnPreferenceClickListener() {
  	    	  @Override public boolean onPreferenceClick(Preference preference) {
  	    		  if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
  	    		  Toast.makeText(getBaseContext(), 
  	    				getString(R.string.option2SearchingEstablishmentUser), 
  	  	    			Toast.LENGTH_LONG).show();
  	    		  new syncEst().execute();//Buscar en Est mi nombre
  	    		  /*if(sharedPrefs.getInt("usuarioapp", 0)!=0)
  	    		  Toast.makeText(getBaseContext(), 
  	  						getString(R.string.option2SynchronizingEstablishment)+est+".", 
  	  	    			Toast.LENGTH_LONG).show();*/
  	    		  return true;
  	    	  }});
	}

	@Override public void onSharedPreferenceChanged(SharedPreferences sp, String s) {flag=1;}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
		if (item.getItemId() == android.R.id.home) {
			if(sharedPrefs.getString("tipo","P").equals("E") && 
					sharedPrefs.getString("nemp","").equals("")){
				Toast.makeText(getBaseContext(),
						getString(R.string.option2CheckTheCompanyName),
						Toast.LENGTH_LONG).show();
			}else{
				if(flag==1){
					//if(!sharedPrefs.getString("nombre","").equals(sharedPrefs.getString("nombreprev", "")))
						new ReplicaDatos().execute();
				}else{Intent intent = new Intent(Opciones2.this, Opciones.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent); finish();} return true; }} return true;
	}
	
	@Override public void onBackPressed() {
		if(sharedPrefs.getString("tipo","P").equals("E") && 
				sharedPrefs.getString("nemp","").equals("")){
				Toast.makeText(getBaseContext(),
						getString(R.string.option2CheckTheCompanyName),
						Toast.LENGTH_LONG).show();
			}else{
				if(flag==1){
					//if(!sharedPrefs.getString("nombre","").equals(sharedPrefs.getString("nombreprev", "")))
						new ReplicaDatos().execute();
				}else{
				Intent intent = new Intent(Opciones2.this, Opciones.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent); finish();}}
	}
	
	//Clase as�ncrona XXX
	public class CambioCP extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog loading;
        
    protected void onPreExecute() {
    	loading = new ProgressDialog(Opciones2.this);
     	loading.setMessage(getString(R.string.ccp1));
     	loading.setCancelable(false); loading.show();}

    protected void onPostExecute(final Boolean success) {
    	if(loading.isShowing()){try{loading.dismiss();}catch(Exception e){}}}

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
					else{
						int repe = 0;
						for(Est e : db.getAllEstablecimientos())
							if(e.getEid()==rs.getInt(1)) repe=1;
						if(repe==0)
							db.addEstablecimiento(new Est(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),
									rs.getString(5),rs.getString(6),"",rs.getBoolean(7),true,
									0,0.0f,rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),
									rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),0,
									rs.getString(16),rs.getString(17),"",rs.getString(18),rs.getString(19),
									rs.getString(20),rs.getString(21),"0",rs.getString(22),rs.getString(23)));
						}
				} rs.close(); st.close(); conn.close();
			} catch (SQLException e) {e.printStackTrace();} return true;}
		}
	
	//Comprobador de e-mail correcto
    public static boolean isEmailValid(String email) {
        boolean isValid = false; CharSequence inputStr = email;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) { isValid = true; } return isValid; }

    private class ReplicaDatos extends AsyncTask<String, Void, Boolean> {
    	ProgressDialog loading;
			protected void onPreExecute() {
	     	loading = new ProgressDialog(Opciones2.this);
	     	loading.setMessage(getString(R.string.option2Synchronizing));
	     	loading.setCancelable(false); loading.show(); }

	    protected void onPostExecute(final Boolean success) {
	     	if (loading.isShowing()) {loading.dismiss();}
	     	Intent intent = new Intent(Opciones2.this, Opciones.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent); finish();}
    	@Override protected Boolean doInBackground(String... arg0) {
    		try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
				try{DriverManager.setLoginTimeout(20);
					Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM clienteglobal " +
					"WHERE nombre='"+nombre.getText().toString()+"'");
				if(rs.next())
					if(rs.getInt(1)>0){
						//Toast.makeText(getBaseContext(),getString(R.string.usernametaken),Toast.LENGTH_LONG).show();
						}
				rs.close(); st.close(); conn.close(); }catch (SQLException e) {e.printStackTrace();}
			if(nuevotipo.equals("")) nuevotipo = tipo.getValue();
			try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
			try{DriverManager.setLoginTimeout(20);
			Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st = conn.createStatement();
			st.executeUpdate("UPDATE clienteglobal SET " +
					"nombre='"+sharedPrefs.getString("nombre", "")+"'," +
					"codigopostal="+sharedPrefs.getString("cp","")+"," +
					"tipocliente='"+nuevotipo+"'," +
					"ultimomensaje="+sharedPrefs.getInt("lastmid", 0)+"," +
					"mail='"+sharedPrefs.getString("mail", "")+"'," +
					"telefono='"+sharedPrefs.getString("tel", "")+"'," +
					"pass='"+sharedPrefs.getString("pass", "")+"'," +
					"seudonimo='"+sharedPrefs.getString("seudonimo", "")+"'," +
					"direccion='"+sharedPrefs.getString("dir", "")+"'," +
					"pais='"+sharedPrefs.getString("pais", "Espa�a")+"'," +
					"nombreempresa='"+sharedPrefs.getString("nemp", "")+"' " +
					"WHERE id="+sharedPrefs.getInt("id",0));
			SharedPreferences.Editor spe = sharedPrefs.edit();
			spe.putString("nombreprev", sharedPrefs.getString("nombre",""));
			spe.putString("passprev", sharedPrefs.getString("pass",""));
			spe.putString("cpprev", sharedPrefs.getString("cp",""));
			spe.commit(); st.close(); conn.close();
			}catch (SQLException er) {er.printStackTrace();}
    	return true; }}

    public class syncEst extends AsyncTask<String, Void, Boolean> {
		protected void onPostExecute(Boolean success){
			if(sharedPrefs.getInt("usuarioapp", 0)!=0)
  		  Toast.makeText(getBaseContext(), 
  				getString(R.string.option2SynchronizingEstablishment)+" "+est+".", 
	    			Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getBaseContext(), 
						getString(R.string.option2YouDoNotHaveSynchronized), 
	    			Toast.LENGTH_LONG).show();}
		
		@Override protected Boolean doInBackground(String... params) {
			try {Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
			try{DriverManager.setLoginTimeout(20); int flagua=0;
				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st4 = conn.createStatement();
				ResultSet rs4 = st4.executeQuery("SELECT "+getString(R.string.camposest)+
						", textcat_all(cnae || ',') FROM app_company, categoriaempresa WHERE usuarioapp LIKE '%"+
						sharedPrefs.getString("nombre","")+"%' AND app_company.idcompanyapp=categoriaempresa.idcompanyapp " +
						"GROUP BY "+getString(R.string.camposest));
				while(rs4.next()) {
					int flagest = 0; String uas[] = rs4.getString(18).split(",");
					for(final String s : uas){
						if(s.equals(sharedPrefs.getString("nombre",""))){ flagua=1;
						SharedPreferences.Editor spe = sharedPrefs.edit();
						spe.putInt("usuarioapp", rs4.getInt(1));
						est=rs4.getString(3);
						spe.commit();
						for(Est e : db.getAllEstablecimientos()) if(e.getEid()==rs4.getInt(1)) flagest=1;
						if(flagest==0) db.addEstablecimiento(new Est(rs4.getInt(1),rs4.getInt(2),rs4.getString(3),rs4.getString(4),
								rs4.getString(5),rs4.getString(6),"",rs4.getBoolean(7),true,
								1,0.0f,rs4.getString(8),rs4.getString(9),rs4.getString(10),rs4.getString(11),
								rs4.getString(12),rs4.getString(13),rs4.getString(14),rs4.getString(15),0,
								rs4.getString(16),rs4.getString(17),"",rs4.getString(18),rs4.getString(19),
								rs4.getString(20),rs4.getString(21),"0",rs4.getString(22),rs4.getString(23)));
					}}} rs4.close(); st4.close(); conn.close();
					if(flagua==0) 
						((SharedPreferences.Editor)sharedPrefs.edit()).putInt("usuarioapp",0).commit();
				}catch(Exception e){e.printStackTrace();}
			return true;
		}}
}