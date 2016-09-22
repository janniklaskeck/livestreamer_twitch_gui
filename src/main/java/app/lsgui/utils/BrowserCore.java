/**
 * MIT License
 *
 * Copyright (c) 2016 Jan-Niklas Keck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package app.lsgui.utils;

import java.util.Locale;

import org.controlsfx.control.GridView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.model.twitch.TwitchChannels;
import app.lsgui.model.twitch.TwitchGame;
import app.lsgui.model.twitch.TwitchGames;
import app.lsgui.remote.twitch.TwitchAPIClient;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ScrollBar;

/**
 *
 * @author Niklas 26.06.2016
 *
 */
public final class BrowserCore {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserCore.class);

    private static BrowserCore instance;
    private GridView<ITwitchItem> browserGridView;
    private String currentGame = "";
    private ObjectProperty<ObservableList<ITwitchItem>> items = new SimpleObjectProperty<>(
            FXCollections.observableArrayList());
    private ObjectProperty<ObservableList<ITwitchItem>> activeItems = new SimpleObjectProperty<>(
            FXCollections.observableArrayList());

    private BrowserCore() {
    }

    public static synchronized BrowserCore getInstance() {
        if (instance == null) {
            instance = new BrowserCore();
        }
        return instance;
    }

    public void setGridView(final GridView<ITwitchItem> displayGridView) {
        this.browserGridView = displayGridView;
        this.browserGridView.itemsProperty().bind(this.activeItems);
    }

    public void goToHome() {
        LOGGER.debug("Go to home");
        final TwitchGames games = TwitchAPIClient.getInstance().getGamesData();
        this.items.set(games.getGames());
        this.activeItems.set(games.getGames());
        this.scrollToTop();
    }

    public void refresh() {
        LOGGER.debug("Refresh: redirect to home page");
        if ("".equals(this.currentGame)) {
            this.goToHome();
        } else {
            this.openGame(this.currentGame);
        }
    }

    public void openGame(final String game) {
        LOGGER.debug("Open Data for Game '{}'", game);
        final TwitchChannels channels = TwitchAPIClient.getInstance().getGameData(game);
        this.items.set(channels.getChannels());
        this.activeItems.set(channels.getChannels());
        this.scrollToTop();
        this.currentGame = game;
    }

    public void filter(final String filter) {
        final ObservableList<ITwitchItem> oldItems = this.items.get();
        final FilteredList<ITwitchItem> filteredItems = new FilteredList<>(oldItems);
        filteredItems.setPredicate(item -> {
            if (item instanceof TwitchGame) {
                final TwitchGame game = (TwitchGame) item;
                return game.getName().get().toLowerCase(Locale.ENGLISH).contains(filter);
            } else if (item instanceof TwitchChannel) {
                final TwitchChannel channel = (TwitchChannel) item;
                return channel.getName().get().toLowerCase(Locale.ENGLISH).contains(filter);
            }
            return true;
        });
        this.activeItems.set(filteredItems);
    }

    private void scrollToTop() {
        final ScrollBar vBar = (ScrollBar) this.browserGridView.lookup(".scroll-bar:vertical");
        if (vBar != null) {
            vBar.setValue(0.0D);
            vBar.setVisible(true);
        }
    }

    public ObjectProperty<ObservableList<ITwitchItem>> getItems() {
        return this.activeItems;
    }
}
