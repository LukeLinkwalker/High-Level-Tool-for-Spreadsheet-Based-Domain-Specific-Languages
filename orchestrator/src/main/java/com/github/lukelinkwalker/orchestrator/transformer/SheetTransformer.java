package com.github.lukelinkwalker.orchestrator.transformer;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import com.github.lukelinkwalker.orchestrator.App;
import com.github.lukelinkwalker.orchestrator.Util.Tuple;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SheetTransformer {
	public static String parse(Sheet sheet) {
		if(sheet.isSGL()) {
			return parseSGL(sheet);
		}
		
		return parseSDSL(sheet);
	}
	
	public static String parseSGL(Sheet sheet) {
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
						valueObj.addProperty("value", rule.getData()); // JsonUtil.tokenWrap(rule.getData()));
						
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
		
		// Move rules table or insert if no rules defined
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
		
		for(int i = 0; i < tables.size(); i += 1) {
			BoundingBox table1 = tables.get(i);
			String tableName = sheet.getCell(table1.getX(), table1.getY()).getData();
			System.out.println("Processing: " + table1.toString());
			
			// Clean table
			tableName = tableName.replaceAll("[^a-zA-Z0-9]+"," ");
			tableName = tableName.stripTrailing();
			System.out.println("Table name: " + tableName);
			
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

			// Modify arrLayout based on break outs
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
			for(int row = 0; row < orderedCellData.size(); row += 1) {
				ArrayList<Tuple<Integer, CellData>> CDs = orderedCellData.get(row);
				ArrayList<JsonObject> Objects = new ArrayList<>();
				
				for(int cell = 0; cell < CDs.size(); cell += 1) {
					Tuple<Integer, CellData> CD = CDs.get(cell);
					
					JsonObject value = new JsonObject();
					value.addProperty("column", CD.getB().getColumn());
					value.addProperty("row", CD.getB().getRow());
					
					int normalizedColumn = CD.getB().getColumn() - table1.getX();
					
					switch(attributes.get(normalizedColumn).getDataType()) {
						case "alternative":
							value.addProperty("value", JsonUtil.tokenWrap(CD.getB().getName()));
							break;
						case "int":
							value.addProperty("value", Integer.parseInt(CD.getB().getName()));
							break;
						case "float":
							value.addProperty("value", Float.parseFloat(CD.getB().getName()));
							break;
						case "string":
							value.addProperty("value", JsonUtil.tokenWrap(CD.getB().getName()));
							break;
						case "boolean":
							value.addProperty("value", Boolean.parseBoolean(CD.getB().getName()));
							break;
					}
					
					JsonObject container = new JsonObject();
					container.add(App.M.getAttribute(tableName, CD.getA()).getName(), value);
					
					Objects.add(value);
					allObjects.add(value);
				}
			}
			
			// Merge JSON objects
			HashMap<String, JsonObject> tmpObjRef = new HashMap<>();
			for(int index = 0; index < attributes.size(); index += 1) {
				String listName = arrLayout.get(index).get(arrLayout.get(index).size() - 1);
				
				if(App.M.isFirstAttribute(tableName, index) == true) {
					tmpObjRef.put(JsonUtil.tokenStrip(listName), null);
					System.out.println(JsonUtil.tokenStrip(listName));
				}
			}
			
			for(int index = 0; index < allObjects.size(); index += 1) {
				JsonObject obj = allObjects.get(index);
				int normalizedColumn = obj.get("column").getAsInt() - table1.getX();
				
				System.out.println(obj);
				
				boolean isFirst = App.M.isFirstAttribute(tableName, normalizedColumn);
				String listName = JsonUtil.tokenStrip(arrLayout.get(normalizedColumn).get(arrLayout.get(normalizedColumn).size() - 1));
				
				// Create List Objects
				if(isFirst) {
					if(arrLayout.get(normalizedColumn).size() == 1) {
						JsonObject newObj = new JsonObject();
						table.add(newObj);
						tmpObjRef.put(listName, newObj);
					} else {
						ArrayList<String> lists = arrLayout.get(normalizedColumn);
						String prevListName = JsonUtil.tokenStrip(lists.get(lists.size() - 2));
						
						JsonObject tmpRoot = tmpObjRef.get(prevListName);
						JsonObject newObj = new JsonObject();
						
						if(tmpRoot.get(JsonUtil.tokenStrip(lists.get(lists.size() - 1))) == null) {
							// Create list and add new object
							JsonArray jArr = new JsonArray();
							newObj.add("List", jArr);
							jArr.add(new JsonObject());
							
							if(tmpRoot.get("List") == null) {
								tmpRoot.add(JsonUtil.tokenStrip(lists.get(lists.size() - 1)), newObj);
							} else {
								JsonArray tmpRootArray = tmpRoot.get("List").getAsJsonArray();
								tmpRootArray.get(tmpRootArray.size() - 1).getAsJsonObject().add(JsonUtil.tokenStrip(lists.get(lists.size() - 1)), newObj);
							}
						} else {
							// Add new object
							tmpRoot = tmpObjRef.get(prevListName);
							JsonArray tmpArr = tmpRoot.get(listName).getAsJsonObject().get("List").getAsJsonArray();
							tmpArr.add(newObj);
						}
						
						tmpObjRef.put(listName, newObj);
						
					}
					
					boolean reset = false;
					for(String key : tmpObjRef.keySet()) {
						if(reset == true) {
							tmpObjRef.put(listName, null);
							System.out.println("Resetting");
						} else {
							if(tmpObjRef.get(key) != null) {
								if(tmpObjRef.get(key).equals(listName)) {
									reset = true;
								}								
							}
						}
					}
				}
				
				// Add Entry
				System.out.println("name : " + listName);
				
				if(arrLayout.get(normalizedColumn).size() == 1) {
					tmpObjRef.get(listName).add(attributes.get(normalizedColumn).getNameOnly(), obj);
				} else {
					if(tmpObjRef.get(listName).get("List") == null) {
						tmpObjRef.get(listName).add(attributes.get(normalizedColumn).getNameOnly(), obj);
					} else {
						JsonArray tmpArr = tmpObjRef.get(listName).get("List").getAsJsonArray();
						tmpArr.get(tmpArr.size() - 1).getAsJsonObject().add(attributes.get(normalizedColumn).getNameOnly(), obj);
					}
				}
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
}
