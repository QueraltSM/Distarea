package com.disoft.distarea.extras;

import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class LanguageController {

	/*
	 * Cambia el idioma de un contexto al especificado como parámetro.
	 * 
	 * @param context El contexto a cambiar el idioma.
	 * @param language 
	 * @return 
	 */
	public static void changeLanguageToContext(Context context,String language){
		Resources res = context.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		android.content.res.Configuration conf = res.getConfiguration();
		conf.locale = getLanguageLocaleFromString(language);
		res.updateConfiguration(conf, dm);
	}
	
	private static Locale getLanguageLocaleFromString(String language){
		String lang = language.toLowerCase();
		if(lang.equals("ingles") || lang.equals("in") || lang.equals("en")){
			return Locale.ENGLISH;
		}else if(lang.equals("frances") || lang.equals("fr")){
			return Locale.FRENCH;
		}else if(lang.equals("aleman") || lang.equals("al")){
			return Locale.GERMAN;
		}else{
			return new Locale("es","ES");
		}
	}
}
