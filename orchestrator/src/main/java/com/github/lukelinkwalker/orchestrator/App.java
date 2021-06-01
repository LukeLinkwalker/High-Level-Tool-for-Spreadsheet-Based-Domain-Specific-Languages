package com.github.lukelinkwalker.orchestrator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.github.lukelinkwalker.orchestrator.lspclient.DummyLSPClient;
import com.github.lukelinkwalker.orchestrator.ssserver.SSServer;

public class App 
{
	public static DummyLSPClient DC;
	public static SSServer SSS;
	public static String Txt;
	
    public static void main( String[] args ) throws URISyntaxException, InterruptedException, ParserConfigurationException, IOException, SAXException
    {
    	//// Hosting server for UI to connect to
    	//SSS = new SSServer(20895);
    	//SSS.start();
    	//
    	//// Connecting to LSP
    	//DC = new DummyLSPClient(new URI("ws://localhost:4389"));
    	//DC.connect();
    }
}
