package com.disoft.distarea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Firma extends View {
		public boolean limpieza = false, guardar = false;
		float x = 50, y = 50; int accion = 0; String pid;  
		Paint paint = new Paint(); Path path = new Path();
		SharedPreferences sharedPrefs; SharedPreferences.Editor Ed;
		
	
		public Firma(Context context){ super(context); setDrawingCacheEnabled(true);
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);}
			//Ed = sharedPrefs.edit(); nfirma = sharedPrefs.getString("nfirma", "0");}

		public Firma(Context context, AttributeSet attrs) {
			super(context, attrs); setDrawingCacheEnabled(true); }
		
		@Override public void onDraw(Canvas canvas) {
			super.onDraw(canvas); canvas.drawColor(Color.WHITE);
			Bitmap fondo = BitmapFactory.decodeResource(getContext().getResources(), 
					R.drawable.marcadeagua_firma);
			float left = 0, top = 0, ancho = fondo.getWidth(), alto = fondo.getWidth();
			while (left < canvas.getWidth()) {
				while (top < canvas.getHeight()) {
					canvas.drawBitmap(fondo, left, top, null); top += alto; }
			  left += ancho; top = 0; }
			paint.setAntiAlias(true); paint.setStyle(Paint.Style.STROKE);
	    paint.setColor(Color.BLACK); paint.setStrokeCap(Paint.Cap.ROUND);
	    paint.setStrokeJoin(Paint.Join.ROUND); paint.setStrokeWidth(6);
			
			if(accion == 1) path.moveTo(x,y);
			if(accion == 2) path.lineTo(x,y);
			canvas.drawPath(path, paint);
			if(limpieza){ x=0; y=0; path.reset(); canvas.drawColor(Color.WHITE); 
			while (left < canvas.getWidth()) {
				while (top < canvas.getHeight()) {
					canvas.drawBitmap(fondo, left, top, null); top += alto; }
			  left += ancho; top = 0; }
			invalidate(); limpieza=false; }
			if(guardar){
				File outFile; FileOutputStream fileStream;
		    outFile = new File( File.separator + "data" + File.separator + "data" + 
		    		File.separator + getContext().getPackageName() + File.separator + 
		    		"firmas" + File.separator);
		    if(!outFile.exists()){ outFile.mkdir();
		    	try{new File (outFile+ File.separator+".nomedia").createNewFile();
		    	}catch (IOException e){e.printStackTrace();} }
				/*try{String salida; Integer numfirma = Integer.parseInt(nfirma); numfirma+=1;
				if(numfirma/100>1) salida = numfirma.toString();
				else{ if (numfirma/10>1) salida = "0"+numfirma;
							else salida = "00"+numfirma; }
				Ed.putString("nfirma",salida).commit(); //REVISAR NOMBRE*/
				try{fileStream = new FileOutputStream(new File(outFile+File.separator +pid+".jpg"));
				getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, fileStream );
				} catch (FileNotFoundException e) {e.printStackTrace();}
				//}catch (Exception e) {e.printStackTrace();}
			}
		}
		
		public boolean onTouchEvent(MotionEvent e){
			x = e.getX(); y = e.getY();
			if(e.getAction() == MotionEvent.ACTION_DOWN) accion = 1;
			if(e.getAction() == MotionEvent.ACTION_MOVE) accion = 2;
			invalidate(); return true; }		
		
		public void borrarFirma(){ limpieza=true; invalidate(); }
		
		public void guardarFirma(String pid){ guardar=true; this.pid = pid; invalidate(); }
	}