import * as elementCell from './cell.js'
import * as client from '../client.js'
import * as setup from '../setup.js'

export function createInfoBox() {
    return $('<div class="infoBox box">')
}

export function getInfoBox(cell) {
    return $('.infoBox', cell)[0]
}

function insertNewMessageInInfoBox(infoBox, value) {
    let currentText = $(infoBox).text()

    if (currentText === '') $(infoBox).text(value)
    else $(infoBox).text(currentText + '\n' + value)
}

export function createTableCodeCompletionForInfoBox(tableName, column, row, spreadsheetType) {
    let cell = elementCell.getCellFromIndexes(column, row)
    let infoBox = getInfoBox(cell)
    let infoBoxText = 'Create ' + tableName + ' table? Press enter to create it.'

    $(cell).off('keydown')
    setup.setupCellKeyDown($(cell))
    insertNewMessageInInfoBox(infoBox, infoBoxText)
    $(infoBox).css('display', 'block')
    $(cell).addClass('infoBoxShown')

    $(cell).on('keydown',(e) => {
        if (e.which === 13) {
            client.requestGetInitialTableRange(tableName, column, row, spreadsheetType)
        }
    })
}

export function hideCreateTableCodeCompletionForInfoBox(infoBox, cell) {
    $(cell).off('keydown')
    setup.setupCellKeyDown($(cell))
    setup.setupCellKeyDownEnter($(cell))
    $(infoBox).css('display', 'none')
    $(infoBox).text('')
    $(cell).removeClass('infoBoxShown')
}