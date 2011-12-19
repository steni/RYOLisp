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
            def (_, var, exp) = x
            env.find(var)[var] = evaluate(exp, env)
        } else if (x[0] == "quote") {
            def (_, exp) = x
            return exp
        } else if (x[0] == "if") {
            def (_, test, conseq, alt) = x
            return evaluate(evaluate(test, env) ? conseq : alt, env)
        } else if (x[0] == "define") {
            def (_, var, exp) = x
            env[var] = evaluate(exp, env)
        } else if (x[0] == "lambda") {
            def (_, vars, exp) = x
            println "lambda: vars: " + vars + ", exp: " + exp
            return { Object[] args -> evaluate(exp, new Env(vars, args, env))}
        } else if (x[0] == "eval") {
            def (_, form) = x
            return evaluate(form, env)
        } else {
            return runProcedure(x, env)
        }
    }

    private def evaluateString(Env env, String x) {
        println "evaluate string, find in env: " + x
        return env.find(x)[x]
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
        env.putAll([
                "+": { Object[] x -> x.sum() },
                "-": { a, b -> a - b },
                "*": { a, b -> a * b },
                ">": { a, b -> a > b ? 1 : 0 },
                "<": { a, b -> a < b ? 1 : 0 },
                "<=": { a, b -> a <= b ? 1 : 0 },
                ">=": { a, b -> a >= b ? 1 : 0 },

                "not": { !it},

                "car": { it.head()},
                "cdr": { it.tail()},

                "list": { Object[] x -> [* x]},
                "list?": { it instanceof List ? 1 : 0 },

                "equal?": { a, b -> a == b ? 1 : 0 },

                "cons": { x, y -> [x] + y }])
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
