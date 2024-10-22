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

    public Environment getOuterEnv(){
        return outerEnv;
    }
    public void assign(String key, Object value){
        Environment toUpdateEnv=findEnvContainskey(key);
        if(toUpdateEnv==null)
            this.map.put(key,value); //means this assign is used in declaraction: var a=3;
        else
            toUpdateEnv.map.put(key,value);//means this assign is used in assignment: a=a+3;
    }

    public Object get(Token t){

        if(map.containsKey(t.literal)) return map.get(t.literal);
        //should recursively find the key, towards outer environment
        if(outerEnv==null)
            throw new RuntimeError(t,"The variable is undefined");
        else
            return outerEnv.get(t);
    }

    Environment findEnvContainskey(String key){
        Environment residentEnv=this;
        while(residentEnv!=null && !residentEnv.map.containsKey(key)){
            residentEnv=residentEnv.outerEnv;
        }

        return residentEnv;
    }

}
