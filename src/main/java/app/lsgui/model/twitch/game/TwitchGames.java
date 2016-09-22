/**
 * MIT License
 *
 * Copyright (c) 2016 Jan-Niklas Keck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
public final class TwitchGames {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchGames.class);

    private JsonObject jsonData;
    private ObservableList<ITwitchItem> games = FXCollections.observableArrayList();

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

                this.games.add(new TwitchGame(gameName, viewers, channels, boxImage));
            }
        }
    }

    public void updateData(final TwitchGames updatedGames) {
        LOGGER.debug("Update Twitch Games Data");
        this.games.clear();
        this.games.addAll(updatedGames.getGames());
    }

    public ObservableList<ITwitchItem> getGames() {
        return this.games;
    }

}
