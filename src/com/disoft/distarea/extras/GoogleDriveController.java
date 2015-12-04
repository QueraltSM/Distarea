package com.disoft.distarea.extras;

import java.io.File;

import android.app.Activity;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveFolder.DriveFolderResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.DriveResource.MetadataResult;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.plus.Plus;

public class GoogleDriveController implements ConnectionCallbacks, OnConnectionFailedListener {
	
	private GoogleApiClient mGoogleApiClient;	
	private GoogleDriveCallback googleDriveCallback;
	private Activity context;
	private boolean connected;
	public final static int RESOLVE_CONNECTION_REQUEST_CODE = 10;
	public final static int SUCESS_CODE = 11;
	private File fileToUpload;
	private String fileDriveName;
	private String mimeType;
	private static DriveId rootAppFolder;
	private static DriveId businessAppFolder;
	private static String appFolderEncoded,businessFolderEncoded;
	private String business;
	
	/**
	 * Construye un controlador con el cual manipular Google Drive
	 * 
	 * @param context Actividad padre 
	 * @param googleDriveCallback Callback al cual notificar eventos
	 * @param business Negocio con el que trabajar, null para inicializar carpeta de aplicacion
	 */
	public GoogleDriveController(Activity context,GoogleDriveCallback googleDriveCallback,String business) {
		super();
		this.googleDriveCallback = googleDriveCallback;
		this.context = context;		
		this.business = business;
		connected = false;
		mGoogleApiClient = new GoogleApiClient.Builder(context)
        	.addApi(Drive.API)
        	.addApi(Plus.API)
        	.addScope(Drive.SCOPE_FILE)
        	.addConnectionCallbacks(this)
        	.addOnConnectionFailedListener(this)
        	.build();
	}
	
	/**
	 * Conecta con drive y al finalizar llama al método onConnected de {@link #googleDriveCallback}
	 */
	public void connect(){ mGoogleApiClient.connect(); }
	
	public boolean isConnected(){ return connected; }
	
	@Override public void onConnectionFailed(ConnectionResult connectionResult) {
	    if (connectionResult.hasResolution()) {
	    	if(connectionResult.isSuccess()){
	    		try{ connectionResult.startResolutionForResult(context, SUCESS_CODE);
				} catch (SendIntentException e) { e.printStackTrace(); }
	    	}else{
		        try{ connectionResult.startResolutionForResult(context, RESOLVE_CONNECTION_REQUEST_CODE);
		        } catch (IntentSender.SendIntentException e) { e.printStackTrace(); }
	    	}
	    } else GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),context, 0).show(); 
	}
	
	@Override public void onConnectionSuspended(int arg0) { connected = false;
	googleDriveCallback.onDisconnect("Disconnected"); }
	
	@Override public void onConnected(Bundle arg0) { connected = true;	
	googleDriveCallback.onStatusChange("Inicializando"); initAppFolder(); }

	private void initAppFolder() {
		appFolderEncoded = SharedPreferencesController.getValue(context, "_root");
		googleDriveCallback.onStatusChange(appFolderEncoded);
		if(appFolderEncoded == null){
			MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("Distarea").build();
			Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(mGoogleApiClient, changeSet).setResultCallback(rootFolderCreatedCallback);
		}
		else{
			setAppDriveFolder(appFolderEncoded);
			googleDriveCallback.onConnected("Connected");
			if(business!=null){ initBusinessFolder();
			Log.e("BUSINESS",business);
			}
		}
	}

	
	
	public void createBusinessFolder(String folderName){
		MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(folderName).build();
		if(rootAppFolder == null){
			googleDriveCallback.onStatusChange("Carpeta de la app no disponible");
		}else{
			try{
				Drive.DriveApi.getFolder(mGoogleApiClient, rootAppFolder).createFolder(mGoogleApiClient, changeSet)
				.setResultCallback(businessFolderCreatedCallback);
				Log.e("CREA","CARPETA BUSSINESS");
			}catch(Exception e){e.printStackTrace();}
		}		
	}

	/*public void createFolder(String folderName){
		MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(folderName).build();
		if(rootAppFolder == null){
			Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(mGoogleApiClient, changeSet).setResultCallback(rootFolderCreatedCallback);
		}else{
			Drive.DriveApi.getFolder(mGoogleApiClient, rootAppFolder).createFolder(mGoogleApiClient, changeSet).setResultCallback(rootFolderCreatedCallback);
		}		
	}*/
	
	private void initBusinessFolder() {
		businessFolderEncoded = SharedPreferencesController.getValue(context,business+"_resource");
		Log.e("CALLBACK","Intenta crear Bussiness Folder");
		if(businessFolderEncoded == null) { 
			if(SharedPreferencesController.getValue(context,business+"_key") == null || 
					SharedPreferencesController.getValue(context,business+"_key").equals("")){
				createBusinessFolder(business); Log.e("CALLBACK","No existe, hazte una.");
			}else queryFolder(business);
		
		}
		else{ checkFolderExistence(business); //Thrashed?
			Log.e("BUSINESSFOLDERENCODED",""+businessFolderEncoded);
			setBusinessDriveFolder(SharedPreferencesController.getValue(context,business+"_key"));
			googleDriveCallback.onConnected("Connected to business folder");
		}
	}

	final private ResultCallback<DriveApi.DriveContentsResult> contentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
        @Override public void onResult(DriveApi.DriveContentsResult result) {
            if (!result.getStatus().isSuccess()) return;           
            if(businessAppFolder == null){
            	googleDriveCallback.onErrorFileUpload("Carpeta de la aplicacion no disponible");
            	Log.e("NULL","NO HAY businessAppFolder");
    		}else{
    			try{
    				DataTransfer.transfer(fileToUpload, result.getDriveContents().getOutputStream());
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(fileDriveName)
                    		.setMimeType(mimeType).setStarred(true).build();                
                    Log.e("BAF",""+businessAppFolder.encodeToString());
        			Drive.DriveApi.getFolder(mGoogleApiClient, businessAppFolder).createFile(mGoogleApiClient, 
        					changeSet, result.getDriveContents()).setResultCallback(fileCallback);
    			}
    			catch (Exception ex){
    				try{
    					if(rootAppFolder != null){
    					DataTransfer.transfer(fileToUpload, result.getDriveContents().getOutputStream());
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(fileDriveName)
                        		.setMimeType(mimeType).setStarred(true).build();                
            			Drive.DriveApi.getFolder(mGoogleApiClient, rootAppFolder).createFile(mGoogleApiClient, 
            					changeSet, result.getDriveContents()).setResultCallback(fileCallback);
    					}
    				}catch(Exception e){e.printStackTrace();}
    				googleDriveCallback.onErrorFileUpload("Error al subir el fichero"+ex.getMessage());
    			}
    		}            
        }
    };
    
    final private ResultCallback<DriveFolderResult> rootFolderCreatedCallback = new ResultCallback<DriveFolderResult>() {
        @Override public void onResult(DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) return;
            if(rootAppFolder==null){
            	setAppDriveFolder(result.getDriveFolder().getDriveId().encodeToString());
            	Log.e("WTF IS",result.getDriveFolder().getDriveId().encodeToString());//1era pulsación -> DriveId:
            	SharedPreferencesController.putValue(result.getDriveFolder().getDriveId().encodeToString(),context, "_root");
            	if(business!=null) initBusinessFolder();
            }   
        }
    };
    
    final private ResultCallback<DriveFolderResult> businessFolderCreatedCallback = new ResultCallback<DriveFolderResult>() {
        @Override public void onResult(DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) return;             
            if(businessAppFolder==null){
            	SharedPreferencesController.putValue(result.getDriveFolder().getDriveId().encodeToString(),context,business+"_key");
            	queryFolder(business);
            }   
        }
    };
    
    final private ResultCallback<DriveResource.MetadataResult> checkFolderTrashedCallback = new ResultCallback<DriveResource.MetadataResult>() {
            @Override public void onResult(DriveResource.MetadataResult result) {
                if (!result.getStatus().isSuccess()){ Log.e("CALLBACK","No tengo Stat OK"); return; }
                Metadata metadata = result.getMetadata();                
                if(metadata.isTrashed()){ 
                	Log.e("CALLBACK","Thrashed.");
                	googleDriveCallback.onStatusChange("Reseting business folder");
                	createBusinessFolder(business);
                }else{
                	Log.e("CALLBACK","Not thrashed");
                	setBusinessDriveFolder(//businessFolderEncoded
                			SharedPreferencesController.getValue(context, business+"_key"));
                	Log.e("BUSINESSFOLDERENCODED",businessFolderEncoded);
                	//SharedPreferencesController.putValue(DriveId.decodeFromString(SharedPreferencesController.getValue(context,"_key")).getResourceId()+"",context, "_resource");
                	//?? SharedPreferencesController.putValue(arg0.getDriveId().getResourceId(),context, "_resource");
                	//googleDriveCallback.onFolderConnected(SharedPreferencesController.getValue(context, business+"_resource"));
                		//Log.e("RESOURCE?!",SharedPreferencesController.getValue(context, "_resource"));
                	googleDriveCallback.onFolderConnected(SharedPreferencesController.getValue(context, business+"_resource"));
        			googleDriveCallback.onConnected("Connected to business folder");
                }
            }
    };
    
    
    
    private void setAppDriveFolder(String s){ rootAppFolder = DriveId.decodeFromString(s); }
    
    private void setBusinessDriveFolder(String s){ businessAppFolder = DriveId.decodeFromString(s);
    	googleDriveCallback.onStatusChange("BusinessResource: "+SharedPreferencesController.getValue(context, business+"_key"));
    }
    
    private void checkFolderExistence(String folderName){
    	 DriveId folderId = DriveId.decodeFromString( SharedPreferencesController.getValue(context,business+"_key"));
         DriveFolder folder = Drive.DriveApi.getFolder(mGoogleApiClient, folderId);
         folder.getMetadata(mGoogleApiClient).setResultCallback(checkFolderTrashedCallback);
    }
    
    public void resetFolder(){
    	SharedPreferencesController.resetKey(context, business+"_key");
    	SharedPreferencesController.resetKey(context, business+"_resource");
    	businessAppFolder=null;
    	createBusinessFolder(business);
    }
    
    /**
	 * Sube el fichero especificado como parámetro a la carpeta raiz de google drive del usuario.
	 * @param file fichero a subir a drive
	 * @param fileDriveName nombre del fichero en drive
	 * @param mimeType tipo de fichero a subir ej. imagen: image/jpg
	 */
	public void uploadFile(File file,String fileDriveName,String mimeType){
		fileToUpload = file;		
		this.mimeType = mimeType;
		this.fileDriveName = fileDriveName;
		if(fileToUpload.exists()){
			Log.e("SUBIENDO",file.getAbsolutePath());
			Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(contentsCallback);
		}
		else{ Log.e("NO ENCONTRADO",fileDriveName);
			googleDriveCallback.onLocalFileError("LocalFileError"); }
	}
	
	final private ResultCallback<DriveFileResult> fileCallback = new ResultCallback<DriveFileResult>() {
        @Override public void onResult(final DriveFileResult result) {
            if (!result.getStatus().isSuccess())             	
            	googleDriveCallback.onErrorFileUpload("Error file upload");
            else
            	Drive.DriveApi.getFile(mGoogleApiClient,result.getDriveFile().getDriveId()).getMetadata(mGoogleApiClient)
					.setResultCallback(fileMetadataCallback); 
        }
    };

    private ResultCallback<MetadataResult> fileMetadataCallback = new ResultCallback<MetadataResult>() {
        @Override public void onResult(MetadataResult result) {
            if (!result.getStatus().isSuccess()) return;
            Metadata metadata = result.getMetadata();
            googleDriveCallback.onFileUploaded(metadata.getTitle());
            }
    };
    
    public String getAccountName(){ return Plus.AccountApi.getAccountName(mGoogleApiClient); }
        
    public void queryFolder(String business){
    	Log.e("STRING BUSINESS",business);
    	DriveFolder raiz = Drive.DriveApi.getFolder(mGoogleApiClient, DriveId.decodeFromString(SharedPreferencesController.getValue(context,"_root")));
    	Log.e("RAIZ",raiz.getDriveId().encodeToString());
    	raiz.listChildren(mGoogleApiClient).setResultCallback(queryFolderCallback);
    }
    
    final private ResultCallback<MetadataBufferResult> queryFolderCallback = new ResultCallback<MetadataBufferResult>() {
    	@Override public void onResult(MetadataBufferResult result) {
        	 if (!result.getStatus().isSuccess()){ return; }
        	 else{
        		 	int i=1;
        		 	for(Metadata m : result.getMetadataBuffer()){
        		 		Log.e("TITLE"+i,""+m.getTitle()); i++;
        		 		if(m.getTitle().equals(business)){
        		 			DriveId did = m.getDriveId();
                	    	Log.e("DRIVEID",did.encodeToString());
                	    	String resource = did.getResourceId();
                	    	Log.e("TITLE",""+m.getTitle());
                	    	Log.e("RESOURCE",""+resource);
                	    	if (resource!=null && !resource.equals("")){
                	    		//Toast.makeText(context, "Ha conectado con su cuenta de Google Drive. Por favor, confirme la referencia para continuar.", Toast.LENGTH_LONG).show();
                	    		SharedPreferencesController.putValue(resource,context, business+"_resource");
                	    		SharedPreferencesController.putValue(did.encodeToString(),context, business+"_key");
                	    		SharedPreferencesTableLoader sptl = new SharedPreferencesTableLoader(context);
                	    		sptl.load();
                	    	}
        		 		}
        		 	}
        	 }
        	//Toast.makeText(context, "Ha conectado con su cuenta de Google Drive. Por favor, confirme la referencia para continuar.", Toast.LENGTH_LONG).show();
        }
    };
    
    //WIP, requiere GAPISDrive.java, y Drive API 1.20
    
    //Test share folder
    /*private static Permission compartir(com.google.api.services.drive.Drive service, String fileId,
    	      String value, String type, String role) {
    		//com.google.api.services.drive.Drive service = new com.google.api.services.drive.Drive();
    		com.google.api.services.drive.Drive service = com.google.api.services.drive.DriveScopes.getDriveService();
    	    Permission newPermission = new Permission();
    	    newPermission.setValue(value);
    	    newPermission.setType(type);
    	    newPermission.setRole(role);
    	    try {
    	      return service.permissions().insert(fileId, newPermission).execute();
    	    } catch (IOException e) {
    	      System.out.println("An error occurred: " + e);
    	    }
    	    return null;
    	  }*/
    
}