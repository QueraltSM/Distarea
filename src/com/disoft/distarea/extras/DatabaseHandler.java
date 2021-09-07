package com.disoft.distarea.extras;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.disoft.distarea.ListaArticulos.Fila;
import com.disoft.distarea.R;
import com.disoft.distarea.models.Art;
import com.disoft.distarea.models.ArtEst;
import com.disoft.distarea.models.CliF;
import com.disoft.distarea.models.Est;
import com.disoft.distarea.models.Fam;
import com.disoft.distarea.models.Msj;
import com.disoft.distarea.models.Ped;

public class DatabaseHandler extends SQLiteOpenHelper {
	SharedPreferences sharedPrefs; Context context;
	private static final int DATABASE_VERSION = 8; 
	/* DBV (DataBase Versions):
	 * Inicial 										4; 
	 * Oficial release 								5;
	 * CNAE en Est 									6;
	 * Campos int->String Familia/Subfamilia		7;
	 * 3 campos de Artículos -> ArtEst + XML		8;
	 * */
	private static final String DATABASE_NAME = "DistareaDB";
	
	// Nombres de tablas
	private String TABLE_ARTICULO = "articulo";
	private String TABLE_ARTICULOESTABLECIMIENTO = "articuloestablecimiento";
	private String TABLE_ESTABLECIMIENTO = "establecimiento";
	private String TABLE_PEDIDO = "pedido";
	private String TABLE_PEDIDOANT = "pedidoant";
	private String TABLE_MENSAJESREC = "mensajesrec";
	private String TABLE_MENSAJESENV = "mensajesenv";
	private String TABLE_INTERESOFERTA = "interesoferta";
	private String TABLE_CNAE = "cnae";
	private String TABLE_PAISES = "paises";
	private String TABLE_FAMILIAS = "familias";
	private String TABLE_CLIENTESF = "clientesf";
	private String TABLE_ACTIVIDADES = "actividades";
	private String TABLE_ACTIVIDADESTIPO = "actividadestipo";
	//Aún no se usa
	private String TABLE_SUBFAMILIAS = "subfamilias";
	
	// Columnas de Artículo
	private static final String KEY_AID = "aid";
	private static final String KEY_ARTICULO = "articulo";
	private static final String KEY_CBARRAS = "cbarras";
	private static final String KEY_TIPO = "tipo";
	
	//Columnas de ArtículoEstablecimiento
	//private static final String KEY_AID = "aid";
	private static final String KEY_AFID = "afid";
	//private static final String KEY_EID = "eid";
	private static final String KEY_FID = "fid";
	private static final String KEY_SFID = "sfid";
	private static final String KEY_EXISTENCIAS = "existencias";
	private static final String KEY_ACTIVO = "activo";
	
	// Columnas de Establecimiento
	private static final String KEY_EID = "eid";
	private static final String KEY_EGGID = "eggid";
	private static final String KEY_NOMBRE = "nombre";
	private static final String KEY_TEL = "tel";
	private static final String KEY_MAIL = "mail";
	private static final String KEY_FECHAULTIMA = "fechaultima";
	private static final String KEY_CODCLIENTE = "codcliente";
	//private static final String KEY_ACTIVO = "activo";
	private static final String KEY_FAV = "fav";
	private static final String KEY_PRIOR = "prior";
	private static final String KEY_VAL = "val";
	private static final String KEY_CVISUAL = "cvisual";
	private static final String KEY_GPS = "gps";
	private static final String KEY_CINTERNO = "cinterno";
	private static final String KEY_CID = "cid";
	private static final String KEY_CIH = "cih";
	private static final String KEY_TV = "tv";
	private static final String KEY_ZI = "zi";
	private static final String KEY_LOGO = "logo";
	private static final String KEY_TARIFA = "tarifa";
	private static final String KEY_CODINVPUB = "codinvpub";
	private static final String KEY_RESTRPED = "restrped";
	private static final String KEY_PASSTV = "passtv";
	private static final String KEY_USER = "user";
	private static final String KEY_REQFIRM = "reqfirm";
	private static final String KEY_REQTEL = "reqtel";
	private static final String KEY_MSJMAIL = "msjmail";
	private static final String KEY_REFERENCIA = "referencia";
	private static final String KEY_CONFIGURA = "configura";
	//Campos datos pedido generales, según Establecimiento
	private static final String KEY_REFERENCIAPEDIDO = "referenciapedido";
	private static final String KEY_FECHACITA = "fechacita";
	private static final String KEY_HORACITA= "horacita";
	private static final String KEY_OBSERVACIONES = "observaciones";
	private static final String KEY_CNAE = "cnae"; //Siempre el último
	//Aún no se usan
	private static final String KEY_CITAS = "citas";
	private static final String KEY_SINCRO = "sincro";
	private static final String KEY_SINCROPROD = "sincroprod";
	private static final String KEY_SINCROFREQ = "sincrofreq";
	private static final String KEY_RELFAMSUBFAM = "relfamsubfam";
	private static final String KEY_IDFACDIS = "idfacdis";
	private static final String KEY_SINCROTV = "sincrotv";
	
	// Columnas de Pedido y PedidoAnt
	private static final String KEY_AUTOID = "autoid";
	private static final String KEY_PID = "pid";
	//private static final String KEY_AID = "aid";
	//private static final String KEY_EID = "eid";
	private static final String KEY_FECHA = "fecha";
	private static final String KEY_CANTIDAD = "cantidad";
	private static final String KEY_PRECIO = "precio";
	private static final String KEY_OBS = "obs";
	private static final String KEY_ESTADO = "estado";
	//private static final String KEY_AFID = "afid";
	private static final String KEY_OFERTA = "oferta";
	private static final String KEY_IDCLIF = "idclif";
	private static final String KEY_PRECIOMANUAL = "preciomanual";
	
	//Columnas de MensajesRec y MensajesEnv
	private static final String KEY_MID = "mid";
	private static final String KEY_CLIENTEGLOBAL = "clienteglobal";
	private static final String KEY_MENSAJE = "mensaje";
	private static final String KEY_HTTP = "http";
	private static final String KEY_FECHAREALIZ = "fecharealiz";
	private static final String KEY_HORAREALIZ = "horarealiz";
	private static final String KEY_ZONAINF = "zonainf";
	//private static final String KEY_EID = "eid";
	private static final String KEY_TIPOMSJ = "tipomsj";
	private static final String KEY_DESDEFECHA = "desdefecha";
	private static final String KEY_HASTAFECHA = "hastafecha";
	private static final String KEY_FECHAREC = "fecharec";
	private static final String KEY_HORAREC = "horarec";
	//private static final String KEY_ESTADO = "estado";
	private static final String KEY_IDMSJAPPMOVIL = "idmsjappmovil";
	private static final String KEY_RMTE = "rmte";
	private static final String KEY_MIDBD = "midbd";
	private static final String KEY_XML = "xml";
	
	//Columnas de InteresOferta
	private static final String KEY_OID = "oid";
	private static final String KEY_QUIEN = "quien";
	//private static final String KEY_FECHA = "fecha";
	private static final String KEY_HORA = "hora";
	private static final String KEY_TEXTO = "texto";
	private static final String KEY_IMAGEN = "imagen";
	//private static final String KEY_HTTP = "http";
	private static final String KEY_CATEGORIAS = "categorias";
	private static final String KEY_CLIENTE = "cliente";
	//private static final String KEY_TIPO = "tipo";
	private static final String KEY_IDMOVIL= "idmovil";
	
	//Columnas de CNAE
	private static final String KEY_CNAEID = "cnaeid";
	private static final String KEY_CNAENOMBRE = "cnaenombre";
	private static final String KEY_CNAETIPO = "cnaetipo";
	private static final String KEY_CNAEACEPTA = "cnaeacepta";
	
	//Columnas de Paises
	private static final String KEY_PAID = "paid";
	private static final String KEY_PNOMBRE = "pnombre";
	private static final String KEY_PCODIGO = "pcodigo";
	
	//Columnas de Familias
	//private static final String KEY_FID = "fid";
	//private static final String KEY_NOMBRE = "nombre";
	//private static final String KEY_FECHA = "fecha";
	//private static final String KEY_EID = "eid";
	
	//Columnas de Subfamilias
	//private static final String KEY_SFID = "sfid";
	//private static final String KEY_FID = "fid";
	//private static final String KEY_NOMBRE = "nombre";
	//private static final String KEY_FECHA= "fecha";
	//private static final String KEY_EID = "eid";
	
	//Columnas de Clientesf
	private static final String KEY_IDCF = "idcf";
	//private static final String KEY_NOMBRE = "nombre";
	//private static final String KEY_MAIL = "mail";
	private static final String KEY_IDEST = "idest";
	private static final String KEY_PAIS = "pais";
	private static final String KEY_PROVINCIA = "provincia";
	private static final String KEY_MUNICIPIO = "municipio";
	private static final String KEY_DIRECCION = "direccion";
	//private static final String KEY_TEL = "telefono";
	private static final String KEY_MOVIL = "movil";
	private static final String KEY_FECHANAC = "fechanac";
	private static final String KEY_NIF = "nif";
	private static final String KEY_REF = "ref";
	private static final String KEY_IDDIS = "iddis";
	private static final String KEY_IDDIS_VEND = "iddis_vend";
	private static final String KEY_SEXO = "sexo";
	private static final String KEY_TIPOCLIENTE = "tipocliente";
	
	//Si es una Asesoría -> tipocliente, nombre y ref
	//Si tiene reservas -> nombre, nif, fechanacimiento, e-mail, pais, direccion, telefono, ref, sexo
	
	//Columnas de Actividades
	private static final String KEY_IDACT = "idact";
	private static final String KEY_ACTIVIDAD = "actividad";
	//private static final String KEY_EID = "eid";
	//private static final String KEY_TIPO = "tipo";
	private static final String KEY_PRIORIDAD = "prioridad";
	
	//Columnas de Actividades_tipo
	//private static final String KEY_IDACT = "idact";
	//private static final String KEY_ACTIVIDAD = "actividad";
	//private static final String KEY_PRIORIDAD = "prioridad";
				
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.context = context;
	}
	
	// Creating Tables
	@Override public void onCreate(SQLiteDatabase db) {
		
		String CREATE_ARTICULO_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ARTICULO+ "("
				+ KEY_AID + " INTEGER PRIMARY KEY NOT NULL," + KEY_ARTICULO + " TEXT,"
				+ KEY_CBARRAS + " TEXT," + KEY_TIPO + " TEXT, UNIQUE("+KEY_ARTICULO+"))";
		
		String CREATE_ARTICULOESTABLECIMIENTO_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ARTICULOESTABLECIMIENTO+ "("
				+ KEY_AID + " INTEGER NOT NULL," + KEY_AFID + " TEXT NOT NULL,"
				+ KEY_EID + " INTEGER NOT NULL, " + KEY_FID + " TEXT NOT NULL,"
				+ KEY_SFID + " TEXT," + KEY_EXISTENCIAS + " REAL," + KEY_ACTIVO + " TEXT,"
				+ " PRIMARY KEY("+KEY_AFID+","+KEY_EID+"))";
		
		String CREATE_ESTABLECIMIENTO_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ESTABLECIMIENTO+ "("
				+ KEY_EID + " INTEGER PRIMARY KEY NOT NULL," + KEY_EGGID + " INTEGER," + KEY_NOMBRE + " TEXT,"
				+ KEY_TEL + " TEXT," + KEY_MAIL + " TEXT," + KEY_FECHAULTIMA + " TEXT,"
				+ KEY_CODCLIENTE + " STRING," + KEY_ACTIVO + " BOOLEAN," + KEY_FAV + " BOOLEAN," 
				+ KEY_PRIOR + " INTEGER," + KEY_VAL + " FLOAT," + KEY_CVISUAL + " TEXT,"
				+ KEY_GPS + " TEXT," + KEY_CINTERNO + " TEXT," + KEY_CID + " TEXT," + KEY_CIH + " TEXT,"
				+ KEY_TV + " TEXT," +  KEY_ZI + " TEXT," + KEY_LOGO + " TEXT," + KEY_TARIFA + " INTEGER,"
				+ KEY_CODINVPUB + " TEXT," + KEY_RESTRPED + " TEXT," + KEY_PASSTV + " TEXT,"
				+ KEY_USER + " TEXT," + KEY_REQFIRM + " TEXT," + KEY_REQTEL + " TEXT," + KEY_CITAS + " INTEGER,"
				+ KEY_SINCRO + " BOOLEAN," + KEY_SINCROPROD + " TEXT," + KEY_SINCROFREQ + " TEXT,"
				+ KEY_RELFAMSUBFAM + " BOOLEAN," + KEY_IDFACDIS + " TEXT," + KEY_SINCROTV + " DATE,"
				+ KEY_MSJMAIL + " TEXT," + KEY_REFERENCIA + " TEXT," + KEY_CONFIGURA + " TEXT,"
				+ KEY_REFERENCIAPEDIDO + " TEXT," + KEY_FECHACITA + " TEXT," + KEY_HORACITA + " TEXT," 
				+ KEY_OBSERVACIONES + " TEXT," + KEY_CNAE + " TEXT)";
		
		String CREATE_PEDIDO_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PEDIDO + "("
				+ KEY_AUTOID + " INTEGER PRIMARY KEY," + KEY_PID + " INTEGER NOT NULL," 
				+ KEY_AID + " INTEGER NOT NULL ," + KEY_EID + " INTEGER NOT NULL," + KEY_FECHA + " TEXT," 
				+ KEY_CANTIDAD + " DOUBLE, " + KEY_PRECIO + " TEXT," + KEY_OBS + " TEXT," 
				+ KEY_ESTADO + " INTEGER," + KEY_AFID + " TEXT," + KEY_OFERTA + " TEXT,"
				+ KEY_IDCLIF + " INTEGER," + KEY_PRECIOMANUAL + " TEXT)";
		
		String CREATE_PEDIDOANT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PEDIDOANT + "("
				+ KEY_AUTOID + " INTEGER PRIMARY KEY," + KEY_PID + " INTEGER NOT NULL," 
				+ KEY_AID + " INTEGER NOT NULL ," + KEY_EID + " INTEGER NOT NULL," + KEY_FECHA + " TEXT," 
				+ KEY_CANTIDAD + " DOUBLE, " + KEY_PRECIO + " TEXT," + KEY_OBS + " TEXT," 
				+ KEY_ESTADO + " INTEGER," + KEY_AFID + " TEXT," + KEY_OFERTA + " TEXT,"
				+ KEY_IDCLIF + " INTEGER," + KEY_PRECIOMANUAL + " TEXT)";
		
		String CREATE_MENSAJESREC_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MENSAJESREC + "("
				+ KEY_MID + " INTEGER NOT NULL," + KEY_CLIENTEGLOBAL + " INTEGER,"
				+ KEY_MENSAJE + " TEXT," + KEY_HTTP + " TEXT," + KEY_FECHAREALIZ + " TEXT,"  
				+ KEY_HORAREALIZ + " TEXT," + KEY_ZONAINF + " TEXT," + KEY_EID + " INTEGER,"
				+ KEY_TIPOMSJ + " TEXT," + KEY_DESDEFECHA + " TEXT," + KEY_HASTAFECHA + " TEXT,"
				+ KEY_FECHAREC + " TEXT," + KEY_HORAREC + " TEXT," + KEY_ESTADO + " TEXT,"
				+ KEY_IDMSJAPPMOVIL + " INTEGER," + KEY_RMTE + " TEXT,"
				+ KEY_MIDBD + " INTEGER," + KEY_XML + " TEXT)";
		
		String CREATE_MENSAJESENV_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MENSAJESENV + "("
				+ KEY_MID + " INTEGER NOT NULL," + KEY_CLIENTEGLOBAL + " INTEGER,"	
				+ KEY_MENSAJE + " TEXT," + KEY_HTTP + " TEXT," + KEY_FECHAREALIZ + " TEXT,"  
				+ KEY_HORAREALIZ + " TEXT," + KEY_ZONAINF + " TEXT," + KEY_EID + " INTEGER,"
				+ KEY_TIPOMSJ + " TEXT," + KEY_DESDEFECHA + " TEXT," + KEY_HASTAFECHA + " TEXT,"
				+ KEY_FECHAREC + " TEXT," + KEY_HORAREC + " TEXT," + KEY_ESTADO + " TEXT,"
				+ KEY_IDMSJAPPMOVIL + " INTEGER PRIMARY KEY," + KEY_RMTE + " TEXT,"
				+ KEY_MIDBD + " INTEGER," + KEY_XML + " TEXT)";
		
		String CREATE_INTERESOFERTA_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_INTERESOFERTA + "("
				+ KEY_OID + " INTEGER NOT NULL," + KEY_QUIEN + " TEXT," + KEY_FECHA + " TEXT,"
				+ KEY_HORA + " TEXT," + KEY_TEXTO + " TEXT," + KEY_IMAGEN + " TEXT,"
				+ KEY_HTTP + " TEXT," + KEY_CATEGORIAS + " TEXT," + KEY_CLIENTE + " TEXT,"
				+ KEY_TIPO + " INTEGER," + KEY_IDMOVIL + " TEXT)";	
		
		String CREATE_CNAE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CNAE + "("
				+ KEY_CNAEID + " INTEGER PRIMARY KEY NOT NULL," + KEY_CNAENOMBRE + " TEXT,"
				+ KEY_CNAETIPO + " TEXT," + KEY_CNAEACEPTA + " BOOLEAN)";
		
		String CREATE_PAISES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PAISES + "("
				+ KEY_PAID + " INTEGER PRIMARY KEY NOT NULL," + KEY_PNOMBRE + " TEXT,"
				+ KEY_PCODIGO + " TEXT)";
		
		String CREATE_FAMILIAS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FAMILIAS + "("
				+ KEY_FID + " TEXT NOT NULL," + KEY_NOMBRE + " TEXT,"
				+ KEY_FECHA + " TEXT," + KEY_EID + " INTEGER, PRIMARY KEY (" 
				+ KEY_FID + " , " + KEY_EID + " ))";
		
		String CREATE_SUBFAMILIAS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SUBFAMILIAS + "("
				+ KEY_SFID + " TEXT KEY NOT NULL," + KEY_FID + " TEXT," 
				+ KEY_NOMBRE + " TEXT," + KEY_FECHA + " TEXT," + KEY_EID + " INTEGER, PRIMARY KEY (" 
				+ KEY_SFID + " , " + KEY_EID + " ))";
		
		String CREATE_CLIENTESF_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CLIENTESF + "("
				+ KEY_IDCF + " INTEGER," + KEY_NOMBRE + " TEXT," + KEY_MAIL + " TEXT," 
				+ KEY_IDEST + " INTEGER," + KEY_PAIS + " TEXT," +  KEY_PROVINCIA + " TEXT,"
				+ KEY_MUNICIPIO + " TEXT," + KEY_DIRECCION + " TEXT," + KEY_TEL + " TEXT," 
				+ KEY_MOVIL + " TEXT,"  + KEY_FECHANAC + " TEXT," + KEY_NIF+ " TEXT," 
				+ KEY_REF + " TEXT," + KEY_IDDIS + " INTEGER," + KEY_IDDIS_VEND + " INTEGER," 
				+ KEY_SEXO + " TEXT," + KEY_TIPOCLIENTE + " TEXT)";	
		
		String CREATE_ACTIVIDADES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTIVIDADES + "("
				+ KEY_IDACT + " INTEGER," + KEY_ACTIVIDAD + " TEXT," + KEY_EID + " INTEGER," 
				+ KEY_TIPO + " INTEGER," + KEY_PRIORIDAD + " INTEGER)";
		
		String CREATE_ACTIVIDADESTIPO_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTIVIDADESTIPO + "("
				+ KEY_IDACT + " INTEGER," + KEY_ACTIVIDAD + " TEXT," + KEY_PRIORIDAD + " INTEGER)";
		
		db.execSQL(CREATE_ARTICULO_TABLE);
		db.execSQL(CREATE_ARTICULOESTABLECIMIENTO_TABLE);
		db.execSQL(CREATE_ESTABLECIMIENTO_TABLE);
		db.execSQL(CREATE_PEDIDO_TABLE);
		db.execSQL(CREATE_PEDIDOANT_TABLE);
		db.execSQL(CREATE_MENSAJESREC_TABLE);
		db.execSQL(CREATE_MENSAJESENV_TABLE);
		db.execSQL(CREATE_INTERESOFERTA_TABLE);
		db.execSQL(CREATE_CNAE_TABLE);
		db.execSQL(CREATE_PAISES_TABLE);
		db.execSQL(CREATE_FAMILIAS_TABLE);
		db.execSQL(CREATE_SUBFAMILIAS_TABLE);
		db.execSQL(CREATE_CLIENTESF_TABLE);
		db.execSQL(CREATE_ACTIVIDADES_TABLE);
		db.execSQL(CREATE_ACTIVIDADESTIPO_TABLE);
	}

	// Upgrading database
	@Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Versión 4 -> 5, incluyo campo String msjmail en Est
		if(oldVersion==4 && newVersion==5)
			db.execSQL("ALTER TABLE " + TABLE_ESTABLECIMIENTO + " ADD " + KEY_MSJMAIL + " TEXT");
		
		//Versión 5 -> 6, incluyo campo String cnae en Est
		if(oldVersion==5 && newVersion==6){
			try{db.execSQL("ALTER TABLE " + TABLE_ESTABLECIMIENTO + " ADD " + KEY_CNAE + " TEXT");
				}catch(Exception e){}
			new ActualizaCNAE().execute();
		}
		
		//Versión 6 -> 7, edito Familia y Subfamilia (INTEGER de los IDs a TEXT)
				if(oldVersion==6 && newVersion==7){
					try{ 
						db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAMILIAS); 
						db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBFAMILIAS);
						onCreate(db);
						}catch(Exception e){}
				}
		
		//Versión 7 -> 8, cambio Artículo -> ArtículoEstablecimiento
				/*if(oldVersion==7 && newVersion==8){
					try{//Cojo los datos de Articulo y ArticuloEstablecimiento
						List<Art> articulos = new ArrayList<Art>();
						String selectQuery = "SELECT  * FROM " + TABLE_ARTICULO + " ORDER BY " +KEY_AID + " ASC";
						Cursor cursor = db.rawQuery(selectQuery, null);
						if (cursor.moveToFirst()) {
							do { Art articulo = new Art();
								articulo.setAid(cursor.getInt(0));
								articulo.setArticulo(cursor.getString(1));
								articulo.setCbarras(cursor.getString(2));
								articulo.setTipo(cursor.getString(3));
								articulos.add(articulo);
							} while (cursor.moveToNext()); }
						cursor.close();// db.close();
						
						List<ArtEst> aes	= new ArrayList<ArtEst>();
						String selectQuery2 = "SELECT  * FROM " + TABLE_ARTICULOESTABLECIMIENTO + 
								" ORDER BY " + KEY_AID + " ASC";
						Cursor cursor2 = db.rawQuery(selectQuery2, null);
						if (cursor2.moveToFirst()) { do {
							ArtEst ae = new ArtEst(cursor2.getInt(2),cursor2.getString(1),
											cursor2.getInt(2),"","",0.0f);
							aes.add(ae); } while (cursor2.moveToNext()); }
						cursor2.close(); //db.close();
						
						//Borro y recreo ambas tablas
						db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICULO);
						db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICULOESTABLECIMIENTO);
						onCreate(db);
						
						//Añado el contenido anterior
						for(Art a : articulos) addArticulo(a);
						for(ArtEst ae : aes) addArticuloEstablecimiento(ae);
						
						//Añado campo XML en los Mensajes
						db.execSQL("ALTER TABLE " + TABLE_MENSAJESENV + " ADD " + KEY_XML + " TEXT");
						db.execSQL("ALTER TABLE " + TABLE_MENSAJESREC + " ADD " + KEY_XML + " TEXT");
					}catch(Exception e){}
				}*/
				
		/* Forzar limpiado y recreación
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICULO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICULOESTABLECIMIENTO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESTABLECIMIENTO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEDIDO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEDIDOANT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENSAJESREC);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENSAJESENV);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERESOFERTA);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CNAE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAISES);
		onCreate(db);*/
		/*Ejemplo de update
		if(oldVersion==1 && newVersion==2){
			String CREATE_FAMILIAS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FAMILIAS + "("
					+ KEY_FID + " INTEGER PRIMARY KEY NOT NULL," + KEY_NOMBRE + " TEXT,"
					+ KEY_FECHA + " DATE)";
			
			String CREATE_SUBFAMILIAS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SUBFAMILIAS + "("
					+ KEY_SFID + " INTEGER PRIMARY KEY NOT NULL," + KEY_FID + " INTEGER," 
					+ KEY_NOMBRE + " TEXT," + KEY_FECHA + " DATE)";
			
			db.execSQL("ALTER TABLE " + TABLE_ARTICULO + " ADD " + KEY_FAMILIA + " INTEGER,"
				+ KEY_SUBFAMILIA + " INTEGER," + KEY_EXISTENCIAS + " INTEGER");
			
			db.execSQL("ALTER TABLE " + TABLE_ESTABLECIMIENTO + " ADD " + KEY_CITAS + " INTEGER,"
				+ KEY_SINCRO + " BOOLEAN," + KEY_SINCROPROD + " TEXT," + KEY_SINCROFREQ + " TEXT,"
				+ KEY_RELFAMSUBFAM + " BOOLEAN," + KEY_IDFACDIS + " TEXT" + KEY_SINCROTV + " DATE");
			db.execSQL(CREATE_FAMILIAS_TABLE);
			db.execSQL(CREATE_SUBFAMILIAS_TABLE);
		}*/
	}

	//////////////////////
	//XXX Funciones de Artículos 
	//////////////////////
	
	//Añadir (Art) [addArticulo]
	//Mostrar uno (aid) [getArticulo]
	//Mostrar uno (articulo) [getArticuloPorNombre]
	//Mostrar uno (cbarras) [getArticulo]
	//Mostrar Todos [getAllArticulos]
	//Contar [getArticulosCount] //(FAIL)Mientras no borremos, podemos usarlo para pillar el último+1 para el siguiente AID
	//Último aid [getLastAid] //Como ahora se borran artículos, hay que basarse en el último real.
	//Buscar (articulo) [searchArticulo]
	//Actualizar (Art) [updateArticulo]
	//Borrar (Art) [deleteArticulo]
	//Reiniciar [resetArticulos]

	// Añadir un artículo
	public void addArticulo(Art articulo) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_AID, articulo.getAid());
		values.put(KEY_ARTICULO, articulo.getArticulo());
		values.put(KEY_CBARRAS, articulo.getCbarras());
		values.put(KEY_TIPO, articulo.getTipo());
		db.insert(TABLE_ARTICULO, null, values); db.close();
	}

	// Mostrar un artículo
	public Art getArticulo(int aid) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ARTICULO, new String[] { KEY_AID,
				KEY_ARTICULO, KEY_CBARRAS, KEY_TIPO }, KEY_AID + "=?",
				new String[] { String.valueOf(aid) }, null, null, null, null);
		if (cursor.getCount()>0) cursor.moveToFirst(); else return null;
		Art articulo = new Art(cursor.getInt(0), cursor.getString(1),
				 cursor.getString(2),cursor.getString(3)
				 //,cursor.getInt(4), cursor.getInt(5), cursor.getDouble(6)
				);
		cursor.close(); db.close(); return articulo;
	}
	
	// Mostrar un artículo por nombre
	public Art getArticuloPorNombre(String articulo) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ARTICULO, new String[] { KEY_AID,
			KEY_ARTICULO, KEY_CBARRAS, KEY_TIPO }, KEY_ARTICULO + "=?",
			new String[] { articulo }, null, null, null, null);
		if (cursor.getCount()>0) { cursor.moveToFirst();
			Art a = new Art(cursor.getInt(0), cursor.getString(1), 
				cursor.getString(2), cursor.getString(3));
			cursor.close(); db.close(); return a; }
		else { cursor.close(); db.close(); return null; }
	}
	
  //Mostrar un artículo por código de barras
	public Art getArticulo(String cbarras) {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.query(TABLE_ARTICULO, new String[] { KEY_AID,
					KEY_ARTICULO, KEY_CBARRAS, KEY_TIPO }, KEY_CBARRAS + "=?",
					new String[] { cbarras }, null, null, null, null);
			if (cursor.getCount()>0) { cursor.moveToFirst();
				Art articulo = new Art(cursor.getInt(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3));
				cursor.close(); db.close(); return articulo; }
			else { cursor.close(); db.close(); return null; }
		}

	// Coger todos los artículos
	public List<Art> getAllArticulos() {
		List<Art> listaArticulos = new ArrayList<Art>();
		String selectQuery = "SELECT  * FROM " + TABLE_ARTICULO + " ORDER BY " +KEY_AID + " ASC";
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do { Art articulo = new Art();
					articulo.setAid(cursor.getInt(0));
					articulo.setArticulo(cursor.getString(1));
					articulo.setCbarras(cursor.getString(2));
					articulo.setTipo(cursor.getString(3));
					listaArticulos.add(articulo);
				} while (cursor.moveToNext()); }
			cursor.close(); db.close(); return listaArticulos;
	}
	
	// Coger todos los artículos
		public ArrayList<Art> getAllArticulosAlfa() {
			ArrayList<Art> listaArticulos = new ArrayList<Art>();
			String selectQuery = "SELECT * FROM " + TABLE_ARTICULO + " ORDER BY " + KEY_ARTICULO + " DESC";
				SQLiteDatabase db = this.getWritableDatabase();
				Cursor cursor = db.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					do { Art articulo = new Art();
						articulo.setAid(cursor.getInt(0));
						articulo.setArticulo(cursor.getString(1));
						articulo.setCbarras(cursor.getString(2));
						articulo.setTipo(cursor.getString(3));
						listaArticulos.add(articulo);
					} while (cursor.moveToNext()); }
				cursor.close(); db.close(); return listaArticulos;
		}
	
	// Contar artículos
	public int getArticulosCount() {
		int count; String countQuery = "SELECT * FROM " + TABLE_ARTICULO;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount(); cursor.close(); db.close(); return count;
	}
	
	//Recuperar último Aid para crear artículo nuevo
	public int getLastAid() {
		int last=0; String Query = "SELECT MAX(" + KEY_AID + ") FROM " + TABLE_ARTICULO;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(Query, null);
		if (cursor.getCount()>0) { cursor.moveToFirst();
		last = cursor.getInt(0); cursor.close(); db.close(); return last;}
		else {cursor.close(); db.close(); return last;}
	}

	// Buscar un artículo
	public List<Art> searchArticulo(String nombre){
		SQLiteDatabase db = this.getReadableDatabase();
		List<Art> listaArticulos = new ArrayList<Art>();
		String selectQuery = "SELECT  * FROM " + TABLE_ARTICULO + " WHERE " + KEY_ARTICULO +
			 " LIKE '%" + nombre +"%'" + " ORDER BY "+ KEY_ARTICULO +" DESC";
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do {Art articulo = new Art();
			articulo.setAid(cursor.getInt(0));
			articulo.setArticulo(cursor.getString(1));
			articulo.setCbarras(cursor.getString(2));
			articulo.setTipo(cursor.getString(3));
			listaArticulos.add(articulo); } while (cursor.moveToNext()); }
		cursor.close(); db.close(); return listaArticulos;
	}
	
	// Actualizar un articulo
	public int updateArticulo(Art articulo) {
		int ret; SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_AID, articulo.getAid());
		values.put(KEY_ARTICULO, articulo.getArticulo());
		values.put(KEY_CBARRAS, articulo.getCbarras());
		values.put(KEY_TIPO, articulo.getTipo());
		ret = db.update(TABLE_ARTICULO, values, KEY_AID + " = ?", new String[] { 
				String.valueOf(articulo.getAid()) });
		db.close(); return ret;
	}

	// Borrar un artículo
	public void deleteArticulo(Art articulo) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ARTICULO, KEY_AID + " = ?", new String[] 
				{ String.valueOf(articulo.getAid()) });
		db.close();
	}
	
	// Borrar artículos en masa
	public void massDeleteArticulos(String articulos) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ARTICULO, KEY_AID + " IN (?)", 
				new String []{articulos});
		db.close();
		}

	//Reset tabla artículos
	public void resetArticulos(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICULO); onCreate(db);
	}
	
	//////////////////////////////////////////
	//XXX Funciones de ArtículoEstablecimiento 
	//////////////////////////////////////////

	//Añadir (aid,afid,eid) [addArticuloEstablecimiento]
	//Mostrar uno (aid,eid) [getArticuloEstablecimientoFacdis]
	//Mostrar uno (afid,eid) [getArticuloEstablecimientoInterno]
	//Mostrar Todos () [getAllArticulosEstablecimiento]
	//Mostrar Todos De (eid) [getAllArticulosEstablecimientoFrom]
	//Mostrar Todos Salvo (eid) [getAllArticulosEstablecimientoBut]
	
	// Añadir un artículo-establecimiento
	public void addArticuloEstablecimiento(int aid, String afid, int eid, 
			String fid, String sfid, double existencias, String activo) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_AID, aid);
		values.put(KEY_AFID, afid);
		values.put(KEY_EID, eid);
		values.put(KEY_FID, fid);
		values.put(KEY_SFID, sfid);
		values.put(KEY_EXISTENCIAS, existencias);
		values.put(KEY_ACTIVO, activo);
		db.insert(TABLE_ARTICULOESTABLECIMIENTO, null, values); db.close();
	}
	
	// Añadir un artículo-establecimiento (Clase ArtEst)
	public void addArticuloEstablecimiento(ArtEst ae) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_AID, ae.getAid());
		values.put(KEY_AFID, ae.getAfid());
		values.put(KEY_EID, ae.getEid());
		values.put(KEY_FID, ae.getFid());
		values.put(KEY_SFID, ae.getSfid());
		values.put(KEY_EXISTENCIAS, ae.getExistencias());
		values.put(KEY_ACTIVO, ae.getActivo());
		db.insert(TABLE_ARTICULOESTABLECIMIENTO, null, values); db.close();
	}
	
	public int getArticuloEstablecimientoCount(int eid){
		int count=0; 
		String countQuery = "SELECT " + KEY_AID + " FROM " + TABLE_ARTICULOESTABLECIMIENTO +
				" WHERE " + KEY_EID + "=" + eid ;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount(); cursor.close(); db.close(); return count;
	}

	// Mostrar un artículo-establecimiento (por afid) (AHORA, CON EID)
	public ArtEst getArticuloEstablecimientoFacdis(String afid, int eid) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ARTICULOESTABLECIMIENTO, new String[] {
			KEY_AID, KEY_EID, KEY_FID, KEY_SFID, KEY_EXISTENCIAS, KEY_ACTIVO },
			KEY_AFID + "=? AND " + KEY_EID +"=?", new String[] { afid, String.valueOf(eid) }, null, null, null, null);
		if (cursor.getCount()>0) cursor.moveToFirst();
		else { cursor.close(); db.close(); return null; }
		ArtEst AE = new ArtEst(cursor.getInt(0), afid, cursor.getInt(1), cursor.getString(2),
				cursor.getString(3), cursor.getFloat(4), cursor.getString(5));
		cursor.close(); db.close(); return AE;
	}
	
	// Mostrar un artículo-establecimiento (por aid, 1 sólo eid)
		public ArtEst getArticuloEstablecimiento(int aid, int eid) {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.query(TABLE_ARTICULOESTABLECIMIENTO, new String[] { 
					KEY_AFID, KEY_EID, KEY_FID, KEY_SFID, KEY_EXISTENCIAS, KEY_ACTIVO},
					KEY_AID + "=? AND " + KEY_EID + "=?",
					new String[] { String.valueOf(aid), String.valueOf(eid) },
					null, null, null, null);
			if (cursor.getCount()>0) cursor.moveToFirst();
			else { cursor.close(); db.close(); return null; }
			ArtEst AE = new ArtEst(aid, cursor.getString(0), cursor.getInt(1),
					cursor.getString(2), cursor.getString(3), cursor.getFloat(4),
					cursor.getString(5));
			cursor.close(); db.close(); return AE;
		}

	
	// Mostrar un artículo-establecimiento (por aid)
	public ArtEst getArticuloEstablecimientoInterno(int aid) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ARTICULOESTABLECIMIENTO, new String[] { 
				KEY_AFID, KEY_EID, KEY_FID, KEY_SFID, KEY_EXISTENCIAS, KEY_ACTIVO},
				KEY_AID + "=?", new String[] { String.valueOf(aid) }, null, null, null, null);
		if (cursor.getCount()>0) cursor.moveToFirst();
		else { cursor.close(); db.close(); return null; }
		ArtEst AE = new ArtEst(aid, cursor.getString(0), cursor.getInt(1),
				cursor.getString(2), cursor.getString(3), cursor.getFloat(4),
				cursor.getString(5));
		cursor.close(); db.close(); return AE;
	}
	
	// Coger todos los artículos-establecimiento
	public List<ArtEst> getAllArticulosEstablecimiento() {
		List<ArtEst> listaAE = new ArrayList<ArtEst>();
		String selectQuery = "SELECT  * FROM " + TABLE_ARTICULOESTABLECIMIENTO + 
				" ORDER BY " + KEY_AID + " ASC";
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) { do {
					ArtEst ae = new ArtEst(cursor.getInt(0),cursor.getString(1),
							cursor.getInt(2),cursor.getString(3),cursor.getString(4),
							cursor.getFloat(5),cursor.getString(6));
					listaAE.add(ae); } while (cursor.moveToNext()); }
			cursor.close(); db.close(); return listaAE;
	}
	
	//Coger todos los artículos-establecimiento De...
	public List<ArtEst> getAllArticulosEstablecimientoFrom(int eid) {
		List<ArtEst> listaAE = new ArrayList<ArtEst>();
		String selectQuery = "SELECT  * FROM " + TABLE_ARTICULOESTABLECIMIENTO + 
				" WHERE " + KEY_EID + "=" + eid + " ORDER BY " + KEY_AID + " ASC";
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) { do {
					ArtEst ae = new ArtEst(cursor.getInt(0), cursor.getString(1),
							cursor.getInt(2), cursor.getString(3),
							cursor.getString(4), cursor.getFloat(5),
							cursor.getString(6));
					listaAE.add(ae); } while (cursor.moveToNext()); }
			cursor.close(); db.close(); return listaAE;
	}
	
	//Coger todos los artículos-establecimiento Salvo...
	public List<ArtEst> getAllArticulosEstablecimientoBut(int eid) {
		List<ArtEst> listaAE = new ArrayList<ArtEst>();
		String selectQuery = "SELECT  * FROM " + TABLE_ARTICULOESTABLECIMIENTO + 
				" WHERE " + KEY_EID + "<>" + eid + " ORDER BY " + KEY_AID + " ASC";
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) { do {
					ArtEst ae = new ArtEst(cursor.getInt(0), cursor.getString(1),
							cursor.getInt(2), cursor.getString(3),
							cursor.getString(4), cursor.getFloat(5),
							cursor.getString(6));
					listaAE.add(ae); } while (cursor.moveToNext()); }
			cursor.close(); db.close(); return listaAE;
	}
	
	// Actualiza un artículo-establecimiento (Clase ArtEst)
	public int updateArticuloEstablecimiento(ArtEst ae) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_AID, ae.getAid());
		values.put(KEY_AFID, ae.getAfid());
		values.put(KEY_EID, ae.getEid());
		values.put(KEY_FID, ae.getFid());
		values.put(KEY_SFID, ae.getSfid());
		values.put(KEY_EXISTENCIAS, ae.getExistencias());
		values.put(KEY_ACTIVO, ae.getActivo());
		int ret = db.update(TABLE_ARTICULOESTABLECIMIENTO, values, 
				KEY_AID + " = ? AND " + KEY_EID + " = ?", 
				new String[] { String.valueOf(ae.getAid()), 
				String.valueOf(ae.getEid()) });
		db.close(); return ret;
	}

	//Borrar un artículo-establecimiento
	public void deleteArticuloEstablecimiento(int aid, int eid) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ARTICULOESTABLECIMIENTO, KEY_AID + " = ? AND " + KEY_EID + " = ?" ,
				new String[] { String.valueOf(aid), String.valueOf(eid) });
		db.close();}
	
	//Borrar artículos-establecimiento en masa
	public void massDeleteArticulosEstablecimiento(int eid, String articulos) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ARTICULOESTABLECIMIENTO, KEY_EID + " = ? AND " + KEY_AID + " IN (?)" ,
				new String[] { String.valueOf(eid), articulos });
		db.close();}

	//////////////////////////////
	//XXX Funciones de Establecimientos 
	//////////////////////////////
	//Añadir (Est) [addEstablecimiento]
	//Mirar último Eid [getLastEid]
	//Mostrar uno (eid) [getEstablecimiento]
	//Mostrar uno (nombre) [getEstablecimiento]
	//Mostrar Todos [getAllEstablecimientos]
	//Contar [getEstablecimientosCount]
	//Contar favoritos [getEstablecimientosFavoritosCount]
	//Actualizar (Est) [updateEstablecimiento]
	//Buscar (String) [searchEstablecimiento]
	//Borrar (Est) [deleteEstablecimiento]
	//Reiniciar [resetEstablecimiento]
	//Comprobar Canchas Deportivas [checkCD]

	// Añadir un establecimiento
	public void addEstablecimiento(Est establecimiento) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_EID, establecimiento.getEid());
		values.put(KEY_EGGID, establecimiento.getEggid());
		values.put(KEY_NOMBRE, establecimiento.getNombre());
		values.put(KEY_TEL, establecimiento.getTel());
		values.put(KEY_MAIL, establecimiento.getMail());
		values.put(KEY_FECHAULTIMA, establecimiento.getFechaultima());
		values.put(KEY_CODCLIENTE, establecimiento.getCodcliente());
		values.put(KEY_ACTIVO, establecimiento.getActivo());
		values.put(KEY_FAV, establecimiento.getFav());
		values.put(KEY_PRIOR, establecimiento.getPrior());
		values.put(KEY_VAL, establecimiento.getVal());
		values.put(KEY_CVISUAL, establecimiento.getCvisual());
		values.put(KEY_GPS, establecimiento.getGps());
		values.put(KEY_CINTERNO, establecimiento.getCinterno());
		values.put(KEY_CID, establecimiento.getCid());
		values.put(KEY_CIH, establecimiento.getCih());
		values.put(KEY_TV, establecimiento.getTv());
		values.put(KEY_ZI, establecimiento.getZi());
		values.put(KEY_LOGO, establecimiento.getLogo());
		values.put(KEY_TARIFA, establecimiento.getTarifa());
		values.put(KEY_CODINVPUB, establecimiento.getCodinvpub());
		values.put(KEY_RESTRPED, establecimiento.getRestrped());
		values.put(KEY_PASSTV, establecimiento.getPassTV());
		values.put(KEY_USER, establecimiento.getUser());
		values.put(KEY_REQFIRM, establecimiento.getReqfirm());
		values.put(KEY_REQTEL, establecimiento.getReqtel());
		values.put(KEY_MSJMAIL, establecimiento.getMsjmail());
		values.put(KEY_REFERENCIA, establecimiento.getReferencia());
		values.put(KEY_CONFIGURA, establecimiento.getConfigura());
		//Valores vacíos por defecto de todos los Establecimientos: Detalles pedido
		values.put(KEY_REFERENCIAPEDIDO, "");
		values.put(KEY_FECHACITA, "");
		values.put(KEY_HORACITA, "");
		values.put(KEY_OBSERVACIONES, "");
		values.put(KEY_CNAE, establecimiento.getCnae());
		/*values.put(KEY_CITAS, establecimiento.getCitas());
		values.put(KEY_SINCRO, establecimiento.getSincro());
		values.put(KEY_SINCROPROD, establecimiento.getSincroprod());
		values.put(KEY_SINCROFREQ, establecimiento.getSincrofreq());
		values.put(KEY_RELFAMSUBFAM, establecimiento.getRelfamsubfam());
		values.put(KEY_IDFACDIS, establecimiento.getIdfacdis());
		*/db.insert(TABLE_ESTABLECIMIENTO, null, values); db.close();
	}
	
	// Coger el último Eid
	public int getLastEid() {
		int eid=-1; SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ESTABLECIMIENTO, 
				new String[] {KEY_EID}, null, null, null, null, "eid DESC", "1");
		if(cursor.getCount()>0) { cursor.moveToFirst(); eid = cursor.getInt(0); }
		cursor.close(); db.close(); return eid;
	}
	
	// Coger el último Eid de Lista
	public int getLastListEid() {
		int eid=0; SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ESTABLECIMIENTO, 
				new String[] {KEY_EID}, null, null, null, null, "eid ASC", "1");
		if(cursor.getCount()>0) { cursor.moveToFirst(); eid = cursor.getInt(0); }
		cursor.close(); db.close(); return eid;
	}
	
	// Mostrar un establecimiento
	public Est getEstablecimiento(int eid) {
		Est establecimiento = null; SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ESTABLECIMIENTO, 
				new String[] { KEY_EID, KEY_EGGID, KEY_NOMBRE, KEY_TEL, KEY_MAIL, KEY_FECHAULTIMA,			// 0- 5 
					KEY_CODCLIENTE, KEY_ACTIVO, KEY_FAV, KEY_PRIOR, KEY_VAL, KEY_CVISUAL, KEY_GPS,			// 6-12
					KEY_CINTERNO, KEY_CID, KEY_CIH, KEY_TV, KEY_ZI, KEY_LOGO, KEY_TARIFA, KEY_CODINVPUB,	//13-20
					KEY_RESTRPED, KEY_PASSTV, KEY_USER, KEY_REQFIRM, KEY_REQTEL, KEY_MSJMAIL, 				//21-26
					KEY_REFERENCIA, KEY_CONFIGURA, KEY_REFERENCIAPEDIDO, KEY_FECHACITA, KEY_HORACITA,		//27-31
					KEY_OBSERVACIONES, KEY_CNAE/*, KEY_CITAS, KEY_SINCRO, KEY_SINCROPROD,  					//32-33(36)
					KEY_SINCROFREQ, KEY_RELFAMSUBFAM, KEY_IDFACDIS*/}, KEY_EID + "=?", 						//37-39
				new String[] { String.valueOf(eid) }, null, null, null, null);
		if(cursor.getCount()>0){ cursor.moveToFirst();
			establecimiento = new Est(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), 
					cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6),
					cursor.getInt(7)>0, cursor.getInt(8)>0, cursor.getInt(9),cursor.getFloat(10),
					cursor.getString(11),cursor.getString(12), cursor.getString(13), cursor.getString(14),
					cursor.getString(15), cursor.getString(16), cursor.getString(17),cursor.getString(18),
					cursor.getInt(19), cursor.getString(20),cursor.getString(21), cursor.getString(22),
					cursor.getString(23),cursor.getString(24), cursor.getString(25), cursor.getString(26),
					cursor.getString(27), cursor.getString(28), cursor.getString(29), cursor.getString(30),
					cursor.getString(31), cursor.getString(32), cursor.getString(33)
					//, cursor.getInt(34), cursor.getInt(35)>0, cursor.getString(36), cursor.getString(37),
					//cursor.getInt(38)>0, cursor.getInt(39)
					); }
		else return null;
		cursor.close(); db.close(); return establecimiento;
	}
	
	// Mostrar un establecimiento por el nombre
	public Est getEstablecimiento(String nombre) {
		Est establecimiento = null; SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ESTABLECIMIENTO, 
				new String[] { KEY_EID, KEY_EGGID, KEY_NOMBRE, KEY_TEL, KEY_MAIL, KEY_FECHAULTIMA,
					KEY_CODCLIENTE, KEY_ACTIVO, KEY_FAV, KEY_PRIOR, KEY_VAL, KEY_CVISUAL, KEY_GPS,
					KEY_CINTERNO, KEY_CID, KEY_CIH, KEY_TV, KEY_ZI, KEY_LOGO, KEY_TARIFA, KEY_CODINVPUB,
					KEY_RESTRPED, KEY_PASSTV, KEY_USER, KEY_REQFIRM, KEY_REQTEL, KEY_MSJMAIL, 
					KEY_REFERENCIA, KEY_CONFIGURA, KEY_REFERENCIAPEDIDO, KEY_FECHACITA, KEY_HORACITA,
					KEY_OBSERVACIONES, KEY_CNAE /*KEY_CITAS, KEY_SINCRO,
					KEY_SINCROPROD, KEY_SINCROFREQ, KEY_RELFAMSUBFAM, KEY_IDFACDIS*/}, KEY_NOMBRE + "=?",
				new String[] { String.valueOf(nombre) }, null, null, null, null);
		if(cursor.getCount()>0) { cursor.moveToFirst();
		establecimiento = new Est(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), 
				cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6),
				cursor.getInt(7)>0, cursor.getInt(8)>0, cursor.getInt(9),cursor.getFloat(10),
				cursor.getString(11),cursor.getString(12), cursor.getString(13), cursor.getString(14),
				cursor.getString(15), cursor.getString(16), cursor.getString(17),cursor.getString(18),
				cursor.getInt(19), cursor.getString(20),cursor.getString(21), cursor.getString(22),
				cursor.getString(23),cursor.getString(24), cursor.getString(25), cursor.getString(26),
				cursor.getString(27), cursor.getString(28), cursor.getString(29), cursor.getString(30),
				cursor.getString(31), cursor.getString(32), cursor.getString(33)
				//, cursor.getInt(34), cursor.getInt(35)>0, cursor.getString(36), cursor.getString(37),
				//cursor.getInt(38)>0, cursor.getInt(39)
				); }
		else { cursor.close(); db.close();  return null; }
		cursor.close(); db.close(); return establecimiento;
	}

	// Coger todos los establecimientos
	public List<Est> getAllEstablecimientos() {
		List<Est> listaEstablecimientos= new ArrayList<Est>();
		String selectQuery = "SELECT  * FROM " + TABLE_ESTABLECIMIENTO + 
				" ORDER BY "+ KEY_EID +" ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do {
			Est establecimiento = new Est();
			establecimiento.setEid(cursor.getInt(0));
			establecimiento.setEggid(cursor.getInt(1));
			establecimiento.setNombre(cursor.getString(2));
			establecimiento.setTel(cursor.getString(3));
			establecimiento.setMail(cursor.getString(4));
			establecimiento.setFechaultima(cursor.getString(5));
			establecimiento.setCodcliente(cursor.getString(6));
			establecimiento.setActivo(cursor.getInt(7)>0);
			establecimiento.setFav(cursor.getInt(8)>0);
			establecimiento.setPrior(cursor.getInt(9));
			establecimiento.setVal(cursor.getFloat(10));
			establecimiento.setCvisual(cursor.getString(11));
			establecimiento.setGps(cursor.getString(12));
			establecimiento.setCinterno(cursor.getString(13));
			establecimiento.setCid(cursor.getString(14));
			establecimiento.setCih(cursor.getString(15));
			establecimiento.setTv(cursor.getString(16));
			establecimiento.setZi(cursor.getString(17));
			establecimiento.setLogo(cursor.getString(18));
			establecimiento.setTarifa(cursor.getInt(19));
			establecimiento.setCodinvpub(cursor.getString(20));
			establecimiento.setRestrped(cursor.getString(21));
			establecimiento.setPassTV(cursor.getString(22));
			establecimiento.setUser(cursor.getString(23));
			establecimiento.setReqfirm(cursor.getString(24));
			establecimiento.setReqtel(cursor.getString(25));
			/*establecimiento.setCitas(cursor.getInt(26));
			establecimiento.setSincro(cursor.getInt(27)>0);
			establecimiento.setSincroprod(cursor.getString(28));
			establecimiento.setSincrofreq(cursor.getString(29));
			establecimiento.setRelfamsubfam(cursor.getInt(30)>0);
			establecimiento.setIdfacdis(cursor.getInt(31));
			¿Date SincroTV?(cursor.getString(32));*/
			establecimiento.setMsjmail(cursor.getString(33));
			establecimiento.setReferencia(cursor.getString(34));
			establecimiento.setConfigura(cursor.getString(35));
			establecimiento.setReferenciapedido(cursor.getString(36));
			establecimiento.setFechacita(cursor.getString(37));
			establecimiento.setHoracita(cursor.getString(38));
			establecimiento.setObservaciones(cursor.getString(39));
			establecimiento.setCnae(cursor.getString(40));
			listaEstablecimientos.add(establecimiento); } while (cursor.moveToNext()); }
			cursor.close(); db.close(); return listaEstablecimientos;
	}
	
	// Coger los establecimientos de una zona de influencia 
	//(POSIBLE CAMBIO POR getEstablecimientosVisibles())
	public List<Est> getEstablecimientosZona() {
		List<Est> listaEstablecimientos= new ArrayList<Est>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_ESTABLECIMIENTO + 
				" WHERE " + KEY_ZI + " LIKE '%" + sharedPrefs.getString("cp","") +"%'"+
				" ORDER BY "+ KEY_EID +" ASC";

			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Est establecimiento = new Est();
					establecimiento.setEid(cursor.getInt(0));
					establecimiento.setEggid(cursor.getInt(1));
					establecimiento.setNombre(cursor.getString(2));
					establecimiento.setTel(cursor.getString(3));
					establecimiento.setMail(cursor.getString(4));
					establecimiento.setFechaultima(cursor.getString(5));
					establecimiento.setCodcliente(cursor.getString(6));
					establecimiento.setActivo(cursor.getInt(7)>0);
					establecimiento.setFav(cursor.getInt(8)>0);
					establecimiento.setPrior(cursor.getInt(9));
					establecimiento.setVal(cursor.getFloat(10));
					establecimiento.setCvisual(cursor.getString(11));
					establecimiento.setGps(cursor.getString(12));
					establecimiento.setCinterno(cursor.getString(13));
					establecimiento.setCid(cursor.getString(14));
					establecimiento.setCih(cursor.getString(15));
					establecimiento.setTv(cursor.getString(16));
					establecimiento.setZi(cursor.getString(17));
					establecimiento.setLogo(cursor.getString(18));
					establecimiento.setTarifa(cursor.getInt(19));
					establecimiento.setCodinvpub(cursor.getString(20));
					establecimiento.setRestrped(cursor.getString(21));
					establecimiento.setPassTV(cursor.getString(22));
					establecimiento.setUser(cursor.getString(23));
					establecimiento.setReqfirm(cursor.getString(24));
					establecimiento.setReqtel(cursor.getString(25));
					/*establecimiento.setCitas(cursor.getInt(26));
					establecimiento.setSincro(cursor.getInt(27)>0);
					establecimiento.setSincroprod(cursor.getString(28));
					establecimiento.setSincrofreq(cursor.getString(29));
					establecimiento.setRelfamsubfam(cursor.getInt(30)>0);
					establecimiento.setIdfacdis(cursor.getInt(31));
					¿Date SincroTV?(cursor.getString(32));*/
					establecimiento.setMsjmail(cursor.getString(33));
					establecimiento.setReferencia(cursor.getString(34));
					establecimiento.setConfigura(cursor.getString(35));
					establecimiento.setReferenciapedido(cursor.getString(36));
					establecimiento.setFechacita(cursor.getString(37));
					establecimiento.setHoracita(cursor.getString(38));
					establecimiento.setObservaciones(cursor.getString(39));
					establecimiento.setCnae(cursor.getString(40));
					listaEstablecimientos.add(establecimiento);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			// return contact list
			return listaEstablecimientos;
	}
	
	//Coger los establecimientos visibles
	public List<Est> getEstablecimientosVisibles() {
		List<Est> listaEstablecimientos= new ArrayList<Est>();
		String selectQuery = "SELECT * FROM " + TABLE_ESTABLECIMIENTO + 
				" WHERE " + KEY_PRIOR + ">=0 AND "+ KEY_EID +">0 ORDER BY "+ KEY_EID +" ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do {
			Est establecimiento = new Est();
			establecimiento.setEid(cursor.getInt(0));
			establecimiento.setEggid(cursor.getInt(1));
			establecimiento.setNombre(cursor.getString(2));
			establecimiento.setTel(cursor.getString(3));
			establecimiento.setMail(cursor.getString(4));
			establecimiento.setFechaultima(cursor.getString(5));
			establecimiento.setCodcliente(cursor.getString(6));
			establecimiento.setActivo(cursor.getInt(7)>0);
			establecimiento.setFav(cursor.getInt(8)>0);
			establecimiento.setPrior(cursor.getInt(9));
			establecimiento.setVal(cursor.getFloat(10));
			establecimiento.setCvisual(cursor.getString(11));
			establecimiento.setGps(cursor.getString(12));
			establecimiento.setCinterno(cursor.getString(13));
			establecimiento.setCid(cursor.getString(14));
			establecimiento.setCih(cursor.getString(15));
			establecimiento.setTv(cursor.getString(16));
			establecimiento.setZi(cursor.getString(17));
			establecimiento.setLogo(cursor.getString(18));
			establecimiento.setTarifa(cursor.getInt(19));
			establecimiento.setCodinvpub(cursor.getString(20));
			establecimiento.setRestrped(cursor.getString(21));
			establecimiento.setPassTV(cursor.getString(22));
			establecimiento.setUser(cursor.getString(23));
			establecimiento.setReqfirm(cursor.getString(24));
			establecimiento.setReqtel(cursor.getString(25));
			/*establecimiento.setCitas(cursor.getInt(26));
			establecimiento.setSincro(cursor.getInt(27)>0);
			establecimiento.setSincroprod(cursor.getString(28));
			establecimiento.setSincrofreq(cursor.getString(29));
			establecimiento.setRelfamsubfam(cursor.getInt(30)>0);
			establecimiento.setIdfacdis(cursor.getInt(31));
			¿Date SincroTV?(cursor.getString(32));*/
			establecimiento.setMsjmail(cursor.getString(33));
			establecimiento.setReferencia(cursor.getString(34));
			establecimiento.setConfigura(cursor.getString(35));
			establecimiento.setReferenciapedido(cursor.getString(36));
			establecimiento.setFechacita(cursor.getString(37));
			establecimiento.setHoracita(cursor.getString(38));
			establecimiento.setObservaciones(cursor.getString(39));
			establecimiento.setCnae(cursor.getString(40));
			listaEstablecimientos.add(establecimiento); } while (cursor.moveToNext());
			} cursor.close(); db.close(); return listaEstablecimientos;
	}
	
//Coger los establecimientos ocultos
	public List<Est> getEstablecimientosOcultos() {
		List<Est> listaEstablecimientos= new ArrayList<Est>();
		String selectQuery = "SELECT * FROM " + TABLE_ESTABLECIMIENTO + 
				" WHERE " + KEY_PRIOR + "=-1 AND "+ KEY_EID +">0 ORDER BY "+ KEY_EID +" ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do {
			Est establecimiento = new Est();
			establecimiento.setEid(cursor.getInt(0));
			establecimiento.setEggid(cursor.getInt(1));
			establecimiento.setNombre(cursor.getString(2));
			establecimiento.setTel(cursor.getString(3));
			establecimiento.setMail(cursor.getString(4));
			establecimiento.setFechaultima(cursor.getString(5));
			establecimiento.setCodcliente(cursor.getString(6));
			establecimiento.setActivo(cursor.getInt(7)>0);
			establecimiento.setFav(cursor.getInt(8)>0);
			establecimiento.setPrior(cursor.getInt(9));
			establecimiento.setVal(cursor.getFloat(10));
			establecimiento.setCvisual(cursor.getString(11));
			establecimiento.setGps(cursor.getString(12));
			establecimiento.setCinterno(cursor.getString(13));
			establecimiento.setCid(cursor.getString(14));
			establecimiento.setCih(cursor.getString(15));
			establecimiento.setTv(cursor.getString(16));
			establecimiento.setZi(cursor.getString(17));
			establecimiento.setLogo(cursor.getString(18));
			establecimiento.setTarifa(cursor.getInt(19));
			establecimiento.setCodinvpub(cursor.getString(20));
			establecimiento.setRestrped(cursor.getString(21));
			establecimiento.setPassTV(cursor.getString(22));
			establecimiento.setUser(cursor.getString(23));
			establecimiento.setReqfirm(cursor.getString(24));
			establecimiento.setReqtel(cursor.getString(25));
			/*establecimiento.setCitas(cursor.getInt(26));
			establecimiento.setSincro(cursor.getInt(27)>0);
			establecimiento.setSincroprod(cursor.getString(28));
			establecimiento.setSincrofreq(cursor.getString(29));
			establecimiento.setRelfamsubfam(cursor.getInt(30)>0);
			establecimiento.setIdfacdis(cursor.getInt(31));
			¿Date SincroTV?(cursor.getString(32));*/
			establecimiento.setMsjmail(cursor.getString(33));
			establecimiento.setReferencia(cursor.getString(34));
			establecimiento.setConfigura(cursor.getString(35));
			establecimiento.setReferenciapedido(cursor.getString(36));
			establecimiento.setFechacita(cursor.getString(37));
			establecimiento.setHoracita(cursor.getString(38));
			establecimiento.setObservaciones(cursor.getString(39));
			establecimiento.setCnae(cursor.getString(40));
			listaEstablecimientos.add(establecimiento); } while (cursor.moveToNext()); }
			cursor.close(); db.close(); return listaEstablecimientos;
	}

	// Contar establecimientos
	public int getEstablecimientosCount() {
		int count; String countQuery = "SELECT * FROM " + TABLE_ESTABLECIMIENTO;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount(); cursor.close(); db.close(); return count;
	}
	
	// Contar establecimientos habilitados
	public int getEstablecimientosHabilitadosCount() {
		int count; String countQuery = "SELECT * FROM " + TABLE_ESTABLECIMIENTO + 
				" WHERE " + KEY_FAV + "='1' AND " + KEY_PRIOR + ">=0";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount(); cursor.close(); db.close(); return count;
	}
	
	// Contar establecimientos favoritos
	public int getEstablecimientosFavoritosCount() {
		int count; String countQuery = "SELECT * FROM " + TABLE_ESTABLECIMIENTO +
				" WHERE " + KEY_FAV + "='1'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount(); cursor.close(); db.close(); return count;
	}

	// Contar listas de compra insertadas por el usuario
	public int getListasCount() {
		int count; String countQuery = "SELECT * FROM " + TABLE_ESTABLECIMIENTO + 
				" WHERE " + KEY_EID + "<'0'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount(); cursor.close(); db.close(); return count;
	}

	// Actualizar un establecimiento
	public int updateEstablecimiento(Est establecimiento) {
		int ret; SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_EID, establecimiento.getEid());
		values.put(KEY_EGGID, establecimiento.getEggid());
		values.put(KEY_NOMBRE, establecimiento.getNombre());
		values.put(KEY_TEL, establecimiento.getTel());
		values.put(KEY_MAIL, establecimiento.getMail());
		values.put(KEY_FECHAULTIMA, establecimiento.getFechaultima());
		values.put(KEY_CODCLIENTE, establecimiento.getCodcliente());
		values.put(KEY_ACTIVO, establecimiento.getActivo());
		values.put(KEY_FAV, establecimiento.getFav());
		values.put(KEY_PRIOR, establecimiento.getPrior());
		values.put(KEY_VAL, establecimiento.getVal());
		values.put(KEY_CVISUAL, establecimiento.getCvisual());
		values.put(KEY_GPS, establecimiento.getGps());
		values.put(KEY_CINTERNO, establecimiento.getCinterno());
		values.put(KEY_CID, establecimiento.getCid());
		values.put(KEY_CIH, establecimiento.getCih());
		values.put(KEY_TV, establecimiento.getTv());
		values.put(KEY_ZI, establecimiento.getZi());
		values.put(KEY_LOGO, establecimiento.getLogo());
		values.put(KEY_TARIFA, establecimiento.getTarifa());
		values.put(KEY_CODINVPUB, establecimiento.getCodinvpub());
		values.put(KEY_RESTRPED, establecimiento.getRestrped());
		values.put(KEY_PASSTV, establecimiento.getPassTV());
		values.put(KEY_USER, establecimiento.getUser());
		values.put(KEY_REQFIRM, establecimiento.getReqfirm());
		values.put(KEY_REQTEL, establecimiento.getReqtel());
		values.put(KEY_MSJMAIL, establecimiento.getMsjmail());
		values.put(KEY_REFERENCIA, establecimiento.getReferencia());
		values.put(KEY_CONFIGURA, establecimiento.getConfigura());
		values.put(KEY_CNAE, establecimiento.getCnae());
		//values.put(KEY_CITAS, establecimiento.getCitas());
		//values.put(KEY_SINCRO, establecimiento.getSincro());
		//values.put(KEY_SINCROPROD, establecimiento.getSincroprod());
		//values.put(KEY_SINCROFREQ, establecimiento.getSincrofreq());
		//values.put(KEY_RELFAMSUBFAM, establecimiento.getRelfamsubfam());
		//values.put(KEY_IDFACDIS, establecimiento.getIdfacdis());
		ret = db.update(TABLE_ESTABLECIMIENTO, values, KEY_EID + " = ?", 
				new String[] { String.valueOf(establecimiento.getEid()) });
		db.close(); return ret;
	}
	
	// Actualizar detalles del pedido de un establecimiento
	public int updateDatosEstablecimiento(Est establecimiento) {
		int ret; SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_REFERENCIAPEDIDO, establecimiento.getReferenciapedido());
		values.put(KEY_FECHACITA, establecimiento.getFechacita());
		values.put(KEY_HORACITA, establecimiento.getHoracita());
		values.put(KEY_OBSERVACIONES, establecimiento.getObservaciones());
		ret = db.update(TABLE_ESTABLECIMIENTO, values, KEY_EID + " = ?", 
				new String[] { String.valueOf(establecimiento.getEid()) });
		db.close(); return ret;
	}
	
	// Buscar un establecimiento
	public List<Est> searchEstablecimiento(String nombre){
		SQLiteDatabase db = this.getReadableDatabase();
		List<Est> listaEstablecimientos= new ArrayList<Est>();
		String selectQuery = "SELECT * FROM " + TABLE_ESTABLECIMIENTO + 
				" WHERE " + KEY_NOMBRE + " LIKE '%" + nombre +"%' AND " + KEY_EID +
				">0 ORDER BY "+ KEY_NOMBRE +" DESC";
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do { Est establecimiento = new Est();
				establecimiento.setEid(cursor.getInt(0));
				establecimiento.setEggid(cursor.getInt(1));
				establecimiento.setNombre(cursor.getString(2));
				establecimiento.setTel(cursor.getString(3));
				establecimiento.setMail(cursor.getString(4));
				establecimiento.setFechaultima(cursor.getString(5));
				establecimiento.setCodcliente(cursor.getString(6));
				establecimiento.setActivo(cursor.getInt(7)>0);
				establecimiento.setFav(cursor.getInt(8)>0);
				establecimiento.setPrior(cursor.getInt(9));
				establecimiento.setVal(cursor.getFloat(10));
				establecimiento.setCvisual(cursor.getString(11));
				establecimiento.setGps(cursor.getString(12));
				establecimiento.setCinterno(cursor.getString(13));
				establecimiento.setCid(cursor.getString(14));
				establecimiento.setCih(cursor.getString(15));
				establecimiento.setTv(cursor.getString(16));
				establecimiento.setZi(cursor.getString(17));
				establecimiento.setLogo(cursor.getString(18));
				establecimiento.setTarifa(cursor.getInt(19));
				establecimiento.setCodinvpub(cursor.getString(20));
				establecimiento.setRestrped(cursor.getString(21));
				establecimiento.setPassTV(cursor.getString(22));
				establecimiento.setUser(cursor.getString(23));
				establecimiento.setReqfirm(cursor.getString(24));
				establecimiento.setReqtel(cursor.getString(25));
				/*establecimiento.setCitas(cursor.getInt(26));
				establecimiento.setSincro(cursor.getInt(27)>0);
				establecimiento.setSincroprod(cursor.getString(28));
				establecimiento.setSincrofreq(cursor.getString(29));
				establecimiento.setRelfamsubfam(cursor.getInt(30)>0);
				establecimiento.setIdfacdis(cursor.getInt(31));
				¿Date SincroTV?(cursor.getString(32));*/
				establecimiento.setMsjmail(cursor.getString(33));
				establecimiento.setReferencia(cursor.getString(34));
				establecimiento.setConfigura(cursor.getString(35));
				establecimiento.setReferenciapedido(cursor.getString(36));
				establecimiento.setFechacita(cursor.getString(37));
				establecimiento.setHoracita(cursor.getString(38));
				establecimiento.setObservaciones(cursor.getString(39));
				establecimiento.setCnae(cursor.getString(40));
				listaEstablecimientos.add(establecimiento);
			} while (cursor.moveToNext());
		} cursor.close(); db.close(); return listaEstablecimientos;
	}
	
	// Borrar un establecimiento
	public void deleteEstablecimiento(Est establecimiento) {
		SQLiteDatabase db = this.getWritableDatabase();
		if(establecimiento!=null)
			db.delete(TABLE_ESTABLECIMIENTO, KEY_EID + " = ?", 
					new String[] { String.valueOf(establecimiento.getEid()) });
		db.close();
	}

	//Reset tabla establecimientos
	public void resetEstablecimientos(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESTABLECIMIENTO); onCreate(db);
	}
	
	//Comprobar si hay Canchas Deportivas
	public boolean checkCD(){
		boolean hayCD=false;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ESTABLECIMIENTO+" WHERE "+
				KEY_CNAE+" LIKE '%52120%'", null);
		if (cursor.moveToFirst()) 
			if(cursor.getInt(0)>=1) hayCD=true;
		return hayCD;
	}
	
	//////////////////////
	//XXX Funciones de Pedidos 
	//////////////////////
	//Añadir (Ped) [addPedido] -> X
	//Mostrar articulo (pid, aid) [getPedido] -> Ped
	//Mostrar Pedido Pendiente (eid) [getPedidoPendiente]
	//Contar con Pedido [getEstablecimientosConPedido] (¿Funciona?)
	//Mostrar todos los pedidos pendientes (eid) [getPedidospendientes]
	//Mostrar todos los pedidos finalizados [getPedidosFinalizados] (¿Necesario? Técnicamente son PedAnt)
	//Historial pedidos Establecimiento (eid) [getAllPedidos] (¿Repetido? ¿Útil?)
	//Mostrar articulos (pid) [getAllArticulosPedidos]
	//Contar Artículos Pedidos (pid) [getArticulosPedidosCount] (¿Nº líneas? Cambio de establecimiento, saber cuántas borrar)
	//Contar [getPedidosCount] (Cuenta cantidad de pedidos distintos hechos, multiestablecimiento)
	//Contar finalizados [getPedidosFinalizadosCount] (¿Para qué? Me pega más en PedidoAnt)
	//Actualizar (Art) [updatePedido]
	//Borrar Articulo (Ped) [deleteArticuloPedido]
	//Borrar todo el pedido (Ped) [deletePedido]
	//Comprobar si un Articulo está pedido (aid) [checkArticuloPedido]
	//Reiniciar [resetPedidos]
	
	// Añadir un pedido
	public void addPedido(Ped pedido) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_AUTOID, pedido.getAutoid());
		values.put(KEY_PID, pedido.getPid());
		values.put(KEY_AID, pedido.getAid());
		values.put(KEY_EID, pedido.getEid());
		values.put(KEY_FECHA, pedido.getFecha());
		values.put(KEY_CANTIDAD, pedido.getCantidad());
		values.put(KEY_PRECIO, pedido.getPrecio());
		values.put(KEY_OBS, pedido.getObservacion());
		values.put(KEY_ESTADO, pedido.getEstado());
		values.put(KEY_AFID, pedido.getAfid());
		values.put(KEY_OFERTA, pedido.getOferta());
		values.put(KEY_IDCLIF, pedido.getIdclif());
		values.put(KEY_PRECIOMANUAL, pedido.getPreciomanual());
		db.insert(TABLE_PEDIDO, null, values); db.close();
	}
	
	// Coger el último Autoid
	public int getLastAutoid() {
		int autoid=-1; SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PEDIDO, 
				new String[] {KEY_AUTOID}, null, null, null, null, "autoid DESC", "1");
		if(cursor.getCount()>0) { cursor.moveToFirst(); autoid = cursor.getInt(0); }
		cursor.close(); db.close(); return autoid;
	}
	
	// Coger el último Autoid de Promoción
	public int getLastPromoAutoid() {
		int autoid=0; SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PEDIDO, 
				new String[] {KEY_AUTOID}, null, null, null, null, "autoid ASC", "1");
		if(cursor.getCount()>0) { cursor.moveToFirst(); autoid = cursor.getInt(0); }
		cursor.close(); db.close(); return autoid;
	}
	
	// Coger el último Autoid Anterior
	public int getLastAutoidAnt() {
		int autoid=-1; SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PEDIDOANT, 
				new String[] {KEY_AUTOID}, null, null, null, null, "autoid DESC", "1");
		if(cursor.getCount()>0) { cursor.moveToFirst(); autoid = cursor.getInt(0); }
		cursor.close(); db.close(); return autoid;
	}
	
	// Coger el último Autoid de Promoción Anterior
	public int getLastPromoAutoidAnt() {
		int autoid=0; SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PEDIDOANT, 
				new String[] {KEY_AUTOID}, null, null, null, null, "autoid ASC", "1");
		if(cursor.getCount()>0) { cursor.moveToFirst(); autoid = cursor.getInt(0); }
		cursor.close(); db.close(); return autoid;
	}
	
	//Comprobar si tiene Preciosmanuales
	public boolean hasManualPrices(int pid/*, int idclif*/){
		boolean flagmanual=false; SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PEDIDO, new String[] { KEY_AUTOID, KEY_PID, KEY_IDCLIF, KEY_PRECIOMANUAL, KEY_AID }, 
				KEY_PRECIOMANUAL + "=? AND " + KEY_PID + /*"=? AND " + KEY_IDCLIF + */"=?"
				, new String[] { "S", String.valueOf(pid)/*, String.valueOf(idclif)*/ },
				null, null, null, null);
	if (cursor.getCount()>0){ flagmanual=true;
		/*cursor.moveToFirst();
		do{
			Log.e("PID",""+cursor.getInt(1));
			Log.e("AID",""+cursor.getInt(4));
			Log.e("IDCLIF",""+cursor.getInt(2));
			Log.e("PRECIOMANUAL",""+cursor.getString(3));
		}while(cursor.moveToNext());*/
	}
	cursor.close(); db.close();  return flagmanual;
	}
	
	//Recoger lista clientes de Autoventa con pedidos
	public int[] countIdclif(int eid){
		int[] nidclif; SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PEDIDO, new String[] { KEY_IDCLIF }, 
				KEY_EID + "=?", new String[] { String.valueOf(eid) },
				KEY_IDCLIF, null, KEY_FECHA, null);
		if (cursor.getCount()>0){
			nidclif = new int[cursor.getCount()]; int i=0;
			if (cursor.moveToFirst()) do 
				nidclif[i]=cursor.getInt(0);
			while (cursor.moveToNext()); 
		} else nidclif = new int[0];
		cursor.close(); db.close(); return nidclif;
	}
	
	// Mostrar un artículo pedido
	/*public Ped getPedido(int pid, int aid) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PEDIDO, new String[] { KEY_AUTOID, KEY_PID, KEY_AID, KEY_EID, KEY_FECHA, 
				KEY_CANTIDAD, KEY_PRECIO, KEY_OBS, KEY_ESTADO, KEY_AFID, KEY_OFERTA}, KEY_PID + "=? AND " +
				KEY_AID + "=?", new String[] { String.valueOf(pid), String.valueOf(aid) },
				null, null, null, null);
	if (cursor.getCount()>0) cursor.moveToFirst();
	else { cursor.close(); db.close();  return null; }
	Ped pedido= new Ped(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), 
			cursor.getInt(3), cursor.getString(4), cursor.getDouble(5), cursor.getDouble(6),
			cursor.getString(7), cursor.getInt(8), cursor.getString(9), cursor.getString(10));
	cursor.close(); db.close(); return pedido;
	}*/
	
	// Mostrar un artículo pedido
	public Ped getPedido(int autoid) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PEDIDO, new String[] { KEY_AUTOID, KEY_PID, KEY_AID, KEY_EID, KEY_FECHA, 
				KEY_CANTIDAD, KEY_PRECIO, KEY_OBS, KEY_ESTADO, KEY_AFID, KEY_OFERTA, KEY_IDCLIF, KEY_PRECIOMANUAL}, 
				KEY_AUTOID + "=?", new String[] { String.valueOf(autoid)}, null, null, null, null);
	if (cursor.getCount()>0) cursor.moveToFirst();
	else { cursor.close(); db.close();  return null; }
	Ped pedido= new Ped(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), 
			cursor.getInt(3), cursor.getString(4), cursor.getDouble(5), cursor.getDouble(6),
			cursor.getString(7), cursor.getInt(8), cursor.getString(9), cursor.getString(10),
			cursor.getInt(11), cursor.getString(12));
	cursor.close(); db.close(); return pedido;
	}
	
	// Tomar un pendiente para añadir artículo
	public Ped getPedidoPendiente(int eid) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PEDIDO, new String[] { KEY_AUTOID, KEY_PID, KEY_AID, KEY_EID, KEY_FECHA, 
				KEY_CANTIDAD, KEY_PRECIO, KEY_OBS, KEY_ESTADO, KEY_AFID, KEY_OFERTA, KEY_IDCLIF, KEY_PRECIOMANUAL}, 
				KEY_EID + "=? AND " + KEY_ESTADO + "<2", new String[] { String.valueOf(eid) }, 
				null, null, null, null);
		if (cursor.getCount()>0) cursor.moveToFirst();
		else{ cursor.close(); db.close(); return null; }
		Ped pedido= new Ped(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), 
				cursor.getInt(3), cursor.getString(4), cursor.getDouble(5), cursor.getDouble(6),
				cursor.getString(7), cursor.getInt(8), cursor.getString(9), cursor.getString(10),
				cursor.getInt(11), cursor.getString(12));
		cursor.close(); db.close(); return pedido;
		}
	
	// Tomar un pendiente para añadir artículo [upgrade: le meto idclif. Si es 0, no es autoventa]
		public Ped getPedidoPendiente(int eid, int idclif) {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.query(TABLE_PEDIDO, new String[] { KEY_AUTOID, KEY_PID, KEY_AID, KEY_EID, KEY_FECHA, 
					KEY_CANTIDAD, KEY_PRECIO, KEY_OBS, KEY_ESTADO, KEY_AFID, KEY_OFERTA, KEY_IDCLIF, KEY_PRECIOMANUAL}, 
					KEY_EID + "=? AND " + KEY_IDCLIF + "=? AND " + KEY_ESTADO + "<2", 
					new String[] { String.valueOf(eid), String.valueOf(idclif) }, null, null, null, null);
			if (cursor.getCount()>0) cursor.moveToFirst();
			else{ cursor.close(); db.close(); return null; }
			Ped pedido= new Ped(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), 
					cursor.getInt(3), cursor.getString(4), cursor.getDouble(5), cursor.getDouble(6),
					cursor.getString(7), cursor.getInt(8), cursor.getString(9), cursor.getString(10),
					cursor.getInt(11), cursor.getString(12));
			cursor.close(); db.close(); return pedido;
			}
		
	// Tomar un pendiente de un pedido concreto para añadir artículo
	public Ped getPedidoPendienteDe(int eid, int pid) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PEDIDO, new String[] { KEY_AUTOID, KEY_PID, KEY_AID, KEY_EID, KEY_FECHA, 
				KEY_CANTIDAD, KEY_PRECIO, KEY_OBS, KEY_ESTADO, KEY_AFID, KEY_OFERTA, KEY_IDCLIF, KEY_PRECIOMANUAL}, 
				KEY_EID + "=? AND " + KEY_PID + "=? AND " + KEY_ESTADO + "<2", new String[] { 
				String.valueOf(eid), String.valueOf(pid) }, null, null, null, null);
		if (cursor.getCount()>0) cursor.moveToFirst();
		else{ cursor.close(); db.close(); return null; }
		Ped pedido= new Ped(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), 
				cursor.getInt(3), cursor.getString(4), cursor.getDouble(5), cursor.getDouble(6),
				cursor.getString(7), cursor.getInt(8), cursor.getString(9), cursor.getString(10),
				cursor.getInt(11), cursor.getString(12));
		cursor.close(); db.close(); return pedido;
		}
	
	// Mirar cuántos establecimientos tiene el cliente con pedidos
	public int getEstablecimientosConPedido() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(true, TABLE_PEDIDO, new String[]{KEY_EID}, 
				null, null, null, null, null, null);
		int establecimientos = cursor.getCount();
		cursor.close(); db.close(); return establecimientos;
		}
	
	// Recoger todos los pedidos pendientes
	public List<Ped> getPedidosPendientes(int eid) {
		List<Ped> listaPedidos = new ArrayList<Ped>();
		String selectQuery = "SELECT * FROM " + TABLE_PEDIDO + " WHERE " + KEY_EID + "=" +
				eid + " AND " + KEY_ESTADO + "<2 ORDER BY " + KEY_AUTOID + " ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do { Ped pedido = new Ped();
				pedido.setAutoid(cursor.getInt(0));
				pedido.setPid(cursor.getInt(1));
				pedido.setAid(cursor.getInt(2));
				pedido.setEid(cursor.getInt(3));
				pedido.setFecha(cursor.getString(4));
				pedido.setCantidad(cursor.getDouble(5));
				pedido.setPrecio(cursor.getDouble(6));
				pedido.setObservacion(cursor.getString(7));
				pedido.setEstado(cursor.getInt(8));
				pedido.setAfid(cursor.getString(9));
				pedido.setOferta(cursor.getString(10));
				pedido.setIdclif(cursor.getInt(11));
				pedido.setPreciomanual(cursor.getString(12));
				listaPedidos.add(pedido); } while (cursor.moveToNext()); }
		cursor.close(); db.close(); return listaPedidos;
	}
	
	// Recoger todos los pedidos pendientes
		public List<Ped> getPedidosPendientesDe(int eid, int idclif) {
		List<Ped> listaPedidos = new ArrayList<Ped>();
		String selectQuery = "SELECT * FROM " + TABLE_PEDIDO + " WHERE " + KEY_EID + "=" + eid + " AND " 
				+ KEY_IDCLIF + "=" + idclif + " AND " + KEY_ESTADO + "<2 ORDER BY " + KEY_AUTOID + " ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do { Ped pedido = new Ped();
				pedido.setAutoid(cursor.getInt(0));
				pedido.setPid(cursor.getInt(1));
				pedido.setAid(cursor.getInt(2));
				pedido.setEid(cursor.getInt(3));
				pedido.setFecha(cursor.getString(4));
				pedido.setCantidad(cursor.getDouble(5));
				pedido.setPrecio(cursor.getDouble(6));
				pedido.setObservacion(cursor.getString(7));
				pedido.setEstado(cursor.getInt(8));
				pedido.setAfid(cursor.getString(9));
				pedido.setOferta(cursor.getString(10));
				pedido.setIdclif(cursor.getInt(11));
				pedido.setPreciomanual(cursor.getString(12));
				listaPedidos.add(pedido); } while (cursor.moveToNext()); }
		cursor.close(); db.close(); return listaPedidos;
	}
	
	//Recoger todos los pedidos finalizados
	public List<Ped> getPedidosFinalizados() {
		List<Ped> listaPedidos = new ArrayList<Ped>();
		String selectQuery = "SELECT * FROM " + TABLE_PEDIDO 
				+ " WHERE " + KEY_ESTADO + ">1 ORDER BY " + KEY_EID + " ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do { Ped pedido = new Ped();
			pedido.setAutoid(cursor.getInt(0));
			pedido.setPid(cursor.getInt(1));
			pedido.setAid(cursor.getInt(2));
			pedido.setEid(cursor.getInt(3));
			pedido.setFecha(cursor.getString(4));
			pedido.setCantidad(cursor.getDouble(5));
			pedido.setPrecio(cursor.getDouble(6));
			pedido.setObservacion(cursor.getString(7));
			pedido.setEstado(cursor.getInt(8));
			pedido.setAfid(cursor.getString(9));
			pedido.setOferta(cursor.getString(10));
			pedido.setIdclif(cursor.getInt(11));
			pedido.setPreciomanual(cursor.getString(12));
			listaPedidos.add(pedido); } while (cursor.moveToNext()); }
		cursor.close(); db.close(); return listaPedidos;
	}
	
	//Recoger todos idclif de un establecimiento Autoventa con OP
		public List<Integer> getPedidosClientes(int eid) {
			List<Integer> listaIdclif= new ArrayList<Integer>();
			String selectQuery = "SELECT DISTINCT " + KEY_IDCLIF + " FROM " + TABLE_PEDIDO 
					+ " WHERE " + KEY_EID + "=" + eid + " AND " 
					+ KEY_ESTADO + "<2 ORDER BY " + KEY_IDCLIF + " ASC";
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) { do { listaIdclif.add(cursor.getInt(0)); } while (cursor.moveToNext()); }
			cursor.close(); db.close(); return listaIdclif;
		}
	
	// Coger todos el historial de pedidos de un Establecimiento
	public List<Ped> getAllPedidos(int eid) {
		List<Ped> listaPedidos = new ArrayList<Ped>();
		String selectQuery = "SELECT DISTINCT * FROM " + TABLE_PEDIDO 
			+ " WHERE " + KEY_EID + "=" + eid + " ORDER BY " + KEY_AUTOID + " ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do { Ped pedido = new Ped();
			pedido.setAutoid(cursor.getInt(0));
			pedido.setPid(cursor.getInt(1));
			pedido.setAid(cursor.getInt(2));
			pedido.setEid(cursor.getInt(3));
			pedido.setFecha(cursor.getString(4));
			pedido.setCantidad(cursor.getDouble(5));
			pedido.setPrecio(cursor.getDouble(6));
			pedido.setObservacion(cursor.getString(7));
			pedido.setEstado(cursor.getInt(8));
			pedido.setAfid(cursor.getString(9));
			pedido.setOferta(cursor.getString(10));
			pedido.setIdclif(cursor.getInt(11));
			pedido.setPreciomanual(cursor.getString(12));
			listaPedidos.add(pedido); } while (cursor.moveToNext()); }
	cursor.close(); db.close(); return listaPedidos;
	}
	
	// Coger todos los artículos de un pedido
	public List<Ped> getAllArticulosPedidos(int pid) {
		List<Ped> listaPedidos = new ArrayList<Ped>();
		String selectQuery = "SELECT * FROM " + TABLE_PEDIDO
			+ " WHERE " + KEY_PID + "=" + pid + " ORDER BY " + KEY_AUTOID + " ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do { Ped pedido = new Ped();
			pedido.setAutoid(cursor.getInt(0));
			pedido.setPid(cursor.getInt(1));
			pedido.setAid(cursor.getInt(2));
			pedido.setEid(cursor.getInt(3));
			pedido.setFecha(cursor.getString(4));
			pedido.setCantidad(cursor.getDouble(5));
			pedido.setPrecio(cursor.getDouble(6));
			pedido.setObservacion(cursor.getString(7));
			pedido.setEstado(cursor.getInt(8));
			pedido.setAfid(cursor.getString(9));
			pedido.setOferta(cursor.getString(10));
			pedido.setIdclif(cursor.getInt(11));
			pedido.setPreciomanual(cursor.getString(12));
			listaPedidos.add(pedido); } while (cursor.moveToNext()); }
		cursor.close(); db.close(); return listaPedidos;
	}
	
	// Contar artículos pedidos
	public int getArticulosPedidosCount(int pid) {
		int count; String countQuery = "SELECT * FROM " + TABLE_PEDIDO 
			+ " WHERE " + KEY_PID + "=" + pid;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount(); cursor.close(); db.close(); return count;
	}
	
	// Contar artículos pedidos en el historial
		public int getArticulosPedidosAntCount(int eid) {
			int count; String countQuery = "SELECT * FROM " + TABLE_PEDIDOANT 
				+ " WHERE " + KEY_EID + "=" + eid;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			count = cursor.getCount(); cursor.close(); db.close(); return count;
		}
	
	// Contar pedidos
	public int getPedidosCount() {
		int count; String countQuery = "SELECT DISTINCT " + KEY_PID + " FROM " + TABLE_PEDIDO;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount(); cursor.close(); db.close(); return count;
	}
	
	// Sacar último Pid para crear pedido || SUSTITUIDO POR MIRARLO EN PREFERENCIAS
	/*public int getLastPid() {
		int last=0; String Query = "SELECT MAX(" + KEY_PID + ") FROM " + TABLE_PEDIDO;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(Query, null);
		if (cursor.moveToFirst()) { 
			last = cursor.getInt(0); cursor.close(); db.close(); return last; }
		else { cursor.close(); db.close(); return last; }
	}*/
	
	// Contar pedidos finalizados
	public int getPedidosFinalizadosCount() {
		int count; String countQuery = "SELECT DISTINCT " + KEY_PID + " FROM " 
				+ TABLE_PEDIDO + " WHERE " + KEY_ESTADO + ">1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount(); cursor.close(); db.close(); return count;
	}
			
	// Actualizar un pedido
	public int updatePedido(Ped pedido) {
		int ret; SQLiteDatabase db = this.getWritableDatabase();	
		ContentValues values = new ContentValues();
		values.put(KEY_AUTOID, pedido.getAutoid());
		values.put(KEY_PID, pedido.getPid());
		values.put(KEY_AID, pedido.getAid());
		values.put(KEY_EID, pedido.getEid());
		values.put(KEY_FECHA, pedido.getFecha());
		values.put(KEY_CANTIDAD, pedido.getCantidad());
		values.put(KEY_PRECIO, pedido.getPrecio());
		values.put(KEY_OBS, pedido.getObservacion());
		values.put(KEY_ESTADO, pedido.getEstado());
		values.put(KEY_AFID, pedido.getAfid());
		values.put(KEY_OFERTA, pedido.getOferta());
		values.put(KEY_IDCLIF, pedido.getIdclif());
		values.put(KEY_PRECIOMANUAL, pedido.getPreciomanual());
		ret = db.update(TABLE_PEDIDO, values, KEY_AUTOID + " = ?", 
				new String[] { String.valueOf(pedido.getAutoid()) });
		db.close(); return ret;
	}
	
	// Actualizar un pedido
	public void switchCBArticuloPedido(Ped pedido) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_AUTOID, pedido.getAutoid());
		values.put(KEY_PID, pedido.getPid());
		values.put(KEY_AID, pedido.getAid());
		values.put(KEY_EID, pedido.getEid());
		values.put(KEY_FECHA, pedido.getFecha());
		values.put(KEY_CANTIDAD, pedido.getCantidad());
		values.put(KEY_PRECIO, pedido.getPrecio());
		values.put(KEY_OBS, pedido.getObservacion());
		values.put(KEY_AFID, pedido.getAfid());
		values.put(KEY_OFERTA, pedido.getOferta());
		values.put(KEY_IDCLIF, pedido.getIdclif());
		values.put(KEY_PRECIOMANUAL, pedido.getPreciomanual());
		switch(pedido.getEstado()){
			case 0: values.put(KEY_ESTADO, -1);				//Lista Des-sel -> Lista Sel 
			break;																		//& Pendiente Des-sel -> Pendiente Sel
			case -1: values.put(KEY_ESTADO, 0);				//Lista Sel -> Lista Des-sel
			break; 									  								//& Pendiente Sel -> Pendiente Des-sel
			case 1: values.put(KEY_ESTADO, -2);break; //Cerrado Des-sel -> Cerrado Sel
			case -2: values.put(KEY_ESTADO, 1);break; //Cerrado Sel -> Cerrado Des-sel
			case 2: values.put(KEY_ESTADO, -3);break; //Enviado Des-sel -> Enviado Sel
			case -3: values.put(KEY_ESTADO, 2);break; //Enviado Sel -> Enviado Des-sel
			case 3: values.put(KEY_ESTADO, -4);break; //Finalizado Des-sel -> Finalizado Sel
			case -4: values.put(KEY_ESTADO, 3);} 	  	//Finalizado Sel -> Finalizado Des-sel
		db.update(TABLE_PEDIDO, values, KEY_AUTOID + " = ?", 
				new String[] { String.valueOf(pedido.getAutoid()) });
		db.close();
	}
	
	//Comprobar si un artículo está pedido
	public boolean checkArticuloPedido(int aid){
		int count = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "SELECT DISTINCT " + KEY_PID + " FROM " + TABLE_PEDIDO + " WHERE " + KEY_AID + "=" + aid;
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount(); cursor.close(); db.close();
		return count>0;
	}
	
	// Borrar un articulo pedido
	public void deleteArticuloPedido(Ped pedido) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PEDIDO, KEY_AUTOID + " = ?", new String[] 
			{ String.valueOf(pedido.getAutoid()) });
		db.close();
	}
	
	// Borrar un pedido entero
	public void deletePedido(Ped pedido) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PEDIDO, KEY_PID + " = ?", 
				new String[] { String.valueOf(pedido.getPid()) });
		db.close();
	}
	
	//Reset tabla pedidos
	public void resetPedidos(){
	SQLiteDatabase db = this.getWritableDatabase();
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEDIDO); onCreate(db);
	}
	
	/////////////////////////
	//XXX Funciones de PedidoAnt 
	/////////////////////////
	//De Pedido a PedidoAnt (List<Ped>) [dePedidoaPedidoAnt]
	//De PedidoAnt a Pedido (List<Ped>) [dePedidoAntaPedido]
	//Añadir (Ped) [addPedidoAnt]
	//Mostrar pedido Anterior (eid) [getPedidoAnt]
	//Mostrar articulo(pid, aid) [getArticuloPedidoAnt]
	//Mostrar articulos(pid) [] (¿Desaparecido?)
	//Contar [getEstablecimientosConPedidoAnt]
	//Actualizar (Art) [updatePedidoAnt]
	//Borrar Artículo (Ped) [deleteArticuloPedidoAnt] (no creo que lo use)
	//Borrar Pedido Anterior (eid) [deletePedidoAnt]
	//Reiniciar [resetPedidosAnt]
	
	/*// Pasar de pedido a pedidoanterior
	public void dePedidoaPedidoAnt(List<Ped> lp) { (PREVIO FIX 3-Ene)
		SQLiteDatabase db = this.getWritableDatabase();
		for(Ped p : lp) { ContentValues values = new ContentValues(); 
		values.put(KEY_PID, p.getPid());
		values.put(KEY_AID, p.getAid());
		values.put(KEY_EID, p.getEid());
		values.put(KEY_FECHA, p.getFecha());
		values.put(KEY_CANTIDAD, p.getCantidad());
		values.put(KEY_PRECIO, p.getPrecio());
		values.put(KEY_OBS, p.getObservacion());
		values.put(KEY_ESTADO, p.getEstado());
		values.put(KEY_AFID, p.getAfid());
		db.insert(TABLE_PEDIDOANT, null, values); }} db.close();
		for(Ped p : lp) deletePedido(p);
	}*/
	
	// Pasar de pedido a pedidoanterior
	/*public void dePedidoaPedidoAnt(List<Ped> lp) {
		List<Ped> estado, actualizar= new ArrayList<Ped>();
		estado = getPedidoAnt(lp.get(0).getEid());
		SQLiteDatabase db = this.getWritableDatabase();
		for(Ped p : lp) { int flag=0;
		if(estado!=null){ for(Ped ped : estado){ //Actualiza si está repetido
				if(p.getAid()==ped.getAid()) actualizar.add(ped); flag=1; break; }}
		if(flag==0){ ContentValues values = new ContentValues();
			values.put(KEY_PID, p.getPid());
			values.put(KEY_AID, p.getAid());
			values.put(KEY_EID, p.getEid());
			values.put(KEY_FECHA, p.getFecha());
			values.put(KEY_CANTIDAD, p.getCantidad());
			values.put(KEY_PRECIO, p.getPrecio());
			values.put(KEY_OBS, p.getObservacion());
			values.put(KEY_ESTADO, p.getEstado());
			values.put(KEY_AFID, p.getAfid());
			db.insert(TABLE_PEDIDOANT, null, values); }} db.close();
		if(!actualizar.isEmpty()) for(Ped ped : actualizar) updatePedidoAnt(ped);
		for(Ped p : lp) deletePedido(p);
	}*/
	
	/*//Proceso inverso, para copiar uno anterior
	public void dePedidoAntaPedido(List<Ped> lp) { (PREVIO FIX 3-Ene)
		//int pid = getPedidosCount()+1, eid=0;
		int pid = sharedPrefs.getInt("nped",1)+1, eid=0;
		SQLiteDatabase db = this.getWritableDatabase();
		for(Ped p : lp) { ContentValues values = new ContentValues();
			values.put(KEY_PID, pid);
			values.put(KEY_AID, p.getAid());
			values.put(KEY_EID, p.getEid());
			values.put(KEY_FECHA, "");
			values.put(KEY_CANTIDAD, p.getCantidad());
			values.put(KEY_PRECIO, p.getPrecio());
			values.put(KEY_OBS, p.getObservacion());
			values.put(KEY_ESTADO, 0);
			values.put(KEY_AFID, p.getAfid());
			db.insert(TABLE_PEDIDO, null, values); eid=p.getEid(); }
		db.close(); deletePedidoAnt(eid);
		SharedPreferences.Editor spe = sharedPrefs.edit();
		spe.putInt("nped",sharedPrefs.getInt("nped",0)+1).commit();
	}*/
	
	//Proceso inverso, para copiar uno anterior
	public void dePedidoAntaPedido(List<Ped> lp) {
		//int pid = sharedPrefs.getInt("nped",1)+1;
		SQLiteDatabase db = this.getWritableDatabase();
		for(Ped pedido : lp) {
			ContentValues values = new ContentValues();
			values.put(KEY_AUTOID, this.getLastAutoid());
			values.put(KEY_PID, pedido.getPid()/*pid*/);
			values.put(KEY_AID, pedido.getAid());
			values.put(KEY_EID, pedido.getEid());
			values.put(KEY_FECHA, "");
			values.put(KEY_CANTIDAD, pedido.getCantidad());
			values.put(KEY_PRECIO, pedido.getPrecio());
			values.put(KEY_OBS, pedido.getObservacion());
			values.put(KEY_ESTADO, 0);
			values.put(KEY_AFID, pedido.getAfid());
			values.put(KEY_OFERTA, pedido.getOferta());
			values.put(KEY_IDCLIF, pedido.getIdclif());
			values.put(KEY_PRECIOMANUAL, pedido.getPreciomanual());
			db.insert(TABLE_PEDIDO, null, values); } db.close(); 
		//SharedPreferences.Editor spe = sharedPrefs.edit();
		//spe.putInt("nped",sharedPrefs.getInt("nped",0)+1).commit();
	}
	
	// Añadir un articulo a un pedido anterior
	public void addPedidoAnt(Ped pedido) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		//values.put(KEY_AUTOID, pedido.getAutoid());
		values.put(KEY_PID, pedido.getPid());
		values.put(KEY_AID, pedido.getAid());
		values.put(KEY_EID, pedido.getEid());
		values.put(KEY_FECHA, pedido.getFecha());
		values.put(KEY_CANTIDAD, pedido.getCantidad());
		values.put(KEY_PRECIO, pedido.getPrecio());
		values.put(KEY_OBS, pedido.getObservacion());
		values.put(KEY_ESTADO, pedido.getEstado());
		values.put(KEY_AFID, pedido.getAfid());
		values.put(KEY_OFERTA, pedido.getOferta());
		values.put(KEY_IDCLIF, pedido.getIdclif());
		values.put(KEY_PRECIOMANUAL, pedido.getPreciomanual());
		db.insert(TABLE_PEDIDOANT, null, values); db.close();
	}
	
	// Coger todo el pedido anterior
	public List<Ped> getPedidoAnt(int eid) {
		SQLiteDatabase db = this.getReadableDatabase();
		List<Ped> anterior = new ArrayList<Ped>();
		Cursor cursor = db.query(TABLE_PEDIDOANT, new String[] { KEY_AUTOID, 
				KEY_PID, KEY_AID, KEY_EID, KEY_FECHA, KEY_CANTIDAD, KEY_PRECIO, 
				KEY_OBS, KEY_ESTADO, KEY_AFID, KEY_OFERTA, KEY_IDCLIF, KEY_PRECIOMANUAL}, KEY_EID + "=?",
				new String[] { String.valueOf(eid)}, null, null, null, null);
		if (cursor.moveToFirst()) { do { Ped pedido = new Ped();
			pedido.setAutoid(cursor.getInt(0));
			pedido.setPid(cursor.getInt(1));
			pedido.setAid(cursor.getInt(2));
			pedido.setEid(cursor.getInt(3));
			pedido.setFecha(cursor.getString(4));
			pedido.setCantidad(cursor.getDouble(5));
			pedido.setPrecio(cursor.getDouble(6));
			pedido.setObservacion(cursor.getString(7));
			pedido.setEstado(cursor.getInt(8));
			pedido.setAfid(cursor.getString(9));
			pedido.setOferta(cursor.getString(10));
			pedido.setIdclif(cursor.getInt(11));
			pedido.setPreciomanual(cursor.getString(12));
			anterior.add(pedido); } while (cursor.moveToNext()); }
		else { cursor.close(); db.close(); return null; }
		cursor.close(); db.close(); return anterior;
	}
	
	//Comentado por no contener EID
	/*// Mostrar un artículo del pedido anterior
	public Ped getArticuloPedidoAnt(int pid, int aid) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PEDIDOANT, new String[] { KEY_AUTOID, KEY_PID, KEY_AID, KEY_EID,
			KEY_FECHA, KEY_CANTIDAD, KEY_PRECIO, KEY_OBS, KEY_ESTADO, KEY_AFID, KEY_OFERTA, KEY_PRECIOMANUAL},
			KEY_PID + "=? AND " + KEY_AID + "=?", new String[] { String.valueOf(pid), String.valueOf(aid) },
			null, null, null, null);
	if (cursor.getCount()>0) cursor.moveToFirst();
	else { cursor.close(); db.close(); return null; }
	Ped pedido= new Ped(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), 
			cursor.getInt(3), cursor.getString(4), cursor.getDouble(5), cursor.getDouble(6), 
			cursor.getString(7), cursor.getInt(8), cursor.getString(9), cursor.getString(10),
			0, cursor.getString(11));
	cursor.close(); db.close(); return pedido;
	}*/
	
	// Mostrar un artículo del pedido anterior, de un establecimiento concreto
		public Ped getArticuloPedidoAnt(int pid, int aid, int eid) {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.query(TABLE_PEDIDOANT, new String[] { KEY_AUTOID, KEY_PID, KEY_AID, KEY_EID,
				KEY_FECHA, KEY_CANTIDAD, KEY_PRECIO, KEY_OBS, KEY_ESTADO, KEY_AFID, KEY_OFERTA, KEY_PRECIOMANUAL},
				KEY_PID + "=? AND " + KEY_AID + "=? AND " + KEY_EID + "=?", 
				new String[] { String.valueOf(pid), String.valueOf(aid), String.valueOf(eid) },
				null, null, null, null);
		if (cursor.getCount()>0) cursor.moveToFirst();
		else { cursor.close(); db.close(); return null; }
		Ped pedido= new Ped(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), 
				cursor.getInt(3), cursor.getString(4), cursor.getDouble(5), cursor.getDouble(6), 
				cursor.getString(7), cursor.getInt(8), cursor.getString(9), cursor.getString(10),
				0, cursor.getString(11));
		cursor.close(); db.close(); return pedido;
		}
	
	//Coger todos los pedidos anteriores de un establecimiento
	public List<Ped> getAllPedidosAnt(int eid) {
		List<Ped> listaPedidos = new ArrayList<Ped>();
		String selectQuery = "SELECT DISTINCT * FROM " + TABLE_PEDIDOANT 
				+ " WHERE " + KEY_EID + "=" + eid + " ORDER BY " + KEY_AUTOID + " ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do { Ped pedido = new Ped();
			pedido.setAutoid(cursor.getInt(0));	
			pedido.setPid(cursor.getInt(1));
			pedido.setAid(cursor.getInt(2));
			pedido.setEid(cursor.getInt(3));
			pedido.setFecha(cursor.getString(4));
			pedido.setCantidad(cursor.getDouble(5));
			pedido.setPrecio(cursor.getDouble(6));
			pedido.setObservacion(cursor.getString(7));
			pedido.setEstado(cursor.getInt(8));
			pedido.setAfid(cursor.getString(9));
			pedido.setOferta(cursor.getString(10));
			pedido.setIdclif(cursor.getInt(11));
			pedido.setPreciomanual(cursor.getString(12));
			listaPedidos.add(pedido); } while (cursor.moveToNext()); }
		cursor.close(); db.close(); return listaPedidos;
	}

	// Mirar cuántos establecimientos tienen pedido anterior
	public int getEstablecimientosConPedidoAnt() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(true, TABLE_PEDIDOANT, new String[]{KEY_EID}, 
			null, null, null, null, null, null);
		int establecimientos = cursor.getCount(); 
		cursor.close(); db.close(); return establecimientos;
	}
	
	public List<Ped> searchPedidoAnt(int eid, String donde, String que) {
		SQLiteDatabase db = this.getReadableDatabase();
		List<Ped> buscado = new ArrayList<Ped>();
		Cursor cursor = db.query(TABLE_PEDIDOANT, new String[] { KEY_AUTOID, KEY_PID,
				KEY_AID, KEY_EID, KEY_FECHA, KEY_CANTIDAD, KEY_PRECIO, KEY_OBS, 
				KEY_ESTADO, KEY_AFID, KEY_OFERTA, KEY_IDCLIF, KEY_PRECIOMANUAL},
				KEY_EID + "=? AND "+donde+" LIKE ?",
			new String[] { String.valueOf(eid), que }, null, null, null, null);
		/*Log.e("QUERY","SELECT * WHERE "+
				KEY_EID+"="+eid+" AND "+donde+" LIKE "+que);*/
		if (cursor.moveToFirst()) { do {Ped pedido = new Ped();
			pedido.setAutoid(cursor.getInt(0));
			pedido.setPid(cursor.getInt(1));
			pedido.setAid(cursor.getInt(2));
			pedido.setEid(cursor.getInt(3));
			pedido.setFecha(cursor.getString(4));
			pedido.setCantidad(cursor.getDouble(5));
			pedido.setPrecio(cursor.getDouble(6));
			pedido.setObservacion(cursor.getString(7));
			pedido.setEstado(cursor.getInt(8));
			pedido.setAfid(cursor.getString(9));
			pedido.setOferta(cursor.getString(10));
			pedido.setIdclif(cursor.getInt(11));
			pedido.setPreciomanual(cursor.getString(12));
			buscado.add(pedido); } while (cursor.moveToNext()); }
		else { cursor.close(); db.close(); return null; }
		cursor.close(); db.close(); return buscado;
	}

	// Actualizar un pedido anterior (Cambios de un artículo vuelto a ser pedido) 
	//(Seguramente, para ver los cambios de un pedido con otra BBDD)
	public int updatePedidoAnt(Ped pedido) {
		int ret; SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_AUTOID, pedido.getAutoid());
		values.put(KEY_PID, pedido.getPid());
		values.put(KEY_AID, pedido.getAid());
		values.put(KEY_EID, pedido.getEid());
		values.put(KEY_FECHA, pedido.getFecha());
		values.put(KEY_CANTIDAD, pedido.getCantidad());
		values.put(KEY_PRECIO, pedido.getPrecio());
		values.put(KEY_OBS, pedido.getObservacion());
		values.put(KEY_ESTADO, pedido.getEstado());
		values.put(KEY_AFID, pedido.getAfid());
		values.put(KEY_OFERTA, pedido.getOferta());
		values.put(KEY_IDCLIF, pedido.getIdclif());
		values.put(KEY_PRECIOMANUAL, pedido.getPreciomanual());
		ret = db.update(TABLE_PEDIDOANT, values, 
			KEY_AUTOID + " = ?",
			new String[] { String.valueOf(pedido.getAutoid())});
			/*KEY_EID + "=? AND " + KEY_AID + "=?", 
			new String[] { String.valueOf(pedido.getEid()), 
				String.valueOf(pedido.getAid()) });*/
		db.close(); return ret;
	}
	
	// Borrar un articulo pedido anterior
	public void deleteArticuloPedidoAnt(Ped pedido) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PEDIDOANT, KEY_AUTOID + " = ?", new String[] 
				{ String.valueOf(pedido.getAutoid()) }); db.close();
	}
	
	// Borrar un pedido anterior de un establecimiento para reemplazar
	public void deletePedidoAnt(int eid) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PEDIDOANT, KEY_EID + " = ?", new String[] { String.valueOf(eid) });
		db.close();
	}
	
	//Reset tabla pedidos anterior
	public void resetPedidosAnt(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEDIDOANT); onCreate(db); db.close();
	}

	////////////////////////
	//XXX Funciones de Mensajes 
	////////////////////////
	//
	// Enviar (Msj) [almacenarMensajeEnviado]
	// Recibir (Msj) [almacenarMensajeRecibido]
	// Mostrar Enviados [getAllMensajesEnv]
	// Mostrar Recibidos [getAllMensajesRec]
	// Mostrar mensaje Pendiente [mensajePendiente]
	// Último mid de Recibidos [getLastMidRec]
	// Último mid de Enviados [getLastMidEnv]
	// Borrar mensaje Enviado/Pendiente (Msj) [deleteMensajeEnviado]
	// Borrar mensaje Recibido (Msj) [deleteMensajeRecibido]
	// Actualizar mensaje Enviado (Msj) [actualizarMensajeEnviado]
	// Actualizar mensaje Recibido (Msj) [actualizarMensajeRecibido]
	// Ver mensaje Enviado por mid (mid) [getMensajeEnv]
	// Ver mensaje Recibido por mid (mid) [getMensajeRec]
	
	//Enviar mensaje
	public void almacenarMensajeEnviado(Msj mensaje){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_MID, mensaje.getMid());
		values.put(KEY_CLIENTEGLOBAL, mensaje.getClienteglobal());
		values.put(KEY_MENSAJE, mensaje.getMensaje());
		values.put(KEY_HTTP, mensaje.getHttp());
		values.put(KEY_FECHAREALIZ, mensaje.getFecharealiz());
		values.put(KEY_HORAREALIZ, mensaje.getHorarealiz());
		values.put(KEY_EID, mensaje.getEid());
		values.put(KEY_TIPOMSJ, mensaje.getTipomsj());
		values.put(KEY_DESDEFECHA, mensaje.getDesdefecha());
		values.put(KEY_HASTAFECHA, mensaje.getHastafecha());
		values.put(KEY_FECHAREC, mensaje.getFecharec());
		values.put(KEY_HORAREC, mensaje.getHorarec());
		values.put(KEY_ESTADO, mensaje.getEstado());
		values.put(KEY_RMTE, mensaje.getRmte());
		values.put(KEY_MIDBD, mensaje.getMidbd());
		values.put(KEY_XML, mensaje.getXml());
		//values.put(KEY_IDMSJAPPMOVIL, mensaje.getIdmsjappmovil()); es autoincrement
		db.insert(TABLE_MENSAJESENV, null, values); db.close();
	}
	
	//Recibir mensaje
	public void recibirMensaje(Msj mensaje){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_MID, mensaje.getMid());
		values.put(KEY_CLIENTEGLOBAL, mensaje.getClienteglobal());
		values.put(KEY_MENSAJE, mensaje.getMensaje());
		values.put(KEY_HTTP, mensaje.getHttp());
		values.put(KEY_FECHAREALIZ, mensaje.getFecharealiz());
		values.put(KEY_HORAREALIZ, mensaje.getHorarealiz());
		values.put(KEY_EID, mensaje.getEid());
		values.put(KEY_TIPOMSJ, mensaje.getTipomsj());
		values.put(KEY_DESDEFECHA, mensaje.getDesdefecha());
		values.put(KEY_HASTAFECHA, mensaje.getHastafecha());
		values.put(KEY_FECHAREC, mensaje.getFecharec());
		values.put(KEY_HORAREC, mensaje.getHorarec());
		values.put(KEY_ESTADO, mensaje.getEstado());
		values.put(KEY_RMTE, mensaje.getRmte());
		values.put(KEY_MIDBD, mensaje.getMidbd());
		values.put(KEY_XML, mensaje.getXml());
		//values.put(KEY_IDMSJAPPMOVIL, mensaje.getIdmsjappmovil()); es autoincrement
		db.insert(TABLE_MENSAJESREC, null, values); db.close();
	}
	
	// Coger todos los mensajes enviados
	public List<Msj> getAllMensajesEnv() {
		List<Msj> listaMensajesEnv = new ArrayList<Msj>();
		String selectQuery = "SELECT * FROM " + TABLE_MENSAJESENV + 
				" ORDER BY " + KEY_MID + " DESC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do { Msj mensaje = new Msj();
			mensaje.setMid(cursor.getInt(0)); 
			mensaje.setClienteglobal(cursor.getInt(1));
			mensaje.setMensaje(cursor.getString(2)); 
			mensaje.setHttp(cursor.getString(3));
			mensaje.setFecharealiz(cursor.getString(4)); 
			mensaje.setHorarealiz(cursor.getString(5)); 
			mensaje.setZonainf(cursor.getString(6));
			mensaje.setEid(cursor.getInt(7));
			mensaje.setTipomsj(cursor.getString(8));
			mensaje.setDesdefecha(cursor.getString(9));
			mensaje.setHastafecha(cursor.getString(10)); 
			mensaje.setFecharec(cursor.getString(11));
			mensaje.setHorarec(cursor.getString(12)); 
			mensaje.setEstado(cursor.getString(13));
			mensaje.setRmte(cursor.getString(15));
			mensaje.setMidbd(cursor.getInt(16));
			mensaje.setXml(cursor.getString(17));
			listaMensajesEnv.add(mensaje); } while (cursor.moveToNext()); }
		cursor.close(); db.close(); return listaMensajesEnv;
	}
	
	// Coger todos los mensajes enviados
	public List<Msj> getAllMensajesRec() {
		List<Msj> listaMensajesRec = new ArrayList<Msj>();
		String selectQuery = "SELECT * FROM " + TABLE_MENSAJESREC +
				" ORDER BY " + KEY_MID + " DESC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do { Msj mensaje = new Msj();
			mensaje.setMid(cursor.getInt(0)); 
			mensaje.setClienteglobal(cursor.getInt(1));
			mensaje.setMensaje(cursor.getString(2)); 
			mensaje.setHttp(cursor.getString(3));
			mensaje.setFecharealiz(cursor.getString(4)); 
			mensaje.setHorarealiz(cursor.getString(5)); 
			mensaje.setZonainf(cursor.getString(6));
			mensaje.setEid(cursor.getInt(7));
			mensaje.setTipomsj(cursor.getString(8));
			mensaje.setDesdefecha(cursor.getString(9));
			mensaje.setHastafecha(cursor.getString(10)); 
			mensaje.setFecharec(cursor.getString(11));
			mensaje.setHorarec(cursor.getString(12)); 
			mensaje.setEstado(cursor.getString(13));
			mensaje.setRmte(cursor.getString(15));
			mensaje.setMidbd(cursor.getInt(16));
			mensaje.setXml(cursor.getString(17));
			listaMensajesRec.add(mensaje); } while (cursor.moveToNext()); }
		cursor.close(); db.close(); return listaMensajesRec;
	}

	public Msj mensajePendiente(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_MENSAJESENV, new String[] { KEY_MID, KEY_CLIENTEGLOBAL,
				KEY_MENSAJE, KEY_HTTP, KEY_FECHAREALIZ, KEY_HORAREALIZ, KEY_ZONAINF, KEY_EID, 
				KEY_TIPOMSJ, KEY_DESDEFECHA, KEY_HASTAFECHA, KEY_FECHAREC, KEY_HORAREC, 
				KEY_ESTADO, KEY_RMTE, KEY_MIDBD, KEY_XML}, KEY_ESTADO + "='P'", new String[] {}, null, null, null, null);
		if (cursor.getCount()>0) cursor.moveToFirst();
		else { cursor.close(); db.close(); return null; }
		Msj pendiente= new Msj(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), 
				cursor.getString(3), cursor.getString(4), cursor.getString(5), 
				cursor.getString(6), cursor.getInt(7), cursor.getString(8),
				cursor.getString(9), cursor.getString(10), cursor.getString(11),
				cursor.getString(12), cursor.getString(13), cursor.getString(14),
				cursor.getInt(15), cursor.getString(16));
		cursor.close(); db.close(); return pendiente;
	}
	
	//Coger último MID de Recibidos
	public int getLastMidRec(){
		int mid = 0; String selectQuery = "SELECT * FROM " + TABLE_MENSAJESREC + 
				" ORDER BY " + KEY_MID + " DESC LIMIT 1;";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) mid = cursor.getInt(0);
		cursor.close(); db.close(); return mid;
	}
	
	//Coger último MID de Enviados
	public int getLastMidEnv(){
		int mid = 0; String selectQuery = "SELECT * FROM " + TABLE_MENSAJESENV + 
				" ORDER BY " + KEY_MID + " DESC LIMIT 1;";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) mid = cursor.getInt(0);
		cursor.close(); db.close(); return mid;
	}
	
	// Borrar un mensaje enviado/pendiente
	public void deleteMensajeEnviado(Msj mensaje) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MENSAJESENV, KEY_MID + " = ?", new String[] 
				{ String.valueOf(mensaje.getMid()) });
		db.close();
		}
		
	// Borrar un mensaje recibido
	public void deleteMensajeRecibido(Msj mensaje) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MENSAJESREC, KEY_MID + " = ?", new String[] 
				{ String.valueOf(mensaje.getMid()) });
		db.close();
		}
		
	// Actualizar mensaje enviado
	public int actualizarMensajeEnviado(Msj mensaje) {
		int ret; SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_MID, mensaje.getMid());
		values.put(KEY_CLIENTEGLOBAL, mensaje.getClienteglobal());
		values.put(KEY_MENSAJE, mensaje.getMensaje());
		values.put(KEY_HTTP, mensaje.getHttp());
		values.put(KEY_FECHAREALIZ, mensaje.getFecharealiz());
		values.put(KEY_HORAREALIZ, mensaje.getHorarealiz());
		values.put(KEY_EID, mensaje.getEid());
		values.put(KEY_TIPOMSJ, mensaje.getTipomsj());
		values.put(KEY_DESDEFECHA, mensaje.getDesdefecha());
		values.put(KEY_HASTAFECHA, mensaje.getHastafecha());
		values.put(KEY_FECHAREC, mensaje.getFecharec());
		values.put(KEY_HORAREC, mensaje.getHorarec());
		values.put(KEY_ESTADO, mensaje.getEstado());
		values.put(KEY_RMTE, mensaje.getRmte());
		values.put(KEY_MIDBD, mensaje.getMidbd());
		//values.put(KEY_IDMSJAPPMOVIL, mensaje.getIdmsjappmovil()); es autoincrement
		ret = db.update(TABLE_MENSAJESENV, values, KEY_MID + " = ?", new String[] 
				{ String.valueOf(mensaje.getMid()) });
		db.close(); return ret;
	}
	
	// Actualizar mensaje enviado
	public int actualizarMensajeRecibido(Msj mensaje) {
		int ret; SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_MID, mensaje.getMid());
		values.put(KEY_CLIENTEGLOBAL, mensaje.getClienteglobal());
		values.put(KEY_MENSAJE, mensaje.getMensaje());
		values.put(KEY_HTTP, mensaje.getHttp());
		values.put(KEY_FECHAREALIZ, mensaje.getFecharealiz());
		values.put(KEY_HORAREALIZ, mensaje.getHorarealiz());
		values.put(KEY_EID, mensaje.getEid());
		values.put(KEY_TIPOMSJ, mensaje.getTipomsj());
		values.put(KEY_DESDEFECHA, mensaje.getDesdefecha());
		values.put(KEY_HASTAFECHA, mensaje.getHastafecha());
		values.put(KEY_FECHAREC, mensaje.getFecharec());
		values.put(KEY_HORAREC, mensaje.getHorarec());
		values.put(KEY_ESTADO, mensaje.getEstado());
		values.put(KEY_RMTE, mensaje.getRmte());
		values.put(KEY_MIDBD, mensaje.getMidbd());
		//values.put(KEY_IDMSJAPPMOVIL, mensaje.getIdmsjappmovil()); es autoincrement
		ret = db.update(TABLE_MENSAJESREC, values, KEY_MID + " = ?", new String[] { String.valueOf(mensaje.getMid()) });
		db.close(); return ret;
	}
	
	// Recoger un mensaje enviado concreto
	public Msj getMensajeEnv(int mid) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_MENSAJESENV, new String[] { KEY_MID, KEY_CLIENTEGLOBAL,
				KEY_MENSAJE, KEY_HTTP, KEY_FECHAREALIZ, KEY_HORAREALIZ, KEY_ZONAINF, KEY_EID, 
				KEY_TIPOMSJ, KEY_DESDEFECHA, KEY_HASTAFECHA, KEY_FECHAREC, KEY_HORAREC, KEY_ESTADO,
				KEY_RMTE, KEY_MIDBD, KEY_XML},KEY_MID + "=?",new String[] { String.valueOf(mid) }, null, null, null, null);
		if (cursor.getCount()>0) cursor.moveToFirst();
		else { cursor.close(); db.close(); return null; }
		Msj mensaje = new Msj(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), 
				cursor.getString(3), cursor.getString(4), cursor.getString(5), 
				cursor.getString(6), cursor.getInt(7), cursor.getString(8),
				cursor.getString(9), cursor.getString(10), cursor.getString(11),
				cursor.getString(12), cursor.getString(13), cursor.getString(14),
				cursor.getInt(15), cursor.getString(16));
		cursor.close(); db.close(); return mensaje;
	}
	
	// Ver mensaje Recibido por mid
	public Msj getMensajeRec(int mid) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_MENSAJESREC, new String[] { KEY_MID, KEY_CLIENTEGLOBAL,
				KEY_MENSAJE, KEY_HTTP, KEY_FECHAREALIZ, KEY_HORAREALIZ, KEY_ZONAINF, KEY_EID, 
				KEY_TIPOMSJ, KEY_DESDEFECHA, KEY_HASTAFECHA, KEY_FECHAREC, KEY_HORAREC, KEY_ESTADO,
				KEY_RMTE, KEY_MIDBD, KEY_XML},KEY_MID + "=?", new String[] { String.valueOf(mid) }, null, null, null, null);
		if (cursor.getCount()>0) cursor.moveToFirst();
		else { cursor.close(); db.close(); return null; }
		Msj mensaje = new Msj(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), 
				cursor.getString(3), cursor.getString(4), cursor.getString(5), 
				cursor.getString(6), cursor.getInt(7), cursor.getString(8),
				cursor.getString(9), cursor.getString(10), cursor.getString(11),
				cursor.getString(12), cursor.getString(13), cursor.getString(14),
				cursor.getInt(15), cursor.getString(16));
		cursor.close(); db.close(); return mensaje;
	}
	
	//Función recoger conversaciones
	public List<Msj> getConversaciones() {
		List<Msj> conversaciones = new ArrayList<Msj>();
		String selectQuery = "SELECT * FROM "+TABLE_MENSAJESREC+
				" UNION SELECT * FROM "+TABLE_MENSAJESENV+
				" ORDER BY "+KEY_FECHAREALIZ+" DESC, "+KEY_HORAREALIZ+" DESC;";
		//Test "SELECT * FROM mensajesrec GROUP BY eid UNION SELECT * FROM mensajesenv
		//GROUP BY eid ORDER BY fecharealiz DESC, horarealiz DESC;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do { Msj mensaje = new Msj();
			mensaje.setMid(cursor.getInt(0)); 
			mensaje.setClienteglobal(cursor.getInt(1));
			mensaje.setMensaje(cursor.getString(2)); 
			mensaje.setHttp(cursor.getString(3));
			mensaje.setFecharealiz(cursor.getString(4)); 
			mensaje.setHorarealiz(cursor.getString(5)); 
			mensaje.setZonainf(cursor.getString(6));
			mensaje.setEid(cursor.getInt(7));
			mensaje.setTipomsj(cursor.getString(8));
			mensaje.setDesdefecha(cursor.getString(9));
			mensaje.setHastafecha(cursor.getString(10)); 
			mensaje.setFecharec(cursor.getString(11));
			mensaje.setHorarec(cursor.getString(12)); 
			mensaje.setEstado(cursor.getString(13));
			mensaje.setRmte(cursor.getString(15));
			mensaje.setMidbd(cursor.getInt(16));
			mensaje.setXml(cursor.getString(17));
			conversaciones.add(mensaje); } while (cursor.moveToNext()); }
		cursor.close(); db.close(); return conversaciones;
	}
	
	//Función recoger conversaciones con nombre "busca"
		public List<Msj> searchConversaciones(String busca) {
			List<Msj> conversaciones = new ArrayList<Msj>();
			String selectQuery = "SELECT * FROM "+TABLE_MENSAJESREC+
					" WHERE (SELECT "+KEY_NOMBRE+" FROM "+TABLE_ESTABLECIMIENTO+
					" WHERE "+KEY_EID+"="+TABLE_MENSAJESREC+"."+KEY_EID+") LIKE '%"+
					busca+"%' UNION SELECT * FROM "+TABLE_MENSAJESENV+
					" WHERE (SELECT "+KEY_NOMBRE+" FROM "+TABLE_ESTABLECIMIENTO+
					" WHERE "+KEY_EID+"="+TABLE_MENSAJESENV+"."+KEY_EID+") LIKE '%"+
					busca+"%' ORDER BY "+KEY_FECHAREALIZ+" DESC, "+KEY_HORAREALIZ+" DESC;";
			//Test "SELECT * FROM mensajesrec GROUP BY eid UNION SELECT * FROM mensajesenv
			//GROUP BY eid ORDER BY fecharealiz DESC, horarealiz DESC;
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) { do { Msj mensaje = new Msj();
				mensaje.setMid(cursor.getInt(0)); 
				mensaje.setClienteglobal(cursor.getInt(1));
				mensaje.setMensaje(cursor.getString(2)); 
				mensaje.setHttp(cursor.getString(3));
				mensaje.setFecharealiz(cursor.getString(4)); 
				mensaje.setHorarealiz(cursor.getString(5)); 
				mensaje.setZonainf(cursor.getString(6));
				mensaje.setEid(cursor.getInt(7));
				mensaje.setTipomsj(cursor.getString(8));
				mensaje.setDesdefecha(cursor.getString(9));
				mensaje.setHastafecha(cursor.getString(10)); 
				mensaje.setFecharec(cursor.getString(11));
				mensaje.setHorarec(cursor.getString(12)); 
				mensaje.setEstado(cursor.getString(13));
				mensaje.setRmte(cursor.getString(15));
				mensaje.setMidbd(cursor.getInt(16));
				mensaje.setXml(cursor.getString(17));
				conversaciones.add(mensaje); } while (cursor.moveToNext()); }
			cursor.close(); db.close(); return conversaciones;
		}
	
	//Función recoger conversación
	public List<Msj> getConversacion(int eid, int off) {
		List<Msj> conversaciones = new ArrayList<Msj>();
		String selectQuery = "SELECT * FROM "+TABLE_MENSAJESREC+
				" WHERE "+KEY_EID+"="+eid+
				" UNION SELECT * FROM "+TABLE_MENSAJESENV+
				" WHERE "+KEY_EID+"="+eid+
				" ORDER BY "+KEY_FECHAREALIZ+" DESC, "+KEY_HORAREALIZ+" DESC"+
				" LIMIT "+(off+1)*20+" ;";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do { Msj mensaje = new Msj();
			mensaje.setMid(cursor.getInt(0)); 
			mensaje.setClienteglobal(cursor.getInt(1));
			mensaje.setMensaje(cursor.getString(2)); 
			mensaje.setHttp(cursor.getString(3));
			mensaje.setFecharealiz(cursor.getString(4)); 
			mensaje.setHorarealiz(cursor.getString(5)); 
			mensaje.setZonainf(cursor.getString(6));
			mensaje.setEid(cursor.getInt(7));
			mensaje.setTipomsj(cursor.getString(8));
			mensaje.setDesdefecha(cursor.getString(9));
			mensaje.setHastafecha(cursor.getString(10)); 
			mensaje.setFecharec(cursor.getString(11));
			mensaje.setHorarec(cursor.getString(12)); 
			mensaje.setEstado(cursor.getString(13));
			mensaje.setRmte(cursor.getString(15));
			mensaje.setMidbd(cursor.getInt(16));
			mensaje.setXml(cursor.getString(17));
			conversaciones.add(mensaje); } while (cursor.moveToNext()); }
		cursor.close(); db.close(); return conversaciones;
	}
	//Coger establecimientos no repetidos en orden DESC de fecharealiz, horarealiz
	
	public int getConversacionCount(int eid) {
		String countQuery = "SELECT * FROM "+TABLE_MENSAJESREC+
				" WHERE "+KEY_EID+"="+eid+
				" UNION SELECT * FROM "+TABLE_MENSAJESENV+
				" WHERE "+KEY_EID+"="+eid;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount(); cursor.close(); db.close(); return count;
	}
	
	////////////////////////
	//XXX Funciones de CNAE 
	////////////////////////
	//
	// Inicializar () [inicializarCNAE]
	// Reinicializar () [reinicializarCNAE]
	// Insertar (int,String,int,boolean) [insertCNAE]
	// Actualizar (int,String,int,boolean) [updateCNAE]
	// Actualizar Checkboxes (int,boolean) [updateCbCNAE]
	// Volcar () [volcarCNAE]
	// Volcar en Cadena (int) [volcarCNAEtoString]
	
	//Inicializar CNAE (local)
	public void inicializarCNAE(){
		SQLiteDatabase predb = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_CNAE;
		Cursor cursor = predb.rawQuery(selectQuery, null);
		if(cursor.getCount()>0) { predb.close(); return; } //No descarga si ya existen
		SQLiteDatabase db = this.getWritableDatabase();
		db.close();
		db = this.getWritableDatabase();
		try{Class.forName("org.postgresql.Driver");}
	  catch(ClassNotFoundException e){e.printStackTrace();}
	  try{DriverManager.setLoginTimeout(20);
	   	Connection conn = DriverManager.getConnection(
	   			context.getResources().getString(R.string.dirbbdd)); //PostgreSQL
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM categoria WHERE tipo='2'");
			while(rs.next()){
				ContentValues values = new ContentValues();
				values.put(KEY_CNAEID, rs.getInt(1));
				values.put(KEY_CNAENOMBRE, rs.getString(2));
				values.put(KEY_CNAETIPO, rs.getString(3));
				values.put(KEY_CNAEACEPTA, true);
				db.insert(TABLE_CNAE, null, values); }
			db.close();} catch (SQLException e){e.printStackTrace();}
	}
	
	//Reinicializar CNAE (valores de DB)
		public void reinicializarCNAE(String cadena){
			String[] vuelque = volcarCNAE();
			SQLiteDatabase db = this.getWritableDatabase();
			if(sharedPrefs.getString("tipo","P").equals("E")){ //Poner los 1 a 0 si es Empresa
				for(int i=0;i<vuelque.length;i++){
					ContentValues values = new ContentValues();
					values.put(KEY_CNAEACEPTA, "0");
					db.update(TABLE_CNAE, values, KEY_CNAEID + " = ?", 
						new String[]{ vuelque[i].split(";")[0] }); }}
			if(cadena!=null && !cadena.equals("")){ 
				String[] CNAEs=cadena.split(",");
				for(int j=0;j<CNAEs.length;j++){
				for(int i=0;i<vuelque.length;i++){
				  if(vuelque[i].split(";")[0].equals(CNAEs[j])){
					ContentValues values = new ContentValues();
					if(sharedPrefs.getString("tipo","P").equals("P"))
						values.put(KEY_CNAEACEPTA, "0");
					else values.put(KEY_CNAEACEPTA, "1");
					db.update(TABLE_CNAE, values, KEY_CNAEID + " = ?", 
						new String[]{ vuelque[i].split(";")[0] });
					break;
				}else if(i==vuelque.length){
					ContentValues values = new ContentValues();
					if(sharedPrefs.getString("tipo","P").equals("P"))
						values.put(KEY_CNAEACEPTA, "1");
					else values.put(KEY_CNAEACEPTA, "0");
					db.update(TABLE_CNAE, values, KEY_CNAEID + " = ?", 
						new String[]{ CNAEs[j] });}
				}}db.close();
			}
		}
	
	//Insertar CNAE
	public void insertCNAE(int CNAEid, String CNAEnombre, int CNAEtipo, boolean CNAEacepta) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CNAEID, CNAEid);
		values.put(KEY_CNAENOMBRE, CNAEnombre);
		values.put(KEY_CNAETIPO, CNAEtipo);
		values.put(KEY_CNAEACEPTA, CNAEacepta);
		db.insert(TABLE_CNAE, null, values); db.close();
	}
	
	//Actualizar CNAE
	public void updateCNAE(int CNAEid, String CNAEnombre, int CNAEtipo, boolean CNAEacepta) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CNAEID, CNAEid);
		values.put(KEY_CNAENOMBRE, CNAEnombre);
		values.put(KEY_CNAETIPO, CNAEtipo);
		values.put(KEY_CNAEACEPTA, CNAEacepta);
		db.update(TABLE_CNAE, values, KEY_CNAEID + "=?", new String[]{ String.valueOf(CNAEid) });
		db.close();
	}
	
	//Actualizar CNAE Checkboxes
	public void updateCbCNAE(int CNAEid, boolean CNAEacepta) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CNAEACEPTA, CNAEacepta);
		db.update(TABLE_CNAE, values, KEY_CNAEID + "=?", new String[]{ String.valueOf(CNAEid) });
		db.close();
	}
	
	// Volcar CNAE
	public String[] volcarCNAE() {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_CNAE + " ORDER BY " + KEY_CNAEID + " ASC";
		Cursor cursor = db.rawQuery(selectQuery, null);
		int i=0; String[] CNAEs = new String[cursor.getCount()];
		if (cursor.moveToFirst()) { do {
				CNAEs[i] = String.valueOf(cursor.getInt(0))+";"+cursor.getString(1)+";"+
					String.valueOf(cursor.getInt(2))+";"+String.valueOf(cursor.getInt(3)); i++;
			} while (cursor.moveToNext()); }
		cursor.close(); db.close(); return CNAEs;
	}
	
	//Volcar CNAE a cadena
	public String volcarCNAEtoString() {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery, CNAEs = "";
		if(sharedPrefs.getString("tipo","P").equals("P")) //Particular 
			selectQuery = "SELECT " + KEY_CNAEID + " FROM " + TABLE_CNAE 
			+ " WHERE " + KEY_CNAEACEPTA + "=0 ORDER BY " + KEY_CNAEID + " ASC";
		else //Empresa
			selectQuery = "SELECT " + KEY_CNAEID + " FROM " + TABLE_CNAE 
			+ " WHERE " + KEY_CNAEACEPTA + "=1 ORDER BY " + KEY_CNAEID + " ASC";
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { do {
				CNAEs += String.valueOf(cursor.getInt(0))+",";
			} while (cursor.moveToNext()); }
		cursor.close(); db.close(); return CNAEs;
	}
	
	////////////////////////
	//XXX Funciones de Paises 
	////////////////////////
	//
	// Inicializar Paises () [inicializarPaises]
	// Pedir nombres (String[]) [pedirNombresPaises]
	// Pedir códigos (String[]) [pedirCodigosPaises]
	// Buscar por código (String) [buscarCodigoPais]
	// Buscar por nombre (String) [buscarNombrePais]
	
	//Inicializar Paises (local)
	public void inicializarPaises(){
		SQLiteDatabase predb = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_PAISES;
		Cursor cursor = predb.rawQuery(selectQuery, null);
		if(cursor.getCount()>0) { predb.close(); return; } //No descarga si ya existen
		SQLiteDatabase db = this.getWritableDatabase();
		if(!db.isOpen())
			db = this.getWritableDatabase();
		try{Class.forName("org.postgresql.Driver");}
    catch(ClassNotFoundException e){e.printStackTrace();}
    try{DriverManager.setLoginTimeout(20);
     	Connection conn = DriverManager.getConnection(
     			context.getResources().getString(R.string.dirbbdd));
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT idpais, nombre, codigo FROM paises");
			while(rs.next()){
				ContentValues values = new ContentValues();
				values.put(KEY_PAID, rs.getInt(1));
				values.put(KEY_PNOMBRE, rs.getString(2));
				values.put(KEY_PCODIGO, rs.getString(3));
				db.insert(TABLE_PAISES, null, values); }
			db.close();} catch (SQLException e){e.printStackTrace();}
	}
	
	//Pedir Nombres de Paises
	public String[] pedirNombresPaises(){
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT " + KEY_PNOMBRE + " FROM " + TABLE_PAISES 
				+ " ORDER BY " + KEY_PNOMBRE + " ASC";
		Cursor cursor = db.rawQuery(selectQuery, null);
		int i=0; String[] nombres = new String[cursor.getCount()];
		if (cursor.moveToFirst()) { do {
			nombres[i] = cursor.getString(0); i++; } while (cursor.moveToNext()); }
		cursor.close(); db.close(); return nombres;
	}
	
	//Pedir Códigos de Paises
	public String[] pedirCodigosPaises(){
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT " + KEY_PCODIGO + " FROM " + TABLE_PAISES;
		Cursor cursor = db.rawQuery(selectQuery, null);
		int i=0; String[] codigos = new String[cursor.getCount()];
		if (cursor.moveToFirst()) { do {
			codigos[i] = cursor.getString(0); i++; } while (cursor.moveToNext()); }
		cursor.close(); db.close(); return codigos;
	}
	
	//Buscar Códigos de País por Nombre
	public String buscarCodigoPais(String nombre){
		SQLiteDatabase db = this.getReadableDatabase();
		String codigo="", selectQuery = "SELECT " + KEY_PCODIGO + " FROM " + TABLE_PAISES +
				" WHERE " + KEY_PNOMBRE + "='" + nombre + "'";
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) 
			if(cursor.getString(0)!=null)
			codigo = cursor.getString(0);
		cursor.close(); db.close(); return codigo;
	}
	
	//Buscar Nombre de País por Código
	public String buscarNombrePais(String codigo){
		SQLiteDatabase db = this.getReadableDatabase();
		String nombre="", selectQuery = "SELECT " + KEY_PNOMBRE + " FROM " + TABLE_PAISES +
				" WHERE " + KEY_PCODIGO + "='" + codigo + "'";
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) 
			if(cursor.getString(0)!=null)
			nombre = cursor.getString(0);
		cursor.close(); db.close(); return nombre;
	}
	
	///////////////////////////
	//XXX Funciones de Familias 
	///////////////////////////
	
	// Añadir una familia
	public void addFamilia(String fid, String nombre, String fecha, int eid) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_FID, fid);
		values.put(KEY_NOMBRE, nombre);
		values.put(KEY_FECHA, fecha);
		values.put(KEY_EID, eid);
		db.insert(TABLE_FAMILIAS, null, values); db.close();
	}
	
	//Busca una familia
	public Fam buscaFamilia(String fid, int eid){
		SQLiteDatabase db = this.getReadableDatabase();
		Fam f = null;
		String selectQuery = "SELECT * FROM " + TABLE_FAMILIAS +
				" WHERE " + KEY_FID + "='" + fid + "' AND " +
				KEY_EID + "=" + eid;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()){
				f=new Fam();
				f.setFid(cursor.getString(0));
				f.setNombre(cursor.getString(1));
				f.setFecha(cursor.getString(2));
				f.setEid(cursor.getInt(3)); }
		cursor.close(); db.close(); return f;
	}
	
	//Recuperar todas las familias
	public List<Fam> getAllFamilias(int eid){
		SQLiteDatabase db = this.getReadableDatabase();
		List<Fam> familias = new ArrayList<Fam>();
		String selectQuery = "SELECT * FROM " + TABLE_FAMILIAS +
				" WHERE " + KEY_EID + "=" + eid;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do { Fam f = new Fam();
			f.setFid(cursor.getString(0));
			f.setNombre(cursor.getString(1));
			f.setFecha(cursor.getString(2));
			f.setEid(cursor.getInt(3));
			familias.add(f);
			} while (cursor.moveToNext());
		} cursor.close(); db.close(); return familias;
	}
		
		//Actualizar Familia
		public void updateFamilia(String fid, String nombre, String fecha, int eid) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(KEY_FID, fid);
			values.put(KEY_NOMBRE, nombre);
			values.put(KEY_FECHA, fecha);
			values.put(KEY_EID, eid);
			db.update(TABLE_FAMILIAS, values, KEY_FID + "=? AND " + KEY_EID + "=?", 
					new String[]{ fid, String.valueOf(eid) });
			db.close();
	}
	
	//////////////////////////////
	//XXX Funciones de Subfamilias 
	//////////////////////////////
	// Añadir una subfamilia
	public void addSubfamilia(String sfid, String fid, String nombre, String fecha, int eid) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_SFID, sfid);
		values.put(KEY_FID, fid);
		values.put(KEY_NOMBRE, nombre);
		values.put(KEY_FECHA, fecha);
		values.put(KEY_EID, eid);
		db.insert(TABLE_SUBFAMILIAS, null, values); db.close();
	}

	// Busca una subfamilia
	public Fam buscaSubfamilia(String sfid, int eid){
		SQLiteDatabase db = this.getReadableDatabase();
		Fam f = null;
		String selectQuery = "SELECT * FROM " + TABLE_SUBFAMILIAS +
				" WHERE " + KEY_SFID + "='" + sfid + "' AND " +
				KEY_EID + "=" + eid;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { f=new Fam();
			f.setSfid(cursor.getString(0));
			f.setFid(cursor.getString(1));
			f.setNombre(cursor.getString(2));
			f.setFecha(cursor.getString(3));
			f.setEid(cursor.getInt(4)); }
		cursor.close(); db.close(); return f;
	}

	//Recuperar todas las subfamilias
	public List<Fam> getAllSubfamilias(int eid){
		SQLiteDatabase db = this.getReadableDatabase();
		List<Fam> subfamilias = new ArrayList<Fam>();
		String selectQuery = "SELECT * FROM " + TABLE_SUBFAMILIAS +
				" WHERE " + KEY_EID + "=" + eid;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do { Fam f = new Fam();
			f.setFid(cursor.getString(0));
			f.setSfid(cursor.getString(1));
			f.setNombre(cursor.getString(2));
			f.setFecha(cursor.getString(3));
			f.setEid(cursor.getInt(4));
			subfamilias.add(f);
			} while (cursor.moveToNext());
		} cursor.close(); db.close(); return subfamilias;
	}

	//Actualizar Subfamilia
	public void updateSubfamilia(String sfid, String fid, String nombre, String fecha, int eid) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_SFID, sfid);
		values.put(KEY_FID, fid);
		values.put(KEY_NOMBRE, nombre);
		values.put(KEY_FECHA, fecha);
		values.put(KEY_EID, eid);
		db.update(TABLE_SUBFAMILIAS, values, KEY_SFID + "=? AND " + KEY_EID + "=?", 
				new String[]{ sfid, String.valueOf(eid) });
		db.close();
	}
	
	//////////////////////////////
	//XXX Funciones de ClientesF
	//////////////////////////////
	// Añadir un clienteF
	public void addClienteF(CliF cliente) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_IDCF, cliente.getIdcf());
		values.put(KEY_NOMBRE, cliente.getNombre());
		values.put(KEY_MAIL, cliente.getMail());
		values.put(KEY_IDEST, cliente.getIdest());
		values.put(KEY_PAIS, cliente.getPais());
		values.put(KEY_PROVINCIA, cliente.getProvincia());
		values.put(KEY_MUNICIPIO, cliente.getMunicipio());
		values.put(KEY_DIRECCION, cliente.getDireccion());
		values.put(KEY_TEL, cliente.getTel());
		values.put(KEY_MOVIL, cliente.getMovil());
		values.put(KEY_FECHANAC, cliente.getFechanac());
		values.put(KEY_NIF, cliente.getNif());
		values.put(KEY_REF, cliente.getRef());
		values.put(KEY_IDDIS, cliente.getIddis());
		values.put(KEY_IDDIS_VEND, cliente.getIddisVend());
		values.put(KEY_SEXO, cliente.getSexo());
		values.put(KEY_TIPOCLIENTE, cliente.getTipocliente());
		db.insert(TABLE_CLIENTESF, null, values); db.close();
	}
	
	// Busca un clientef
	public CliF buscaClienteF(int idcf){
		SQLiteDatabase db = this.getReadableDatabase();
		CliF c = null;
		String selectQuery = "SELECT * FROM " + TABLE_CLIENTESF +
				" WHERE " + KEY_IDCF + "=" + idcf;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) { c=new CliF();
			c.setIdcf(cursor.getInt(0));
			c.setNombre(cursor.getString(1));
			c.setMail(cursor.getString(2));
			c.setIdest(cursor.getInt(3));
			c.setPais(cursor.getString(4));
			c.setProvincia(cursor.getString(5));
			c.setMunicipio(cursor.getString(6));
			c.setDireccion(cursor.getString(7));
			c.setTel(cursor.getString(8));
			c.setMovil(cursor.getString(9));
			c.setFechanac(cursor.getString(10));
			c.setNif(cursor.getString(11));
			c.setRef(cursor.getString(12));
			c.setIddis(cursor.getInt(13));
			c.setIddisVend(cursor.getInt(14));
			c.setSexo(cursor.getString(15));
			c.setTipocliente(cursor.getString(16));}
		cursor.close(); db.close(); return c;
	}
		
	// Actualizar mensaje enviado
	public int updateClienteF(CliF cliente) {
		int ret; SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_IDCF, cliente.getIdcf());
		values.put(KEY_NOMBRE, cliente.getNombre());
		values.put(KEY_MAIL, cliente.getMail());
		values.put(KEY_IDEST, cliente.getIdest());
		values.put(KEY_PAIS, cliente.getPais());
		values.put(KEY_PROVINCIA, cliente.getProvincia());
		values.put(KEY_MUNICIPIO, cliente.getMunicipio());
		values.put(KEY_DIRECCION, cliente.getDireccion());
		values.put(KEY_TEL, cliente.getTel());
		values.put(KEY_MOVIL, cliente.getMovil());
		values.put(KEY_FECHANAC, cliente.getFechanac());
		values.put(KEY_NIF, cliente.getNif());
		values.put(KEY_REF, cliente.getRef());
		values.put(KEY_IDDIS, cliente.getIddis());
		values.put(KEY_IDDIS_VEND, cliente.getIddisVend());
		values.put(KEY_SEXO, cliente.getSexo());
		values.put(KEY_TIPOCLIENTE, cliente.getTipocliente());
		ret = db.update(TABLE_CLIENTESF, values, KEY_IDCF + " = ?", new String[] 
				{ String.valueOf(cliente.getIdcf()) });
		db.close(); return ret;
	}
	
	//Recuperar todos los clientesF
	public List<CliF> getAllClientesF(int eid){
		SQLiteDatabase db = this.getReadableDatabase();
		List<CliF> clientes = new ArrayList<CliF>();
		String selectQuery = "SELECT * FROM " + TABLE_CLIENTESF +
				" WHERE " + KEY_IDEST + "=" + eid;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do { CliF c = new CliF();
			c.setIdcf(cursor.getInt(0));
			c.setNombre(cursor.getString(1));
			c.setMail(cursor.getString(2));
			c.setIdest(cursor.getInt(3));
			c.setPais(cursor.getString(4));
			c.setProvincia(cursor.getString(5));
			c.setMunicipio(cursor.getString(6));
			c.setDireccion(cursor.getString(7));
			c.setTel(cursor.getString(8));
			c.setMovil(cursor.getString(9));
			c.setFechanac(cursor.getString(10));
			c.setNif(cursor.getString(11));
			c.setRef(cursor.getString(12));
			c.setIddis(cursor.getInt(13));
			c.setIddisVend(cursor.getInt(14));
			c.setSexo(cursor.getString(15));
			c.setTipocliente(cursor.getString(16));
			clientes.add(c);
			} while (cursor.moveToNext());
		} cursor.close(); db.close(); return clientes;
	}
	
	public List<CliF> getAllClientesFAV(int eid){
		SQLiteDatabase db = this.getReadableDatabase();
		List<CliF> clientes = new ArrayList<CliF>();
		String selectQuery = "SELECT * FROM " + TABLE_CLIENTESF +
				" WHERE " + KEY_IDEST + "=" + eid + 
				" AND (" + KEY_IDDIS_VEND + "="+sharedPrefs.getInt("solicitaclin", 0) +
				" OR " + KEY_IDDIS_VEND + "=0)";
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do { CliF c = new CliF();
			c.setIdcf(cursor.getInt(0));
			c.setNombre(cursor.getString(1));
			c.setMail(cursor.getString(2));
			c.setIdest(cursor.getInt(3));
			c.setPais(cursor.getString(4));
			c.setProvincia(cursor.getString(5));
			c.setMunicipio(cursor.getString(6));
			c.setDireccion(cursor.getString(7));
			c.setTel(cursor.getString(8));
			c.setMovil(cursor.getString(9));
			c.setFechanac(cursor.getString(10));
			c.setNif(cursor.getString(11));
			c.setRef(cursor.getString(12));
			c.setIddis(cursor.getInt(13));
			c.setIddisVend(cursor.getInt(14));
			c.setSexo(cursor.getString(15));
			c.setTipocliente(cursor.getString(16));
			clientes.add(c);
			} while (cursor.moveToNext());
		} cursor.close(); db.close(); return clientes;
	}
	
	// Borrar un ClienteF
	public void deleteClienteF(int idcf) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CLIENTESF, KEY_IDCF + " = ?", new String[] 
				{ String.valueOf(idcf) });
		db.close();
	}
	
	//////////////////////////////
	//XXX Funciones de Actividades
	//////////////////////////////
	//Inicializar Actividades (local)
		public void inicializarActividades(){
			SQLiteDatabase predb = this.getReadableDatabase();
			String selectQuery = "SELECT * FROM " + TABLE_ACTIVIDADES;
			Cursor cursor = predb.rawQuery(selectQuery, null);
			if(cursor.getCount()>0) { predb.close(); return; } //No descarga si ya existen
			SQLiteDatabase db = this.getWritableDatabase();
			if(!db.isOpen())
				db = this.getWritableDatabase();
			try{Class.forName("org.postgresql.Driver");}
	    catch(ClassNotFoundException e){e.printStackTrace();}
	    try{DriverManager.setLoginTimeout(20);
	     	Connection conn = DriverManager.getConnection(
	     			context.getResources().getString(R.string.dirbbdd));
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT idactividad, actividad, idestablecimientofk, " +
						"tipo, prioridad FROM actividades");
				while(rs.next()){
					ContentValues values = new ContentValues();
					values.put(KEY_IDACT, rs.getInt(1));
					values.put(KEY_ACTIVIDAD, rs.getString(2));
					values.put(KEY_EID, rs.getInt(3));
					values.put(KEY_TIPO, rs.getInt(4));
					values.put(KEY_PRIORIDAD, rs.getInt(5));
					db.insert(TABLE_ACTIVIDADES, null, values); }
				db.close();} catch (SQLException e){e.printStackTrace();}
		}
	
	// Añadir una actividad
	public void addActividad(int idact, String actividad, int eid, int tipo, int prioridad) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_IDACT, idact);
		values.put(KEY_ACTIVIDAD, actividad);
		values.put(KEY_EID, eid);
		values.put(KEY_TIPO, tipo);
		values.put(KEY_PRIORIDAD, prioridad);
		db.insert(TABLE_ACTIVIDADES, null, values); db.close();
	}
	
	//////////////////////////////
	//XXX Funciones de Actividadestipo
	//////////////////////////////
	//Inicializar Actividades (local)
			public void inicializarActividadesTipo(){
				SQLiteDatabase predb = this.getReadableDatabase();
				String selectQuery = "SELECT * FROM " + TABLE_ACTIVIDADESTIPO;
				Cursor cursor = predb.rawQuery(selectQuery, null);
				if(cursor.getCount()>0) { predb.close(); return; } //No descarga si ya existen
				SQLiteDatabase db = this.getWritableDatabase();
				if(!db.isOpen())
					db = this.getWritableDatabase();
				try{Class.forName("org.postgresql.Driver");}
		    catch(ClassNotFoundException e){e.printStackTrace();}
		    try{DriverManager.setLoginTimeout(20);
		     	Connection conn = DriverManager.getConnection(
		     			context.getResources().getString(R.string.dirbbdd));
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery("SELECT idactividad, actividad, " +
							"prioridad FROM actividades_tipo");
					while(rs.next()){
						ContentValues values = new ContentValues();
						values.put(KEY_IDACT, rs.getInt(1));
						values.put(KEY_ACTIVIDAD, rs.getString(2));
						values.put(KEY_PRIORIDAD, rs.getInt(3));
						db.insert(TABLE_ACTIVIDADESTIPO, null, values); }
					db.close();} catch (SQLException e){e.printStackTrace();}
			}
			
	// Añadir una actividad_tipo
	public void addActividadtipo(int idact, String actividad, int prioridad) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_IDACT, idact);
		values.put(KEY_ACTIVIDAD, actividad);
		values.put(KEY_PRIORIDAD, prioridad);
		db.insert(TABLE_ACTIVIDADESTIPO, null, values); db.close();
	}
		
	// Separador final. XXX
	
	//Comprobador tabla existe
	public boolean tablaExiste(String tableName, SQLiteDatabase db) {
		if(db.isOpen()) {
			if(db == null || !db.isOpen())
				db = getReadableDatabase(); 
		  if(!db.isReadOnly()) {
		    db.close(); db = getReadableDatabase(); }}
		Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where" +
				" tbl_name = '"+tableName+"'", null);
		if(cursor!=null) {
		  if(cursor.getCount()>0) { cursor.close(); db.close(); return true; }
		    cursor.close(); db.close(); } return false; }
	
	private class ActualizaCNAE extends AsyncTask<String, Void, Boolean> {
		ProgressDialog loading;
		protected void onPreExecute() {
     	loading = new ProgressDialog(context);
     	loading.setMessage(context.getString(R.string.acbdespere));
     	loading.setCancelable(false); loading.show(); }

    protected void onPostExecute(final Boolean success) {
     	if (loading.isShowing()) {loading.dismiss();}}
    
	@Override protected Boolean doInBackground(String... params) {
		List<Est> est = getAllEstablecimientos();
		for(Est e:est){
			try{Class.forName("org.postgresql.Driver");}catch(ClassNotFoundException ex){ex.printStackTrace();}
			try{DriverManager.setLoginTimeout(20);
			Connection conn = DriverManager.getConnection(context.getString(R.string.dirbbdd));
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT textcat_all(cnae || ',') " +
					"FROM categoriaempresa WHERE idcompanyapp="+e.getEid());
			if(rs.next()){ e.setCnae(rs.getString(1));
				updateEstablecimiento(e);
			} rs.close(); st.close(); }catch (SQLException ex) {ex.printStackTrace();}
		}
		return true;}}
	
	//Intento unificar las dos llamadas de ListaArticulos para la creación de filas, a los métodos
	//db.searchPedidoAnt() y db.getArticuloEstablecimiento()
		public ArrayList<Fila> preparaFilaLA(int eid, ArrayList<Art> original) {
		//La fila se compone de Art, Ped y ArtEst. Inicialmente, tenemos la lista de Art, y el eid del Establecimiento
			ArrayList<Fila> results = new ArrayList<Fila>();
			SQLiteDatabase db = this.getReadableDatabase();
			//Recojo todos los Pedidos, conservando aparte las filas que no valgan, si hay
			//List<Art> redux = original;
			int counter=0;
			for(Art a : original){counter++;
			if(counter%10==0) Log.e("COUNTER",""+counter);
			//List<Ped> buscado = new ArrayList<Ped>();
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PEDIDOANT + " AS pa LEFT JOIN " + 
					TABLE_ARTICULOESTABLECIMIENTO + " AS ae ON pa." + KEY_AID + "=ae." + KEY_AID +
					" AND pa." + KEY_EID + "=ae." + KEY_EID + " WHERE pa." + KEY_AID + "=?  AND pa." + 
					KEY_EID + "=?", new String[]{ String.valueOf(a.getAid()), String.valueOf(eid) });
			if(cursor.getCount()>0){ cursor.moveToFirst();
				Ped PA = new Ped(cursor.getInt(0),
				cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4),
				cursor.getDouble(5), cursor.getDouble(6), cursor.getString(7), cursor.getInt(8),
				cursor.getString(9), cursor.getString(10), cursor.getInt(11), cursor.getString(12));
				ArtEst AE = null;
				if(cursor.getString(14)!=null){ AE = new ArtEst(cursor.getInt(13), 
					cursor.getString(14), cursor.getInt(15), cursor.getString(16), 
					cursor.getString(17), cursor.getInt(18),cursor.getString(19)); }
				results.add(new Fila(a,PA,AE));
			} cursor.close(); } db.close(); return results;
		}
		
		//Rework porque hago 11k peticiones a la BD por el for
		public ArrayList<Fila> preparaFilaLA2(int eid, ArrayList<Art> original) {
			//La fila se compone de Art, Ped y ArtEst. Inicialmente, tenemos la lista de Art, y el eid del Establecimiento
				ArrayList<Fila> results = new ArrayList<Fila>();
				SQLiteDatabase db = this.getReadableDatabase();
				int counter=0;
				Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ARTICULOESTABLECIMIENTO + " AS ae LEFT JOIN " + 
						TABLE_PEDIDOANT + " AS pa ON pa." + KEY_AID + "=ae." + KEY_AID +
						" AND pa." + KEY_EID + "=ae." + KEY_EID + " WHERE pa." + KEY_EID + "=?" +
						" GROUP BY ae." + KEY_AID, 
						new String[]{ String.valueOf(eid) });
				if(cursor.getCount()>0){ //cursor.moveToFirst();
				while(cursor.moveToNext()){
					for(Art a : original){
						if(a.getAid()==cursor.getInt(0)){
							Ped PA = new Ped(cursor.getInt(7),
							cursor.getInt(8), cursor.getInt(9), cursor.getInt(10), cursor.getString(11),
							cursor.getDouble(12), cursor.getDouble(13), cursor.getString(14), cursor.getInt(15),
							cursor.getString(16), cursor.getString(17), cursor.getInt(18), cursor.getString(19));
							ArtEst AE = null;
							if(cursor.getString(1)!=null){ AE = new ArtEst(cursor.getInt(0), 
								cursor.getString(1), cursor.getInt(2), cursor.getString(3), 
								cursor.getString(4), cursor.getInt(5),cursor.getString(6)); }
							results.add(new Fila(a,PA,AE));
							break;
						}
					}
					counter++;
					if(counter%10==0) Log.e("COUNTER",""+counter);
				} cursor.close(); } db.close(); return results;
			}
		
		//Me la juego a unificar los artículos en la consulta
		public ArrayList<Fila> preparaFilaLA3(int eid) {
			//La fila se compone de Art, Ped y ArtEst. Inicialmente, tenemos la lista de Art, y el eid del Establecimiento
				ArrayList<Fila> results = new ArrayList<Fila>();
				SQLiteDatabase db = this.getReadableDatabase();
				int counter=0;
				Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ARTICULOESTABLECIMIENTO + " AS ae LEFT JOIN " + 
						TABLE_PEDIDOANT + " AS pa JOIN " + TABLE_ARTICULO + " AS a" +
						" ON pa."+KEY_AID+"=ae."+KEY_AID + " AND pa."+KEY_AID+"=a."+KEY_AID + 
						" AND pa."+KEY_EID+"=ae."+KEY_EID + " WHERE pa." + KEY_EID + "=?" +
						" AND pa."+KEY_PID+"=0 GROUP BY ae." + KEY_AID, 
						new String[]{ String.valueOf(eid) });
				if(cursor.getCount()>0){
				while(cursor.moveToNext()){
					Ped PA = new Ped(cursor.getInt(7),
					cursor.getInt(8), cursor.getInt(9), cursor.getInt(10), cursor.getString(11),
					cursor.getDouble(12), cursor.getDouble(13), cursor.getString(14), cursor.getInt(15),
					cursor.getString(16), cursor.getString(17), cursor.getInt(18), cursor.getString(19));
					ArtEst AE = null;
					if(cursor.getString(1)!=null){ AE = new ArtEst(cursor.getInt(0), 
						cursor.getString(1), cursor.getInt(2), cursor.getString(3), 
						cursor.getString(4), cursor.getInt(5),cursor.getString(6)); }
					Art A = new Art(cursor.getInt(20),cursor.getString(21),cursor.getString(22),cursor.getString(23));
					results.add(new Fila(A,PA,AE));
					counter++;
					if(counter%10==0) Log.e("COUNTER",""+counter);
				} cursor.close(); } db.close(); return results;
			}
		
		public ArrayList<Fila> preparaFilaLC(int eid) {
			ArrayList<Fila> results = new ArrayList<Fila>();
			SQLiteDatabase db = this.getReadableDatabase();
			int counter=0;
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PEDIDOANT + " AS pa JOIN " + TABLE_ARTICULO +
					" AS a ON pa.aid=a.aid WHERE " + KEY_EID + "=?",
					new String[]{ String.valueOf(eid) });
			if(cursor.getCount()>0){ //cursor.moveToFirst();
			while(cursor.moveToNext()){
				Ped PA = new Ped(cursor.getInt(0),
				cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4),
				cursor.getDouble(5), cursor.getDouble(6), cursor.getString(7), cursor.getInt(8),
				cursor.getString(9), cursor.getString(10), cursor.getInt(11), cursor.getString(12));
				Art A = new Art(cursor.getInt(13),cursor.getString(14),cursor.getString(15),cursor.getString(16));
				results.add(new Fila(A,PA,null));
				counter++;
				if(counter%10==0) Log.e("COUNTER",""+counter);
			} cursor.close(); } db.close(); return results;
		}
		
		public void reemplazarArticulos(ArrayList<Fila> filas){
			SQLiteDatabase db = this.getWritableDatabase();
			int contador=0;
			for(Fila f: filas){ Log.e("CONTADOR",""+contador);
			try{
			db.execSQL("INSERT OR REPLACE INTO articulo (aid,articulo,cbarras,tipo) VALUES " +
					"("+f.getArticulo().getAid()+",'"+f.getArticulo().getArticulo()+"','"+
					f.getArticulo().getCbarras()+"','"+ f.getArticulo().getTipo()+"')");
			db.execSQL("INSERT OR REPLACE INTO articuloestablecimiento (aid,afid,eid,fid,sfid,existencias,activo) VALUES " +
					"("+f.getArticuloEstablecimiento().getAid()+",'"+f.getArticuloEstablecimiento().getAfid()+"',"+
					f.getArticuloEstablecimiento().getEid()+",'"+ f.getArticuloEstablecimiento().getFid()+"','"+
					f.getArticuloEstablecimiento().getSfid()+"',"+f.getArticuloEstablecimiento().getExistencias()+",'"+
					f.getArticuloEstablecimiento().getActivo()+"')");
			db.execSQL("INSERT OR REPLACE INTO pedidoant (autoid,pid,aid,eid,fecha,cantidad,precio,obs,estado,afid,oferta,idclif,preciomanual) VALUES " +
					"("+f.getPedido().getAutoid()+","+f.getPedido().getPid()+","+
					f.getPedido().getAid()+","+f.getPedido().getEid()+",'"+
					f.getPedido().getFecha()+"',"+f.getPedido().getCantidad()+","+
					f.getPedido().getPrecio()+",'"+f.getPedido().getObservacion()+"',"+
					f.getPedido().getEstado()+",'"+f.getPedido().getAfid()+"','"+
					f.getPedido().getOferta()+"',"+f.getPedido().getIdclif()+",'"+
					f.getPedido().getPreciomanual()+"')");
			}catch(Exception exc){exc.printStackTrace();}
			contador++;
			//publishProgress(contador);
		}
		}
		
		public void reemplazarArticulos2(String[] valores){
			SQLiteDatabase db = this.getWritableDatabase();
			String[] consulta = new String[]{"INSERT OR REPLACE INTO articulo (aid,articulo,cbarras,tipo) VALUES ",
				"INSERT OR REPLACE INTO articuloestablecimiento (aid,afid,eid,fid,sfid,existencias,activo) VALUES ",
				"INSERT OR REPLACE INTO pedidoant (autoid,pid,aid,eid,fecha,cantidad,precio,obs,estado,afid,oferta," +
				"idclif,preciomanual) VALUES "};
			try{
			/*Log.e("BACKUP","INSERT OR REPLACE INTO articulo (aid,articulo,cbarras,tipo) VALUES " + 
					valores[0].substring(0,10)+"[...]" + valores[0].substring(valores[0].length()-10,valores[0].length())+ "; " +
					"INSERT OR REPLACE INTO articuloestablecimiento " +
						"(aid,afid,eid,fid,sfid,existencias,activo) VALUES " +
					valores[1].substring(0,10)+"[...]" + valores[1].substring(valores[1].length()-10,valores[1].length())+ "; " +
					"INSERT OR REPLACE INTO pedidoant " +
						"(autoid,pid,aid,eid,fecha,cantidad,precio,obs,estado,afid,oferta,idclif,preciomanual) " + "VALUES " + 
						valores[2].substring(0,10)+"[...]" + valores[2].substring(valores[2].length()-10,valores[2].length())+ ";");*/
			for(int i=0;i<3;i++){
				String[] filas = valores[i].split("\\),\\("); 
				if(filas.length>50){
					String cincuenta="";
					for(int j=0;j<filas.length/50+1;j++){
						if(j==filas.length/50)
							//for(int k=0;k<(filas.length-(j-1)*500);k++) quinientos+=filas[k+j*500]+"),(";
							for(int k=0;k<filas.length%50;k++) cincuenta+=filas[k+j*50]+"),(";
						else for(int k=0;k<50;k++) cincuenta+=filas[k+j*50]+"),(";
						if(j>0){
							if (j==filas.length/50)
								cincuenta="("+cincuenta.substring(0,cincuenta.length()-3);
							else cincuenta="("+cincuenta.substring(0,cincuenta.length()-2);
						} else cincuenta=cincuenta.substring(0,cincuenta.length()-2);
						db.execSQL(consulta[i]+cincuenta); cincuenta="";
					}
				}else db.execSQL(consulta[i]+valores[i]);
			}
			
			/*db.execSQL("INSERT OR REPLACE INTO articulo (aid,articulo,cbarras,tipo) VALUES " + valores[0] + "; " +
					"INSERT OR REPLACE INTO articuloestablecimiento " +
						"(aid,afid,eid,fid,sfid,existencias,activo) VALUES " + valores[1] + "; " +
					"INSERT OR REPLACE INTO pedidoant " +
						"(autoid,pid,aid,eid,fecha,cantidad,precio,obs,estado,afid,oferta,idclif,preciomanual) " +
						"VALUES " + valores[2] + ";");*/
			}catch(Exception exc){exc.printStackTrace();}
			//contador++;
			//publishProgress(contador);
		//}
		}
		
		/*public void reemplazarArticulos(ArrayList<Fila> filas){
			new reemplazarArticulosPAR(filas).execute();
		}
		
		public class reemplazarArticulosPAR extends AsyncTask<String, Integer, Boolean> {
			ProgressDialog loading; ArrayList<Fila> filas;
			int nmensaje = 0;
			public reemplazarArticulosPAR(ArrayList<Fila> filas) 
				{ this.filas=filas;}
			
			
			protected void onPreExecute() {
	     	loading = new ProgressDialog(context);
			loading.setMessage("Verificando integridad de artículos...");
	     	loading.setCancelable(false); loading.show(); 
				}

	    protected void onPostExecute(final Boolean success) {
	     	if (loading.isShowing()) {loading.dismiss();}}
	    
	    protected void onProgressUpdate(Integer... progreso) {
            loading.setProgress(progreso[0]); }
	    
		@Override protected Boolean doInBackground(String... params) {
			SQLiteDatabase db = getWritableDatabase();
			int contador=0;
			loading.setMax(filas.size());
			for(Fila f: filas){ Log.e("CONTADOR",""+contador);
			try{
			db.execSQL("INSERT OR REPLACE INTO articulo (aid,articulo,cbarras,tipo) VALUES " +
					"("+f.articulo.getAid()+",'"+f.articulo.getArticulo()+"','"+
					f.articulo.getCbarras()+"','"+ f.articulo.getTipo()+"')");
			db.execSQL("INSERT OR REPLACE INTO articuloestablecimiento (aid,afid,eid,fid,sfid,existencias,activo) VALUES " +
					"("+f.artest.getAid()+",'"+f.artest.getAfid()+"',"+
					f.artest.getEid()+",'"+ f.artest.getFid()+"','"+
					f.artest.getSfid()+"',"+f.artest.getExistencias()+",'"+
					f.artest.getActivo()+"')");
			db.execSQL("INSERT OR REPLACE INTO pedidoant (autoid,pid,aid,eid,fecha,cantidad,precio,obs,estado,afid,oferta,idclif,preciomanual) VALUES " +
					"("+f.panterior.getAutoid()+","+f.panterior.getPid()+","+
					f.panterior.getAid()+","+f.panterior.getEid()+",'"+
					f.panterior.getFecha()+"',"+f.panterior.getCantidad()+","+
					f.panterior.getPrecio()+",'"+f.panterior.getObservacion()+"',"+
					f.panterior.getEstado()+",'"+f.panterior.getAfid()+"','"+
					f.panterior.getOferta()+"',"+f.panterior.getIdclif()+",'"+
					f.panterior.getPreciomanual()+"')");
			}catch(Exception exc){exc.printStackTrace();}
			contador++;
			publishProgress(contador);
		}
		return true;
		}
		}*/
}