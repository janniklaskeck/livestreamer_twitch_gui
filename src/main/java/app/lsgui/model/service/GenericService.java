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
package app.lsgui.model.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
import app.lsgui.model.generic.channel.GenericChannel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public final class GenericService implements IService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericService.class);

    private StringProperty name;
    private StringProperty url;
    private ObjectProperty<SortedList<IChannel>> channelProperty;
    private ObservableList<IChannel> channelList = FXCollections.observableArrayList(GenericChannel.extractor());

    public GenericService(final String name, final String url) {
        this.name = new SimpleStringProperty(name);
        this.url = new SimpleStringProperty(url);
        this.channelProperty = new SimpleObjectProperty<>(new SortedList<>(this.channelList));
    }

    @Override
    public StringProperty getName() {
        return this.name;
    }

    @Override
    public StringProperty getUrl() {
        return this.url;
    }

    @Override
    public ObjectProperty<SortedList<IChannel>> getChannelProperty() {
        return this.channelProperty;
    }

    @Override
    public void addChannel(final String channelName) {
        LOGGER.debug("Adding Channel {} to Service {}", channelName, this.getName().get());
        final IChannel channelToAdd = new GenericChannel(channelName);
        this.channelList.add(channelToAdd);
    }

    @Override
    public void removeChannel(final IChannel channel) {
        if (channel instanceof GenericChannel) {
            LOGGER.debug("Remove Channel {} from list", channel.getName());
            this.channelList.remove(channel);
        }
    }
}
