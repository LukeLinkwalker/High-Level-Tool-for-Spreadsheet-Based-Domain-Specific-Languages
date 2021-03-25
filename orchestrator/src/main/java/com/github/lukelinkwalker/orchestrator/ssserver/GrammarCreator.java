package com.github.lukelinkwalker.orchestrator.ssserver;

import com.google.gson.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringJoiner;

public class GrammarCreator {

    //TODO: Delete after testing
//    public static void main(String[] args) {
//        System.out.println(createGrammar(""));
//    }
    //////////////////////////////

    private static final StringBuilder model = new StringBuilder();
    private static final StringBuilder terminals = new StringBuilder();
    private static final StringBuilder tables = new StringBuilder();
    private static int tablesInitialLength;

    public static String createGrammar(String json) {
        model.append("Model:\n")
                .append("   {Model}\n")
                .append("   '[' tables += Table? (',' tables += Table)* ']'\n")
                .append(";\n");

        int modelLengthBeforeIteratingRoot = model.length();

        tables.append("\nTable:\n\n;\n");
        tablesInitialLength = tables.length();

        JsonArray root = JsonParser.parseString(json).getAsJsonArray();
        //TODO: Delete after testing
//        Gson gson = new Gson();
//        Reader reader = null;
//        try {
//            reader = Files.newBufferedReader(Paths.get("orchestrator/src/main/java/com/github/lukelinkwalker/orchestrator/ssserver/ssmodel.json"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        JsonArray root = gson.fromJson(reader, JsonArray.class);
        ///////////////////////////////

        rootIterator(root);
        model.insert(modelLengthBeforeIteratingRoot, tables);
        terminals.append(insertDefaultTerminalRules());
        model.append(terminals);

        //TODO: Delete after testing
//        try {
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //////////////////////////

        return model.toString();
    }

    private static void rootIterator(JsonArray jsonArray) {
        for (JsonElement jsonElement : jsonArray) print(jsonElement.getAsJsonObject(), "");
    }

    private static StringBuilder print(JsonObject jsonObject, String parentName) {
        String type = jsonObject.get("type").getAsString();

        switch (type) {
            case "object":
                return printObject(jsonObject, parentName);
            case "attribute":
                return printAttribute(jsonObject);
            case "array":
                return printArray(jsonObject, parentName);
            case "alternative":
                return printAlternative(jsonObject, parentName);
            case "customtype":
                printTypeTable(jsonObject);
                break;
            case "customrule":
                printRuleTable(jsonObject);
                break;
            default:
                break;
        }

        return new StringBuilder();
    }

    private static StringBuilder printObject(JsonObject jsonObject, String parentName) {
        String name = removeSingleQuotes(jsonObject.get("name").getAsString());
        boolean isOptional = jsonObject.get("isOptional").getAsBoolean();
        JsonArray children = jsonObject.getAsJsonArray("children");
        String prefix = "\n" + parentName + name + ":\n   '{'";
        String suffix = "\n   '}'\n;\n";
        StringJoiner stringJoiner = new StringJoiner(" ',' ", prefix, suffix);
        StringBuilder sb = new StringBuilder();

        sb.append("\n      '\"").append(name).append("\"' ':' '{'\n");
        model.append(printTable(name, parentName));

        //If the object have a parent, it can be referenced to by another table. A reference rule is thus needed.
//        if (!parentName.equals("")) {
            model.append(printBreakoutReference(name, parentName));
            sb.append("         ").append(makeFirstLetterLowerCase(name)).append(" = (").append(parentName).append(name)
                    .append(" | ").append(parentName).append(name).append("Reference)")
                    .append(returnQuestionMarkIfOptional(isOptional));
//        }
//        else sb.append("         ").append(makeFirstLetterLowerCase(name)).append(" = ").append(parentName).append(name)
//                .append(returnQuestionMarkIfOptional(isOptional));

        sb.append("\n      '}'");

        int indexBeforeIterating = model.length();

        for (JsonElement jsonElement : children) stringJoiner.add(print(jsonElement.getAsJsonObject(), parentName + name).toString());
        model.insert(indexBeforeIterating, stringJoiner);

        return sb;
    }

    private static StringBuilder printArray(JsonObject jsonObject, String parentName) {
        String name = removeSingleQuotes(jsonObject.get("name").getAsString());
        boolean isOptional = jsonObject.get("isOptional").getAsBoolean();
        JsonArray children = jsonObject.getAsJsonArray("children");
        String prefix = "\n" + parentName + name + ":\n   '{'";
        String suffix = "\n   '}'\n;\n";
        StringJoiner stringJoiner = new StringJoiner(" ',' ", prefix, suffix);
        StringBuilder sb = new StringBuilder();
        StringBuilder assignment = new StringBuilder();

        sb.append("\n      '\"").append(name).append("\"' ':' '['\n");
        model.append(printTable(name, parentName));

        assignment.append(makeFirstLetterLowerCase(name)).append(" += ").append(parentName).append(name)
                .append(returnQuestionMarkIfOptional(isOptional)).append(" (',' ")
                .append(makeFirstLetterLowerCase(name)).append(" += ").append(parentName).append(name).append(")*");

        //If the object have a parent, it can be referenced to by another table. A reference rule is thus needed.
        if (!parentName.equals("")) {
            model.append(printBreakoutReference(name, parentName));

            assignment.insert(0, "((");
            assignment.append(") | (").append(makeFirstLetterLowerCase(name)).append(" += ").append(parentName)
                    .append(name).append("Reference").append(returnQuestionMarkIfOptional(isOptional)).append(" (',' ")
                    .append(makeFirstLetterLowerCase(name)).append(" += ").append(parentName).append(name)
                    .append("Reference)*))");
        }

        sb.append("         ").append(assignment).append("\n      ']'");

        int indexBeforeIterating = model.length();

        for (JsonElement jsonElement : children) stringJoiner.add(print(jsonElement.getAsJsonObject(), parentName + name).toString());
        model.insert(indexBeforeIterating, stringJoiner);

        return sb;
    }

    private static StringBuilder printAlternative(JsonObject jsonObject, String parentName) {
        String name = removeSingleQuotes(jsonObject.get("name").getAsString());
        boolean isOptional = jsonObject.get("isOptional").getAsBoolean();
        JsonArray dataTypes = jsonObject.get("dataTypes").getAsJsonArray();

        StringJoiner sj = new StringJoiner(" | ");
        StringBuilder sb = new StringBuilder();

        for (JsonElement jsonElement : dataTypes) {
            String dataType = jsonElement.getAsJsonObject().get("value").getAsString();
            dataType = replaceWhiteSpaceWithUnderscore(dataType);
            dataType = removeSingleQuotes(dataType);
            sj.add(dataType.toUpperCase());
        }

        sb.append("\n").append(parentName).append(name).append(":\n")
                .append("   '{'\n")
                .append("      '\"column\"' ':' column = INT ','\n")
                .append("      '\"row\"' ':' row = INT ','\n")
                .append("      '\"value\"' ':' value = (").append(sj).append(")\n")
                .append("   '}'\n")
                .append(";\n");

        model.append(sb);

        StringBuilder returnSb = new StringBuilder();
        returnSb
                .append("\n      '\"").append(name).append("\"' ':' '['\n")
                .append("         ").append(makeFirstLetterLowerCase(name)).append(" += ").append(parentName)
                    .append(name).append(" (',' ").append(makeFirstLetterLowerCase(name)).append(" += ")
                    .append(parentName).append(name).append(")*\n")
                .append("      ']'");

        return returnSb;
    }

    private static StringBuilder printAttribute(JsonObject jsonObject) {
        StringBuilder sb = new StringBuilder();
        String name = removeSingleQuotes(jsonObject.get("name").getAsString());
        boolean isOptional = jsonObject.get("isOptional").getAsBoolean();

        JsonObject dataTypeObject = jsonObject.get("dataTypes").getAsJsonArray().get(0).getAsJsonObject();
        String dataType = dataTypeObject.get("value").getAsString().toUpperCase();
        String valueName = getCorrectValueName(makeFirstLetterLowerCase(name));
        String value = valueName + " = " + dataType;

        sb
                .append("\n")
                .append("      '\"").append(name).append("\"' ':' '{'\n")
                .append("         '\"column\"' ':' ").append(makeFirstLetterLowerCase(name)).append("Column = INT").append(" ','\n")
                .append("         '\"row\"' ':' ").append(makeFirstLetterLowerCase(name)).append("Row = INT").append(" ','\n")
                .append("         '\"value\"' ':' ").append(wrapInDoubleQuotesBasedOnDataType(dataType, value)).append("\n")
                .append("      '}'");

        if (isOptional) printOptional(sb);

        return sb;
    }

//    private static void printMethodTerminal(JsonObject jsonObject) {
//        JsonArray children = jsonObject.getAsJsonArray("children");
//
//        for (JsonElement jsonElement : children) {
//            JsonArray parameters = jsonElement.getAsJsonObject().getAsJsonArray("parameters");
//            String name = jsonElement.getAsJsonObject().get("name").getAsString();
//
//            String prefix =  "\n" + name + ":\n'\"" + name + "\"' ':' '{' ";
//            String suffix = "'}';\n";
//
//            StringJoiner stringJoiner = new StringJoiner(" ',' ", prefix, suffix);
//
//            for (JsonElement jsonElementInMethod : parameters) {
//                String parameterName = jsonElementInMethod.getAsJsonObject().get("name").getAsString();
//                JsonArray dataTypeArray = jsonElementInMethod.getAsJsonObject().get("dataTypes").getAsJsonArray();
//                JsonObject dataTypeObject = dataTypeArray.get(0).getAsJsonObject();
//                String dataType = dataTypeObject.get("value").getAsString().toUpperCase();
//
//                stringJoiner.add("'\"" + parameterName + "\"' ':' " + parameterName + " = " + dataType);
//            }
//            model.append(stringJoiner);
//        }
//    }

    private static StringBuilder printTable(String objectName, String parentName) {
        String name = parentName + objectName;
        StringBuilder sb = new StringBuilder();

        if (tables.length() == tablesInitialLength) tables.insert(tables.length() - 3,"   " + name + "Table");
        else tables.insert(tables.length() - 3, " | " + name + "Table");

        sb
                .append("\n").append(name).append("Table:\n")
                .append("   {").append(name).append("Table}\n")
                .append("   '{'\n")
                .append("      '\"Name\"' ':' '\"").append(objectName).append("\"' ','\n")
                .append("      '\"Table\"' ':' '['\n")
                .append("           ").append(makeFirstLetterLowerCase(objectName)).append(" += ").append(name).append("? (',' ")
                    .append(makeFirstLetterLowerCase(objectName)).append(" += ").append(name).append(")*\n")
                .append("      ']'\n")
                .append("   '}'\n")
                .append(";\n");

        return sb;
    }

    private static void printTypeTable(JsonObject jsonObject) {
        JsonArray children = jsonObject.getAsJsonArray("children");

        for (JsonElement child : children) {
            String name = removeSingleQuotes(child.getAsJsonObject().get("name").getAsString());
            JsonArray subtypes = child.getAsJsonObject().get("subtypes").getAsJsonArray();
            StringBuilder sb = new StringBuilder();
            StringJoiner sj = new StringJoiner(" | ");

            sb.append("\nterminal ").append(name.toUpperCase()).append(": '\"' '*' (");

            for (JsonElement subtype : subtypes) {
                String value = removeSingleQuotes(subtype.getAsJsonObject().get("value").getAsString());
                sj.add("'" + value + "'");
            }

            sb.append(sj).append(") '*' '\"';\n");
            terminals.append(sb);
        }
    }

    //TODO Shall we use name here?
    private static void printRuleTable(JsonObject jsonObject) {
        JsonArray children = jsonObject.getAsJsonArray("children");

        for (JsonElement child : children) {
            JsonObject nameObject = child.getAsJsonObject().get("name").getAsJsonObject();
            JsonObject ruleObject = child.getAsJsonObject().get("rule").getAsJsonObject();
            String name = removeSingleQuotes(nameObject.get("value").getAsString());
            String rule = ruleObject.get("value").getAsString();

            model.append("\n").append(rule).append("\n");
        }
    }

    private static StringBuilder printBreakoutReference(String objectName, String parentName) {
        String name = parentName + objectName;
        StringBuilder sb = new StringBuilder();

        sb
                .append("\n").append(name).append("Reference:\n")
                .append("   '{'\n")
                .append("      '\"column\"' ':' column = INT ','\n")
                .append("      '\"row\"' ':' row = INT ','\n")
                .append("      '\"name\"' ':' '\"' name = [").append(name).append("|STRING] '\"'\n")
                .append("   '}'\n")
                .append(";\n");

        return sb;
    }

    private static void printOptional(StringBuilder sb) {
        sb.insert(0, "(");
        sb.append(")?");
    }

    private static String insertDefaultTerminalRules() {
        return "\nterminal STRING: '\"' \"'\" ( '\\\\' . /* 'b'|'t'|'n'|'f'|'r'|'u'|'\"'|\"'\"|'\\\\' " +
                "*/ | !('\\\\'|\"'\") )* \"'\" '\"';\n" +
                "\nterminal INT returns ecore::EInt: ('0'..'9')+;\n" +
                "\nterminal FLOAT: '-'? INT? '.' INT (('E'|'e') '-'? INT)?;\n" +
                "\nterminal BOOLEAN: 'true' | 'false';\n" +
                "\nterminal ML_COMMENT: '/*' -> '*/';\n" +
                "\nterminal SL_COMMENT: '//' !('\\n'|'\\r')* ('\\r'? '\\n')?;\n" +
                "\nterminal WS: (' '|'\\t'|'\\r'|'\\n')+;\n" +
                "\nterminal ANY_OTHER: .;";
    }

    private static String returnQuestionMarkIfOptional(boolean isOptional) {
        if (isOptional) return "?";
        else return "";
    }

    private static String removeSingleQuotes(String string) {
        return string.substring(1, string.length() - 1);
    }

    private static String wrapInDoubleQuotesBasedOnDataType(String dataType, String string) {
        if (dataType.equals("INT") || dataType.equals("FLOAT") || dataType.equals("BOOLEAN")) return string;
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