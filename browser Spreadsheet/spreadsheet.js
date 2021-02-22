var arr = null;
var mouseDown = false;
var initialCell = null;
var hoveringCell = null;

function init() {
};

function initActionBar(container) {

};

function initInputBar(container) {

};

function initCells(container) {
    createCells(container);
};

function createCells(container) {
    let tableSize = 10;
    arr = create2DArray(tableSize);

    $("#" + container).append('<table id="dynamicTable">');
    $("#" + container).append('<tbody id="dynamicBody">');

    for(var row = 0; row < tableSize; row += 1) {

        $("#" + container).append('<tr>');

        for(var column = 0; column < tableSize; column += 1) {
            $("#" + container).append('<td contenteditable="false" colspan="1" mouseenter="function() { console.log("test!"); };"onclick="activateCell(' + column + ',' + row + ')" onkeyup="store(' + column + ',' + row + ')" id="' + "tr-" + column + "-" + row + '"></td>');
            $("#td-" + column + "-" + row).mouseenter(function() {
                console.log("mouse over!");
            });
        }

        $("#" + container).append('</tr>');
    }

    $("#" + container).append('</tbody>');
    $("#" + container).append('</table>');

    $('body').mousedown(function() {
        mouseDown = true;
        hoveringCell.addClass("selected");
        initialCell = ID2POS(hoveringCell.prop('id'));
        console.log(initialCell);
    });

    $('body').mouseup(function() {
        mouseDown = false;
        // trigger editable and more events
        initialCell = null;
    });

    $('td').mouseover(function() {
        hoveringCell = $(this);

        if(mouseDown == true) {
            //console.log(hoveringCell);
            //let column = Number(ID2POS(hoveringCell.prop('id')[0]));
            //console.log(initialCell[0] + " - " + column);
            //console.log(Number(Math.abs(Number(initialCell[0]) - column)));
            //console.log(Number(initialCell[0]) - Number(column))
            //if(Math.abs(initialCell[0] - column) > 0) {
            //    if(column < initialCell[0]) {
            //        // left
            //        for(var i = 0; i < Math.abs(initialCell[0] - column); i += 1) {
            //            console.log("Selected: " + "#tr-" + (initialCell[0] - i) + "-" + initialCell[1]);
            //            $("#tr-" + (initialCell[0] - i) + "-" + initialCell[1]).addClass("selected");
            //        }
            //    } else {
            //        // right
            //        for(var i = 0; i < Math.abs(initialCell[0] - column); i += 1) {
            //            $("#tr-" + (initialCell[0] + i) + "-" + initialCell[1]).addClass("selected");
            //        }
            //    }
            //}
        }
    });
}

var activeCell = null;
function activateCell(column, row) {
    if(activeCell != null && ("#tr-" + column + "-" + row) != activeCell) {
        $(activeCell).prop("contenteditable", false);
        $(activeCell).removeClass("selected");
    }

    activeCell = "#tr-" + column + "-" + row;
    $(activeCell).prop("contenteditable", true);
    $(activeCell).addClass("selected");
}

function store(column, row) {
    arr[column][row] = $("#tr-" + column + "-" + row).text();
}

function create2DArray(size) {
    var array = new Array(size);

    for(var i = 0; i < size; i += 1) {
        array[i] = new Array(size);
    }

    return array;
}

function ID2POS(id) {
    let parts = id.split('-');
    return [parts[1], parts[2]];
}