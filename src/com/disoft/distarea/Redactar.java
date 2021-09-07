package com.disoft.distarea;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
//import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.internal.widget.IcsSpinner;*/
import android.support.v7.app.ActionBar;
import com.disoft.distarea.extras.Automailer;
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.Est;
import com.disoft.distarea.models.Msj;

public class Redactar extends Fragment implements AdapterView.OnItemSelectedListener {
	Locale spanish = new Locale("es", "ES"); File f; DatabaseHandler db;
	SharedPreferences sharedPrefs; int cuentaficheros=0, emailenviado=0;
	//public IcsSpinner is, testicss; private IcsAdapter mAdapter, testada;
	private String[] mNameList, testopcs; LinearLayout adjuntos, ladjuntos;
	View view; ImageButton enviar, solicitud; EditText cuerpo, files; int[] eids;
	Msj mensaje; FragmentActivity sfa; PopupWindow popupWindow;
	SimpleDateFormat nombrefichero = new SimpleDateFormat("yyyy.MM.dd.kk.mm.ss", spanish);
	
	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		sfa = getActivity();
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(sfa.getBaseContext());
		db = new DatabaseHandler(sfa);
		enviar = (ImageButton) view.findViewById(R.id.enviar);
		solicitud = (ImageButton) view.findViewById(R.id.solicitud);
		cuerpo = (EditText) view.findViewById(R.id.cuerpo);
		files = (EditText) view.findViewById(R.id.files);
		adjuntos = (LinearLayout) view.findViewById(R.id.adjuntos);
		ladjuntos = (LinearLayout) view.findViewById(R.id.listadjuntos);

//		is = (IcsSpinner) view.findViewById(R.id.ics_spinner);
		int i = 0, size = 0;
		for (Est e : db.getAllEstablecimientos()) if (e.getFav() && e.getPrior() >= 0) size++;
		if (size == 0) {
			cuerpo.setEnabled(false);
			enviar.setEnabled(false);
			((TextView) sfa.findViewById(R.id.nofav)).setVisibility(View.VISIBLE);
		} else {
			mNameList = new String[size];
			eids = new int[size];
			for (Est e : db.getAllEstablecimientos()) {
				if (e.getFav() && e.getPrior() >= 0) {
					mNameList[i] = e.getNombre();
					eids[i] = e.getEid();
					i++;
				}
			}
			/*mAdapter = new IcsAdapter(sfa, R.layout.spinner, mNameList);
			is.setAdapter(mAdapter);
			is.setOnItemSelectedListener(this);
			for (int j = 0; j < eids.length; j++)
				if (eids[j] == sfa.getIntent().getIntExtra("eid", 0)) {
					is.setSelection(j);
					break;
				}*/
			enviar.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
			enviar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
					if (!cuerpo.getText().toString().equals("")) {
						if (isNetworkAvailable()) {
							if (sharedPrefs.getBoolean("ms", false) == true) {
								LayoutInflater layoutInflater = (LayoutInflater) sfa.getBaseContext()
										.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								final View popupView = layoutInflater.inflate(R.layout.popupms, null);
								popupWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.MATCH_PARENT,
										android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
								popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
								popupWindow.showAtLocation(sfa.findViewById(R.id.base), Gravity.CENTER, 0, 0);
								((TextView) popupView.findViewById(R.id.texto)).setText
										((((TextView) popupView.findViewById(R.id.texto)).getText()) + " " + getString(R.string.motivo1));
								ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
								ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
								si.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
								no.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
								si.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										if (sharedPrefs.getBoolean("ch", true))
											v.performHapticFeedback(1);
										/*if (((EditText) popupView.findViewById(R.id.mspass)).getText().toString()
												.equals(sharedPrefs.getString("pass", "")))
											new EnviarMensaje().execute();
										else
											((EditText) popupView.findViewById(R.id.mspass)).requestFocus();*/
									}
								});
								no.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										if (sharedPrefs.getBoolean("ch", true))
											v.performHapticFeedback(1);
										popupWindow.dismiss();
										Toast.makeText(sfa.getApplicationContext(), getString(R.string.cancel1), Toast.LENGTH_LONG).show();
									}
								});
							}// else new EnviarMensaje().execute(); //Sin MS
						} /*else {
							mensaje = new Msj(db.getLastMidEnv() + 1, sharedPrefs.getInt("id", 0),
									cuerpo.getText().toString(), files.getText().toString(), new SimpleDateFormat("yyyy-MM-dd", spanish).format(new Date()),
									new SimpleDateFormat("HH:mm:ss", spanish).format(new Date()), "",
									eids[is.getSelectedItemPosition()], "A", "", "", "", "", "P",
									sharedPrefs.getString("seudonimo", sharedPrefs.getString("nombre", "")), 0, "");
							Toast.makeText(sfa.getBaseContext(), getString(R.string.malmacen), Toast.LENGTH_LONG).show();
							db.almacenarMensajeEnviado(mensaje);
							Intent intent = new Intent(getActivity(), Mensajes.class).putExtra("tipo", 1);
							startActivity(intent);
							getActivity().finish();
						}*/
					} else cuerpo.requestFocus();
				}
			});
			testopcs = new String[]{getString(R.string.writeAttatchFile), getString(R.string.writeTakeAPhoto), getString(R.string.writeMakeAVideo), getString(R.string.writeRecordVoice)};
			/*testicss = (IcsSpinner) view.findViewById(R.id.testicss);
			testada = new IcsAdapter(sfa, R.layout.spinner, testopcs);
			testicss.setAdapter(testada);*/
			solicitud.getBackground().setColorFilter(0xFF9ACDD2, PorterDuff.Mode.MULTIPLY);
			solicitud.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (sharedPrefs.getBoolean("ch", true)) v.performHapticFeedback(1);
//					testicss.performClick();
				}
			});

		}}
		@Override public void onItemSelected (AdapterView< ? > parent, View view,int position, long id) {}
		@Override public void onNothingSelected (AdapterView<?> parent){}

	/////////////////
	//Métodos spinner
	/////////////////
	public class IcsAdapter extends ArrayAdapter<String> {

		String[] nameList = new String[mNameList.length];

		public IcsAdapter(Context context, int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
			this.nameList = objects;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			parent.setBackgroundColor(getResources().getColor(R.color.azulDisoft3));
//			return getCustomView(position, convertView, parent);
//			Era obligatorio dejar un Return aqu?. Amagado
			return getView(position, convertView, parent);
		}

		@Override
		public void setNotifyOnChange(boolean notifyOnChange) {
			super.setNotifyOnChange(notifyOnChange);
		}
	/*@Override public View getView(int position, View convertView, ViewGroup parent){
		return getCustomView(position, convertView, parent);}
	public View getCustomView(int position, View convertView,ViewGroup parent) {
	LayoutInflater inflater = getLayoutInflater(null);
	View row = inflater.inflate(R.layout.spinner, parent, false);
	TextView spinnerText = (TextView) row.findViewById(R.id.name_view);
	if (position < nameList.length) {
		String cateName = nameList[position];
	  spinnerText.setText(cateName);
	  if(cateName.equals(getString(R.string.writeAttatchFile))){
	  	try{
	  	((ImageView)row.findViewById(R.id.iconospinner)).setImageDrawable(
	  		getResources().getDrawable(R.drawable.content_attachment));
	  	row.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
      	if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);*/
//      	startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("*/*"),1);
		//REVISAR ADJUNTAR ARCHIVO
		/*
      	hideSpinnerDropDown(testicss); }}); }
	  	catch(Exception e){e.printStackTrace();}}
	  else if(cateName.equals(getString(R.string.writeTakeAPhoto))){
	  	((ImageView)row.findViewById(R.id.iconospinner)).setImageDrawable(
				getResources().getDrawable(R.drawable.device_access_camera));
	  	row.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
	  		f = new File(sfa.getExternalCacheDir()+File.separator+
	  				nombrefichero.format(new Date())+".jpg");
	  		startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
	  			.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f)),2); 
	  		hideSpinnerDropDown(testicss); }}); }
		else if(cateName.equals(getString(R.string.writeMakeAVideo))){
			((ImageView)row.findViewById(R.id.iconospinner)).setImageDrawable(
				getResources().getDrawable(R.drawable.device_access_video));
			row.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
				f = new File(sfa.getExternalCacheDir()+File.separator+
						nombrefichero.format(new Date())+".3gp");
				startActivityForResult(new Intent(MediaStore.ACTION_VIDEO_CAPTURE)
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f)),3); 
				hideSpinnerDropDown(testicss); }}); }
		else if(cateName.equals(getString(R.string.writeRecordVoice))){
	  	((ImageView)row.findViewById(R.id.iconospinner)).setImageDrawable(
	  		getResources().getDrawable(R.drawable.device_access_mic));
			row.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
				startActivityForResult(new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION),4); 
				hideSpinnerDropDown(testicss); }}); }
		else{ if(db.getEstablecimiento(cateName).getLogo()!=null){
				File logo = new File( File.separator + "data" + File.separator + "data" + 
		    		File.separator + sfa.getPackageName() +
		    		File.separator + "logos" + File.separator+
		    		db.getEstablecimiento(cateName).getLogo());
				if(logo.exists())
					((ImageView)row.findViewById(R.id.iconospinner)).setImageDrawable
						(Drawable.createFromPath(logo.getPath()));
			}
		}
	}
	row.setPadding(0, 10, 0, 10);
	return row;}}*/
	}
//    @Override public void onItemSelected(IcsAdapterView<?> spinAdapView, View view, int position, long id) {}
//    @Override public void onNothingSelected(IcsAdapterView<?> parent) {}
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.redactar, container, false); return view;}
    @Override public void onSaveInstanceState(Bundle outState) { 
    	super.onSaveInstanceState(outState); setUserVisibleHint(true); }
  
    @Override public void onActivityResult(int requestCode, int resultCode, final Intent intent) {
    	if (resultCode == Activity.RESULT_OK){
    	LinearLayout ll = new LinearLayout(sfa.getBaseContext());
    	ImageButton ib = new ImageButton(sfa.getBaseContext());
    	TextView tv = new TextView(sfa.getBaseContext());
    	ll.setOrientation(LinearLayout.HORIZONTAL);
    	ib.setImageDrawable(getResources().getDrawable(R.drawable.navigation_cancel));
    	ib.setBackgroundDrawable(getResources().getDrawable(R.drawable.botonfino));
    	tv.setTextAppearance(sfa, android.R.style.TextAppearance_Large);
    	LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
          LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
    	tv.setLayoutParams(param);
    	tv.setGravity(Gravity.CENTER_VERTICAL);
    	if(!files.getText().toString().equals(""))
    		files.setText(files.getText().toString()+"<!>");
  		if (requestCode == 1) { // Adjuntar archivo
  			
  			if (resultCode == Activity.RESULT_OK) {
  				tv.setText(intent.getData().getPath().substring(
  					intent.getData().getPath().lastIndexOf("/")+1));
  				ib.setOnClickListener(new OnClickListener(){ public void onClick(View v){
  					ladjuntos.removeView(ladjuntos.findViewWithTag(intent.getData().getPath()));
  					if(ladjuntos.getChildCount()==0) adjuntos.setVisibility(View.GONE); }});
  				ll.addView(ib); ll.addView(tv); ll.setTag(intent.getData().getPath());
  				ladjuntos.addView(ll); 
  				if(cuerpo.getText().toString().equals(""))
 						cuerpo.setText(intent.getData().getPath().substring(
 		  				intent.getData().getPath().lastIndexOf("/")+1));
  				files.setText(files.getText().toString()+intent.getData().getPath());
  				adjuntos.setVisibility(View.VISIBLE);
  			}
  		} else if (requestCode == 2) { // Sacar foto

  			if (resultCode == Activity.RESULT_OK) {
  				tv.setText(f.getPath().substring(f.getPath().lastIndexOf("/")+1));
  				ib.setOnClickListener(new OnClickListener(){ public void onClick(View v){
  					ladjuntos.removeView(ladjuntos.findViewWithTag(f.getPath()));
  					if(ladjuntos.getChildCount()==0) adjuntos.setVisibility(View.GONE); }});
  				ll.addView(ib); ll.addView(tv); ll.setTag(f.getPath());
  				ladjuntos.addView(ll); 
  				if(cuerpo.getText().toString().equals(""))
 						cuerpo.setText(f.getPath().substring(f.getPath().lastIndexOf("/")+1));
  				files.setText(files.getText().toString()+f.getPath());
  				adjuntos.setVisibility(View.VISIBLE);
  				
  			}
  		} else if (requestCode == 3) { // Grabar vídeo

  			if (resultCode == Activity.RESULT_OK) {
  				tv.setText(f.getPath().substring(f.getPath().lastIndexOf("/")+1));
  				ib.setOnClickListener(new OnClickListener(){ public void onClick(View v){
  					ladjuntos.removeView(ladjuntos.findViewWithTag(f.getPath()));
  					if(ladjuntos.getChildCount()==0) adjuntos.setVisibility(View.GONE); }});
  				ll.addView(ib); ll.addView(tv); ll.setTag(f.getPath());
  				ladjuntos.addView(ll); 
  				if(cuerpo.getText().toString().equals(""))
  					cuerpo.setText(f.getPath().substring(f.getPath().lastIndexOf("/")+1));
  				files.setText(files.getText().toString()+f.getPath());
  				adjuntos.setVisibility(View.VISIBLE);
  			}
  		} else if (requestCode == 4) { // Grabar voz

  			if (resultCode == Activity.RESULT_OK) {
  				if(intent.getData().getPath().contains("/external/")){
  					tv.setText(getRealPathFromURI(intent.getData()).substring(
    						getRealPathFromURI(intent.getData()).lastIndexOf("/")+1));
  					ib.setOnClickListener(new OnClickListener(){ public void onClick(View v){
  						ladjuntos.removeView(ladjuntos.findViewWithTag(getRealPathFromURI(intent.getData())));
  						if(ladjuntos.getChildCount()==0) adjuntos.setVisibility(View.GONE); }});
  					ll.addView(ib); ll.addView(tv); ll.setTag(getRealPathFromURI(intent.getData()));
  					ladjuntos.addView(ll); 
    				if(cuerpo.getText().toString().equals(""))
    					cuerpo.setText(getRealPathFromURI(intent.getData()).substring(
      						getRealPathFromURI(intent.getData()).lastIndexOf("/")+1));
    				files.setText(files.getText().toString()+getRealPathFromURI(intent.getData()));
    				adjuntos.setVisibility(View.VISIBLE);
  				}else{
  					tv.setText(intent.getData().getPath().substring(
  						intent.getData().getPath().lastIndexOf("/")+1));
  					ib.setOnClickListener(new OnClickListener(){ public void onClick(View v){
  						ladjuntos.removeView(ladjuntos.findViewWithTag(intent.getData().getPath()));
  						if(ladjuntos.getChildCount()==0) adjuntos.setVisibility(View.GONE); }});
  					ll.addView(ib); ll.addView(tv); ll.setTag(intent.getData().getPath());
  					ladjuntos.addView(ll); 
    				if(cuerpo.getText().toString().equals(""))
    					cuerpo.setText(intent.getData().getPath().substring(
  	  				intent.getData().getPath().lastIndexOf("/")+1));
    				files.setText(files.getText().toString()+intent.getData().getPath());
  				}}}
  		}}
    
    public String getRealPathFromURI(Uri contentUri){
    	String[] proj = { MediaStore.Audio.Media.DATA};
      CursorLoader cl = new CursorLoader(sfa, 
      		contentUri, proj, null, null, null);
      Cursor cursor = cl.loadInBackground();
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    }
    
  public boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
	    	.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected(); }

  /*public static void hideSpinnerDropDown(IcsSpinner spinner) {
    try{Method method = IcsSpinner.class.getDeclaredMethod("onDetachedFromWindow");
        method.setAccessible(true); method.invoke(spinner);
    }catch (Exception e) {e.printStackTrace();}}*/
   
 /* private class EnviarMensaje extends AsyncTask<String, Void, Boolean> {
  	ProgressDialog loading; int midbd=0; String tocho;
		protected void onPreExecute() {
     	loading = new ProgressDialog(sfa);
     	loading.setMessage(getString(R.string.writeSendingMessage));
     	loading.setCancelable(false); loading.show(); }

    protected void onPostExecute(final Boolean success) {
    	mensaje = new Msj(db.getLastMidEnv()+1,sharedPrefs.getInt("id",0),
					tocho,files.getText().toString(),new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),
					new SimpleDateFormat("HH:mm:ss",spanish).format(new Date()),"",
					eids[is.getSelectedItemPosition()],"A","","","","","E",
					sharedPrefs.getString("seudonimo",sharedPrefs.getString("nombre","")),midbd,"");
    	if(sharedPrefs.getBoolean("ms",false)==true) popupWindow.dismiss();
    	db.almacenarMensajeEnviado(mensaje);
    	if (loading.isShowing()) {loading.dismiss();
    	if(emailenviado==1){
    		
    		Toast.makeText(sfa, getString(R.string.writeEmailSent), Toast.LENGTH_LONG).show();
    		if(db.mensajePendiente() != null) db.deleteMensajeEnviado(db.mensajePendiente());
    	}
			Intent intent = new Intent(getActivity(), Mensajes.class);
			intent.putExtra("tipo", 1);
			startActivity(intent);
			getActivity().finish();}}
     	
  	@Override protected Boolean doInBackground(String... arg0) {
  		try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException e){e.printStackTrace();}
			try{DriverManager.setLoginTimeout(20);
				tocho = cuerpo.getText().toString();
				if(!files.getText().toString().equals("")){
					if(files.getText().toString().contains("<!>")){
						tocho+=getString(R.string.writeFilesHasBeenAttachedStart)+
								files.getText().toString().split("<!>").length+
								getString(R.string.writeFilesHasBeenAttachedEnd);
						String archivos[] = files.getText().toString().split("<!>");
					for(String s : archivos){
						float size = new File(s).length(); int div=0; String medida="";
						while(size>1024){ size=size/1024; div++;}
						switch (div){
							case 1: medida="KB"; break; case 2: medida="MB"; break;
							case 3: medida="GB"; break; default: medida="B";}
						tocho+="\n\t ·"+s.substring(s.lastIndexOf("/")+1)+
							"("+String.format("%.2f",size)+medida+")";}
					}else{
						float size = new File(files.getText().toString()).length(); int div=0; String medida="";
						while(size>1024){ size=size/1024; div++;}
						switch (div){
							case 1: medida="KB"; break; case 2: medida="MB"; break;
							case 3: medida="GB"; break; default: medida="B";}
						tocho+=getString(R.string.writeFilesAttached)+
							files.getText().toString().substring(files.getText().toString().lastIndexOf("/")+1)+
							"("+String.format("%.2f",size)+medida+")";}
				}
				Connection conn = DriverManager.getConnection(getString(R.string.dirbbdd));
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("INSERT INTO mensajeapp (clienteglobal,mensaje," +
					"fecharealizacion,horarealizacion,idestablecimiento,tipomensaje,estado," +
					"idmensajeappmovil,remitente) VALUES("+sharedPrefs.getInt("id",0)+",'"+tocho+"','"+//["+
					//db.getEstablecimiento(is.getSelectedItem().toString()).getReferencia()+"]','"+
					new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date())+"','"+
					new SimpleDateFormat("HH:mm:ss",spanish).format(new Date())+"',"+
					db.getEstablecimiento(is.getSelectedItem().toString()).getEid()+
					",'A','E',"+(db.getLastMidEnv()+1)+",'"+
					sharedPrefs.getString("seudonimo",sharedPrefs.getString("nombre",""))+"')" +
					" RETURNING id");
				if(rs.next()) midbd=rs.getInt(1);
  			rs.close(); st.close(); conn.close();
  			//Envío de e-mail
  			Est e = db.getEstablecimiento(is.getSelectedItem().toString());
  			Automailer am = new Automailer(getString(R.string.userauto),getString(R.string.passauto));
  			String[] mail = {"",""};
  			if(files != null) mail[0] = e.getMail();
  			if(e.getMsjmail()!=null ) mail[1] = e.getMsjmail();
  			am.setTo(mail);
  			if(sharedPrefs.getString("mail","").equals(""))
  				am.setFrom(getString(R.string.userauto));
  			else am.setFrom(sharedPrefs.getString("mail",""));
  			am.setSubject("Mensaje de "+
  				sharedPrefs.getString("seudonimo", sharedPrefs.getString("nombre", ""))+
  				" para "+e.getNombre());
  			am.setBody(tocho);
  			//Adjuntos
  			try{
  			if(!files.getText().toString().equals("")){
  			 if(files.getText().toString().contains("<!>")){
  				String filelist[] = files.getText().toString().split("<!>");
  				for(String s : filelist) am.addAttachment(s);
  			 } else am.addAttachment(files.getText().toString());
  			}if(am.send()) emailenviado=1;
  		}catch (Exception e1) {e1.printStackTrace();}
  	}catch (SQLException e){e.printStackTrace();}
		return true; }}*/
}