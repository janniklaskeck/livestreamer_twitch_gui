package app.lsgui.gui.chat;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

import org.jibble.pircbot.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.service.Settings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);

    private ChatClient chatClient;

    @FXML
    private TextArea chatTextArea;

    @FXML
    private TextField inputTextField;

    @FXML
    private Button sendButton;

    @FXML
    public void initialize() {
        LOGGER.info("SettingsController init");

        chatClient = new ChatClient("", chatTextArea);
        sendButton.setOnAction(event -> {
            chatClient.sendMessage("#" + chatClient.getChannel(), inputTextField.getText());
            chatTextArea.appendText(Settings.instance().getTwitchUser() + ": " + inputTextField.getText());
        });
    }

    public void connect() {
        chatClient.setChannel((String) ((Stage) chatTextArea.getScene().getWindow()).getProperties().get("channel"));
        chatClient.setVerbose(false);
        if (!chatClient.isConnected()) {
            if (!"".equals(Settings.instance().getTwitchUser())) {
                LOGGER.info("Try login with user data");
                chatClient.setUserName(Settings.instance().getTwitchUser());
                try {
                    chatClient.connect("irc.twitch.tv", 6667, Settings.instance().getTwitchOAuth());
                } catch (IOException | IrcException e) {
                    LOGGER.error("ERROR while connecting to twitch chat", e);
                }
                chatClient.joinChannel("#" + chatClient.getChannel());
                LOGGER.info("Join Channel {}", chatClient.getChannel());
            } else {
                LOGGER.info("Try login anonymously");
                String uuid = UUID.randomUUID().toString().replace("-", "");
                chatClient.setUserName("justinfan" + new BigInteger(uuid, 16));
                try {
                    chatClient.connect("irc.twitch.tv", 6667, "");
                } catch (IOException | IrcException e) {
                    LOGGER.error("ERROR while connecting to twitch chat", e);
                }
                chatClient.joinChannel("#" + chatClient.getChannel());
                LOGGER.info("Join Channel {}", chatClient.getChannel());
            }
        }
    }

    public void disconnect() {
        chatClient.disconnect();
        chatClient.dispose();
    }

}
