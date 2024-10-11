package Lox;

import Lox.Declaration.Statement.BlockStmt;
import Lox.Declaration.Statement.IfStmt;
import Lox.Exp.*;
import Lox.Declaration.Statement.Stmt;

public class Printer implements Visitor {

    int space=1;

    Printer(){

    }
    Printer(int space){
        this.space=space;
    }
    @Override
    public String visit(Grouping grouping) {
        space++;

        StringBuffer sb=new StringBuffer();
        sb.append(" ".repeat(space)+"/group\n");
        sb.append(" ".repeat(space)+grouping.exp.accept(new Printer(this.space)));

        return sb.toString();
    }

    @Override
    public String visit(Unary unary) {
        space++;

        StringBuffer sb=new StringBuffer();
        sb.append(" ".repeat(space)+"/"+unary.operator.type+"\n");
        sb.append(" ".repeat(space)+unary.right.accept(new Printer(this.space)));

        return sb.toString();
    }

    @Override
    public String visit(Binary binary) {
        space++;

        StringBuffer sb=new StringBuffer();
        sb.append(" ".repeat(space)+"/"+binary.operator.type+"\n");
        sb.append(" ".repeat(space)+binary.left.accept(new Printer(this.space)));

        sb.append(" ".repeat(space)+binary.right.accept(new Printer(this.space)));

        return sb.toString();
    }

    @Override
    public Object visit(Variable variable) {
        return null;
    }

    @Override
    public String visit(Literal literal) {
        space++;
        StringBuffer sb=new StringBuffer();
        sb.append(" ".repeat(space)+"/literal --");
        sb.append(" ".repeat(space)+literal.value.toString()+"\n");
        return sb.toString();
    }

    @Override
    public Object visit(Stmt stmt) {
        return null;
    }

    @Override
    public Object visit(Assign assign) {
        return null;
    }

    @Override
    public Object visit(BlockStmt blockStmt) {
        return null;
    }

    @Override
    public Object visit(IfStmt ifStmt) {
        return null;
    }

    @Override
    public Object visit(LogicOpration logicOpration) {
        return null;
    }
}
