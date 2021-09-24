package net.c01n31.tokenscope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JwtTest {

    // {"alg":"HS256","typ":"JWT"} . {"sub":"1","exp":1609459200} . sig
    private static final String TOKEN =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        + ".eyJzdWIiOiIxIiwiZXhwIjoxNjA5NDU5MjAwfQ"
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
    public void readsAlg() {
        assertEquals("HS256", Jwt.parse(TOKEN).getAlg());
    }

    @Test
    public void readsStringClaim() {
        assertEquals("1", Jwt.parse(TOKEN).getSubject());
    }

    @Test
    public void readsNumericExp() {
        assertEquals(Long.valueOf(1609459200L), Jwt.parse(TOKEN).getExp());
    }

    @Test
    public void missingClaimIsNull() {
        String noExp = "eyJhbGciOiJub25lIn0.eyJzdWIiOiIxIn0.";
        assertNull(Jwt.parse(noExp).getExp());
    }

    @Test
    public void garbageDoesNotThrow() {
        assertEquals("", Jwt.parse("!!!.!!!.!!!").getHeader());
    }
}
