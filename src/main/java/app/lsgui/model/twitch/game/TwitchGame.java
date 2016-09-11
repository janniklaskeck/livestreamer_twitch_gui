package app.lsgui.model.twitch.game;

import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.model.twitch.channel.TwitchChannels;
import app.lsgui.rest.twitch.TwitchAPIClient;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

/**
 *
 * @author Niklas 26.06.2016
 *
 */
public class TwitchGame implements ITwitchItem {

    private StringProperty name;
    private StringProperty viewers;
    private StringProperty channelCount;
    private ObjectProperty<Image> boxImage;

    private TwitchChannels channels;

    public TwitchGame(final String name, final int viewers, final int channelCount, final Image boxImage) {
        this.name = new SimpleStringProperty(name);
        this.viewers = new SimpleStringProperty(Integer.toString(viewers));
        this.boxImage = new SimpleObjectProperty<>(boxImage);
        this.channelCount = new SimpleStringProperty(Integer.toString(channelCount));
    }

    public void loadChannelData() {
        channels = TwitchAPIClient.getInstance().getGameData(getName().get());
    }

    public TwitchChannels getChannels() {
        return channels;
    }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getViewers() {
        return viewers;
    }

    public StringProperty getChannelCount() {
        return channelCount;
    }

    public ObjectProperty<Image> getBoxImage() {
        return boxImage;
    }

    public void updateData(final TwitchGame updatedGame) {
        this.name = new SimpleStringProperty(updatedGame.getName().get());
        this.viewers = new SimpleStringProperty(updatedGame.getViewers().get());
        this.boxImage = new SimpleObjectProperty<>(updatedGame.getBoxImage().get());
        this.channelCount = new SimpleStringProperty(updatedGame.getChannelCount().get());
        loadChannelData();
    }

}
