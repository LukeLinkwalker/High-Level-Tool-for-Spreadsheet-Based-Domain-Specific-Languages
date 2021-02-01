package com.github.lukelinkwalker.orchestrator.transpiler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DOM {
    private static Document dom;
    private static HashMap<Integer, String> map = new HashMap<>();
    private static ArrayList<String> list = new ArrayList<>();

    public static void loadEcoreIntoDOM() throws ParserConfigurationException, IOException, SAXException {
        //https://zetcode.com/java/dom/
        File xmlFile = new File("IotLSP.ecore");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        dom = builder.parse(xmlFile);
        dom.getDocumentElement().normalize();
    }

//    private static StringBuilder createStringsForTable(String[][] table) {
//        String ruleName = table[0][0];
//        Element ruleElement = getRuleElement(ruleName);
//        NodeList featureNodes = getFeaturesInRule(ruleElement);
//
//        int firstDataRowNumber = getFirstDataRowNumber(table);
//        int lastDataRowNumber = table.length;
//
//        StringBuilder returnString = new StringBuilder();
//
//        for (int i = firstDataRowNumber; i < lastDataRowNumber; i++) {
//            returnString.append(ruleName);
//
//            for (int j = 0; j < featureNodes.getLength(); j++) {
//                returnString.append(" ").append(table[i][j]);
//            }
//
//            returnString.append(System.getProperty("line.separator"));
//        }
//
//        return returnString;
//    }

    public static Element getRuleElement(String name) {
        NodeList ruleNodes = dom.getElementsByTagName("eClassifiers");
        Element ruleElement = null;

        for (int i = 0; i < ruleNodes.getLength(); i++) {
            Node node = ruleNodes.item(i);
            Element element = (Element) node;
            String ruleName = element.getAttribute("name");

            if (name.equals(ruleName)) {
                ruleElement = element;
            }
        }

        return ruleElement;
    }

    public static String getElementName(Element element) {
        return element.getAttribute("name");
    }

    public static NodeList getFeaturesInRule(Element element) {
        return element.getElementsByTagName("eStructuralFeatures");
    }
}