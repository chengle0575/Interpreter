package Lox;

import Lox.Declaration.Statement.*;
import Lox.Exp.*;

import java.security.spec.ECPoint;
import java.util.*;

public class Interpreter implements Visitor {


    private Environment globalEnv=new Environment();
    private Environment env=globalEnv;

    private Map<Expression,Integer> bindingHops=new HashMap<>();

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
            Token identifier=((VarStmt) stmt).getIdentifier();

            env.declare(identifier,evaluateExpression(stmt.getExp()));
            //env.assign(identifier, evaluateExpression(stmt.getExp()));
        }
        return null;
    }

    @Override
    public Object visit(Assign assign) {
         Token identifier=assign.getName();

         //int hopnum=getHop(identifier);

         //if(hopnum==-1){
             if(globalEnv.get(identifier)!=null){ //this will check if identifier is exsiting in map and throw error if not
                 globalEnv.assign(identifier.literal,evaluateExpression(assign.getValue()));
             }else throw new RuntimeError(identifier,"Not declared yet.");
         //} else{
             //if(env.get(identifier,hopnum)!=null){ //this will check if identifier is exsiting in map and throw error if not
         //        env.assign(identifier.literal,evaluateExpression(assign.getValue()));
             //}
         //}

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

        Token callableName=((Variable)call.getFunctionName()).getName();
        List<List<Expression>> argmentsLists=call.getArgmentsList();
        Object value=null;

        for(List<Expression> argumentList:argmentsLists){

            Object loxCallable=env.get(callableName);


            if(loxCallable instanceof LoxFunction){
                LoxFunction callee=(LoxFunction) loxCallable;

                //test the same size of argume
                if(argumentList.size()!=callee.arity())
                    throw new RuntimeError(callableName,"The arguments you passed are less/more than requirement");

                List<Object> argumentListAftEvaluation=getArgumentListAftEvaluation(argumentList);

                env=new Environment(((LoxFunction) loxCallable).closure);
                value=callee.call(this,argumentListAftEvaluation); //need to create a new env for the running function
                env=env.getOuterEnv();//exist the function env

            }else if(loxCallable instanceof LoxClass){
                LoxClass classTemplate=(LoxClass) loxCallable;

                //test the same size of argume
               // if(argumentList.size()!=classTemplate.arity())
                 //   throw new RuntimeError(callableName,"The arguments you passed are less/more than requirement");

                List<Object> argumentListAftEvaluation=getArgumentListAftEvaluation(argumentList);

                env=new Environment(((LoxClass) loxCallable).closure);
                value=classTemplate.call(this,argumentListAftEvaluation); //need to create a new env for the running function
                env=env.getOuterEnv();//exist the function env
            }

            else{
                throw new RuntimeError(callableName,"Cannot find function");
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

    @Override
    public Object visit(ClassStmt classStmt) {
        Token classname=classStmt.getClassname();
        List<Stmt> methods=classStmt.getMethods();

        env.declare(classname,new LoxClass(classname.literal,methods,env));
        return null;
    }

    @Override
    public Object visit(Get get) {
        return null;
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
        int hopnum=getHop(variable);
        if(hopnum==-1){
            return globalEnv.get(variable.getName());
        }else
            return env.get(variable.getName(),hopnum);
    }

    @Override
    public Object visit(Literal literal) {

        return literal.value; //convert a tree node into a runtime value
    }




    public void bind(Expression exp,int hop){
        //bind the token & hops away from its current scope
        if(hop==-1) return;

        if(bindingHops.containsKey(exp)) ; //means already binds. To follow the static scope rule, do not bind again
        else{
            bindingHops.put(exp,hop);
        }
        //the Token need to make sure can be identifier. think about equals and hashcode method under Token class && whether the Parser finish its part of job

    }

    public int getHop(Expression exp){
        if(bindingHops.containsKey(exp))
            return bindingHops.get(exp);
        return -1;
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
