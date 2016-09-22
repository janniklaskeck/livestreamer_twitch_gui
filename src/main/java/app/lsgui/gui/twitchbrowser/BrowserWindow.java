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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author Niklas 25.06.2016
 *
 */
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

        browserStage.setMinHeight(620);
        browserStage.setHeight(620);
        browserStage.setMinWidth(950);
        browserStage.setWidth(950);

        browserStage.setTitle("Livestreamer GUI Twitch Browser v" + LsGuiUtils.readVersionProperty());
        browserStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
        browserStage.setScene(scene);
        browserStage.initModality(Modality.APPLICATION_MODAL);
        browserStage.initOwner(parentWindow);
        BrowserWindow.getBrowserStage().getScene().getStylesheets().add(BrowserWindow.class
                .getResource("/styles/" + Settings.getInstance().getWindowStyle() + ".css").toExternalForm());
        browserStage.setOnCloseRequest(event -> setBrowserStage(null));
    }

    private static synchronized void setBrowserStage(final Stage stage) {
        browserStage = stage;
    }

    public static synchronized Stage getBrowserStage() {
        return browserStage;
    }

    public void showAndWait() {
        browserStage.showAndWait();
    }

}
