/*
 * generated by Xtext 2.18.0
 */
package org.galimatias.parser.antlr;

import com.google.inject.Inject;
import org.eclipse.xtext.parser.antlr.AbstractAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.galimatias.parser.antlr.internal.InternalHelloParser;
import org.galimatias.services.HelloGrammarAccess;

public class HelloParser extends AbstractAntlrParser {

	@Inject
	private HelloGrammarAccess grammarAccess;

	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	

	@Override
	protected InternalHelloParser createParser(XtextTokenStream stream) {
		return new InternalHelloParser(stream, getGrammarAccess());
	}

	@Override 
	protected String getDefaultRuleName() {
		return "Program";
	}

	public HelloGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	public void setGrammarAccess(HelloGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
