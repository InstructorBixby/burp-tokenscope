package net.c01n31.tokenscope;

import java.io.PrintWriter;

import burp.IBurpExtenderCallbacks;

/**
 * Thin wrapper over the callbacks' output streams. System.out goes to
 * whatever terminal started Burp, which is nowhere when Burp was
 * started from a desktop icon.
 */
public final class Log {

    private static PrintWriter out;
    private static PrintWriter err;
    private static boolean debug;

    private Log() {
    }

    public static void init(IBurpExtenderCallbacks callbacks) {
        out = new PrintWriter(callbacks.getStdout(), true);
        err = new PrintWriter(callbacks.getStderr(), true);
    }

    public static void setDebug(boolean on) {
        debug = on;
    }

    public static void info(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public static void debug(String message) {
        if (debug) {
            info("[debug] " + message);
        }
    }

    public static void error(String message, Throwable t) {
        if (err == null) {
            return;
        }
        err.println(message);
        if (t != null) {
            t.printStackTrace(err);
        }
    }
}
