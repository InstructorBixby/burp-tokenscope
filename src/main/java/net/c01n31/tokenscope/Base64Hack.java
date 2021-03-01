package net.c01n31.tokenscope;

// decodes standard base64. there is probably something in the jdk that
// does this but i could not find it and this was twenty minutes.
public class Base64Hack {

    private static final String ALPHABET =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    public static byte[] decode(String s) {
        StringBuilder bits = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '=') {
                continue;
            }
            int v = ALPHABET.indexOf(c);
            if (v < 0) {
                continue;
            }
            String b = Integer.toBinaryString(v);
            while (b.length() < 6) {
                b = "0" + b;
            }
            bits.append(b);
        }
        int n = bits.length() / 8;
        byte[] out = new byte[n];
        for (int i = 0; i < n; i++) {
            String b = bits.substring(i * 8, i * 8 + 8);
            out[i] = (byte) Integer.parseInt(b, 2);
        }
        return out;
    }
}
