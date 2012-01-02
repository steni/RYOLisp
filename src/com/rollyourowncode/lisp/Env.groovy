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
        for (int i = 0; i < keys.size(); i++) {
            put(keys[i], values[i])
        }
    }

    def find(var) {
        containsKey(var) ? this : outer?.find(var)
    }

}

