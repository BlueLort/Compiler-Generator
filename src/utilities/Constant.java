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

    /*
     * Valid Operators : * ( ) \\L Math Operators: \\+ \\* / - Comparison Operators
     * : \\= < > Add the \\, To split the regex correctly
     */

    public static final String[] REGEX_OPERATOR = {"*", "+", "|", "`", "(", ")"};

    /*
     * PARSER
     */

    public static final String PRODUCTION_RULE_ASSIGNMENT = "::=";//TODO 8yr el STRING ya 3mo
    public static final String NONTERMINAL_DASH = "_DASH";//TODO 8yr el STRING ya 3mo
    public static final String END_MARKER = "$";
    public static final String SYNC_TOK = "SYNC_TOK";
    public static final String PARSING_TABLE_SEPARATOR = " | ";
}
