package util;

import com.google.gson.JsonObject;

public class JsonUtil {

    public static String getStringIfNotNull(final String name, JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsString();
        }
        return null;
    }

    public static Boolean getBooleanIfNotNull(final String name, JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsBoolean();
        }
        return null;
    }

    public static Integer getIntegerIfNotNull(final String name, JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsInt();
        }
        return null;
    }

}
