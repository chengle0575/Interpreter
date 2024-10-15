package Lox.Declaration.Statement;

import Lox.Token;

import java.util.List;

public class FuncStmt extends Stmt{
    private Token identifier;
    private List<Token> parameters;
    private List<Stmt> functionContent;

    public FuncStmt(Token identifier, List<Token> parameters, List<Stmt> functionContent){
        this.identifier=identifier;
        this.parameters=parameters;
        this.functionContent=functionContent;
    }
}
