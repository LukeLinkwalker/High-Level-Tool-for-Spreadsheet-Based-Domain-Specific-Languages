const socket = new WebSocket('ws://localhost:20895');
const debug = true;

socket.addEventListener('open', function(event) {
    //socket.send('Hello world');
})

socket.addEventListener('message', function(event) {
    console.log('Message from server ', event.data);
})

function sendChange(cellIndexes, width, data) {
    // let  obj = {
    //     cellx: _cellx,
    //     celly: _celly,
    //     position: _position,
    //     character: _character
    // }

    let  obj = {
        column: cellIndexes[0],
        row: cellIndexes[1],
        position: width,
        character: data
    }

    if (debug) console.log("Send change: " + JSON.stringify(obj));

    socket.send(JSON.stringify(obj));
}