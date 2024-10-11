package Lox.Exp;

import Lox.Token;

import java.util.List;

public class LogicOpration extends Expression{
    List<Expression> operands;
    Token operator;

    public LogicOpration(List<Expression> operands,Token operator){
        this.operands=operands;
        this.operator=operator;
    }

    public List<Expression> getOperands(){
        return operands;
    }

    public Token getOperator(){
        return operator;
    }
    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}
