package net.c01n31.tokenscope;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TokenParserTest {

    @Test
    public void readsSetCookie() {
        List<String> headers = Arrays.asList(
            "HTTP/1.1 200 OK",
            "Set-Cookie: SESSION=abc123; Path=/; HttpOnly",
            "Content-Type: text/html");
        List<TokenParser.Found> found = TokenParser.fromResponse(headers);
        assertEquals(1, found.size());
        assertEquals("SESSION", found.get(0).name);
        assertEquals("abc123", found.get(0).value);
    }

    @Test
    public void valueMayContainEquals() {
        List<String> headers = Arrays.asList(
            "Set-Cookie: s=YWJjPT09; Path=/");
        assertEquals("YWJjPT09",
                     TokenParser.fromResponse(headers).get(0).value);
    }

    @Test
    public void readsBearer() {
        List<String> headers = Arrays.asList(
            "GET / HTTP/1.1",
            "Authorization: Bearer abc.def.ghi");
        List<TokenParser.Found> found = TokenParser.fromRequest(headers);
        assertEquals(1, found.size());
        assertEquals("abc.def.ghi", found.get(0).value);
    }

    @Test
    public void ignoresBasicAuth() {
        List<String> headers = Arrays.asList(
            "Authorization: Basic dXNlcjpwYXNz");
        assertEquals(0, TokenParser.fromRequest(headers).size());
    }
}
