import * as events from './spreadsheetEvents.js'
import * as tools from './spreadsheetTools.js'
import * as setup from './spreadsheetSetup.js'
import * as globals from './spreadsheetGlobalVariables.js'

initialize()

function initialize() {
    setup.setupMergeButton()
    setup.setupBoldTextButton()
    setup.setupCellAsHeaderButton()
    setup.setupBlackBorderButton()

    setup.setupInputBar()

    createTable()

    //TODO: Remove when showError works
    $('#testErrorButton').on('click', () => {
        tools.showError(globals.errorCellIndexes, globals.errorLineIndexes, globals.errorMessage)
    })

    $('#removeErrorButton').on('click', () => {
        tools.removeError(globals.editingCell)
    })
}

function createTable() {
    globals.setColumnSize(10)
    globals.setRowSize(10)

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

function createCell(column, row) {
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
    cell.on('focusout', (e) => events.onCellLosesFocus(e.target))

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

    for (let column = 0; column < 10; column++) {
        for (let row = 0; row < 10; row++) {
            let cell = getCellFromID(column, row)

            if (column >= column1 && column <= column2 && row >= row1 && row <= row2) {
                globals.selectedCells.push(cell)
            }
        }
    }
}