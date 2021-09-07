package com.disoft.distarea.extras;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataTransfer {

	public static boolean transfer(File inputFile,OutputStream outputStream){
        try {
	        byte[] buf = new byte[8192];
	        InputStream is = new FileInputStream(inputFile);
	        int c = 0;
	        while ((c = is.read(buf, 0, buf.length)) > 0) {
	        	outputStream.write(buf, 0, c);
	        	outputStream.flush();
	        }
	        outputStream.close();
	        is.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
