package net.c01n31.tokenscope;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class TokenTable extends AbstractTableModel {

    private static final String[] COLUMNS =
        {"host", "name", "value", "seen"};

    private List<String[]> rows = new ArrayList<String[]>();

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return COLUMNS.length;
    }

    public String getColumnName(int col) {
        return COLUMNS[col];
    }

    public Object getValueAt(int row, int col) {
        return rows.get(row)[col];
    }

    public void add(String host, String name, String value) {
        rows.add(new String[] {host, name, value, "1"});
        fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
    }

    public void bump(String value) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i)[2].equals(value)) {
                int n = Integer.parseInt(rows.get(i)[3]) + 1;
                rows.get(i)[3] = String.valueOf(n);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
    }
}
