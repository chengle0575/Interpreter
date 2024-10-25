package Lox;

import Lox.Declaration.Statement.*;
import Lox.Exp.*;

import java.security.spec.ECPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Interpreter implements Visitor {

    private Environment env=new Environment();

    public Object execute(List<Stmt> stmtlist){
        for(Stmt stmt:stmtlist){
            evaluateStatement(stmt);
        }
        return  null;
    }


    public Environment getEnv(){
        return env;
    }
    private Object evaluateStatement(Stmt stmt){
        return stmt.accept(this);
    }
    private Object evaluateExpression(Expression exp){
        return exp.accept(this);
    }




    @Override
    public Object visit(Stmt stmt) {
        if(stmt instanceof PrintStmt) {
            System.out.println(evaluateExpression(stmt.getExp()));
            return null;
        } else if (stmt instanceof ExprStmt){
            Expression exp=stmt.getExp();
            return evaluateExpression(exp);
        } else if (stmt instanceof VarStmt){
            String identifier=((VarStmt) stmt).getIdentifier().literal;
            env.assign(identifier, evaluateExpression(stmt.getExp()));
        }
        return null;
    }

    @Override
    public Object visit(Assign assign) {
        ;Token identifier=assign.getName();
         if(env.get(identifier)!=null){ //this will check if identifier is exsiting in map and throw error if not
             env.assign(identifier.literal,evaluateExpression(assign.getValue()));
         }
         return null;
    }

    @Override
    public Object visit(BlockStmt blockStmt) {
        env=new Environment(env); //current env is a new env. point to its parent env

        for(Stmt stmt:blockStmt.getStmtslist()){
            evaluateStatement(stmt);
        }

        env=env.getOuterEnv();
        return null;
    }

    @Override
    public Object visit(IfStmt ifStmt) {
        if(evaluateExpression(ifStmt.getConditionExp()).equals(true))
            return evaluateStatement(ifStmt.getIfstmt());
        else{
            if(ifStmt.getElsestmt()!=null)
                return evaluateStatement(ifStmt.getElsestmt());
        }
        return null;
    }

    @Override
    public Object visit(LogicOpration logicOpration) {
        List<Expression> expl=logicOpration.getOperands();
        Token operator=logicOpration.getOperator();

        if(expl.size()==1)
            return evaluateExpression(expl.get(0));

        if(operator.type==TokenType.AND){
            for(Expression exp:expl){
                if(evaluateExpression(exp)==null || evaluateExpression(exp).equals(false)){
                    return evaluateExpression(exp);
                }
            }
            return true;
        }else{
            for(Expression exp:expl){
                if(!Objects.equals(evaluateExpression(exp),false)&&!Objects.equals(evaluateExpression(exp),null))
                    return evaluateExpression(exp);
            }

            return false;
        }
    }

    @Override
    public Object visit(WhileStmt whileStmt) {
        while(isTruth(evaluateExpression(whileStmt.getCondition()))){
            evaluateStatement(whileStmt.getLoopbody());
        }
        return null;
    }

    @Override
    public Object visit(FuncStmt funcStmt) {
        //declare a function and store it in the env
        Token identifier=funcStmt.getIdentifier();
        LoxFunction loxFunction=new LoxFunction(funcStmt.getParameters(),funcStmt.getFunctionContent(),env);
        env.assign(identifier.literal,loxFunction);

        return null;
    }

    @Override
    public Object visit(Call call) {

        Token functionName=((Variable)call.getFunctionName()).getName();
        List<List<Expression>> argmentsLists=call.getArgmentsList();
        Object value=null;

        for(List<Expression> argumentList:argmentsLists){
            Object loxFunction=env.get(functionName);

            if(loxFunction instanceof LoxFunction){
                LoxFunction callee=(LoxFunction) loxFunction;

                //test the same size of argume
                if(argumentList.size()!=callee.arity())
                    throw new RuntimeError(functionName,"The arguments you passed are less/more than requirement");

                List<Object> argumentListAftEvaluation=getArgumentListAftEvaluation(argumentList);

                env=new Environment(((LoxFunction) loxFunction).closure);
                value=callee.call(this,argumentListAftEvaluation); //need to create a new env for the running function
                env=env.getOuterEnv();//exist the function env

            }else{
                throw new RuntimeError(functionName,"Cannot find function");
            }

        }
        return value;
    }

    @Override
    public Object visit(ReturnStmt returnStmt) {
        if (returnStmt.getValue()==null)
            throw new ReturnValue(null);
        throw new ReturnValue( evaluateExpression(returnStmt.getValue()));
    }

    //helper function
    List<Object> getArgumentListAftEvaluation(List<Expression> l){
        List<Object> res=new ArrayList<>();

        for(Expression exp:l){
            res.add(evaluateExpression(exp));
        }
        return res;
    }

    @Override
    public Object visit(Grouping grouping) {
        return evaluateExpression(grouping.exp);
    }

    @Override
    public Object visit(Unary unary) {
        Object right=evaluateExpression(unary.right);

        switch (unary.operator.type){

            case MINUS:
                checkNumOperand(unary.operator,right);
                return -(double)right;
            case BANG:
                return !isTruth(right);
        }
        return null;
    }


    @Override
    public Object visit(Binary binary) {

            Object left=evaluateExpression(binary.left);
            Object right=evaluateExpression(binary.right);

            switch(binary.operator.type){

                case PLUS: //the '+' operator can be used for both add values and concat strings
                    if(left==null && right instanceof Double)
                        return (double)right;
                    else if(right==null && left instanceof Double)
                        return (double)left;
                    else if(left==null && right instanceof String)
                        return (String)right;
                    else if(right==null &&left instanceof String)
                        return (String)left;
                    else if(left instanceof Double && right instanceof Double)
                        return (double)left+(double)right;
                    else if(left instanceof String && right instanceof String)
                        return (String)left+(String)right;
                    throw new RuntimeError(binary.operator,"Two operands should both be number of string");
                case MINUS:
                    checkNumOperands(binary.operator,left,right);
                    return (double)left-(double)right;
                case SLASH:
                    checkNumOperands(binary.operator,left,right);
                    checkZeroDivision(binary.operator,right);
                    return (double)left/(double) right;
                case STAR:
                    checkNumOperands(binary.operator,left,right);
                    return (double)left*(double) right;
                case GREATER:
                    checkNumOperands(binary.operator,left,right);
                    return (double)left>(double) right;
                case GREATER_EQUAL:
                    checkNumOperands(binary.operator,left,right);
                    return (double)left>=(double) right;
                case LESS:
                    checkNumOperands(binary.operator,left,right);
                    return (double)left<(double) right;
                case LESS_EQUAL:
                    checkNumOperands(binary.operator,left,right);
                    return (double)left<=(double) right;
                case BANG_EQUAL:
                    return !Objects.equals(left,right);
                case EQUAL_EQUAL:
                    return Objects.equals(left,right);
            }


        return null;
    }

    @Override
    public Object visit(Variable variable) { //deal with variable in the right hand side in the expression
        return env.get(variable.getName());
    }

    @Override
    public Object visit(Literal literal) {

        return literal.value; //convert a tree node into a runtime value
    }


    public void bind(Token token,int hop){
        //bind the token & hops away from its current scope

        //the Token need to make sure can be identifier. think about equals and hashcode method under Token class && whether the Parser finish its part of job
    }


    //helper functions
    private static boolean isTruth(Object o){
        if(Objects.equals(o,null)|| Objects.equals(o,false))
            return false;
        return true;
    }


    private void checkNumOperand(Token operator,Object operand){
        if(operand instanceof Double) return;
        throw new RuntimeError(operator,"The operand in not a number");

    }
    
    private void checkNumOperands(Token operator,Object left,Object right){
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator,"Both the operands should be number");
    }
    
    private void checkZeroDivision(Token operator,Object right){
        if(right instanceof Double && (Double)right==0)
            throw new RuntimeError(operator,"Cannot divide by zero");
    }
}
