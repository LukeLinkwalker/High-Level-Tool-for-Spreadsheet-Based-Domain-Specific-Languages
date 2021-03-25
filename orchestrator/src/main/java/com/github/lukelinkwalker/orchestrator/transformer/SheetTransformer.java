package com.github.lukelinkwalker.orchestrator.transformer;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.github.lukelinkwalker.orchestrator.App;
import com.github.lukelinkwalker.orchestrator.Util.Tuple;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SheetTransformer {
	public static String parseSGL(Sheet sheet) {
		JsonArray root = new JsonArray();
		ArrayList<BoundingBox> tables = sheet.getTableRanges();
		
		for(int i = 0; i < tables.size(); i += 1) {
			JsonObject entry = new JsonObject();
			
			BoundingBox table = tables.get(i);
		
			int rowStart = table.getY();
			int rowEnd = table.getY() + table.getHeight();
			int columnStart = table.getX();
			int columnEnd = table.getX() + table.getWidth();
			
			Cell firstCell = sheet.getCell(table.getX(), table.getY());
			CellData firstCellData = parseCellData(firstCell.getData(), firstCell.getColumn(), firstCell.getRow());
			String tableName = firstCellData.getName();
			
			if(tableName.toLowerCase().equals("customrule") || tableName.toLowerCase().equals("customtype")) {
				entry.addProperty("column", columnStart);
				entry.addProperty("row", rowStart);
				entry.addProperty("type", tableName);
				entry.add("children", new JsonArray());
			}
			
			if(tableName.toLowerCase().equals("customrule")) {
				for(int row = rowStart + 2; row < rowEnd; row += 1) {
					Cell name = sheet.getCell(columnStart, row);
					Cell rule = sheet.getCell(columnStart + 1, row);
					
					if(name != null && rule != null) {
						JsonObject customrule = new JsonObject();
						
						JsonObject nameObj = new JsonObject();
						nameObj.addProperty("column", columnStart);
						nameObj.addProperty("row", row);
						nameObj.addProperty("value", "'" + name.getData() + "'");

						JsonObject ruleObj = new JsonObject();
						ruleObj.addProperty("column", columnStart + 1);
						ruleObj.addProperty("row", row);
						ruleObj.addProperty("value", "'" + rule.getData() + "'");
						
						customrule.add("name", nameObj);
						customrule.add("rule", ruleObj);
						
						entry.get("children").getAsJsonArray().add(customrule);
					}
				}
			} else if (tableName.toLowerCase().equals("customtype")) {
				JsonObject customtype = null;
				for(int row = rowStart + 2; row < rowEnd; row += 1) {
					Cell name = sheet.getCell(columnStart, row);
					Cell type = sheet.getCell(columnStart + 1, row);
					
					if(name == null) {
						if(type != null) {
							JsonObject subtype = new JsonObject();
							subtype.addProperty("column", columnStart + 1);
							subtype.addProperty("row", row);
							subtype.addProperty("value", "'" + type.getData() + "'");
							customtype.get("subtypes").getAsJsonArray().add(subtype);
						}
					} else {
						if(customtype != null) {
							if(customtype.get("subtypes").getAsJsonArray().size() > 0) {
								entry.get("children").getAsJsonArray().add(customtype);
							}
						}
						
						customtype = new JsonObject();
						customtype.addProperty("column", columnStart);
						customtype.addProperty("row", row);
						customtype.addProperty("name", "'" + name.getData() + "'");
						customtype.add("subtypes", new JsonArray());
						
						JsonObject subtype = new JsonObject();
						subtype.addProperty("column", columnStart + 1);
						subtype.addProperty("row", row);
						subtype.addProperty("value", "'" + type.getData() + "'");
						
						customtype.get("subtypes").getAsJsonArray().add(subtype);
					}
					
					if(row == (rowEnd - 1)) {
						entry.get("children").getAsJsonArray().add(customtype);
					}
				}
			} else {
				for(int row = rowStart; row < rowEnd; row += 1) {
					String prevData = "";
					
					for(int column = columnStart; column < columnEnd; column += 1) {
						if(sheet.getCell(column, row) != null) {
							Cell cell = sheet.getCell(column, row);
							CellData CD = parseCellData(cell.getData(), cell.getColumn(), cell.getRow());
							
							if(prevData.equals(cell.getData()) == false || CD.getType().equals("type")) {
								if(row == rowStart) {
									entry = CD.getAsObject();
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
			System.out.println("Processing: " + table1.toString());
			//System.out.println("Table name: " + tableName);
			
			// Put out error due to skip? - Yes. Todo
			if(App.M.checkIfExists(tableName) == false) {
				continue;
			}
			
			JsonObject base = new JsonObject();
			JsonArray table = new JsonArray();
			base.addProperty("Name", tableName);
			base.add("Table", table);
			root.add(base);
			
			List<JsonObj> attributes = App.M.getAttributes(tableName);
			System.out.println("Attributes : " + attributes.size());
			
			int rowStart = table1.getY() + App.M.getDepth(tableName);
			int rowEnd = table1.getY() + table1.getHeight();
			int columnStart = table1.getX();
			int columnEnd = table1.getX() + table1.getWidth();
			
			ArrayList<ArrayList<String>> arrLayout = App.M.getArrayLayout(tableName);
			
			// Order cell data
			ArrayList<ArrayList<Tuple<Integer, CellData>>> orderedCellData = new ArrayList<>();
			for(int row = rowStart; row < rowEnd; row += 1) {
				ArrayList<Tuple<Integer, CellData>> CDs = new ArrayList<>();
				
				for(int column = columnStart; column < columnEnd; column += 1) {
					Cell cell = sheet.getCell(column, row);
					
					if(cell != null) {
						CellData cellData = parseCellData(cell.getData(), cell.getColumn(), cell.getRow());
						cellData.setCellName(attributes.get(column).getName());
						CDs.add(new Tuple(column, cellData));
						System.out.println(cellData.getColumn() + " | " + cellData.getRow() + " -> " + cellData.getName() + " in " + arrLayout.get(column).get(arrLayout.get(column).size() - 1));						
					}
				}
				
				orderedCellData.add(CDs);
			}
			
			// Create JSON objects
			ArrayList<JsonObject> allObjects = new ArrayList<>();
			ArrayList<ArrayList<JsonObject>> orderedJsonObjects = new ArrayList<>();
			for(int row = 0; row < orderedCellData.size(); row += 1) {
				ArrayList<Tuple<Integer, CellData>> CDs = orderedCellData.get(row);
				ArrayList<JsonObject> Objects = new ArrayList<>();
				
				for(int cell = 0; cell < CDs.size(); cell += 1) {
					Tuple<Integer, CellData> CD = CDs.get(cell);
					
					JsonObject value = new JsonObject();
					value.addProperty("column", CD.getB().getColumn());
					value.addProperty("row", CD.getB().getRow());
					value.addProperty("value", CD.getB().getName());
					
					JsonObject container = new JsonObject();
					container.add(App.M.getAttribute(tableName, CD.getA()).getName(), value);
					
					System.out.println(value);
					
					Objects.add(value);
					allObjects.add(value);
				}
				
				orderedJsonObjects.add(Objects);
			}
			
			// Tmp objects to list
			//ArrayList<JsonObject> allObjects = new ArrayList<>();
			//for(int row = 0; row < orderedJsonObjects.size(); i += 1) {
			//	ArrayList<JsonObject> Objects = orderedJsonObjects.get(row);
			//	System.out.println("Test!");
			//	for(int object = 0; object < Objects.size(); object += 1) {
			//		allObjects.add(Objects.get(object));
			//	}
			//}
			
			//// Merge JSON objects
			//for(int row = 0; row < orderedJsonObjects.size(); i += 1) {
			//	ArrayList<JsonObject> Objects = orderedJsonObjects.get(row);
			//	
			//	for(int object = 0; object < Objects.size(); object += 1) {
			//		
			//	}
			//}
			
			
			
			// Create Final Objects
			for(int index = 0; index < allObjects.size(); index += 1) {
				JsonObject obj = allObjects.get(index);
				
				int normalizedColumn = obj.get("column").getAsInt() - table1.getX();
				
				if(normalizedColumn == 0) {
					System.out.println("New object!");
					
					JsonObject objectRoot = new JsonObject();
					
					for(int headerColumn = 0; headerColumn < arrLayout.size(); headerColumn += 1) {
						ArrayList<String> arrays = arrLayout.get(headerColumn);
						
						JsonArray arr = null;
						for(int headerRow = 0; headerRow < arrays.size(); headerRow += 1) {
							boolean exists = objectRoot.has(arrays.get(headerRow));
							arr = objectRoot.get(arrays.get(headerRow)).getAsJsonArray();
						}
					}
				}
			}
			
			//HashMap<String, JsonObject> mapOfObjects = new HashMap<>();
			//
			//// Needs to be updated to get from header dynamically
			//int[] arrLevels = new int[] { 0, 1, 2, 2, 3, 3, 0 };
			//HashMap<Integer, String> lists = new HashMap<>();
			//lists.put(1, "Sensors");
			//lists.put(2, "Inputs");
			//lists.put(3, "Outputs");
			//
			//int rowStart = table1.getY() + depth;
			//int rowEnd = table1.getY() + table1.getHeight();
			//int columnStart = table1.getX();
			//int columnEnd = table1.getX() + table1.getWidth();
			//
			//// Organize cell data
			//ArrayList<ArrayList<Tuple<Integer, ArrayList<CellData>>>> rows = new ArrayList<>();
			//for(int row = rowStart; row < rowEnd; row += 1) {
			//	ArrayList<Tuple<Integer, ArrayList<CellData>>> rowEntries = new ArrayList<>();
			//	ArrayList<CellData> currentEntry = new ArrayList<>();
			//	int currentDepth = -1;
			//	
			//	for(int column = columnStart; column < columnEnd; column += 1) {
			//		if(sheet.getCell(column, row) != null) {
			//			Cell cell = sheet.getCell(column, row);
			//			CellData CD = parseCellData(cell.getData(), cell.getColumn(), cell.getRow());
			//			// Needs to be dynamic -> use tableName
			//			CD.setCellName(App.M.getAttribute(tableName, column).getName());
			//			
			//			if(currentDepth == -1 || currentDepth == arrLevels[column]) {
			//				currentEntry.add(CD);
			//				currentDepth = arrLevels[column];
			//			} else {
			//				rowEntries.add(new Tuple<>(currentDepth, currentEntry));
			//				currentDepth = arrLevels[column];
			//				currentEntry = new ArrayList<CellData>();
			//				currentEntry.add(CD);
			//			}
			//		}
			//	}
			//	
			//	rowEntries.add(new Tuple<>(currentDepth, currentEntry));
			//	rows.add(rowEntries);
			//	
			//	//System.out.println("Row entries: " + rowEntries.size());
			//	//for(int k = 0; k < rowEntries.size(); k += 1) {
			//		//System.out.println("Entry " + k + " : " + rowEntries.get(k).getA() + " - " + rowEntries.get(k).getB().size());
			//	//}
			//}
			//
			//// Convert to JSON objects
			//ArrayList<ArrayList<Tuple<Integer, JsonObject>>> objectRows = new ArrayList<>();
			//			
			//for(int row = 0; row < rows.size(); row += 1) {
			//	ArrayList<Tuple<Integer, ArrayList<CellData>>> entries = rows.get(row);
			//	ArrayList<Tuple<Integer, JsonObject>> objects = new ArrayList<>();
			//	
			//	for(int entry = 0; entry < entries.size(); entry += 1) {
			//		Tuple<Integer, ArrayList<CellData>> object = entries.get(entry);
			//		
			//		JsonObject obj = new JsonObject();
			//		
			//		for(int cell = 0; cell < object.getB().size(); cell += 1) {
			//			// Needs to be dynamic "Config" -> tableName
			//			JsonObj attr = App.M.getAttribute(tableName, object.getB().get(cell).getCellName());
			//			//System.out.println(attr.getName() + " -> " + attr.getDataType());
			//			
			//			JsonObject tmp = new JsonObject();
			//			tmp.addProperty("column", object.getB().get(cell).getColumn());
			//			tmp.addProperty("row", object.getB().get(cell).getRow());
			//			
			//			switch(attr.getDataType()) {
			//				case "alternative":
			//					tmp.addProperty("value", "'" + object.getB().get(cell).getName() + "'");
			//					break;
			//				case "Int":
			//					tmp.addProperty("value", Integer.parseInt(object.getB().get(cell).getName()));
			//					break;
			//				case "String":
			//					tmp.addProperty("value", "'" + object.getB().get(cell).getName() + "'");
			//					break;
			//				case "Boolean":
			//					tmp.addProperty("value", Boolean.parseBoolean(object.getB().get(cell).getName()));
			//					break;
			//			}
			//			
			//			obj.add(object.getB().get(cell).getCellName(), tmp);
			//		}
			//		
			//		objects.add(new Tuple<>(object.getA(), obj));
			//	}
			//	
			//	
			//	objectRows.add(objects);
			//}
			//
			//// Merge JSON objects
			//for(int row = 0; row < objectRows.size(); row += 1) {
			//	ArrayList<Tuple<Integer, JsonObject>> objects = objectRows.get(row);
			//	//System.out.println("Row: " + row + "  (" + objects.size() + ")");
			//	
			//	for(int object = 0; object < objects.size(); object += 1) {
			//		Tuple<Integer, JsonObject> jsonEntry = objects.get(object);
			//		// Store to handle objects at same level ?
			//		JsonObject parent = findJsonParent(objectRows, objects, row, object, objects.get(object).getA());
			//		System.out.println(jsonEntry.getA() + " : " + jsonEntry.getB().toString().toString() + " with " + parent);
			//		
			//		
			//		if(parent != null) {
			//			if(jsonEntry.getA() == 0) {
			//				for(String key : jsonEntry.getB().keySet()) {
			//					parent.add(key, jsonEntry.getB().get(key));
			//				}
			//			} else {
			//				String listKey = lists.get(jsonEntry.getA());
			//				JsonArray arr = null;
			//				
			//				if(parent.get(listKey) == null) {
			//					parent.add(listKey, new JsonArray());
			//				}
			//				
			//				arr = parent.get(listKey).getAsJsonArray();
			//				arr.add(jsonEntry.getB());
			//			}
			//		} else {
			//			table.add(jsonEntry.getB());
			//		}
			//	}
			//}
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
	
	private static JsonObject getJsonParent(ArrayList<ArrayList<JsonObject>> objects, int row, int index) {
		JsonObject root = objects.get(row).get(index);
		return null;
	}
	
	private static JsonObject getJsonParent(JsonObject root, Sheet sheet, BoundingBox table, int tableIndex, int childColumn, int childRow) {
		JsonObject parent = root.getAsJsonObject();
		
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
	
	//private static JsonObject getJsonParent(JsonArray root, Sheet sheet, BoundingBox table, int tableIndex, int childColumn, int childRow) {
	//	JsonObject parent = root.get(tableIndex).getAsJsonObject();
	//	
	//	ArrayList<Cell> parents = getCellParents(sheet, table, childColumn, childRow);
	//	parents.remove(0);
	//	
	//	for(int i = 0; i < parents.size(); i += 1) {
	//		CellData CD = parseCellData(parents.get(i).getData(), parents.get(i).getColumn(), parents.get(i).getRow());
	//		JsonArray children = parent.get("children").getAsJsonArray();
	//		JsonObject obj = findJsonObject(children, CD.getName());
	//		
	//		if(obj == null) {
	//			break;
	//		}
	//		
	//		if(!obj.get("type").getAsString().equals("type")) {
	//			parent = obj;				
	//		}
	//	}
	//	
	//	return parent;
	//}
	
	private static JsonObject findJsonObject(JsonArray arr, String name) {
		JsonObject obj = null;
		
		for(int i = 0; i < arr.size(); i += 1) {
			JsonObj tmp = new Gson().fromJson(arr.get(i).getAsJsonObject(), JsonObj.class);
			//JsonObject tmp = arr.get(i).getAsJsonObject();
			//System.out.println(tmp);
			
			//System.out.println("Name test: " + tmp.getNameOnly()); //tmp.get("name").getAsString());
			//if(tmp.get("name").getAsString().equals(name)) {
			//	obj = tmp;
			//	break;
			//}
			
			if(tmp.getNameOnly().equals(name)) {
				obj = arr.get(i).getAsJsonObject();
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
