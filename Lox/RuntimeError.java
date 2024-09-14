package Lox;

//runtime error is detected in interpreter phase
public class RuntimeError extends RuntimeException{

    public RuntimeError(){

    }

    public RuntimeError(String s){
        super(s);
    }
}
