package com.github.lukelinkwalker.orchestrator.transformer;

import com.github.lukelinkwalker.orchestrator.Util.Tuple;

public class Diff {
	public static Tuple<Integer, Integer> check(String _old, String _new) {
		int limitA = 0;
		int limitB = 0;
		
		for(int i = 0; i < Math.min(_old.length(), _new.length()); i += 1) {
			//if(i < _old.length() && i < _new.length()) {
			//	break;
			//}
			
			if(_old.charAt(i) == _new.charAt(i)) {
				limitA += 1;
			} else {
				break;
			}
		}
		
		for(int i = 0; i < Math.max(_old.length(), _new.length()); i += 1) {
			int indexOld = (_old.length() - 1) - i;
			int indexNew = (_new.length() - 1) - i;
			
			if(_old.charAt(indexOld) == _new.charAt(indexNew)) {
				limitB += 1;
			} else {
				break;
			}
		}
		
		limitB = _new.length() - limitB;
		
		return new Tuple<Integer, Integer>(limitA, limitB);
	}
	
	public static Tuple<Integer, Integer> checkB(String _old, String _new) {
		int limitA = 0;
		int limitB = 0;
		
		String _oldStr = _old;
		String _newStr = _new;
		
		for(int i = 0; i < Math.min(_oldStr.length(), _newStr.length()); i += 1) {
			//if(i < _old.length() && i < _new.length()) {
			//	break;
			//}
			
			if(_oldStr.charAt(i) == _newStr.charAt(i)) {
				limitA += 1;
			} else {
				break;
			}
		}
		
		_oldStr = _oldStr.substring(limitA, _oldStr.length());
		
		System.out.println("Temp: " + _oldStr);
		
		
		for(int i = 0; i < Math.min(_oldStr.length(), _newStr.length()); i += 1) {
			int indexOld = (_oldStr.length() - 1) - i;
			int indexNew = (_newStr.length() - 1) - i;
			
			if(_oldStr.charAt(indexOld) == _newStr.charAt(indexNew)) {
				limitB += 1;
			} else {
				break;
			}
		}
		
		limitB = _newStr.length() - limitB;
		
		return new Tuple<Integer, Integer>(limitA, limitB);
	}
}
