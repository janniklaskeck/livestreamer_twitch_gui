package app.lsgui.gui.twitchbrowser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.twitch.game.TwitchGame;
import app.lsgui.model.twitch.game.TwitchGames;
import app.lsgui.rest.twitch.TwitchGamesUpdateService;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;

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
    private ProgressBar browserProgressBar;

    @FXML
    private ScrollPane browserScrollPane;

    private GridPane browserGridPane;
    private TwitchGames games;
    private TwitchGamesUpdateService updaterServiceGames;

    /**
     * Init method
     */
    @FXML
    public void initialize() {
        setupToolBar();
        startUpdateService();
        setupGrid();
        setupScrollPane();
    }

    private void setupScrollPane() {
        browserScrollPane.setContent(browserGridPane);
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
    }

    private void refreshBrowser() {
        LOGGER.debug("Refresh current page");
        browserGridPane.getChildren().clear();
        for (int i = 0; i < games.getGames().size(); i++) {
            final TwitchGame game = games.getGames().get(i);
            LOGGER.debug(games.getGames().get(i).getName());
            final int x = i % 5;
            final int y = i / 5;
            browserGridPane.add(new TwitchGamePane(game.getName(), game.getBoxImage()), x, y);
        }
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

    private void startUpdateService() {
        games = new TwitchGames();
        updaterServiceGames = new TwitchGamesUpdateService(games);
        updaterServiceGames.start();
    }

    private void setupGrid() {
        browserGridPane = new GridPane();
        for (int i = 0; i < games.getGames().size(); i++) {
            final TwitchGame game = games.getGames().get(i);
            final int x = i % 5;
            final int y = i / 5;
            browserGridPane.add(new TwitchGamePane(game.getName(), game.getBoxImage()), x, y);
        }

    }

}
