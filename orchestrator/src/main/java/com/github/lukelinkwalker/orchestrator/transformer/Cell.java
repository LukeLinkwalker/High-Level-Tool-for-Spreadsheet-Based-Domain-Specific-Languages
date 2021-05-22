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
		if(JsonUtil.tokenWrapped(type) == true) {
			String cleanType = JsonUtil.tokenStrip(type);
			
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
		if(JsonUtil.tokenWrapped(type)) {
			result.addProperty("value", JsonUtil.tokenWrap(data));
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
						result.addProperty("value", JsonUtil.tokenWrap(data));						
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
				result.addProperty("value", JsonUtil.tokenWrap(data));
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
	
	public JsonObject getAsJsonObject(JsonType[] types) {
		System.out.println("getting as json object : " + types[0]);
		JsonObject result = new JsonObject();
		result.addProperty("column", column);
		result.addProperty("row", row);
		
		//switch(type) {
		//	case "alternative":
		//		if(StringUtilities.isInteger(data)) {
		//			result.addProperty("value", Integer.parseInt(data));
		//		}
		//		else if (StringUtilities.isBoolean(data)) {
		//			result.addProperty("value", Boolean.parseBoolean(data));
		//		}
		//		else if (StringUtilities.isFloat(data)) {
		//			result.addProperty("value", Float.parseFloat(data));
		//		}
		//		else {
		//			result.addProperty("value", JsonUtil.tokenWrap(data));
		//		}
		//		
		//		// String eller custom -> Wrap
		//		// int, boolean, float -> Don't wrap
		//		break;
		//	case "int":
		//		if(this.isType(type) == true) {
		//			result.addProperty("value", Integer.parseInt(data));
		//		} else {
		//			result.addProperty("value", data);
		//		}
		//		break;
		//	case "float":
		//		if(this.isType(type) == true) {
		//			result.addProperty("value", Float.parseFloat(data));
		//		} else {
		//			result.addProperty("value", data);
		//		}
		//		break;
		//	case "string":
		//		result.addProperty("value", JsonUtil.tokenWrap(data));
		//		break;
		//	case "boolean":
		//		if(this.isType(type) == true) {
		//			result.addProperty("value", Boolean.parseBoolean(data));
		//		} else {
		//			result.addProperty("value", data);
		//		}
		//		break;
		//}
		
		//if(isCustomType(type) == true) {
		//	result.addProperty("value", data);
		//}
		
		return result;
	}

	@Override
	public String toString() {
		return "Cell [data=" + data + ", column=" + column + ", row=" + row + ", width=" + width + ", isHeader="
				+ isHeader + "]";
	}
	
	
}