package utilities;

import java.util.ArrayList;
import java.util.HashSet;

public class Utility {
	// TODO FIX THIS MESS WHICH OBJECT IS PARENT TO HashSet & ArrayList
	public static String getStringFromHashSet(HashSet<String> set) {
		String out = "";
		for (String val : set) {
			out += val;
			out += " ";
		}
		return out;
	}

	public static String getStringFromArrayList(ArrayList<String> arr) {
		String out = "";
		for (String val : arr) {
			out += val;
			out += " ";
		}
		return out;
	}

}
