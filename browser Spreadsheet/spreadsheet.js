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
    setupMergeButton()
    setupBoldTextButton()
    setupCellAsHeaderButton()
    setupBlackBorderButton()
})

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
    cell.on('mousedown', () => onCellMouseDown())
    cell.on('mouseup', () => onCellMouseUp())
    cell.on('mouseover', (e) => onCellMouseOver(e))
    cell.on('focus', (e) => onCellFocus(e))
    cell.on('input', (e) => onCellInput(e))
    cell.on('focusout', (e) => onCellLosesFocus(e))

    return cell
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

function onCellMouseDown() {
    mouseDown = true
}

function onCellMouseUp() {
    mouseDown = false
}

function onCellMouseOver(e) {
    if (editingCell !== e.target && mouseDown) {
        selectedEndCell = e.target
        cellsMarked = true
        setSelectedCells(getCellIndexes(selectedStartCell), (getCellIndexes(selectedEndCell)))
        clearMarkedCells()
        markCells()
    }
}

function onCellFocus(e) {
    if (cellsMarked) clearMarkedCells()
    editingCell = e.target
    selectedStartCell = editingCell
    selectedCells = [editingCell]

    $('#input-bar').val($(e.target).text())
}

function onCellInput(e) {
    // let cell = $(e.target)
    //
    // //TODO: Virker ikke med ": ", tror ikke spacet blievr registeret korrekt. Måske brug nbsp eller sådan noget.
    // if (!$('span', cell).hasClass('hiddenCellText')) {
    //     if (cell.text().includes(":")) {
    //         let cellText = cell.text()
    //         let textToBeHidden = cellText.substr(0, cellText.indexOf(":") + 1)
    //         let hiddenText = $('<span class="hiddenCellText">')
    //         let newText = cellText.replace(textToBeHidden, "")
    //         cell.text(newText)
    //
    //         // cell.text(cellText.replace(textToBeHidden, "&nbsp;"))
    //         // hiddenText.text(textToBeHidden)
    //         cell.prepend(hiddenText)
    //         cell.append(document.createTextNode("NU"))
    //         // cell.append(newText)
    //
    //
    //
    //     }
    // }
    // console.log(cell.text())


    // $('#input-bar').val(cellText)

    // if (e.t)
}

function onCellLosesFocus(e) {
    let cell = $(e.target)
    let cellText = cell.text()

    //Match String mellemrum kolon mellemrum string
    //^[a-z0-9_]+:\K(?!//).*
    let regex = new RegExp("^[a-zA-Z0-9_]+ : [a-zA-Z0-9_]+")
    if (regex.test(cellText)) {
        let textToBeHidden = cellText.substr(0, cellText.indexOf(":") + 1)
        let hiddenText = $('<span class="hiddenCellText">')

        cell.text(cellText.replace(textToBeHidden, ""))
        hiddenText.text(textToBeHidden)
        cell.prepend(hiddenText)
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

function setupMergeButton() {
    $('#mergeButton').on('click', () => {
        //Used for when multiple merged cells are selected.
        let mergedCells = []

        selectedCells.forEach((cell) => {
            if ($(cell).attr('colspan') > 1) mergedCells.push(cell)
        })

        if ($(editingCell).attr('colspan') > 1) mergedCells.push(editingCell)

        if (mergedCells.length > 0) demergeCells(mergedCells)
        else mergeCells()
    }).on('mousedown',(e) => {
        //TODO: Virker ikke altid?
        //Doesn't remove focus from cell, when button is clicked.
        e.preventDefault()
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

function setupBoldTextButton() {
    $('#boldText').on('click', () => {
        let allCellsAreBold = true
        selectedCells.forEach((cell) => {
            if (!$(cell).hasClass('bold')) allCellsAreBold = false
        })

        if (allCellsAreBold) removeBoldText()
        else setBoldText()
    }).on('mousedown', (e) => {
        e.preventDefault()
    })
}

function setBoldText() {
    selectedCells.forEach((cell) => {
        $(cell).addClass('bold')
    })
}

function removeBoldText() {
    selectedCells.forEach((cell) => {
        $(cell).removeClass('bold')
    })
}

function setupCellAsHeaderButton() {
    $('#setCellAsHeader').on('click', () => {
        let allCellsAreHeaders = true
        selectedCells.forEach((cell) => {
            if (!$(cell).hasClass('header')) allCellsAreHeaders = false
        })
        if (allCellsAreHeaders) removeCellAsHeader()
        else setCellAsHeader()
    }).on('mousedown', (e) => {
        e.preventDefault()
    })
}

function setCellAsHeader() {
    selectedCells.forEach((cell) => {
        $(cell).addClass('header')
    })
}

function removeCellAsHeader() {
    selectedCells.forEach((cell) => {
        $(cell).removeClass('header')
    })
}

function setupBlackBorderButton() {
    $('#setBlackBorders').on('click', () => {
        let allCellsHaveBlackBorders = true
        selectedCells.forEach((cell) => {
            if (!$(cell).hasClass('blackBorder')) allCellsHaveBlackBorders = false
        })
        if (allCellsHaveBlackBorders) removeBlackBorder()
        else setBlackBorder()
    })
}

function setBlackBorder() {
    selectedCells.forEach((cell) => {
        $(cell).addClass('blackBorder')
        $(cell).css('background-color', 'black')
    })
}

function removeBlackBorder() {
    selectedCells.forEach((cell) => {
        $(cell).removeClass('blackBorder')
    })
}









function init() {
}

function initActionBar(container) {
}

function initInputBar(container) {
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

