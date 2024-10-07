package Lox.Statement;

import Lox.Exp.Expression;

public class ExprStmt extends Stmt{
    Expression exp;

    public ExprStmt(Expression exp){
        this.exp=exp;
    }

    public Expression getExp(){
        return this.exp;
    }
}
