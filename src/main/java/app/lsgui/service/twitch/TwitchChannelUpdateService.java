package app.lsgui.service.twitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.Channel;
import app.lsgui.model.twitch.TwitchChannel;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class TwitchChannelUpdateService extends ScheduledService<TwitchChannelData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchChannelUpdateService.class);
    private TwitchChannel model;

    public TwitchChannelUpdateService(final Channel model) {
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
            });
            setOnFailed(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    LOGGER.warn("UPDATE SERVICE FAILED");
                }
            });
        }
    }

    @Override
    protected Task<TwitchChannelData> createTask() {
        return new Task<TwitchChannelData>() {
            @Override
            protected TwitchChannelData call() throws Exception {
                TwitchChannelData tsd = null;
                try {
                    tsd = TwitchProcessor.instance().getStreamData(model.getName().get());
                } catch (Exception e) {
                    LOGGER.error("TASK EXCEPTION", e);
                }
                return tsd;
            }
        };
    }

}
