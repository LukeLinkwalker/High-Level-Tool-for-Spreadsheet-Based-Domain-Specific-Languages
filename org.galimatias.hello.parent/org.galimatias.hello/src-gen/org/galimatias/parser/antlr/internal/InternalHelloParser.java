package org.galimatias.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import org.galimatias.services.HelloGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalHelloParser extends AbstractInternalAntlrParser {
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

        public InternalHelloParser(TokenStream input, HelloGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "Program";
       	}

       	@Override
       	protected HelloGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleProgram"
    // InternalHello.g:64:1: entryRuleProgram returns [EObject current=null] : iv_ruleProgram= ruleProgram EOF ;
    public final EObject entryRuleProgram() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleProgram = null;


        try {
            // InternalHello.g:64:48: (iv_ruleProgram= ruleProgram EOF )
            // InternalHello.g:65:2: iv_ruleProgram= ruleProgram EOF
            {
             newCompositeNode(grammarAccess.getProgramRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleProgram=ruleProgram();

            state._fsp--;

             current =iv_ruleProgram; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleProgram"


    // $ANTLR start "ruleProgram"
    // InternalHello.g:71:1: ruleProgram returns [EObject current=null] : ( (lv_webserver_0_0= ruleWebserver ) ) ;
    public final EObject ruleProgram() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_webserver_0_0 = null;



        	enterRule();

        try {
            // InternalHello.g:77:2: ( ( (lv_webserver_0_0= ruleWebserver ) ) )
            // InternalHello.g:78:2: ( (lv_webserver_0_0= ruleWebserver ) )
            {
            // InternalHello.g:78:2: ( (lv_webserver_0_0= ruleWebserver ) )
            // InternalHello.g:79:3: (lv_webserver_0_0= ruleWebserver )
            {
            // InternalHello.g:79:3: (lv_webserver_0_0= ruleWebserver )
            // InternalHello.g:80:4: lv_webserver_0_0= ruleWebserver
            {

            				newCompositeNode(grammarAccess.getProgramAccess().getWebserverWebserverParserRuleCall_0());
            			
            pushFollow(FOLLOW_2);
            lv_webserver_0_0=ruleWebserver();

            state._fsp--;


            				if (current==null) {
            					current = createModelElementForParent(grammarAccess.getProgramRule());
            				}
            				set(
            					current,
            					"webserver",
            					lv_webserver_0_0,
            					"org.galimatias.Hello.Webserver");
            				afterParserOrEnumRuleCall();
            			

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleProgram"


    // $ANTLR start "entryRuleWebserver"
    // InternalHello.g:100:1: entryRuleWebserver returns [String current=null] : iv_ruleWebserver= ruleWebserver EOF ;
    public final String entryRuleWebserver() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleWebserver = null;


        try {
            // InternalHello.g:100:49: (iv_ruleWebserver= ruleWebserver EOF )
            // InternalHello.g:101:2: iv_ruleWebserver= ruleWebserver EOF
            {
             newCompositeNode(grammarAccess.getWebserverRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleWebserver=ruleWebserver();

            state._fsp--;

             current =iv_ruleWebserver.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleWebserver"


    // $ANTLR start "ruleWebserver"
    // InternalHello.g:107:1: ruleWebserver returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : kw= 'Webserver' ;
    public final AntlrDatatypeRuleToken ruleWebserver() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalHello.g:113:2: (kw= 'Webserver' )
            // InternalHello.g:114:2: kw= 'Webserver'
            {
            kw=(Token)match(input,11,FOLLOW_2); 

            		current.merge(kw);
            		newLeafNode(kw, grammarAccess.getWebserverAccess().getWebserverKeyword());
            	

            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleWebserver"

    // Delegated rules


 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});

}