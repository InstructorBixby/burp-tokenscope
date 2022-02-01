package net.c01n31.tokenscope;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Claim-by-claim comparison of two JWTs.
 *
 * The question this answers in practice is "what actually changed when
 * I logged in as the other user" -- usually sub and iat, and
 * occasionally a role claim that was not supposed to be there.
 */
public final class TokenDiff {

    /** One claim, as seen in each of the two tokens. */
    public static final class Row {

        public final String claim;
        public final String left;
        public final String right;

        Row(String claim, String left, String right) {
            this.claim = claim;
            this.left = left;
            this.right = right;
        }

        public boolean same() {
            return left != null && left.equals(right);
        }
    }

    private TokenDiff() {
    }

    public static List<Row> of(Claims left, Claims right) {
        Set<String> keys = new LinkedHashSet<String>();
        keys.addAll(left.asMap().keySet());
        keys.addAll(right.asMap().keySet());

        Map<String, String> l = left.asMap();
        Map<String, String> r = right.asMap();
        List<Row> rows = new ArrayList<Row>();
        for (String key : keys) {
            rows.add(new Row(key, l.get(key), r.get(key)));
        }
        return rows;
    }

    public static List<Row> changed(Claims left, Claims right) {
        List<Row> rows = new ArrayList<Row>();
        for (Row row : of(left, right)) {
            if (!row.same()) {
                rows.add(row);
            }
        }
        return rows;
    }

    public static String render(List<Row> rows) {
        StringBuilder sb = new StringBuilder();
        for (Row row : rows) {
            sb.append(row.same() ? "  " : "! ");
            sb.append(row.claim).append("\n");
            sb.append("    left:  ")
              .append(row.left == null ? "(absent)" : row.left)
              .append("\n");
            sb.append("    right: ")
              .append(row.right == null ? "(absent)" : row.right)
              .append("\n");
        }
        return sb.toString();
    }
}
