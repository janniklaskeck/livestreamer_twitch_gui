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
package app.lsgui.model.twitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import app.lsgui.remote.twitch.TwitchBrowserUpdateService;
import app.lsgui.utils.TwitchUtils;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

/**
 *
 * @author Niklas 26.06.2016
 *
 */
public final class TwitchChannels {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchChannels.class);

    private JsonObject jsonData;
    private ListProperty<ITwitchItem> channels = new SimpleListProperty<>(FXCollections.observableArrayList());

    public TwitchChannels(final JsonObject jsonData) {
        this.jsonData = jsonData;
        this.addGames();
    }

    private void addGames() {
        final JsonArray streams = this.jsonData.get("streams").getAsJsonArray();
        LOGGER.debug("Update {} channels", streams.size());
        for (final JsonElement element : streams) {
            final JsonObject object = element.getAsJsonObject();

            final JsonObject channelObject = object.get("channel").getAsJsonObject();
            final String name = channelObject.get("name").getAsString();

            final TwitchChannel channel = TwitchUtils.constructTwitchChannel(new JsonObject(), name, true);
            final TwitchBrowserUpdateService tcus = new TwitchBrowserUpdateService(channel);
            tcus.start();
            this.channels.add(channel);
        }
    }

    public ListProperty<ITwitchItem> getChannels() {
        return this.channels;
    }

}
