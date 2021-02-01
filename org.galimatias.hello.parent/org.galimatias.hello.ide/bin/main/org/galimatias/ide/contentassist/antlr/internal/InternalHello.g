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
		{ before(grammarAccess.getProgramAccess().getGroup()); }
		(rule__Program__Group__0)
		{ after(grammarAccess.getProgramAccess().getGroup()); }
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
		{ before(grammarAccess.getWebserverAccess().getGroup()); }
		(rule__Webserver__Group__0)
		{ after(grammarAccess.getWebserverAccess().getGroup()); }
	)
;
finally {
	restoreStackSize(stackSize);
}

// Entry rule entryRuleWifi
entryRuleWifi
:
{ before(grammarAccess.getWifiRule()); }
	 ruleWifi
{ after(grammarAccess.getWifiRule()); } 
	 EOF 
;

// Rule Wifi
ruleWifi 
	@init {
		int stackSize = keepStackSize();
	}
	:
	(
		{ before(grammarAccess.getWifiAccess().getGroup()); }
		(rule__Wifi__Group__0)
		{ after(grammarAccess.getWifiAccess().getGroup()); }
	)
;
finally {
	restoreStackSize(stackSize);
}

rule__Program__Group__0
	@init {
		int stackSize = keepStackSize();
	}
:
	rule__Program__Group__0__Impl
	rule__Program__Group__1
;
finally {
	restoreStackSize(stackSize);
}

rule__Program__Group__0__Impl
	@init {
		int stackSize = keepStackSize();
	}
:
(
	{ before(grammarAccess.getProgramAccess().getWebserverAssignment_0()); }
	(rule__Program__WebserverAssignment_0)
	{ after(grammarAccess.getProgramAccess().getWebserverAssignment_0()); }
)
;
finally {
	restoreStackSize(stackSize);
}

rule__Program__Group__1
	@init {
		int stackSize = keepStackSize();
	}
:
	rule__Program__Group__1__Impl
;
finally {
	restoreStackSize(stackSize);
}

rule__Program__Group__1__Impl
	@init {
		int stackSize = keepStackSize();
	}
:
(
	(
		{ before(grammarAccess.getProgramAccess().getWifisAssignment_1()); }
		(rule__Program__WifisAssignment_1)
		{ after(grammarAccess.getProgramAccess().getWifisAssignment_1()); }
	)
	(
		{ before(grammarAccess.getProgramAccess().getWifisAssignment_1()); }
		(rule__Program__WifisAssignment_1)*
		{ after(grammarAccess.getProgramAccess().getWifisAssignment_1()); }
	)
)
;
finally {
	restoreStackSize(stackSize);
}


rule__Webserver__Group__0
	@init {
		int stackSize = keepStackSize();
	}
:
	rule__Webserver__Group__0__Impl
	rule__Webserver__Group__1
;
finally {
	restoreStackSize(stackSize);
}

rule__Webserver__Group__0__Impl
	@init {
		int stackSize = keepStackSize();
	}
:
(
	{ before(grammarAccess.getWebserverAccess().getWebserverKeyword_0()); }
	'Webserver'
	{ after(grammarAccess.getWebserverAccess().getWebserverKeyword_0()); }
)
;
finally {
	restoreStackSize(stackSize);
}

rule__Webserver__Group__1
	@init {
		int stackSize = keepStackSize();
	}
:
	rule__Webserver__Group__1__Impl
	rule__Webserver__Group__2
;
finally {
	restoreStackSize(stackSize);
}

rule__Webserver__Group__1__Impl
	@init {
		int stackSize = keepStackSize();
	}
:
(
	{ before(grammarAccess.getWebserverAccess().getUrlAssignment_1()); }
	(rule__Webserver__UrlAssignment_1)
	{ after(grammarAccess.getWebserverAccess().getUrlAssignment_1()); }
)
;
finally {
	restoreStackSize(stackSize);
}

rule__Webserver__Group__2
	@init {
		int stackSize = keepStackSize();
	}
:
	rule__Webserver__Group__2__Impl
;
finally {
	restoreStackSize(stackSize);
}

rule__Webserver__Group__2__Impl
	@init {
		int stackSize = keepStackSize();
	}
:
(
	{ before(grammarAccess.getWebserverAccess().getPortAssignment_2()); }
	(rule__Webserver__PortAssignment_2)
	{ after(grammarAccess.getWebserverAccess().getPortAssignment_2()); }
)
;
finally {
	restoreStackSize(stackSize);
}


rule__Wifi__Group__0
	@init {
		int stackSize = keepStackSize();
	}
:
	rule__Wifi__Group__0__Impl
	rule__Wifi__Group__1
;
finally {
	restoreStackSize(stackSize);
}

rule__Wifi__Group__0__Impl
	@init {
		int stackSize = keepStackSize();
	}
:
(
	{ before(grammarAccess.getWifiAccess().getWifiKeyword_0()); }
	'Wifi'
	{ after(grammarAccess.getWifiAccess().getWifiKeyword_0()); }
)
;
finally {
	restoreStackSize(stackSize);
}

rule__Wifi__Group__1
	@init {
		int stackSize = keepStackSize();
	}
:
	rule__Wifi__Group__1__Impl
	rule__Wifi__Group__2
;
finally {
	restoreStackSize(stackSize);
}

rule__Wifi__Group__1__Impl
	@init {
		int stackSize = keepStackSize();
	}
:
(
	{ before(grammarAccess.getWifiAccess().getSsidAssignment_1()); }
	(rule__Wifi__SsidAssignment_1)
	{ after(grammarAccess.getWifiAccess().getSsidAssignment_1()); }
)
;
finally {
	restoreStackSize(stackSize);
}

rule__Wifi__Group__2
	@init {
		int stackSize = keepStackSize();
	}
:
	rule__Wifi__Group__2__Impl
;
finally {
	restoreStackSize(stackSize);
}

rule__Wifi__Group__2__Impl
	@init {
		int stackSize = keepStackSize();
	}
:
(
	{ before(grammarAccess.getWifiAccess().getPasswordAssignment_2()); }
	(rule__Wifi__PasswordAssignment_2)
	{ after(grammarAccess.getWifiAccess().getPasswordAssignment_2()); }
)
;
finally {
	restoreStackSize(stackSize);
}


rule__Program__WebserverAssignment_0
	@init {
		int stackSize = keepStackSize();
	}
:
	(
		{ before(grammarAccess.getProgramAccess().getWebserverWebserverParserRuleCall_0_0()); }
		ruleWebserver
		{ after(grammarAccess.getProgramAccess().getWebserverWebserverParserRuleCall_0_0()); }
	)
;
finally {
	restoreStackSize(stackSize);
}

rule__Program__WifisAssignment_1
	@init {
		int stackSize = keepStackSize();
	}
:
	(
		{ before(grammarAccess.getProgramAccess().getWifisWifiParserRuleCall_1_0()); }
		ruleWifi
		{ after(grammarAccess.getProgramAccess().getWifisWifiParserRuleCall_1_0()); }
	)
;
finally {
	restoreStackSize(stackSize);
}

rule__Webserver__UrlAssignment_1
	@init {
		int stackSize = keepStackSize();
	}
:
	(
		{ before(grammarAccess.getWebserverAccess().getUrlSTRINGTerminalRuleCall_1_0()); }
		RULE_STRING
		{ after(grammarAccess.getWebserverAccess().getUrlSTRINGTerminalRuleCall_1_0()); }
	)
;
finally {
	restoreStackSize(stackSize);
}

rule__Webserver__PortAssignment_2
	@init {
		int stackSize = keepStackSize();
	}
:
	(
		{ before(grammarAccess.getWebserverAccess().getPortINTTerminalRuleCall_2_0()); }
		RULE_INT
		{ after(grammarAccess.getWebserverAccess().getPortINTTerminalRuleCall_2_0()); }
	)
;
finally {
	restoreStackSize(stackSize);
}

rule__Wifi__SsidAssignment_1
	@init {
		int stackSize = keepStackSize();
	}
:
	(
		{ before(grammarAccess.getWifiAccess().getSsidSTRINGTerminalRuleCall_1_0()); }
		RULE_STRING
		{ after(grammarAccess.getWifiAccess().getSsidSTRINGTerminalRuleCall_1_0()); }
	)
;
finally {
	restoreStackSize(stackSize);
}

rule__Wifi__PasswordAssignment_2
	@init {
		int stackSize = keepStackSize();
	}
:
	(
		{ before(grammarAccess.getWifiAccess().getPasswordSTRINGTerminalRuleCall_2_0()); }
		RULE_STRING
		{ after(grammarAccess.getWifiAccess().getPasswordSTRINGTerminalRuleCall_2_0()); }
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