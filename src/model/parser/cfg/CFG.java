package model.parser.cfg;

import model.parser.construction.ParserRulesContainer;

public class CFG {

	/*
	 * Left recursion elimination and left factoring goes here
	 */

	private ParserRulesContainer rulesCont;

	public CFG(ParserRulesContainer rulesCont, String startSymbol) {
		this.rulesCont = rulesCont;
	}

	public ParserRulesContainer getRulesCont() {
		return rulesCont;
	}

}
