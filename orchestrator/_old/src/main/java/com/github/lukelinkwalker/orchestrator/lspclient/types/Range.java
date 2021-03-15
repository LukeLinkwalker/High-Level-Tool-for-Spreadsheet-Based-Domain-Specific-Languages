package com.github.lukelinkwalker.orchestrator.lspclient.types;

public class Range {
	private SubRange start;
	private SubRange end;
	
	public SubRange getStart() {
		return start;
	}

	public void setStart(SubRange start) {
		this.start = start;
	}

	public SubRange getEnd() {
		return end;
	}

	public void setEnd(SubRange end) {
		this.end = end;
	}

	public class SubRange {
		private int line;
		private int character;
		
		public int getLine() {
			return line;
		}
		
		public void setLine(int line) {
			this.line = line;
		}
		
		public int getCharacter() {
			return character;
		}
		
		public void setCharacter(int character) {
			this.character = character;
		}
	}
}