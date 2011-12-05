package com.rollyourowncode.lisp

public class RYOLisp {
    Env outerEnv

    static void main(args) {
        RYOLisp ryoLisp = new RYOLisp()
        ryoLisp.interactive()
    }

    RYOLisp() {
        outerEnv = addGlobals(new Env())
    }

    def interactive() {
        print "repl >> "
        System.in.eachLine() { line ->
            if (!line.equals("exit")) {
                println(repl(line))
                print "repl >> "
            } else {
                System.exit(0)
            }
        }
    }

    def repl(s) {
        return evaluate(parse(s))
    }

    def evaluate(x, Env env = outerEnv) {
        if (x instanceof String) {
            return evaluateString(env, x)
        } else if (!(x instanceof Collection)) {
            return x;
        } else if (x[0] == "set!") {
            setVariable(x, env)
        } else if (x[0] == "quote") {
            def (_, exp) = x
            return exp
        } else if (x[0] == "if") {
            def (_, test, conseq, alt) = x
            return evaluate( evaluate(test, env) ? conseq : alt , env)
        } else {
            return runProcedure(x, env)
        }
    }

    private def setVariable(Collection x, Env env) {
        def (_, var, exp) = x
        env.put(var, evaluate(exp, env))
    }

    private def evaluateString(Env env, String x) {
        return env.find(x)[x]
    }

    private def runProcedure(Collection x, Env env) {
        def expressions = new ArrayDeque()
        for (expression in x) {
            expressions.add(evaluate(expression, env))
        }
        def procedure = expressions.pop()
        return procedure(expressions.pop(), expressions.pop())
    }

    def addGlobals(Env env) {
        env.put("+", { a, b -> a + b })
        env.put("-", { a, b -> a - b })
        env.put(">", { a, b -> a > b ? 1 : 0})
        env.put("<", { a, b -> a < b ? 1 : 0})
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
        if (token.isFloat()) {
            return token.toFloat()
        }
        //return new Symbol(symbol: token)
        return token
    }

    ArrayDeque<String> tokenize(s) {
        return s.replace('(', ' ( ').replace(')', ' ) ').split()
    }
}
