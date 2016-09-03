package app.lsgui.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import app.lsgui.settings.Settings;
import javafx.application.Platform;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class LivestreamerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LivestreamerUtils.class);
    private static final String LIVESTREAMERCMD = "livestreamer";
    private static final JsonParser PARSER = new JsonParser();

    private LivestreamerUtils() {
    }

    /**
     *
     * @param url
     * @return
     */
    public static JsonObject getQualityJsonFromLivestreamer(final String url) {
        try {
            final String livestreamerExec = LIVESTREAMERCMD;
            final Process process = new ProcessBuilder(livestreamerExec, "-j", url).redirectErrorStream(true).start();
            final InputStreamReader isr = new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8);
            final JsonReader jr = new JsonReader(isr);
            JsonObject jsonQualities = PARSER.parse(jr).getAsJsonObject();
            process.waitFor();
            return jsonQualities;
        } catch (IOException | InterruptedException e) {
            LOGGER.error("failed to retrieve stream qualites for " + url + "," + " reason: " + e.getMessage(), e);
        }
        return new JsonObject();
    }

    /**
     *
     * @param url
     */
    public static void startLivestreamer(final String url) {
        final String quality = Settings.instance().getQuality();
        startLivestreamer(url, quality);
    }

    /**
     *
     * @param url
     * @param quality
     */
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

    /**
     *
     * @param url
     * @param quality
     * @param filePath
     */
    public static void recordLivestreamer(final String url, final String quality, final File filePath) {
        LOGGER.info("Record Stream {} with Quality {} to file {}", url, quality, filePath);
        Thread t = new Thread(() -> {
            try {
                String path = "\"" + filePath.getAbsolutePath() + "\"";
                path = path.replace('\\', '/');
                Settings.instance().setRecordingPath(path);
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
            if (!checkForLivestreamerOnPath()) {
                Platform.runLater(LivestreamerUtils::showLivestreamerPathWarning);
                return "";
            }
            return LIVESTREAMERCMD;
        } else {
            return Settings.instance().getLivestreamerExePath();
        }
    }

    private static void showLivestreamerPathWarning() {
        Notifications.create().title("Livestreamer GUI Warning").text("Check for livestreamer on path").darkStyle()
                .showWarning();
    }

    private static boolean checkForLivestreamerOnPath() {
        Map<String, String> env = System.getenv();
        final String windowsPath = env.get("Path");
        if (windowsPath.toLowerCase().contains(LIVESTREAMERCMD)) {
            return true;
        }
        return false;
    }

}
