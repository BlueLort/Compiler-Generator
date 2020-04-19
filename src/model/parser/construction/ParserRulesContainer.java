package model.parser.construction;

import java.util.ArrayList;
import java.util.HashMap;

public class ParserRulesContainer {

    private HashMap<String, String> productionRules;
    private ArrayList<String> productionRulesKeys;

    private boolean hasErrors;

    public boolean isValid() {
        return hasErrors;
    }

    /**
     * no access to the private members but public wrapper functions around them is
     * defined
     */
    public String getProductionRule(String key) {
        return productionRules.get(key);
    }

    public ArrayList<String> getProductionRules() {
        return productionRulesKeys;
    }

    public ParserRulesContainer(String rulesFile) {
        // init members
        productionRules = new HashMap<String, String>();
        productionRulesKeys = new ArrayList<String>();
        // regex search for each one of the elements and save them
        hasErrors = processRules(rulesFile);
    }

    private boolean processRules(String rulesFile) {
        String rules[] = rulesFile.split("#");//# is a mark for new rule
        for (int i = 0; i < rules.length; i++) {
            if (rules[i].equals("")) continue;
            if (ParserLineProcessor.getInstance().processLine(rules[i], this) == false) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "ParserRulesContainer{" + "\nproductionRules=" + productionRules + "\n productuionRulesKeys=" + productionRulesKeys + "\n}";
    }

    /**
     * default functions for ParserLineProcessor to use
     */
    void putProductionRule(String key, String val) {
        productionRules.put(key, val);
        productionRulesKeys.add(key);
    }

}