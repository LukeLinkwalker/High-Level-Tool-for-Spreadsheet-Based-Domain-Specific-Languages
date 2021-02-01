/*
 * generated by Xtext 2.18.0
 */
package org.galimatias.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.Group;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.common.services.TerminalsGrammarAccess;
import org.eclipse.xtext.service.AbstractElementFinder.AbstractGrammarElementFinder;
import org.eclipse.xtext.service.GrammarProvider;

@Singleton
public class HelloGrammarAccess extends AbstractGrammarElementFinder {
	
	public class ProgramElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "org.galimatias.Hello.Program");
		private final Assignment cWebserverAssignment = (Assignment)rule.eContents().get(1);
		private final RuleCall cWebserverWebserverParserRuleCall_0 = (RuleCall)cWebserverAssignment.eContents().get(0);
		
		//Program:
		//	webserver=Webserver // wifis+=Wifi+
		//;
		@Override public ParserRule getRule() { return rule; }
		
		//webserver=Webserver
		public Assignment getWebserverAssignment() { return cWebserverAssignment; }
		
		//Webserver
		public RuleCall getWebserverWebserverParserRuleCall_0() { return cWebserverWebserverParserRuleCall_0; }
	}
	public class WebserverElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "org.galimatias.Hello.Webserver");
		private final Keyword cWebserverKeyword = (Keyword)rule.eContents().get(1);
		
		//Webserver:
		//	'Webserver' // url=STRING port=INT
		//;
		@Override public ParserRule getRule() { return rule; }
		
		//'Webserver'
		public Keyword getWebserverKeyword() { return cWebserverKeyword; }
	}
	public class WifiElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "org.galimatias.Hello.Wifi");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword cWifiKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cSsidAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cSsidSTRINGTerminalRuleCall_1_0 = (RuleCall)cSsidAssignment_1.eContents().get(0);
		private final Assignment cPasswordAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cPasswordSTRINGTerminalRuleCall_2_0 = (RuleCall)cPasswordAssignment_2.eContents().get(0);
		
		//Wifi:
		//	'Wifi' ssid=STRING password=STRING;
		@Override public ParserRule getRule() { return rule; }
		
		//'Wifi' ssid=STRING password=STRING
		public Group getGroup() { return cGroup; }
		
		//'Wifi'
		public Keyword getWifiKeyword_0() { return cWifiKeyword_0; }
		
		//ssid=STRING
		public Assignment getSsidAssignment_1() { return cSsidAssignment_1; }
		
		//STRING
		public RuleCall getSsidSTRINGTerminalRuleCall_1_0() { return cSsidSTRINGTerminalRuleCall_1_0; }
		
		//password=STRING
		public Assignment getPasswordAssignment_2() { return cPasswordAssignment_2; }
		
		//STRING
		public RuleCall getPasswordSTRINGTerminalRuleCall_2_0() { return cPasswordSTRINGTerminalRuleCall_2_0; }
	}
	
	
	private final ProgramElements pProgram;
	private final WebserverElements pWebserver;
	private final WifiElements pWifi;
	
	private final Grammar grammar;
	
	private final TerminalsGrammarAccess gaTerminals;

	@Inject
	public HelloGrammarAccess(GrammarProvider grammarProvider,
			TerminalsGrammarAccess gaTerminals) {
		this.grammar = internalFindGrammar(grammarProvider);
		this.gaTerminals = gaTerminals;
		this.pProgram = new ProgramElements();
		this.pWebserver = new WebserverElements();
		this.pWifi = new WifiElements();
	}
	
	protected Grammar internalFindGrammar(GrammarProvider grammarProvider) {
		Grammar grammar = grammarProvider.getGrammar(this);
		while (grammar != null) {
			if ("org.galimatias.Hello".equals(grammar.getName())) {
				return grammar;
			}
			List<Grammar> grammars = grammar.getUsedGrammars();
			if (!grammars.isEmpty()) {
				grammar = grammars.iterator().next();
			} else {
				return null;
			}
		}
		return grammar;
	}
	
	@Override
	public Grammar getGrammar() {
		return grammar;
	}
	
	
	public TerminalsGrammarAccess getTerminalsGrammarAccess() {
		return gaTerminals;
	}

	
	//Program:
	//	webserver=Webserver // wifis+=Wifi+
	//;
	public ProgramElements getProgramAccess() {
		return pProgram;
	}
	
	public ParserRule getProgramRule() {
		return getProgramAccess().getRule();
	}
	
	//Webserver:
	//	'Webserver' // url=STRING port=INT
	//;
	public WebserverElements getWebserverAccess() {
		return pWebserver;
	}
	
	public ParserRule getWebserverRule() {
		return getWebserverAccess().getRule();
	}
	
	//Wifi:
	//	'Wifi' ssid=STRING password=STRING;
	public WifiElements getWifiAccess() {
		return pWifi;
	}
	
	public ParserRule getWifiRule() {
		return getWifiAccess().getRule();
	}
	
	//terminal ID:
	//	'^'? ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;
	public TerminalRule getIDRule() {
		return gaTerminals.getIDRule();
	}
	
	//terminal INT returns ecore::EInt:
	//	'0'..'9'+;
	public TerminalRule getINTRule() {
		return gaTerminals.getINTRule();
	}
	
	//terminal STRING:
	//	'"' ('\\' . | !('\\' | '"'))* '"' | "'" ('\\' . | !('\\' | "'"))* "'";
	public TerminalRule getSTRINGRule() {
		return gaTerminals.getSTRINGRule();
	}
	
	//terminal ML_COMMENT:
	//	'/*'->'*/';
	public TerminalRule getML_COMMENTRule() {
		return gaTerminals.getML_COMMENTRule();
	}
	
	//terminal SL_COMMENT:
	//	'//' !('\n' | '\r')* ('\r'? '\n')?;
	public TerminalRule getSL_COMMENTRule() {
		return gaTerminals.getSL_COMMENTRule();
	}
	
	//terminal WS:
	//	' ' | '\t' | '\r' | '\n'+;
	public TerminalRule getWSRule() {
		return gaTerminals.getWSRule();
	}
	
	//terminal ANY_OTHER:
	//	.;
	public TerminalRule getANY_OTHERRule() {
		return gaTerminals.getANY_OTHERRule();
	}
}
