package Lox.Exp;
import Lox.Exp.*;
public interface Visitor<R>{
public R visit(Grouping grouping);
public R visit(Unary unary);
public R visit(Binary binary);
}
