package app.lsgui.service.twitch;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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

public class TwitchProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchProcessor.class);
    private static final JsonParser JSONPARSER = new JsonParser();
    private static final String TWITCHBASEURL = "https://api.twitch.tv/kraken/";
    private static final String LSGUI_CLIENT_ID = "rfpepzumaxd1iija3ip3fixao6z13pj";
    private static final int CONNECTION_COUNT = 100;
    private static final HttpClient HTTP_CLIENT;

    private static TwitchProcessor instance = null;

    private TwitchProcessor() {
        LOGGER.debug("TwitchManager constructed");
    }

    public static TwitchProcessor instance() {
        if (instance == null) {
            instance = new TwitchProcessor();
        }
        return instance;
    }

    public TwitchStreamData getStreamData(final String streamName) {
        LOGGER.debug("Load streamData for {}", streamName);

        JsonObject jo = JSONPARSER.parse(getAPIResponse(TWITCHBASEURL + "streams/" + streamName)).getAsJsonObject();
        TwitchStreamData data = new TwitchStreamData(jo);
        return data;
    }

    public TwitchGames getGameData() {
        LOGGER.debug("Load gamesData");
        LOGGER.debug("gamestoload not implemented");
        JsonObject jo = JSONPARSER.parse(getAPIResponse(TWITCHBASEURL + "games/top?limit=" + 20 + "&offset=0"))
                .getAsJsonObject();
        TwitchGames data = new TwitchGames(jo);
        return data;
    }

    public TwitchChannelsData getChannelData(final String gameName) {
        LOGGER.debug("Load channelsData for {}", gameName);
        LOGGER.debug("gamestoload not implemented");
        JsonObject jo = JSONPARSER.parse(getAPIResponse(TWITCHBASEURL + "streams?game=" + gameName + "&limit=" + 20))
                .getAsJsonObject();
        TwitchChannelsData data = new TwitchChannelsData(jo);
        return data;
    }

    public Set<String> getListOfFollowedStreams(final String userName) {
        Set<String> followedStreams = new TreeSet<String>();
        JsonObject jo = JSONPARSER
                .parse(getAPIResponse("https://api.twitch.tv/kraken/users/" + userName + "/follows/channels"))
                .getAsJsonObject();

        final int total = jo.get("_total").getAsInt();
        JsonArray streams = jo.getAsJsonArray("follows");
        JsonObject _links = jo.get("_links").getAsJsonObject();
        String self = _links.get("self").getAsString();
        String next = _links.get("next").getAsString();

        int offset = Integer.valueOf(self.split("&")[2].split("=")[1]);

        while (offset < total) {

            for (JsonElement je : streams) {
                JsonObject channel = je.getAsJsonObject().get("channel").getAsJsonObject();
                String name = channel.get("name").getAsString();
                followedStreams.add(name);
            }

            jo = JSONPARSER.parse(getAPIResponse(next)).getAsJsonObject();
            streams = jo.getAsJsonArray("follows");
            _links = jo.get("_links").getAsJsonObject();
            self = _links.get("self").getAsString();
            next = _links.get("next").getAsString();
            offset = Integer.valueOf(self.split("&")[2].split("=")[1]);
        }

        return followedStreams;
    }

    public boolean channelExists(final String channel) {
        if (getAPIResponse(TWITCHBASEURL + "streams/" + channel) == null) {
            return false;
        }
        return true;
    }

    public String getAPIResponse(final String apiUrl) {
        String result = null;
        try {
            final URI URL = new URI(apiUrl);
            final HttpGet request = new HttpGet(URL);
            request.setHeader("Client-ID", LSGUI_CLIENT_ID);
            final HttpResponse response = HTTP_CLIENT.execute(request);
            result = new BasicResponseHandler().handleResponse(response);
        } catch (URISyntaxException | IOException e) {
            LOGGER.error("Error when fetching twitch api response", e);
        }
        return result;

    }

    static {
        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(CONNECTION_COUNT);
        cm.setDefaultMaxPerRoute(CONNECTION_COUNT);
        HTTP_CLIENT = HttpClients.createMinimal(cm);
    }
}
