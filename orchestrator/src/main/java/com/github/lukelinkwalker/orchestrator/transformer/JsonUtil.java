package com.github.lukelinkwalker.orchestrator.transformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonUtil {
	private static String tokenStart = "OKZVVTSPKHOVYSMU";
	private static String tokenStop = "SQPSUQMWUPQSBXDT";
	
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
	
	public static String tokenWrap(String str) {
		return (tokenStart + str + tokenStop);
	}
	
	public static String tokenStrip(String str) {
		return str.replace(tokenStart, "").replace(tokenStop, "");
	}
}
