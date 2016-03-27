package app.lsgui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import app.lsgui.service.twitch.TwitchStreamData;

public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static void startLivestreamer(String URL, String quality) {
        LOGGER.info("Starting Stream {} with Quality {}", URL, quality);
    }

    public static void recordLivestreamer(String URL, String quality) {
        LOGGER.info("Record Stream {} with Quality {}", URL, quality);

    }

    public static void openURLInBrowser(String URL) {

    }

    public static void loadImageFromURLAsync(final TwitchStreamData data) {
        ExecutorServiceSingleton.instance().submit(new StreamImageUpdateCallable(data));
    }

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
