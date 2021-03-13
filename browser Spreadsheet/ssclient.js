import * as spreadsheet from './spreadsheet.js'
import * as tools from './spreadsheetTools.js'

const socket = new WebSocket('ws://localhost:20895');
const debug = true;

socket.addEventListener('open', function(event) {
    //socket.send('Hello world');
})

socket.addEventListener('message', function(event) {
    console.log('Message from server ', event.data);
})

export function sendChange(cell) {
    let cellIndexes = spreadsheet.getCellIndexes(cell)
    let colspan = $(cell).attr('colspan')
    let width = (colspan === undefined) ? 1 : colspan
    let hiddenText = $(cell).data('hiddenText')
    let cellClone = $(cell).clone()
    let divs = $('.errorMessage', cellClone)

    divs.each((i, element) => {
        element.remove()
    })

    let data = hiddenText + cellClone.text()

    let object = {
        column: cellIndexes[0],
        row: cellIndexes[1],
        width: width,
        character: data
    }

    if (debug) console.log("Send change: " + JSON.stringify(object));

    socket.send(JSON.stringify(object));
}

//TODO: Remove after testing.
export function testCreateTable(column, row) {
    testCommand('setText', [0 + parseInt(column), 0 + parseInt(row), 'Config'])
    testCommand('setText', [0 + parseInt(column), 1 + parseInt(row), 'Name'])
    testCommand('setText', [1 + parseInt(column), 1 + parseInt(row), 'Sensors'])
    testCommand('setText', [1 + parseInt(column), 2 + parseInt(row), 'Name'])
    testCommand('setText', [2 + parseInt(column), 2 + parseInt(row), 'Outputs'])
    testCommand('setText', [2 + parseInt(column), 3 + parseInt(row), 'Type'])
    testCommand('setText', [3 + parseInt(column), 3 + parseInt(row), 'Rate'])
    testCommand('setText', [4 + parseInt(column), 1 + parseInt(row), 'Functions'])
    testCommand('merge', [0 + parseInt(column), 0 + parseInt(row), 4 + parseInt(column), 0 + parseInt(row)])
    testCommand('merge', [1 + parseInt(column), 1 + parseInt(row), 3 + parseInt(column), 1 + parseInt(row)])
    testCommand('merge', [2 + parseInt(column), 2 + parseInt(row), 3 + parseInt(column), 2 + parseInt(row)])
    testCommand('boldText', [0 + parseInt(column), 0 + parseInt(row)])
    testCommand('boldText', [1 + parseInt(column), 1 + parseInt(row)])
    testCommand('boldText', [2 + parseInt(column), 2 + parseInt(row)])
    testCommand('boldText', [4 + parseInt(column), 1 + parseInt(row)])
    testCommand('centerText', [0 + parseInt(column), 0 + parseInt(row)])
    testCommand('centerText', [0 + parseInt(column), 1 + parseInt(row)])
    testCommand('centerText', [0 + parseInt(column), 2 + parseInt(row)])
    testCommand('centerText', [0 + parseInt(column), 3 + parseInt(row)])
    testCommand('centerText', [1 + parseInt(column), 0 + parseInt(row)])
    testCommand('centerText', [1 + parseInt(column), 1 + parseInt(row)])
    testCommand('centerText', [1 + parseInt(column), 2 + parseInt(row)])
    testCommand('centerText', [1 + parseInt(column), 3 + parseInt(row)])
    testCommand('centerText', [2 + parseInt(column), 0 + parseInt(row)])
    testCommand('centerText', [2 + parseInt(column), 1 + parseInt(row)])
    testCommand('centerText', [2 + parseInt(column), 2 + parseInt(row)])
    testCommand('centerText', [2 + parseInt(column), 3 + parseInt(row)])
    testCommand('centerText', [3 + parseInt(column), 0 + parseInt(row)])
    testCommand('centerText', [3 + parseInt(column), 1 + parseInt(row)])
    testCommand('centerText', [3 + parseInt(column), 2 + parseInt(row)])
    testCommand('centerText', [3 + parseInt(column), 3 + parseInt(row)])
    testCommand('centerText', [4 + parseInt(column), 0 + parseInt(row)])
    testCommand('centerText', [4 + parseInt(column), 1 + parseInt(row)])
    testCommand('centerText', [4 + parseInt(column), 2 + parseInt(row)])
    testCommand('centerText', [4 + parseInt(column), 3 + parseInt(row)])
    testCommand('centerText', [0 + parseInt(column), 4 + parseInt(row)])
    testCommand('centerText', [1 + parseInt(column), 4 + parseInt(row)])
    testCommand('centerText', [2 + parseInt(column), 4 + parseInt(row)])
    testCommand('centerText', [3 + parseInt(column), 4 + parseInt(row)])
    testCommand('centerText', [4 + parseInt(column), 4 + parseInt(row)])
    testCommand('blackBorder', [0 + parseInt(column), 0 + parseInt(row)])
    testCommand('blackBorder', [0 + parseInt(column), 1 + parseInt(row)])
    testCommand('blackBorder', [0 + parseInt(column), 2 + parseInt(row)])
    testCommand('blackBorder', [0 + parseInt(column), 3 + parseInt(row)])
    testCommand('blackBorder', [1 + parseInt(column), 0 + parseInt(row)])
    testCommand('blackBorder', [1 + parseInt(column), 1 + parseInt(row)])
    testCommand('blackBorder', [1 + parseInt(column), 2 + parseInt(row)])
    testCommand('blackBorder', [1 + parseInt(column), 3 + parseInt(row)])
    testCommand('blackBorder', [2 + parseInt(column), 0 + parseInt(row)])
    testCommand('blackBorder', [2 + parseInt(column), 1 + parseInt(row)])
    testCommand('blackBorder', [2 + parseInt(column), 2 + parseInt(row)])
    testCommand('blackBorder', [2 + parseInt(column), 3 + parseInt(row)])
    testCommand('blackBorder', [3 + parseInt(column), 0 + parseInt(row)])
    testCommand('blackBorder', [3 + parseInt(column), 1 + parseInt(row)])
    testCommand('blackBorder', [3 + parseInt(column), 2 + parseInt(row)])
    testCommand('blackBorder', [3 + parseInt(column), 3 + parseInt(row)])
    testCommand('blackBorder', [4 + parseInt(column), 0 + parseInt(row)])
    testCommand('blackBorder', [4 + parseInt(column), 1 + parseInt(row)])
    testCommand('blackBorder', [4 + parseInt(column), 2 + parseInt(row)])
    testCommand('blackBorder', [4 + parseInt(column), 3 + parseInt(row)])
    testCommand('blackBorder', [0 + parseInt(column), 4 + parseInt(row)])
    testCommand('blackBorder', [1 + parseInt(column), 4 + parseInt(row)])
    testCommand('blackBorder', [2 + parseInt(column), 4 + parseInt(row)])
    testCommand('blackBorder', [3 + parseInt(column), 4 + parseInt(row)])
    testCommand('blackBorder', [4 + parseInt(column), 4 + parseInt(row)])
    testCommand('setAsHeader', [0 + parseInt(column), 0 + parseInt(row)])
    testCommand('setAsHeader', [0 + parseInt(column), 1 + parseInt(row)])
    testCommand('setAsHeader', [0 + parseInt(column), 2 + parseInt(row)])
    testCommand('setAsHeader', [0 + parseInt(column), 3 + parseInt(row)])
    testCommand('setAsHeader', [1 + parseInt(column), 0 + parseInt(row)])
    testCommand('setAsHeader', [1 + parseInt(column), 1 + parseInt(row)])
    testCommand('setAsHeader', [1 + parseInt(column), 2 + parseInt(row)])
    testCommand('setAsHeader', [1 + parseInt(column), 3 + parseInt(row)])
    testCommand('setAsHeader', [2 + parseInt(column), 0 + parseInt(row)])
    testCommand('setAsHeader', [2 + parseInt(column), 1 + parseInt(row)])
    testCommand('setAsHeader', [2 + parseInt(column), 2 + parseInt(row)])
    testCommand('setAsHeader', [2 + parseInt(column), 3 + parseInt(row)])
    testCommand('setAsHeader', [3 + parseInt(column), 0 + parseInt(row)])
    testCommand('setAsHeader', [3 + parseInt(column), 1 + parseInt(row)])
    testCommand('setAsHeader', [3 + parseInt(column), 2 + parseInt(row)])
    testCommand('setAsHeader', [3 + parseInt(column), 3 + parseInt(row)])
    testCommand('setAsHeader', [4 + parseInt(column), 0 + parseInt(row)])
    testCommand('setAsHeader', [4 + parseInt(column), 1 + parseInt(row)])
    testCommand('setAsHeader', [4 + parseInt(column), 2 + parseInt(row)])
    testCommand('setAsHeader', [4 + parseInt(column), 3 + parseInt(row)])
    testCommand('setAsData', [0 + parseInt(column), 4 + parseInt(row)])
    testCommand('setAsData', [1 + parseInt(column), 4 + parseInt(row)])
    testCommand('setAsData', [2 + parseInt(column), 4 + parseInt(row)])
    testCommand('setAsData', [3 + parseInt(column), 4 + parseInt(row)])
    testCommand('setAsData', [4 + parseInt(column), 4 + parseInt(row)])
}

function testCommand(command, parameters) {
    if (command === 'tableNames') return ['Config']
    else if (command === 'tableRange') {
        let startCell = spreadsheet.getCellFromID(parameters[0], parameters[1])
        let endCell = spreadsheet.getCellFromID(parameters[2], parameters[3])
        return spreadsheet.getCellsInRange(startCell, endCell)
    }
    else if (command === 'setText') tools.setText(spreadsheet.getCellFromID(parameters[0], parameters[1]), parameters[2])
    else if (command === 'merge') {
        let startCell = spreadsheet.getCellFromID(parameters[0], parameters[1])
        let endCell = spreadsheet.getCellFromID(parameters[2], parameters[3])
        tools.mergeCells(spreadsheet.getCellsInRange(startCell, endCell))
    }
    else if (command === 'boldText') tools.setBoldText(spreadsheet.getCellFromID(parameters[0], parameters[1]))
    else if (command === 'centerText') tools.setCenterText(spreadsheet.getCellFromID(parameters[0], parameters[1]))
    else if (command === 'blackBorder') tools.setBlackBorder(spreadsheet.getCellFromID(parameters[0], parameters[1]))
    else if (command === 'setAsHeader') tools.setCellAsHeader(spreadsheet.getCellFromID(parameters[0], parameters[1]))
    else if (command === 'setAsData') tools.setCellAsData(spreadsheet.getCellFromID(parameters[0], parameters[1]))
}

export function testSendToServer(command, parameters) {
    if (command === 'getTableNames') return testCommand('tableNames')
    //TODO: getTableRange only made for testing. Fix it later.
    else if (command === 'getTableRange') {
        let cellIndexes = spreadsheet.getCellIndexes(parameters[0])
        let column = cellIndexes[0]
        let row = cellIndexes[1]
        return testCommand('tableRange', [0 + column, 0 + row, 4 + column, 4 + row])
    }
    else if (command === 'createTable') testCreateTable(parameters[0], parameters[1])
}