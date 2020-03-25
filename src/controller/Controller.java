package controller;

import model.construction.RulesContainer;
import model.nfa.Keyword;
import model.nfa.Punctuation;
import model.nfa.RegularDefinition;

public class Controller {
	// TODO ADD NFA/DFA MEMBERS TO BE USED IN RUN CODE ANALYSIS

	public Controller() {

	}

	public boolean ConstructRules(String file) {
		RulesContainer rulesCont = new RulesContainer(file);
		if (rulesCont.IsValid()) {
			// Finished Processing the rules
			System.out.println(rulesCont);
			// TODO pass rulesCont as a parameter to NFA/DFA class to get processed data
			// easily
			RegularDefinition regularDefinition = new RegularDefinition(rulesCont);
			Keyword keyword = new Keyword(rulesCont);
			Punctuation punctuation = new Punctuation(rulesCont);
			regularDefinition.definitionsToNfa();
			regularDefinition.dfsGraphs();
			keyword.keywordToNfa();
			keyword.dfsGraphs();
			punctuation.punctuationToNfa();
			punctuation.dfsGraphs();
			return true;
		}
		return false;
	}

	public boolean RunCodeAnalysisOnAction(String file) {
		// TODO HANDLE ERRORS IF FILE IS BAD OR DFA/NFA NOT CONSTRUCTED [Return false if
		// bad operation]

		return false;
	}

}