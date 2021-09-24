package net.c01n31.tokenscope;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// reads a jwt. does NOT verify the signature, and never will -- this is
// for looking at what an app is handing out, nothing else.
public class Jwt {

    private final String header;
    private final String payload;

    private Jwt(String header, String payload) {
        this.header = header;
        this.payload = payload;
    }

    public static boolean looksLikeJwt(String token) {
        return token.split("\\.").length == 3;
    }

    public static Jwt parse(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }
        return new Jwt(decode(parts[0]), decode(parts[1]));
    }

    private static String decode(String part) {
        String b64 = part;
        int pad = b64.length() % 4;
        if (pad == 2) {
            b64 = b64 + "==";
        } else if (pad == 3) {
            b64 = b64 + "=";
        }
        try {
            return new String(Base64.getUrlDecoder().decode(b64), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    // not a json parser. pulling one scalar claim out of a flat object
    // is all this needs to do, and i am not shading a json library in
    // for it yet.
    private static String claim(String json, String key) {
        Pattern p = Pattern.compile(
            "\"" + Pattern.quote(key) + "\"\\s*:\\s*(\"([^\"]*)\"|[0-9]+)");
        Matcher m = p.matcher(json);
        if (!m.find()) {
            return null;
        }
        return m.group(2) != null ? m.group(2) : m.group(1);
    }

    public String getHeader() {
        return header;
    }

    public String getPayload() {
        return payload;
    }

    public String getAlg() {
        return claim(header, "alg");
    }

    public String getSubject() {
        return claim(payload, "sub");
    }

    public Long getExp() {
        String v = claim(payload, "exp");
        if (v == null) {
            return null;
        }
        try {
            return Long.valueOf(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
