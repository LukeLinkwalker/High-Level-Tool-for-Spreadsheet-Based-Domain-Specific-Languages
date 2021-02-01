package com.github.lukelinkwalker.orchestrator.lspclient.messages;

import com.github.lukelinkwalker.orchestrator.lspclient.types.InitializeParams;

public class Initialize {
	final private String jsonrpc = "2.0";
	final private String method = "initialize";
	
	private int id;
	private InitializeParams params;
	
	public Initialize(int id) {
		this.id = id;
	}

	public InitializeParams getParams() {
		return params;
	}

	public void setParams(InitializeParams params) {
		this.params = params;
	}
}
