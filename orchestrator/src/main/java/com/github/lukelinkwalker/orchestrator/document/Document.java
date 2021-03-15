package com.github.lukelinkwalker.orchestrator.document;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.TableStringConverter;

import com.github.lukelinkwalker.orchestrator.Util.Tuple;

public class Document {
	private LinkedList<Table> tables;
	private LinkedList<Text> texts;
	private String languageID;
	
	public Document(String language_id) {
		this.languageID = language_id;
		tables = new LinkedList<>();
		texts = new LinkedList<>();
	}
	
	public void add(int index, Table table) {
		tables.add(index, table);
		texts.add(index, table.getEquivalent());
	}
	
	public void add(int index, Text text) {
		texts.add(index, text);
		tables.add(text.getEquivalent());
	}
	
	public Tuple<Table, Text> remove(int index) {
		Tuple<Table, Text> result = new Tuple<>();
		result.setA(tables.remove(index));
		result.setB(texts.remove(index));
		return result;
	}
	
	public Tuple<Table, Text> remove(Table table) {
		return remove(tables.indexOf(table));
	}
	
	public Tuple<Table, Text> remove(Text text) {
		return remove(texts.indexOf(text));
	}
	
	public void move(int source, int destination) {
		Table table = tables.remove(source);
		Text text = texts.remove(source);
		tables.add(destination, table);
		texts.add(destination, text);
	}
	
	public int indexOf(Table table) {
		return tables.indexOf(table);
	}
	
	public int indexOf(Text text) {
		return texts.indexOf(text);
	}
	
	public Tuple<Table, Text> get(int index) {
		Tuple<Table, Text> result = new Tuple<>();
		result.setA(tables.get(index));
		result.setB(texts.get(index));
		return result;
	}
	
	public List<Table> getTables() {
		return Collections.unmodifiableList(tables);
	}
	
	public List<Text> getTexts() {
		return Collections.unmodifiableList(texts);
	}
	
	public String getLanguageID() {
		return languageID;
	}
	
	public void getAllCells() {
		
	}
	
	public void getAllText() {
		
	}
}
