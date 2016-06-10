package app.lsgui.model.channel.twitch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
import app.lsgui.rest.twitch.TwitchChannelData;
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
public class TwitchChannel implements IChannel {

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

    /**
     *
     * @param name
     */
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

    /**
     *
     * @param data
     */
    public void updateData(final TwitchChannelData data) {
	if (data != null && data.isOnline()) {
	    setOnline(data);
	} else if (data != null && !data.isOnline()) {
	    setOffline(data);
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
	isOnline.setValue(false);
	isPlaylist.setValue(data.isPlaylist());
	previewImage.setValue(defaultLogo);
	description.setValue(CHANNEL_IS_OFFLINE);
	availableQualities = new ArrayList<>();
    }

    private void setOnline(final TwitchChannelData data) {
	LOGGER.info("update {} with data {}", data.getName(), data.isOnline());
	name.setValue(data.getName());
	logoURL.setValue(data.getLogoURL());
	previewURL.setValue(data.getPreviewURL());
	game.setValue(data.getGame());
	title.setValue(data.getTitle());
	uptime.setValue(data.getUptime());
	String upTimeStringValue = buildUptimeString();
	uptimeString.setValue(upTimeStringValue);
	viewers.setValue(data.getViewers());
	viewersString.setValue(Integer.toString(getViewers().get()));
	if (data.isOnline() && !isOnline.get()) {
	    isOnline.setValue(true);
	    showOnlineNotification();
	} else if (!data.isOnline() && isOnline.get()) {
	    isOnline.setValue(false);
	}
	isPlaylist.setValue(data.isPlaylist());
	previewImage.setValue(data.getPreviewImage());
	description.setValue(getTitle().get());
	availableQualities = new ArrayList<>(data.getQualities());
    }

    private void showOnlineNotification() {
	final String nameString = this.name.get();
	final String gameString = this.game.get();
	final String titleString = this.title.get();
	if (nameString != null && gameString != null && titleString != null) {
	    Notifications.create().title("Channel Update")
		    .text(nameString + " just came online!\n The Game is " + gameString + ".\n" + titleString)
		    .darkStyle().showInformation();
	}
    }

    private String buildUptimeString() {
	return String.format("%02d:%02d:%02d Uptime", TimeUnit.MILLISECONDS.toHours(uptime.get()),
		TimeUnit.MILLISECONDS.toMinutes(uptime.get())
			- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(uptime.get())),
		TimeUnit.MILLISECONDS.toSeconds(uptime.get())
			- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(uptime.get())));
    }

    /**
     *
     * @return
     */
    public static Callback<IChannel, Observable[]> extractor() {
	return (IChannel sm) -> new Observable[] { ((TwitchChannel) sm).getName(), ((TwitchChannel) sm).getGame(),
		((TwitchChannel) sm).isOnline(), ((TwitchChannel) sm).getTitle(), ((TwitchChannel) sm).getDescription(),
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
     * @return the logoURL
     */
    public StringProperty getLogoURL() {
	return logoURL;
    }

    /**
     * @return the previewURL
     */
    public StringProperty getPreviewURL() {
	return previewURL;
    }

    /**
     * @return the game
     */
    public StringProperty getGame() {
	return game;
    }

    /**
     * @return the title
     */
    public StringProperty getTitle() {
	return title;
    }

    /**
     * @return the uptime
     */
    public LongProperty getUptime() {
	return uptime;
    }

    /**
     * @return the viewers
     */
    public IntegerProperty getViewers() {
	return viewers;
    }

    /**
     * @return the online
     */
    @Override
    public BooleanProperty isOnline() {
	return isOnline;
    }

    /**
     * @return the previewImage
     */
    public ObjectProperty<Image> getPreviewImage() {
	return previewImage;
    }

    public StringProperty getDescription() {
	return description;
    }

    /**
     * @return the availableQualities
     */
    @Override
    public List<String> getAvailableQualities() {
	if (availableQualities.isEmpty()) {
	    availableQualities.add(CHANNEL_IS_OFFLINE);
	}
	return availableQualities;
    }

    /**
     * @return the uptimeString
     */
    public StringProperty getUptimeString() {
	return uptimeString;
    }

    /**
     * @return the viewersString
     */
    public StringProperty getViewersString() {
	return viewersString;
    }

    /**
     * @return the isPlaylist
     */
    public BooleanProperty getIsPlaylist() {
	return isPlaylist;
    }

    /**
     * @param isPlaylist
     *            the isPlaylist to set
     */
    public void setIsPlaylist(BooleanProperty isPlaylist) {
	this.isPlaylist = isPlaylist;
    }
}
