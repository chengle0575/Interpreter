package Lox;

import Lox.Exp.*;

import java.util.List;

public class Parser {

    List<Token> input;
    int p=0;


    Parser(List<Token> input){
        this.input=input;
    }

    /*
    Expression generateAST(){

        Token curtoken=this.input.get(p);

        if(match(TokenType.LEFT_PAREN)){

        }
    }

     */

    boolean match(TokenType ty){
        return this.input.get(p).type==ty;
    }

    void moveahead(){
        p++;
    }

    private Expression expression(){ //expression->equality
        return equality();
    }

    private Expression equality(){//equality-> comparison (((!=|==) comparison))*
        Expression exp=comparison();

        if(match(TokenType.BANG_EQUAL)||match(TokenType.EQUAL_EQUAL)){

                Token t=this.input.get(p);
                moveahead();
                Expression exp2= comparison();
                exp=new Binary(exp,t,exp2);
        }

        return exp;
    }

    private  Expression comparison(){
        Expression exp=term();

        if(match(TokenType.GREATER)||match(TokenType.GREATER_EQUAL)||match(TokenType.LESS)||match(TokenType.LESS_EQUAL)){
            Token t=this.input.get(p);
            moveahead();
            Expression exp2=term();
            exp=new Binary(exp,t,exp2);
        }


        return exp;
    }


    private Expression term(){
        Expression exp=factor();

        if(match(TokenType.PLUS)||match(TokenType.MINUS)){
            Token t=this.input.get(p);
            moveahead();
            Expression exp2=factor();
            exp=new Binary(exp,t,exp2);
        }

        return exp;
    }

    private Expression factor(){
        Expression exp=unary();

        if(match(TokenType.SLASH)||match(TokenType.STAR)){
            Token t=this.input.get(p);
            moveahead();
            Expression exp2=unary();
            exp=new Binary(exp,t,exp2);
        }

        return exp;

    }

    private Expression unary(){
        if(match(TokenType.BANG)||match(TokenType.MINUS)){
            Token t=this.input.get(p);
            return new Unary(t,unary());
        }

        return primary();
    }

    private Expression primary(){
       if(match(TokenType.NUMBER)||match(TokenType.STRING))
           return new Literal(this.input.get(p).literal);
       if(match(TokenType.TRUE)||match(TokenType.FALSE)||match(TokenType.NIL))
           return new Literal();

       if(match(TokenType.LEFT_BRACE)){
            Expression exp=expression();
            moveahead();
            return new Grouping(exp);
       }

    }

}
