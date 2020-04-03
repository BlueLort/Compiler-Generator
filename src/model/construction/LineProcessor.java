package model.construction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * static class to process lines of rules file
 **/
public class LineProcessor {
    public static final String[] REGEX_FORMATS = {RegexFormats.REGULAR_DEFINITION, RegexFormats.REGULAR_EXPRESSION,
            RegexFormats.KEYWORD, RegexFormats.OPERATOR};
    private static LineProcessor instance = null;

    public static LineProcessor GetInstance() {
        if (instance == null) {
            instance = new LineProcessor();
        }
        return instance;
    }

    private LineProcessor() {

    }

    public boolean processLine(String line, RulesContainer container) {
        Rule lineRules = null;
        Pattern reg;
        for (int i = 0; i < REGEX_FORMATS.length; i++) {
            reg = Pattern.compile(REGEX_FORMATS[i]);
            Matcher mat = reg.matcher(line);
            if (mat.find()) {
                switch (i) {
                    case 0:
                        lineRules = new RegularDefinition(mat.group(1), mat.group(2));
                        break;
                    case 1:
                        lineRules = new RegularExpression(mat.group(1), mat.group(2));
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
        if (lineRules == null) {
            return false;
        }
        lineRules.addRule(container);
        return true;
    }

    private abstract class Rule {

        abstract void addRule(RulesContainer container);
    }

    private class RegularDefinition extends Rule {
        public String key;
        public String value;

        public RegularDefinition(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        void addRule(RulesContainer container) {
            container.putRegularDefinition(key, value);
        }
    }

    private class RegularExpression extends Rule {
        public String key;
        public String value;

        public RegularExpression(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        void addRule(RulesContainer container) {
            container.putRegularExpression(key, value);
        }
    }

    private class Keywords extends Rule {
        public String[] keywords;// Takes keywords separated by space

        public Keywords(String keywords) {
            this.keywords = keywords.split(" ");

        }

        @Override
        void addRule(RulesContainer container) {
            for (int i = 0; i < this.keywords.length; i++) {
                if (!this.keywords[i].equals(""))
                    container.addKeyword(this.keywords[i]);
            }
        }
    }

    private class Operators extends Rule {
        public char[] operators;// Takes keywords separated by space

        public Operators(String ops) {
            this.operators = ops.toCharArray();
        }

        @Override
        void addRule(RulesContainer container) {
            for (int i = 0; i < this.operators.length; i++) {
                if (this.operators[i] != ' ')
                    if (this.operators[i] != '\\') {
                        container.addOperator(String.valueOf(this.operators[i]));
                    } else {
                        container.addOperator(String.valueOf(this.operators, i, 2));
                        i++;
                    }

            }
        }
    }

    /**
     * non constructable class to define project input file rules
     */
    private class RegexFormats {

        public static final String REGULAR_DEFINITION = "^(\\w+)(?: )*=(.+)$";
        /**
         * match RD
         */
        public static final String REGULAR_EXPRESSION = "^(\\w+)(?: )*:(.+)$";
        /**
         * match RE
         */
        public static final String KEYWORD = "^\\{(.+)\\}$";
        /**
         * match and split on space
         */
        public static final String OPERATOR = "^\\[(.+)\\]$";

        /**
         * Read After to end
         */
        private RegexFormats() {
        }
    }
}
