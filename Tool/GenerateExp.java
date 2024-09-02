package Tool;

import java.io.*;

public class GenerateExp {
                            //args:[classname,fieldclass,fieldclass,fieldclass]
    public static void main(String[] args) throws IOException {
        //generate java class for all expressions => create a java file and write required content to it
        String classname=args[0];
        String[] fields=new String[4];

        //create a file class file
        String filepath="./Lox/"+classname+".java";
        File f=new File(filepath);

        BufferedWriter w=new BufferedWriter(new FileWriter(f));

        w.write("public class "+classname+"{"+"\n");

        for(int i=1;i<args.length;i++){
            String fieldclass=args[i];
            fields[i-1]=args[i];

            w.write("public final ");
            w.write(fieldclass+" ");
            w.write(fieldclass.toLowerCase()+";"+"\n");

        }

        String constructor=generateConstructor(classname,fields);
        w.write(constructor);
        w.write('\n');
        w.write("}");
        w.flush();
        w.close();
    }

    public static String generateConstructor(String classname, String[] fieldclass){

        StringBuilder sb=new StringBuilder();
        sb.append("public "+classname+"(");

        for(String fclass:fieldclass){

            if(fclass==null)
                break;
            sb.append(fclass);
            sb.append(" ");
            sb.append(fclass.toLowerCase());
            sb.append(',');
        }

        sb.deleteCharAt(sb.length()-1);
        sb.append(") {");
        sb.append('\n');

        for(String fclass:fieldclass){
            if(fclass==null)
                break;

            sb.append("this."+fclass.toLowerCase());
            sb.append("=");
            sb.append(fclass.toLowerCase());

            sb.append(';');
            sb.append('\n');
        }

        sb.append("};");

        return sb.toString();
    }
}
