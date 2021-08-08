package net.c01n31.tokenscope;

import java.net.URL;

import burp.IHttpRequestResponse;
import burp.IHttpService;
import burp.IScanIssue;

public class Issue implements IScanIssue {

    private final URL url;
    private final IHttpRequestResponse[] messages;
    private final String name;
    private final String severity;
    private final String confidence;
    private final String detail;
    private final String remediation;

    public Issue(URL url, IHttpRequestResponse[] messages, String name,
                 String severity, String confidence, String detail,
                 String remediation) {
        this.url = url;
        this.messages = messages;
        this.name = name;
        this.severity = severity;
        this.confidence = confidence;
        this.detail = detail;
        this.remediation = remediation;
    }

    public URL getUrl() {
        return url;
    }

    public String getIssueName() {
        return name;
    }

    public int getIssueType() {
        return 0x08000000;
    }

    public String getSeverity() {
        return severity;
    }

    public String getConfidence() {
        return confidence;
    }

    public String getIssueBackground() {
        return null;
    }

    public String getRemediationBackground() {
        return null;
    }

    public String getIssueDetail() {
        return detail;
    }

    public String getRemediationDetail() {
        return remediation;
    }

    public IHttpRequestResponse[] getHttpMessages() {
        return messages;
    }

    public IHttpService getHttpService() {
        return messages[0].getHttpService();
    }
}
