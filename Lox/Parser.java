package Lox;

import Lox.Exp.*;

import java.util.List;

public class Parser {

    List<Token> input;
    int p=0;


    Parser(List<Token> input){
        this.input=input;
    }

    public Expression generateAST(){
        return expression();
    }

    private boolean reachEnd(int pt){
        return pt==this.input.size()-1;
    }

    private boolean match(TokenType ty){
        if(this.p>=this.input.size())
            return false;
        return this.input.get(this.p).type==ty;
    }

    private void moveahead(){
        this.p++;
    }

    private Expression expression(){ //expression->equality
        return equality();
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
               return new Grouping(new Parser(this.input.subList(p,pt)).generateAST());
           } else if(pt>this.p){
               return new Binary(new Grouping(expression()),this.input.get(pt+1),new Parser(this.input.subList(pt+2,this.input.size())).generateAST());
           }

       }

       Lox.error(this.input.get(this.p-1).line,"Parser Error: invalid "+this.input.get(this.p-1).type.toString());

       return null;
    }

}
