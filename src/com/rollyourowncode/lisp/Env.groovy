package com.rollyourowncode.lisp

class Env extends HashMap {
    def outer

    Env() {}

    Env(List keys, List values, Env outer = null) {
        assert keys.size() == values.size()
        this.outer = outer
        for ( int i = 0; i < keys.size(); i++ ) {
            put(keys[i], values[i])
        }
    }

    def find(var) {
        if ( containsKey(var) ) {
            return this
        } else {
            return outer.find(var)
        }
    }
}

