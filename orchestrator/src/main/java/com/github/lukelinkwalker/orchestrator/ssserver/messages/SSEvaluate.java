package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSEvaluate {
	private String sheetName;
	
	public SSEvaluate() {
		
	}
	
	public SSEvaluate(SSUpdate msg) {
		this.sheetName = msg.getSheetName();
	}
	
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
}
