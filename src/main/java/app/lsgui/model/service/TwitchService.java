package app.lsgui.model.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
import app.lsgui.model.generic.channel.GenericChannel;
import app.lsgui.model.twitch.channel.TwitchChannel;
import app.lsgui.rest.twitch.TwitchAPIClient;
import app.lsgui.rest.twitch.TwitchChannelUpdateService;
import javafx.beans.Observable;
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
import javafx.util.Callback;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class TwitchService implements IService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchService.class);
    public static final String TWITCH_ID = "twitch.tv";

    private StringProperty name;
    private StringProperty url;
    private ListProperty<IChannel> channelProperty;
    private BooleanProperty sortChannels;
    private Comparator<IChannel> comp;

    private static final ObservableMap<IChannel, TwitchChannelUpdateService> UPDATESERVICES = FXCollections
            .observableHashMap();

    /**
     *
     * @param name
     * @param url
     */
    public TwitchService(String name, String url) {
        this.name = new SimpleStringProperty(name);
        this.url = new SimpleStringProperty(url);
        channelProperty = new SimpleListProperty<>();
        sortChannels = new SimpleBooleanProperty();
    }

    /**
     *
     * @param property
     */
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
            final Callback<IChannel, Observable[]> extractor;
            if (this.getUrl().get().toLowerCase().contains(TWITCH_ID)) {
                extractor = TwitchChannel.extractor();
            } else {
                extractor = GenericChannel.extractor();
            }
            ObservableList<IChannel> obsChannels = FXCollections.observableArrayList(extractor);
            obsChannels.addAll(getChannels().getValue());
            SortedList<IChannel> obsChannelsSorted = new SortedList<>(obsChannels);
            obsChannelsSorted.setComparator(comp);
            getChannels().setValue(obsChannelsSorted);
        }
    }

    @Override
    public void addChannel(final String name) {
        List<IChannel> channels = new ArrayList<>(getChannels().subList(0, getChannels().getSize()));
        final IChannel channelToAdd;
        final Callback<IChannel, Observable[]> extractor;
        if (this.getUrl().get().toLowerCase().contains(TWITCH_ID)) {
            channelToAdd = new TwitchChannel(name);
            extractor = TwitchChannel.extractor();
            final TwitchChannelUpdateService tcus = new TwitchChannelUpdateService(channelToAdd);
            tcus.start();
            UPDATESERVICES.put(channelToAdd, tcus);
        } else {
            channelToAdd = new GenericChannel(name);
            extractor = GenericChannel.extractor();
        }
        channels.add(channelToAdd);
        ObservableList<IChannel> obsChannels = FXCollections.observableArrayList(extractor);
        obsChannels.addAll(channels);
        SortedList<IChannel> obsChannelsSorted = new SortedList<>(obsChannels);
        obsChannelsSorted.setComparator(comp);
        getChannels().setValue(obsChannelsSorted);
    }

    @Override
    public void removeChannel(final IChannel channel) {
        if (channel != null) {
            LOGGER.debug("Remove Channel {} from list", channel.getName());
            final Callback<IChannel, Observable[]> extractor;
            if (channel.getClass().equals(TwitchChannel.class)
                    && this.getUrl().get().toLowerCase().contains(TWITCH_ID)) {
                extractor = TwitchChannel.extractor();
                final TwitchChannelUpdateService tcus = UPDATESERVICES.remove(channel);
                tcus.cancel();
            } else {
                extractor = GenericChannel.extractor();
            }
            List<IChannel> channels = new ArrayList<>(getChannels().subList(0, getChannels().getSize()));
            channels.remove(channel);
            LOGGER.info("remove Channel {}", channel.getName());
            ObservableList<IChannel> obsChannels = FXCollections.observableArrayList(extractor);
            obsChannels.addAll(channels);
            SortedList<IChannel> obsChannelsSorted = new SortedList<>(obsChannels);
            obsChannelsSorted.setComparator(comp);
            getChannels().setValue(obsChannelsSorted);
        }
    }

    /**
     *
     * @param username
     */
    public void addFollowedChannels(final String username) {
        LOGGER.info("Add followed Channels if it is a Twitch.tv Channel");
        if (this.getUrl().get().toLowerCase().contains(TWITCH_ID)) {
            LOGGER.debug("Import followed Streams for user {}", username);
            final Set<String> set = TwitchAPIClient.getInstance().getListOfFollowedStreams(username);
            for (final String s : set) {
                addChannel(s);
            }
        } else {
            LOGGER.info("{} is no Twitch.tv Channel", username);
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

    @Override
    public ListProperty<IChannel> getChannels() {
        return channelProperty;
    }

    @Override
    public StringProperty getName() {
        return name;
    }

    @Override
    public StringProperty getUrl() {
        return url;
    }

    public ObservableMap<IChannel, TwitchChannelUpdateService> getUpdateServices() {
        return UPDATESERVICES;
    }

}
