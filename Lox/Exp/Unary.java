package Lox.Exp;
import Lox.Token;
import Lox.Exp.Expression;
public class Unary{
public final Token operator;
public final Expression right;
public Unary(Token operator,Expression right) { 
this.operator=operator;
this.right=right;
};
}
