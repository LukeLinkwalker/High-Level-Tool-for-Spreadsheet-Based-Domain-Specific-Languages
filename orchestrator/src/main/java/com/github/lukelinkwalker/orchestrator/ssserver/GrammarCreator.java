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
                return printAlternative(jsonObject, parentName);
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

    private static StringBuilder printObject(JsonObject jsonObject, String parentName) {
        String name = StringUtilities.removeTokensFromString(jsonObject.get("name").getAsString());
        boolean isOptional = jsonObject.get("isOptional").getAsBoolean();
        JsonArray children = jsonObject.getAsJsonArray("children");
        String prefix = "\n" + parentName + name + ":\n   '{'";
        String suffix = "\n   '}'\n;\n";
        StringJoiner stringJoiner = new StringJoiner(" (',')? ", prefix, suffix);
        StringBuilder sb = new StringBuilder();

        sb.append("\n      '\"").append(name).append("\"' ':' '{'\n");
        model.append(printTable(name, parentName));

        model.append(printBreakoutReference(name, parentName));
        sb.append("         ").append(makeFirstLetterLowerCase(name)).append(" = (").append(parentName).append(name)
                    .append(" | ").append(parentName).append(name).append("Reference)")
                    .append(makeOptionalIfTrue(isOptional)).append("\n")
                .append("      '}'");

        int indexBeforeIterating = model.length();

        for (JsonElement jsonElement : children) stringJoiner.add(print(jsonElement.getAsJsonObject(),
                parentName + name).toString());

        model.insert(indexBeforeIterating, stringJoiner);

        return sb;
    }

    private static StringBuilder printArray(JsonObject jsonObject, String parentName) {
        String name = StringUtilities.removeTokensFromString(jsonObject.get("name").getAsString());
        boolean isOptional = jsonObject.get("isOptional").getAsBoolean();
        JsonArray children = jsonObject.getAsJsonArray("children");
        String prefix = "\n" + parentName + name + ":\n   '{'";
        String suffix = "\n   '}'\n;\n";
        StringJoiner stringJoiner = new StringJoiner(" (',')? ", prefix, suffix);
        StringBuilder sb = new StringBuilder();
        StringBuilder assignment = new StringBuilder();

        sb.append("\n      '\"").append(name).append("\"' ':' '['\n");
        model.append(printTable(name, parentName));

        assignment.append(makeFirstLetterLowerCase(name)).append(" += ").append(parentName).append(name)
                .append(makeOptionalIfTrue(isOptional)).append(" (',' ")
                .append(makeFirstLetterLowerCase(name)).append(" += ").append(parentName).append(name).append(")*");

        //If the object have a parent, it can be referenced to by another table. A reference rule is thus needed.
        if (!parentName.equals("")) {
            model.append(printBreakoutReference(name, parentName));

            assignment.insert(0, "((");
            assignment.append(") | (").append(makeFirstLetterLowerCase(name)).append(" += ").append(parentName)
                    .append(name).append("Reference").append(makeOptionalIfTrue(isOptional)).append(" (',' ")
                    .append(makeFirstLetterLowerCase(name)).append(" += ").append(parentName).append(name)
                    .append("Reference)*))");
        }

        sb.append("         ").append(assignment).append("\n      ']'");
        int indexBeforeIterating = model.length();

        for (JsonElement jsonElement : children) stringJoiner.add(print(jsonElement.getAsJsonObject(),
                parentName + name).toString());

        model.insert(indexBeforeIterating, stringJoiner);

        return sb;
    }

    private static StringBuilder printAlternative(JsonObject jsonObject, String parentName) {
        String name = StringUtilities.removeTokensFromString(jsonObject.get("name").getAsString());
        boolean isOptional = jsonObject.get("isOptional").getAsBoolean();
        JsonArray dataTypes = jsonObject.get("dataTypes").getAsJsonArray();
        StringJoiner sj = new StringJoiner(" | ");
        StringBuilder sb = new StringBuilder();

        for (JsonElement jsonElement : dataTypes) {
            String dataType = jsonElement.getAsJsonObject().get("value").getAsString();
            String type = jsonElement.getAsJsonObject().get("type").getAsString();

            if (type.equals("predefined")) dataType = dataType.toUpperCase();
            else dataType = StringUtilities.removeTokensFromString(dataType);
            dataType = replaceWhiteSpaceWithUnderscore(dataType);
            sj.add(makeFirstLetterLowerCase(name) + "Value" + dataType + " = " + dataType);
        }

        sb.append("\n")
                .append("      '\"").append(name).append("\"' ':' '{'\n")
                .append("         '\"column\"' ':' ").append(makeFirstLetterLowerCase(name)).append("Column = INT").append(" ','\n")
                .append("         '\"row\"' ':' ").append(makeFirstLetterLowerCase(name)).append("Row = INT").append(" ','\n")
                .append("         '\"value\"' ':' (").append(sj).append(")\n")
                .append("      '}'");

        if (isOptional) makeAttributeOptionalIfTrue(sb);

        return sb;
    }

    private static StringBuilder printAttribute(JsonObject jsonObject) {
        StringBuilder sb = new StringBuilder();
        String name = StringUtilities.removeTokensFromString(jsonObject.get("name").getAsString());
        boolean isOptional = jsonObject.get("isOptional").getAsBoolean();

        JsonObject dataTypeObject = jsonObject.get("dataTypes").getAsJsonArray().get(0).getAsJsonObject();
        String type = dataTypeObject.get("type").getAsString();
        String dataType = dataTypeObject.get("value").getAsString();
        String valueName = getCorrectValueName(makeFirstLetterLowerCase(name));
        if (type.equals("predefined")) dataType = dataType.toUpperCase();
        else dataType = StringUtilities.removeTokensFromString(dataType);
        String value = valueName + " = " + dataType;

        sb.append("\n")
                .append("      '\"").append(name).append("\"' ':' '{'\n")
                .append("         '\"column\"' ':' ").append(makeFirstLetterLowerCase(name)).append("Column = INT").append(" ','\n")
                .append("         '\"row\"' ':' ").append(makeFirstLetterLowerCase(name)).append("Row = INT").append(" ','\n")
                .append("         '\"value\"' ':' ").append(wrapInDoubleQuotesBasedOnDataType(dataType, value)).append("\n")
                .append("      '}'");

        if (isOptional) makeAttributeOptionalIfTrue(sb);

        return sb;
    }

    private static StringBuilder printTable(String objectName, String parentName) {
        String name = parentName + objectName;
        StringBuilder sb = new StringBuilder();

        if (tables.length() == tablesInitialLength) tables.insert(tables.length() - 3,"   " + name + "Table");
        else tables.insert(tables.length() - 3, " | " + name + "Table");

        sb.append("\n").append(name).append("Table:\n")
                .append("   {").append(name).append("Table}\n")
                .append("   '{'\n")
                .append("      '\"Name\"' ':' '\"").append(name).append("\"' ','\n")
                .append("      '\"Table\"' ':' '['\n")
                .append("           ").append(makeFirstLetterLowerCase(objectName)).append(" += ").append(name).append("? (',' ")
                    .append(makeFirstLetterLowerCase(objectName)).append(" += ").append(name).append(")*\n")
                .append("      ']'\n")
                .append("   '}'\n")
                .append(";\n");

        return sb;
    }

    private static void printRules(JsonObject jsonObject) {
        JsonArray children = jsonObject.getAsJsonArray("children");

        for (JsonElement child : children) {
            JsonObject ruleObject = child.getAsJsonObject().get("rule").getAsJsonObject();
            String rule = ruleObject.get("value").getAsString();

            model.append("\n").append(rule).append("\n");
        }
    }

    private static StringBuilder printBreakoutReference(String objectName, String parentName) {
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

    private static String makeOptionalIfTrue(boolean isOptional) {
        if (isOptional) return "?";
        else return "";
    }

    private static void makeAttributeOptionalIfTrue(StringBuilder sb) {
        sb.insert(0, "(");
        sb.append(")?");
    }

    private static String wrapInDoubleQuotesBasedOnDataType(String dataType, String string) {
        if (dataType.equals("STRING") | dataType.equals("INT") || dataType.equals("FLOAT") ||
                dataType.equals("BOOLEAN")) return string;
        else return "'\"' " + string + " '\"' ";
    }

    //If attribute name is name, it shall be used to cross-reference. Thus, the assignment name shall be name instead
    //of name + Value.
    private static String getCorrectValueName(String string) {
        if (string.equals("name")) return string;
        else return string + "Value";
    }

    //Terminal rule names cannot contain spaces. They therefore need to be removed.
    private static String replaceWhiteSpaceWithUnderscore(String string) {
        return string.replaceAll(" ", "_");
    }

    private static String makeFirstLetterLowerCase(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }
}