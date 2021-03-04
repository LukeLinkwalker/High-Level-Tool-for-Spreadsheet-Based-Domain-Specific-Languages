import * as events from './spreadsheetEvents.js';

export function setupDocument() {
    $(() => {
        events.onDocumentReady()
    })

    $(document)
        .on('keydown', (e) => {
            if (e.which === 9) events.onDocumentKeypressTab(e)
            else if (e.which === 13) events.onDocumentKeypressEnter(e)
        })
}

export function setupInputBar() {
    $('#input-bar')
        .on('input', (e) => events.onInputBarInput(e.target))
        .on('focus', () => events.onInputBarFocus())
        .on('focusout', () => events.onInputBarFocusOut())
}

export function setupMergeButton() {
    $('#mergeButton')
        .on('click', () => events.onMergeButtonClick())
        //TODO Fjern det her hvis e.preventDefault() virker
        // .on('mouseup',() => events.onMergeButtonMouseDown())
        .on('mousedown', (e) => e.preventDefault())
}

export function setupBoldTextButton() {
    $('#boldText')
        .on('click', () => events.onBoldTextButtonClick())
        .on('mousedown', (e) => e.preventDefault())
}

export function setupCellAsHeaderButton() {
    $('#setCellAsHeader')
        .on('click', () => events.onCellAsHeaderButtonClick())
        .on('mousedown', (e) => e.preventDefault())
}

export function setupBlackBorderButton() {
    $('#setBlackBorders')
        .on('click', () => events.onBlackBorderButtonClick())
        .on('mousedown', (e) => e.preventDefault())
}