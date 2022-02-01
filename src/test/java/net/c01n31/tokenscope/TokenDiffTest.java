package net.c01n31.tokenscope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class TokenDiffTest {

    private static final Claims A = Claims.parse(
        "{\"sub\":\"1\",\"iat\":100,\"role\":\"user\"}");
    private static final Claims B = Claims.parse(
        "{\"sub\":\"2\",\"iat\":200,\"role\":\"user\"}");

    @Test
    public void unionOfClaimsInOrder() {
        List<TokenDiff.Row> rows = TokenDiff.of(A, B);
        assertEquals(3, rows.size());
        assertEquals("sub", rows.get(0).claim);
    }

    @Test
    public void onlyChangedClaims() {
        List<TokenDiff.Row> rows = TokenDiff.changed(A, B);
        assertEquals(2, rows.size());
        assertEquals("sub", rows.get(0).claim);
        assertEquals("iat", rows.get(1).claim);
    }

    @Test
    public void sameValueIsNotAChange() {
        assertTrue(TokenDiff.of(A, B).get(2).same());
    }

    @Test
    public void absentOnOneSideIsAChange() {
        Claims withAdmin = Claims.parse("{\"sub\":\"1\",\"admin\":true}");
        List<TokenDiff.Row> rows = TokenDiff.changed(A, withAdmin);
        boolean sawAdmin = false;
        for (TokenDiff.Row row : rows) {
            if (row.claim.equals("admin")) {
                sawAdmin = true;
                assertEquals(null, row.left);
                assertEquals("true", row.right);
            }
        }
        assertTrue(sawAdmin);
    }

    @Test
    public void renderMarksTheChangedOnes() {
        String text = TokenDiff.render(TokenDiff.of(A, B));
        assertTrue(text.contains("! sub"));
        assertTrue(text.contains("  role"));
        assertFalse(text.contains("! role"));
    }
}
