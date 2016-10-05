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

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import app.lsgui.gui.chat.ChatWindow;
import app.lsgui.model.IChannel;
import app.lsgui.model.IService;
import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.model.twitch.TwitchService;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.util.Duration;

public final class TwitchUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchUtils.class);
    private static final ZoneOffset OFFSET = ZoneOffset.ofHours(0);
    private static final String PREFIX = "GMT";
    private static final ZoneId GMT = ZoneId.ofOffset(PREFIX, OFFSET);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'").withZone(GMT);
    private static final String STREAM = "stream";
    private static final String CHANNEL_IS_OFFLINE = "Channel is offline";
    public static final String NO_QUALITIES = "Error fetching Quality Options!";
    public static final Image DEFAULT_LOGO = new Image(
            TwitchUtils.class.getClassLoader().getResource("default_channel.png").toExternalForm());

    private TwitchUtils() {
    }

    public static String getColorFromString(final String input) {
        final int maxRgbValue = 200;
        final int shiftTwoBytes = 16;
        final int shiftOneByte = 8;
        int r = 0;
        int g = 0;
        int b = 0;
        if (!"".equals(input)) {
            int hash = input.hashCode();
            r = (hash & 0xFF0000) >> shiftTwoBytes;
            if (r > maxRgbValue) {
                r = maxRgbValue;
            }
            g = (hash & 0x00FF00) >> shiftOneByte;
            if (g > maxRgbValue) {
                g = maxRgbValue;
            }
            b = hash & 0x0000FF;
            if (b > maxRgbValue) {
                b = maxRgbValue;
            }
        }
        return "rgb(" + r + "," + g + "," + b + ")";
    }

    public static boolean isTwitchChannel(final IChannel channel) {
        return channel instanceof TwitchChannel;
    }

    public static boolean isTwitchService(final IService service) {
        return service instanceof TwitchService;
    }

    public static boolean isChannelOnline(final IChannel channel) {
        if (channel != null) {
            if (TwitchUtils.isTwitchChannel(channel)) {
                return channel.isOnline().get();
            } else {
                return true;
            }
        }
        return false;
    }

    public static void addFollowedChannelsToService(final String username, final TwitchService service) {
        if (!"".equals(username)) {
            service.addFollowedChannels(username);
        }
    }

    public static void openTwitchChat(final IChannel channel) {
        if (TwitchUtils.isTwitchChannel(channel)) {
            final String channelName = channel.getName().get();
            final ChatWindow cw = new ChatWindow(channelName);
            cw.connect();
        }
    }

    public static void showOnlineNotification(final TwitchChannel channel) {
        final String nameString = channel.getDisplayName().get();
        final String gameString = channel.getGame().get();
        final String titleString = channel.getTitle().get();
        if (nameString != null && gameString != null && titleString != null) {
            final String title = "Channel Update";
            final String text = nameString + " just came online!\n The Game is " + gameString + ".\n" + titleString;
            Notifications.create().title(title).text(text).darkStyle().showInformation();
        }
    }

    public static void showReminderNotification(final TwitchChannel twitchChannel) {
        final String nameString = twitchChannel.getDisplayName().get();
        final String gameString = twitchChannel.getGame().get();
        final String titleString = twitchChannel.getTitle().get();
        if (nameString != null && gameString != null && titleString != null) {
            final String title = "Channel Online Reminder!";
            final String text = nameString + " just came online!\nThe Game is " + gameString + ".\n" + titleString;
            Notifications.create().title(title).text(text).darkStyle().hideAfter(Duration.INDEFINITE).showInformation();
        }
    }

    public static TwitchChannel constructTwitchChannel(final JsonObject data, final String name,
            final boolean isBrowser) {
        LOGGER.trace("Create TwitchChannel '{}'", name);
        final TwitchChannel channel = new TwitchChannel();
        channel.setBrowser(isBrowser);
        final JsonElement streamElement = data.get(STREAM);
        if (streamElement != null && !streamElement.isJsonNull()) {
            final JsonObject streamObject = streamElement.getAsJsonObject();
            if (isStreamObjectValid(streamObject)) {
                setData(channel, streamObject, name);
            }
        } else {
            setData(channel, new JsonObject(), name);
        }

        return channel;
    }

    private static boolean isStreamObjectValid(final JsonObject streamObject) {
        return streamObject != null && !streamObject.get("channel").isJsonNull()
                && !streamObject.get("preview").isJsonNull() && !streamObject.isJsonNull();
    }

    private static void setData(final TwitchChannel channel, final JsonObject channelObject, final String name) {
        if (!channelObject.equals(new JsonObject())) {
            setOnlineData(channel, channelObject);
        } else {
            setOfflineData(channel, name);
        }
    }

    private static void setOnlineData(final TwitchChannel channel, final JsonObject channelObject) {
        final JsonObject channelJson = channelObject.get("channel").getAsJsonObject();
        final JsonObject previewJson = channelObject.get("preview").getAsJsonObject();
        channel.getName().set(JsonUtils.getStringIfNotNull("name", channelJson));
        channel.displayNameProperty().set(JsonUtils.getStringIfNotNull("display_name", channelJson));
        channel.getLogoURL().set(JsonUtils.getStringIfNotNull("logo", channelJson));
        channel.isPartneredProperty().set(JsonUtils.getBooleanIfNotNull("partner", channelJson));
        channel.getPreviewUrlLarge().set(JsonUtils.getStringIfNotNull("large", previewJson));
        channel.getPreviewUrlMedium().set(JsonUtils.getStringIfNotNull("medium", previewJson));
        channel.getGame().set(JsonUtils.getStringIfNotNull("game", channelObject));
        channel.getTitle().set(JsonUtils.getStringIfNotNull("status", channelJson));
        final String createdAt = JsonUtils.getStringIfNotNull("created_at", channelObject);
        channel.getUptime().set(calculateUptime(createdAt));
        channel.getViewers().set(JsonUtils.getIntegerIfNotNull("viewers", channelObject));
        channel.isOnline().set(true);
        channel.getIsPlaylist().set(JsonUtils.getBooleanIfNotNull("is_playlist", channelObject));
        channel.getPreviewImageLarge().set(new Image(channel.getPreviewUrlLarge().get(), true));
        channel.getPreviewImageMedium().set(new Image(channel.getPreviewUrlMedium().get(), true));
        channel.getUptimeString().set(buildUptimeString(channel.getUptime().get()));
        channel.getViewersString().set(Integer.toString(channel.getViewers().get()));
        channel.getAvailableQualities().clear();
        if (!channel.isBrowser()) {
            final List<String> availableQualities;
            if (channel.isPartneredProperty().get()) {
                availableQualities = TwitchUtils.getStreamQualitiesForPartnered();
            } else {
                availableQualities = TwitchUtils.getStreamQualities();
            }
            channel.getAvailableQualities().addAll(availableQualities);
        }
    }

    public static void setOfflineData(final TwitchChannel channel, final String name) {
        channel.getName().set(name);
        channel.displayNameProperty().set(name);
        channel.getLogoURL().set("");
        channel.isPartneredProperty().set(false);
        channel.getPreviewUrlLarge().set("");
        channel.getGame().set("");
        channel.getTitle().set(CHANNEL_IS_OFFLINE);
        channel.getUptime().set(0);
        channel.getViewers().set(0);
        channel.isOnline().set(false);
        channel.getIsPlaylist().set(false);
        channel.getPreviewImageLarge().set(DEFAULT_LOGO);
        channel.getAvailableQualities().clear();
        channel.getAvailableQualities().add(CHANNEL_IS_OFFLINE);
    }

    private static long calculateUptime(final String createdAt) {
        final ZonedDateTime nowDate = ZonedDateTime.now(GMT);
        final ZonedDateTime startDate = ZonedDateTime.parse(createdAt, DTF);
        return startDate.until(nowDate, ChronoUnit.MILLIS);
    }

    public static String buildUptimeString(final Long uptime) {
        final long hours = TimeUnit.MILLISECONDS.toHours(uptime);
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(uptime));
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(uptime));
        return String.format("%02d:%02d:%02d Uptime", hours, minutes, seconds);
    }

    public static void addChannelToList(final ListProperty<TwitchChannel> activeList, final TwitchChannel channel) {
        synchronized (activeList) {
            final ObservableList<TwitchChannel> activeChannelServices = FXCollections
                    .observableArrayList(activeList.get());
            activeChannelServices.add(channel);
            activeList.set(activeChannelServices);
        }
    }

    public static List<String> getStreamQualitiesForPartnered() {
        final List<String> qualities = new ArrayList<>();
        qualities.add("Audio");
        qualities.add("Mobile");
        qualities.add("Low");
        qualities.add("Medium");
        qualities.add("High");
        qualities.add("Source");
        return qualities;
    }

    public static List<String> getStreamQualities() {
        final List<String> qualities = new ArrayList<>();
        qualities.add("Audio");
        qualities.add("Source");
        return qualities;
    }
}
