import * as spreadsheet from './spreadsheet.js'
import * as tools from './spreadsheetTools.js'
import * as globals from './spreadsheetGlobalVariables.js'
import * as events from './spreadsheetEvents.js'

let id = 1
let updateCounter = 10_000_000;

const socket = new WebSocket('ws://localhost:20895');
const debug = true;

//TODO: Do properly.
globals.setSpreadsheetName('Hello')

socket.addEventListener('open', function(event) {
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //  Dummy opening a sheet on the server .. only for demo purposes before functionality is added  //
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    let close = { sheetName:"Hello" }
    let cmsg = { method:"close-sheet", id:"0", data:JSON.stringify(close) };
    socket.send(JSON.stringify(cmsg));

    let open = { sheetName:"Hello", isSGL:true }
    let omsg = { method:"open-sheet", id:"1", data:JSON.stringify(open) };
    socket.send(JSON.stringify(omsg));

    //TODO: Remove after testing
    let cell00 = spreadsheet.getCellFromIndexes(0, 0)
    let cell10 = spreadsheet.getCellFromIndexes(1, 0)
    let cell20 = spreadsheet.getCellFromIndexes(2, 0)
    let cell30 = spreadsheet.getCellFromIndexes(3, 0)
    let cell40 = spreadsheet.getCellFromIndexes(4, 0)
    let cell01 = spreadsheet.getCellFromIndexes(0, 1)
    let cell11 = spreadsheet.getCellFromIndexes(1, 1)
    let cell21 = spreadsheet.getCellFromIndexes(2, 1)
    let cell31 = spreadsheet.getCellFromIndexes(3, 1)
    let cell41 = spreadsheet.getCellFromIndexes(4, 1)
    let cell12 = spreadsheet.getCellFromIndexes(1, 2)
    let cell22 = spreadsheet.getCellFromIndexes(2, 2)
    let cell32 = spreadsheet.getCellFromIndexes(3, 2)
    let cell23 = spreadsheet.getCellFromIndexes(2, 3)
    let cell33 = spreadsheet.getCellFromIndexes(3, 3)
    let cell04 = spreadsheet.getCellFromIndexes(0, 4)
    let cell14 = spreadsheet.getCellFromIndexes(1, 4)
    let cell24 = spreadsheet.getCellFromIndexes(2, 4)
    let cell34 = spreadsheet.getCellFromIndexes(3, 4)
    let cell44 = spreadsheet.getCellFromIndexes(4, 4)

    spreadsheet.setCellText(cell00, 'array : Config')
    spreadsheet.setCellText(cell01, 'attribute : Name')
    spreadsheet.setCellText(cell11, 'object : Sensor')
    spreadsheet.setCellText(cell41, 'attribute : Number')
    spreadsheet.setCellText(cell12, 'attribute : Name')
    spreadsheet.setCellText(cell22, 'object : SensorType')
    spreadsheet.setCellText(cell23, 'attribute : Name')
    spreadsheet.setCellText(cell33, 'attribute : Category')
    spreadsheet.setCellText(cell04, 'type : String')
    spreadsheet.setCellText(cell14, 'type : String')
    spreadsheet.setCellText(cell24, 'type : String')
    spreadsheet.setCellText(cell34, 'type : String')
    spreadsheet.setCellText(cell44, 'type : String')

    sendChange(cell00)
    sendChange(cell01)
    sendChange(cell11)
    sendChange(cell41)
    sendChange(cell12)
    sendChange(cell22)
    sendChange(cell23)
    sendChange(cell33)
    sendChange(cell04)
    sendChange(cell14)
    sendChange(cell24)
    sendChange(cell34)
    sendChange(cell44)

    tools.mergeCells([cell00, cell10, cell20, cell30, cell40])
    tools.mergeCells([cell11, cell21, cell31])
    tools.mergeCells([cell22, cell32])
    requestBuild()

    tools.changeToSDSL()
    $('#sdslRadioButton').prop('checked', true)

    let cell00sdsl = spreadsheet.getCellFromIndexes(0, 0)

    spreadsheet.setCellText(cell00sdsl, 'Config')
    events.onCreateTableButtonClick()

    // spreadsheet.setCellText(cell00sdsl, 'Hej med dig')
    // tools.createError([0, 0], [1, 5], 'Error, write Config')

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

//TODO: This need to be changed, as it doesn't include line index
function handleErrors(errors) {
    console.log("Diagnostic - Number of errors : " + errors.length);
    for(let i = 0; i < errors.length; i++) {
        tools.createError(errors[i].cellIndexes, errors[i].lineIndexes, errors[i].message)
    }
}

function handleCheckIfTextIsATableName(params) {
    let tableNameExists = params[3]
    let column = params[1]
    let row = params[2]
    let cell = spreadsheet.getCellFromIndexes(column, row)
    let infoBox = spreadsheet.getInfoBox(cell)
    let infoBoxShown = $(cell).hasClass('infoBoxShown')
    let cellType = spreadsheet.getCellType(cell)

    tools.hideCreateTableCodeCompletionForInfoBox(infoBox, cell)

    if (cellType === 'normal' && tableNameExists && !infoBoxShown) {
        let tableName = params[0]
        tools.createTableCodeCompletionForInfoBox(tableName, column, row)
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

    spreadsheet.setCellText(cell, text)
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

function handleItalicText(params) {
    let cell = spreadsheet.getCellFromIndexes(params[0], params[1])
    tools.setItalicText(cell)
}

function handleBlackBorder(params) {
    let cell = spreadsheet.getCellFromIndexes(params[0], params[1])
    tools.setBlackBorder(cell)
}

function handleSetAsHeaderCell(params) {
    let cell = spreadsheet.getCellFromIndexes(params[0], params[1])
    spreadsheet.setCellAsHeader(cell)
}

function handleSetAsDataCell(params) {
    let cell = spreadsheet.getCellFromIndexes(params[0], params[1])
    spreadsheet.setCellAsData(cell)
}

export function sendChange(cell) {
    let cellIndexes = spreadsheet.getCellIndexes(cell)
    let colspan = $(cell).prop('colspan')
    let content = spreadsheet.getCellText(cell)

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

export function requestBuild() {
    let data = { sheetName: globals.spreadsheetName }
    let message = { method: 'build', id: id, data: JSON.stringify(data) }

    socket.send(JSON.stringify(message))
    id++
}