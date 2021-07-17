package net.c01n31.tokenscope;

import java.util.List;

import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import burp.IHttpListener;
import burp.IHttpRequestResponse;

public class BurpExtender implements IBurpExtender, IHttpListener {

    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private final TokenStore store = new TokenStore();
    private TokenScopeTab tab;

    public void registerExtenderCallbacks(IBurpExtenderCallbacks cb) {
        this.callbacks = cb;
        this.helpers = cb.getHelpers();
        this.tab = new TokenScopeTab(store);

        cb.setExtensionName("tokenscope");
        cb.registerHttpListener(this);
        cb.addSuiteTab(tab);
        cb.printOutput("tokenscope loaded");
    }

    public void processHttpMessage(int toolFlag, boolean messageIsRequest,
                                   IHttpRequestResponse message) {
        String host = message.getHttpService().getHost();
        List<TokenParser.Found> found;
        if (messageIsRequest) {
            found = TokenParser.fromRequest(
                helpers.analyzeRequest(message).getHeaders());
        } else {
            found = TokenParser.fromResponse(
                helpers.analyzeResponse(message.getResponse()).getHeaders());
        }
        if (found.isEmpty()) {
            return;
        }
        for (TokenParser.Found f : found) {
            store.record(host, f.name, f.value);
        }
        tab.refresh();
    }
}
