package app.lsgui.gui.chat;

import java.io.IOException;

import org.fxmisc.richtext.InlineCssTextArea;
import org.jibble.pircbot.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.settings.Settings;
import app.lsgui.utils.IrcClient;
import app.lsgui.utils.LsGuiUtils;
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
public class ChatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);

    private IrcClient client;
    private InlineCssTextArea chatTextArea;

    @FXML
    private TextField inputTextField;

    @FXML
    private Button sendButton;

    @FXML
    private BorderPane chatBorderPane;

    @FXML
    public void initialize() {
        LOGGER.info("SettingsController init");
        chatTextArea = new InlineCssTextArea();
        chatTextArea.setWrapText(true);
        chatTextArea.setFont(new Font(20));
        chatTextArea.setEditable(false);

        chatBorderPane.setCenter(chatTextArea);
        inputTextField.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                sendMessage(inputTextField.getText());
            }
        });

        sendButton.setOnAction(event -> sendMessage(inputTextField.getText()));
        client = new IrcClient(chatTextArea);
    }

    private void sendMessage(final String message) {
        if (!"".equals(message)) {

            final String twitchUsername = Settings.getInstance().getTwitchUser();
            final int start = chatTextArea.getText().length();
            final int end = start + twitchUsername.length() + 1;
            chatTextArea.appendText(twitchUsername + ": " + message + "\n");
            setColoredNickName(chatTextArea, start, end);
            setChatMessageStyle(chatTextArea, end, end + message.length() + 1);
            inputTextField.clear();
        }
    }

    public void connect(final String channel) {
        final String twitchIrc = "irc.chat.twitch.tv";
        if (!"".equals(Settings.getInstance().getTwitchUser()) && !"".equals(Settings.getInstance().getTwitchOAuth())) {
            final String user = Settings.getInstance().getTwitchUser();
            final String oauth = Settings.getInstance().getTwitchOAuth();

            client.setUserName(user);
            client.setChannel(channel);
            clientConnect(twitchIrc, oauth);
            LOGGER.info("DATA Login");
        }
    }

    public void clientConnect(String twitchIrc, String oauth) {
        try {
            client.connect(twitchIrc, 6667, oauth);
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        client.disconnect();
        client.dispose();
    }

    public static void setColoredNickName(final InlineCssTextArea cta, final int start, final int end) {
        cta.setStyle(start, end,
                "-fx-fill: " + LsGuiUtils.getColorFromString(cta.getText(start, end)) + "; -fx-font-size: 12pt");

    }

    public static void setChatMessageStyle(final InlineCssTextArea cta, final int start, final int end) {
        cta.setStyle(start, end, "-fx-font-size: 12pt");
    }
}
