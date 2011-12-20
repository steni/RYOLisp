package com.rollyourowncode.lisp

public class RYOMiniLisp {
    Env outerEnv

    RYOMiniLisp() {
        outerEnv = addGlobals(new Env())
    }

    def repl(s) {
        return evaluate(parse(s))
    }

    def evaluate(x, Env env = outerEnv) {
        if (x instanceof String) {
            return env.find(x)[x]
        } else if (!(x instanceof Collection)) {
            return x;
        } else {
            return runProcedure(x, env)
        }
    }

    private def runProcedure(Collection x, Env env) {
        def expressions = new ArrayDeque()
        for (expression in x) {
            expressions.add(evaluate(expression, env))
        }
        def procedure = expressions.pop()
        List arguments = new ArrayList(expressions)
        return procedure(* arguments)
    }

    def addGlobals(Env env) {
        //env.put("+", { a, b -> a + b })
        env.put("+", { Object[] x -> x.sum() })
        return env
    }

    def parse(s) {
        return readFrom(tokenize(s))
    }

    def readFrom(Deque<String> tokens) {
        if (tokens.size() == 0) throw new Exception("unexpected EOF while reading")
        def token = tokens.pop() //[0]
        if ('('.equals(token)) {
            def L = []
            while (tokens.first != ')') {
                L.add(readFrom(tokens))
            }
            tokens.pop() // pop off ')'
            return L
        } else if (')'.equals(token)) {
            throw new Exception("unexpected )")
        } else {
            return atom(token)
        }
    }

    def atom(String token) {
        if (token.isInteger()) {
            return token.toInteger()
        }
        return token
    }

    ArrayDeque<String> tokenize(s) {
        return s.replace('(', ' ( ').replace(')', ' ) ').split()
    }
}
