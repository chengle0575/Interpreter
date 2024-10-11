package Lox.Exp;
import Lox.Declaration.Statement.BlockStmt;
import Lox.Declaration.Statement.IfStmt;
import Lox.Declaration.Statement.Stmt;
import Lox.Exp.*;
public interface Visitor<R>{
public R visit(Grouping grouping);
public R visit(Unary unary);
public R visit(Binary binary);
public R visit(Variable variable);
public R visit(Literal literal);
public R visit(Stmt stmt);
public R visit(Assign assign);
public R visit(BlockStmt blockStmt);
public R visit(IfStmt ifStmt);
public R visit(LogicOpration logicOpration);
}
