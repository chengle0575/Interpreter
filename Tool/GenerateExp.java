package Tool;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateExp {
    //args:[classname,fieldclass,fieldclass,fieldclass]
    public static void main(String[] args) throws IOException{

        List<String[]> allClassToGenerate=List.of(
                new String[]{"Literal","Object value"}, //??why object here
                new String[]{"Grouping","Expression exp"},
                new String[]{"Unary","Token operator","Expression right"},
                new String[]{"Binary","Expression left", "Token operator", "Expression right"}
        );

        //collect all the child class's name
        List<String> childclasses=new ArrayList<>();
        for(int i=1;i<allClassToGenerate.size();i++){
            childclasses.add(allClassToGenerate.get(i)[0]);
        }

        //create a directory called 'Exp' and generate basic Expression class
        File baseDirectory=generatePackageBase();
        //generate the visitor interface. Make use of Visitor design pattern to make adding methods to all the child classes easier.
        generateVisitor(baseDirectory,childclasses);

        allClassToGenerate.forEach(  //exception inside lambda expression are not propagated to the callinf method unless explicitly catch and rethrow it then
                content-> {
                    try {
                        generateClassFile(content,baseDirectory);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }


    public static File generatePackageBase() throws IOException{
        File d=new File("./Lox/Exp");
        d.mkdir();

        File fExpression=new File(d,"Expression.java");

        PrintWriter w=new PrintWriter(fExpression);
        w.println("package Lox.Exp;");
        w.println("public abstract class Expression {");
        w.println("public abstract <R> R accept(Visitor<R> v);"); //part of Visitor design pattern. Using generics to enable mumltiple possiblities of return type
        w.println("}");
        w.flush();
        w.close();
        return d;
    }

    public static void generateVisitor(File directory,List<String> classes) throws IOException{
        File fVisitor=new File(directory,"Visitor.java");
        PrintWriter pw=new PrintWriter(fVisitor);
        pw.println("package Lox.Exp;");
        pw.println("import Lox.Exp.*;");
        pw.println("public interface Visitor<R>{");

        //implement a visit method with each class as parameter
        for(String childclassnamd:classes){
            pw.println("public R visit("+childclassnamd+" "+childclassnamd.toLowerCase()+");");
        }

        pw.println("}");
        pw.flush();
        pw.close();
    }

    public static void generateClassFile(String[] args, File basedirectory) throws IOException{
        //generate java class for all expressions => create a java file and write required content to it
        String classname=args[0];
        String[] fields=new String[4];

        //create a file class file
        String filepath=basedirectory.getAbsolutePath()+"/"+classname+".java";
        File f=new File(filepath);
        PrintWriter w=new PrintWriter(f);

        //generate class
        w.println("package Lox.Exp;");
        w.println("import Lox.Token;");
        w.println("import Lox.Exp.Expression;");
        w.println("import Lox.Exp.Visitor;");
        w.println("public class "+classname+" extends Expression {");

        //generate fields
        for(int i=1;i<args.length;i++){
            String[] fieldclass=args[i].split(" ");

            fields[i-1]=args[i];
            w.println("public final "+fieldclass[0]+" "+fieldclass[1]+";");
        }

        w.println(generateConstructor(classname,fields));
        w.println(generateAcceptFunction());
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
            String[] fclasssplit=fclass.split(" ");
            sb.append(fclasssplit[0]+" "+fclasssplit[1]+",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(") { \n");

        for(String fclass:fieldclass){
            if(fclass==null)
                break;
            String[] fclasssplit=fclass.split(" ");
            sb.append("this."+fclasssplit[1]+"="+fclasssplit[1]+";\n");
        }
        sb.append("};");

        return sb.toString();
    }

    public static String generateAcceptFunction(){
        StringBuilder sb=new StringBuilder();

        sb.append("@Override\n");
        sb.append("public <R> R accept(Visitor<R> v){\n");
        sb.append("return v.visit(this);\n");
        sb.append("}\n");

        return sb.toString();
    }


}
