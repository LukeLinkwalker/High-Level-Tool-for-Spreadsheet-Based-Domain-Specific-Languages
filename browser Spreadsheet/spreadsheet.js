let array = null;
let initialCell = null;
let hoveringCell = null;

//MIT
let editingCell = null
let mouseDown = false
let cellsMarked = false
let selectedCells = []
let selectedStartCell, selectedEndCell
let columnSize, rowSize

$(() =>  {
    $('#mergeButton').on('click', () => {
        //Used for when multiple merged cells are selected
        let mergedCells = []

        selectedCells.forEach((cell) => {
            if ($(cell).attr('colspan') > 1) mergedCells.push(cell)
        })

        if ($(editingCell).attr('colspan') > 1) mergedCells.push(editingCell)

        if (mergedCells.length > 0) demergeCells(mergedCells)
        else mergeCells()
    }).on('mousedown',(e) => {
        //Don't remove focus from cell, when button is clicked
        e.preventDefault()
    })
})


function init() {
}

function initActionBar(container) {
}

function initInputBar(container) {
}

function initCells(container) {
    createCells(container)
}

function createCells(container) {
    columnSize = 10
    rowSize = 10
    let tableBody = $('<tbody id="dynamicBody">')

    for (let row = 0; row < rowSize; row++) {
        let newRow = $('<tr>')

        for (let column = 0; column < columnSize; column++) {
            newRow.append(createCell(column, row))
        }
        tableBody.append(newRow)
    }

    let table = $('<table id="dynamicTable">')

    createRowHeader(rowSize, tableBody)
    createColumnHeader(columnSize, table)
    table.append(tableBody)
    $('#' + container).append(table)
}

function createCell(column, row) {
    let cell = $('<td>')
    cell.attr('id', createCellID(column, row))
    cell.attr('contenteditable', true)
    cell.on('mousedown', (e) => onCellMouseDown(e))
    cell.on('mouseup', (e) => onCellMouseUp(e))
    cell.on('mouseover', (e) => onCellMouseOver(e))

    return cell

    // td.ondblclick = onDoubleClick;
    // td.onkeypress = onTdKeyPress;
    // var text = document.createTextNode("");
    // td.appendChild(text);
}

function createCellID(column, row) {
    return "cell-" + column + "-" + row
}

function getCellFromID(column, row) {
    return $('#' + createCellID(column, row))[0]
}

function getCellIndexes(cell) {
    let cellID = $(cell).attr('id')
    let matches = cellID.match(/^cell-(\d+)-(\d+)/)

    if (matches) return [Number(matches[1]), Number(matches[2])]
}

function onCellMouseDown(e) {
    mouseDown = true
    editingCell = e.target
    selectedStartCell = editingCell

    if (cellsMarked) clearMarkedCells()
}

function onCellMouseUp() {
    mouseDown = false
}

function onCellMouseOver(e) {
    if (editingCell !== e.target && mouseDown) {
        selectedEndCell = e.target
        cellsMarked = true
        setSelectedCells(getCellIndexes(selectedStartCell), (getCellIndexes(selectedEndCell)))
        // setAndMarkSelectedCells(getCellIndexes(selectedStartCell), (getCellIndexes(selectedEndCell)))
        clearMarkedCells()
        markCells()
    }
}

function createColumnHeader(tableSize, table) {
    let columnRow = $('<thead>')
    columnRow.append($('<th>'))

    for (let column = 0; column < tableSize; column++) {
        let tableHeader = $('<th>')
        tableHeader.text(String.fromCharCode(65 + column))
        columnRow.append(tableHeader)
    }

    $(table).prepend(columnRow)
}

function createRowHeader(tableSize, table) {
    $('tr', table).each( (index, element) => {
        let tableHeader = $('<th>')
        tableHeader.text(index + 1)
        $(element).prepend(tableHeader)
    })
}

function setSelectedCells(selectedStartIndexes, selectedEndIndexes) {
    let column1 = selectedStartIndexes[0]
    let row1 = selectedStartIndexes[1]
    let column2 = selectedEndIndexes[0]
    let row2 = selectedEndIndexes[1]

    if (column2 < column1) {
        let temp = column1;
        column1 = column2;
        column2 = temp;
    }
    if (row2 < row1) {
        let temp = row1;
        row1 = row2;
        row2 = temp;
    }

    selectedCells = []

    for (let column = 0; column < 10; column++) {
        for (let row = 0; row < 10; row++) {
            let cell = getCellFromID(column, row)
            if (column >= column1 && column <= column2 && row >= row1 && row <= row2) {
                selectedCells.push(cell)
            }
        }
    }
}

function markCells() {
    selectedCells.forEach((cell) => {
        $(cell).addClass('selected')
    })
}

function clearMarkedCells() {
    $('.selected').each((i, element) => {
        $(element).removeClass('selected')
    })
}

function mergeCells() {
    let numberOfRowsSelected = new Set()

    selectedCells.forEach((cell) => {
        let cellIndexes = getCellIndexes(cell)
        numberOfRowsSelected.add(cellIndexes[1])
    })

    if (numberOfRowsSelected.size > 1) alert("Cannot merge rows!")
    else {
        $(selectedCells[0]).attr('colspan', selectedCells.length)
        selectedCells.splice(1).forEach((cell) => {
            $(cell).css('display', 'none')
        })

        editingCell = selectedCells[0]
        clearMarkedCells()
    }
}

//TODO: Fokus og editing cell bliver ikke sat til leftmost
function demergeCells(mergedCells) {
    mergedCells.forEach((mergedCell) => {
        let cellWidth = $(mergedCell).attr('colspan')
        let cellIndexes = getCellIndexes(mergedCell)

        for (let i = 1; i < cellWidth; i++) {
            let cell = getCellFromID(cellIndexes[0] + i, cellIndexes[1])
            $(cell).css('display', '')
        }

        $(mergedCell).removeAttr('colspan')
    })
}















let activeCell = null

function activateCell(column, row) {
    if(activeCell != null && ("#tr-" + column + "-" + row) !== activeCell) {
        $(activeCell).prop("contenteditable", false)
        $(activeCell).removeClass("selected")
    }

    activeCell = "#tr-" + column + "-" + row;
    $(activeCell).prop("contenteditable", true);
    $(activeCell).addClass("selected");
}

function store(column, row) {
    array[column][row] = $("#tr-" + column + "-" + row).text();
}

function create2DArray(size) {
    let array = new Array(size)

    for (let i = 0; i < size; i++) {
        array[i] = new Array(size)
    }

    return array
}

function ID2POS(id) {
    let parts = id.split('-');
    return [parts[1], parts[2]];
}

