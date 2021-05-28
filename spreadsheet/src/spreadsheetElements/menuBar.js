import * as elementCell from './cell.js'
import * as spreadsheet from './spreadsheet.js'
import * as globalVariables from '../globalVariables.js'

export function changeToSML() {
    $('.sdslClass').css('display', 'none')
    $('.smlClass').css('display', '')

    $('#dynamicTablesml').remove()
    globalVariables.setSpreadsheetType('sml')
    spreadsheet.createSpreadsheet()
    elementCell.setInitialEditingCell()
    globalVariables.setRuleTableCreated(false)
}

export function changeToSDSL() {
    $('.sdslClass').css('display', '')
    $('.smlClass').css('display', 'none')

    $('#dynamicTablesdsl').remove()
    globalVariables.setSpreadsheetType('sdsl')
    spreadsheet.createSpreadsheet()
    elementCell.setInitialEditingCell()
}