package com.github.lukelinkwalker.orchestrator.transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.github.lukelinkwalker.orchestrator.App;
import com.github.lukelinkwalker.orchestrator.Util.StringUtilities;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SheetTransformer {
	public static String parse(Sheet sheet) {
		if(sheet.isSML()) {
			return parseSML(sheet);
		}
		
		return parseSDSL(sheet);
	}
	
	public static String parseSML(Sheet sheet) {
		JsonArray root = new JsonArray();
		ArrayList<BoundingBox> tables = sheet.getTableRanges();
		
		for(int i = 0; i < tables.size(); i += 1) {
			JsonObject entry = null;
			
			BoundingBox table = tables.get(i);
		
			int rowStart = table.getRowStart();
			int rowEnd = table.getRowEnd();
			int columnStart = table.getColumnStart();
			int columnEnd = table.getColumnEnd();
			
			String tableName = sheet.getHead(table).getData();
			if(tableName.toLowerCase().equals("rules")) {
				entry = JsonUtil.find(root, "type", "rules");
				
				if(entry == null) {
					entry = new JsonObject();
					entry.addProperty("column", columnStart);
					entry.addProperty("row", rowStart);
					entry.addProperty("type", "rules");
					entry.add("children", new JsonArray());
				}
				
				for(int row = rowStart + 2; row < rowEnd; row += 1) {
					Cell name = sheet.getCell(columnStart, row);
					Cell rule = sheet.getCell(columnStart + 1, row);
					
					if(name != null && rule != null) {
						JsonObject ruleObj = new JsonObject();
						
						JsonObject nameObj = new JsonObject();
						nameObj.addProperty("column", columnStart);
						nameObj.addProperty("row", row);
						nameObj.addProperty("value", JsonUtil.tokenWrap(name.getData()));

						JsonObject valueObj = new JsonObject();
						valueObj.addProperty("column", columnStart + 1);
						valueObj.addProperty("row", row);
						valueObj.addProperty("value", rule.getData());
						
						ruleObj.add("name", nameObj);
						ruleObj.add("rule", valueObj);
						
						entry.get("children").getAsJsonArray().add(ruleObj);
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

			if(root.contains(entry) == false) {
				root.add(entry);
			}
		}
		
		// Move rules table or insert empty table if no rules are defined
		int index = JsonUtil.indexOf(root, "type", "rules");
		if(index != -1) {
			JsonObject obj = root.get(index).getAsJsonObject();
			root.remove(index);
			root.add(obj);
		} else {
			JsonObject obj = new JsonObject();
			obj.addProperty("column", -1);
			obj.addProperty("row", -1);
			obj.addProperty("type", "rules");
			obj.add("children", new JsonArray());
			root.add(obj);
		}
		
		return root.toString().toString();
	}
	
	public static String parseSDSL(Sheet sheet) {
		JsonArray root = new JsonArray();
		ArrayList<BoundingBox> tables = sheet.getTableRanges();
		
		// Prepare list of breakouts
		HashMap<String, ArrayList<String>> mapOfBreakouts = new HashMap<>();
		for(int i = 0; i < tables.size(); i += 1) {
			BoundingBox table1 = tables.get(i);
			String tableName = sheet.getCell(table1.getX(), table1.getY()).getData();
			
			String[] tableInfo = parseTableName(tableName);
			if(tableInfo != null) {
				String firstElement = StringUtilities.stripTrailingSpecials(tableInfo[0]);
				String lastElement = StringUtilities.stripTrailingSpecials(tableInfo[tableInfo.length - 1]);
				
				if(mapOfBreakouts.containsKey(firstElement)) {
					mapOfBreakouts.get(firstElement).add(lastElement);
				} else {
					ArrayList<String> tmpList = new ArrayList<>();
					tmpList.add(lastElement);
					mapOfBreakouts.put(firstElement, tmpList);
				}
			}
		}
		
		for(int i = 0; i < tables.size(); i += 1) {
			BoundingBox table1 = tables.get(i);
			String tableName = sheet.getCell(table1.getX(), table1.getY()).getData();
			
			System.out.println("Processing: " + table1.toString());
			
			// Clean table
			tableName = StringUtilities.stripTrailingSpecials(tableName);
			System.out.println("Table name: " + tableName + " (" + tableName.length() + ")");
			
			// Build Table Name
			String[] tableNameParts = parseTableName(tableName);
			StringBuilder SB = new StringBuilder();
			for(String str : tableNameParts) {
				SB.append(StringUtilities.stripTrailingSpecials(JsonUtil.tokenStrip(str)));
			}
			
			// Structure objects
			ArrayList<ArrayList<String>> arrLayout = null;
			List<JsonObj> attributes = null;
			int headerDepth = 0;
			
			// Modify arrLayout and attributes
			if(App.M.checkIfExists(tableName) == true) {
				// Modifed and unmodified tables
				arrLayout = App.M.getArrayLayout(tableName, mapOfBreakouts.get(tableName));
				attributes = App.M.getAttributes(tableName, mapOfBreakouts.get(tableName));
				headerDepth = App.M.getDepth(tableName, true, mapOfBreakouts.get(tableName));
			} else {
				// Broken out tables
				if(tableNameParts.length < 2) {
					// Put out server-generated error due to skip
					continue;
				}
				
				JsonObj arr = App.M.getArray(
					StringUtilities.stripTrailingSpecials(tableNameParts[0]), 
					StringUtilities.stripTrailingSpecials(tableNameParts[1])
				);
				
				arrLayout = App.M.getArrayLayout(arr);
				attributes = App.M.getAttributes(arr, null);
				headerDepth = App.M.getDepth(arr, true, null);
			}
			
			// Add base element to output
			JsonObject base = new JsonObject();
			JsonArray table = new JsonArray();
			base.addProperty("Name", SB.toString());
			base.add("Table", table);
			root.add(base);
			
			// Find limits for current table
			int rowStart = table1.getY() + headerDepth; 
			int rowEnd = table1.getY() + table1.getHeight();
			int columnStart = table1.getX();
			int columnEnd = table1.getX() + table1.getWidth();
			
			// Create JSON objects
			ArrayList<JsonObject> allObjects = new ArrayList<>();
			for(int row = rowStart; row < rowEnd; row += 1) {
				for(int column = columnStart; column < columnEnd; column += 1) {
					Cell cell = sheet.getCell(column, row);
					
					if(cell != null) {
						int normalizedColumn = cell.getColumn() - table1.getX();
						String type = attributes.get(normalizedColumn).getDataType();
						allObjects.add(cell.getAsJsonObject(type));	
					}
				}
			}
			
			// Setup list reference map
			HashMap<String, JsonElement> tmpObjRef = new HashMap<>();
			for(int index = 0; index < attributes.size(); index += 1) {
				String listName = arrLayout.get(index).get(arrLayout.get(index).size() - 1);
				
				if(App.M.isFirstAttribute(attributes, arrLayout, index) == true) {
					tmpObjRef.put(JsonUtil.tokenStrip(listName), null);
				}
				
				//if(App.M.isFirstAttribute(tableName, index) == true) {
				//	tmpObjRef.put(JsonUtil.tokenStrip(listName), null);
				//}
			}
			
			// Merge JSON objects
			for(int index = 0; index < allObjects.size(); index += 1) {
				JsonObject obj = allObjects.get(index);
				int normalizedColumn = obj.get("column").getAsInt() - table1.getX();
				
				ArrayList<String> currListStructure = arrLayout.get(normalizedColumn);
				
				String prevListName = "";
				if(currListStructure.size() > 1) {
					prevListName = JsonUtil.tokenStrip(currListStructure.get(currListStructure.size() - 2));
				}
				String currListName = JsonUtil.tokenStrip(currListStructure.get(currListStructure.size() - 1));
				
				JsonElement tmpElement = null;
				JsonArray tmpArray = null;
				JsonObject tmpObject = null;
				
				//if(App.M.isFirstAttribute(tableName, normalizedColumn)) {
				if(App.M.isFirstAttribute(attributes, arrLayout, normalizedColumn) == true) {
					if(currListStructure.size() == 1) {
						table.add(new JsonObject());
						tmpObjRef.put(currListName, table.get(table.size() - 1));
					} else {
						tmpElement = tmpObjRef.get(prevListName);
						
						if(tmpElement.isJsonObject()) {
							tmpObject = tmpElement.getAsJsonObject();
						} else if (tmpElement.isJsonArray()) {
							tmpArray = tmpElement.getAsJsonArray();
							tmpObject = tmpArray.get(tmpArray.size() - 1).getAsJsonObject();
						}
						
						if(tmpObject.get(currListName) == null) {
							JsonArray jArr = new JsonArray();
							jArr.add(new JsonObject());
							tmpObject.add(currListName, jArr);
							tmpObjRef.put(currListName, jArr);
						} else {
							tmpObject.get(currListName).getAsJsonArray().add(new JsonObject());
						}
					}
				}
				
				// Add Entry
				tmpElement = tmpObjRef.get(currListName);
				if(tmpElement.isJsonArray()) {
					tmpArray = tmpElement.getAsJsonArray();
					tmpObject = tmpArray.get(tmpArray.size() - 1).getAsJsonObject();
				} else if (tmpElement.isJsonObject()) {
					tmpObject = tmpObjRef.get(currListName).getAsJsonObject();
				}
				
				tmpObject.add(attributes.get(normalizedColumn).getNameOnly(), obj);
			}
		}
		
		return root.toString().toString();
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
	
	private static JsonObject getJsonParent(JsonObject root, Sheet sheet, BoundingBox table, int tableIndex, int childColumn, int childRow) {
		JsonObject parent = root.getAsJsonObject();
		
		ArrayList<Cell> parents = getCellParents(sheet, table, childColumn, childRow);
		if(parents.size() > 0) {
			parents.remove(0);			
		}
		
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
			JsonObj tmp = new Gson().fromJson(arr.get(i).getAsJsonObject(), JsonObj.class);
			
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
	
	private static String[] parseTableName(String str) {
		if(str.contains(" -> ")) {
			return str.split(" -> ");
		}
		
		return new String[] { str };
	}
}
