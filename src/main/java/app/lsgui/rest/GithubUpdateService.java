package app.lsgui.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import app.lsgui.settings.Settings;
import app.lsgui.utils.LsGuiUtils;

public class GithubUpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GithubUpdateService.class);

    private static final int CONNECTION_COUNT = 100;
    private static final CloseableHttpClient HTTP_CLIENT;
    private static final String RELEASES_URL = "https://api.github.com/repos/westerwave/livestreamer_twitch_gui/releases/latest";
    private static final ZoneOffset OFFSET = ZoneOffset.ofHours(0);
    private static final String PREFIX = "GMT";
    private static final ZoneId GMT = ZoneId.ofOffset(PREFIX, OFFSET);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'").withZone(GMT);

    static {
        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(CONNECTION_COUNT);
        cm.setDefaultMaxPerRoute(CONNECTION_COUNT);
        HTTP_CLIENT = HttpClients.createMinimal(cm);
    }

    public static void checkForUpdate() {
        LOGGER.debug("Check for updates on URL '{}'", RELEASES_URL);
        String responseString = "";
        final HttpGet request = new HttpGet(convertToURI(RELEASES_URL));
        request.setHeader("Accept", "application/vnd.github.v3+json");
        try (CloseableHttpResponse response = HTTP_CLIENT.execute(request)) {
            responseString = new BasicResponseHandler().handleResponse(response);
        } catch (IOException e) {
            if (e.getClass().equals(UnknownHostException.class)) {
                LOGGER.error("Twitch is not reachable. Check your Internet Connection");
            } else if (e.getClass().equals(HttpResponseException.class)) {
                LOGGER.error("Http Error when fetching twitch api response. Status Code: {}",
                        ((HttpResponseException) e).getStatusCode());
            } else {
                LOGGER.error("Error when fetching twitch api response", e);
            }
        } finally {
            request.reset();
        }
        if (responseString != null && !"".equals(responseString)) {
            processJsonResponse(responseString);
        }
    }

    private static URI convertToURI(final String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            LOGGER.error("Could not convert String to URI", e);
        }
        return null;
    }

    private static void processJsonResponse(final String responseString) {
        final JsonParser parser = new JsonParser();
        final JsonElement releaseElement = parser.parse(responseString);
        if (releaseElement.isJsonObject()) {
            final JsonObject latestReleaseFull = releaseElement.getAsJsonObject();
            final String tag = latestReleaseFull.get("tag_name").getAsString();
            final String publishedAt = latestReleaseFull.get("published_at").getAsString();
            final boolean isPreRelease = latestReleaseFull.get("prerelease").getAsBoolean();
            final String htmlUrl = latestReleaseFull.get("html_url").getAsString();
            if (!isPreRelease && isVersionNewer(tag)) {
                final ZonedDateTime publishedDate = convertPublishedDate(publishedAt);
                LsGuiUtils.showUpdateNotification(tag, publishedDate, event -> LsGuiUtils.openURLInBrowser(htmlUrl));
                Settings.getInstance().setUpdateLink(htmlUrl);
            }
        }
    }

    private static boolean isVersionNewer(final String tag) {
        final String realVersionTag = tag.substring(1);
        String currentVersionTag = readVersionProperty();
        if (currentVersionTag.endsWith("-SNAPSHOT")) {
            currentVersionTag = currentVersionTag.replace("-SNAPSHOT", "");
            LOGGER.info("Running development version!");
        }
        final int newVersion = calcVersionSum(realVersionTag);
        final int currentVersion = calcVersionSum(currentVersionTag);
        return newVersion > currentVersion;
    }

    private static String readVersionProperty() {
        final InputStream propertyStream = GithubUpdateService.class.getClassLoader()
                .getResourceAsStream("version.properties");
        final Properties versionProperty = new Properties();
        String version = "";
        try {
            versionProperty.load(propertyStream);
            version = versionProperty.getProperty("versionNumber");
            LOGGER.debug("Read Version {} from version.properties", version);
        } catch (IOException e) {
            LOGGER.error("Could not load Properties from Inpustream!", e);
        }
        return version;
    }

    private static int calcVersionSum(final String version) {
        final String[] versionSplit = version.split("\\.");
        int sum = 0;
        // TODO such a bad system
        sum += Integer.parseInt(versionSplit[0]) * 10;
        sum += Integer.parseInt(versionSplit[1]) * 5;
        sum += Integer.parseInt(versionSplit[2]);
        return sum;
    }

    private static ZonedDateTime convertPublishedDate(final String publishedAt) {
        return ZonedDateTime.parse(publishedAt, DTF);
    }
}
