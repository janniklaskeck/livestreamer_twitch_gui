package app.lsgui.utils;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import app.lsgui.service.Settings;
import javafx.application.Platform;

public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
    private static final JsonParser PARSER = new JsonParser();
    private static final String LIVESTREAMERCMD = "livestreamer";

    private Utils() {
    }

    public static void startLivestreamer(final String url, final String quality) {
        LOGGER.info("Starting Stream {} with Quality {}", url, quality);
        Thread t = new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(Arrays.asList(getLivestreamerExe(), url, quality));
                pb.redirectOutput(Redirect.INHERIT);
                pb.redirectError(Redirect.INHERIT);
                Process prc = pb.start();
                prc.waitFor();
            } catch (IOException | InterruptedException e) {
                LOGGER.error("ERROR while running livestreamer", e);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public static void recordLivestreamer(final String url, final String quality, final File filePath) {
        LOGGER.info("Record Stream {} with Quality {} to file {}", url, quality, filePath);
        Thread t = new Thread(() -> {
            try {
                String path = "\"" + filePath.getAbsolutePath() + "\"";
                path = path.replace("\\", "/");
                LOGGER.debug(path);
                ProcessBuilder pb = new ProcessBuilder(Arrays.asList(getLivestreamerExe(), "-o", path, url, quality));
                pb.redirectOutput(Redirect.INHERIT);
                pb.redirectError(Redirect.INHERIT);
                Process prc = pb.start();
                prc.waitFor();
            } catch (IOException | InterruptedException e) {
                LOGGER.error("ERROR while recording", e);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private static String getLivestreamerExe() {
        if ("".equals(Settings.instance().getLivestreamerExePath())) {
            if (!checkForLivestreamerCMD()) {
                Platform.runLater(() -> Notifications.create().title("Livestreamer GUI Warning")
                        .text("Check for livestreamer on path").darkStyle().showInformation());
                return "";
            }
            return LIVESTREAMERCMD;
        } else {
            return Settings.instance().getLivestreamerExePath();
        }
    }

    private static boolean checkForLivestreamerCMD() {
        LOGGER.info("NOT IMPLEMENTED. Returning true");
        // TODO implement check if livestreamer is on PATH
        return true;
    }

    public static void openURLInBrowser(final String url) {
        LOGGER.info("Open Browser URL {}", url);
        try {
            URI uri = new URI(url);
            Desktop.getDesktop().browse(uri);
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("ERROR while opening URL in Browser", e);
        }
    }

    public static String getStringIfNotNull(final String name, final JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsString();
        }
        return "";
    }

    public static Boolean getBooleanIfNotNull(final String name, final JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsBoolean();
        }
        return false;
    }

    public static Integer getIntegerIfNotNull(final String name, final JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsInt();
        }
        return 0;
    }

    public static List<String> getAvailableQuality(final String url) {
        final List<String> qualities = new ArrayList<>();
        final JsonObject qualitiesJson = getQualityJsonFromLivestreamer(url);
        if (!qualitiesJson.toString().contains("error")) {
            final JsonObject jsonQualitiyList = qualitiesJson.get("streams").getAsJsonObject();
            jsonQualitiyList.entrySet().forEach(entry -> qualities.add(entry.getKey()));
            return sortQualities(qualities);
        }
        return qualities;
    }

    private static JsonObject getQualityJsonFromLivestreamer(final String url) {
        try {
            final String livestreamerExec = "livestreamer";
            final Process process = new ProcessBuilder(livestreamerExec, "-j", url).redirectErrorStream(true).start();
            JsonObject jsonQualities = PARSER
                    .parse(new JsonReader(new BufferedReader(new InputStreamReader(process.getInputStream()))))
                    .getAsJsonObject();
            process.waitFor();
            return jsonQualities;
        } catch (IOException | InterruptedException e) {
            LOGGER.error("failed to retrieve stream qualites for " + url + "," + " reason: " + e.getMessage(), e);
        }
        return new JsonObject();
    }

    private static List<String> sortQualities(final List<String> qualities) {
        List<String> sortedQualities = new ArrayList<>();
        qualities.forEach(s -> s = s.toLowerCase());
        if (qualities.contains("audio")) {
            sortedQualities.add("Audio");
        }
        if (qualities.contains("mobile")) {
            sortedQualities.add("Mobile");
        }
        if (qualities.contains("low")) {
            sortedQualities.add("Low");
        }
        if (qualities.contains("medium")) {
            sortedQualities.add("Medium");
        }
        if (qualities.contains("high")) {
            sortedQualities.add("High");
        }
        if (qualities.contains("source")) {
            sortedQualities.add("Source");
        }
        if (sortedQualities.isEmpty()) {
            sortedQualities.add("Worst");
            sortedQualities.add("Best");
        }
        return sortedQualities;
    }

    public static String getColorFromString(final String input) {
        int hash = input.hashCode();
        int r = (hash & 0xFF0000) >> 16;
        if (r > 200) {
            r = 200;
        }
        int g = (hash & 0x00FF00) >> 8;
        if (g > 200) {
            g = 200;
        }
        int b = hash & 0x0000FF;
        if (b > 200) {
            b = 200;
        }
        return "rgb(" + r + "," + g + "," + b + ")";
    }
}
