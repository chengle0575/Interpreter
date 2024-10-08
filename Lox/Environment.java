package Lox;



import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Map<String, Object> map;

    public Environment(){
        map=new HashMap<>();
    }

    public void assign(String key, Object value){
        map.put(key,value);
    }

    public Object get(Token t){
        if(map.containsKey(t.literal)) return map.get(t.literal);
        throw new RuntimeError(t,"The variable is undefined");
    }

}
