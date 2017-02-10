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
import app.lsgui.model.twitch.TwitchChannels;
import app.lsgui.model.twitch.TwitchGames;
import app.lsgui.remote.HttpClientInterface;
import app.lsgui.utils.JsonUtils;
import app.lsgui.utils.Settings;
import app.lsgui.utils.TwitchUtils;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public final class TwitchAPIClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitchAPIClient.class);
	private static final JsonParser JSONPARSER = new JsonParser();

	private static final String TWITCH_API_VERSION_HEADER = "application/vnd.twitchtv.v5+json";
	private static final String TWITCH_BASE_URL = "https://api.twitch.tv/kraken/";
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

	public TwitchChannel getStreamData(final String channelName, final boolean isBrowser) {
		LOGGER.debug("Get Data for Channel '{}'", channelName);
		TwitchChannel channel = TwitchUtils.constructTwitchChannel(new JsonObject(), channelName, isBrowser);
		if (!"".equals(channelName)) {
			try {
				final String twitchUserId = getTwitchUserIdFromName(channelName, false);
				if (twitchUserId.isEmpty()) {
					return channel;
				}
				final URI uri = convertToURI(TWITCH_BASE_URL + "streams/" + twitchUserId);
				final JsonObject jsonData = JSONPARSER.parse(getAPIResponse(uri)).getAsJsonObject();
				channel = TwitchUtils.constructTwitchChannel(jsonData, channelName, isBrowser);
			} catch (JsonSyntaxException e) {
				LOGGER.error("ERROR while loading channel data. Return empty channel", e);
			}
		} else {
			LOGGER.error("Channelname is empty");
		}
		return channel;
	}

	private static String getTwitchUserIdFromName(final String channelName, final boolean dontRepeat) {
		LOGGER.trace("Request Twitch UserId for username {}", channelName);
		final URI uri = convertToURI(TWITCH_BASE_URL + "search/channels?query=" + channelName);
		final JsonObject jsonData = JSONPARSER.parse(getAPIResponse(uri)).getAsJsonObject();
		final JsonArray channels = jsonData.get("channels").getAsJsonArray();
		if (channels.size() == 0 && !dontRepeat) {
			LOGGER.warn("Search result for channel '{}' was empty, retrying UserId search once!", channelName);
			return getTwitchUserIdFromName(channelName, true);
		} else if (channels.size() == 0 && dontRepeat) {
			LOGGER.warn("Search result for channel '{}' still empty after retry!", channelName);
			return "";
		}
		String userId = "";
		for (final JsonElement element : channels) {
			final JsonObject jsonObject = element.getAsJsonObject();
			final String userName = JsonUtils.getStringIfNotNull("name", jsonObject);
			if (userName.equalsIgnoreCase(channelName)) {
				userId = jsonObject.get("_id").getAsString();
				break;
			}
		}
		LOGGER.debug("Return Twitch User id {} for username {}", userId, channelName);
		return userId;
	}

	public TwitchChannels getGameData(final String game) {
		LOGGER.debug("Load game Data");
		final String gameName = game.replace(' ', '+');
		final int maxChannelsToLoad = Settings.getInstance().maxChannelsProperty().get();
		final URI uri = convertToURI(
				TWITCH_BASE_URL + "streams/?game=" + gameName + "&offset=0&limit=" + maxChannelsToLoad);
		final String response = getAPIResponse(uri);
		final JsonObject jo = JSONPARSER.parse(response).getAsJsonObject();
		return new TwitchChannels(jo);
	}

	public TwitchGames getGamesData() {
		LOGGER.debug("Load gamesData");
		final int maxGamesToLoad = Settings.getInstance().maxGamesProperty().get();
		final URI uri = convertToURI(TWITCH_BASE_URL + "games/top?offset=0&limit=" + maxGamesToLoad);
		final String response = getAPIResponse(uri);
		final JsonObject jo = JSONPARSER.parse(response).getAsJsonObject();
		return new TwitchGames(jo);
	}

	public Set<String> getListOfFollowedStreams(final String userName) {
		final Set<String> followedStreams = new TreeSet<>();
		if (!"".equals(userName) && this.channelExists(userName)) {
			final String userId = getTwitchUserIdFromName(userName, false);
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
		final String channelId = getTwitchUserIdFromName(channel, false);
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
			newRequest.header("Accept", TWITCH_API_VERSION_HEADER);
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
