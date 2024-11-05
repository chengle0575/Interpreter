package Lox.Exp;

public class Get extends Expression{
    Expression primary;
    Expression property;

    //primary.property..property

    public Get(Expression primary,Expression property){
        this.primary=primary;
        this.property=property;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return null;
    }
}
