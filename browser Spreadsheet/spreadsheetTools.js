import * as spreadsheet from './spreadsheet.js';
import * as globals from './spreadsheetGlobalVariables.js'

export function markCells() {
    globals.setCellsMarked(true)

    globals.selectedCells.forEach((cell) => {
        $(cell).addClass('selected')
    })
}

export function clearMarkedCells() {
    globals.setCellsMarked(false)

    $('.selected').each((i, element) => {
        $(element).removeClass('selected')
    })
}

export function mergeCells() {
    let numberOfRowsSelected = new Set()

    globals.selectedCells.forEach((cell) => {
        let cellIndexes = spreadsheet.getCellIndexes(cell)
        numberOfRowsSelected.add(cellIndexes[1])
    })

    if (numberOfRowsSelected.size > 1) alert('Cannot merge rows!')
    else {
        $(globals.selectedCells[0]).attr('colspan', globals.selectedCells.length)

        globals.selectedCells.splice(1).forEach((cell) => {
            $(cell).css('display', 'none')
        })

        globals.setEditingCell(globals.selectedCells[0])
        clearMarkedCells()
    }
}

//TODO: Fokus og editing cell bliver ikke sat til leftmost
export function demergeCells(mergedCells) {
    mergedCells.forEach((mergedCell) => {
        let cellWidth = $(mergedCell).attr('colspan')
        let cellIndexes = spreadsheet.getCellIndexes(mergedCell)

        for (let i = 1; i < cellWidth; i++) {
            let cell = spreadsheet.getCellFromID(cellIndexes[0] + i, cellIndexes[1])
            $(cell).css('display', '')
        }

        $(mergedCell).removeAttr('colspan')
    })
}

export function setBoldText() {
    globals.selectedCells.forEach((cell) => {
        $(cell).addClass('bold')
    })
}

export function removeBoldText() {
    globals.selectedCells.forEach((cell) => {
        $(cell).removeClass('bold')
    })
}

export function setCellAsHeader() {
    globals.selectedCells.forEach((cell) => {
        $(cell).addClass('header')
    })
}

export function removeCellAsHeader() {
    globals.selectedCells.forEach((cell) => {
        $(cell).removeClass('header')
    })
}

export function setBlackBorder() {
    globals.selectedCells.forEach((cell) => {
        $(cell).addClass('blackBorder')
    })
}

export function removeBlackBorder() {
    globals.selectedCells.forEach((cell) => {
        $(cell).removeClass('blackBorder')
    })
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