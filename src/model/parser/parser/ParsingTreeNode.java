package model.parser.parser;

import javafx.util.Pair;

import java.util.ArrayList;

public class ParsingTreeNode {
    private String name;
    private ArrayList<ParsingTreeNode> children;
    private ArrayList<Pair<String, String>> attributes;

    public ParsingTreeNode() {

        this.children = new ArrayList<>();
        this.attributes = new ArrayList<>();
    }

    public ParsingTreeNode(String name) {
        this.name = name;
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
}
