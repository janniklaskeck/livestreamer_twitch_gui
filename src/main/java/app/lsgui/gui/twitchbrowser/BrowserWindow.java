package app.lsgui.gui.twitchbrowser;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import app.lsgui.model.twitch.game.TwitchGames;
import app.lsgui.settings.Settings;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class BrowserWindow {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserWindow.class);
    private static final String BROWSER_FXML = "fxml/BrowserWindow.fxml";
    private static Stage browserStage;

    public BrowserWindow(final Window parentWindow) {
        setBrowserStage(new Stage());
        Parent root = loadFXML();
        setupStage(root, browserStage, parentWindow);
    }

    private Parent loadFXML() {
        try {
            return FXMLLoader.load(getClass().getClassLoader().getResource(BROWSER_FXML));
        } catch (IOException e) {
            LOGGER.error("ERROR while load browser fxml", e);
            Platform.exit();
            return null;
        }
    }

    private void setupStage(final Parent root, final Stage browserStage, final Window parentWindow) {
        Scene scene = new Scene(root);

        browserStage.setResizable(false);

        browserStage.setTitle("Livestreamer GUI v3.0 Twitch Browser");
        browserStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
        browserStage.setScene(scene);
        browserStage.initModality(Modality.APPLICATION_MODAL);
        browserStage.initOwner(parentWindow);
        BrowserWindow.getBrowserStage().getScene().getStylesheets().add(BrowserWindow.class
                .getResource("/styles/" + Settings.instance().getWindowStyle() + ".css").toExternalForm());
        browserStage.setOnCloseRequest(event -> setBrowserStage(null));
    }

    private static void setBrowserStage(final Stage stage) {
        browserStage = stage;
    }

    public static Stage getBrowserStage() {
        return browserStage;
    }

    /**
     * Show Browser Stage
     */
    public void showAndWait() {
        browserStage.showAndWait();
    }

}
