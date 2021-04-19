package com.github.lukelinkwalker.orchestrator.ssserver;

import com.github.lukelinkwalker.orchestrator.App;
import com.github.lukelinkwalker.orchestrator.Util.StringUtilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class TableCreator {

    public static boolean initializeCreateTable(String tableName, int column, int row, String spreadsheetType) {
        JsonObject tableObject = findTableJsonObject(tableName, spreadsheetType);
        boolean success = true;

        if (tableObject == null) {
            success = false;
        }
        else {
            int rootColumn = tableObject.get("column").getAsInt();
            int rootRow = tableObject.get("row").getAsInt();

            int[] tableHeaderAreaIndexes = getTableHeaderAreaIndexes(tableObject, column, row, rootColumn, rootRow);
            int[] tableDataAreaIndexes = getTableDataAreaIndexes(tableHeaderAreaIndexes);

            sendMergeCommandForAppropriateCells(tableObject, tableName, tableHeaderAreaIndexes[2], column, row,
                    rootColumn, rootRow);
            sendBoldTextCommandForAppropriateCells(tableObject, column, row, rootColumn, rootRow);
            sendBlackBorderCommandForRangeOfCells(tableHeaderAreaIndexes);
            sendBlackBorderCommandForRangeOfCells(tableDataAreaIndexes);
            sendSetAsHeaderCommandForAppropriateCells(tableHeaderAreaIndexes);
            sendSetAsDataCommandForAppropriateCells(tableDataAreaIndexes);
            sendTextCommandForHeaderNamesForAppropriateCells(tableObject, column, row, rootColumn, rootRow);
            sendTextAndItalicCommandForTypesForAppropriateCells(tableObject, column, row, rootColumn, rootRow);
        }

        return success;
    }

    private static int[] getTableHeaderAreaIndexes(JsonObject tableObject, int column, int row, int rootColumn,
            int rootRow) {
        int[] endCellIndexes = findEndCellIndexes(tableObject, column, row, rootColumn, rootRow);

        return new int[] {column, row, endCellIndexes[0], endCellIndexes[1]};
    }

    private static int[] getTableDataAreaIndexes(int[] headerArea) {
        int startCellColumn = headerArea[0];
        int startCellRow = headerArea[3] + 1;
        int endCellColumn = headerArea[2];
        int endCellRow = headerArea[3] + 1;

        return new int[] {startCellColumn, startCellRow, endCellColumn, endCellRow};
    }

    private static JsonObject findTableJsonObject(String name, String spreadsheetType) {
        //TODO: Remove after testing
//        JsonArray ssModel = App.SSS.getSsModel();
//        JsonArray ssModel = App.SSS.getSDSLSSModelTest();
        JsonArray ssModel;

        if (spreadsheetType.equals("sgl")) ssModel = App.SSS.getSGLSSModel();
        else ssModel = App.SSS.getSDSLSSModelTest();

        if (ssModel != null) {
            for (JsonElement jsonElement : ssModel) {
                String type = jsonElement.getAsJsonObject().get("type").getAsString();

                if (!type.equals("rules")) {
                    String objectName = StringUtilities.removeTokensFromString(jsonElement.getAsJsonObject().get("name")
                            .getAsString());

                    if (name.equals(objectName)) return jsonElement.getAsJsonObject();
                }
            }
        }

        return null;
    }

    private static int[] findEndCellIndexes(JsonObject jsonObject, int column, int row, int rootColumn, int rootRow) {
        int endCellColumn = jsonObject.get("column").getAsInt() + column - rootColumn;
        int endCellRow = jsonObject.get("row").getAsInt() + row - rootRow;

        //+1 to add row for datatypes
        int[] biggestEndCellIndexes = new int[] {endCellColumn, endCellRow + 1};

        for (JsonElement jsonElement : jsonObject.get("children").getAsJsonArray()) {
            int[] thisCellIndexes = findEndCellIndexes(jsonElement.getAsJsonObject(), column, row, rootColumn, rootRow);

            if (thisCellIndexes[0] > biggestEndCellIndexes[0]) biggestEndCellIndexes[0] = thisCellIndexes[0];
            if (thisCellIndexes[1] > biggestEndCellIndexes[1]) biggestEndCellIndexes[1] = thisCellIndexes[1];
        }

        return biggestEndCellIndexes;
    }

    public static boolean checkIfTextIsATableName(String cellText, String spreadsheetType) {
        return findTableJsonObject(cellText, spreadsheetType) != null;
    }

    public static int[] getInitialTableRangeResponse(String name, int column, int row, String spreadsheetType) {
        JsonObject tableObject = findTableJsonObject(name, spreadsheetType);

        if (tableObject == null) return null;
        else {
            int rootColumn = tableObject.get("column").getAsInt();
            int rootRow = tableObject.get("row").getAsInt();

            int[] tableHeaderAreaIndexes = getTableHeaderAreaIndexes(tableObject, column, row, rootColumn, rootRow);
            int[] tableDataAreaIndexes = getTableDataAreaIndexes(tableHeaderAreaIndexes);

            return new int[] {tableHeaderAreaIndexes[0], tableHeaderAreaIndexes[1], tableDataAreaIndexes[2],
                    tableDataAreaIndexes[3]};
        }
    }

    private static void sendTextCommandForHeaderNamesForAppropriateCells(JsonObject jsonObject, int startColumn,
            int startRow, int rootColumn, int rootRow) {
        String name = StringUtilities.removeTokensFromString(jsonObject.get("name").getAsString());
        int column = jsonObject.get("column").getAsInt() + startColumn - rootColumn;
        int row = jsonObject.get("row").getAsInt() + startRow - rootRow;
        String type = jsonObject.get("type").getAsString();
        boolean isOptional = jsonObject.get("isOptional").getAsBoolean();

        if (type.equals("array")) name += " [ ]";
        if (isOptional) name += " ?";

        JsonArray children = jsonObject.get("children").getAsJsonArray();

        App.SSS.sendNotification("set-text", new Object[] {column, row, name});

        for (JsonElement child : children) {
            sendTextCommandForHeaderNamesForAppropriateCells(child.getAsJsonObject(), startColumn, startRow, rootColumn,
                    rootRow);
        }
    }

    private static void sendTextAndItalicCommandForTypesForAppropriateCells(JsonObject jsonObject, int startColumn,
            int startRow, int rootColumn, int rootRow) {
        JsonArray dataTypes = jsonObject.get("dataTypes").getAsJsonArray();
        JsonArray children = jsonObject.get("children").getAsJsonArray();

        if (dataTypes.size() != 0) {
            String type = jsonObject.get("type").getAsString();
            JsonObject dataType = dataTypes.get(0).getAsJsonObject();

            int column = dataType.get("column").getAsInt() + startColumn - rootColumn;
            int row = dataType.get("row").getAsInt() + startRow - rootRow;
            String value;

            if (type.equals("alternative")) value = "alternative";
            else {
                String typeOfDataType = dataType.get("type").getAsString();
                value = dataType.get("value").getAsString();
                if (typeOfDataType.equals("custom")) value = StringUtilities.removeTokensFromString(value);
            }

            App.SSS.sendNotification("set-text", new Object[] {column, row, value});
            App.SSS.sendNotification("italic-text", new Object[] {column, row});
        }

        for (JsonElement child : children) {
            sendTextAndItalicCommandForTypesForAppropriateCells(child.getAsJsonObject(), startColumn, startRow,
                    rootColumn, rootRow);
        }
    }

    private static void sendMergeCommandForAppropriateCells(JsonObject jsonObject, String tableName, int endCellColumn,
            int startColumn, int startRow, int rootColumn, int rootRow) {
        String name = StringUtilities.removeTokensFromString(jsonObject.get("name").getAsString());
        JsonArray children = jsonObject.get("children").getAsJsonArray();

        if (name.equals(tableName)) {
            int column = jsonObject.get("column").getAsInt() + startColumn - rootColumn;
            int row = jsonObject.get("row").getAsInt() + startRow - rootRow;

            App.SSS.sendNotification("merge", new Object[] {column, row, endCellColumn, row});
        }

        for (JsonElement child : children) {
            JsonArray childrenWithoutThis = children.deepCopy();
            childrenWithoutThis.remove(child);

            int column = child.getAsJsonObject().get("column").getAsInt() + startColumn - rootColumn;
            int row = child.getAsJsonObject().get("row").getAsInt() + startRow - rootRow;
            JsonObject closestChild = null;
            int closestChildColumn;

            for (JsonElement otherChild : childrenWithoutThis) {
                int otherColumn = otherChild.getAsJsonObject().get("column").getAsInt() + startColumn - rootColumn;
                int distanceFromChildAndOtherChild = otherColumn - column;

                if (closestChild == null) {
                    if (distanceFromChildAndOtherChild > 0) closestChild = otherChild.getAsJsonObject();
                }
                else {
                    closestChildColumn = closestChild.get("column").getAsInt() + startColumn - rootColumn;
                    int distanceFromChildAndClosetsChild = closestChildColumn - column;

                    if (distanceFromChildAndOtherChild > 0 && distanceFromChildAndOtherChild <
                            distanceFromChildAndClosetsChild) closestChild = otherChild.getAsJsonObject();
                }
            }

            if (closestChild == null) {
                if (column != endCellColumn) {
                    App.SSS.sendNotification("merge", new Object[] {column, row, endCellColumn, row});
                    sendMergeCommandForAppropriateCells(child.getAsJsonObject(), tableName, endCellColumn, startColumn,
                            startRow, rootColumn, rootRow);
                }
            }
            else {
                closestChildColumn = closestChild.get("column").getAsInt() + startColumn - rootColumn;

                if (closestChildColumn > column + 1) {
                    App.SSS.sendNotification("merge", new Object[] {column, row, closestChildColumn - 1, row});
                    sendMergeCommandForAppropriateCells(child.getAsJsonObject(), tableName,
                            closestChildColumn - 1, startColumn, startRow, rootColumn, rootRow);
                }
            }
        }
    }

    private static void sendBoldTextCommandForAppropriateCells(JsonObject jsonObject, int startColumn, int startRow,
            int rootColumn, int rootRow) {
        List<JsonObject> headerCellsExceptAttributes = findHeaderCellsExceptAttributes(jsonObject);

        for (JsonObject cell : headerCellsExceptAttributes) {
            int column = cell.get("column").getAsInt() + startColumn - rootColumn;
            int row = cell.get("row").getAsInt() + startRow - rootRow;
            App.SSS.sendNotification("bold-text", new Object[] {column, row});
        }
    }

    private static ArrayList<JsonObject> findHeaderCellsExceptAttributes(JsonObject jsonObject) {
        ArrayList<JsonObject> objectOrArrayCells = new ArrayList<>();
        JsonArray children = jsonObject.get("children").getAsJsonArray();
        String type = jsonObject.get("type").getAsString();

        if (!type.equals("attribute")) objectOrArrayCells.add(jsonObject);

        for (JsonElement child : children) {
            objectOrArrayCells.addAll(findHeaderCellsExceptAttributes(child.getAsJsonObject()));
        }

        return objectOrArrayCells;
    }

    private static void sendBlackBorderCommandForRangeOfCells(int[] rangeOfCells) {
        for (int i = rangeOfCells[0]; i <= rangeOfCells[2]; i++) {
            for (int j = rangeOfCells[1]; j <= rangeOfCells[3]; j++) {
                App.SSS.sendNotification("black-border", new Object[] {i, j});
            }
        }
    }

    private static void sendSetAsHeaderCommandForAppropriateCells(int[] rangeOfCells) {
        for (int i = rangeOfCells[0]; i <= rangeOfCells[2]; i++) {
            for (int j = rangeOfCells[1]; j <= rangeOfCells[3]; j++) {
                App.SSS.sendNotification("set-as-header-cell", new Object[] {i, j});
            }
        }
    }

    private static void sendSetAsDataCommandForAppropriateCells(int[] rangeOfCells) {
        for (int i = rangeOfCells[0]; i <= rangeOfCells[2]; i++) {
            for (int j = rangeOfCells[1]; j <= rangeOfCells[3]; j++) {
                App.SSS.sendNotification("set-as-data-cell", new Object[] {i, j});
            }
        }
    }
}