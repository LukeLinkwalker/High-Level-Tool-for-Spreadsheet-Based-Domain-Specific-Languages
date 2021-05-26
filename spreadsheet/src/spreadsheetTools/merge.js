import * as elementCell from '../spreadsheetElements/cell.js'
import * as client from '../client.js'

export function mergeCells(cells) {
    let leftmostCellIndexes = elementCell.getCellIndexes(cells[0])
    let className = createMergedCellsName(leftmostCellIndexes[0], leftmostCellIndexes[1])

    cells.slice(1).forEach((cell) => {
        $(cell).css('display', 'none')
        elementCell.setCellText(cell, '', true)
    })

    $(cells[0]).attr('colspan', cells.length)
    cells.forEach((cell) => $(cell).addClass(className))
    client.sendChange(cells[0])
}

export function demergeCell(cell) {
    $(cell).css('display', '')
    $(cell).removeAttr('colspan')
    $(cell).removeClass(getMergedCellsName(cell))
    client.sendChange(cell)
}

export function getMergedCells(cell) {
    let mergedCellClassName = getMergedCellsName(cell)

    if (mergedCellClassName === null) return null
    else return $('.' + getMergedCellsName(cell))
}

export function getMergedCellsName(cell) {
    let classNames = $(cell).attr('class')

    if (classNames !== undefined) {
        let classNamesArray = classNames.split(/\s+/)
        let mergedCellClassName = null

        classNamesArray.forEach((className) => {
            if (className.startsWith('merged-cell-')) mergedCellClassName = className
        })

        if (mergedCellClassName === null) return null
        else return mergedCellClassName
    }

    else return null
}

export function createNewMergedCellsNameForCopyingCell(oldCell, newCell) {
    let cellIndexesFromMergedCellsName = getCellIndexesFromMergedCellsName(getMergedCellsName(oldCell))
    let newMergedCellsIndexes = getIndexDifferencesForHeaderAndMergeCellNamesForWhenCopying(oldCell, newCell, cellIndexesFromMergedCellsName)

    return createMergedCellsName(newMergedCellsIndexes[0], newMergedCellsIndexes[1])
}

export function getIndexDifferencesForHeaderAndMergeCellNamesForWhenCopying(oldCell, newCell, cellIndexes) {
    let oldCellIndexes = elementCell.getCellIndexes(oldCell)
    let newCellIndexes = elementCell.getCellIndexes(newCell)
    let columnDifference = newCellIndexes[0] - oldCellIndexes[0]
    let rowDifference = newCellIndexes[1] - oldCellIndexes[1]
    let newColumn = cellIndexes[0] + columnDifference
    let newRow = cellIndexes[1] + rowDifference

    return [newColumn, newRow]
}

function getCellIndexesFromMergedCellsName(mergedCellsName) {
    let matches = mergedCellsName.match(/^merged-cell-(\d+)-(\d+)/)
    return [Number(matches[1]), Number(matches[2])]
}

function createMergedCellsName(column, row) {
    let cellID = elementCell.createCellID(column, row)
    return 'merged-' + cellID
}