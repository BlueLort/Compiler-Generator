package model.parser.construction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * static class to process lines of rules file
 **/
public class ParserLineProcessor {
    public static final String PRODUCTION_RULE_REGEX = "^[ \\t]*(\\w+)(?: )*::=([\\s\\S]+)$";
    private static ParserLineProcessor instance = null;

    public static ParserLineProcessor getInstance() {
        if (instance == null) {
            instance = new ParserLineProcessor();
        }
        return instance;
    }

    private ParserLineProcessor() {

    }

    public boolean processLine(String line, ParserRulesContainer container) {
        Pattern reg;
        reg = Pattern.compile(PRODUCTION_RULE_REGEX);
        Matcher mat = reg.matcher(line);
        if (mat.find()) {
            container.putProductionRule(mat.group(1), mat.group(2));
        } else return false;
        return true;
    }
}
