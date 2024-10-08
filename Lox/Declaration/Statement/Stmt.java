package Lox.Declaration.Statement;

import Lox.Exp.Expression;
import Lox.Exp.Visitor;

public class Stmt {
    private Expression exp;

    public Object accept(Visitor v){
        return v.visit(this);
    }
    public Expression getExp(){
        return this.exp;
    }


}
