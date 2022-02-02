package net.c01n31.tokenscope;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import burp.ITab;

/** The suite tab: table of tokens on top, detail or diff underneath. */
public final class TokenScopeTab implements ITab {

    private final TokenTableModel model;
    private final JTable table;
    private final JTextArea detail;
    private final JPanel panel;

    public TokenScopeTab(TokenStore store) {
        model = new TokenTableModel(store);
        table = new JTable(model);
        table.setSelectionMode(
            ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setAutoCreateRowSorter(true);

        detail = new JTextArea();
        detail.setEditable(false);

        table.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        showSelection();
                    }
                }
            });

        JButton diff = new JButton("diff selected");
        diff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDiff();
            }
        });
        JButton clear = new JButton("clear");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                store.clear();
                model.refresh();
                detail.setText("");
            }
        });

        JPanel buttons = new JPanel();
        buttons.add(diff);
        buttons.add(clear);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                          new JScrollPane(table),
                                          new JScrollPane(detail));
        split.setResizeWeight(0.6);

        panel = new JPanel(new BorderLayout());
        panel.add(buttons, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
    }

    private Token tokenAt(int viewRow) {
        return model.getToken(table.convertRowIndexToModel(viewRow));
    }

    private void showSelection() {
        int[] rows = table.getSelectedRows();
        if (rows.length != 1) {
            return;
        }
        Token t = tokenAt(rows[0]);
        StringBuilder sb = new StringBuilder();
        sb.append(t.name()).append(" on ").append(t.host()).append("\n");
        sb.append("length ").append(t.value().length());
        sb.append(", ").append(String.format("%.2f", t.entropy()));
        sb.append(" bits/char, seen ").append(t.count()).append("\n\n");
        sb.append(t.value()).append("\n");
        if (t.jwt().isPresent()) {
            Jwt jwt = t.jwt().get();
            sb.append("\nheader:  ").append(jwt.header().asMap());
            sb.append("\npayload: ").append(jwt.payload().asMap());
            sb.append("\n\n(signature not checked, ever)\n");
        }
        set(sb.toString());
    }

    private void showDiff() {
        int[] rows = table.getSelectedRows();
        if (rows.length != 2) {
            set("select exactly two rows to diff\n");
            return;
        }
        Token left = tokenAt(rows[0]);
        Token right = tokenAt(rows[1]);
        if (!left.jwt().isPresent() || !right.jwt().isPresent()) {
            set("both tokens have to be jwts to diff claims\n");
            return;
        }
        set(TokenDiff.render(TokenDiff.of(left.jwt().get().payload(),
                                          right.jwt().get().payload())));
    }

    private void set(String text) {
        detail.setText(text);
        detail.setCaretPosition(0);
    }

    /** Safe to call from Burp's threads. */
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
        return panel;
    }
}
