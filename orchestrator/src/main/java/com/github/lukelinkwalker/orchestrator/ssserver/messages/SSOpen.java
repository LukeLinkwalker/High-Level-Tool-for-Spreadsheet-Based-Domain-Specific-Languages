package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSOpen {
	private String sheetName;
	private boolean isSML;

	public String getName() {
		return sheetName;
	}

	public void setName(String name) {
		this.sheetName = name;
	}

	public boolean isSML() {
		return isSML;
	}

	public void setSML(boolean isSML) {
		this.isSML = isSML;
	}

	@Override
	public String toString() {
		return "SSOpen [sheetName=" + sheetName + ", isSML=" + isSML + "]";
	}
}
