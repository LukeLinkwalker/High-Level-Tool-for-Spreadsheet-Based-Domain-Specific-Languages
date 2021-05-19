package com.github.lukelinkwalker.orchestrator.lspclient;

import java.net.URI;

import org.apache.commons.lang3.StringEscapeUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.github.lukelinkwalker.orchestrator.App;
import com.github.lukelinkwalker.orchestrator.Util.Tuple;
import com.github.lukelinkwalker.orchestrator.lspclient.messages.LSPMessage;
import com.github.lukelinkwalker.orchestrator.lspclient.messages.PublishDiagnostics;
import com.github.lukelinkwalker.orchestrator.lspclient.messages.PublishDiagnostics.DiagnosticParams.Diagnostics;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSDiagnostic;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSError;
import com.github.lukelinkwalker.orchestrator.transformer.JsonObj;
import com.github.lukelinkwalker.orchestrator.transformer.JsonSearch;

public class DumbLSPClient extends WebSocketClient {

	private final String initializeMsg = "{\"jsonrpc\":\"2.0\",\"id\":0,\"method\":\"initialize\",\"params\":{\"rootPath\":null,\"rootUri\":null,\"capabilities\":{\"workspace\":{\"applyEdit\":true,\"workspaceEdit\":{\"documentChanges\":true,\"resourceOperations\":[\"create\",\"rename\",\"delete\"],\"failureHandling\":\"textOnlyTransactional\"},\"didChangeConfiguration\":{\"dynamicRegistration\":true},\"didChangeWatchedFiles\":{\"dynamicRegistration\":true},\"symbol\":{\"dynamicRegistration\":true,\"symbolKind\":{\"valueSet\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26]}},\"executeCommand\":{\"dynamicRegistration\":true},\"configuration\":true,\"workspaceFolders\":true},\"textDocument\":{\"publishDiagnostics\":{\"relatedInformation\":true,\"tagSupport\":true},\"synchronization\":{\"dynamicRegistration\":true,\"willSave\":true,\"willSaveWaitUntil\":true,\"didSave\":true},\"completion\":{\"dynamicRegistration\":true,\"contextSupport\":true,\"completionItem\":{\"snippetSupport\":true,\"commitCharactersSupport\":true,\"documentationFormat\":[\"markdown\",\"plaintext\"],\"deprecatedSupport\":true,\"preselectSupport\":true},\"completionItemKind\":{\"valueSet\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25]}},\"hover\":{\"dynamicRegistration\":true,\"contentFormat\":[\"markdown\",\"plaintext\"]},\"signatureHelp\":{\"dynamicRegistration\":true,\"signatureInformation\":{\"documentationFormat\":[\"markdown\",\"plaintext\"],\"parameterInformation\":{\"labelOffsetSupport\":true}}},\"definition\":{\"dynamicRegistration\":true,\"linkSupport\":true},\"references\":{\"dynamicRegistration\":true},\"documentHighlight\":{\"dynamicRegistration\":true},\"documentSymbol\":{\"dynamicRegistration\":true,\"symbolKind\":{\"valueSet\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26]},\"hierarchicalDocumentSymbolSupport\":true},\"codeAction\":{\"dynamicRegistration\":true,\"codeActionLiteralSupport\":{\"codeActionKind\":{\"valueSet\":[\"\",\"quickfix\",\"refactor\",\"refactor.extract\",\"refactor.inline\",\"refactor.rewrite\",\"source\",\"source.organizeImports\"]}}},\"codeLens\":{\"dynamicRegistration\":true},\"formatting\":{\"dynamicRegistration\":true},\"rangeFormatting\":{\"dynamicRegistration\":true},\"onTypeFormatting\":{\"dynamicRegistration\":true},\"rename\":{\"dynamicRegistration\":true,\"prepareSupport\":true},\"documentLink\":{\"dynamicRegistration\":true},\"typeDefinition\":{\"dynamicRegistration\":true,\"linkSupport\":true},\"implementation\":{\"dynamicRegistration\":true,\"linkSupport\":true},\"colorProvider\":{\"dynamicRegistration\":true},\"foldingRange\":{\"dynamicRegistration\":true,\"rangeLimit\":5000,\"lineFoldingOnly\":true},\"declaration\":{\"dynamicRegistration\":true,\"linkSupport\":true}}},\"trace\":\"off\",\"workspaceFolders\":null}}\r\n";
	private final String initializedMsg = "{\"jsonrpc\":\"2.0\",\"method\":\"initialized\",\"params\":{}}";
	private final String openEmptyFileMsg = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didOpen\",\"params\":{\"textDocument\":{\"uri\":\"inmemory:/demo/dc915592-2cda-71e9-3c1a-f61dc6884cae.hello\",\"languageId\":\"hello\",\"version\":1,\"text\":\"\"}}}";
	private final String clearFile = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"inmemory:/demo/dc915592-2cda-71e9-3c1a-f61dc6884cae.hello\",\"version\":$VERSION$},\"contentChanges\":[{\"range\":{\"start\":{\"line\":0,\"character\":0},\"end\":{\"line\":9999,\"character\":0}},\"rangeLength\":999999,\"text\":\"\"}]}}";
	private final String didChangeTemplateMsg = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"inmemory:/demo/dc915592-2cda-71e9-3c1a-f61dc6884cae.hello\",\"version\":$VERSION$},\"contentChanges\":[{\"range\":{\"start\":{\"line\":$LINE$,\"character\":$POSITION$},\"end\":{\"line\":$LINE$,\"character\":$POSITION$}},\"rangeLength\":0,\"text\":\"$CHARACTER$\"}]}}";
	
	private final String openFileMsg = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didOpen\",\"params\":{\"textDocument\":{\"uri\":\"inmemory:/demo/$INCREMENTINGID$.hello\",\"languageId\":\"hello\",\"version\":1,\"text\":\"$FILECONTENTS$\"}}}";

	private final String didChangeMsg = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"inmemory:/demo/dc915592-2cda-71e9-3c1a-f61dc6884cae.hello\",\"version\":3},\"contentChanges\":[{\"range\":{\"start\":{\"line\":4,\"character\":0},\"end\":{\"line\":4,\"character\":0}},\"rangeLength\":0,\"text\":\"H\"}]}}";
	private boolean initialized = false;
	private int version = 1;
	private int incrementingID = 1;
	
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
			System.out.println(message);
			LSPMessage msg = new Gson().fromJson(message, LSPMessage.class);
			
			if(msg.getMethod().equals("textDocument/publishDiagnostics")) {
				PublishDiagnostics PD = new Gson().fromJson(message, PublishDiagnostics.class);
				
				String dumbURI = "inmemory:/demo/" + incrementingID + ".hello";
				
				if(dumbURI.equals(PD.getParams().getUri()) == true) {
					System.out.println("Number of diagnostics: " + PD.getParams().getDiagnostics().length);
					
					JsonObject errorMsg = new JsonObject();
					errorMsg.addProperty("method", "diagnostic");
					JsonArray errors = new JsonArray();
					errorMsg.add("content", errors);
					
					for(int i = 0; i < PD.getParams().getDiagnostics().length; i += 1) {
						Diagnostics diag = PD.getParams().getDiagnostics()[i];
						
						//Tuple<Tuple<Integer, Integer>, String> errorInfo = JsonSearch.find(App.Txt, diag.getRange().getStart().getCharacter());
						
						Tuple<Tuple<Integer, Integer>, Tuple<Integer, Integer>> errorInfos = JsonSearch.find(App.Txt, diag.getRange().getStart().getCharacter(), diag.getRange().getEnd().getCharacter());
						
						//JsonObj errorCell = JsonSearch.find(App.Txt, diag.getRange().getStart().getCharacter());
						
						//JsonObject error = new JsonObject();
						//error.addProperty("column", errorCell.getColumn());
						//error.addProperty("row", errorCell.getRow());
						//error.addProperty("message", diag.getMessage());
						
						JsonObject error = new JsonObject();
						
						int column = errorInfos.getA().getA();
						if(column == -1) {
							column = 0;
						}
						int row = errorInfos.getA().getB();
						if(row == -1) {
							row = 0;
						}
						
						error.addProperty("column", column);
						error.addProperty("row", row);
						error.addProperty("start", errorInfos.getB().getA());
						error.addProperty("end", errorInfos.getB().getB());
						error.addProperty("message", diag.getMessage());
						
						// Add error indeces
						
						errors.add(error);
					}
					
					//System.out.println("Diagnostic document : " + PD.getParams().getUri());
					//System.out.println("Dumb ID : " + dumbURI + " -> " + (dumbURI.equals(PD.getParams().getUri())));
					//System.out.println("Number of errors : " + PD.getParams().getDiagnostics().length);
					
					App.SSS.sendErrors(errorMsg);		
				}
				
			}
			
			//System.out.println("Diagnostic: " + message);
			//Gson gson = new Gson();
			//PublishDiagnostics PD = gson.fromJson(message, PublishDiagnostics.class);
			//
			//for(Diagnostics diag : PD.getParams().getDiagnostics()) {
			//	SSDiagnostic result = new SSDiagnostic();
	    	//	result.setUri("inmemory:/demo/world.hello"); // PD.getParams().getUri());
	    	//	result.setCode(diag.getCode());
	    	//	result.setMessage(diag.getMessage());
	    	//	result.setSeverity(diag.getSeverity());
	    	//	App.SSS.sendDiagnostic(result);
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
	
	public void openFileWithContent(String content) {
		final String _content = content;
		
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
				
				incrementingID += 1;
				String toSend = openFileMsg;
				toSend = toSend.replace("$INCREMENTINGID$", String.valueOf(incrementingID));
				toSend = toSend.replace("$FILECONTENTS$", StringEscapeUtils.escapeJson(_content));
				send(toSend);
			}
		}.run();
	}
	
	public void doInsert(int Line, int Position, String Chr) {
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
	
	public void setContent(String content) {
		final String _Chr = content;
		
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
				
				// Clear
				String toSend = clearFile;
				toSend = toSend.replace("$VERSION$", String.valueOf(++version));
				send(toSend);
				
				// Set
				toSend = didChangeTemplateMsg;
				toSend = toSend.replace("$VERSION$", String.valueOf(++version));
				toSend = toSend.replace("$LINE$", String.valueOf("0"));
				toSend = toSend.replace("$POSITION$", String.valueOf("0"));
				toSend = toSend.replace("$CHARACTER$", StringEscapeUtils.escapeJson(_Chr));
				System.out.println("Sending : " + StringEscapeUtils.escapeJson(_Chr));
				//System.out.println("Sending : " + StringEscapeUtils.escapeJson(_Chr));
				send(toSend);
			}
		}.run();
	}
	
	public void clearFile() {
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
				
				String toSend = clearFile;
				
				toSend = toSend.replace("$VERSION$", String.valueOf(++version));
				
				send(toSend);
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