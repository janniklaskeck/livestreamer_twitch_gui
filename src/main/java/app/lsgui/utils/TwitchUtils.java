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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import app.lsgui.model.IChannel;
import app.lsgui.model.IService;
import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.model.twitch.TwitchService;
import app.lsgui.remote.twitch.TwitchAPIClient;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.util.Duration;

public final class TwitchUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(TwitchUtils.class);
	private static final ZoneOffset OFFSET = ZoneOffset.ofHours(0);
	private static final String PREFIX = "GMT";
	private static final ZoneId GMT = ZoneId.ofOffset(PREFIX, OFFSET);
	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'").withZone(GMT);
	private static final String CHANNEL_IS_OFFLINE = "Channel is offline";
	public static final String NO_QUALITIES = "Error fetching Quality Options!";
	public static final Image DEFAULT_LOGO = new Image(
			TwitchUtils.class.getClassLoader().getResource("default_channel.png").toExternalForm());
	private static final String TWITCH_CHAT_TEMPLATE = "https://www.twitch.tv/%s/chat";

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

	public static void openTwitchChatInBrowser(final IChannel channel) {
		URI uri = null;
		try {
			uri = new URI(String.format(TWITCH_CHAT_TEMPLATE, channel.getName().get()));
		} catch (URISyntaxException e) {
			LOGGER.error("Could not convert url to uri", e);
		}
		final Desktop desktop = Desktop.getDesktop();
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (IOException e) {
				LOGGER.error("Could not open uri in browser", e);
			}
		}
	}

	public static void showOnlineNotification(final TwitchChannel channel) {
		final String nameString = channel.getDisplayName().get();
		final String gameString = channel.getGame().get();
		final String titleString = channel.getTitle().get();
		if (nameString != null && gameString != null && titleString != null) {
			final String title = "Channel Update";
			final String text = nameString + " just came online!\n The Game is " + gameString + ".\n" + titleString;
			Notifications.create().owner(Screen.getPrimary()).title(title).text(text).darkStyle().showInformation();
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

	public static void fetchChannelIds(TwitchService service) {
		StringBuilder builder = new StringBuilder();
		int channelCount = service.getChannelProperty().get().size();
		for (int i = 0; i < channelCount; i++) {
			TwitchChannel twitchChannel = (TwitchChannel) service.getChannelProperty().get().get(i);
			if (twitchChannel.getId().get() == 0L) {
				if (i > 0)
					builder.append("&");
				builder.append("login=");
				builder.append(twitchChannel.getName().get());
			}
		}
		String idsString = builder.toString();
		if (idsString.length() > 0) {
			Map<String, String> ids = TwitchAPIClient.getInstance().getTwitchUserIdsFromNames(idsString);
			for (int i = 0; i < channelCount; i++) {
				TwitchChannel twitchChannel = (TwitchChannel) service.getChannelProperty().get().get(i);

				twitchChannel.getId().set(Long.parseLong(ids.get(twitchChannel.getName().get().toLowerCase())));
			}
		}
	}

	public static TwitchChannel constructTwitchChannel(final String name) {
		LOGGER.trace("Create TwitchChannel '{}'", name);
		final TwitchChannel channel = new TwitchChannel();
		setOfflineData(channel, name);
		return channel;
	}

	public static void updateTwitchService(final TwitchService service, final JsonObject streamData,
			final JsonObject gameData) {
		LOGGER.trace("Update TwitchService");
		final JsonArray dataArray = streamData.get("data").getAsJsonArray();
		if (dataArray != null && !dataArray.isJsonNull() && dataArray.size() > 0) {
			for (int i = 0; i < dataArray.size(); i++) {
				final JsonObject streamObject = dataArray.get(i).getAsJsonObject();
				final long userId = Long.parseLong(streamObject.get("user_id").getAsString());
				Optional<IChannel> foundChannel = service.getChannelProperty().get().stream().filter((c) -> {
					TwitchChannel twitchChannel = (TwitchChannel) c;
					return twitchChannel.getId().get() == userId;
				}).findFirst();
				if (foundChannel.isPresent()) {
					TwitchChannel channel = (TwitchChannel) foundChannel.get();
					if (streamObject.get("type").getAsString().equals("live")) {
						setData(channel, streamObject, channel.getName().get());
						updateGameData(gameData, channel);
					} else {
						setData(channel, new JsonObject(), channel.getName().get());
					}
				}	
			}
		}
	}

	private static void updateGameData(JsonObject gameData, TwitchChannel channel) {
		if (gameData != null && !gameData.isJsonNull() && gameData.has("data")) {
			final JsonArray gameArray = gameData.get("data").getAsJsonArray();
			if (gameArray != null && !gameArray.isJsonNull() && gameArray.size() > 0) {
				for (int e = 0; e < gameArray.size(); e++) {
					final JsonObject gameDataObject = gameArray.get(e).getAsJsonObject();
					if (gameDataObject.get("id").getAsString().equals(channel.getGameId().get())) {
						setGameData(channel, gameDataObject);
					}
				}
			}
		}
	}

	private static void setData(final TwitchChannel channel, final JsonObject channelObject, final String name) {
		if (!channelObject.equals(new JsonObject())) {
			setOnlineData(channel, channelObject);
		} else {
			setOfflineData(channel, name);
		}
	}

	private static void setOnlineData(final TwitchChannel channel, final JsonObject channelObject) {
		String idString = JsonUtils.getStringIfNotNull("user_id", channelObject);
		channel.getId().set(Long.parseLong(idString));
		channel.getName().set(JsonUtils.getStringIfNotNull("user_name", channelObject));
		channel.displayNameProperty().set(JsonUtils.getStringIfNotNull("user_name", channelObject));
		channel.getPreviewUrl().set(JsonUtils.getStringIfNotNull("thumbnail_url", channelObject));
		channel.getGameId().set(JsonUtils.getStringIfNotNull("game_id", channelObject)); // todo game
		channel.getTitle().set(JsonUtils.getStringIfNotNull("title", channelObject));
		final String createdAt = JsonUtils.getStringIfNotNull("started_at", channelObject);
		channel.getUptime().set(calculateUptime(createdAt));
		channel.getViewers().set(JsonUtils.getIntegerIfNotNull("viewer_count", channelObject));
		channel.isOnline().set(true);
		channel.getIsPlaylist().set(false); // todo isplaylist
		String previewUrl = new String(channel.getPreviewUrl().get());
		previewUrl = previewUrl.replace("{width}", "1280").replace("{height}", "720");
		channel.getPreviewImageLarge().set(new Image(previewUrl, true));
		channel.getUptimeString().set(buildUptimeString(channel.getUptime().get()));
		channel.getViewersString().set(Integer.toString(channel.getViewers().get()));
		channel.getAvailableQualities().clear();
		if (!channel.isBrowser()) {
			channel.getAvailableQualities().addAll(TwitchUtils.getStreamQualitiesForPartnered());
		}
	}

	public static void setOfflineData(final TwitchChannel channel, final String name) {
		channel.getName().set(name);
		channel.displayNameProperty().set(name);
		channel.getLogoURL().set("");
		channel.isPartneredProperty().set(false);
		channel.getPreviewUrl().set("");
		channel.getGame().set("");
		channel.getGameId().set("");
		channel.getTitle().set(CHANNEL_IS_OFFLINE);
		channel.getUptime().set(0);
		channel.getViewers().set(0);
		channel.isOnline().set(false);
		channel.getIsPlaylist().set(false);
		channel.getPreviewImageLarge().set(DEFAULT_LOGO);
		channel.getAvailableQualities().clear();
		channel.getAvailableQualities().add(CHANNEL_IS_OFFLINE);
	}

	public static void setGameData(final TwitchChannel channel, final JsonObject data) {
		channel.getGame().set(JsonUtils.getStringIfNotNull("name", data));
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
		qualities.add("Best");
		return qualities;
	}

	public static List<String> getStreamQualities() {
		final List<String> qualities = new ArrayList<>();
		qualities.add("Audio");
		qualities.add("Best");
		return qualities;
	}
}
