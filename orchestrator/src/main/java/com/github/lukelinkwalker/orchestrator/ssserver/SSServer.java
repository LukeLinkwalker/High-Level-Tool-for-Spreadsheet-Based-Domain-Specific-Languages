package com.github.lukelinkwalker.orchestrator.ssserver;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSChange;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSDiagnostic;
import com.github.lukelinkwalker.orchestrator.transpiler.CellPosition;
import com.github.lukelinkwalker.orchestrator.transpiler.TranspilerTools;
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
		System.out.println("Received [1/2]: " + message);
		SSChange ssc = gson.fromJson(message, SSChange.class);
		System.out.println("Received [2/2]: " + ssc.toString());
		
		if(ssc.getCharacter().equals("\n")) {
			TranspilerTools.enterPressed(
					new CellPosition(ssc.getCellx(), ssc.getCelly())
			);
		} else {
			TranspilerTools.letterReceivedFromSpreadsheet(
					new CellPosition(ssc.getCellx(), ssc.getCelly()), 
					ssc.getPosition(),
					ssc.getCharacter().charAt(0)
			);
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
		broadcast(gson.toJson(diagnostic));
	}
}
