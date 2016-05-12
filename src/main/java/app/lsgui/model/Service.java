package app.lsgui.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.generic.GenericChannel;
import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.service.twitch.TwitchAPIClient;
import app.lsgui.service.twitch.TwitchChannelUpdateService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private BooleanProperty sortChannels;
    private Comparator<Channel> comp;

    private static final ObservableMap<Channel, TwitchChannelUpdateService> UPDATESERVICES = FXCollections
            .observableHashMap();

    public Service(String name, String url) {
        this.name = new SimpleStringProperty(name);
        this.url = new SimpleStringProperty(url);
        channelProperty = new SimpleListProperty<>();
        sortChannels = new SimpleBooleanProperty();
    }

    public void bindSortProperty(final BooleanProperty property) {
        if (!sortChannels.isBound()) {
            sortChannels.bind(property);
            sortChannels.addListener((obs, oldValue, newValue) -> {
                changeComparator(newValue);
                refreshList();
                LOGGER.info("Change Sorting method and refresh list");
            });
        }
    }

    private void refreshList() {
        if (getChannels().getValue() != null) {
            if (this.getName().get().toLowerCase().contains("twitch")) {
                ObservableList<Channel> obsChannels = FXCollections.observableArrayList(TwitchChannel.extractor());
                obsChannels.addAll(getChannels().getValue());
                SortedList<Channel> obsChannelsSorted = new SortedList<>(obsChannels);
                obsChannelsSorted.setComparator(comp);
                getChannels().setValue(obsChannelsSorted);
            } else {
                ObservableList<Channel> obsChannels = FXCollections.observableArrayList(GenericChannel.extractor());
                obsChannels.addAll(getChannels().getValue());
                SortedList<Channel> obsChannelsSorted = new SortedList<>(obsChannels);
                obsChannelsSorted.setComparator(comp);
                getChannels().setValue(obsChannelsSorted);
            }
        }
    }

    public void addChannel(final String name) {
        List<Channel> channels = new ArrayList<>(getChannels().subList(0, getChannels().getSize()));
        if (this.getName().get().toLowerCase().contains("twitch")) {
            Channel sm = new TwitchChannel(name);
            channels.add(sm);
            ObservableList<Channel> obsChannels = FXCollections.observableArrayList(TwitchChannel.extractor());
            obsChannels.addAll(channels);
            SortedList<Channel> obsChannelsSorted = new SortedList<>(obsChannels);
            obsChannelsSorted.setComparator(comp);
            getChannels().setValue(obsChannelsSorted);
            final TwitchChannelUpdateService tcus = new TwitchChannelUpdateService(sm);
            tcus.start();
            UPDATESERVICES.put(sm, tcus);
        } else {
            Channel sm = new GenericChannel(name);
            channels.add(sm);
            ObservableList<Channel> obsChannels = FXCollections.observableArrayList(GenericChannel.extractor());
            obsChannels.addAll(channels);
            SortedList<Channel> obsChannelsSorted = new SortedList<>(obsChannels);
            obsChannelsSorted.setComparator(comp);
            getChannels().setValue(obsChannelsSorted);
        }

    }

    public void removeSelectedChannel(final Channel selectedChannel) {
        if (selectedChannel != null) {
            LOGGER.debug("Remove Channel {} from list", selectedChannel.getName());
            List<Channel> channels = new ArrayList<>(getChannels().subList(0, getChannels().getSize()));
            channels.remove(selectedChannel);
            LOGGER.info("remove Channel {}", selectedChannel.getName());
            ObservableList<Channel> obsChannels = FXCollections.observableArrayList(TwitchChannel.extractor());
            obsChannels.addAll(channels);
            SortedList<Channel> obsChannelsSorted = new SortedList<>(obsChannels);
            obsChannelsSorted.setComparator(comp);
            getChannels().setValue(obsChannelsSorted);
            if (this.getUrl().get().toLowerCase().contains("twitch")) {
                final TwitchChannelUpdateService tcus = UPDATESERVICES.remove(selectedChannel);
                tcus.cancel();
            }
        }
    }

    public void addFollowedChannels(final String username) {
        LOGGER.debug("Import followed Streams for user {}", username);
        final Set<String> set = TwitchAPIClient.instance().getListOfFollowedStreams(username);
        for (String s : set) {
            addChannel(s);
        }
    }

    private void changeComparator(boolean doSorting) {
        if (!doSorting) {
            comp = (ch1, ch2) -> ch1.getName().get().compareTo(ch2.getName().get());
        } else {
            comp = (ch1, ch2) -> {
                if (ch1.isOnline().get() && !ch2.isOnline().get()) {
                    return -1;
                } else if (!ch1.isOnline().get() && ch2.isOnline().get()) {
                    return 1;
                }
                return 0;
            };
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
