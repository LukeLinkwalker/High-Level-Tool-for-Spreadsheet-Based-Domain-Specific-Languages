import * as spreadsheet from './spreadsheet.js'
import * as globals from './spreadsheetGlobalVariables.js'
import * as client from './ssclient.js'
import * as setup from './spreadsheetSetup.js'

export function mergeCells(cells) {
    let leftmostCellIndexes = spreadsheet.getCellIndexes(cells[0])
    let leftmostCellID = spreadsheet.createCellID(leftmostCellIndexes[0], leftmostCellIndexes[1])
    let className = 'merged-' + leftmostCellID

    $(cells[0]).attr('colspan', cells.length)
    cells.forEach((cell) => $(cell).addClass(className))

    cells.slice(1).forEach((cell) => {
        $(cell).css('display', 'none')
        spreadsheet.setCellText(cell, '')
    })

    client.sendChange(spreadsheet.getCellFromIndexes(leftmostCellIndexes[0], leftmostCellIndexes[1]));
}

export function demergeCell(cell) {
    $(cell).css('display', '')
    $(cell).removeAttr('colspan')
}

export function setBoldText(cell) {
    let cellTextDiv = spreadsheet.getCellTextDiv(cell)
    $(cellTextDiv).addClass('bold')
}

export function removeBoldText(cell) {
    let cellTextDiv = spreadsheet.getCellTextDiv(cell)
    $(cellTextDiv).removeClass('bold')
}

export function setItalicText(cell) {
    let cellTextDiv = spreadsheet.getCellTextDiv(cell)
    $(cellTextDiv).addClass('italic')
}

export function removeItalicText(cell) {
    let cellTextDiv = spreadsheet.getCellTextDiv(cell)
    $(cellTextDiv).removeClass('italic')
}

export function setCenterText(cell) {
    let cellTextDiv = spreadsheet.getCellTextDiv(cell)
    $(cellTextDiv).addClass('center')
}

export function removeCenterText(cell) {
    let cellTextDiv = spreadsheet.getCellTextDiv(cell)
    $(cellTextDiv).removeClass('center')
}

export function setBlackBorder(cell) {
    $(cell).addClass('blackBorder')
}

export function removeBlackBorder(cell) {
    $(cell).removeClass('blackBorder')
}

//TODO: Update this after the use of NodeValue
export function showError(errorCellIndexes, errorLineIndexes) {
    let cell = $(spreadsheet.getCellFromIndexes(errorCellIndexes[0], errorCellIndexes[1]))
    // let errorText = cell.text().substring(errorLineIndexes[0], errorLineIndexes[1])
    let errorText = spreadsheet.getCellText(cell).substring(errorLineIndexes[0], errorLineIndexes[1])

    cell.data('hasError', true)
    //TODO Update here???
    cell.html(cell.html().replace(errorText, '<span class="error">' + errorText + '</span>'))

    createErrorMessage(cell, globals.errorMessage)
}

//TODO: Update as well
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

//TODO: Update with Infobox
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
    removeItalicText(cell)
    removeCenterText(cell)
    spreadsheet.removeCellAsHeader(cell)
    spreadsheet.removeCellAsData(cell)
    removeBlackBorder(cell)
    spreadsheet.removeCellFromTable(cell)
    spreadsheet.setCellText(cell, '')
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
    let cellIndexes = spreadsheet.getCellIndexes(cell)

    if (cellIndexes[1] >= globals.rowSize - 1) {
        alert('Cannot add row as the spreadsheet is not big enough!')
        return false
    }
    else {
        let cellsInNewRow = spreadsheet.getCellsInNewTableRow(cell)

        if (cellsInNewRow === null) {
            alert('Cannot add row as current cell is not in a table!')
            return false
        }
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
                else return false
            }
            else {
                let tableName = spreadsheet.getTableName(cell)

                cellsInNewRow.forEach((cellInNewRow) => createDataCellInNewRow(cellInNewRow, tableName))
            }
        }
    }

    return true
}

export function deleteRow(cell) {
    let tableRange = spreadsheet.getTableRange(cell)

    if (tableRange === null) {
        alert('Cannot delete row as cell is not in a table!')
        return false
    }
    else {
        let startCell = spreadsheet.getCellFromIndexes(tableRange[0], tableRange[3])
        let endCell = spreadsheet.getCellFromIndexes(tableRange[2], tableRange[3])
        let lastRow = spreadsheet.getCellsInRange(startCell, endCell)

        let allCellsAreDataCells = lastRow.every((cellInRow) => {
            return (spreadsheet.getCellType(cellInRow) === 'data')
        })

        if (!allCellsAreDataCells) {
            alert('Cannot delete row as some cells are headers!')
            return false
        }
        else {
            let allCellsInNewRowAreEmpty = lastRow.every((cellInRow) => {
                return spreadsheet.checkCellIsEmpty(cellInRow)
            })

            if (!allCellsInNewRowAreEmpty) {
                let warning = confirm('Some cells are not empty and their data will be deleted. Do you still wish to ' +
                    'delete the last row?')

                if (warning) lastRow.forEach((cellInRow) => clearCell(cellInRow))
                else return false
            }
            else lastRow.forEach((cellInRow) => clearCell(cellInRow))
        }
    }

    return true
}

function createDataCellInNewRow(cell, tableName) {
    spreadsheet.addTableNameToCell(cell, tableName)
    spreadsheet.setCellAsData(cell)
    setBlackBorder(cell)
    setCenterText(cell)
}

export function createTableCodeCompletionForInfoBox(tableName, column, row) {
    let cell = spreadsheet.getCellFromIndexes(column, row)
    let infoBox = spreadsheet.getInfoBox(cell)
    let infoBoxText = 'A table with the name ' + tableName + ' exists. Press enter to create it.'

    $(cell).off('keydown')
    setup.setupCellKeyDown($(cell))
    infoBox.text(infoBoxText)
    infoBox.css('display', 'block')
    $(cell).data('infoBoxShown', true)

    $(cell).on('keydown',(e) => {
        if (e.which === 13) {
            client.requestGetInitialTableRange(tableName, column, row)
        }
    })
}
export function hideCreateTableCodeCompletionForInfoBox(infoBox, cell) {
    $(cell).off('keydown')
    setup.setupCellKeyDown($(cell))
    setup.setupCellKeyDownEnter($(cell))
    infoBox.css('display', 'none')
    $(cell).data('infoBoxShown', false)
}

export function moveOneCellLeft(event) {
    let editingCellIndexes = spreadsheet.getCellIndexes(globals.editingCell)
    let mergedCells = spreadsheet.getMergedCells(globals.editingCell)

    event.preventDefault()
    $(globals.editingCell).css('outline', '')

    if (editingCellIndexes[0] > 0) {
        if (mergedCells !== null) {
            let mergedCellIndexes = spreadsheet.getCellIndexes(mergedCells[0])
            let columnNumberToSkipMergedCell = globals.currentColumn - mergedCellIndexes[0]

            setNextCell(globals.currentColumn - columnNumberToSkipMergedCell - 1, globals.currentRow)
        }
        else setNextCell(globals.currentColumn - 1, globals.currentRow)
    }
}

export function moveOneCellUp(event) {
    let editingCellIndexes = spreadsheet.getCellIndexes(globals.editingCell)

    event.preventDefault()
    $(globals.editingCell).css('outline', '')

    if (editingCellIndexes[1] > 0) setNextCell(globals.currentColumn, globals.currentRow - 1)
}

export function moveOneCellRight(event) {
    let editingCellIndexes = spreadsheet.getCellIndexes(globals.editingCell)
    let mergedCells = spreadsheet.getMergedCells(globals.editingCell)

    event.preventDefault()
    $(globals.editingCell).css('outline', '')

    if (editingCellIndexes[0] < globals.columnSize - 1) {
        if (mergedCells !== null) {
            let width = $(globals.editingCell).prop('colspan')
            let mergedCellIndexes = spreadsheet.getCellIndexes(mergedCells[0])
            let columnNumberToSkipMergedCell = width - (globals.currentColumn - mergedCellIndexes[0])

            setNextCell(globals.currentColumn + columnNumberToSkipMergedCell, globals.currentRow)
        }
        else setNextCell(globals.currentColumn + 1, globals.currentRow)
    }
}

export function moveOneCellDown(event) {
    let editingCellIndexes = spreadsheet.getCellIndexes(globals.editingCell)

    event.preventDefault()
    $(globals.editingCell).css('outline', '')

    if (editingCellIndexes[1] < globals.rowSize - 1) setNextCell(globals.currentColumn, globals.currentRow + 1)
}

export function setNextCell(column, row) {
    let possibleNewEditingCell = spreadsheet.getCellFromIndexes(column, row)
    let mergedCells = spreadsheet.getMergedCells(possibleNewEditingCell)
    let newEditingCell = (mergedCells === null) ? possibleNewEditingCell : mergedCells[0]
    let newEditingCellTextDiv = spreadsheet.getCellTextDiv(newEditingCell)

    globals.setCurrentColumn(column)
    globals.setCurrentRow(row)
    globals.setEditingCell(newEditingCell)
    newEditingCellTextDiv.focus()
}

export function changeToSGL() {
    $('.sdslClass').css('display', 'none')
    $('.sglClass').css('display', '')
    globals.setSpreadsheetType('sgl')
}

export function changeToSDSL() {
    $('.sdslClass').css('display', '')
    $('.sglClass').css('display', 'none')
    globals.setSpreadsheetType('sdsl')
}

export function changeNextCellToStartOfNewRowInTable(cell, tableRange, event) {
    let cellIndexes = spreadsheet.getCellIndexes(cell)
    let newEditingCell = spreadsheet.getCellFromIndexes(tableRange[0], cellIndexes[1] + 1)
    let newEditingCellTextDiv = spreadsheet.getCellTextDiv(newEditingCell)

    event.preventDefault()
    $(globals.editingCell).css('outline', '')
    globals.setCurrentColumn(tableRange[0])
    globals.setCurrentRow(cellIndexes[1] + 1)
    globals.setEditingCell(newEditingCell)
    newEditingCellTextDiv.focus()
}

export function changeCellOneDownAndPossiblyAddRow(event) {
    let cell = globals.editingCell
    let cellIndexes = spreadsheet.getCellIndexes(cell)
    let tableRange = spreadsheet.getTableRange(cell)

    if (tableRange !== null && cellIndexes [1] === tableRange[3]) addRow(cell)

    moveOneCellDown(event)
}

export function deleteTable(cell) {
    let cellsInTable = spreadsheet.getAllCellsFromTableCellIsIn(cell)

    if (cellsInTable === null) alert('Cannot delete table as the current cell is not in a table!')
    else {
        let warning = confirm('Are you sure you want to delete this table and all its contents?')

        if (warning) cellsInTable.each((i, cellInTable) => clearCell(cellInTable))
    }
}