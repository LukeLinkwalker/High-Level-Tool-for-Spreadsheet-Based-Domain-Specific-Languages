import * as globalVariables from '../globalVariables.js'
import * as elementCell from '../spreadsheetElements/cell.js'

export function markCells() {
    globalVariables.setCellsMarked(true)
    globalVariables.selectedCells.forEach((cell) => {
        if ($(cell).hasClass('header')) $(cell).addClass('selectedHeader')
        else if  ($(cell).hasClass('data')) $(cell).addClass('selectedData')
        else $(cell).addClass('selected')
    })
}

export function clearMarkedCells() {
    globalVariables.setCellsMarked(false)
    $('.selected').each((i, element) => $(element).removeClass('selected'))
    $('.selectedData').each((i, element) => $(element).removeClass('selectedData'))
    $('.selectedHeader').each((i, element) => $(element).removeClass('selectedHeader'))
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

    globalVariables.setSelectedCells([])

    for (let column = 0; column < globalVariables.columnSize; column++) {
        for (let row = 0; row < globalVariables.rowSize; row++) {
            let cell = elementCell.getCellFromIndexes(column, row)

            if (column >= column1 && column <= column2 && row >= row1 && row <= row2) {
                globalVariables.selectedCells.push(cell)
            }
        }
    }
}