package app.lsgui.service.twitch;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import app.lsgui.utils.Utils;
import javafx.scene.image.Image;

public class TwitchChannelData {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchChannelData.class);

    private static final ZoneOffset OFFSET = ZoneOffset.ofHours(-2);
    private static final String PREFIX = "GMT"; // Greenwich Mean Time
    private static final ZoneId GMT = ZoneId.ofOffset(PREFIX, OFFSET);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'").withZone(GMT);;

    private boolean online = false;
    private String name = "";
    private String title = "";
    private String createdAt = "";
    private String updatedAt = "";
    private String previewURL = "";
    private String logoURL = "";
    private String game = "";
    private long uptime = 0L;
    private int viewers = 0;
    private Image previewImage;
    private Image logoImage;
    private List<String> qualities;

    public TwitchChannelData(final JsonObject channelAPIResponse, final String name) {
        if (!channelAPIResponse.get("stream").isJsonNull()) {
            JsonObject streamObject = channelAPIResponse.get("stream").getAsJsonObject();
            if (streamObject != null && !streamObject.get("channel").isJsonNull() && !streamObject.isJsonNull()) {
                setData(streamObject, name);
            }
        } else {
            setData(null, name);
        }
    }

    private void setData(final JsonObject channelObject, final String name) {
        if (channelObject != null) {
            JsonObject channel = channelObject.get("channel").getAsJsonObject();
            JsonObject preview = channelObject.get("preview").getAsJsonObject();
            setName(name);
            setTitle(Utils.getStringIfNotNull("status", channel));
            setGame(Utils.getStringIfNotNull("game", channelObject));
            setViewers(Utils.getIntegerIfNotNull("viewers", channelObject));
            setPreviewURL(Utils.getStringIfNotNull("large", preview));
            setCreatedAt(Utils.getStringIfNotNull("created_at", channelObject));
            setUpdatedAt(Utils.getStringIfNotNull("updated_at", channel));
            setLogoURL(Utils.getStringIfNotNull("logo", channel));
            setOnline(true);
            calculateAndSetUptime();
            setPreviewImage(new Image(getPreviewURL()));
            setLogoImage(null);
            setQualities(Utils.getAvailableQuality("http://twitch.tv/" + name));
        } else {
            setName(name);
            setTitle("");
            setGame("");
            setViewers(0);
            setPreviewURL("");
            setCreatedAt("");
            setUpdatedAt("");
            setLogoURL("");
            setOnline(false);
            setQualities(new ArrayList<String>());
            setPreviewImage(null);
            setLogoImage(null);
        }
    }

    private void calculateAndSetUptime() {
        try {
            final ZonedDateTime nowDate = ZonedDateTime.now().withZoneSameLocal(GMT);
            ZonedDateTime startDate = ZonedDateTime.parse(getCreatedAt(), DTF);
            long time = startDate.until(nowDate, ChronoUnit.MILLIS);
            final long gmtCorrection = -7200000L;
            time += gmtCorrection;
            setUptime(time);
        } catch (Exception e) {
            LOGGER.error("ERROR while parsing date", e);
            setUptime(0L);
        }
    }

    public boolean isOnline() {
        return online;
    }

    private void setOnline(boolean online) {
        this.online = online;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public int getViewers() {
        return viewers;
    }

    private void setViewers(int viewers) {
        this.viewers = viewers;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    private void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    private void setPreviewURL(String previewURL) {
        this.previewURL = previewURL;
    }

    public String getLogoURL() {
        return logoURL;
    }

    private void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public String getGame() {
        return game;
    }

    private void setGame(String game) {
        this.game = game;
    }

    public long getUptime() {
        return uptime;
    }

    private void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public Image getLogoImage() {
        return logoImage;
    }

    private void setLogoImage(Image logoImage) {
        this.logoImage = logoImage;
    }

    public Image getPreviewImage() {
        return previewImage;
    }

    private void setPreviewImage(Image previewImage) {
        this.previewImage = previewImage;
    }

    public List<String> getQualities() {
        return qualities;
    }

    private void setQualities(List<String> qualities) {
        this.qualities = qualities;
    }

}
