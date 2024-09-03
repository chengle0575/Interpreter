package Lox.Exp;
import Lox.Token;
import Lox.Exp.Expression;
import Lox.Exp.Visitor;
public class Unary extends Expression {
public final Token operator;
public final Expression right;
public Unary(Token operator,Expression right) { 
this.operator=operator;
this.right=right;
};
@Override
public <R> R accept(Visitor<R> v){
return v.visit(this);
}

}
