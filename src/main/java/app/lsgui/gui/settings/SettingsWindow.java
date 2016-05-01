package app.lsgui.gui.settings;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.service.Settings;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SettingsWindow extends AnchorPane {// NOSONAR

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsWindow.class);
    private static Stage settingsStage;

    public SettingsWindow(final Window parentWindow) {
        settingsStage = new Stage();
        Parent root = loadFXML();
        setupStage(root, settingsStage, parentWindow);

    }

    private Parent loadFXML() {
        try {
            return FXMLLoader.load(getClass().getClassLoader().getResource("fxml/SettingsWindow.fxml"));
        } catch (IOException e) {
            LOGGER.error("ERROR while load settings fxml", e);
            Platform.exit();
            return null;
        }
    }

    private void setupStage(final Parent root, final Stage settingsStage, final Window parentWindow) {
        Scene scene = new Scene(root);

        settingsStage.setResizable(false);

        settingsStage.setTitle("Livestreamer GUI v3.0 Settings");
        settingsStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
        settingsStage.setScene(scene);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.initOwner(parentWindow);
        SettingsWindow.getSettingsStage().getScene().getStylesheets().add(SettingsWindow.class
                .getResource("/styles/" + Settings.instance().getWindowStyle() + ".css").toExternalForm());
    }

    /**
     * @return the settingsStage
     */
    static Stage getSettingsStage() {
        return settingsStage;
    }

    public void showAndWait() {
        settingsStage.showAndWait();
    }

}
