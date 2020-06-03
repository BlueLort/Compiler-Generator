package model.code_generator.java.java_ast;

import model.parser.parser.ParsingTreeNode;

public class AssignASTNode extends ASTNode{

    private String id;
    private ExpressionASTNode expreASTNode;
    /***
     *  pass DECLARATION NODE
     *
     *  first child -> id
     *  third child -> expression
     *
     */
    public AssignASTNode(ParsingTreeNode node, ASTNode parent){
        this.nodeType = NodeType.ASSIGNMENT_TYPE_NODE;
        this.id = node.getChildren().get(0).getAttribute("value");
        this.expreASTNode = new ExpressionASTNode(node.getChildren().get(2),this);
    }
    public void action(){

        expreASTNode.action();
    }
}
