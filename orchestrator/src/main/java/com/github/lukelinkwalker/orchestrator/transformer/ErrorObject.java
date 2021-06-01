package com.github.lukelinkwalker.orchestrator.transformer;

public class ErrorObject {
	private int column;
	private int row;
	private String json;
	
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
}