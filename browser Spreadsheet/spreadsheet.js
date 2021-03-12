import * as events from './spreadsheetEvents.js'
import * as tools from './spreadsheetTools.js'
import * as globals from './spreadsheetGlobalVariables.js'

//TODO: Remove after testing
export function testFunction() {
    $('#testErrorButton').on('click', () => {
        tools.showError(globals.errorCellIndexes, globals.errorLineIndexes, globals.errorMessage)
    })

    $('#removeErrorButton').on('click', () => {
        tools.removeError(globals.editingCell)
    })
}

export function createTable() {
    globals.setColumnSize(20)
    globals.setRowSize(20)

    let table = $('<table id="dynamicTable">')
    let tableBody = $('<tbody id="dynamicBody">')

    for (let row = 0; row < globals.rowSize; row++) {
        let newRow = $('<tr>')

        for (let column = 0; column < globals.columnSize; column++) {
            newRow.append(createCell(column, row))
        }
        tableBody.append(newRow)
    }

    createRowHeader(globals.rowSize, tableBody)
    createColumnHeader(globals.columnSize, table)
    table.append(tableBody)
    $('#cell-container').append(table)
}

export function createCell(column, row) {
    let cell = $('<td>')
    cell.attr('id', createCellID(column, row))
    cell.attr('contenteditable', 'true')
    cell.data('hasError', false)
    cell.data('hiddenText', '')
    cell.on('mousedown', () => events.onCellMouseDown())
    cell.on('mouseup', () => events.onCellMouseUp())
    cell.on('mouseenter', (e) => events.onCellMouseEnter(e.target))
    cell.on('mouseleave', (e) => events.onCellMouseLeave(e.target))
    cell.on('focus', (e) => events.onCellFocus(e.target))
    cell.on('input', (e) => events.onCellInput(e.target))
    cell.on('focusout', (e) => events.onCellFocusOut(e.target))

    return cell
}

export function createCellID(column, row) {
    return 'cell-' + column + '-' + row
}

export function getCellFromID(column, row) {
    return $('#' + createCellID(column, row))[0]
}

export function getCellIndexes(cell) {
    let cellID = $(cell).attr('id')
    let matches = cellID.match(/^cell-(\d+)-(\d+)/)

    if (matches) return [Number(matches[1]), Number(matches[2])]
}

export function setInitialEditingCell() {
    let cell = getCellFromID(0, 0)

    globals.setEditingCell(cell)
    cell.focus()
}

export function createColumnHeader(tableSize, table) {
    let columnRow = $('<thead>')
    columnRow.append($('<th>'))

    for (let column = 0; column < tableSize; column++) {
        let tableHeader = $('<th>')
        tableHeader.text(String.fromCharCode(65 + column))
        columnRow.append(tableHeader)
    }

    $(table).prepend(columnRow)
}

export function createRowHeader(tableSize, table) {
    $('tr', table).each( (index, element) => {
        let tableHeader = $('<th>')
        tableHeader.text(index + 1)
        $(element).prepend(tableHeader)
    })
}

export function findSelectedCells(selectedStartIndexes, selectedEndIndexes) {
    let column1 = selectedStartIndexes[0]
    let row1 = selectedStartIndexes[1]
    let column2 = selectedEndIndexes[0]
    let row2 = selectedEndIndexes[1]

    if (column2 < column1) {
        let temp = column1;
        column1 = column2;
        column2 = temp;
    }
    if (row2 < row1) {
        let temp = row1;
        row1 = row2;
        row2 = temp;
    }

    globals.setSelectedCells([])

    for (let column = 0; column < globals.columnSize; column++) {
        for (let row = 0; row < globals.rowSize; row++) {
            let cell = getCellFromID(column, row)

            if (column >= column1 && column <= column2 && row >= row1 && row <= row2) {
                globals.selectedCells.push(cell)
            }
        }
    }
}

export function getCellsInRange(startCell, endCell) {
    let cellsInRange = []

    for (let i = startCell[0]; i <= endCell[0] ; i++) {
        for (let j = startCell[1]; j <= endCell[1]; j++) {
            cellsInRange.push(getCellFromID(i, j))
        }
    }

    return cellsInRange
}

export function checkCellIsEmpty(cell) {
    return $(cell).text() === ''
}

export function setText(cell, text) {
    //TODO: Change this based on errorMessage and hiddenText? Not sure.
    $(cell).text(text)
}

export function markCells() {
    globals.setCellsMarked(true)

    //TODO: Update for data cells as well.
    globals.selectedCells.forEach((cell) => {
        if ($(cell).hasClass('header')) $(cell).addClass('selectedHeader')
        else $(cell).addClass('selected')
    })
}

export function clearMarkedCells() {
    globals.setCellsMarked(false)

    //TODO: Update for data cells as well.
    $('.selected').each((i, element) => $(element).removeClass('selected'))
    $('.selectedHeader').each((i, element) => $(element).removeClass('selectedHeader'))
}

export function clearCell(cell) {
    let mergedCells = tools.getMergedCells(cell)

    if (mergedCells !== null) {
        mergedCells.each((i, mergedCell) => {
            tools.demergeCell(mergedCell)
            clearCellHelper(mergedCell)
        })
    }
    else clearCellHelper(cell)
}

function clearCellHelper(cell) {
    tools.removeBoldText(cell)
    tools.removeCenterText(cell)
    tools.removeCellAsHeader(cell)
    tools.removeCellAsData(cell)
    tools.removeBlackBorder(cell)
    $(cell).text('')
}