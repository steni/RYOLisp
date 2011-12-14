package com.rollyourowncode.lisp

class Env extends HashMap {
    def outer

    Env() {}

    Env(List keys, List values, Env outer = null) {
        assert keys.size() == values.size()
        this.outer = outer
        for (int i = 0; i < keys.size(); i++) {
            put(keys[i], values[i])
        }
    }

    Env(key, value, Env outer = null) {
        println "new Env(), key: " + key + ", value: " + value
        this.outer = outer
        put(key.get(0), value)
    }

    def find(var) {
        try {
            return tryToFind(var)
        } catch ( NullPointerException npe ) {
            println "Cannot find variable " + var
        }

    }

    def tryToFind(var) {
        if (containsKey(var)) {
            return this
        } else {
            return outer.find(var)
        }
    }
}

