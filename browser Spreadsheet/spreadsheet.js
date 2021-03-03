let array = null;

let editingCell = null
let mouseDown = false
let cellsMarked = false
let selectedCells = []
let selectedStartCell, selectedEndCell
let columnSize, rowSize

let errorMessage = "ERROR!"
let errorCellIndexes = [0, 0]
let errorLineIndexes = [1, 3]

function init() {
    initActionBar()
    initInputBar()
    initCells()
}

function initActionBar(container) {
    setupMergeButton()
    setupBoldTextButton()
    setupCellAsHeaderButton()
    setupBlackBorderButton()

    //TODO: Remove when showError works
    $('#testErrorButton').on('click', () => {
        showError(errorCellIndexes, errorLineIndexes, errorMessage)
    })

    $('#removeErrorButton').on('click', () => {
        removeError(editingCell)
    })
}

function initInputBar(container) {
    setupInputBar()
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
    cell.data('hasError', false)
    cell.data('hiddenText', '')
    cell.on('mousedown', () => onCellMouseDown())
    cell.on('mouseup', () => onCellMouseUp())
    //Have changed this from mouseover to mouseenter - don't know if it breaks anything? Change onCellMouseEnter name aswell
    cell.on('mouseenter', (e) => onCellMouseEnter(e))
    cell.on('mouseleave', (e) => onCellMouseLeave(e))
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

function setupInputBar() {
    $('#input-bar')
        .on('input', (e) => onInputBarInput(e))
    //TODO: Virker ikke ordenligt, da markøren ikke flytter op i inputbare, og det skal den.
        // .on('mousedown', (e) => e.preventDefault())
}

function onInputBarInput(e) {
    let inputBar = $(e.target)
    let inputBarText = String(inputBar.val())
    let regex = new RegExp('^[a-zA-Z0-9_]+ : [a-zA-Z0-9_]+')
    let chosenCell = $(inputBar.data('chosenCell'))

    if (!regex.test(inputBarText)) {
        chosenCell.text(inputBarText)
        chosenCell.removeData('hiddenText')
    }
    else {
        let textToBeHidden = inputBarText.substr(0, inputBarText.indexOf(":") + 2)
        chosenCell.data('hiddenText', textToBeHidden)
        chosenCell.text(inputBarText.replace(textToBeHidden, ""))
    }
}

function onCellMouseDown() {
    mouseDown = true
}

function onCellMouseUp() {
    mouseDown = false
}

function onCellMouseEnter(e) {
    if (editingCell !== e.target && mouseDown) {
        selectedEndCell = e.target
        cellsMarked = true
        setSelectedCells(getCellIndexes(selectedStartCell), (getCellIndexes(selectedEndCell)))
        clearMarkedCells()
        markCells()
    }

    let cell = $(e.target)
    if (cell.data('hasError')) showErrorMessage(e.target, errorMessage)
}

function onCellMouseLeave(e) {
    let cell = $(e.target)
    if (cell.data('hasError')) hideErrorMessage(e.target)
}

function onCellFocus(e) {
    if (cellsMarked) clearMarkedCells()
    editingCell = e.target
    selectedStartCell = editingCell
    selectedCells = [editingCell]

    let cell = $(e.target)
    let hiddenText = cell.data('hiddenText')
    let inputBar = $('#input-bar')

    if (hiddenText !== undefined) inputBar.val(hiddenText + cell.text())
    else inputBar.val(cell.text())

    inputBar.data('chosenCell', editingCell)
}

function onCellInput(e) {
    let cell = $(e.target)
    let inputBar = $('#input-bar')
    let regex = new RegExp('^[a-zA-Z0-9_]')
    let hiddenText = cell.data('hiddenText')

    if (!regex.test(cell.text())) cell.removeData('hiddenText')

    if (hiddenText !== undefined && !cell.data('hasError')) inputBar.val(hiddenText + cell.text())
    else inputBar.val(cell.text())
}

//TODO: Måske fjerne cellChosen når en celle msiter fokus? Eller den bliver jo nok bare overwritten, men kan den miste fokus uden en anden skal vælges???
function onCellLosesFocus(e) {
    let cell = $(e.target)
    let cellText = cell.text()
    let hiddenText = cell.data('hiddenText')

    let regex = new RegExp('^[a-zA-Z0-9_]+ : [a-zA-Z0-9_]+')
    if (regex.test(cellText) && hiddenText === undefined && !cell.data('hasError')) {
        let textToBeHidden = cellText.substr(0, cellText.indexOf(":") + 2)
        cell.data('hiddenText', textToBeHidden)
        cell.text(cellText.replace(textToBeHidden, ""))
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
    $('#mergeButton')
        .on('click', () => {
        //Used for when multiple merged cells are selected.
        let mergedCells = []

        selectedCells.forEach((cell) => {
            if ($(cell).attr('colspan') > 1) mergedCells.push(cell)
        })

        if ($(editingCell).attr('colspan') > 1) mergedCells.push(editingCell)

        if (mergedCells.length > 0) demergeCells(mergedCells)
        else mergeCells()
    })
        .on('mousedown',(e) => e.preventDefault())
    //TODO: Virker ikke altid?
    //Doesn't remove focus from cell, when button is clicked.
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
    $('#boldText')
        .on('click', () => {
        let allCellsAreBold = true

        selectedCells.forEach((cell) => {
            if (!$(cell).hasClass('bold')) allCellsAreBold = false
        })

        if (allCellsAreBold) removeBoldText()
        else setBoldText()
    })
        .on('mousedown', (e) => e.preventDefault())
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
    $('#setCellAsHeader')
        .on('click', () => {
        let allCellsAreHeaders = true

        selectedCells.forEach((cell) => {
            if (!$(cell).hasClass('header')) allCellsAreHeaders = false
        })

        if (allCellsAreHeaders) removeCellAsHeader()
        else setCellAsHeader()
    })
        .on('mousedown', (e) => e.preventDefault())
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
    })
}

function removeBlackBorder() {
    selectedCells.forEach((cell) => {
        $(cell).removeClass('blackBorder')
    })
}

function showError(errorCellIndexes, errorLineIndexes) {
    let cell = $(getCellFromID(errorCellIndexes[0], errorCellIndexes[1]))
    let errorText = cell.text().substring(errorLineIndexes[0], errorLineIndexes[1])

    cell.data('hasError', true)
    cell.html(cell.html().replace(errorText, '<span class="error">' + errorText + '</span>'))

    createErrorMessage(cell, errorMessage)
}

function removeError(cell) {
    let textWithSpanRemoved = $(cell).html().replace('<span class="error">', "").replace('</span>', "")

    $(cell).data('hasError', false)
    $(cell).html(textWithSpanRemoved)

    removeErrorMessage(cell)
}

function showErrorMessage(cell) {
    let errorMessage = $('div.errorMessage', cell)
    errorMessage.css('visibility', 'visible')
}

function hideErrorMessage(cell) {
    let errorMessage = $('div.errorMessage', cell)
    errorMessage.css('visibility', 'hidden')
}

function createErrorMessage(cell, errorMessage) {
    let div = $('<div/>')
    div.addClass('errorMessage')
    div.text(errorMessage)
    div.prop('contenteditable', false)
    cell.append(div)
}

function removeErrorMessage(cell) {
    let errorMessage = $('div.errorMessage', cell)
    errorMessage.remove()
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

