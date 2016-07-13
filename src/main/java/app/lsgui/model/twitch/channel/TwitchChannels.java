package app.lsgui.model.twitch.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import app.lsgui.model.twitch.game.TwitchGames;
import app.lsgui.rest.twitch.TwitchAPIClient;
import app.lsgui.rest.twitch.TwitchChannelData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Niklas 26.06.2016
 *
 */
public class TwitchChannels {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchGames.class);

    private JsonObject jsonData;
    private ObservableList<TwitchChannel> channels;

    /**
     *
     * @param jsonData
     */
    public TwitchChannels(final JsonObject jsonData) {
        this.jsonData = jsonData;
        this.channels = FXCollections.observableArrayList();
        this.addGames();
    }

    /**
     * Empty Constructor
     */
    public TwitchChannels() {
        this.channels = FXCollections.observableArrayList();
    }

    private void addGames() {
        final JsonArray streams = this.jsonData.get("streams").getAsJsonArray();
        LOGGER.debug("Update {} channels", streams.size());
        for (final JsonElement element : streams) {
            final JsonObject object = element.getAsJsonObject();

            final JsonObject channelObject = object.get("channel").getAsJsonObject();
            final String name = channelObject.get("name").getAsString();

            final TwitchChannelData channelData = TwitchAPIClient.getInstance().getStreamData(name);
            final TwitchChannel channel = new TwitchChannel(name);
            channel.updateData(channelData);
            channels.add(channel);
        }
    }

    /**
     *
     * @param updatedGames
     */
    public void updateData(final TwitchChannels updatedGames) {
        LOGGER.debug("Update Twitch Channels Data");
        this.channels.clear();
        this.channels.addAll(updatedGames.getChannels());
    }

    public ObservableList<TwitchChannel> getChannels() {
        return channels;
    }

}
