package Lox.Exp;
import Lox.Token;
import Lox.Exp.Expression;
public class Grouping{
public final Expression exp;
public Grouping(Expression exp) { 
this.exp=exp;
};
}
