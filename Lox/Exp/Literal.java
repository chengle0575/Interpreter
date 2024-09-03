package Lox.Exp;
import Lox.Token;
import Lox.Exp.Expression;
import Lox.Exp.Visitor;
public class Literal extends Expression {
public final Object value;
public Literal(Object value) { 
this.value=value;
};
@Override
public <R> R accept(Visitor<R> v){
return v.visit(this);
}

}
