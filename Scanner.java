import java.io.DataInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Scanner {

    String source;
    List<Token> tokenlist;

    int pstart=0;
    int pend=0;



    public Scanner(String source){
        this.source=source;
    }

    public void addToken(TokenType tokentype){
        tokenlist.add(new Token(tokentype));
    }
    public void addToken(TokenType tokentype,String literal){
        tokenlist.add(new Token(tokentype,literal));
    }


    public boolean matchAhead(char tomatch){
        int ahead=pend+1;
        if(ahead>=source.length())
            return false;
        if(source.charAt(ahead)==tomatch)
            return true;
        return false;
    }

    public void exhaustComment(){
        while(!matchAhead('\n')){
            pend++;
        }
        pstart=pend;
    }

    public String getString(){
        while (!matchAhead('"')){
            pend++;
        }

        int start=pstart;
        int end=pend+1;

        pstart=pend;
        return source.substring(start,end);
    }

    public String getNumber(){
        while (Character.isDigit(source.charAt(pend))||source.charAt(pend)=='.'){
            pend++;
        }
        int start=pstart;
        int end=pend;

        pstart=pend;
        return source.substring(start,end);
    }


    public String getIdentifier(){
        while(source.charAt(pend)!=' '){
            pend++;
        }
        int start=pstart;
        int end=pend;

        pstart=pend;
        return source.substring(pstart,pend);
    }


    public TokenType isReservedKeywords(String literal){
            return Token.reserveKeywordsMap.getOrDefault(literal,null);
    }

    public void scanTokens(){

        while (pend<source.length()){
            switch (source.charAt(pstart)){
                case '{': addToken(TokenType.LEFT_PAREN);break;
                case '}': addToken(TokenType.RIGHT_PAREN);break;
                case '(': addToken(TokenType.LEFT_BRACE);break;
                case ')': addToken(TokenType.RIGHT_BRACE);break;
                case ',': addToken(TokenType.COMMA);break;
                case '.': addToken(TokenType.DOT);break;
                case '-': addToken(TokenType.MINUS);break;
                case '+': addToken(TokenType.PLUS);break;
                case ';': addToken(TokenType.SEMICOLON);break;
                case '*': addToken(TokenType.STAR);break;
                case '/':
                    if(matchAhead('/')){//follows comments whose content should be ignored
                        exhaustComment();
                    }else{
                        addToken(TokenType.SLASH);
                    }
                    break;
                case '=':
                    if(matchAhead('='))
                        addToken(TokenType.EQUAL_EQUAL);
                    else
                        addToken(TokenType.EQUAL);
                    break;
                case '!':
                    if(matchAhead('='))
                        addToken(TokenType.BANG_EQUAL);
                    else
                        addToken(TokenType.BANG);
                    break;
                case '>':
                    if(matchAhead('='))
                        addToken(TokenType.GREATER_EQUAL);
                    else
                        addToken(TokenType.GREATER);
                    break;
                case '<':
                    if(matchAhead('='))
                        addToken(TokenType.LESS_EQUAL);
                    else
                        addToken(TokenType.LESS);
                    break;
                case '"':
                    String s=getString();
                    addToken(TokenType.STRING,s);
                    break;
                default:
                    if(Character.isDigit(source.charAt(pstart))){
                        String numliteral=getNumber();
                        addToken(TokenType.NUMBER,numliteral);
                    }else if(Character.isAlphabetic(source.charAt(pstart))){ //key idea here: 1.keywords are reserved identifier 2.the match should follow maximal munch principle
                        String identifierLiteral=getIdentifier();
                        if(isReservedKeywords(identifierLiteral)!=null){
                            addToken(isReservedKeywords(identifierLiteral));
                        }else{
                            addToken(TokenType.IDENTIFIER,identifierLiteral);
                        }
                    }else{//inexpected character, for example: @,$
                        System.out.println("unexpected input");
                    }
            }


            pstart++;
            pend=pstart;
        }




    }



}
