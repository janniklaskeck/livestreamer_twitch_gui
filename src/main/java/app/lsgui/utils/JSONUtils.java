package app.lsgui.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
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

    public static String getStringIfNotNull(final String name, final JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsString();
        }
        return "";
    }

    public static boolean getBooleanIfNotNull(final String name, final JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsBoolean();
        }
        return false;
    }

    public static int getIntegerIfNotNull(final String name, final JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsInt();
        }
        return 0;
    }
}
