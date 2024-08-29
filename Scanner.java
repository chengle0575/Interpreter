import java.io.DataInputStream;
import java.io.IOException;
import java.io.StringReader;
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
                case ' ':
                    break;
                case '\n':
                    line++;
                    break;
                default:
                    if(Character.isDigit(source.charAt(pstart))){
                        String numliteral=getNumber();
                        pstart=pend;
                        addToken(TokenType.NUMBER,numliteral);
                    }else if(Character.isAlphabetic(source.charAt(pstart))||source.charAt(pstart)=='_'){ //key idea here: 1.keywords are reserved identifier 2.the match should follow maximal munch principle
                        String identifierLiteral=getIdentifier();
                        pstart=pend;
                        if(isReservedKeywords(identifierLiteral)!=null){
                            addToken(isReservedKeywords(identifierLiteral));
                        }else{
                            addToken(TokenType.IDENTIFIER,identifierLiteral);
                        }
                    }else{//inexpected character, for example: @,$
                        Lox.error(line,"Unexpected Input");

                    }
            }
            pstart++;
            pend=pstart;
        }
    }


    public void addToken(TokenType tokentype){
        tokenlist.add(new Token(tokentype));
    }
    public void addToken(TokenType tokentype,String literal){
        tokenlist.add(new Token(tokentype,literal));
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
        return source.substring(start,end);
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
        return source.substring(pstart,pend+1);
    }


    public boolean isAlphaNumerical(char c){//a variable name can only consists of alphabetic, numeric and underscore character
        return Character.isDigit(c)||Character.isAlphabetic(c)||c=='_';
    }

    public TokenType isReservedKeywords(String literal){
        return Token.reserveKeywordsMap.getOrDefault(literal,null);
    }




}
