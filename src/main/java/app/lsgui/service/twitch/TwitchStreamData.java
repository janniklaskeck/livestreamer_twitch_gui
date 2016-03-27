package app.lsgui.serviceapi.twitch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import app.lsgui.utils.Utils;
import javafx.scene.image.Image;

public class TwitchStreamData {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchStreamData.class);
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

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

    public TwitchStreamData(JsonObject streamObject) {
        LOGGER.debug("Load stream Json");

        JsonObject channel = streamObject.get("channel").getAsJsonObject();
        JsonObject preview = streamObject.get("preview").getAsJsonObject();

        setTitle(Utils.getStringIfNotNull("status", channel));
        setGame(Utils.getStringIfNotNull("game", streamObject));
        setViewers(Utils.getIntegerIfNotNull("viewers", streamObject));
        setPreviewURL(Utils.getStringIfNotNull("large", preview));
        setCreatedAt(Utils.getStringIfNotNull("created_at", streamObject));
        setUpdatedAt(Utils.getStringIfNotNull("updated_at", channel));
        setLogoURL(Utils.getStringIfNotNull("logo", channel));

        calculateAndSetUptime();
        Utils.loadImageFromURLAsync(this);
    }

    private void calculateAndSetUptime() {
        SDF.setTimeZone(TimeZone.getTimeZone("GMT"));
        long uptime = 0L;
        try {
            Date start_date;
            final Date now_date = new Date();

            start_date = SDF.parse(getCreatedAt());
            uptime = now_date.getTime() - start_date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setUptime(uptime);
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

}