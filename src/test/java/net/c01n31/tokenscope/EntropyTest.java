package net.c01n31.tokenscope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EntropyTest {

    // the same 16 bytes, written two ways
    private static final String HEX =
        "9f2c4b1ea7d3680f5c1b9ae4d27f3061";
    private static final String B64 = "nyxLHqfTaA9cG5rk0n8wYQ";

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
    void hexDecodesToSixteenBytes() {
        assertEquals(16, Entropy.decodeIfEncoded(HEX).length);
    }

    @Test
    void base64urlDecodesToSixteenBytes() {
        assertEquals(16, Entropy.decodeIfEncoded(B64).length);
    }

    @Test
    void sameBytesScoreTheSameEitherWay() {
        assertEquals(Entropy.perByte(HEX), Entropy.perByte(B64), 0.0001);
    }

    @Test
    void oddShapedTokensAreNotDecoded() {
        assertNull(Entropy.decodeIfEncoded("abc.def.ghi"));
        assertNull(Entropy.decodeIfEncoded("short"));
    }

    @Test
    void sequentialIdIsLow() {
        assertTrue(Entropy.perByte("1000000001") < 2.5);
    }
}
