package net.c01n31.tokenscope;

import java.io.UnsupportedEncodingException;
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

    // new String(bytes) uses whatever the platform default charset is,
    // which on my box is not utf-8, so every name with an accent in it
    // came out as garbage. say utf-8 explicitly.
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

            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                callbacks.printOutput("  header:  "
                    + text(Base64Hack.decode(parts[0])));
                callbacks.printOutput("  payload: "
                    + text(Base64Hack.decode(parts[1])));
            }
        }
    }
}
