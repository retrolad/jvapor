package com.retrolad.jvapor;

/** 
 * At the point that we recognize a lexeme, we also remember which 
 * kind of lexeme it represents. We have a different type for each 
 * keyword, operator, bit of punctuation, and literal type.
 */
enum TokenType {
    // Sigle-character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character tokens.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords.
    AND, CLASS, ELSE, FLASE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, VAR, WHILE,

    EOF
}

