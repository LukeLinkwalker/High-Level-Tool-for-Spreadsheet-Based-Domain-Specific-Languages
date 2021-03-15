package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSMessage {
	private String method;
	private int id;
	private String data;
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "SSMessage [method=" + method + ", id=" + id + ", data=" + data + "]";
	}
}