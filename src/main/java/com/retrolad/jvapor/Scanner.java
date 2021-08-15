package com.retrolad.jvapor;

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
        }
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
            default:
                Vapor.error(line, "Unexpected character.");
                break;    
        }
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
}