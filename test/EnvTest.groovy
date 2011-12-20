package com.rollyourowncode.lisp;

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.*

import org.junit.Before;
import org.junit.Test;

class EnvTest {
    Env env

    @Before
    void setUp() {
        env = new Env(["key1", "key2", new Symbol(symbol: "key3")], ["value1", "value2", "value3"])
    }

    @Test
    void canGetValueBasedOnKey() {
        assertThat( env.get("key2"), is("value2") )
    }
}
