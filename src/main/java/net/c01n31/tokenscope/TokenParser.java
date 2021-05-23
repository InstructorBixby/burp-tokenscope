package net.c01n31.tokenscope;

import java.util.ArrayList;
import java.util.List;

// pulls candidate session tokens out of header lists.
public class TokenParser {

    public static class Found {
        public final String name;
        public final String value;

        public Found(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

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
            if (low.startsWith("authorization: bearer ")) {
                out.add(new Found("Authorization",
                    h.substring("authorization: bearer ".length()).trim()));
            } else if (low.startsWith("x-auth-token:")
                    || low.startsWith("x-csrf-token:")
                    || low.startsWith("x-session-token:")) {
                out.add(new Found(h.substring(0, h.indexOf(':')),
                    h.substring(h.indexOf(':') + 1).trim()));
            }
        }
        return out;
    }
}
