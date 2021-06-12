package net.c01n31.tokenscope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JwtTest {

    // {"alg":"HS256","typ":"JWT"} . {"sub":"1","name":"a"} . sig
    private static final String TOKEN =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        + ".eyJzdWIiOiIxIiwibmFtZSI6ImEifQ"
        + ".c2lnbmF0dXJlCg";

    @Test
    public void detectsThreeParts() {
        assertTrue(Jwt.looksLikeJwt(TOKEN));
        assertFalse(Jwt.looksLikeJwt("abc123"));
        assertFalse(Jwt.looksLikeJwt("abc.def"));
    }

    @Test
    public void decodesHeader() {
        assertEquals("{\"alg\":\"HS256\",\"typ\":\"JWT\"}",
                     Jwt.parse(TOKEN).getHeader());
    }

    @Test
    public void decodesUnpaddedPayload() {
        assertEquals("{\"sub\":\"1\",\"name\":\"a\"}",
                     Jwt.parse(TOKEN).getPayload());
    }

    @Test
    public void garbageDoesNotThrow() {
        assertEquals("", Jwt.parse("!!!.!!!.!!!").getHeader());
    }
}
