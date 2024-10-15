package Lox.Exp;

import Lox.Token;

import java.util.List;

public class Call extends Expression{
    Expression functionName;
    //foo(a,b,c)(e,f)(..)
    List<List<Expression>> argmentsLists;

    public Call(Expression functionname,List<List<Expression>> argmentsList){
       this.functionName=functionname;
       this.argmentsLists=argmentsList;
    }
    public  Expression getFunctionName(){return functionName;};
    public  List<List<Expression>> getArgmentsList(){
        return argmentsLists;
    }
    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}
