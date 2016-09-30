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
package app.lsgui.gui.twitchbrowser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.remote.twitch.TwitchBrowserUpdateService;
import app.lsgui.utils.BrowserCore;
import app.lsgui.utils.Settings;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Niklas 25.06.2016
 *
 */
public final class BrowserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserController.class);
    private static final int PROGRESS_BAR_MIN_HEIGHT = 20;

    @FXML
    private ToolBar browserToolBar;

    @FXML
    private BorderPane browserRootBorderPane;

    private final BrowserTabPane browserTabPane = new BrowserTabPane();
    private final ComboBox<String> qualityComboBox = new ComboBox<>();
    private final TextField searchTextField = new TextField();
    private final BrowserCore browserCore = BrowserCore.getInstance();

    public BrowserController() {
        LOGGER.trace("BrowserController created.");
    }

    @FXML
    public void initialize() {
        this.setupToolBar();
        this.setupProgressBar();
        this.setupTabPane();
        this.browserCore.bindQualityProperty(this.qualityComboBox.getSelectionModel().selectedItemProperty());
        this.browserCore.setTabPane(this.browserTabPane);
        Platform.runLater(this.browserCore::goToHome);
    }

    private void setupTabPane() {
        this.browserRootBorderPane.setCenter(this.browserTabPane);
        this.browserTabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
        this.browserTabPane.getTabs().addListener((ListChangeListener<Tab>) change -> {
            if (change.next() && change.wasAdded()) {
                LOGGER.debug("Tab was added");
                this.browserTabPane.getSelectionModel().select(change.getAddedSubList().get(0));
            }
        });
        this.browserTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            final BrowserTab oldTab = (BrowserTab) oldValue;
            if (oldTab != null) {
                this.searchTextField.setText("");
                oldTab.resetActiveItems();
            }
        });
    }

    private void setupProgressBar() {
        final VBox vbox = new VBox();
        final ProgressBar browserProgressBar = new ProgressBar();
        browserProgressBar.setVisible(false);
        browserProgressBar.setMinHeight(PROGRESS_BAR_MIN_HEIGHT);
        browserProgressBar.setMaxWidth(Double.MAX_VALUE);
        vbox.getChildren().add(browserProgressBar);
        this.browserRootBorderPane.setBottom(vbox);
        final DoubleProperty progress = new SimpleDoubleProperty();
        TwitchBrowserUpdateService.activeServicesProperty().addListener((observable, oldValue, newValue) -> {
            final int size = observable.getValue().size();
            if (size == 0) {
                progress.set(1.0D);
                browserProgressBar.setVisible(false);
            } else {
                browserProgressBar.setVisible(true);
                progress.set(1.0D / observable.getValue().size());
            }
        });
        browserProgressBar.progressProperty().bind(progress);
    }

    private void setupToolBar() {
        final Button homeButton = GlyphsDude.createIconButton(FontAwesomeIcon.HOME);
        homeButton.setOnAction(event -> this.browserCore.goToHome());
        final Button refreshButton = GlyphsDude.createIconButton(FontAwesomeIcon.REFRESH);
        refreshButton.setOnAction(event -> this.browserCore.refresh());

        this.searchTextField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                this.browserCore.filter(newValue);
            }
        });
        final Label searchLabel = new Label("Filter");
        final ComboBox<String> favouriteGameComboBox = new ComboBox<>();
        final ListProperty<String> favouriteGames = Settings.getInstance().getFavouriteGames();
        favouriteGameComboBox.itemsProperty().bind(favouriteGames);
        favouriteGameComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                this.browserCore.openGame(newValue);
                Platform.runLater(() -> favouriteGameComboBox.setValue(null));
            }
        });

        this.qualityComboBox.getItems().add("Worst");
        this.qualityComboBox.getItems().add("Best");
        this.qualityComboBox.getSelectionModel().select(1);

        this.browserToolBar.getItems().add(homeButton);
        this.browserToolBar.getItems().add(new Separator(Orientation.VERTICAL));
        this.browserToolBar.getItems().add(refreshButton);
        this.browserToolBar.getItems().add(new Separator(Orientation.VERTICAL));
        this.browserToolBar.getItems().add(searchLabel);
        this.browserToolBar.getItems().add(this.searchTextField);
        this.browserToolBar.getItems().add(new Separator(Orientation.VERTICAL));
        this.browserToolBar.getItems().add(favouriteGameComboBox);
        this.browserToolBar.getItems().add(new Separator(Orientation.VERTICAL));
        this.browserToolBar.getItems().add(this.qualityComboBox);
    }
}
