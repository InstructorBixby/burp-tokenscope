package net.c01n31.tokenscope;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * One observed token. The value is the identity: the same value seen on
 * two hosts is one Token with a count of two, which is exactly the case
 * worth noticing.
 */
public final class Token {

    private final String host;
    private final String name;
    private final String value;
    private final long firstSeen;
    private final AtomicInteger count = new AtomicInteger(1);

    public Token(String host, String name, String value, long firstSeen) {
        this.host = host;
        this.name = name;
        this.value = value;
        this.firstSeen = firstSeen;
    }

    public String host() {
        return host;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    public long firstSeen() {
        return firstSeen;
    }

    public int count() {
        return count.get();
    }

    public void seenAgain() {
        count.incrementAndGet();
    }

    public boolean isJwt() {
        return Jwt.looksLikeJwt(value);
    }

    public Optional<Jwt> jwt() {
        return Optional.ofNullable(Jwt.parse(value));
    }

    public double entropy() {
        return Entropy.perChar(value);
    }

    @Override
    public String toString() {
        return name + "@" + host + " (" + value.length() + " chars)";
    }
}
