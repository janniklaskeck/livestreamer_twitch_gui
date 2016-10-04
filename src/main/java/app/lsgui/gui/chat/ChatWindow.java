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
package app.lsgui.gui.chat;

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

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public final class ChatWindow extends Stage {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatWindow.class);
    private static final int MIN_WIDTH = 600;
    private static final int MIN_HEIGHT = 400;
    private String channel;
    private FXMLLoader loader;

    public ChatWindow(final String channel) {
        this.channel = channel;
        this.loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/ChatWindow.fxml"));
        final Parent root = this.loadFxml();
        this.setupStage(root);
    }

    private Parent loadFxml() {
        try {
            return this.loader.load();
        } catch (IOException e) {
            LOGGER.error("ERROR while loading chat fxml", e);
            Platform.exit();
            return null;
        }
    }

    private void setupStage(final Parent root) {
        this.setMinHeight(MIN_HEIGHT);
        this.setMinWidth(MIN_WIDTH);

        this.setTitle(this.channel + " - Livestreamer GUI Chat v" + LsGuiUtils.readVersionProperty());
        this.getIcons().add(new Image(getClass().getResourceAsStream("/icon.jpg")));
        final Scene scene = new Scene(root);
        scene.getStylesheets().add(ChatWindow.class
                .getResource("/styles/" + Settings.getInstance().windowStyleProperty() + ".css").toExternalForm());
        this.setScene(scene);
        this.initModality(Modality.NONE);
        this.show();

        this.setOnCloseRequest(event -> this.disconnect());
    }

    private void disconnect() {
        ((ChatController) this.loader.getController()).disconnect();
    }

    public void connect() {
        final ChatController chatController = (ChatController) this.loader.getController();
        chatController.connect(this.channel);
    }
}
