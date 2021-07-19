package net.c01n31.tokenscope;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import burp.ITab;

public class TokenScopeTab implements ITab {

    private final TokenTableModel model;
    private final JTable table;
    private final JTextArea detail;
    private final JSplitPane split;

    public TokenScopeTab(TokenStore store) {
        model = new TokenTableModel(store);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        detail = new JTextArea();
        detail.setEditable(false);

        table.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    showSelected();
                }
            });

        split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                               new JScrollPane(table),
                               new JScrollPane(detail));
        split.setResizeWeight(0.6);
    }

    private void showSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            detail.setText("");
            return;
        }
        Token t = model.getToken(table.convertRowIndexToModel(row));
        StringBuilder sb = new StringBuilder();
        sb.append(t.getName()).append(" on ").append(t.getHost());
        sb.append("\nlength ").append(t.getValue().length());
        sb.append(", ").append(String.format("%.2f", t.entropy()));
        sb.append(" bits/char, seen ").append(t.getCount());
        sb.append("\n\n").append(t.getValue()).append("\n");
        if (t.isJwt()) {
            Jwt jwt = Jwt.parse(t.getValue());
            sb.append("\nheader:  ").append(jwt.getHeader());
            sb.append("\npayload: ").append(jwt.getPayload());
            sb.append("\n\n(signature not checked, ever)\n");
        }
        detail.setText(sb.toString());
        detail.setCaretPosition(0);
    }

    // processHttpMessage runs on burp's threads. touching a table
    // model from there corrupts the view -- rows go blank, or the
    // sorter throws IndexOutOfBounds. everything goes through the EDT.
    public void refresh() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.refresh();
            }
        });
    }

    public String getTabCaption() {
        return "tokenscope";
    }

    public Component getUiComponent() {
        return split;
    }
}
