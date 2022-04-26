package net.c01n31.tokenscope;

import java.util.List;

import javax.swing.table.AbstractTableModel;

/** Read-only view of the store. Only touched on the EDT. */
public final class TokenTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
        "host", "name", "jwt", "alg", "bits/byte", "len", "seen", "value",
    };

    private final TokenStore store;
    private List<Token> rows;

    public TokenTableModel(TokenStore store) {
        this.store = store;
        this.rows = store.all();
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }

    public Token getToken(int row) {
        return rows.get(row);
    }

    public Object getValueAt(int row, int col) {
        Token t = rows.get(row);
        switch (col) {
            case 0: return t.host();
            case 1: return t.name();
            case 2: return t.isJwt() ? "yes" : "";
            case 3: return t.jwt().map(Jwt::getAlg).orElse("");
            case 4: return String.format("%.2f", t.entropy());
            case 5: return t.value().length();
            case 6: return t.count();
            default: return t.value();
        }
    }

    public void refresh() {
        rows = store.all();
        fireTableDataChanged();
    }
}
