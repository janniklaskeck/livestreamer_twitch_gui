package app.lsgui.gui.twitchbrowser;

import org.controlsfx.control.GridView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.browser.BrowserCore;
import app.lsgui.model.twitch.ITwitchItem;
import app.lsgui.rest.twitch.TwitchChannelUpdateService;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
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

    private ProgressBar browserProgressBar;

    @FXML
    private BorderPane browserRootBorderPane;

    private GridView<ITwitchItem> browserGridView;

    private BrowserCore browserCore;

    @FXML
    public void initialize() {
        browserCore = BrowserCore.getInstance();
        setupToolBar();
        setupProgressBar();
        setupGrid();
        browserRootBorderPane.setCenter(browserGridView);
        browserCore.setGridView(browserGridView);
        browserCore.goToHome();
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
        TwitchChannelUpdateService.getActiveSingleChannelServicesProperty()
                .addListener((observable, oldValue, newValue) -> {
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
        final Separator firstSeparator = new Separator(Orientation.VERTICAL);
        final Button refreshButton = GlyphsDude.createIconButton(FontAwesomeIcon.REFRESH);
        refreshButton.setOnAction(event -> refreshBrowser());
        final Separator secondSeparator = new Separator(Orientation.VERTICAL);
        final Button backButton = GlyphsDude.createIconButton(FontAwesomeIcon.ARROW_LEFT);
        backButton.setOnAction(event -> backBrowser());
        final Button forwardButton = GlyphsDude.createIconButton(FontAwesomeIcon.ARROW_RIGHT);
        forwardButton.setOnAction(event -> forwardBrowser());
        final Separator thirdSeparator = new Separator(Orientation.VERTICAL);
        final TextField searchTextField = new TextField();
        final Button searchButton = GlyphsDude.createIconButton(FontAwesomeIcon.SEARCH);
        searchButton.setOnAction(event -> startSearch());

        browserToolBar.getItems().add(homeButton);
        browserToolBar.getItems().add(firstSeparator);
        browserToolBar.getItems().add(refreshButton);
        browserToolBar.getItems().add(secondSeparator);
        browserToolBar.getItems().add(backButton);
        browserToolBar.getItems().add(forwardButton);
        browserToolBar.getItems().add(thirdSeparator);
        browserToolBar.getItems().add(searchTextField);
        browserToolBar.getItems().add(searchButton);
    }

    private void goToHome() {
        LOGGER.debug("Go to home directory");
        browserCore.goToHome();
    }

    private void refreshBrowser() {
        LOGGER.debug("Refresh current page");
        browserCore.refresh();
    }

    private void backBrowser() {
        LOGGER.debug("Go back one page");
    }

    private void forwardBrowser() {
        LOGGER.debug("Go one page forward");
    }

    private void startSearch() {
        LOGGER.debug("Start search");
    }

    private void setupGrid() {
        browserGridView = new GridView<>();
        browserGridView.setUserData(browserCore);
        browserGridView.setCellFactory(param -> new TwitchItemPane());
        browserGridView.setCellWidth(TwitchItemPane.WIDTH);
        browserGridView.cellHeightProperty().bind(TwitchItemPane.HEIGHT_PROPERTY);
    }

}
