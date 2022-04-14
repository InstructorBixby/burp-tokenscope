package net.c01n31.tokenscope;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Shannon entropy estimate, in bits per byte.
 *
 * <p>The first version ran over the token's characters, which measures
 * the encoding as much as the secret: 16 random bytes in hex scores
 * about 4 bits/char and the same 16 bytes in base64 about 6, while
 * both carry the same 128 bits. So decode first where the token
 * obviously is an encoding, and report bits per underlying byte.
 *
 * <p>It is still only an estimate. 16 bytes is 16 samples, and 4.0
 * bits/byte out of a possible 8 is what random data of that length
 * looks like. Treat a score under ~2.5 on a long token as the
 * interesting case, not the absolute number.
 */
public final class Entropy {

    private Entropy() {
    }

    public static double perChar(String s) {
        return bitsPerSymbol(s);
    }

    /** Entropy per byte of the decoded value where one can be decoded. */
    public static double perByte(String token) {
        byte[] raw = decodeIfEncoded(token);
        if (raw == null) {
            return bitsPerSymbol(token);
        }
        Map<Byte, Integer> counts = new HashMap<Byte, Integer>();
        for (byte b : raw) {
            Integer n = counts.get(b);
            counts.put(b, n == null ? 1 : n + 1);
        }
        return shannon(counts.values(), raw.length);
    }

    static byte[] decodeIfEncoded(String token) {
        if (token == null || token.length() < 8) {
            return null;
        }
        if (token.matches("[0-9a-fA-F]+") && token.length() % 2 == 0) {
            byte[] out = new byte[token.length() / 2];
            for (int i = 0; i < out.length; i++) {
                out[i] = (byte) Integer.parseInt(
                    token.substring(i * 2, i * 2 + 2), 16);
            }
            return out;
        }
        if (token.matches("[A-Za-z0-9_-]+")) {
            String b64 = token;
            int pad = b64.length() % 4;
            if (pad == 2) {
                b64 = b64 + "==";
            } else if (pad == 3) {
                b64 = b64 + "=";
            } else if (pad == 1) {
                return null;
            }
            try {
                return Base64.getUrlDecoder().decode(b64);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    private static double bitsPerSymbol(String s) {
        if (s == null || s.isEmpty()) {
            return 0.0;
        }
        Map<Character, Integer> counts =
            new HashMap<Character, Integer>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            Integer n = counts.get(c);
            counts.put(c, n == null ? 1 : n + 1);
        }
        return shannon(counts.values(), s.length());
    }

    private static double shannon(Iterable<Integer> counts, int total) {
        double h = 0.0;
        for (Integer n : counts) {
            double p = n / (double) total;
            h = h - p * (Math.log(p) / Math.log(2));
        }
        return h;
    }
}
