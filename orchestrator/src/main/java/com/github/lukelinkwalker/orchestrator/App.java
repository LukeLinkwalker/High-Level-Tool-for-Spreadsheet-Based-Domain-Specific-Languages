package com.github.lukelinkwalker.orchestrator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.github.lukelinkwalker.orchestrator.Util.FileReader;
import com.github.lukelinkwalker.orchestrator.Util.Tuple;
import com.github.lukelinkwalker.orchestrator.document.Table;
import com.github.lukelinkwalker.orchestrator.lspclient.DumbLSPClient;
import com.github.lukelinkwalker.orchestrator.ssserver.SSServer;
import com.github.lukelinkwalker.orchestrator.transformer.BoundingBox;
import com.github.lukelinkwalker.orchestrator.transformer.Diff;
import com.github.lukelinkwalker.orchestrator.transformer.JsonSearch;
import com.github.lukelinkwalker.orchestrator.transformer.JsonObj;
import com.github.lukelinkwalker.orchestrator.transformer.JsonTransformer;
import com.github.lukelinkwalker.orchestrator.transformer.Model;
import com.github.lukelinkwalker.orchestrator.transformer.Sheet;
import com.github.lukelinkwalker.orchestrator.transformer.SheetTransformer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

public class App 
{
	public static DumbLSPClient DC;
	public static SSServer SSS;
	public static Model M;
	public static String Txt;
	
    public static void main( String[] args ) throws URISyntaxException, InterruptedException, ParserConfigurationException, IOException, SAXException
    {
    	// Loading model
    	M = new Model("GrammarExamples.ssmodel");
    	
    	// Hosting server for client to connect to
    	SSS = new SSServer(20895);
    	SSS.start();
    	
    	// Connecting to LSP
    	DC = new DumbLSPClient(new URI("ws://localhost:4389"));
    	DC.connect();
    }
}
