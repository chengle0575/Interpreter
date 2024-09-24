package Lox.Statement;

import Lox.Exp.Expression;

public class PrintStmt extends Stmt{
    Expression exp;

    public PrintStmt(Expression exp){
        this.exp=exp;
    }
}
