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
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.Est;

public class RegFin extends Fragment {
	public static int flagventana=0;
	Locale spanish = new Locale("es", "ES"); TextView terminos;
	SharedPreferences sharedPrefs; DatabaseHandler db;
	FragmentActivity sfa; SharedPreferences.Editor spe;
	EditText cp = RegCP.cp, nemp = RegCP.nemp, codsuc = RegCP.codsuc, 
			email = RegRecPass.email, respuesta = RegRecPass.respuesta, 
			nombre = RegOp1.nombre, tlf = RegOp1.tlf, dir = RegOp1.dir;
	String recpass = RegRecPass.recpass, rp, CNAE, nemp2 = RegCP.nemp.getText().toString(), 
			email2 = RegRecPass.email2.getText().toString(); 
	View view, popupView; PopupWindow popupWindow; CheckBox ms = RegOp2.ms, tyc; 
	Spinner pregunta = RegRecPass.pregunta, moneda = RegOp2.moneda;
	RadioGroup tipo = RegCP.tipo;
	SimpleDateFormat sdfdia = new SimpleDateFormat("yyyy-MM-dd",spanish),
					 sdfhora = new SimpleDateFormat("HH:mm:ss",spanish);

	@Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
		sfa = getActivity(); db = new DatabaseHandler(sfa);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(sfa.getBaseContext());
		tyc=((CheckBox)sfa.findViewById(R.id.tyc));
		tyc.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) terminos.setTextColor(Color.BLACK); }});
		terminos = ((TextView)sfa.findViewById(R.id.terminos));
		SpannableString ss = new SpannableString("Confirmo ser mayor de edad y he le�do y acepto los t�rminos y condiciones de uso.");
    ClickableSpan clickableSpan = new ClickableSpan() {@Override public void onClick(View v) {
  		LayoutInflater layoutInflater = (LayoutInflater)sfa.getSystemService(sfa.LAYOUT_INFLATER_SERVICE);
  		popupView = layoutInflater.inflate(R.layout.popupad, null);  
  		popupWindow = new PopupWindow(popupView, 
  			android.view.ViewGroup.LayoutParams.MATCH_PARENT,
  			android.view.ViewGroup.LayoutParams.MATCH_PARENT, true);
  		popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
  		popupWindow.showAtLocation(sfa.findViewById(R.id.base), Gravity.CENTER, 0, 0);
  		WebView wv = (WebView) popupView.findViewById(R.id.webView);
  			wv.loadDataWithBaseURL("",getString(R.string.terminosycondiciones),"text/html", "UTF-8", "");
  		((ImageButton)popupView.findViewById(R.id.cerrar)).setOnClickListener(new OnClickListener(){ 
  		 	@Override public void onClick(View v) {
  				if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
  				popupWindow.dismiss(); }});
  		}};
  	ss.setSpan(clickableSpan, 51, 80, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    terminos.setText(ss);
    terminos.setMovementMethod(LinkMovementMethod.getInstance());
		((TextView)sfa.findViewById(R.id.userfin))
			.setText(" "+sfa.getIntent().getStringExtra("nombre"));
		((TextView)sfa.findViewById(R.id.passfin))
			.setText(" "+sfa.getIntent().getStringExtra("pass"));
		((TextView)sfa.findViewById(R.id.pais))
			.setText(" "+sfa.getIntent().getStringExtra("pais"));
		
		((Button)sfa.findViewById(R.id.finalizar)).setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
				if(tyc.isChecked()==false)terminos.setTextColor(Color.RED);
				else{
					//Genero c�digo seguro
					Random r = new Random();
					String codigoseguro="";
					for(int i=0;i<10;i++){
						if(i%2==0) codigoseguro+=(char)(r.nextInt(123-97)+97);
						else codigoseguro+=(r.nextInt(10-1)+1); }
				if(tipo.getCheckedRadioButtonId()==R.id.particular) new DescargaEstablecimientos().execute();
				else new ReplicaDatos().execute();
				spe = sharedPrefs.edit();
				spe.putString("nombre", sfa.getIntent().getStringExtra("nombre"));
				spe.putString("pass", sfa.getIntent().getStringExtra("pass"));
				spe.putString("cp", cp.getText().toString());
				if(tipo.getCheckedRadioButtonId()==R.id.empresa)
					spe.putString("nemp", nemp2);
				if(recpass.equals(getResources().getStringArray(R.array.recpass)[0]))
					rp=getResources().getStringArray(R.array.recpassv)[0];
				else if(recpass.equals(getResources().getStringArray(R.array.recpass)[1]))
				rp=getResources().getStringArray(R.array.recpassv)[1];
				spe.putString("recpass", rp);
				if(tipo.getCheckedRadioButtonId()==R.id.particular){
					if(recpass.equals(getResources().getStringArray(R.array.recpass)[0]))
						spe.putString("mail", email.getText().toString());
				else{
					spe.putString("ps", getResources().getStringArray(R.array.preguntasv)
						[pregunta.getSelectedItemPosition()]);
					spe.putString("rs", respuesta.getText().toString());}}
				else
					spe.putString("mail", email2);
				spe.putString("seudonimo", nombre.getText().toString())
				.putString("tel", tlf.getText().toString()).putString("dir", dir.getText().toString());
				RadioButton rb = (RadioButton) RegCP.tipo.findViewById(tipo.getCheckedRadioButtonId());
				spe.putString("tipo", rb.getText().toString().substring(0,1))
				.putBoolean("ms", ms.isChecked())
				.putString("moneda", getResources().getStringArray(R.array.monedav)
					[moneda.getSelectedItemPosition()])
				.putString("pais", sfa.getIntent().getStringExtra("pais"))
				.putString("codsuc", codsuc.getText().toString())
				.putString("cpprev", cp.getText().toString())
				.putString("nombreprev", sfa.getIntent().getStringExtra("nombre"))
				.putString("passprev", sfa.getIntent().getStringExtra("pass"))
				.putString("vcdate", new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()))
				.putString("tono", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString())
				.putBoolean("bg", true)  
				.putString("idfoto", "000")
				.putString("codigoseguro", codigoseguro)
				.putString("fechaalta", sdfdia.format(new Date()))
				.putString("horaalta", sdfhora.format(new Date())).commit(); }
			}});
		((Button)sfa.findViewById(R.id.finalizar))
			.setBackgroundDrawable(getResources().getDrawable(R.drawable.botondisoft));
		((Button)sfa.findViewById(R.id.finalizar))
			.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	}
	
	@Override public void setUserVisibleHint(boolean isVisibleToUser) {
	super.setUserVisibleHint(isVisibleToUser);
	try{if (isVisibleToUser) { 
		if(tipo.getCheckedRadioButtonId()==R.id.particular){ 
			((TextView)sfa.findViewById(R.id.txtinteres)).setText(
					getString(R.string.txtinteres));
			((LinearLayout)sfa.findViewById(R.id.tablafin1)).setVisibility(View.VISIBLE);
			((LinearLayout)sfa.findViewById(R.id.tablafin0_99)).setVisibility(View.GONE);
		}else{
			((TextView)sfa.findViewById(R.id.txtinteres)).setText(
					getString(R.string.txtinteresempresa));
			((LinearLayout)sfa.findViewById(R.id.tablafin0_99)).setVisibility(View.VISIBLE);
			((TextView)sfa.findViewById(R.id.nempfin)).setText(" "+nemp2);
		}
		((TextView)sfa.findViewById(R.id.cpfin)).setText(" "+cp.getText().toString());
		if(!cp.getText().toString().matches("^[0-9]{5}$") 
				&& sfa.getIntent().getStringExtra("pais").equals("Espa�a")){
			((TextView)sfa.findViewById(R.id.txtcp)).setTextColor(Color.RED);
			((Button)sfa.findViewById(R.id.finalizar)).setEnabled(false); }
		else if (!cp.getText().toString().matches("^d$")
				&& !sfa.getIntent().getStringExtra("pais").equals("Espa�a")){
			((TextView)sfa.findViewById(R.id.txtcp)).setTextColor(Color.RED);
			((Button)sfa.findViewById(R.id.finalizar)).setEnabled(false); }
		if(recpass==null) recpass = getResources().getStringArray(R.array.recpass)[0];
		if(tipo.getCheckedRadioButtonId()==R.id.particular){
			((LinearLayout)sfa.findViewById(R.id.tablafin2)).setVisibility(View.VISIBLE);
		if(recpass.equals(getResources().getStringArray(R.array.recpass)[0]))
			((TextView)sfa.findViewById(R.id.txtrecpassc1)).setText(getString(R.string.mail)+" ");
		else
			((TextView)sfa.findViewById(R.id.txtrecpassc1)).setText(getString(R.string.ps)+": ");
		((TextView)sfa.findViewById(R.id.recpassfin)).setText(" "+recpass);
		if(recpass.equals(getResources().getStringArray(R.array.recpass)[0])){
			if(!isEmailValid(email.getText().toString())){
				((TextView)sfa.findViewById(R.id.txtrecpassc1)).setTextColor(Color.RED);
				((Button)sfa.findViewById(R.id.finalizar)).setEnabled(false);}
			((TextView)sfa.findViewById(R.id.recpassc1)).setText(" "+email.getText().toString());
			((TextView)sfa.findViewById(R.id.rsfin)).setVisibility(View.GONE);}
		else{
			if(respuesta.getText().toString().equals("")){
				((TextView)sfa.findViewById(R.id.txtrs)).setTextColor(Color.RED);
				((Button)sfa.findViewById(R.id.finalizar)).setEnabled(false); }
			((TextView)sfa.findViewById(R.id.txtrecpassc1))
				.setText(getString(R.string.ps)+" ");
			((TextView)sfa.findViewById(R.id.recpassc1))
				.setText(" "+pregunta.getSelectedItem().toString());
			((TextView)sfa.findViewById(R.id.rsfin))
				.setText(" "+respuesta.getText().toString());
			((LinearLayout)sfa.findViewById(R.id.tablafin3_5)).setVisibility(View.VISIBLE); }
		}else{
			((TextView)sfa.findViewById(R.id.txtrecpassc1)).setText(getString(R.string.mail)+" ");
			((LinearLayout)sfa.findViewById(R.id.tablafin2)).setVisibility(View.GONE);
			nemp.setText(nemp2);
			if(nemp2.equals("")){
				((TextView)sfa.findViewById(R.id.txtnemp)).setTextColor(Color.RED);
				((Button)sfa.findViewById(R.id.finalizar)).setEnabled(false);}
			email.setText(email2);
			if(!isEmailValid(email.getText().toString())){
				((TextView)sfa.findViewById(R.id.txtrecpassc1)).setTextColor(Color.RED);
				((Button)sfa.findViewById(R.id.finalizar)).setEnabled(false);}
			((TextView)sfa.findViewById(R.id.recpassc1)).setText(" "+email2);
			((TextView)sfa.findViewById(R.id.rsfin)).setVisibility(View.GONE);}
		if(nombre.getText().toString().equals(""))
			((TextView)sfa.findViewById(R.id.nombrefin))
				.setText(" "+sfa.getIntent().getStringExtra("nombre"));
		else ((TextView)sfa.findViewById(R.id.nombrefin))
				.setText(" "+nombre.getText().toString());
		((TextView)sfa.findViewById(R.id.tlffin)).setText(" "+tlf.getText().toString());
		((TextView)sfa.findViewById(R.id.dirfin)).setText(" "+dir.getText().toString());
		RadioButton rb = (RadioButton) RegCP.tipo.findViewById(tipo.getCheckedRadioButtonId());
		((TextView)sfa.findViewById(R.id.tcfin)).setText(" "+rb.getText().toString());
		if(ms.isChecked()) 
			((TextView)sfa.findViewById(R.id.msfin)).setText(" "+getString(R.string.on));
		else ((TextView)sfa.findViewById(R.id.msfin)).setText(" "+getString(R.string.off));
      ((TextView)sfa.findViewById(R.id.monedafin)).setText(" "+moneda.getSelectedItem().toString());
    int check=0, uncheck=0;
    if(tipo.getCheckedRadioButtonId()==R.id.particular){
    	for(String s : db.volcarCNAE()){ String[] sa = s.split(";");
      	if(Integer.parseInt(sa[2])==2&&tipo.getCheckedRadioButtonId()==R.id.particular ||
      		Integer.parseInt(sa[2])==1&&tipo.getCheckedRadioButtonId()==R.id.empresa ||
      		Integer.parseInt(sa[2])==0){
      		if(sa[3].equals("1")) check++; else uncheck++; }
      	if(uncheck==0) ((TextView)sfa.findViewById(R.id.interesfin)).setText(" "+getString(R.string.todos));
      	else if(check==0) ((TextView)sfa.findViewById(R.id.interesfin)).setText(" "+getString(R.string.ninguno));
      	else ((TextView)sfa.findViewById(R.id.interesfin)).setText(" Varios ("+(check)+"/"+(check+uncheck)+")"); }
    }else{ String tipos=" "; 
			 for(String s : db.volcarCNAE()){ String[] sa = s.split(";");
	      if(Integer.parseInt(sa[2])==2){
	      	if(sa[3].equals("1")){ check++; 
	      		if(tipos.equals(" ")) tipos+=sa[1]; else tipos+=", "+sa[1];
	      	}else uncheck++; }
			 } if(check==0 /*|| check>5*/){ 
	      	((TextView)sfa.findViewById(R.id.txtinteres)).setTextColor(Color.RED);
	      	((TextView)sfa.findViewById(R.id.interesfin)).setText(tipos);
	      	((Button)sfa.findViewById(R.id.finalizar)).setEnabled(false);
	      }else{
	      	((TextView)sfa.findViewById(R.id.txtinteres)).setTextColor(Color.BLACK);
	      	((TextView)sfa.findViewById(R.id.interesfin)).setText(tipos); }
	      }
    }else { //Reinicializo los campos, para actualizar valores que cambien
			tipo = RegCP.tipo; cp = RegCP.cp ; recpass = RegRecPass.recpass; 
			nemp2 = RegCP.nemp.getText().toString(); email2 = RegRecPass.email2.getText().toString(); 
			pregunta = RegRecPass.pregunta; respuesta = RegRecPass.respuesta; 
			nombre = RegOp1.nombre; tlf = RegOp1.tlf; dir = RegOp1.dir; 
			ms = RegOp2.ms; moneda = RegOp2.moneda;
			if(tipo.getCheckedRadioButtonId()==R.id.particular) email = RegRecPass.email;
			else email.setText(email2); }
   }catch(NullPointerException e){e.printStackTrace();}}

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {super.onViewCreated(view, savedInstanceState);}
    @Override public View onCreateView
    (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.regfin, container, false); return view; }
    @Override public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState); setUserVisibleHint(true);}
    
  //Clase as�ncrona XXX
  private class DescargaEstablecimientos extends AsyncTask<String, Void, Boolean> {
  	private ProgressDialog loading;

  	protected void onPreExecute() {
    	loading = new ProgressDialog(sfa);
      loading.setMessage(getString(R.string.ccp1));
      loading.setCancelable(false); loading.show();}

    protected void onPostExecute(final Boolean success) {
    	if(loading.isShowing()){try{loading.dismiss();}catch(Exception e){}}
    	new descargaLogos().execute(); }

  	@Override protected Boolean doInBackground(String... arg0) {
  		try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
  		try{DriverManager.setLoginTimeout(20);
  			Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
  			Statement st = conn.createStatement(); int ncliente=0; //XXX ncliente?
  			ResultSet rs = st.executeQuery("SELECT "+getString(R.string.camposest)+
					", textcat_all(cnae || ','), nclientes FROM app_company, categoriaempresa WHERE zonainfluencia LIKE '%"+
					sharedPrefs.getString("cp","")+"%' AND app_company.idcompanyapp=categoriaempresa.idcompanyapp " +
					"AND restringepedidos <> 'S' AND activo = true AND configura LIKE '%,MCS,%' GROUP BY "+getString(R.string.camposest)+", nclientes");
  			while(rs.next()){
  				if(db.getEstablecimientosCount()==0)
  					db.addEstablecimiento(new Est(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),
							rs.getString(5),rs.getString(6),"",rs.getBoolean(7),true,
							0,0.0f,rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),
							rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),0,
							rs.getString(16),rs.getString(17),"",rs.getString(18),rs.getString(19),
							rs.getString(20),rs.getString(21),"0",rs.getString(22),rs.getString(23)));
  				else{
  					int flag = 0;
  					for(Est e : db.getAllEstablecimientos())
  						if(e.getEid()==rs.getInt(1)) flag=1;
  					if(flag==0)
  						db.addEstablecimiento(new Est(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),
  								rs.getString(5),rs.getString(6),"",rs.getBoolean(7),true,
  								0,0.0f,rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),
  								rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),0,
  								rs.getString(16),rs.getString(17),"",rs.getString(18),rs.getString(19),
  								rs.getString(20),rs.getString(21),"0",rs.getString(22),rs.getString(23))); }
  				//Mensaje autom�tico
  				ncliente = rs.getInt(23);
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
						st5.executeUpdate("INSERT INTO mensajeapp (clienteglobal,mensaje,fecharealizacion,horarealizacion," +
							"idestablecimiento,tipomensaje,estado,remitente) VALUES " +
							"("+sharedPrefs.getInt("id",0)+",'"+cuerpo+"','"+
							sdfdia.format(new Date())+"','"+sdfhora.format(new Date())+"',"+
							rs.getInt(1)+",'A','E','Sistema')");
		  			st5.close();
  			
  			} rs.close(); st.close(); conn.close(); } catch (SQLException e) {e.printStackTrace();}
  		return true;}
  	}
  
  private class ReplicaDatos extends AsyncTask<String, Void, Boolean> {
  	ProgressDialog loading;
	protected void onPreExecute() {
     	loading = new ProgressDialog(sfa);
     	loading.setMessage("Guardando sus datos\nPor favor, espere...");
     	loading.setCancelable(false); loading.show(); }

    protected void onPostExecute(final Boolean success) {
     	if (loading.isShowing()) {loading.dismiss();}
     	View popupView; final PopupWindow popupWindow;
     	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			{Display display = ((WindowManager)sfa.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
			if(display.getRotation()==Surface.ROTATION_90)
				sfa.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else if(display.getRotation()==Surface.ROTATION_270){ 
				if(android.os.Build.VERSION.SDK_INT == 8)
					sfa.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				else sfa.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
		}else sfa.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			LayoutInflater layoutInflater = (LayoutInflater)sfa.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
			popupView = layoutInflater.inflate(R.layout.popupsino, null);  
			popupWindow = new PopupWindow(popupView, 
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, false);
			popupWindow.setOutsideTouchable(false); flagventana=1;
			popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
			popupWindow.showAtLocation(sfa.findViewById(R.id.base), Gravity.CENTER, 0, 0);
			((ImageButton)popupView.findViewById(R.id.no)).setVisibility(View.GONE);
			((TextView) popupView.findViewById(R.id.texto)).setText("Registrado correctamente." +
					" A continuaci�n se mostrar� la relaci�n de establecimientos.");
			((ImageButton)popupView.findViewById(R.id.si)).setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
				if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
				popupWindow.dismiss(); flagventana=0;
				Intent i = new Intent(sfa, ListaEstablecimientos.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("vienedereg",1); startActivity(i); sfa.finish();}});
			((Button)sfa.findViewById(R.id.finalizar)).setEnabled(false);
			}
    
  	@Override protected Boolean doInBackground(String... arg0) {
  		try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
			try{DriverManager.setLoginTimeout(20);
				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
			
			//Aprovecho conexi�n para pedir n� lastmid
  			Statement st0 = conn.createStatement();
  			ResultSet rs0 = st0.executeQuery("SELECT MAX(id) FROM mensajeapp");
  			if(rs0.next()){ ((SharedPreferences.Editor)sharedPrefs.edit())
  				.putInt("lastmid",rs0.getInt(1)).commit();}
  			rs0.close(); st0.close();
  			//Continuo con la replicaci�n
				Statement st = conn.createStatement();
				st.executeUpdate("INSERT INTO clienteglobal (nombre,codigopostal,tipocliente,"+
					"mensajesadmitidos,ultimomensaje,mail,telefono,pass,seudonimo,direccion," +
					"pais,recpass,pregunta,respuesta,nombreempresa,codigoseguro,fechaalta,horaalta) VALUES ('"+
					sharedPrefs.getString("nombre","")+"',"+sharedPrefs.getString("cp","0")+",'"+
					sharedPrefs.getString("tipo","P")+"','"+db.volcarCNAEtoString()+"',"+
					sharedPrefs.getInt("lastmid",0)+",'"+sharedPrefs.getString("mail","")+"','"+
					sharedPrefs.getString("tel","")+"','"+sharedPrefs.getString("pass","")+"','"+
					sharedPrefs.getString("seudonimo","")+"','"+sharedPrefs.getString("dir","")+"','"+
					sharedPrefs.getString("pais","Espa�a")+"','"+sharedPrefs.getString("recpass","")+"','"+
					sharedPrefs.getString("ps","") +"','"+sharedPrefs.getString("rs","")+"','"+
					sharedPrefs.getString("nemp","")+"','"+sharedPrefs.getString("codigoseguro", "")+"','"+
					sharedPrefs.getString("fechaalta","")+"','"+sharedPrefs.getString("horaalta", "")+"')");
  				st.close(); conn.close(); }catch (SQLException e){e.printStackTrace();}
			try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
			try{DriverManager.setLoginTimeout(20);
			Connection conn2 = DriverManager.getConnection(getString(R.string.dirbbdd));
			Statement st2 = conn2.createStatement();
			ResultSet rs2 = st2.executeQuery("SELECT id FROM clienteglobal " +
				"ORDER BY id DESC LIMIT 1");
			if(rs2.next()) { spe.putInt("id", rs2.getInt(1)); spe.commit(); }
			rs2.close(); st2.close();
			}catch (SQLException e){e.printStackTrace();
			Toast.makeText(sfa,getString(R.string.regFinSQLError),Toast.LENGTH_LONG).show();
			Intent i = new Intent(sfa, Registro.class); startActivity(i); sfa.finish();}
  return true; }}
  
  public class descargaLogos extends AsyncTask<String, Void, Boolean> {//S�lo descarga de nuevos
		/*ProgressDialog loading;
		protected void onPreExecute() {
     	loading = new ProgressDialog(sfa);
     	loading.setMessage(getString(R.string.regFinDownloadingLogos));
     	loading.setCancelable(false); loading.show(); }*/

    protected void onPostExecute(final Boolean success) {
     	//if (loading.isShowing()) {loading.dismiss();}
     	new ReplicaDatos().execute();}
		@Override protected Boolean doInBackground(String... params) {
  	URL url; URLConnection conn;
    BufferedInputStream inStream; BufferedOutputStream outStream;
    File outFile; FileOutputStream fileStream;
    outFile = new File( File.separator + "data" + File.separator + "data" + 
    		File.separator + sfa.getPackageName() + File.separator + "logos" + File.separator);
    if(!outFile.exists()){ outFile.mkdir();
    	try{new File (outFile+ File.separator+".nomedia").createNewFile();
    	}catch (IOException e){e.printStackTrace();} }
    for(Est es : db.getAllEstablecimientos()){
    	if(es.getLogo()==null) continue; else{
    try{url = new URL(getString(R.string.dirlogos)+es.getLogo());
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
    return true;} }
		
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
}