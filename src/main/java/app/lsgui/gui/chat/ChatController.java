package app.lsgui.gui.chat;

import java.io.IOException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.fxmisc.richtext.InlineCssTextArea;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.settings.Settings;
import app.lsgui.utils.LsGuiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
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
        chatTextArea.setFont(new Font(20));
        chatTextArea.setEditable(false);

        chatBorderPane.setCenter(chatTextArea);
        inputTextField.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                sendMessage(inputTextField.getText());
            }
        });

        sendButton.setOnAction(event -> sendMessage(inputTextField.getText()));
    }

    private void sendMessage(final String message) {
        if (!"".equals(message)) {
            String channel = (String) ((Stage) chatTextArea.getScene().getWindow()).getProperties().get("channel");
            pircBotX.send().message("#" + channel, message);
            final String twitchUsername = Settings.instance().getTwitchUser();
            final int start = chatTextArea.getText().length();
            final int end = start + twitchUsername.length() + 1;
            chatTextArea.appendText(twitchUsername + ": " + message + "\n");
            setColoredNickName(chatTextArea, start, end);
            setChatMessageStyle(chatTextArea, end, end + message.length() + 1);
            inputTextField.clear();
        }
    }

    public void connect() {
        final String channel = (String) ((Stage) chatTextArea.getScene().getWindow()).getProperties().get("channel");

        final ChatListener listener = new ChatListener(chatTextArea);
        final EnableCapHandler capHandler = new EnableCapHandler("twitch.tv/membership");
        final String twitchIrc = "irc.twitch.tv";
        final String channelToJoin = "#" + channel;
        final Configuration.Builder cfgBuilder = new Configuration.Builder().addAutoJoinChannel(channelToJoin)
                .addListener(listener).setAutoNickChange(false).setOnJoinWhoEnabled(false).setCapEnabled(true)
                .addCapHandler(capHandler).setEncoding(StandardCharsets.UTF_8).addServer(twitchIrc);
        final Configuration cfg;
        if (!"".equals(Settings.instance().getTwitchUser()) && !"".equals(Settings.instance().getTwitchOAuth())) {
            final String user = Settings.instance().getTwitchUser();
            final String oauth = Settings.instance().getTwitchOAuth();
            cfg = cfgBuilder.setName(user).setLogin(user).setServerPassword(oauth).buildConfiguration();
            LOGGER.info("DATA Login");
        } else {
            final String uuid = UUID.randomUUID().toString().replace("-", "");
            final String name = "justinfan" + new BigInteger(uuid, 16);
            cfg = cfgBuilder.setName(name).setLogin(name).buildConfiguration();
            LOGGER.info("ANON Login");
        }
        pircBotX = new PircBotX(cfg);
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
                "-fx-fill: " + LsGuiUtils.getColorFromString(cta.getText(start, end)) + "; -fx-font-size: 12pt");

    }

    public static void setChatMessageStyle(final InlineCssTextArea cta, final int start, final int end) {
        cta.setStyle(start, end, "-fx-font-size: 12pt");
    }
}
