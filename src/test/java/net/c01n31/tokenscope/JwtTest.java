package net.c01n31.tokenscope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTest {

    // {"alg":"HS256","typ":"JWT"} . {"sub":"1","exp":1609459200} . sig
    private static final String TOKEN =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        + ".eyJzdWIiOiIxIiwiZXhwIjoxNjA5NDU5MjAwfQ"
        + ".c2lnbmF0dXJlCg";

    @Test
    void detectsThreeParts() {
        assertTrue(Jwt.looksLikeJwt(TOKEN));
        assertFalse(Jwt.looksLikeJwt("abc123"));
        assertFalse(Jwt.looksLikeJwt("abc.def"));
        assertFalse(Jwt.looksLikeJwt(null));
    }

    @Test
    @DisplayName("alg none tokens have an empty third segment")
    void emptySignatureStillParses() {
        assertTrue(Jwt.looksLikeJwt("eyJhbGciOiJub25lIn0.eyJzdWIiOiIxIn0."));
    }

    @Test
    void readsHeaderClaims() {
        Jwt jwt = Jwt.parse(TOKEN);
        assertEquals("HS256", jwt.getAlg());
        assertEquals("JWT", jwt.header().get("typ").get());
    }

    @Test
    void readsPayloadClaims() {
        Jwt jwt = Jwt.parse(TOKEN);
        assertEquals("1", jwt.subject().get());
        assertEquals(Long.valueOf(1609459200L), jwt.exp().get());
    }

    @Test
    void lifetimeNeedsBothClaims() {
        assertFalse(Jwt.parse(TOKEN).lifetimeSeconds().isPresent());
    }

    @Test
    void notAJwtIsNull() {
        assertNull(Jwt.parse("abc.def"));
    }

    @Test
    void garbageSegmentsDecodeToNothing() {
        assertEquals(0, Jwt.parse("!!!.!!!.!!!").payload().size());
    }
}
