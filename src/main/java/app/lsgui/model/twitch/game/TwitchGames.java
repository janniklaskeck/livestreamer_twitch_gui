package app.lsgui.model.twitch.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import app.lsgui.model.twitch.ITwitchItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class TwitchGames {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchGames.class);

    private JsonObject jsonData;
    private ObservableList<ITwitchItem> games = FXCollections.observableArrayList();

    /**
     *
     * @param jsonData
     */
    public TwitchGames(final JsonObject jsonData) {
        this.jsonData = jsonData;
        this.games = FXCollections.observableArrayList();
        this.addGames();
    }

    private void addGames() {
        final JsonElement topElement = this.jsonData.get("top");
        if (topElement != null) {
            final JsonArray topArray = topElement.getAsJsonArray();
            LOGGER.debug("Update {} games", topArray.size());
            for (final JsonElement element : topArray) {
                final JsonObject object = element.getAsJsonObject();
                final int viewers = object.get("viewers").getAsInt();
                final int channels = object.get("channels").getAsInt();

                final JsonObject game = object.get("game").getAsJsonObject();
                final String gameName = game.get("name").getAsString();

                final JsonObject box = game.get("box").getAsJsonObject();
                final String imageUrl = box.get("large").getAsString();
                final Image boxImage = new Image(imageUrl, true);

                games.add(new TwitchGame(gameName, viewers, channels, boxImage));
            }
        }
    }

    /**
     *
     * @param updatedGames
     */
    public void updateData(final TwitchGames updatedGames) {
        LOGGER.debug("Update Twitch Games Data");
        this.games.clear();
        this.games.addAll(updatedGames.getGames());
    }

    public ObservableList<ITwitchItem> getGames() {
        return games;
    }

}
