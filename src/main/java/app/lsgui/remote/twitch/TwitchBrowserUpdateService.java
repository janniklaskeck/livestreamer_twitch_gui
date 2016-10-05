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
package app.lsgui.remote.twitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.utils.TwitchUtils;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public final class TwitchBrowserUpdateService extends Service<TwitchChannel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchBrowserUpdateService.class);
    private static final ListProperty<TwitchChannel> ACTIVE_LIST = new SimpleListProperty<>(
            FXCollections.observableArrayList());
    private TwitchChannel channel;

    public TwitchBrowserUpdateService(final TwitchChannel model) {
        this.channel = model;
        this.setUpChannel();
    }

    public void setUpChannel() {
        setOnSucceeded(event -> {
            final TwitchChannel updatedChannel = (TwitchChannel) event.getSource().getValue();
            if (updatedChannel != null) {
                synchronized (this.channel) {
                    this.channel.updateData(updatedChannel, false);
                }
            }
            ACTIVE_LIST.remove(this.channel);
        });
        setOnFailed(event -> LOGGER.warn("UPDATE SERVICE FAILED"));
    }

    @Override
    protected Task<TwitchChannel> createTask() {
        return new Task<TwitchChannel>() {
            @Override
            protected TwitchChannel call() throws Exception {
                TwitchUtils.addChannelToList(ACTIVE_LIST, channel);
                return TwitchAPIClient.getInstance().getStreamData(channel.getName().get(), true);
            }
        };
    }

    public static ListProperty<TwitchChannel> activeServicesProperty() {
        return TwitchBrowserUpdateService.ACTIVE_LIST;
    }

}
