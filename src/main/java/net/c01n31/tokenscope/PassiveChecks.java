package net.c01n31.tokenscope;

import java.util.ArrayList;
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

    private static final List<String> SESSIONISH = new ArrayList<String>();

    static {
        SESSIONISH.add("session");
        SESSIONISH.add("sess");
        SESSIONISH.add("sid");
        SESSIONISH.add("auth");
        SESSIONISH.add("token");
        SESSIONISH.add("jwt");
        SESSIONISH.add("phpsessid");
        SESSIONISH.add("jsessionid");
        SESSIONISH.add("asp.net_sessionid");
    }

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
        return notes;
    }
}
