package controller;

import java.util.*;

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
import model.parser.parser.Parser;
import model.parser.parser.ParserGenerator;
import utilities.Constant;
import view.CodeAnalysisInfo;
import view.ParserInfo;

public class Controller {

    private Tokenizer tokenizer = null;// member because it's used with the parser
    private ParserGenerator parserGenerator = null;
    private Parser parser = null;

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
            ParserInfo infoViewer = new ParserInfo();
            infoViewer.initialize(parserGenerator,getTerminals(grammar));
            // TODO DO LEFT FACTORING
            return true;
        }
        return false;
    }

    public boolean parseInput() {
        // TODO REMOVE THE NEXT LINE AFTER FINISHING THE PARSER
        // INITIALIZING TOKENIZER HERE MAKE IT EASIER FOR DEVELOPMENT
        tokenizer = new Tokenizer("output/lexemes.txt");
        // tokenizer.getSavedLexemes(); // Word , Match for the last code analysis ran by
        // the user.
        if (tokenizer == null)
            return false;
        // TODO TRACE OUT USING STACK & PANIC MODE RECOVERY
        parser = new Parser(parserGenerator,tokenizer.getSavedLexems());
        int length = parser.getOutStacks().size();

        System.out.println("output of parser:");
        for (int i = 0; i < length ; i++) {
            Stack<String> temp = (Stack)parser.getOutStacks().get(i).clone();
            Queue<String> outQueue = new LinkedList<>();
            while (!temp.empty()) {
                outQueue.add(temp.pop());
            }
            while (!outQueue.isEmpty()) {
                System.out.print(outQueue.poll()+"\t");
            }
            System.out.println();
        }
        return true;
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

    private ArrayList<String> getTerminals(CFG grammar){
        HashSet<String> terminals = new HashSet<>();
        ArrayList<String> out = new ArrayList<>();
        ArrayList<String> nonTerminals = grammar.getNonTerminals();
        for(String nonTerminal : nonTerminals){
            ArrayList<ArrayList<String>> productions = grammar.getRHS(nonTerminal);
            for (ArrayList<String> production : productions){
                for (String token:production){
                    if(!token.equals(Constant.EPSILON) && !grammar.isNonTerminal(token)&&!terminals.contains(token)){
                        terminals.add(token);
                        out.add(token);
                    }
                }

            }
        }
        out.add(Constant.END_MARKER);
        return out;
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

}