import * as events from './spreadsheetEvents.js'

$(() => {
    events.onDocumentReady()
})

export function setupActionBar() {
    $('#action-container')
        .on('mousedown', (e) => e.preventDefault())
}

export function setupSDSL() {
    setupCreateTableButton()
    setupAddRowButton()
    setupDeleteRowButton()
    setupDeleteTableButton()
}

export function setupSGL() {
    setupBuildButton()
    setupMergeButton()
}

export function setupInputBar() {
    $('#input-bar')
        .on('input', (e) => events.onInputBarInput(e.target))
        .on('focus', () => events.onInputBarFocus())
        .on('focusout', () => events.onInputBarFocusOut())
}

export function setupSpreadsheetTypeRadioButtons() {
    $('input[name="spreadsheetType"]')
        .on('change', () => events.onSpreadsheetTypeRadioButtonsChange())
}

function setupCreateTableButton() {
    $('#createTable')
        .on('click', () => events.onCreateTableButtonClick())
}

function setupAddRowButton() {
    $('#addRow')
        .on('click', () => events.onAddRowButtonClick())
}

function setupDeleteRowButton() {
    $('#deleteRow')
        .on('click', () => events.onDeleteRowButtonClick())
}

function setupDeleteTableButton() {
    $('#deleteTable')
        .on('click', () => events.onDeleteTableButtonClick())
}

function setupBuildButton() {
    $('#build')
        .on('click', () => events.onBuildButtonClick())
}

function setupMergeButton() {
    $('#merge')
        .on('click', () => events.onMergeButtonClick())
}

export function setupCell(cell) {
    setupCellKeyDown(cell)
    setupCellKeyDownEnter(cell)
    cell
        .on('mousedown', () => events.onCellMouseDown())
        .on('mouseup', () => events.onCellMouseUp())
        .on('mouseenter', (e) => events.onCellMouseEnter(e.currentTarget))
        .on('mouseleave', (e) => events.onCellMouseLeave(e.currentTarget))
        .on('input', (e) => events.onCellInput(e.currentTarget))
        .on('click', () => events.onCellClick())
}

export function setupCellKeyDown(cell) {
    cell.on('keydown', (e) => {
        if (e.which === 9) events.onCellKeydownTab(e)
        else if (e.which === 37) events.onCellKeydownArrowLeft(e)
        else if (e.which === 38) events.onCellKeydownArrowUp(e)
        else if (e.which === 39) events.onCellKeydownArrowRight(e)
        else if (e.which === 40) events.onCellKeydownArrowDown(e)
    })
}

export function setupCellKeyDownEnter(cell) {
    cell.on('keydown', (e) => {
        if (e.which === 13) events.onCellKeydownEnter(e)
    })
}

export function setupCellTextDiv(cellTextDiv) {
    cellTextDiv
        .on('focus', (e) => events.onCellTextDivFocus(e.currentTarget))
        .on('focusout', (e) => events.onCellTextDivFocusout(e.currentTarget))
}