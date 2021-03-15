package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSResponse {
	private String method;
	private int id;
	private int code;
	
	public SSResponse() {
		
	}
	
	public SSResponse(SSMessage msg) {
		this.id = msg.getId();
		this.method = msg.getMethod();
	}
	
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
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
