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

		JsonObj[] headers = new Gson().fromJson(input, JsonObj[].class);
		for(JsonObj header : headers) {
			headerMap.put(header.getName(), header);
		}
	}
	
	public boolean checkIfExists(String headerIdentifier) {
		return headerMap.containsKey(headerIdentifier);
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
	
	public ArrayList<JsonObj> getAttributes(JsonObj root) {
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
	
	public ArrayList<ArrayList<String>> getArrayLayout(String headerIdentifier) {
		ArrayList<ArrayList<String>> attributes = new ArrayList<>();
		
		if(headerMap.containsKey(headerIdentifier) == false) {
			return null;
		}
		
		ArrayList<String> arrays = new ArrayList<>();
		arrays.add(headerIdentifier);
		
		JsonObj root = headerMap.get(headerIdentifier);
		
		attributes = getArrayLayout(root, copyList(arrays));
		
		return attributes;
	}
	
	private ArrayList<ArrayList<String>> getArrayLayout(JsonObj root, ArrayList<String> arrays) {
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		
		if(root.getType().equals("array")) {
			arrays.add(root.getName());
		}
		
		if(root.getType().equals("attribute") || root.getType().equals("alternative")) {
			result.add(arrays);
		}
		
		if(root.getChildren() != null && root.getChildren().length > 0) {
			for(JsonObj header : root.getChildren()) {
				result.addAll(getArrayLayout(header, copyList(arrays)));
			}
		}
		
		return result;
	}
	
	public JsonObj getArray(String headerIdentifier, String arrayIdentifier) {
		JsonObj root = headerMap.get(headerIdentifier);
		
		for(JsonObj obj : root.getChildren()) {
			JsonObj tmp = getArray(obj, arrayIdentifier);
			
			if(tmp != null) {
				return tmp;
			}
		}
		
		return null;
	}
	
	private JsonObj getArray(JsonObj root, String arrayIdentifier) {
		if(root.getType().equals("array") && root.getName().equals(arrayIdentifier)) {
			return root;
		}
		
		for(JsonObj obj : root.getChildren()) {
			JsonObj tmp = getArray(obj, arrayIdentifier);
			
			if(tmp != null) {
				return tmp;
			}
		}
		
		return null;
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
		
		return getDepth(headerMap.get(header), 1);
	}
	
	public int getDepthWithTypes(String header) {
		JsonObj tmp = headerMap.get(header);
		
		if(tmp == null) {
			return -1;
		}
		
		return getDepth(headerMap.get(header), 1);
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
	
	private ArrayList<String> copyList(ArrayList<String> list) {
		ArrayList<String> copy = new ArrayList<>();
		
		for(String str : list) {
			copy.add(str);
		}
		
		return copy;
	}
}