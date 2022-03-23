package net.c01n31.tokenscope;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class TokenParserTest {

    @Test
    void readsSetCookie() {
        List<TokenParser.Found> found = TokenParser.fromResponse(
            Arrays.asList("HTTP/1.1 200 OK",
                          "Set-Cookie: SESSION=abc123; Path=/; HttpOnly",
                          "Content-Type: text/html"));
        assertEquals(1, found.size());
        assertEquals("SESSION", found.get(0).name);
        assertEquals("abc123", found.get(0).value);
    }

    @Test
    void valueMayContainEquals() {
        assertEquals("YWJjPT09", TokenParser.fromResponse(
            Arrays.asList("Set-Cookie: s=YWJjPT09; Path=/"))
            .get(0).value);
    }

    @Test
    void readsBearer() {
        List<TokenParser.Found> found = TokenParser.fromRequest(
            Arrays.asList("GET / HTTP/1.1",
                          "Authorization: Bearer abc.def.ghi"));
        assertEquals(1, found.size());
        assertEquals("abc.def.ghi", found.get(0).value);
    }

    @Test
    void readsCustomHeaders() {
        assertEquals(1, TokenParser.fromRequest(
            Arrays.asList("X-Auth-Token: abc123")).size());
    }

    @Test
    void ignoresBasicAuth() {
        assertEquals(0, TokenParser.fromRequest(
            Arrays.asList("Authorization: Basic dXNlcjpwYXNz")).size());
    }
}
