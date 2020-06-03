package model.code_generator.java.java_ast;

import model.parser.parser.ParsingTreeNode;

public class DeclASTNode extends ASTNode {
    private String type;
    private String id;

    /***
     *  pass DECLARATION NODE
     *
     *  first child -> PRIMITIVE_TYPE
     *  second child -> id
     *
     */
    public DeclASTNode(ParsingTreeNode node, ASTNode parent){
            this.nodeType = NodeType.DECLARATION_TYPE_NODE;
            this.type = node.getChildren().get(0).getAttribute("value");
            this.id = node.getChildren().get(1).getAttribute("value");
            //TODO MAINTAIN SYM TABLE
    }
    public void action(){

    }
}
