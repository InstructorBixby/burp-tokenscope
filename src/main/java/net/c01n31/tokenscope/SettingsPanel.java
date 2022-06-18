package net.c01n31.tokenscope;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Ignore list and debug flag. Small enough to keep in one panel. */
public final class SettingsPanel extends JPanel {

    private final Settings settings;
    private final JTextArea names = new JTextArea(6, 28);
    private final JCheckBox debug = new JCheckBox("verbose logging");

    public SettingsPanel(Settings settings) {
        super(new BorderLayout(8, 8));
        this.settings = settings;
        setBorder(BorderFactory.createTitledBorder("settings"));

        names.setText(String.join("\n", settings.ignoreNames()));
        debug.setSelected(settings.isDebug());

        JButton apply = new JButton("apply");
        apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                apply();
            }
        });

        JPanel south = new JPanel();
        south.add(debug);
        south.add(apply);

        add(new JLabel("cookie names to ignore, one per line:"),
            BorderLayout.NORTH);
        add(new JScrollPane(names), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    private void apply() {
        settings.setIgnoreNames(Arrays.asList(names.getText().split("\n")));
        settings.setDebug(debug.isSelected());
        Log.info("settings saved, ignoring "
                 + settings.ignoreNames().size() + " names");
    }
}
