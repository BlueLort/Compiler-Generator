package model.parser.cfg;

import java.lang.reflect.Array;
import java.util.ArrayList;

import model.parser.construction.ParserLineProcessor;
import model.parser.construction.ParserRulesContainer;
import model.parser.parser.Parser;
import utilities.Constant;

public class CFG {

    /*
     * CONTEXT FREE GRAMMAR
     * Left recursion elimination and left factoring goes here
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

    private void eliminateImmediateLeftRecursion() {
        ArrayList<String> nontermnials = rulesCont.getProductionRules();
        ArrayList<String> addedRules = new ArrayList<>();
        for (String nonterminal : nontermnials) {
            //preprocessing if new nonterminal is needed
            String newNonTerminal = nonterminal + Constant.NONTERMINAL_DASH;
            while (rulesCont.getProductionRules().contains(newNonTerminal)) newNonTerminal += Constant.NONTERMINAL_DASH;
            String immediateRecursiveProductions = getImmediateLeftRecursiveProductions(nonterminal, newNonTerminal);
            if (!immediateRecursiveProductions.equals("")) {
                //adding Epsilon
                immediateRecursiveProductions += Constant.EPSILON;
                String newProductionRule = newNonTerminal + Constant.PRODUCTION_RULE_ASSIGNMENT + immediateRecursiveProductions;
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
                for (int i = 0; i < production.size(); i++) out += production.get(i) + " ";
                out += "|";
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

    private void eliminateIndirectLeftRecursion() {
        ArrayList<String> nontermnials = rulesCont.getProductionRules();
        for (int i = 0; i < nontermnials.size(); i++) {
            String currentNonterminal = nontermnials.get(i); // Ai
            for (int j = 0; j < i; j++) {
                String subNonterminal = nontermnials.get(j);//Aj
                //for each Ai -> Aj b  replace Aj by Ai -> a b | b b | c b as Aj -> a | b | c
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

    private void leftFactoring() {

    }

    @Override
    public String toString() {
        return rulesCont.toString();
    }


}
