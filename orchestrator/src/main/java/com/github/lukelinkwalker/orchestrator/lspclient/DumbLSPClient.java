package com.github.lukelinkwalker.orchestrator.lspclient;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.github.lukelinkwalker.orchestrator.lspclient.messages.PublishDiagnostics;
import com.github.lukelinkwalker.orchestrator.transpiler.TranspilerTools;

public class DumbLSPClient extends WebSocketClient {

	private final String initializeMsg = "{\"jsonrpc\":\"2.0\",\"id\":0,\"method\":\"initialize\",\"params\":{\"rootPath\":null,\"rootUri\":null,\"capabilities\":{\"workspace\":{\"applyEdit\":true,\"workspaceEdit\":{\"documentChanges\":true,\"resourceOperations\":[\"create\",\"rename\",\"delete\"],\"failureHandling\":\"textOnlyTransactional\"},\"didChangeConfiguration\":{\"dynamicRegistration\":true},\"didChangeWatchedFiles\":{\"dynamicRegistration\":true},\"symbol\":{\"dynamicRegistration\":true,\"symbolKind\":{\"valueSet\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26]}},\"executeCommand\":{\"dynamicRegistration\":true},\"configuration\":true,\"workspaceFolders\":true},\"textDocument\":{\"publishDiagnostics\":{\"relatedInformation\":true,\"tagSupport\":true},\"synchronization\":{\"dynamicRegistration\":true,\"willSave\":true,\"willSaveWaitUntil\":true,\"didSave\":true},\"completion\":{\"dynamicRegistration\":true,\"contextSupport\":true,\"completionItem\":{\"snippetSupport\":true,\"commitCharactersSupport\":true,\"documentationFormat\":[\"markdown\",\"plaintext\"],\"deprecatedSupport\":true,\"preselectSupport\":true},\"completionItemKind\":{\"valueSet\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25]}},\"hover\":{\"dynamicRegistration\":true,\"contentFormat\":[\"markdown\",\"plaintext\"]},\"signatureHelp\":{\"dynamicRegistration\":true,\"signatureInformation\":{\"documentationFormat\":[\"markdown\",\"plaintext\"],\"parameterInformation\":{\"labelOffsetSupport\":true}}},\"definition\":{\"dynamicRegistration\":true,\"linkSupport\":true},\"references\":{\"dynamicRegistration\":true},\"documentHighlight\":{\"dynamicRegistration\":true},\"documentSymbol\":{\"dynamicRegistration\":true,\"symbolKind\":{\"valueSet\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26]},\"hierarchicalDocumentSymbolSupport\":true},\"codeAction\":{\"dynamicRegistration\":true,\"codeActionLiteralSupport\":{\"codeActionKind\":{\"valueSet\":[\"\",\"quickfix\",\"refactor\",\"refactor.extract\",\"refactor.inline\",\"refactor.rewrite\",\"source\",\"source.organizeImports\"]}}},\"codeLens\":{\"dynamicRegistration\":true},\"formatting\":{\"dynamicRegistration\":true},\"rangeFormatting\":{\"dynamicRegistration\":true},\"onTypeFormatting\":{\"dynamicRegistration\":true},\"rename\":{\"dynamicRegistration\":true,\"prepareSupport\":true},\"documentLink\":{\"dynamicRegistration\":true},\"typeDefinition\":{\"dynamicRegistration\":true,\"linkSupport\":true},\"implementation\":{\"dynamicRegistration\":true,\"linkSupport\":true},\"colorProvider\":{\"dynamicRegistration\":true},\"foldingRange\":{\"dynamicRegistration\":true,\"rangeLimit\":5000,\"lineFoldingOnly\":true},\"declaration\":{\"dynamicRegistration\":true,\"linkSupport\":true}}},\"trace\":\"off\",\"workspaceFolders\":null}}\r\n";
	private final String initializedMsg = "{\"jsonrpc\":\"2.0\",\"method\":\"initialized\",\"params\":{}}";
	private final String openEmptyFileMsg = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didOpen\",\"params\":{\"textDocument\":{\"uri\":\"inmemory:/demo/dc915592-2cda-71e9-3c1a-f61dc6884cae.hello\",\"languageId\":\"hello\",\"version\":1,\"text\":\"\"}}}";
	private final String didChangeMsg = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"inmemory:/demo/dc915592-2cda-71e9-3c1a-f61dc6884cae.hello\",\"version\":3},\"contentChanges\":[{\"range\":{\"start\":{\"line\":4,\"character\":0},\"end\":{\"line\":4,\"character\":0}},\"rangeLength\":0,\"text\":\"H\"}]}}";
	private final String didChangeTemplateMsg = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"inmemory:/demo/dc915592-2cda-71e9-3c1a-f61dc6884cae.hello\",\"version\":$VERSION$},\"contentChanges\":[{\"range\":{\"start\":{\"line\":$LINE$,\"character\":$POSITION$},\"end\":{\"line\":$LINE$,\"character\":$POSITION$}},\"rangeLength\":0,\"text\":\"$CHARACTER$\"}]}}";
	private boolean initialized = false;
	private int version = 1;
	
	public DumbLSPClient(URI serverUri) {
		super(serverUri);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		send(initializeMsg);
	}

	@Override
	public void onMessage(String message) {
		JsonElement element = new JsonParser().parse(message);
		JsonObject obj = element.getAsJsonObject();
		
		if(initialized == false) {
			if(obj.get("id").getAsInt() == 0) {
				System.out.println("Finishing initialization.");
				send(initializedMsg);
				initialized = true;
			}
		} else {
			System.out.println("Diagnostic: " + message);
			Gson gson = new Gson();
			PublishDiagnostics PD = gson.fromJson(message, PublishDiagnostics.class);
			TranspilerTools. receiveMessageFromServer(PD);
			
			//System.out.println("Severity: " + PD.getParams().getDiagnostics()[0].getSeverity());
			
			//if(obj.get("method").getAsString().equals("textDocument/publishDiagnostics")) {
			//	System.out.println(message);
			//}
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("Closed");
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
	}
	
	public void openFile(String str) {
		new Runnable() {
			
			@Override
			public void run() {
				while(initialized == false) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				send(openEmptyFileMsg);
			}
		}.run();
	}
	
	public void sendChar(int Line, int Position, String Chr) {
		final int _Line = Line;
		final int _Position = Position;
		final String _Chr = Chr;
		
		new Runnable() {
			@Override
			public void run() {
				while(initialized == false) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				String toSend = didChangeTemplateMsg;
				
				toSend = toSend.replace("$VERSION$", String.valueOf(++version));
				toSend = toSend.replace("$LINE$", String.valueOf(_Line));
				toSend = toSend.replace("$POSITION$", String.valueOf(_Position));
				toSend = toSend.replace("$CHARACTER$", _Chr);
				
				send(toSend);
			}
		}.run();
		
		
	}
	
}