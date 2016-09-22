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

import java.util.Comparator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import app.lsgui.model.IChannel;
import app.lsgui.model.IService;
import app.lsgui.remote.twitch.TwitchAPIClient;
import app.lsgui.remote.twitch.TwitchChannelUpdateService;
import app.lsgui.utils.Settings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.SortedList;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public final class TwitchService implements IService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchService.class);
    public static final String TWITCH_ID = "twitch.tv";

    private StringProperty name;
    private StringProperty url;
    private ObjectProperty<SortedList<IChannel>> channelProperty;
    private ObservableList<IChannel> channelList = FXCollections.observableArrayList(TwitchChannel.extractor());
    private BooleanProperty sortChannels;

    private static final ObservableMap<IChannel, TwitchChannelUpdateService> UPDATESERVICES = FXCollections
            .observableHashMap();

    public TwitchService(final String name, final String url) {
        this.name = new SimpleStringProperty(name);
        this.url = new SimpleStringProperty(url);
        this.channelProperty = new SimpleObjectProperty<>(new SortedList<>(this.channelList));
        this.sortChannels = new SimpleBooleanProperty();
        this.sortChannels.bind(Settings.getInstance().getSortTwitch());
        this.sortChannels.addListener((observable, oldValue, newVale) -> this.changeComparator(newVale));
        this.channelProperty.get().addListener((ListChangeListener<IChannel>) change -> {
            change.next();
            this.changeComparator(this.sortChannels.get());
        });
    }

    @Override
    public void addChannel(final String name) {
        LOGGER.debug("Add Channel {} to {} Service", name, this.getName().get());
        final TwitchChannel channelToAdd = new TwitchChannel(new JsonObject(), name, false);
        final TwitchChannelUpdateService tcus = new TwitchChannelUpdateService(channelToAdd);
        tcus.start();
        UPDATESERVICES.put(channelToAdd, tcus);
        this.channelList.add(channelToAdd);
    }

    @Override
    public void removeChannel(final IChannel channel) {
        if (channel instanceof TwitchChannel) {
            LOGGER.debug("Remove Channel {} from Service {}", channel.getName(), this.getName().get());
            final TwitchChannelUpdateService tcus = UPDATESERVICES.remove(channel);
            tcus.cancel();
            this.channelList.remove(channel);
        }
    }

    public void addFollowedChannels(final String username) {
        LOGGER.debug("Import followed Streams for user {} into Service {}", username, this.getName().get());
        final Set<String> set = TwitchAPIClient.getInstance().getListOfFollowedStreams(username);
        for (final String s : set) {
            this.addChannel(s);
        }
    }

    private void changeComparator(final boolean doSorting) {
        final Comparator<IChannel> comparator;
        if (!doSorting) {
            comparator = (channel1, channel2) -> channel1.getName().get().compareToIgnoreCase(channel2.getName().get());
        } else {
            comparator = (channel1, channel2) -> {
                if (channel1.isOnline().get() && !channel2.isOnline().get()) {
                    return -1;
                } else if (!channel1.isOnline().get() && channel2.isOnline().get()) {
                    return 1;
                } else {
                    return channel1.getName().get().compareToIgnoreCase(channel2.getName().get());
                }
            };
        }
        this.getChannelProperty().get().setComparator(comparator);
    }

    @Override
    public ObjectProperty<SortedList<IChannel>> getChannelProperty() {
        return this.channelProperty;
    }

    @Override
    public StringProperty getName() {
        return this.name;
    }

    @Override
    public StringProperty getUrl() {
        return this.url;
    }

    public ObservableMap<IChannel, TwitchChannelUpdateService> getUpdateServices() {
        return UPDATESERVICES;
    }

}
