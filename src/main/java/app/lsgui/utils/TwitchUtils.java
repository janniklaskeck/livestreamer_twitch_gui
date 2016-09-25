package app.lsgui.utils;

import org.controlsfx.control.Notifications;

import com.google.gson.JsonObject;

import app.lsgui.gui.chat.ChatWindow;
import app.lsgui.model.IChannel;
import app.lsgui.model.IService;
import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.model.twitch.TwitchService;
import javafx.util.Duration;

public final class TwitchUtils {

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
        return channel.getClass().equals(TwitchChannel.class);
    }

    public static boolean isTwitchService(final IService service) {
        return service.getClass().equals(TwitchService.class);
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
            ChatWindow cw = new ChatWindow(channelName);
            cw.connect();
        }
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
            final String text = nameString + " just came online!\nThe Game is " + gameString + ".\n" + titleString;
            Notifications.create().title(title).text(text).darkStyle().hideAfter(Duration.INDEFINITE).showInformation();
        }
    }

    public static TwitchChannel constructTwitchChannel(final JsonObject data) {
        return null;

    }
}
