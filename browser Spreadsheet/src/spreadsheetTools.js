import * as spreadsheet from './spreadsheet.js'
import * as globals from './spreadsheetGlobalVariables.js'
import * as client from './ssclient.js'
import * as setup from './spreadsheetSetup.js'

export function mergeCells(cells) {
    let leftmostCellIndexes = spreadsheet.getCellIndexes(cells[0])
    let className = spreadsheet.createMergedCellsName(leftmostCellIndexes[0], leftmostCellIndexes[1])

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
    $(cell).removeClass(spreadsheet.getMergedCellsName(cell))
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

//TODO: Refactor after errorLineIndexes work on server.
// export function createError(errorCellIndexes, errorLineIndexes, errorMessage) {
export function createError(errorCellColumn, errorCellRow, errorMessage) {
    let cell = spreadsheet.getCellFromIndexes(errorCellColumn, errorCellRow)
    let errorBox = spreadsheet.getErrorBox(cell)

    spreadsheet.insertNewMessageInErrorBox(errorBox, errorMessage)
    //TODO: Refactor after errorLineIndexes work on server.
    // createErrorUnderline(cell, errorLineIndexes)
    createErrorUnderline(cell)
    $(cell).addClass('error')
}

//TODO: Refactor after errorLineIndexes work on server.
// export function createErrorUnderline(cell, errorLineIndexes) {
export function createErrorUnderline(cell) {
    let cellText = spreadsheet.getCellText(cell)
    //TODO: Refactor after errorLineIndexes work on server.
    let textWithError = cellText
    // let textWithError = cellText.substring(errorLineIndexes[0], errorLineIndexes[1])
    let textWithErrorUnderline = '<span class="errorLine">' + textWithError + '</span>'
    let cellTextDiv = spreadsheet.getCellTextDiv(cell)

    // $(cell).html($(cell).html().replace(textWithError, textWithRedLine))
    // $(cellTextDiv).html($(cell).html().replace(textWithError, textWithRedLine))

    if ($(cellTextDiv).is(":focus")) {
        let caret = spreadsheet.getCaretPosition(cellTextDiv)

        $(cellTextDiv).html(textWithErrorUnderline)
        spreadsheet.setCaretPosition(cellTextDiv, caret)
    } else $(cellTextDiv).html(textWithErrorUnderline)
}

export function hideAndClearAllErrors() {
    let errorCells = $('.error')

    errorCells.each((i, element) => {
        $(element).removeClass('error')
        $('.errorBox')
            .css('display', 'none')
            .text('')
        removeErrorUnderline(element)
    })
}

export function removeErrorUnderline(cell) {
    let cellText = spreadsheet.getCellText(cell)
    spreadsheet.setCellText(cell, cellText)
}

export function showErrorMessage(cell) {
    let errorBox = spreadsheet.getErrorBox(cell)
    $(errorBox).css('display', 'block')
}

export function hideErrorMessage(cell) {
    let errorBox = spreadsheet.getErrorBox(cell)
    $(errorBox).css('display', 'none')
}

export function markCells() {
    globals.setCellsMarked(true)
    globals.selectedCells.forEach((cell) => {
        if ($(cell).hasClass('header')) $(cell).addClass('selectedHeader')
        else if  ($(cell).hasClass('data')) $(cell).addClass('selectedData')
        else $(cell).addClass('selected')
    })
}

export function clearMarkedCells() {
    globals.setCellsMarked(false)
    $('.selected').each((i, element) => $(element).removeClass('selected'))
    $('.selectedData').each((i, element) => $(element).removeClass('selectedData'))
    $('.selectedHeader').each((i, element) => $(element).removeClass('selectedHeader'))
}

export function clearCell(cell) {
    demergeCell(cell)
    removeBoldText(cell)
    removeItalicText(cell)
    removeCenterText(cell)
    spreadsheet.removeCellAsHeader(cell)
    spreadsheet.removeCellAsData(cell)
    removeBlackBorder(cell)
    spreadsheet.removeCellFromTable(cell)
    spreadsheet.removeBreakoutReferenceToOriginalTable(cell)
    spreadsheet.setCellText(cell, '')
}

export function createTable(tableName, tableRange, spreadsheetType) {
    let startCell = spreadsheet.getCellFromIndexes(tableRange[0], tableRange[1])
    let endCell = spreadsheet.getCellFromIndexes(tableRange[2], tableRange[3])
    let tableRangeCells = spreadsheet.getCellsInRange(startCell, endCell)
    let tableNameForCells = spreadsheet.createTableName(tableRange[0], tableRange[1])

    let allTableCellsAreEmpty = tableRangeCells.slice(1).every((cellInRange) => {
        return spreadsheet.checkCellIsEmpty(cellInRange)
    })

    if (allTableCellsAreEmpty) {
        tableRangeCells.forEach((cellInRange) => spreadsheet.addTableNameToCell(cellInRange, tableNameForCells))
        client.requestCreateTable(tableName, tableRange[0], tableRange[1], spreadsheetType)
    }
    else alert('Cannot create table as some of the cells are not empty! ')
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
    let tableCells = spreadsheet.getAllCellsFromTableCellIsIn(cell)
    let tableRange = spreadsheet.getTableRange(tableCells)

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
                return spreadsheet.getCellText(cellInRow) === ''
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

export function createTableCodeCompletionForInfoBox(tableName, column, row, spreadsheetType) {
    let cell = spreadsheet.getCellFromIndexes(column, row)
    let infoBox = spreadsheet.getInfoBox(cell)
    let infoBoxText = 'Create ' + tableName + ' table? Press enter to create it.'

    $(cell).off('keydown')
    setup.setupCellKeyDown($(cell))
    spreadsheet.insertNewMessageInInfoBox(infoBox, infoBoxText)
    $(infoBox).css('display', 'block')
    $(cell).addClass('infoBoxShown')

    $(cell).on('keydown',(e) => {
        if (e.which === 13) {
            client.requestGetInitialTableRange(tableName, column, row, spreadsheetType)
        }
    })
}

export function hideCreateTableCodeCompletionForInfoBox(infoBox, cell) {
    $(cell).off('keydown')
    setup.setupCellKeyDown($(cell))
    setup.setupCellKeyDownEnter($(cell))
    $(infoBox).css('display', 'none')
    $(infoBox).text('')
    $(cell).removeClass('infoBoxShown')
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
    let newCell = (mergedCells === null) ? possibleNewEditingCell : mergedCells[0]

    spreadsheet.setFocusOnCell(newCell)
}

export function changeToSGL() {
    $('.sdslClass').css('display', 'none')
    $('.sglClass').css('display', '')
    globals.setSpreadsheetType('sgl')
    spreadsheet.setInitialEditingCell()
}

export function changeToSDSL() {
    $('.sdslClass').css('display', '')
    $('.sglClass').css('display', 'none')
    globals.setSpreadsheetType('sdsl')
    spreadsheet.setInitialEditingCell()
}

export function changeNextCellToStartOfNewRowInTable(cell, tableRange, event) {
    let cellIndexes = spreadsheet.getCellIndexes(cell)
    let newCell = spreadsheet.getCellFromIndexes(tableRange[0], cellIndexes[1] + 1)

    event.preventDefault()
    $(globals.editingCell).css('outline', '')
    spreadsheet.setFocusOnCell(newCell)
}

export function changeCellOneDownAndPossiblyAddRow(event) {
    let cell = globals.editingCell
    let cellIndexes = spreadsheet.getCellIndexes(cell)
    let tableCells = spreadsheet.getAllCellsFromTableCellIsIn(cell)
    let tableRange = spreadsheet.getTableRange(tableCells)

    if (tableRange !== null && cellIndexes [1] === tableRange[3]) addRow(cell)

    moveOneCellDown(event)
}

export function deleteTable(cell) {
    let cellsInTable = spreadsheet.getAllCellsFromTableCellIsIn(cell)
    let breakoutReferenceToOriginalTable = spreadsheet.getBreakoutReferenceToOriginalTable(cell)

    if (cellsInTable === null) alert('Cannot delete table as the current cell is not in a table!')
        else if (breakoutReferenceToOriginalTable !== null) alert('Cannot delete breakout table. Delete the table it ' +
        'was broken out from instead!')
    else {
        let warning = confirm('Are you sure you want to delete this table and all its contents?')

        if (warning) {
            let breakoutTableCells = spreadsheet.findBrokenOutTableCells(cell)
            cellsInTable.forEach((cellInTable) => clearCell(cellInTable))
            breakoutTableCells.forEach((breakoutTableCell) => clearCell(breakoutTableCell))
        }
    }
}

export function showBreakoutTableOutline(cell) {
    let breakoutOutlineCells = spreadsheet.getBreakoutOutlineCells(cell)
    let breakoutTableRange = spreadsheet.getTableRange(breakoutOutlineCells)
    let leftColumn = breakoutTableRange[0]
    let topRow = breakoutTableRange[1]
    let rightColumn = breakoutTableRange[2]
    let bottomRow = breakoutTableRange[3]

    breakoutOutlineCells.forEach((boCell) => {
        let cellIndexes = spreadsheet.getCellIndexes(boCell)
        let width = $(boCell).prop('colspan')

        if (leftColumn === cellIndexes[0]) $(boCell).css('border-left','2px double royalblue')
        if (topRow === cellIndexes[1]) $(boCell).css('border-top','2px double royalblue')
        if (rightColumn === cellIndexes[0] + width - 1) $(boCell).css('border-right','2px double royalblue')
        if (bottomRow === cellIndexes[1]) $(boCell).css('border-bottom','2px double royalblue')
    })
}

export function removeBreakoutTableOutline(cell) {
    let breakoutOutlineCells = spreadsheet.getBreakoutOutlineCells(cell)

    breakoutOutlineCells.forEach((cell) => {
        $(cell).css('border-left', '')
        $(cell).css('border-top', '')
        $(cell).css('border-right', '')
        $(cell).css('border-bottom', '')
    })
}

export function copyCell(oldCell, newCell, newHeader) {
    let newHeaderIndexes = spreadsheet.getCellIndexes(newHeader)
    let oldDiv = spreadsheet.getCellTextDiv(oldCell)
    let newDiv = spreadsheet.getCellTextDiv(newCell)
    let oldTableName = spreadsheet.getTableName(oldCell)
    let newTableName = spreadsheet.createTableName(newHeaderIndexes[0], newHeaderIndexes[1])
    let mergedCellsName = spreadsheet.getMergedCellsName(oldCell)

    $(newCell).attr('class', $(oldCell).attr('class'))
    $(newCell).prop('colspan', $(oldCell).prop('colspan'))
    $(newCell).css('display', $(oldCell).css('display'))
    $(newDiv).attr('class', $(oldDiv).attr('class'))
    spreadsheet.setCellText(newCell, spreadsheet.getCellText(oldCell))
    $(newCell).removeClass(oldTableName)
    $(newCell).addClass(newTableName)

    if (mergedCellsName !== null) {
        let newMergedCellsName = spreadsheet.createNewMergedCellsNameForCopyingCell(oldCell, newCell)

        $(newCell).removeClass(mergedCellsName)
        $(newCell).addClass(newMergedCellsName)
    }
}

export function copyCellsAndClearOldCells(breakoutOutlineCells, breakoutHeader, isBreakingOut) {
    let nonEmptyCells = []
    let oldTableHeader = spreadsheet.getAllCellsFromTableCellIsIn(breakoutHeader)[0]

    breakoutOutlineCells.forEach((boCell) => {
        if (!spreadsheet.checkCellIsEmpty(boCell)) nonEmptyCells.push(boCell)
    })

    let allNonEmptyCellsAreOriginalBreakoutCells = nonEmptyCells.every((nonEmptyCell) => {
        return globals.breakoutTableCells.includes(nonEmptyCell)
    })

    if (nonEmptyCells.size === 0 || allNonEmptyCellsAreOriginalBreakoutCells) {
        if (globals.breakoutTableCells.length !== nonEmptyCells.length) {
            let copyOfBreakOutTableCells = []

            globals.breakoutTableCells.forEach((boCell) => {
                copyOfBreakOutTableCells.push($(boCell).clone()[0])
                clearCell(boCell)
            })

            copyOfBreakOutTableCells.forEach((oldCell, index) => {
                let newTableHeader = spreadsheet.getNewTableHeaderForCopyingCell(oldCell, breakoutOutlineCells[index],
                    breakoutHeader)
                copyCell(oldCell, breakoutOutlineCells[index], newTableHeader)
            })

            if (isBreakingOut) {
                let referenceToOriginalTable = spreadsheet.createBreakoutReferenceToOriginalTable(oldTableHeader)
                breakoutOutlineCells.forEach((cell) => $(cell).addClass(referenceToOriginalTable))
                insertNameColumnWhenBreakingOut(copyOfBreakOutTableCells, oldTableHeader)
                cleanupTableAfterInsertingNameColumnWhenBreakingOut(copyOfBreakOutTableCells[0], oldTableHeader)
            }
        }
    }
    else {
        alert('Cannot place table here as some of the cells are not empty! ')
    }
}

export function insertNameColumnWhenBreakingOut(copyOfBreakoutTableCells, tableHeader) {
    let breakoutHeader = copyOfBreakoutTableCells[0]
    let breakoutHeaderIndexes = spreadsheet.getCellIndexes(breakoutHeader)
    let rowWhereNameAttributeIs = breakoutHeaderIndexes[1] + 1
    let nameAttributeInFirstRow = copyOfBreakoutTableCells.filter((boCell) => {
        let boCellIndexes = spreadsheet.getCellIndexes(boCell)
        let boCellText = spreadsheet.getCellText(boCell)

        return boCellIndexes[1] === rowWhereNameAttributeIs && boCellText.toLowerCase() === 'name'
    })

    let nameAttributeCellIndexes = spreadsheet.getCellIndexes(nameAttributeInFirstRow)
    let breakoutHeaderCellFromOriginalTable = spreadsheet.getCellFromIndexes(breakoutHeaderIndexes[0],
        breakoutHeaderIndexes[1])

    copyOfBreakoutTableCells.forEach((boCell) => {
        let boCellIndexes = spreadsheet.getCellIndexes(boCell)

        if (boCellIndexes[0] === nameAttributeCellIndexes[0] && boCellIndexes[1] !== breakoutHeaderIndexes[1]) {
            let newCell = spreadsheet.getCellFromIndexes(breakoutHeaderIndexes[0], boCellIndexes[1])
            copyCell(boCell, newCell, tableHeader)
        }
    })

    copyCell(breakoutHeader, breakoutHeaderCellFromOriginalTable, tableHeader)
    demergeCell(breakoutHeaderCellFromOriginalTable)
}

export function cleanupTableAfterInsertingNameColumnWhenBreakingOut(breakoutHeader, tableHeader) {
    let width = $(breakoutHeader).prop('colspan')
    let shrinkSize = width - 1
    let cellsToTheRightOfBreakoutHeader = getCellsToTheRightOfTheBreakoutHeader(breakoutHeader)
    let mergedCellsStraightUpFromBreakoutHeader = getMergedCellsStraightUpFromBreakoutHeader(breakoutHeader)

    moveCellsToTheRightOfBreakoutHeader(cellsToTheRightOfBreakoutHeader, shrinkSize, tableHeader)
    mergeAndDemergeCellsAfterBreakout(mergedCellsStraightUpFromBreakoutHeader, shrinkSize)

    let tableCells = spreadsheet.getAllCellsFromTableCellIsIn(tableHeader)

    deleteHeaderRowIfEmpty(tableCells)
    moveTableUpAfterRowIsDeleted(tableCells, tableHeader)
}

function deleteHeaderRowIfEmpty(tableCells) {
    let headerRows = spreadsheet.getTableCellsAsRows(tableCells)

    headerRows.forEach((row) => {
        let headerRowIsEmpty = row.every((cell) => {
            let cellType = spreadsheet.getCellType(cell)
            let cellText = spreadsheet.getCellText(cell)

            return cellType === 'header' && cellText === ''
        })
        if (headerRowIsEmpty) row.forEach((cell) => clearCell(cell))
    })
}

function moveTableUpAfterRowIsDeleted(tableCells, tableHeader) {
    let headerRows = spreadsheet.getTableCellsAsRows(tableCells)
    let emptyRow
    let restOfRowCells = []
    let emptyRowIsFound = false

    for (let i = 0; i < headerRows.length; i++) {
        if (!emptyRowIsFound) {
            let rowIsEmpty = headerRows[i].every((cell) => spreadsheet.checkCellIsEmpty(cell))

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
        clearCell(cell)
    })

    copyOfCells.forEach((oldCell) => {
        let oldCellIndexes = spreadsheet.getCellIndexes(oldCell)
        let newCell = spreadsheet.getCellFromIndexes(oldCellIndexes[0], oldCellIndexes[1] - 1)
        copyCell(oldCell, newCell, tableHeader)
    })
}

function moveCellsToTheRightOfBreakoutHeader(cellsToTheRightOfBreakoutHeader, shrinkSize, tableHeader) {
    cellsToTheRightOfBreakoutHeader.forEach((cell) => {
        let cellIndexes = spreadsheet.getCellIndexes(cell)
        let newCell = spreadsheet.getCellFromIndexes(cellIndexes[0] - shrinkSize, cellIndexes[1])

        copyCell(cell, newCell, tableHeader)
        clearCell(cell)
    })
}

function mergeAndDemergeCellsAfterBreakout(mergedCellsStraightUpFromBreakoutHeader, shrinkSize) {
    mergedCellsStraightUpFromBreakoutHeader.forEach((mergedCell) => {
        let cellsInMergedCell = spreadsheet.getMergedCells(mergedCell)
        let mergedCellWidth = $(mergedCell).prop('colspan')
        let mergedCellShrinkSize = mergedCellWidth - shrinkSize
        let cellsToBeMerged = []

        cellsInMergedCell.each((index, cell) => {
            demergeCell(cell)
            if (index + 1 > mergedCellShrinkSize) clearCell(cell)
            else cellsToBeMerged.push(cell)
        })

        mergeCells(cellsToBeMerged)
    })
}

export function getCellsToTheRightOfTheBreakoutHeader(breakoutHeader) {
    let breakoutHeaderIndexes = spreadsheet.getCellIndexes(breakoutHeader)
    let allCellsInTable = spreadsheet.getAllCellsFromTableCellIsIn(breakoutHeader)

    return allCellsInTable.filter((cell) => {
        let cellIndexes = spreadsheet.getCellIndexes(cell)
        let mergedCells = spreadsheet.getMergedCells(cell)

        if (mergedCells !== null) {
            let mergedCellIndexes = spreadsheet.getCellIndexes(mergedCells[0])
            return mergedCellIndexes[0] > breakoutHeaderIndexes[0]
        }
        else return cellIndexes[0] > breakoutHeaderIndexes[0]
    })
}

export function getMergedCellsStraightUpFromBreakoutHeader(breakoutHeader) {
    let breakoutHeaderIndexes = spreadsheet.getCellIndexes(breakoutHeader)
    let allCellsInTable = spreadsheet.getAllCellsFromTableCellIsIn(breakoutHeader)
    let mergedCells = []

    allCellsInTable.forEach((cell) => {
        let cellIndexes = spreadsheet.getCellIndexes(cell)

        if (cellIndexes[0] === breakoutHeaderIndexes[0] && cellIndexes[1] < breakoutHeaderIndexes[1]) {
            let mergedCellsInTable = spreadsheet.getMergedCells(cell)
            if (mergedCellsInTable !== null) mergedCells.push(mergedCellsInTable[0])
        }
    })

    return mergedCells
}

export function moveOrBreakoutCells(cell) {
    let breakoutHeader = globals.breakoutTableCells[0]
    let width = $(breakoutHeader).prop('colspan')

    if (width < 2) {
        removeBreakoutTableOutline(cell)
        alert('Cannot break out table as it only has 1 column!')
    }
    else {
        let breakoutOutlineCells = spreadsheet.getBreakoutOutlineCells(cell)
        //TODO: To Mikkel
        let breakoutHeader = globals.breakoutTableCells[0]
        let tableHeader = spreadsheet.findTableHeader(globals.breakoutTableCells[0])
        let tableHeaderIndexes = spreadsheet.getCellIndexes(tableHeader)
        let isBreakingOut = !spreadsheet.checkHeaderCellIsHeaderForWholeTable(breakoutHeader)

        removeBreakoutTableOutline(cell)
        copyCellsAndClearOldCells(breakoutOutlineCells, globals.breakoutTableCells[0], isBreakingOut)
        spreadsheet.setFocusOnCell(breakoutOutlineCells[0])
    }
}