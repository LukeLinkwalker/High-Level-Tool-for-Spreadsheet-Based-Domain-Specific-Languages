package com.github.lukelinkwalker.orchestrator.transformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CellData {
	private String name = "";
	private String type = "";
	private boolean isOptional = false;
	private int column = 0;
	private int row = 0;
	private String cellName = "";
	
	public CellData(String input, int column, int row) {
		this.column = column;
		this.row = row;
		
		String[] parts = input.split(" ");
		
		int splitterIndex = parts.length - 2;
		
		name = parts[splitterIndex + 1];
		
		for(int i = 0; i < splitterIndex; i += 1) {
			if(parts[i].toLowerCase().equals("optional")) {
				isOptional = true;
			} else {
				type = parts[i].toLowerCase();					
			}
		}
	}

	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean isOptional() {
		return isOptional;
	}

	public String getCellName() {
		return cellName;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}

	@Override
	public String toString() {
		return "CellData [name=" + name + ", type=" + type + ", isOptional=" + isOptional + "]";
	}
	
	public JsonObject getAsObject() {
		JsonObject obj = new JsonObject();
		obj.addProperty("column", column);
		obj.addProperty("row", row);
		obj.addProperty("name", name);
		obj.addProperty("type", type.toLowerCase());
		obj.addProperty("isOptional", isOptional);
		obj.add("children", new JsonArray());
		obj.add("dataTypes", new JsonArray());
		return obj;
	}
	
	public JsonObject getAsType() {
		JsonObject obj = new JsonObject();
		obj.addProperty("column", column);
		obj.addProperty("row", row);
		obj.addProperty("value", name);
		return obj;
	}
}