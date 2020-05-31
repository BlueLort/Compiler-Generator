package model.parser.parser;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ParsingTreeNode {
    private static int STATIC_ID = 0;
    private String name;
    private int ID;
    private ArrayList<ParsingTreeNode> children;
    private ArrayList<Pair<String, String>> attributes;

    public ParsingTreeNode() {

        this.children = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.ID = STATIC_ID;
        STATIC_ID++;
    }

    public ParsingTreeNode(String name) {
        this.children = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.name = name;
        this.ID = STATIC_ID;
        STATIC_ID++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ParsingTreeNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<ParsingTreeNode> children) {
        this.children = children;
    }

    public ArrayList<Pair<String, String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Pair<String, String>> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        String out = "";
        out += DFSPrintTree(this);
        return out;
    }

    private String DFSPrintTree(ParsingTreeNode node) {
        String out = Integer.toString(node.ID) + "\n";
        for (ParsingTreeNode child : node.getChildren()) {
                String edge = child.name;
                if (edge.equals("\\L"))
                    edge = "eps";
                out += Integer.toString(node.ID) + " " + Integer.toString(child.ID) + " " + edge + "\n";
                out += DFSPrintTree(child);
            }
        return out;
    }
}
