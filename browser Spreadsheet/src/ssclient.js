import * as spreadsheet from './spreadsheet.js'
import * as tools from './spreadsheetTools.js'
import * as globals from './spreadsheetGlobalVariables.js'

let id = 1

const socket = new WebSocket('ws://localhost:20895');
const debug = true;

socket.addEventListener('open', function(event) {
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //  Dummy opening a sheet on the server .. only for demo purposes before functionality is added  //
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    let close = { sheetName:"Hello" }
    let cmsg = { method:"close-sheet", id:"0", data:JSON.stringify(close) };
    socket.send(JSON.stringify(cmsg));

    let open = { sheetName:"Hello" }
    let omsg = { method:"open-sheet", id:"1", data:JSON.stringify(open) };
    socket.send(JSON.stringify(omsg));
    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
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
        case 'set-text':
            handleSetText(jsonObject.params)
            break
        case 'merge':
            handleMerge(jsonObject.params)
            break
        case 'bold-text':
            handleBoldText(jsonObject.params)
            break
        case 'center-text':
            handleCenterText(jsonObject.params)
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
    }
})

function handleCheckIfTextIsATableName(params) {
    let tableNameExists = params[3]

    if (tableNameExists) {
        let cellText = params[0]
        let column = params[1]
        let row = params[2]

        tools.suggestion(cellText, column, row)
    }
}

function handleGetInitialTableRange(params) {
    if (params[3] === null) alert('A table with this name does not exist!')
    else {
        let tableName = params[0]
        let column = params[1]
        let row = params[2]

        tools.createTable(tableName, column, row, params[3])
    }
}

function handleSetText(params) {
    let cell = spreadsheet.getCellFromIndexes(params[0], params[1])
    let text = params[2]

    tools.setText(cell, text)
    sendChange(cell)
}

function handleMerge(params) {
    let startCell = spreadsheet.getCellFromIndexes(params[0], params[1])
    let endCell = spreadsheet.getCellFromIndexes(params[2], params[3])

    tools.mergeCells(spreadsheet.getCellsInRange(startCell, endCell))
}

function handleBoldText(params) {
    let cell = spreadsheet.getCellFromIndexes(params[0], params[1])

    tools.setBoldText(cell)
}

function handleCenterText(params) {
    let cell = spreadsheet.getCellFromIndexes(params[0], params[1])

    tools.setCenterText(cell)
}

function handleBlackBorder(params) {
    let cell = spreadsheet.getCellFromIndexes(params[0], params[1])

    tools.setBlackBorder(cell)
}

function handleSetAsHeaderCell(params) {
    let cell = spreadsheet.getCellFromIndexes(params[0], params[1])

    tools.setCellAsHeader(cell)
}

function handleSetAsDataCell(params) {
    let cell = spreadsheet.getCellFromIndexes(params[0], params[1])

    tools.setCellAsData(cell)
}

//TODO: Fix this. Should only take text
var updateCounter = 10_000_000;
export function sendChange(cell) {
    let cellIndexes = spreadsheet.getCellIndexes(cell)
    let colspan = $(cell).prop('colspan')
    //let width = (colspan === undefined) ? 1 : colspan + 1
    let hiddenText = $(cell).data('hiddenText')
    let cellClone = $(cell).clone()
    let divs = $('.errorMessage', cellClone)

    divs.each((i, element) => {
        element.remove()
    })

    let content = hiddenText + cellClone.text()

    let object = {
        column: cellIndexes[0],
        row: cellIndexes[1],
        width: colspan,
        character: content
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     Sending cell data to server                               //
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    let update = { sheetName:"Hello", column:cellIndexes[0], row:cellIndexes[1], width:colspan, data: content};
    let update_msg = { method:"update-sheet", id:updateCounter++, data:JSON.stringify(update) };
    socket.send(JSON.stringify(update_msg));

    if (debug) console.log("Send change: " + JSON.stringify(object));

    //TODO: This caused an error on the server
    // socket.send(JSON.stringify(object));
}

export function requestCheckIfTextIsATableName(cellText, column, row) {
    let data = { sheetName: globals.spreadsheetName, cellText: cellText, column: column, row: row }
    let message = { method: 'check-if-text-is-a-table-name', id: id, data: JSON.stringify(data) }

    socket.send(JSON.stringify(message))
    id++
}

export function requestGetInitialTableRange(tableName, column, row) {
    let data = { sheetName: globals.spreadsheetName, tableName: tableName, column: column, row: row }
    let message = { method: 'get-initial-table-range', id: id, data: JSON.stringify(data) }

    socket.send(JSON.stringify(message))
    id++
}

export function requestCreateTable(tableName, column, row) {
    let data = { sheetName: globals.spreadsheetName, tableName: tableName, column: column, row: row}
    let message = { method: 'create-table', id: id, data: JSON.stringify(data) }

    socket.send(JSON.stringify(message))
    id++
}