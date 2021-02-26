package net.c01n31.tokenscope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import burp.IHttpListener;
import burp.IHttpRequestResponse;
import burp.IResponseInfo;

public class BurpExtender implements IBurpExtender, IHttpListener {

    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    // token value -> how many times we have seen it
    private Map<String, Integer> seen = new HashMap<String, Integer>();

    public void registerExtenderCallbacks(IBurpExtenderCallbacks cb) {
        this.callbacks = cb;
        this.helpers = cb.getHelpers();
        cb.setExtensionName("tokenscope");
        cb.registerHttpListener(this);
        cb.printOutput("tokenscope loaded");
    }

    public void processHttpMessage(int toolFlag, boolean messageIsRequest,
                                   IHttpRequestResponse message) {
        if (messageIsRequest) {
            return;
        }
        IResponseInfo info = helpers.analyzeResponse(message.getResponse());
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

            Integer count = seen.get(token);
            if (count == null) {
                callbacks.printOutput("new token: " + name + " = " + token);
                seen.put(token, 1);
            } else {
                seen.put(token, count + 1);
            }
        }
    }
}
