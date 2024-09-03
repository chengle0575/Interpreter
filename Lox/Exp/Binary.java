package Lox.Exp;
import Lox.Token;
import Lox.Exp.Expression;
import Lox.Exp.Visitor;
public class Binary extends Expression {
public final Expression left;
public final Token operator;
public final Expression right;
public Binary(Expression left,Token operator,Expression right) { 
this.left=left;
this.operator=operator;
this.right=right;
};
@Override
public <R> R accept(Visitor<R> v){
return v.visit(this);
}

}
