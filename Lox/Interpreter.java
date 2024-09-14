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
                checkNumOperand(right);
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
                throw new RuntimeException("Runtime error: Two operands should both be number of string");
            case MINUS:
                checkNumOperands(left,right);
                return (double)left-(double)right;
            case SLASH:
                checkNumOperands(left,right);
                return (double)left/(double) right;
            case STAR:
                checkNumOperands(left,right);
                return (double)left*(double) right;
            case GREATER:
                checkNumOperands(left,right);
                return (double)left>(double) right;
            case GREATER_EQUAL:
                checkNumOperands(left,right);
                return (double)left>=(double) right;
            case LESS:
                checkNumOperands(left,right);
                return (double)left<(double) right;
            case LESS_EQUAL:
                checkNumOperands(left,right);
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



    private void checkNumOperand(Object operand){
        if(operand instanceof Double) return;
        throw new RuntimeError("Runtime Error: the operand in not a number");

    }
    
    private void checkNumOperands(Object left,Object right){
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeException("Runtime Error: both the operands are not number");
    }
    

}
