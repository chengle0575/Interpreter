package Lox.Declaration.Statement;

import Lox.Exp.Visitor;
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

    public Token getIdentifier() {
        return identifier;
    }

    public List<Token> getParameters(){
        return parameters;
    }

    public List<Stmt> getFunctionContent(){
        return functionContent;
    }

    public Object accept(Visitor v){
        return v.visit(this);
    }
}
