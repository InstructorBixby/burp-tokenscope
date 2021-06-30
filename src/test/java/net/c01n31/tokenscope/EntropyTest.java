package net.c01n31.tokenscope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EntropyTest {

    @Test
    public void emptyIsZero() {
        assertEquals(0.0, Entropy.perChar(""), 0.0001);
    }

    @Test
    public void oneRepeatedCharIsZero() {
        assertEquals(0.0, Entropy.perChar("aaaaaaaa"), 0.0001);
    }

    @Test
    public void twoEqualCharsIsOneBit() {
        assertEquals(1.0, Entropy.perChar("abab"), 0.0001);
    }

    @Test
    public void hexTokenIsAboutFour() {
        String hex = "9f2c4b1ea7d3680f5c1b9ae4d27f3061";
        double h = Entropy.perChar(hex);
        assertTrue("got " + h, h > 3.0 && h < 4.1);
    }

    @Test
    public void sequentialIdIsLow() {
        assertTrue(Entropy.perChar("1000000001") < 1.5);
    }
}
