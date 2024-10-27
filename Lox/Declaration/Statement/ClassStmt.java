package Lox.Declaration.Statement;

import Lox.Exp.Visitor;
import Lox.Token;

import java.util.List;

public class ClassStmt extends Stmt{
    private Token classname;
    private List<Stmt> methods;

    public ClassStmt(Token classname, List<Stmt> methods){
        this.classname=classname;
        this.methods=methods;
    }

    public Token getClassname(){
        return  classname;
    }
    public List<Stmt> getMethods() {
        return methods;
    }



    public Object accept(Visitor v){
        return v.visit(this);
    }
}
