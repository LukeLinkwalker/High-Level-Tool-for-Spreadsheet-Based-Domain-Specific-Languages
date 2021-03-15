package com.github.lukelinkwalker.orchestrator.lspclient.messages;

import com.github.lukelinkwalker.orchestrator.lspclient.types.Range;

public class PublishDiagnostics {
	private String jsonrpc;
	private String method;
	private DiagnosticParams params;
	
	public String getJsonrpc() {
		return jsonrpc;
	}
	
	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public DiagnosticParams getParams() {
		return params;
	}
	
	public void setParams(DiagnosticParams params) {
		this.params = params;
	}
	
	public class DiagnosticParams {
		private String uri;
		private Diagnostics[] diagnostics;
		
		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public Diagnostics[] getDiagnostics() {
			return diagnostics;
		}

		public void setDiagnostics(Diagnostics[] diagnostics) {
			this.diagnostics = diagnostics;
		}

		public class Diagnostics {
			private Range range;
			private int severity;
			private String code;
			private String message;
			
			public Range getRange() {
				return range;
			}
			
			public void setRange(Range range) {
				this.range = range;
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
	}
}
