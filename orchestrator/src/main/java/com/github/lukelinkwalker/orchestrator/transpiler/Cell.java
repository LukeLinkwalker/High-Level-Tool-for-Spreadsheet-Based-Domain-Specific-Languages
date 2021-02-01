package com.github.lukelinkwalker.orchestrator.transpiler;

import java.util.HashMap;
import java.util.List;

public class Cell {
    private CellPosition globalCellPosition;
    private CellPosition localCellPositionInParentTable;
    private Table parentTable;
    private int length = 0;
    private String data = "";

    private int lineNumber = -1;
    private int startIndex = -1;
    private int endIndex = -1;

    private static HashMap<CellPosition, Cell> allCells = new HashMap<>();

    public Cell(CellPosition globalCellPosition) {
        this.globalCellPosition = globalCellPosition;
        allCells.put(globalCellPosition, this);
    }

    public Cell(CellPosition globalCellPosition, CellPosition localCellPositionInParentTable, Table parentTable) {
        this.globalCellPosition = globalCellPosition;
        this.localCellPositionInParentTable = localCellPositionInParentTable;
        this.parentTable = parentTable;

        allCells.put(globalCellPosition, this);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        length = data.length();
    }

    public void addData(char character, int lineIndex, int lineNumber, int totalLineIndex) {
        data = data.substring(0, lineIndex) + character + data.substring(lineIndex, length);
        length = data.length();
        if (this.lineNumber == -1) this.lineNumber = lineNumber;

        if (startIndex == -1) startIndex = totalLineIndex;
        if (endIndex < totalLineIndex + 1) endIndex = totalLineIndex;
    }

    private void updateIndexesInFollowingCells() {
        for (int i = localCellPositionInParentTable.getColumnNumber(); i < getParentTable().getWidth(); i++) {
            if (startIndex != -1) parentTable.getTable()[localCellPositionInParentTable.getRowNumber()][i].incrementStartIndex();
            if (endIndex != -1) parentTable.getTable()[localCellPositionInParentTable.getRowNumber()][i].incrementEndIndex();
        }
    }

    public boolean spaceBetweenLetters() {
        return (localCellPositionInParentTable.getRowNumber() != 0 && data.length() == 0);
    }

    public static Cell getCell(CellPosition cellPosition) {
        return allCells.get(cellPosition);
    }

    public Table getParentTable() {
        return parentTable;
    }

    public void setParentTable(Table parentTable) {
        this.parentTable = parentTable;
    }

    public CellPosition getLocalCellPositionInParentTable() {
        return localCellPositionInParentTable;
    }

    public CellPosition getGlobalCellPosition() {
        return globalCellPosition;
    }

    public int getTotalLinePosition(int linePositionInCell) {
        if (parentTable != null) {
            //+1 to account for space between header and first cell.
            int linePosition = parentTable.getTable()[0][0].length + 1;

            for (int i = 0; i < localCellPositionInParentTable.getColumnNumber(); i++) {
                //+1 to account for spaces between cells in same row.
                linePosition += parentTable.getTable()[localCellPositionInParentTable.getRowNumber()][i].length + 1;
            }

            return linePosition + linePositionInCell;
        }

        else return linePositionInCell;
    }

    public int getLength() {
        return length;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void incrementStartIndex() {
        startIndex++;
    }

    public void incrementEndIndex() {
        endIndex++;
    }

    public static HashMap<CellPosition, Cell> getAllCells() {
        return allCells;
    }

    //Used for testing.
    @Override
    public String toString() {
        return data;
    }
}
