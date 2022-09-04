package net.c01n31.tokenscope;

import burp.IBurpExtenderCallbacks;
import burp.IMessageEditorController;
import burp.IMessageEditorTab;
import burp.IMessageEditorTabFactory;

/** One tab instance per editor, as the api expects. */
public final class TokenEditorTabFactory implements IMessageEditorTabFactory {

    private final IBurpExtenderCallbacks callbacks;

    public TokenEditorTabFactory(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public IMessageEditorTab createNewInstance(
            IMessageEditorController controller, boolean editable) {
        return new TokenEditorTab(callbacks);
    }
}
