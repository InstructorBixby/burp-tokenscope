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
        assertFalse(Jwt.looksLikeJwt(null));
    }

    @Test
    public void emptySignatureStillParses() {
        assertTrue(Jwt.looksLikeJwt("eyJhbGciOiJub25lIn0.eyJzdWIiOiIxIn0."));
    }

    @Test
    public void readsHeaderClaims() {
        Jwt jwt = Jwt.parse(TOKEN);
        assertEquals("HS256", jwt.getAlg());
        assertEquals("JWT", jwt.header().get("typ").get());
    }

    @Test
    public void readsPayloadClaims() {
        Jwt jwt = Jwt.parse(TOKEN);
        assertEquals("1", jwt.subject().get());
        assertEquals(Long.valueOf(1609459200L), jwt.exp().get());
    }

    @Test
    public void lifetimeNeedsBothClaims() {
        assertFalse(Jwt.parse(TOKEN).lifetimeSeconds().isPresent());
    }

    @Test
    public void notAJwtIsNull() {
        assertNull(Jwt.parse("abc.def"));
    }

    @Test
    public void garbageSegmentsDecodeToNothing() {
        assertEquals(0, Jwt.parse("!!!.!!!.!!!").payload().size());
    }
}
