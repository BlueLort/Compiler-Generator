package controller;

import java.util.ArrayList;

import javafx.util.Pair;
import model.lexical_analyzer.construction.LexicalRulesContainer;
import model.lexical_analyzer.dfa.DFA;
import model.lexical_analyzer.dfa.DFAOptimizer;
import model.lexical_analyzer.graph.Graph;
import model.lexical_analyzer.nfa.Keyword;
import model.lexical_analyzer.nfa.NFA;
import model.lexical_analyzer.nfa.Punctuation;
import model.lexical_analyzer.nfa.RegularDefinition;
import model.lexical_analyzer.nfa.RegularExpression;
import model.lexical_analyzer.tokenization.Tokenizer;
import model.parser.cfg.CFG;
import model.parser.construction.ParserRulesContainer;
import model.parser.parser.ParserGenerator;
import view.CodeAnalysisInfo;

public class Controller {

    private Tokenizer tokenizer = null;// member because it's used with the parser
    private ParserGenerator parserGenerator = null;

    // TODO MEMBER VAR. PARSING TABLE
    public Controller() {

    }

    // Lexical Analyzer
    // -----------------------------------------------------------------------
    public boolean constructLexicalRules(String file) {
        LexicalRulesContainer rulesCont = new LexicalRulesContainer(file);
        if (rulesCont.isValid()) { // if No Errors found during rules processing
            Graph NFACombined = getCombinedNFA(rulesCont);
            DFA DFA = new DFA(NFACombined);
            DFAOptimizer minimalDFA = new DFAOptimizer(DFA);
            tokenizer = new Tokenizer(minimalDFA, rulesCont.getRegularExpressionsKeys());
            return true;
        }
        return false;
    }

    public boolean runCodeAnalysisOnAction(String file) {
        if (tokenizer == null)
            return false;
        ArrayList<Pair<String, String>> lexemes = tokenizer.getTokens(file);
        CodeAnalysisInfo infoViewer = new CodeAnalysisInfo();
        infoViewer.initialize(lexemes, tokenizer.getTransitionTable());
        return tokenizer.isValidTokenization();
    }

    // Parser
    // -----------------------------------------------------------------------
    public boolean loadLexemesFromFile(String filePath) {
        tokenizer = new Tokenizer(filePath);
        return true;
    }

    public boolean constructParserRules(String file) {
        ParserRulesContainer rulesCont = new ParserRulesContainer(file);
        if (rulesCont.isValid()) { // if No Errors found during rules processing

            System.out.println(rulesCont); // Rules captured
            CFG grammar = new CFG(rulesCont);
            System.out.println(grammar); // Rules with left factoring  & eliminated left recursion
            parserGenerator = new ParserGenerator(grammar);
            parserGenerator.constructParser();
            // TODO PASRE THE RULES CAPTURED
            // TODO ELIMINATE LEFT RECURSION , DO LEFT FACTORING
            // TODO FIRST FOLLOW SETS
            // TODO PARSING TABLE

            return true;
        }
        return false;
    }

    public boolean parseInput() {
        // TODO REMOVE THE NEXT LINE AFTER FINISHING THE PARSER
        // INITIALIZING TOKENIZER HERE MAKE IT EASIER FOR DEVELOPMENT
        tokenizer = new Tokenizer("output/lexemes.txt");
        // tokenizer.getSavedLexems(); // Word , Match for the last code analysis ran by
        // the user.
        if (tokenizer == null)
            return false;
        // TODO TRACE OUT USING STACK & PANIC MODE RECOVERY
        return false;
    }


    private Graph getCombinedNFA(LexicalRulesContainer rulesCont) {
        RegularDefinition regularDefinition = new RegularDefinition(rulesCont);
        Keyword keyword = new Keyword(rulesCont);
        Punctuation punctuation = new Punctuation(rulesCont);
        RegularExpression regex = new RegularExpression(rulesCont, regularDefinition.getDefinitionNfa());
        NFA NFACombined = new NFA(regularDefinition, keyword, punctuation, regex);
        Graph combinedNFAs = NFACombined.getCombinedGraph();
        return combinedNFAs;
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

}