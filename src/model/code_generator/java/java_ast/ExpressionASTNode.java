package model.code_generator.java.java_ast;

import model.parser.parser.ParsingTreeNode;

public class ExpressionASTNode extends ASTNode{
    private OPType operation;
    private String operand;
    private String factor;
    public ExpressionASTNode(ParsingTreeNode node, ASTNode parent){
        this.nodeType = NodeType.EXPRESSION_TYPE_NODE;

    }
    public void action(){

    }
}
