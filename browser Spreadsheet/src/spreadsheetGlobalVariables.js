export let editingCell = null
export let cellsMarked = false
export let selectedCells = []
export let selectedStartCell
export let selectedEndCell
export let columnSize
export let rowSize
export let mouseDown
export let spreadsheetType
export let currentColumn
export let currentRow

//TODO: Should sheetName be here?
export let spreadsheetName

export function setEditingCell(value) {
    editingCell = value
}

export function setCellsMarked(value) {
    cellsMarked = value
}

export function setSelectedCells(value) {
    selectedCells = value
}

export function setSelectedStartCell(value) {
    selectedStartCell = value
}

export function setSelectedEndCell(value) {
    selectedEndCell = value
}

export function setColumnSize(value) {
    columnSize = value
}

export function setRowSize(value) {
    rowSize = value
}

export function setMouseDown(value) {
    mouseDown = value
}

export function setSpreadsheetType(value) {
    spreadsheetType = value
}

export function setSpreadsheetName(value) {
    spreadsheetName = value
}

export function setCurrentColumn(value) {
    currentColumn = value
}

export function setCurrentRow(value) {
    currentRow = value
}