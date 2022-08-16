package net.c01n31.tokenscope;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import burp.IMessageEditorTab;
import burp.ITextEditor;

/**
 * A "Tokens" tab inside the request/response editor, so a token can be
 * read in place instead of hunting for it in the suite tab.
 */
public final class TokenEditorTab implements IMessageEditorTab {

    private final IExtensionHelpers helpers;
    private final ITextEditor editor;
    private byte[] current;

    public TokenEditorTab(IBurpExtenderCallbacks callbacks) {
        this.helpers = callbacks.getHelpers();
        this.editor = callbacks.createTextEditor();
        this.editor.setEditable(false);
    }

    public String getTabCaption() {
        return "Tokens";
    }

    public Component getUiComponent() {
        return new JScrollPane(editor.getComponent());
    }

    public boolean isEnabled(byte[] content, boolean isRequest) {
        return content != null && !found(content, isRequest).isEmpty();
    }

    private List<TokenParser.Found> found(byte[] content,
                                          boolean isRequest) {
        if (content == null) {
            return new ArrayList<TokenParser.Found>();
        }
        List<String> headers = isRequest
            ? helpers.analyzeRequest(content).getHeaders()
            : helpers.analyzeResponse(content).getHeaders();
        return isRequest
            ? TokenParser.fromRequest(headers)
            : TokenParser.fromResponse(headers);
    }

    public void setMessage(byte[] content, boolean isRequest) {
        current = content;
        if (content == null) {
            editor.setText(new byte[0]);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (TokenParser.Found f : found(content, isRequest)) {
            sb.append(f.name).append("\n  ").append(f.value).append("\n");
            Jwt jwt = Jwt.parse(f.value);
            if (jwt != null) {
                sb.append("  header:  ").append(jwt.header().asMap())
                  .append("\n");
                sb.append("  payload: ").append(jwt.payload().asMap())
                  .append("\n");
                sb.append("  (signature not checked)\n");
            }
            sb.append("\n");
        }
        editor.setText(sb.toString().getBytes());
    }

    public byte[] getMessage() {
        return current;
    }

    public boolean isModified() {
        return false;
    }

    public byte[] getSelectedData() {
        return editor.getSelectedText();
    }
}
