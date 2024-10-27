package Lox;

import Lox.Declaration.Statement.*;
import Lox.Exp.*;

import java.util.*;

public class Resolver implements Visitor {
    // the main job of this resolver is:
    // 1. traverse the AST tree,
    // 2. resolve the scope --> figure out the nodes involve:
    // 3. and defer the scope binding information to the interpreter
    // 4. detect meaningless return statement outside of a function and report error

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
            if(this.stack.size()>0){
                if(this.stack.peek().containsKey(identifier))
                    throw new ParseError(identifier,"Duplication definiation on the same identifier");
                this.stack.peek().put(identifier,true);// means the value of this identifier exists in current scope
            }

        }

        return null;
    }


    @Override
    public Object visit(BlockStmt blockStmt) {
        addStackEntry();
        //do something
        resolve(blockStmt.getStmtslist());

        popSrackEntry();
        return null;

    }

    //helper function
    void addDeclarationInCurrentScope(Token identifier){ // var a;
        Map<Token,Boolean> currentScope=this.stack.peek();
        if(currentScope.containsKey(identifier) && currentScope.get(identifier)==true)
            throw new ParseError(identifier,"Duplication definiation on the same identifier");
        currentScope.put(identifier,false); //means now this scope can find this variable but not any value;
    }
    void addDefinitionInAccordinglyScope(Token identifier){ // a=3
        //should throw error if the identifier already has a value binded? /////////////////////////////////////////////////////

        Map<Token,Boolean> toModifyScope=searchOutwardsForScope(identifier);
        if(toModifyScope==null) //means this is in the global env
            return;
        toModifyScope.put(identifier,true);// means the value of this identifier exists in current scope
    }



    //node that involve define a variable in current scope
    @Override
    public Object visit(FuncStmt funcStmt) {

        if(this.stack.size()>0){
            addDeclarationInCurrentScope(funcStmt.getIdentifier());
            addDefinitionInAccordinglyScope(funcStmt.getIdentifier());
        }

        resolve(funcStmt.getFunctionContent());
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

        //detect if there is a function

        resolve(returnStmt.getValue());
        return null;
    }

    @Override
    public Object visit(ClassStmt classStmt) {
        if(this.stack.size()>0){
            addDeclarationInCurrentScope(classStmt.getClassname());
            addDefinitionInAccordinglyScope(classStmt.getClassname());
        }

        resolve(classStmt.getMethods());
        return null;



    }


    @Override
    public Object visit(Assign assign) {//need to deal with assignment

        Token identifier=assign.getName();
        if(this.stack.size()>0)
            addDefinitionInAccordinglyScope(identifier);

        resolve(assign.getValue());
        return null;
    }




    @Override
    public Object visit(LogicOpration logicOpration) {
        for(Expression exp:logicOpration.getOperands()){
            resolve(exp);
        }
        return null;
    }


    @Override
    public Object visit(Grouping grouping) {
        resolve(grouping.exp);
        return null;
    }

    @Override
    public Object visit(Unary unary) {
        resolve(unary.right);
        return null;
    }

    @Override
    public Object visit(Binary binary) {
        resolve(binary.left);
        resolve(binary.right);
        return null;
    }

    @Override
    public Object visit(Literal literal) {
        return null;
    }

    @Override
    public Object visit(Call call) {
        resolve(call.getFunctionName());
        for(List<Expression> expressionList:call.getArgmentsList()){
            for(Expression exp:expressionList)
                resolve(exp);
        }

        return null;
    }



    //These nodes involve variable binding
    @Override
    public Object visit(Variable variable) {
        //search from current scope && send the binding information to the interpreter
        int hopnum=searchOutwards(variable.getName());
        interpreter.bind(variable,hopnum);
        return null;
    }



    //helper function
    private int searchOutwards(Token name){ //keep searching following the stack
        Queue<Map<Token,Boolean>> saveStack=new ArrayDeque<>();

        int count=0;

        while(stack.size()>0){
            Map<Token,Boolean> seachStack=stack.pop();
            saveStack.add(seachStack);
            if(seachStack.containsKey(name)&&seachStack.get(name))
                break;
            count+=1;

        }

        //restore the stack
        while(saveStack.size()>0){
            stack.add(saveStack.poll());
        }

        if(count==stack.size()) return -1; //means cannot find this variable in the stack, implies that this variable is global
        return count;
    }

    private Map<Token,Boolean> searchOutwardsForScope(Token name){
        Queue<Map<Token,Boolean>> saveStack=new ArrayDeque<>();
        Map<Token,Boolean> res=null;
        while(stack.size()>0){
            Map<Token,Boolean> seachStack=stack.pop();
            saveStack.add(seachStack);

            if(seachStack.containsKey(name)&&seachStack.get(name)==false){
                res=seachStack;
                break;
            }

        }

        //restore the stack
        while(saveStack.size()>0){
            stack.add(saveStack.poll());
        }

        return res;
    }

    private void addStackEntry(){
        Map<Token,Boolean> scope=new HashMap<>();
        stack.add(scope);
    }

    private void popSrackEntry(){
        stack.pop();
    }









}
