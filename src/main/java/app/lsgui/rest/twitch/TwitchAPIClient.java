package app.lsgui.rest.twitch;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import app.lsgui.model.twitch.channel.TwitchChannels;
import app.lsgui.model.twitch.game.TwitchGames;
import app.lsgui.settings.Settings;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class TwitchAPIClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchAPIClient.class);
    private static final JsonParser JSONPARSER = new JsonParser();
    private static final String TWITCH_BASE_URL = "https://api.twitch.tv/kraken/";
    private static final String LSGUI_CLIENT_ID = "rfpepzumaxd1iija3ip3fixao6z13pj";
    private static final int CONNECTION_COUNT = 100;
    private static final HttpClient HTTP_CLIENT;

    private static TwitchAPIClient instance = null;

    private TwitchAPIClient() {
        LOGGER.debug("TwitchProcessor constructed");
    }

    /**
     *
     * @return
     */
    public static synchronized TwitchAPIClient getInstance() {
        if (instance == null) {
            instance = new TwitchAPIClient();
        }
        return instance;
    }

    /**
     *
     * @param channelName
     * @return
     */
    public TwitchChannelData getStreamData(final String channelName) {
        if (!"".equals(channelName)) {
            try {
                JsonObject jo = JSONPARSER.parse(getAPIResponse(TWITCH_BASE_URL + "streams/" + channelName))
                        .getAsJsonObject();
                return new TwitchChannelData(jo, channelName);
            } catch (JsonSyntaxException e) {
                LOGGER.error("ERROR while loading channel data. Return empty channel", e);
                return new TwitchChannelData(new JsonObject(), channelName);
            }
        }
        return null;
    }

    public TwitchChannels getGameData(final String game) {
        LOGGER.debug("Load game Data");
        final String gameName = game.replace(" ", "+");
        final int maxChannelsToLoad = Settings.instance().getMaxChannelsLoad();
        final String response = getAPIResponse(
                TWITCH_BASE_URL + "streams/?game=" + gameName + "&offset=0&limit=" + maxChannelsToLoad);
        final JsonObject jo = JSONPARSER.parse(response).getAsJsonObject();
        return new TwitchChannels(jo);
    }

    public TwitchGames getGamesData() {
        LOGGER.debug("Load gamesData");
        final int maxGamesToLoad = Settings.instance().getMaxGamesLoad();
        final String response = getAPIResponse(TWITCH_BASE_URL + "games/top?offset=0&limit=" + maxGamesToLoad);
        final JsonObject jo = JSONPARSER.parse(response).getAsJsonObject();
        return new TwitchGames(jo);
    }

    /**
     *
     * @param userName
     * @return
     */
    public Set<String> getListOfFollowedStreams(final String userName) {
        final Set<String> followedStreams = new TreeSet<>();
        if (!"".equals(userName) && channelExists(userName)) {
            JsonObject jo = JSONPARSER
                    .parse(getAPIResponse("https://api.twitch.tv/kraken/users/" + userName + "/follows/channels"))
                    .getAsJsonObject();

            final int total = jo.get("_total").getAsInt();
            JsonArray streams = jo.getAsJsonArray("follows");
            JsonObject links = jo.get("_links").getAsJsonObject();
            String self = links.get("self").getAsString();
            String next = links.get("next").getAsString();

            int offset = Integer.parseInt(self.split("&")[2].split("=")[1]);
            while (offset < total) {
                for (JsonElement je : streams) {
                    final JsonObject channel = je.getAsJsonObject().get("channel").getAsJsonObject();
                    final String name = channel.get("name").getAsString();
                    followedStreams.add(name);
                }
                jo = JSONPARSER.parse(getAPIResponse(next)).getAsJsonObject();
                streams = jo.getAsJsonArray("follows");
                links = jo.get("_links").getAsJsonObject();
                self = links.get("self").getAsString();
                next = links.get("next").getAsString();
                offset = Integer.valueOf(self.split("&")[2].split("=")[1]);
            }
        }
        return followedStreams;
    }

    /**
     *
     * @param channel
     * @return
     */
    public boolean channelExists(final String channel) {
        LOGGER.debug("Checking if {} is a twitch channel", channel);
        if ("{}".equals(getAPIResponse(TWITCH_BASE_URL + "streams/" + channel)) || "".equals(channel)) {
            LOGGER.debug("{} is no twitch channel", channel);
            return false;
        }
        LOGGER.debug("{} is a twitch channel", channel);
        return true;
    }

    private String getAPIResponse(final String apiUrl) {
        LOGGER.debug("Send Request to API URL '{}'", apiUrl);
        try {
            final URI url = new URI(apiUrl);
            final HttpGet request = new HttpGet(url);
            request.setHeader("Client-ID", LSGUI_CLIENT_ID);
            final HttpResponse response = HTTP_CLIENT.execute(request);
            return new BasicResponseHandler().handleResponse(response);
        } catch (URISyntaxException e) {
            LOGGER.error("URL syntax Error. Please message developer", e);
            return "";
        } catch (IOException e) {
            if (e.getClass().equals(UnknownHostException.class)) {
                LOGGER.error("Twitch is not reachable. Check your Internet Connection");
            } else if (e.getClass().equals(HttpResponseException.class)) {
                LOGGER.error("Http Error when fetching twitch api response. Status Code: {}",
                        ((HttpResponseException) e).getStatusCode());
            } else {
                LOGGER.error("Error when fetching twitch api response", e);
            }
            return "{}";
        }
    }

    static {
        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(CONNECTION_COUNT);
        cm.setDefaultMaxPerRoute(CONNECTION_COUNT);
        HTTP_CLIENT = HttpClients.createMinimal(cm);
    }
}
