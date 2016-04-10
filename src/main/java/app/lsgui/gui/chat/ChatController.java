package app.lsgui.gui.chat;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
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

    private PircBotX pircBotX;

    @FXML
    private TextArea chatTextArea;

    @FXML
    private TextField inputTextField;

    @FXML
    private Button sendButton;

    @FXML
    public void initialize() {
        LOGGER.info("SettingsController init");

        sendButton.setOnAction(event -> {
            String channel = (String) ((Stage) chatTextArea.getScene().getWindow()).getProperties().get("channel");
            pircBotX.send().message("#" + channel, inputTextField.getText());
            chatTextArea.appendText(Settings.instance().getTwitchUser() + ": " + inputTextField.getText() + "\n");
            inputTextField.setText("");
        });
    }

    public void connect() {
        String channel = (String) ((Stage) chatTextArea.getScene().getWindow()).getProperties().get("channel");
        Configuration cfg;
        if (!"".equals(Settings.instance().getTwitchUser()) && !"".equals(Settings.instance().getTwitchOAuth())) {
            cfg = new Configuration.Builder().setName(Settings.instance().getTwitchUser())
                    .setLogin(Settings.instance().getTwitchUser()).addAutoJoinChannel(channel)
                    .addListener(new ChatListener(chatTextArea))
                    .buildForServer("irc.twitch.tv", 6667, Settings.instance().getTwitchOAuth());
        } else {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            cfg = new Configuration.Builder().setName(Settings.instance().getTwitchUser())
                    .setLogin("justinfan" + new BigInteger(uuid, 16)).addAutoJoinChannel(channel)
                    .addListener(new ChatListener(chatTextArea))
                    .buildForServer("irc.twitch.tv", 6667, Settings.instance().getTwitchOAuth());
        }

        pircBotX = new PircBotX(cfg);

        Thread t = new Thread(() -> {
            try {
                pircBotX.startBot();
            } catch (IOException | IrcException e) {
                LOGGER.error("ERROR while trying to connecto to chat", e);
            }
        });
        t.setDaemon(true);
        t.start();

    }

    public void disconnect() {
        if (pircBotX.isConnected()) {
            pircBotX.sendIRC().quitServer();
        }
    }
}
