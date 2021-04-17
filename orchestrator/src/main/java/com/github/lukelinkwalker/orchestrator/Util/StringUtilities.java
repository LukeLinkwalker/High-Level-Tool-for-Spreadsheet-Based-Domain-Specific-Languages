package com.github.lukelinkwalker.orchestrator.Util;

public class StringUtilities {
    public static String removeTokensFromString(String string) {
        return string.substring(16, string.length() - 16);
    }
}
