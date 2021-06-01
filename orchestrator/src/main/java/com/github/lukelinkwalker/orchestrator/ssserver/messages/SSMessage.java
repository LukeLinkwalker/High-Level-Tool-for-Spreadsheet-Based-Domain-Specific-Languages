package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSMessage {
	private String jsonrpc;
	private String method;
	private String params;
	private int id;
	
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
	
	public String getParams() {
		return params;
	}
	
	public void setParams(String data) {
		this.params = data;
	}
	
	@Override
	public String toString() {
		return "SSMessage [method=" + method + ", id=" + id + ", data=" + params + "]";
	}
}