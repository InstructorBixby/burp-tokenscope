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

            if (seen.containsKey(token)) {
                seen.put(token, seen.get(token) + 1);
                continue;
            }
            seen.put(token, 1);
            callbacks.printOutput("new token: " + name + " = " + token);

            // header.payload.signature -- we never touch the signature,
            // this does NOT verify anything, it just reads the claims
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                callbacks.printOutput("  header:  "
                    + new String(Base64Hack.decode(parts[0])));
                callbacks.printOutput("  payload: "
                    + new String(Base64Hack.decode(parts[1])));
            }
        }
    }
}
