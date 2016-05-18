package app.lsgui.rest.twitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
import app.lsgui.model.channel.twitch.TwitchChannel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

public class TwitchChannelUpdateService extends ScheduledService<TwitchChannelData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchChannelUpdateService.class);
    private static final ListProperty<IChannel> ACTIVELIST = new SimpleListProperty<>(
            FXCollections.observableArrayList());
    private TwitchChannel model;

    public TwitchChannelUpdateService(final IChannel model) {
        LOGGER.debug("Create UpdateService for {}", model.getName().get());
        if (model.getClass().equals(TwitchChannel.class)) {
            this.model = (TwitchChannel) model;
            setPeriod(Duration.seconds(40));
            setRestartOnFailure(true);
            setOnSucceeded(event -> {
                final TwitchChannelData updatedModel = (TwitchChannelData) event.getSource().getValue();
                if (updatedModel != null) {
                    synchronized (this.model) {
                        this.model.updateData(updatedModel);
                    }
                }
                synchronized (ACTIVELIST) {
                    ObservableList<IChannel> activeChannelServices = FXCollections.observableArrayList(ACTIVELIST.get());
                    activeChannelServices.remove(model);
                    ACTIVELIST.set(activeChannelServices);
                }
            });
            setOnFailed(event -> LOGGER.warn("UPDATE SERVICE FAILED"));
        }

    }

    @Override
    protected Task<TwitchChannelData> createTask() {
        return new Task<TwitchChannelData>() {
            @Override
            protected TwitchChannelData call() throws Exception {
                synchronized (ACTIVELIST) {
                    ObservableList<IChannel> activeChannelServices = FXCollections.observableArrayList(ACTIVELIST.get());
                    activeChannelServices.add(model);
                    ACTIVELIST.set(activeChannelServices);
                }
                return TwitchAPIClient.instance().getStreamData(model.getName().get());
            }
        };
    }

    public static ListProperty<IChannel> getActiveChannelServicesProperty() {
        return ACTIVELIST;
    }
}
