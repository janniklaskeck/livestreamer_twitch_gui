package app.lsgui.browser;

import org.controlsfx.control.GridView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.model.twitch.channel.TwitchChannels;
import app.lsgui.model.twitch.game.TwitchGames;
import app.lsgui.rest.twitch.TwitchAPIClient;

/**
 *
 * @author Niklas 26.06.2016
 *
 */
public class BrowserCore {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserCore.class);

    private static BrowserCore instance;
    private GridView<ITwitchItem> gridView;

    private BrowserCore() {
    }

    public static BrowserCore getInstance() {
        if (instance == null) {
            instance = new BrowserCore();
        }
        return instance;
    }

    public void setGridView(final GridView<ITwitchItem> displayGridView) {
        gridView = displayGridView;
    }

    /**
     * Go to main directory page
     */
    public void goToHome() {
        final TwitchGames games = TwitchAPIClient.getInstance().getGamesData();
        gridView.setItems(games.getGames());
    }

    /**
     * Refresh the current Page
     */
    public void refresh() {
        LOGGER.debug("Refresh: redirect to home page");
        goToHome();
    }

    public void openGame(final String name) {
        LOGGER.debug("Open Data for Game '{}'", name);
        final TwitchChannels channels = TwitchAPIClient.getInstance().getGameData(name);
        gridView.setItems(channels.getChannels());
    }

}
