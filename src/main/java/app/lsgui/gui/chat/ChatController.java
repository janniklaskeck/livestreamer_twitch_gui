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

import org.fxmisc.richtext.InlineCssTextArea;
import org.jibble.pircbot.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.utils.IrcClient;
import app.lsgui.utils.LsGuiUtils;
import app.lsgui.utils.Settings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public final class ChatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);
    private static final int FONT_SIZE = 20;

    private IrcClient client;
    private InlineCssTextArea chatTextArea;

    @FXML
    private TextField inputTextField;

    @FXML
    private Button sendButton;

    @FXML
    private BorderPane chatBorderPane;

    public ChatController() {
        // Empty Constructor
    }

    @FXML
    public void initialize() {
        LOGGER.info("SettingsController init");
        this.chatTextArea = new InlineCssTextArea();
        this.chatTextArea.setWrapText(true);
        this.chatTextArea.setFont(new Font(FONT_SIZE));
        this.chatTextArea.setEditable(false);

        this.chatBorderPane.setCenter(this.chatTextArea);
        this.inputTextField.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                this.sendMessage(this.inputTextField.getText());
            }
        });

        this.sendButton.setOnAction(event -> this.sendMessage(this.inputTextField.getText()));
        this.client = new IrcClient(this.chatTextArea);
    }

    private void sendMessage(final String message) {
        if (!"".equals(message)) {
            final String twitchUsername = Settings.getInstance().getTwitchUser();
            final int start = this.chatTextArea.getText().length();
            final int end = start + twitchUsername.length() + 1;
            this.chatTextArea.appendText(twitchUsername + ": " + message + "\n");
            setColoredNickName(this.chatTextArea, start, end);
            setChatMessageStyle(this.chatTextArea, end, end + message.length() + 1);
            this.inputTextField.clear();
        }
    }

    public void connect(final String channel) {
        final String twitchIrc = "irc.chat.twitch.tv";
        if (!"".equals(Settings.getInstance().getTwitchUser()) && !"".equals(Settings.getInstance().getTwitchOAuth())) {
            final String user = Settings.getInstance().getTwitchUser();
            final String oauth = Settings.getInstance().getTwitchOAuth();

            this.client.setUserName(user);
            this.client.setChannel(channel);
            this.clientConnect(twitchIrc, oauth);
            LOGGER.info("DATA Login");
        }
    }

    public void clientConnect(final String twitchIrc, final String oauth) {
        try {
            final int port = 6667;
            this.client.connect(twitchIrc, port, oauth);
        } catch (IOException | IrcException e) {
            LOGGER.error("Could not connect to Twitch IRC", e);
        }
    }

    public void disconnect() {
        this.client.disconnect();
        this.client.dispose();
    }

    public static void setColoredNickName(final InlineCssTextArea cta, final int start, final int end) {
        cta.setStyle(start, end,
                "-fx-fill: " + LsGuiUtils.getColorFromString(cta.getText(start, end)) + "; -fx-font-size: 12pt");
    }

    public static void setChatMessageStyle(final InlineCssTextArea cta, final int start, final int end) {
        cta.setStyle(start, end, "-fx-font-size: 12pt");
    }
}
