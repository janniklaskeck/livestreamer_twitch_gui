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
package app.lsgui.gui.main;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.IChannel;
import app.lsgui.model.twitch.TwitchService;
import app.lsgui.remote.GithubUpdateService;
import app.lsgui.remote.twitch.TwitchChannelUpdateService;
import app.lsgui.utils.LsGuiUtils;
import app.lsgui.utils.Settings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public class LsGuiWindow extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(LsGuiWindow.class);
    private static final int MIN_WIDTH = 650;
    private static final int MIN_HEIGHT = 550;
    private static Stage rootstage;

    public LsGuiWindow() {
        // Empty Constructor
    }

    @Override
    public final void init() {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            LOGGER.error("Uncaught Exception on JavaFX Thread", throwable);
            LOGGER.error("Exiting JavaFX Thread...");
            Platform.exit();
        });
        Platform.setImplicitExit(false);
        Settings.getInstance();
    }

    @Override
    public final void start(Stage primaryStage) {
        final Parent root = this.loadFxml();
        this.setupStage(root, primaryStage);
        GithubUpdateService.checkForUpdate();
    }

    private Parent loadFxml() {
        try {
            return FXMLLoader.load(getClass().getClassLoader().getResource("fxml/MainWindow.fxml"));
        } catch (IOException e) {
            LOGGER.error("ERROR while load main fxml", e);
            Platform.exit();
            return null;
        }
    }

    private void setupStage(final Parent root, final Stage primaryStage) {
        setRootStage(primaryStage);

        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setHeight(MIN_HEIGHT);

        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setWidth(MIN_WIDTH);

        primaryStage.setTitle("Livestreamer GUI v" + LsGuiUtils.readVersionProperty());
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
        final Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            Settings.getInstance().saveSettings();
            Iterator<Map.Entry<IChannel, TwitchChannelUpdateService>> it = ((TwitchService) Settings.getInstance()
                    .servicesProperty().get(0)).getUpdateServices().entrySet().iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
            Platform.exit();
        });

        primaryStage.setOnHiding(event -> {
            Settings.getInstance().saveSettings();
            Iterator<Map.Entry<IChannel, TwitchChannelUpdateService>> it = ((TwitchService) Settings.getInstance()
                    .servicesProperty().get(0)).getUpdateServices().entrySet().iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
            Platform.exit();
        });
        LsGuiWindow.getRootStage().getScene().getStylesheets().add(LsGuiWindow.class
                .getResource("/styles/" + Settings.getInstance().windowStyleProperty() + ".css").toExternalForm());
    }

    public static final synchronized Stage getRootStage() {
        return rootstage;
    }

    private static final synchronized void setRootStage(final Stage newRootStage) {
        rootstage = newRootStage;
    }
}
