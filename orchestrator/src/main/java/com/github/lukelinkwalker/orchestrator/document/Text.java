package com.github.lukelinkwalker.orchestrator.document;

public class Text {
	private Table equivalent;
	private String data;
	
	public Text() {
		equivalent = new Table(this);
	}
	
	public Text(Table equivalent) {
		this.equivalent = equivalent;
	}
	
	public Table getEquivalent() {
		return this.equivalent;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
