package app.lsgui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.service.twitch.TwitchAPIClient;
import app.lsgui.service.twitch.TwitchChannelUpdateService;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.SortedList;

public class Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private StringProperty name;
    private StringProperty url;
    private ListProperty<Channel> channelProperty;
    private ObservableList<Channel> observableChannels;

    private static final ObservableMap<Channel, TwitchChannelUpdateService> UPDATESERVICES = FXCollections
            .observableHashMap();

    public Service(String name, String url) {
        this.name = new SimpleStringProperty(name);
        this.url = new SimpleStringProperty(url);
        this.channelProperty = new SimpleListProperty<>();
        this.observableChannels = FXCollections.observableArrayList();
    }

    public void addChannel(final String name) {
        LOGGER.debug("Add Channel to List");
        List<Channel> channels = new ArrayList<>(getChannels().subList(0, getChannels().getSize()));
        Channel sm = new TwitchChannel(name);
        channels.add(sm);
        ObservableList<Channel> obsChannels = FXCollections.observableArrayList(TwitchChannel.extractor());
        obsChannels.addAll(channels);
        SortedList<Channel> obsChannelsSorted = new SortedList<>(obsChannels);
        obsChannelsSorted.setComparator((ch1, ch2) -> {
            if (ch1.isOnline().get() && !ch2.isOnline().get()) {
                return -1;
            } else if (!ch1.isOnline().get() && ch2.isOnline().get()) {
                return 1;
            }
            return 0;
        });

        getChannels().setValue(obsChannelsSorted);

        final TwitchChannelUpdateService tcus = new TwitchChannelUpdateService(sm);
        tcus.start();
        UPDATESERVICES.put(sm, tcus);
    }

    public void removeSelectedChannel(final Channel selectedStream) {
        if (selectedStream != null) {
            LOGGER.debug("Remove Channel {} from list", selectedStream.getName());
            List<Channel> channels = getChannels().subList(0, getChannels().getSize());
            channels.remove(selectedStream);
            ObservableList<Channel> obsChannels = FXCollections.observableArrayList(TwitchChannel.extractor());
            obsChannels.addAll(channels);
            SortedList<Channel> obsChannelsSorted = new SortedList<>(obsChannels);
            obsChannelsSorted.setComparator((ch1, ch2) -> {
                if (ch1.isOnline().get() && !ch2.isOnline().get()) {
                    return -1;
                } else if (!ch1.isOnline().get() && ch2.isOnline().get()) {
                    return 1;
                }
                return 0;
            });
            getChannels().setValue(obsChannelsSorted);

            final TwitchChannelUpdateService tcus = UPDATESERVICES.remove(selectedStream);
            tcus.cancel();
        }
    }

    public void addFollowedChannels(final String username) {
        LOGGER.debug("Import followed Streams for user {}", username);

        Set<String> set = TwitchAPIClient.instance().getListOfFollowedStreams(username);
        List<Channel> channels = new ArrayList<>();
        for (String s : set) {
            channels.add(new TwitchChannel(s));
        }
        observableChannels = FXCollections.observableArrayList(channels);
        ObservableList<Channel> obsChannels = FXCollections.observableArrayList(TwitchChannel.extractor());
        obsChannels.addAll(channels);
        SortedList<Channel> obsChannelsSorted = new SortedList<>(obsChannels);
        obsChannelsSorted.setComparator((ch1, ch2) -> {
            if (ch1.isOnline().get() && !ch2.isOnline().get()) {
                return -1;
            } else if (!ch1.isOnline().get() && ch2.isOnline().get()) {
                return 1;
            }
            return 0;
        });
        getChannels().setValue(obsChannelsSorted);
        // TODO Find better Solution

        for (Channel c : observableChannels) {
            final TwitchChannelUpdateService tcus = new TwitchChannelUpdateService(c);
            tcus.start();
            UPDATESERVICES.put(c, tcus);
        }
    }

    public ListProperty<Channel> getChannels() {
        return channelProperty;
    }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getUrl() {
        return url;
    }

    public static ObservableMap<Channel, TwitchChannelUpdateService> getUpdateServices() {
        return UPDATESERVICES;
    }

}
