package com.disoft.distarea.extras;
import java.util.Map;
import android.content.Context;

public class SharedPreferencesTableLoader{
	
	public static final String KEY = "com.disoft.distarea.tableloader";
	private Context context;
	
	public SharedPreferencesTableLoader(Context context) {
		this.context = context;
	}
	
	public Table load() {
		Table table = new StaticTableBuilder().build();
		for(String key : table.getKeys()){
			if(getValue(key)!= null){				
				for(String newDescriptor : getValue(key).split(";")){
					if(table.isKey(key) && !table.isDefinition(key, newDescriptor)) table.addValueToKey(key,newDescriptor);
				}				
			}
		}
		return table;
	}
	
	public boolean setValue(String value,String keyName){
		return context.getSharedPreferences(KEY+".empresa", Context.MODE_PRIVATE).edit().putString(keyName, value).commit();
	}

	private boolean putValue(String value,String keyName){
		String existingValue = getValue(keyName);
		if(existingValue == null){
			existingValue = value;
		}
		else{
			existingValue += ";"+value;
		}
		return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit().putString(keyName, existingValue).commit();
	}
	
	public void putValues(Map<String,String> map){
		Table table = new StaticTableBuilder().build();
		for(String key : map.keySet()){			
			String calibrada = map.get(key);
			key = key.toUpperCase();
			if(table.isKey(key)){
				if(!table.isDefinition(key,calibrada)){
					putValue(calibrada,key);
				}
			}
		}
	}
	
	private String getValue(String keyName){
		return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(keyName, null);
	}
}
