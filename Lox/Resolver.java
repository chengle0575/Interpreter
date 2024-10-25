package Lox;

import Lox.Declaration.Statement.*;
import Lox.Exp.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Visitor {
    // the main job of this resolver is:
    // 1. traverse the AST tree,
    // 2. resolve the scope --> figure out the nodes involve:
    // 3. and defer the scope binding information to the interpreter

    private Interpreter interpreter;

    public Resolver(Interpreter interpreter){
        this.interpreter=interpreter;
    }

    Stack<Map<Token,Boolean>> stack=new Stack<>();

    void resolve(List<Stmt> stmts){
        for(Stmt stmt:stmts){
            resolve(stmt);
        }
    }



    void resolve(Stmt stmt){
        stmt.accept(this);
    }
    void resolve(Expression expression){
        expression.accept(this);
    }




    //some nodes involve define in scope. put 'true' in current scope to tag the existence of the variable.
    @Override
    public Object visit(Stmt stmt) {
        if(stmt instanceof PrintStmt){ //->print expression?;
            resolve(((PrintStmt)stmt).getExp());
        }
        else if(stmt instanceof ExprStmt){ // exprstmt -> expression ";"
            resolve(((ExprStmt)stmt).getExp());
        }
        else if(stmt instanceof VarStmt){
            //need to deal with declaration
            //put this variable binding into the scope
            Token identifier=((VarStmt) stmt).getIdentifier();
            if(this.stack.peek().containsKey(identifier))
                throw new ParseError(identifier,"Duplication definiation on the same identifier");
            this.stack.peek().put(identifier,true);// means the value of this identifier exists in current scope
        }

        return null;
    }


    @Override
    public Object visit(BlockStmt blockStmt) {

        Map<Token,Integer> scope=new HashMap<>();
        addStackEntry(scope);
        //do something
        resolve(blockStmt);

        popSrackEntry();
        return null;

    }

    //helper function
    void addDeclarationInCurrentScope(Token identifier){

    }
    void addDefinitionInCurrentScope(Token identifier){


    }

    //node that involve define a variable in current scope
    @Override
    public Object visit(FuncStmt funcStmt) {


        //open scope for the content in a function
        Map<Token,Integer> scope=new HashMap<>();
        addStackEntry(scope);
        resolve(funcStmt.getFunctionContent());
        popSrackEntry();
        return null;
    }

    @Override
    public Object visit(IfStmt ifStmt) {
        resolve(ifStmt.getConditionExp());
        resolve(ifStmt.getIfstmt());
        resolve(ifStmt.getElsestmt());
        return null;
    }

    @Override
    public Object visit(WhileStmt whileStmt) {
        resolve(whileStmt.getCondition());
        resolve(whileStmt.getLoopbody());
        return null;
    }



    @Override
    public Object visit(ReturnStmt returnStmt) {
        resolve(returnStmt.getValue());
        return null;
    }


    @Override
    public Object visit(Assign assign) {//need to deal with assignment

        Token identifier=assign.getName();
        // if(this.stack.peek().containsKey(identifier))
        //     throw new RuntimeError(identifier,"Duplication definiation on the same identifier");
        this.stack.peek().put(identifier,true);// means the value of this identifier exists in current scope


        return null;
    }




    @Override
    public Object visit(LogicOpration logicOpration) {
        return null;
    }






    @Override
    public Object visit(Grouping grouping) {
        return null;
    }

    @Override
    public Object visit(Unary unary) {
        return null;
    }

    @Override
    public Object visit(Binary binary) {
        return null;
    }


    //These nodes involve variable binding
    @Override
    public Object visit(Variable variable) {
        //search from current scope && send the binding information to the interpreter
        int hopnum=searchOutwards(variable.getName());
        interpreter.bind(variable.getName(),hopnum);
        return null;
    }



    @Override
    public Object visit(Literal literal) {
        return null;
    }

    @Override
    public Object visit(Call call) {
        return null;
    }









    //helper function
    int searchOutwards(Token name){

    }

    //helper function
    void addStackEntry(Map<Token,Integer> scope){
        stack.add(scope);
    }

    void popSrackEntry(){
        stack.pop();
    }









}
