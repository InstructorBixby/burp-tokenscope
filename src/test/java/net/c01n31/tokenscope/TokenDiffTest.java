package net.c01n31.tokenscope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class TokenDiffTest {

    private static final Claims A = Claims.parse(
        "{\"sub\":\"1\",\"iat\":100,\"role\":\"user\"}");
    private static final Claims B = Claims.parse(
        "{\"sub\":\"2\",\"iat\":200,\"role\":\"user\"}");

    @Test
    void unionOfClaimsInOrder() {
        List<TokenDiff.Row> rows = TokenDiff.of(A, B);
        assertEquals(3, rows.size());
        assertEquals("sub", rows.get(0).claim);
    }

    @Test
    void onlyChangedClaims() {
        List<TokenDiff.Row> rows = TokenDiff.changed(A, B);
        assertEquals(2, rows.size());
        assertEquals("sub", rows.get(0).claim);
        assertEquals("iat", rows.get(1).claim);
    }

    @Test
    void sameValueIsNotAChange() {
        assertTrue(TokenDiff.of(A, B).get(2).same());
    }

    @Test
    void absentOnOneSideIsAChange() {
        Claims withAdmin = Claims.parse("{\"sub\":\"1\",\"admin\":true}");
        for (TokenDiff.Row row : TokenDiff.changed(A, withAdmin)) {
            if (row.claim.equals("admin")) {
                assertNull(row.left);
                assertEquals("true", row.right);
                return;
            }
        }
        throw new AssertionError("admin claim was not reported");
    }

    @Test
    void renderMarksTheChangedOnes() {
        String text = TokenDiff.render(TokenDiff.of(A, B));
        assertTrue(text.contains("! sub"));
        assertTrue(text.contains("  role"));
        assertFalse(text.contains("! role"));
    }
}
