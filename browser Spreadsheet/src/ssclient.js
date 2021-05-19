import * as spreadsheet from './spreadsheet.js'
import * as tools from './spreadsheetTools.js'
import * as globals from './spreadsheetGlobalVariables.js'

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

    //TODO: Fix this to SML along with on server
    let open = { sheetName:"Hello", isSGL:true }
    let omsg = { method:"open-sheet", id:"1", data:JSON.stringify(open) };
    socket.send(JSON.stringify(omsg));

    //TODO: Remove after testing

    let cell00 = spreadsheet.getCellFromIndexes(0, 0)
    let cell10 = spreadsheet.getCellFromIndexes(1, 0)
    let cell20 = spreadsheet.getCellFromIndexes(2, 0)
    let cell30 = spreadsheet.getCellFromIndexes(3, 0)
    let cell40 = spreadsheet.getCellFromIndexes(4, 0)
    let cell50 = spreadsheet.getCellFromIndexes(5, 0)
    let cell60 = spreadsheet.getCellFromIndexes(6, 0)
    let cell01 = spreadsheet.getCellFromIndexes(0, 1)
    let cell11 = spreadsheet.getCellFromIndexes(1, 1)
    let cell21 = spreadsheet.getCellFromIndexes(2, 1)
    let cell31 = spreadsheet.getCellFromIndexes(3, 1)
    let cell41 = spreadsheet.getCellFromIndexes(4, 1)
    let cell51 = spreadsheet.getCellFromIndexes(5, 1)
    let cell61 = spreadsheet.getCellFromIndexes(6, 1)
    let cell12 = spreadsheet.getCellFromIndexes(1, 2)
    let cell22 = spreadsheet.getCellFromIndexes(2, 2)
    let cell32 = spreadsheet.getCellFromIndexes(3, 2)
    let cell42 = spreadsheet.getCellFromIndexes(4, 2)
    let cell52 = spreadsheet.getCellFromIndexes(5, 2)
    let cell23 = spreadsheet.getCellFromIndexes(2, 3)
    let cell33 = spreadsheet.getCellFromIndexes(3, 3)
    let cell43 = spreadsheet.getCellFromIndexes(4, 3)
    let cell53 = spreadsheet.getCellFromIndexes(5, 3)

    let cell04 = spreadsheet.getCellFromIndexes(0, 4)
    let cell14 = spreadsheet.getCellFromIndexes(1, 4)
    let cell24 = spreadsheet.getCellFromIndexes(2, 4)
    let cell34 = spreadsheet.getCellFromIndexes(3, 4)
    let cell44 = spreadsheet.getCellFromIndexes(4, 4)
    let cell54 = spreadsheet.getCellFromIndexes(5, 4)
    let cell64 = spreadsheet.getCellFromIndexes(6, 4)

    //spreadsheet.setCellText(cell00, 'array : Config')
    //sendChange(cell00)
    //spreadsheet.setCellText(cell01, 'attribute : Name')
    //sendChange(cell01)
    //spreadsheet.setCellText(cell11, 'array : Sensors')
    //sendChange(cell11)
    //spreadsheet.setCellText(cell61, 'attribute : Functions')
    //sendChange(cell61)
    //spreadsheet.setCellText(cell12, 'attribute : Name')
    //sendChange(cell12)
    //spreadsheet.setCellText(cell22, 'array : Inputs')
    //sendChange(cell22)
    //spreadsheet.setCellText(cell42, 'array : Outputs')
    //sendChange(cell42)
    //spreadsheet.setCellText(cell23, 'attribute : Source')
    //sendChange(cell23)
    //spreadsheet.setCellText(cell33, 'attribute : Rate')
    //sendChange(cell33)
    //spreadsheet.setCellText(cell43, 'attribute : Type')
    //sendChange(cell43)
    //spreadsheet.setCellText(cell53, 'attribute : Rate')
    //sendChange(cell53)
    //spreadsheet.setCellText(cell04, 'type : String')
    //sendChange(cell04)
    //spreadsheet.setCellText(cell14, 'type : String')
    //sendChange(cell14)
    //spreadsheet.setCellText(cell24, 'type : String')
    //sendChange(cell24)
    //spreadsheet.setCellText(cell34, 'type : int')
    //sendChange(cell34)
    //spreadsheet.setCellText(cell44, 'type : String')
    //sendChange(cell44)
    //spreadsheet.setCellText(cell54, 'type : float')
    //sendChange(cell54)
    //spreadsheet.setCellText(cell64, 'type : string')
    //sendChange(cell64)
//
//
    //tools.mergeCells([cell00, cell10, cell20, cell30, cell40, cell50, cell60])
    //tools.mergeCells([cell11, cell21, cell31, cell41, cell51])
    //tools.mergeCells([cell22, cell32])
    //tools.mergeCells([cell42, cell52])
//
    //requestBuild()

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    close = { sheetName:"Hello" }
    cmsg = { method:"close-sheet", id:"0", data:JSON.stringify(close) };
    socket.send(JSON.stringify(cmsg));

    open = { sheetName:"Hello", isSGL:false }
    omsg = { method:"open-sheet", id:"1", data:JSON.stringify(open) };
    socket.send(JSON.stringify(omsg));

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    tools.changeToSDSL()
    $('#sdslRadioButton').prop('checked', true)

    let cell00sdsl = spreadsheet.getCellFromIndexes(0, 0)

    //spreadsheet.setCellText(cell00sdsl, 'Config')
    //events.onCreateTableButtonClick()

    // spreadsheet.setCellText(cell00sdsl, 'Hej med dig')
    // tools.createError(0, 0, 'Error, write Config')

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

function handleErrors(errors) {
    tools.hideAndClearAllErrors()

    console.log("Diagnostic - Number of errors : " + errors.length);
    for(let i = 0; i < errors.length; i++) {
        console.log("Error @ " + errors[i].column + " | " + errors[i].row + " - " + errors[i].start + " | " + errors[i].end + " -> " + errors[i].message)
        tools.createError(errors[i].column, errors[i].row, errors[i].start, errors[i].end, errors[i].message)
    }
}

function handleCheckIfTextIsATableName(params) {
    let tableNameExists = params[3]
    let column = params[1]
    let row = params[2]
    let spreadsheetType = params[4]
    let cell = spreadsheet.getCellFromIndexes(column, row)
    let infoBox = spreadsheet.getInfoBox(cell)
    let infoBoxShown = $(cell).hasClass('infoBoxShown')
    let cellType = spreadsheet.getCellType(cell)

    tools.hideCreateTableCodeCompletionForInfoBox(infoBox, cell)

    if (cellType === 'normal' && tableNameExists && !infoBoxShown) {
        let tableName = params[0]
        tools.createTableCodeCompletionForInfoBox(tableName, column, row, spreadsheetType)
    }
}

function handleGetInitialTableRange(params) {
    let tableRange = params[1]
    let spreadsheetType = params[2]

    if (tableRange === null) alert('A table with this name does not exist!')
    else {
        let tableName = params[0]
        tools.createTable(tableName, tableRange, spreadsheetType)
    }
}

function handleSetText(params) {
    let cell = spreadsheet.getCellFromIndexes(params[0], params[1])
    let text = params[2]

    spreadsheet.setCellText(cell, text, true)
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

    let update = { sheetName:"Hello", column:cellIndexes[0], row:cellIndexes[1], width:colspan, data: content}
    let update_msg = { method:"update-sheet", id:updateCounter++, data:JSON.stringify(update) }

    socket.send(JSON.stringify(update_msg))
    if (debug) console.log("Send change: " + JSON.stringify(object))
}

export function requestCheckIfTextIsATableName(cellText, column, row, spreadsheetType) {
    let data = { sheetName: globals.spreadsheetName, cellText: cellText, column: column, row: row,
        spreadsheetType: spreadsheetType }
    let message = { method: 'check-if-text-is-a-table-name', id: id, data: JSON.stringify(data) }

    socket.send(JSON.stringify(message))
    id++
}

export function requestGetInitialTableRange(tableName, column, row, spreadsheetType) {
    let data = { sheetName: globals.spreadsheetName, tableName: tableName, column: column, row: row,
        spreadsheetType: spreadsheetType }
    let message = { method: 'get-initial-table-range', id: id, data: JSON.stringify(data) }

    socket.send(JSON.stringify(message))
    id++
}

export function requestCreateTable(tableName, column, row, spreadsheetType) {
    let data = { sheetName: globals.spreadsheetName, tableName: tableName, column: column, row: row,
        spreadsheetType: spreadsheetType }
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

//Refactor with SML - Mikkels
export function requestNewFile(isSML) {
    if(isSML == true) {
        let close = { sheetName:"Hello" }
        let cmsg = { method:"close-sheet", id:"0", data:JSON.stringify(close) };
        socket.send(JSON.stringify(cmsg));
        
        //TODO: Fix this to SML along with on server
        let open = { sheetName:"Hello", isSGL:true }
        let omsg = { method:"open-sheet", id:"1", data:JSON.stringify(open) };
        socket.send(JSON.stringify(omsg));
    } else {
        let close = { sheetName:"Hello" }
        let cmsg = { method:"close-sheet", id:"0", data:JSON.stringify(close) };
        socket.send(JSON.stringify(cmsg));

        //TODO: Fix this to SML along with on server
        let open = { sheetName:"Hello", isSGL:false }
        let omsg = { method:"open-sheet", id:"1", data:JSON.stringify(open) };
        socket.send(JSON.stringify(omsg));
    }
}