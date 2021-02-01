const socket = new WebSocket('ws://localhost:20895');
const debug = true;

socket.addEventListener('open', function(event) {
    //socket.send('Hello world');
});

socket.addEventListener('message', function(event) {
    console.log('Message from server ', event.data);
});

function sendChange(_cellx, _celly, _position, _character) {
    var obj = {
        cellx: _cellx,
        celly: _celly,
        position: _position,
        character: _character
    };

    if(debug) {
        console.log("Send change: " + JSON.stringify(obj));
    }

    socket.send(JSON.stringify(obj));
};

function demoSendChange() {
    sendChange(0,0,0, "W");
    sendChange(0,0,1, "e");
    sendChange(0,0,2, "b");
    sendChange(0,0,3, "s");
    sendChange(0,0,4, "e");
    sendChange(0,0,5, "r");
    sendChange(0,0,6, "v");
    sendChange(0,0,7, "e");
    sendChange(0,0,8, "r");
}