package com.github.lukelinkwalker.orchestrator.ssserver;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.github.lukelinkwalker.orchestrator.App;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.*;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSCheckIfTextIsATableName;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSResponse;
import com.google.gson.JsonArray;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.github.lukelinkwalker.orchestrator.transformer.Sheet;
import com.github.lukelinkwalker.orchestrator.transformer.SheetStore;
import com.github.lukelinkwalker.orchestrator.transformer.SheetTransformer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.io.FileUtils;

public class SSServer extends WebSocketServer {

	private Gson gson;
	private String sdslGrammar;
	private JsonArray ssModel;
	
	public SSServer(int port)  {
		super(new InetSocketAddress(port));
		gson = new Gson();
	}
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("Connection established.");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("Server closed.");
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		SSMessage msg = gson.fromJson(message, SSMessage.class);
		
		System.out.println(msg.toString());
		
		switch(msg.getMethod().toLowerCase()) {
			case "open-sheet":
				handleOpenSheet(msg);
				break;
			case "close-sheet":
				handleCloseSheet(msg);
				break;
			case "update-sheet":
				handleUpdateSheet(msg);
				break;
			case "evaluate":
				handleEvaluate(msg);
				break;
			case "build":
				handleBuild(msg);
				break;
			case "header":
				handleHeaderLookup(msg);
				break;
			case "check-if-text-is-a-table-name":
				handleCheckIfTextIsATableName(msg);
				break;
			case "get-initial-table-range":
				handleGetInitialTableRange(msg);
				break;
			case "create-table":
				handleCreateTable(msg);
				break;
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}
	
	public void sendDiagnostic(SSDiagnostic diagnostic) {
		System.out.println("Sending message to browser!");
		broadcast(gson.toJson(diagnostic));
	}
	
	public void sendErrors(JsonObject errors) {
		System.out.println("Sending errors to client!");
		broadcast(gson.toJson(errors));
	}
	
	private void handleOpenSheet(SSMessage msg) {
		SSOpen sso = gson.fromJson(msg.getData(), SSOpen.class);
		System.out.println("Opening sheet: " + sso.getName());
		
		boolean sheetOpened = SheetStore.openSheet(sso.getName(), sso.isSML());

		SSResponse response = new SSResponse(msg);
		
		if(sheetOpened) {
			response.setCode(200);
		} else {
			response.setCode(400);
		}
		
		broadcast(gson.toJson(response));
	}
	
	private void handleCloseSheet(SSMessage msg) {
		SSClose ssc = gson.fromJson(msg.getData(), SSClose.class);
		System.out.println("Closing sheet: " + ssc.getName());
		
		Sheet sheetClosed = SheetStore.closeSheet(ssc.getName());
		
		SSResponse response = new SSResponse(msg);
		
		if(sheetClosed != null) {
			response.setCode(200);
		} else {
			response.setCode(400);
		}

		broadcast(gson.toJson(response));
	}
	
	private void handleUpdateSheet(SSMessage msg) {
		SSUpdate ssu = gson.fromJson(msg.getData(), SSUpdate.class);
		Sheet sheet = SheetStore.getSheet(ssu.getSheetName());
		
		System.out.println("Updating sheet: " + ssu.getSheetName());
		
		if(sheet != null) {
			sheet.addData(ssu.getColumn(), ssu.getRow(), ssu.getWidth(), ssu.getData(), ssu.getSkipEval());
			
		}
		
		SSResponse response = new SSResponse(msg);
		
		if(sheet != null) {
			response.setCode(200);
		} else {
			response.setCode(400);
		}
		
		broadcast(gson.toJson(response));
		
		// Initiate evaluation of sheet
		SSEvaluate sse = new SSEvaluate(ssu);
		handleEvaluate(sse);
	}

	private void handleEvaluate(SSMessage msg) {
		SSEvaluate sse = gson.fromJson(msg.getData(), SSEvaluate.class);
	}
	
	private void handleEvaluate(SSEvaluate sse) {
		Sheet sheet = SheetStore.getSheet(sse.getSheetName());
		
		String JSON;
		
		if(sheet.isSML()) {
			JSON = SheetTransformer.parseSML(sheet);
		} else {
			JSON = SheetTransformer.parseSDSL(sheet);
		}
		
		System.out.println("Evaluating : " + JSON);
		//App.DC.setContent(SDSL_JSON);
		App.Txt = JSON;
		App.DC.openFileWithContent(JSON);
		
		//System.out.println("Evaluated: " + SDSL_JSON);
	}
	
	private void handleBuild(SSMessage msg) {
		SSBuild ssb = gson.fromJson(msg.getData(), SSBuild.class);
		System.out.println("Building sheet: " + ssb.getSheetName());
		
		Sheet sheet = SheetStore.getSheet(ssb.getSheetName());
		boolean success = false;
		
		SSResponse response = new SSResponse(msg);
		
		if(sheet != null) {
			response.setCode(200);
			
			if(sheet.isSML()) {
				// handle SML generator
				String ssModelString = SheetTransformer.parseSML(sheet);
				loadSSModel(ssModelString);
				sdslGrammar = GrammarCreator.createGrammar();
				System.out.println("SS Model : " + ssModel);
				System.out.println("XText Grammar : " + sdslGrammar);
				

				File outputDir = new File("outputs/" + ssb.getSheetName());
				File modelFile = new File("outputs/" + ssb.getSheetName() + "/ssmodel.json");
				File grammarFile = new File("outputs/" + ssb.getSheetName() + "/sdsl.xtext");
				try {
					FileUtils.forceMkdir(outputDir);
					FileUtils.writeStringToFile(modelFile, ssModel.toString(), (String) null);
					FileUtils.writeStringToFile(grammarFile, sdslGrammar, (String) null);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else {
				// handle SDSL generator
			}
		}
		
		if(sheet == null) {
			response.setCode(400);
		}
		
		broadcast(gson.toJson(response));
	}
	
	private void handleHeaderLookup(SSMessage msg) {
		// Send response with broadcast()
	}

	private void handleCheckIfTextIsATableName(SSMessage msg) {
		SSCheckIfTextIsATableName ssCheckIfTextIsATableName = gson.fromJson(msg.getData(), SSCheckIfTextIsATableName.class);
		String cellText = ssCheckIfTextIsATableName.getCellText();
		int column = ssCheckIfTextIsATableName.getColumn();
		int row = ssCheckIfTextIsATableName.getRow();
		String spreadsheetType = ssCheckIfTextIsATableName.getSpreadsheetType();
		boolean tableNameExists = TableCreator.checkIfTextIsATableName(cellText, spreadsheetType);

		SSResponse response = new SSResponse(msg);
		response.setCode(200);
		response.setParams(new Object[] {cellText, column, row, tableNameExists, spreadsheetType});

		broadcast(gson.toJson(response));
		System.out.println("Getting table names");
	}

	private void handleGetInitialTableRange(SSMessage msg) {
		SSGetInitialTableRange ssGetInitialTableRange = gson.fromJson(msg.getData(), SSGetInitialTableRange.class);
		String tableName = ssGetInitialTableRange.getTableName();
		int column = ssGetInitialTableRange.getColumn();
		int row = ssGetInitialTableRange.getRow();
		String spreadsheetType = ssGetInitialTableRange.getSpreadsheetType();
		int[] tableRange = TableCreator.getInitialTableRangeResponse(tableName, column, row, spreadsheetType);

		SSResponse response = new SSResponse(msg);
		response.setCode(200);
		response.setParams(new Object[] {tableName, tableRange, spreadsheetType});
		broadcast(gson.toJson(response));

		System.out.println("Getting initial table range");
	}

	private void handleCreateTable(SSMessage msg) {
		SSCreateTable ssCreateTable = gson.fromJson(msg.getData(), SSCreateTable.class);
		String tableName = ssCreateTable.getTableName();
		int column = ssCreateTable.getColumn();
		int row = ssCreateTable.getRow();
		String spreadsheetType = ssCreateTable.getSpreadsheetType();
		boolean success = TableCreator.initializeCreateTable(tableName, column, row, spreadsheetType);

		SSResponse response = new SSResponse(msg);
		if (success) response.setCode(200);
		else response.setCode(400);
		broadcast(gson.toJson(response));

		System.out.println("Creating table");
	}

	public void sendNotification(String method, Object[] parameters) {
		SSNotification notification = new SSNotification(method, parameters);
		broadcast(gson.toJson(notification));

		System.out.println("Sending notification for method " + method);
	}

	private void loadSSModel(String ssModelString) {
		Gson gson = new Gson();
		ssModel = gson.fromJson(ssModelString, JsonArray.class);
	}

	//Use when dyncamic loading of things is implemented.
	public JsonArray getSDSLModelForDynamicLoading() {
		return ssModel;
	}

	public JsonArray getSDSLSSModel() {
		Gson gson = new Gson();
		Reader reader = null;

		try {
			reader = Files.newBufferedReader(Paths.get("outputs/Hello/ssmodel.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return gson.fromJson(reader, JsonArray.class);
	}

	public JsonArray getSMLSSModel() {
		Gson gson = new Gson();
		Reader reader = null;

		try {
			reader = Files.newBufferedReader(Paths.get("orchestrator/src/main/java/com/github/lukelinkwalker/orchestrator/ssserver/smlSSModel.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return gson.fromJson(reader, JsonArray.class);
	}
}
