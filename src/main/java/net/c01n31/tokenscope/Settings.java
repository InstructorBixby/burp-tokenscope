package net.c01n31.tokenscope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import burp.IBurpExtenderCallbacks;

/**
 * User settings, persisted through Burp so they survive a reload.
 *
 * saveExtensionSetting only stores strings, so the ignore list is a
 * comma separated line. That is fine: cookie names cannot contain a
 * comma.
 */
public final class Settings {

    private static final String KEY_IGNORE = "ignoreNames";
    private static final String KEY_DEBUG = "debug";

    private static final List<String> DEFAULT_IGNORE = Arrays.asList(
        "_ga", "_gid", "_gat", "_fbp", "__utma", "__utmb", "__utmz",
        "ai_user", "ai_session", "locale", "timezone");

    private final IBurpExtenderCallbacks callbacks;
    private final List<String> ignore = new ArrayList<String>();
    private boolean debug;

    public Settings(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        load();
    }

    private void load() {
        ignore.clear();
        String stored = callbacks.loadExtensionSetting(KEY_IGNORE);
        if (stored == null || stored.trim().isEmpty()) {
            ignore.addAll(DEFAULT_IGNORE);
        } else {
            for (String name : stored.split(",")) {
                String trimmed = name.trim();
                if (!trimmed.isEmpty()) {
                    ignore.add(trimmed);
                }
            }
        }
        debug = "true".equals(callbacks.loadExtensionSetting(KEY_DEBUG));
        Log.setDebug(debug);
    }

    public void save() {
        callbacks.saveExtensionSetting(KEY_IGNORE,
                                       String.join(",", ignore));
        callbacks.saveExtensionSetting(KEY_DEBUG,
                                       Boolean.toString(debug));
    }

    public List<String> ignoreNames() {
        return Collections.unmodifiableList(ignore);
    }

    public void setIgnoreNames(List<String> names) {
        ignore.clear();
        for (String name : names) {
            String trimmed = name.trim();
            if (!trimmed.isEmpty()) {
                ignore.add(trimmed);
            }
        }
        save();
    }

    public boolean isIgnored(String cookieName) {
        for (String name : ignore) {
            if (name.equalsIgnoreCase(cookieName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean on) {
        debug = on;
        Log.setDebug(on);
        save();
    }
}
