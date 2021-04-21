package com.github.lukelinkwalker.orchestrator.Util;

public class StringUtilities {
    public static String removeTokensFromString(String string) {
        return string.substring(16, string.length() - 16);
    }
    
    public static boolean isInteger(String str) {
    	for(int i = 0; i < str.length(); i += 1) {
    		char c = str.charAt(i);
    		
    		if(Character.isDigit(c) == false) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    public static boolean isFloat(String str) {
    	int commas = 0;
    	
    	for(int i = 0; i < str.length(); i += 1) {
    		char c = str.charAt(i);
    		
    		if(c == '.') {
    			if(commas == 0) {
    				commas += 1;
    				continue;
    			} else {
    				return false;
    			}
    		}
    		
    		if(Character.isDigit(c) == false) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    public static boolean isBoolean(String str) {
    	if(str.toLowerCase().equals("true") || str.toLowerCase().equals("false")) {
    		return true;
    	}
    	
    	return false;
    }
}
