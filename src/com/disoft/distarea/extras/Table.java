package com.disoft.distarea.extras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Table {

	private Map<String, ArrayList<String>> map;
	
	public Table(){
		map = new HashMap<String, ArrayList<String>>();
	}
	
	public Table(Map<String, ArrayList<String>> map){
		this.map = map;
	}
	
	public Map getMap(){
		return map;
	}
	
	public void addKey(String key){
		map.put(key, new ArrayList<String>());
	}
	
	public Set<String> getKeys(){
		return map.keySet();
	}
	
	
	public boolean addValueToKey(String key,String value){
		return map.get(key).add(value);
	}
	
	public ArrayList getDefinitionsByWord(String keyWord){
		return map.get(keyWord);
	}
	
	public String getRegExpByKey(String keyWord){
		String regexp = keyWord;
		for(String s : map.get(keyWord)){
			regexp = regexp+"|"+s;
		}
		return regexp;
	}
	
	public String getRegExpDifferentByKeyWord(String keyWord){
		String regexp = "TTTTTTTT";
		for(String key : map.keySet()){
			if(!key.equals(keyWord)){
				for(String value : map.get(key)){
					regexp = regexp+"|"+value;
				}
			}
		}
		return regexp;
	}
	
	public boolean isDefinition(String keyWord, String definition){
		if(map.get(keyWord)!= null){
			return map.get(keyWord).contains(definition);
		}
		return false;
	}
	
	public boolean isKey(String keyWord){
		return map.containsKey(keyWord);
	}
	
	public String toString(){
		String toString = "";
		for(String key : map.keySet()){
			toString += getRegExpByKey(key)+"\n";
		}
		return toString;
	}
	
}
