package model.parser.construction;

import java.util.ArrayList;
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
			container.putProductionRule(mat.group(1), splitLine(mat.group(2)));
		} else
			return false;
		return true;
	}

	private ArrayList<ArrayList<String>> splitLine(String line) {
		ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();

		String[] lineWithoutORs = line.split("\\|");

		for (String word : lineWithoutORs) {
			word = word.trim();
			if (word.contains("‘") || word.contains("'") || word.contains("‘")) {
				word = word.replace("'", "");
				word = word.replace("’", "");
				word = word.replace("‘", "");
			}

			String[] withoutSpaces = word.split(" ");
			ArrayList<String> temp = new ArrayList<String>();
			for (String s : withoutSpaces) {
				temp.add(s);
			}
			output.add(temp);
		}

		return output;

	}
}
