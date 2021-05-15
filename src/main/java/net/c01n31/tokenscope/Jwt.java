package net.c01n31.tokenscope;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

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

    // jwt uses the url safe alphabet (- and _ instead of + and /) and
    // strips the = padding, so the standard decoder throws on about
    // two thirds of real tokens. getUrlDecoder() plus padding back on.
    private static String decode(String part) {
        String b64 = part;
        int pad = b64.length() % 4;
        if (pad == 2) {
            b64 = b64 + "==";
        } else if (pad == 3) {
            b64 = b64 + "=";
        }
        try {
            byte[] raw = Base64.getUrlDecoder().decode(b64);
            return new String(raw, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    public String getHeader() {
        return header;
    }

    public String getPayload() {
        return payload;
    }
}
