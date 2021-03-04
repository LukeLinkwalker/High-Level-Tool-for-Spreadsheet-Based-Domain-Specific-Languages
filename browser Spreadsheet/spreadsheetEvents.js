import * as globals from './spreadsheetGlobalVariables.js'
import * as tools from './spreadsheetTools.js'
import * as spreadsheet from './spreadsheet.js'

export function onInputBarInput(inputBar) {
    let inputBarText = String($(inputBar).val())
    let regex = new RegExp('^[a-zA-Z0-9_]+ : [a-zA-Z0-9_]+')
    let $editingCell = $(globals.editingCell)

    if (!regex.test(inputBarText)) $editingCell.data('hiddenText', '')
    else {
        let textToBeHidden = inputBarText.substr(0, inputBarText.indexOf(':') + 2)
        $editingCell.data('hiddenText', textToBeHidden)
        $editingCell.text(inputBarText.replace(textToBeHidden, ''))
    }
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

    if ($(cell).data('hasError')) tools.showErrorMessage(cell, globals.errorMessage)
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

    if (hiddenText !== undefined) inputBar.val(hiddenText + $(cell).text())
    else inputBar.val($(cell).text())
}

export function onCellInput(cell) {
    let $cell = $(cell)
    let inputBar = $('#input-bar')
    let regex = new RegExp('^[a-zA-Z0-9_]')
    let hiddenText = $cell.data('hiddenText')

    if (!regex.test($cell.text())) $cell.removeData('hiddenText')

    if (hiddenText !== undefined && !$cell.data('hasError')) {
        inputBar.val(hiddenText + $cell.text())
    }
    else {
        inputBar.val($cell.text())
    }
}

//TODO: Måske fjerne cellChosen når en celle msiter fokus? Eller den bliver jo nok bare overwritten, men kan den miste fokus uden en anden skal vælges???
export function onCellLosesFocus(cell) {
    let $cell = $(cell)
    let cellText = $cell.text()
    let hiddenText = $cell.data('hiddenText')

    let regex = new RegExp('^[a-zA-Z0-9_]+ : [a-zA-Z0-9_]+')
    if (regex.test(cellText) && hiddenText === undefined && !$cell.data('hasError')) {
        let textToBeHidden = cellText.substr(0, cellText.indexOf(':') + 2)
        $cell.data('hiddenText', textToBeHidden)
        $cell.text(cellText.replace(textToBeHidden, ''))
    }
}