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

export function createSpreadsheet() {
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
    cell.on('focusout', (e) => events.onCellFocusOut(e.target))

    return cell
}

function createColumnHeader(tableSize, table) {
    let columnRow = $('<thead>')
    columnRow.append($('<th>'))

    for (let column = 0; column < tableSize; column++) {
        let tableHeader = $('<th>')
        tableHeader.text(String.fromCharCode(65 + column))
        columnRow.append(tableHeader)
    }

    $(table).prepend(columnRow)
}

function createRowHeader(tableSize, table) {
    $('tr', table).each( (index, element) => {
        let tableHeader = $('<th>')
        tableHeader.text(index + 1)
        $(element).prepend(tableHeader)
    })
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
    let startCellIndexes = getCellIndexes(startCell)
    let endCellIndexes = getCellIndexes(endCell)
    let cellsInRange = []

    for (let i = startCellIndexes[0]; i <= endCellIndexes[0] ; i++) {
        for (let j = startCellIndexes[1]; j <= endCellIndexes[1]; j++) {
            cellsInRange.push(getCellFromID(i, j))
        }
    }

    return cellsInRange
}

export function checkCellIsEmpty(cell) {
    return $(cell).text() === ''
}

export function createTableName(cell) {
    let cellIndexes = getCellIndexes(cell)
    let cellID = createCellID(cellIndexes[0], cellIndexes[1])
    return 'table-' + cellID
}

export function getTableName(cell) {
    let classNames = $(cell).attr('class')

    if (classNames !== undefined) {
        let classNamesArray = classNames.split(/\s+/)
        let tableName = undefined

        classNamesArray.forEach((className) => {
            if (className.startsWith('table-cell-')) tableName = className
        })

        if (tableName === undefined) return null
        else return tableName
    }
    else return null
}

export function addTableNameToCell(cell, tableName) {
    $(cell).addClass(tableName)
}

export function getAllCellsFromTableCellIsIn(cell) {
    let tableName = getTableName(cell)

    if (tableName === null) return null
    else return $('.' + tableName)
}

export function getTableRange(cell) {
    let tableCells = getAllCellsFromTableCellIsIn(cell)
    let startCell = null
    let endCell = null

    if (tableCells !== null) {
        tableCells.each((i, cell) => {
            if (startCell === null && endCell === null) {
                startCell = cell
                endCell = cell
            }

            let startCellIndexes = getCellIndexes(startCell)
            let endCellIndexes = getCellIndexes(endCell)
            let cellIndexes = getCellIndexes(cell)

            if (cellIndexes[0] <= startCellIndexes[0] && cellIndexes[1] <= startCellIndexes[1]) startCell = cell
            if (cellIndexes[0] >= endCellIndexes[0] && cellIndexes[1] >= endCellIndexes[1]) endCell = cell
        })

        let startCellIndexes = getCellIndexes(startCell)
        let endCellIndexes = getCellIndexes(endCell)

        return [startCellIndexes[0], startCellIndexes[1], endCellIndexes[0], endCellIndexes[1]]
    }
    else return null
}

export function getMergedCells(cell) {
    let classNames = $(cell).attr('class')

    if (classNames !== undefined) {
        let classNamesArray = classNames.split(/\s+/)
        let mergedCellClassName = null

        classNamesArray.forEach((className) => {
            if (className.startsWith('merged-cell-')) mergedCellClassName = className
        })

        if (mergedCellClassName === null) return null
        else return $('.' + mergedCellClassName)
    }
    else return null
}

export function getCellsInNewTableRow(cell) {
    let tableRange = getTableRange(cell)

    if (tableRange === null) return null
    else {
        let newRowStartCell = getCellFromID(tableRange[0], tableRange[3] + 1)
        let newRowEndCell = getCellFromID(tableRange[2], tableRange[3] + 1)

        return getCellsInRange(newRowStartCell, newRowEndCell)
    }
}