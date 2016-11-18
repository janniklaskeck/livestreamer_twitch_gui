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
package app.lsgui.remote;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zafarkhaja.semver.Version;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import app.lsgui.utils.LsGuiUtils;
import app.lsgui.utils.Settings;

public final class GithubUpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GithubUpdateService.class);

    private static final int CONNECTION_COUNT = 100;
    private static final CloseableHttpClient HTTP_CLIENT;
    private static final String RELEASES_URL = "https://api.github.com/repos/westerwave/"
            + "livestreamer_twitch_gui/releases/latest";
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

    private GithubUpdateService() {
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
                Settings.getInstance().updateLinkProperty().set(htmlUrl);
            }
        }
    }

    private static boolean isVersionNewer(final String tag) {
        String currentVersionTag = LsGuiUtils.readVersionProperty();
        final Version currentVersion = Version.valueOf(currentVersionTag);
        final String realVersionTag = tag.substring(1);
        final Version newVersion = Version.valueOf(realVersionTag);
        if (currentVersion.getPreReleaseVersion().toLowerCase(Locale.ENGLISH).contains("snapshot")) {
            LOGGER.info("Running development version!");
        }
        return newVersion.greaterThan(currentVersion);
    }

    private static ZonedDateTime convertPublishedDate(final String publishedAt) {
        return ZonedDateTime.parse(publishedAt, DTF);
    }
}
