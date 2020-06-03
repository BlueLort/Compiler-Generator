package model.code_generator.java.java_ast;

import java.util.ArrayList;

public abstract class ASTNode {
    public enum NodeType {
        DECLARATION_TYPE_NODE,
        IF_TYPE_NODE,
        WHILE_TYPE_NODE,
        ASSIGNMENT_TYPE_NODE,
        EXPRESSION_TYPE_NODE,
        SIMPLE_EXPRESSION_TYPE_NODE
    }
    public enum OPType{
        NONE,
        ADD,
        MIN,
        DIV,
        MUL
    };
    protected NodeType nodeType;
    protected ASTNode parent;
    protected ArrayList<ASTNode> children;

    abstract public void action();

}
