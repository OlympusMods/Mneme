package com.github.olympus.mneme.core.calc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Calculator {
    public static final class CalcException extends Exception {
        public CalcException(String message) {
            super(message);
        }
    }

    private final String source;
    private final double previous;
    private int position;

    private Calculator(String source, double previous) {
        this.source = source;
        this.previous = previous;
    }

    public static double evaluate(String expression, double previous) throws CalcException {
        Calculator calculator = new Calculator(expression, previous);
        double value = calculator.expression();
        calculator.skipSpaces();
        if (calculator.position < expression.length()) {
            throw new CalcException("Unexpected " + expression.charAt(calculator.position));
        }
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new CalcException("Not a number");
        }
        return value;
    }

    public static String format(double value) {
        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return Long.toString((long) value);
        }
        String text = String.format(Locale.ROOT, "%.6f", value);
        text = text.replaceAll("0+$", "").replaceAll("\\.$", "");
        return text;
    }

    public static String stacks(double value) {
        if (value != Math.rint(value) || Math.abs(value) < 64 || Math.abs(value) >= 1e12) {
            return "";
        }
        long total = Math.abs((long) value);
        String sign = value < 0 ? "-" : "";
        List<String> parts = new ArrayList<>();
        long shulkers = total / 1728;
        long rest = total % 1728;
        long stacks = rest / 64;
        long items = rest % 64;
        if (shulkers > 0) {
            parts.add(shulkers + (shulkers == 1 ? " shulker" : " shulkers"));
        }
        if (stacks > 0) {
            parts.add(stacks + (stacks == 1 ? " stack" : " stacks"));
        }
        if (items > 0) {
            parts.add(Long.toString(items));
        }
        return sign + String.join(" + ", parts);
    }

    private double expression() throws CalcException {
        double value = term();
        while (true) {
            skipSpaces();
            if (accept('+')) {
                value += term();
            } else if (accept('-')) {
                value -= term();
            } else {
                return value;
            }
        }
    }

    private double term() throws CalcException {
        double value = power();
        while (true) {
            skipSpaces();
            if (accept('*') || accept('x') || accept('×')) {
                value *= power();
            } else if (accept('/') || accept('÷')) {
                double divisor = power();
                if (divisor == 0) {
                    throw new CalcException("Division by zero");
                }
                value /= divisor;
            } else if (accept('%')) {
                double divisor = power();
                if (divisor == 0) {
                    throw new CalcException("Division by zero");
                }
                value %= divisor;
            } else {
                return value;
            }
        }
    }

    private double power() throws CalcException {
        double base = unary();
        skipSpaces();
        if (accept('^')) {
            return Math.pow(base, power());
        }
        return base;
    }

    private double unary() throws CalcException {
        skipSpaces();
        if (accept('-')) {
            return -unary();
        }
        if (accept('+')) {
            return unary();
        }
        return unit(primary());
    }

    private double unit(double value) throws CalcException {
        int saved = position;
        skipSpaces();
        String word = word();
        if (word.isEmpty()) {
            position = saved;
            return value;
        }
        double factor = switch (word) {
            case "stack", "stacks" -> 64;
            case "shulker", "shulkers", "box", "boxes", "chest", "chests" -> 1728;
            case "dchest", "dchests", "doublechest" -> 3456;
            default -> 0;
        };
        if (factor == 0) {
            position = saved;
            return value;
        }
        return value * factor;
    }

    private double primary() throws CalcException {
        skipSpaces();
        if (accept('(')) {
            double value = expression();
            skipSpaces();
            if (!accept(')')) {
                throw new CalcException("Missing )");
            }
            return value;
        }
        if (position < source.length() && (Character.isDigit(source.charAt(position)) || source.charAt(position) == '.')) {
            return number();
        }
        String word = word();
        if (word.isEmpty()) {
            throw new CalcException(position < source.length() ? "Unexpected " + source.charAt(position) : "Incomplete");
        }
        if (word.equals("ans")) {
            return previous;
        }
        if (word.equals("pi")) {
            return Math.PI;
        }
        skipSpaces();
        if (!accept('(')) {
            throw new CalcException("Unknown " + word);
        }
        List<Double> arguments = new ArrayList<>();
        skipSpaces();
        if (!accept(')')) {
            do {
                arguments.add(expression());
                skipSpaces();
            } while (accept(','));
            if (!accept(')')) {
                throw new CalcException("Missing )");
            }
        }
        return call(word, arguments);
    }

    private double call(String name, List<Double> arguments) throws CalcException {
        return switch (name) {
            case "sqrt" -> Math.sqrt(one(name, arguments));
            case "abs" -> Math.abs(one(name, arguments));
            case "floor" -> Math.floor(one(name, arguments));
            case "ceil" -> Math.ceil(one(name, arguments));
            case "round" -> Math.round(one(name, arguments));
            case "min" -> two(name, arguments, true);
            case "max" -> two(name, arguments, false);
            default -> throw new CalcException("Unknown " + name);
        };
    }

    private static double one(String name, List<Double> arguments) throws CalcException {
        if (arguments.size() != 1) {
            throw new CalcException(name + " takes one number");
        }
        return arguments.get(0);
    }

    private static double two(String name, List<Double> arguments, boolean smallest) throws CalcException {
        if (arguments.size() != 2) {
            throw new CalcException(name + " takes two numbers");
        }
        return smallest ? Math.min(arguments.get(0), arguments.get(1)) : Math.max(arguments.get(0), arguments.get(1));
    }

    private double number() throws CalcException {
        int start = position;
        while (position < source.length()
                && (Character.isDigit(source.charAt(position)) || source.charAt(position) == '.')) {
            position++;
        }
        try {
            return Double.parseDouble(source.substring(start, position));
        } catch (NumberFormatException exception) {
            throw new CalcException("Bad number");
        }
    }

    private String word() {
        int start = position;
        while (position < source.length() && Character.isLetter(source.charAt(position))) {
            position++;
        }
        return source.substring(start, position).toLowerCase(Locale.ROOT);
    }

    private boolean accept(char expected) {
        if (position < source.length() && source.charAt(position) == expected) {
            position++;
            return true;
        }
        return false;
    }

    private void skipSpaces() {
        while (position < source.length() && Character.isWhitespace(source.charAt(position))) {
            position++;
        }
    }
}
