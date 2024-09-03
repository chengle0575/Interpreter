package Lox.Exp;
import Lox.Token;
import Lox.Exp.Expression;
import Lox.Exp.Visitor;
public class Grouping extends Expression {
public final Expression exp;
public Grouping(Expression exp) { 
this.exp=exp;
};
@Override
public <R> R accept(Visitor<R> v){
return v.visit(this);
}

}
