package com.retrolad.jvapor;

import java.util.ArrayList;
import java.util.List;

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
            // two characters lexems
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
            // longer lexems
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
                Vapor.error(line, "Unexpected character.");
                break;    
        }
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
     * Determine two characters lexems
     * @param expected Character that will compose two character lexem with the current one
     * @return true of false 🤣
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
}