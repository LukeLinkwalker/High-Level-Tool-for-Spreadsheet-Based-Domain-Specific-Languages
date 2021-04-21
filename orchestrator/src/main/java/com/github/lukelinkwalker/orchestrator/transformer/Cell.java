package com.github.lukelinkwalker.orchestrator.transformer;

import java.util.ArrayList;

import com.github.lukelinkwalker.orchestrator.Util.StringUtilities;
import com.google.gson.JsonObject;

public class Cell {
	String data;
	int column;
	int row;
	int width;
	boolean isHeader;
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public boolean isHeader() {
		return isHeader;
	}

	public void setHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}

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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public boolean isType(String type) {
		switch(type) {
			//case "alternative":
			//	return true; // Needs proper check
			case "int":
				return StringUtilities.isInteger(data);
			case "float":
				return StringUtilities.isFloat(data);
			//case "string":
			//	return true; // Needs proper check
			case "boolean":
				return StringUtilities.isBoolean(data);
			default:
				return true;
		}
	}
	
	public JsonObject getAsJsonObject(String type) {
		JsonObject result = new JsonObject();
		result.addProperty("column", column);
		result.addProperty("row", row);
		
		switch(type) {
			case "alternative":
				result.addProperty("value", JsonUtil.tokenWrap(data));
				break;
			case "int":
				result.addProperty("value", Integer.parseInt(data));
				break;
			case "float":
				result.addProperty("value", Float.parseFloat(data));
				break;
			case "string":
				result.addProperty("value", JsonUtil.tokenWrap(data));
				break;
			case "boolean":
				result.addProperty("value", Boolean.parseBoolean(data));
				break;
		}
		
		return result;
	}
}