package controller;

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

import java.util.ArrayList;
import java.util.HashMap;

public class Controller {

	Graph  DFAMinimized;
	HashMap<String, Node> DFAMinimizedTransTable;

	public Controller() {

	}

	public boolean ConstructRules(String file) {
		RulesContainer rulesCont = new RulesContainer(file);
		if (rulesCont.isValid()) { // if No Errors found during rules processing
			Graph NFACombined = getCombinedNFA(rulesCont);
			DFA DFA = new  DFA(NFACombined);
			DFAOptimizer dfaOptimizer = new DFAOptimizer(DFA);
			DFAMinimized = dfaOptimizer.getDFAMinimized();
			DFAMinimizedTransTable = dfaOptimizer.getMinimizedDFATransTable();
			return true;
		}
		return false;
	}

	public boolean RunCodeAnalysisOnAction(String file) {
		// TODO HANDLE ERRORS IF FILE IS BAD OR DFA/NFA NOT CONSTRUCTED [Return false if
		// bad operation]

		return false;
	}

	private Graph getCombinedNFA(RulesContainer rulesCont) {
		RegularDefinition regularDefinition = new RegularDefinition(rulesCont);
		Keyword keyword = new Keyword(rulesCont);
		Punctuation punctuation = new Punctuation(rulesCont);
		RegularExpression regex = new RegularExpression(rulesCont, regularDefinition.getDefinitionNfa());
		NFA NFACombined = new NFA(regularDefinition, keyword, punctuation, regex);
		Graph combinedNFAs = NFACombined.combine();
		return combinedNFAs;
	}
}