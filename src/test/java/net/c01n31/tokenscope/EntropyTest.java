package net.c01n31.tokenscope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EntropyTest {

    @Test
    void emptyIsZero() {
        assertEquals(0.0, Entropy.perChar(""), 0.0001);
    }

    @Test
    void oneRepeatedCharIsZero() {
        assertEquals(0.0, Entropy.perChar("aaaaaaaa"), 0.0001);
    }

    @Test
    void twoEqualCharsIsOneBit() {
        assertEquals(1.0, Entropy.perChar("abab"), 0.0001);
    }

    @Test
    void hexTokenIsAboutFour() {
        double h = Entropy.perChar("9f2c4b1ea7d3680f5c1b9ae4d27f3061");
        assertTrue(h > 3.0 && h < 4.1, "got " + h);
    }

    @Test
    void sequentialIdIsLow() {
        assertTrue(Entropy.perChar("1000000001") < 1.5);
    }
}
