package Lox;

import Lox.Exp.*;

import java.util.Objects;

public class Interpreter implements Visitor {

    public Object evaluate(Expression exp){
        return exp.accept(this);
    }

    public static boolean isTruth(Object o){
        if(Objects.equals(o,null)|| Objects.equals(o,false))
            return false;
        return true;
    }

    @Override
    public Object visit(Grouping grouping) {
        return evaluate(grouping.exp);
    }

    @Override
    public Object visit(Unary unary) {
        Object right=evaluate(unary.right);

        switch (unary.operator.type){
            case MINUS:
                return -(double)right;
            case BANG:
                return !isTruth(right);
        }
        return null;
    }

    @Override
    public Object visit(Binary binary) {
        Object left=evaluate(binary.left);
        Object right=evaluate(binary.right);

        switch(binary.operator.type){
            case PLUS: //the '+' operator can be used for both add values and concat strings
                if(left instanceof Double && right instanceof Double)
                    return (double)left+(double)right;
                if(left instanceof String && right instanceof String)
                    return (String)left+(String)right;
            case MINUS:
                return (double)left-(double)right;
            case SLASH:
                return (double)left/(double) right;
            case STAR:
                return (double)left*(double) right;
            case GREATER:
                return (double)left>(double) right;
            case GREATER_EQUAL:
                return (double)left>=(double) right;
            case LESS:
                return (double)left<(double) right;
            case LESS_EQUAL:
                return (double)left<=(double) right;
            case BANG_EQUAL:
                return !Objects.equals(left,right);
            case EQUAL_EQUAL:
                return Objects.equals(left,right);


        }
        return null;
    }

    @Override
    public Object visit(Literal literal) {
        return literal.value; //convert a tree node into a runtime value
    }
}
