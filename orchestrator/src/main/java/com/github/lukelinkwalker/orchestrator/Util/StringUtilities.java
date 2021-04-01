package com.github.lukelinkwalker.orchestrator.Util;

public class StringUtilities {
    public static String removeSingleQuotes(String string) {
        return string.substring(1, string.length() - 1);
    }
}
