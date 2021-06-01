package com.github.lukelinkwalker.orchestrator.transformer;

import java.util.ArrayList;

import com.github.lukelinkwalker.orchestrator.Util.StringUtilities;
import com.google.gson.JsonNull;
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
	
	public boolean isCustomType(String type) {
		if(StringUtilities.tokenWrapped(type) == true) {
			String cleanType = StringUtilities.tokenStrip(type);
			
			if(
					!cleanType.equals("int") && 
					!cleanType.equals("alternative") && 
					!cleanType.equals("float") && 
					!cleanType.equals("string") && 
					!cleanType.equals("boolean")) 
			{
				return true;
			}
		}
		
		return false;
	}
	
	public JsonObject getAsJsonObject(String type) {
		System.out.println("getting as json object : " + type);
		JsonObject result = new JsonObject();
		result.addProperty("column", column);
		result.addProperty("row", row);
		
		if(StringUtilities.isNull(data)) {
			result.add("value", JsonNull.INSTANCE);
			return result;
		}

		// Temp hax - rules & references
		if(StringUtilities.tokenWrapped(type)) {
			result.addProperty("value", StringUtilities.tokenWrap(data));
			return result;
		}
		
		switch(type) {
			case "alternative":
				if(StringUtilities.isInteger(data)) {
					result.addProperty("value", Integer.parseInt(data));
				}
				else if (StringUtilities.isBoolean(data)) {
					result.addProperty("value", Boolean.parseBoolean(data));
				}
				else if (StringUtilities.isFloat(data)) {
					result.addProperty("value", Float.parseFloat(data));
				}
				else {
					if(isCustomType(type) == true) {
						result.addProperty("value", String.valueOf(data));
					} else {
						result.addProperty("value", StringUtilities.tokenWrap(data));						
					}
				}
				
				// String eller custom -> Wrap
				// int, boolean, float -> Don't wrap
				break;
			case "int":
				if(this.isType(type) == true) {
					result.addProperty("value", Integer.parseInt(data));
				} else {
					result.addProperty("value", data);
				}
				break;
			case "float":
				if(this.isType(type) == true) {
					result.addProperty("value", Float.parseFloat(data));
				} else {
					result.addProperty("value", data);
				}
				break;
			case "string":
				result.addProperty("value", StringUtilities.tokenWrap(data));
				break;
			case "boolean":
				if(this.isType(type) == true) {
					result.addProperty("value", Boolean.parseBoolean(data));
				} else {
					result.addProperty("value", data);
				}
				break;
			default:
				result.addProperty("value", String.valueOf(data));
				break;
		}
		
		return result;
	}
	
	public JsonObject getAsJsonObject(JsonObj attribute) {
		System.out.println("getting as json object : " + attribute.getDataType());
		System.out.println("type : " + attribute.getType());
		
		JsonObject result = new JsonObject();
		result.addProperty("column", column);
		result.addProperty("row", row);
		
		if(attribute.getType().equals("attribute")) {
			// Attribute
			
			JsonType attributeType = attribute.getDataTypes()[0];

			System.out.println("Attribute type : " + attributeType.getType());
			System.out.println("Attribute value : " + attributeType.getValue());
			
			if(attributeType.getType().equals("reference")) {
				// Reference
				result.addProperty("value", StringUtilities.tokenWrap(data));
			} else {
				// Predefined or custom
				if(attributeType.getValue().equals("int")) {
					if(StringUtilities.isInteger(data)) {
						result.addProperty("value", Integer.parseInt(data));
					} else {
						result.addProperty("value", data);
					}
				}
				else if (attributeType.getValue().equals("float")) {
					if (StringUtilities.isFloat(data)) {
						result.addProperty("value", Float.parseFloat(data));
					} else {
						result.addProperty("value", data);
					}
				}
				else if (attributeType.getValue().equals("boolean")) {
					if(StringUtilities.isBoolean(data)) {
						result.addProperty("value", Boolean.parseBoolean(data));
					} else {
						result.addProperty("value", data);
					}
				}
				else if (attributeType.getValue().equals("null")) {
					if (StringUtilities.isNull(data)) {
						result.add("value", JsonNull.INSTANCE);
					} else {
						result.addProperty("value", data);
					}
				}
				else if (attributeType.getValue().equals("String")) {
					result.addProperty("value", StringUtilities.tokenWrap(data));
				}
				else {
					// Rule
					result.addProperty("value", data);
				}
			}
		} else {
			// Alternative
			if(StringUtilities.isInteger(data)) {
				result.addProperty("value", Integer.parseInt(data));
			}
			else if (StringUtilities.isBoolean(data)) {
				result.addProperty("value", Boolean.parseBoolean(data));
			}
			else if (StringUtilities.isFloat(data)) {
				result.addProperty("value", Float.parseFloat(data));
			}
			else if (StringUtilities.isNull(data)) {
				result.add("value", JsonNull.INSTANCE);
			}
			else {
				result.addProperty("value", data);
			}
		}
		
		return result;
	}

	@Override
	public String toString() {
		return "Cell [data=" + data + ", column=" + column + ", row=" + row + ", width=" + width + ", isHeader="
				+ isHeader + "]";
	}
	
	
}