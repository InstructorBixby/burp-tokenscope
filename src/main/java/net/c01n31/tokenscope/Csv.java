package net.c01n31.tokenscope;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/** Writes the token table out for pasting into notes. */
public final class Csv {

    private static final String HEADER =
        "host,name,jwt,alg,bits_per_char,length,seen,value";

    private Csv() {
    }

    static String escape(String field) {
        if (field == null) {
            return "";
        }
        if (field.indexOf(',') < 0 && field.indexOf('"') < 0
                && field.indexOf('\n') < 0) {
            return field;
        }
        return '"' + field.replace("\"", "\"\"") + '"';
    }

    public static String render(List<Token> tokens) {
        StringBuilder sb = new StringBuilder(HEADER).append('\n');
        for (Token t : tokens) {
            sb.append(escape(t.host())).append(',');
            sb.append(escape(t.name())).append(',');
            sb.append(t.isJwt() ? "yes" : "").append(',');
            sb.append(escape(t.jwt().map(Jwt::getAlg).orElse("")))
              .append(',');
            sb.append(String.format("%.2f", t.entropy())).append(',');
            sb.append(t.value().length()).append(',');
            sb.append(t.count()).append(',');
            sb.append(escape(t.value())).append('\n');
        }
        return sb.toString();
    }

    public static void write(Writer out, List<Token> tokens)
            throws IOException {
        out.write(render(tokens));
        out.flush();
    }
}
