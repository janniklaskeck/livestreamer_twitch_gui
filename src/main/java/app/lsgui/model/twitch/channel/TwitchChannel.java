package app.lsgui.model.twitch.channel;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import app.lsgui.model.channel.IChannel;
import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.utils.JSONUtils;
import app.lsgui.utils.LsGuiUtils;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.util.Callback;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class TwitchChannel implements IChannel, ITwitchItem {

    private static final String CHANNEL_IS_OFFLINE = "Channel is offline";
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchChannel.class);
    private static final ZoneOffset OFFSET = ZoneOffset.ofHours(0);
    private static final String PREFIX = "GMT";
    private static final ZoneId GMT = ZoneId.ofOffset(PREFIX, OFFSET);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'").withZone(GMT);
    private static final String STREAM = "stream";
    private static final Image defaultLogo = new Image(
            TwitchChannel.class.getClassLoader().getResource("default_channel.png").toExternalForm());

    private StringProperty name = new SimpleStringProperty();
    private StringProperty logoURL = new SimpleStringProperty();
    private StringProperty previewURL = new SimpleStringProperty();
    private StringProperty game = new SimpleStringProperty();
    private StringProperty title = new SimpleStringProperty();
    private LongProperty uptime = new SimpleLongProperty();
    private StringProperty uptimeString = new SimpleStringProperty();
    private IntegerProperty viewers = new SimpleIntegerProperty();
    private StringProperty viewersString = new SimpleStringProperty();
    private BooleanProperty isOnline = new SimpleBooleanProperty();
    private BooleanProperty isPlaylist = new SimpleBooleanProperty();
    private ObjectProperty<Image> previewImage = new SimpleObjectProperty<>();
    private List<String> availableQualities = new ArrayList<>();

    private boolean cameOnline = false;

    public TwitchChannel(final JsonObject channelAPIResponse, final String name) {
        final JsonElement streamElement = channelAPIResponse.get(STREAM);
        if (channelAPIResponse.get(STREAM) != null && !streamElement.isJsonNull()) {
            final JsonObject streamObject = streamElement.getAsJsonObject();
            if (isStreamObjectValid(streamObject)) {
                setData(streamObject, name);
            }
        } else {
            setData(new JsonObject(), name);
        }
    }

    private boolean isStreamObjectValid(final JsonObject streamObject) {
        return streamObject != null && !streamObject.get("channel").isJsonNull()
                && !streamObject.get("preview").isJsonNull() && !streamObject.isJsonNull();
    }

    private void setData(final JsonObject channelObject, final String name) {
        if (!channelObject.equals(new JsonObject())) {
            setOnlineData(channelObject);
        } else {
            setOffline(name);
        }
    }

    private void setOnlineData(final JsonObject channelObject) {
        final JsonObject channel = channelObject.get("channel").getAsJsonObject();
        final JsonObject preview = channelObject.get("preview").getAsJsonObject();

        this.name.set(JSONUtils.getStringIfNotNull("display_name", channel));
        this.logoURL.set(JSONUtils.getStringIfNotNull("logo", channel));
        this.previewURL.set(JSONUtils.getStringIfNotNull("large", preview));
        this.game.set(JSONUtils.getStringIfNotNull("game", channelObject));
        this.title.set(JSONUtils.getStringIfNotNull("status", channel));
        final String createdAt = JSONUtils.getStringIfNotNull("created_at", channelObject);
        this.uptime.set(calculateUptime(createdAt));
        this.viewers.set(JSONUtils.getIntegerIfNotNull("viewers", channelObject));
        this.isOnline.set(true);
        this.isPlaylist.set(JSONUtils.getBooleanIfNotNull("is_playlist", channelObject));
        this.previewImage.set(new Image(getPreviewURL().get(), true));
        this.availableQualities = new ArrayList<>();
        this.uptimeString.set(buildUptimeString());
        this.viewersString.set(Integer.toString(this.viewers.get()));
        this.availableQualities.clear();
        this.availableQualities.addAll(LsGuiUtils.getAvailableQuality("http://twitch.tv/" + this.name.get()));
    }

    private long calculateUptime(final String createdAt) {
        long uptime = 0L;
        try {
            final ZonedDateTime nowDate = ZonedDateTime.now(GMT);
            final ZonedDateTime startDate = ZonedDateTime.parse(createdAt, DTF);
            long time = startDate.until(nowDate, ChronoUnit.MILLIS);
            uptime = time;
        } catch (Exception e) {
            LOGGER.error("ERROR while parsing date", e);
        }
        return uptime;
    }

    public void updateData(final TwitchChannel data, final boolean notify) {
        if (data != null && data.isOnline().get()) {
            setOnline(data);
        } else if (data != null && !data.isOnline().get()) {
            setOffline(data.getName().get());
        }
        if (this.cameOnline && notify) {
            LsGuiUtils.showOnlineNotification(this);
            this.cameOnline = false;
        }
    }

    private void setOffline(final String name) {
        this.name.setValue(name);
        this.logoURL.setValue(null);
        this.previewURL.setValue(null);
        this.game.setValue(null);
        this.title.setValue(CHANNEL_IS_OFFLINE);
        this.uptime.setValue(null);
        this.viewers.setValue(null);
        this.isOnline.set(false);
        this.isPlaylist.setValue(false);
        this.previewImage.setValue(defaultLogo);
        this.availableQualities = new ArrayList<>();
    }

    private void setOnline(final TwitchChannel data) {
        LOGGER.trace("update {} with data {}", data.getName(), data.isOnline());
        this.name.setValue(data.getName().get());
        this.logoURL.setValue(data.getLogoURL().get());
        this.previewURL.setValue(data.getPreviewURL().get());
        this.game.setValue(data.getGame().get());
        this.title.setValue(data.getTitle().get());
        this.uptime.setValue(data.getUptime().get());
        this.uptimeString.setValue(buildUptimeString());
        this.viewers.setValue(data.getViewers().get());
        this.viewersString.setValue(Integer.toString(getViewers().get()));
        if (data.isOnline().get() && !isOnline.get()) {
            this.isOnline.set(true);
            this.cameOnline = true;
        } else if (!data.isOnline().get()) {
            this.isOnline.set(false);
        }
        this.isPlaylist.setValue(data.getIsPlaylist().get());
        this.previewImage.setValue(data.getPreviewImage().get());
        this.availableQualities = new ArrayList<>(data.getAvailableQualities());
    }

    private String buildUptimeString() {
        final long hours = TimeUnit.MILLISECONDS.toHours(uptime.get());
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime.get())
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(uptime.get()));
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime.get())
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(uptime.get()));
        return String.format("%02d:%02d:%02d Uptime", hours, minutes, seconds);
    }

    public static Callback<IChannel, Observable[]> extractor() {
        return (IChannel sm) -> new Observable[] { ((TwitchChannel) sm).getName(), ((TwitchChannel) sm).getGame(),
                ((TwitchChannel) sm).isOnline(), ((TwitchChannel) sm).getTitle(), ((TwitchChannel) sm).getLogoURL(),
                ((TwitchChannel) sm).getPreviewImage(), ((TwitchChannel) sm).getPreviewURL(),
                ((TwitchChannel) sm).getUptime(), ((TwitchChannel) sm).getViewers() };
    }

    @Override
    public StringProperty getName() {
        return name;
    }

    public StringProperty getLogoURL() {
        return logoURL;
    }

    public StringProperty getPreviewURL() {
        return previewURL;
    }

    public StringProperty getGame() {
        return game;
    }

    public StringProperty getTitle() {
        return title;
    }

    public LongProperty getUptime() {
        return uptime;
    }

    public IntegerProperty getViewers() {
        return viewers;
    }

    @Override
    public BooleanProperty isOnline() {
        return isOnline;
    }

    public ObjectProperty<Image> getPreviewImage() {
        return previewImage;
    }

    @Override
    public List<String> getAvailableQualities() {
        if (availableQualities.isEmpty()) {
            availableQualities.add(CHANNEL_IS_OFFLINE);
        }
        return availableQualities;
    }

    public StringProperty getUptimeString() {
        return uptimeString;
    }

    public StringProperty getViewersString() {
        return viewersString;
    }

    public BooleanProperty getIsPlaylist() {
        return isPlaylist;
    }

    public void setIsPlaylist(BooleanProperty isPlaylist) {
        this.isPlaylist = isPlaylist;
    }
}
