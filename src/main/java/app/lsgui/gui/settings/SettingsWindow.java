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
package app.lsgui.gui.settings;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.settings.Settings;
import app.lsgui.utils.LsGuiUtils;
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
public final class SettingsWindow extends AnchorPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsWindow.class);
    private static final String SETTINGS_FXML = "fxml/SettingsWindow.fxml";
    private static Stage settingsStage;

    public SettingsWindow(final Window parentWindow) {
        setSettingsStage(new Stage());
        final Parent root = this.loadFxml();
        this.setupStage(root, settingsStage, parentWindow);

    }

    private Parent loadFxml() {
        try {
            return FXMLLoader.load(getClass().getClassLoader().getResource(SETTINGS_FXML));
        } catch (IOException e) {
            LOGGER.error("ERROR while load settings fxml", e);
            Platform.exit();
            return null;
        }
    }

    private void setupStage(final Parent root, final Stage settingsStage, final Window parentWindow) {
        settingsStage.setResizable(false);
        settingsStage.setTitle("Livestreamer GUI Settings v3" + LsGuiUtils.readVersionProperty());
        settingsStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
        final Scene scene = new Scene(root);
        settingsStage.setScene(scene);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.initOwner(parentWindow);
        SettingsWindow.getSettingsStage().getScene().getStylesheets().add(SettingsWindow.class
                .getResource("/styles/" + Settings.getInstance().getWindowStyle() + ".css").toExternalForm());
        settingsStage.setOnCloseRequest(event -> setSettingsStage(null));
    }

    private static synchronized void setSettingsStage(final Stage stage) {
        settingsStage = stage;
    }

    public static synchronized Stage getSettingsStage() {
        return settingsStage;
    }

    /**
     * Show Settings Stage
     */
    public void showAndWait() {
        settingsStage.showAndWait();
    }
}
