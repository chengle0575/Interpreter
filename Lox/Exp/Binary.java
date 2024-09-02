package Lox.Exp;
import Lox.Token;
import Lox.Exp.Expression;
public class Binary{
public final Expression left;
public final Token operator;
public final Expression right;
public Binary(Expression left,Token operator,Expression right) { 
this.left=left;
this.operator=operator;
this.right=right;
};
}
