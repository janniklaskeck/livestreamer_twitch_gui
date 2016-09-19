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
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import app.lsgui.gui.LsGUIWindow;
import app.lsgui.gui.chat.ChatWindow;
import app.lsgui.model.channel.IChannel;
import app.lsgui.model.service.GenericService;
import app.lsgui.model.service.IService;
import app.lsgui.model.service.TwitchService;
import app.lsgui.model.twitch.channel.TwitchChannel;
import app.lsgui.rest.twitch.TwitchAPIClient;
import app.lsgui.settings.Settings;
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
public class LsGuiUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LsGuiUtils.class);

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
        int r = 0;
        int g = 0;
        int b = 0;
        if (!"".equals(input)) {
            int hash = input.hashCode();
            r = (hash & 0xFF0000) >> 16;
            if (r > 200) {
                r = 200;
            }
            g = (hash & 0x00FF00) >> 8;
            if (g > 200) {
                g = 200;
            }
            b = hash & 0x0000FF;
            if (b > 200) {
                b = 200;
            }
        }
        return "rgb(" + r + "," + g + "," + b + ")";
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

    public static boolean isTwitchChannel(final IChannel channel) {
        return channel.getClass().equals(TwitchChannel.class);
    }

    public static boolean isTwitchService(final IService service) {
        return service.getClass().equals(TwitchService.class);
    }

    public static void addChannelToService(final String channel, final IService service) {
        if (isTwitchService(service) && !"".equals(channel)) {
            if (TwitchAPIClient.getInstance().channelExists(channel)) {
                service.addChannel(channel);
            }
        } else {
            service.addChannel(channel);
        }
    }

    public static void addFollowedChannelsToService(final String username, final TwitchService service) {
        if (!"".equals(username)) {
            service.addFollowedChannels(username);
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

    public static boolean isChannelOnline(final IChannel channel) {
        if (channel != null) {
            if (LsGuiUtils.isTwitchChannel(channel)) {
                return channel.isOnline().get();
            } else {
                return true;
            }
        }
        return false;
    }

    public static void recordStream(final IService service, final IChannel channel) {
        if (LsGuiUtils.isChannelOnline(channel)) {
            final String url = buildUrl(service.getUrl().get(), channel.getName().get());
            final String quality = Settings.getInstance().getQuality();

            final FileChooser recordFileChooser = new FileChooser();
            recordFileChooser.setTitle("Choose Target file");
            recordFileChooser.getExtensionFilters().add(new ExtensionFilter("MPEG4", ".mpeg4"));
            final File recordFile = recordFileChooser.showSaveDialog(LsGUIWindow.getRootStage());
            if (recordFile != null) {
                LivestreamerUtils.recordLivestreamer(url, quality, recordFile);
            }
        }
    }

    public static void openTwitchChat(final IChannel channel) {
        if (LsGuiUtils.isTwitchChannel(channel)) {
            final String channelName = channel.getName().get();
            ChatWindow cw = new ChatWindow(channelName);
            cw.connect();
        }
    }

    public static void removeService(final IService service) {
        LOGGER.debug("Removing Service {}", service.getName().get());
        Settings.getInstance().getStreamServices().remove(service);
    }

    public static void showOnlineNotification(final TwitchChannel channel) {
        final String nameString = channel.getName().get();
        final String gameString = channel.getGame().get();
        final String titleString = channel.getTitle().get();
        if (nameString != null && gameString != null && titleString != null) {
            final String title = "Channel Update";
            final String text = nameString + " just came online!\n The Game is " + gameString + ".\n" + titleString;
            Notifications.create().title(title).text(text).darkStyle().showInformation();
        }
    }

    public static void showReminderNotification(final TwitchChannel twitchChannel) {
        final String nameString = twitchChannel.getName().get();
        final String gameString = twitchChannel.getGame().get();
        final String titleString = twitchChannel.getTitle().get();
        if (nameString != null && gameString != null && titleString != null) {
            final String title = "Channel Online Reminder!";
            final String text = nameString + " just came online!\n The Game is " + gameString + ".\n" + titleString;
            Notifications.create().title(title).text(text).darkStyle().hideAfter(Duration.INDEFINITE).showInformation();
        }
    }

    public static void showUpdateNotification(final String version, final ZonedDateTime date,
            final EventHandler<ActionEvent> action) {
        final String title = "Update available!";
        final String updateMessage = "Version " + version + " is available! Released at " + date
                + ". Click this or check Settings for a Link.";
        Notifications.create().title(title).text(updateMessage).onAction(action).hideAfter(Duration.seconds(10))
                .darkStyle().showInformation();
    }

    public static boolean isFileEmpty(final File file) {
        boolean result = true;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            result = br.readLine() == null;
        } catch (IOException e) {
            LOGGER.error("Could not read from Settings file.", e);
        }
        return result;
    }

}
