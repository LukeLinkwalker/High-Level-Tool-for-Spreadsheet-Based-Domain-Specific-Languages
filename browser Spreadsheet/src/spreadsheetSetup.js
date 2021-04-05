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
    cell.on('mousedown', () => events.onCellMouseDown())
    cell.on('mouseup', () => events.onCellMouseUp())
    cell.on('mouseenter', (e) => events.onCellMouseEnter(e.target))
    cell.on('mouseleave', (e) => events.onCellMouseLeave(e.target))
    cell.on('focus', (e) => events.onCellFocus(e.target))
    cell.on('input', (e) => events.onCellInput(e.target))
    cell.on('click', () => events.onCellClick())
    cell.on('keydown', (e) => {
        if (e.which === 9) events.onDocumentKeydownTab(e)
        else if (e.which === 13) events.onDocumentKeydownEnter(e)
        else if (e.which === 37) events.onCellKeydownArrowLeft(e)
        else if (e.which === 38) events.onCellKeydownArrowUp(e)
        else if (e.which === 39) events.onCellKeydownArrowRight(e)
        else if (e.which === 40) events.onCellKeydownArrowDown(e)
    })
}