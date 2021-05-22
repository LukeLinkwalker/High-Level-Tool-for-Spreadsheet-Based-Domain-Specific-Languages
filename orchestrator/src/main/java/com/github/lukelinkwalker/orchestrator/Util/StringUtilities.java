package com.github.lukelinkwalker.orchestrator.Util;

public class StringUtilities {
    public static String removeTokensFromString(String string) {
        return string.substring(16, string.length() - 16);
    }
    
    public static String stripTrailingSpecials(String str) {
    	int limit = str.length() - 1;
    	
    	for(int i = str.length() - 1; i > 0; i -= 1) {
    		char c = str.charAt(i);
    		
    		if(c == ' ' || Character.isLetter(c) == false) {
    			limit -= 1;
    		} else {
    			break;
    		}
    	}
    	
    	return str.substring(0, limit + 1).stripTrailing();
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
    
    public static boolean isAny(String str) {
    	if(isBoolean(str)) {
    		return true;
    	}
    	
    	if(isFloat(str)) {
    		return true;
    	}
    	
    	if(isInteger(str)) {
    		return true;
    	}
    	
    	return false;
    }
    
    public static boolean isNull(String str) {
    	if(str.toLowerCase().equals("null")) {
    		return true;
    	}
    	
    	return false;
    }
}
