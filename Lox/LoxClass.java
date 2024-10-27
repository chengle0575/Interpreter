package Lox;

import Lox.Declaration.Statement.Stmt;

import java.util.List;

public class LoxClass implements LoxCallable{

    String classname;
    List<Stmt> methods;
    Environment closure;

    public LoxClass(String classname,List<Stmt> methods, Environment closure){
        this.classname=classname;
        this.methods=methods;
        this.closure=closure;
    }


    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        return new LoxInstance(this);
    }

    @Override
    public String toString(){
        return this.classname;
    }
}
