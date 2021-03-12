import * as spreadsheet from './spreadsheet.js';
import * as globals from './spreadsheetGlobalVariables.js'

export function mergeCells(cells) {
    let leftmostCellIndexes = spreadsheet.getCellIndexes(cells[0])
    let leftmostCellID = spreadsheet.createCellID(leftmostCellIndexes[0], leftmostCellIndexes[1])

    $(cells[0]).attr('colspan', cells.length)

    cells.forEach((cell) => {
        let className = 'merged-' + leftmostCellID
        $(cell).addClass(className)
    })

    cells.splice(1).forEach((cell) => {
        $(cell).css('display', 'none')
        $(cell).text('')
    })
}

export function getMergedCells(cell) {
    let classNames = $(cell).attr('class')

    if (classNames !== undefined) {
        let classNamesArray = classNames.split(/\s+/)
        let mergedCellClassName = undefined

        classNamesArray.forEach((className) => {
            if (className.startsWith('merged-cell-')) mergedCellClassName = className
        })

        if (mergedCellClassName === undefined) return null
        else return $('.' + mergedCellClassName)
    }
    else return null
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
    let cell = $(spreadsheet.getCellFromID(errorCellIndexes[0], errorCellIndexes[1]))
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