package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSDiagnostic {
	private int cell;
	private int startPosition;
	private int endPosition;
	private String uri;
	private int severity;
	private String code;
	private String message;
	
	public int getCell() {
		return cell;
	}
	
	public void setCell(int cell) {
		this.cell = cell;
	}
	
	public int getStartPosition() {
		return startPosition;
	}
	
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	
	public int getEndPosition() {
		return endPosition;
	}
	
	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public int getSeverity() {
		return severity;
	}
	
	public void setSeverity(int severity) {
		this.severity = severity;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
