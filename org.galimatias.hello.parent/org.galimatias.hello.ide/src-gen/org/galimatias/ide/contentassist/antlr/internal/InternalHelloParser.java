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



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalHelloParser extends AbstractInternalContentAssistParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_INT", "RULE_STRING", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'Webserver'"
    };
    public static final int RULE_ID=4;
    public static final int RULE_WS=9;
    public static final int RULE_STRING=6;
    public static final int RULE_ANY_OTHER=10;
    public static final int RULE_SL_COMMENT=8;
    public static final int RULE_INT=5;
    public static final int T__11=11;
    public static final int RULE_ML_COMMENT=7;
    public static final int EOF=-1;

    // delegates
    // delegators


        public InternalHelloParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalHelloParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalHelloParser.tokenNames; }
    public String getGrammarFileName() { return "InternalHello.g"; }


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



    // $ANTLR start "entryRuleProgram"
    // InternalHello.g:53:1: entryRuleProgram : ruleProgram EOF ;
    public final void entryRuleProgram() throws RecognitionException {
        try {
            // InternalHello.g:54:1: ( ruleProgram EOF )
            // InternalHello.g:55:1: ruleProgram EOF
            {
             before(grammarAccess.getProgramRule()); 
            pushFollow(FOLLOW_1);
            ruleProgram();

            state._fsp--;

             after(grammarAccess.getProgramRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleProgram"


    // $ANTLR start "ruleProgram"
    // InternalHello.g:62:1: ruleProgram : ( ( rule__Program__WebserverAssignment ) ) ;
    public final void ruleProgram() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalHello.g:66:2: ( ( ( rule__Program__WebserverAssignment ) ) )
            // InternalHello.g:67:2: ( ( rule__Program__WebserverAssignment ) )
            {
            // InternalHello.g:67:2: ( ( rule__Program__WebserverAssignment ) )
            // InternalHello.g:68:3: ( rule__Program__WebserverAssignment )
            {
             before(grammarAccess.getProgramAccess().getWebserverAssignment()); 
            // InternalHello.g:69:3: ( rule__Program__WebserverAssignment )
            // InternalHello.g:69:4: rule__Program__WebserverAssignment
            {
            pushFollow(FOLLOW_2);
            rule__Program__WebserverAssignment();

            state._fsp--;


            }

             after(grammarAccess.getProgramAccess().getWebserverAssignment()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleProgram"


    // $ANTLR start "entryRuleWebserver"
    // InternalHello.g:78:1: entryRuleWebserver : ruleWebserver EOF ;
    public final void entryRuleWebserver() throws RecognitionException {
        try {
            // InternalHello.g:79:1: ( ruleWebserver EOF )
            // InternalHello.g:80:1: ruleWebserver EOF
            {
             before(grammarAccess.getWebserverRule()); 
            pushFollow(FOLLOW_1);
            ruleWebserver();

            state._fsp--;

             after(grammarAccess.getWebserverRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleWebserver"


    // $ANTLR start "ruleWebserver"
    // InternalHello.g:87:1: ruleWebserver : ( 'Webserver' ) ;
    public final void ruleWebserver() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalHello.g:91:2: ( ( 'Webserver' ) )
            // InternalHello.g:92:2: ( 'Webserver' )
            {
            // InternalHello.g:92:2: ( 'Webserver' )
            // InternalHello.g:93:3: 'Webserver'
            {
             before(grammarAccess.getWebserverAccess().getWebserverKeyword()); 
            match(input,11,FOLLOW_2); 
             after(grammarAccess.getWebserverAccess().getWebserverKeyword()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleWebserver"


    // $ANTLR start "rule__Program__WebserverAssignment"
    // InternalHello.g:102:1: rule__Program__WebserverAssignment : ( ruleWebserver ) ;
    public final void rule__Program__WebserverAssignment() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalHello.g:106:1: ( ( ruleWebserver ) )
            // InternalHello.g:107:2: ( ruleWebserver )
            {
            // InternalHello.g:107:2: ( ruleWebserver )
            // InternalHello.g:108:3: ruleWebserver
            {
             before(grammarAccess.getProgramAccess().getWebserverWebserverParserRuleCall_0()); 
            pushFollow(FOLLOW_2);
            ruleWebserver();

            state._fsp--;

             after(grammarAccess.getProgramAccess().getWebserverWebserverParserRuleCall_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Program__WebserverAssignment"

    // Delegated rules


 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});

}