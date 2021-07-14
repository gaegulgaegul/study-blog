package me.gaegul.ch19;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.function.Function;
import java.util.function.Supplier;

public class PatternMatching {
    public static void main(String[] args) {

    }

    static class Expr {}

    @ToString
    @AllArgsConstructor
    static class Number extends Expr {
        int val;
    }

    @ToString
    @AllArgsConstructor
    static class BinOp extends Expr {
        String opname;
        Expr left, right;
    }

    static <T> T myIf(boolean b, Supplier<T> trueCase, Supplier<T> falseCase) {
        return b ? trueCase.get() : falseCase.get();
    }

    interface TriFunction<S, T, U, R> {
        R apply(S s, T t, U u);
    }

    static <T> T patternMatchExpr(Expr e, TriFunction<String, Expr, Expr, T> binopCase, Function<Integer, T> numCase, Supplier<T> defaultCase) {
        return (e instanceof BinOp) ? binopCase.apply(((BinOp) e).opname, ((BinOp) e).left, ((BinOp) e).right) :
                (e instanceof  Number) ? numCase.apply(((Number) e).val) : defaultCase.get();
    }
}
