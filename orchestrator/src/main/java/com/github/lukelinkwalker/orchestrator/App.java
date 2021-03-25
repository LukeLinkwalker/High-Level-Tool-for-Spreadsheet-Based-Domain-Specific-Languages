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
import com.github.lukelinkwalker.orchestrator.document.LanguageLoader;
import com.github.lukelinkwalker.orchestrator.document.Table;
import com.github.lukelinkwalker.orchestrator.lspclient.DumbLSPClient;
import com.github.lukelinkwalker.orchestrator.ssserver.SSServer;
import com.github.lukelinkwalker.orchestrator.transformer.BoundingBox;
import com.github.lukelinkwalker.orchestrator.transformer.Diff;
import com.github.lukelinkwalker.orchestrator.transformer.JsonSearch;
import com.github.lukelinkwalker.orchestrator.transformer.JsonTerminal;
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
//    	LanguageLoader LL = new LanguageLoader();
//
//    	// Loading model
//    	M = new Model("orchestrator/GrammarExamples.ssmodel");
//
//    	for(JsonObj header : attributes) {
//    		System.out.println(header.getName() + " is " + header.getType() + " requiring type " + header.getDataTypes()[0].getValue()); // " and " + ((header.isOptional() == true ? "is optional" : "is not optional")));
//    	}
//
//    	ArrayList<ArrayList<String>> arrLayout = M.getArrayLayout("Config");
//    	System.out.println("Arrays : " + arrLayout.size());
//    	for(int i = 0; i < arrLayout.size(); i += 1) {
//    		System.out.print(i + " : ");
//
//    		for(int k = 0; k < arrLayout.get(i).size(); k += 1) {
//    			System.out.print(arrLayout.get(i).get(k) + " ");
//    		}
//
//    		System.out.print("\n");
//    	}
//
//    	JsonTerminal terminal = new JsonTerminal();
//    	terminal.setName("Test");
//    	terminal.addType("Loco");
//    	terminal.addType("Gali");
//    	System.out.println(new Gson().toJson(terminal));
    	
    	// Hosting server for client to connect to
    	SSS = new SSServer(20895);
    	SSS.start();
    	
    	// Connecting to LSP
    	DC = new DumbLSPClient(new URI("ws://localhost:4389"));
    	DC.connect();
    	
    	
    	
    	//Sheet test = new Sheet(true);
    	//
    	//test.addData(0, 0, 2, "CustomRule");
    	//test.addData(0, 1, 1, "Name");
    	//test.addData(1, 1, 1, "Rule");
    	//
    	//test.addData(0, 2, 1, "Galimatias");
    	//test.addData(1, 2, 1, "10 > 5");
    	//
    	//test.addData(0, 3, 1, "Kagearm");
    	//test.addData(1, 3, 1, "55 < 10 AND 1 > 10");
    	//
    	//// ------------------------------------------------------------
    	//
    	//test.addData(0, 7, 2, "CustomType");
    	//test.addData(0, 8, 1, "Name");
    	//test.addData(1, 8, 1, "Subtype");
    	//
    	//test.addData(0, 9, 1, "SensorType");
    	//test.addData(1, 9, 1, "Thermostat");
    	//test.addData(1, 10, 1, "Ambient Light");
    	//
    	//test.addData(0, 11, 1, "Temperature Unit");
    	//test.addData(1, 11, 1, "Celsius");
    	//test.addData(1, 12, 1, "Fahrenheit");
        //
    	//// ------------------------------------------------------------
    	//
    	//test.addData(0, 14, 7, "Array : Config");
    	//
    	//test.addData(0, 15, 1, "Attribute : Name");
    	//test.addData(1, 15, 5, "Array : Sensors");
    	//test.addData(6, 15, 1, "Alternative : Functions");
        //
    	//test.addData(1, 16, 1, "Attribute : Name");
    	//test.addData(2, 16, 2, "Array : Inputs");
    	//test.addData(4, 16, 2, "Array : Outputs");
        //
    	//test.addData(2, 17, 1, "Attribute : Source");
    	//test.addData(3, 17, 1, "Attribute : Rate");
    	//test.addData(4, 17, 1, "Attribute : Type");
    	//test.addData(5, 17, 1, "Attribute : Rate");
        //
    	//test.addData(0, 18, 1, "Type : String");
    	//test.addData(1, 18, 1, "Type : String");
    	//test.addData(2, 18, 1, "Type : String");
    	//test.addData(3, 18, 1, "Type : Int");
    	//test.addData(4, 18, 1, "Type : String");
    	//test.addData(5, 18, 1, "Type : Float");
    	//test.addData(6, 18, 1, "Type : Get");
    	//test.addData(6, 19, 1, "Type : Set");
    	//
    	//// ------------------------------------------------------------
    	//
    	//test.addData(0, 21, 5, "Object : OldConfig");
    	//test.addData(0, 22, 1, "Attribute : Name");
    	//test.addData(1, 22, 3, "Array : Sensors");
    	//test.addData(4, 22, 1, "Alternative : Functions");
        //
    	//test.addData(1, 23, 1, "Attribute : Name");
    	//test.addData(2, 23, 2, "Array : Outputs");
        //
    	//test.addData(2, 24, 1, "Attribute : Type");
    	//test.addData(3, 24, 1, "Attribute : Rate");
        //
    	//test.addData(0, 25, 1, "Type : String");
    	//test.addData(1, 25, 1, "Type : String");
    	//test.addData(2, 25, 1, "Type : String");
    	//test.addData(3, 25, 1, "Type : Int");
    	//test.addData(4, 25, 1, "Type : Get");
    	//test.addData(4, 26, 1, "Type : Set");
    	//
    	//ArrayList<BoundingBox> tables = test.getTableRanges();
    	//for(BoundingBox bb : tables) {
    	//	System.out.println("Test: " + bb.toString());
    	//}
    	//
    	//System.out.println("Test: " + SheetTransformer.parseSGL(test));
    	
//    	Sheet test = new Sheet(false);
//
//    	// Header
//    	test.addData(0, 0, 7, "Config");
//
//    	test.addData(0, 1, 1, "Name");
//    	test.addData(1, 1, 5, "Sensors");
//    	test.addData(4, 1, 1, "Functions");
//
//    	test.addData(1, 2, 1, "Name");
//    	test.addData(2, 2, 2, "Inputs");
//    	test.addData(4, 2, 2, "Outputs");
//
//    	test.addData(2, 3, 1, "Source");
//    	test.addData(3, 3, 1, "Rate");
//    	test.addData(4, 3, 1, "Type");
//    	test.addData(5, 3, 1, "Rate");
//
//    	// Content
//    	test.addData(0, 4, 1, "EUConfig");
//        test.addData(1, 4, 1, "Temperature");
//        test.addData(2, 4, 1, "Environment");
//        test.addData(3, 4, 1, "50");
//        test.addData(4, 4, 1, "Celsius");
//        test.addData(5, 4, 1, "1000");
//        test.addData(6, 4, 1, "Get");
//
//        test.addData(4, 5, 1, "Fahrenheit");
//        test.addData(5, 5, 1, "1000");
//
//        test.addData(1, 6, 1, "Position");
//        test.addData(2, 6, 1, "Satellite");
//        test.addData(3, 6, 1, "50");
//        test.addData(4, 6, 1, "Relative");
//        test.addData(5, 6, 1, "1000");
//
//        test.addData(2, 7, 1, "Map");
//        test.addData(3, 7, 1, "10000");
//        test.addData(4, 7, 1, "Absolute");
//        test.addData(5, 7, 1, "1000");
//
//        // ---------------------------------------
//
//    	test.addData(0, 8, 1, "USConfig");
//        test.addData(1, 8, 1, "Temperature");
//        test.addData(2, 8, 1, "Environment");
//        test.addData(3, 8, 1, "20");
//        test.addData(4, 8, 1, "Celsius");
//        test.addData(5, 8, 1, "2000");
//        test.addData(6, 8, 1, "Get");
//
//        test.addData(4, 9, 1, "Fahrenheit");
//        test.addData(5, 9, 1, "2000");
//
//        test.addData(1, 10, 1, "Position");
//        test.addData(2, 10, 1, "Satellite");
//        test.addData(3, 10, 1, "20");
//        test.addData(4, 10, 1, "Relative");
//        test.addData(5, 10, 1, "2000");
//
//        test.addData(4, 11, 1, "Absolute");
//        test.addData(5, 11, 1, "2000");
//
//
//
//        //test.addData(20, 20, 2, "Speedometer");
//        //test.addData(20, 21, 1, "kmh");
//        //test.addData(21, 21, 1, "mph");
//
//        ArrayList<BoundingBox> tables = test.getTableRanges();
//        System.out.println("Size: " + tables.size());
//    	for(BoundingBox bb : tables) {
//    		System.out.println("Test: " + bb.toString());
//    	}
//
//    	String SDSL_JSON = SheetTransformer.parseSDSL(test);
//    	System.out.println("SDSL : " + SDSL_JSON);
    }
}
