package Lox.Declaration.Statement;

import Lox.Exp.Expression;
import Lox.Exp.Visitor;
import Lox.Token;

public class VarStmt extends Stmt{
    private Token identifier;
    private Expression exp;

    public VarStmt(Token identifier,Expression exp){
        this.identifier=identifier;
        this.exp=exp;
    }

    public Token getIdentifier() {
        return identifier;
    }

    public Expression getExp() {
        return exp;
    }

    public Object accept(Visitor v){
        return v.visit(this);
    }
}
