package model.construction;

import javafx.util.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** static class to process lines of rules file **/
public class LineProcessor {
    public static final String[] REGEX_FORMATS =
            {RegexFormats.REGULAR_DEFINITION ,RegexFormats.REGULAR_EXPRESSION ,RegexFormats.KEYWORD , RegexFormats.OPERATOR};
    private static LineProcessor instance=null;

    public static LineProcessor GetInstance() {
            if(instance == null){
                instance = new LineProcessor();
            }
        return instance;
    }
    private LineProcessor(){

    }
    public void ProcessLine(String line,RulesContainer container){
        Rule lineRules = null;
        Pattern reg;
        for(int i = 0 ; i < REGEX_FORMATS.length ; i++){
            Pair<String,Integer> regData = GetRegexFormat(i,line);
            reg = Pattern.compile(regData.getKey());
            Matcher mat = reg.matcher(line);
            if (mat.find()){
                switch (regData.getValue()){
                    case 0:
                        lineRules = new RegularDefinition(mat.group(1),mat.group(2));
                        break;
                    case 1:
                        lineRules = new RegularExpression(mat.group(1),mat.group(2));
                        break;
                    case 2:
                        lineRules = new Keywords(mat.group(1));
                        break;
                    case 3:
                        lineRules = new Operators(mat.group(1));
                        break;
                }
                break;

            }
        }
        lineRules.AddRule(container);
    }
    // conflicts keywords with operators so i do early checking to go and get certain regex
    // alternative solution
    // also i need to return index of the REGEX_FORMAT so i can construct classes safely
    private Pair<String,Integer> GetRegexFormat(int idx, String line){
        String regexFormat;
        int startLoc = 0;
        int formatID = idx;
        while(line.charAt(startLoc) == ' ')startLoc++;
        if(line.charAt(startLoc) == '['){
            regexFormat = RegexFormats.OPERATOR;
            formatID = 3;
        }
        else if(line.charAt(startLoc) == '{'){
            regexFormat = RegexFormats.KEYWORD;
            formatID = 2;
        }
        else regexFormat = REGEX_FORMATS[idx];
        return new Pair(regexFormat,formatID);
    }
    private abstract class Rule {

        abstract void AddRule(RulesContainer container);
    }
    private class RegularDefinition extends Rule{
        public String key;
        public String value;
        public  RegularDefinition(String key,String value){
            this.key = key;
            this.value = value;
        }
        @Override
        void AddRule(RulesContainer container){
                container.PutRegularDefinition(key,value);
        }
    }
    private class RegularExpression extends Rule{
        public String key;
        public String value;
        public  RegularExpression(String key,String value){
            this.key = key;
            this.value = value;
        }
        @Override
        void AddRule(RulesContainer container){
            container.PutRegularExpression(key,value);
        }
    }
    private class Keywords extends Rule{
        public String[] keywords;//Takes keywords separated by space
        public  Keywords(String keywords){
            this.keywords = keywords.split(" ");
        }
        @Override
        void AddRule(RulesContainer container){
            for(int i = 0 ; i <this.keywords.length; i++){
                container.AddKeyword(this.keywords[i]);
            }
        }
    }
    private class Operators extends Rule{
        public String[] operators;//Takes keywords separated by space
        public  Operators(String ops){
            this.operators = ops.split(" ");
        }
        @Override
        void AddRule(RulesContainer container){
            for(int i = 0 ; i <this.operators.length; i++){
                container.AddOperator(this.operators[i]);
            }
        }
    }

    /** non constructable class to define project input file rules */
    private class RegexFormats {

        public static final String REGULAR_DEFINITION = "(\\w+) =(.+)";/** match RD */
        public static final String REGULAR_EXPRESSION = "(\\w+):(.+)";/** match RE */
        public static final String KEYWORD = "\\{(.+)\\}";/** match and split on space */
        public static final String OPERATOR = "\\[(.+)\\]";/** Read After to end */
        private RegexFormats() { }
    }
}
