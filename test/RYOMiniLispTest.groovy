package com.rollyourowncode.lisp;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.assertThat;

public class RYOMiniLispTest {
    RYOMiniLisp ryoLisp;

    @Before
    void setup() {
        ryoLisp = new RYOMiniLisp()
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
    void parse() {
        String program = "(+ 1 1)"
        assertThat(ryoLisp.parse(program), is(['+', 1, 1]))
    }

    @Test
    void addTwoInts() {
        assertThat(ryoLisp.repl("(+ 1 1)"), is(2))
    }

    @Test
    void addThreeInts() {
        assertThat(ryoLisp.repl("(+ 1 (+ 1 1))"), is(3))
    }
    
    @Test 
    void addThreeIntsInOneOperation() {
        assertThat(ryoLisp.repl("(+ 1 1 1)"), is(3))
    }
}
