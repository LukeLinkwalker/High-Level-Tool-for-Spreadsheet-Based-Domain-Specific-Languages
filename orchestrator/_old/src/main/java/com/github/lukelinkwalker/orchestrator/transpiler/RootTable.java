package com.github.lukelinkwalker.orchestrator.transpiler;

import org.w3c.dom.Element;
import java.util.ArrayList;

public class RootTable extends Table{
    private static ArrayList<Table> rootTables = new ArrayList<>();

    public RootTable(CellPosition rootCellPosition, Element element) {
        super(rootCellPosition, element);
        rootTables.add(this);
    }

    public static int calculateFirstLineNumberForTable(Table table) {
        int tableNumberInList = rootTables.indexOf(table);
        int numberOfLinesBeforeFirstLine = 0;

        for (int i = 0; i < tableNumberInList; i++) {
            Table tempTable = rootTables.get(i);
            numberOfLinesBeforeFirstLine += tempTable.getHeight() - tempTable.getFirstDataRowNumber();
        }

        return numberOfLinesBeforeFirstLine + 1;
    }

    public static int getTotalLinesInAllRootTables() {
        int totalLines = 0;
        for (int i = 0; i < rootTables.size(); i++) {
            Table tempTable = rootTables.get(i);
             totalLines += tempTable.getHeight() - tempTable.getFirstDataRowNumber();
        }

        return totalLines;
    }

    public static ArrayList<Table> getRootTables() {
        return rootTables;
    }
}
