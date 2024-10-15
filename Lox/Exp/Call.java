package Lox.Exp;

import java.util.List;

public class Call extends Expression{
    Expression functionName;
    //foo(a,b,c)(e,f)(..)
    List<List<Expression>> argmentsList;

    public Call(Expression functionname,List<List<Expression>> argmentsList){
       this.functionName=functionname;
       this.argmentsList=argmentsList;
    }

    public  List<List<Expression>> getArgmentsList(){
        return argmentsList;
    }
    @Override
    public <R> R accept(Visitor<R> v) {
        return null;
    }
}
