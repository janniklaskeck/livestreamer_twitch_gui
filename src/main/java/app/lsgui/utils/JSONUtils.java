package app.lsgui.utils;

import com.google.gson.JsonElement;

public class JSONUtils {
    private JSONUtils() {

    }

    public static int getIntSafe(final JsonElement element, final int defaultValue) {
        if (element != null && !element.isJsonNull()) {
            return element.getAsInt();
        }
        return defaultValue;
    }

    public static boolean getBooleanSafe(final JsonElement element, final boolean defaultValue) {
        if (element != null && !element.isJsonNull()) {
            return element.getAsBoolean();
        }
        return defaultValue;
    }

    public static String getStringSafe(final JsonElement element, final String defaultValue) {
        if (element != null && !element.isJsonNull()) {
            return element.getAsString();
        }
        return defaultValue;
    }
}
