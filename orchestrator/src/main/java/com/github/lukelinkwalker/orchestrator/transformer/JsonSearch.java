package com.github.lukelinkwalker.orchestrator.transformer;

import com.github.lukelinkwalker.orchestrator.Util.Tuple;
import com.google.gson.Gson;

public class JsonSearch {
	public static Tuple<Tuple<Integer, Integer>, String> find(String JSON, int position) {
		int start = -1;
		int end = -1;
		int objectCounter = 0;
		int arrayCounter = 0;
		
		for(int i = position; i > 0; i -= 1) {
			if(JSON.charAt(i) == '}') {
				objectCounter += 1;
			}
			
			if(JSON.charAt(i) == '{') {
				objectCounter -= 1;
				
				if(objectCounter <= 0) {
					start = i;
					break;
				}

			}
		}
		
		objectCounter = 0;
		
		for(int i = start + 1; i < JSON.length(); i += 1) {
			if(end != -1) {
				break;
			}
			
			switch(JSON.charAt(i)) {
				case '{':
					objectCounter += 1;
					break;
				case '[':
					arrayCounter += 1;
					break;
				case ']':
					arrayCounter -= 1;
					break;
				case '}':
					if(objectCounter > 0 || arrayCounter > 0) {
						objectCounter -= 1;
					} else {
						end = i;
					}
					break;
			}
		}
		
		if(start == -1) {
			return new Tuple<>(
					new Tuple<Integer, Integer>(
							0, 
							0
					), 
					"Empty"
				);
		}
		
		String json = JSON.substring(start, end + 1);
		
		return new Tuple<>(
				new Tuple<Integer, Integer>(
						getFirstColumn(json), 
						getFirstRow(json)
				), 
				json
			);
	}
	
	public static Tuple<Integer, Integer> getCharPositions(String obj, int charBegin, int charEnd) {
		Tuple<Integer, Integer> result = new Tuple<>();
		result.setA(-1);
		result.setB(-1);
		
		String str = obj.toString();
		
		if(str.contains("name\":")) {
			System.out.println("name !!!");
		} 
		else if (str.contains("value\":")) {
			System.out.println("value !!!");
		}
		
		return result;
	}
	
	private static int getFirstColumn(String str) {
		if(str.indexOf("\"column\":") == -1) {
			return -1;
		}
		
		String tmp = str.substring(str.indexOf("\"column\":") + 9);
		tmp = tmp.substring(0, tmp.indexOf(','));
		
		return Integer.parseInt(tmp);
	}
	
	private static int getFirstRow(String str) {		
		if(str.indexOf("\"row\":") == -1) {
			return -1;
		}
		
		String tmp = str.substring(str.indexOf("\"row\":") + 6);
		tmp = tmp.substring(0, tmp.indexOf(','));
		
		return Integer.parseInt(tmp);
	}
}
