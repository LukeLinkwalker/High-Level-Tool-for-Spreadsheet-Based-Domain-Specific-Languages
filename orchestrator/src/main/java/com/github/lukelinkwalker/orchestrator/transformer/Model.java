package com.github.lukelinkwalker.orchestrator.transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.github.lukelinkwalker.orchestrator.Util.FileReader;
import com.github.lukelinkwalker.orchestrator.Util.StringUtilities;
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
		
		attributes = getAttributes(root, new ArrayList<String>());
		
		return Collections.unmodifiableList(attributes);
	}
	
	public List<JsonObj> getAttributes(String headerIdentifier, ArrayList<String> excluded) {
		ArrayList<JsonObj> attributes = new ArrayList<>();
		
		if(headerMap.containsKey(headerIdentifier) == false) {
			return null;
		}
		
		JsonObj root = headerMap.get(headerIdentifier);
		
		attributes = getAttributes(root, excluded);
		
		return Collections.unmodifiableList(attributes);
	}
	
	public ArrayList<JsonObj> getAttributes(JsonObj root, ArrayList<String> excluded) {
		ArrayList<JsonObj> result = new ArrayList<>();
		
		if(root.getType().equals("attribute") || root.getType().equals("alternative")) {
			result.add(root);
		}
		
		if(excluded == null) {
			excluded = new ArrayList<>();
		}
		
		if(root.getChildren() != null && root.getChildren().length > 0) {
			for(JsonObj header : root.getChildren()) {
				if(excluded.contains(header.getNameOnly()) == false) {
					result.addAll(getAttributes(header, excluded));					
				} else {
					JsonObj newHeader = header.getAsModifiedArray();
					result.addAll(getAttributes(newHeader, excluded));
				}
			}
		}
		
		return result;
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
	
	public boolean isFirstAttribute(List<JsonObj> attributes, ArrayList<ArrayList<String>> arrays, int column) {
		ArrayList<String> visited = new ArrayList<>();
		
		String listName = arrays.get(column).get(arrays.get(column).size() - 1);
		for(int i = 0; i < attributes.size(); i += 1) {
			String tmpListName = arrays.get(i).get(arrays.get(i).size() - 1);
			
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

	public ArrayList<ArrayList<String>> getArrayLayout(String headerIdentifier) {
		ArrayList<ArrayList<String>> attributes = new ArrayList<>();
		
		if(headerMap.containsKey(headerIdentifier) == false) {
			return null;
		}
		
		ArrayList<String> arrays = new ArrayList<>();
		
		JsonObj root = headerMap.get(headerIdentifier);
		
		attributes = getArrayLayout(root, copyList(arrays), new ArrayList<String>());
		
		return attributes;
	}
	
	public ArrayList<ArrayList<String>> getArrayLayout(JsonObj root) {
		ArrayList<ArrayList<String>> attributes = new ArrayList<>();

		ArrayList<String> arrays = new ArrayList<>();

		attributes = getArrayLayout(root, copyList(arrays), new ArrayList<String>());
		
		return attributes;
	}
	
	public ArrayList<ArrayList<String>> getArrayLayout(String headerIdentifier, ArrayList<String> excluded) {
		ArrayList<ArrayList<String>> attributes = new ArrayList<>();
		
		if(headerMap.containsKey(headerIdentifier) == false) {
			return null;
		}
		
		ArrayList<String> arrays = new ArrayList<>();
		//arrays.add(headerIdentifier);
		
		JsonObj root = headerMap.get(headerIdentifier);
		
		attributes = getArrayLayout(root, copyList(arrays), excluded);
		
		return attributes;
	}
	
	private ArrayList<ArrayList<String>> getArrayLayout(JsonObj root, ArrayList<String> arrays, ArrayList<String> excluded) {
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		
		if(root.getType().equals("array")) {
			arrays.add(root.getName());
		}
		
		if(root.getType().equals("attribute") || root.getType().equals("alternative")) {
			result.add(arrays);
		}
		
		if(root.getChildren() != null && root.getChildren().length > 0) {
			for(JsonObj header : root.getChildren()) {
				if(excluded.contains(header.getNameOnly()) == false) {
					result.addAll(getArrayLayout(header, copyList(arrays), excluded));
				} else {
					JsonObj newHeader = header.getAsModifiedArray();
					result.addAll(getArrayLayout(newHeader, copyList(arrays), excluded));
				}
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
		if(root.getType().equals("array") && root.getNameOnly().equals(arrayIdentifier)) {
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

	public int getDepth(JsonObj root, boolean includeTypes, ArrayList<String> excluded) {
		if(root == null) {
			return -1;
		}
		
		if(excluded == null) {
			excluded = new ArrayList<>();
		}
		
		if(includeTypes) {
			return getDepth(root, 1, excluded) + 1;
		}

		return getDepth(root, 1, excluded);
	}
	
	public int getDepth(String header, boolean includeTypes, ArrayList<String> excluded) {
		JsonObj tmp = headerMap.get(header);
		
		if(tmp == null) {
			return -1;
		}
		
		if(excluded == null) {
			excluded = new ArrayList<>();
		}
		
		if(includeTypes) {
			return getDepth(headerMap.get(header), 1, excluded) + 1;
		}

		return getDepth(headerMap.get(header), 1, excluded);
	}
	
	private int getDepth(JsonObj obj, int depth, ArrayList<String> excluded) {
		int maxDepth = 0;
		
		if(obj.getChildren().length == 0) {
			return depth;
		}
		
		for(int i = 0; i < obj.getChildren().length; i += 1) {
			JsonObj child = obj.getChildren()[i];
			
			if(excluded.contains(child.getNameOnly()) == false) {
				int tmpDepth = getDepth(child, depth + 1, excluded);
				if(tmpDepth > maxDepth) {
					maxDepth = tmpDepth;
				}
			} else {
				JsonObj newChild = child.getAsModifiedArray();
				int tmpDepth = getDepth(newChild, depth + 1, excluded);
				if(tmpDepth > maxDepth) {
					maxDepth = tmpDepth;
				}
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