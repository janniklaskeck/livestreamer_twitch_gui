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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.gui.twitchbrowser.BrowserTab;
import app.lsgui.gui.twitchbrowser.BrowserTabPane;
import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.model.twitch.TwitchChannel;
import app.lsgui.model.twitch.TwitchChannels;
import app.lsgui.model.twitch.TwitchGame;
import app.lsgui.model.twitch.TwitchGames;
import app.lsgui.remote.twitch.TwitchAPIClient;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;

/**
 *
 * @author Niklas 26.06.2016
 *
 */
public final class BrowserCore {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserCore.class);

    private static BrowserCore instance;
    private ObjectProperty<String> qualityProperty = new SimpleObjectProperty<>();
    private BrowserTabPane tabPane;

    private BrowserCore() {
    }

    public static synchronized BrowserCore getInstance() {
        if (instance == null) {
            instance = new BrowserCore();
        }
        return instance;
    }

    public void setTabPane(final BrowserTabPane tabPane) {
        this.tabPane = tabPane;
    }

    public void bindQualityProperty(final ReadOnlyObjectProperty<String> qualityProperty) {
        this.qualityProperty.bind(qualityProperty);
    }

    public void refresh() {
        LOGGER.debug("Refresh: redirect to home page");
        if (this.tabPane.getSelectionModel().getSelectedIndex() == 0) {
            this.goToHome();
        } else {
            final BrowserTab currentTab = (BrowserTab) this.tabPane.getSelectionModel().getSelectedItem();
            this.openGame(currentTab.getText());
        }
    }

    public void goToHome() {
        LOGGER.debug("Go to home");
        final TwitchGames games = TwitchAPIClient.getInstance().getGamesData();
        final BrowserTab homeTab;
        if (this.tabPane.getTabs().isEmpty()) {
            homeTab = new BrowserTab("Home");
            homeTab.setClosable(false);
            this.tabPane.getTabs().add(homeTab);
        } else {
            homeTab = this.tabPane.getBrowserTabs().get(0);
            this.scrollToTop();
        }
        homeTab.itemsProperty().set(games.getGames());
        homeTab.activeItemsProperty().set(games.getGames());
    }

    public void openGame(final String game) {
        LOGGER.debug("Open Data for Game '{}'", game);
        final TwitchChannels channels = TwitchAPIClient.getInstance().getGameData(game);
        final BrowserTab gameTab;
        if (gameTabAlreadyExists(game)) {
            final Optional<BrowserTab> optionalTab = this.tabPane.getBrowserTabs().stream()
                    .filter(tab -> tab.getText().equalsIgnoreCase(game)).findFirst();
            if (optionalTab.isPresent()) {
                gameTab = optionalTab.get();
            } else {
                throw new UnsupportedOperationException("Could not get already existing Game Tab");
            }
            this.tabPane.getSelectionModel().select(gameTab);
        } else {
            gameTab = this.addGameTab(game);
        }
        gameTab.itemsProperty().set(channels.getChannels());
        gameTab.activeItemsProperty().set(channels.getChannels());
        this.scrollToTop();
    }

    private boolean gameTabAlreadyExists(final String game) {
        boolean alreadyExists = false;
        final ObservableList<BrowserTab> browserTabs = this.tabPane.getBrowserTabs();
        for (final BrowserTab tab : browserTabs) {
            if (tab.getText().equalsIgnoreCase(game)) {
                alreadyExists = true;
                break;
            }
        }
        return alreadyExists;
    }

    public void filter(final String filter) {
        final BrowserTab currentTab = this.tabPane.getSelectedItem();
        final ObservableList<ITwitchItem> oldItems = currentTab.itemsProperty().get();
        final FilteredList<ITwitchItem> filteredItems = new FilteredList<>(oldItems);
        filteredItems.setPredicate(item -> {
            if (item.isTwitchGame()) {
                final TwitchGame game = (TwitchGame) item;
                return game.getName().get().toLowerCase(Locale.ENGLISH).contains(filter);
            } else if (item.isTwitchChannel()) {
                final TwitchChannel channel = (TwitchChannel) item;
                return channel.getName().get().toLowerCase(Locale.ENGLISH).contains(filter);
            }
            return true;
        });
        currentTab.activeItemsProperty().set(filteredItems);
    }

    private void scrollToTop() {
        final Node tabContent = this.tabPane.getSelectedItem().getContent();
        final ScrollBar vBar = (ScrollBar) tabContent.lookup(".scroll-bar:vertical");
        if (vBar != null) {
            vBar.setValue(0.0D);
            vBar.setVisible(true);
        }
    }

    private BrowserTab addGameTab(final String name) {
        final BrowserTab gameTab = new BrowserTab(name);
        this.tabPane.getTabs().add(gameTab);
        return gameTab;
    }

    public void startStream(final String string) {
        LivestreamerUtils.startLivestreamer("twitch.tv/" + string, this.qualityProperty.get());

    }
}
