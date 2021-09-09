package net.c01n31.tokenscope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// passive observations only. nothing in here sends a request.
public class PassiveChecks {

    public static class Note {
        public final String name;
        public final String severity;
        public final String detail;

        public Note(String name, String severity, String detail) {
            this.name = name;
            this.severity = severity;
            this.detail = detail;
        }
    }

    private static final List<String> SESSIONISH = Arrays.asList(
        "session", "sess", "sid", "auth", "token", "jwt",
        "phpsessid", "jsessionid", "asp.net_sessionid");

    public static boolean looksLikeSession(String cookieName) {
        String low = cookieName.toLowerCase();
        for (String hint : SESSIONISH) {
            if (low.contains(hint)) {
                return true;
            }
        }
        return false;
    }

    public static List<Note> checkSetCookie(String setCookieValue) {
        List<Note> notes = new ArrayList<Note>();
        int eq = setCookieValue.indexOf('=');
        if (eq < 1) {
            return notes;
        }
        String name = setCookieValue.substring(0, eq).trim();
        if (!looksLikeSession(name)) {
            return notes;
        }
        String low = setCookieValue.toLowerCase();
        if (!low.contains("httponly")) {
            notes.add(new Note("Session cookie without HttpOnly",
                "Low", "Set-Cookie for " + name + " has no HttpOnly "
                + "flag, so script in the page can read it."));
        }
        if (!low.contains("secure")) {
            notes.add(new Note("Session cookie without Secure",
                "Low", "Set-Cookie for " + name + " has no Secure "
                + "flag, so it goes out over plain http too."));
        }
        if (!low.contains("samesite")) {
            notes.add(new Note("Session cookie without SameSite",
                "Information", "Set-Cookie for " + name + " does not "
                + "set SameSite. Browsers default to Lax now, but the "
                + "app is relying on that default rather than saying "
                + "what it wants."));
        }
        return notes;
    }
}
