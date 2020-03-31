package controller;

import model.construction.RulesContainer;
import model.dfa.DFA;
import model.dfa.DFAOptimizer;
import model.graph.Graph;
import model.nfa.Keyword;
import model.nfa.NFA;
import model.nfa.Punctuation;
import model.nfa.RegularDefinition;
import model.nfa.RegularExpression;

public class Controller {
	// TODO ADD NFA/DFA MEMBERS TO BE USED IN RUN CODE ANALYSIS
	DFA  DFAMinimized;

	public Controller() {

	}

	public boolean ConstructRules(String file) {
		RulesContainer rulesCont = new RulesContainer(file);
		if (rulesCont.isValid()) { // if No Errors found during rules processing
			Graph NFACombined = getCombinedNFA(rulesCont);
            System.out.println("\n\n\n\n");
            System.out.println(NFACombined);
			DFAMinimized = new  DFA(NFACombined);
			System.out.println("\n\n\n\n");
            System.out.println(DFAMinimized.getDFA());
            System.out.println("\n\n\n\n\n\n");
			DFAOptimizer dfaOptimizer = new DFAOptimizer(DFAMinimized);
			// TODO Pass NFACombined to DFA Constructor
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