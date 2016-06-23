package app.lsgui.rest.twitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
import app.lsgui.model.channel.twitch.TwitchChannel;
import app.lsgui.model.channel.twitch.TwitchGames;
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
public class TwitchGamesUpdateService extends ScheduledService<TwitchGames> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchGamesUpdateService.class);

    private final TwitchGames games;

    public TwitchGamesUpdateService(final TwitchGames games) {
        LOGGER.debug("Create UpdateService for Twitch.tv Games");
        this.games = games;
        setPeriod(Duration.seconds(40));
        setRestartOnFailure(true);
        setOnSucceeded(event -> {
            final TwitchGames updatedGames = (TwitchGames) event.getSource().getValue();
            if (updatedGames != null) {
                synchronized (this.games) {
                    this.games.updateData(updatedGames);
                }
            }
        });
        setOnFailed(event -> LOGGER.warn("UPDATE SERVICE FAILED"));

    }

    @Override
    protected Task<TwitchGames> createTask() {
        return new Task<TwitchGames>() {
            @Override
            protected TwitchGames call() throws Exception {
                return TwitchAPIClient.instance().getGameData();
            }
        };
    }

}
