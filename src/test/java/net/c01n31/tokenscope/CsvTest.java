package net.c01n31.tokenscope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class CsvTest {

    @Test
    public void plainFieldIsNotQuoted() {
        assertEquals("SESSION", Csv.escape("SESSION"));
    }

    @Test
    public void commaForcesQuotes() {
        assertEquals("\"a,b\"", Csv.escape("a,b"));
    }

    @Test
    public void quotesAreDoubled() {
        assertEquals("\"say \"\"hi\"\"\"", Csv.escape("say \"hi\""));
    }

    @Test
    public void nullIsEmpty() {
        assertEquals("", Csv.escape(null));
    }

    @Test
    public void rendersAHeaderAndARow() {
        String csv = Csv.render(Arrays.asList(
            new Token("example.com", "SESSION", "abc123", 1L)));
        String[] lines = csv.split("\n");
        assertEquals(2, lines.length);
        assertTrue(lines[0].startsWith("host,name,jwt"));
        assertTrue(lines[1].startsWith("example.com,SESSION,,"));
    }
}
