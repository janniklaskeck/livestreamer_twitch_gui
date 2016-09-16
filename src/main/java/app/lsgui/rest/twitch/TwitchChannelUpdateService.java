package app.lsgui.rest.twitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.channel.TwitchChannel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class TwitchChannelUpdateService extends ScheduledService<TwitchChannel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchChannelUpdateService.class);
    private static final ListProperty<TwitchChannel> ACTIVE_LIST = new SimpleListProperty<>(
            FXCollections.observableArrayList());
    private TwitchChannel channel;

    public TwitchChannelUpdateService(final TwitchChannel channel) {
        LOGGER.debug("Create UpdateService for {}", channel.getName().get());
        this.channel = channel;
        setUpConstant();
    }

    public final void setUpConstant() {
        setPeriod(Duration.seconds(40));
        setRestartOnFailure(true);
        setOnSucceeded(event -> {
            final TwitchChannel updatedModel = (TwitchChannel) event.getSource().getValue();
            if (updatedModel != null) {
                synchronized (this.channel) {
                    this.channel.updateData(updatedModel, true);
                }
            }
            synchronized (ACTIVE_LIST) {
                ObservableList<TwitchChannel> activeChannelServices = FXCollections
                        .observableArrayList(ACTIVE_LIST.get());
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
                return TwitchAPIClient.getInstance().getStreamData(channel.getName().get(), false);
            }
        };
    }

    private static ObservableList<TwitchChannel> addAndGetChannelToList(final TwitchChannel channel,
            final ObservableList<TwitchChannel> list) {
        final ObservableList<TwitchChannel> activeChannelServices = FXCollections.observableArrayList(list);
        activeChannelServices.add(channel);
        return activeChannelServices;
    }

    public static ListProperty<TwitchChannel> getActiveChannelServicesProperty() {
        return ACTIVE_LIST;
    }
}
