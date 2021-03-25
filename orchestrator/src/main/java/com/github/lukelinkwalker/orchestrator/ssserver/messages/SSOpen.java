package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSOpen {
	private String sheetName;
	private boolean isSGL;

	public String getName() {
		return sheetName;
	}

	public void setName(String name) {
		this.sheetName = name;
	}

	public boolean isSGL() {
		return isSGL;
	}

	public void setSGL(boolean isSGL) {
		this.isSGL = isSGL;
	}
}
