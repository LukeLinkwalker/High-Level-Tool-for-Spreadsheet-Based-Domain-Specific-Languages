package com.github.lukelinkwalker.orchestrator.transpiler;

import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Table {

    private Cell[][] table;
    private CellPosition rootCellPosition;
    //CellPosition and array is row, column / [row][column]. But real tables are column, row.
    //height = number of rows, width = number of columns.
    private int height, width, firstDataRowNumber;
    private Element element;

    public Table(CellPosition rootCellPosition, Element element) {
        this.rootCellPosition = rootCellPosition;
        this.element = element;

        setHeight();
        setWidth();
        setFirstDataRowNumber();
        table = new Cell[height][width];

        populateTableWithCells(1);
        createHeaders(element);
    }

    //TODO: Find a way to calculate this.
    private void setFirstDataRowNumber() {
        firstDataRowNumber = 2;
    }

    public int getFirstDataRowNumber() {
        return firstDataRowNumber;
    }
    
    private void populateTableWithCells(int startCellHeight) {
        Cell.getCell(rootCellPosition).setParentTable(this);
        table[0][0] = Cell.getCell(rootCellPosition);

        for (int i = startCellHeight; i < height; i++) {
            for (int j = 0; j < width; j++) {
                CellPosition localCellPosition = new CellPosition(i, j);
                CellPosition globalCellPosition = new CellPosition(i + rootCellPosition.getRowNumber(),
                        j + rootCellPosition.getColumnNumber());

                table[i][j] = new Cell(globalCellPosition, localCellPosition, this);
            }
        }
    }

    //TODO: Make work with nested headers
    private void createHeaders(Element element) {
        for (int i = 0; i < width; i++) {
            Element tempElement = (Element) DOM.getFeaturesInRule(element).item(i);
            table[1][i].setData(tempElement.getAttribute("name"));
        }
    }

    public int getHeight() {
        return height;
    }

    //TODO: Make it work with nested.
    private void setHeight() {
        height = 3;
    }

    public int getWidth() {
        return width;
    }

    //TODO: Make it work with nested.
    private void setWidth() {
        width = DOM.getFeaturesInRule(element).getLength();
    }

    public Cell[][] getTable() {
        return table;
    }

    //Virker måske ikke, ikke brugt pt.
    public ArrayList<Cell> getRow(int rowNumber) {
        return new ArrayList<Cell>(Arrays.asList(table[rowNumber]).subList(0, width));
    }

    //Virker måske ikke, ikke brugt pt.
    public ArrayList<Cell> getColumns(int columnNumber) {
        ArrayList<Cell> list = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            list.add(table[i][columnNumber]);
        }
        return list;
    }

    public void addRow() {
        //TODO: Better approach than copying everything? Too expensive maybe?
        int index = RootTable.getRootTables().indexOf(this);
        table = Arrays.copyOf(table, height + 1);
        table[height] = new Cell[width];

        height++;
        populateTableWithCells(height - 1);
        RootTable.getRootTables().set(index, this);

        updateAllFollowingRows(index);
    }

    //When a row is added, the lineNumber of each cell below this row shall be incremeneted.
    //This addRow should maybe only work with rootTables?
    private void updateAllFollowingRows(int index) {
        for (int i = index + 1; i < RootTable.getRootTables().size(); i++) {
            Table table = RootTable.getRootTables().get(i);
            for (int j = 0; j < table.getHeight(); j++) {
                for (int k = 0; k < table.getWidth(); k++) {
                    int lineNumber = table.getTable()[j][k].getLineNumber() + 1;
                    table.getTable()[j][k].setLineNumber(lineNumber);
                }
            }
        }
    }
}
