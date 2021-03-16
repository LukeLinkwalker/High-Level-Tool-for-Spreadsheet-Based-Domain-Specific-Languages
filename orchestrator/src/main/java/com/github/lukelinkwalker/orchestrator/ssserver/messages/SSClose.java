package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSClose {
	private String sheetName;

	public String getName() {
		return sheetName;
	}

	public void setName(String name) {
		this.sheetName = name;
	}
}
