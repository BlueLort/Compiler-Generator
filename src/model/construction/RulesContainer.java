package model.construction;

import java.util.ArrayList;
import java.util.HashMap;

public class RulesContainer {

	private HashMap<String, String> regularDefinitions;
	private ArrayList<String> regularDefinitionsKeys;
	private HashMap<String, String> regularExpressions;
	private ArrayList<String> regularExpressionsKeys;
	private ArrayList<String> operators;
	private ArrayList<String> keywords;
	private ArrayList<String> symbols;

	private boolean hasErrors;

	public boolean isValid() {
		return hasErrors;
	}

	/**
	 * no access to the private members but public wrapper functions around them is
	 * defined
	 */
	public String getRegularDefinition(String key) {
		return regularDefinitions.get(key);
	}

	public String getRegularExpression(String key) {
		return regularExpressions.get(key);
	}

	public String getKeyword(int idx) {
		return keywords.get(idx);
	}

	public String getOperator(int idx) {
		return operators.get(idx);
	}

	public RulesContainer(String rulesFile) {
		// init members
		regularDefinitions = new HashMap<String, String>();
		regularDefinitionsKeys = new ArrayList<String>();
		regularExpressions = new HashMap<String, String>();
		regularExpressionsKeys = new ArrayList<String>();
		operators = new ArrayList<String>();
		keywords = new ArrayList<String>();
		symbols = new ArrayList<String>();
		// regex search for each one of the elements and save them
		hasErrors = processRules(rulesFile);
	}

	private boolean processRules(String rulesFile) {
		String lines[] = rulesFile.split("\\r?\\n");
		for (int i = 0; i < lines.length; i++) {
			if (LineProcessor.GetInstance().processLine(lines[i], this) == false) {
				return false;
			}
		}
		return true;
	}

	public ArrayList<String> getRegularDefinitionsKeys() {
		return regularDefinitionsKeys;
	}

	public ArrayList<String> getRegularExpressionsKeys() {
		return regularExpressionsKeys;
	}

	public ArrayList<String> getSymbols() {
		return symbols;
	}

	@Override
	public String toString() {
		return "RulesContainer{" + "\nregularDefinitions=" + regularDefinitions + "\n regularDefinitionsKeys="
				+ regularDefinitionsKeys + "\n regularExpressions=" + regularExpressions + "\n regularExpressionsKeys="
				+ regularExpressionsKeys + "\n operators=" + operators + "\n keywords=" + keywords + "\n}";
	}

	/**
	 * default functions for LineProcessor to use
	 */
	void putRegularDefinition(String key, String val) {
		regularDefinitions.put(key, val);
		regularDefinitionsKeys.add(key);
	}

	void putRegularExpression(String key, String val) {
		regularExpressions.put(key, val);
		regularExpressionsKeys.add(key);
	}

	void addKeyword(String key) {
		keywords.add(key);
	}

	void addOperator(String op) {
		operators.add(op);
	}

	void addSymbol(String symbol) {
		if (!symbols.contains(symbol))
			symbols.add(symbol);
	}

	public ArrayList<String> getOperators() {
		return operators;
	}

	public ArrayList<String> getKeywords() {
		return keywords;
	}

}
