package Lox;

import Lox.Declaration.Statement.*;
import Lox.Exp.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;


public class Parser {

    List<Token> input;
    int p=0;
    List<Stmt> stmts;
    //HashMap<String,Expression> variable_valueMap;

    Parser(List<Token> input){
        this.input=input;
        stmts=new ArrayList<>();
    }



    public List<Stmt> generateStmts(){
        if(this.input.get(this.input.size()-1).type!=TokenType.SEMICOLON)
            this.input.add(new Token(TokenType.SEMICOLON));

        while(!reachEnd(p)){
            stmts.add(declaration());
        }

        return stmts;
    }

    private Stmt declaration(){ //declaration -> valDecl || statement
        try{
            if(match(TokenType.VAR)) return valDecl();
            return statement();
        }catch (ParseError e){
            System.out.println(e);
            //update pointer p to be ready for the next declaration to parse
            panicRecoverFromError();
        }

        return null;
    }



    private Stmt valDecl(){ //left the var declaration be seperately deal with
        moveahead(); //consume the 'var' keyword;
        Token identifier= this.input.get(p);
        p++;
        Token operator=this.input.get(p);
        p++;
        return new VarStmt(identifier,parseExpressionInStatement(p));
    }

    private Stmt statement(){ //statement -> printStmt || exprStmt || block
        if(match(TokenType.PRINT)) return printStatement();
        if(match(TokenType.LEFT_PAREN)) return new BlockStmt(block()); /////////////////
        return expressionStatement();
    }

    private PrintStmt printStatement(){
        moveahead(); //ignore this print identifier
        return new PrintStmt(parseExpressionInStatement(p));
    }

    private List<Stmt> block(){
        //find the last "}", and parse inside
        int start=p+1;
        int q=findPairedRightBrace(p);
        p=q+1;
        return new Parser(input.subList(start,q)).generateStmts();
    }

    private int findPairedRightBrace(int p){ //p point to the left brace now
        Stack<Token> stack=new Stack<>();
        int q=p+1;
        stack.push(this.input.get(p));

        while(stack.size()>0 && q<this.input.size()){
            if(this.input.get(q).type.equals(TokenType.LEFT_PAREN))
                stack.push(this.input.get(q));
            if(this.input.get(q).type.equals(TokenType.RIGHT_PAREN))
                stack.pop();
            q++;
        }

        if(stack.size()==0)
            return q-1;
        else
            throw new ParseError(this.input.get(p),"The left brace need to be paired.");
    }

    private  ExprStmt expressionStatement(){
        return new ExprStmt(parseExpressionInStatement(p));
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
                return equality();
            }
        }else{
            return equality();
        }

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

        return primary();
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

    private Expression parseExpressionInStatement(int p){
        int pend=p+1;
        while(this.input.get(pend).type!=TokenType.SEMICOLON){
            pend++;
        }

        Parser innerparser=new Parser(this.input.subList(p,pend));

        this.p=pend+1;
        return innerparser.expression();
    }


    private void panicRecoverFromError(){
        while(this.input.get(p).type!=TokenType.SEMICOLON){
            p++;
        }
        p++;
    }
}
