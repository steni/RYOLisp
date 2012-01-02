package com.rollyourowncode.lisp

class Env {
    def outer

    @Delegate
    private Map map = [:]

    Env() {}

    Env(def keys, def values, Env outer = null) {
        println "new Env(), keys: $keys, values: $values"
        assert keys.size() == values.size()
        this.outer = outer
        keys.eachWithIndex { it, index ->
            put(it, values[index])
        }
    }

    def find(var) {
        containsKey(var) ? this : outer?.find(var)
    }

}

