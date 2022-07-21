package net.c01n31.tokenscope;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import burp.ITab;

/** The suite tab: tokens table and detail, plus a settings pane. */
public final class TokenScopeTab implements ITab {

    private final TokenStore store;
    private final TokenTableModel model;
    private final JTable table;
    private final JTextArea detail;
    private final JTabbedPane tabs;
    private final JPanel tokensPanel;

    public TokenScopeTab(TokenStore store, Settings settings) {
        this.store = store;
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

        JPanel buttons = new JPanel();
        buttons.add(button("diff selected", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDiff();
            }
        }));
        buttons.add(button("export csv", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                export();
            }
        }));
        buttons.add(button("clear", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                store.clear();
                model.refresh();
                set("");
            }
        }));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                          new JScrollPane(table),
                                          new JScrollPane(detail));
        split.setResizeWeight(0.6);

        tokensPanel = new JPanel(new BorderLayout());
        tokensPanel.add(buttons, BorderLayout.NORTH);
        tokensPanel.add(split, BorderLayout.CENTER);

        tabs = new JTabbedPane();
        tabs.addTab("tokens", tokensPanel);
        tabs.addTab("settings", new SettingsPanel(settings));
    }

    private static JButton button(String label, ActionListener action) {
        JButton b = new JButton(label);
        b.addActionListener(action);
        return b;
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
        sb.append(" bits/byte, seen ").append(t.count()).append("\n\n");
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

    private void export() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("tokens.csv"));
        if (chooser.showSaveDialog(tabs) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Writer out = null;
        try {
            out = new FileWriter(chooser.getSelectedFile());
            Csv.write(out, store.all());
        } catch (IOException e) {
            Log.error("csv export failed", e);
            JOptionPane.showMessageDialog(tabs, e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                    // nothing useful to do here
                }
            }
        }
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
        return tabs;
    }
}
