package com.disoft.distarea;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;*/
import android.support.v7.app.ActionBar;
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.extras.ViewPagerAdapterMensajes;
import com.disoft.distarea.models.Msj;

public class Mensajes extends AppCompatActivity {
	SharedPreferences sharedPrefs; DatabaseHandler db; ActionBar ab; //IcsSpinner is;
  static ActionBar.Tab tab; ViewPager vp; int unavez=0; EditText cuerpo, files;
  String[] listaarchivos; LinearLayout adjuntos, ladjuntos; Msj pend;
  String[] listaarchivos2; String cuerpo2;
  /*
   * Aclaración importante sobre el funcionamiento de las pestañas:
   * En este código, se controlan las dos formas de navegar de manera independiente,
   * pero ambas hacen lo mismo. Por un lado, tenemos la selección de pestañas
   * (tabSelection), usando un tabListener, y mirando onTabSelected, y onTabReselected.
   * Por otro lado, se controla también la navegación por deslizamiento, que
   * prácticamente usa lo mismo de las pestañas, pero requiere ciertas aclaraciones, 
   * como la reasignación de elementos, como EditText y LinearLayout, o la parte de
   * la composición de la ventana de adjuntos. Ambos usan el flag "unavez", para
   * identificar correctamente si se ha visto previamente, o si es una reaparición
   * (principalmente, usado para controlar las declaraciones).
   */

  @Override protected void onCreate(Bundle savedInstanceState) {
  	super.onCreate(savedInstanceState);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.mensajes);
    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    db = new DatabaseHandler(this); ab = getSupportActionBar();
    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
    ab.setTitle(getString(R.string.mensajes)); ab.setSubtitle(getString(R.string.recibidos));
    ab.setIcon(R.drawable.content_email); ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    ab.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.azbackground2));
    vp = (ViewPager) findViewById(R.id.pager);
    NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(1101);
    FragmentManager fm = getSupportFragmentManager();
    ViewPager.SimpleOnPageChangeListener ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
      @Override public void onPageSelected(int position) {
        super.onPageSelected(position); 
        ab.setSelectedNavigationItem(position);
        if(position==2) { if(vp.getChildCount()>=position){
//        	is = ((IcsSpinner)vp.findViewById(R.id.ics_spinner));
          cuerpo = ((EditText)vp.findViewById(R.id.cuerpo));
          files = ((EditText)vp.findViewById(R.id.files));
          adjuntos = ((LinearLayout)vp.findViewById(R.id.adjuntos));
          ladjuntos = ((LinearLayout)vp.findViewById(R.id.listadjuntos)); 
            if(unavez==0){ unavez=1;
             /*if(getIntent().getIntExtra("eid", 0)>0)
	            is.setSelection(((Redactar.IcsAdapter) is.getAdapter()).getPosition(
	              	db.getEstablecimiento(getIntent().getIntExtra("eid", 0)).getNombre()));
            }else{*/ if(listaarchivos2!=null){
              cuerpo.setText(cuerpo2); files.setText("");
    	      	for(int i=0;i<listaarchivos2.length;i++){
    	      		if(i==listaarchivos2.length-1)
    	      			files.setText(files.getText()+listaarchivos2[i]);
    	      		else files.setText(files.getText()+listaarchivos2[i]+"<!>"); }
    	      	ladjuntos.removeAllViews();
    	      	for(final String s : listaarchivos2){
            		LinearLayout ll = new LinearLayout(getBaseContext());
              	ImageButton ib = new ImageButton(getBaseContext());
              	TextView tv = new TextView(getBaseContext());
              	ll.setOrientation(LinearLayout.HORIZONTAL);
              	ib.setImageDrawable(getResources().getDrawable(R.drawable.navigation_cancel));
              	ib.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
              	tv.setText(s.substring(s.lastIndexOf("/")+1));
              	tv.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Large);
              	tv.setTextColor(getResources().getColor(android.R.color.black));
              	LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
              			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1.0f);
              	tv.setLayoutParams(param); tv.setGravity(Gravity.CENTER_VERTICAL);
    	    			ib.setOnClickListener(new OnClickListener(){ public void onClick(View v){
    	    				AlertDialog.Builder adb = new AlertDialog.Builder(Mensajes.this);
			    				adb.setTitle("Quitar archivo adjunto")
			    					 .setMessage(getString(R.string.messagesYouAreAboutToRemoveStart)+
			    							 s.substring(s.lastIndexOf("/")+1)+getString(R.string.messagesYouAreAboutToRemoveStart))
			    					 .setPositiveButton(getString(R.string.messagesYesRemoveIt),new DialogInterface.OnClickListener(){
    							 public void onClick(DialogInterface dialog, int id){
    								 ladjuntos.removeView(adjuntos.findViewWithTag(s));
    			    				if(ladjuntos.getChildCount()==0) adjuntos.setVisibility(View.GONE);
    			    				/*4 casos:
    			    					·<!>En medio<!>
    			    					·<!>Termina
    			    					·Empieza<!>
    			    					·Termina y empieza (sólo él)*/
    			    				if(files.getText().toString().endsWith(s)){ //Termina
    			    					if(files.getText().toString().startsWith(s)) files.setText(""); //Termina pero no hay más
    			    					else files.setText(files.getText().toString().substring( //Termina, pero hay más
    			    						0,files.getText().toString().length()-(s.length()+3)));
    			    				}else //Doble caso: Empieza por, pero hay más, o está en medio.
    			    					files.setText(files.getText().toString().replace(s+"<!>", ""));
    			    				dialog.dismiss(); }})
			    					 .setNegativeButton(getString(R.string.messagesNoKeepIt),new DialogInterface.OnClickListener(){
			    						 @Override public void onClick(DialogInterface dialog, int id){
			    							 dialog.dismiss(); }});
			    				AlertDialog ad = adb.create(); ad.show(); }});
    	    			ll.addView(ib); ll.addView(tv); ll.setTag(s); ladjuntos.addView(ll);}
    	      	if(ladjuntos.getChildCount()!=0){ 
    	      		adjuntos.setVisibility(View.VISIBLE);
    	      		ladjuntos.setVisibility(View.VISIBLE);} }
            }}} }};
    vp.setOnPageChangeListener(ViewPagerListener);
    final ViewPagerAdapterMensajes vpa = new ViewPagerAdapterMensajes(fm);
    vp.setAdapter(vpa);
    // Capture tab button clicks
    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
    	@Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
    		// Pass the position on tab click to ViewPager
        ab.setSubtitle(tab.getText().toString());
        if(vp.getChildCount()>=tab.getPosition()) vp.setCurrentItem(tab.getPosition());
        if(vp.getCurrentItem()==2){
          //Si en la BBDD hay un mensaje con estado P, echarlo en el EditText de Redactar
          if(unavez==0) { unavez=1;  
//          	is = ((IcsSpinner)vp.findViewById(R.id.ics_spinner));
          	cuerpo = ((EditText)vp.findViewById(R.id.cuerpo));
	          files = ((EditText)vp.findViewById(R.id.files));
            adjuntos = ((LinearLayout)vp.findViewById(R.id.adjuntos));
            ladjuntos = ((LinearLayout)vp.findViewById(R.id.listadjuntos));}
          /*if(getIntent().getIntExtra("eid", 0)>0)
	            is.setSelection(((Redactar.IcsAdapter) is.getAdapter()).getPosition(
	        -      	db.getEstablecimiento(getIntent().getIntExtra("eid", 0)).getNombre()));*/
         }
        	}
    	@Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    		if(vp.getCurrentItem()!=2 && unavez==1){
    			cuerpo2 = cuerpo.getText().toString();
    			if(!files.getText().toString().equals(""))
    				listaarchivos2 = files.getText().toString().split("<!>");}
    	}
      @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
      	if(vp.getCurrentItem()==2 && unavez==1){
      		if(listaarchivos2!=null){
//      		is = ((IcsSpinner)vp.findViewById(R.id.ics_spinner));
	        cuerpo = ((EditText)vp.findViewById(R.id.cuerpo));
      		adjuntos = ((LinearLayout)vp.findViewById(R.id.adjuntos));
      		files = ((EditText)vp.findViewById(R.id.files));
          ladjuntos = ((LinearLayout)vp.findViewById(R.id.listadjuntos));
	      	cuerpo.setText(cuerpo2); files.setText("");
	      	for(int i=0;i<listaarchivos2.length;i++){
	      		if(i==listaarchivos2.length-1)
	      			files.setText(files.getText()+listaarchivos2[i]);
	      		else files.setText(files.getText()+listaarchivos2[i]+"<!>"); }
	      	ladjuntos.removeAllViews();
	      	for(final String s : listaarchivos2){
        		LinearLayout ll = new LinearLayout(getBaseContext());
          	ImageButton ib = new ImageButton(getBaseContext());
          	TextView tv = new TextView(getBaseContext());
          	ll.setOrientation(LinearLayout.HORIZONTAL);
          	ib.setImageDrawable(getResources().getDrawable(R.drawable.navigation_cancel));
          	ib.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
          	tv.setText(s.substring(s.lastIndexOf("/")+1));
          	tv.setTextAppearance(getBaseContext(), android.R.style.TextAppearance_Large);
          	tv.setTextColor(getResources().getColor(android.R.color.black));
          	LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
          			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1.0f);
          	tv.setLayoutParams(param); tv.setGravity(Gravity.CENTER_VERTICAL);
	    			ib.setOnClickListener(new OnClickListener(){ public void onClick(View v){
	    				AlertDialog.Builder adb = new AlertDialog.Builder(getBaseContext());
	    				adb.setTitle(getString(R.string.messagesRemoveAttachedFile))
	    					 .setMessage(getString(R.string.messagesYouAreAboutToRemoveStart)+"\""+
	    							 s.substring(s.lastIndexOf("/")+1)+"\""+getString(R.string.messagesYouAreAboutToRemoveEnd))
	    					 .setPositiveButton(getString(R.string.messagesYesRemoveIt),new DialogInterface.OnClickListener(){
	    							 public void onClick(DialogInterface dialog, int id){
	    								 ladjuntos.removeView(adjuntos.findViewWithTag(s));
	    			    				if(ladjuntos.getChildCount()==0) adjuntos.setVisibility(View.GONE);
	    			    				/*4 casos:
	    			    					·<!>En medio<!>
	    			    					·<!>Termina
	    			    					·Empieza<!>
	    			    					·Termina y empieza (sólo él)*/
	    			    				if(files.getText().toString().endsWith(s)){ //Termina
	    			    					if(files.getText().toString().startsWith(s)) files.setText(""); //Termina pero no hay más
	    			    					else files.setText(files.getText().toString().substring( //Termina, pero hay más
	    			    						0,files.getText().toString().length()-(s.length()+3)));
	    			    				}else //Doble caso: Empieza por, pero hay más, o está en medio.
	    			    					files.setText(files.getText().toString().replace(s+"<!>", ""));
	    			    				dialog.dismiss(); }})
	    					 .setNegativeButton(getString(R.string.messagesNoKeepIt),new DialogInterface.OnClickListener(){
	    						 public void onClick(DialogInterface dialog, int id){
	    							 dialog.dismiss(); }})
	    					 .show();
	    				 }});
	    			ll.addView(ib); ll.addView(tv); ll.setTag(s); ladjuntos.addView(ll);}
	      	if(ladjuntos.getChildCount()!=0){ 
	      		adjuntos.setVisibility(View.VISIBLE);
	      		ladjuntos.setVisibility(View.VISIBLE);}
      	}}
      }
    };
    ab.addTab(ab.newTab().setText(getString(R.string.recibidos)).setTabListener(tabListener));
    ab.addTab(ab.newTab().setText(getString(R.string.enviados)).setTabListener(tabListener));
    ab.addTab(ab.newTab().setText(getString(R.string.redactar)).setTabListener(tabListener));
    if(getIntent().getIntExtra("tipo",0)==1){
      ab.setSelectedNavigationItem(1); vp.setCurrentItem(1);
    }else if(getIntent().getIntExtra("tipo",0)==2){
      ab.setSelectedNavigationItem(2); vp.setCurrentItem(2);
      vpa.notifyDataSetChanged();
    }
  }
    
  @Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mensajes, menu);
		/*ShareActionProvider sap = (ShareActionProvider)
				menu.findItem(R.id.share).getActionProvider();*/
// 	 	if (sap != null) {sap.setShareIntent(creaShareIntent());}
	return true;}
  
  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if(sharedPrefs.getBoolean("ch",true)) vp.performHapticFeedback(1);
    if (item.getItemId() == android.R.id.home) {
       Intent intent = new Intent(this, ListaCompra.class);
       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
       startActivity(intent); return true;
     }else if(item.getItemId() == R.id.refrescarmenu) {
       if(sharedPrefs.getBoolean("ch",true)) vp.performHapticFeedback(1);
       Intent i = new Intent(Mensajes.this, Mensajes.class);
       if(vp.getCurrentItem()<2) i.putExtra("tipo", vp.getCurrentItem());
       else i.putExtra("tipo", 0);
       i.putExtra("refresh", true); startActivity(i); finish(); return true;
  	}else if(item.getItemId() == R.id.conversacionesmenu) {
  		if(sharedPrefs.getBoolean("ch",true)) vp.performHapticFeedback(1);
  		startActivity(new Intent(Mensajes.this, Conversaciones.class));}
  		return true;
    }
  
    @Override public void onBackPressed() {
      super.onBackPressed();
      Intent intent = new Intent(this, ListaCompra.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    }

    public Intent creaShareIntent(){
    	Intent sendIntent = new Intent();
      sendIntent.setAction(Intent.ACTION_SEND);
      sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.cadenacompartir));
      sendIntent.setType("text/plain"); return sendIntent; }
}