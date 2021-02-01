/*
 * generated by Xtext 2.18.0
 */
grammar InternalHello;

options {
	superClass=AbstractInternalContentAssistParser;
}

@lexer::header {
package org.galimatias.ide.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.Lexer;
}

@parser::header {
package org.galimatias.ide.contentassist.antlr.internal;

import java.io.InputStream;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.DFA;
import org.galimatias.services.HelloGrammarAccess;

}
@parser::members {
	private HelloGrammarAccess grammarAccess;

	public void setGrammarAccess(HelloGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}

	@Override
	protected Grammar getGrammar() {
		return grammarAccess.getGrammar();
	}

	@Override
	protected String getValueForTokenName(String tokenName) {
		return tokenName;
	}
}

// Entry rule entryRuleProgram
entryRuleProgram
:
{ before(grammarAccess.getProgramRule()); }
	 ruleProgram
{ after(grammarAccess.getProgramRule()); } 
	 EOF 
;

// Rule Program
ruleProgram 
	@init {
		int stackSize = keepStackSize();
	}
	:
	(
		{ before(grammarAccess.getProgramAccess().getWebserverAssignment()); }
		(rule__Program__WebserverAssignment)
		{ after(grammarAccess.getProgramAccess().getWebserverAssignment()); }
	)
;
finally {
	restoreStackSize(stackSize);
}

// Entry rule entryRuleWebserver
entryRuleWebserver
:
{ before(grammarAccess.getWebserverRule()); }
	 ruleWebserver
{ after(grammarAccess.getWebserverRule()); } 
	 EOF 
;

// Rule Webserver
ruleWebserver 
	@init {
		int stackSize = keepStackSize();
	}
	:
	(
		{ before(grammarAccess.getWebserverAccess().getWebserverKeyword()); }
		'Webserver'
		{ after(grammarAccess.getWebserverAccess().getWebserverKeyword()); }
	)
;
finally {
	restoreStackSize(stackSize);
}

rule__Program__WebserverAssignment
	@init {
		int stackSize = keepStackSize();
	}
:
	(
		{ before(grammarAccess.getProgramAccess().getWebserverWebserverParserRuleCall_0()); }
		ruleWebserver
		{ after(grammarAccess.getProgramAccess().getWebserverWebserverParserRuleCall_0()); }
	)
;
finally {
	restoreStackSize(stackSize);
}

RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

RULE_INT : ('0'..'9')+;

RULE_STRING : ('"' ('\\' .|~(('\\'|'"')))* '"'|'\'' ('\\' .|~(('\\'|'\'')))* '\'');

RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

RULE_WS : (' '|'\t'|'\r'|'\n')+;

RULE_ANY_OTHER : .;
