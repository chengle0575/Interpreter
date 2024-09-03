package Lox.Exp;
public abstract class Expression {
public abstract <R> R accept(Visitor<R> v);
}
