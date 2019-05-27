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
package app.lsgui.remote.twitch;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.model.twitch.TwitchService;
import app.lsgui.remote.HttpClientInterface;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public final class TwitchAPIClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitchAPIClient.class);
	private static final JsonParser JSONPARSER = new JsonParser();

	private static final String TWITCH_BASE_URL = "https://api.twitch.tv/helix/";
	private static final String LSGUI_CLIENT_ID = "rfpepzumaxd1iija3ip3fixao6z13pj";

	private static TwitchAPIClient instance;

	private TwitchAPIClient() {
		LOGGER.debug("TwitchProcessor constructed");
	}

	public static synchronized TwitchAPIClient getInstance() {
		if (instance == null) {
			instance = new TwitchAPIClient();
		}
		return instance;
	}

	public JsonObject getStreamData(final TwitchService service) {
		LOGGER.debug("Get Data for TwitchService");
		StringBuilder builder = new StringBuilder();

		int channelAmount = service.getChannelProperty().get().size();
		for (int i = 0; i < channelAmount; i++) {
			TwitchChannel twitchChannel = (TwitchChannel) service.getChannelProperty().get().get(i);
			if (twitchChannel.getId().get() != 0L) {
				if (i > 0)
					builder.append("&");
				builder.append("user_id=");
				builder.append(twitchChannel.getId().get());
			}
		}
		String idsString = builder.toString();
		if (idsString.length() > 0) {
			final URI uri = convertToURI(TWITCH_BASE_URL + "streams?" + idsString);
			try {
				return JSONPARSER.parse(getAPIResponse(uri)).getAsJsonObject();
			} catch (JsonSyntaxException e) {
				LOGGER.error("ERROR while loading channel data. Return empty data", e);
			}
		}
		return new JsonObject();
	}

	public JsonObject getGameInfo(TwitchService service) {
		LOGGER.debug("Get GameInfo for TwitchService");
		StringBuilder builder = new StringBuilder();

		int channelAmount = service.getChannelProperty().get().size();
		for (int i = 0; i < channelAmount; i++) {
			TwitchChannel twitchChannel = (TwitchChannel) service.getChannelProperty().get().get(i);
			if (twitchChannel.getId().get() != 0L && twitchChannel.getGameId().get().length() > 0) {
				if (i > 0)
					builder.append("&");
				builder.append("id=");
				builder.append(twitchChannel.getGameId().get());
			}
		}
		String idsString = builder.toString();
		if (idsString.length() > 0) {
			final URI uri = convertToURI(TWITCH_BASE_URL + "games?" + idsString);
			try {
				return JSONPARSER.parse(getAPIResponse(uri)).getAsJsonObject();
			} catch (JsonSyntaxException e) {
				LOGGER.error("ERROR while loading channel data. Return empty data", e);
			}
		}
		return new JsonObject();
	}

	private String getTwitchUserIdFromName(final String channelName) {
		LOGGER.trace("Request Twitch UserId for username {}", channelName);
		final URI uri = convertToURI(TWITCH_BASE_URL + "users?login=" + channelName);
		final JsonObject jsonData = JSONPARSER.parse(getAPIResponse(uri)).getAsJsonObject();
		final JsonArray data = jsonData.get("data").getAsJsonArray();
		for (final JsonElement element : data) {
			final JsonObject jsonObject = element.getAsJsonObject();
			String userId = jsonObject.get("id").getAsString();
			LOGGER.debug("Return Twitch User id {} for username {}", userId, channelName);
			return userId;
		}
		LOGGER.warn("User with Name '{}' not found!", channelName);
		return "";
	}

	public Map<String, String> getTwitchUserIdsFromNames(final String channelNames) {
		LOGGER.trace("Request Twitch UserId for usernames '{}'", channelNames);
		Map<String, String> userIds = new HashMap<>();
		final URI uri = convertToURI(TWITCH_BASE_URL + "users?" + channelNames);
		final JsonObject jsonData = JSONPARSER.parse(getAPIResponse(uri)).getAsJsonObject();
		final JsonArray data = jsonData.get("data").getAsJsonArray();
		for (final JsonElement element : data) {
			final JsonObject jsonObject = element.getAsJsonObject();
			userIds.put(jsonObject.get("login").getAsString(), jsonObject.get("id").getAsString());
		}
		return userIds;
	}

	public Set<String> getListOfFollowedStreams(final String userName) {
		final Set<String> followedStreams = new TreeSet<>();
		if (!"".equals(userName) && this.channelExists(userName)) {
			final String userId = getTwitchUserIdFromName(userName);
			final URI uri = convertToURI(TWITCH_BASE_URL + "users/" + userId + "/follows/channels?limit=100");
			JsonObject jo = JSONPARSER.parse(getAPIResponse(uri)).getAsJsonObject();
			JsonArray streams = jo.getAsJsonArray("follows");
			for (JsonElement je : streams) {
				final JsonObject channel = je.getAsJsonObject().get("channel").getAsJsonObject();
				final String name = channel.get("name").getAsString();
				followedStreams.add(name);
			}
		}
		return followedStreams;
	}

	public boolean channelExists(final String channel) {
		LOGGER.debug("Checking if {} is a twitch channel", channel);
		final String channelId = getTwitchUserIdFromName(channel);
		final URI uri = convertToURI(TWITCH_BASE_URL + "streams/" + channelId);
		if ("{}".equals(getAPIResponse(uri)) || "".equals(channel)) {
			LOGGER.debug("{} is no twitch channel", channel);
			return false;
		}
		LOGGER.debug("{} is a twitch channel", channel);
		return true;
	}

	private static String getAPIResponse(final URI apiUrl) {
		LOGGER.trace("Send Request to API URL '{}'", apiUrl);
		final HttpClient client = HttpClientInterface.getClient();
		HttpClientInterface.startClient();
		try {
			final Request newRequest = client.newRequest(apiUrl);
			newRequest.header("Client-ID", LSGUI_CLIENT_ID);
			return newRequest.send().getContentAsString();
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			LOGGER.error("Error while sending GET Request", e);
		} catch (Exception e) {
			LOGGER.error("Could not start HTTP Client", e);
		}
		return "{}";
	}

	private static URI convertToURI(final String url) {
		try {
			return new URI(url);
		} catch (URISyntaxException e) {
			LOGGER.error("Could not convert String to URI", e);
		}
		return null;
	}

}
