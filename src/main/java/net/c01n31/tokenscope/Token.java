package net.c01n31.tokenscope;

// one observed token. value is the key we dedupe on.
public class Token {

    private final String host;
    private final String name;
    private final String value;
    private int count = 1;

    public Token(String host, String name, String value) {
        this.host = host;
        this.name = name;
        this.value = value;
    }

    public String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getCount() {
        return count;
    }

    public void seenAgain() {
        count++;
    }

    public boolean isJwt() {
        return Jwt.looksLikeJwt(value);
    }

    public double entropy() {
        return Entropy.perChar(value);
    }
}
