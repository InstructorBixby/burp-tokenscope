package net.c01n31.tokenscope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PassiveChecksTest {

    // {"alg":"none"} . {"sub":"1"} . (empty)
    private static final String ALG_NONE =
        "eyJhbGciOiJub25lIn0.eyJzdWIiOiIxIn0.";

    // {"alg":"HS256"} . {"sub":"1","exp":1609459200}
    private static final String HS256_EXP =
        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNjA5NDU5MjAwfQ.sig";

    @ParameterizedTest
    @CsvSource({
        "PHPSESSID,          true",
        "JSESSIONID,         true",
        "app_session,        true",
        "access_token,       true",
        "_ga,                false",
        "locale,             false",
        "cart_items,         false",
    })
    void sessionNameHeuristic(String name, boolean expected) {
        assertEquals(expected, PassiveChecks.looksLikeSession(name));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
        "SESSION=a; HttpOnly; Secure; SameSite=Lax | 0",
        "SESSION=a; HttpOnly; Secure                | 1",
        "SESSION=a; Secure                          | 2",
        "SESSION=a; Path=/                          | 3",
        "_ga=GA1.2.3; Path=/                        | 0",
    })
    void cookieFlagNotes(String setCookie, int expected) {
        assertEquals(expected,
            PassiveChecks.checkSetCookie(setCookie.trim()).size());
    }

    @Test
    void algNoneAndNoExp() {
        List<PassiveChecks.Note> notes =
            PassiveChecks.checkJwt(ALG_NONE, 1600000000L);
        assertEquals(2, notes.size());
        assertEquals("JWT with alg none", notes.get(0).name);
        assertEquals("High", notes.get(0).severity);
    }

    @Test
    void shortLifetimeIsQuiet() {
        assertEquals(0, PassiveChecks.checkJwt(HS256_EXP, 1609000000L)
                     .size());
    }

    @Test
    void expADecadeOutIsANote() {
        List<PassiveChecks.Note> notes =
            PassiveChecks.checkJwt(HS256_EXP, 1300000000L);
        assertEquals(1, notes.size());
        assertTrue(notes.get(0).name.contains("long lifetime"));
    }
}
