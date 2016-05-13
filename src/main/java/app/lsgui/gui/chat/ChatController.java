package app.lsgui.gui.chat;

import java.io.IOException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.UUID;

import org.fxmisc.richtext.InlineCssTextArea;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.settings.Settings;
import app.lsgui.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ChatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);

    private PircBotX pircBotX;

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

        chatBorderPane.setCenter(chatTextArea);

        sendButton.setOnAction(event -> {
            String channel = (String) ((Stage) chatTextArea.getScene().getWindow()).getProperties().get("channel");
            pircBotX.send().message("#" + channel, inputTextField.getText());
            final int start = chatTextArea.getText().length();
            final int end = start + Settings.instance().getTwitchUser().length() + 1;
            chatTextArea.appendText(Settings.instance().getTwitchUser() + ": " + inputTextField.getText() + "\n");
            setColoredNickName(chatTextArea, start, end);
            setChatMessageStyle(chatTextArea, end, end + inputTextField.getText().length() + 1);
            inputTextField.setText("");
        });
    }

    public void connect() {
        String channel = (String) ((Stage) chatTextArea.getScene().getWindow()).getProperties().get("channel");
        final Configuration cfg;

        if (!"".equals(Settings.instance().getTwitchUser()) && !"".equals(Settings.instance().getTwitchOAuth())) {

            final String user = Settings.instance().getTwitchUser();
            final String oauth = Settings.instance().getTwitchOAuth();

            cfg = new Configuration.Builder().setName(user).setLogin(user).addAutoJoinChannel("#" + channel)
                    .addListener(new ChatListener(chatTextArea)).setAutoNickChange(true)
                    .buildForServer("irc.twitch.tv", 6667, oauth);

            LOGGER.info("DATA Login");
        } else {
            final String uuid = UUID.randomUUID().toString().replace("-", "");

            cfg = new Configuration.Builder().setName("justinfan" + new BigInteger(uuid, 16))
                    .setLogin("justinfan" + new BigInteger(uuid, 16)).addAutoJoinChannel("#" + channel)
                    .setAutoNickChange(true).addListener(new ChatListener(chatTextArea))
                    .buildForServer("irc.twitch.tv", 6667);

            LOGGER.info("ANON Login");
        }

        pircBotX = new PircBotX(cfg);

        LOGGER.info("CAP {}", pircBotX.getEnabledCapabilities());
        Thread t = new Thread(() -> {
            try {
                pircBotX.startBot();
            } catch (IOException | IrcException e) {
                if (e.getClass().equals(UnknownHostException.class)) {
                    LOGGER.error(
                            "ERROR Unknown Hosts while trying to connecto to chat. Check your Internet Connection");
                } else {
                    LOGGER.error("ERROR while trying to connecto to chat", e);
                }
                sendButton.setDisable(true);
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

    public static void setColoredNickName(final InlineCssTextArea cta, final int start, final int end) {
        cta.setStyle(start, end,
                "-fx-fill: " + Utils.getColorFromString(cta.getText(start, end)) + "; -fx-font-size: 12pt");

    }

    public static void setChatMessageStyle(final InlineCssTextArea cta, final int start, final int end) {
        cta.setStyle(start, end, "-fx-font-size: 12pt");
    }
}
