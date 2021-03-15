package com.github.lukelinkwalker.orchestrator.lspclient.types;

public class ClientCapabilities {
	/**
	 * Workspace specific client capabilities.
	 */
	private WorkspaceClientCapabilities workspace;
	
	/**
	 * Text document specific client capabilities.
	 */
	private TextDocumentClientCapabilities textDocument;
	
	/**
	 * Experimental client capabilities.
	 */
	//private Object experimental; Not implemented.
}
