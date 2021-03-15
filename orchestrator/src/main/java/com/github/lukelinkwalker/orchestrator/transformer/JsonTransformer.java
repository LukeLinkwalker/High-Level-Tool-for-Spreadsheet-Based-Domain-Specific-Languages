package com.github.lukelinkwalker.orchestrator.transformer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonTransformer {
	public static Sheet parseJson(String json) {
		JsonObj[] root = new Gson().fromJson(json, JsonObj[].class);
		
		Sheet sheet = new Sheet();
		
		int row = 0;
		for(int i = 0; i < root.length; i += 1) {
			JsonObj table = root[i];
			row += addData(sheet, table, row + getDepth(table, 1), 0, row) + 1;
		}
		
		return sheet;
	}
	
	private static int addData(Sheet sheet, JsonObj obj, int typeRow, int column, int row) {
		int height = 0;
		
		sheet.addData(column, row, getLeafCount(obj), obj.toString());
		
		if(obj.getType().toLowerCase().equals("attribute") || obj.getType().toLowerCase().equals("alternative")) {
			for(int i = 0; i < obj.getDataTypes().length; i += 1) {
				sheet.addData(column, typeRow + i, 1, ("Type : " + obj.getDataTypes()[i]));
			}
		}
		
		if(obj.getChildren().length != 0) {
			height += 1;
		}
		
		int columnSum = 0;
		for(int i = 0; i < obj.getChildren().length; i += 1) {
			if(i > 0) {
				columnSum += getLeafCount(obj.getChildren()[i - 1]);				
			}
			
			addData(sheet, obj.getChildren()[i], typeRow, column + columnSum, row + height);
		}
		
		return height;
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
	
	private static int getDepth(JsonObj obj, int depth) {
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
