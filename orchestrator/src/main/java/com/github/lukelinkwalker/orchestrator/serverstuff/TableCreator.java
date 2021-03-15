package com.github.lukelinkwalker.orchestrator.serverstuff;

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
    private static JsonArray ssmodel;

    public static void main(String[] args) throws IOException {
        setup();
        receiveFromSpreadsheet("getTableNames", new Object[] {});
        System.out.println();
        receiveFromSpreadsheet("getTableRange", new Object[] {"Config"});
        System.out.println();
        receiveFromSpreadsheet("createTable", new Object[] {"Config"});
    }

    private static void setup() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get("orchestrator/src/main/java/com/github/lukelinkwalker/orchestrator/serverstuff/ssmodel.json"));
        ssmodel = gson.fromJson(reader, JsonArray.class);
    }

    private static void createTable(String name) {
        JsonObject tableObject = findTableJsonObject(name);

        int[] tableHeaderAreaIndexes = getTableHeaderAreaIndexes(tableObject);
        int[] tableDataAreaIndexes = getTableDataAreaIndexes(tableHeaderAreaIndexes);

        sendTextCommandForAppropriateCells(tableObject);
        sendMergeCommandForAppropriateCells(tableObject, name, tableHeaderAreaIndexes[2]);
        sendBoldTextCommandForAppropriateCells(tableObject);
        sendCenterTextCommandForAppropriateCells(tableHeaderAreaIndexes);
        sendCenterTextCommandForAppropriateCells(tableDataAreaIndexes);
        sendBlackBorderCommandForRangeOfCells(tableHeaderAreaIndexes);
        sendBlackBorderCommandForRangeOfCells(tableDataAreaIndexes);
        sendSetAsHeaderCommandForAppropriateCells(tableHeaderAreaIndexes);
        sendSetAsDataCommandForAppropriateCells(tableDataAreaIndexes);
    }

    private static int[] getTableHeaderAreaIndexes(JsonObject tableObject) {
        int startCellColumn = tableObject.get("column").getAsInt();
        int startCellRow = tableObject.get("row").getAsInt();
        int[] endCellIndexes = findEndCellIndexes(tableObject);

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
        for (JsonElement jsonElement : ssmodel) {
            String objectName = jsonElement.getAsJsonObject().get("name").getAsString();

            if (name.equals(objectName)) return jsonElement.getAsJsonObject();
        }

        return null;
    }

    private static int[] findEndCellIndexes(JsonObject jsonObject) {
        int column = jsonObject.get("column").getAsInt();
        int row = jsonObject.get("row").getAsInt();
        int[] biggestEndCellIndexes = new int[] {column, row};

        for (JsonElement jsonElement : jsonObject.get("children").getAsJsonArray()) {
            int[] thisCellIndexes = findEndCellIndexes(jsonElement.getAsJsonObject());

            if (thisCellIndexes[0] > biggestEndCellIndexes[0]) biggestEndCellIndexes[0] = thisCellIndexes[0];
            if (thisCellIndexes[1] > biggestEndCellIndexes[1]) biggestEndCellIndexes[1] = thisCellIndexes[1];
        }

        return biggestEndCellIndexes;
    }

    private static void sendTableNamesCommand() {
        List<String> tableNames = new ArrayList<>();

        for (JsonElement table : ssmodel) {
            tableNames.add(table.getAsJsonObject().get("name").getAsString());
        }

        Object[] parameters = tableNames.toArray();

        sendToSpreadsheet("tableNames", parameters);
    }

    private static void sendTableRangeCommand(String name) {
        JsonObject tableObject = findTableJsonObject(name);
        int[] tableHeaderAreaIndexes = getTableHeaderAreaIndexes(tableObject);
        int[] tableDataAreaIndexes = getTableDataAreaIndexes(tableHeaderAreaIndexes);

        sendToSpreadsheet("tableRange", new Object[] {tableHeaderAreaIndexes[0], tableHeaderAreaIndexes[1],
                tableDataAreaIndexes[2], tableDataAreaIndexes[3]});
    }

    private static void sendTextCommandForAppropriateCells(JsonObject jsonObject) {
        String name = jsonObject.get("name").getAsString();
        int column = jsonObject.get("column").getAsInt();
        int row = jsonObject.get("row").getAsInt();
        JsonArray children = jsonObject.get("children").getAsJsonArray();

        sendToSpreadsheet("setText", new Object[] {column, row, name});

        for (JsonElement child : children) {
            sendTextCommandForAppropriateCells(child.getAsJsonObject());
        }
    }

    private static void sendMergeCommandForAppropriateCells(JsonObject jsonObject, String tableName, int endCellColumn) {
        String name = jsonObject.get("name").getAsString();
        JsonArray children = jsonObject.get("children").getAsJsonArray();

        if (name.equals(tableName)) {
            int column = jsonObject.get("column").getAsInt();
            int row = jsonObject.get("row").getAsInt();

            sendToSpreadsheet("merge", new Object[] {column, row, endCellColumn, row});
        }

        for (JsonElement child : children) {
            JsonArray childrenWithoutThis = children.deepCopy();
            childrenWithoutThis.remove(child);

            int column = child.getAsJsonObject().get("column").getAsInt();
            int row = child.getAsJsonObject().get("row").getAsInt();
            JsonObject closestChild = null;
            int closestChildColumn;

            for (JsonElement otherChild : childrenWithoutThis) {
                int otherColumn = otherChild.getAsJsonObject().get("column").getAsInt();
                int distanceFromChildAndOtherChild = otherColumn - column;

                if (closestChild == null) {
                    if (distanceFromChildAndOtherChild > 0) closestChild = otherChild.getAsJsonObject();
                }
                else {
                    closestChildColumn = closestChild.get("column").getAsInt();
                    int distanceFromChildAndClosetsChild = closestChildColumn - column;

                    if (distanceFromChildAndOtherChild > 0 && distanceFromChildAndOtherChild <
                            distanceFromChildAndClosetsChild) closestChild = otherChild.getAsJsonObject();
                }
            }

            if (closestChild == null) {
                if (column != endCellColumn) {
                    sendToSpreadsheet("merge", new Object[] {column, row, endCellColumn, row});
                    sendMergeCommandForAppropriateCells(child.getAsJsonObject(), tableName, endCellColumn);
                }
            }
            else {
                closestChildColumn = closestChild.get("column").getAsInt();

                if (closestChildColumn > column + 1) {
                    sendToSpreadsheet("merge", new Object[] {column, row, closestChildColumn - 1, row});
                    sendMergeCommandForAppropriateCells(child.getAsJsonObject(), tableName, closestChildColumn - 1);
                }
            }
        }
    }

    private static void sendBoldTextCommandForAppropriateCells(JsonObject jsonObject) {
        List<JsonObject> headerCellsExceptAttributes = findHeaderCellsExceptAttributes(jsonObject);

        for (JsonObject cell : headerCellsExceptAttributes) {
            int column = cell.get("column").getAsInt();
            int row = cell.get("row").getAsInt();
            sendToSpreadsheet("boldText", new Object[] {column, row});
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
                sendToSpreadsheet("centerText", new Object[] {i, j});
            }
        }
    }

    private static void sendBlackBorderCommandForRangeOfCells(int[] rangeOfCells) {
        for (int i = rangeOfCells[0]; i <= rangeOfCells[2]; i++) {
            for (int j = rangeOfCells[1]; j <= rangeOfCells[3]; j++) {
                sendToSpreadsheet("blackBorder", new Object[] {i, j});
            }
        }
    }

    private static void sendSetAsHeaderCommandForAppropriateCells(int[] rangeOfCells) {
        for (int i = rangeOfCells[0]; i <= rangeOfCells[2]; i++) {
            for (int j = rangeOfCells[1]; j <= rangeOfCells[3]; j++) {
                sendToSpreadsheet("setAsHeader", new Object[] {i, j});
            }
        }
    }

    private static void sendSetAsDataCommandForAppropriateCells(int[] rangeOfCells) {
        for (int i = rangeOfCells[0]; i <= rangeOfCells[2]; i++) {
            for (int j = rangeOfCells[1]; j <= rangeOfCells[3]; j++) {
                sendToSpreadsheet("setAsData", new Object[] {i, j});
            }
        }
    }

    private static void receiveFromSpreadsheet(String command, Object[] parameters) {
        //TODO: Setup with JSON-RPC
        switch (command) {
            case "getTableNames":
                sendTableNamesCommand();
                break;
            case "getTableRange":
                sendTableRangeCommand(parameters[0].toString());
                break;
            case "createTable":
                createTable(parameters[0].toString());
                break;
        }
    }

    private static void sendToSpreadsheet(String command, Object[] parameters) {
        //TODO: Setup with JSON-RPC
        switch (command) {
            case "tableNames":
                String[] stringArray = Arrays.copyOf(parameters, parameters.length, String[].class);
                String stringJoiner = String.join("', '", stringArray);
                System.out.println("testCommand('tableNames', ['" + stringJoiner + "'])");
                break;
            case "tableRange":
                System.out.println("testCommand('tableRange', [" + parameters[0] + ", " + parameters[1] + ", " + parameters[2] + ", " + parameters[3] + "])");
                break;
            case "setText":
                System.out.println("testCommand('setText', [" + parameters[0] + " + parseInt(column), " + parameters[1] + " + parseInt(row), '" + parameters[2] + "'])");
                break;
            case "merge":
                System.out.println("testCommand('merge', [" + parameters[0] + " + parseInt(column), " + parameters[1] + " + parseInt(row), " + parameters[2] + " + parseInt(column), " + parameters[3] + " + parseInt(row)])");
                break;
            case "boldText":
                System.out.println("testCommand('boldText', [" + parameters[0] + " + parseInt(column), " + parameters[1] + " + parseInt(row)])");
                break;
            case "centerText":
                System.out.println("testCommand('centerText', [" + parameters[0] + " + parseInt(column), " + parameters[1] + " + parseInt(row)])");
                break;
            case "blackBorder":
                System.out.println("testCommand('blackBorder', [" + parameters[0] + " + parseInt(column), " + parameters[1] + " + parseInt(row)])");
                break;
            case "setAsHeader":
                System.out.println("testCommand('setAsHeader', [" + parameters[0] + " + parseInt(column), " + parameters[1] + " + parseInt(row)])");
                break;
            case "setAsData":
                System.out.println("testCommand('setAsData', [" + parameters[0] + " + parseInt(column), " + parameters[1] + " + parseInt(row)])");
                break;
        }
    }
}
