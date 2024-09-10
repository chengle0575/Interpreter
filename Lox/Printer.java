package Lox;

import Lox.Exp.*;

public class Printer implements Visitor {

    @Override
    public String visit(Grouping grouping) {
        StringBuffer sb=new StringBuffer();
        sb.append("group\n");
        sb.append("/   ");
        sb.append(grouping.exp.accept(new Printer()));

        return sb.toString();
    }

    @Override
    public String visit(Unary unary) {
        StringBuffer sb=new StringBuffer();
        sb.append(unary.operator.type+"\n");
        sb.append("/   ");
        sb.append(unary.right.accept(new Printer()));

        return sb.toString();
    }

    @Override
    public String visit(Binary binary) {
        StringBuffer sb=new StringBuffer();
        sb.append(binary.operator.type+"\n");
        sb.append("/         \\");
        sb.append(binary.left.accept(new Printer()));
        sb.append("      ");
        sb.append(binary.right.accept(new Printer()));

        return sb.toString();
    }

    @Override
    public String visit(Literal literal) {
        return literal.value.toString()+"\n";
    }
}
