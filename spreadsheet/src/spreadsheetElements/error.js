import * as elementCell from './cell.js'

export function createError(errorCellColumn, errorCellRow, errorLineIndexStart, errorLineIndexEnd, errorMessage) {
    let cell = elementCell.getCellFromIndexes(errorCellColumn, errorCellRow)
    let errorBox = getErrorBox(cell)

    insertNewMessageInErrorBox(errorBox, errorMessage)
    createErrorUnderline(cell, errorLineIndexStart, errorLineIndexEnd)
    $(cell).addClass('error')
}

function createErrorUnderline(cell, errorLineIndexStart, errorLineIndexEnd) {
    let cellText = elementCell.getCellText(cell)
    let textWithError
    let cellTextDiv = elementCell.getCellTextDiv(cell)

    if (errorLineIndexStart === -1 || errorLineIndexEnd === -1) textWithError = cellText
    else textWithError = cellText.substring(errorLineIndexStart, errorLineIndexEnd)

    let textWithErrorUnderline = '<span class="errorLine">' + textWithError + '</span>'
    let updatedCellText = cellText.replace(textWithError, textWithErrorUnderline)

    if ($(cellTextDiv).is(':focus')) {
        let caret = getCaretPosition(cellTextDiv)

        $(cellTextDiv).html(updatedCellText)
        setCaretPosition(cellTextDiv, caret)
    } else $(cellTextDiv).html(updatedCellText)
}

export function hideAndClearAllErrors() {
    let errorCells = $('.error')
    errorCells.each((i, element) => hideAndClearError(element))
}

export function hideAndClearError(cell) {
    let errorBox = getErrorBox(cell)

    $(cell).removeClass('error')
    setErrorBoxText(errorBox, '')
    removeErrorUnderline(cell)
    hideErrorMessage(errorBox)
}

function removeErrorUnderline(cell) {
    let cellText = elementCell.getCellText(cell)
    elementCell.setCellText(cell, cellText, false)
}

export function showErrorMessage(errorBox) {
    $(errorBox).css('display', 'block')
}

export function hideErrorMessage(errorBox) {
    $(errorBox).css('display', 'none')
}

export function createErrorBox() {
    return $('<div class="errorBox box">')
}

export function getErrorBox(cell) {
    return $('.errorBox', cell)[0]
}

function insertNewMessageInErrorBox(errorBox, value) {
    let currentText = $(errorBox).text()

    if (currentText === '') setErrorBoxText(errorBox, value)
    else setErrorBoxText(errorBox, currentText + '\n' + value)
}

export function setErrorBoxText(errorBox, text) {
    $(errorBox).text(text)
}

export function getCaretPosition(element) {
    let position = 0
    let isSupported = typeof window.getSelection !== 'undefined'

    if (isSupported) {
        let selection = window.getSelection()

        if (selection.rangeCount !== 0) {
            let range = window.getSelection().getRangeAt(0)
            let preCaretRange = range.cloneRange()

            preCaretRange.selectNodeContents(element)
            preCaretRange.setEnd(range.endContainer, range.endOffset)

            position = preCaretRange.toString().length
        }
    }
    return position
}

export function setCaretPosition(element, position) {
    for (let node of element.childNodes) {
        if (node.nodeType === 3) {
            if (node.length >= position) {
                let range = document.createRange()
                let selection = window.getSelection()

                range.setStart(node, position)
                range.collapse(true)
                selection.removeAllRanges()
                selection.addRange(range)

                return -1
            } else position -= node.length
        } else {
            position = setCaretPosition(node, position)
            if (position === -1) return -1
        }
    }

    return position
}