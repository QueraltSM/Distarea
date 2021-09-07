package com.disoft.distarea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/
import com.disoft.distarea.R.color;
import com.disoft.distarea.extras.DatabaseHandler;

public class RegCP extends Fragment {
	public static EditText cp, nemp, codsuc; public static RadioGroup tipo;
	SharedPreferences sharedPrefs; DatabaseHandler db;  View view;
	FragmentActivity sfa; SharedPreferences.Editor spe;
	int nest=0, flaglibre=0, tipoact=0; String[] nomest;//, CNAE;
	
	@Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
		sfa = getActivity(); db = new DatabaseHandler(sfa);
		//CNAE = db.volcarCNAE();
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(sfa.getBaseContext());
		tipo = (RadioGroup)sfa.findViewById(R.id.tipo);
		tipo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				//if(tipoact!=checkedId){ //Alterno CNAE
					/*String[] CNAE = db.volcarCNAEinverso();
					for(String s : CNAE){
						String dec[] = s.split(";"); 
						db.updateCNAE(Integer.parseInt(dec[0]), dec[1], 
								Integer.parseInt(dec[2]), Integer.parseInt(dec[3])>0);}
					tipoact=checkedId;}*/
				if(checkedId==R.id.particular){
					//Cambio CNAEs a marcados
					if(tipoact!=R.id.particular){//Supuestamente he cambiado
					for (String s : db.volcarCNAE()){
						String dec[] = s.split(";");
						db.updateCNAE(Integer.parseInt(dec[0]), dec[1], 
								Integer.parseInt(dec[2]), true); }
						tipoact=R.id.particular;}
					((LinearLayout)sfa.findViewById(R.id.lineacp)).setVisibility(View.VISIBLE);
					((TextView)sfa.findViewById(R.id.regtxtnemp)).setVisibility(View.GONE);
					((EditText)sfa.findViewById(R.id.nemp)).setVisibility(View.GONE);
					((TextView)sfa.findViewById(R.id.regtxtcodsuc)).setVisibility(View.GONE);
					((LinearLayout)sfa.findViewById(R.id.lineacodsuc)).setVisibility(View.GONE);
					if(((LinearLayout)sfa.findViewById(R.id.listacp)).getChildCount()>0){
						((LinearLayout)sfa.findViewById(R.id.desliza)).setVisibility(View.VISIBLE);
						((LinearLayout)sfa.findViewById(R.id.listacp)).setVisibility(View.VISIBLE);}
					else{
						((LinearLayout)sfa.findViewById(R.id.desliza)).setVisibility(View.GONE);
						((ImageView)sfa.findViewById(R.id.flechas)).setVisibility(View.GONE);
						((TextView)sfa.findViewById(R.id.nest)).setVisibility(View.GONE);
						((LinearLayout)sfa.findViewById(R.id.listacp)).setVisibility(View.GONE);}
				}else {
					if(flaglibre==0) { flaglibre=1;
						((Registro2)getActivity()).activarNavegacion(); }
				//Cambio CNAEs a marcados
					if(tipoact!=R.id.empresa){
					for (String s : db.volcarCNAE()){
						String dec[] = s.split(";");
						db.updateCNAE(Integer.parseInt(dec[0]), dec[1], 
								Integer.parseInt(dec[2]), false); }
						tipoact=R.id.empresa;}
					((LinearLayout)sfa.findViewById(R.id.desliza)).setVisibility(View.VISIBLE);
					((TextView)sfa.findViewById(R.id.regtxtnemp)).setVisibility(View.VISIBLE);
					((EditText)sfa.findViewById(R.id.nemp)).setVisibility(View.VISIBLE);
					((TextView)sfa.findViewById(R.id.regtxtcodsuc)).setVisibility(View.VISIBLE);
					((LinearLayout)sfa.findViewById(R.id.lineacodsuc)).setVisibility(View.VISIBLE);
					((LinearLayout)sfa.findViewById(R.id.lineacp)).setVisibility(View.VISIBLE);
					((ImageView)sfa.findViewById(R.id.flechas)).setVisibility(View.GONE);
					((TextView)sfa.findViewById(R.id.nest)).setVisibility(View.GONE);
					((LinearLayout)sfa.findViewById(R.id.listacp)).setVisibility(View.GONE); }}
				 });
    cp = (EditText)sfa.findViewById(R.id.cp);
    nemp = (EditText)sfa.findViewById(R.id.nemp);
    codsuc = (EditText)sfa.findViewById(R.id.codsuc);
    ((Button)sfa.findViewById(R.id.validar)).setOnClickListener(new OnClickListener() {
    @Override public void onClick(View v) {
    	if (sharedPrefs.getBoolean("ch", true))view.performHapticFeedback(1);
    	if(sfa.getIntent().getStringExtra("pais").equals("España")){
     	if(cp.getText().toString().matches("^[0-9]{5}$")){
     		((TextView)sfa.findViewById(R.id.txtcp)).setTextColor(getResources().getColor(R.color.verdeDisoft3));
     		InputMethodManager imm=(InputMethodManager)sfa.getSystemService(Context.INPUT_METHOD_SERVICE);
     		imm.hideSoftInputFromWindow(cp.getWindowToken(), 0);
     		if(tipoact==R.id.particular){ new PrimerCP().execute();
     		((TextView)sfa.findViewById(R.id.nest)).setVisibility(View.VISIBLE);}
      	if(flaglibre==0) { flaglibre=1;
      		((Registro2)getActivity()).activarNavegacion(); }
       	((LinearLayout)sfa.findViewById(R.id.desliza)).setVisibility(View.VISIBLE);}
    	}else if(cp.getText().toString().matches("^d$")){
    		((TextView)sfa.findViewById(R.id.txtcp)).setTextColor(getResources().getColor(R.color.verdeDisoft3));
    		if(flaglibre==0) { flaglibre=1;
    		((Registro2)getActivity()).activarNavegacion(); }
    		((LinearLayout)sfa.findViewById(R.id.desliza)).setVisibility(View.VISIBLE);
    	}
    }});
    ((Button)sfa.findViewById(R.id.validar))
			.setBackgroundDrawable(getResources().getDrawable(R.drawable.botondisoft));
    ((Button)sfa.findViewById(R.id.validar))
      .getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
	
	cp.setOnKeyListener(new View.OnKeyListener(){
		      public boolean onKey(View v, int keyCode, KeyEvent event){
		          if (event.getAction() == KeyEvent.ACTION_DOWN){
		              switch (keyCode){
		                  case KeyEvent.KEYCODE_DPAD_CENTER:
		                  case KeyEvent.KEYCODE_ENTER:
		                	  ((Button)sfa.findViewById(R.id.validar)).performClick(); return true;
		                  default: break;
		              }
		          }return false;
		      }});
	}
 
    /*@Override public FragmentActivity getActivity(){
    	return super.getActivity(); }*/
    @Override public void onViewCreated(View view, Bundle savedInstanceState){
    	super.onViewCreated(view, savedInstanceState); }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, 
    	Bundle savedInstanceState){
      view = inflater.inflate(R.layout.regcp, container, false); return view; }
    @Override public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState); setUserVisibleHint(true); }
    
  //Clase asíncrona XXX Hay que cambiar que busque los nombres, para volcarlos
  private class PrimerCP extends AsyncTask<String, Void, Boolean> {
    private ProgressDialog loading; String codigopostal;
          
    protected void onPreExecute() {
		codigopostal = cp.getText().toString();
      	loading = new ProgressDialog(sfa);
  		loading.setMessage(getString(R.string.be));
  		loading.setCancelable(false); loading.show();}

    protected void onPostExecute(final Boolean success) {
    	if(loading.isShowing()){try{loading.dismiss();}catch(Exception e){}}
      if(nest==0) ((TextView)sfa.findViewById(R.id.nest)).setText(getString(R.string.nest0));
      else if(nest==1) ((TextView)sfa.findViewById(R.id.nest)).setText(getString(R.string.nestuno));
			else ((TextView)sfa.findViewById(R.id.nest))
					.setText(getString(R.string.nest1)+" "+nest+" "+getString(R.string.nest2));
      if(nest>0){
     		((LinearLayout)sfa.findViewById(R.id.listacp)).setVisibility(View.VISIBLE);
     		((ImageView)sfa.findViewById(R.id.flechas)).setVisibility(View.VISIBLE);
     		int par=0;
     		for(String s : nomest){
     			TextView linea = new TextView(sfa.getBaseContext());
     			linea.setTextAppearance(sfa.getBaseContext(),android.R.style.TextAppearance_Medium);
     			linea.setTextColor(getResources().getColor(android.R.color.black));
     			if(par==0) { par=1; 
     				linea.setBackgroundColor(getResources().getColor(R.color.azulDisoft3)); }
     			else { par=0; 
     				linea.setBackgroundColor(getResources().getColor(R.color.azulDisoft2)); }
     			linea.setText(s); ((LinearLayout)sfa.findViewById(R.id.listacp)).addView(linea); }
     		TextView pie = new TextView(getActivity().getBaseContext());
     		pie.setText(getString(R.string.pie));
     		pie.setGravity(Gravity.CENTER_HORIZONTAL);
     		pie.setTextColor(getResources().getColor(android.R.color.black));
     		((LinearLayout)sfa.findViewById(R.id.listacp)).addView(pie); }
      }

  		@Override protected Boolean doInBackground(String... arg0) {
  			try{Class.forName("org.postgresql.Driver");}
  			catch(ClassNotFoundException e){e.printStackTrace();}
  			try{DriverManager.setLoginTimeout(20);
  				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
  				Statement st = conn.createStatement();
  				ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM app_company "+
  					"WHERE zonainfluencia LIKE '%"+codigopostal+"%'");
  				if(rs.next()) nest = rs.getInt(1); rs.close();
  				if(nest>0) { nomest = new String[nest]; int i=0;
  				rs = st.executeQuery("SELECT nombre FROM app_company "+
  						"WHERE zonainfluencia LIKE '%"+codigopostal+"%' ORDER BY nombre ASC");
  				while(rs.next()) { nomest[i]=rs.getString(1); i++; }
  				rs.close(); } st.close(); conn.close();
  			} catch (SQLException e) {e.printStackTrace();}
  			return true;}
  		}
}