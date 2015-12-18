package com.disoft.distarea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.internal.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;*/
import com.disoft.distarea.extras.DatabaseHandler;

public class OpCNAE extends AppCompatActivity {
	SharedPreferences sharedPrefs; DatabaseHandler db;
  View v; ListView lv; Menu menu; ActionBar ab; String save, original;
  String[] CNAE, nCNAE, idCNAE, cbCNAE; int desmarcados=0, tipo=2; //Tipo puesto a 2 para que salgan todos
	ArrayList<String> checkpos = new ArrayList<String>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); ab = getSupportActionBar();
    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME
    		|ActionBar.DISPLAY_HOME_AS_UP);
    ab.setTitle(getString(R.string.opciones));
    ab.setIcon(R.drawable.action_settings);
    setContentView(R.layout.opcnae); v = findViewById(R.id.base);
    lv = (ListView)findViewById(android.R.id.list);
    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    if(sharedPrefs.getString("tipo","P").equals("E"))
    	ab.setSubtitle(getString(R.string.opCNAEBusineddActivities));
    else ab.setSubtitle(getString(R.string.opCNAEActivityTypes));
   	/*if(sharedPrefs.getString("tipo","P").equals("P")) tipo=2;
   	else if(sharedPrefs.getString("tipo","P").equals("E")) tipo=1;*/
    db = new DatabaseHandler(this); original = db.volcarCNAEtoString();
    CNAE = db.volcarCNAE(); mostrarLista(CNAE);}
    
    @Override public boolean onCreateOptionsMenu(Menu menu){this.menu=menu; return true;}
    @Override public boolean onOptionsItemSelected(MenuItem item) {
    	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    	super.onBackPressed(); save=db.volcarCNAEtoString();
      if(!save.equals(original)) new ReplicaCNAE().execute(); return false; }
    
    @Override public void onBackPressed() {
	    if(sharedPrefs.getString("tipo","P").equals("E")){
	    	int contador=0;
	    	for(String s : CNAE)
	     		if(Integer.parseInt(s.split(";")[3])==1) contador++;
	    	if (contador==0) Toast.makeText(getBaseContext(),
	    			getString(R.string.opCNAEDefineBusinessActivity), Toast.LENGTH_LONG).show();
	    	/*else if(contador>5) Toast.makeText(getBaseContext(),
	    				getString(R.string.opCNAEMoreThanFive),
	    				Toast.LENGTH_LONG).show();*/
	    	else{ super.onBackPressed(); save=db.volcarCNAEtoString();
	        if(!save.equals(original)) new ReplicaCNAE().execute(); }
	    }else{ super.onBackPressed(); save=db.volcarCNAEtoString();
	      if(!save.equals(original)) new ReplicaCNAE().execute(); }
    }

    public class Adaptador extends ArrayAdapter<String> {
    	public Adaptador(Context context, int textViewResourceId, String[] objects){
    		super(context, textViewResourceId, objects); }
 
    @Override public View getView(final int position,View convertView,ViewGroup parent){
     	LayoutInflater inflater = OpCNAE.this.getLayoutInflater();
      final View row = inflater.inflate(R.layout.lvcnae, parent, false);
      row.setTag(idCNAE[position]);
      TextView nombre = (TextView)row.findViewById(R.id.catcnae);
      nombre.setText(nCNAE[position]); 
     	if(cbCNAE[position].equals("1"))
    		((CheckBox)row.findViewById(R.id.cb)).setChecked(true);
    	else if(cbCNAE[position].equals("0")) { checkpos.add((String)row.getTag());
    		((CheckBox)row.findViewById(R.id.cb)).setChecked(false); }
      ((CheckBox)row.findViewById(R.id.cb)).setOnClickListener(new OnClickListener() {
  			@Override public void onClick(View v) {
  				if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
  				if(((CheckBox)row.findViewById(R.id.cb)).isChecked()){	
  					desmarcados++; checkpos.add((String)row.getTag());
  					db.updateCbCNAE(Integer.parseInt((String)row.getTag()), true);
  				} else { desmarcados--; checkpos.remove((String)row.getTag());
  					db.updateCbCNAE(Integer.parseInt((String)row.getTag()), false);}
  		}});
      for(String s : checkpos) if(row.getTag().equals(s))
        ((CheckBox)row.findViewById(R.id.cb)).setChecked(false);
      return row;}
    }
    
    public void mostrarLista(String[] CNAE) {
		ArrayList<String> CNAE2 = new ArrayList<String>();
		int i = 0;
		for (String s : CNAE) {
			if (tipo == 0) CNAE2.add(s);
			else if (Integer.parseInt(s.split(";")[2]) == tipo)
				CNAE2.add(s);
		}
		nCNAE = new String[CNAE2.size()];
		idCNAE = new String[CNAE2.size()];
		cbCNAE = new String[CNAE2.size()];
		for (String s2 : CNAE2) {
			String[] sa = s2.split(";");
			idCNAE[i] = sa[0];
			nCNAE[i] = sa[1];
			cbCNAE[i] = sa[3];
			i++;
		}
		ArrayAdapter<String> adapter = new Adaptador(this, R.layout.lvlistaest, nCNAE);
		lv.setAdapter(adapter);
	}
      //setListAdapter(adapter); }

    
    private class ReplicaCNAE extends AsyncTask<String, Void, Boolean> {
    	@Override protected Boolean doInBackground(String... arg0) {
    	//Conectar a la BBDD y subir save en permitemensajes
      	try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
				try{DriverManager.setLoginTimeout(20);
				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st = conn.createStatement();
				st.executeUpdate("UPDATE clienteglobal SET mensajesadmitidos='" + save + 
					"' WHERE id="+sharedPrefs.getInt("id",0)); st.close(); conn.close();
				}catch (SQLException e) {e.printStackTrace();}
    		return true; }}
}