package app.lsgui.rest.twitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
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
public class TwitchChannelUpdateService extends ScheduledService<TwitchChannelData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchChannelUpdateService.class);
    private static final ListProperty<IChannel> ACTIVE_LIST = new SimpleListProperty<>(
            FXCollections.observableArrayList());
    private static final ListProperty<IChannel> ACTIVE_SINGLE_LIST = new SimpleListProperty<>(
            FXCollections.observableArrayList());
    private TwitchChannel model;
    private boolean runOnce = false;

    public TwitchChannelUpdateService(final IChannel model, final boolean runOnce) {
        LOGGER.debug("Create UpdateService for {}", model.getName().get());
        this.runOnce = runOnce;
        if (model.getClass().equals(TwitchChannel.class)) {
            this.model = (TwitchChannel) model;
            if (this.runOnce) {
                setUpSingle();
            } else {
                setUpConstant();
            }
        }
    }

    public final void setUpSingle() {
        setPeriod(Duration.seconds(40));
        setRestartOnFailure(true);
        setOnSucceeded(event -> {
            final TwitchChannelData updatedModel = (TwitchChannelData) event.getSource().getValue();
            if (updatedModel != null) {
                synchronized (this.model) {
                    this.model.updateData(updatedModel, false);
                }
            }
            synchronized (ACTIVE_SINGLE_LIST) {
                ObservableList<IChannel> activeChannelServices = FXCollections
                        .observableArrayList(ACTIVE_SINGLE_LIST.get());
                activeChannelServices.remove(model);
                ACTIVE_SINGLE_LIST.set(activeChannelServices);
            }
            this.cancel();
        });
        setOnFailed(event -> LOGGER.warn("UPDATE SERVICE FAILED"));
    }

    public final void setUpConstant() {
        setPeriod(Duration.seconds(40));
        setRestartOnFailure(true);
        setOnSucceeded(event -> {
            final TwitchChannelData updatedModel = (TwitchChannelData) event.getSource().getValue();
            if (updatedModel != null) {
                synchronized (this.model) {
                    this.model.updateData(updatedModel, true);
                }
            }
            synchronized (ACTIVE_LIST) {
                ObservableList<IChannel> activeChannelServices = FXCollections.observableArrayList(ACTIVE_LIST.get());
                activeChannelServices.remove(model);
                ACTIVE_LIST.set(activeChannelServices);
            }
        });
        setOnFailed(event -> LOGGER.warn("UPDATE SERVICE FAILED"));
    }

    @Override
    protected Task<TwitchChannelData> createTask() {
        return new Task<TwitchChannelData>() {
            @Override
            protected TwitchChannelData call() throws Exception {
                if (runOnce) {
                    synchronized (ACTIVE_SINGLE_LIST) {
                        ACTIVE_SINGLE_LIST.set(addAndGetChannelToList(model, ACTIVE_SINGLE_LIST));
                    }
                } else {
                    synchronized (ACTIVE_LIST) {
                        ACTIVE_LIST.set(addAndGetChannelToList(model, ACTIVE_LIST));
                    }
                }
                return TwitchAPIClient.getInstance().getStreamData(model.getName().get());
            }
        };
    }

    private static ObservableList<IChannel> addAndGetChannelToList(final IChannel channel,
            final ObservableList<IChannel> list) {
        final ObservableList<IChannel> activeChannelServices = FXCollections.observableArrayList(list);
        activeChannelServices.add(channel);
        return activeChannelServices;
    }

    public static ListProperty<IChannel> getActiveSingleChannelServicesProperty() {
        return ACTIVE_SINGLE_LIST;
    }

    public static ListProperty<IChannel> getActiveChannelServicesProperty() {
        return ACTIVE_LIST;
    }
}
