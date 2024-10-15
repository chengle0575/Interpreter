package Lox;

import Lox.Declaration.Statement.Stmt;
import Lox.Exp.Expression;

import java.util.List;

public class LoxFunction implements LoxCallable{
    //used in the interpreter to store the function body
    List<Token> parameters;
    List<Stmt>  funtionbody;

    public LoxFunction(List<Token> parameters,List<Stmt>  funtionbody){
        this.parameters=parameters;
        this.funtionbody=funtionbody;
    }

    public Object call(List<Expression> arguments){ //the interpreter will pass arguments into the function to replace the parameters
        return null;
    }
}
