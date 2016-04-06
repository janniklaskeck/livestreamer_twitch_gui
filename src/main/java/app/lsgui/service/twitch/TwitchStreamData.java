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

public class TwitchStreamData {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchStreamData.class);

    private final static ZoneOffset offset = ZoneOffset.ofHours(-2);
    private final static String prefix = "GMT"; // Greenwich Mean Time
    private final static ZoneId gmt = ZoneId.ofOffset(prefix, offset);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'").withZone(gmt);;

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

    public TwitchStreamData(final JsonObject streamJson, final String name) {
        JsonObject streamObject = null;
        if (!streamJson.get("stream").isJsonNull()) {
            streamObject = streamJson.get("stream").getAsJsonObject();
            if (streamObject != null && !streamObject.get("channel").isJsonNull() && !streamObject.isJsonNull()) {
                JsonObject channel = streamObject.get("channel").getAsJsonObject();
                JsonObject preview = streamObject.get("preview").getAsJsonObject();
                setName(name);
                setTitle(Utils.getStringIfNotNull("status", channel));
                setGame(Utils.getStringIfNotNull("game", streamObject));
                setViewers(Utils.getIntegerIfNotNull("viewers", streamObject));
                setPreviewURL(Utils.getStringIfNotNull("large", preview));
                setCreatedAt(Utils.getStringIfNotNull("created_at", streamObject));
                setUpdatedAt(Utils.getStringIfNotNull("updated_at", channel));
                setLogoURL(Utils.getStringIfNotNull("logo", channel));
                setOnline(true);
                calculateAndSetUptime();
                setPreviewImage(new Image(getPreviewURL()));
                setQualities(Utils.getAvailableQuality("http://twitch.tv/" + name));
            }
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
        }
    }

    private void calculateAndSetUptime() {
        try {

            final ZonedDateTime now_date = ZonedDateTime.now().withZoneSameLocal(gmt);
            ZonedDateTime start_date = ZonedDateTime.parse(getCreatedAt(), DTF);
            long time = start_date.until(now_date, ChronoUnit.MILLIS);
            time -= 7200000L;
            setUptime(time);
        } catch (Exception e) {
            LOGGER.error("ERROR while parsing date", e);
            setUptime(0L);
        }

    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getViewers() {
        return viewers;
    }

    public void setViewers(int viewers) {
        this.viewers = viewers;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public void setPreviewURL(String previewURL) {
        this.previewURL = previewURL;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    /**
     * @return the logoImage
     */
    public Image getLogoImage() {
        return logoImage;
    }

    /**
     * @param logoImage
     *            the logoImage to set
     */
    public void setLogoImage(Image logoImage) {
        this.logoImage = logoImage;
    }

    /**
     * @return the previewImage
     */
    public Image getPreviewImage() {
        return previewImage;
    }

    /**
     * @param previewImage
     *            the previewImage to set
     */
    public void setPreviewImage(Image previewImage) {
        this.previewImage = previewImage;
    }

    /**
     * @return the qualities
     */
    public List<String> getQualities() {
        return qualities;
    }

    /**
     * @param qualities
     *            the qualities to set
     */
    public void setQualities(List<String> qualities) {
        this.qualities = qualities;
    }

}
