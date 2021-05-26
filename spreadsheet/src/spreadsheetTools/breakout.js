import * as elementCell from '../spreadsheetElements/cell.js'
import * as elementTable from '../spreadsheetElements/table.js'
import * as toolsMerge from './merge.js'
import * as globalVariables from '../globalVariables.js'

//TODO: Breakout highlighting
export function createBreakoutDataCellReference(column, row) {
    let cellID = elementCell.createCellID(column, row)
    return 'breakout-data-cell-reference-' + cellID
}

//TODO: Breakout highlighting
export function getBreakoutDataCellReference(cell) {
    return elementCell.getSpecificClassFromCell(cell, 'breakout-data-cell-reference-')
}

function createBreakoutReferenceToOriginalTable(cell) {
    let tableHeader = elementTable.findTableHeader(cell)
    let cellIndexes = elementCell.getCellIndexes(tableHeader)
    return 'breakout-from-' + elementTable.createTableName(cellIndexes[0], cellIndexes[1])
}

export function getBreakoutReferenceToOriginalTable(cell) {
    return elementCell.getSpecificClassFromCell(cell, 'breakout-from-')
}

export function removeBreakoutReferenceToOriginalTable(cell) {
    $(cell).removeClass(getBreakoutReferenceToOriginalTable(cell))
}

export function getBreakoutTableCells(cell) {
    let width = $(cell).prop('colspan')
    let cellIndexes = elementCell.getCellIndexes(cell)
    let breakoutTableCells = []
    let tableName = elementTable.getTableName(cell)

    for (let i = cellIndexes[0]; i < width + cellIndexes[0]; i++) {
        let nextCell = elementCell.getCellFromIndexes(i, cellIndexes[1])
        let nextCellTableName = elementTable.getTableName(nextCell)

        while (nextCellTableName === tableName) {
            breakoutTableCells.push(nextCell)

            let nextCellIndexes = elementCell.getCellIndexes(nextCell)
            nextCell = elementCell.getCellFromIndexes(i, nextCellIndexes[1] + 1)
            nextCellTableName = elementTable.getTableName(nextCell)
        }
    }

    return breakoutTableCells
}

function getBreakoutOutlineCells(cell) {
    let breakoutTableRange = elementTable.getTableRange(globalVariables.breakoutTableCells)
    let headerCellIndexes = elementCell.getCellIndexes(globalVariables.breakoutTableCells[0])
    let currentCellIndexes = elementCell.getCellIndexes(cell)
    let headerAndCurrentColumnDifference = currentCellIndexes[0] - headerCellIndexes[0]
    let headerAndCurrentRowDifference = currentCellIndexes[1] - headerCellIndexes[1]
    let startCell = elementCell.getCellFromIndexes(breakoutTableRange[0] + headerAndCurrentColumnDifference,
        breakoutTableRange[1] + headerAndCurrentRowDifference)
    let endCell = elementCell.getCellFromIndexes(breakoutTableRange[2] + headerAndCurrentColumnDifference,
        breakoutTableRange[3] + headerAndCurrentRowDifference)

    return elementCell.getCellsInRange(startCell, endCell)
}

export function findBrokenOutTableCells(cell) {
    let breakoutTableName = createBreakoutReferenceToOriginalTable(cell)
    return $('.' + breakoutTableName).get()
}

function markAsBrokenOut(cell) {
    $(cell).addClass('brokenOutNameCell')
}

export function removeMarkAsBrokenOut(cell) {
    $(cell).removeClass('brokenOutNameCell')
}

//TODO: Breakout highlighting
export function getIsBrokenOut(cell) {
    return $(cell).hasClass('brokenOutNameCell')
}

//TODO: Breakout highlighting
export function getBreakoutReferenceCell(cell) {
    let cellIndexes = elementCell.getCellIndexes(cell)
    let nameAttributeHeaderOriginalTable = elementTable.findNameAttributeHeaderInColumnForCell(cell)
    let firstDataCellOriginalTable = elementTable.findFirstDataCellForHeaderCell(nameAttributeHeaderOriginalTable)
    let firstDataCellOriginalTableIndexes = elementCell.getCellIndexes(firstDataCellOriginalTable)
    let rowDifferenceFromNameAttributeHeader = cellIndexes[1] - firstDataCellOriginalTableIndexes[1]

    let breakoutReference = createBreakoutReferenceToOriginalTable(cell)
    let breakoutCells = $('.' + breakoutReference).get()
    let rows = elementTable.getTableCellsAsRows(breakoutCells)
    let firstRow = rows[1]
    let nameAttributeHeaderBreakoutTable = firstRow.filter((cell) => elementCell.getCellText(cell).toLowerCase() ===
        'name')
    let firstDataCellBreakoutTable = elementTable.findFirstDataCellForHeaderCell(nameAttributeHeaderBreakoutTable)
    let firstDataCellBreakoutTableIndexes = elementCell.getCellIndexes(firstDataCellBreakoutTable)

    return elementCell.getCellFromIndexes(firstDataCellBreakoutTableIndexes[0],
        firstDataCellBreakoutTableIndexes[1] + rowDifferenceFromNameAttributeHeader)
}

export function showBreakoutTableOutline(cell) {
    let breakoutOutlineCells = getBreakoutOutlineCells(cell)
    let breakoutTableRange = elementTable.getTableRange(breakoutOutlineCells)
    let leftColumn = breakoutTableRange[0]
    let topRow = breakoutTableRange[1]
    let rightColumn = breakoutTableRange[2]
    let bottomRow = breakoutTableRange[3]

    breakoutOutlineCells.forEach((boCell) => {
        let cellIndexes = elementCell.getCellIndexes(boCell)
        let width = $(boCell).prop('colspan')

        if (leftColumn === cellIndexes[0]) $(boCell).css('border-left','2px double royalblue')
        if (topRow === cellIndexes[1]) $(boCell).css('border-top','2px double royalblue')
        if (rightColumn === cellIndexes[0] + width - 1) $(boCell).css('border-right','2px double royalblue')
        if (bottomRow === cellIndexes[1]) $(boCell).css('border-bottom','2px double royalblue')
    })
}

export function removeBreakoutTableOutline(cell) {
    let breakoutOutlineCells = getBreakoutOutlineCells(cell)

    breakoutOutlineCells.forEach((cell) => {
        $(cell).css('border-left', '')
        $(cell).css('border-top', '')
        $(cell).css('border-right', '')
        $(cell).css('border-bottom', '')
    })
}

function insertNameColumnWhenBreakingOut(copyOfBreakoutTableCells, tableHeader) {
    let breakoutHeader = copyOfBreakoutTableCells[0]
    let breakoutHeaderIndexes = elementCell.getCellIndexes(breakoutHeader)
    let rowWhereNameAttributeIs = breakoutHeaderIndexes[1] + 1
    let nameAttributeInFirstRow = copyOfBreakoutTableCells.filter((boCell) => {
        let boCellIndexes = elementCell.getCellIndexes(boCell)
        let boCellText = elementCell.getCellText(boCell)

        return boCellIndexes[1] === rowWhereNameAttributeIs && boCellText.toLowerCase() === 'name'
    })

    let nameAttributeCellIndexes = elementCell.getCellIndexes(nameAttributeInFirstRow)
    let breakoutHeaderCellFromOriginalTable = elementCell.getCellFromIndexes(breakoutHeaderIndexes[0],
        breakoutHeaderIndexes[1])

    copyOfBreakoutTableCells.forEach((boCell) => {
        let boCellIndexes = elementCell.getCellIndexes(boCell)

        if (boCellIndexes[0] === nameAttributeCellIndexes[0] && boCellIndexes[1] !== breakoutHeaderIndexes[1]) {
            let newCell = elementCell.getCellFromIndexes(breakoutHeaderIndexes[0], boCellIndexes[1])
            elementCell.copyCell(boCell, newCell, tableHeader)
            if (elementCell.getCellType(newCell) === 'data') markAsBrokenOut(newCell)
        }
    })

    elementCell.copyCell(breakoutHeader, breakoutHeaderCellFromOriginalTable, tableHeader)
    toolsMerge.demergeCell(breakoutHeaderCellFromOriginalTable)
}

function cleanupTableAfterInsertingNameColumnWhenBreakingOut(breakoutHeader, tableHeader) {
    let width = $(breakoutHeader).prop('colspan')
    let shrinkSize = width - 1
    let cellsToTheRightOfBreakoutHeader = getCellsToTheRightOfTheBreakoutHeader(breakoutHeader)
    let mergedCellsStraightUpFromBreakoutHeader = getMergedCellsStraightUpFromBreakoutHeader(breakoutHeader)

    moveCellsToTheRightOfBreakoutHeader(cellsToTheRightOfBreakoutHeader, shrinkSize, tableHeader)
    mergeAndDemergeCellsAfterBreakout(mergedCellsStraightUpFromBreakoutHeader, shrinkSize)

    let tableCells = elementTable.getAllCellsFromTableCellIsIn(tableHeader)

    elementTable.deleteHeaderRowIfEmpty(tableCells)
    elementTable.moveTableUpAfterRowIsDeleted(tableCells, tableHeader)
}

function moveCellsToTheRightOfBreakoutHeader(cellsToTheRightOfBreakoutHeader, shrinkSize, tableHeader) {
    cellsToTheRightOfBreakoutHeader.forEach((cell) => {
        let cellIndexes = elementCell.getCellIndexes(cell)
        let newCell = elementCell.getCellFromIndexes(cellIndexes[0] - shrinkSize, cellIndexes[1])

        elementCell.copyCell(cell, newCell, tableHeader)
        elementCell.clearCell(cell)
    })
}

function mergeAndDemergeCellsAfterBreakout(mergedCellsStraightUpFromBreakoutHeader, shrinkSize) {
    mergedCellsStraightUpFromBreakoutHeader.forEach((mergedCell) => {
        let cellsInMergedCell = toolsMerge.getMergedCells(mergedCell)
        let mergedCellWidth = $(mergedCell).prop('colspan')
        let mergedCellShrinkSize = mergedCellWidth - shrinkSize
        let cellsToBeMerged = []

        cellsInMergedCell.each((index, cell) => {
            toolsMerge.demergeCell(cell)
            if (index + 1 > mergedCellShrinkSize) elementCell.clearCell(cell)
            else cellsToBeMerged.push(cell)
        })

        toolsMerge.mergeCells(cellsToBeMerged)
    })
}

function getCellsToTheRightOfTheBreakoutHeader(breakoutHeader) {
    let breakoutHeaderIndexes = elementCell.getCellIndexes(breakoutHeader)
    let allCellsInTable = elementTable.getAllCellsFromTableCellIsIn(breakoutHeader)

    return allCellsInTable.filter((cell) => {
        let cellIndexes = elementCell.getCellIndexes(cell)
        let mergedCells = toolsMerge.getMergedCells(cell)

        if (mergedCells !== null) {
            let mergedCellIndexes = elementCell.getCellIndexes(mergedCells[0])
            return mergedCellIndexes[0] > breakoutHeaderIndexes[0]
        }
        else return cellIndexes[0] > breakoutHeaderIndexes[0]
    })
}

function getMergedCellsStraightUpFromBreakoutHeader(breakoutHeader) {
    let breakoutHeaderIndexes = elementCell.getCellIndexes(breakoutHeader)
    let allCellsInTable = elementTable.getAllCellsFromTableCellIsIn(breakoutHeader)
    let mergedCells = []

    allCellsInTable.forEach((cell) => {
        let cellIndexes = elementCell.getCellIndexes(cell)

        if (cellIndexes[0] === breakoutHeaderIndexes[0] && cellIndexes[1] < breakoutHeaderIndexes[1]) {
            let mergedCellsInTable = toolsMerge.getMergedCells(cell)
            if (mergedCellsInTable !== null) mergedCells.push(mergedCellsInTable[0])
        }
    })

    return mergedCells
}

export function moveOrBreakoutCells(cell) {
    let breakoutHeader = globalVariables.breakoutTableCells[0]
    let width = $(breakoutHeader).prop('colspan')

    if (width < 2) {
        removeBreakoutTableOutline(cell)
        if (cell !== breakoutHeader) alert('Cannot break out table as it only has 1 column!')
    }
    else {
        let breakoutOutlineCells = getBreakoutOutlineCells(cell)
        let breakoutHeader = globalVariables.breakoutTableCells[0]
        let isBreakingOut = !elementTable.checkHeaderCellIsHeaderForWholeTable(breakoutHeader)

        removeBreakoutTableOutline(cell)
        copyCellsAndClearOldCells(breakoutOutlineCells, globalVariables.breakoutTableCells[0], isBreakingOut)
        elementCell.setFocusOnCell(breakoutOutlineCells[0])
    }
}

export function copyCellsAndClearOldCells(breakoutOutlineCells, breakoutHeader, isBreakingOut) {
    let nonEmptyCells = []
    let oldTableHeader = elementTable.getAllCellsFromTableCellIsIn(breakoutHeader)[0]

    breakoutOutlineCells.forEach((boCell) => {
        if (!elementCell.checkCellIsEmpty(boCell)) nonEmptyCells.push(boCell)
    })

    let allNonEmptyCellsAreOriginalBreakoutCells = nonEmptyCells.every((nonEmptyCell) => {
        return globalVariables.breakoutTableCells.includes(nonEmptyCell)
    })

    if (nonEmptyCells.size === 0 || allNonEmptyCellsAreOriginalBreakoutCells) {
        if (globalVariables.breakoutTableCells.length !== nonEmptyCells.length) {
            let copyOfBreakOutTableCells = []

            globalVariables.breakoutTableCells.forEach((boCell) => {
                copyOfBreakOutTableCells.push($(boCell).clone()[0])
                elementCell.clearCell(boCell)
            })

            copyOfBreakOutTableCells.forEach((oldCell, index) => {
                if(elementCell.getCellText(oldCell).length === 0) {
                    let newTableHeader = elementTable.getNewTableHeaderForCopyingCell(oldCell, breakoutOutlineCells[index],
                        breakoutHeader)
                    elementCell.copyCell(oldCell, breakoutOutlineCells[index], newTableHeader)
                }
            })

            copyOfBreakOutTableCells.forEach((oldCell, index) => {
                if(elementCell.getCellText(oldCell).length > 0) {
                    let newTableHeader = elementTable.getNewTableHeaderForCopyingCell(oldCell, breakoutOutlineCells[index],
                        breakoutHeader)
                    elementCell.copyCell(oldCell, breakoutOutlineCells[index], newTableHeader)
                }
            })

            if (isBreakingOut) {
                let referenceToOriginalTable = createBreakoutReferenceToOriginalTable(oldTableHeader)

                breakoutOutlineCells.forEach((cell) => {
                    $(cell).addClass(referenceToOriginalTable)
                    //TODO: For breakout highlighting. Create reference name here to original cells. They maybe have a
                    // new location.
                })
                insertNameColumnWhenBreakingOut(copyOfBreakOutTableCells, oldTableHeader)
                cleanupTableAfterInsertingNameColumnWhenBreakingOut(copyOfBreakOutTableCells[0], oldTableHeader)

                let oldTableHeaderText = elementCell.getCellText(oldTableHeader)
                let newBreakoutHeader = breakoutOutlineCells[0]
                let newBreakoutHeaderText = elementCell.getCellText(newBreakoutHeader)
                let newBreakoutHeaderNewText = oldTableHeaderText + ' -> ' + newBreakoutHeaderText

                elementCell.setCellText(newBreakoutHeader, newBreakoutHeaderNewText, true)
            }
        }
    }
    else {
        alert('Cannot place table here as some of the cells are not empty! ')
    }
}