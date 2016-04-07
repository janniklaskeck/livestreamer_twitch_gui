package app.lsgui.model.twitch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.Channel;
import app.lsgui.service.twitch.TwitchChannelData;
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

public class TwitchChannel implements Channel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchChannel.class);
    // TODO Choose other default image
    @SuppressWarnings("unused")
    private static final String DEFAULT_CHANNEL_LOGO = "";

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
    private ObjectProperty<Image> previewImage;
    private List<String> availableQualities;

    public TwitchChannel(final String name) {
        this.name = new SimpleStringProperty(name);
        this.logoURL = new SimpleStringProperty("");
        this.previewURL = new SimpleStringProperty("");
        this.game = new SimpleStringProperty("");
        this.title = new SimpleStringProperty("");
        this.uptime = new SimpleLongProperty(0L);
        this.viewers = new SimpleIntegerProperty(0);
        this.isOnline = new SimpleBooleanProperty(false);
        this.previewImage = new SimpleObjectProperty<Image>(null);
        this.description = new SimpleStringProperty("Stream is offline");
        this.availableQualities = new SimpleListProperty<String>();
        this.uptimeString = new SimpleStringProperty("");
        this.viewersString = new SimpleStringProperty("");
        this.availableQualities = new ArrayList<String>();
    }

    public void updateData(final TwitchChannelData data) {
        if (data != null) {
            LOGGER.info("update {} with data {}", data.getName(), data.isOnline());
            name.setValue(data.getName());
            logoURL.setValue(data.getLogoURL());
            previewURL.setValue(data.getPreviewURL());
            game.setValue(data.getGame());
            title.setValue(data.getTitle());
            uptime.setValue(data.getUptime());
            String upTimeStringValue = String.format("%02d:%02d:%02d Uptime",
                    TimeUnit.MILLISECONDS.toHours(uptime.get()),
                    TimeUnit.MILLISECONDS.toMinutes(uptime.get())
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(uptime.get())),
                    TimeUnit.MILLISECONDS.toSeconds(uptime.get())
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(uptime.get())));
            uptimeString.setValue(upTimeStringValue);
            viewers.setValue(data.getViewers());
            viewersString.setValue(getViewers().get() + "");
            if (data.isOnline() && !isOnline.get()) {
                isOnline.setValue(true);
                LOGGER.info("Stream {} just came online. TODO Notice User", getName().get());
            } else if (!data.isOnline() && isOnline.get()) {
                isOnline.setValue(false);
            }
            previewImage.setValue(data.getPreviewImage());
            description.setValue(getTitle().get());
            this.availableQualities = new ArrayList<String>(data.getQualities());
        } else {
            name.setValue(null);
            logoURL.setValue(null);
            previewURL.setValue(null);
            game.setValue(null);
            title.setValue(null);
            uptime.setValue(null);
            viewers.setValue(null);
            isOnline.setValue(false);
            previewImage.setValue(null);
            description.setValue("Stream is offline");
            availableQualities = new ArrayList<String>();
            availableQualities.add("worst, best");
        }
    }

    public static Callback<Channel, Observable[]> extractor() {
        return (Channel sm) -> new Observable[] { ((TwitchChannel) sm).getName(),
                ((TwitchChannel) sm).getGame(), ((TwitchChannel) sm).isOnline(),
                ((TwitchChannel) sm).getTitle(), ((TwitchChannel) sm).getDescription(),
                ((TwitchChannel) sm).getLogoURL(), ((TwitchChannel) sm).getPreviewImage(),
                ((TwitchChannel) sm).getPreviewURL(), ((TwitchChannel) sm).getUptime(),
                ((TwitchChannel) sm).getViewers() };
    }

    /**
     * @return the name
     */
    @Override
    public StringProperty getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(StringProperty name) {
        this.name = name;
    }

    /**
     * @return the logoURL
     */
    public StringProperty getLogoURL() {
        return logoURL;
    }

    /**
     * @param logoURL
     *            the logoURL to set
     */
    public void setLogoURL(StringProperty logoURL) {
        this.logoURL = logoURL;
    }

    /**
     * @return the previewURL
     */
    public StringProperty getPreviewURL() {
        return previewURL;
    }

    /**
     * @param previewURL
     *            the previewURL to set
     */
    public void setPreviewURL(StringProperty previewURL) {
        this.previewURL = previewURL;
    }

    /**
     * @return the game
     */
    public StringProperty getGame() {
        return game;
    }

    /**
     * @param game
     *            the game to set
     */
    public void setGame(StringProperty game) {
        this.game = game;
    }

    /**
     * @return the title
     */
    public StringProperty getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(StringProperty title) {
        this.title = title;
    }

    /**
     * @return the uptime
     */
    public LongProperty getUptime() {
        return uptime;
    }

    /**
     * @param uptime
     *            the uptime to set
     */
    public void setUptime(LongProperty uptime) {
        this.uptime = uptime;
    }

    /**
     * @return the viewers
     */
    public IntegerProperty getViewers() {
        return viewers;
    }

    /**
     * @param viewers
     *            the viewers to set
     */
    public void setViewers(IntegerProperty viewers) {
        this.viewers = viewers;
    }

    /**
     * @return the online
     */
    @Override
    public BooleanProperty isOnline() {
        return isOnline;
    }

    /**
     * @param online
     *            the online to set
     */
    public void setIsOnline(BooleanProperty isOnline) {
        this.isOnline = isOnline;
    }

    /**
     * @return the previewImage
     */
    public ObjectProperty<Image> getPreviewImage() {
        return previewImage;
    }

    /**
     * @param previewImage
     *            the previewImage to set
     */
    public void setPreviewImage(ObjectProperty<Image> previewImage) {
        this.previewImage = previewImage;
    }

    public StringProperty getDescription() {
        return description;
    }

    /**
     * @return the availableQualities
     */
    public List<String> getAvailableQualities() {
        if (availableQualities.size() == 0) {
            availableQualities.add("Stream is offline");
        }
        return availableQualities;
    }

    /**
     * @param availableQualities
     *            the availableQualities to set
     */
    public void setAvailableQualities(List<String> availableQualities) {
        this.availableQualities = availableQualities;
    }

    /**
     * @return the uptimeString
     */
    public StringProperty getUptimeString() {
        return uptimeString;
    }

    /**
     * @param uptimeString
     *            the uptimeString to set
     */
    public void setUptimeString(StringProperty uptimeString) {
        this.uptimeString = uptimeString;
    }

    /**
     * @return the viewersString
     */
    public StringProperty getViewersString() {
        return viewersString;
    }

    /**
     * @param viewersString
     *            the viewersString to set
     */
    public void setViewersString(StringProperty viewersString) {
        this.viewersString = viewersString;
    }

}
