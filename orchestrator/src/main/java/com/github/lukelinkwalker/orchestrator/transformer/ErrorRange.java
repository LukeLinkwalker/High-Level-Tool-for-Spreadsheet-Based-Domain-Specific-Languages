package com.github.lukelinkwalker.orchestrator.transformer;

public class ErrorRange {
	private int start = -1;
	private int end = -1;
	
	public int getStart() {
		return start;
	}
	public void setStart(int charBegin) {
		this.start = charBegin;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int charEnd) {
		this.end = charEnd;
	}
}
