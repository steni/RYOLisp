package com.rollyourowncode.lisp;

class RYOMiniLispTest extends GroovyTestCase {
    RYOMiniLisp ryoLisp;

    void setUp() {
        ryoLisp = new RYOMiniLisp()
    }

    void testTokenize() {
        def tokens = ryoLisp.tokenize("(+ 1 1)")

        assert tokens.size() == 5
        assert tokens.pop() == "("
        assert tokens.pop() == "+"
        assert tokens.pop() == "1"
        assert tokens.pop() == "1"
        assert tokens.pop() == ")"
    }

    void testParse() {
        assert ryoLisp.parse("(+ 1 1)") == ['+', 1, 1]
    }

    void testAddTwoInts() {
        assert ryoLisp.repl("(+ 1 1)") == 2
    }

    void testAddThreeInts() {
        assert ryoLisp.repl("(+ 1 (+ 1 1))") == 3
    }

    void testAddThreeIntsInOneOperation() {
        assert ryoLisp.repl("(+ 1 1 1)") == 3
    }
}
