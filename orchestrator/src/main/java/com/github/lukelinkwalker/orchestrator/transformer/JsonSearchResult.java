package com.github.lukelinkwalker.orchestrator.transformer;

public class JsonSearchResult {
	private int column;
	private int row;
	private int charBegin;
	private int charEnd;
	
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
	public int getCharBegin() {
		return charBegin;
	}
	public void setCharBegin(int charBegin) {
		this.charBegin = charBegin;
	}
	public int getCharEnd() {
		return charEnd;
	}
	public void setCharEnd(int charEnd) {
		this.charEnd = charEnd;
	}
}