package model.parser.parser;

import javafx.util.Pair;
import model.parser.cfg.CFG;
import utilities.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Parser {
	private HashMap<String, HashMap<String, ArrayList<ArrayList<String>>>> parsingTable;
	/**
	 * key of outer hashmap is a non terminal entry key of inner hashmap is terminal
	 * char input ArrayList value of inner hashmap is set of rules (in case of
	 * ambiguous) inner ArrayList is the production
	 */

	private CFG grammar;

	private ArrayList<Pair<String, String>> inputTokens;
	private HashMap<Integer, String> errors;
	private ArrayList<Stack<String>> outStacks;

	public Parser(ParserGenerator parserGenerator, ArrayList<Pair<String, String>> inputTokens) {
		this.parsingTable = parserGenerator.getParsingTable();
		this.inputTokens = inputTokens;
		this.grammar = parserGenerator.getGrammar();
		errors = new HashMap<>();
		outStacks = new ArrayList<>();
		parse();
	}

	public void parse() {
		int inputTokenIndex = 0;
		Stack<String> stack = new Stack<>();
		stack.push(Constant.END_MARKER);
		stack.push(grammar.getStartingNonTerminal());
		while (!stack.empty() && inputTokenIndex != inputTokens.size()) {
			outStacks.add((Stack) stack.clone());
			String TOS = stack.pop();

			if (grammar.isNonTerminal(TOS)) { /** if top of stack is non terminal */
				/** if top of stack leads to empty entry */
				if (!parsingTable.get(TOS).containsKey(inputTokens.get(inputTokenIndex))) {
					errors.put(inputTokenIndex, inputTokens.get(inputTokenIndex).getValue());
					inputTokenIndex++;
					stack.push(TOS);
					continue;
				}
				/** if top of stack leads to epsilon */
				if (parsingTable.get(TOS).get(inputTokens.get(inputTokenIndex)).get(0).get(0)
						.equals(Constant.EPSILON)) {
					continue;
				}
				/** if top of stack is production rule */
				if (parsingTable.get(TOS).get(inputTokens.get(inputTokenIndex)).get(0).get(0)
						.equals(Constant.SYNC_TOK)) {
					errors.put(inputTokenIndex, inputTokens.get(inputTokenIndex).getValue());
					inputTokenIndex++;
					stack.push(TOS);
					continue;
				}
				/** a production rule needs to be pushed to stack */
				int lengthOfArray = parsingTable.get(TOS).get(inputTokens.get(inputTokenIndex).getValue()).get(0)
						.size();
				for (int i = lengthOfArray - 1; i >= 0; i++) {
					stack.push(parsingTable.get(TOS).get(inputTokens.get(inputTokenIndex).getValue()).get(0).get(i));
				}
				continue;
			} else { /** if top of stack is terminal */
				/** if input token match top of stack */
				if (TOS.equals(inputTokens.get(inputTokenIndex).getValue())) {
					inputTokenIndex++;
					continue;
				}
				/** if input token doesn't match top of stack */
				else {
					errors.put(inputTokenIndex, inputTokens.get(inputTokenIndex).getValue());
					inputTokenIndex++;
				}
			}
		}
	}

	public HashMap<Integer, String> getErrors() {
		return errors;
	}

	public ArrayList<Stack<String>> getOutStacks() {
		return outStacks;
	}
}
