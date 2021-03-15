package com.github.lukelinkwalker.orchestrator.transformer;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.github.lukelinkwalker.orchestrator.App;
import com.github.lukelinkwalker.orchestrator.Util.Tuple;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SheetTransformer {
	public static String parseSGL(Sheet sheet) {
		JsonArray root = new JsonArray();
		ArrayList<BoundingBox> tables = sheet.getTableRanges();
		
		// Limited to 1 -> tables.size()
		for(int i = 0; i < 1; i += 1) {
			JsonArray entry = new JsonArray();
			
			BoundingBox table = tables.get(i);
		
			int rowStart = table.getY();
			int rowEnd = table.getY() + table.getHeight();
			int columnStart = table.getX();
			int columnEnd = table.getX() + table.getWidth();
			
			for(int row = rowStart; row < rowEnd; row += 1) {
				String prevData = "";
				
				for(int column = columnStart; column < columnEnd; column += 1) {
					if(sheet.getCell(column, row) != null) {
						Cell cell = sheet.getCell(column, row);
						CellData CD = parseCellData(cell.getData(), cell.getColumn(), cell.getRow());

						if(prevData.equals(cell.getData()) == false || CD.getType().equals("type")) {
							if(row == rowStart) {
								entry.add(CD.getAsObject());
							} else {
								JsonObject parent = getJsonParent(entry, sheet, table, i, column, row);
								if(CD.getType().equals("type")) {
									parent.get("dataTypes").getAsJsonArray().add(CD.getAsType());
								} else {
									parent.get("children").getAsJsonArray().add(CD.getAsObject());
								}
							}

							prevData = cell.getData();
						}
											
					}
				}
			}
			
			root.add(entry);
		}
		
		return root.toString().toString();
	}
	
	private static JsonObject findJsonParent(
			ArrayList<ArrayList<Tuple<Integer, JsonObject>>> objectRows,
			ArrayList<Tuple<Integer, JsonObject>> objects,
			int rowIndex,
			int objectIndex,
			int arrLevel
			) {
		
		JsonObject result = null;
		int matchedArrLevel = -1;
		
		// Same line
		for(int object = 0; object < objectIndex; object += 1) {
			if(objects.get(object).getA() == arrLevel || objects.get(object).getA() == (arrLevel - 1)) {
				if(matchedArrLevel == -1) {
					result = objects.get(object).getB();
					matchedArrLevel = objects.get(object).getA();
				} else if(matchedArrLevel < objects.get(object).getA()) {
					result = objects.get(object).getB();
				}
			}
		}
		
		if(result != null) {
			return result;
		}

		// Upper line
		for(int offset = 1; offset < objectRows.size(); offset += 1) {
			if(result != null) {
				break;
			}
			
			if(rowIndex - offset < 0) {
				break;
			}
			
			ArrayList<Tuple<Integer, JsonObject>> higherObjects = objectRows.get(rowIndex - offset);
			
			for(int object = 0; object < higherObjects.size(); object += 1) {
				if(higherObjects.get(object).getA() == arrLevel) {
					break;
				}
				
				if(higherObjects.get(object).getA() == (arrLevel - 1)) {
					result = higherObjects.get(object).getB();
				}
			}
		}
		
		return result;
	}
	
	private static ArrayList<Cell> getCellParents(Sheet sheet, BoundingBox table, int childColumn, int childRow) {
		ArrayList<Cell> result = new ArrayList<>();
		
		for(int row = childRow - 1; row >= table.getY(); row -= 1) {
			Cell tmp = sheet.getCell(childColumn, row);
			
			if(tmp != null) {
				result.add(tmp);
			}
		}
		
		Collections.reverse(result);
		return result;
	}
	
	private static JsonObject getJsonParent(JsonArray root, Sheet sheet, BoundingBox table, int tableIndex, int childColumn, int childRow) {
		JsonObject parent = root.get(tableIndex).getAsJsonObject();
		
		ArrayList<Cell> parents = getCellParents(sheet, table, childColumn, childRow);
		parents.remove(0);
		
		for(int i = 0; i < parents.size(); i += 1) {
			CellData CD = parseCellData(parents.get(i).getData(), parents.get(i).getColumn(), parents.get(i).getRow());
			JsonArray children = parent.get("children").getAsJsonArray();
			JsonObject obj = findJsonObject(children, CD.getName());
			
			if(obj == null) {
				break;
			}
			
			if(!obj.get("type").getAsString().equals("type")) {
				parent = obj;				
			}
		}
		
		return parent;
	}
	
	private static JsonObject findJsonObject(JsonArray arr, String name) {
		JsonObject obj = null;
		
		for(int i = 0; i < arr.size(); i += 1) {
			JsonObject tmp = arr.get(i).getAsJsonObject();
			
			if(tmp.get("name").getAsString().equals(name)) {
				obj = tmp;
				break;
			}
		}
		
		return obj;
	}
	
	private static CellData parseCellData(String input, int column, int row) {
		return new CellData(input, column, row);
	}
	
	private static boolean checkIfExists(int[] arr, int limit, int value) {
		for(int i = 0; i < limit; i += 1) {
			if(arr[i] == value) {
				return true;
			}
		}
		
		return false;
	}
	
	
}
