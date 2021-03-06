package com.retrolad.jvapor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Scanner {
    // we store the raw source code as a simple string
    private final String source;
    // store generated tokens in a list
    private final List<Token> tokens = new ArrayList<>();
    // points to the first character in the lexeme being scanned
    private int start = 0;
    // points at the character currently being considered
    private int current = 0;
    // tracks what source line current is on, so we can produce tokens that know their location
    private int line = 1;
    // Set of reversed words in a map
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    TokenType.AND);
        keywords.put("class",  TokenType.CLASS);
        keywords.put("else",   TokenType.ELSE);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("for",    TokenType.FOR);
        keywords.put("fun",    TokenType.FUN);
        keywords.put("if",     TokenType.IF);
        keywords.put("nil",    TokenType.NIL);
        keywords.put("or",     TokenType.OR);
        keywords.put("print",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super",  TokenType.SUPER);
        keywords.put("this",   TokenType.THIS);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("var",    TokenType.VAR);
        keywords.put("while",  TokenType.WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while(!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Scan a single token
     */
    private void scanToken() {
        char c = advance();
        switch(c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            // two characters lexemes
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            // longer lexemes
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            // skip meaningless characters
            case ' ':
            case '\r':
            case '\t':
                // ignore whitespaces
                break;
            case '\n':
                line++;
                break;
            // strings
            case '"': string(); break;
            default:
                // digits
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } 
                else {
                    Vapor.error(line, "Unexpected character.");
                }
            break;    
        }
    } 

    /**
     * Identifiers start with a letter or underscore.
     */
    private void identifier() {
        while(isAlphaNumeric(peek())) advance();

        // Check if identifier is a keyword
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }

    /** 
    *  
    * <p>
    * All numbers in Vapor are floating point at runtime, but both integer and decimal literals are supported. 
    * A number literal is a series of digits optionally followed by a . and one or more trailing digits.
    * <p> 
    * |   1234
    * |   12.34
    * <p>
    * We don???t allow a leading or trailing decimal point, so these are both invalid:
    * <p>
    * |   .1234
    * |   1234.
    */
    private void number() {
        // While characters are numbers advance the pointer
        while (isDigit(peek())) advance();

        // Look for a fractional part
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while(isDigit(peek())) advance();
        }

        // Convert lexeme to its numeric value and create token.
        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    /**
     * Function to handle strings.
     * 
     * Consume characters until we hit " that ends the string.
     * Also handle tunning out of input before the string is closed.
     * 
     * Vapor supports multi-line characters.
     */
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Vapor.error(line, "Unterminated string");
            return;
        }

        // Advance the closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    /**
     * 
     * @return Next character in the source file
     */
    private char advance() {
        return source.charAt(current++);
    }
    
    /**
     * Creates token for the text of the current lexeme
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    /**
     * Determine two characters lexemes
     * @param expected Character that will compose two character lexeme with the current one
     * @return true of false ????
     */
    private boolean match(char expected) {
        if(isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    /**
     * Look at the current unconsumed character
     * @return
     */
    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /**
     * Look at the next unconsumed character
     */
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    /**
     * Check if char is an alphabet character or an underscore. 
     * @param c Char to check
     * @return
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }{}

    /**
     * Check if char is an alphabet, underscore or digit.
     * @param c
     * @return
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Check if character is digit
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}