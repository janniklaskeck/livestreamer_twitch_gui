package app.lsgui.model.twitch.game;

import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.model.twitch.channel.TwitchChannels;
import app.lsgui.rest.twitch.TwitchAPIClient;
import javafx.scene.image.Image;

/**
 *
 * @author Niklas 26.06.2016
 *
 */
public class TwitchGame implements ITwitchItem {

    private String name;
    private int viewers;
    private int channelCount;
    private Image boxImage;

    private TwitchChannels channels;

    public TwitchGame(final String name, final int viewers, final int channelCount, final Image boxImage) {
        this.name = name;
        this.viewers = viewers;
        this.boxImage = boxImage;
        this.channelCount = channelCount;
    }

    public void loadChannelData() {
        channels = TwitchAPIClient.getInstance().getGameData(getName());
    }

    public TwitchChannels getChannels() {
        return channels;
    }

    public String getName() {
        return name;
    }

    public int getViewers() {
        return viewers;
    }

    public int getChannelCount() {
        return channelCount;
    }

    public Image getBoxImage() {
        return boxImage;
    }

}
