package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSCheckIfTextIsATableName {
    private String cellText;
    private int column;
    private int row;

    public String getCellText() {
        return cellText;
    }

    public void setCellText(String cellText) {
        this.cellText = cellText;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
