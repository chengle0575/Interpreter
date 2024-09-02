package Tool;

import java.io.*;
import java.util.Arrays;

public class GenerateExp {
                            //args:[classname,fieldclass,fieldclass,fieldclass]
    public static void main(String[] args) throws IOException {
        //generate java class for all expressions => create a java file and write required content to it
        String classname=args[0];
        String[] fields=new String[4];

        //create a file class file
        String filepath="./Lox/"+classname+".java";
        File f=new File(filepath);
        PrintWriter w=new PrintWriter(f);

        //generate class
        w.println("public class "+classname+"{");

        //generate fields
        for(int i=1;i<args.length;i++){
            String fieldclass=args[i];
            fields[i-1]=args[i];
            w.println("public final "+fieldclass+" "+fieldclass.toLowerCase()+";");
        }

        w.println(generateConstructor(classname,fields));
        w.println("}");
        w.flush();
        w.close();
    }

    public static String generateConstructor(String classname, String[] fieldclass){
        StringBuilder sb=new StringBuilder();
        sb.append("public "+classname+"(");

        for(String fclass:fieldclass){
            if(fclass==null)
                break;
            sb.append(fclass+" "+fclass.toLowerCase()+",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(") { \n");

        for(String fclass:fieldclass){
            if(fclass==null)
                break;
            sb.append("this."+fclass.toLowerCase()+"="+fclass.toLowerCase()+";\n");
        }
        sb.append("};");

        return sb.toString();
    }




}
