package com.sprintrunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GreetTest {

    @Test
    void greetsNamedUser() {
        assertEquals("Hello Balaji", new Greet().sayHello("Balaji"));
    }

    @Test
    void greetsWorldWhenNameIsMissing() {
        Greet greet = new Greet();
        assertEquals("Hello World!", greet.sayHello(""));
        assertEquals("Hello World!", greet.sayHello(null));
    }
}

