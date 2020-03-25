package utilities;

public class Constant {
	
	public static final String EPSILON = "\\L";
	public static final String CONCATENATE = "`";
	public static final String OR = "|";
	public static final String KLEENE = "*";
	public static final String PLUS = "+";

	public static final String ALPHABETS = "abcdefghijklmnopqrstuvwxyz";
	public static final String DIGITS = "0123456789";
	
	
	/*
	 * Valid Operators : * ( ) \\L 
	 * Math Operators: \\+ \\* / -
	 * Comparison Operators : \\= < > 
	 * Add the \\, To split the regex correctly
	 */
	public static final String REGEX_OPERATORS = "\\*()\\L\\+*/-\\=<>";
	public static final String[] REGEX_OPERATORS2 = {
			"\\+",
			"\\*",
			"\\(",
			"\\=",
			"\\)",
	};

	
}
