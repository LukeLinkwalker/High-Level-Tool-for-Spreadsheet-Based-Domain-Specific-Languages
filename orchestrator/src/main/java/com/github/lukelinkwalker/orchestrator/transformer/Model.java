package com.github.lukelinkwalker.orchestrator.transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.github.lukelinkwalker.orchestrator.Util.FileReader;
import com.google.gson.Gson;

public class Model {
	private HashMap<String, JsonObj> headerMap = new HashMap<>();
	
	public Model(String path) {
		String input = FileReader.readTextFile(path);

		// Remove first '['
		for(int i = 0; i < input.length(); i += 1) {
			if(input.charAt(i) == '[') {
				input = input.substring(i + 1);
				break;
			}
		}
		
		// Remove last ']'
		for(int i = input.length() - 1; i > 0; i -= 1) {
			if(input.charAt(i) == ']') {
				input = input.substring(0, i);
				break;
			}
		}
		
		// Split headers
		ArrayList<String> parts = new ArrayList<>();
		int prev = 0;
		int curr = 0;
		int count = 0;
		for(int i = 0; i < input.length(); i += 1) {
			curr = i;
			
			if(input.charAt(i) == '[') {
				count += 1;
			}
			
			if(input.charAt(i) == ']') {
				count -= 1;
			}
			
			if(input.charAt(i) == ',' && count == 0) {
				parts.add(input.substring(prev, curr));
				prev = i + 1;
			}
		}
		parts.add(input.substring(prev, curr + 1));
		
		// Store headers
		for(int i = 0; i < parts.size(); i += 1) {
			JsonObj[] header = new Gson().fromJson(parts.get(i), JsonObj[].class);
			headerMap.put(header[0].getName(), header[0]);
		}
	}
	
	public JsonObj getAttribute(String headerIdentifier, int index) {
		List<JsonObj> attributes = getAttributes(headerIdentifier);
		
		if(index > attributes.size()) {
			return null;
		} else {
			return attributes.get(index);
		}
	}
	
	public JsonObj getAttribute(String headerIdentifier, String attributeIdentifier) {
		List<JsonObj> attributes = getAttributes(headerIdentifier);
		
		for(int i = 0; i < attributes.size(); i += 1) {
			if(attributes.get(i).getName().equals(attributeIdentifier)) {
				return attributes.get(i);
			}
		}
		
		return null;
	}
	
	public List<JsonObj> getAttributes(String headerIdentifier) {
		ArrayList<JsonObj> attributes = new ArrayList<>();
		
		if(headerMap.containsKey(headerIdentifier) == false) {
			return null;
		}
		
		JsonObj root = headerMap.get(headerIdentifier);
		
		attributes = getAttributes(root);
		
		return Collections.unmodifiableList(attributes);
	}
	
	private ArrayList<JsonObj> getAttributes(JsonObj root) {
		ArrayList<JsonObj> result = new ArrayList<>();
		
		if(root.getType().equals("attribute") || root.getType().equals("alternative")) {
			result.add(root);
		}
		
		if(root.getChildren() != null && root.getChildren().length > 0) {
			for(JsonObj header : root.getChildren()) {
				result.addAll(getAttributes(header));
			}
		}
		
		return result;
	}
	
	private ArrayList<ArrayList<String>> getArrayLayout(JsonObj root) {
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		
		for(int i = 0; i < root.getChildren().length; i += 1) {
			
		}
		
		return result;
	}
	
	private static int getLeafCount(JsonObj obj) {
		int count = 0;
		
		JsonObj[] children = obj.getChildren();
		
		if(children.length == 0) {
			return 1;
		}
		
		for(int i = 0; i < children.length; i += 1) {
			count += getLeafCount(children[i]);
		}
		
		return count;
	}
	
	public int getDepth(String header) {
		JsonObj tmp = headerMap.get(header);
		
		if(tmp == null) {
			return -1;
		}
		
		return getDepth(headerMap.get(header), 0);
	}
	
	private int getDepth(JsonObj obj, int depth) {
		int maxDepth = 0;
		
		if(obj.getChildren().length == 0) {
			return depth;
		}
		
		for(int i = 0; i < obj.getChildren().length; i += 1) {
			int tmpDepth = getDepth(obj.getChildren()[i], depth + 1);
			if(tmpDepth > maxDepth) {
				maxDepth = tmpDepth;
			}
		}
		
		return maxDepth;
	}
}
