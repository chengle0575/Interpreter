package Lox.Declaration.Statement;

import Lox.Exp.Expression;
import Lox.Exp.Visitor;

public class ReturnStmt extends Stmt{
    private Expression value;

    public ReturnStmt(Expression value){
        this.value=value;
    }

    public Expression getValue(){
        return value;
    }

    @Override
    public Object accept(Visitor v){
        return v.visit(this);
    }
}
