package net.c01n31.tokenscope;

import java.awt.Component;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import javax.swing.JScrollPane;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import burp.IMessageEditorTab;
import burp.ITextEditor;

/**
 * A "Tokens" tab inside the request/response editor, so a token can be
 * read in place instead of hunting for it in the suite tab.
 *
 * <p>isEnabled is called for every message Burp renders, including
 * while scrolling the proxy history, so it has to stay cheap: parse
 * headers only, never the body.
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
        return !found(content, isRequest).isEmpty();
    }

    private List<TokenParser.Found> found(byte[] content,
                                          boolean isRequest) {
        if (content == null || content.length == 0) {
            return Collections.emptyList();
        }
        try {
            List<String> headers = isRequest
                ? helpers.analyzeRequest(content).getHeaders()
                : helpers.analyzeResponse(content).getHeaders();
            return isRequest
                ? TokenParser.fromRequest(headers)
                : TokenParser.fromResponse(headers);
        } catch (RuntimeException e) {
            // helpers throw on a truncated message, and burp hands us
            // truncated messages while a response is still streaming
            return Collections.emptyList();
        }
    }

    public void setMessage(byte[] content, boolean isRequest) {
        current = content;
        List<TokenParser.Found> tokens = found(content, isRequest);
        if (tokens.isEmpty()) {
            editor.setText(new byte[0]);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (TokenParser.Found f : tokens) {
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
        editor.setText(sb.toString().getBytes(StandardCharsets.UTF_8));
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
