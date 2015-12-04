package com.disoft.distarea;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;*/
import com.disoft.distarea.ListaArticulos.Fila;
import com.disoft.distarea.extras.DatabaseHandler;
import com.disoft.distarea.models.Art;
import com.disoft.distarea.models.ArtEst;
import com.disoft.distarea.models.CliF;
import com.disoft.distarea.models.Est;
import com.disoft.distarea.models.Fam;
import com.disoft.distarea.models.Ped;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class TiendaVirtual extends AppCompatActivity {
	SharedPreferences sharedPrefs; DatabaseHandler db;
	View v; WebView wv; int contador=0, flagventana=0, idclif=0, flagskip=0, updated=0, noloop=0; double precio;
	String[] partes; String page="", url, urloriginal, pedido=""; Est e; int pidclif=0;
	Locale spanish = new Locale("es", "ES"); List<Ped> acalcular;
	SimpleDateFormat sdfdia = new SimpleDateFormat("yyyy-MM-dd",spanish),
					 postgrestyle = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",spanish);
	Map<Integer,String> urls; ArrayList<Fila> filasini;
	File fileparcel, dirparcel; Parcel parcel;
	/* Páginas Facdis
	 * 
	 * Inicial:
	 * http://172.26.0.250:8089/privadomovil.asp
	 * ?idcm=&nbcm=&tcm=&ucm=&emailcm=&aliascm=&tlfcm=&cpcm=&dircm=&tacm=&passcm=&passucm=
	 * 
	 * Redirect:
	 * http://172.26.0.250:8089/comprar.asp
	 * 
	 * Final de pedido:
	 * http://172.26.0.250:8089/pedidofinmovil.asp
	 * 
	 * Ejemplo registrado:
	 * Nuevo= ?idcm=15&tcm=0&ucm=nuevo"&tacm=false&passcm=n
	 * 
	 * Ejemplo sin registrar:
	 * Nuevo= ?&tcm=0&ucm=nuevo&aliascm=nuevo&tlfcm="670771457"&cpcm=35460&dircm="C/ Las Majadillas, 52"&tacm=false&passucm=n
	 * 
	 * Campos:
	 * idcm= UID; nbcm= Nombre; tcm=Tarifa; ucm=Codcliente; emailcm=Mail; 
	 * aliascm=*Alias; tlfcm=Telefono; cpcm=CodigoPostal; dircm=*Direccion; tacm=¿Es Tablet?
	 * passcm=Contraseña TV; passucm=Contraseña usuario
	 */
	
	//@SuppressLint({ "JavascriptInterface", "NewApi" })
	@Override protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true);
//		setSupportProgressBarIndeterminateVisibility(true);
		super.onCreate(savedInstanceState); setContentView(R.layout.tiendavirtual);
		v = (View) findViewById(R.id.base); wv = (WebView) findViewById(R.id.webView);
		db = new DatabaseHandler(this);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		e = db.getEstablecimiento(getIntent().getIntExtra("eid",0));
		ActionBar ab = getSupportActionBar();
		if(e!=null) ab.setTitle(e.getNombre());
		ab.setSubtitle("Tienda Virtual"); ab.setIcon(R.drawable.location_web_site);
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME
				|ActionBar.DISPLAY_HOME_AS_UP);
		//Página registro
		wv.getSettings().setJavaScriptEnabled(true);
    wv.getSettings().setPluginState(PluginState.ON);
    wv.getSettings().setAllowFileAccess(true);
    if(e!=null && e.getTv()!=null){ page=e.getTv();
    if(!e.getTv().substring(e.getTv().length()-1,e.getTv().length()).equals("/")) page+="/"; }
    //if(getIntent().getIntExtra("eid",0)!=0){
    	if(getIntent().getStringExtra("funcion") != null &&
    			getIntent().getStringExtra("funcion").equals("calcularprecio")){ 
    		url=page+"preciodistarea.asp";
    		idclif = getIntent().getIntExtra("idclif",0);
    		if(idclif!=0) acalcular = db.getPedidosPendientesDe(getIntent().getIntExtra("eid",0),idclif);
    		else acalcular = db.getPedidosPendientes(getIntent().getIntExtra("eid",0));
    		//Esquema: autoid[@]codigofacdis[@]nombrearticulo[@]unidades[@]¿precio?[@]oferta[@]fecha[@]cbarras[@]tipounidad[@]activo[/art]
    		//Ejemplo: -12[@]000132[@]Vestido blanco[@]2[@]56,78[@]101[@]21051990[@]182731897[@]UN[@]S[/art]
    		for(Ped p : acalcular){
    			if(idclif!=0) pidclif = p.getPid();
    			Art a = db.getArticulo(p.getAid());
    			pedido+=p.getAutoid()+"[@]"+p.getAfid()+"[@]"+a.getArticulo()+"[@]"+
    				p.getCantidad()+"[/ART]"; }
    		try{ pedido = URLEncoder.encode(pedido, "ISO-8859-1");
    		}catch(Exception e){ e.printStackTrace(); }
    		flagskip=1;
    	}else if(getIntent().getStringExtra("funcion") != null &&
    			getIntent().getStringExtra("funcion").equals("descarga")) url = page+"desprodt.asp";
    	else if (getIntent().getStringExtra("funcion") != null &&
    			getIntent().getStringExtra("funcion").equals("reservaonline")){
    		if (getIntent().getIntExtra("eid",0) != 0){
    			e = db.getEstablecimiento(getIntent().getIntExtra("eid",0));
    			ab.setTitle(e.getNombre()); ab.setSubtitle("Reservas"); }
    		else ab.setTitle("Reservas online");
    		
    		Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
    		if(display.getRotation()==Surface.ROTATION_90 || 
    				display.getRotation()==Surface.ROTATION_180)
    			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    		else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
    		url="http://www.distarea.es/privado/reservas.asp?d=M&dd_idcli="+sharedPrefs.getInt("id",0);
    		if(e!=null) url+="&cliente="+getIntent().getIntExtra("eid",0);
    		//else url="http://www.reservoyjuego.com/index.asp?desde=app&idcli="+sharedPrefs.getInt("id",0);
    	}else{
    String ini; try {
    	 ini = "privadomovil.asp?idcm="+sharedPrefs.getInt("id",0)+
				"&nbcm="+URLEncoder.encode(sharedPrefs.getString("seudonimo",""),"ISO-8859-1")+
				"&tcm="+e.getTarifa()+"&ucm="+e.getCodcliente()+
				"&emailcm="+sharedPrefs.getString("mail","")+
				"&aliascm="+sharedPrefs.getString("nombre","")+
				"&paiscm="+db.buscarCodigoPais(sharedPrefs.getString("pais","es"))+
				"&tlfcm="+sharedPrefs.getString("tel","")+"&cpcm="+sharedPrefs.getString("cp","") +
				"&dircm="+URLEncoder.encode(sharedPrefs.getString("dir",""),"ISO-8859-1")+
				"&tacm="+isTablet(getBaseContext())+"&passcm="+e.getPassTV()+
				"&passucm="+sharedPrefs.getString("pass", "");
    	 if(getIntent().getStringExtra("funcion") != null &&
     			getIntent().getStringExtra("funcion").equals("autoventa"))
    		 ini += "&referencia="+getIntent().getStringExtra("referencia");
    	 if(getIntent().getStringExtra("catalogo")!= null){
    		 	Log.e("EXTRA","Catálogo");
    			 ini += getIntent().getStringExtra("catalogo"); //"&hacer=catalogo&familia="+f.getFid());
    	 }
			} catch (UnsupportedEncodingException e1) {e1.printStackTrace();
				ini = "privadomovil.asp?idcm="+sharedPrefs.getInt("id",0)+
					"&nbcm="+sharedPrefs.getString("seudonimo","")+"&tcm="+e.getTarifa()+
					"&ucm="+e.getCodcliente()+"&emailcm="+sharedPrefs.getString("mail","")+
					"&aliascm="+sharedPrefs.getString("nombre","")+
					"&paiscm="+db.buscarCodigoPais(sharedPrefs.getString("pais","es"))+
					"&tlfcm="+sharedPrefs.getString("tel","")+"&cpcm="+sharedPrefs.getString("cp","") +
					"&dircm="+sharedPrefs.getString("dir","")+"&tacm="+isTablet(getBaseContext()) +
					"&passcm="+e.getPassTV()+"&passucm="+sharedPrefs.getString("pass", ""); }
    	url = page+ini;
    	Log.e("PAGE+INI",url+""); }
    if(page.startsWith(getString(R.string.paginaefacdis))) //http://efacdis.disoft.es/
    	page=getString(R.string.paginaefacdis);
    Log.e("PAGE",page);
    
    wv.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
    wv.setWebViewClient(new WebViewClient(){
    	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    		if (view.canGoBack()) view.goBack();
        if(contador<3)wv.loadUrl(e.getTv());
        else { wv.stopLoading();
        	wv.loadDataWithBaseURL("","<br/><br/><p style=\"text-align: center;\">"+
        			getString(R.string.error404)+"</p>", "text/html", "UTF-8", "");
        } contador++; 
        setProgressBarIndeterminateVisibility(false);
//        setSupportProgressBarIndeterminateVisibility(false);
	}
    	@Override public void onPageStarted(WebView view, String url, Bitmap favicon){
    		setProgressBarIndeterminateVisibility(true);
//        setSupportProgressBarIndeterminateVisibility(true);
        if(flagskip==1 && !url.equals("") && url.equals("preciodistareaok.asp")){
        	try{setProgressBarIndeterminateVisibility(true);
//				setSupportProgressBarIndeterminateVisibility(true);
				wv.loadUrl("javascript:window.HTMLOUT.processHTML4('<head>'" +
					"+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
			}catch(Exception e){e.printStackTrace();}
        }}
    	@Override public void onPageFinished(WebView view, String url){
    		if(url.contains("comprar.asp") || url.contains("catalogo.asp") /*&& !page.equals("http://efacdis.disoft.es/")*/)
          try{setProgressBarIndeterminateVisibility(true);
//        		setSupportProgressBarIndeterminateVisibility(true);
        		Log.e("ESTADO","Cargo comprar o catálogo");
        		Log.e("URLESTADO", url);
        		wv.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'" +
						"+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                  	setProgressBarIndeterminateVisibility(false);
//               		 	setSupportProgressBarIndeterminateVisibility(false);
          }catch(Exception e){e.printStackTrace();}
    		else if(url.contains("pedidofinmovil.asp"))
    			try{setProgressBarIndeterminateVisibility(true);
//    				setSupportProgressBarIndeterminateVisibility(true);
    				wv.loadUrl("javascript:window.HTMLOUT.processHTML2('<head>'" +
        					"+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
    				
    				//while(this.getProgressBarIndeterminateVisibility()==true)
    				wv.loadUrl(page+"pedidofinmovilok.asp");
    			}catch(Exception e){e.printStackTrace();}
    		else if(url.contains("pedidofinmovilok.asp")){
    			Intent intent = new Intent(TiendaVirtual.this, ListaCompra.class);
      		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
      		intent.putExtra("establecimiento",getIntent().getIntExtra("establecimiento",0));
      		startActivity(intent); finish();
    		}else if(url.contains("desprodtok.asp"))
    			try{setProgressBarIndeterminateVisibility(true);
//  				setSupportProgressBarIndeterminateVisibility(true);
  				wv.loadUrl("javascript:window.HTMLOUT.processHTML3('<head>'" +
    					"+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
    			}catch(Exception e){e.printStackTrace();
    		}else if(url.contains("preciodistareaok.asp"))
    			try{setProgressBarIndeterminateVisibility(true);
//  				setSupportProgressBarIndeterminateVisibility(true);
  				wv.loadUrl("javascript:window.HTMLOUT.processHTML4('<head>'" +
    					"+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
    			}catch(Exception e){e.printStackTrace(); Log.e("URL",url);
    		}else{ setProgressBarIndeterminateVisibility(false);
//   		 		   setSupportProgressBarIndeterminateVisibility(false);
   		 		   Log.e("NO CASE",url); }
      }});
    		if(flagskip==0) wv.loadUrl(url);
    		else{ wv.postUrl(url,EncodingUtils.getBytes("clidistarea="+sharedPrefs.getString("nombre", "")
    				+"&clif="+idclif+"&clifacdis="+e.getReferencia()+"&pedido="+pedido, "BASE64"));
    		}
			Log.e("URL",url);
	}//http://demotienda.disoft.es/privadomovil.asp?idcm=9&nbcm=Curro+4&tcm=0&ucm=&emailcm=disoftmovil@gmail.com&aliascm=curro4&paiscm=es&tlfcm=+3467771457&cpcm=35460&dircm=Direccion+Aleatoria74&tacm=false&passcm=&passucm=c
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if(sharedPrefs.getBoolean("ch",true)) v.performHapticFeedback(1);
    if (item.getItemId() == android.R.id.home) {
      CookieManager cookieManager = CookieManager.getInstance();
      cookieManager.removeAllCookie();
      Intent intent = new Intent(this, ListaCompra.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra("establecimiento",getIntent().getIntExtra("establecimiento",0));
      startActivity(intent); finish(); return true; } return false;}
	
	@Override public void onBackPressed() { if(flagventana==1) {}
		else{if(wv.canGoBack()) wv.goBack();
		else{
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();
			Intent intent = new Intent(this, ListaCompra.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("establecimiento",getIntent().getIntExtra("establecimiento",0));
			startActivity(intent); finish();}}}
	
	public class MyJavaScriptInterface{
		@JavascriptInterface
		public void processHTML(String html){
		    try{String[] paso1 = html.split("<distarea><cli>");
		    String[] paso2 = paso1[1].split("</cli></distarea>");
		    String[] variables = paso2[0].split("\\[@\\+\\]");
		    e.setCodcliente(variables[1]); e.setPassTV(variables[2]);
		    db.updateEstablecimiento(e);}catch(Exception e){e.printStackTrace();} }
		
		@JavascriptInterface
	    public void processHTML2(String html){
			setRequestedOrientation(getResources().getConfiguration().orientation);
			//Añado cadena pre = "-", para por si es un artículo de tienda genérica, que el nº FacDis sea negativo
    		String pre=""; if(e.getEid()<0) pre="-";
	    	String[] paso1 = html.split("<distarea>");
	    	if(paso1.length>1){
	    	String[] paso2 = paso1[1].split("</distarea>"); //paso2[0]=todo <distarea>
	    	Log.e("PASO2",paso2[0]);
	    	
	    	//Familias ¿Ya aplicado?
	    	/*String[] familias = paso2[0].split("&lt;@\\+\\+&gt;");
	    	for(int i=1;i<familias.length;i++){
	    		String[] camposf = familias[i].split("\\[@\\+\\]");
	    		String fecha = camposf[2].substring(0,10);
	    		//EJEMPLO Familia: <@++>Hombre[@+]1[@+]2013-05-20</@++>
	    		//camposf[0]=fid;camposf[1]=nombre;camposf[2]=fecha;
	    		Fam f = db.buscaFamilia(camposf[0],e.getEid()); //Comprueba existencia de la familia
	    		if(f==null){ db.addFamilia(camposf[0],camposf[1],fecha,e.getEid()); //Si no existe, añade
	    		Log.e("CREA FAMILIA",camposf[0]+"; "+camposf[1]+"; "+fecha+"; "+e.getEid());
	    		}else if(f.getFecha().compareTo(fecha)==1){ //Si existe, y fecha más reciente -> sobreescribe
	    				/*Log.e("EXISTE","-> ACTUALIZA");
	    				Log.e("FID",f.getFid()+"->"+camposf[0]);
	    				Log.e("NOMBRE",f.getNombre()+"->"+camposf[1]);
	    				Log.e("FECHA",f.getFecha()+"->"+fecha);
	    				Log.e("EID",f.getEid()+"->"+e.getEid());*//*
	    				db.updateFamilia(camposf[0],camposf[1],fecha,e.getEid());
	    			}
	    		}*/
	    		
	    	String[] pedidos = paso2[0].split("&lt;@\\+&gt;");
	    	for(int i=1;i<pedidos.length;i++){
	    		String[] campos = pedidos[i].split("\\[@\\+\\]");
	    		//EJEMPLO: <@+>Papas del pais[@+]1[@+]KI[@+][@+]0.5[@+]0.8[@+]Modelo:7735(NEGRO-M)
	    		//[@+]001[@+]Camisetas[@+]13-03-2015[@+]001002[@+]Hombre[@+]14-05-2014[@+]7735</@+>
	    		//campos[0]=articulo;campos[1]=idfacdis;campos[2]=tipo;
	    		//campos[3]=cbarras;campos[4]=cantidad;campos[5]=precio;campos[6]=obs;
	    		//campos[7]=familia;campos[8]=nombrefamilia;campos[9]=fechafamilia;
	    		//campos[10]=subfamilia;campos[11]=nombresubfamilia;
	    		//campos[12]=fechasubfamilia;campos[13]=modelo;
	    		/*Log.e("CAMPOS",campos[0]+";"+campos[1]+";"+campos[2]+";"+campos[3]+";"+campos[4]+";"+
	    			campos[5]+";"+campos[6]+";"+campos[7]+";"+campos[8]+";"+campos[9]+";"+campos[10]
	    			+";"+campos[11]+";"+campos[12]+";"+campos[13]);*/

	    		ArtEst ae = db.getArticuloEstablecimientoFacdis(pre+campos[1],e.getEid());
	    		Art a=null; if(ae!=null) a = db.getArticulo(ae.getAid());
	    		String tipo = "UN";
	    		if(campos[2].equals("KI")) tipo = "KG";
	    		else if (campos[2].equals("MT") || campos[2].equals("GR")) tipo = campos[2];
	    		if(a==null) { 
	    			a = new Art(db.getLastAid() + 1,campos[0], campos[3], tipo);
	    			db.addArticulo(a);
	    			db.addArticuloEstablecimiento(a.getAid(),pre+campos[1],e.getEid(),campos[7],campos[10],0,"S");
	    			//Familia
	    			//Log.e("FAMILIA",campos[8]);
	    			Fam f = db.buscaFamilia(campos[7],e.getEid()); //Comprueba existencia de la familia
		    		if(f==null){ db.addFamilia(campos[7],campos[8],campos[9],e.getEid()); //Si no existe, añade
		    		Log.e("CREA FAMILIA",campos[7]+"; "+campos[8]+"; "+campos[9]+"; "+e.getEid());
		    		}else if(f.getFecha().compareTo(campos[9])==1){ //Si existe, y fecha más reciente -> sobreescribe
		    				/*Log.e("EXISTE","-> ACTUALIZA");
		    				Log.e("FID",f.getFid()+"->"+campos[7]);
		    				Log.e("NOMBRE",f.getNombre()+"->"+campos[8]);
		    				Log.e("FECHA",f.getFecha()+"->"+campos[9]);
		    				Log.e("EID",f.getEid()+"->"+e.getEid());*/
		    				db.updateFamilia(campos[7],campos[8],campos[9],e.getEid());
		    			}
	    		}else{ // Actualiza
	    				a.setArticulo(campos[0]); a.setTipo(tipo); 
	    				a.setCbarras(campos[3]); db.updateArticulo(a); }
	    		//Mirar pedido pendiente
	    		Ped p = db.getPedidoPendiente(e.getEid()); String obs = "";
	    		if(campos.length>6){ if(campos[6].contains("<!--@+-->")) 
	    			obs=campos[6].replace("<!--@+-->", "").trim();
	    			else obs=campos[6].trim();}
	    		if(p==null){ //Pedido nuevo
						try{
							if(e.getEid()<0) // Lista
								p = new Ped(db.getLastAutoid()+1,e.getEid(), a.getAid(), e.getEid(),
									new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),
									NumberFormat.getNumberInstance(spanish).parse(campos[4]).doubleValue(),
									NumberFormat.getNumberInstance(spanish).parse(campos[5]).doubleValue(),
									obs,0,pre+campos[1],"",getIntent().getIntExtra("idclif",0),"N");
							else{
								p = new Ped(db.getLastAutoid()+1,sharedPrefs.getInt("nped",0)+1, a.getAid(), e.getEid(),
										new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),
										NumberFormat.getNumberInstance(spanish).parse(campos[4]).doubleValue(),
										NumberFormat.getNumberInstance(spanish).parse(campos[5]).doubleValue(),
										obs,0,pre+campos[1],"",getIntent().getIntExtra("idclif",0),"N");
										SharedPreferences.Editor spe = sharedPrefs.edit();
										spe.putInt("nped",sharedPrefs.getInt("nped",0)+1).commit();
							}
						db.addPedido(p);
						Ped pant = new Ped(p.getAutoid(),0, a.getAid(), e.getEid(),p.getFecha(),
								1.0,p.getPrecio(),"",2,p.getAfid(),"",p.getIdclif(),"N");
						db.addPedidoAnt(pant);
						}catch(Exception e1){e1.printStackTrace();}
	    				
	    		}else{ //Pedido ya existente
	    			//PARECE QUE ANTES ACTUALIZABA SI SE REPETÍA UN ARTÍCULO.
	    			//AHORA SE PERMITE QUE SE DUPLIQUEN, ASÍ QUE SÓLO AÑADE.
						//for(Ped pe : db.getAllArticulosPedidos(p.getPid())){
							//if(a.getAid()==pe.getAid()){
								try{p = new Ped(db.getLastAutoid()+1,p.getPid(), a.getAid(), e.getEid(),
										new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),
										NumberFormat.getNumberInstance(spanish).parse(campos[4]).doubleValue(),
										NumberFormat.getNumberInstance(spanish).parse(campos[5]).doubleValue(),
										obs.trim(),0,pre+campos[1],"",getIntent().getIntExtra("idclif",0),"N");
									db.addPedido(p);
									Ped pant = new Ped(p.getAutoid(),0, a.getAid(), e.getEid(),p.getFecha(),
										1.0,p.getPrecio(),"",2,p.getAfid(),"",p.getIdclif(),"N");
									db.addPedidoAnt(pant);
								}catch(Exception e1){e1.printStackTrace();} 
								 //break;} }
						/* REPETIDO POR MOTIVO DESCONOCIDO (¿ERROR DE COPYPASTE?)
						try{p = new Ped(db.getLastAutoid()+1,p.getPid(), a.getAid(), e.getEid(),
								new SimpleDateFormat("yyyy-MM-dd",spanish).format(new Date()),
								NumberFormat.getNumberInstance(spanish).parse(campos[4]).doubleValue(),
								NumberFormat.getNumberInstance(spanish).parse(campos[5]).doubleValue(),
								obs.trim(),p.getEstado(),pre+campos[1],"",getIntent().getIntExtra("idclif",0),"N");
						}catch(NumberFormatException e1){} catch(ParseException e1){}*/
						}
	    		 }
	    	setProgressBarIndeterminateVisibility(false);
//    		setSupportProgressBarIndeterminateVisibility(false);
	    	}else{
	    		Toast.makeText(getBaseContext(),getString(R.string.nohayarticulos),
	    				Toast.LENGTH_LONG).show();
	    		wv.loadUrl(page+"comprar.asp");}
			}
	    
		@JavascriptInterface public void processHTML3(String html){
			setRequestedOrientation(getResources().getConfiguration().orientation);
			if(noloop==0) new descargaArticulos(html).execute();
		}
	
		@JavascriptInterface public void processHTML4(String html){
			setRequestedOrientation(getResources().getConfiguration().orientation);
		    new actualizaPrecios(html).execute();
		}
		
    @JavascriptInterface public void showToast(String message) {
    	Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();}
    
    @JavascriptInterface public void showDialog(String message) {
    	final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
    	final View popupView = layoutInflater.inflate(R.layout.popupsino, null);
			final PopupWindow popupWindow = new PopupWindow(popupView,android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,false);
			popupWindow.setOutsideTouchable(false); flagventana=1;
			popupWindow.setBackgroundDrawable(getResources().getDrawable(
				android.R.drawable.alert_light_frame));
			popupWindow.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			{Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
			if(display.getRotation()==Surface.ROTATION_90)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else if(display.getRotation()==Surface.ROTATION_270){ 
				if(android.os.Build.VERSION.SDK_INT == 8)
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
			}else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			((TextView) popupView.findViewById(R.id.texto)).setText(message);
			ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
			si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
				popupWindow.dismiss(); flagventana=0; }});
			((ImageButton) popupView.findViewById(R.id.no)).setVisibility(View.GONE); }
    
    @JavascriptInterface public void showWindow(String message) {
    	final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
    	View popupView = layoutInflater.inflate(R.layout.popupsino, null);
			final PopupWindow popupWindow = new PopupWindow(popupView,android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,false);
			popupWindow.setOutsideTouchable(false); flagventana=1;
			popupWindow.setBackgroundDrawable(getResources().getDrawable(
				android.R.drawable.alert_light_frame));
			popupWindow.showAtLocation(findViewById(R.id.base),Gravity.CENTER, 0, 0);
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			{Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
			if(display.getRotation()==Surface.ROTATION_90)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else if(display.getRotation()==Surface.ROTATION_270){ 
				if(android.os.Build.VERSION.SDK_INT == 8)
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);}
			}else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			((TextView) popupView.findViewById(R.id.texto)).setText(message);
			ImageButton si = (ImageButton) popupView.findViewById(R.id.si);
			si.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			si.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
				wv.loadUrl("javascript:aceptarcontinuar()"); popupWindow.dismiss(); flagventana=0; }});
			ImageButton no = (ImageButton) popupView.findViewById(R.id.no);
			no.getBackground().setColorFilter(0xFF9ACDD2,PorterDuff.Mode.MULTIPLY);
			no.setOnClickListener(new OnClickListener(){@Override public void onClick(View v){
				popupWindow.dismiss(); flagventana=0; }});
    }
	}
	
	private class descargaArticulos extends AsyncTask<String, Integer, Boolean> {
		ProgressDialog pdloading; String html; private Context c;
		int nuevos=0, actualizados=0, flagacronimo=0, nmensaje=0; 
		public descargaArticulos(String html) { this.html=html; }
		
		protected void onPreExecute() { noloop=1; //Cierro reentradas, porque está habiendo un bucle raro...
			c = TiendaVirtual.this;
			filasini = new ArrayList<Fila>();
			urls = new HashMap<Integer,String>();
			pdloading = new ProgressDialog(c);
			//pdloading.setMessage("Descargando familias.\nEspere, por favor...");
			pdloading.setMessage("Descargando artículos.\nEspere, por favor...");
			pdloading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pdloading.setIndeterminate(false);
			pdloading.setCancelable(false); 
			//loading.takeKeyEvents(true);
			pdloading.show();}

		protected void onPostExecute(final Boolean success) {
			if(success){
				if (pdloading.isShowing()) {pdloading.dismiss();}
				//Limpiar cookies
		    	CookieManager cookieManager = CookieManager.getInstance();
		    	cookieManager.removeAllCookie();
			  		if(nuevos>0 && actualizados>0) Toast.makeText(getBaseContext(), 
			  				"Descargados "+nuevos+" artículos.\nActualizados "+
			  				actualizados+" artículos.",Toast.LENGTH_LONG).show();
			  		else if(nuevos>0) Toast.makeText(getBaseContext(), 
			  				"Descargados "+nuevos+" artículos.",Toast.LENGTH_LONG).show();
			  		else if(actualizados>0) Toast.makeText(getBaseContext(), 
			  				"Actualizados "+actualizados+" artículos.",Toast.LENGTH_LONG).show();
			  		else{
			  			Log.e("NHN","NO HAY NOVEDADES");
			  			Toast.makeText(getBaseContext(),"No hay novedades.",Toast.LENGTH_LONG).show();
			  		}
			  		
			  		//Borro parcel anterior, si existe, y autocrea siguiente.
			  		dirparcel = new File(getExternalCacheDir()+File.separator+"parcel"+File.separator);
					if(dirparcel.exists()){ 
						File archivo = new File(getExternalCacheDir()+File.separator+"parcel"+File.separator+e.getEid());
						archivo.delete();}
			  		//new backupParcel(1).execute();
					//Este tramo está en el backupParcel. Retirar si se recupera aquí.
					
					//Añado descargaClientes
					if(e.getEid()==sharedPrefs.getInt("solicitacliest",0) && 
							e.getConfigura().contains(",V,")){
						Log.e("ENTRA","DescargaClientes");
						new descargaClientes().execute();
					}else{
						Intent intent = new Intent(TiendaVirtual.this, ListaCompra.class);
						if(urls.isEmpty()){
					  		if(getIntent().getStringExtra("intent").equals("listacompra"))
					  			startActivity(intent);
					  		finish();
				  		}else new descargaImagenes().execute();
					}
			  		
		    	}else{ Toast.makeText(getBaseContext(),getString(R.string.nohayarticulos),
		    				Toast.LENGTH_LONG).show();
		    		wv.loadUrl(page+"comprar.asp");
		    		}
		}
		
		protected void onProgressUpdate(Integer... progreso) {
			/*switch (nmensaje){
			case 1:
				runOnUiThread(new Runnable() { @Override public void run() {    
				pdloading.setMessage("Descargando artículos.\nEspere, por favor...");
			    }});
				break;
			case 2:
				runOnUiThread(new Runnable() { @Override public void run() {
				pdloading.setMessage("Verificando Base de Datos...");
				pdloading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				}});
				break;
			}*/
            pdloading.setProgress(progreso[0]);
            if(nmensaje==2){
            	pdloading.dismiss();
            	pdloading = new ProgressDialog(c);
            	pdloading.setMessage("Verificando Base de Datos...");
            	pdloading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            	pdloading.show();
            }
		}

		/*@Override protected Boolean doInBackground(String... arg0) {
	    	String[] paso1 = html.split("<distarea>");
	    	if(paso1.length>1){
	    	String[] paso2 = paso1[1].split("</distarea>"); //paso2[0]=todo <distarea>
	    	//Familias
	    	String[] familias = paso2[0].split("&lt;@\\+\\+&gt;");
	    	Log.e("TOCHO", paso2[0]);
	    	writeToFile(paso2[0],"DESPRODT");
	    	pdloading.setMax(familias.length-1);
	    	for(int i=1;i<familias.length;i++){
	    		String[] camposf = familias[i].split("\\[@\\+\\]");
	    		String fecha = camposf[2].substring(0,10);
	    		//EJEMPLO Familia: <@++>Hombre[@+]1[@+]2013-05-20</@++>
	    		//camposf[0]=fid;camposf[1]=nombre;camposf[2]=fecha;
	    		Fam f = db.buscaFamilia(camposf[0],e.getEid()); //Comprueba existencia de la familia
	    		if(f==null){ db.addFamilia(camposf[0],camposf[1],fecha,e.getEid()); //Si no existe, añade
	    		Log.e("FID",""+camposf[0]);
	    		Log.e("FAMILIA",""+camposf[1]);
	    		}else if(f.getFecha().compareTo(fecha)==1){ //Si existe, y fecha más reciente -> sobreescribe
	    				db.updateFamilia(camposf[0],camposf[1],fecha,e.getEid());
	    		}
	    		//publishProgress((int) (i / (float) familias.length * 100));
	    		publishProgress(i);
	    	}
	    	
	    	/+//Subfamilias
	    	String[] subfamilias = paso2[0].split("&lt;@\\+@&gt;");
	    	for(int i=1;i<subfamilias.length;i++){
	    		String[] campossf = subfamilias[i].split("\\[@\\+@\\]");
	    		String fecha = campossf[3].split("&lt;/@\\+@&gt;")[0];
	    		//EJEMPLO Subfamilia: <@+@>Hombre[@+]1*[@+]2[@+]2013-05-20</@+@>
	    		//campossf[0]=nombre;campossf[1]=fid*;campossf[2]=sfid;campossf[3]=fecha;
	    		Fam f = db.buscaSubfamilia(campossf[2],e.getEid()); //Comprueba existencia de la subfamilia
	    		if(f!=null && f.getFecha().compareTo(fecha)==1) //Si existe, y fecha más reciente -> sobreescribe
	    			db.updateSubfamilia(campossf[2],campossf[1],campossf[0],fecha,e.getEid());
	    		else db.addSubfamilia(campossf[2],campossf[1],campossf[0],fecha,e.getEid()); //Si no existe, añade
	    	} //Fin subfamilias+/
	    		
	    	String[] pedidos = paso2[0].split("&lt;@\\+&gt;");
	    	/*for(int i=1;i<pedidos.length;i++){
	    		String[] camposp = pedidos[i].split("\\[@\\+\\]");
	    		Log.e("MINICHECK","["+i+"/"+pedidos.length+"]"+camposp[0]+";"+camposp[1]+";"+camposp[2]+";"+camposp[3]+";"+camposp[4]+";"+camposp[5]+";"+camposp[6]);
	    	}+/
	    	nmensaje=1;
	    	pdloading.setMax(pedidos.length-1);
	    		int incr=1;
	    	for(int i=1;i<pedidos.length;i++){
	    		//Log.e("PEDIDOS", pedidos[i]);
	    		String[] camposp = pedidos[i].split("\\[@\\+\\]");
	    		//EJEMPLO Articulo: <@+>Papas del pais[@+]PAP123[@+]3.75[@+]KI*[@+]123456*[@+]1[@+]2[@+]http://...</@+>
	    		//camposp[0]=articulo;camposp[1]=idfacdis;camposp[2]=precio;camposp[3]=tipo*;
	    		//camposp[4]=cbarras*;camposp[5]=familia*;camposp[6]=subfamilia*;camposp[7]=rutafoto;
	    		
	    		//Tratado de campos Unidad, Precio y Subfamilia
	    		String tipo="UN", sf="";
	    		if(camposp[3].equals("KI")) tipo = "KG";
	    		else if (camposp[3].equals("MT") || camposp[3].equals("GR")) tipo = camposp[3];
	    		if(!camposp[2].equals(""))
	    			precio = Double.parseDouble(camposp[2].replace(",","."));
	    		else precio = 0.0;
	    		if(!camposp[6].equals("")){ 
    				if(camposp[6].contains("<!--@+-->")) 
    					sf=camposp[6].replace("<!--@+-->", "").trim();
    				else sf=camposp[6].trim(); 
    			}
	    		
	    		//Creación de clases en sus Listas
	    		int aid = db.getLastAid() + incr;
	    		
	    		ArtEst ae = db.getArticuloEstablecimientoFacdis(camposp[1], e.getEid());
	    		if(ae!=null) aid = ae.getAid();
	    		else ae = new ArtEst(aid,camposp[1],e.getEid(),camposp[5],sf,0,"S");
	    		Art a 		= new Art(aid,camposp[0], camposp[4], tipo);
	    		int autoid = db.getLastAutoidAnt()+incr, estado=0;
	    		if(db.getArticuloPedidoAnt(0,aid,e.getEid())!=null){
	    			autoid = db.getArticuloPedidoAnt(0,aid,e.getEid()).getAutoid();
	    			estado = db.getArticuloPedidoAnt(0,aid,e.getEid()).getEstado();
	    		}
	    		Ped p = new Ped(autoid,0,aid,e.getEid(),sdfdia.format(new Date()),
		    				1,precio,"",estado,camposp[1],"",
		    				getIntent().getIntExtra("idclif",0),"S");
	    		if(autoid==db.getLastAutoidAnt()+incr) incr++;
	    		filasini.add(new Fila(a,p,ae));
	    		nuevos++;
	    		
	    		//publishProgress((int) (i / (float) pedidos.length * 100));
	    		//Log.e("I",i+"");
	    		//Integro imágenes en cada consulta de archivo
	    		if(getIntent().getBooleanExtra("imagenes",true)){
	    			String url = camposp[7].substring(0,camposp[7].indexOf("<!--@+-->"));
	    			if(url!=null && !url.equals("") && !url.contains("/nofoto.gif") 
	    					&& checkFotoOnline(page+url)){ urls.put(a.getAid(), url);
	    					Log.e("POS",""+urls.size());
	    	    			Log.e("AID",""+a.getAid());
	    	    			Log.e("URL",""+url);
	    					}
	    		}
	    		publishProgress(i);
	    	}
	    	nmensaje=2;
	    	publishProgress(0);
	    		db.reemplazarArticulos(filasini);
	    	
	    	return true;
		}else return false;
	}*/
		
		@Override protected Boolean doInBackground(String... arg0) {
	    	String[] paso1 = html.split("<distarea>");
	    	if(paso1.length>1){
	    	String[] paso2 = paso1[1].split("</distarea>"); //paso2[0]=todo <distarea>
	    	//Familias
	    	String[] familias = paso2[0].split("&lt;@\\+\\+&gt;");
	    	Log.e("TOCHO", paso2[0]);
	    	writeToFile(paso2[0],"DESPRODT");
	    	pdloading.setMax(familias.length-1);
	    	for(int i=1;i<familias.length;i++){
	    		String[] camposf = familias[i].split("\\[@\\+\\]");
	    		String fecha = camposf[2].substring(0,10);
	    		//EJEMPLO Familia: <@++>Hombre[@+]1[@+]2013-05-20</@++>
	    		//camposf[0]=fid;camposf[1]=nombre;camposf[2]=fecha;
	    		Fam f = db.buscaFamilia(camposf[0],e.getEid()); //Comprueba existencia de la familia
	    		if(f==null){ db.addFamilia(camposf[0],camposf[1],fecha,e.getEid()); //Si no existe, añade
	    		Log.e("FID",""+camposf[0]);
	    		Log.e("FAMILIA",""+camposf[1]);
	    		}else if(f.getFecha().compareTo(fecha)==1){ //Si existe, y fecha más reciente -> sobreescribe
	    				db.updateFamilia(camposf[0],camposf[1],fecha,e.getEid());
	    		}
	    		publishProgress(i);
	    	}
	    	
	    	/*//Subfamilias
	    	String[] subfamilias = paso2[0].split("&lt;@\\+@&gt;");
	    	for(int i=1;i<subfamilias.length;i++){
	    		String[] campossf = subfamilias[i].split("\\[@\\+@\\]");
	    		String fecha = campossf[3].split("&lt;/@\\+@&gt;")[0];
	    		//EJEMPLO Subfamilia: <@+@>Hombre[@+]1*[@+]2[@+]2013-05-20</@+@>
	    		//campossf[0]=nombre;campossf[1]=fid*;campossf[2]=sfid;campossf[3]=fecha;
	    		Fam f = db.buscaSubfamilia(campossf[2],e.getEid()); //Comprueba existencia de la subfamilia
	    		if(f!=null && f.getFecha().compareTo(fecha)==1) //Si existe, y fecha más reciente -> sobreescribe
	    			db.updateSubfamilia(campossf[2],campossf[1],campossf[0],fecha,e.getEid());
	    		else db.addSubfamilia(campossf[2],campossf[1],campossf[0],fecha,e.getEid()); //Si no existe, añade
	    	} //Fin subfamilias*/
	    		
	    	String[] pedidos = paso2[0].split("&lt;@\\+&gt;");
	    	/*for(int i=1;i<pedidos.length;i++){
	    		String[] camposp = pedidos[i].split("\\[@\\+\\]");
	    		Log.e("MINICHECK","["+i+"/"+pedidos.length+"]"+camposp[0]+";"+camposp[1]+";"+camposp[2]+";"+camposp[3]+";"+camposp[4]+";"+camposp[5]+";"+camposp[6]);
	    	}*/
	    	nmensaje=1;
	    	pdloading.setMax(pedidos.length-1);
	    	int incr=1, lastaid=db.getLastAid(), lastautoid = db.getLastAutoidAnt();
	    	String[] valores = new String[]{"","",""};
	    	
	    	for(int i=1;i<pedidos.length;i++){
	    		//Log.e("PEDIDOS", pedidos[i]);
	    		String[] camposp = pedidos[i].split("\\[@\\+\\]");
	    		//EJEMPLO Articulo: <@+>Papas del pais[@+]PAP123[@+]3.75[@+]KI*[@+]123456*[@+]1[@+]2[@+]http://...</@+>
	    		//camposp[0]=articulo;camposp[1]=idfacdis;camposp[2]=precio;camposp[3]=tipo*;
	    		//camposp[4]=cbarras*;camposp[5]=familia*;camposp[6]=subfamilia*;camposp[7]=rutafoto;
	    		
	    		//Tratado de campos Unidad, Precio y Subfamilia
	    		String tipo="UN", sf="";
	    		if(camposp[3].equals("KI")) tipo = "KG";
	    		else if (camposp[3].equals("MT") || camposp[3].equals("GR")) tipo = camposp[3];
	    		if(!camposp[2].equals(""))
	    			precio = Double.parseDouble(camposp[2].replace(",","."));
	    		else precio = 0.0;
	    		if(!camposp[6].equals("")){ 
    				if(camposp[6].contains("<!--@+-->")) 
    					sf=camposp[6].replace("<!--@+-->", "").trim();
    				else sf=camposp[6].trim(); 
    			}
	    		
	    		//Creación de clases en sus Listas
	    		int aid = lastaid + incr; //Ya no hay creación dinámica, no se inserta en BD hasta el final.
	    		ArtEst ae;
	    		
	    		//Idea para consulta NOT IN () --> Activos="N" (No vienen de la última descarga, dentro del EID)
	    		if(getIntent().getBooleanExtra("borrados",false)){
	    			ae = db.getArticuloEstablecimientoFacdis(camposp[1], e.getEid());
		    		if(ae!=null) aid = ae.getAid();
	    		}
	    		
		    	//ae = new ArtEst(aid,camposp[1],e.getEid(),camposp[5],sf,0,"S");
	    		
	    		//Art a = new Art(aid,camposp[0], camposp[4], tipo);
	    		
	    		int autoid = lastautoid+incr, estado=0;
	    		if(!getIntent().getBooleanExtra("borrados",false)){
	    			Ped pant = db.getArticuloPedidoAnt(0,aid,e.getEid()); 
	    			if(pant!=null){ actualizados++;
	    				autoid = pant.getAutoid(); estado = pant.getEstado(); 
	    			} else nuevos++;
	    		} else nuevos++;
	    			
	    		/*Ped p = new Ped(autoid,0,aid,e.getEid(),sdfdia.format(new Date()),
		    				1,precio,"",estado,camposp[1],"",
		    				getIntent().getIntExtra("idclif",0),"S");*/
	    		
	    		if(autoid==lastautoid+incr) incr++;
	    		//filasini.add(new Fila(a,p,ae));
	    		
	    		//Añado todos los campos para la base de datos, con el formato directo
	    		valores[0]+="("+aid+",'"+camposp[0]+"','"+camposp[4]+"','"+tipo+"'),"; //Art
	    		valores[1]+="("+aid+",'"+camposp[1]+"',"+e.getEid()+",'"+camposp[5]+"','"+sf+"',0,'S'),"; //ArtEst
	    		valores[2]+="("+autoid+",0,"+aid+","+e.getEid()+",'"+sdfdia.format(new Date())+"',1,"+
	    				precio+",'',"+estado+",'"+camposp[1]+"','',"+getIntent().getIntExtra("idclif",0)+",'S'),"; //Ped
	    		
	    		//Integro imágenes en cada consulta de archivo
	    		if(getIntent().getBooleanExtra("imagenes",true)){
	    			String url = camposp[7].substring(0,camposp[7].indexOf("<!--@+-->"));
	    			/*if(url!=null && !url.equals("") && !url.contains("/nofoto.gif") 
	    					&& checkFotoOnline(page+url)){ urls.put(aid, url);
	    					Log.e("POS",""+urls.size());
	    	    			Log.e("AID",""+aid);
	    	    			Log.e("URL",""+url);
	    					}*/ //Skipping checkFotoOnline
	    			if(url!=null && !url.equals("") && !url.contains("/nofoto.gif")) urls.put(aid, url);
	    		}
	    		publishProgress(i);
	    	}
	    	nmensaje=2;
	    	publishProgress(0);
	    		//db.reemplazarArticulos(filasini);
	    		valores[0]=valores[0].substring(0,valores[0].length()-1);
	    		valores[1]=valores[1].substring(0,valores[1].length()-1);
	    		valores[2]=valores[2].substring(0,valores[2].length()-1);
	    		db.reemplazarArticulos2(valores);
	    	
	    	return true;
		}else return false;
	}
	}
	
	//Background descargaImágenes
	private class descargaImagenes extends AsyncTask<Void, Integer, Boolean> {
		ProgressDialog loading;
		
		protected void onPreExecute() {
			ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(getBaseContext());
		    config.threadPriority(Thread.NORM_PRIORITY - 2);
			config.denyCacheImageMultipleSizesInMemory();
			//config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
			//config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
			//config.tasksProcessingOrder(QueueProcessingType.LIFO);
			config.writeDebugLogs();
		    ImageLoader.getInstance().init(config.build());
			
			loading = new ProgressDialog(TiendaVirtual.this);
			loading.setMessage("Descargando imágenes de artículos.\nEspere, por favor...");
			loading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			loading.setIndeterminate(false);
			loading.setMax(urls.size());
			loading.setCancelable(false); loading.show(); }

		protected void onPostExecute(final Boolean success) {
			if(success){
				if (loading.isShowing()) {loading.dismiss();}
				//Limpiar cookies
		    	CookieManager cookieManager = CookieManager.getInstance();
		    	cookieManager.removeAllCookie();
		      			Intent intent = new Intent(TiendaVirtual.this, ListaCompra.class);
			  		if(getIntent().getStringExtra("intent").equals("listacompra"))
			  			startActivity(intent);
			  		finish(); }
		}
			
		protected void onProgressUpdate(Integer... progreso) {
            loading.setProgress(progreso[0]); }

		@Override protected Boolean doInBackground(Void... arg0) {
			Iterator<Entry<Integer, String>> i = urls.entrySet().iterator();
			int contador=0;
			while(i.hasNext()){ contador++;
				//i.next();
			Map.Entry<Integer,String> par = i.next();
		String rutitafoto=par.getValue();
		//String[] pacro=paso2[0].split("<alias>");
		//if(pacro.length>1 && !pacro[1].equals("")) rutitafoto=pacro[1].split("</alias>")[0];
		//rutitafoto=camposp[7].substring(0,camposp[7].indexOf("<!--@+-->"));
		if(!rutitafoto.equals("") && !rutitafoto.equals("/nofoto.gif")){
			Log.e("rutitafoto",rutitafoto);
			//rutitafoto=page+"files/foto/articulo/"+ae.getAfid()+"_1";
			//rutitafoto=page+"files/eugeni738/foto/articulo/"+ae.getAfid()+"_1";
		//else{ 
			
		/*runOnUiThread(new Runnable(){public void run(){
		//loading.setMessage("Descargando imágenes de los artículos.\nEspere, por favor...");
		loading.setProgress(0); }});
		loading.setMax(pedidos.length);*/
		URL url; URLConnection conn; BufferedInputStream inStream; 
	    BufferedOutputStream outStream; FileOutputStream fileStream;
	    File carpeta = new File( getExternalCacheDir()+File.separator+"articulos"+File.separator),
	    	 carpeta2 = new File( getExternalCacheDir()+File.separator+"articulos"+File.separator+e.getEid()+File.separator);
	    if(!carpeta.exists()){ carpeta.mkdir();}
	    if(!carpeta2.exists()){ carpeta2.mkdir();}
	    	//Ya tengo un .nomedia en la carpeta anterior
	    	/*try{new File (carpeta+ File.separator+".nomedia").createNewFile();
	    	}catch (IOException e){e.printStackTrace();}*/
	    //}
	    
			try{ 
				String stringUrl="";
				stringUrl = page+rutitafoto;
				url =new URL(stringUrl);
				String ext="";
				ext=rutitafoto.substring(rutitafoto.lastIndexOf(".")); //Fin parche sin extensión
				//Log.e("EXT",""+ext);
			
			  
			  Log.e("URL",url.toString());
			  conn = url.openConnection();
		      conn.setUseCaches(false);
		      inStream = new BufferedInputStream(conn.getInputStream());
		      if(inStream!=null){
	    	      File outFile = new File (carpeta2 + File.separator + "temp" + ext); 
	    	    		  //db.getArticulo(ae.getAid()).getArticulo() 
	    	      //Log.e("OUTFILE",""+outFile.getAbsolutePath());
	    	      fileStream = new FileOutputStream(outFile);
	    	      outStream = new BufferedOutputStream(fileStream, 4096);
	    	      byte[] data = new byte[4096]; int bytesRead = 0;
	    	      while((bytesRead = inStream.read(data, 0, data.length)) >= 0)
	    	          outStream.write(data, 0, bytesRead);
	    	      outStream.close(); fileStream.close(); inStream.close();
	    	      Log.e("OUT",""+carpeta2+File.separator+par.getKey()+".png");
	    	      //Convertir archivo a .png 100x100
	    	      FileOutputStream out = new FileOutputStream(new File(
	    	    	    		carpeta2+File.separator+par.getKey()+".png"));
	    	      		
	    	    	    resizeBitmap(outFile.getAbsolutePath(),100,100).compress(Bitmap.CompressFormat.PNG, 100, out);
	    		 //XXX Puede que tenga que mantener coherencia al borrar artículos, borrar imagen
		      }
		     }catch(MalformedURLException e){} catch(FileNotFoundException e){} catch(Exception e){
		    	 e.printStackTrace(); } 
			try{
	    		File temp = new File(carpeta2+File.separator+"temp.jpg");
	    		if(temp.exists()) temp.delete();
	    		temp = new File(carpeta2+File.separator+"temp.bmp");
	    		if(temp.exists()) temp.delete();
	    		temp = new File(carpeta2+File.separator+"temp.png");
	    		if(temp.exists()) temp.delete();
	    		temp = new File(carpeta2+File.separator+"temp.gif");
	    		if(temp.exists()) temp.delete();
	    	}catch(Exception e){e.printStackTrace();}
	    	}
		publishProgress(contador);
		}
		if(contador!=urls.size())
			Log.e("FIN INESPERADO",""+contador+"/"+urls.size());
		return true; 
		
		}
	}
	/* APLICAR ESTO A LA DESCARGA NUEVA
	 * ImageSize mini = new ImageSize(100, 100);
    ImageLoader.getInstance().loadImage(url, mini, new ImageLoadingListener() {
        @Override public void onLoadingStarted(String imageUri, View view) {
        	Log.e("URL",""+imageUri); }
        @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {}
        @Override public void onLoadingCancelled(String imageUri, View view) {}
        @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            //loadedImage.compress ?
        }
    });
	 */
	
	private class descargaClientes extends AsyncTask<String, Void, Boolean> {    	
      	ProgressDialog loading;
      	
      	protected void onPreExecute() {
         	loading = new ProgressDialog(TiendaVirtual.this);
         	loading.setMessage("Descargando sus clientes...");
         	loading.setCancelable(false); loading.show(); 
        }

        protected void onPostExecute(final Boolean success) {
        	if (loading.isShowing()) loading.dismiss();
        	Intent intent = new Intent(TiendaVirtual.this, ListaCompra.class);
			if(urls.isEmpty()){
		  		if(getIntent().getStringExtra("intent").equals("listacompra"))
		  			startActivity(intent);
		  		finish();
	  		}else new descargaImagenes().execute();
        }
         	
        @Override protected Boolean doInBackground(String... arg0) {
        	Log.e("DENTRO","DescargaClientes");
        	ArrayList<Integer> activos = new ArrayList<Integer>();
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
    			 			"'::time <= hora_ultima_revision" +*/
    			 			" ORDER BY referencia ASC");
    			 while(rs.next()){
    				 if(rs.getString(18).equals("S")){
    				 CliF c = new CliF(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
    						 rs.getString(5), "", "", rs.getString(8), String.valueOf(rs.getInt(9)), 
    						 "","","",rs.getString(13), rs.getInt(14), rs.getInt(15), "M", "");
    				 activos.add(rs.getInt(1));
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
    			 
    			 //Borrado de clientes desaparecidos
    			 List<CliF> borrarinactivos = db.getAllClientesFAV(e.getEid());
    			 for(CliF c : borrarinactivos){
    				 int flag=0;
    				 for(int a : activos){
    					 Log.e("COMPARA","a="+a+"="+c.getIdcf()+"IDCF");
    					 if(a==c.getIdcf()){
    						 Log.e("MATCH",a+"="+c.getIdcf());
    						 flag=1; break;}
    				 }
    				 if(flag==0){ Log.e("BORRANDO",""+c.getNombre()); 
    					 db.deleteClienteF(c.getIdcf()); }
    			 }
    			 
    		}catch (SQLException e){ e.printStackTrace(); }
        	/*else clientes = (ArrayList<CliF>) db.getAllClientesF(e.getEid());*/
    		Log.e("SALE","DescargaClientes");return true; 
      	}
    }
	
	public Bitmap resizeBitmap(String path, int targetW, int targetH) {
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    int scaleFactor = 1;
	    if ((targetW > 0) || (targetH > 0)) {
	            scaleFactor = Math.min(photoW/targetW, photoH/targetH);        
	    }

	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    return BitmapFactory.decodeFile(path, bmOptions);            
	}

	private class actualizaPrecios extends AsyncTask<String, Void, Boolean> {
		String html; public actualizaPrecios(String html) { this.html=html; }

		protected void onPostExecute(final Boolean success) {
			if(success){
				setProgressBarIndeterminateVisibility(false);
//	    		setSupportProgressBarIndeterminateVisibility(false);
	    		Intent intent = new Intent(TiendaVirtual.this, ListaCompra.class);
		  		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		  		intent.putExtra("eid",getIntent().getIntExtra("eid",0));
		  		if(getIntent().getIntExtra("idclif", 0)!=0)
		  			intent.putExtra("idclif", getIntent().getIntExtra("idclif", 0)); 
	    		if(updated>1)
	    			Toast.makeText(getBaseContext(), "Actualizados "+updated+" artículos del pedido.", Toast.LENGTH_LONG).show();
	    		else if(updated==1)
	    			Toast.makeText(getBaseContext(), "Actualizado un artículo del pedido.", Toast.LENGTH_LONG).show();
	    		else{ intent.putExtra("auto",true);
	    			Toast.makeText(getBaseContext(), "No hay novedades.", Toast.LENGTH_LONG).show(); }
	    		startActivity(intent); finish();
			}
		}

		@Override protected Boolean doInBackground(String... arg0) {
			String[] paso1 = html.split("<distarea>");
		    String[] paso2 = paso1[1].split("</distarea>");
		    String[] articulos = paso2[0].split("\\[/ART\\]");
		    int i=0; double temprecio=0.0; String tempart="";
		    for(String s : articulos){ 
		    	if (i==articulos.length-1){ //Guardar idfacdis
		    	e.setReferencia(articulos[i]);
		    }else{
			   	String[] variables = s.split("\\[@\\]");
			   	//if(!variables[0].trim().equals("")){
			   	//Esquema: autoid[@]codigofacdis[@]nombrearticulo[@]unidades[@]¿precio?[@]
			   	//oferta[@]fecha[@]cbarras[@]tipounidad[@]activo[/art]
			   	//try{
			   	Ped ped = db.getPedido(Integer.parseInt(variables[0].trim()));
			   	Ped coped = ped;
			   	Art a = db.getArticulo(ped.getAid());
			   	ArtEst ae = db.getArticuloEstablecimiento(a.getAid(), e.getEid());
			   	if(ae!=null){ ae.setAfid(variables[1]); ae.setActivo(variables[9]);
			   		db.updateArticuloEstablecimiento(ae); }
			   	tempart = a.getArticulo();
			   	a.setArticulo(variables[2]); a.setCbarras(variables[7]);
			   	a.setTipo(variables[8]); db.updateArticulo(a);
			   	ped.setAfid(variables[1]); ped.setOferta(variables[5]);
			   	temprecio = ped.getPrecio();
			   	ped.setCantidad(Double.parseDouble(variables[3]));
			   	ped.setPrecio(Double.parseDouble(variables[4].replaceAll(",",".")));
			   	ped.setFecha(variables[6]); ped.setPreciomanual("N");
			   	if(db.updatePedido(ped)>0 && (temprecio!=ped.getPrecio())){ 
			   		updated++; //Revisar: cambios en PedAnt
			   		ped.setPid(0); ped.setCantidad(1); ped.setObservacion("");
			   		ped.setEstado(0); ped.setIdclif(0);
			   		db.updatePedidoAnt(ped);
			   	}else{ //Salto preguntar por el precio de nuevo.
			   		coped.setPreciomanual("N");
			   		db.updatePedido(coped);
			   	//Revisar: cambios en PedAnt
			   		coped.setPid(0); coped.setCantidad(1); coped.setObservacion("");
			   		coped.setEstado(0); coped.setIdclif(0);
			   	//Revisión de datos
			   		db.updatePedidoAnt(coped);
			   	}//}catch(Exception e){ e.printStackTrace(); } 
			   	} i++; }
		    return true;
		}
	}

	private void writeToFile(String data, String name) {
	    try{OutputStreamWriter osw = new OutputStreamWriter(openFileOutput("test"+name+".txt", Context.MODE_PRIVATE));
	        osw.write(data); osw.close();
	    }catch (IOException e) {Log.e("Exception", "Fallo de escritura: " + e.toString());} 
	}
	
	private void writeToFile(String[] data) {
		String finales="";
		for(int i=0;i<data.length;i++){
			finales+=data[i]+"\n";
		}
			
	    try{OutputStreamWriter osw = new OutputStreamWriter(openFileOutput("test.txt", Context.MODE_PRIVATE));
	        osw.write(finales); osw.close();
	    }catch (IOException e) {Log.e("Exception", "Fallo de escritura: " + e.toString());} 
	}
	
	public static boolean isTablet(Context context) {
    return (context.getResources().getConfiguration().screenLayout
      & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE; }

	public static boolean checkFotoOnline(String URLName){
	    try {
	      HttpURLConnection.setFollowRedirects(false);
	      HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
	      con.setRequestMethod("HEAD");
	      return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
	    }
	    catch (Exception e) { e.printStackTrace(); return false; }
	  }
	
	//Escribir parcel
	final class backupParcel extends AsyncTask<String, Void, Boolean> {
		int modo; public backupParcel(int modo) { this.modo=modo; }
		
		protected void onPreExecute() {
			dirparcel = new File(getExternalCacheDir()+File.separator+"parcel"+File.separator);
		    //fileparcel = new File(dirparcel.getAbsolutePath()+e.getEid());
		    fileparcel = new File(getExternalCacheDir()+File.separator+"parcel"+File.separator+e.getEid());
		    parcel = Parcel.obtain();
		}
	    
		protected void onPostExecute(final Boolean success) {
			if(success) Log.e("PARCELSTATUS","FILEOK");
			Intent intent = new Intent(TiendaVirtual.this, ListaCompra.class);
			if(urls.isEmpty()){
		  		if(getIntent().getStringExtra("intent").equals("listacompra"))
		  			startActivity(intent);
		  		finish();
	  		}else new descargaImagenes().execute();
		}
		
		@Override protected Boolean doInBackground(String... params) {
			 try{
				if(modo==1){ //Borra el archivo para volverlo a crear.
					if(dirparcel.exists()){ 
					File archivo = new File(getExternalCacheDir()+File.separator+"parcel"+File.separator+e.getEid());
					archivo.delete();}
				}
		    	if(!dirparcel.exists()) dirparcel.mkdirs();
		    	FileOutputStream fos = new FileOutputStream(getExternalCacheDir()+File.separator+"parcel"+File.separator+e.getEid());
		    	for(Fila f : filasini)
		    		f.writeToParcel(parcel, 0);
		    	fos.write(parcel.marshall());
		    	fos.flush(); fos.close(); return true;
		    }catch(Exception e){e.printStackTrace();
		    	Log.e("PARCELSTATUS","FAIL"); return false;}
		}
	
	}
}