package app.lsgui.model.twitch.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.rest.twitch.TwitchChannelUpdateService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Niklas 26.06.2016
 *
 */
public class TwitchChannels {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchChannels.class);

    private JsonObject jsonData;
    private ObservableList<ITwitchItem> channels;

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

            final TwitchChannel channel = new TwitchChannel(name);
            final TwitchChannelUpdateService tcus = new TwitchChannelUpdateService(channel, true);
            tcus.start();
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

    public ObservableList<ITwitchItem> getChannels() {
        return channels;
    }

}
