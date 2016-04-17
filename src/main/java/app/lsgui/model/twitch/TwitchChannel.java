package app.lsgui.model.twitch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.controlsfx.control.Notifications;
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
        this.previewImage = new SimpleObjectProperty<>(null);
        this.description = new SimpleStringProperty("Channel is offline");
        this.availableQualities = new SimpleListProperty<>();
        this.uptimeString = new SimpleStringProperty("");
        this.viewersString = new SimpleStringProperty("");
        this.availableQualities = new ArrayList<>();
    }

    public void updateData(final TwitchChannelData data) {
        if (data != null) {
            setOnline(data);
        } else {
            setOffline();
        }
    }

    private void setOffline() {
        name.setValue(null);
        logoURL.setValue(null);
        previewURL.setValue(null);
        game.setValue(null);
        title.setValue(null);
        uptime.setValue(null);
        viewers.setValue(null);
        isOnline.setValue(false);
        previewImage.setValue(defaultLogo);
        description.setValue(getOfflineString());
        availableQualities = new ArrayList<>();
        availableQualities.add("worst, best");
    }

    private void setOnline(final TwitchChannelData data) {
        LOGGER.info("update {} with data {}", data.getName(), data.isOnline());
        name.setValue(data.getName());
        logoURL.setValue(data.getLogoURL());
        previewURL.setValue(data.getPreviewURL());
        game.setValue(data.getGame());
        title.setValue(data.getTitle());
        uptime.setValue(data.getUptime());
        String upTimeStringValue = String.format("%02d:%02d:%02d Uptime", TimeUnit.MILLISECONDS.toHours(uptime.get()),
                TimeUnit.MILLISECONDS.toMinutes(uptime.get())
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(uptime.get())),
                TimeUnit.MILLISECONDS.toSeconds(uptime.get())
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(uptime.get())));
        uptimeString.setValue(upTimeStringValue);
        viewers.setValue(data.getViewers());
        viewersString.setValue(Integer.toString(getViewers().get()));
        if (data.isOnline() && !isOnline.get()) {
            isOnline.setValue(true);
            Notifications.create().title("Channel Update")
                    .text(name.get() + " just came online!\n The Game is " + game.get() + ".\n" + title.get())
                    .showInformation();
        } else if (!data.isOnline() && isOnline.get()) {
            isOnline.setValue(false);
        }
        previewImage.setValue(data.getPreviewImage());
        description.setValue(getTitle().get());
        availableQualities = new ArrayList<>(data.getQualities());
    }

    public static Callback<Channel, Observable[]> extractor() {
        return (Channel sm) -> new Observable[] { ((TwitchChannel) sm).getName(), ((TwitchChannel) sm).getGame(),
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
            availableQualities.add(getOfflineString());
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

    public final String getOfflineString() {
        return "Channel is offline";
    }

}
