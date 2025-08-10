package calc;

import java.util.*;

import static java.lang.Double.NaN;
import static java.lang.Math.pow;


/*
 *   A calculator for rather simple arithmetic expressions
 *
 *   This is not the program, it's a class declaration (with methods) in it's
 *   own file (which must be named Calculator.java)
 *
 *   NOTE:
 *   - No negative numbers implemented
 */
class Calculator {

    // Here are the only allowed instance variables!
    // Error messages (more on static later)
    final static String MISSING_OPERAND = "Missing or bad operand";
    final static String DIV_BY_ZERO = "Division with 0";
    final static String MISSING_OPERATOR = "Missing operator or parenthesis";
    final static String OP_NOT_FOUND = "Operator not found";

    // Definition of operators
    final static String OPERATORS = "+-*/^";

    // Method used in REPL
    double eval(String expr) {
        if (expr.length() == 0) {
            return NaN;
        }
        List<String> tokens = tokenize(expr);
        List<String> postfix = infix2Postfix(tokens);
        return evalPostfix(postfix);
    }

    // ------  Evaluate RPN expression -------------------

    public double evalPostfix(List<String> postfix) { // TODO - Done by me
        Deque<Double> stack = new ArrayDeque<>();

        for (String evalToken : postfix) {
            if (evalToken.matches("\\d+")) {
                stack.push(Double.parseDouble(evalToken));
            } else if (OPERATORS.contains(evalToken)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException(MISSING_OPERAND);
                }
                double d1 = stack.pop();
                double d2 = stack.pop();
                stack.push(applyOperator(evalToken, d1, d2));
            } else {
                throw new IllegalArgumentException(OP_NOT_FOUND);
            }
        }

        if (stack.isEmpty()) {
            throw new IllegalArgumentException(MISSING_OPERAND);
        }
        if (stack.size() > 1) {
            throw new IllegalArgumentException(MISSING_OPERATOR);
        }

        return stack.pop();
    }

    double applyOperator(String op, double d1, double d2) {
        switch (op) {
            case "+":
                return d1 + d2;
            case "-":
                return d2 - d1;
            case "*":
                return d1 * d2;
            case "/":
                if (d1 == 0) {
                    throw new IllegalArgumentException(DIV_BY_ZERO);
                }
                return d2 / d1;
            case "^":
                return pow(d2, d1);
        }
        throw new RuntimeException(OP_NOT_FOUND);
    }

    // ------- Infix 2 Postfix ------------------------

    public List<String> infix2Postfix(List<String> tokens) { // TODO - done by me
        List<String> postFix = new ArrayList<>();
        Deque<String> operators = new ArrayDeque<>();

        for (String token : tokens) {
            if (token.matches("\\d+")) {
                postFix.add(token);

            } else if (OPERATORS.contains(token)) {

                while (!operators.isEmpty()
                        && !operators.peek().equals("(")
                        && (getPrecedence(operators.peek()) > getPrecedence(token)
                        || (getPrecedence(operators.peek()) == getPrecedence(token)
                        && getAssociativity(token) == Assoc.LEFT))) {
                    postFix.add(operators.pop());
                }
                operators.push(token);

            } else if (token.equals("(")) {
                operators.push(token);

            } else if (token.equals(")")) {

                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    postFix.add(operators.pop());
                }
                if (operators.isEmpty()) {
                    throw new IllegalArgumentException(MISSING_OPERATOR);
                }
                operators.pop();
            } else {
                throw new RuntimeException(OP_NOT_FOUND);
            }
        }

        while (!operators.isEmpty()) {
            String op = operators.pop();
            if (op.equals("(") || op.equals(")")) {
                throw new IllegalArgumentException(MISSING_OPERATOR);
            }
            postFix.add(op);
        }

        return postFix;
    }

    int getPrecedence(String op) {
        if ("+-".contains(op)) {
            return 2;
        } else if ("*/".contains(op)) {
            return 3;
        } else if ("^".contains(op)) {
            return 4;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    Assoc getAssociativity(String op) {
        if ("+-*/".contains(op)) {
            return Assoc.LEFT;
        } else if ("^".contains(op)) {
            return Assoc.RIGHT;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    enum Assoc {
        LEFT,
        RIGHT
    }

    // ---------- Tokenize -----------------------

    public List<String> tokenize(String expr) { // TODO - Done by me
        if (expr.isEmpty()) {
            throw new IllegalArgumentException(MISSING_OPERAND);
        }
        List<String> tokens = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (OPERATORS.indexOf(c) != -1 || c == '(' || c == ')') {
                if (buffer.length() > 0) {
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else if (c == ' ') {
                if (buffer.length() > 0) {
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                }
            } else if (Character.isDigit(c)) {
                buffer.append(c);
            } else {
                throw new IllegalArgumentException(OP_NOT_FOUND);
            }
        }
        if (buffer.length() > 0) {
            tokens.add(buffer.toString());
        }

        return tokens;
    }


}
