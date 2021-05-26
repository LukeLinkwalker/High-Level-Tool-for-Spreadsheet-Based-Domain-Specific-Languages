import * as elementCell from '../spreadsheetElements/cell.js'

export function setBoldText(cell) {
    let cellTextDiv = elementCell.getCellTextDiv(cell)
    $(cellTextDiv).addClass('bold')
}

export function removeBoldText(cell) {
    let cellTextDiv = elementCell.getCellTextDiv(cell)
    $(cellTextDiv).removeClass('bold')
}

export function setItalicText(cell) {
    let cellTextDiv = elementCell.getCellTextDiv(cell)
    $(cellTextDiv).addClass('italic')
}

export function removeItalicText(cell) {
    let cellTextDiv = elementCell.getCellTextDiv(cell)
    $(cellTextDiv).removeClass('italic')
}

export function setCenterText(cell) {
    let cellTextDiv = elementCell.getCellTextDiv(cell)
    $(cellTextDiv).addClass('center')
}

export function removeCenterText(cell) {
    let cellTextDiv = elementCell.getCellTextDiv(cell)
    $(cellTextDiv).removeClass('center')
}

export function setBlackBorder(cell) {
    $(cell).addClass('blackBorder')
}

export function removeBlackBorder(cell) {
    $(cell).removeClass('blackBorder')
}