package com.github.lukelinkwalker.orchestrator.document;

import com.github.lukelinkwalker.orchestrator.transformer.Cell;

public class Table {
	private Text equivalent;
	private Cell root;
	private Cell[][] sheet;
	
	public Table() {
		equivalent = new Text(this);
	}
	
	public Table(Text equivalent) {
		this.equivalent = equivalent;
	}
	
	public Text getEquivalent() {
		return this.equivalent;
	}

	public Cell getData() {
		return root;
	}

	public void setData(Cell data) {
		this.root = data;
	}
	
	
	public boolean addData(int column, int row, String data) {
		Cell cell = new Cell();
		cell.setColumn(column);
		cell.setRow(row);
		cell.setData(data);
		
		System.out.println("addData: " + column + " - " + row);
		
		if(root == null) {
			root = cell;
			return true;
		} else {
			Cell parentCell = findParentCell(root, cell);
			System.out.println("parent: " + parentCell);
			if(parentCell != null) {
				System.out.println("test");
				//parentCell.addChildCell(cell);
				return true;
			}
		}
		
		return false;
	}
	
	private Cell findParentCell(Cell start, Cell child) {
		Cell result = null;
		
		System.out.println("findParentCell: " + child.getColumn() + " - " + child.getRow());
		
		int rowDiff = child.getRow() - start.getRow();
		int columnDiff = child.getColumn() - start.getColumn();
		
		System.out.println("column : (" + child.getColumn() + " - " + start.getColumn() + ") = " + columnDiff);
		System.out.println("row : (" + child.getRow() + " - " + start.getRow() + ") = " + rowDiff);
		
		if(rowDiff == 1) {
			if(columnDiff == 0) { // || columnDiff == 1) {
				result = start;
			}
		}
		
		if(result == null) {
			//for(Cell childCell : start.getChildCells()) {
			//	System.out.print("Looping");
			//	Cell tmp = findParentCell(start, child);
			//	if(result == null && tmp != null) {
			//		result = tmp;
			//	}
			//}			
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return toString(root);
	}
	
	private String toString(Cell cell) {
		StringBuilder SB = new StringBuilder();
		
		SB.append(root.getData());
		
		SB.append("[");
		
		//if(cell.getChildCells().size() > 0) {
		//	for(Cell childCell : cell.getChildCells()) {
		//		SB.append(toString(childCell));
		//	}			
		//}
		
		SB.append("]");
		
		return SB.toString();
	}
}
