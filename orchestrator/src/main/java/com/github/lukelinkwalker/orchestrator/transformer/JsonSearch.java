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
			if(JSON.charAt(i) == '{') {
				start = i;
				break;
			}
		}
		
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

		JsonObj obj = new Gson().fromJson(JSON.substring(start, end + 1), JsonObj.class);
		Tuple<Integer, Integer> posResult = new Tuple<>(obj.getColumn(), obj.getRow());
		
		
		return new Tuple<>(posResult, JSON.substring(start, end + 1));
	}
}
