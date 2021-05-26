import * as elementCell from './cell.js'
import * as globalVariables from '../globalVariables.js'
import * as setup from '../setup.js'

export function changeToSML() {
    $('.sdslClass').css('display', 'none')
    $('.smlClass').css('display', '')

    $('#dynamicTablesml').remove()
    globalVariables.setSpreadsheetType('sml')
    setup.createSpreadsheet()
    elementCell.setInitialEditingCell()
    globalVariables.setRuleTableCreated(false)
}

export function changeToSDSL() {
    $('.sdslClass').css('display', '')
    $('.smlClass').css('display', 'none')

    $('#dynamicTablesdsl').remove()
    globalVariables.setSpreadsheetType('sdsl')
    setup.createSpreadsheet()
    elementCell.setInitialEditingCell()
}