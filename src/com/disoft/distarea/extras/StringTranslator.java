package com.disoft.distarea.extras;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTranslator {
	
	/**
	 * Devuelve un codigo de factura del tipo A-12312312 a partir de una string desordenada
	 * @param stringToTranslate
	 * @return
	 */
	public static String translateBill(String stringToTranslate){
		if(stringToTranslate ==null){
			return "";
		}
		stringToTranslate = letterToDigit(stringToTranslate);
		stringToTranslate = stringToTranslate.replaceAll(";","");
		Pattern pattern = Pattern.compile("([a-z|-].*)");
		Matcher matcher = pattern.matcher(stringToTranslate.toLowerCase());
		while(matcher.find())
		{
			stringToTranslate = matcher.group(1);
		}
		stringToTranslate = stringToTranslate.replaceAll("guion", "-");		
		String letter = getRealLetter(stringToTranslate);
		String digits = stringToTranslate.replaceAll("[a-zA-Z]|-","");
		if(letter.equals("")){
			return digits.replaceAll(" ","");
		}
		return new String(letter+"-"+digits).replaceAll(" ","");
	}
	
	/**
	 * Devuelve un codigo de factura a partir de una string desordenada
	 * @param stringToTranslate
	 * @return
	 */
	public static String translateLargeBill(String stringToTranslate){
		if(stringToTranslate ==null){
			return "";
		}
		stringToTranslate = letterToDigit(stringToTranslate);
		stringToTranslate = stringToTranslate.replaceAll("([a-zA-Z]{5,})","");			
		String result = "";
		for(String s : stringToTranslate.split(" ")){
			if(!s.equals(" ")){
				if(isNumeric(s)){
					result += s;
				}else{
					if(s.matches(".*\\d.*")){
						result += s;
					} 
					else result += getRealLetter(s);
				}
			}
		}
		return result;
	}
	/**
	 * Devuelve un NIF a partir de una string desordenada
	 * @param stringToTranslate
	 * @return
	 */
	public static String translateNIF(String stringToTranslate){
		if(stringToTranslate ==null){
			return "";
		}
		stringToTranslate = letterToDigit(stringToTranslate);
		Pattern pattern = Pattern.compile("([0-9]|[0-9][\\s]){7,8}");		
		Matcher matcher = pattern.matcher(stringToTranslate);
		String digits = "";
		while(matcher.find()){
			digits = matcher.group(0);
		}
		String preDigits = stringToTranslate.substring(0,stringToTranslate.indexOf(digits));
		String postDigits = stringToTranslate.substring(stringToTranslate.indexOf(digits)+digits.length(),stringToTranslate.length());
		preDigits = preDigits.split(" ")[preDigits.split(" ").length-1];
		preDigits = preDigits.replaceAll("[^a-zA-Z]","");
		preDigits = getRealLetter(preDigits);
		if(!isNumeric(postDigits)){
			postDigits = getRealLetter(postDigits);
		}else{
			postDigits = postDigits.charAt(0)+"";
		}
		return preDigits+digits.replaceAll(" ","")+postDigits;
	}
	
	/**
	 * Devuelve una fecha partir de una string desordenada
	 * @param stringToTranslate
	 * @return
	 */
	public static String translateDate(String stringToTranslate){
		if(stringToTranslate ==null){
			return "";
		}
		stringToTranslate = letterToDigit(stringToTranslate);
		stringToTranslate = monthToDigit(stringToTranslate);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date dateObject;
		Calendar cal = Calendar.getInstance();		
		stringToTranslate = stringToTranslate.replaceAll("[^0-9]"," ");
		stringToTranslate =internalTrim(stringToTranslate);
		String[] dateSplited = stringToTranslate.split(" ");
		try{
			if(dateSplited.length >=3){
				if(Integer.parseInt(dateSplited[2])<100) cal.set(Calendar.YEAR, Integer.parseInt(dateSplited[2])+2000);
				else cal.set(Calendar.YEAR, Integer.parseInt(dateSplited[2]));
			}
			if(dateSplited.length >=2) cal.set(Calendar.MONTH, Integer.parseInt(dateSplited[1])-1);
			if(dateSplited.length >=1) cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateSplited[0]));
		}catch(Exception ex){
			
		}		
		dateObject = cal.getTime();
		return sdf.format(dateObject);
	}
	
	/**
	 * Devuelve un total a partir de una string desordenada
	 * @param stringToTranslate
	 * @return
	 */
	public static String translateTotal(String stringToTranslate){
		if(stringToTranslate ==null){
			return "";
		}
		stringToTranslate = stringToTranslate.toUpperCase();
		stringToTranslate = letterToDigit(stringToTranslate);
		stringToTranslate = stringToTranslate.replaceAll("[^a-zA-Z0-9]","");
		stringToTranslate = stringToTranslate.replaceAll("MENOS","-");
		stringToTranslate = stringToTranslate.replaceAll("CON",".");
		stringToTranslate = stringToTranslate.replaceAll("COMA",".");
		stringToTranslate = stringToTranslate.replaceAll("SON",".");
		stringToTranslate = stringToTranslate.replaceAll("PON",".");
		stringToTranslate = stringToTranslate.replaceAll("[a-zA-Z]","");
		String first = "";
		String last = "";
		for(String s : stringToTranslate.split("\\.")){
			if(!s.equals("") && first.equals("")){
				first = s;
			}else if(!s.equals("")){
				last+=s;
			}			
		}
		return first+"."+last;
	}
	
	public static String translateIVA(String stringToTranslate){
		if(stringToTranslate ==null){
			return "";
		}
		stringToTranslate = letterToDigit(stringToTranslate);
		stringToTranslate = stringToTranslate.replaceAll("MENOS","-");
		stringToTranslate = stringToTranslate.replaceAll("CON",".");
		stringToTranslate = stringToTranslate.replaceAll("COMA",".");
		stringToTranslate = stringToTranslate.replaceAll("SON",".");
		stringToTranslate = stringToTranslate.replaceAll("PON",".");
		stringToTranslate = stringToTranslate.replaceAll("[a-zA-Z]"," ");
		String result = "";
		boolean first = true;
		boolean suma = true;
		int total = 0;
		for(String s : stringToTranslate.split("[\\s]{1,}")){
			/*if(first){
				result += s+"%";
				if(stringToTranslate.split("[\\s]{1,}").length > 1){
					result+= " de ";
				}				
				first = false;
			}else{		
				if(isNumeric(s)){
					if(suma){
						total+= Integer.valueOf(s);
					}
				}
				else if(suma){
					suma = false;
					result+=total;
				}
				if(!suma) result+=s;
			}	*/
			
		}
		return result;
	}
	private static String letterToDigit(String stringToTranslate){
		if(stringToTranslate == null){
			return "";
		}
		stringToTranslate = stringToTranslate.toUpperCase();		
		stringToTranslate = stringToTranslate.replaceAll("DOSMIL","2000");
		stringToTranslate = stringToTranslate.replaceAll("DOS MIL","2000");
		stringToTranslate = stringToTranslate.replaceAll("TRESMIL","3000");
		stringToTranslate = stringToTranslate.replaceAll("TRES MIL","3000");
		stringToTranslate = stringToTranslate.replaceAll("CUATROMIL","4000");
		stringToTranslate = stringToTranslate.replaceAll("CUATRO MIL","4000");
		stringToTranslate = stringToTranslate.replaceAll("CINCOMIL","5000");
		stringToTranslate = stringToTranslate.replaceAll("CINCO MIL","5000");
		stringToTranslate = stringToTranslate.replaceAll("SEISMIL","6000");
		stringToTranslate = stringToTranslate.replaceAll("SEIS MIL","6000");
		stringToTranslate = stringToTranslate.replaceAll("SIETEMIL","7000");
		stringToTranslate = stringToTranslate.replaceAll("SIETE MIL","7000");
		stringToTranslate = stringToTranslate.replaceAll("OCHOMIL","8000");
		stringToTranslate = stringToTranslate.replaceAll("OCHO MIL","8000");
		stringToTranslate = stringToTranslate.replaceAll("CERO","0");
		stringToTranslate = stringToTranslate.replaceAll("UNO","1");
		stringToTranslate = stringToTranslate.replaceAll("DOS","2");
		stringToTranslate = stringToTranslate.replaceAll("TRES","3");
		stringToTranslate = stringToTranslate.replaceAll("CUATRO","4");
		stringToTranslate = stringToTranslate.replaceAll("CINCO","5");
		stringToTranslate = stringToTranslate.replaceAll("SEIS","6");		
		stringToTranslate = stringToTranslate.replaceAll("SIETE","7");
		stringToTranslate = stringToTranslate.replaceAll("OCHO","8");
		stringToTranslate = stringToTranslate.replaceAll("NUEVE","9");		
		stringToTranslate = stringToTranslate.replaceAll("MIL","1000");
		return stringToTranslate;
	}
	
	private static String monthToDigit(String stringToTranslate){
		stringToTranslate = stringToTranslate.toUpperCase();
		stringToTranslate = stringToTranslate.replaceAll("ENERO","1");
		stringToTranslate = stringToTranslate.replaceAll("FEBRERO","2");
		stringToTranslate = stringToTranslate.replaceAll("MARZO","3");
		stringToTranslate = stringToTranslate.replaceAll("ABRIL","4");
		stringToTranslate = stringToTranslate.replaceAll("MAYO","5");
		stringToTranslate = stringToTranslate.replaceAll("JUNIO","6");
		stringToTranslate = stringToTranslate.replaceAll("JULIO","7");		
		stringToTranslate = stringToTranslate.replaceAll("AGOSTO","8");
		stringToTranslate = stringToTranslate.replaceAll("SEPTIEMBRE","9");
		stringToTranslate = stringToTranslate.replaceAll("OCTUBRE","10");
		stringToTranslate = stringToTranslate.replaceAll("NOVIEMBRE","11");
		stringToTranslate = stringToTranslate.replaceAll("DICIEMBRE","12");
		return stringToTranslate;
	}
	
	public static Map<String,String> getStringMapped(String stringToMap,Table table){
		Map<String,String> map = new HashMap<String, String>();
		stringToMap = stringToMap.toUpperCase();	
		try{
			for(String keyWord : table.getKeys()){
				stringToMap = stringToMap.replaceAll(table.getRegExpByKey(keyWord),keyWord);
				if(stringToMap.contains(keyWord)){
					String testWord = "";
					
						testWord = stringToMap.split(keyWord)[stringToMap.split(keyWord).length-1].trim();	
						testWord = testWord.replaceAll(table.getRegExpDifferentByKeyWord(keyWord),";;;;;").split(";;;;;")[0];
									
					map.put(keyWord, testWord.trim());
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return map;
	}
	
	public static String[] getAviso(String stringToTranslate){
		String [] result = new String[2];
		stringToTranslate = stringToTranslate.toUpperCase();
		if(stringToTranslate.contains("AVISÓ")){
			result[0] = stringToTranslate.split("AVISÓ")[0];
			if(stringToTranslate.split("AVISÓ").length==1){
				result[1] = "";
			}else{
				result[1] = stringToTranslate.split("AVISÓ")[1];	
			}	
		}else{
			result[0] = stringToTranslate.split("AVISO")[0];
			if(stringToTranslate.split("AVISO").length==1){
				result[1] = "";
			}else{
				result[1] = stringToTranslate.split("AVISO")[1];	
			}
		}
		return result;
	}
	/*
	 *  PRIVATE METHODS
	 */
	private static boolean isNumeric(String str)  
	{  
	  try{  
	    Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe){  
	    return false;  
	  }  
	  return true; 
	}
	
	private static String getRealLetter(String bill){
		String letterCode = bill.replaceAll("[0-9]| |-","");		
		if(letterCode.length()>1){
			if(letterCode.contains("UVE")){
				return "V";
			}
			if(letterCode.contains("SETA")){
				return "Z";
			}
			letterCode = letterCode.replaceAll("e|E","");
			letterCode = letterCode.replaceAll("v|V","b");
			letterCode = letterCode.charAt(0)+"";
		}
		return letterCode.toUpperCase();
	}
	
	private static String internalTrim(String string){
		string = string.trim();
		boolean building = false;
		String stringTrimmed = "";
		for(Character character : string.toCharArray()){
			if(character != ' '){
				stringTrimmed += character;
				building = true;
			}else{
				if(building==true){
					stringTrimmed += character;
					building = false;
				}
			}
		}
		return stringTrimmed;
	}
	
}
