package app.lsgui.gui.chat;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

import org.fxmisc.richtext.InlineCssTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.lsgui.irc.pircbot.IrcClient;
import app.lsgui.irc.pircbot.IrcException;
import app.lsgui.settings.Settings;
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
    private String channel;

    @FXML
    private TextField inputTextField;

    @FXML
    private Button sendButton;

    @FXML
    private BorderPane chatBorderPane;

    @FXML
    public void initialize() {
        LOGGER.info("SettingsController init");
        client = new IrcClient();
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
            client.joinChannel("#" + this.channel);
            final String twitchUsername = Settings.instance().getTwitchUser();
            final int start = chatTextArea.getText().length();
            final int end = start + twitchUsername.length() + 1;
            chatTextArea.appendText(twitchUsername + ": " + message + "\n");
            setColoredNickName(chatTextArea, start, end);
            setChatMessageStyle(chatTextArea, end, end + message.length() + 1);
            inputTextField.clear();
        }
    }

    public void connect(final String channel) {
        this.channel = channel;
        final ChatListener listener = new ChatListener(chatTextArea);
        // final EnableCapHandler capHandler = new
        // EnableCapHandler("twitch.tv/membership");
        final String twitchIrc = "irc.twitch.tv";
        final String channelToJoin = "#" + channel;
        // final Configuration.Builder cfgBuilder = new
        // Configuration.Builder().addAutoJoinChannel(channelToJoin)
        // .addListener(listener).setAutoNickChange(false).setOnJoinWhoEnabled(false).setCapEnabled(true)
        // .addCapHandler(capHandler).setEncoding(StandardCharsets.UTF_8).addServer(twitchIrc);
        // final Configuration cfg;
        if (!"".equals(Settings.instance().getTwitchUser()) && !"".equals(Settings.instance().getTwitchOAuth())) {
            final String user = Settings.instance().getTwitchUser();
            final String oauth = Settings.instance().getTwitchOAuth();

            client.setUserName(user);
            clientConnect(twitchIrc, oauth);
            LOGGER.info("DATA Login");
        } else {
            final String uuid = UUID.randomUUID().toString().replace("-", "");
            final String name = "justinfan" + new BigInteger(uuid, 16);
            LOGGER.info("ANON Login");
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
