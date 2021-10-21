package net.c01n31.tokenscope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Pulls candidate session tokens out of header lists. */
public class TokenParser {

    /** One name/value pair that looked like a token. */
    public static class Found {

        public final String name;
        public final String value;

        public Found(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    private static final String BEARER = "authorization: bearer ";

    private static final List<String> HEADERS = Arrays.asList(
        "x-auth-token:", "x-csrf-token:", "x-session-token:",
        "x-access-token:");

    public static List<Found> fromResponse(List<String> headers) {
        List<Found> out = new ArrayList<Found>();
        for (String h : headers) {
            if (!h.toLowerCase().startsWith("set-cookie:")) {
                continue;
            }
            String value = h.substring(h.indexOf(':') + 1).trim();
            String pair = value.split(";")[0];
            int eq = pair.indexOf('=');
            if (eq < 1) {
                continue;
            }
            out.add(new Found(pair.substring(0, eq).trim(),
                              pair.substring(eq + 1).trim()));
        }
        return out;
    }

    public static List<Found> fromRequest(List<String> headers) {
        List<Found> out = new ArrayList<Found>();
        for (String h : headers) {
            String low = h.toLowerCase();
            if (low.startsWith(BEARER)) {
                out.add(new Found("Authorization",
                                  h.substring(BEARER.length()).trim()));
                continue;
            }
            for (String prefix : HEADERS) {
                if (low.startsWith(prefix)) {
                    out.add(new Found(h.substring(0, h.indexOf(':')),
                        h.substring(h.indexOf(':') + 1).trim()));
                    break;
                }
            }
        }
        return out;
    }
}
