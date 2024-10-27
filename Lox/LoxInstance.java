package Lox;

public class LoxInstance {

    private LoxClass classname;

    public LoxInstance(LoxClass classname){
        this.classname=classname;
    }


    @Override
    public String toString(){
        return classname+" instance";
    }
}
