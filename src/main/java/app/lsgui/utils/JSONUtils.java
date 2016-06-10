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

    /**
     *
     * @param element
     * @param defaultValue
     * @return
     */
    public static int getIntSafe(final JsonElement element, final int defaultValue) {
	if (element != null && !element.isJsonNull()) {
	    return element.getAsInt();
	}
	return defaultValue;
    }

    /**
     *
     * @param element
     * @param defaultValue
     * @return
     */
    public static boolean getBooleanSafe(final JsonElement element, final boolean defaultValue) {
	if (element != null && !element.isJsonNull()) {
	    return element.getAsBoolean();
	}
	return defaultValue;
    }

    /**
     *
     * @param element
     * @param defaultValue
     * @return
     */
    public static String getStringSafe(final JsonElement element, final String defaultValue) {
	if (element != null && !element.isJsonNull()) {
	    return element.getAsString();
	}
	return defaultValue;
    }

    /**
     *
     * @param name
     * @param obj
     * @return
     */
    public static String getStringIfNotNull(final String name, final JsonObject obj) {
	if (obj.has(name) && !obj.get(name).isJsonNull()) {
	    return obj.get(name).getAsString();
	}
	return "";
    }

    /**
     *
     * @param name
     * @param obj
     * @return
     */
    public static Boolean getBooleanIfNotNull(final String name, final JsonObject obj) {
	if (obj.has(name) && !obj.get(name).isJsonNull()) {
	    return obj.get(name).getAsBoolean();
	}
	return false;
    }

    /**
     *
     * @param name
     * @param obj
     * @return
     */
    public static Integer getIntegerIfNotNull(final String name, final JsonObject obj) {
	if (obj.has(name) && !obj.get(name).isJsonNull()) {
	    return obj.get(name).getAsInt();
	}
	return 0;
    }
}
