package com.disoft.distarea.extras;

public interface GoogleDriveCallback {

	public void onConnected(String callbackInfo);
	public void onDisconnect(String callbackInfo);
	public void onFileUploaded(String callbackInfo);
	public void onErrorFileUpload(String callbackInfo);
	public void onLocalFileError(String callbackInfo);
	public void onFinishUploads(String callbackInfo);
	public void onStatusChange(String callbackInfo);
	public void onBusinessFolderCreated(String callbackInfo);
	public void onFolderConnected(String callbackInfo);
	
}
