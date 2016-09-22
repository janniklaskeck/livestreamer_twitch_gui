/**
 * MIT License
 *
 * Copyright (c) 2016 Jan-Niklas Keck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package app.lsgui.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
public final class LivestreamerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LivestreamerUtils.class);
    private static final String LIVESTREAMERCMD = "livestreamer";
    private static final JsonParser PARSER = new JsonParser();

    private LivestreamerUtils() {
    }

    public static JsonObject getQualityJsonFromLivestreamer(final String url) {
        LOGGER.trace("Get available quality options for {}", url);
        JsonObject jsonQualities = new JsonObject();
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(getQualityCommand(url));
            processBuilder.redirectError(Redirect.INHERIT);
            final Process process = processBuilder.start();
            final InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(),
                    StandardCharsets.UTF_8);
            final JsonReader jsonReader = new JsonReader(inputStreamReader);
            jsonQualities = PARSER.parse(jsonReader).getAsJsonObject();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("failed to retrieve stream qualites for " + url + "," + " reason: " + e.getMessage(), e);
        }
        LOGGER.trace("Return {}", jsonQualities);
        return jsonQualities;
    }

    private static List<String> getQualityCommand(final String url) {
        final List<String> command = new ArrayList<>();
        command.add(getLivestreamerExe());
        command.add(url);
        command.add("-j");
        if (url.toLowerCase(Locale.ENGLISH).contains("twitch")) {
            command.add("--twitch-oauth-token");
            command.add(getTwitchOAuth());
        }
        return command;
    }

    public static void startLivestreamer(final String url, final String quality) {
        LOGGER.info("Starting Stream {} with Quality {}", url, quality);
        final Thread t = new Thread(() -> {
            try {
                final ProcessBuilder processBuilder = new ProcessBuilder(getRunCommand(url, quality));
                processBuilder.redirectOutput(Redirect.INHERIT);
                processBuilder.redirectError(Redirect.INHERIT);
                final Process process = processBuilder.start();
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                LOGGER.error("ERROR while running livestreamer", e);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private static List<String> getRunCommand(final String url, final String quality) {
        final List<String> command = new ArrayList<>();
        command.add(getLivestreamerExe());
        command.add(url);
        command.add(quality);
        if (url.toLowerCase(Locale.ENGLISH).contains("twitch")) {
            command.add("--twitch-oauth-token");
            command.add(getTwitchOAuth());
        }
        return command;
    }

    private static String getTwitchOAuth() {
        final String oauth = Settings.getInstance().getTwitchOAuth();
        String parameter;
        if (oauth.startsWith("oauth")) {
            final String oauthKey = oauth.split(":")[1];
            parameter = oauthKey;
        } else {
            parameter = oauth;
        }
        if ("".equals(parameter)) {
            parameter = "ERROR";
        }
        return parameter;
    }

    public static void recordLivestreamer(final String url, final String quality, final File filePath) {
        LOGGER.info("Record Stream {} with Quality {} to file {}", url, quality, filePath);
        Thread t = new Thread(() -> {
            try {
                String path = "\"" + filePath.getAbsolutePath() + "\"";
                path = path.replace('\\', '/');
                Settings.getInstance().setRecordingPath(path);
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
        if ("".equals(Settings.getInstance().getLivestreamerExePath())) {
            if (!checkForLivestreamerOnPath()) {
                Platform.runLater(LivestreamerUtils::showLivestreamerPathWarning);
                return "";
            }
            return LIVESTREAMERCMD;
        } else {
            return Settings.getInstance().getLivestreamerExePath();
        }
    }

    private static void showLivestreamerPathWarning() {
        Notifications.create().title("Livestreamer GUI Warning").text("Check for livestreamer on path").darkStyle()
                .showWarning();
    }

    private static boolean checkForLivestreamerOnPath() {
        Map<String, String> env = System.getenv();
        final String windowsPath = env.get("Path");
        if (windowsPath.toLowerCase(Locale.ENGLISH).contains(LIVESTREAMERCMD)) {
            return true;
        }
        return false;
    }

}
