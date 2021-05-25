package com.github.lukelinkwalker.orchestrator.transformer;

public class JsonType {
	private int column;
	private int row;
	private String type;
	private String value;
	
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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "JsonType [column=" + column + ", row=" + row + ", value=" + value + "]";
	}
}
