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

import app.lsgui.utils.LsGuiUtils;
import app.lsgui.utils.Settings;
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
public final class BrowserWindow extends Stage {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserWindow.class);
    private static final String BROWSER_FXML = "fxml/BrowserWindow.fxml";
    private static final int BROWSER_WINDOW_MIN_HEIGHT = 620;
    private static final int BROWSER_WINDOW_MIN_WIDTH = 950;

    public BrowserWindow(final Window parentWindow) {
        final Parent root = this.loadFxml();
        this.setupStage(root, parentWindow);
    }

    private Parent loadFxml() {
        try {
            return FXMLLoader.load(getClass().getClassLoader().getResource(BROWSER_FXML));
        } catch (IOException e) {
            LOGGER.error("ERROR while load browser fxml", e);
            Platform.exit();
            return null;
        }
    }

    private void setupStage(final Parent root, final Window parentWindow) {
        this.setMinHeight(BROWSER_WINDOW_MIN_HEIGHT);
        this.setHeight(BROWSER_WINDOW_MIN_HEIGHT);
        this.setMinWidth(BROWSER_WINDOW_MIN_WIDTH);
        this.setWidth(BROWSER_WINDOW_MIN_WIDTH);

        this.setTitle("Livestreamer GUI Twitch Browser v" + LsGuiUtils.readVersionProperty());
        this.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
        final Scene scene = new Scene(root);
        this.setScene(scene);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initOwner(parentWindow);
        this.getScene().getStylesheets().add(BrowserWindow.class
                .getResource("/styles/" + Settings.getInstance().getWindowStyle() + ".css").toExternalForm());
    }
}
