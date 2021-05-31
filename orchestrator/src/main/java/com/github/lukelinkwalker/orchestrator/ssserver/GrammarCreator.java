package com.github.lukelinkwalker.orchestrator.ssserver;

import com.github.lukelinkwalker.orchestrator.App;
import com.github.lukelinkwalker.orchestrator.Util.StringUtilities;
import com.google.gson.*;

import java.util.StringJoiner;

public class GrammarCreator {
    private static StringBuilder model;
    private static StringBuilder tables;
    private static int tablesInitialLength;

    public static String createGrammar() {
        JsonArray ssModel = App.SSS.getSDSLSSModel();
        StringBuilder terminals = new StringBuilder();
        model = new StringBuilder();
        tables = new StringBuilder();

        model.append("Model:\n")
                .append("   {Model}\n")
                .append("   '[' tables += Table? (',' tables += Table)* ']'\n")
                .append(";\n");
        tables.append("\nTable:\n\n;\n");

        int modelLengthBeforeIteratingRoot = model.length();
        tablesInitialLength = tables.length();

        ssModelOuterArrayIterator(ssModel);
        model.insert(modelLengthBeforeIteratingRoot, tables);
        terminals.append(insertDefaultTerminalRules());
        model.append(terminals);

        return model.toString();
    }

    private static void ssModelOuterArrayIterator(JsonArray jsonArray) {
        for (JsonElement jsonElement : jsonArray) print(jsonElement.getAsJsonObject(), "");
    }

    private static StringBuilder print(JsonObject jsonObject, String parentName) {
        String type = jsonObject.get("type").getAsString();

        switch (type) {
            case "object":
                return printObject(jsonObject, parentName);
            case "array":
                return printArray(jsonObject, parentName);
            case "alternative":
                return printAlternative(jsonObject);
            case "attribute":
                return printAttribute(jsonObject);
            case "rules":
                printRules(jsonObject);
                break;
            default:
                break;
        }

        return new StringBuilder();
    }

    public static StringBuilder printObject(JsonObject jsonObject, String parentName) {
        String name = StringUtilities.removeTokensFromString(jsonObject.get("name").getAsString());
        JsonArray children = jsonObject.getAsJsonArray("children");
        StringBuilder text = new StringBuilder("\n" + parentName + name + ":\n   '{'");
        StringBuilder sb = new StringBuilder();
        boolean firstChild = true;

        sb.append("\n      '\"").append(name).append("\"' ':' '{'\n");
        model.append(printTable(name, parentName));
        model.append(printBreakoutReference(name, parentName));
        sb.append("         ").append(StringUtilities.makeFirstLetterLowerCase(name)).append(" = (").append(parentName)
                .append(name).append(" | ").append(parentName).append(name).append("Reference)").append("\n")
                .append("      '}'");

        int indexBeforeIterating = model.length();

        for (JsonElement child : children) {
            boolean childIsOptional = child.getAsJsonObject().get("isOptional").getAsBoolean();
            String childText = print(child.getAsJsonObject(),parentName + name).toString();

            if (!firstChild) {
                StringBuilder prependText = new StringBuilder("','");
                String appendText = "";

                if (childIsOptional) {
                    prependText.insert(0, "(");
                    appendText = ")? ";
                }
                text.append(prependText).append(childText).append(appendText);
            }
            else text.append(childText);

            firstChild = false;
        }

        text.append("\n   '}'\n;\n");
        model.insert(indexBeforeIterating, text);

        return sb;
    }

    public static StringBuilder printArray(JsonObject jsonObject, String parentName) {
        String name = StringUtilities.removeTokensFromString(jsonObject.get("name").getAsString());
        JsonArray children = jsonObject.getAsJsonArray("children");
        StringBuilder sb = new StringBuilder();
        StringBuilder assignment = new StringBuilder();

        sb.append("\n      '\"").append(name).append("\"' ':' '['\n");
        model.append(printTable(name, parentName));
        assignment.append(StringUtilities.makeFirstLetterLowerCase(name)).append(" += ").append(parentName).append(name)
                .append(" (',' ").append(StringUtilities.makeFirstLetterLowerCase(name)).append(" += ")
                .append(parentName).append(name).append(")*");

        //If the object have a parent, it can be referenced to by another table. A reference rule is thus needed.
        if (!parentName.equals("")) {
            model.append(printBreakoutReference(name, parentName));
            assignment.insert(0, "((");
            assignment.append(") | (").append(StringUtilities.makeFirstLetterLowerCase(name)).append(" += ")
                    .append(parentName).append(name).append("Reference").append(" (',' ")
                    .append(StringUtilities.makeFirstLetterLowerCase(name)).append(" += ").append(parentName)
                    .append(name).append("Reference)*))");
        }

        sb.append("         ").append(assignment).append("\n      ']'");
        int indexBeforeIterating = model.length();

        StringBuilder text = new StringBuilder("\n" + parentName + name + ":\n   '{'");
        boolean firstChild = true;

        for (JsonElement child : children) {
            boolean childIsOptional = child.getAsJsonObject().get("isOptional").getAsBoolean();
            String childText = print(child.getAsJsonObject(),parentName + name).toString();

            if (!firstChild) {
                StringBuilder prependText = new StringBuilder("','");
                String appendText = "";

                if (childIsOptional) {
                    prependText.insert(0, "(");
                    appendText = ")? ";
                }
                text.append(prependText).append(childText).append(appendText);
            }
            else text.append(childText);

            firstChild = false;
        }

        text.append("\n   '}'\n;\n");
        model.insert(indexBeforeIterating, text);

        return sb;
    }

    public static StringBuilder printAlternative(JsonObject jsonObject) {
        String name = StringUtilities.removeTokensFromString(jsonObject.get("name").getAsString());
        JsonArray dataTypes = jsonObject.get("dataTypes").getAsJsonArray();
        StringJoiner sj = new StringJoiner(" | ");
        StringBuilder sb = new StringBuilder();

        for (JsonElement jsonElement : dataTypes) {
            String value = jsonElement.getAsJsonObject().get("value").getAsString();
            String type = jsonElement.getAsJsonObject().get("type").getAsString();
            String valueName = getCorrectValueName(StringUtilities.makeFirstLetterLowerCase(name));
            String formattedValue;

            if (type.equals("predefined")) formattedValue = value.toUpperCase();
            else {
                value = StringUtilities.removeTokensFromString(value);
                if (type.equals("reference")) formattedValue = "[" + value + "|STRING]";
                else formattedValue = value;
            }
            formattedValue = StringUtilities.replaceWhiteSpaceWithUnderscore(formattedValue);
            sj.add(valueName + value + " = " + formattedValue);
        }

        sb.append("\n")
                .append("      '\"").append(name).append("\"' ':' '{'\n")
                .append("         '\"column\"' ':' ").append(StringUtilities.makeFirstLetterLowerCase(name)).append("Column = INT").append(" ','\n")
                .append("         '\"row\"' ':' ").append(StringUtilities.makeFirstLetterLowerCase(name)).append("Row = INT").append(" ','\n")
                .append("         '\"value\"' ':' (").append(sj).append(")\n")
                .append("      '}'");

        return sb;
    }

    public static StringBuilder printAttribute(JsonObject jsonObject) {
        StringBuilder sb = new StringBuilder();
        String name = StringUtilities.removeTokensFromString(jsonObject.get("name").getAsString());
        JsonObject dataTypeObject = jsonObject.get("dataTypes").getAsJsonArray().get(0).getAsJsonObject();
        String type = dataTypeObject.get("type").getAsString();
        String value = dataTypeObject.get("value").getAsString();
        String valueName = getCorrectValueName(StringUtilities.makeFirstLetterLowerCase(name));
        if (type.equals("predefined")) value = value.toUpperCase();
        else if (type.equals("reference")) value = "[" + StringUtilities.removeTokensFromString(value) + "|STRING]";
        else value = StringUtilities.removeTokensFromString(value);
        String assignment = valueName + " = " + value;

        sb.append("\n")
                .append("      '\"").append(name).append("\"' ':' '{'\n")
                .append("         '\"column\"' ':' ").append(StringUtilities.makeFirstLetterLowerCase(name)).append("Column = INT").append(" ','\n")
                .append("         '\"row\"' ':' ").append(StringUtilities.makeFirstLetterLowerCase(name)).append("Row = INT").append(" ','\n")
                .append("         '\"value\"' ':' ").append(wrapInDoubleQuotesBasedOnDataType(type, assignment)).append("\n")
                .append("      '}' ");

        return sb;
    }

    public static StringBuilder printTable(String objectName, String parentName) {
        String name = parentName + objectName;
        StringBuilder sb = new StringBuilder();

        if (tables.length() == tablesInitialLength) tables.insert(tables.length() - 3,"   " + name + "Table");
        else tables.insert(tables.length() - 3, " | " + name + "Table");

        sb.append("\n").append(name).append("Table:\n")
                .append("   {").append(name).append("Table}\n")
                .append("   '{'\n")
                .append("      '\"Name\"' ':' '\"").append(name).append("\"' ','\n")
                .append("      '\"Table\"' ':' '['\n")
                .append("           ").append(StringUtilities.makeFirstLetterLowerCase(objectName)).append(" += ").append(name).append("? (',' ")
                    .append(StringUtilities.makeFirstLetterLowerCase(objectName)).append(" += ").append(name).append(")*\n")
                .append("      ']'\n")
                .append("   '}'\n")
                .append(";\n");

        return sb;
    }

    public static void printRules(JsonObject jsonObject) {
        JsonArray children = jsonObject.getAsJsonArray("children");

        for (JsonElement child : children) {
            JsonObject ruleObject = child.getAsJsonObject().get("rule").getAsJsonObject();
            String rule = ruleObject.get("value").getAsString();

            model.append("\n").append(rule).append("\n");
        }
    }

    public static StringBuilder printBreakoutReference(String objectName, String parentName) {
        String name = parentName + objectName;
        StringBuilder sb = new StringBuilder();

        sb.append("\n").append(name).append("Reference:\n")
                .append("   '{'\n")
                .append("      '\"Name\"' ':' '{'\n")
                .append("         '\"column\"' ':' column = INT ','\n")
                .append("         '\"row\"' ':' row = INT ','\n")
                .append("         '\"value\"' ':' name = [").append(name).append("|STRING]\n")
                .append("      '}'\n")
                .append("   '}'\n")
                .append(";\n");

        return sb;
    }

    private static String insertDefaultTerminalRules() {
        return
                "\nterminal NULL: 'null';\n" +
                "\nterminal ID: '^'?('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;\n" +
                "\nterminal STRING: '\"OKZVVTSPKHOVYSMU' -> 'SQPSUQMWUPQSBXDT\"';\n" +
                "\nterminal INT returns ecore::EInt: ('0'..'9')+;\n" +
                "\nterminal FLOAT: '-'? INT? '.' INT (('E'|'e') '-'? INT)?;\n" +
                "\nterminal BOOLEAN: 'true' | 'false';\n" +
                "\nterminal ML_COMMENT: '/*' -> '*/';\n" +
                "\nterminal SL_COMMENT: '//' !('\\n'|'\\r')* ('\\r'? '\\n')?;\n" +
                "\nterminal WS: (' '|'\\t'|'\\r'|'\\n')+;\n" +
                "\nterminal ANY_OTHER: .;";
    }

    private static String wrapInDoubleQuotesBasedOnDataType(String dataType, String string) {
        if (dataType.equals("custom")) return "'\"' " + string + " '\"' ";
        else return string;
    }

    //If attribute name is name, it shall be used to cross-reference. Thus, the assignment name shall be name instead
    //of name + Value.
    private static String getCorrectValueName(String string) {
        if (string.equals("name")) return string;
        else return string + "Value";
    }
}