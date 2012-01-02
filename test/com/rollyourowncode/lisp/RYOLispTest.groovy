package com.rollyourowncode.lisp

class RYOLispTest extends GroovyTestCase {
    RYOLisp ryoLisp;

    void setUp() {
        ryoLisp = new RYOLisp()
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

    void testTokenizeProgram() {
        def tokens = ryoLisp.tokenize("(set! x*2 (* x 2))")
        assert tokens.size() == 9
        assert tokens.pop() == "("
        assert tokens.pop() == "set!"
        assert tokens.pop() == "x*2"
        assert tokens.pop() == "("
        assert tokens.pop() == "*"
        assert tokens.pop() == "x"
        assert tokens.pop() == "2"
        assert tokens.pop() == ")"
        assert tokens.pop() == ")"
    }

    void testAtomConvertsNumbersToNumbers() {
        assert ryoLisp.atom("1") == 1
        assert ryoLisp.atom("1.0") == 1.0F
    }

    void testAtomConvertStringsToStrings() {
        assert ryoLisp.atom("+") instanceof String
        assert ryoLisp.atom("+") == "+"
        assert ryoLisp.atom("hei") == "hei"
    }

    void testReadFromCreatesASimpleExpressionTree() {
        def tokens = ryoLisp.tokenize("(+ 1 1)")
        assert ryoLisp.readFrom(tokens) == ['+', 1, 1]
    }

    void testReadFromCreatesAnExpressionTree() {
        def tokens = ryoLisp.tokenize("(+ 1 (+ 1 1))")
        assert ryoLisp.readFrom(tokens) == ['+', 1, ['+', 1, 1]]
    }

    void testparse() {
        assert ryoLisp.parse("(+ 1 1)") == ['+', 1, 1]
    }

    void testParseHandlesEmptyLists() {
        assert ryoLisp.parse("()") == []
    }

    void testEvalRecognizesGlobalFunctions() {
        assert ryoLisp.evaluate("+") instanceof Closure
    }

    void testEvalReturnsNumbersForNumbers() {
        assert ryoLisp.evaluate(1) == 1
    }

    void testSetFailsWithoutPrecedingDefine() {
        shouldFail(NullPointerException) {
            def x = ['set!', 'a', 2]
            ryoLisp.evaluate(x)
            def env = ryoLisp.outerEnv.find('a')
            def value = env.get('a')
            assert value == 2
        }
    }

    void testEvalAssignsValueOnSetThatIsStoredInTheEnvironment() {
        def define = ['define', 'a', 3]
        ryoLisp.evaluate(define)
        def set = ['set!', 'a', 2]
        ryoLisp.evaluate(set)
        def env = ryoLisp.outerEnv.find('a')
        def value = env.get('a')
        assert value == 2
    }

    void testEvalCanBringOutStoredVariableValues() {
        def x = ['define', 'a', 2]
        ryoLisp.evaluate(x)
        assert ryoLisp.evaluate('a') == 2

    }

    void testEvalRunsFunctions() {
        def result = ryoLisp.evaluate(['+', 1, 1])
        assert result == 2
    }

    void testPlusHandlesArbitraryNumberOfArguments() {
        assert ryoLisp.repl("(+ 2 3 4 5)") == 14
    }

    void testQuoteReturnsTheAtomOfTheExpression() {
        assert ryoLisp.repl("(quote hei)") == "hei"
        assert ryoLisp.repl("(quote 1)") == 1
    }

    void testQuoteEmptyList() {
        assert ryoLisp.repl("(quote ())") == []
    }

    void testQuoteListOfSymbols() {
        assert ryoLisp.repl("(quote (a b c))") == ['a', 'b', 'c']
    }

    void testGreaterThan() {
        assert ryoLisp.repl("(> 1 2)") == 0
        assert ryoLisp.repl("(> 2 1)") == 1
    }

    void testLessThan() {
        assert ryoLisp.repl("(< 2 1)") == 0
        assert ryoLisp.repl("(< 1 2)") == 1
    }

    void testCar() {
        assert ryoLisp.repl("(car (list 1 2 3))") == 1
    }

    void testCdr() {
        assert ryoLisp.repl("(cdr (list 1 2 3))") == [2, 3]
    }

    void testParseIf() {
        String program = "(if (> 2 1) (quote 2isbiggethan1) (quote 2isnotbiggerthanone))"
        assert ryoLisp.parse(program) == ["if", [">", 2, 1], ["quote", "2isbiggethan1"], ["quote", "2isnotbiggerthanone"]]
    }

    void testBranchingWithIf() {
        assert ryoLisp.repl("(if 1 (quote true) (quote false))") == "true"
        assert ryoLisp.repl("(if 0 (quote true) (quote false))") == "false"
    }

    void testBranchingWithIfEvaluatesTest() {
        assert ryoLisp.repl("(if (> 2 1) (quote 2isbiggerthan1) (quote 2isnotbiggerthanone))") == "2isbiggerthan1"
        assert ryoLisp.repl("(if (> 1 2) (quote 2isbiggerthan1) (quote 2isnotbiggerthanone))") == "2isnotbiggerthanone"
    }

    void testDefineAVariable() {
        ryoLisp.repl("(define a 3)")
        assert ryoLisp.repl("a") == 3
    }

    void testDefineAVariableEvaluatesExpression() {
        ryoLisp.repl("(define a (+ 1 3))")
        assert ryoLisp.repl("a") == 4
    }

    void testfn() {
        def program = "(fn (r) (* 3 (* r r)))"
        def result = ryoLisp.repl(program)
        assert result instanceof Closure

        Closure closure = result
        def number = closure(2)
        assert number == 12
    }

    void testFunctionCall() {
        assert ryoLisp.repl("((fn (r) (* 3 (* r r))) 2)") == 12
    }

    void testfnWithMoreArgs() {
        def program = "(fn (a b) (* 3 (* a b)))"
        def result = ryoLisp.repl(program)
        assert result instanceof Closure

        Closure closure = result
        def number = closure(3, 4)
        assert number == 36
    }

    void testDefinefn() {
        ryoLisp.repl("(define area (fn (r) (* 3 (* r r))))")
        assert ryoLisp.repl("(area 2)") == 12
    }

    void testRyoList() {
        assert ryoLisp.repl("(list 0 1 2)") == ([0, 1, 2])
    }

    void testIsList() {
        assert ryoLisp.repl("(list? (list 0 1 2))") == 1
    }

    void testReplCanAddInts() {
        assert ryoLisp.repl("(+ 1 1)") == 2
    }

    void testFactorialInLisp() {
        def program = "(define fact (fn (n) (if (<= n 1) 1 (* n (fact (- n 1))))))"
        ryoLisp.repl(program)
        assert ryoLisp.repl("(fact 10)") == 3628800
    }

    void testCountInLisp1() {
        ryoLisp.repl("(define first car)")
        ryoLisp.repl("(define rest cdr)")
        ryoLisp.repl("(define count (fn (item L) (if L (+ (equal? item (first L)) (count item (rest L))) 0)))")
        assert ryoLisp.repl("(count 0 (list 0 1 2 3 0 0))") == 3
    }

    void testCountInLisp2() {
        ryoLisp.repl("(define first car)")
        ryoLisp.repl("(define rest cdr)")
        ryoLisp.repl("(define count (fn (item L) (if L (+ (equal? item (first L)) (count item (rest L))) 0)))")
        assert ryoLisp.repl("(count (quote the) (quote (the more the merrier the bigger better)))") == 3
    }

    void testConsNumberOntoEmptyList() {
        assert ryoLisp.repl("(cons 1 (quote()))") == [1]
    }

    void testConsNumberOntoListOfNumbers() {
        assert ryoLisp.repl("(cons 1 (list 2 3))") == [1, 2, 3]
    }

    void testConsSymbolOntoEmpyList() {
        assert ryoLisp.repl("(cons (quote a) (quote()))") == ['a']
    }

    void testConsSymbolOntoListOfSymbols() {
        assert ryoLisp.repl("(cons (quote a) (cons (quote b) (quote())))") == ['a', 'b']
    }

    void testBeginEvaluatesFromLeftToRightAndReturnsRightmostValue() {
        def program = "(begin (define x 1) (define x (+ x 1)) (* x 2))"
        assert ryoLisp.parse(program) == ['begin', ['define', 'x', 1], ['define', 'x', ['+', 'x', 1]], ['*', 'x', 2]]
        assert ryoLisp.repl(program) == 4
    }

    void testClosureEnclosesValues() {
        def program = """(begin
                            (define a 2) 
                            (define multiplyByA 
                                (fn (x) (* x a))
                            )
                            (multiplyByA 3)
                         )"""
        assert ryoLisp.repl(program) == 6
    }

    void testLexicalScopingForfns() {
        def program = """(begin
                            (define a 2)
                            (define multiplyByA
                                (fn (x)
                                    (begin
                                        (define a (* x a))
                                        a
                                    )
                                )
                            )                            
                         )"""
        ryoLisp.repl(program)
        assert ryoLisp.repl("(multiplyByA 3)") == 6
        assert ryoLisp.repl("a") == 2
    }

    void testEvalInCode() {
        ryoLisp.repl("(define aListToEvaluateLater (list (quote +) 1 2))")
        assert ryoLisp.repl("(eval aListToEvaluateLater)") == 3
    }

    void testClosureEnclosesValuesAndKeepsThem() {
        def program = """(begin
                            (define a 2)
                            (define multiplyByA
                                (fn (x) (* x a))
                            )
                            (set! a 3)
                            (multiplyByA 3)
                         )"""
        assert ryoLisp.repl(program) == 9
    }
}