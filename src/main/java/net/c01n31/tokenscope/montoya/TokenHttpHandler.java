package net.c01n31.tokenscope.montoya;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpHandler;
import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.handler.RequestToBeSentAction;
import burp.api.montoya.http.handler.ResponseReceivedAction;
import burp.api.montoya.http.message.Cookie;
import burp.api.montoya.http.message.HttpHeader;

import net.c01n31.tokenscope.Jwt;
import net.c01n31.tokenscope.TokenStore;

/**
 * Collects tokens the Montoya way: cookies and headers come back as
 * objects, so there is no string splitting left to get wrong.
 *
 * <p>WIP. It fills a store that nothing displays yet.
 */
final class TokenHttpHandler implements HttpHandler {

    private final MontoyaApi api;
    private final TokenStore store = new TokenStore();

    TokenHttpHandler(MontoyaApi api) {
        this.api = api;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(
            HttpRequestToBeSent request) {
        long now = System.currentTimeMillis() / 1000L;
        for (HttpHeader header : request.headers()) {
            if (!header.name().equalsIgnoreCase("Authorization")) {
                continue;
            }
            String value = header.value();
            if (!value.regionMatches(true, 0, "Bearer ", 0, 7)) {
                continue;
            }
            String token = value.substring(7).trim();
            store.record(request.httpService().host(), "Authorization",
                         token, now);
            if (Jwt.looksLikeJwt(token)) {
                api.logging().logToOutput(
                    "bearer jwt, alg " + Jwt.parse(token).getAlg());
            }
        }
        return RequestToBeSentAction.continueWith(request);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(
            HttpResponseReceived response) {
        long now = System.currentTimeMillis() / 1000L;
        for (Cookie cookie : response.cookies()) {
            store.record(response.initiatingRequest().httpService().host(),
                         cookie.name(), cookie.value(), now);
        }
        return ResponseReceivedAction.continueWith(response);
    }

    int size() {
        return store.size();
    }
}
