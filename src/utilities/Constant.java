package utilities;

public class Constant {
    /*
     * LEXICAL ANALYZER
     */

    public static final String EPSILON = "\\L";
    public static final String CONCATENATE = "`";
    public static final String OR = "|";
    public static final String KLEENE = "*";
    public static final String PLUS = "+";

    public static final String ALPHABETS = "abcdefghijklmnopqrstuvwxyz";
    public static final String DIGITS = "0123456789";
    public static final String SEPARATOR = " ";
    public static final String LEXICAL_SAVING_PATH = "output/lexemes.txt";
    /*
     * Valid Operators : * ( ) \\L Math Operators: \\+ \\* / - Comparison Operators
     * : \\= < > Add the \\, To split the regex correctly
     */

    public static final String[] REGEX_OPERATOR = {"*", "+", "|", "`", "(", ")"};

    /*
     * PARSER
     */

    public static final String PRODUCTION_RULE_ASSIGNMENT = "::=";
    public static final String NONTERMINAL_LEFT_RECURSION_DASH = "_DASH";
    public static final String NONTERMINAL_LEFT_FACTOR_DASH = "_HASH";
    public static final String END_MARKER = "$";
    public static final String SYNC_TOK = "SYNC";
    public static final String PARSING_TABLE_SEPARATOR = " | ";
    public static final String PARSING_SAVING_PATH = "output/parsing.txt";
}
