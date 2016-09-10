package app.lsgui.gui.settings;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.settings.Settings;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class SettingsWindow extends AnchorPane { // NOSONAR

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsWindow.class);
    private static final String SETTINGSFXML = "fxml/SettingsWindow.fxml";
    private static Stage settingsStage;

    /**
     *
     * @param parentWindow
     */
    public SettingsWindow(final Window parentWindow) {
        setSettingsStage(new Stage());
        Parent root = loadFXML();
        setupStage(root, settingsStage, parentWindow);

    }

    private Parent loadFXML() {
        try {
            return FXMLLoader.load(getClass().getClassLoader().getResource(SETTINGSFXML));
        } catch (IOException e) {
            LOGGER.error("ERROR while load settings fxml", e);
            Platform.exit();
            return null;
        }
    }

    private void setupStage(final Parent root, final Stage settingsStage, final Window parentWindow) {
        Scene scene = new Scene(root);

        settingsStage.setResizable(false);

        settingsStage.setTitle("Livestreamer GUI v3 Settings");
        settingsStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
        settingsStage.setScene(scene);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.initOwner(parentWindow);
        SettingsWindow.getSettingsStage().getScene().getStylesheets().add(SettingsWindow.class
                .getResource("/styles/" + Settings.instance().getWindowStyle() + ".css").toExternalForm());
        settingsStage.setOnCloseRequest(event -> setSettingsStage(null));
    }

    private static void setSettingsStage(final Stage stage) {
        settingsStage = stage;
    }

    public static Stage getSettingsStage() {
        return settingsStage;
    }

    /**
     * Show Settings Stage
     */
    public void showAndWait() {
        settingsStage.showAndWait();
    }
}
