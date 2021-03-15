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
import java.util.StringJoiner;

public class GrammarCreator {
    private static final StringBuilder model = new StringBuilder();
    private static final StringBuilder terminals = new StringBuilder();
    private static final StringBuilder tables = new StringBuilder();
    private static final List<String> terminalsList = new ArrayList<>(Arrays.asList("String", "int", "float", "boolean", "null"));

    public static void main(String[] args) throws IOException {
        setup();
        System.out.println(model);
    }

    private static void setup() throws IOException {
        model.append("Model:\n'[' tables += Table (',' tables += Table)* ']';\n");
        tables.append("\nTable:;\n");

        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get("orchestrator/src/main/java/com/github/lukelinkwalker/orchestrator/serverstuff/sdslExample.json"));
        JsonArray root = gson.fromJson(reader, JsonArray.class);
        rootIterator(root);

        terminals.append("\nterminal STRING: '\"' \"'\" ( '\\\\' . /* 'b'|'t'|'n'|'f'|'r'|'u'|'\"'|\"'\"|'\\\\' " +
                "*/ | !('\\\\'|\"'\") )* \"'\" '\"';\n" +
                "\nterminal INT returns ecore::EInt: ('0'..'9')+;\n" +
                "\nterminal FLOAT: '-'? INT? '.' INT (('E'|'e') '-'? INT)?;\n" +
                "\nterminal BOOLEAN: 'true' | 'false';\n" +
                "\nterminal NULL: 'null';\n" +
                "\nterminal ML_COMMENT: '/*' -> '*/';\n" +
                "\nterminal SL_COMMENT: '//' !('\\n'|'\\r')* ('\\r'? '\\n')?;\n" +
                "\nterminal WS: (' '|'\\t'|'\\r'|'\\n')+;\n" +
                "\nterminal ANY_OTHER: .;");

        model.insert(55, tables);
        model.append(terminals);

        reader.close();
    }

    private static void rootIterator(JsonArray jsonArray) {
        for (JsonElement jsonElement : jsonArray) {
            String type = jsonElement.getAsJsonObject().get("type").getAsString();

            if (type.equals("object")) printTable(jsonElement.getAsJsonObject());
            else print(jsonElement.getAsJsonObject());
        }
    }

    private static StringBuilder print(JsonObject jsonObject) {
        String type = jsonObject.get("type").getAsString();

        switch (type) {
            case "attribute":
                return printAttribute(jsonObject);
            case "array":
                return printArray(jsonObject);
            case "alternative":
                return printAlternative(jsonObject);
            case "object":
                return printObject(jsonObject);
            default:
                if (jsonObject.get("name").getAsString().equals("TypeTerminal")) printTypeTerminal(jsonObject);
                else printMethodTerminal(jsonObject);
                return new StringBuilder();
        }
    }

    private static void printTable(JsonObject jsonObject) {
        String name = jsonObject.get("name").getAsString();

        if (tables.length() == 9) tables.insert(tables.length() - 2,"\n" + name + "Table");
        else tables.insert(tables.length() - 2, " | " + name + "Table");

        model.append("\n").append(name).append("Table:\n'{' '\"Name\"' ':' '\"").append(name).append("\"' ',' '\"Table\"' ':' '[' ");
        int indexBeforeIterating = model.length();
        String objectString = printObject(jsonObject).toString();
        model.insert(indexBeforeIterating, objectString);
        model.insert(indexBeforeIterating + objectString.length()," ']' '}';\n");
    }

    private static StringBuilder printObject(JsonObject jsonObject) {
        String name = jsonObject.get("name").getAsString();

        JsonArray children = jsonObject.getAsJsonArray("children");
        int indexBeforeIterating = model.length();

        String prefix = "\n" + name + ":\n'{' ";
        String suffix = "'}';\n";
        StringJoiner stringJoiner = new StringJoiner(" ',' ", prefix, suffix);

        for (JsonElement jsonElement : children) {
            String string = print(jsonElement.getAsJsonObject()).toString();
            stringJoiner.add(string);
        }

        model.insert(indexBeforeIterating, stringJoiner);

        return new StringBuilder().append(name.toLowerCase()).append("s += ").append(name).append(" (',' ").
                append(name.toLowerCase()).append("s += ").append(name).append(")*");
    }

    private static StringBuilder printArray(JsonObject jsonObject) {
        String name = jsonObject.get("name").getAsString();
        boolean isOptional = jsonObject.get("isOptional").getAsBoolean();

        model.append("\n").append(name).append("List:\n'\"").append(name).append("List\"' ':' '[' ").append(name.toLowerCase()).
                append("s += ").append(name).append(" (',' ").append(name.toLowerCase()).append("s += ").
                append(name).append(")* ']';\n");

        printObject(jsonObject);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name.toLowerCase()).append("List = ").append(name).append("List");
        if (isOptional) printOptional(stringBuilder);

        return stringBuilder;
    }

    private static StringBuilder printAlternative(JsonObject jsonObject) {
        String name = jsonObject.get("name").getAsString();
        JsonArray dataTypes = jsonObject.get("dataType").getAsJsonArray();
        boolean isOptional = jsonObject.get("isOptional").getAsBoolean();

        String prefix = "\n" + name + "Alternative:\n'\"" + name + "Alternative\"'" + " ':' '{' (";
        String suffix = ") '}';\n";
        StringJoiner stringJoiner = new StringJoiner(" | ", prefix, suffix);

        for (JsonElement jsonElement : dataTypes) {
            String dataType = jsonElement.getAsString();
            if (terminalsList.contains(dataType)) dataType = dataType.toUpperCase();
            stringJoiner.add(dataType);
        }

        model.append(stringJoiner);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name.toLowerCase()).append("Alternative = ").append(name).append("Alternative");
        if (isOptional) printOptional(stringBuilder);

        return stringBuilder;
    }

    private static StringBuilder printAttribute(JsonObject jsonObject) {
        StringBuilder stringBuilder = new StringBuilder();
        String name = jsonObject.get("name").getAsString();
        String dataType = jsonObject.get("dataType").getAsString().toUpperCase();
        boolean isOptional = jsonObject.get("isOptional").getAsBoolean();

        stringBuilder.append("'\"").append(name).append("\"' ':' ").append(name.toLowerCase()).append(" = ").
                append(dataType);
        if (isOptional) printOptional(stringBuilder);

        return stringBuilder;
    }

    private static void printOptional(StringBuilder stringBuilder) {
        stringBuilder.insert(0, "(");
        stringBuilder.append(")?");
    }

    private static void printMethodTerminal(JsonObject jsonObject) {
        JsonArray children = jsonObject.getAsJsonArray("children");

        for (JsonElement jsonElement : children) {
            JsonArray parameters = jsonElement.getAsJsonObject().getAsJsonArray("parameters");
            String name = jsonElement.getAsJsonObject().get("name").getAsString();

            String prefix =  "\n" + name + ":\n'\"" + name + "\"' ':' '{' ";
            String suffix = "'}';\n";

            StringJoiner stringJoiner = new StringJoiner(" ',' ", prefix, suffix);

            for (JsonElement jsonElementInMethod : parameters) {
                String parameterName = jsonElementInMethod.getAsJsonObject().get("name").getAsString();
                String dataType = jsonElementInMethod.getAsJsonObject().get("dataType").getAsString().toUpperCase();

                stringJoiner.add("'\"" + parameterName + "\"' ':' " + parameterName + " = " + dataType);
            }
            model.append(stringJoiner);
        }
    }

    private static void printTypeTerminal(JsonObject jsonObject) {
        JsonArray children = jsonObject.getAsJsonArray("children");

        for (JsonElement jsonElement : children) {
            String name = jsonElement.getAsJsonObject().get("name").getAsString().toUpperCase();
            String rule = jsonElement.getAsJsonObject().get("rule").getAsString();

            terminalsList.add(name);
            terminals.append("\nterminal ").append(name).append(": ").append(rule).append(";\n");
        }
    }
}
