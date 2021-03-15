package com.github.lukelinkwalker.orchestrator.transformer;

import java.util.ArrayList;

import com.github.lukelinkwalker.orchestrator.Util.Tuple;

public class Sheet {
	private Cell[][] cells;
	private ArrayList<BoundingBox> tables;
	
	public Sheet() {
		cells = new Cell[1000][1000];
		tables = new ArrayList<>();
	}
	
	public void addData(int column, int row, int width, String data) {
		Cell cell = new Cell();
		cell.setColumn(column);
		cell.setRow(row);
		cell.setData(data);
		
		for(int i = 0; i < width; i += 1) {
			cells[column + i][row] = cell;
		}
	}
	
	public Cell getCell(int column, int row) {
		return cells[column][row];
	}
	
	public ArrayList<BoundingBox> getTableRanges() {
		ArrayList<BoundingBox> resultingBoxes = new ArrayList<>();
		ArrayList<BoundingBox> workingBoxes = new ArrayList<>();
		ArrayList<BoundingBox> toRemove = new ArrayList<>();
		
		// Limits possible size of documents could be made dynamic
		for(int x = 0; x < 1000; x += 1) { 
			for(int y = 0; y < 1000; y += 1) {
				if(cells[x][y] != null) {			
					workingBoxes.add(new BoundingBox(x, y));
				}
			}
		}
		
		while(true) {
			if(workingBoxes.size() == 0) {
				break;
			}
			
			int mergeCount = 0;
			toRemove.clear();
			
			BoundingBox tmpBB = workingBoxes.get(0);
			for(int i = 1; i < workingBoxes.size(); i += 1) {
				if(BoundingBox.mergeCheck(tmpBB, workingBoxes.get(i)).size() == 1) {
					toRemove.add(workingBoxes.get(i));
					tmpBB = BoundingBox.merge(tmpBB, workingBoxes.get(i));
					mergeCount += 1;
				}
			}
			
			if(mergeCount > 0) {
				workingBoxes.remove(0);
				resultingBoxes.add(tmpBB);
			}
			
			for(BoundingBox bb : toRemove) {
				workingBoxes.remove(bb);
			}
			
			if(mergeCount == 0) {
				break;
			}
		}
		
		resultingBoxes.addAll(workingBoxes);
		
		return resultingBoxes;
	}
}
