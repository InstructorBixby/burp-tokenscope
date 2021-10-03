package net.c01n31.tokenscope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class PassiveChecksTest {

    // {"alg":"none"} . {"sub":"1"} . (empty)
    private static final String ALG_NONE =
        "eyJhbGciOiJub25lIn0.eyJzdWIiOiIxIn0.";

    // {"alg":"HS256"} . {"sub":"1","exp":1609459200}
    private static final String HS256_EXP =
        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNjA5NDU5MjAwfQ.sig";

    @Test
    public void recognisesCommonSessionNames() {
        assertTrue(PassiveChecks.looksLikeSession("PHPSESSID"));
        assertTrue(PassiveChecks.looksLikeSession("JSESSIONID"));
        assertTrue(PassiveChecks.looksLikeSession("app_session"));
        assertFalse(PassiveChecks.looksLikeSession("_ga"));
        assertFalse(PassiveChecks.looksLikeSession("locale"));
    }

    @Test
    public void allFlagsPresentIsQuiet() {
        assertEquals(0, PassiveChecks.checkSetCookie(
            "SESSION=abc; Path=/; HttpOnly; Secure; SameSite=Lax").size());
    }

    @Test
    public void missingEverythingGivesThreeNotes() {
        assertEquals(3,
            PassiveChecks.checkSetCookie("SESSION=abc; Path=/").size());
    }

    @Test
    public void ignoresNonSessionCookies() {
        assertEquals(0,
            PassiveChecks.checkSetCookie("_ga=GA1.2.3; Path=/").size());
    }

    @Test
    public void algNoneAndNoExp() {
        List<PassiveChecks.Note> notes =
            PassiveChecks.checkJwt(ALG_NONE, 1600000000L);
        assertEquals(2, notes.size());
        assertEquals("JWT with alg none", notes.get(0).name);
        assertEquals("High", notes.get(0).severity);
    }

    @Test
    public void expiredOrSoonIsNotANote() {
        assertEquals(0,
            PassiveChecks.checkJwt(HS256_EXP, 1609000000L).size());
    }

    @Test
    public void expADecadeOutIsANote() {
        List<PassiveChecks.Note> notes =
            PassiveChecks.checkJwt(HS256_EXP, 1300000000L);
        assertEquals(1, notes.size());
        assertTrue(notes.get(0).name.contains("long lifetime"));
    }
}
