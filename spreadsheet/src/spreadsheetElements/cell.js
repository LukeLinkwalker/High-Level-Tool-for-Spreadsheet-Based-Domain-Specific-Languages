import * as elementError from './error.js'
import * as elementInformationBox from './informationBox.js'
import * as elementTable from './table.js'
import * as toolsBreakout from '../spreadsheetTools/breakout.js'
import * as toolsFormatting from '../spreadsheetTools/formatting.js'
import * as toolsMerge from '../spreadsheetTools/merge.js'
import * as client from '../client.js'
import * as globalVariables from '../globalVariables.js'
import * as setup from '../setup.js'

export function createCell(column, row) {
    let cell = $('<td>')
    let divToContainBoxes = $('<div class="boxContainer">')

    divToContainBoxes.append(elementInformationBox.createInfoBox())
    divToContainBoxes.append(elementError.createErrorBox())
    cell.append(createCellText())
    cell.append(divToContainBoxes)
    cell.attr('id', createCellID(column, row))
    setup.setupCell(cell)

    return cell
}

function createCellText() {
    let cellText = $('<div class="cellText">')
    cellText.attr('contenteditable', 'true')
    setup.setupCellTextDiv(cellText)

    return cellText
}

export function createCellID(column, row) {
    return 'cell-' + column + '-' + row + '-' + globalVariables.spreadsheetType
}

export function getCellFromIndexes(column, row) {
    return $('#' + createCellID(column, row))[0]
}

export function getCellIndexes(cell) {
    let cellID = $(cell).attr('id')
    let matches = cellID.match(/^cell-(\d+)-(\d+)/)

    if (matches) return [Number(matches[1]), Number(matches[2])]
}

export function setInitialEditingCell() {
    let cell = getCellFromIndexes(0, 0)
    setFocusOnCell(cell)
}

export function getCellsInRange(startCell, endCell) {
    let startCellIndexes = getCellIndexes(startCell)
    let endCellIndexes = getCellIndexes(endCell)
    let cellsInRange = []

    for (let i = startCellIndexes[0]; i <= endCellIndexes[0] ; i++) {
        for (let j = startCellIndexes[1]; j <= endCellIndexes[1]; j++) {
            cellsInRange.push(getCellFromIndexes(i, j))
        }
    }

    return cellsInRange
}

export function checkCellIsEmpty(cell) {
    let cellType = getCellType(cell)
    return getCellText(cell) === '' && cellType === 'normal'
}

export function getSpecificClassFromCell(cell, specificClassName) {
    let classNames = $(cell).attr('class')

    if (classNames !== undefined) {
        let classNamesArray = classNames.split(/\s+/)
        let tableName = undefined

        classNamesArray.forEach((className) => {
            if (className.startsWith(specificClassName)) tableName = className
        })

        if (tableName === undefined) return null
        else return tableName
    }
    else return null
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

export function getCellType(cell) {
    let $cell = $(cell)

    if ($cell.hasClass('header')) return 'header'
    else if ($cell.hasClass('data')) return 'data'
    else return 'normal'
}

export function getCellTextDiv(cell) {
    return $('.cellText', cell)[0]
}

export function getCellText(cell) {
    return $(getCellTextDiv(cell)).text()
}

export function setCellText(cell, value, sendChange) {
    let cellTextDiv = getCellTextDiv(cell)

    if ($(cellTextDiv).is(':focus')) {
        let caret = elementError.getCaretPosition(cellTextDiv)

        $(getCellTextDiv(cell)).text(value)
        elementError.setCaretPosition(cellTextDiv, caret)
    }
    else $(getCellTextDiv(cell)).text(value)

    if (cell === globalVariables.editingCell) {
        let inputBar = $('#input-bar')
        inputBar.val(value)
    }

    if (sendChange) client.sendChange(cell)
}

export function getCellFromCellTextDiv(cellTextDiv) {
    return $(cellTextDiv).parent()[0]
}

export function setFocusOnCell(cell) {
    let cellTextDiv = getCellTextDiv(cell)
    let cellIndexes = getCellIndexes(cell)

    globalVariables.setEditingCell(cell)
    globalVariables.setCurrentColumn(cellIndexes[0])
    globalVariables.setCurrentRow(cellIndexes[1])
    cellTextDiv.focus()
}

export function clearCell(cell) {
    toolsMerge.demergeCell(cell)
    toolsFormatting.removeBoldText(cell)
    toolsFormatting.removeItalicText(cell)
    toolsFormatting.removeCenterText(cell)
    removeCellAsHeader(cell)
    removeCellAsData(cell)
    toolsFormatting.removeBlackBorder(cell)
    elementTable.removeCellFromTable(cell)
    toolsBreakout.removeBreakoutReferenceToOriginalTable(cell)
    toolsBreakout.removeMarkAsBrokenOut(cell)
    setCellText(cell, '', true)
    elementError.hideAndClearError(cell)
}

export function createDataCellInNewRow(cell, tableName) {
    elementTable.addTableNameToCell(cell, tableName)
    setCellAsData(cell)
    toolsFormatting.setBlackBorder(cell)
    toolsFormatting.setCenterText(cell)
}

export function copyCell(oldCell, newCell, newHeader) {
    let newHeaderIndexes = getCellIndexes(newHeader)
    let oldDiv = getCellTextDiv(oldCell)
    let newDiv = getCellTextDiv(newCell)
    let oldTableName = elementTable.getTableName(oldCell)
    let newTableName = elementTable.createTableName(newHeaderIndexes[0], newHeaderIndexes[1])
    let mergedCellsName = toolsMerge.getMergedCellsName(oldCell)

    $(newCell).attr('class', $(oldCell).attr('class'))
    $(newCell).prop('colspan', $(oldCell).prop('colspan'))
    $(newCell).css('display', $(oldCell).css('display'))
    $(newDiv).attr('class', $(oldDiv).attr('class'))
    setCellText(newCell, getCellText(oldCell), true)
    //TODO: For breakout highlighting - update referencename
    $(newCell).removeClass(oldTableName)
    $(newCell).addClass(newTableName)

    if (mergedCellsName !== null) {
        let newMergedCellsName = toolsMerge.createNewMergedCellsNameForCopyingCell(oldCell, newCell)

        $(newCell).removeClass(mergedCellsName)
        $(newCell).addClass(newMergedCellsName)
    }
}