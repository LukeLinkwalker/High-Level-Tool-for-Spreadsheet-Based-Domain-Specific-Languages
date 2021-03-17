import * as events from './spreadsheetEvents.js';

$(() => {
    events.onDocumentReady()
})

export function setupKeys() {
    $(document)
        .on('keydown', (e) => {
            if (e.which === 9) events.onDocumentKeypressTab(e)
            else if (e.which === 13) events.onDocumentKeypressEnter(e)
        })
}

export function setupSDSL() {
    $('.sglClass').css('display', 'none')
    $('.sdslClass').css('display', '')

    setupAddRow()
}

export function setupSGL() {
    $('.sdslClass').css('display', 'none')
    $('.sglClass').css('display', '')
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
        .on('mousedown', (e) => e.preventDefault())
}

export function setupCreateTableButton() {
    $('#createTable')
        .on('click', () => events.onCreateTableButtonClick())
        .on('mousedown', (e) => e.preventDefault())
}

function setupAddRow() {
    $('#addRow')
        .on('click', () => events.onAddRowButtonClick())
        .on('mousedown', (e) => e.preventDefault())
}