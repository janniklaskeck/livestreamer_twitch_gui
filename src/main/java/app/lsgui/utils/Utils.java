package app.lsgui.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import app.lsgui.model.channel.IChannel;
import app.lsgui.model.channel.twitch.TwitchChannel;
import app.lsgui.model.service.IService;
import app.lsgui.model.service.TwitchService;
import javafx.stage.Stage;

public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private Utils() {
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

    public static List<String> getAvailableQuality(final String url) {
        final List<String> qualities = new ArrayList<>();
        final JsonObject qualitiesJson = LivestreamerUtils.getQualityJsonFromLivestreamer(url);
        if (!qualitiesJson.toString().contains("error")) {
            final JsonObject jsonQualitiyList = qualitiesJson.get("streams").getAsJsonObject();
            jsonQualitiyList.entrySet().forEach(entry -> qualities.add(entry.getKey()));
            return sortQualities(qualities);
        }
        return qualities;
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

    public static void addStyleSheetToStage(final Stage stage, final String style) {
        if (stage != null && !stage.getScene().getStylesheets().contains(style)) {
            stage.getScene().getStylesheets().add(style);
        }
    }

    public static void clearStyleSheetsFromStage(final Stage stage) {
        if (stage != null) {
            stage.getScene().getStylesheets().clear();
        }
    }

    public static boolean isTwitchChannel(final IChannel channel) {
        return channel.getClass().equals(TwitchChannel.class);
    }

    public static boolean isTwitchService(final IService service) {
        return service.getClass().equals(TwitchService.class);
    }
}
