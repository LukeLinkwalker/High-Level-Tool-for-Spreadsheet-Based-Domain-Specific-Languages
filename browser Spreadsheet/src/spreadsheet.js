import * as globals from './spreadsheetGlobalVariables.js'
import * as setup from './spreadsheetSetup.js'

export function createSpreadsheet() {
    globals.setColumnSize(30)
    globals.setRowSize(40)

    let table = $('<table>').attr('id', 'dynamicTable' + globals.spreadsheetType)
    let tableBody = $('<tbody>').attr('id', 'dynamicBody' + globals.spreadsheetType)

    for (let row = 0; row < globals.rowSize; row++) {
        let newRow = $('<tr>')

        for (let column = 0; column < globals.columnSize; column++) {
            newRow.append(createCell(column, row, globals.spreadsheetType))
        }
        tableBody.append(newRow)
    }

    createRowHeader(globals.rowSize, tableBody)
    createColumnHeader(globals.columnSize, table)
    table.append(tableBody)
    $('#cell-container-' + globals.spreadsheetType).append(table)
}

function createCell(column, row) {
    let cell = $('<td>')
    let divToContainBoxes = $('<div class="boxContainer">')

    divToContainBoxes.append(createInfoBox())
    divToContainBoxes.append(createErrorBox())
    cell.append(createCellText())
    cell.append(divToContainBoxes)
    cell.attr('id', createCellID(column, row))
    setup.setupCell(cell)

    return cell
}

function createCellText() {
    let cellText = $('<div class="cellText">')
    cellText.attr('contenteditable', 'true')
    setup.setupCellTextDiv(cellText)

    return cellText
}

function createInfoBox() {
    return $('<div class="infoBox box">')
}

function createErrorBox() {
    return $('<div class="errorBox box">')
}

function createColumnHeader(tableSize, table) {
    let columnRow = $('<thead>')

    for (let column = 0; column <= tableSize; column++) {
        let tableHeader = $('<th>')

        if (column !== 0) tableHeader.text(createTableHeaderText(column))
        tableHeader.on('mousedown', (e) => e.preventDefault())
        columnRow.append(tableHeader)
    }

    $(table).prepend(columnRow)
}

function createTableHeaderText(columnNumber) {
    let temp
    let letter = ''

    while (columnNumber > 0) {
        temp = (columnNumber - 1) % 26
        letter = String.fromCharCode(temp + 65) + letter
        columnNumber = (columnNumber - temp - 1) / 26
    }

    return letter
}

function createRowHeader(tableSize, table) {
    $('tr', table).each( (index, element) => {
        let tableHeader = $('<th>')

        tableHeader.text(index + 1)
        tableHeader.on('mousedown', (e) => e.preventDefault())
        $(element).prepend(tableHeader)
    })
}

export function createCellID(column, row) {
    return 'cell-' + column + '-' + row + '-' + globals.spreadsheetType
}

export function getCellFromIndexes(column, row) {
    return $('#' + createCellID(column, row))[0]
}

export function getCellIndexes(cell) {
    let cellID = $(cell).attr('id')
    let matches = cellID.match(/^cell-(\d+)-(\d+)/)

    if (matches) return [Number(matches[1]), Number(matches[2])]
}

export function setInitialEditingCell() {
    let cell = getCellFromIndexes(0, 0)
    let cellTextDiv = getCellTextDiv(cell)

    globals.setEditingCell(cell)
    globals.setCurrentColumn(0)
    globals.setCurrentRow(0)
    cellTextDiv.focus()
}

export function findSelectedCells(selectedStartIndexes, selectedEndIndexes) {
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

    globals.setSelectedCells([])

    for (let column = 0; column < globals.columnSize; column++) {
        for (let row = 0; row < globals.rowSize; row++) {
            let cell = getCellFromIndexes(column, row)

            if (column >= column1 && column <= column2 && row >= row1 && row <= row2) {
                globals.selectedCells.push(cell)
            }
        }
    }
}

export function getCellsInRange(startCell, endCell) {
    let startCellIndexes = getCellIndexes(startCell)
    let endCellIndexes = getCellIndexes(endCell)
    let cellsInRange = []

    for (let i = startCellIndexes[0]; i <= endCellIndexes[0] ; i++) {
        for (let j = startCellIndexes[1]; j <= endCellIndexes[1]; j++) {
            cellsInRange.push(getCellFromIndexes(i, j))
        }
    }

    return cellsInRange
}

export function checkCellIsEmpty(cell) {
    let cellType = getCellType(cell)
    return getCellText(cell) === '' && cellType === 'normal'
}

export function createTableName(column, row) {
    let cellID = createCellID(column, row)
    return 'table-' + cellID
}

export function getTableName(cell) {
    let classNames = $(cell).attr('class')

    if (classNames !== undefined) {
        let classNamesArray = classNames.split(/\s+/)
        let tableName = undefined

        classNamesArray.forEach((className) => {
            if (className.startsWith('table-cell-')) tableName = className
        })

        if (tableName === undefined) return null
        else return tableName
    }
    else return null
}

export function addTableNameToCell(cell, tableName) {
    $(cell).addClass(tableName)
}

export function removeCellFromTable(cell) {
    $(cell).removeClass(getTableName(cell))
}

export function getAllCellsFromTableCellIsIn(cell) {
    let tableName = getTableName(cell)

    if (tableName === null) return null
    else return $('.' + tableName).get()
}

export function getTableRange(tableCells) {
    let startCell = null
    let endCell = null

    if (tableCells !== null) {
        tableCells.forEach((cell) => {
            if (startCell === null && endCell === null) {
                startCell = cell
                endCell = cell
            }

            let startCellIndexes = getCellIndexes(startCell)
            let endCellIndexes = getCellIndexes(endCell)
            let cellIndexes = getCellIndexes(cell)

            if (cellIndexes[0] <= startCellIndexes[0] && cellIndexes[1] <= startCellIndexes[1]) startCell = cell
            if (cellIndexes[0] >= endCellIndexes[0] && cellIndexes[1] >= endCellIndexes[1]) endCell = cell
        })

        let startCellIndexes = getCellIndexes(startCell)
        let endCellIndexes = getCellIndexes(endCell)

        return [startCellIndexes[0], startCellIndexes[1], endCellIndexes[0], endCellIndexes[1]]
    }
    else return null
}

export function getMergedCells(cell) {
    let mergedCellClassName = getMergedCellsName(cell)

    if (mergedCellClassName === null) return null
    else return $('.' + getMergedCellsName(cell))
}

export function getMergedCellsName(cell) {
    let classNames = $(cell).attr('class')

    if (classNames !== undefined) {
        let classNamesArray = classNames.split(/\s+/)
        let mergedCellClassName = null

        classNamesArray.forEach((className) => {
            if (className.startsWith('merged-cell-')) mergedCellClassName = className
        })

        if (mergedCellClassName === null) return null
        else return mergedCellClassName
    }

    else return null
}

export function getCellsInNewTableRow(cell) {
    let tableCells = getAllCellsFromTableCellIsIn(cell)
    let tableRange = getTableRange(tableCells)

    if (tableRange === null) return null
    else {
        let newRowStartCell = getCellFromIndexes(tableRange[0], tableRange[3] + 1)
        let newRowEndCell = getCellFromIndexes(tableRange[2], tableRange[3] + 1)

        return getCellsInRange(newRowStartCell, newRowEndCell)
    }
}

export function setCellAsHeader(cell) {
    $(cell).addClass('header')
}

export function removeCellAsHeader(cell) {
    $(cell).removeClass('header')
}

export function setCellAsData(cell) {
    $(cell).addClass('data')
}

export function removeCellAsData(cell) {
    $(cell).removeClass('data')
}

export function getCellType(cell) {
    let $cell = $(cell)

    if ($cell.hasClass('header')) return 'header'
    else if ($cell.hasClass('data')) return 'data'
    else return 'normal'
}

export function getInfoBox(cell) {
    return $('.infoBox', cell)[0]
}

export function getErrorBox(cell) {
    return $('.errorBox', cell)[0]
}

export function getCellTextDiv(cell) {
    return $('.cellText', cell)[0]
}

export function getCellText(cell) {
    return $(getCellTextDiv(cell)).text()
}

export function setCellText(cell, value) {
    $(getCellTextDiv(cell)).text(value)

    if (cell === globals.editingCell) {
        let inputBar = $('#input-bar')
        inputBar.val(value)
    }
}

export function getCellFromCellTextDiv(cellTextDiv) {
    return $(cellTextDiv).parent()[0]
}

export function insertNewMessageInInfoBox(infoBox, value) {
    let currentText = $(infoBox).text()

    if (currentText === '') $(infoBox).text(value)
    else $(infoBox).text(currentText + '\n' + value)
}

export function insertNewMessageInErrorBox(errorBox, value) {
    let currentText = $(errorBox).text()

    if (currentText === '') $(errorBox).text(value)
    else $(errorBox).text(currentText + '\n' + value)
}

export function getBreakoutTableCells(cell) {
    let width = $(cell).prop('colspan')
    let cellIndexes = getCellIndexes(cell)
    let breakoutTableCells = []
    let tableName = getTableName(cell)

    for (let i = cellIndexes[0]; i < width + cellIndexes[0]; i++) {
        let nextCell = getCellFromIndexes(i, cellIndexes[1])
        let nextCellTableName = getTableName(nextCell)

        while (nextCellTableName === tableName) {
            breakoutTableCells.push(nextCell)

            let nextCellIndexes = getCellIndexes(nextCell)
            nextCell = getCellFromIndexes(i, nextCellIndexes[1] + 1)
            nextCellTableName = getTableName(nextCell)
        }
    }

    return breakoutTableCells
}

export function getBreakoutOutlineCells(cell) {
    let breakoutTableRange = getTableRange(globals.breakoutTableCells)
    let headerCellIndexes = getCellIndexes(globals.breakoutTableCells[0])
    let currentCellIndexes = getCellIndexes(cell)
    let headerAndCurrentColumnDifference = currentCellIndexes[0] - headerCellIndexes[0]
    let headerAndCurrentRowDifference = currentCellIndexes[1] - headerCellIndexes[1]
    let startCell = getCellFromIndexes(breakoutTableRange[0] + headerAndCurrentColumnDifference,
        breakoutTableRange[1] + headerAndCurrentRowDifference)
    let endCell = getCellFromIndexes(breakoutTableRange[2] + headerAndCurrentColumnDifference,
        breakoutTableRange[3] + headerAndCurrentRowDifference)

    return getCellsInRange(startCell, endCell)
}

export function checkTableHasNameAttribute(breakoutTableCells) {
    let headerRow = getCellIndexes(breakoutTableCells[0])[1]
    let tableHasNameAttribute = false

    breakoutTableCells.forEach((boCell) => {
        let boCellIndexes = getCellIndexes(boCell)
        let cellText = getCellText(boCell).toLowerCase()

        if (boCellIndexes[1] === headerRow + 1 && cellText === 'name') tableHasNameAttribute = true
    })

    return tableHasNameAttribute
}

export function checkHeaderCellIsHeaderForWholeTable(cell) {
    let headerCellTableName = getTableName(cell)
    let headerCellIndexes = getCellIndexes(cell)
    let cellAboveHeader = getCellFromIndexes(headerCellIndexes[0], headerCellIndexes[1] - 1)
    let cellAboveHeaderTableName = getTableName(cellAboveHeader)

    return headerCellTableName !== cellAboveHeaderTableName;
}

export function createNewTableNameForCopyingCell(oldCell, newCell) {
    let cellIndexesFromTableName = getCellIndexesFromTableName(getTableName(oldCell))
    let newTableIndexes = getIndexDifferencesForNewCellWhenCopying(oldCell, newCell, cellIndexesFromTableName)

    return createTableName(newTableIndexes[0], newTableIndexes[1])
}

export function createNewMergedCellsNameForCopyingCell(oldCell, newCell) {
    let cellIndexesFromMergedCellsName = getCellIndexesFromMergedCellsName(getMergedCellsName(oldCell))
    let newMergedCellsIndexes = getIndexDifferencesForNewCellWhenCopying(oldCell, newCell, cellIndexesFromMergedCellsName)

    return createMergedCellsName(newMergedCellsIndexes[0], newMergedCellsIndexes[1])
}

export function getIndexDifferencesForNewCellWhenCopying(oldCell, newCell, cellIndexesFromName) {
    let oldCellIndexes = getCellIndexes(oldCell)
    let newCellIndexes = getCellIndexes(newCell)
    let columnDifference = newCellIndexes[0] - oldCellIndexes[0]
    let rowDifference = newCellIndexes[1] - oldCellIndexes[1]
    let newColumn = cellIndexesFromName[0] + columnDifference
    let newRow = cellIndexesFromName[1] + rowDifference

    return [newColumn, newRow]
}

export function getCellIndexesFromTableName(tableName) {
    let matches = tableName.match(/^table-cell-(\d+)-(\d+)/)
    return [Number(matches[1]), Number(matches[2])]
}

export function getCellIndexesFromMergedCellsName(mergedCellsName) {
    let matches = mergedCellsName.match(/^merged-cell-(\d+)-(\d+)/)
    return [Number(matches[1]), Number(matches[2])]
}

export function createMergedCellsName(column, row) {
    let cellID = createCellID(column, row)
    return 'merged-' + cellID
}