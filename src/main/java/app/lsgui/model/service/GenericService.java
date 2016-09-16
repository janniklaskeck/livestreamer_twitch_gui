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
public class GenericService implements IService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericService.class);

    private StringProperty name;
    private StringProperty url;
    private ObjectProperty<SortedList<IChannel>> channelProperty;
    private ObservableList<IChannel> channelList = FXCollections.observableArrayList(GenericChannel.extractor());

    public GenericService(final String name, final String url) {
        this.name = new SimpleStringProperty(name);
        this.url = new SimpleStringProperty(url);
        channelProperty = new SimpleObjectProperty<>(new SortedList<>(channelList));
    }

    @Override
    public StringProperty getName() {
        return name;
    }

    @Override
    public StringProperty getUrl() {
        return url;
    }

    @Override
    public ObjectProperty<SortedList<IChannel>> getChannelProperty() {
        return channelProperty;
    }

    @Override
    public void addChannel(final String channelName) {
        LOGGER.debug("Adding Channel {} to Service {}", channelName, this.getName().get());
        final IChannel channelToAdd = new GenericChannel(channelName);
        channelList.add(channelToAdd);
    }

    @Override
    public void removeChannel(final IChannel channel) {
        if (channel instanceof GenericChannel) {
            LOGGER.debug("Remove Channel {} from list", channel.getName());
            channelList.remove(channel);
        }
    }
}
