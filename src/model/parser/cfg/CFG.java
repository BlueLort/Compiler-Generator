package model.parser.cfg;

import java.util.ArrayList;

import model.parser.construction.ParserRulesContainer;

public class CFG {

	/*
	 * CONTEXT FREE GRAMMAR 
	 * Left recursion elimination and left factoring goes here
	 */

	private ParserRulesContainer rulesCont;

	public CFG(ParserRulesContainer rulesCont) {
		this.rulesCont = rulesCont;
	}

	public ParserRulesContainer getRulesCont() {
		return rulesCont;
	}

	public boolean isNonTerminal(String word) {
		return rulesCont.getProductionRules().contains(word);
	}

	public ArrayList<String> getNonTerminals() {
		return rulesCont.getProductionRules();
	}

	public ArrayList<ArrayList<String>> getRHS(String key) {
		return rulesCont.getProductionRule(key);
	}

	public String getStartingNonTerminal() {
		// The starting non terminal is the first symbol in the non terminals array list
		return getNonTerminals().get(0);
	}

}
