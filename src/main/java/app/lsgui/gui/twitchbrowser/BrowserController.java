package app.lsgui.gui.twitchbrowser;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;

public class BrowserController {

    @FXML
    private ToolBar browserToolBar;

    @FXML
    private ProgressBar browserProgressBar;

    @FXML
    private GridPane browserGridPane;

    @FXML
    public void initialize() {
        setupToolBar();
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
