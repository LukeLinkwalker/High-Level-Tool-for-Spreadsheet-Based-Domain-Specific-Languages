import * as elementCell from './spreadsheetElements/cell.js'
import * as elementError from './spreadsheetElements/error.js'
import * as elementInformationBox from './spreadsheetElements/informationBox.js'
import * as elementTable from './spreadsheetElements/table.js'
import * as toolsFormatting from './spreadsheetTools/formatting.js'
import * as toolsMerge from './spreadsheetTools/merge.js'
import * as globalVariables from './globalVariables.js'

let id = 1
let updateCounter = 10_000_000;
const socket = new WebSocket('ws://localhost:20895')

socket.addEventListener('open', () => {
    let close = { sheetName: 'Hello' }
    let cmsg = { method: 'close-sheet', id: '0', params: JSON.stringify(close) }
    socket.send(JSON.stringify(cmsg))

    let open = { sheetName: 'Hello', isSML: true }
    let omsg = { method: 'open-sheet', id: '1', params:JSON.stringify(open) }
    socket.send(JSON.stringify(omsg))
})

socket.addEventListener('message', (event) => {
    let jsonObject = JSON.parse(event.data)

    switch (jsonObject.method) {
        case 'check-if-text-is-a-table-name':
            handleCheckIfTextIsATableName(jsonObject.params)
            break
        case 'get-initial-table-range':
            handleGetInitialTableRange(jsonObject.params)
            break
        case 'create-table':
            handleCreateTable(jsonObject.code)
            break
        case 'set-text':
            handleSetText(jsonObject.params)
            break
        case 'merge':
            handleMerge(jsonObject.params)
            break
        case 'bold-text':
            handleBoldText(jsonObject.params)
            break
        case 'italic-text':
            handleItalicText(jsonObject.params)
            break
        case 'black-border':
            handleBlackBorder(jsonObject.params)
            break
        case 'set-as-header-cell':
            handleSetAsHeaderCell(jsonObject.params)
            break
        case 'set-as-data-cell':
            handleSetAsDataCell(jsonObject.params)
            break
        case 'diagnostic':
            handleErrors(jsonObject.content)
            break
    }
})

function handleErrors(errors) {
    elementError.hideAndClearAllErrors()

    for(let i = 0; i < errors.length; i++) {
        elementError.createError(errors[i].column, errors[i].row, errors[i].start, errors[i].end, errors[i].message)
    }
}

function handleCheckIfTextIsATableName(params) {
    let tableNameExists = params[3]
    let column = params[1]
    let row = params[2]
    let spreadsheetType = params[4]
    let cell = elementCell.getCellFromIndexes(column, row)
    let infoBox = elementInformationBox.getInfoBox(cell)
    let infoBoxShown = $(cell).hasClass('infoBoxShown')
    let cellType = elementCell.getCellType(cell)

    elementInformationBox.hideCreateTableCodeCompletionForInfoBox(infoBox, cell)

    if (cellType === 'normal' && tableNameExists && !infoBoxShown) {
        let tableName = params[0]
        elementInformationBox.createTableCodeCompletionForInfoBox(tableName, column, row, spreadsheetType)
    }
}

function handleGetInitialTableRange(params) {
    let tableRange = params[1]
    let spreadsheetType = params[2]

    if (tableRange === null) alert('A table with this name does not exist!')
    else {
        let tableName = params[0]
        elementTable.createTable(tableName, tableRange, spreadsheetType)
    }
}

function handleCreateTable(code) {
    if (code === 400) alert('Could not create table!')
}

function handleSetText(params) {
    let cell = elementCell.getCellFromIndexes(params[0], params[1])
    let text = params[2]

    elementCell.setCellText(cell, text, true)
}

function handleMerge(params) {
    let startCell = elementCell.getCellFromIndexes(params[0], params[1])
    let endCell = elementCell.getCellFromIndexes(params[2], params[3])

    toolsMerge.mergeCells(elementCell.getCellsInRange(startCell, endCell))
}

function handleBoldText(params) {
    let cell = elementCell.getCellFromIndexes(params[0], params[1])
    toolsFormatting.setBoldText(cell)
}

function handleItalicText(params) {
    let cell = elementCell.getCellFromIndexes(params[0], params[1])
    toolsFormatting.setItalicText(cell)
}

function handleBlackBorder(params) {
    let cell = elementCell.getCellFromIndexes(params[0], params[1])
    toolsFormatting.setBlackBorder(cell)
}

function handleSetAsHeaderCell(params) {
    let cell = elementCell.getCellFromIndexes(params[0], params[1])
    elementCell.setCellAsHeader(cell)
}

function handleSetAsDataCell(params) {
    let cell = elementCell.getCellFromIndexes(params[0], params[1])
    elementCell.setCellAsData(cell)
}

export function sendChange(cell) {
    let cellIndexes = elementCell.getCellIndexes(cell)
    let colspan = $(cell).prop('colspan')
    let content = elementCell.getCellText(cell)
    let cellType = elementCell.getCellType(cell)
    let skipEval = (cellType !== 'normal')

    let update = { sheetName: 'Hello', column: cellIndexes[0], row: cellIndexes[1], width: colspan, data: content,
        skipEval: skipEval }
    let update_msg = { method: 'update-sheet', id: updateCounter++, params: JSON.stringify(update) }

    socket.send(JSON.stringify(update_msg))
}

export function requestCheckIfTextIsATableName(cellText, column, row, spreadsheetType) {
    let data = { sheetName: globalVariables.spreadsheetName, cellText: cellText, column: column, row: row,
        spreadsheetType: spreadsheetType }
    let message = { method: 'check-if-text-is-a-table-name', id: id, params: JSON.stringify(data) }

    socket.send(JSON.stringify(message))
    id++
}

export function requestGetInitialTableRange(tableName, column, row, spreadsheetType) {
    let data = { sheetName: globalVariables.spreadsheetName, tableName: tableName, column: column, row: row,
        spreadsheetType: spreadsheetType }
    let message = { method: 'get-initial-table-range', id: id, params: JSON.stringify(data) }

    socket.send(JSON.stringify(message))
    id++
}

export function requestCreateTable(tableName, column, row, spreadsheetType) {
    let data = { sheetName: globalVariables.spreadsheetName, tableName: tableName, column: column, row: row,
        spreadsheetType: spreadsheetType }
    let message = { method: 'create-table', id: id, params: JSON.stringify(data) }

    socket.send(JSON.stringify(message))
    id++
}

export function requestBuild() {
    let data = { sheetName: globalVariables.spreadsheetName }
    let message = { method: 'build', id: id, params: JSON.stringify(data) }

    socket.send(JSON.stringify(message))
    id++

    alert('Model has been builded!')
}

export function requestNewFile(isSML) {
    if (isSML) {
        let close = { sheetName: 'Hello' }
        let cmsg = { method: 'close-sheet', id: '0', params: JSON.stringify(close) }
        socket.send(JSON.stringify(cmsg))
        
        let open = { sheetName: 'Hello', isSML: true }
        let omsg = { method: 'open-sheet', id: '1', params: JSON.stringify(open) }
        socket.send(JSON.stringify(omsg));
    } else {
        let close = { sheetName: 'Hello' }
        let cmsg = { method: 'close-sheet', id: '0', params: JSON.stringify(close) }
        socket.send(JSON.stringify(cmsg));

        let open = { sheetName: 'Hello', isSML: false }
        let omsg = { method: 'open-sheet', id: '1', params: JSON.stringify(open) }
        socket.send(JSON.stringify(omsg))
    }
}