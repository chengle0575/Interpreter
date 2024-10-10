package Lox.Declaration.Statement;

import Lox.Exp.Expression;
import Lox.Exp.Visitor;

public class IfStmt extends Stmt{
    Expression conditionExp;
    Stmt ifstmt;
    Stmt elsestmt;

    public IfStmt(Expression conditionExp,Stmt ifstmt,Stmt elsestmt){
        this.conditionExp=conditionExp;
        this.ifstmt=ifstmt;
        this.elsestmt=elsestmt;
    }

    public Object accept(Visitor v){
        return v.visit(this);
    }
}
