package app.lsgui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.service.twitch.TwitchChannelUpdateService;
import app.lsgui.service.twitch.TwitchAPIClient;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private StringProperty name;
    private StringProperty url;
    private ListProperty<Channel> channels;
    private ObservableList<Channel> streams;

    public final static ObservableMap<Channel, TwitchChannelUpdateService> UPDATESERVICES = FXCollections
            .observableHashMap();

    public Service(String name, String url) {
        this.name = new SimpleStringProperty(name);
        this.url = new SimpleStringProperty(url);
        this.channels = new SimpleListProperty<Channel>();
        this.streams = FXCollections.observableArrayList();
    }

    public void addStream(final String name) {
        LOGGER.debug("Add Stream to List");
        List<Channel> streams = new ArrayList<Channel>(getChannels().subList(0, getChannels().getSize()));
        Channel sm = new TwitchChannel(name);
        streams.add(sm);
        ObservableList<Channel> obsStreams = FXCollections.observableArrayList(TwitchChannel.extractor());
        obsStreams.addAll(streams);
        getChannels().setValue(obsStreams);

        final TwitchChannelUpdateService tcus = new TwitchChannelUpdateService(sm);
        tcus.start();
        UPDATESERVICES.put(sm, tcus);
    }

    public void removeSelectedStream(final Channel selectedStream) {
        if (selectedStream != null) {
            LOGGER.debug("Remove stream {} from list", selectedStream.getName());
            List<Channel> streams = getChannels().subList(0, getChannels().getSize());
            streams.remove(selectedStream);
            ObservableList<Channel> obsStreams = FXCollections.observableArrayList(TwitchChannel.extractor());
            obsStreams.addAll(streams);
            getChannels().setValue(obsStreams);

            final TwitchChannelUpdateService tcus = UPDATESERVICES.remove(selectedStream);
            tcus.cancel();
        }
    }

    public void addFollowedStreams(final String username) {
        LOGGER.debug("Import followed Streams for user {}", username);

        Set<String> set = TwitchAPIClient.instance().getListOfFollowedStreams(username);
        List<Channel> list = new ArrayList<Channel>();
        for (String s : set) {
            list.add(new TwitchChannel(s));
        }
        streams = FXCollections.observableArrayList(list);
        ObservableList<Channel> obsStreams = FXCollections.observableArrayList(TwitchChannel.extractor());
        obsStreams.addAll(streams);
        getChannels().setValue(obsStreams);

        for (Channel sm : streams) {
            final TwitchChannelUpdateService tcus = new TwitchChannelUpdateService(sm);
            tcus.start();
            UPDATESERVICES.put(sm, tcus);
        }
    }

    public ListProperty<Channel> getChannels() {
        return channels;
    }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getUrl() {
        return url;
    }

}
