package app.lsgui.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.IChannel;
import app.lsgui.model.generic.GenericChannel;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class GenericService implements IService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericService.class);

    private StringProperty name;
    private StringProperty url;
    private ListProperty<IChannel> channelProperty;

    public GenericService() {
        name = new SimpleStringProperty();
        url = new SimpleStringProperty();
        channelProperty = new SimpleListProperty<>();
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
    public ListProperty<IChannel> getChannels() {
        return channelProperty;
    }

    @Override
    public void addChannel(final String channelName) {
        List<IChannel> channels = new ArrayList<>(getChannels().subList(0, getChannels().getSize()));
        final Callback<IChannel, Observable[]> extractor = GenericChannel.extractor();
        final IChannel channelToAdd = new GenericChannel(channelName);
        channels.add(channelToAdd);
        ObservableList<IChannel> obsChannels = FXCollections.observableArrayList(extractor);
        obsChannels.addAll(channels);
        getChannels().setValue(obsChannels);
    }

    @Override
    public void removeChannel(final IChannel channel) {
        if (channel != null) {
            LOGGER.debug("Remove Channel {} from list", channel.getName());
            final Callback<IChannel, Observable[]> extractor = GenericChannel.extractor();

            List<IChannel> channels = new ArrayList<>(getChannels().subList(0, getChannels().getSize()));
            channels.remove(channel);
            LOGGER.info("remove Channel {}", channel.getName());
            ObservableList<IChannel> obsChannels = FXCollections.observableArrayList(extractor);
            obsChannels.addAll(channels);
            getChannels().setValue(obsChannels);
        }

    }

}
