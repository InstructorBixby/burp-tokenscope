package net.c01n31.tokenscope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** Passive observations only. Nothing in here sends a request. */
public final class PassiveChecks {

    /** One thing worth telling the user about. */
    public static final class Note {

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

    /** A year; longer than this means no real expiry story. */
    private static final long LONG_LIFETIME = 365L * 24 * 3600;

    private PassiveChecks() {
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
            notes.add(new Note("Session cookie without HttpOnly", "Low",
                "Set-Cookie for " + name + " has no HttpOnly flag, so "
                + "script in the page can read it."));
        }
        if (!low.contains("secure")) {
            notes.add(new Note("Session cookie without Secure", "Low",
                "Set-Cookie for " + name + " has no Secure flag, so it "
                + "goes out over plain http too."));
        }
        if (!low.contains("samesite")) {
            notes.add(new Note("Session cookie without SameSite",
                "Information", "Set-Cookie for " + name + " does not "
                + "set SameSite, so the app relies on the browser "
                + "default rather than saying what it wants."));
        }
        return notes;
    }

    public static List<Note> checkJwt(String token, long nowSeconds) {
        List<Note> notes = new ArrayList<Note>();
        Jwt jwt = Jwt.parse(token);
        if (jwt == null) {
            return notes;
        }

        if ("none".equalsIgnoreCase(jwt.getAlg())) {
            notes.add(new Note("JWT with alg none", "High",
                "The application issued a token whose header says "
                + "alg=none, so it carries no signature at all."));
        }

        Optional<Long> exp = jwt.exp();
        if (!exp.isPresent()) {
            notes.add(new Note("JWT without exp", "Low",
                "The token has no exp claim, so nothing in the token "
                + "itself limits how long it stays valid."));
        } else if (exp.get() - nowSeconds > LONG_LIFETIME) {
            long days = (exp.get() - nowSeconds) / (24 * 3600);
            notes.add(new Note("JWT with a very long lifetime", "Low",
                "The exp claim is " + days + " days away."));
        }
        return notes;
    }
}
