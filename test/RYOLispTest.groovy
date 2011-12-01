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
        assertThat( tokens.size(), is(9))
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
        assertThat(ryoLisp.atom("hei"), instanceOf(String.class))
        assertTrue(ryoLisp.atom("hei").equals("hei"))
    }

    @Test
    void parse() {
        String program = "(+ 1 1)"
        assertThat(ryoLisp.parse(program), is(['+', 1, 1]))
    }

    @Test @Ignore
    void eval() {
        assertThat(ryoLisp.evaluate("+"), is({}))
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
        assertThat(value , is(2))
    }

    @Test
    void evalCanBringOutStoredVariableValues() {
        def x = ['set!', 'a', 2]
        ryoLisp.evaluate(x)
        assertThat( ryoLisp.evaluate('a'), is(2))

    }

    @Test
    void evalRunsFunctions() {
        def x = ['+', 1, 1]
        def result = ryoLisp.evaluate(x)
        assertThat( result, is(2))
    }

    @Test
    void replCanAddInts() {
        assertThat(ryoLisp.repl("(+ 1 1)"), is(2))
    }


}