package net.c01n31.tokenscope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class PassiveChecksTest {

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
        List<PassiveChecks.Note> notes = PassiveChecks.checkSetCookie(
            "SESSION=abc; Path=/; HttpOnly; Secure; SameSite=Lax");
        assertEquals(0, notes.size());
    }

    @Test
    public void missingEverythingGivesThreeNotes() {
        List<PassiveChecks.Note> notes =
            PassiveChecks.checkSetCookie("SESSION=abc; Path=/");
        assertEquals(3, notes.size());
    }

    @Test
    public void ignoresNonSessionCookies() {
        assertEquals(0,
            PassiveChecks.checkSetCookie("_ga=GA1.2.3; Path=/").size());
    }
}
