package Lox;

public class ReturnValue extends RuntimeException {
    Object returnValue;

    public ReturnValue(String message) {
        super(message);
    }
    public ReturnValue(Object value){
        this.returnValue=value;
        System.out.println("this is return value:"+value);
    };


}
