package com.github.lukelinkwalker.orchestrator.transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.github.lukelinkwalker.orchestrator.Util.FileReader;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class Model {
	private HashMap<String, JsonObj> headerMap = new HashMap<>();
	
	public Model(String path) {
		String input = FileReader.readTextFile(path);

		JsonArray arr = new Gson().fromJson(input, JsonArray.class);
		
		for(int i = 0; i < arr.size(); i += 1) {
			if(!arr.get(i).getAsJsonObject().get("type").getAsString().equals("rules")) {
				JsonObj header = new Gson().fromJson(arr.get(i), JsonObj.class);
				headerMap.put(header.getNameOnly(), header);
			}
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
	
	public boolean isFirstAttribute(String headerIdentifier, int column) {
		List<JsonObj> attributes = getAttributes(headerIdentifier);
		ArrayList<ArrayList<String>> lists = getArrayLayout(headerIdentifier);
		
		ArrayList<String> visited = new ArrayList<>();
		
		String listName = lists.get(column).get(lists.get(column).size() - 1);
		for(int i = 0; i < attributes.size(); i += 1) {
			String tmpListName = lists.get(i).get(lists.get(i).size() - 1);
			
			if(listName.equals(tmpListName) == true) {
				if(i == column) {
					return true;
				} else {
					return false;
				}
			}
		}
		
		return false;
	}
	
	public String getListName(String headerIdentifier, int column) {
		ArrayList<ArrayList<String>> lists = getArrayLayout(headerIdentifier);
		return lists.get(column).get(lists.get(column).size() - 1);
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
		//arrays.add(headerIdentifier);
		
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

	public int getDepth(String header, boolean includeTypes) {
		JsonObj tmp = headerMap.get(header);
		
		if(tmp == null) {
			return -1;
		}
		
		if(includeTypes) {
			return getDepth(headerMap.get(header), 1) + 1;
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