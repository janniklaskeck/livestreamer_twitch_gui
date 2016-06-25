package app.lsgui.gui.twitchbrowser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import app.lsgui.model.twitch.game.TwitchGame;
import app.lsgui.model.twitch.game.TwitchGames;
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

    /**
     * Init method
     */
    @FXML
    public void initialize() {
        setupToolBar();

        loadExampleFile();
        setupGrid();
        browserScrollPane.setContent(browserGridPane);
    }

    private void loadExampleFile() {
        final FileInputStream fis;
        try {
            fis = new FileInputStream(new File(getClass().getClassLoader().getResource("gamesDump.json").getPath()));
            final InputStreamReader isr = new InputStreamReader(fis);
            final BufferedReader bufferedReader = new BufferedReader(isr);
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            final Gson g = new Gson();
            final JsonObject data = g.fromJson(sb.toString(), JsonObject.class);
            games = new TwitchGames(data);
        } catch (IOException e) {
            LOGGER.error("Error loading json dumpfile", e);
        }
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

    private void setupToolBar() {
        final Button homeButton = GlyphsDude.createIconButton(FontAwesomeIcon.HOME);
        final Separator firstSeparator = new Separator(Orientation.VERTICAL);
        final Button backButton = GlyphsDude.createIconButton(FontAwesomeIcon.ARROW_LEFT);
        final Button forwardButton = GlyphsDude.createIconButton(FontAwesomeIcon.ARROW_RIGHT);
        final Separator secondSeparator = new Separator(Orientation.VERTICAL);
        final TextField searchTextField = new TextField();
        final Button searchButton = GlyphsDude.createIconButton(FontAwesomeIcon.SEARCH);

        browserToolBar.getItems().add(homeButton);
        browserToolBar.getItems().add(firstSeparator);
        browserToolBar.getItems().add(backButton);
        browserToolBar.getItems().add(forwardButton);
        browserToolBar.getItems().add(secondSeparator);
        browserToolBar.getItems().add(searchTextField);
        browserToolBar.getItems().add(searchButton);
    }

}
