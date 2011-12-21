package com.rollyourowncode.lisp;

class EnvTest extends GroovyTestCase {
    Env env

    void setUp() {
        env = new Env(["key1", "key2", new Symbol(symbol: "key3")], ["value1", "value2", "value3"])
    }

    void testCanGetValueBasedOnKey() {
        assert env.key2 == "value2"
    }
}
