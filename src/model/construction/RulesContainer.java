package model.construction;


import java.util.ArrayList;
import java.util.HashMap;

public class RulesContainer {

    private HashMap<String,String> regularDefinitions;
    private ArrayList<String>  regularDefinitionsKeys;
    private HashMap<String,String> regularExpressions;
    private ArrayList<String>  regularExpressionsKeys;
    private ArrayList<String> operators;
    private ArrayList<String> keywords;

    private boolean hasErrors;

    public boolean IsValid(){return hasErrors;}

    /** no access to the private members but public wrapper functions around them is defined */
    public String GetRegularDefinition(String key){
        return regularDefinitions.get(key);
    }
    public String GetRegularExpression(String key){
        return regularExpressions.get(key);
    }

    public String GetKeyword(int idx){
        return keywords.get(idx);
    }
    public String GetOperator(int idx){
        return operators.get(idx);
    }


    public RulesContainer(String rulesFile){
        // init members
        regularDefinitions = new HashMap();
        regularDefinitionsKeys = new ArrayList<>();
        regularExpressions = new HashMap();
        regularExpressionsKeys = new ArrayList<>();
        operators = new ArrayList();
        keywords = new ArrayList();
        // regex search for each one of the elements and save them
        hasErrors = ProcessRules(rulesFile);
    }

    private  boolean ProcessRules(String rulesFile){
        String lines[] = rulesFile.split("\\r?\\n");
        for(int i = 0 ; i < lines.length ;i++){
            if(LineProcessor.GetInstance().ProcessLine(lines[i],this ) == false){
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

    @Override
    public String toString() {
        return "RulesContainer{" +
                "\nregularDefinitions=" + regularDefinitions +
                "\n regularDefinitionsKeys=" + regularDefinitionsKeys +
                "\n regularExpressions=" + regularExpressions +
                "\n regularExpressionsKeys=" + regularExpressionsKeys +
                "\n operators=" + operators +
                "\n keywords=" + keywords +
                "\n}";
    }

    /** default functions for LineProcessor to use */
    void PutRegularDefinition(String key,String val){
        regularDefinitions.put(key,val);
        regularDefinitionsKeys.add(key);
    }
    void PutRegularExpression(String key,String val){
        regularExpressions.put(key,val);
        regularExpressionsKeys.add(key);
    }
    void AddKeyword(String key){
        keywords.add(key);
    }
    void AddOperator(String op){
        operators.add(op);
    }

}
