import * as spreadsheet from './spreadsheet.js'

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