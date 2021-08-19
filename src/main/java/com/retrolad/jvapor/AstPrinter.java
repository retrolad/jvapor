package com.retrolad.jvapor;

/**
 * Test class to show the nesting structure of the tree.
 * Each expression is explicitly parenthesized and all of
 * its subexpressions and tokens are contained in that.
 */
public class AstPrinter implements Expr.Visitor<String> {
    
    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
            new Expr.Unary(
                new Token(TokenType.MINUS, "-", null, 1),
                new Expr.Literal(123)), 
            new Token(TokenType.STAR, "*", null, 1), 
            new Expr.Grouping(
                new Expr.Literal(45.67)));

        System.out.println(new AstPrinter().print(expression));
    }

    String print(Expr expr) {
        return expr.accept(this);
    }
    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }
    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }
    /**
     * Literal expressions are easy - they convert the value
     * to a string with a check to handle Java's null standing
     * in for Vapor's nil
     */
    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }
    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    /**
     * <pre>
     * Takes a name and a list of subexpressions and wraps them 
     * all in parentheses.
     * 
     * For expression:
     * -123 * (45.67) 
     * We have a syntax tree:
     * 
     *      * 
     *     / \
     *    -  ()
     *    |   |
     *   123 45.67
     * 
     * So the function produces:
     * (* (- 123) (group 45.67))
     * </pre>
     * @param name
     * @param exprs
     * @return
     */
    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}
