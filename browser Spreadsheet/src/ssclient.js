import * as spreadsheet from './spreadsheet.js'
import * as tools from './spreadsheetTools.js'
import * as globals from './spreadsheetGlobalVariables.js'

let id = 1
let updateCounter = 10_000_000;

const socket = new WebSocket('ws://localhost:20895');
const debug = true;

//TODO do properly.
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
    let cell01 = spreadsheet.getCellFromIndexes(0, 1)
    let cell11 = spreadsheet.getCellFromIndexes(1, 1)
    let cell21 = spreadsheet.getCellFromIndexes(2, 1)
    let cell12 = spreadsheet.getCellFromIndexes(1, 2)
    let cell22 = spreadsheet.getCellFromIndexes(2, 2)
    let cell03 = spreadsheet.getCellFromIndexes(0, 3)
    let cell13 = spreadsheet.getCellFromIndexes(1, 3)
    let cell23 = spreadsheet.getCellFromIndexes(2, 3)

    spreadsheet.setCellText(cell00, 'array : Config')
    spreadsheet.setCellText(cell01, 'attribute : Name')
    spreadsheet.setCellText(cell11, 'object : Sensor')
    spreadsheet.setCellText(cell12, 'attribute : Name')
    spreadsheet.setCellText(cell22, 'attribute : Type')
    spreadsheet.setCellText(cell03, 'type : String')
    spreadsheet.setCellText(cell13, 'type : String')
    spreadsheet.setCellText(cell23, 'type : String')

    sendChange(cell00)
    sendChange(cell01)
    sendChange(cell11)
    sendChange(cell12)
    sendChange(cell22)
    sendChange(cell03)
    sendChange(cell13)
    sendChange(cell23)

    tools.mergeCells([cell00, cell10, cell20])
    tools.mergeCells([cell11, cell21])
    requestBuild()

    tools.changeToSDSL()
    $('#sdslRadioButton').prop('checked', true)

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
            break;
        // case 'center-text':
        //     handleCenterText(jsonObject.params)
        //     break
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

//TODO: Rød streg skal vises hele, besked skal vises ved mouseover, og det skal ske automatisk.
function handleErrors(errors) {
    removeErrors();

    console.log("Diagnostic - Number of errors : " + errors.length);
    for(let i = 0; i < errors.length; i += 1) {
        globals.setError(errors[i].message, errors[i].column, errors[i].row);
        showError(errors[i].column, errors[i].row);
    }
}

function removeErrors() {
    $("td").each(function() {
        $(this).removeClass("errorHighlight");
    });
}

function showError(column, row) {
    $(spreadsheet.getCellFromIndexes(column, row)).addClass("errorHighlight");
}

function handleCheckIfTextIsATableName(params) {
    let tableNameExists = params[3]
    let column = params[1]
    let row = params[2]
    let cell = spreadsheet.getCellFromIndexes(column, row)
    let infoBox = spreadsheet.getInfoBox(cell)
    let infoBoxShown = $(cell).data('infoBoxShown')

    tools.hideCreateTableCodeCompletionForInfoBox(infoBox, cell)

    if (tableNameExists && !infoBoxShown) {
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

// function handleCenterText(params) {
//     let cell = spreadsheet.getCellFromIndexes(params[0], params[1])
//     tools.setCenterText(cell)
// }

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

export function requestBuild() {
    let data = { sheetName: globals.spreadsheetName }
    let message = { method: 'build', id: id, data: JSON.stringify(data) }

    socket.send(JSON.stringify(message))
    id++
}