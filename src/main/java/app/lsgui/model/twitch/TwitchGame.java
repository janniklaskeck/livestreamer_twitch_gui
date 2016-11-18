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

import app.lsgui.remote.twitch.TwitchAPIClient;
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
public final class TwitchGame implements ITwitchItem {

    private StringProperty name;
    private StringProperty viewers;
    private StringProperty channelCount;
    private ObjectProperty<Image> boxImage;

    private TwitchChannels channels;

    public TwitchGame(final String name, final int viewers, final int channelCount, final Image boxImage) {
        this.name = new SimpleStringProperty(shortenString(name));
        this.viewers = new SimpleStringProperty(Integer.toString(viewers));
        this.boxImage = new SimpleObjectProperty<>(boxImage);
        this.channelCount = new SimpleStringProperty(Integer.toString(channelCount));
    }

    public void loadChannelData() {
        this.channels = TwitchAPIClient.getInstance().getGameData(this.getName().get());
    }

    public void updateData(final TwitchGame updatedGame) {
        this.name = new SimpleStringProperty(shortenString(updatedGame.getName().get()));
        this.viewers = new SimpleStringProperty(updatedGame.getViewers().get());
        this.boxImage = new SimpleObjectProperty<>(updatedGame.getBoxImage().get());
        this.channelCount = new SimpleStringProperty(updatedGame.getChannelCount().get());
        this.loadChannelData();
    }

    public static String shortenString(final String input) {
        String result = input;
        final int maxLength = 20;
        if (input.length() > maxLength) {
            result = input.substring(0, maxLength - 1);
        }
        return result;
    }

    public TwitchChannels getChannels() {
        return this.channels;
    }

    public StringProperty getName() {
        return this.name;
    }

    public StringProperty getViewers() {
        return this.viewers;
    }

    public StringProperty getChannelCount() {
        return this.channelCount;
    }

    public ObjectProperty<Image> getBoxImage() {
        return this.boxImage;
    }

    @Override
    public boolean isTwitchGame() {
        return true;
    }

    @Override
    public boolean isTwitchChannel() {
        return false;
    }

}
