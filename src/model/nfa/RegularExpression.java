package model.nfa;

import java.util.HashMap;

import model.construction.RulesContainer;
import model.graph.Graph;

public class RegularExpression {

	HashMap<String, Graph> definitionNfa;

	RulesContainer rulesContainer;

	public RegularExpression(RulesContainer rulesCont) {
		definitionNfa = new HashMap<String, Graph>();
		this.rulesContainer = rulesCont;
	}
}
