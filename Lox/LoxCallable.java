package Lox;

import java.util.List;

public interface LoxCallable {

    public Object call(Interpreter interpreter,List<Object> arguments);
}
