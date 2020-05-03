package model.parser.cfg;

import java.util.ArrayList;

import javafx.util.Pair;
import model.parser.construction.ParserLineProcessor;
import model.parser.construction.ParserRulesContainer;
import utilities.Constant;
import utilities.TrieNode;

public class CFG {

    /*
     * CONTEXT FREE GRAMMAR Left recursion elimination and left factoring goes here
     */

    private ParserRulesContainer rulesCont;

    public CFG(ParserRulesContainer rulesContainer) {
        this.rulesCont = new ParserRulesContainer(rulesContainer);
        eliminateLeftRecursion();
        leftFactoring();

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

    // Left Recursion elimination
    private void eliminateLeftRecursion() {
        eliminateIndirectLeftRecursion();
        eliminateImmediateLeftRecursion();
    }

    private void eliminateIndirectLeftRecursion() {
        ArrayList<String> nontermnials = rulesCont.getProductionRules();
        for (int i = 0; i < nontermnials.size(); i++) {
            String currentNonterminal = nontermnials.get(i); // Ai
            for (int j = 0; j < i; j++) {
                String subNonterminal = nontermnials.get(j);// Aj
                // for each Ai -> Aj b replace Aj by Ai -> a b | b b | c b as Aj -> a | b | c
                replaceNonterminal(currentNonterminal, subNonterminal);
            }
        }
    }

    private void replaceNonterminal(String nonterminal, String subNonterminal) {
        ArrayList<ArrayList<String>> modifiedProduction = new ArrayList<>();
        ArrayList<ArrayList<String>> nonterminalProductions = this.rulesCont.getProductionRule(nonterminal);
        ArrayList<ArrayList<String>> subNonterminalProductions = this.rulesCont.getProductionRule(subNonterminal);
        for (ArrayList<String> nonterminalProduction : nonterminalProductions) {
            if (subNonterminal.equals(nonterminalProduction.get(0))) {
                ArrayList<ArrayList<String>> currentProductions = new ArrayList<>();
                nonterminalProduction.remove(0);
                for (ArrayList<String> subNonterminalProduction : subNonterminalProductions) {
                    ArrayList<String> currentProduction = new ArrayList<>();
                    currentProduction.addAll(subNonterminalProduction);
                    currentProduction.addAll(nonterminalProduction);
                    currentProductions.add(currentProduction);
                }
                modifiedProduction.addAll(currentProductions);
            } else {
                ArrayList<String> currentProduction = new ArrayList<>();
                currentProduction.addAll(nonterminalProduction);
                modifiedProduction.add(currentProduction);
            }
        }
        this.rulesCont.changeProductionEntry(nonterminal, modifiedProduction);

    }

    private void eliminateImmediateLeftRecursion() {
        ArrayList<String> nontermnials = rulesCont.getProductionRules();
        ArrayList<String> addedRules = new ArrayList<>();
        for (String nonterminal : nontermnials) {
            // preprocessing if new nonterminal is needed
            String newNonTerminal = getNewNonTerminal(nonterminal, Constant.NONTERMINAL_LEFT_RECURSION_DASH);
            String immediateRecursiveProductions = getImmediateLeftRecursiveProductions(nonterminal, newNonTerminal);
            if (!immediateRecursiveProductions.equals("")) {
                // adding Epsilon
                immediateRecursiveProductions += Constant.EPSILON;
                String newProductionRule = newNonTerminal + Constant.PRODUCTION_RULE_ASSIGNMENT
                        + immediateRecursiveProductions;
                addedRules.add(newProductionRule);
                sanitizeNonterminal(nonterminal, newNonTerminal);
            }
        }
        for (String rule : addedRules) {
            ParserLineProcessor.getInstance().processLine(rule, rulesCont);
        }
    }

    private String getImmediateLeftRecursiveProductions(String nonterminal, String newNonterminal) {
        String out = "";
        ArrayList<ArrayList<String>> productions = this.rulesCont.getProductionRule(nonterminal);
        for (ArrayList<String> production : productions) {
            if (nonterminal.equals(production.get(0))) {
                production.remove(0);
                production.add(newNonterminal);
                for (int i = 0; i < production.size(); i++)
                    out += production.get(i) + " ";
                out += Constant.OR;
            }
        }
        return out;
    }

    private void sanitizeNonterminal(String nonterminal, String newNonterminalName) {
        ArrayList<ArrayList<String>> modifiedProduction = new ArrayList<>();
        ArrayList<ArrayList<String>> productions = this.rulesCont.getProductionRule(nonterminal);
        for (ArrayList<String> production : productions) {
            if (newNonterminalName.equals(production.get(production.size() - 1)) == false) {
                ArrayList<String> currentProduction = new ArrayList<>();
                currentProduction.addAll(production);
                currentProduction.add(newNonterminalName);
                modifiedProduction.add(currentProduction);
            }
        }
        this.rulesCont.changeProductionEntry(nonterminal, modifiedProduction);
    }

    private void leftFactoring() {
        ArrayList<String> nontermnials = rulesCont.getProductionRules();
        for (int i = 0; i < nontermnials.size(); i++) {
            String nonterminal = nontermnials.get(i);
            TrieNode root = new TrieNode();
            ArrayList<ArrayList<String>> productions = rulesCont.getProductionRule(nonterminal);
            for (ArrayList<String> production : productions) {
                root.addString(production, 0);
            }
            constructLeftFactoredRule(root, nonterminal);

        }

    }

    private void constructLeftFactoredRule(TrieNode currentNode, String nonterminal) {
        ArrayList<String> currentWords = currentNode.getNodeKeys();
        for (String word : currentWords) {
            Pair<TrieNode, Integer> nextNode = currentNode.getNode(word);
            if (nextNode.getValue() > 1) {
                String newNonTerminal = getNewNonTerminal(nonterminal, Constant.NONTERMINAL_LEFT_FACTOR_DASH);
                String newRule = newNonTerminal + Constant.PRODUCTION_RULE_ASSIGNMENT;
                ArrayList<ArrayList<String>> productions = rulesCont.getProductionRule(nonterminal);
                for (ArrayList<String> production : productions) {
                    if (production.get(0).equals(word)) {
                        if (production.size() == 1) {
                            newRule += Constant.EPSILON;
                        } else {
                            for (int i = 1; i < production.size(); i++) {
                                newRule += production.get(i) + " ";
                            }
                        }
                        newRule += Constant.OR;
                        production.clear();
                        production.add(word);
                        production.add(newNonTerminal);
                    }
                }
                // Remove duplicates after left factoring
                for (int i = 0; i < productions.size(); i++) {
                    ArrayList<String> production = productions.get(i);
                    for (int j = i + 1; j < productions.size(); j++) {
                        ArrayList<String> otherProduction = productions.get(i);
                        if (production.equals(otherProduction)) {
                            productions.remove(j);
                        }
                        j--;
                    }
                }
                newRule = newRule.substring(0, newRule.length() - 1);// remove last '|'
                ParserLineProcessor.getInstance().processLine(newRule, rulesCont);
                constructLeftFactoredRule(nextNode.getKey(), newNonTerminal);
            }

        }

    }

    private String getNewNonTerminal(String nonterminal, String concatString) {
        String newNonTerminal = nonterminal + concatString;
        while (rulesCont.getProductionRules().contains(newNonTerminal))
            newNonTerminal += concatString;
        return newNonTerminal;
    }

    @Override
    public String toString() {
        return rulesCont.toString();
    }

}
