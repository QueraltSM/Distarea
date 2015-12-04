package com.disoft.distarea.extras;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VoiceCalibrator {

	public static String pattern = "Empresa Fecha Total IVA Recargo Factura Retencion Implicito";
	
	public static Map<String,String> calibrate(String voiceIn){		
		Map<String,String> words = new HashMap<String,String>();		
		voiceIn = removeGarbage(voiceIn);	
		voiceIn = voiceIn.toUpperCase();
		String voiceInArray[] = voiceIn.split("\\s++");
		String patternInArray[] = pattern.split("\\s++");
		if(voiceInArray.length == patternInArray.length){
			int count = 0;
			for(String word : voiceInArray){
				words.put(patternInArray[count],word);				
				count++;
			}
		}
		return words;
	}

	private static String removeGarbage(String text){
		text = " "+text+" ";
		return text.replaceAll("([\\s++]((\\w{1,2})|(\\w{1,2}[s]))[\\s++])+", " ").trim();
	}
}
