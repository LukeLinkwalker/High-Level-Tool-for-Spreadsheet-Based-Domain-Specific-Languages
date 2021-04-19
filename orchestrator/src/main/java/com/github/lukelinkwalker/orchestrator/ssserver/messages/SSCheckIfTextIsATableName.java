package com.github.lukelinkwalker.orchestrator.ssserver.messages;

public class SSCheckIfTextIsATableName {
    private String cellText;
    private int column;
    private int row;
    private String spreadsheetType;

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

    public String getSpreadsheetType() {
        return spreadsheetType;
    }

    public void setSpreadsheetType(String spreadsheetType) {
        this.spreadsheetType = spreadsheetType;
    }
}
