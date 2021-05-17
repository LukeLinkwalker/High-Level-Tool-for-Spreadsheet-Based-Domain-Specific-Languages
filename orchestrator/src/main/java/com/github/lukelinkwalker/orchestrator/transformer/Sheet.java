package com.github.lukelinkwalker.orchestrator.transformer;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.github.lukelinkwalker.orchestrator.Util.Tuple;

public class Sheet {
	private Cell[][] cells;
	private ArrayList<BoundingBox> tables;
	private boolean isSGL;
	
	public static Sheet newSGL() {
		return new Sheet(true);
	}
	
	public static Sheet newSDSL() {
		return new Sheet(false);
	}
	
	public Sheet(boolean isSGL) {
		cells = new Cell[1000][1000];
		tables = new ArrayList<>();
		this.isSGL = isSGL;
	}
	
	public boolean isSML() {
		return isSGL;
	}
	
	public void addData(int column, int row, int width, String data) {
		Cell cell = null;

		if(data.isEmpty() == false) {
			cell = new Cell();
			cell.setColumn(column);
			cell.setRow(row);
			cell.setData(data);
		}
		
		for(int i = 0; i < width; i += 1) {
			cells[column + i][row] = cell;
		}
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
