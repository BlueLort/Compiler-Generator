package model.code_generator.java.java_ast;

import java.util.ArrayList;

public abstract class ASTNode {
    public static final int DECLARATION_TYPE_NODE = 1;
    public static final int IF_TYPE_NODE = 2;
    public static final int WHILE_TYPE_NODE = 3;
    public static final int ASSIGNMENT_TYPE_NODE = 4;
    public static final int EXPRESSION_TYPE_NODE = 5;
    public static final int SIMPLE_EXPRESSION_TYPE_NODE = 6;
    protected int  type;
    protected ASTNode parent;
    protected ArrayList<ASTNode> children;

    abstract public void action();

}
