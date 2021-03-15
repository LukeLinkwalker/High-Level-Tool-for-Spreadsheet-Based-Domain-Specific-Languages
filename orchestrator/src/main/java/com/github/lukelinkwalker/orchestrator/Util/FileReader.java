package com.github.lukelinkwalker.orchestrator.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileReader {
	public static String readTextFile(String path) {
		List<String> lines = null;
		
		try {
			lines = Files.readAllLines(new File(path).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(lines == null) {
			return null;
		}
    	
		StringBuilder SB = new StringBuilder();
		
    	for(String line : lines) {
    		SB.append(line);
    	}
    	
    	return SB.toString();
	}
}
