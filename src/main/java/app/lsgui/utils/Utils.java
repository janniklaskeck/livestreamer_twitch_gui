package app.lsgui.utils;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import app.lsgui.service.twitch.TwitchStreamData;

public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
    private static final JsonParser PARSER = new JsonParser();

    public static void startLivestreamer(String serviceURL, String name, String quality) {
        LOGGER.info("Starting Stream {} on {} with Quality {}", name, serviceURL, quality);
    }

    public static void recordLivestreamer(String URL, String quality) {
        LOGGER.info("Record Stream {} with Quality {}", URL, quality);

    }

    public static void openURLInBrowser(final String URL) {
        LOGGER.info("Open Browser URL {}", URL);
        try {
            URI uri = new URI(URL);
            Desktop.getDesktop().browse(uri);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void loadImageFromURLAsync(final TwitchStreamData data) {
        ExecutorServiceSingleton.instance().submit(new StreamImageUpdateCallable(data));
    }

    public static String getStringIfNotNull(final String name, JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsString();
        }
        return "";
    }

    public static Boolean getBooleanIfNotNull(final String name, JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsBoolean();
        }
        return false;
    }

    public static Integer getIntegerIfNotNull(final String name, JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsInt();
        }
        return 0;
    }

    public static List<String> getAvailableQuality(final String URL, final String channel) {
        /*final List<String> qualities = new ArrayList<String>();

        final String livestreamerExec = "livestreamer";

        try {
            final Process process = new ProcessBuilder(livestreamerExec, "-j", URL + channel).redirectErrorStream(true)
                    .start();

            final JsonObject jsonQualities = PARSER
                    .parse(new JsonReader(new BufferedReader(new InputStreamReader(process.getInputStream()))))
                    .getAsJsonObject();

            process.waitFor();
            if (!jsonQualities.toString().contains("error")) {
                final JsonObject jsonQualitiyList = jsonQualities.get("streams").getAsJsonObject();

                jsonQualitiyList.entrySet().forEach(entry -> qualities.add(entry.getKey()));
                return qualities;
            }
        } catch (final IOException | InterruptedException e) {
            LOGGER.error(
                    "failed to retrieve stream qualites for " + URL + channel + "," + " reason: " + e.getMessage());
        }*/
        return Arrays.asList("best", "worst");
    }

}
