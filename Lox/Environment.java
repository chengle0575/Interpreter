package Lox;



import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Environment outerEnv; //use a pointer to chain the environments
    private Map<String, Object> map;

    public Environment(){
        map=new HashMap<>();
    }

    public Environment(Environment e){
        this.outerEnv=e;
        map=new HashMap<>();
    }

    public void assign(String key, Object value){
        map.put(key,value);
    }

    public Object get(Token t){

        if(map.containsKey(t.literal)) return map.get(t.literal);
        //should recursively find the key, towards outer environment
        if(outerEnv==null)
            throw new RuntimeError(t,"The variable is undefined");
        else
            return outerEnv.get(t);
    }

}
