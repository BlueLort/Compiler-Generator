package controller;

import model.construction.RulesContainer;
import model.nfa.Keyword;
import model.nfa.NFA;
import model.nfa.Punctuation;
import model.nfa.RegularDefinition;
import model.nfa.RegularExpression;

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
			RegularExpression regex = new RegularExpression(rulesCont, regularDefinition.getDefinitionNfa());
			NFA nfa = new NFA(regularDefinition, keyword, punctuation, regex);
			nfa.combine();
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