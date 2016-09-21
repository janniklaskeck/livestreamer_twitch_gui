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

import org.controlsfx.control.GridView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.rest.twitch.TwitchBrowserUpdateService;
import app.lsgui.settings.Settings;
import app.lsgui.utils.BrowserCore;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Niklas 25.06.2016
 *
 */
public class BrowserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserController.class);

    @FXML
    private ToolBar browserToolBar;

    @FXML
    private BorderPane browserRootBorderPane;

    private ComboBox<String> favouriteGameComboBox;
    private ComboBox<String> qualityComboBox;
    private ProgressBar browserProgressBar;
    private GridView<ITwitchItem> browserGridView;
    private BrowserCore browserCore;

    @FXML
    public void initialize() {
        setupToolBar();
        setupProgressBar();
        setupGrid();
        browserCore = BrowserCore.getInstance();
        browserCore.setGridView(browserGridView);
        browserRootBorderPane.setCenter(browserGridView);
        Platform.runLater(() -> browserCore.goToHome());
    }

    private void setupProgressBar() {
        final VBox vbox = new VBox();
        browserProgressBar = new ProgressBar();
        browserProgressBar.setVisible(false);
        browserProgressBar.setMinHeight(20);
        browserProgressBar.setMaxWidth(Double.MAX_VALUE);
        vbox.getChildren().add(browserProgressBar);
        browserRootBorderPane.setBottom(vbox);
        final DoubleProperty progress = new SimpleDoubleProperty();
        TwitchBrowserUpdateService.getActiveChannelServicesProperty().addListener((observable, oldValue, newValue) -> {
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
        homeButton.setOnAction(event -> goToHome());
        final Button refreshButton = GlyphsDude.createIconButton(FontAwesomeIcon.REFRESH);
        refreshButton.setOnAction(event -> refreshBrowser());
        final TextField searchTextField = new TextField();
        searchTextField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                browserCore.filter(newValue);
            }
        });
        final Label searchLabel = new Label("Filter");
        favouriteGameComboBox = new ComboBox<>();
        final ListProperty<String> favouriteGames = Settings.getInstance().getFavouriteGames();
        favouriteGameComboBox.itemsProperty().bind(favouriteGames);
        favouriteGameComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                browserCore.openGame(newValue);
            }
        });

        qualityComboBox = new ComboBox<>();
        qualityComboBox.getItems().add("Worst");
        qualityComboBox.getItems().add("Best");
        qualityComboBox.getSelectionModel().select(1);

        browserToolBar.getItems().add(homeButton);
        browserToolBar.getItems().add(new Separator(Orientation.VERTICAL));
        browserToolBar.getItems().add(refreshButton);
        browserToolBar.getItems().add(new Separator(Orientation.VERTICAL));
        browserToolBar.getItems().add(searchLabel);
        browserToolBar.getItems().add(searchTextField);
        browserToolBar.getItems().add(new Separator(Orientation.VERTICAL));
        browserToolBar.getItems().add(favouriteGameComboBox);
        browserToolBar.getItems().add(new Separator(Orientation.VERTICAL));
        browserToolBar.getItems().add(qualityComboBox);
    }

    private void goToHome() {
        LOGGER.debug("Go to home directory");
        browserCore.goToHome();
    }

    private void refreshBrowser() {
        LOGGER.debug("Refresh current page");
        browserCore.refresh();
    }

    private void setupGrid() {
        browserGridView = new GridView<>();
        browserGridView.setCellFactory(
                param -> new TwitchItemPane(qualityComboBox.getSelectionModel().selectedItemProperty()));
        browserGridView.setCellWidth(TwitchItemPane.WIDTH);
        browserGridView.cellHeightProperty().bind(TwitchItemPane.HEIGHT_PROPERTY);
    }

}
