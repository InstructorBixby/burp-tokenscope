package net.c01n31.tokenscope;

import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;

public class BurpExtender implements IBurpExtender {

    private IBurpExtenderCallbacks callbacks;

    public void registerExtenderCallbacks(IBurpExtenderCallbacks cb) {
        this.callbacks = cb;
        cb.setExtensionName("tokenscope");
        cb.printOutput("tokenscope loaded");
        System.out.println("tokenscope loaded");
    }
}
