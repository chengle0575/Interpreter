package Lox;

//runtime error is detected in interpreter phase
public class RuntimeError extends RuntimeException{

    public RuntimeError(String s){

    }

    public RuntimeError(Token t,String s){
        super("[line "+t.line+"]"+" around "+t.toString()+" operator :"+s);
    }


}
