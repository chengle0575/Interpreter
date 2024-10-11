package Lox.Declaration.Statement;

import Lox.Exp.Expression;
import Lox.Exp.Visitor;

public class WhileStmt extends Stmt{
    Expression condition;
    Stmt loopbody;

    public WhileStmt(Expression condition,Stmt loopbody){
        this.condition=condition;
        this.loopbody=loopbody;
    }

    public Expression getCondition(){
        return  condition;
    }

    public Stmt getLoopbody(){
        return loopbody;
    }

    public Object accept(Visitor v){
        return v.visit(this);
    }
}
