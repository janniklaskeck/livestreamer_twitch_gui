package app.lsgui.service.twitch;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
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
import com.google.gson.JsonSyntaxException;

public class TwitchAPIClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchAPIClient.class);
    private static final JsonParser JSONPARSER = new JsonParser();
    private static final String TWITCHBASEURL = "https://api.twitch.tv/kraken/";
    private static final String LSGUI_CLIENT_ID = "rfpepzumaxd1iija3ip3fixao6z13pj";
    private static final int CONNECTION_COUNT = 100;
    private static final HttpClient HTTP_CLIENT;

    private static TwitchAPIClient instance = null;

    private TwitchAPIClient() {
        LOGGER.debug("TwitchProcessor constructed");
    }

    public static synchronized TwitchAPIClient instance() {
        if (instance == null) {
            instance = new TwitchAPIClient();
        }

        return instance;
    }

    public TwitchChannelData getStreamData(final String channelName) {
        try {
            JsonObject jo = JSONPARSER.parse(getAPIResponse(TWITCHBASEURL + "streams/" + channelName))
                    .getAsJsonObject();
            return new TwitchChannelData(jo, channelName);
        } catch (JsonSyntaxException e) {
            LOGGER.error("ERROR while loading channel data. Return empty channel", e);
            return new TwitchChannelData(new JsonObject(), channelName);
        }
    }

    public TwitchGameData getGameData() {
        LOGGER.debug("Load gamesData");
        LOGGER.debug("gamestoload not implemented");
        JsonObject jo = JSONPARSER.parse(getAPIResponse(TWITCHBASEURL + "games/top?limit=" + 20 + "&offset=0"))
                .getAsJsonObject();
        return new TwitchGameData(jo);
    }

    public Set<String> getListOfFollowedStreams(final String userName) {
        Set<String> followedStreams = new TreeSet<>();
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
                JsonObject channel = je.getAsJsonObject().get("channel").getAsJsonObject();
                String name = channel.get("name").getAsString();
                followedStreams.add(name);
            }
            jo = JSONPARSER.parse(getAPIResponse(next)).getAsJsonObject();
            streams = jo.getAsJsonArray("follows");
            links = jo.get("_links").getAsJsonObject();
            self = links.get("self").getAsString();
            next = links.get("next").getAsString();
            offset = Integer.valueOf(self.split("&")[2].split("=")[1]);
        }
        return followedStreams;
    }

    public boolean channelExists(final String channel) {
        if ("".equals(getAPIResponse(TWITCHBASEURL + "streams/" + channel))) {
            return false;
        }
        return true;
    }

    public String getAPIResponse(final String apiUrl) {
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
