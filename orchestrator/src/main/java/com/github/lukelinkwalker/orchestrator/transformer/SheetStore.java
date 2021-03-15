package com.github.lukelinkwalker.orchestrator.transformer;

import java.util.HashMap;

public class SheetStore {
	// Temp solution
	private static HashMap<String, Sheet> sheets = new HashMap<>();
	
	public static boolean openSheet(String name) {
		if(sheets.containsKey(name)) {
			return false;
		}
		
		sheets.put(name, new Sheet());
		
		return true;
	}
	
	public static Sheet getSheet(String name) {
		return sheets.get(name);
	}
	
	public static Sheet closeSheet(String name) {
		return sheets.remove(name);
	}
}
