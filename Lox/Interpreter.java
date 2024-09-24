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
                checkNumOperand(unary.operator,right);
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
                throw new RuntimeError(binary.operator,"Two operands should both be number of string");
            case MINUS:
                checkNumOperands(binary.operator,left,right);
                return (double)left-(double)right;
            case SLASH:
                checkNumOperands(binary.operator,left,right);
                checkZeroDivision(binary.operator,right);
                return (double)left/(double) right;
            case STAR:
                checkNumOperands(binary.operator,left,right);
                return (double)left*(double) right;
            case GREATER:
                checkNumOperands(binary.operator,left,right);
                return (double)left>(double) right;
            case GREATER_EQUAL:
                checkNumOperands(binary.operator,left,right);
                return (double)left>=(double) right;
            case LESS:
                checkNumOperands(binary.operator,left,right);
                return (double)left<(double) right;
            case LESS_EQUAL:
                checkNumOperands(binary.operator,left,right);
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



    private void checkNumOperand(Token operator,Object operand){
        if(operand instanceof Double) return;
        throw new RuntimeError(operator,"The operand in not a number");

    }
    
    private void checkNumOperands(Token operator,Object left,Object right){
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator,"Both the operands should be number");
    }
    
    private void checkZeroDivision(Token operator,Object right){
        if(right instanceof Double && (Double)right==0)
            throw new RuntimeError(operator,"Cannot divide by zero");
    }
}
