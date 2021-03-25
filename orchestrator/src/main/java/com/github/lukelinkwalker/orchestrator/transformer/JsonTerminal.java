package com.github.lukelinkwalker.orchestrator.transformer;

import java.util.ArrayList;

public class JsonTerminal {
	private String name;
	private final String type = "terminal";
	private ArrayList<String> subtypes;
	
	public JsonTerminal() {
		subtypes = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<String> getTypes() {
		return subtypes;
	}
	
	public void setTypes(ArrayList<String> types) {
		this.subtypes = types;
	}
	
	public void addType(String type) {
		subtypes.add(type);
	}
}
