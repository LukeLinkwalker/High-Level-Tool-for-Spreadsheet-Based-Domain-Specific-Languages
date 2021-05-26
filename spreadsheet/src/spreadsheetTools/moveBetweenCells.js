import * as elementCell from '../spreadsheetElements/cell.js'
import * as elementTable from '../spreadsheetElements/table.js'
import * as toolsMerge from './merge.js'
import * as globalVariables from '../globalVariables.js'

export function moveOneCellLeft(event) {
    let editingCellIndexes = elementCell.getCellIndexes(globalVariables.editingCell)
    let mergedCells = toolsMerge.getMergedCells(globalVariables.editingCell)

    event.preventDefault()
    $(globalVariables.editingCell).css('outline', '')

    if (editingCellIndexes[0] > 0) {
        if (mergedCells !== null) {
            let mergedCellIndexes = elementCell.getCellIndexes(mergedCells[0])
            let columnNumberToSkipMergedCell = globalVariables.currentColumn - mergedCellIndexes[0]

            setNextCell(globalVariables.currentColumn - columnNumberToSkipMergedCell - 1, globalVariables.currentRow)
        }
        else setNextCell(globalVariables.currentColumn - 1, globalVariables.currentRow)
    }
}

export function moveOneCellUp(event) {
    let editingCellIndexes = elementCell.getCellIndexes(globalVariables.editingCell)

    event.preventDefault()
    $(globalVariables.editingCell).css('outline', '')

    if (editingCellIndexes[1] > 0) setNextCell(globalVariables.currentColumn, globalVariables.currentRow - 1)
}

export function moveOneCellRight(event) {
    let editingCellIndexes = elementCell.getCellIndexes(globalVariables.editingCell)
    let mergedCells = toolsMerge.getMergedCells(globalVariables.editingCell)

    event.preventDefault()
    $(globalVariables.editingCell).css('outline', '')

    if (editingCellIndexes[0] < globalVariables.columnSize - 1) {
        if (mergedCells !== null) {
            let width = $(globalVariables.editingCell).prop('colspan')
            let mergedCellIndexes = elementCell.getCellIndexes(mergedCells[0])
            let columnNumberToSkipMergedCell = width - (globalVariables.currentColumn - mergedCellIndexes[0])

            setNextCell(globalVariables.currentColumn + columnNumberToSkipMergedCell, globalVariables.currentRow)
        }
        else setNextCell(globalVariables.currentColumn + 1, globalVariables.currentRow)
    }
}

export function moveOneCellDown(event) {
    let editingCellIndexes = elementCell.getCellIndexes(globalVariables.editingCell)

    event.preventDefault()
    $(globalVariables.editingCell).css('outline', '')

    if (editingCellIndexes[1] < globalVariables.rowSize - 1) setNextCell(globalVariables.currentColumn, globalVariables.currentRow + 1)
}

function setNextCell(column, row) {
    let possibleNewEditingCell = elementCell.getCellFromIndexes(column, row)
    let mergedCells = toolsMerge.getMergedCells(possibleNewEditingCell)
    let newCell = (mergedCells === null) ? possibleNewEditingCell : mergedCells[0]

    elementCell.setFocusOnCell(newCell)
}

export function changeNextCellToStartOfNewRowInTable(cell, tableRange, event) {
    let cellIndexes = elementCell.getCellIndexes(cell)
    let newCell = elementCell.getCellFromIndexes(tableRange[0], cellIndexes[1] + 1)

    event.preventDefault()
    $(globalVariables.editingCell).css('outline', '')
    elementCell.setFocusOnCell(newCell)
}

export function changeCellOneDownAndPossiblyAddRow(event) {
    let cell = globalVariables.editingCell
    let cellIndexes = elementCell.getCellIndexes(cell)
    let tableCells = elementTable.getAllCellsFromTableCellIsIn(cell)
    let tableRange = elementTable.getTableRange(tableCells)

    if (tableRange !== null && cellIndexes [1] === tableRange[3]) elementTable.addRow(cell)

    moveOneCellDown(event)
}