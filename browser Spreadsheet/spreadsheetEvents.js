import * as globals from './spreadsheetGlobalVariables.js'
import * as tools from './spreadsheetTools.js'
import * as spreadsheet from './spreadsheet.js'

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
    let cellClone = $(cell).clone()
    let divs = $('.errorMessage', cellClone)

    divs.each((i, element) => {
        element.remove()
    })

    if (hiddenText !== '') inputBar.val(hiddenText + cellClone.text())
    else inputBar.val(cellClone.text())
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

    divs.each((i, element) => {
        element.remove()
    })

    if (!regex.test(cellClone.text())) $cell.data('')

    if (hiddenText !== '' && !$cell.data('hasError')) inputBar.val(hiddenText + cellClone.text())
    else inputBar.val(cellClone.text())
}

export function onMergeButtonClick() {
    let mergedCells = []

    globals.selectedCells.forEach((cell) => {
        if ($(cell).attr('colspan') > 1) mergedCells.push(cell)
    })

    if (mergedCells.length > 1) tools.demergeCell(mergedCells)
    else tools.mergeCells()
}

//TODO: Fjern denne hvis e.preventDefault() virker
export function onMergeButtonMouseDown() {
    globals.editingCell.focus()
}

export function onBoldTextButtonClick() {
    let allCellsAreBold = true

    globals.selectedCells.forEach((cell) => {
        if (!$(cell).hasClass('bold')) allCellsAreBold = false
    })

    if (allCellsAreBold) tools.removeBoldText()
    else tools.setBoldText()
}

export function onCellAsHeaderButtonClick() {
    let allCellsAreHeaders = globals.selectedCells.every((cell) => {
        return $(cell).hasClass('header')
    })

    if (allCellsAreHeaders) tools.removeCellAsHeader()
    else tools.setCellAsHeader()
}

export function onBlackBorderButtonClick() {
    let allCellsHaveBlackBorders = globals.selectedCells.every((cell) => {
        return $(cell).hasClass('blackBorder')
    })

    if (allCellsHaveBlackBorders) tools.removeBlackBorder()
    else tools.setBlackBorder()
}

export function onDocumentReady() {
    spreadsheet.setInitialEditingCell()
}

export function onDocumentKeypressTab(event) {
    let editingCellIndexes = spreadsheet.getCellIndexes(globals.editingCell)

    event.preventDefault()
    $(globals.editingCell).css('outline', '')

    if (editingCellIndexes[0] < globals.columnSize - 1) {
        let newEditingCell = spreadsheet.getCellFromID(editingCellIndexes[0] + 1, editingCellIndexes[1])

        globals.setEditingCell(newEditingCell)
        newEditingCell.focus()
    }
}

export function onDocumentKeypressEnter(event) {
    let editingCellIndexes = spreadsheet.getCellIndexes(globals.editingCell)

    event.preventDefault()
    $(globals.editingCell).css('outline', '')

    if (editingCellIndexes[1] < globals.rowSize - 1) {
        let newEditingCell = spreadsheet.getCellFromID(editingCellIndexes[0], editingCellIndexes[1] + 1)

        globals.setEditingCell(newEditingCell)
        newEditingCell.focus()
    }
}