package com.github.lukelinkwalker.orchestrator.ssserver;

import java.net.InetSocketAddress;

import org.apache.commons.text.StringEscapeUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.github.lukelinkwalker.orchestrator.App;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSBuild;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSClose;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSDiagnostic;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSMessage;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSOpen;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSResponse;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSUpdate;
import com.github.lukelinkwalker.orchestrator.transformer.Sheet;
import com.github.lukelinkwalker.orchestrator.transformer.SheetStore;
import com.github.lukelinkwalker.orchestrator.transformer.SheetTransformer;
import com.google.gson.Gson;

public class SSServer extends WebSocketServer {

	private Gson gson;
	
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
	
	private void handleOpenSheet(SSMessage msg) {
		SSOpen sso = gson.fromJson(msg.getData(), SSOpen.class);
		System.out.println("Opening sheet: " + sso.getName());
		
		boolean sheetOpened = SheetStore.openSheet(sso.getName());

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
		System.out.println("Updating sheet: " + ssu.getSheetName());
		
		Sheet sheet = SheetStore.getSheet(ssu.getSheetName());
		
		if(sheet != null) {
			sheet.addData(ssu.getColumn(), ssu.getRow(), ssu.getWidth(), ssu.getData());
		}
		
		SSResponse response = new SSResponse(msg);
		
		if(sheet != null) {
			response.setCode(200);
		} else {
			response.setCode(400);
		}
		
		broadcast(gson.toJson(response));
	}

	private void handleEvaluate(SSMessage msg) {
		
	}
	
	private void handleBuild(SSMessage msg) {
		SSBuild ssb = gson.fromJson(msg.getData(), SSBuild.class);
		System.out.println("Building sheet: " + ssb.getSheetName());
		
		Sheet sheet = SheetStore.getSheet(ssb.getSheetName());
		boolean success = false;
		
		SSResponse response = new SSResponse(msg);
		
		if(sheet != null) {
			response.setCode(200);
			
			if(ssb.isSGL()) {
				// handle SGL generator
				String SGL_JSON = SheetTransformer.parseSGL(sheet);
				System.out.println(SGL_JSON);
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
}
