package app.lsgui.model.channel.twitch;

import java.util.LinkedHashMap;
import java.util.Map;

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

    private JsonObject jsonData;
    private Map<String, Image> gamesMap;

    /**
     *
     * @param jsonData
     */
    public TwitchGames(final JsonObject jsonData) {
        this.jsonData = jsonData;
        this.gamesMap = new LinkedHashMap<>();
        this.addGamesToMap();
    }

    private void addGamesToMap() {
        final JsonArray top = this.jsonData.get("top").getAsJsonArray();
        for (final JsonElement element : top) {
            final JsonObject object = element.getAsJsonObject();
            final JsonObject game = object.get("game").getAsJsonObject();
            final String gameName = game.get("name").getAsString();
            final JsonObject box = game.get("box").getAsJsonObject(); 
            final String imageUrl = box.get("medium").getAsString();
            final Image boxImage = new Image(imageUrl);
            gamesMap.put(gameName, boxImage);
        }
        
    }

    public void updateData(final TwitchGames updatedGames) {

    }

}
