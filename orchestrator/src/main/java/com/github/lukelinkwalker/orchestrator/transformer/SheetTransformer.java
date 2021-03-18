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
		
		for(int i = 0; i < tables.size(); i += 1) {
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
	
	public static String parseSDSL(Sheet sheet) {
		JsonArray root = new JsonArray();
		ArrayList<BoundingBox> tables = sheet.getTableRanges();
		
		for(int i = 0; i < tables.size(); i += 1) {
			BoundingBox table1 = tables.get(i);
			String tableName = sheet.getCell(table1.getX(), table1.getY()).getData();
			//System.out.println("Processing: " + table1.toString());
			//System.out.println("Table name: " + tableName);
			
			// Put out error due to skip?
			if(App.M.checkIfExists(tableName) == false) {
				continue;
			}
			
			int depth = App.M.getDepth(tableName);
			
			JsonObject base = new JsonObject();
			JsonArray table = new JsonArray();
			base.addProperty("Name", tableName);
			base.add("Table", table);
			root.add(base);
			
			HashMap<String, JsonObject> mapOfObjects = new HashMap<>();
			
			// Needs to be updated to get from header dynamically
			int[] arrLevels = new int[] { 0, 1, 2, 2, 0 };
			HashMap<Integer, String> lists = new HashMap<>();
			lists.put(1, "Sensors");
			lists.put(2, "Outputs");
			
			int rowStart = table1.getY() + depth;
			int rowEnd = table1.getY() + table1.getHeight();
			int columnStart = table1.getX();
			int columnEnd = table1.getX() + table1.getWidth();
			
			// Organize cell data
			ArrayList<ArrayList<Tuple<Integer, ArrayList<CellData>>>> rows = new ArrayList<>();
			for(int row = rowStart; row < rowEnd; row += 1) {
				ArrayList<Tuple<Integer, ArrayList<CellData>>> rowEntries = new ArrayList<>();
				ArrayList<CellData> currentEntry = new ArrayList<>();
				int currentDepth = -1;
				
				for(int column = columnStart; column < columnEnd; column += 1) {
					if(sheet.getCell(column, row) != null) {
						Cell cell = sheet.getCell(column, row);
						CellData CD = parseCellData(cell.getData(), cell.getColumn(), cell.getRow());
						// Needs to be dynamic -> use tableName
						CD.setCellName(App.M.getAttribute(tableName, column).getName());
						
						if(currentDepth == -1 || currentDepth == arrLevels[column]) {
							currentEntry.add(CD);
							currentDepth = arrLevels[column];
						} else {
							rowEntries.add(new Tuple<>(currentDepth, currentEntry));
							currentDepth = arrLevels[column];
							currentEntry = new ArrayList<CellData>();
							currentEntry.add(CD);
						}
					}
				}
				
				rowEntries.add(new Tuple<>(currentDepth, currentEntry));
				rows.add(rowEntries);
				
				//System.out.println("Row entries: " + rowEntries.size());
				//for(int k = 0; k < rowEntries.size(); k += 1) {
					//System.out.println("Entry " + k + " : " + rowEntries.get(k).getA() + " - " + rowEntries.get(k).getB().size());
				//}
			}
			
			// Convert to JSON objects
			ArrayList<ArrayList<Tuple<Integer, JsonObject>>> objectRows = new ArrayList<>();
						
			for(int row = 0; row < rows.size(); row += 1) {
				ArrayList<Tuple<Integer, ArrayList<CellData>>> entries = rows.get(row);
				ArrayList<Tuple<Integer, JsonObject>> objects = new ArrayList<>();
				
				for(int entry = 0; entry < entries.size(); entry += 1) {
					Tuple<Integer, ArrayList<CellData>> object = entries.get(entry);
					
					JsonObject obj = new JsonObject();
					
					for(int cell = 0; cell < object.getB().size(); cell += 1) {
						// Needs to be dynamic "Config" -> tableName
						JsonObj attr = App.M.getAttribute(tableName, object.getB().get(cell).getCellName());
						//System.out.println(attr.getName() + " -> " + attr.getDataType());
						
						JsonObject tmp = new JsonObject();
						tmp.addProperty("column", object.getB().get(cell).getColumn());
						tmp.addProperty("row", object.getB().get(cell).getRow());
						
						switch(attr.getDataType()) {
							case "alternative":
								tmp.addProperty("value", "'" + object.getB().get(cell).getName() + "'");
								break;
							case "Int":
								tmp.addProperty("value", Integer.parseInt(object.getB().get(cell).getName()));
								break;
							case "String":
								tmp.addProperty("value", "'" + object.getB().get(cell).getName() + "'");
								break;
							case "Boolean":
								tmp.addProperty("value", Boolean.parseBoolean(object.getB().get(cell).getName()));
								break;
						}
						
						obj.add(object.getB().get(cell).getCellName(), tmp);
					}
					
					objects.add(new Tuple<>(object.getA(), obj));
				}
				
				
				objectRows.add(objects);
			}
			
			// Merge JSON objects
			for(int row = 0; row < objectRows.size(); row += 1) {
				ArrayList<Tuple<Integer, JsonObject>> objects = objectRows.get(row);
				//System.out.println("Row: " + row + "  (" + objects.size() + ")");
				
				for(int object = 0; object < objects.size(); object += 1) {
					Tuple<Integer, JsonObject> jsonEntry = objects.get(object);
					// Store to handle objects at same level ?
					JsonObject parent = findJsonParent(objectRows, objects, row, object, objects.get(object).getA());
					//System.out.println(jsonEntry.getA() + " : " + jsonEntry.getB().toString().toString() + " with " + parent);
					
					
					if(parent != null) {
						if(jsonEntry.getA() == 0) {
							for(String key : jsonEntry.getB().keySet()) {
								parent.add(key, jsonEntry.getB().get(key));
							}
						} else {
							String listKey = lists.get(jsonEntry.getA());
							JsonArray arr = null;
							
							if(parent.get(listKey) == null) {
								parent.add(listKey, new JsonArray());
							}
							
							arr = parent.get(listKey).getAsJsonArray();
							arr.add(jsonEntry.getB());
						}
					} else {
						table.add(jsonEntry.getB());
					}
				}
			}
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
