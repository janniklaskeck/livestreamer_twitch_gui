package app.lsgui.serviceapi.twitch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        JsonObject jo = JSONPARSER.parse(getAPIResponse(TWITCHBASEURL + "/streams/" + streamName)).getAsJsonObject();
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

    /**
     * Responsible for downloading the source from an URL and returning it as a
     * String
     *
     * @param urlString
     * @return
     */
    @Deprecated
    public String readJsonFromUrl(String urlString) {
        LOGGER.debug("replace method readJsonFromUrl");
        BufferedReader reader = null;
        HttpURLConnection connUrl;
        try {
            URL url = new URL(urlString);
            connUrl = (HttpURLConnection) url.openConnection();
            if (connUrl.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connUrl.getInputStream()));
            } else {
                return null;
            }
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            if (reader != null) {
                reader.close();
            }
            return buffer.toString();
        } catch (IOException e) {
            if (e.getClass().equals(UnknownHostException.class)) {
                LOGGER.error("No Internet Connection or URL has changed", e);
            } else {
                e.printStackTrace();
            }
        }
        return null;
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
