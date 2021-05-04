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

    private static String decode(String part) {
        try {
            return new String(Base64.getDecoder().decode(part), "UTF-8");
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
