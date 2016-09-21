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
package app.lsgui.gui;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.model.channel.IChannel;
import app.lsgui.model.service.TwitchService;
import app.lsgui.rest.GithubUpdateService;
import app.lsgui.rest.twitch.TwitchChannelUpdateService;
import app.lsgui.settings.Settings;
import app.lsgui.utils.LsGuiUtils;
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
public class LsGUIWindow extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(LsGUIWindow.class);
    private static Stage rootstage;

    @Override
    public void init() {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            LOGGER.error("Uncaught Exception on JavaFX Thread", throwable);
            LOGGER.error("Exiting JavaFX Thread...");
            Platform.exit();
        });
        Platform.setImplicitExit(false);
        Settings.getInstance();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = loadFXML();
        setupStage(root, primaryStage);
        GithubUpdateService.checkForUpdate();
    }

    private Parent loadFXML() {
        try {
            return FXMLLoader.load(getClass().getClassLoader().getResource("fxml/MainWindow.fxml"));
        } catch (IOException e) {
            LOGGER.error("ERROR while load main fxml", e);
            Platform.exit();
            return null;
        }
    }

    private void setupStage(final Parent root, final Stage primaryStage) {
        Scene scene = new Scene(root);
        setRootStage(primaryStage);

        primaryStage.setMinHeight(550);
        primaryStage.setHeight(550);

        primaryStage.setMinWidth(650);
        primaryStage.setWidth(750);

        primaryStage.setTitle("Livestreamer GUI v" + LsGuiUtils.readVersionProperty());
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            Settings.getInstance().saveSettings();
            Iterator<Map.Entry<IChannel, TwitchChannelUpdateService>> it = ((TwitchService) Settings.getInstance()
                    .getStreamServices().get(0)).getUpdateServices().entrySet().iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
            Platform.exit();
        });

        primaryStage.setOnHiding(event -> {
            Settings.getInstance().saveSettings();
            Iterator<Map.Entry<IChannel, TwitchChannelUpdateService>> it = ((TwitchService) Settings.getInstance()
                    .getStreamServices().get(0)).getUpdateServices().entrySet().iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
            Platform.exit();
        });
        LsGUIWindow.getRootStage().getScene().getStylesheets().add(LsGUIWindow.class
                .getResource("/styles/" + Settings.getInstance().getWindowStyle() + ".css").toExternalForm());
    }

    public static final Stage getRootStage() {
        return rootstage;
    }

    private static final void setRootStage(final Stage newRootStage) {
        rootstage = newRootStage;
    }
}
