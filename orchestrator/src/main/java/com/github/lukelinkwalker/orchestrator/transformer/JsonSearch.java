package com.github.lukelinkwalker.orchestrator.transformer;

import com.github.lukelinkwalker.orchestrator.Util.Tuple;
import com.google.gson.Gson;

public class JsonSearch {
	public static Tuple<Tuple<Integer, Integer>, Tuple<Integer, Integer>> find(String global, int charBegin, int charEnd) {
		Tuple<Tuple<Integer, Integer>, String> error = JsonSearch.find(global, charBegin);
		Tuple<Integer, Integer> positions = JsonSearch.getCharPositions(global, error.getB(), charBegin, charEnd);
		Tuple<Tuple<Integer, Integer>, Tuple<Integer, Integer>> result = new Tuple<>();
		result.setA(error.getA());
		result.setB(positions);
		return result;
	}
	
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
	
	public static Tuple<Integer, Integer> getCharPositions(String global, String local, int charBegin, int charEnd) {
		Tuple<Integer, Integer> result = new Tuple<>();
		result.setA(-1);
		result.setB(-1);
		
		String str = local.toString();
		int localBegin = charBegin - global.indexOf(local);
    	int localEnd = charEnd - global.indexOf(local);

		if(str.contains("name\":")) {
			int valueBegin = str.indexOf("name\":") + 7;
			int valueEnd = valueBegin + str.substring(valueBegin).indexOf("\"");
			
			//System.out.println("Value Begin : " + valueBegin);
	    	//System.out.println("Value End : " + valueEnd);
			
			if(localBegin >= valueBegin && localEnd <= valueEnd - 1) {
				String valueString = str.substring(valueBegin, valueEnd);
				String errorString = local.substring(localBegin, localEnd + 1);
				
				//System.out.println("Value : " + valueString);
				//System.out.println("Error : " + errorString);
				
				int resultBegin = valueString.indexOf(errorString);
				int resultEnd = resultBegin + errorString.length() - 1;
				
				//System.out.println("Result : " + resultBegin + " - " + resultEnd);
				
				result.setA(resultBegin);
				result.setB(resultEnd);
			}
		} 
		else if (str.contains("value\":")) {
			int valueBegin = str.indexOf("value\":") + 8;
			int valueEnd = valueBegin + str.substring(valueBegin).indexOf("\"");
			
	    	//System.out.println("Value Begin : " + valueBegin);
	    	//System.out.println("Value End : " + valueEnd);
			
			if(localBegin >= valueBegin && localEnd <= valueEnd - 1) {
				String valueString = str.substring(valueBegin, valueEnd);
				String errorString = local.substring(localBegin, localEnd + 1);
				
				//System.out.println("Value : " + valueString);
				//System.out.println("Error : " + errorString);
				
				int resultBegin = valueString.indexOf(errorString);
				int resultEnd = resultBegin + errorString.length() - 1;
				
				//System.out.println("Result : " + resultBegin + " - " + resultEnd);
				
				result.setA(resultBegin);
				result.setB(resultEnd);
			}
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
