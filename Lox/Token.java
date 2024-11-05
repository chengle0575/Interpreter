package Lox;

import java.util.HashMap;

public class Token {
    TokenType type;
    String literal;
    int line;


    public final static HashMap<String,TokenType> reserveKeywordsMap=createReservedKeywordsMap();


    public Token(TokenType t){
        this.type=t;
        this.literal="";
    }

    public Token(TokenType t,int line){
        this.type=t;
        this.literal="";
        this.line=line;
    }

    public Token(TokenType t, String literal,int line){
        this.type=t;
        this.literal=literal;
        this.line=line;
    }

    public TokenType getType() {
        return type;
    }

    public String getLiteral(){
        return literal;
    }

    public String toString(){
        return this.type.toString()+" "+this.literal;
    }


    public static HashMap<String,TokenType> createReservedKeywordsMap(){
        HashMap<String,TokenType> hmap=new HashMap<>();
        hmap.put("and",TokenType.AND);
        hmap.put("class",TokenType.CLASS);
        hmap.put("else", TokenType.ELSE);
        hmap.put("false",TokenType.FALSE);
        hmap.put("fun", TokenType.FUN);
        hmap.put("for", TokenType.FOR);
        hmap.put("if", TokenType.IF);

        hmap.put("nil",TokenType.NIL);

        hmap.put("or",TokenType.OR);
        hmap.put("print", TokenType.PRINT);
        hmap.put("return", TokenType.RETURN);
        hmap.put("super", TokenType.SUPER);
        hmap.put("this", TokenType.THIS);
        hmap.put("true", TokenType.TRUE);
        hmap.put("var", TokenType.VAR);
        hmap.put("while", TokenType.WHILE);
        return hmap;
    }

    @Override
    public boolean equals(Object that){
        if(that instanceof Token){
            if(this.type==((Token) that).type && this.literal.equals(((Token) that).literal))
                return true;
        }

        return false;
    }

    @Override
    public int hashCode(){
        return this.literal.hashCode();
    }
}
