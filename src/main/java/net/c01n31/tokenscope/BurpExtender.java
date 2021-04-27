package net.c01n31.tokenscope;

import java.awt.Component;
import java.io.UnsupportedEncodingException;
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
import burp.IResponseInfo;
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

    private String text(byte[] b) {
        try {
            return new String(b, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new String(b);
        }
    }

    public void processHttpMessage(int toolFlag, boolean messageIsRequest,
                                   IHttpRequestResponse message) {
        if (messageIsRequest) {
            return;
        }
        IResponseInfo info = helpers.analyzeResponse(message.getResponse());
        String host = message.getHttpService().getHost();
        List<String> headers = info.getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            String h = headers.get(i);
            if (!h.toLowerCase().startsWith("set-cookie:")) {
                continue;
            }
            String value = h.substring(h.indexOf(":") + 1).trim();
            String pair = value.split(";")[0];
            String name = pair.split("=")[0];
            String token = pair.substring(pair.indexOf("=") + 1);

            if (seen.containsKey(token)) {
                seen.put(token, seen.get(token) + 1);
                model.bump(token);
                continue;
            }
            seen.put(token, 1);
            model.add(host, name, token);

            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                callbacks.printOutput(text(Base64Hack.decode(parts[1])));
            }
        }
    }
}
