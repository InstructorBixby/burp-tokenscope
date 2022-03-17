package net.c01n31.tokenscope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ClaimsTest {

    @Test
    void readsStringsAndNumbers() {
        Claims c = Claims.parse(
            "{\"sub\":\"42\",\"exp\":1609459200,\"adm\":true}");
        assertEquals("42", c.get("sub").get());
        assertEquals(Long.valueOf(1609459200L), c.getLong("exp").get());
        assertEquals("true", c.get("adm").get());
    }

    @Test
    void keepsNestedObjectsAsText() {
        Claims c = Claims.parse(
            "{\"sub\":\"1\",\"realm\":{\"roles\":[\"a\",\"b\"]}}");
        assertEquals("{\"roles\":[\"a\",\"b\"]}", c.get("realm").get());
        assertEquals(2, c.size());
    }

    @Test
    void missingKeyIsEmpty() {
        assertFalse(Claims.parse("{\"sub\":\"1\"}").get("exp").isPresent());
    }

    @Test
    void emptyAndGarbageAreEmpty() {
        assertEquals(0, Claims.parse("").size());
        assertEquals(0, Claims.parse(null).size());
        assertTrue(Claims.parse("not json at all").asMap().isEmpty());
    }

    @Test
    void nonNumericExpIsEmptyNotAnException() {
        assertFalse(Claims.parse("{\"exp\":\"soon\"}").getLong("exp")
                    .isPresent());
    }
}
