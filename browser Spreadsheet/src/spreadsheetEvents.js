import * as globals from './spreadsheetGlobalVariables.js'
import * as tools from './spreadsheetTools.js'
import * as spreadsheet from './spreadsheet.js'
import * as client from './ssclient.js'
import * as setup from './spreadsheetSetup.js'

export function onInputBarInput(inputBar) {
    let inputBarText = String($(inputBar).val())
    let regex = new RegExp('^[a-zA-Z0-9_]+ : [a-zA-Z0-9_]+')
    let $editingCell = $(globals.editingCell)

    if (!regex.test(inputBarText)) {
        $editingCell.text($editingCell.data('hiddenText') + inputBarText)
        $editingCell.data('hiddenText', '')
    }
    else {
        let textToBeHidden = inputBarText.substr(0, inputBarText.indexOf(':') + 2)
        $editingCell.data('hiddenText', textToBeHidden)
        $editingCell.text(inputBarText.replace(textToBeHidden, ''))
    }

    client.sendChange(globals.editingCell)
}

export function onInputBarFocus() {
    $(globals.editingCell).css('outline', 'royalblue auto')
    $('#input-bar').css('outline', 'none')
}

export function onInputBarFocusOut() {
    $(globals.editingCell).css('outline', '')
}

export function onCellMouseDown() {
    globals.setMouseDown(true)
}

export function onCellMouseUp() {
    globals.setMouseDown(false)
}

export function onCellMouseEnter(cell) {
    if (globals.editingCell !== cell && globals.mouseDown) {
        globals.setSelectedEndCell(cell)

        let startCellIndexes = spreadsheet.getCellIndexes(globals.selectedStartCell)
        let endCellIndexes = spreadsheet.getCellIndexes(globals.selectedEndCell)

        spreadsheet.findSelectedCells(startCellIndexes, endCellIndexes)
        tools.clearMarkedCells()
        tools.markCells()
    }

    if ($(cell).data('hasError')) tools.showErrorMessage(cell)
}

export function onCellMouseLeave(cell) {
    if ($(cell).data('hasError')) tools.hideErrorMessage(cell)
}

export function onCellFocus(cell) {
    if (globals.cellsMarked) tools.clearMarkedCells()

    globals.setEditingCell(cell)
    globals.setSelectedStartCell(cell)
    globals.setSelectedCells([cell])

    let hiddenText = $(cell).data('hiddenText')
    let inputBar = $('#input-bar')
    let cellClone = $(cell).clone()
    let divs = $('.errorMessage', cellClone)

    divs.each((i, element) => {
        element.remove()
    })

    inputBar.val(hiddenText + cellClone.text())
}

export function onCellFocusOut(cell) {
    let $cell = $(cell)
    let cellText = $cell.text()
    let hiddenText = $cell.data('hiddenText')

    let regex = new RegExp('^[a-zA-Z0-9_]+ : [a-zA-Z0-9_]+')
    if (regex.test(cellText) && hiddenText === '' && !$cell.data('hasError')) {
        let textToBeHidden = cellText.substr(0, cellText.indexOf(':') + 2)
        $cell.data('hiddenText', textToBeHidden)
        $cell.text(cellText.replace(textToBeHidden, ''))
    }
}

export function onCellInput(cell) {
    let $cell = $(cell)
    let inputBar = $('#input-bar')
    let regex = new RegExp('^[a-zA-Z0-9_]')
    let hiddenText = $cell.data('hiddenText')
    let cellClone = $(cell).clone()
    let divs = $('.errorMessage', cellClone)
    let cellIndexes = spreadsheet.getCellIndexes(cell)

    divs.each((i, element) => {
        element.remove()
    })

    if (!regex.test(cellClone.text())) $cell.data('')

    if (hiddenText !== '' && !$cell.data('hasError')) inputBar.val(hiddenText + cellClone.text())
    else inputBar.val(cellClone.text())

    client.sendChange(cell)
    client.requestCheckIfTextIsATableName(cellClone.text(), cellIndexes[0], cellIndexes[1])
}

export function onDocumentReady() {
    setup.setupKeys()
    setup.setupSpreadsheetTypeRadioButtons()
    setup.setupCreateTableButton()
    setup.setupInputBar()
    setup.setupSDSL()

    spreadsheet.createSpreadsheet()
    spreadsheet.setInitialEditingCell()

    $('#sdslRadioButton').prop('checked', true)

    //TODO: Remove after testing
    spreadsheet.testFunction()
}

export function onDocumentKeypressTab(event) {
    let cell = globals.editingCell
    let cellIndexes = spreadsheet.getCellIndexes(cell)
    let tableRange = spreadsheet.getTableRange(cell)

    if (tableRange !== null && cellIndexes[0] === tableRange[2]) {
        if (cellIndexes[1] !== tableRange[3]) changeNextCellToStartOfNewRowInTable(cell, tableRange, event)
        else {
            tools.addRow(cell)
            changeNextCellToStartOfNewRowInTable(cell, tableRange, event)
        }
    }
    else changeNextCellHorizontally(globals.editingCell, event)
}

export function changeNextCellHorizontally(cell, event) {
    let cellIndexes = spreadsheet.getCellIndexes(cell)

    event.preventDefault()
    $(globals.editingCell).css('outline', '')

    if (cellIndexes[0] < globals.columnSize - 1) {
        let newEditingCell = spreadsheet.getCellFromIndexes(cellIndexes[0] + 1, cellIndexes[1])

        globals.setEditingCell(newEditingCell)
        newEditingCell.focus()
    }
}

export function changeNextCellToStartOfNewRowInTable(cell, tableRange, event) {
    let cellIndexes = spreadsheet.getCellIndexes(cell)
    let newEditingCell = spreadsheet.getCellFromIndexes(tableRange[0], cellIndexes[1] + 1)

    event.preventDefault()
    $(globals.editingCell).css('outline', '')
    globals.setEditingCell(newEditingCell)
    newEditingCell.focus()
}

export function onDocumentKeypressEnter(event) {
    let editingCellIndexes = spreadsheet.getCellIndexes(globals.editingCell)

    event.preventDefault()
    $(globals.editingCell).css('outline', '')

    if (editingCellIndexes[1] < globals.rowSize - 1) {
        let newEditingCell = spreadsheet.getCellFromIndexes(editingCellIndexes[0], editingCellIndexes[1] + 1)

        globals.setEditingCell(newEditingCell)
        newEditingCell.focus()
    }
}

export function onCreateTableButtonClick() {
    let cellIndexes = spreadsheet.getCellIndexes(globals.editingCell)
    let tableName = $(globals.editingCell).text();

    client.requestGetInitialTableRange(tableName, cellIndexes[0], cellIndexes[1])
    // globals.setError('Hej', 0, 0)
}

export function onSpreadsheetTypeRadioButtonsChange() {
    let spreadsheetType = $('input[name="spreadsheetType"]:checked').val()

    if (spreadsheetType === 'sdsl') setup.setupSDSL()
    else if (spreadsheetType === 'sgl') setup.setupSGL()

    globals.setSpreadsheetType(spreadsheetType)
}

export function onAddRowButtonClick() {
    tools.addRow(globals.editingCell)
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