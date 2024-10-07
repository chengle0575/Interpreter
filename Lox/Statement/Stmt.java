package Lox.Statement;

import Lox.Exp.Expression;

public class Stmt {
    private Expression exp;

    public Expression getExp(){
        return this.exp;
    }
}
