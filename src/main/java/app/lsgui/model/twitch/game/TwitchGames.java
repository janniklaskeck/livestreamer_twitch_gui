package app.lsgui.model.twitch.game;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javafx.scene.image.Image;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class TwitchGames {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchGames.class);

    private JsonObject jsonData;
    private List<TwitchGame> games;

    /**
     *
     * @param jsonData
     */
    public TwitchGames(final JsonObject jsonData) {
        this.jsonData = jsonData;
        this.games = new ArrayList<>();
        this.addGames();
    }

    /**
     * Empty Constructor
     */
    public TwitchGames() {
        this.games = new ArrayList<>();
    }

    private void addGames() {
        final JsonArray top = this.jsonData.get("top").getAsJsonArray();
        LOGGER.debug("Update {} games", top.size());
        for (final JsonElement element : top) {
            final JsonObject object = element.getAsJsonObject();
            final int viewers = object.get("viewers").getAsInt();
            final JsonObject game = object.get("game").getAsJsonObject();
            final String gameName = game.get("name").getAsString();
            final JsonObject box = game.get("box").getAsJsonObject();
            final String imageUrl = box.get("large").getAsString();
            final Image boxImage = new Image(imageUrl, true);
            games.add(new TwitchGame(gameName, viewers, boxImage));
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

    public List<TwitchGame> getGames() {
        return games;
    }

}
