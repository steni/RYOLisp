package com.rollyourowncode.lisp

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.*

import org.junit.Test
import org.junit.Before
import org.junit.Ignore
import static org.junit.Assert.assertTrue

class RYOLispTest {
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

    @Test
    void evalAssignsValueOnSetThatIsStoredInTheEnvironment() {
        def x = ['set!', 'a', 2]
        ryoLisp.evaluate(x)
        Map env = ryoLisp.outerEnv.find('a')
        def value = env.get('a')
        assertThat(value, is(2))
    }

    @Test
    void evalCanBringOutStoredVariableValues() {
        def x = ['set!', 'a', 2]
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
        String program1 = "(if (> 2 1) (quote 2isbiggerthan1) (quote 2isnotbiggerthanone))"
        String program2 = "(if (> 1 2) (quote 2isbiggerthan1) (quote 2isnotbiggerthanone))"
        assertThat(ryoLisp.repl(program1), is("2isbiggerthan1"))
        assertThat( ryoLisp.repl(program2), is("2isnotbiggerthanone"))
    }

    @Test
    void replCanAddInts() {
        assertThat(ryoLisp.repl("(+ 1 1)"), is(2))
    }


}