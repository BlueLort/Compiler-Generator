package model.nfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import model.construction.RulesContainer;
import model.graph.Graph;
import utilities.Constant;
import utilities.GraphUtility;
import utilities.NfaUtility;

public class RegularExpression {

    private HashMap<String, Graph> definitionNfa;
    private HashMap<String, Graph> regExpressionNfa;
    private ArrayList<String> backlashSymbols;
    private RulesContainer rulesContainer;

    public RegularExpression(RulesContainer rulesCont, HashMap<String, Graph> definitionNfa) {
        regExpressionNfa = new HashMap<String, Graph>();
        this.definitionNfa = definitionNfa;
        rulesContainer = rulesCont;
        backlashSymbols = generateSymbols();
        regexToNfa(rulesCont);
    }

    private void regexToNfa(RulesContainer rulesContainer) {

        for (int i = 0; i < rulesContainer.getRegularExpressionsKeys().size(); i++) {
            String definitionKey = rulesContainer.getRegularExpressionsKeys().get(i);
            String definitionValue = rulesContainer.getRegularExpression(definitionKey);

            definitionValue = definitionValue.replace(" ", "");

            ArrayList<String> words = separateRegularExpression(definitionValue);
            words = addConcatSymbolToRegex(words);
            ArrayList<String> postFixExpression = NfaUtility.infixToPostFix(words);
            Graph nfa = createNfa(postFixExpression);
            regExpressionNfa.put(definitionKey, nfa);
            nfa.getDestination().setNodeTypes(definitionKey);
        }
    }

    private ArrayList<String> separateRegularExpression(String regex) {

        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> symbols = rulesContainer.getSymbols();
        int i = 0;
        while (i < regex.length()) {
            // To keep track of the last valid definition/operator
            int j = i + 1;
            for (int k = i + 1; k < regex.length(); k++) {
                // K + 1 - > Substring is exclusive
                String temp = regex.substring(i, k + 1);
                if (definitionNfa.containsKey(temp) || symbols.contains(temp) || this.backlashSymbols.contains(temp)) {
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
            if (!NfaUtility.isRegexOperator(output.get(output.size() - 1)) && !NfaUtility.isRegexOperator(word.get(i)))
                output.add(Constant.CONCATENATE);

            // If the previous is * or + and the next is not or
            if (NfaUtility.isKleeneOrPlus(output.get(output.size() - 1)) && !word.get(i).equals("|"))
                output.add(Constant.CONCATENATE);

            output.add(word.get(i));
        }

        return output;

    }

    private Graph createNfa(ArrayList<String> expression) {
        // create a stack
        Stack<Graph> nfa = new Stack<Graph>();
        // Scan all characters one by one
        for (int i = 0; i < expression.size(); i++) {
            String currentExpression = expression.get(i);
            if (NfaUtility.isRegexOperator(currentExpression)) {
                if (currentExpression.equals(Constant.KLEENE)) {
                    Graph g = nfa.pop();
                    nfa.push(GraphUtility.kleeneClosure(g));
                } else if (currentExpression.equals(Constant.PLUS)) {
                    Graph g = nfa.pop();
                    nfa.push(GraphUtility.plusClosure(g));
                } else if (currentExpression.equals(Constant.OR)) {
                    Graph right = nfa.pop();
                    Graph left = nfa.pop();
                    nfa.push(GraphUtility.or(right, left));
                } else if (currentExpression.equals(Constant.CONCATENATE)) {
                    Graph right = nfa.pop();
                    Graph left = nfa.pop();
                    nfa.push(GraphUtility.concatenate(left, right));
                }
            } else {
                if (definitionNfa.containsKey(currentExpression)) {
                    Graph g = new Graph(definitionNfa.get(currentExpression));
                    nfa.push(g);
                } else if (backlashSymbols.contains(currentExpression) && !currentExpression.equals("\\L")) {
                    String nodeName = expression.get(i).substring(1);
                    nfa.push(new Graph(nodeName));
                } else {
                    nfa.push(new Graph(currentExpression));
                }
            }
        }
        return nfa.pop();
    }

    public ArrayList<String> generateSymbols() {
        ArrayList<String> result = new ArrayList<String>();

        for (String s : rulesContainer.getOperators()) {
            if (s.startsWith("\\")) {
                result.add(s);
            }
        }

        for (String s : rulesContainer.getRegularExpressionsKeys()) {
            String regularExpression = rulesContainer.getRegularExpression(s);
            int i = 0;
            while (i < regularExpression.length()) {
                char c = regularExpression.charAt(i);
                if (c == '\\') {
                    result.add("\\" + regularExpression.charAt(i + 1));
                    i += 2;
                } else {
                    i++;
                }
            }
        }
        result = NfaUtility.removeDuplicates(result);
        return result;
    }

    public HashMap<String, Graph> getRegExpressionNfa() {
        return regExpressionNfa;
    }

    public ArrayList<String> getBackSlashSymbols() {
        return backlashSymbols;
    }

}
