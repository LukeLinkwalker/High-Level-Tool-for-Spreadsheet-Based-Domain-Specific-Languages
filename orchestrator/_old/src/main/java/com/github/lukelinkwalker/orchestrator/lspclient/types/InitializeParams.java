package com.github.lukelinkwalker.orchestrator.lspclient.types;

public class InitializeParams {
	private String processId; // int or null
	private String rootPath; // string or null
	private String rootUri; // string or null
	//private String initializationOptions; // any but not used thus not implemented
	private String capabilities; // must be set
	private String trace; // off | messages | verbose
	private WorkspaceFolder[] workspaceFolders; // workspacefolder[] or null
	
	public String getProcessId() {
		return processId;
	}
	
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	public String getRootPath() {
		return rootPath;
	}
	
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	
	public String getRootUri() {
		return rootUri;
	}
	
	public void setRootUri(String rootUri) {
		this.rootUri = rootUri;
	}
	
	public String getCapabilities() {
		return capabilities;
	}
	
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}
	
	public String getTrace() {
		return trace;
	}
	
	public void setTrace(String trace) {
		this.trace = trace;
	}
	
	public WorkspaceFolder[] getWorkspaceFolders() {
		return workspaceFolders;
	}
	
	public void setWorkspaceFolders(WorkspaceFolder[] workspaceFolders) {
		this.workspaceFolders = workspaceFolders;
	}
}
