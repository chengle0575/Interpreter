package Lox;

import Lox.Exp.*;

public class AstPrinter {


    public AstPrinter(){

    }

    public void generateString(Expression ex){
        System.out.println(ex.accept(new Printer()));
    }


}

