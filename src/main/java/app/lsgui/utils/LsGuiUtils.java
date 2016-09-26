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

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import app.lsgui.model.IChannel;
import app.lsgui.model.IService;
import app.lsgui.model.generic.GenericService;
import app.lsgui.remote.twitch.TwitchAPIClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public final class LsGuiUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LsGuiUtils.class);
    private static final double UPDATE_NOTIFICATION_DURATION = 10;

    private LsGuiUtils() {
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
        if (qualitiesJson.get("error") == null) {
            final JsonObject jsonQualitiyList = qualitiesJson.get("streams").getAsJsonObject();
            jsonQualitiyList.entrySet().forEach(entry -> qualities.add(entry.getKey()));
            return sortQualities(qualities);
        }
        return qualities;
    }

    private static List<String> sortQualities(final List<String> qualities) {
        final List<String> sortedQualities = new ArrayList<>();
        qualities.forEach(quality -> quality = quality.toLowerCase(Locale.ENGLISH));
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

    public static void addStyleSheetToStage(final Stage stage, final String style) {
        if (stage != null && !stage.getScene().getStylesheets().contains(style) && !"".equals(style)) {
            stage.getScene().getStylesheets().add(style);
        }
    }

    public static void clearStyleSheetsFromStage(final Stage stage) {
        if (stage != null) {
            stage.getScene().getStylesheets().clear();
        }
    }

    public static void addChannelToService(final String channel, final IService service) {
        if (TwitchUtils.isTwitchService(service) && !"".equals(channel)) {
            if (TwitchAPIClient.getInstance().channelExists(channel)) {
                service.addChannel(channel);
            }
        } else {
            service.addChannel(channel);
        }
    }

    public static void removeChannelFromService(final IChannel channel, final IService service) {
        service.removeChannel(channel);
    }

    public static void addService(final String serviceName, final String serviceUrl) {
        LOGGER.debug("Add new Service {} with URL {}", serviceName, serviceUrl);
        if (!"".equals(serviceName) && !"".equals(serviceUrl)) {
            String correctedUrl = correctUrl(serviceUrl);
            Settings.getInstance().getStreamServices().add(new GenericService(serviceName, correctedUrl));
        }
    }

    private static String correctUrl(final String url) {
        if (!url.endsWith("/")) {
            return url + "/";
        }
        return url;
    }

    public static String buildUrl(final String serviceUrl, final String channelUrl) {
        return serviceUrl + channelUrl;
    }

    public static void recordStream(final Stage stage, final IService service, final IChannel channel) {
        final String url = buildUrl(service.getUrl().get(), channel.getName().get());
        final String quality = Settings.getInstance().getQuality().get();

        final FileChooser recordFileChooser = new FileChooser();
        recordFileChooser.setTitle("Choose Target file");
        recordFileChooser.getExtensionFilters().add(new ExtensionFilter("MPEG4", ".mpeg4"));
        final File recordFile = recordFileChooser.showSaveDialog(stage);
        if (recordFile != null) {
            LivestreamerUtils.recordLivestreamer(url, quality, recordFile);
        }
    }

    public static void removeService(final IService service) {
        LOGGER.debug("Removing Service {}", service.getName().get());
        Settings.getInstance().getStreamServices().remove(service);
    }

    public static void showUpdateNotification(final String version, final ZonedDateTime date,
            final EventHandler<ActionEvent> action) {
        final String title = "Update available!";
        final String updateMessage = "Version " + version + " is available! Released at " + date
                + ". Click this or check Settings for a Link.";
        Notifications.create().title(title).text(updateMessage).onAction(action)
                .hideAfter(Duration.seconds(UPDATE_NOTIFICATION_DURATION)).darkStyle().showInformation();
    }

    public static void showWarningNotification(final String title, final String message) {
        Notifications.create().title(title).darkStyle().text(message).showWarning();
    }

    public static boolean isFileEmpty(final File file) {
        boolean result = true;
        BufferedReader br;

        try (final FileInputStream inputStream = new FileInputStream(file);) {
            br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String line = br.readLine();
            result = line == null;
            br.close();
        } catch (IOException e) {
            LOGGER.error("Could not read from Settings file.", e);
        }
        return result;
    }

    public static String readVersionProperty() {
        final InputStream propertyStream = LsGuiUtils.class.getClassLoader().getResourceAsStream("version.properties");
        final Properties versionProperty = new Properties();
        String version = "";
        try {
            versionProperty.load(propertyStream);
            version = versionProperty.getProperty("versionNumber");
            version = version.replaceAll("[\n\r]", "");
            LOGGER.debug("Read Version {} from version.properties", version);
        } catch (IOException e) {
            LOGGER.error("Could not load Properties from Inpustream!", e);
        }
        return version;
    }

}
