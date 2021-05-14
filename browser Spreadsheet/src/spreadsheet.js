import * as globals from './spreadsheetGlobalVariables.js'
import * as setup from './spreadsheetSetup.js'
import * as client from './ssclient.js'

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
    setFocusOnCell(cell)
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
    return getSpecificClassFromCell(cell, 'table-cell-')
}

export function createBreakoutDataCellReference(column, row) {
    let cellID = createCellID(column, row)
    return 'breakout-data-cell-reference-' + cellID
}

export function getBreakoutDataCellReference(cell) {
    return getSpecificClassFromCell(cell, 'breakout-data-cell-reference-')
}

export function createBreakoutReferenceToOriginalTable(cell) {
    let tableHeader = findTableHeader(cell)
    let cellIndexes = getCellIndexes(tableHeader)
    return 'breakout-from-' + createTableName(cellIndexes[0], cellIndexes[1])
}

export function getBreakoutReferenceToOriginalTable(cell) {
    return getSpecificClassFromCell(cell, 'breakout-from-')
}

export function removeBreakoutReferenceToOriginalTable(cell) {
    $(cell).removeClass(getBreakoutReferenceToOriginalTable(cell))
}

function getSpecificClassFromCell(cell, specificClassName) {
    let classNames = $(cell).attr('class')

    if (classNames !== undefined) {
        let classNamesArray = classNames.split(/\s+/)
        let tableName = undefined

        classNamesArray.forEach((className) => {
            if (className.startsWith(specificClassName)) tableName = className
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

export function setCellText(cell, value, sendChange) {
    let cellTextDiv = getCellTextDiv(cell)

    if ($(cellTextDiv).is(":focus")) {
        let caret = getCaretPosition(cellTextDiv)

        $(getCellTextDiv(cell)).text(value)
        setCaretPosition(cellTextDiv, caret)
    }
    else $(getCellTextDiv(cell)).text(value)

    if (cell === globals.editingCell) {
        let inputBar = $('#input-bar')
        inputBar.val(value)
    }

    if (sendChange) client.sendChange(cell)
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

    if (currentText === '') setErrorBoxText(errorBox, value)
    else setErrorBoxText(errorBox, currentText + '\n' + value)
}

export function setErrorBoxText(errorBox, text) {
    $(errorBox).text(text)
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

export function getNewTableHeaderForCopyingCell(oldCell, newCell, headerCell) {
    let headerCellIndexes = getCellIndexes(headerCell)
    let newTableIndexes = getIndexDifferencesForHeaderAndMergeCellNamesForWhenCopying(oldCell, newCell, headerCellIndexes)

    return getCellFromIndexes(newTableIndexes[0], newTableIndexes[1])
}

export function createNewMergedCellsNameForCopyingCell(oldCell, newCell) {
    let cellIndexesFromMergedCellsName = getCellIndexesFromMergedCellsName(getMergedCellsName(oldCell))
    let newMergedCellsIndexes = getIndexDifferencesForHeaderAndMergeCellNamesForWhenCopying(oldCell, newCell, cellIndexesFromMergedCellsName)

    return createMergedCellsName(newMergedCellsIndexes[0], newMergedCellsIndexes[1])
}

export function getIndexDifferencesForHeaderAndMergeCellNamesForWhenCopying(oldCell, newCell, cellIndexes) {
    let oldCellIndexes = getCellIndexes(oldCell)
    let newCellIndexes = getCellIndexes(newCell)
    let columnDifference = newCellIndexes[0] - oldCellIndexes[0]
    let rowDifference = newCellIndexes[1] - oldCellIndexes[1]
    let newColumn = cellIndexes[0] + columnDifference
    let newRow = cellIndexes[1] + rowDifference

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

export function getTableCellsAsRows(tableCells) {
    let tableRange = getTableRange(tableCells)
    let headerRows = []

    for (let i = tableRange[1]; i <= tableRange[3]; i++) {
        let headerRow = []

        tableCells.forEach((cell) => {
            let cellIndexes = getCellIndexes(cell)
            if (cellIndexes[1] === i) headerRow.push(cell)
        })

        headerRows.push(headerRow)
    }

    return headerRows
}

export function setFocusOnCell(cell) {
    let cellTextDiv = getCellTextDiv(cell)
    let cellIndexes = getCellIndexes(cell)

    globals.setEditingCell(cell)
    globals.setCurrentColumn(cellIndexes[0])
    globals.setCurrentRow(cellIndexes[1])
    cellTextDiv.focus()
}

export function findBrokenOutTableCells(cell) {
    let breakoutTableName = createBreakoutReferenceToOriginalTable(cell)
    return $('.' + breakoutTableName).get()
}

export function findTableHeader(cell) {
    return getAllCellsFromTableCellIsIn(cell)[0]
}

export function getCaretPosition(element) {
    let position = 0
    let isSupported = typeof window.getSelection !== 'undefined'

    if (isSupported) {
        let selection = window.getSelection()

        if (selection.rangeCount !== 0) {
            let range = window.getSelection().getRangeAt(0)
            let preCaretRange = range.cloneRange()

            preCaretRange.selectNodeContents(element)
            preCaretRange.setEnd(range.endContainer, range.endOffset)

            position = preCaretRange.toString().length
        }
    }
    return position
}

export function setCaretPosition(element, position){
    for (let node of element.childNodes){
        if (node.nodeType === 3) {
            if (node.length >= position) {
                let range = document.createRange()
                let selection = window.getSelection()

                range.setStart(node, position)
                range.collapse(true)
                selection.removeAllRanges()
                selection.addRange(range)
                return -1
            } else position -= node.length
        } else {
            position = setCaretPosition(node, position);
            if (position === -1) return -1
        }
    }
    return position
}

export function markAsBrokenOut(cell) {
    $(cell).addClass('brokenOutNameCell')
}

export function removeMarkAsBrokenOut(cell) {
    $(cell).removeClass('brokenOutNameCell')
}

export function getIsBrokenOut(cell) {
    return $(cell).hasClass('brokenOutNameCell')
}

export function findNameAttributeHeaderInColumnForCell(cell) {
    let nextCell = cell
    let nextCellType = getCellType(nextCell)
    let nextCellText = getCellText(nextCell)

    while (!(nextCellType === 'header' && nextCellText.toLowerCase() === 'name')) {
        let nextCellIndexes = getCellIndexes(nextCell)
        nextCell = getCellFromIndexes(nextCellIndexes[0], nextCellIndexes[1] - 1)
        nextCellType = getCellType(nextCell)
        nextCellText = getCellText(nextCell)
    }

    return nextCell
}

export function findFirstDataCellForHeaderCell(cell) {
    let nextCell = cell
    let nextCellType = getCellType(cell)

    while (nextCellType !== 'data') {
        let nextCellIndexes = getCellIndexes(nextCell)
        nextCell = getCellFromIndexes(nextCellIndexes[0], nextCellIndexes[1] + 1)
        nextCellType = getCellType(nextCell)
    }

    return nextCell
}

export function getBreakoutReferenceCell(cell) {
    let cellIndexes = getCellIndexes(cell)
    let nameAttributeHeaderOriginalTable = findNameAttributeHeaderInColumnForCell(cell)
    let firstDataCellOriginalTable = findFirstDataCellForHeaderCell(nameAttributeHeaderOriginalTable)
    let firstDataCellOriginalTableIndexes = getCellIndexes(firstDataCellOriginalTable)
    let rowDifferenceFromNameAttributeHeader = cellIndexes[1] - firstDataCellOriginalTableIndexes[1]

    let breakoutReference = createBreakoutReferenceToOriginalTable(cell)
    let breakoutCells = $('.' + breakoutReference).get()
    let rows = getTableCellsAsRows(breakoutCells)
    let firstRow = rows[1]
    let nameAttributeHeaderBreakoutTable = firstRow.filter((cell) => getCellText(cell).toLowerCase() ===
        'name')
    let firstDataCellBreakoutTable = findFirstDataCellForHeaderCell(nameAttributeHeaderBreakoutTable)
    let firstDataCellBreakoutTableIndexes = getCellIndexes(firstDataCellBreakoutTable)

    return  getCellFromIndexes(firstDataCellBreakoutTableIndexes[0],
        firstDataCellBreakoutTableIndexes[1] + rowDifferenceFromNameAttributeHeader)
}