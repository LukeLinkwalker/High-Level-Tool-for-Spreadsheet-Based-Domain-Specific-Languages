import * as spreadsheet from './spreadsheet.js';
import * as globals from './spreadsheetGlobalVariables.js'
import * as client from "./ssclient.js";

export function mergeCells(cells) {
    let leftmostCellIndexes = spreadsheet.getCellIndexes(cells[0])
    let leftmostCellID = spreadsheet.createCellID(leftmostCellIndexes[0], leftmostCellIndexes[1])
    let className = 'merged-' + leftmostCellID

    $(cells[0]).attr('colspan', cells.length)
    cells.forEach((cell) => $(cell).addClass(className))

    cells.slice(1).forEach((cell) => {
        $(cell).css('display', 'none')
        $(cell).text('')
    })

    client.sendChange(spreadsheet.getCellFromIndexes(leftmostCellIndexes[0], leftmostCellIndexes[1]));
}

export function demergeCell(cell) {
    $(cell).css('display', '')
    $(cell).removeAttr('colspan')
}

export function setBoldText(cell) {
    $(cell).addClass('bold')
}

export function removeBoldText(cell) {
    $(cell).removeClass('bold')
}

export function setCenterText(cell) {
    $(cell).addClass('center')
}

export function removeCenterText(cell) {
    $(cell).removeClass('center')
}

export function setCellAsHeader(cell) {
    $(cell).addClass('header')
}

export function removeCellAsHeader(cell) {
    $(cell).removeClass('header')
}

export function setCellAsData(cell) {
    $(cell).addClass('data')
}

export function removeCellAsData(cell) {
    $(cell).removeClass('data')
}

export function setBlackBorder(cell) {
    $(cell).addClass('blackBorder')
}

export function removeBlackBorder(cell) {
    $(cell).removeClass('blackBorder')
}

export function showError(errorCellIndexes, errorLineIndexes) {
    let cell = $(spreadsheet.getCellFromIndexes(errorCellIndexes[0], errorCellIndexes[1]))
    let errorText = cell.text().substring(errorLineIndexes[0], errorLineIndexes[1])

    cell.data('hasError', true)
    cell.html(cell.html().replace(errorText, '<span class="error">' + errorText + '</span>'))

    createErrorMessage(cell, globals.errorMessage)
}

export function removeError(cell) {
    let textWithSpanRemoved = $(cell).html().replace('<span class="error">', '').replace('</span>', '')

    $(cell).data('hasError', false)
    $(cell).html(textWithSpanRemoved)

    removeErrorMessage(cell)
}

export function showErrorMessage(cell) {
    let errorMessage = $('div.errorMessage', cell)
    errorMessage.css('visibility', 'visible')
}

export function hideErrorMessage(cell) {
    let errorMessage = $('div.errorMessage', cell)
    errorMessage.css('visibility', 'hidden')
}

export function createErrorMessage(cell, errorMessage) {
    let div = $('<div/>')
    div.addClass('errorMessage')
    div.text(errorMessage)
    div.prop('contenteditable', false)
    cell.append(div)
}

export function removeErrorMessage(cell) {
    let errorMessage = $('div.errorMessage', cell)
    errorMessage.remove()
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
    let mergedCells = spreadsheet.getMergedCells(cell)

    if (mergedCells !== null) {
        mergedCells.each((i, mergedCell) => {
            demergeCell(mergedCell)
            clearCellHelper(mergedCell)
        })
    }
    else clearCellHelper(cell)
}

function clearCellHelper(cell) {
    removeBoldText(cell)
    removeCenterText(cell)
    removeCellAsHeader(cell)
    removeCellAsData(cell)
    removeBlackBorder(cell)
    $(cell).text('')
}

//TODO: Refactor this?
export function createTable(tableName, column, row, tableRange) {
    let startCell = spreadsheet.getCellFromIndexes(tableRange[0], tableRange[1])
    let endCell = spreadsheet.getCellFromIndexes(tableRange[2], tableRange[3])
    let tableRangeCells = spreadsheet.getCellsInRange(startCell, endCell)
    let tableNameForCells = spreadsheet.createTableNameForCells(startCell)

    let allTableCellsAreEmpty = tableRangeCells.slice(1).every((cellInRange) => {
        return spreadsheet.checkCellIsEmpty(cellInRange)
    })

    if (allTableCellsAreEmpty) {
        tableRangeCells.forEach((cellInRange) => spreadsheet.addTableNameToCell(cellInRange, tableNameForCells))
        client.requestCreateTable(tableName, tableRange[0], tableRange[1])
    }
    else {
        let warning = confirm('Some cells are not empty. Their data will be overwritten. Do you still wish to create a table?')

        if (warning) {
            tableRangeCells.forEach((cellInRange) => {
                clearCell(cellInRange)
                spreadsheet.addTableNameToCell(cellInRange, tableNameForCells)
            })

            client.requestCreateTable(tableName, tableRange[0], tableRange[1])
        }
    }
}

export function addRow(cell) {
    let cellsInNewRow = spreadsheet.getCellsInNewTableRow(cell)

    if (cellsInNewRow === null) alert('Cannot add row as current cell is not in a table!')
    else {
        let allCellsInNewRowAreEmpty = cellsInNewRow.every((cellInNewRow) => {
            return spreadsheet.checkCellIsEmpty(cellInNewRow)
        })

        if (!allCellsInNewRowAreEmpty) {
            let warning = confirm('Some cells are not empty. Their data will be overwritten. Do you still wish to add a row?')

            if (warning) {
                let tableName = spreadsheet.getTableName(cell)

                cellsInNewRow.forEach((cellInNewRow) => {
                    clearCell(cellInNewRow)
                    createDataCellInNewRow(cellInNewRow, tableName)
                })
            }
        }
        else {
            let tableName = spreadsheet.getTableName(cell)

            cellsInNewRow.forEach((cellInNewRow) => createDataCellInNewRow(cellInNewRow, tableName))
        }
    }
}

function createDataCellInNewRow(cell, tableName) {
    spreadsheet.addTableNameToCell(cell, tableName)
    setCellAsData(cell)
    setBlackBorder(cell)
}

//TODO: Fix name and everything about this method!
export function suggestion(cellText, column, row) {

    client.requestGetInitialTableRange(cellText, column, row)

    // console.log(cellText + 'is a table. WANNA CREATE TABLE? Indexes: ' + column + " " + row)
}