package net.c01n31.tokenscope;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * A JWT as observed on the wire.
 *
 * <p>This reads the header and payload and nothing else. It does
 * <b>not</b> verify the signature, and it never will: the point is to
 * see what an application is handing out, not to decide whether the
 * token is genuine.
 */
public final class Jwt {

    private final Claims header;
    private final Claims payload;

    private Jwt(Claims header, Claims payload) {
        this.header = header;
        this.payload = payload;
    }

    /** Three dot-separated segments; cheap enough to call per header. */
    public static boolean looksLikeJwt(String token) {
        return token != null && token.split("\\.", -1).length == 3;
    }

    /** Returns null when the token is not three segments. */
    public static Jwt parse(String token) {
        if (!looksLikeJwt(token)) {
            return null;
        }
        String[] parts = token.split("\\.", -1);
        return new Jwt(Claims.parse(decode(parts[0])),
                       Claims.parse(decode(parts[1])));
    }

    /**
     * base64url, unpadded. The standard decoder rejects the url-safe
     * alphabet and the missing padding, which is most real tokens.
     */
    static String decode(String part) {
        String b64 = part;
        int pad = b64.length() % 4;
        if (pad == 2) {
            b64 = b64 + "==";
        } else if (pad == 3) {
            b64 = b64 + "=";
        } else if (pad == 1) {
            return "";
        }
        try {
            return new String(Base64.getUrlDecoder().decode(b64),
                              StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    public Claims header() {
        return header;
    }

    public Claims payload() {
        return payload;
    }

    public String getAlg() {
        return header.get("alg").orElse("");
    }

    public Optional<String> subject() {
        return payload.get("sub");
    }

    public Optional<Long> exp() {
        return payload.getLong("exp");
    }

    public Optional<Long> issuedAt() {
        return payload.getLong("iat");
    }

    /** Seconds between iat and exp, when the token carries both. */
    public Optional<Long> lifetimeSeconds() {
        if (!exp().isPresent() || !issuedAt().isPresent()) {
            return Optional.empty();
        }
        return Optional.of(exp().get() - issuedAt().get());
    }
}
