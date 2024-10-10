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

    public Expression getConditionExp(){
        return conditionExp;
    }

    public Stmt getIfstmt(){
        return ifstmt;
    }

    public Stmt getElsestmt(){
        return elsestmt;
    }
    public Object accept(Visitor v){
        return v.visit(this);
    }
}
