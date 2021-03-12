import * as globals from './spreadsheetGlobalVariables.js'
import * as tools from './spreadsheetTools.js'
import * as spreadsheet from './spreadsheet.js'
import * as client from './ssclient.js'
import * as setup from './spreadsheetSetup.js'

export function onInputBarInput(inputBar) {
    let inputBarText = String($(inputBar).val())
    let regex = new RegExp('^[a-zA-Z0-9_]+ : [a-zA-Z0-9_]+')
    let $editingCell = $(globals.editingCell)

    if (!regex.test(inputBarText)) {
        $editingCell.text($editingCell.data('hiddenText') + inputBarText)
        $editingCell.data('hiddenText', '')
    }
    else {
        let textToBeHidden = inputBarText.substr(0, inputBarText.indexOf(':') + 2)
        $editingCell.data('hiddenText', textToBeHidden)
        $editingCell.text(inputBarText.replace(textToBeHidden, ''))
    }

    client.sendChange(globals.editingCell)
}

export function onInputBarFocus() {
    $(globals.editingCell).css('outline', 'royalblue auto')
    $('#input-bar').css('outline', 'none')
}

export function onInputBarFocusOut() {
    $(globals.editingCell).css('outline', '')
}

export function onCellMouseDown() {
    globals.setMouseDown(true)
}

export function onCellMouseUp() {
    globals.setMouseDown(false)
}

export function onCellMouseEnter(cell) {
    if (globals.editingCell !== cell && globals.mouseDown) {
        globals.setSelectedEndCell(cell)

        let startCellIndexes = spreadsheet.getCellIndexes(globals.selectedStartCell)
        let endCellIndexes = spreadsheet.getCellIndexes(globals.selectedEndCell)

        spreadsheet.findSelectedCells(startCellIndexes, endCellIndexes)
        spreadsheet.clearMarkedCells()
        spreadsheet.markCells()
    }

    if ($(cell).data('hasError')) tools.showErrorMessage(cell, globals.errorMessage)
}

export function onCellMouseLeave(cell) {
    if ($(cell).data('hasError')) tools.hideErrorMessage(cell)
}

export function onCellFocus(cell) {
    if (globals.cellsMarked) spreadsheet.clearMarkedCells()

    globals.setEditingCell(cell)
    globals.setSelectedStartCell(cell)
    globals.setSelectedCells([cell])

    let hiddenText = $(cell).data('hiddenText')
    let inputBar = $('#input-bar')
    let cellClone = $(cell).clone()
    let divs = $('.errorMessage', cellClone)

    divs.each((i, element) => {
        element.remove()
    })

    inputBar.val(hiddenText + cellClone.text())
}

export function onCellFocusOut(cell) {
    let $cell = $(cell)
    let cellText = $cell.text()
    let hiddenText = $cell.data('hiddenText')

    let regex = new RegExp('^[a-zA-Z0-9_]+ : [a-zA-Z0-9_]+')
    if (regex.test(cellText) && hiddenText === '' && !$cell.data('hasError')) {
        let textToBeHidden = cellText.substr(0, cellText.indexOf(':') + 2)
        $cell.data('hiddenText', textToBeHidden)
        $cell.text(cellText.replace(textToBeHidden, ''))
    }
}

export function onCellInput(cell) {
    let $cell = $(cell)
    let inputBar = $('#input-bar')
    let regex = new RegExp('^[a-zA-Z0-9_]')
    let hiddenText = $cell.data('hiddenText')
    let cellClone = $(cell).clone()
    let divs = $('.errorMessage', cellClone)

    divs.each((i, element) => {
        element.remove()
    })

    if (!regex.test(cellClone.text())) $cell.data('')

    if (hiddenText !== '' && !$cell.data('hasError')) inputBar.val(hiddenText + cellClone.text())
    else inputBar.val(cellClone.text())

    client.sendChange(cell)
}

// // TODO: Focus and editing is not set to the leftmost.
// export function onMergeButtonClick() {
//     let mergedCells = []
//     let numberOfRowsSelected = new Set()
//     let allCellsAreEmpty = globals.selectedCells.every((cell) => { return spreadsheet.checkCellIsEmpty(cell) })
//
//     globals.selectedCells.forEach((cell) => {
//         if ($(cell).attr('colspan') > 1) mergedCells.push(cell)
//     })
//
//     if ((mergedCells.length > 1) || (mergedCells.length === 1 && globals.selectedCells.length > 1)) {
//         if (!allCellsAreEmpty) {
//             let warning = confirm('Only the left most value will be kept, if the cells are merged. Do you want to merge anyway?')
//
//             if (warning) {
//                 mergedCells.forEach((cell) => tools.demergeCell(cell))
//                 tools.mergeCells(globals.selectedCells)
//             }
//         }
//         else {
//             mergedCells.forEach((cell) => tools.demergeCell(cell))
//             tools.mergeCells(globals.selectedCells)
//             }
//         }
//     else if (mergedCells.length === 1) tools.demergeCell(mergedCells)
//     else if (globals.selectedCells.length > 1) {
//         if (numberOfRowsSelected > 1)alert('Cannot merge rows!')
//         else {
//             if (!allCellsAreEmpty) {
//                 let warning = confirm('Only the left most value will be kept, if the cells are merged. Do you want to merge anyway?')
//
//                 if (warning) tools.mergeCells(globals.selectedCells)
//             }
//             else tools.mergeCells(globals.selectedCells)
//         }
//     }
//         //TODO: Maybe remove alert and grey out button instead, if we even need the button or alert.
//     else alert('Select at least two cells!')
//
//     globals.setEditingCell(globals.selectedCells[0])
//     globals.editingCell.focus()
//     spreadsheet.clearMarkedCells()
// }

// export function onBoldTextButtonClick() {
//     let allCellsAreBold = globals.selectedCells.every((cell) => {
//         return $(cell).hasClass('bold')
//     })
//
//     if (allCellsAreBold) globals.selectedCells.forEach((cell) => tools.removeBoldText(cell))
//     else globals.selectedCells.forEach((cell) => tools.setBoldText(cell))
// }
//
// export function onCenterTextButtonClick() {
//     let allCellsAreCentered = globals.selectedCells.every((cell) => {
//         return $(cell).hasClass('center')
//     })
//
//     if (allCellsAreCentered) globals.selectedCells.forEach((cell) => tools.removeCenterText(cell))
//     else globals.selectedCells.forEach((cell) => tools.setCenterText(cell))
// }
//
// export function onCellAsHeaderButtonClick() {
//     let allCellsAreHeaders = globals.selectedCells.every((cell) => {
//         return $(cell).hasClass('header')
//     })
//
//     if (allCellsAreHeaders) globals.selectedCells.forEach((cell) => tools.removeCellAsHeader(cell))
//     else globals.selectedCells.forEach((cell) => tools.setCellAsHeader(cell))
// }
//
// export function onCellAsDataButtonClick() {
//     let allCellsAreData = globals.selectedCells.every((cell) => {
//         return $(cell).hasClass('data')
//     })
//
//     if (allCellsAreData) globals.selectedCells.forEach((cell) => tools.removeCellAsData(cell))
//     else globals.selectedCells.forEach((cell) => tools.setCellAsData(cell))
// }
//
// export function onBlackBorderButtonClick() {
//     let allCellsHaveBlackBorders = globals.selectedCells.every((cell) => {
//         return $(cell).hasClass('blackBorder')
//     })
//
//     if (allCellsHaveBlackBorders) globals.selectedCells.forEach((cell) => tools.removeBlackBorder(cell))
//     else globals.selectedCells.forEach((cell) => tools.setBlackBorder(cell))
// }

export function onDocumentReady() {
    setup.setupKeys()
    setup.setupSpreadsheetTypeRadioButtons()
    // setup.setupMergeButton()
    // setup.setupBoldTextButton()
    // setup.setupCenterTextButton()
    // setup.setupCellAsHeaderButton()
    // setup.setupCellAsDataButton()
    // setup.setupBlackBorderButton()
    setup.setupCreateTableButton()
    setup.setupInputBar()
    setup.setupSDSL()

    spreadsheet.createTable()
    spreadsheet.setInitialEditingCell()

    $('#sdslRadioButton').prop('checked', true)

    //TODO: Remove after testing
    spreadsheet.testFunction()
}

export function onDocumentKeypressTab(event) {
    let editingCellIndexes = spreadsheet.getCellIndexes(globals.editingCell)

    event.preventDefault()
    $(globals.editingCell).css('outline', '')

    if (editingCellIndexes[0] < globals.columnSize - 1) {
        let newEditingCell = spreadsheet.getCellFromID(editingCellIndexes[0] + 1, editingCellIndexes[1])

        globals.setEditingCell(newEditingCell)
        newEditingCell.focus()
    }
}

export function onDocumentKeypressEnter(event) {
    let editingCellIndexes = spreadsheet.getCellIndexes(globals.editingCell)

    event.preventDefault()
    $(globals.editingCell).css('outline', '')

    if (editingCellIndexes[1] < globals.rowSize - 1) {
        let newEditingCell = spreadsheet.getCellFromID(editingCellIndexes[0], editingCellIndexes[1] + 1)

        globals.setEditingCell(newEditingCell)
        newEditingCell.focus()
    }
}

export function onCreateTableButtonClick() {
    //TODO: Connect to server. Remove the following when it's working. Maybe not done correctly.
    let tableRangeCells = client.testSendToServer('getTableRange')
    let allTableCellsAreEmpty = tableRangeCells.every((cell) => {
        return spreadsheet.checkCellIsEmpty(cell)
    })

    //TODO: Change positions based on the current cell.
    if (allTableCellsAreEmpty) client.testSendToServer('createTable')
    else {
        let warning = confirm('Some cells are not empty. Their data will be overwritten. Do you still wish to create a table?')

        if (warning) {
            tableRangeCells.forEach((cell) => spreadsheet.clearCell(cell))

            client.testCreateTable()
        }
    }
}

export function onSpreadsheetTypeRadioButtonsChange() {
    let spreadsheetType = $('input[name="spreadsheetType"]:checked').val()

    if (spreadsheetType === 'sdsl') setup.setupSDSL()
    else if (spreadsheetType === 'sgl') setup.setupSGL()

    globals.setSpreadsheetType(spreadsheetType)
}