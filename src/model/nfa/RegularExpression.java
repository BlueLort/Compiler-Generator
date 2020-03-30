package model.nfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.Map.Entry;

import model.construction.RulesContainer;
import model.graph.Graph;
import utilities.Constant;
import utilities.GraphUtility;
import utilities.NfaUtility;

public class RegularExpression {

	private HashMap<String, Graph> definitionNfa;
	private HashMap<String, Graph> regExpressionNfa;

	RulesContainer rulesContainer;

	public RegularExpression(RulesContainer rulesCont, HashMap<String, Graph> definitionNfa) {
		regExpressionNfa = new HashMap<String, Graph>();
		this.definitionNfa = definitionNfa;
		this.rulesContainer = rulesCont;
	}

	public void regexToNfa() {

		for (int i = 0; i < rulesContainer.getRegularExpressionsKeys().size(); i++) {
			String definitionKey = rulesContainer.getRegularExpressionsKeys().get(i);
			String definitionValue = rulesContainer.getRegularExpression(definitionKey);

			definitionValue = definitionValue.replace(" ", "");

			ArrayList<String> words = separateRegularExpression(definitionValue);

			words = addConcatSymbolToRegex(words);
			ArrayList<String> postFixExpression = NfaUtility.infixToPostFix(words);
			Graph nfa = createNfa(postFixExpression);
			regExpressionNfa.put(definitionKey, nfa);

		}
	}

	private ArrayList<String> separateRegularExpression(String regex) {

		ArrayList<String> result = new ArrayList<String>();

		int i = 0;
		while (i < regex.length()) {
			// To keep track of the last valid definition/operator
			int j = i + 1;
			for (int k = i + 1; k < regex.length(); k++) {
				// K + 1 - > Substring is exclusive
				String temp = regex.substring(i, k + 1);
				if (definitionNfa.containsKey(temp) || Constant.REGEX_OPERATORS.indexOf(temp) != -1) {
					// Don't break from the loop (Digit / Digit(s))
					j = k + 1;
				}
			}
			result.add(regex.substring(i, j));
			i = j;

		}

		return result;
	}

	private ArrayList<String> addConcatSymbolToRegex(ArrayList<String> word) {
		ArrayList<String> output = new ArrayList<String>();

		output.add(word.get(0));

		for (int i = 1; i < word.size(); i++) {

			/* If current letter is ( and previous not equal | -> digit | (digits) */
			if (output.get(output.size() - 1) != Constant.OR && word.get(i).equals("("))
				output.add(Constant.CONCATENATE);

			/* If 2 words */
			if (!NfaUtility.isOperator(output.get(output.size() - 1)) && !NfaUtility.isOperator(word.get(i)))
				output.add(Constant.CONCATENATE);

			// If the previous is * or + and the next is not or
			if (NfaUtility.isKleeneOrPlus(output.get(output.size() - 1)) && !word.get(i).equals("|"))
				output.add(Constant.CONCATENATE);

			output.add(word.get(i));
		}

		return output;

	}

	private Graph createNfa(ArrayList<String> expression) {
		//System.out.println(expression);
		// create a stack
		Stack<Graph> nfa = new Stack<Graph>();
		// Scan all characters one by one
		for (int i = 0; i < expression.size(); i++) {
			String currentExpression = expression.get(i);
			if (NfaUtility.isRegexOperator(currentExpression)) {
				if (currentExpression.equals(Constant.KLEENE)) {
					Graph g = nfa.pop();
					//System.out.println("Now star");
					//System.out.println(g);
					//System.out.println();
					nfa.push(GraphUtility.kleeneClosure(g));
					//System.out.println("Star result");
					//System.out.println(nfa.peek());
				} else if (currentExpression.equals(Constant.PLUS)) {
					Graph g = nfa.pop();
					//System.out.println("Now plus");
					//System.out.println(g);
					//System.out.println();
					nfa.push(GraphUtility.plusClosure(g));
					//System.out.println("Plus result");
					//System.out.println(nfa.peek());
				} else if (currentExpression.equals(Constant.OR)) {
					Graph right = nfa.pop();
					Graph left = nfa.pop();
					//System.out.println("Now Oring 2 graphs");
					//System.out.println(right);
					//System.out.println();
					//System.out.println(left);
					nfa.push(GraphUtility.or(right, left));
					//System.out.println("Or result");
					//System.out.println(nfa.peek());
				} else if (currentExpression.equals(Constant.CONCATENATE)) {
					Graph right = nfa.pop();
					Graph left = nfa.pop();
					//System.out.println("Now concatenating 2 graphs");
					//System.out.println(left);
					//System.out.println();
					//System.out.println(right);
					nfa.push(GraphUtility.concatenate(left, right));
					//System.out.println("Concatenate result");
					//System.out.println(nfa.peek());
				}
			} else {
				if (definitionNfa.containsKey(currentExpression)) {
					Graph g = new Graph(definitionNfa.get(currentExpression));
					nfa.push(g);
				} else if (NfaUtility.isSymbol(currentExpression)) {
					String nodeName = expression.get(i).substring(1);
					nfa.push(new Graph(nodeName));
				} else {
					nfa.push(new Graph(currentExpression));
				}
			}
		}
		return nfa.pop();
	}

	public HashMap<String, Graph> getRegExpressionNfa() {
		return regExpressionNfa;
	}

}
