package Lox;

public class ParseError extends RuntimeException {
    Token token;
    String message;

    public ParseError(Token t,String message){
        this.token=t;
        this.message=message;
    }

    public ParseError(String message) {
        super(message);
    }


}
