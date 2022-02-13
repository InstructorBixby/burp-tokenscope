package net.c01n31.tokenscope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Every token seen this session, keyed by value.
 *
 * Synchronized throughout because Burp calls listeners from several
 * threads at once. Bounded because an app that issues a fresh CSRF
 * token per response will otherwise fill the heap; oldest entries go
 * first, which is the right end to lose.
 */
public final class TokenStore {

    public static final int DEFAULT_MAX = 5000;

    private final Map<String, Token> byValue =
        new LinkedHashMap<String, Token>();
    private final int max;

    public TokenStore() {
        this(DEFAULT_MAX);
    }

    public TokenStore(int max) {
        this.max = max;
    }

    public synchronized Token record(String host, String name,
                                     String value, long now) {
        Token existing = byValue.get(value);
        if (existing != null) {
            existing.seenAgain();
            return existing;
        }
        Token token = new Token(host, name, value, now);
        byValue.put(value, token);
        trim();
        return token;
    }

    private void trim() {
        Iterator<String> it = byValue.keySet().iterator();
        while (byValue.size() > max && it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    public synchronized Optional<Token> find(String value) {
        return Optional.ofNullable(byValue.get(value));
    }

    public synchronized List<Token> all() {
        return Collections.unmodifiableList(
            new ArrayList<Token>(byValue.values()));
    }

    public synchronized int size() {
        return byValue.size();
    }

    public synchronized void clear() {
        byValue.clear();
    }
}
