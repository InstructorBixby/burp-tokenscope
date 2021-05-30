package net.c01n31.tokenscope;

import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import burp.IHttpListener;
import burp.IHttpRequestResponse;
import burp.ITab;

public class BurpExtender implements IBurpExtender, IHttpListener, ITab {

    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private Map<String, Integer> seen = new HashMap<String, Integer>();

    private TokenTable model;
    private JScrollPane scroll;

    public void registerExtenderCallbacks(IBurpExtenderCallbacks cb) {
        this.callbacks = cb;
        this.helpers = cb.getHelpers();
        cb.setExtensionName("tokenscope");

        model = new TokenTable();
        scroll = new JScrollPane(new JTable(model));

        cb.registerHttpListener(this);
        cb.addSuiteTab(this);
        cb.printOutput("tokenscope loaded");
    }

    public String getTabCaption() {
        return "tokenscope";
    }

    public Component getUiComponent() {
        return scroll;
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

        for (TokenParser.Found f : found) {
            if (seen.containsKey(f.value)) {
                seen.put(f.value, seen.get(f.value) + 1);
                model.bump(f.value);
                continue;
            }
            seen.put(f.value, 1);
            model.add(host, f.name, f.value);

            if (Jwt.looksLikeJwt(f.value)) {
                Jwt jwt = Jwt.parse(f.value);
                callbacks.printOutput(jwt.getHeader());
                callbacks.printOutput(jwt.getPayload());
            }
        }
    }
}
