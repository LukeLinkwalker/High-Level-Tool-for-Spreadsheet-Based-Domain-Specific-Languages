import * as elementCell from './spreadsheetElements/cell.js'
import * as elementError from './spreadsheetElements/error.js'
import * as elementInformationBox from './spreadsheetElements/informationBox.js'
import * as elementMenuBar from './spreadsheetElements/menuBar.js'
import * as elementTable from './spreadsheetElements/table.js'
import * as toolsBreakout from './spreadsheetTools/breakout.js'
import * as toolsMarking from './spreadsheetTools/marking.js'
import * as toolsMerge from './spreadsheetTools/merge.js'
import * as toolsMoveBetweenCells from './spreadsheetTools/moveBetweenCells.js'
import * as client from './client.js'
import * as globalVariables from './globalVariables.js'
import * as setup from './setup.js'

export function onInputBarInput(inputBar) {
    let inputBarText = $(inputBar).val()

    elementError.hideAndClearAllErrors()
    elementCell.setCellText(globalVariables.editingCell, inputBarText, true)
}

export function onInputBarFocus() {
    $(globalVariables.editingCell).css('outline', 'royalblue auto')
    $('#input-bar').css('outline', 'none')
}

export function onInputBarFocusOut() {
    $(globalVariables.editingCell).css('outline', '')
}

export function onCellMouseDown(cell) {
    let cellTextDiv = elementCell.getCellTextDiv(cell)
    let cellType = elementCell.getCellType(cell)
    let hasBoldText = $(cellTextDiv).hasClass('bold')

    if (cellType !== 'header') globalVariables.setMouseDown(true)
    if (cellType === 'header' && hasBoldText) {
        let breakoutTableCells = toolsBreakout.getBreakoutTableCells(cell)

        globalVariables.setMoveBreakoutTableActivated(true)
        globalVariables.setBreakoutTableCells(breakoutTableCells)
        toolsBreakout.showBreakoutTableOutline(cell)
    }
}

export function onCellMouseUp(cell) {
    if (globalVariables.moveBreakoutTableActivated) {
        if (elementTable.checkHeaderCellIsHeaderForWholeTable(globalVariables.breakoutTableCells[0]) ||
                elementTable.checkTableHasNameAttribute(globalVariables.breakoutTableCells)) toolsBreakout.moveOrBreakoutCells(cell)
        else {
            if (cell !== globalVariables.breakoutTableCells[0]) alert('Cannot breakout table as it doesn\'t have a name attribute')
            toolsBreakout.removeBreakoutTableOutline(cell)
        }
    }

    globalVariables.setMouseDown(false)
    globalVariables.setMoveBreakoutTableActivated(false)
}

export function onCellMouseEnter(cell) {
    let errorBox = elementError.getErrorBox(cell)

    if (globalVariables.moveBreakoutTableActivated) toolsBreakout.showBreakoutTableOutline(cell)
    if ($(cell).hasClass('error')) elementError.showErrorMessage(errorBox)

    if (globalVariables.editingCell !== cell && globalVariables.mouseDown) {
        globalVariables.setSelectedEndCell(cell)

        let startCellIndexes = elementCell.getCellIndexes(globalVariables.selectedStartCell)
        let endCellIndexes = elementCell.getCellIndexes(globalVariables.selectedEndCell)

        toolsMarking.findSelectedCells(startCellIndexes, endCellIndexes)
        toolsMarking.clearMarkedCells()
        toolsMarking.markCells()
    }
}

export function onCellMouseLeave(cell) {
    let errorBox = elementError.getErrorBox(cell)

    if (globalVariables.moveBreakoutTableActivated) toolsBreakout.removeBreakoutTableOutline(cell)
    if ($(cell).hasClass('error')) elementError.hideErrorMessage(errorBox)
}

export function onCellTextDivFocus(cellTextDiv) {
    let cell = elementCell.getCellFromCellTextDiv(cellTextDiv)
    let inputBar = $('#input-bar')

    //TODO: Breakout highlighting
    // if (spreadsheet.getIsBrokenOut(cell)) tools.highlightCellAndBreakoutReferenceCell(cell, cellTextDiv)

    if (globalVariables.cellsMarked) toolsMarking.clearMarkedCells()
    globalVariables.setEditingCell(cell)
    globalVariables.setSelectedStartCell(cell)
    globalVariables.setSelectedCells([cell])
    inputBar.val(elementCell.getCellText(cell))
}

export function onCellTextDivFocusout(cellTextDiv) {
    let cell = elementCell.getCellFromCellTextDiv(cellTextDiv)
    let infoBox = elementInformationBox.getInfoBox(cell)

    elementInformationBox.hideCreateTableCodeCompletionForInfoBox(infoBox, cell)
    globalVariables.setHasTypedInCell(false)
    //TODO: Breakout highlighting
    // if (spreadsheet.getIsBrokenOut(cell)) tools.removeHighlightCellAndBreakoutReferenceCell(cell, cellTextDiv)
}

export function onCellInput(cell) {
    let inputBar = $('#input-bar')
    let cellIndexes = elementCell.getCellIndexes(cell)
    let cellText = elementCell.getCellText(cell)

    elementError.hideAndClearAllErrors()
    inputBar.val(cellText)
    client.sendChange(cell)
    client.requestCheckIfTextIsATableName(cellText, cellIndexes[0], cellIndexes[1], globalVariables.spreadsheetType)
    globalVariables.setHasTypedInCell(true)
}

export function onCellClick(cell) {
    let cellIndexes = elementCell.getCellIndexes(cell);

    globalVariables.setCurrentColumn(cellIndexes[0])
    globalVariables.setCurrentRow(cellIndexes[1])
}

export function onDocumentReady() {
    setup.setupSpreadsheetTypeRadioButtons()
    setup.setupInputBar()
    setup.setupActionBar()
    setup.setupSDSL()
    setup.setupSML()
    elementMenuBar.changeToSML()
    $('#smlRadioButton').prop('checked', true)
    globalVariables.setSpreadsheetName('Hello')
}

export function onCellKeydownTab(event) {
    let cell = globalVariables.editingCell
    let cellIndexes = elementCell.getCellIndexes(cell)
    let tableCells = elementTable.getAllCellsFromTableCellIsIn(cell)
    let tableRange = elementTable.getTableRange(tableCells)
    let mergedCells = toolsMerge.getMergedCells(cell)

    if (tableRange !== null && cellIndexes[0] === tableRange[2]) {
        if (cellIndexes[1] !== tableRange[3]) toolsMoveBetweenCells.changeNextCellToStartOfNewRowInTable(cell, tableRange, event)
        else {
            if (elementTable.addRow(cell)) toolsMoveBetweenCells.changeNextCellToStartOfNewRowInTable(cell, tableRange, event)
        }
    }
    else if (mergedCells !== null) {
        let width = $(globalVariables.editingCell).prop('colspan')

        if (tableRange !== null && cellIndexes[0] + width - 1 === tableRange[2]) {
            if (cellIndexes[1] !== tableRange[3]) toolsMoveBetweenCells.changeNextCellToStartOfNewRowInTable(cell, tableRange, event)
            else {
                if (elementTable.addRow(cell)) toolsMoveBetweenCells.changeNextCellToStartOfNewRowInTable(cell, tableRange, event)
            }
        }
    }

    else {
        toolsMoveBetweenCells.moveOneCellRight(event)
    }
}

export function onCellKeydownEnter(event) {
    toolsMoveBetweenCells.changeCellOneDownAndPossiblyAddRow(event)
}

export function onCreateTableButtonClick() {
    let cellIndexes = elementCell.getCellIndexes(globalVariables.editingCell)
    let tableName = elementCell.getCellText(globalVariables.editingCell)
    let infoBox = elementInformationBox.getInfoBox(globalVariables.editingCell)

    elementInformationBox.hideCreateTableCodeCompletionForInfoBox(infoBox, globalVariables.editingCell)
    client.requestGetInitialTableRange(tableName, cellIndexes[0], cellIndexes[1], globalVariables.spreadsheetType)
}

export function onSpreadsheetTypeRadioButtonsChange() {
    let spreadsheetType = $('input[name="spreadsheetType"]:checked').val()

    if (spreadsheetType === 'sdsl') {
        client.requestNewFile(false);
        elementMenuBar.changeToSDSL()
    }
    else {
        client.requestNewFile(true);
        elementMenuBar.changeToSML()
    } 
}

export function onAddRowButtonClick() {
    elementTable.addRow(globalVariables.editingCell)
}

export function onDeleteRowButtonClick() {
    elementTable.deleteRow(globalVariables.editingCell)
}

export function onMergeButtonClick() {
    let mergedCells = []
    let numberOfRowsSelected = new Set()

    globalVariables.selectedCells.forEach((cell) => {
        let width = $(cell).attr('colspan')
        if (width > 1) {
            let cellIndexes = elementCell.getCellIndexes(cell)

            for (let i = 0; i < width; i++) {
                let mergedCell = elementCell.getCellFromIndexes(cellIndexes[0] + i, cellIndexes[1])
                mergedCells.push(mergedCell)
            }
        }
    })

    globalVariables.selectedCells.forEach((cell) => {
        let cellIndexes = elementCell.getCellIndexes(cell)
        numberOfRowsSelected.add(cellIndexes[1])
    })

    if (numberOfRowsSelected.size > 1) alert('Cannot merge rows!')
    else {
        if (mergedCells.length > 0) mergedCells.forEach((cell) => toolsMerge.demergeCell(cell))
        else toolsMerge.mergeCells(globalVariables.selectedCells)
    }
}

export function onCreateRulesTableButtonClick() {
    let cellIndexes = elementCell.getCellIndexes(globalVariables.editingCell)
    let infoBox = elementInformationBox.getInfoBox(globalVariables.editingCell)

    if (!globalVariables.ruleTableCreated) {
        elementInformationBox.hideCreateTableCodeCompletionForInfoBox(infoBox, globalVariables.editingCell)
        client.requestGetInitialTableRange('Rules', cellIndexes[0], cellIndexes[1], globalVariables.spreadsheetType)
        globalVariables.setRuleTableCreated(true)
    }
    else alert('Rules table is already created!')
}

export function onBuildButtonClick() {
    client.requestBuild()
}

export function onCellKeydownArrowLeft(event) {
    if (!globalVariables.hasTypedInCell) toolsMoveBetweenCells.moveOneCellLeft(event)
}

export function onCellKeydownArrowUp(event) {
    toolsMoveBetweenCells.moveOneCellUp(event)
}

export function onCellKeydownArrowRight(event) {
    if (!globalVariables.hasTypedInCell) toolsMoveBetweenCells.moveOneCellRight(event)
}

export function onCellKeydownArrowDown(event) {
    toolsMoveBetweenCells.changeCellOneDownAndPossiblyAddRow(event)
}

export function onDeleteTableButtonClick() {
    elementTable.deleteTable(globalVariables.editingCell)
}