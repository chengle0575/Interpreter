package Lox;

import Lox.Exp.*;

public class AstPrinter {

    Expression startExp;

    AstPrinter(Expression ex){
        this.startExp=ex;
    }

    public void generateString(){
        System.out.println(this.startExp.accept(new Printer()));
    }


}

