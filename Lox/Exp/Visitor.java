package Lox.Exp;
import Lox.Exp.*;
import Lox.Statement.Stmt;

public interface Visitor<R>{
public R visit(Grouping grouping);
public R visit(Unary unary);
public R visit(Binary binary);
public R visit(Literal literal);
public R visit(Stmt stmt);
}
