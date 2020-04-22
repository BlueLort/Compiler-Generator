package model.parser.parser;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    private HashMap<String, HashMap<String, ArrayList<ArrayList<String>>>> parsingTable;
    /* key of outer hashmap is a non terminal entry  */
    /* key of inner hashmap is terminal char input */
    /* ArrayList value of inner hashmap is set of rules (in case of ambiguous) */
    /* inner ArrayList is the production */

    public Parser(HashMap<String, HashMap<String, ArrayList<ArrayList<String>>>> parsingTable) {
        this.parsingTable = parsingTable;
    }
}
