package com.retrolad.jvapor;

import java.util.List;

/**
 * A parser that uses a recursive descent technique to build
 * an abstract syntax tree out of a sequence of tokens.
 *
 * <p>An expression grammar that this parser uses is defined as
 * follows:
 *
 * <p>
 * <p> expression     → equality ;
 * <p> equality       → comparison ( ( "!=" | "==" ) comparison )* ;
 * <p> comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
 * <p> term           → factor ( ( "-" | "+" ) factor )* ;
 * <p> factor         → unary ( ( "/" | "*" ) unary )* ;
 * <p> unary          → ( "!" | "-" ) unary | primary ;
 * <p> primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
 */
public class Parser {
    /**
     * The list of tokens to process
     */
    private final List<Token> tokens;
    /**
     * Index of the next token to process
     */
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parse expressions of the highest level of precedence.
     * Can produce a number or a string literal, true of false
     * boolean values, nil or another expression
     *
     * @return Expression
     */
    private Expr primary() {
        if(match(TokenType.TRUE)) return new Expr.Literal(true);
        if(match(TokenType.FALSE)) return new Expr.Literal(false);
        if(match(TokenType.NIL)) return new Expr.Literal(null);

        if(match(TokenType.NUMBER, TokenType.STRING))
            return new Expr.Literal(previous().literal);

        if(match(TokenType.LEFT_PAREN)) {
            Expr expr = null;
            consume(TokenType.RIGHT_PAREN);
            return new Expr.Grouping(expr);
        }

        // Make progress to the next token
        advance();
        return null;
    }

    /**
     * Checks if current token has any of the given types.
     * @param types Types to check
     * @return {@code true} If token has any of the given types,
     * {@code false} otherwise
     */
    private boolean match(TokenType... types) {
        for(TokenType type : types) {
            if(check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    /**
     * Consumes the current token and returns it.
     * @return The current token.
     */
    private Token advance() {
        if(!atEnd()) current++;
        return previous();
    }

    /**
     * Returns {@code true} if the current token is of the given type.
     * Unlike {@link #match(TokenType...)} do not advance the
     * {@link #current} pointer.
     *
     * @param type The type to check
     * @return Returns {@code true} if the current token is of the given type.
     */
    private boolean check(TokenType type) {
        if(!atEnd()) return peek().type == type;
        return false;
    }

    /**
     * Returns the current token
     * @return The current token
     */
    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * Consumes tokens until reach the specified one.
     * @param type
     * @return
     */
    private Token consume(TokenType type) {
        if(check(type)) return advance();
    }

    /**
     * Checks if parser reached end of source string.
     * @return {@code true} if parser reached end of source string,
     * {@code false} otherwise
     */
    private boolean atEnd() {
        return current == tokens.size();
    }
}
