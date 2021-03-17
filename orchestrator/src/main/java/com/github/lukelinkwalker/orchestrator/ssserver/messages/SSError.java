package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSError {
	private int column;
	private int row;
	private String message;
	
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
