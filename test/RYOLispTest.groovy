package com.rollyourowncode.lisp

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.*

import org.junit.Test
import org.junit.Before
import org.junit.Ignore
import static org.junit.Assert.assertTrue
import org.junit.rules.ExpectedException

public class RYOLispTest {
    RYOLisp ryoLisp;

    @Before
    void setup() {
        ryoLisp = new RYOLisp()
    }

    @Test
    void tokenize() {
        String program = "(+ 1 1)"
        ArrayDeque<String> tokens = ryoLisp.tokenize(program)
        assertThat(tokens.size(), is(5))
        assertThat(tokens.pop(), is("("))
        assertThat(tokens.pop(), is("+"))
        assertThat(tokens.pop(), is("1"))
        assertThat(tokens.pop(), is("1"))
        assertThat(tokens.pop(), is(")"))
    }

    @Test
    void tokenizeProgram() {
        String program = "(set! x*2 (* x 2))"
        ArrayDeque<String> tokens = ryoLisp.tokenize(program)
        assertThat(tokens.size(), is(9))
        assertThat(tokens.pop(), is("("))
        assertThat(tokens.pop(), is("set!"))
        assertThat(tokens.pop(), is("x*2"))
        assertThat(tokens.pop(), is("("))
        assertThat(tokens.pop(), is("*"))
        assertThat(tokens.pop(), is("x"))
        assertThat(tokens.pop(), is("2"))
        assertThat(tokens.pop(), is(")"))
        assertThat(tokens.pop(), is(")"))

    }

    @Test
    void atomConvertsNumbersToNumbers() {
        assertThat(ryoLisp.atom("1"), is(1))
        assertThat(ryoLisp.atom("1.0"), is(1.0F))
    }

    @Test
    void atomConvertStringsToStrings() {
        assertThat(ryoLisp.atom("+"), instanceOf(String.class))
        assertTrue(ryoLisp.atom("+").equals("+"))
        assertTrue(ryoLisp.atom("hei").equals("hei"))
    }

    @Test
    public void readFromCreatesASimpleExpressionTree() {
        String program = "(+ 1 1)"
        ArrayDeque<String> tokens = ryoLisp.tokenize(program)
        assertThat(ryoLisp.readFrom(tokens), is(['+', 1, 1]))
    }

    @Test
    public void readFromCreatesAnExpressionTree() {
        String program = "(+ 1 (+ 1 1))"
        ArrayDeque<String> tokens = ryoLisp.tokenize(program)
        assertThat(ryoLisp.readFrom(tokens), is(['+', 1, ['+', 1, 1]]))
    }

    @Test
    void parse() {
        String program = "(+ 1 1)"
        assertThat(ryoLisp.parse(program), is(['+', 1, 1]))
    }

    @Test
    void parseHandlesEmptyLists() {
        assertThat(ryoLisp.parse("()"), is([]))
    }

    @Test
    void eval() {
        assertThat(ryoLisp.evaluate("+"), instanceOf(Closure.class))
    }

    @Test
    void evalReturnsNumbersForNumbers() {
        assertThat(ryoLisp.evaluate(1), is(1))
    }

    @Test(expected = NullPointerException.class)
    void setFailsWithoutPrecedingDefine() {
        def x = ['set!', 'a', 2]
        ryoLisp.evaluate(x)
        Map env = ryoLisp.outerEnv.find('a')
        def value = env.get('a')
        assertThat(value, is(2))
    }

    @Test
    void evalAssignsValueOnSetThatIsStoredInTheEnvironment() {
        def define = ['define', 'a', 3]
        ryoLisp.evaluate(define)
        def set = ['set!', 'a', 2]
        ryoLisp.evaluate(set)
        Map env = ryoLisp.outerEnv.find('a')
        def value = env.get('a')
        assertThat(value, is(2))
    }

    @Test
    void evalCanBringOutStoredVariableValues() {
        def x = ['define', 'a', 2]
        ryoLisp.evaluate(x)
        assertThat(ryoLisp.evaluate('a'), is(2))

    }

    @Test
    void evalRunsFunctions() {
        def x = ['+', 1, 1]
        def result = ryoLisp.evaluate(x)
        assertThat(result, is(2))
    }

    @Test
    void quoteReturnsTheAtomOfTheExpression() {
        assertThat(ryoLisp.repl("(quote hei)"), is("hei"))
        assertThat(ryoLisp.repl("(quote 1)"), is(1))
    }

    @Test
    void greaterThan() {
        assertThat(ryoLisp.repl("(> 1 2)"), is(0))
        assertThat(ryoLisp.repl("(> 2 1)"), is(1))
    }

    @Test
    void lessThan() {
        assertThat(ryoLisp.repl("(< 2 1)"), is(0))
        assertThat(ryoLisp.repl("(< 1 2)"), is(1))
    }

    @Test
    void parseIf() {
        String program = "(if (> 2 1) (quote 2isbiggethan1) (quote 2isnotbiggerthanone))"
        assertThat(ryoLisp.parse(program), is(["if", [">", 2, 1], ["quote", "2isbiggethan1"], ["quote", "2isnotbiggerthanone"]]))
    }

    @Test
    void branchingWithIf() {
        String program1 = "(if 1 (quote true) (quote false))"
        String program2 = "(if 0 (quote true) (quote false))"
        assertThat(ryoLisp.repl(program1), is("true"))
        assertThat(ryoLisp.repl(program2), is("false"))
    }

    @Test
    void branchingWithIfEvaluatesTest() {
        String program1 = "(if (> 2 1) (quote 2isbiggerthan1) (quote 2isnotbiggerthanone))"
        String program2 = "(if (> 1 2) (quote 2isbiggerthan1) (quote 2isnotbiggerthanone))"
        assertThat(ryoLisp.repl(program1), is("2isbiggerthan1"))
        assertThat(ryoLisp.repl(program2), is("2isnotbiggerthanone"))
    }

    @Test
    void defineAVariable() {
        String program = "(define a 3)"
        ryoLisp.repl(program)
        assertThat(ryoLisp.repl("a"), is(3))
    }

    @Test
    void defineAVariableEvaluatesExpression() {
        String program = "(define a (+ 1 3))"
        ryoLisp.repl(program)
        assertThat(ryoLisp.repl("a"), is(4))
    }

    @Test
    void lambda() {
        def program = "(lambda (r) (* 3 (* r r)))"
        Object result = ryoLisp.repl(program)
        assertThat(result, instanceOf(Closure.class))

        Closure closure = result
        def number = closure(2)
        assertThat(number, is(12))
    }

    @Test
    void lambdaWithMoreArgs() {
        def program = "(lambda (a b) (* 3 (* a b)))"
        Object result = ryoLisp.repl(program)
        assertThat(result, instanceOf(Closure.class))

        Closure closure = result

        def number = closure(3, 4)

        assertThat(number, is(36))
    }

    @Test
    void defineLambda() {
        def program = "(define area (lambda (r) (* 3 (* r r))))"
        ryoLisp.repl(program)
        assertThat(ryoLisp.repl("(area 2)"), is(12))
    }

    @Test
    void replCanAddInts() {
        assertThat(ryoLisp.repl("(+ 1 1)"), is(2))
    }

    @Test
    void factorialInLisp() {
        def program = "(define fact (lambda (n) (if (<= n 1) 1 (* n (fact (- n 1))))))"
        ryoLisp.repl(program)
        assertThat(ryoLisp.repl("(fact 10)"), is(3628800))
    }

    @Test
    void countInLisp1() {
        ryoLisp.repl("(define first car)")
        ryoLisp.repl("(define rest cdr)")
        ryoLisp.repl("(define count (lambda (item L) (if L (+ (equal? item (first L)) (count item (rest L))) 0)))")
        assertThat(ryoLisp.repl("(count 0 (list 0 1 2 3 0 0))"), is(3))
    }

}