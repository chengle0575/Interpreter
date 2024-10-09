package Lox.Exp;

import Lox.Token;

public class Assign extends Expression{
    Token name;
    Expression value;

    public Assign(Token name,Expression value){
        this.name=name;
        this.value=value;
    }

    public Token getName(){
        return name;
    }

    public Expression getValue(){
        return value;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}
