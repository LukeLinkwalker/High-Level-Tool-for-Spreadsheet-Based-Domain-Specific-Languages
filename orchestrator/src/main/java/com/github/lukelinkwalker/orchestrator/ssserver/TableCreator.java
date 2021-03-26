package com.github.lukelinkwalker.orchestrator.ssserver;

import com.github.lukelinkwalker.orchestrator.App;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSMessage;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSNotification;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableCreator {
    private static JsonArray ssModelAsJson;

//    //TODO: Remove setup after functionality to setup ssmodel is added
//    private static void setup(String string) throws IOException {
//        Gson gson = new Gson();
//        Reader reader = Files.newBufferedReader(Paths.get("orchestrator/src/main/java/com/github/lukelinkwalker/orchestrator/ssserver/ssmodel.json"));
//        ssmodel = gson.fromJson(reader, JsonArray.class);
////        ssmodel = gson.fromJson(string, JsonArray.class);
//    }
//
//    public static void loadSSModel() {
//        Gson gson = new Gson();
//        ssModelAsJson = gson.fromJson(App.SSS.getSsModel(), JsonArray.class);
//    }

    //TODO Update with setup
    public static boolean initializeCreateTable(String tableName, int column, int row, String string) {
//        try {
//            setup(string);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        JsonObject tableObject = findTableJsonObject(tableName);
        boolean success;

        if (tableObject == null) {
            success = false;
        }
        else {
            int[] tableHeaderAreaIndexes = getTableHeaderAreaIndexes(tableObject, column, row);
            int[] tableDataAreaIndexes = getTableDataAreaIndexes(tableHeaderAreaIndexes);

            sendMergeCommandForAppropriateCells(tableObject, tableName, tableHeaderAreaIndexes[2], column, row);
            sendBoldTextCommandForAppropriateCells(tableObject, column, row);
            sendCenterTextCommandForAppropriateCells(tableHeaderAreaIndexes);
            sendCenterTextCommandForAppropriateCells(tableDataAreaIndexes);
            sendBlackBorderCommandForRangeOfCells(tableHeaderAreaIndexes);
            sendBlackBorderCommandForRangeOfCells(tableDataAreaIndexes);
            sendSetAsHeaderCommandForAppropriateCells(tableHeaderAreaIndexes);
            sendSetAsDataCommandForAppropriateCells(tableDataAreaIndexes);
            sendTextCommandForAppropriateCells(tableObject, column, row);

            success = true;
        }

        return success;
    }

    private static int[] getTableHeaderAreaIndexes(JsonObject tableObject, int column, int row) {
        int startCellColumn = tableObject.get("column").getAsInt() + column;
        int startCellRow = tableObject.get("row").getAsInt() + row;
        int[] endCellIndexes = findEndCellIndexes(tableObject, column, row);

        return new int[] {startCellColumn, startCellRow, endCellIndexes[0], endCellIndexes[1]};
    }

    private static int[] getTableDataAreaIndexes(int[] headerArea) {
        int startCellColumn = headerArea[0];
        int startCellRow = headerArea[3] + 1;
        int endCellColumn = headerArea[2];
        int endCellRow = headerArea[3] + 1;

        return new int[] {startCellColumn, startCellRow, endCellColumn, endCellRow};
    }

    private static JsonObject findTableJsonObject(String name) {
        JsonArray ssModel = App.SSS.getSsModel();
        for (JsonElement jsonElement : ssModel) {
            String objectName = jsonElement.getAsJsonObject().get("name").getAsString();

            if (name.equals(objectName)) return jsonElement.getAsJsonObject();
        }

        return null;
    }

    private static int[] findEndCellIndexes(JsonObject jsonObject, int column, int row) {
        int endCellColumn = jsonObject.get("column").getAsInt() + column;
        int endCellRow = jsonObject.get("row").getAsInt() + row;
        int[] biggestEndCellIndexes = new int[] {endCellColumn, endCellRow};

        for (JsonElement jsonElement : jsonObject.get("children").getAsJsonArray()) {
            int[] thisCellIndexes = findEndCellIndexes(jsonElement.getAsJsonObject(), column, row);

            if (thisCellIndexes[0] > biggestEndCellIndexes[0]) biggestEndCellIndexes[0] = thisCellIndexes[0];
            if (thisCellIndexes[1] > biggestEndCellIndexes[1]) biggestEndCellIndexes[1] = thisCellIndexes[1];
        }

        return biggestEndCellIndexes;
    }

    public static boolean checkIfTextIsATableName(String cellText, String string) {
//        try {
//            setup(string);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return findTableJsonObject(cellText) != null;
    }

    public static int[] getInitialTableRangeResponse(String name, int column, int row, String string) {
//        try {
//            setup(string);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        JsonObject tableObject = findTableJsonObject(name);

        if (tableObject == null) return null;
        else {
            int[] tableHeaderAreaIndexes = getTableHeaderAreaIndexes(tableObject, column, row);
            int[] tableDataAreaIndexes = getTableDataAreaIndexes(tableHeaderAreaIndexes);

            return new int[] {tableHeaderAreaIndexes[0], tableHeaderAreaIndexes[1], tableDataAreaIndexes[2],
                    tableDataAreaIndexes[3]};
        }
    }

    private static void sendTextCommandForAppropriateCells(JsonObject jsonObject, int startColumn, int startRow) {
        String name = jsonObject.get("name").getAsString();
        int column = jsonObject.get("column").getAsInt() + startColumn;
        int row = jsonObject.get("row").getAsInt() + startRow;
        JsonArray children = jsonObject.get("children").getAsJsonArray();

        App.SSS.sendNotification("set-text", new Object[] {column, row, name});

        for (JsonElement child : children) {
            sendTextCommandForAppropriateCells(child.getAsJsonObject(), startColumn, startRow);
        }
    }

    private static void sendMergeCommandForAppropriateCells(JsonObject jsonObject, String tableName, int endCellColumn, int startColumn, int startRow) {
        String name = jsonObject.get("name").getAsString();
        JsonArray children = jsonObject.get("children").getAsJsonArray();

        if (name.equals(tableName)) {
            int column = jsonObject.get("column").getAsInt() + startColumn;
            int row = jsonObject.get("row").getAsInt() + startRow;

            App.SSS.sendNotification("merge", new Object[] {column, row, endCellColumn, row});
        }

        for (JsonElement child : children) {
            JsonArray childrenWithoutThis = children.deepCopy();
            childrenWithoutThis.remove(child);

            int column = child.getAsJsonObject().get("column").getAsInt() + startColumn;
            int row = child.getAsJsonObject().get("row").getAsInt() + startRow;
            JsonObject closestChild = null;
            int closestChildColumn;

            for (JsonElement otherChild : childrenWithoutThis) {
                int otherColumn = otherChild.getAsJsonObject().get("column").getAsInt() + startColumn;
                int distanceFromChildAndOtherChild = otherColumn - column;

                if (closestChild == null) {
                    if (distanceFromChildAndOtherChild > 0) closestChild = otherChild.getAsJsonObject();
                }
                else {
                    closestChildColumn = closestChild.get("column").getAsInt() + startColumn;
                    int distanceFromChildAndClosetsChild = closestChildColumn - column;

                    if (distanceFromChildAndOtherChild > 0 && distanceFromChildAndOtherChild <
                            distanceFromChildAndClosetsChild) closestChild = otherChild.getAsJsonObject();
                }
            }

            if (closestChild == null) {
                if (column != endCellColumn) {
                    App.SSS.sendNotification("merge", new Object[] {column, row, endCellColumn, row});
                    sendMergeCommandForAppropriateCells(child.getAsJsonObject(), tableName, endCellColumn, startColumn, startRow);
                }
            }
            else {
                closestChildColumn = closestChild.get("column").getAsInt() + startColumn;

                if (closestChildColumn > column + 1) {
                    App.SSS.sendNotification("merge", new Object[] {column, row, closestChildColumn - 1, row});
                    sendMergeCommandForAppropriateCells(child.getAsJsonObject(), tableName, closestChildColumn - 1, startColumn, startRow);
                }
            }
        }
    }

    private static void sendBoldTextCommandForAppropriateCells(JsonObject jsonObject, int startColumn, int startRow) {
        List<JsonObject> headerCellsExceptAttributes = findHeaderCellsExceptAttributes(jsonObject);

        for (JsonObject cell : headerCellsExceptAttributes) {
            int column = cell.get("column").getAsInt() + startColumn;
            int row = cell.get("row").getAsInt() + startRow;
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

    private static void sendCenterTextCommandForAppropriateCells(int[] rangeOfCells) {
        for (int i = rangeOfCells[0]; i <= rangeOfCells[2]; i++) {
            for (int j = rangeOfCells[1]; j <= rangeOfCells[3]; j++) {
                App.SSS.sendNotification("center-text", new Object[] {i, j});
            }
        }
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