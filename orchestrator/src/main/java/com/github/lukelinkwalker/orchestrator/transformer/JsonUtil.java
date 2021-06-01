package com.github.lukelinkwalker.orchestrator.transformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonUtil {
	public static JsonObject find(JsonArray arr, String memberName, String value) {
		for(int index = 0; index < arr.size(); index += 1) {
			JsonObject obj = arr.get(index).getAsJsonObject();
			if(obj.get(memberName).getAsString().equals(value)) {
				return obj;
			}
		}
		
		return null;
	}
	
	public static int indexOf(JsonArray arr, String memberName, String value) {
		for(int index = 0; index < arr.size(); index += 1) {
			JsonObject obj = arr.get(index).getAsJsonObject();
			if(obj.get(memberName).getAsString().equals(value)) {
				return index;
			}
		}
		
		return -1;
	}
}
