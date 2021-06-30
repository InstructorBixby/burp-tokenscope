package net.c01n31.tokenscope;

import java.util.HashMap;
import java.util.Map;

// rough shannon entropy, in bits per character. a 32 char hex token
// tops out around 4 bits/char, base64 around 6. anything much under
// 3 is either short or has structure in it worth looking at.
public class Entropy {

    public static double perChar(String s) {
        if (s == null || s.length() == 0) {
            return 0.0;
        }
        Map<Character, Integer> counts = new HashMap<Character, Integer>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            Integer n = counts.get(c);
            counts.put(c, n == null ? 1 : n + 1);
        }
        double h = 0.0;
        double len = s.length();
        for (Integer n : counts.values()) {
            double p = n / len;
            h = h - p * (Math.log(p) / Math.log(2));
        }
        return h;
    }

    public static double total(String s) {
        return s == null ? 0.0 : perChar(s) * s.length();
    }
}
