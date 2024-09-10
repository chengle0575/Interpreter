package Tests;
//writing JUnit framework to test
import Lox.AstPrinter;
import Lox.Exp.*;
import Lox.Token;
import Lox.TokenType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


public class AstPrinterTest {
    AstPrinter astp=new AstPrinter();

    @Test
    public void testUnary(){
        //-10
        Expression exp=new Unary(new Token(TokenType.MINUS),new Literal(10));
        astp.generateString(exp);
    }


    @Test
    public void testGrouping(){
        //(2+3)*5
        Expression exp=new Binary(new Grouping(new Binary(new Literal(2),new Token(TokenType.PLUS),new Literal(3))),new Token(TokenType.STAR),new Literal(5));
        astp.generateString(exp);
    }



}
