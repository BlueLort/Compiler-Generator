package utilities;

import java.util.ArrayList;
import java.util.Stack;

import model.graph.Graph;

public class NfaUtility {

	public static ArrayList<String> addConcatSymbolToWords(String[] word) {

		ArrayList<String> output = new ArrayList<String>();

		for (int i = 0; i < word.length - 1; i++) {
			output.add(word[i]);
			output.add(Constant.concatenate);
		}
		output.add(word[word.length - 1]);
		return output;
	}

	public static int precedence(String c) {
		if (c == Constant.concatenate)
			return 3;
		else if (c == Constant.or)
			return 2;
		else if (c == Constant.kleene || c == Constant.plus)
			return 1;
		return -1;
	}

	public static String infixToPostFix(ArrayList<String> expression) {

		StringBuilder result = new StringBuilder();

		Stack<String> stack = new Stack<String>();

		for (int i = 0; i < expression.size(); i++) {
			String c = expression.get(i);

			if (precedence(c) > 0) {
				while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(c))
					result.append(stack.pop());
				stack.push(c);
			} else if (c == ")") {
				if (expression.size() != 1) {
					String x = stack.pop();
					while (x != "(") {
						result.append(x);
						x = stack.pop();
					}
				} else {
					stack.push(c);
				}
			} else if (c == "(") {
				stack.push(c);
			} else { // Character is neither operator nor (
				result.append(c);
			}

		}

		while (!stack.isEmpty())
			result.append(stack.pop());

		return result.toString();
	}

	public static Graph createNfa(String expression) {
		// create a stack
		Stack<Graph> nfa = new Stack<Graph>();

		// Scan all characters one by one
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);

			if (isOperator(String.valueOf(c))) {
				if (c == Constant.kleene.charAt(0)) {
					Graph g = nfa.pop();
					nfa.push(GraphUtilities.kleeneClosure(g));
				} else if (c == Constant.plus.charAt(0)) {
					Graph g = nfa.pop();
					nfa.push(GraphUtilities.plusClosure(g));
				} else if (c == Constant.or.charAt(0)) {
					Graph a = nfa.pop();
					Graph b = nfa.pop();
					nfa.push(GraphUtilities.or(a, b));
				} else if (c == Constant.concatenate.charAt(0)) {
					Graph a = nfa.pop();
					Graph b = nfa.pop();
					nfa.push(GraphUtilities.concatenate(b, a));
				}
			} else {
				String nodeName = String.valueOf(c);
				nfa.push(new Graph(nodeName));
			}
		}
		return nfa.pop();
	}

	public static boolean isOperator(String character) {
		String operators = "*+|`";
		return operators.indexOf(character) == -1 ? false : true;
	}

}
