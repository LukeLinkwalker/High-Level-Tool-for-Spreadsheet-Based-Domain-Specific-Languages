package com.github.lukelinkwalker.orchestrator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.github.lukelinkwalker.orchestrator.lspclient.DumbLSPClient;
import com.github.lukelinkwalker.orchestrator.lspclient.messages.Initialize;
import com.github.lukelinkwalker.orchestrator.lspclient.types.InitializeParams;
import com.github.lukelinkwalker.orchestrator.ssserver.SSServer;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSChange;
import com.github.lukelinkwalker.orchestrator.transpiler.CellPosition;
import com.github.lukelinkwalker.orchestrator.transpiler.DOM;
import com.github.lukelinkwalker.orchestrator.transpiler.RootTable;
import com.github.lukelinkwalker.orchestrator.transpiler.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App 
{
	public static DumbLSPClient DC;
	public static SSServer SSS;
	
    public static void main( String[] args ) throws URISyntaxException, InterruptedException, ParserConfigurationException, IOException, SAXException
    {
    	// Opening connection to demo LSP
    	DC = new DumbLSPClient(new URI("ws://localhost:4389"));
    	DC.connect();
    	DC.openFile("demo");
    	
    	// Setting up transpiler
    	DOM.loadEcoreIntoDOM();
    	
    	// Hosting server for browser to connect to
    	SSS = new SSServer(20895);
    	SSS.start();
    }
}
