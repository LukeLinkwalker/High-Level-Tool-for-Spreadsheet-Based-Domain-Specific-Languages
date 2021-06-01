package com.github.lukelinkwalker.orchestrator.transformer;

import com.github.lukelinkwalker.orchestrator.Util.Tuple;
import com.google.gson.Gson;

public class JsonSearch {
	public static JsonSearchResult find(String context, int charBegin, int charEnd) {
		ErrorObject error = JsonSearch.find(context, charBegin);
		ErrorRange range = JsonSearch.getCharPositions(context, error.getJson(), charBegin, charEnd);
		
		JsonSearchResult result = new JsonSearchResult();
		result.setColumn(error.getColumn());
		result.setRow(error.getRow());
		result.setCharBegin(range.getStart());
		result.setCharEnd(range.getEnd());
		
		return result;
	}

	public static ErrorObject find(String JSON, int position) {
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
		
		ErrorObject result = new ErrorObject();
		
		if(start == -1) {
			result.setJson("Empty");
			return result;
		}
		
		String json = JSON.substring(start, end + 1);

		result.setColumn(getFirstColumn(json));
		result.setRow(getFirstRow(json));
		result.setJson(json);
		
		return result;
	}
	
	public static ErrorRange getCharPositions(String context, String object, int charBegin, int charEnd) {
		ErrorRange result = new ErrorRange();
		
		String str = object.toString();
		int localBegin = charBegin - context.indexOf(object);
    	int localEnd = charEnd - context.indexOf(object);

		if(str.contains("name\":")) {
			int valueBegin = str.indexOf("name\":") + 7;
			int valueEnd = valueBegin + str.substring(valueBegin).indexOf("\"");
			
			if(localBegin >= valueBegin && localEnd <= valueEnd - 1) {
				String valueString = str.substring(valueBegin, valueEnd);
				String errorString = object.substring(localBegin, localEnd + 1);
				
				int resultBegin = valueString.indexOf(errorString);
				int resultEnd = resultBegin + errorString.length() - 1;
				
				result.setStart(resultBegin);
				result.setEnd(resultEnd);
			}
		} 
		else if (str.contains("value\":")) {
			int valueBegin = str.indexOf("value\":") + 8;
			int valueEnd = valueBegin + str.substring(valueBegin).indexOf("\"");
			
			if(localBegin >= valueBegin && localEnd <= valueEnd - 1) {
				String valueString = str.substring(valueBegin, valueEnd);
				String errorString = object.substring(localBegin, localEnd + 1);
				
				int resultBegin = valueString.indexOf(errorString);
				int resultEnd = resultBegin + errorString.length() - 1;
				
				result.setStart(resultBegin);
				result.setEnd(resultEnd);
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
