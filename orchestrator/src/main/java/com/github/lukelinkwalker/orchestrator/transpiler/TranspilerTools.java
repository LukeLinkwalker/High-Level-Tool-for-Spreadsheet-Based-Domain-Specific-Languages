package com.github.lukelinkwalker.orchestrator.transpiler;

import com.github.lukelinkwalker.orchestrator.App;
import com.github.lukelinkwalker.orchestrator.lspclient.messages.PublishDiagnostics;
import com.github.lukelinkwalker.orchestrator.lspclient.messages.PublishDiagnostics.DiagnosticParams;
import com.github.lukelinkwalker.orchestrator.lspclient.messages.PublishDiagnostics.DiagnosticParams.Diagnostics;
import com.github.lukelinkwalker.orchestrator.ssserver.messages.SSDiagnostic;

public class TranspilerTools {	
	public static void letterReceivedFromSpreadsheet(CellPosition cellPosition, int linePositionInCell, char character) {
        Cell cell = Cell.getCell(cellPosition);
        if (cell == null) cell = new Cell(cellPosition);

        int totalLinePosition = cell.getTotalLinePosition(linePositionInCell);
        int lineNumber = 0;

        Table parentTable = cell.getParentTable();

        if (parentTable != null) {
            lineNumber = RootTable.calculateFirstLineNumberForTable(parentTable);
            lineNumber += cell.getLocalCellPositionInParentTable().getRowNumber() - parentTable.getFirstDataRowNumber();

            //Checks if it isn't the first datarow. Rule name has to be written again then.
            if (cell.getLocalCellPositionInParentTable().getColumnNumber() == 0 &&
                    cell.getLocalCellPositionInParentTable().getRowNumber() > parentTable.getFirstDataRowNumber() &&
                    cell.getLength() == 0) {
                for (int i = 0; i < parentTable.getTable()[0][0].getLength(); i++) {
                    sendToServer(lineNumber, i, parentTable.getTable()[0][0].getData().charAt(i));
                }
            }

            if (cell.spaceBetweenLetters()) sendToServer(lineNumber, totalLinePosition - 1, ' ');
        }
        else {
            lineNumber = RootTable.getTotalLinesInAllRootTables() + 1;
        }

        cell.addData(character, linePositionInCell, lineNumber, totalLinePosition);
        sendToServer(lineNumber, totalLinePosition, character);
    }

    public static void enterPressed(CellPosition cellPosition) {
        Cell cell = Cell.getCell(cellPosition);
        String textInCell = cell.getData();

        if (DOM.getRuleElement(textInCell) != null) new RootTable(cellPosition, DOM.getRuleElement(textInCell));
    }

    public static void tabPressed(CellPosition cellPosition) {
        Table table = Cell.getCell(cellPosition).getParentTable();
        if (table != null) table.addRow();
    }

    public static void sendToServer(int lineNumber, int position, char character) {
    	//System.out.println(lineNumber + " " + position + " " + character);
    	App.DC.sendChar(lineNumber - 1, position, String.valueOf(character));
        //System.out.println("LINENUMBER: " + lineNumber + " POSITION: " + position +  " CHAR: "+ character);
    }

    // Skal ændres til PublishDiagnostic parameter
    public static void receiveMessageFromServer(PublishDiagnostics diagnostic) { //int lineNumber, int lineIndex) {
    	boolean sent = false;
    	
    	for(Diagnostics diag : diagnostic.getParams().getDiagnostics()) {
    		if(diagnostic.getParams().getDiagnostics().length > 0) {
        		int lineNumber = diagnostic.getParams().getDiagnostics()[0].getRange().getStart().getLine();
            	int lineIndex = diagnostic.getParams().getDiagnostics()[0].getRange().getStart().getCharacter();
            	
                for (Cell cell : Cell.getAllCells().values()) {
                    if (cell.getLineNumber() == lineNumber) {
                        if (lineIndex >= cell.getStartIndex() && lineIndex <= cell.getEndIndex()) {
                            System.out.println(cell.getGlobalCellPosition() + " CELLINDEX: " + (lineIndex - cell.getStartIndex()));
                        }
                    }
                }
                
                sent = true;
                SSDiagnostic result = new SSDiagnostic();
        		result.setUri(diagnostic.getParams().getUri());
        		result.setCode(diag.getCode());
        		result.setMessage(diag.getMessage());
        		result.setSeverity(diag.getSeverity());
        		App.SSS.sendDiagnostic(result);
        	}
    	}
    	
    	if(sent == false) {
    		SSDiagnostic result = new SSDiagnostic();
    		result.setUri(diagnostic.getParams().getUri());
    		result.setCode("-1");
    		App.SSS.sendDiagnostic(result);
    	}
    }
}
