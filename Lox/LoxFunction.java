package Lox;

import Lox.Declaration.Statement.Stmt;
import Lox.Exp.Expression;

import java.util.List;

public class LoxFunction implements LoxCallable{
    //used in the interpreter to store the function body
    List<Token> parameters;
    List<Stmt>  funtionbody;
    Environment closure; //to save the snapshot of current environment whose variable maybe used inside this function

    public LoxFunction(List<Token> parameters,List<Stmt>  funtionbody, Environment closure){
        this.parameters=parameters;
        this.funtionbody=funtionbody;
        this.closure=closure;
    }
    public int arity(){
        return parameters.size();
    }
    @Override
    public Object call(Interpreter interpreter,List<Object> arguments){ //the interpreter will pass arguments into the function to replace the parameters

        try{
            //pair parameters and arguments, store in the interpreter's cur env
            for(int i=0;i<arity();i++){
                interpreter.getEnv().assign(parameters.get(i).literal,arguments.get(i));
            }

            //get the body of the function and execute
            interpreter.execute(funtionbody);

        }catch (ReturnValue rv){
            return rv.returnValue;
        }

        return null;
    }
}
