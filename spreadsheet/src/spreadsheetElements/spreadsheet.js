import * as globalVariables from '../globalVariables.js'
import * as elementCell from './cell.js'

export function createSpreadsheet() {
    globalVariables.setColumnSize(30)
    globalVariables.setRowSize(40)

    let table = $('<table>').attr('id', 'dynamicTable' + globalVariables.spreadsheetType)
    let tableBody = $('<tbody>').attr('id', 'dynamicBody' + globalVariables.spreadsheetType)

    for (let row = 0; row < globalVariables.rowSize; row++) {
        let newRow = $('<tr>')

        for (let column = 0; column < globalVariables.columnSize; column++) {
            newRow.append(elementCell.createCell(column, row, globalVariables.spreadsheetType))
        }
        tableBody.append(newRow)
    }

    createRowHeader(globalVariables.rowSize, tableBody)
    createColumnHeader(globalVariables.columnSize, table)
    table.append(tableBody)
    $('#cell-container-' + globalVariables.spreadsheetType).append(table)
}

export function createColumnHeader(tableSize, table) {
    let columnRow = $('<thead>')

    for (let column = 0; column <= tableSize; column++) {
        let tableHeader = $('<th>')

        if (column !== 0) tableHeader.text(createSpreadsheetHeaderText(column))
        tableHeader.on('mousedown', (e) => e.preventDefault())
        columnRow.append(tableHeader)
    }

    $(table).prepend(columnRow)
}

function createSpreadsheetHeaderText(columnNumber) {
    let temp
    let letter = ''

    while (columnNumber > 0) {
        temp = (columnNumber - 1) % 26
        letter = String.fromCharCode(temp + 65) + letter
        columnNumber = (columnNumber - temp - 1) / 26
    }

    return letter
}

export function createRowHeader(tableSize, table) {
    $('tr', table).each( (index, element) => {
        let tableHeader = $('<th>')

        tableHeader.text(index + 1)
        tableHeader.on('mousedown', (e) => e.preventDefault())
        $(element).prepend(tableHeader)
    })
}