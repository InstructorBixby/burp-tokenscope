package net.c01n31.tokenscope;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class TokenTableModel extends AbstractTableModel {

    private static final String[] COLUMNS =
        {"host", "name", "jwt", "bits/char", "len", "seen", "value"};

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

    public String getColumnName(int col) {
        return COLUMNS[col];
    }

    public Token getToken(int row) {
        return rows.get(row);
    }

    public Object getValueAt(int row, int col) {
        Token t = rows.get(row);
        switch (col) {
            case 0: return t.getHost();
            case 1: return t.getName();
            case 2: return t.isJwt() ? "yes" : "";
            case 3: return String.format("%.2f", t.entropy());
            case 4: return t.getValue().length();
            case 5: return t.getCount();
            default: return t.getValue();
        }
    }

    public void refresh() {
        rows = store.all();
        fireTableDataChanged();
    }
}
