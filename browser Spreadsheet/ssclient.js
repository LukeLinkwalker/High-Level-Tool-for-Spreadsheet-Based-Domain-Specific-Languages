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
export function testCreateTable() {
    testCommand('setText', [0, 0, 'Config'])
    testCommand('setText', [0, 1, 'Name'])
    testCommand('setText', [1, 1, 'Sensors'])
    testCommand('setText', [1, 2, 'Name'])
    testCommand('setText', [2, 2, 'Outputs'])
    testCommand('setText', [2, 3, 'Type'])
    testCommand('setText', [3, 3, 'Rate'])
    testCommand('setText', [4, 1, 'Functions'])
    testCommand('merge', [0, 0, 4, 0])
    testCommand('merge', [1, 1, 3, 1])
    testCommand('merge', [2, 2, 3, 2])
    testCommand('boldText', [0, 0])
    testCommand('boldText', [1, 1])
    testCommand('boldText', [2, 2])
    testCommand('boldText', [4, 1])
    testCommand('centerText', [0, 0])
    testCommand('centerText', [0, 1])
    testCommand('centerText', [0, 2])
    testCommand('centerText', [0, 3])
    testCommand('centerText', [1, 0])
    testCommand('centerText', [1, 1])
    testCommand('centerText', [1, 2])
    testCommand('centerText', [1, 3])
    testCommand('centerText', [2, 0])
    testCommand('centerText', [2, 1])
    testCommand('centerText', [2, 2])
    testCommand('centerText', [2, 3])
    testCommand('centerText', [3, 0])
    testCommand('centerText', [3, 1])
    testCommand('centerText', [3, 2])
    testCommand('centerText', [3, 3])
    testCommand('centerText', [4, 0])
    testCommand('centerText', [4, 1])
    testCommand('centerText', [4, 2])
    testCommand('centerText', [4, 3])
    testCommand('centerText', [0, 4])
    testCommand('centerText', [1, 4])
    testCommand('centerText', [2, 4])
    testCommand('centerText', [3, 4])
    testCommand('centerText', [4, 4])
    testCommand('blackBorder', [0, 0])
    testCommand('blackBorder', [0, 1])
    testCommand('blackBorder', [0, 2])
    testCommand('blackBorder', [0, 3])
    testCommand('blackBorder', [1, 0])
    testCommand('blackBorder', [1, 1])
    testCommand('blackBorder', [1, 2])
    testCommand('blackBorder', [1, 3])
    testCommand('blackBorder', [2, 0])
    testCommand('blackBorder', [2, 1])
    testCommand('blackBorder', [2, 2])
    testCommand('blackBorder', [2, 3])
    testCommand('blackBorder', [3, 0])
    testCommand('blackBorder', [3, 1])
    testCommand('blackBorder', [3, 2])
    testCommand('blackBorder', [3, 3])
    testCommand('blackBorder', [4, 0])
    testCommand('blackBorder', [4, 1])
    testCommand('blackBorder', [4, 2])
    testCommand('blackBorder', [4, 3])
    testCommand('blackBorder', [0, 4])
    testCommand('blackBorder', [1, 4])
    testCommand('blackBorder', [2, 4])
    testCommand('blackBorder', [3, 4])
    testCommand('blackBorder', [4, 4])
    testCommand('setAsHeader', [0, 0])
    testCommand('setAsHeader', [0, 1])
    testCommand('setAsHeader', [0, 2])
    testCommand('setAsHeader', [0, 3])
    testCommand('setAsHeader', [1, 0])
    testCommand('setAsHeader', [1, 1])
    testCommand('setAsHeader', [1, 2])
    testCommand('setAsHeader', [1, 3])
    testCommand('setAsHeader', [2, 0])
    testCommand('setAsHeader', [2, 1])
    testCommand('setAsHeader', [2, 2])
    testCommand('setAsHeader', [2, 3])
    testCommand('setAsHeader', [3, 0])
    testCommand('setAsHeader', [3, 1])
    testCommand('setAsHeader', [3, 2])
    testCommand('setAsHeader', [3, 3])
    testCommand('setAsHeader', [4, 0])
    testCommand('setAsHeader', [4, 1])
    testCommand('setAsHeader', [4, 2])
    testCommand('setAsHeader', [4, 3])
    testCommand('setAsData', [0, 4])
    testCommand('setAsData', [1, 4])
    testCommand('setAsData', [2, 4])
    testCommand('setAsData', [3, 4])
    testCommand('setAsData', [4, 4])
}

export function testCommand(command, parameters) {
    if (command === 'tableNames') return ['Config']
    else if (command === 'tableRange') return spreadsheet.getCellsInRange([parameters[0], parameters[1]], [parameters[2], parameters[3]])
    else if (command === 'setText') spreadsheet.setText(spreadsheet.getCellFromID(parameters[0], parameters[1]), parameters[2])
    else if (command === 'merge') tools.mergeCells(spreadsheet.getCellsInRange([parameters[0], parameters[1]], [parameters[2], parameters[3]]))
    else if (command === 'boldText') tools.setBoldText(spreadsheet.getCellFromID(parameters[0], parameters[1]))
    else if (command === 'centerText') tools.setCenterText(spreadsheet.getCellFromID(parameters[0], parameters[1]))
    else if (command === 'blackBorder') tools.setBlackBorder(spreadsheet.getCellFromID(parameters[0], parameters[1]))
    else if (command === 'setAsHeader') tools.setCellAsHeader(spreadsheet.getCellFromID(parameters[0], parameters[1]))
    else if (command === 'setAsData') tools.setCellAsData(spreadsheet.getCellFromID(parameters[0], parameters[1]))
}

export function testSendToServer(command, parameters) {
    if (command === 'getTableNames') return testCommand('tableNames')
    else if (command === 'getTableRange') return testCommand('tableRange', [0, 0, 4, 4])
    else if (command === 'createTable') testCreateTable()
}