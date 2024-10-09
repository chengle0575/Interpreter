package Lox.Declaration.Statement;

import Lox.Exp.Visitor;

import java.util.List;

public class BlockStmt extends Stmt{
    List<Stmt> stmtslist;

    public BlockStmt(List<Stmt> stmts){
        this.stmtslist=stmts;
    }

    public List<Stmt> getStmtslist(){
        return stmtslist;
    }

    public Object accept(Visitor v){
        return v.visit(this);
    }
}
