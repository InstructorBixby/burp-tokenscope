package net.c01n31.tokenscope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class CsvTest {

    @Test
    void plainFieldIsNotQuoted() {
        assertEquals("SESSION", Csv.escape("SESSION"));
    }

    @Test
    void commaForcesQuotes() {
        assertEquals("\"a,b\"", Csv.escape("a,b"));
    }

    @Test
    void quotesAreDoubled() {
        assertEquals("\"say \"\"hi\"\"\"", Csv.escape("say \"hi\""));
    }

    @Test
    void nullIsEmpty() {
        assertEquals("", Csv.escape(null));
    }

    @Test
    void rendersAHeaderAndARow() {
        String[] lines = Csv.render(Arrays.asList(
            new Token("example.com", "SESSION", "abc123", 1L)))
            .split("\n");
        assertEquals(2, lines.length);
        assertTrue(lines[0].startsWith("host,name,jwt"));
        assertTrue(lines[1].startsWith("example.com,SESSION,,"));
    }
}
