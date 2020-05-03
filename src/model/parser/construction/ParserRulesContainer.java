package model.parser.construction;

import java.util.ArrayList;
import java.util.HashMap;

public class ParserRulesContainer {

	private HashMap<String, ArrayList<ArrayList<String>>> productionRules;
	private ArrayList<String> nonTerminals;

	private boolean hasErrors;

	public boolean isValid() {
		return hasErrors;
	}

	/**
	 * no access to the private members but public wrapper functions around them is
	 * defined
	 */
	public ArrayList<ArrayList<String>> getProductionRule(String key) {
		return productionRules.get(key);
	}

	public ArrayList<String> getProductionRules() {
		return nonTerminals;
	}

	public ParserRulesContainer(String rulesFile) {
		// Init members
		productionRules = new HashMap<String, ArrayList<ArrayList<String>>>();
		nonTerminals = new ArrayList<String>();
		// Regex search for each one of the elements and save them
		hasErrors = processRules(rulesFile);
	}

	public ParserRulesContainer(ParserRulesContainer rulesContainer) {
		// Init members
		this.productionRules = new HashMap<String, ArrayList<ArrayList<String>>>();
		this.nonTerminals = new ArrayList<String>();
		// Regex search for each one of the elements and save them
		hasErrors = rulesContainer.hasErrors;
		this.nonTerminals.addAll(rulesContainer.nonTerminals);
		this.productionRules.putAll(rulesContainer.productionRules);
	}

	private boolean processRules(String rulesFile) {
		String rules[] = rulesFile.split("#");// # is a mark for new rule
		for (int i = 0; i < rules.length; i++) {
			if (rules[i].equals(""))
				continue;
			if (ParserLineProcessor.getInstance().processLine(rules[i], this) == false) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "Parser Rules Container{" + "\nProduction Rules = " + productionRules + "\nNon terminals=" + nonTerminals
				+ "\n}";
	}

	public void changeProductionEntry(String key, ArrayList<ArrayList<String>> val) {
		productionRules.put(key, val);
	}

	/**
	 * default functions for ParserLineProcessor to use
	 */
	void putProductionRule(String key, ArrayList<ArrayList<String>> val) {
		productionRules.put(key, val);
		nonTerminals.add(key);
	}

}
