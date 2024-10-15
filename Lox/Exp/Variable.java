package Lox.Exp;
import Lox.Token;
import Lox.Exp.Expression;
import Lox.Exp.Visitor;
public class Variable extends Expression {
private final Token name;

public Variable(Token name) { 
this.name=name;
};

public Token getName(){
    return name;
}
@Override
public <R> R accept(Visitor<R> v){
return v.visit(this);
}

}
