package Lox;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

    String source;
    List<Token> tokenlist=new ArrayList<>();

    int pstart=0;
    int pend=0;
    int line=1;

    public Scanner(String source){
        this.source=source;
    }

    public void scanTokens(){

        while (pend<source.length()){
            switch (source.charAt(pstart)){
                case '{': addToken(TokenType.LEFT_PAREN,line);break;
                case '}': addToken(TokenType.RIGHT_PAREN,line);break;
                case '(': addToken(TokenType.LEFT_BRACE,line);break;
                case ')': addToken(TokenType.RIGHT_BRACE,line);break;
                case ',': addToken(TokenType.COMMA,line);break;
                case '.': addToken(TokenType.DOT,line);break;
                case '-': addToken(TokenType.MINUS,line);break;
                case '+': addToken(TokenType.PLUS,line);break;
                case ';': addToken(TokenType.SEMICOLON,line);break;
                case '*': addToken(TokenType.STAR,line);break;
                case '/':
                    if(matchAhead('/')){//follows comments whose content should be ignored
                        exhaustComment();
                    }else{
                        addToken(TokenType.SLASH,line);
                    }
                    break;
                case '=':
                    if(matchAhead('=')){
                        addToken(TokenType.EQUAL_EQUAL,line);
                        pstart++;
                    } else
                        addToken(TokenType.EQUAL,line);
                    break;
                case '!':
                    if(matchAhead('=')){
                        addToken(TokenType.BANG_EQUAL,line);
                        pstart++;
                    } else
                        addToken(TokenType.BANG,line);
                    break;
                case '>':
                    if(matchAhead('=')){
                        addToken(TokenType.GREATER_EQUAL,line);
                        pstart++;
                    } else
                        addToken(TokenType.GREATER,line);
                    break;
                case '<':
                    if(matchAhead('=')) {
                        addToken(TokenType.LESS_EQUAL,line);
                        pstart++;
                    } else
                        addToken(TokenType.LESS,line);
                    break;
                case '"':
                    String s=getString();
                    addToken(TokenType.STRING,s,line);
                    break;
                case ' ':
                    break;
                case '\n':
                    line++;
                    break;
                default:
                    if(Character.isDigit(source.charAt(pstart))){
                        String numliteral=getNumber();
                        pstart=pend;
                        addToken(TokenType.NUMBER,numliteral,line);
                    }else if(Character.isAlphabetic(source.charAt(pstart))||source.charAt(pstart)=='_'){ //key idea here: 1.keywords are reserved identifier 2.the match should follow maximal munch principle
                        String identifierLiteral=getIdentifier();
                        pstart=pend-1;
                        if(isReservedKeywords(identifierLiteral)!=null){
                            addToken(isReservedKeywords(identifierLiteral),line);
                        }else{
                            addToken(TokenType.IDENTIFIER,identifierLiteral,line);
                        }
                    }else{//inexpected character, for example: @,$
                        Lox.error(line,"Unexpected Input");

                    }
            }
            pstart++;
            pend=pstart;
        }
    }


    public void addToken(TokenType tokentype, int line){
        tokenlist.add(new Token(tokentype,line));
    }
    public void addToken(TokenType tokentype,String literal,int line){
        tokenlist.add(new Token(tokentype,literal,line));
    }


    public boolean matchAhead(char tomatch){
        int ahead=pend+1;
        // System.out.println("ahead is: "+source.charAt(ahead));
        if(ahead>=source.length())
            return false;
        if(source.charAt(ahead)==tomatch)
            return true;
        return false;
    }

    public boolean matchAheadDigit(){
        int ahead=pend+1;
        if(ahead>=source.length())
            return false;
        if(Character.isDigit(source.charAt(ahead))||source.charAt(ahead)=='.')
            return true;
        return false;
    }


    public void exhaustComment(){
        while(!matchAhead('\n')&&pend<source.length()){
            pend++;
        }
        pstart=pend;
    }

    public String getString(){
        while (!matchAhead('"')){
            //System.out.println("pend now= "+pend);
            pend++;
            if(pend>=source.length()-1){
                Lox.error(line,"invalid string input, no ending with\"");
                pstart=pend;
                return "";
            }

        }
        //System.out.println("pend now= "+pend);
        int start=pstart;
        int end=pend+1;

        pstart=pend+1;
        return source.substring(start+1,end);
    }

    public String getNumber(){
        //valid number input:  123.123
        //invalid number input: 123., .123 (leading and trailing dot are both invalid for lox language)
        while (matchAheadDigit()){
            pend++;
        }

        if(source.substring(pstart,pend+1).endsWith(".")){
            Lox.error(line,"invalid number input, try to remove trailing dot .");
            return "";
        }else{
            return source.substring(pstart,pend+1);
        }
    }


    public String getIdentifier(){
        while(pend<source.length()-1&&isAlphaNumerical(source.charAt(pend))){
            pend++;
        }
        return source.substring(pstart,pend);
    }


    public boolean isAlphaNumerical(char c){//a variable name can only consists of alphabetic, numeric and underscore character
        return Character.isDigit(c)||Character.isAlphabetic(c)||c=='_';
    }

    public TokenType isReservedKeywords(String literal){
        return Token.reserveKeywordsMap.getOrDefault(literal,null);
    }




}
