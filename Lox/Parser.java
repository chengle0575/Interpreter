package Lox;

import Lox.Declaration.Statement.*;
import Lox.Exp.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;


public class Parser {

    List<Token> input;
    int p=0;
    List<Stmt> stmts;


    Parser(List<Token> input){
        this.input=input;
        stmts=new ArrayList<>();
    }



    public List<Stmt> generateStmts(){
        while(!reachEnd(p)){
            stmts.add(declaration());
        }

        return stmts;
    }

    private Stmt declaration(){ //declaration -> funDecl || valDecl || statement
        try{
            if(match((TokenType.CLASS))) return classDecl();
            else if((match(TokenType.FUN))) return funDecl();
            else if(match(TokenType.VAR)) return valDecl();
            else return statement();
        }catch (ParseError e){
            System.out.println(e.message);
            //update pointer p to be ready for the next declaration to parse
            panicRecoverFromError();
        }

        return null;
    }

    private Stmt classDecl(){
        moveahead();
        Token identifier=input.get(p);
        moveahead();

        if(!match(TokenType.LEFT_PAREN))
            throw new ParseError(identifier,"Lack '{' after CLASS"+identifier);
        moveahead();

        List<Stmt> methods=new ArrayList<>();
        while(!reachEnd(p)&&!match(TokenType.RIGHT_PAREN) ){
            methods.add(function());
        }

        if(reachEnd(p))
            throw new ParseError(identifier,"Lack '}' at end of CLASS"+identifier);

        moveahead();

       return new ClassStmt(identifier,methods);
    }


    private Stmt funDecl(){
        moveahead(); //consume 'fun' keyword
        Stmt func=function();
        return func;
    }

    private Stmt function(){
        Token identifier=this.input.get(p);
        moveahead();

        if(this.input.get(p).type!=TokenType.LEFT_BRACE)
            throw new ParseError("Function parameters should be wrapped in '()',lack '(' here");
        moveahead();


        List<Token> parameters=new ArrayList<>();
        List<Stmt> functionContent=null;

        if(this.input.get(p).type!=TokenType.RIGHT_BRACE){
            parameters=parameters();

            if(this.input.get(p).type!=TokenType.RIGHT_BRACE)
                throw new ParseError("Function parameters should be wrapped in '()',lack ')' here");
            moveahead();

            functionContent=block();
        }else{ //implies no paramters
            moveahead();
            functionContent=block();
        }


        return new FuncStmt(identifier,parameters,functionContent);
    }

    //helper for function()
    private List<Token> parameters(){
        List<Token> parameterList=new ArrayList<>();
        Token identifier=this.input.get(p);
        parameterList.add(identifier);
        moveahead();

        while(match(TokenType.COMMA)){
            moveahead();
            Token t=this.input.get(p);
            parameterList.add(t);
            moveahead();
        }

        return parameterList;
    }


    private Stmt valDecl(){ //left the var declaration be seperately deal with
        moveahead(); //consume the 'var' keyword;
        Token identifier= this.input.get(p);
        moveahead();

        if(this.input.get(p).type==TokenType.SEMICOLON){
            skipEndSemicolon();
            return new VarStmt(identifier,new Literal(null));
        }



        moveahead();
        Expression exp=expression();
        skipEndSemicolon();
        return new VarStmt(identifier,exp);

    }

    private Stmt statement(){ //statement -> printStmt || exprStmt || block

        if(match(TokenType.RETURN)) return returnStmt();
        else if(match(TokenType.PRINT)) return printStatement();
        else if(match(TokenType.LEFT_PAREN)) return new BlockStmt(block()); /////////////////
        else if(match(TokenType.IF)) return ifStmt();
        else if(match(TokenType.WHILE)) return whileStmt();
        else if(match(TokenType.FOR)) return forStmt();
        else return expressionStatement();
    }



    private Stmt returnStmt(){
        moveahead();
        if(match(TokenType.SEMICOLON))
            return new ReturnStmt(null);

        Expression exp=expression();
        skipEndSemicolon();
        return new ReturnStmt(exp);
    }

    private List<Stmt> block(){
        //find the last "}", and parse inside
        moveahead();

        List<Stmt> blockstmt=new ArrayList<>();

        while(!match(TokenType.RIGHT_PAREN)){
            blockstmt.add(declaration());
        }
        moveahead();
        return blockstmt;

    }

    private PrintStmt printStatement(){
        moveahead(); //ignore this print identifier
        Expression exp=expression();
        skipEndSemicolon();
        return new PrintStmt(exp);
    }

    private IfStmt ifStmt(){//parse the if statement here, where p is pointing to 'IF' now
        moveahead();
        if(this.input.get(p).type!=TokenType.LEFT_BRACE)
            throw new ParseError("the condition in If statement should be wrapped in braces '()', lack '(' here");

        moveahead();
        Expression conditionExp=expression();

        if(this.input.get(p).type!=TokenType.RIGHT_BRACE)
            throw new ParseError("the condition in If statement should be wrapped in braces '()' , lack ')' here");

        moveahead();
        Stmt ifStmt=statement();

        Stmt elseStmt=null;
        if(match(TokenType.ELSE)){
            moveahead();
            elseStmt=statement();
        }


        return new IfStmt(conditionExp,ifStmt,elseStmt);
    }

    private WhileStmt whileStmt(){
        moveahead();
        if(this.input.get(p).type!=TokenType.LEFT_BRACE)
            throw new ParseError("The condition in while loop should be wrapped in '()', lack '(' here");
        moveahead();
        Expression condition=expression();
        if(this.input.get(p).type!=TokenType.RIGHT_BRACE)
            throw new ParseError("The condition in while loop should be wrapped in '()', lack ')' here");
        moveahead();
        Stmt loopbody=statement();

        return new WhileStmt(condition,loopbody);
    }

    private Stmt forStmt(){
        moveahead();
        if(this.input.get(p).type!=TokenType.LEFT_BRACE)
            throw new ParseError("The condition in for loop should be wrapped in '()', lack '(' here");
        moveahead();

        Stmt initilizer=null;


        if(match(TokenType.VAR))
            initilizer=valDecl();
        else if(!match(TokenType.SEMICOLON))
            initilizer=expressionStatement();
        else
            moveahead();

        Expression condition=null;
        if(!match(TokenType.SEMICOLON))
            condition=expression();
        moveahead();

        Expression loopbody1=null;
        if(!match(TokenType.SEMICOLON))
            loopbody1=expression();
        moveahead();
        moveahead();

        Stmt loopboday2=statement();

        /*use desugaring technique to change the enhanced for loop to the form of while loop
        * {
            * initializer;
            * while(condition){
            *       loopboday2;
            *       loopboday1;
            * }
        * }*/
        //have to dead with the null here. because null here is not a valid AST node for furthur interpretion
        List<Stmt> stmtlistInloop=new ArrayList<>();
        stmtlistInloop.add(loopboday2);

        if(loopbody1!=null)
            stmtlistInloop.add(new ExprStmt(loopbody1));

        if(condition==null)
            condition=new Literal(true);

        WhileStmt whileStmt=new WhileStmt(condition,new BlockStmt(stmtlistInloop));

        if(initilizer!=null)
            return new BlockStmt(Arrays.asList(initilizer,whileStmt));
        else
            return whileStmt;
    }

    private  ExprStmt expressionStatement(){
        Expression exp=expression();
        skipEndSemicolon();
        return new ExprStmt(exp);
    }

    private void skipEndSemicolon(){
        if(this.input.get(p).type!=TokenType.SEMICOLON)
            throw new ParseError("This statement need to end with semicolon");
        moveahead();
    }


    private Expression expression(){ //expression->equality
        return assignment();
    }

    private Expression assignment(){ //assignment-> identifier "=assignment" | equality
        if(match(TokenType.IDENTIFIER)){
            Token identifier=this.input.get(p);
            p++;
            if(match(TokenType.EQUAL) && !this.input.get(p+1).type.equals(TokenType.EQUAL)){
                p++;
                Expression assign2=assignment();
                return new Assign(identifier,assign2);
            }else{// a==b
                p--;
                return logic_or();
            }
        }else{
            return logic_or();
        }

    }

    private LogicOpration logic_or(){ //logic_or -> logic_and ("or" logic_and)*

        List<Expression> operandsForOr=new ArrayList<>();

        LogicOpration logicAnd=logic_and();
        operandsForOr.add(logicAnd);
        while(match(TokenType.OR)){
            moveahead();
            operandsForOr.add(logic_and());
        }


        return new LogicOpration(operandsForOr,new Token(TokenType.OR));

    }

    private LogicOpration logic_and(){//-> equality ("and" equality)*
        List<Expression> operandsForAnd=new ArrayList<>();

        Expression equalityExp=equality();
        operandsForAnd.add(equalityExp);
        while(match(TokenType.AND)){
            moveahead();
            operandsForAnd.add(equality());
        }


        return new LogicOpration(operandsForAnd,new Token(TokenType.AND));
    }

    private Expression equality(){//equality-> comparison (((!=|==) comparison))*

        Expression exp=comparison();

        while(match(TokenType.BANG_EQUAL)||match(TokenType.EQUAL_EQUAL)){
                Token t=this.input.get(this.p);
                moveahead();
                Expression exp2= comparison();
                exp=new Binary(exp,t,exp2);
        }

        return exp;
    }

    private  Expression comparison(){

        Expression exp=term();

        while(match(TokenType.GREATER)||match(TokenType.GREATER_EQUAL)||match(TokenType.LESS)||match(TokenType.LESS_EQUAL)){
            Token t=this.input.get(this.p);
            moveahead();
            Expression exp2=term();
            exp=new Binary(exp,t,exp2);
        }

        return exp;
    }

    private Expression term(){

        Expression exp=factor();

        while(match(TokenType.PLUS)||match(TokenType.MINUS)){
            Token t=this.input.get(this.p);
            moveahead();
            Expression exp2=factor();
            exp=new Binary(exp,t,exp2);
        }

        return exp;
    }

    private Expression factor(){

        Expression exp=unary();

        while(match(TokenType.SLASH)||match(TokenType.STAR)){
            Token t=this.input.get(this.p);
            moveahead();
            Expression exp2=unary();
            exp=new Binary(exp,t,exp2);
        }

        return exp;

    }

    private Expression unary(){

        while(match(TokenType.BANG)||match(TokenType.MINUS)){
            Token t=this.input.get(this.p);
            moveahead();
            return new Unary(t,unary());
        }

        return call();
    }

    private Expression call(){ // call -> primary ( "(" arguments? ")")*

        Expression primaryExp=primary();

        List<List<Expression>> argmentsList=new ArrayList<>();

        while (match(TokenType.LEFT_BRACE)){
            //find all arguments
            moveahead();
            List<Expression> arg=argument();
            argmentsList.add(arg);
            moveahead();
        }
        if(argmentsList.isEmpty())
            return primaryExp;
        else
            return new Call(primaryExp,argmentsList);
    }

    private List<Expression> argument(){
        List<Expression> argments=new ArrayList<>();

        if(match(TokenType.RIGHT_BRACE)) //means 0 arguments
            return argments;

        Expression exp=expression();
        argments.add(exp);

        while(match(TokenType.COMMA)){
            moveahead();
            Expression exp2=expression();
            argments.add(exp2);
        }

        return argments;

    }

    private Expression primary(){

        if(match(TokenType.IDENTIFIER)){
            moveahead();
            return new Variable(this.input.get(this.p-1));
        }
        if(match(TokenType.NUMBER)){
            moveahead();
            return new Literal((double)Integer.parseInt(this.input.get(this.p-1).literal));
        }

        if(match(TokenType.STRING)){
            moveahead();
            return new Literal(this.input.get(this.p-1).literal);
        }
       if(match(TokenType.TRUE)) {
           moveahead();
           return new Literal(true);
       }
       if(match(TokenType.FALSE)) {
           moveahead();
           return new Literal(false);
       }
       if(match(TokenType.NIL)){
           moveahead();
           return new Literal(null);
       }

       if(match(TokenType.LEFT_BRACE)){
            //find the next right brace
           int pt=this.input.size()-1;
           while(pt>this.p){
               if(this.input.get(pt).type==TokenType.RIGHT_BRACE){//find the outer brace
                   break;
               }
               pt--;
           }

           moveahead();

           if(pt>this.p && pt==this.input.size()-1){
               return new Grouping(new Parser(this.input.subList(p,pt)).expression());
           } else if(pt>this.p){
               return new Binary(new Grouping(expression()),this.input.get(pt+1),new Parser(this.input.subList(pt+2,this.input.size())).expression());
           }

       }

       throw new ParseError(this.input.get(this.p-1).line+"Parser Error: invalid "+this.input.get(this.p-1).type.toString());
    }


    //helper fundtions here
    private boolean reachEnd(int pt){
        return pt>=this.input.size()-1;
    }

    private boolean match(TokenType ty){
        if(this.p>=this.input.size())
            return false;
        return this.input.get(this.p).type==ty;
    }

    private void moveahead(){
        this.p++;
    }

    private boolean isPrintStmt(int i){
        if(this.input.get(i).type==TokenType.PRINT) return true;
        return false;
    }

    private void panicRecoverFromError(){
        if(reachEnd(p))
            return;

        while(this.input.get(p).type!=TokenType.SEMICOLON){
            p++;
        }
        p++;
    }

}
