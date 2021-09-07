package com.disoft.distarea;

import android.accounts.Account;
import android.accounts.AccountManager;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/
import com.disoft.distarea.extras.DatabaseHandler;

public class RegRecPass extends Fragment {
	public static String recpass; public static Spinner pregunta;
	public static EditText email, email2, respuesta; View view;
	SharedPreferences sharedPrefs; DatabaseHandler db;
	FragmentActivity sfa; SharedPreferences.Editor spe;
	RadioGroup tipo = RegCP.tipo; String gmail = null;
	String[] CNAE, nCNAE, idCNAE, cbCNAE;
	
	@Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
		sfa = getActivity(); db = new DatabaseHandler(sfa);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(sfa.getBaseContext());
		email = (EditText)sfa.findViewById(R.id.email);
		email2 = (EditText)sfa.findViewById(R.id.email2);
		
		AccountManager manager = (AccountManager) sfa.getSystemService(Context.ACCOUNT_SERVICE);
		Account[] list = manager.getAccounts();
		for(Account account: list){
		    if(account.type.equalsIgnoreCase("com.google")){
		        gmail = account.name; break; }}
		if(tipo != null && tipo.getCheckedRadioButtonId()==R.id.empresa){
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
    ((LinearLayout)sfa.findViewById(R.id.listarecpass)).addView(adapter.getView(j,null,null));
		}}
	
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
		if(tipo==null || tipo.getCheckedRadioButtonId()==R.id.particular){
		((ScrollView)sfa.findViewById(R.id.emp)).setVisibility(View.GONE);
		((ScrollView)sfa.findViewById(R.id.part)).setVisibility(View.VISIBLE);
		if(email.getText().toString().equals("")) email.setText(gmail);
		((RadioGroup)sfa.findViewById(R.id.recpass)).setOnCheckedChangeListener
			(new OnCheckedChangeListener(){
	        public void onCheckedChanged(RadioGroup group, int checkedId) {
					respuesta = (EditText)sfa.findViewById(R.id.respuesta);
					pregunta = (Spinner)sfa.findViewById(R.id.preguntas);
		        	if(sharedPrefs.getBoolean("ch", true))view.performHapticFeedback(1);
		        	if(((RadioButton)group.findViewById(checkedId))
		        			.equals(sfa.findViewById(R.id.radprs))){
		        		email.setVisibility(View.GONE);
		        		respuesta.setVisibility(View.VISIBLE);
		        		pregunta.setVisibility(View.VISIBLE);
		        		recpass = getResources().getStringArray(R.array.recpass)[1];}
		        	else if(((RadioButton)group.findViewById(checkedId))
		        			.equals(sfa.findViewById(R.id.rademail))){
		        		email.setVisibility(View.VISIBLE);
		        		respuesta.setVisibility(View.GONE);
		        		pregunta.setVisibility(View.GONE);
		        		recpass = getResources().getStringArray(R.array.recpass)[0];}
				}});
		}else if(tipo.getCheckedRadioButtonId()==R.id.empresa){
				CNAE = db.volcarCNAE(); int i = 0, CNAElength=0;
					for(String s : CNAE) { String[] sa=s.split(";");
						if(Integer.parseInt(sa[2])==2 && tipo.getCheckedRadioButtonId()==R.id.particular) //Particular
							CNAElength++;
						else if(Integer.parseInt(sa[2])==2 && tipo.getCheckedRadioButtonId()==R.id.empresa) //Empresa
							CNAElength++; }
					nCNAE = new String[CNAElength]; idCNAE = new String[CNAElength];
					cbCNAE = new String[CNAElength+1];
					for(String s : CNAE){ String[] sa=s.split(";");
						if(Integer.parseInt(sa[2])==2 && tipo.getCheckedRadioButtonId()==R.id.particular){
							idCNAE[i]=sa[0]; nCNAE[i]=sa[1]; cbCNAE[i]=sa[3]; i++; }
						else if(Integer.parseInt(sa[2])==2 && tipo.getCheckedRadioButtonId()==R.id.empresa){
							idCNAE[i]=sa[0]; nCNAE[i]=sa[1]; cbCNAE[i]=sa[3]; i++; }
					}
				
				((LinearLayout)sfa.findViewById(R.id.listarecpass)).removeAllViews();
		    ArrayAdapter<String> adapter = new Adaptador(sfa,R.layout.lvcnae, nCNAE);
		    
		    for(int j=0;j<nCNAE.length;j++)
		    ((LinearLayout)sfa.findViewById(R.id.listarecpass)).addView(adapter.getView(j,null,null));
			((ScrollView)sfa.findViewById(R.id.emp)).setVisibility(View.VISIBLE);
			((ScrollView)sfa.findViewById(R.id.part)).setVisibility(View.GONE);
			if(email2.getText().toString().equals("")) email2.setText(gmail);}
		}}catch(NullPointerException e){e.printStackTrace();}}

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {super.onViewCreated(view, savedInstanceState);}
    @Override public View onCreateView
    (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.regrecpass, container, false); return view;}
    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState); setUserVisibleHint(true);}
}