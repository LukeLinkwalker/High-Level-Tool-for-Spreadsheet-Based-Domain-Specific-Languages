package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSBuild {
	private String sheetName;
	private boolean isSGL;
	
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public boolean isSGL() {
		return isSGL;
	}
	public void setSGL(boolean isSGL) {
		this.isSGL = isSGL;
	}
}
