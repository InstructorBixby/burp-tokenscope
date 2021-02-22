package net.c01n31.tokenscope;

import java.util.List;

import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import burp.IHttpListener;
import burp.IHttpRequestResponse;
import burp.IResponseInfo;

public class BurpExtender implements IBurpExtender, IHttpListener {

    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;

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
            if (h.toLowerCase().startsWith("set-cookie:")) {
                String value = h.substring(h.indexOf(":") + 1).trim();
                // name=value; Path=/; HttpOnly   -- first bit is the one
                String pair = value.split(";")[0];
                String name = pair.split("=")[0];
                String token = pair.substring(pair.indexOf("=") + 1);
                callbacks.printOutput(name + " = " + token);
            }
        }
    }
}
