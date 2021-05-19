package com.github.lukelinkwalker.orchestrator.transformer;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.lukelinkwalker.orchestrator.Util.Tuple;

public class Sheet {
	private Cell[][] cells;
	private ArrayList<BoundingBox> tables;
	private static ArrayList<Tuple<Integer, Integer>> errors;
	private boolean isSGL;
	Pattern SML_CELL_CONTENT_PATTERN;
	
	public static Sheet newSGL() {
		return new Sheet(true);
	}
	
	public static Sheet newSDSL() {
		return new Sheet(false);
	}
	
	public Sheet(boolean isSGL) {
		SML_CELL_CONTENT_PATTERN = Pattern.compile("^((optional )?(object|array|alternative|attribute|type) : [a-zA-Z0-9_]+)|Rules");

		cells = new Cell[1000][1000];
		tables = new ArrayList<>();
		errors = new ArrayList<>();
		this.isSGL = isSGL;
	}
	
	public boolean isSML() {
		return isSGL;
	}
	
	public void addData(int column, int row, int width, String data, boolean skipEval) {
		Cell cell = null;

		if(data.isEmpty() == false) {
			cell = new Cell();
			cell.setColumn(column);
			cell.setRow(row);
			cell.setData(data);
			
			if(isSGL == true && skipEval == false) {
				removeCellError(column, row);
				
				if(SML_CELL_CONTENT_PATTERN.matcher(data).find() == false) {
					errors.add(new Tuple<Integer, Integer>(column, row));					
				}
			}
		}
		
		for(int i = 0; i < width; i += 1) {
			cells[column + i][row] = cell;
		}
	}
	
	public List<Tuple<Integer, Integer>> getAllErrors() {
		return Collections.unmodifiableList(errors);
	}
	
	public void removeCellError(int column, int row) {
		List<Tuple<Integer, Integer>> toRemove = new ArrayList<>();
		
		for(int i = 0; i < errors.size(); i += 1) {
			Tuple<Integer, Integer> tmp = errors.get(i);
			if(tmp.getA() == column && tmp.getB() == row) {
				toRemove.add(tmp);
			}
		}
		
		errors.removeAll(toRemove);
	}
	
	public Cell getHead(BoundingBox bb) {
		return cells[bb.getX()][bb.getY()];
	}
	
	public Cell getCell(int column, int row) {
		return cells[column][row];
	}
	
	public ArrayList<BoundingBox> getTableRanges() {
		for(int row = 0; row < 20; row += 1) {
			for(int column = 0; column < 20; column += 1) {
				if(cells[column][row] != null) {
					System.out.print(cells[column][row].data + "  ");
				}
			}
			
			System.out.print("\n");
		}
		
		ArrayList<BoundingBox> boxes = new ArrayList<>();
		
		for(int row = 0; row < 1000; row += 1) {
			for(int column = 0; column < 1000; column += 1) {
				if(cells[column][row] != null) {
					BoundingBox bb = new BoundingBox(column, row);
					BoundingBox container = null;
					
					for(int i = 0; i < boxes.size(); i += 1) {
						BoundingBox tmp = boxes.get(i);
						
						if(tmp.getX() + tmp.getWidth() - bb.getX() == 0) {
							tmp.setWidth(tmp.getWidth() + 1);
							container = tmp;
							break;
						}
						
						if(tmp.getY() + tmp.getHeight() - bb.getY() == 0) {
							tmp.setHeight(tmp.getHeight() + 1);
							container = tmp;
							break;
						}
						
						if(bb.getY() < tmp.getY() + tmp.getHeight() &&
						   bb.getX() < tmp.getX() + tmp.getWidth() &&
						   bb.getX() > tmp.getX() &&
						   bb.getY() > tmp.getY()) 
						{
							container = tmp;
							break;
						}
					}
					
					if(container == null) {
						boxes.add(bb);
					}
				}
			}
		}
		
		return boxes;
	}
	
	
}
