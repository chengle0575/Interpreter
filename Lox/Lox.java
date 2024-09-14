package Lox;

import Lox.Exp.Expression;

import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Lox {

    static boolean hadError = false;

    public static void main(String[] args) throws IOException {

        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]); //run the file directly
        } else {
            runPrompt(); //run commmand interactively
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code.
        if (hadError) System.exit(65);
    }

    private static void runPrompt() throws IOException {

        System.out.println("lox start");
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);

            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        scanner.scanTokens();

        // For now, just print the tokens.
        for (Token token : scanner.tokenlist) {
            System.out.println(token);
        }



        //put the tokenlist into parser
        Parser parser=new Parser(scanner.tokenlist);
        Expression exp=parser.generateAST();
        AstPrinter ap=new AstPrinter();
        ap.generateString(exp);

        Interpreter ip=new Interpreter();
        System.out.println(ip.evaluate(exp));

    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

}