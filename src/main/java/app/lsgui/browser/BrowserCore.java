package app.lsgui.browser;

import org.controlsfx.control.GridView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.model.twitch.channel.TwitchChannels;
import app.lsgui.model.twitch.game.TwitchGames;
import app.lsgui.rest.twitch.TwitchAPIClient;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollBar;

/**
 *
 * @author Niklas 26.06.2016
 *
 */
public class BrowserCore {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserCore.class);

    private static BrowserCore instance;
    private GridView<ITwitchItem> gridView;
    private String currentGame = "";
    private ObjectProperty<ObservableList<ITwitchItem>> items = new SimpleObjectProperty<>();

    private BrowserCore(final GridView<ITwitchItem> displayGridView) {
        gridView = displayGridView;
    }

    public static BrowserCore getInstance(final GridView<ITwitchItem> displayGridView) {
        if (instance == null) {
            instance = new BrowserCore(displayGridView);
        }
        return instance;
    }

    public static BrowserCore getInstance() {
        if (instance == null) {
            throw new UnsupportedOperationException("BrowserCore was not initialized with GrideView!");
        }
        return instance;
    }

    public void goToHome() {
        final TwitchGames games = TwitchAPIClient.getInstance().getGamesData();
        items.set(games.getGames());
        gridView.setItems(items.get());
        scrollToTop();
    }

    public void refresh() {
        LOGGER.debug("Refresh: redirect to home page");
        if ("".equals(currentGame)) {
            goToHome();
        } else {
            openGame(currentGame);
        }
    }

    public void openGame(final String game) {
        LOGGER.debug("Open Data for Game '{}'", game);
        final TwitchChannels channels = TwitchAPIClient.getInstance().getGameData(game);
        items.set(channels.getChannels());
        gridView.setItems(items.get());
        scrollToTop();
        currentGame = game;
    }

    private void scrollToTop() {
        final ScrollBar vBar = (ScrollBar) gridView.lookup(".scroll-bar:vertical");
        if (vBar != null) {
            vBar.setValue(0.0D);
            vBar.setVisible(true);
        }
    }

    public ObjectProperty<ObservableList<ITwitchItem>> getItems() {
        return items;
    }

}
