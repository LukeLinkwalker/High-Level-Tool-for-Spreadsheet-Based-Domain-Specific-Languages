package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSUpdate {
	private String sheetName;
	private int column;
	private int row;
	private int width;
	private String data;
	private boolean skipEval;
	
	public String getSheetName() {
		return sheetName;
	}
	
	public int getColumn() {
		return column;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getWidth() {
		return width;
	}
	
	public String getData() {
		return data;
	}
	
	public boolean getSkipEval() {
		return skipEval;
	}
}