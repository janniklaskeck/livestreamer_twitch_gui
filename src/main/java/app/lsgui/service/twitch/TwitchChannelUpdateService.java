package app.lsgui.service.twitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.StreamModel;
import app.lsgui.model.twitch.TwitchStreamModel;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

public class TwitchChannelUpdateService extends ScheduledService<TwitchStreamData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchChannelUpdateService.class);
    private TwitchStreamModel model;

    public TwitchChannelUpdateService(final StreamModel model) {
        LOGGER.debug("Create UpdateService for {}", model.getName().get());
        if (model.getClass().equals(TwitchStreamModel.class)) {
            this.model = (TwitchStreamModel) model;

            setPeriod(Duration.seconds(60));
            setRestartOnFailure(true);
            setOnSucceeded(event -> {
                LOGGER.info("task succeeded");
                final TwitchStreamData updatedModel = (TwitchStreamData) event.getSource().getValue();
                if (updatedModel != null) {
                    // synchronized (this.model) {
                    LOGGER.debug("Update model {}", model.getName().get());
                    this.model.updateData(updatedModel);
                    // }
                }
            });
        }
    }

    @Override
    protected Task<TwitchStreamData> createTask() {
        return new Task<TwitchStreamData>() {

            @Override
            protected TwitchStreamData call() throws Exception {
                return TwitchProcessor.instance().getStreamData(model.getName().get());
            }
        };
    }

}
