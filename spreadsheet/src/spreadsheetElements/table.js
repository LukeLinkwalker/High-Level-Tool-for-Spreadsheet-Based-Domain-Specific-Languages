import * as elementCell from './cell.js'
import * as toolsBreakout from '../spreadsheetTools/breakout.js'
import * as toolsMerge from '../spreadsheetTools/merge.js'
import * as client from '../client.js'
import * as globalVariables from '../globalVariables.js'

export function createTableName(column, row) {
    let cellID = elementCell.createCellID(column, row)
    return 'table-' + cellID
}

export function getTableName(cell) {
    return elementCell.getSpecificClassFromCell(cell, 'table-cell-')
}

export function getTableRange(tableCells) {
    let startCell = null
    let endCell = null

    if (tableCells !== null) {
        tableCells.forEach((cell) => {
            if (startCell === null && endCell === null) {
                startCell = cell
                endCell = cell
            }

            let startCellIndexes = elementCell.getCellIndexes(startCell)
            let endCellIndexes = elementCell.getCellIndexes(endCell)
            let cellIndexes = elementCell.getCellIndexes(cell)

            if (cellIndexes[0] <= startCellIndexes[0] && cellIndexes[1] <= startCellIndexes[1]) startCell = cell
            if (cellIndexes[0] >= endCellIndexes[0] && cellIndexes[1] >= endCellIndexes[1]) endCell = cell
        })

        let startCellIndexes = elementCell.getCellIndexes(startCell)
        let endCellIndexes = elementCell.getCellIndexes(endCell)

        return [startCellIndexes[0], startCellIndexes[1], endCellIndexes[0], endCellIndexes[1]]
    }
    else return null
}

function getCellsInNewTableRow(cell) {
    let tableCells = getAllCellsFromTableCellIsIn(cell)
    let tableRange = getTableRange(tableCells)

    if (tableRange === null) return null
    else {
        let newRowStartCell = elementCell.getCellFromIndexes(tableRange[0], tableRange[3] + 1)
        let newRowEndCell = elementCell.getCellFromIndexes(tableRange[2], tableRange[3] + 1)

        return elementCell.getCellsInRange(newRowStartCell, newRowEndCell)
    }
}

export function addTableNameToCell(cell, tableName) {
    $(cell).addClass(tableName)
}

export function removeCellFromTable(cell) {
    $(cell).removeClass(getTableName(cell))
}

export function getAllCellsFromTableCellIsIn(cell) {
    let tableName = getTableName(cell)

    if (tableName === null) return null
    else return $('.' + tableName).get()
}

export function checkTableHasNameAttribute(breakoutTableCells) {
    let headerRow = elementCell.getCellIndexes(breakoutTableCells[0])[1]
    let tableHasNameAttribute = false

    breakoutTableCells.forEach((boCell) => {
        let boCellIndexes = elementCell.getCellIndexes(boCell)
        let cellText = elementCell.getCellText(boCell).toLowerCase()

        if (boCellIndexes[1] === headerRow + 1 && cellText === 'name') tableHasNameAttribute = true
    })

    return tableHasNameAttribute
}

export function checkHeaderCellIsHeaderForWholeTable(cell) {
    let headerCellTableName = getTableName(cell)
    let headerCellIndexes = elementCell.getCellIndexes(cell)
    let cellAboveHeader = elementCell.getCellFromIndexes(headerCellIndexes[0], headerCellIndexes[1] - 1)
    let cellAboveHeaderTableName = getTableName(cellAboveHeader)

    return headerCellTableName !== cellAboveHeaderTableName;
}

export function getNewTableHeaderForCopyingCell(oldCell, newCell, headerCell) {
    let headerCellIndexes = elementCell.getCellIndexes(headerCell)
    let newTableIndexes = toolsMerge.getIndexDifferencesForHeaderAndMergeCellNamesForWhenCopying(oldCell, newCell, headerCellIndexes)

    return elementCell.getCellFromIndexes(newTableIndexes[0], newTableIndexes[1])
}

//TODO: Maybe use for breakout highlighting
export function getCellIndexesFromTableName(tableName) {
    let matches = tableName.match(/^table-cell-(\d+)-(\d+)/)
    return [Number(matches[1]), Number(matches[2])]
}

export function getTableCellsAsRows(tableCells) {
    let tableRange = getTableRange(tableCells)
    let headerRows = []

    for (let i = tableRange[1]; i <= tableRange[3]; i++) {
        let headerRow = []

        tableCells.forEach((cell) => {
            let cellIndexes = elementCell.getCellIndexes(cell)
            if (cellIndexes[1] === i) headerRow.push(cell)
        })

        headerRows.push(headerRow)
    }

    return headerRows
}

export function findTableHeader(cell) {
    return getAllCellsFromTableCellIsIn(cell)[0]
}

export function findNameAttributeHeaderInColumnForCell(cell) {
    let nextCell = cell
    let nextCellType = elementCell.getCellType(nextCell)
    let nextCellText = elementCell.getCellText(nextCell)

    while (!(nextCellType === 'header' && nextCellText.toLowerCase() === 'name')) {
        let nextCellIndexes = elementCell.getCellIndexes(nextCell)
        nextCell = elementCell.getCellFromIndexes(nextCellIndexes[0], nextCellIndexes[1] - 1)
        nextCellType = elementCell.getCellType(nextCell)
        nextCellText = elementCell.getCellText(nextCell)
    }

    return nextCell
}

export function findFirstDataCellForHeaderCell(cell) {
    let nextCell = cell
    let nextCellType = elementCell.getCellType(cell)

    while (nextCellType !== 'data') {
        let nextCellIndexes = elementCell.getCellIndexes(nextCell)
        nextCell = elementCell.getCellFromIndexes(nextCellIndexes[0], nextCellIndexes[1] + 1)
        nextCellType = elementCell.getCellType(nextCell)
    }

    return nextCell
}

export function createTable(tableName, tableRange, spreadsheetType) {
    let startCell = elementCell.getCellFromIndexes(tableRange[0], tableRange[1])
    let endCell = elementCell.getCellFromIndexes(tableRange[2], tableRange[3])
    let tableRangeCells = elementCell.getCellsInRange(startCell, endCell)
    let tableNameForCells = createTableName(tableRange[0], tableRange[1])

    let allTableCellsAreEmpty = tableRangeCells.slice(1).every((cellInRange) => {
        return elementCell.checkCellIsEmpty(cellInRange)
    })

    if (allTableCellsAreEmpty) {
        tableRangeCells.forEach((cellInRange) => addTableNameToCell(cellInRange, tableNameForCells))
        client.requestCreateTable(tableName, tableRange[0], tableRange[1], spreadsheetType)
    }
    else alert('Cannot create table as some of the cells are not empty! ')
}

export function addRow(cell) {
    let cellIndexes = elementCell.getCellIndexes(cell)

    if (cellIndexes[1] >= globalVariables.rowSize - 1) {
        alert('Cannot add row as the spreadsheet is not big enough!')
        return false
    }
    else {
        let cellsInNewRow = getCellsInNewTableRow(cell)

        if (cellsInNewRow === null) {
            alert('Cannot add row as current cell is not in a table!')
            return false
        }
        else {
            let allCellsInNewRowAreEmpty = cellsInNewRow.every((cellInNewRow) => {
                return elementCell.checkCellIsEmpty(cellInNewRow)
            })

            if (!allCellsInNewRowAreEmpty) {
                let warning = confirm('Some cells are not empty. Their data will be overwritten. Do you still wish to add a row?')

                if (warning) {
                    let tableName = getTableName(cell)

                    cellsInNewRow.forEach((cellInNewRow) => {
                        elementCell.clearCell(cellInNewRow)
                        elementCell.createDataCellInNewRow(cellInNewRow, tableName)
                    })
                }
                else return false
            }
            else {
                let tableName = getTableName(cell)

                cellsInNewRow.forEach((cellInNewRow) => elementCell.createDataCellInNewRow(cellInNewRow, tableName))
            }
        }
    }

    return true
}

export function deleteRow(cell) {
    let tableCells = getAllCellsFromTableCellIsIn(cell)
    let tableRange = getTableRange(tableCells)

    if (tableRange === null) {
        alert('Cannot delete row as cell is not in a table!')
        return false
    }
    else {
        let startCell = elementCell.getCellFromIndexes(tableRange[0], tableRange[3])
        let endCell = elementCell.getCellFromIndexes(tableRange[2], tableRange[3])
        let lastRow = elementCell.getCellsInRange(startCell, endCell)

        let allCellsAreDataCells = lastRow.every((cellInRow) => {
            return (elementCell.getCellType(cellInRow) === 'data')
        })

        if (!allCellsAreDataCells) {
            alert('Cannot delete row as some cells are headers!')
            return false
        }
        else {
            let allCellsInNewRowAreEmpty = lastRow.every((cellInRow) => {
                return elementCell.getCellText(cellInRow) === ''
            })

            if (!allCellsInNewRowAreEmpty) {
                let warning = confirm('Some cells are not empty and their data will be deleted. Do you still wish to ' +
                    'delete the last row?')

                if (warning) lastRow.forEach((cellInRow) => elementCell.clearCell(cellInRow))
                else return false
            }
            else lastRow.forEach((cellInRow) => elementCell.clearCell(cellInRow))
        }
    }

    return true
}

export function deleteTable(cell) {
    let cellsInTable = getAllCellsFromTableCellIsIn(cell)
    let breakoutReferenceToOriginalTable = toolsBreakout.getBreakoutReferenceToOriginalTable(cell)

    if (cellsInTable === null) alert('Cannot delete table as the current cell is not in a table!')
    else if (breakoutReferenceToOriginalTable !== null) alert('Cannot delete breakout table. Delete the table it ' +
        'was broken out from instead!')
    else {
        let warning = confirm('Are you sure you want to delete this table and all its contents?')

        if (warning) {
            let breakoutTableCells = toolsBreakout.findBrokenOutTableCells(cell)
            cellsInTable.forEach((cellInTable) => elementCell.clearCell(cellInTable))
            breakoutTableCells.forEach((breakoutTableCell) => elementCell.clearCell(breakoutTableCell))
        }
    }
}

export function deleteHeaderRowIfEmpty(tableCells) {
    let headerRows = getTableCellsAsRows(tableCells)

    headerRows.forEach((row) => {
        let headerRowIsEmpty = row.every((cell) => {
            let cellType = elementCell.getCellType(cell)
            let cellText = elementCell.getCellText(cell)

            return cellType === 'header' && cellText === ''
        })
        if (headerRowIsEmpty) row.forEach((cell) => elementCell.clearCell(cell))
    })
}

export function moveTableUpAfterRowIsDeleted(tableCells, tableHeader) {
    let headerRows = getTableCellsAsRows(tableCells)
    let emptyRow
    let restOfRowCells = []
    let emptyRowIsFound = false

    for (let i = 0; i < headerRows.length; i++) {
        if (!emptyRowIsFound) {
            let rowIsEmpty = headerRows[i].every((cell) => elementCell.checkCellIsEmpty(cell))

            if (rowIsEmpty) {
                emptyRow = headerRows[i]
                emptyRowIsFound = true
            }
        }
        else headerRows[i].forEach((cell) => restOfRowCells.push(cell))
    }

    let copyOfCells = []

    restOfRowCells.forEach((cell) => {
        copyOfCells.push($(cell).clone()[0])
        elementCell.clearCell(cell)
    })

    copyOfCells.forEach((oldCell) => {
        let oldCellIndexes = elementCell.getCellIndexes(oldCell)
        let newCell = elementCell.getCellFromIndexes(oldCellIndexes[0], oldCellIndexes[1] - 1)
        elementCell.copyCell(oldCell, newCell, tableHeader)
    })
}