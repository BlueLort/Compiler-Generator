package utilities;

import java.util.ArrayList;
import java.util.Stack;

public class NfaUtility {

	public static int precedence(String c) {
		if (c.equals(Constant.KLEENE) || c.equals(Constant.PLUS))
			return 4;
		if (c.equals(Constant.CONCATENATE))
			return 3;
		if (c.equals(Constant.OR))
			return 2;
		return -1;
	}

	public static ArrayList<String> infixToPostFix(ArrayList<String> expression) {

		ArrayList<String> result = new ArrayList<String>();

		Stack<String> stack = new Stack<String>();

		for (int i = 0; i < expression.size(); i++) {
			String c = expression.get(i);

			if (precedence(c) > 0) {
				while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(c))
					result.add(stack.pop());
				stack.push(c);
			} else if (c.equals(")")) {
				if (expression.size() != 1) {
					while (!stack.isEmpty() && !(stack.peek().equals("("))) {
						result.add(stack.pop());
					}
					stack.pop();
				} else {
					stack.push(c);
				}
			} else if (c.equals("(")) {
				stack.push(c);
			} else { // Character is neither operator nor (
				result.add(c);
			}

		}

		while (!stack.isEmpty())
			result.add(stack.pop());

		return result;
	}

	public static ArrayList<String> addConcatSymbolToWords(String[] word) {

		ArrayList<String> output = new ArrayList<String>();

		for (int i = 0; i < word.length - 1; i++) {
			output.add(word[i]);
			output.add(Constant.CONCATENATE);
		}
		output.add(word[word.length - 1]);
		return output;
	}

	public static boolean isKleeneOrPlus(String character) {
		if (character.equals(Constant.KLEENE) || character.equals(Constant.PLUS))
			return true;
		return false;
	}

	public static boolean isRegexOperator(String character) {
		for (String s : Constant.REGEX_OPERATOR) {
			if (s.equals(character))
				return true;
		}
		return false;
	}

	public static ArrayList<String> removeDuplicates(ArrayList<String> input) {
		ArrayList<String> result = new ArrayList<String>();
		for (String s : input) {
			if (!result.contains(s))
				result.add(s);
		}
		return result;
	}

}
