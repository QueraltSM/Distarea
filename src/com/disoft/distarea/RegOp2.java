package com.disoft.distarea;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/
import com.disoft.distarea.extras.DatabaseHandler;

public class RegOp2 extends Fragment {
	SharedPreferences sharedPrefs; DatabaseHandler db;
	String[] CNAE, nCNAE, idCNAE, cbCNAE; View view;
	FragmentActivity sfa; SharedPreferences.Editor spe;
	public static CheckBox ms; public static Spinner moneda;
	RadioGroup tipo = RegCP.tipo;
	
	@Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
		sfa = getActivity(); db = new DatabaseHandler(sfa);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(sfa.getBaseContext());
		ms = ((CheckBox)sfa.findViewById(R.id.ms));
		moneda = ((Spinner)sfa.findViewById(R.id.moneda));
		if(tipo.getCheckedRadioButtonId()==R.id.particular)
			((LinearLayout)sfa.findViewById(R.id.cnae)).setVisibility(View.VISIBLE);
		else ((LinearLayout)sfa.findViewById(R.id.cnae)).setVisibility(View.GONE);
			CNAE = db.volcarCNAE();
		int i = 0, CNAElength=0;
			for(String s : CNAE) { String[] sa=s.split(";");
				if(Integer.parseInt(sa[2])==2 && tipo.getCheckedRadioButtonId()==R.id.particular) //Particular
					CNAElength++;
				else if(Integer.parseInt(sa[2])==2 && tipo.getCheckedRadioButtonId()==R.id.empresa) //Empresa
					CNAElength++; }
			nCNAE = new String[CNAElength]; idCNAE = new String[CNAElength];
			cbCNAE = new String[CNAElength];
			for(String s : CNAE){ String[] sa=s.split(";");
				if(Integer.parseInt(sa[2])==2 && tipo.getCheckedRadioButtonId()==R.id.particular){
					idCNAE[i]=sa[0]; nCNAE[i]=sa[1]; cbCNAE[i]=sa[3]; i++; }
				else if(Integer.parseInt(sa[2])==2 && tipo.getCheckedRadioButtonId()==R.id.empresa){
					idCNAE[i]=sa[0]; nCNAE[i]=sa[1]; cbCNAE[i]=sa[3]; i++; }}
		
    ArrayAdapter<String> adapter = new Adaptador(sfa,R.layout.lvcnae, nCNAE);
    for(int j=0;j<nCNAE.length;j++)
    ((LinearLayout)sfa.findViewById(R.id.listaop2)).addView(adapter.getView(j,null,null));
		}
	
	public class Adaptador extends ArrayAdapter<String> {
    public Adaptador(Context context, int textViewResourceId, String[] objects){
     	super(context, textViewResourceId, objects);}

    @Override public View getView(final int position, View convertView, ViewGroup parent){
      LayoutInflater inflater = sfa.getLayoutInflater();
      final View row = inflater.inflate(R.layout.lvcnae, parent, false);
      TextView item = (TextView)row.findViewById(R.id.catcnae);
      item.setText(nCNAE[position]); row.setTag(idCNAE[position]);
      if(cbCNAE[position].equals("1"))
  			((CheckBox)row.findViewById(R.id.cb)).setChecked(true);
  		else if(cbCNAE[position].equals("0")) { 
  			((CheckBox)row.findViewById(R.id.cb)).setChecked(false); }
      ((CheckBox)row.findViewById(R.id.cb)).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
					if(((CheckBox)row.findViewById(R.id.cb)).isChecked())
						db.updateCbCNAE(Integer.parseInt((String)row.getTag()), true);
					else db.updateCbCNAE(Integer.parseInt((String)row.getTag()), false);
			}}); return row;}
  }
	
	@Override public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		try{if (isVisibleToUser) { tipo = RegCP.tipo;
			if(tipo.getCheckedRadioButtonId()==R.id.particular){
				((LinearLayout)sfa.findViewById(R.id.cnae)).setVisibility(View.VISIBLE);
				CNAE = db.volcarCNAE(); int i = 0, CNAElength=0;
				for(String s : CNAE) { String[] sa=s.split(";");
					if(Integer.parseInt(sa[2])==2 && tipo.getCheckedRadioButtonId()==R.id.particular) //Particular
						CNAElength++;
					else if(Integer.parseInt(sa[2])==2 && tipo.getCheckedRadioButtonId()==R.id.empresa) //Empresa
						CNAElength++; }
				nCNAE = new String[CNAElength]; idCNAE = new String[CNAElength];
				cbCNAE = new String[CNAElength];
				for(String s : CNAE){ String[] sa=s.split(";");
					if(Integer.parseInt(sa[2])==2 && tipo.getCheckedRadioButtonId()==R.id.particular){
						idCNAE[i]=sa[0]; nCNAE[i]=sa[1]; cbCNAE[i]=sa[3]; i++; }
					else if(Integer.parseInt(sa[2])==2 && tipo.getCheckedRadioButtonId()==R.id.empresa){
						idCNAE[i]=sa[0]; nCNAE[i]=sa[1]; cbCNAE[i]=sa[3]; i++; }}
				((LinearLayout)sfa.findViewById(R.id.listaop2)).removeAllViews();
		    ArrayAdapter<String> adapter = new Adaptador(sfa,R.layout.lvcnae, nCNAE);
		    
		    for(int j=0;j<nCNAE.length;j++)
		    ((LinearLayout)sfa.findViewById(R.id.listaop2)).addView(adapter.getView(j,null,null));
			}else ((LinearLayout)sfa.findViewById(R.id.cnae)).setVisibility(View.GONE);
		}}catch(Exception e){e.printStackTrace();}}

  @Override public void onViewCreated(View view, Bundle savedInstanceState){super.onViewCreated(view, savedInstanceState);}
  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
    view = inflater.inflate(R.layout.regop2, container, false); return view; }
  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState); setUserVisibleHint(true);}
}