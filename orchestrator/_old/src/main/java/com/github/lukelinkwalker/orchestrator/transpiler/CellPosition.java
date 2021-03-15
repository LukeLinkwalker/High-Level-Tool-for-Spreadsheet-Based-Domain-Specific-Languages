package com.github.lukelinkwalker.orchestrator.transpiler;

import java.util.Objects;

public class CellPosition {
    private int rowNumber;
    private int columnNumber;

    public CellPosition(int rowNumber, int columnNumber) {
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellPosition cellPosition = (CellPosition) o;
        return rowNumber == cellPosition.rowNumber && columnNumber == cellPosition.columnNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowNumber, columnNumber);
    }

    @Override
    public String toString() {
        return "CellPosition{" +
                "rowNumber=" + rowNumber +
                ", columnNumber=" + columnNumber +
                '}';
    }
}
