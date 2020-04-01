package controller;

import javafx.stage.Stage;
import javafx.util.Pair;
import model.construction.RulesContainer;
import model.dfa.DFA;
import model.dfa.DFAOptimizer;
import model.graph.Graph;
import model.graph.Node;
import model.nfa.Keyword;
import model.nfa.NFA;
import model.nfa.Punctuation;
import model.nfa.RegularDefinition;
import model.nfa.RegularExpression;
import model.tokenization.Tokenizer;
import view.CodeAnalysisInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class Controller {

    private Tokenizer tokenizer = null;

    public Controller() {

    }

    public boolean ConstructRules(String file) {
        RulesContainer rulesCont = new RulesContainer(file);
        if (rulesCont.isValid()) { // if No Errors found during rules processing
            Graph NFACombined = getCombinedNFA(rulesCont);
            DFA DFA = new DFA(NFACombined);
            DFAOptimizer minimalDFA = new DFAOptimizer(DFA);
            tokenizer = new Tokenizer(minimalDFA,rulesCont.getRegularExpressionsKeys());
            return true;
        }
        return false;
    }

    public boolean RunCodeAnalysisOnAction(String file) {
        if (tokenizer == null) return false;
        ArrayList<Pair<String, String>> lexemes = tokenizer.getTokens(file);
        if (lexemes == null) return false;
        CodeAnalysisInfo infoViewer = new CodeAnalysisInfo();
        infoViewer.initialize(lexemes, tokenizer.getTransitionTable());
        return true;
    }

    private Graph getCombinedNFA(RulesContainer rulesCont) {
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