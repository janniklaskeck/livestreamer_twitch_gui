package app.lsgui.model.twitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.StreamModel;
import app.lsgui.service.twitch.TwitchStreamData;
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

public class TwitchStreamModel implements StreamModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchStreamModel.class);
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
    private IntegerProperty viewers;
    private BooleanProperty online;
    private ObjectProperty<Image> previewImage;

    public TwitchStreamModel(final String name) {
        this.name = new SimpleStringProperty(name);
        this.logoURL = new SimpleStringProperty("");
        this.previewURL = new SimpleStringProperty("");
        this.game = new SimpleStringProperty("");
        this.title = new SimpleStringProperty("");
        this.uptime = new SimpleLongProperty(0L);
        this.viewers = new SimpleIntegerProperty(0);
        this.online = new SimpleBooleanProperty(false);
        this.previewImage = new SimpleObjectProperty<Image>(null);
        this.description = new SimpleStringProperty("Stream is offline");
    }

    public void updateData(final TwitchStreamData data) {
        if (data != null) {
            LOGGER.info("update {} with data {}", data.getName(), data.isOnline());
            this.name.setValue(data.getName());
            this.logoURL.setValue(data.getLogoURL());
            this.previewURL.setValue(data.getPreviewURL());
            this.game.setValue(data.getGame());
            this.title.setValue(data.getTitle());
            this.uptime.setValue(data.getUptime());
            this.viewers.setValue(data.getViewers());
            this.online.setValue(data.isOnline());
            this.previewImage.setValue(data.getPreviewImage());
            this.description.setValue(getTitle().get() + " Viewers: " + getViewers().get() + "\n" + getGame().get());
        } else {
            this.name.setValue(null);
            this.logoURL.setValue(null);
            this.previewURL.setValue(null);
            this.game.setValue(null);
            this.title.setValue(null);
            this.uptime.setValue(null);
            this.viewers.setValue(null);
            this.online.setValue(false);
            this.previewImage.setValue(null);
            this.description.setValue("Stream is offline");
        }
    }

    public static Callback<StreamModel, Observable[]> extractor() {
        return (StreamModel sm) -> new Observable[] { ((TwitchStreamModel) sm).getName(),
                ((TwitchStreamModel) sm).getGame(), ((TwitchStreamModel) sm).getOnline(),
                ((TwitchStreamModel) sm).getTitle(), ((TwitchStreamModel) sm).getDescription(),
                ((TwitchStreamModel) sm).getLogoURL(), ((TwitchStreamModel) sm).getPreviewImage(),
                ((TwitchStreamModel) sm).getPreviewURL(), ((TwitchStreamModel) sm).getUptime(),
                ((TwitchStreamModel) sm).getViewers() };
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
    public BooleanProperty getOnline() {
        return online;
    }

    /**
     * @param online
     *            the online to set
     */
    public void setOnline(BooleanProperty online) {
        this.online = online;
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

}
