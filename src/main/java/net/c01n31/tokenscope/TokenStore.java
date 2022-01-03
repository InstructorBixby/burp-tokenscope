package net.c01n31.tokenscope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Every token seen this session, keyed by value.
 *
 * Burp calls listeners from several threads at once, so this is
 * synchronized throughout. The first version was not, and the table
 * model threw ConcurrentModificationException about once a minute.
 */
public final class TokenStore {

    private final Map<String, Token> byValue =
        new LinkedHashMap<String, Token>();

    public synchronized Token record(String host, String name,
                                     String value, long now) {
        Token existing = byValue.get(value);
        if (existing != null) {
            existing.seenAgain();
            return existing;
        }
        Token token = new Token(host, name, value, now);
        byValue.put(value, token);
        return token;
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
