package net.c01n31.tokenscope;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// burp calls processHttpMessage from several threads at once, so this
// has to be synchronized. the first version was not and the table
// model threw ConcurrentModificationException about once a minute.
public class TokenStore {

    private final Map<String, Token> byValue =
        new LinkedHashMap<String, Token>();

    public synchronized Token record(String host, String name, String value) {
        Token existing = byValue.get(value);
        if (existing != null) {
            existing.seenAgain();
            return existing;
        }
        Token t = new Token(host, name, value);
        byValue.put(value, t);
        return t;
    }

    public synchronized boolean isNew(String value) {
        return !byValue.containsKey(value);
    }

    public synchronized List<Token> all() {
        return new ArrayList<Token>(byValue.values());
    }

    public synchronized int size() {
        return byValue.size();
    }

    public synchronized void clear() {
        byValue.clear();
    }
}
