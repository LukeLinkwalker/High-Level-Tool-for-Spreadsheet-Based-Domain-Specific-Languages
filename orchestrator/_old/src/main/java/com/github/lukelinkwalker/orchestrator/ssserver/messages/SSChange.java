package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSChange {
	private int cellx;
	private int celly;
	private int position;
	private String character;
	
	public int getCellx() {
		return cellx;
	}
	
	public void setCellx(int cell) {
		this.cellx = cell;
	}
	
	public int getCelly() {
		return celly;
	}
	
	public void setCelly(int cell) {
		this.celly = cell;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public String getCharacter() {
		return character;
	}
	
	public void setCharacter(String character) {
		this.character = character;
	}

	@Override
	public String toString() {
		return "SSChange [cellx=" + cellx + ", celly=" + celly + ", position=" + position + ", character=" + character
				+ "]";
	}
}