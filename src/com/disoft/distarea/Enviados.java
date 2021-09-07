package com.disoft.distarea;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.Msj;

public class Enviados extends Fragment {
	Locale spanish = new Locale("es", "ES");
	SharedPreferences sharedPrefs; DatabaseHandler db;
	View view; TableLayout tl; int par=0;
	TableRow fila; TextView enviadoa, fechahora, estado;
	
	@Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
		final FragmentActivity sfa = getActivity();
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(sfa.getBaseContext());
		db = new DatabaseHandler(sfa); tl = (TableLayout) view.findViewById(R.id.tl);
		if(db.getAllMensajesEnv().size()==0){
			((ScrollView) view.findViewById(R.id.sv)).setVisibility(View.GONE);
			((TextView) view.findViewById(R.id.leyenda)).setVisibility(View.GONE);
			TextView noenviados = new TextView(sfa.getBaseContext());
			noenviados.setText(getString(R.string.noenviados));
			noenviados.setTextColor(Color.BLACK); noenviados.setGravity(17);
//			noenviados.setTextAppearance(sfa.getBaseContext(), android.R.attr.textAppearanceLarge);
			//Omitido por clase innecesaria
			((LinearLayout) view.findViewById(R.id.base)).addView(noenviados); }
		for(final Msj m : db.getAllMensajesEnv()){
			fila = new TableRow(sfa.getBaseContext());
			enviadoa = new TextView(sfa.getBaseContext());
			fechahora = new TextView(sfa.getBaseContext());
			estado = new TextView(sfa.getBaseContext());
			ImageButton ver = new ImageButton(sfa.getBaseContext());
			enviadoa.setText(db.getEstablecimiento(m.getEid()).getNombre());
			enviadoa.setTextColor(Color.BLACK);
			if(m.getFecharealiz().equals("")) fechahora.setText(getString(R.string.notsentyet));
			else{ try {fechahora.setText(new SimpleDateFormat("dd-MM-yyyy",spanish)
				.format(new SimpleDateFormat("yyyy-MM-dd",spanish)
				.parse(m.getFecharealiz()))+" \n"+m.getHorarealiz());
			} catch (ParseException e) {e.printStackTrace();}}
			fechahora.setTextColor(Color.BLACK);
			if(m.getEstado().equals("P")) estado.setText("[ "+Html.fromHtml("&#8230")+"]");
			else if(m.getEstado().equals("F")) estado.setText("["+Html.fromHtml("&#10003")+"]");
			else if(m.getEstado().equals("E")) estado.setText("[ "+Html.fromHtml("&raquo;")+" ]");//&#187;
			else estado.setText("[ ? ]"); 
			estado.setTextColor(Color.BLACK); estado.setGravity(17);
			ver.setBackgroundDrawable(getResources().getDrawable(R.drawable.botondisoft));
			ver.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
			ver.setImageDrawable(getResources().getDrawable(R.drawable.content_unread));
			ver.setOnClickListener(new OnClickListener(){@Override public void onClick(View v) {
				if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
				Intent i = new Intent(sfa, Mensaje.class);
				i.putExtra("tipo", 1); i.putExtra("mid", m.getMid());
				startActivity(i); sfa.finish();}});
			TableRow.LayoutParams lpe= new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
			lpe.weight=0.5f;
			TableRow.LayoutParams lpf= new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
			lpf.weight=0.3f;
			TableRow.LayoutParams lp= new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
			lp.weight=0.1f;
			fila.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT); 
			fila.addView(enviadoa,lpe); fila.addView(fechahora,lpf);
			fila.addView(estado,lp); fila.addView(ver,lp);
			if(par==0) { par=1;
				fila.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));}
			else { par=0;
				fila.setBackgroundColor(getResources().getColor(R.color.azulDisoft2));}
			fila.setTag(m.getMid());
			tl.addView(fila);}
	}
 
    @Override public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
      view = inflater.inflate(R.layout.enviados, container, false); return view; }
    @Override public void onSaveInstanceState(Bundle outState){
      super.onSaveInstanceState(outState); setUserVisibleHint(true); }
    
    public void actualizaEstado(){
    	String estado;
    	for(int i=1;i<tl.getChildCount();i++){
    		TableRow tr = (TableRow) tl.getChildAt(i);
    	for(Msj m : db.getAllMensajesEnv()){
    		if(m.getMid()==(Integer) tr.getTag()){
    			if(m.getEstado().equals("P")) estado="[ "+Html.fromHtml("&#8230")+"]";
    			else if(m.getEstado().equals("F")) estado="["+Html.fromHtml("&#10003")+"]";
    			else if(m.getEstado().equals("E")) estado="[ "+Html.fromHtml("&raquo;")+" ]";//&#187;
    			else estado="[ ? ]"; ((TextView) tr.getChildAt(2)).setText(estado); }}}}
}