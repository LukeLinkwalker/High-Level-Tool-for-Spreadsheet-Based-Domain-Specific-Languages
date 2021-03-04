import * as events from './spreadsheetEvents.js';
import * as globals from './spreadsheetGlobalVariables.js';
import * as tools from './spreadsheetTools.js';

//TODO: e.preventDefault virker måske ikke altid?

export function setupInputBar() {
    $('#input-bar')
        .on('input', (e) => events.onInputBarInput(e.target))
    //TODO: Virker ikke ordenligt, da markøren ikke flytter op i inputbare, og det skal den.
    // .on('mousedown', (e) => e.preventDefault())
}

export function setupMergeButton() {
    $('#mergeButton')
        .on('click', () => {
            let mergedCells = []

            globals.selectedCells.forEach((cell) => {
                if ($(cell).attr('colspan') > 1) mergedCells.push(cell)
            })

            if ($(globals.editingCell).attr('colspan') > 1) mergedCells.push(globals.editingCell)

            if (mergedCells.length > 0) tools.demergeCells(mergedCells)
            else tools.mergeCells()
        })
        .on('mousedown',(e) => e.preventDefault())
    //TODO: Virker ikke altid?
}

export function setupBoldTextButton() {
    $('#boldText')
        .on('click', () => {
            let allCellsAreBold = true

            globals.selectedCells.forEach((cell) => {
                if (!$(cell).hasClass('bold')) allCellsAreBold = false
            })

            if (allCellsAreBold) tools.removeBoldText()
            else tools.setBoldText()
        })
        .on('mousedown', (e) => e.preventDefault())
}

export function setupCellAsHeaderButton() {
    $('#setCellAsHeader')
        .on('click', () => {
            let allCellsAreHeaders = globals.selectedCells.every((cell) => {
                return $(cell).hasClass('header')
            })

            if (allCellsAreHeaders) tools.removeCellAsHeader()
            else tools.setCellAsHeader()
        })
        .on('mousedown', (e) => e.preventDefault())
}

export function setupBlackBorderButton() {
    $('#setBlackBorders').on('click', () => {
        let allCellsHaveBlackBorders = globals.selectedCells.every((cell) => {
            return $(cell).hasClass('blackBorder')
        })

        if (allCellsHaveBlackBorders) tools.removeBlackBorder()
        else tools.setBlackBorder()
    })
}