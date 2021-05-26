import * as toolsBreakout from './breakout.js'

//TODO: Only works one way right now. From original table highlighting breakout cell, not the other way around.
export function highlightCellAndBreakoutReferenceCell(cell, cellTextDiv) {
    let breakoutReferenceCell = toolsBreakout.getBreakoutReferenceCell(cell)

    $(cellTextDiv).css('outline', 'none')
    $(breakoutReferenceCell).addClass('breakoutHighlight')
    $(cell).addClass('breakoutHighlight')
}

//TODO: Only works one way right now. From original table highlighting breakout cell, not the other way around.
export function removeHighlightCellAndBreakoutReferenceCell(cell, cellTextDiv) {
    let breakoutReferenceCell = toolsBreakout.getBreakoutReferenceCell(cell)

    $(cellTextDiv).css('outline', '')
    $(breakoutReferenceCell).removeClass('breakoutHighlight')
    $(cell).removeClass('breakoutHighlight')
}