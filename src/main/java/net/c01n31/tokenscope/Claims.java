package net.c01n31.tokenscope;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Minimal reader for a flat JSON object, which is what a JWT payload
 * is in practice. Nested objects and arrays are kept as their raw text
 * rather than parsed, because nothing here needs to walk into them.
 *
 * This is not a general JSON parser and should not grow into one; if it
 * ever needs to be, shade in a real library instead.
 */
public final class Claims {

    private final Map<String, String> values;

    private Claims(Map<String, String> values) {
        this.values = values;
    }

    public static Claims parse(String json) {
        Map<String, String> out = new LinkedHashMap<String, String>();
        if (json == null) {
            return new Claims(out);
        }
        int i = 0;
        while (i < json.length()) {
            int keyStart = json.indexOf('"', i);
            if (keyStart < 0) {
                break;
            }
            int keyEnd = json.indexOf('"', keyStart + 1);
            if (keyEnd < 0) {
                break;
            }
            String key = json.substring(keyStart + 1, keyEnd);
            int colon = json.indexOf(':', keyEnd);
            if (colon < 0) {
                break;
            }
            i = readValue(json, colon + 1, key, out);
        }
        return new Claims(out);
    }

    private static int readValue(String json, int from, String key,
                                 Map<String, String> out) {
        int i = from;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }
        if (i >= json.length()) {
            return json.length();
        }
        char c = json.charAt(i);
        if (c == '"') {
            int end = json.indexOf('"', i + 1);
            if (end < 0) {
                return json.length();
            }
            out.put(key, json.substring(i + 1, end));
            return end + 1;
        }
        if (c == '{' || c == '[') {
            char close = c == '{' ? '}' : ']';
            int depth = 0;
            int start = i;
            while (i < json.length()) {
                char d = json.charAt(i);
                if (d == c) {
                    depth++;
                } else if (d == close) {
                    depth--;
                    if (depth == 0) {
                        out.put(key, json.substring(start, i + 1));
                        return i + 1;
                    }
                }
                i++;
            }
            return json.length();
        }
        int end = i;
        while (end < json.length() && ",}".indexOf(json.charAt(end)) < 0) {
            end++;
        }
        out.put(key, json.substring(i, end).trim());
        return end;
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(values.get(key));
    }

    public Optional<Long> getLong(String key) {
        try {
            return get(key).map(Long::valueOf);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(values);
    }

    public int size() {
        return values.size();
    }
}
