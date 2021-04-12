import * as globals from './spreadsheetGlobalVariables.js'
import * as tools from './spreadsheetTools.js'
import * as spreadsheet from './spreadsheet.js'
import * as client from './ssclient.js'
import * as setup from './spreadsheetSetup.js'

//TODO: Use later: // let regex = new RegExp('^(optional )?(object|array|alternative|attribute) : [a-zA-Z0-9_]+')

export function onInputBarInput(inputBar) {
    let inputBarText = $(inputBar).val()

    tools.hideAndClearAllErrors()
    spreadsheet.setCellText(globals.editingCell, inputBarText)
    client.sendChange(globals.editingCell)
}

export function onInputBarFocus() {
    $(globals.editingCell).css('outline', 'royalblue auto')
    $('#input-bar').css('outline', 'none')
}

export function onInputBarFocusOut() {
    $(globals.editingCell).css('outline', '')
}

export function onCellMouseDown(cell) {
    let cellTextDiv = spreadsheet.getCellTextDiv(cell)
    let cellType = spreadsheet.getCellType(cell)
    let hasBoldText = $(cellTextDiv).hasClass('bold')

    globals.setMouseDown(true)

    if (cellType === 'header' && hasBoldText) {
        let breakoutTableCells = spreadsheet.getBreakoutTableCells(cell)

        if (spreadsheet.checkHeaderCellIsHeaderForWholeTable(cell) || spreadsheet.checkTableHasNameAttribute(breakoutTableCells)) {

            globals.setMoveBreakoutTableActivated(true)
            globals.setBreakoutTableCells(breakoutTableCells)
            tools.showBreakoutTableOutline(cell)
        }
        else alert('Cannot breakout table as it doesn\'t have a name attribute')
    }
}

export function onCellMouseUp(cell) {
    if (globals.moveBreakoutTableActivated) {
        let breakoutOutlineCells = spreadsheet.getBreakoutOutlineCells(cell)

        tools.removeBreakoutTableOutline(cell)
        tools.copyAllBreakoutTableCellsAndClearOldCells(breakoutOutlineCells)
    }

    globals.setMouseDown(false)
    globals.setMoveBreakoutTableActivated(false)
}

export function onCellMouseEnter(cell) {
    // if (globals.editingCell !== cell && globals.mouseDown) {
    //     globals.setSelectedEndCell(cell)
    //
    //     let startCellIndexes = spreadsheet.getCellIndexes(globals.selectedStartCell)
    //     let endCellIndexes = spreadsheet.getCellIndexes(globals.selectedEndCell)
    //
    //     spreadsheet.findSelectedCells(startCellIndexes, endCellIndexes)
    //     tools.clearMarkedCells()
    //     tools.markCells()
    // }

    if (globals.moveBreakoutTableActivated) tools.showBreakoutTableOutline(cell)
    if ($(cell).hasClass('error')) tools.showErrorMessage(cell)
}

export function onCellMouseLeave(cell) {
    if (globals.moveBreakoutTableActivated) tools.removeBreakoutTableOutline(cell)
    if ($(cell).hasClass('error')) tools.hideErrorMessage(cell)
}

export function onCellTextDivFocus(cellTextDiv) {
    let cell = spreadsheet.getCellFromCellTextDiv(cellTextDiv)
    let inputBar = $('#input-bar')

    // if (globals.cellsMarked) tools.clearMarkedCells()
    globals.setEditingCell(cell)
    globals.setSelectedStartCell(cell)
    globals.setSelectedCells([cell])
    inputBar.val(spreadsheet.getCellText(cell))
}

export function onCellTextDivFocusout(cellTextDiv) {
    let cell = spreadsheet.getCellFromCellTextDiv(cellTextDiv)
    let infoBox = spreadsheet.getInfoBox(cell)

    tools.hideCreateTableCodeCompletionForInfoBox(infoBox, cell)
}

export function onCellInput(cell) {
    let inputBar = $('#input-bar')
    let cellIndexes = spreadsheet.getCellIndexes(cell)
    let cellText = spreadsheet.getCellText(cell)

    tools.hideAndClearAllErrors()
    inputBar.val(cellText)
    client.sendChange(cell)
    if (globals.spreadsheetType === 'sdsl') client.requestCheckIfTextIsATableName(cellText, cellIndexes[0], cellIndexes[1])
}

export function onCellClick(cell) {
    let cellIndexes = spreadsheet.getCellIndexes(cell);

    globals.setCurrentColumn(cellIndexes[0])
    globals.setCurrentRow(cellIndexes[1])
}

export function onDocumentReady() {
    setup.setupSpreadsheetTypeRadioButtons()
    setup.setupInputBar()
    setup.setupActionBar()
    setup.setupSDSL()
    setup.setupSGL()
    globals.setSpreadsheetType('sdsl')
    spreadsheet.createSpreadsheet()
    globals.setSpreadsheetType('sgl')
    spreadsheet.createSpreadsheet()
    tools.changeToSGL()
    spreadsheet.setInitialEditingCell()
    $('#sglRadioButton').prop('checked', true)
}

export function onCellKeydownTab(event) {
    let cell = globals.editingCell
    let cellIndexes = spreadsheet.getCellIndexes(cell)
    let tableCells = spreadsheet.getAllCellsFromTableCellIsIn(cell)
    let tableRange = spreadsheet.getTableRange(tableCells)
    let mergedCells = spreadsheet.getMergedCells(cell)

    if (tableRange !== null && cellIndexes[0] === tableRange[2]) {
        if (cellIndexes[1] !== tableRange[3]) tools.changeNextCellToStartOfNewRowInTable(cell, tableRange, event)
        else {
            if (tools.addRow(cell)) tools.changeNextCellToStartOfNewRowInTable(cell, tableRange, event)
        }
    }
    else if (mergedCells !== null) {
        let width = $(globals.editingCell).prop('colspan')

        if (tableRange !== null && cellIndexes[0] + width - 1=== tableRange[2]) {
            if (cellIndexes[1] !== tableRange[3]) tools.changeNextCellToStartOfNewRowInTable(cell, tableRange, event)
            else {
                if (tools.addRow(cell)) tools.changeNextCellToStartOfNewRowInTable(cell, tableRange, event)
            }
        }
    }

    else {
        tools.moveOneCellRight(event)
    }
}

export function onCellKeydownEnter(event) {
    tools.changeCellOneDownAndPossiblyAddRow(event)
}

export function onCreateTableButtonClick() {
    let cellIndexes = spreadsheet.getCellIndexes(globals.editingCell)
    let tableName = spreadsheet.getCellText(globals.editingCell)

    client.requestGetInitialTableRange(tableName, cellIndexes[0], cellIndexes[1])
}

export function onSpreadsheetTypeRadioButtonsChange() {
    let spreadsheetType = $('input[name="spreadsheetType"]:checked').val()

    if (spreadsheetType === 'sdsl') tools.changeToSDSL()
    else if (spreadsheetType === 'sgl') tools.changeToSGL()
}

export function onAddRowButtonClick() {
    tools.addRow(globals.editingCell)
}

export function onDeleteRowButtonClick() {
    tools.deleteRow(globals.editingCell)
}

export function onBuildButtonClick() {
    client.requestBuild()
}

//TODO Update. Done quickly for demo
export function onMergeButtonClick() {
    let mergedCells = []
    let numberOfRowsSelected = new Set()

    globals.selectedCells.forEach((cell) => {
        if ($(cell).attr('colspan') > 1) mergedCells.push(cell)
    })

    if ($(globals.editingCell).attr('colspan') > 1) mergedCells.push(globals.editingCell)

    globals.selectedCells.forEach((cell) => {
        let cellIndexes = spreadsheet.getCellIndexes(cell)
        numberOfRowsSelected.add(cellIndexes[1])
    })

    if (numberOfRowsSelected.size > 1) alert("Cannot merge rows!")
    else {
        if (mergedCells.length > 0) mergedCells.forEach((cell) => tools.demergeCell(cell))
        else tools.mergeCells(globals.selectedCells)
    }
}

export function onCellKeydownArrowLeft(event) {
    tools.moveOneCellLeft(event)
}

export function onCellKeydownArrowUp(event) {
    tools.moveOneCellUp(event)
}

export function onCellKeydownArrowRight(event) {
    tools.moveOneCellRight(event)
}

export function onCellKeydownArrowDown(event) {
    tools.changeCellOneDownAndPossiblyAddRow(event)
}

export function onDeleteTableButtonClick() {
    tools.deleteTable(globals.editingCell)
}