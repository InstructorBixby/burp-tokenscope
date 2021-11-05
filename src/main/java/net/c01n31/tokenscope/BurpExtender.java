package net.c01n31.tokenscope;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import burp.IHttpListener;
import burp.IHttpRequestResponse;
import burp.IRequestInfo;
import burp.IScanIssue;
import burp.IScannerCheck;
import burp.IScannerInsertionPoint;

public class BurpExtender implements IBurpExtender, IHttpListener,
        IScannerCheck {

    // intruder sends thousands of requests with the same cookie and
    // repeater is where i am poking at one token on purpose. proxy and
    // repeater are the two that tell me anything.
    private static final int TOOLS =
        IBurpExtenderCallbacks.TOOL_PROXY
        | IBurpExtenderCallbacks.TOOL_REPEATER
        | IBurpExtenderCallbacks.TOOL_TARGET;

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
        cb.registerScannerCheck(this);
        cb.addSuiteTab(tab);
        cb.printOutput("tokenscope loaded");
    }

    public void processHttpMessage(int toolFlag, boolean messageIsRequest,
                                   IHttpRequestResponse message) {
        if ((toolFlag & TOOLS) == 0) {
            return;
        }
        IRequestInfo request = helpers.analyzeRequest(message);
        if (!callbacks.isInScope(request.getUrl())) {
            return;
        }
        String host = message.getHttpService().getHost();
        List<TokenParser.Found> found;
        if (messageIsRequest) {
            found = TokenParser.fromRequest(request.getHeaders());
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

    private Issue issue(URL url, IHttpRequestResponse message,
                        PassiveChecks.Note note) {
        return new Issue(url, new IHttpRequestResponse[] {message},
            note.name, note.severity, "Certain", note.detail,
            "See the issue detail.");
    }

    public List<IScanIssue> doPassiveScan(IHttpRequestResponse message) {
        if (message.getResponse() == null) {
            return null;
        }
        URL url = helpers.analyzeRequest(message).getUrl();
        long now = System.currentTimeMillis() / 1000L;
        List<IScanIssue> issues = new ArrayList<IScanIssue>();
        List<String> headers =
            helpers.analyzeResponse(message.getResponse()).getHeaders();

        for (String h : headers) {
            if (!h.toLowerCase().startsWith("set-cookie:")) {
                continue;
            }
            String value = h.substring(h.indexOf(':') + 1).trim();
            for (PassiveChecks.Note note
                    : PassiveChecks.checkSetCookie(value)) {
                issues.add(issue(url, message, note));
            }
        }
        for (TokenParser.Found f : TokenParser.fromResponse(headers)) {
            if (!Jwt.looksLikeJwt(f.value)) {
                continue;
            }
            for (PassiveChecks.Note note
                    : PassiveChecks.checkJwt(f.value, now)) {
                issues.add(issue(url, message, note));
            }
        }
        return issues.isEmpty() ? null : issues;
    }

    public List<IScanIssue> doActiveScan(IHttpRequestResponse message,
                                         IScannerInsertionPoint point) {
        return null;
    }

    public int consolidateDuplicateIssues(IScanIssue existing,
                                          IScanIssue latest) {
        if (existing.getIssueName().equals(latest.getIssueName())
                && existing.getUrl().equals(latest.getUrl())) {
            return -1;
        }
        return 0;
    }
}
