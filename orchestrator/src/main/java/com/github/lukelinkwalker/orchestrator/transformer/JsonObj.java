package com.github.lukelinkwalker.orchestrator.transformer;

import java.util.Arrays;

import com.github.lukelinkwalker.orchestrator.Util.StringUtilities;

public class JsonObj {
	private int column;
	private int row;
	private String name;
	private String type;
	private boolean isOptional;
	private JsonObj[] children;
	private JsonType[] dataTypes;
	
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public String getNameOnly() {
		return StringUtilities.tokenStrip(name);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isOptional() {
		return isOptional;
	}
	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}
	public JsonObj[] getChildren() {
		return children;
	}
	public void setChildren(JsonObj[] children) {
		this.children = children;
	}
	public JsonType[] getDataTypes() {
		return dataTypes;
	}
	public void setDataTypes(JsonType[] dataTypes) {
		this.dataTypes = dataTypes;
	}
	
	public String getDataType() {
		if(type.toLowerCase().equals("alternative")) {
			return "alternative";
		}
		
		return dataTypes[0].getValue();
	}
	
	public JsonObj getAsModifiedArray() {
		JsonObj modifiedArray = new JsonObj();
		modifiedArray.setColumn(getColumn());
		modifiedArray.setRow(getRow());
		modifiedArray.setName(StringUtilities.tokenWrap(getNameOnly()));
		modifiedArray.setType("array");
		modifiedArray.setOptional(false);
		
		JsonObj newChild = new JsonObj();
		newChild.setColumn(getColumn());
		newChild.setRow(getRow() + 1);
		newChild.setName(StringUtilities.tokenWrap("Name"));
		newChild.setType("attribute");
		newChild.setOptional(false);
		newChild.setChildren(new JsonObj[] {});
		
		JsonType modifiedJsonType = new JsonType();
		modifiedJsonType.setColumn(getColumn());
		modifiedJsonType.setRow(getRow() + 2);
		modifiedJsonType.setValue("string");
		
		newChild.setDataTypes(new JsonType[] { modifiedJsonType });
		
		modifiedArray.setChildren(new JsonObj[] { newChild });
		
		return modifiedArray;
	}
	
	@Override
	public String toString() {
		return "JsonObj [column=" + column + ", row=" + row + ", name=" + name + ", type=" + type + ", isOptional="
				+ isOptional + ", children=" + Arrays.toString(children) + ", dataTypes=" + Arrays.toString(dataTypes)
				+ "]";
	}
}