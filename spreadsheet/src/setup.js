import * as elementCell from './spreadsheetElements/cell.js'
import * as elementTable from './spreadsheetElements/table.js'
import * as events from './events.js'
import * as globalVariables from './globalVariables.js'

$(() => {
    events.onDocumentReady()
})

export function createSpreadsheet() {
    globalVariables.setColumnSize(30)
    globalVariables.setRowSize(40)

    let table = $('<table>').attr('id', 'dynamicTable' + globalVariables.spreadsheetType)
    let tableBody = $('<tbody>').attr('id', 'dynamicBody' + globalVariables.spreadsheetType)

    for (let row = 0; row < globalVariables.rowSize; row++) {
        let newRow = $('<tr>')

        for (let column = 0; column < globalVariables.columnSize; column++) {
            newRow.append(elementCell.createCell(column, row, globalVariables.spreadsheetType))
        }
        tableBody.append(newRow)
    }

    elementTable.createRowHeader(globalVariables.rowSize, tableBody)
    elementTable.createColumnHeader(globalVariables.columnSize, table)
    table.append(tableBody)
    $('#cell-container-' + globalVariables.spreadsheetType).append(table)
}

export function setupActionBar() {
    $('#action-container').on('mousedown', (e) => e.preventDefault())
}

export function setupSDSL() {
    setupCreateTableButton()
    setupAddRowButton()
    setupDeleteRowButton()
    setupDeleteTableButton()
}

export function setupSML() {
    setupBuildButton()
    setupCreateRulesTableButton()
    setupMergeButton()
}

export function setupInputBar() {
    $('#input-bar')
        .on('input', (e) => events.onInputBarInput(e.target))
        .on('focus', () => events.onInputBarFocus())
        .on('focusout', () => events.onInputBarFocusOut())
}

export function setupSpreadsheetTypeRadioButtons() {
    $('input[name="spreadsheetType"]').on('change', () => events.onSpreadsheetTypeRadioButtonsChange())
}

function setupCreateTableButton() {
    $('#createTable').on('click', () => events.onCreateTableButtonClick())
}

function setupAddRowButton() {
    $('#addRow').on('click', () => events.onAddRowButtonClick())
}

function setupDeleteRowButton() {
    $('#deleteRow').on('click', () => events.onDeleteRowButtonClick())
}

function setupDeleteTableButton() {
    $('#deleteTable').on('click', () => events.onDeleteTableButtonClick())
}

function setupBuildButton() {
    $('#build').on('click', () => events.onBuildButtonClick())
}

function setupCreateRulesTableButton() {
    $('#createRulesTable').on('click', () => events.onCreateRulesTableButtonClick())
}

function setupMergeButton() {
    $('#merge').on('click', () => events.onMergeButtonClick())
}

export function setupCell(cell) {
    setupCellKeyDown(cell)
    setupCellKeyDownEnter(cell)
    cell
        .on('mousedown', (e) => events.onCellMouseDown(e.currentTarget))
        .on('mouseup', (e) => events.onCellMouseUp(e.currentTarget))
        .on('mouseenter', (e) => events.onCellMouseEnter(e.currentTarget))
        .on('mouseleave', (e) => events.onCellMouseLeave(e.currentTarget))
        .on('input', (e) => events.onCellInput(e.currentTarget))
        .on('click', (e) => events.onCellClick(e.currentTarget))
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