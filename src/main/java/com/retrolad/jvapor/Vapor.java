package com.retrolad.jvapor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import jdk.javadoc.internal.doclets.formats.html.SourceToHTMLConverter;

public class Vapor {
    // Ensure we don't try to execute code that has a kown error.
    static boolean hadError = false;

    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("Usage: jvapor [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    /**
     * Execute directly from source
     * @param path Path to source file
     * @throws IOException
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
    }

    /**
     * Run interactively, executing one line at a time
     * @throws IOException
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for(;;) {
            System.out.println("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // For now, just print tokens.
        for  (Token token : tokens) {
            System.out.println(token);
        }
    }

    /**
     * Tells the user syntax error occured on a given line
     * @param line Error line
     * @param message Error message
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}