package net.c01n31.tokenscope.montoya;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

/**
 * Montoya entry point. INCOMPLETE: it registers a name and a handler
 * and nothing else yet. The legacy {@code net.c01n31.tokenscope
 * .BurpExtender} is still the one to load.
 *
 * <p>Burp picks an entry point by what the jar declares, so having
 * both classes in the tree is harmless; only one gets used.
 */
public class TokenScopeMontoya implements BurpExtension {

    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("tokenscope (montoya, wip)");

        Logging log = api.logging();
        log.logToOutput("tokenscope montoya port loaded");
        log.logToOutput("this build only collects tokens, "
                        + "no ui and no scanner checks yet");

        api.http().registerHttpHandler(new TokenHttpHandler(api));
    }
}
