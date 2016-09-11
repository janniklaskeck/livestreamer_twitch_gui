package app.lsgui.rest.twitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
import app.lsgui.model.twitch.channel.TwitchChannel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class TwitchBrowserUpdateService extends Service<TwitchChannel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchBrowserUpdateService.class);
    private static final ListProperty<IChannel> ACTIVE_LIST = new SimpleListProperty<>(
            FXCollections.observableArrayList());
    private TwitchChannel channel;

    public TwitchBrowserUpdateService(final TwitchChannel model) {
        this.channel = model;
        setUpChannel();
    }

    public final void setUpChannel() {
        setOnSucceeded(event -> {
            final TwitchChannel updatedChannel = (TwitchChannel) event.getSource().getValue();
            if (updatedChannel != null) {
                synchronized (this.channel) {
                    this.channel.updateData(updatedChannel, false);
                }
            }
            synchronized (ACTIVE_LIST) {
                ObservableList<IChannel> activeChannelServices = FXCollections.observableArrayList(ACTIVE_LIST.get());
                activeChannelServices.remove(channel);
                ACTIVE_LIST.set(activeChannelServices);
            }
        });
        setOnFailed(event -> LOGGER.warn("UPDATE SERVICE FAILED"));
    }

    @Override
    protected Task<TwitchChannel> createTask() {
        return new Task<TwitchChannel>() {
            @Override
            protected TwitchChannel call() throws Exception {
                synchronized (ACTIVE_LIST) {
                    ACTIVE_LIST.set(addAndGetChannelToList(channel, ACTIVE_LIST));
                }
                return TwitchAPIClient.getInstance().getStreamData(channel.getName().get());
            }
        };
    }

    private static ObservableList<IChannel> addAndGetChannelToList(final IChannel channel,
            final ObservableList<IChannel> list) {
        final ObservableList<IChannel> activeChannelServices = FXCollections.observableArrayList(list);
        activeChannelServices.add(channel);
        return activeChannelServices;
    }

    public static ListProperty<IChannel> getActiveChannelServicesProperty() {
        return ACTIVE_LIST;
    }

}
