package app.lsgui.model.twitch.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.rest.twitch.TwitchChannelData;
import app.lsgui.utils.LsGuiUtils;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
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
    private static Image defaultLogo = new Image(
            TwitchChannel.class.getClassLoader().getResource("default_channel.png").toExternalForm());

    private StringProperty name;
    private StringProperty logoURL;
    private StringProperty previewURL;
    private StringProperty game;
    private StringProperty title;
    private StringProperty description;
    private LongProperty uptime;
    private StringProperty uptimeString;
    private IntegerProperty viewers;
    private StringProperty viewersString;
    private BooleanProperty isOnline;
    private BooleanProperty isPlaylist;
    private ObjectProperty<Image> previewImage;
    private List<String> availableQualities;

    private boolean cameOnline = false;

    public TwitchChannel(final String name) {
        this.name = new SimpleStringProperty(name);
        this.logoURL = new SimpleStringProperty("");
        this.previewURL = new SimpleStringProperty("");
        this.game = new SimpleStringProperty("");
        this.title = new SimpleStringProperty("");
        this.uptime = new SimpleLongProperty(0L);
        this.viewers = new SimpleIntegerProperty(0);
        this.isOnline = new SimpleBooleanProperty(false);
        this.isPlaylist = new SimpleBooleanProperty(false);
        this.previewImage = new SimpleObjectProperty<>(null);
        this.description = new SimpleStringProperty(CHANNEL_IS_OFFLINE);
        this.availableQualities = new SimpleListProperty<>();
        this.uptimeString = new SimpleStringProperty("");
        this.viewersString = new SimpleStringProperty("");
        this.availableQualities = new ArrayList<>();
    }

    public void updateData(final TwitchChannelData data, final boolean notify) {
        if (data != null && data.isOnline()) {
            setOnline(data);
        } else if (data != null && !data.isOnline()) {
            setOffline(data);
        }
        if (cameOnline && notify) {
            LsGuiUtils.showOnlineNotification(this);
            cameOnline = false;
        }
    }

    private void setOffline(final TwitchChannelData data) {
        name.setValue(data.getName());
        logoURL.setValue(null);
        previewURL.setValue(null);
        game.setValue(null);
        title.setValue(null);
        uptime.setValue(null);
        viewers.setValue(null);
        isOnline.set(false);
        isPlaylist.setValue(data.isPlaylist());
        previewImage.setValue(defaultLogo);
        description.setValue(CHANNEL_IS_OFFLINE);
        availableQualities = new ArrayList<>();
    }

    private void setOnline(final TwitchChannelData data) {
        LOGGER.debug("update {} with data {}", data.getName(), data.isOnline());
        name.setValue(data.getName());
        logoURL.setValue(data.getLogoURL());
        previewURL.setValue(data.getPreviewURL());
        game.setValue(data.getGame());
        title.setValue(data.getTitle());
        uptime.setValue(data.getUptime());
        uptimeString.setValue(buildUptimeString());
        viewers.setValue(data.getViewers());
        viewersString.setValue(Integer.toString(getViewers().get()));
        if (data.isOnline() && !isOnline.get()) {
            isOnline.set(true);
            cameOnline = true;
        } else if (!data.isOnline()) {
            isOnline.set(false);
        }
        isPlaylist.setValue(data.isPlaylist());
        previewImage.setValue(data.getPreviewImage());
        description.bind(title);
        availableQualities = new ArrayList<>(data.getQualities());
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
                ((TwitchChannel) sm).isOnline(), ((TwitchChannel) sm).getTitle(), ((TwitchChannel) sm).getDescription(),
                ((TwitchChannel) sm).getLogoURL(), ((TwitchChannel) sm).getPreviewImage(),
                ((TwitchChannel) sm).getPreviewURL(), ((TwitchChannel) sm).getUptime(),
                ((TwitchChannel) sm).getViewers() };
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

    public StringProperty getDescription() {
        return description;
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
